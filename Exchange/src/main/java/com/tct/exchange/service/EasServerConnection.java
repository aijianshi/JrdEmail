/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/* ========================================================================== */
/*     Modifications on Features list / Changes Request / Problems Report     */
/* -------------------------------------------------------------------------- */
/*    date   |        author        |         Key          |     comment      */
/* ----------|----------------------|----------------------|----------------- */
/* 10/28/2014|     qinglian.zhang   |      FR 736417       |[Orange]Proxy su- */
/*           |                      |                      |pport             */
/* ----------|----------------------|----------------------|----------------- */
/******************************************************************************/
/*
 ==========================================================================
 *HISTORY
 *
 *Tag            Date         Author          Description
 *============== ============ =============== ==============================
 *BUGFIX-946537  2015/3/12    ChaoZhang       [Android5.0][Exchange]Exchange service will force close after optimization by "Tencent mobile housekeeper"
 *BUGFIX-1014490  2015/6/12   zheng.zou      [Email]Not possible to set up Exchange account
 ============================================================================
 */

package com.tct.exchange.service;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.NetworkOnMainThreadException;
import android.os.SystemProperties;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Base64;

import com.tct.emailcommon.internet.MimeUtility;
import com.tct.emailcommon.provider.Account;
import com.tct.emailcommon.provider.EmailContent;
import com.tct.emailcommon.provider.HostAuth;
import com.tct.emailcommon.service.AccountServiceProxy;
import com.tct.emailcommon.utility.EmailClientConnectionManager;
import com.tct.emailcommon.utility.Utility;
import com.tct.exchange.Eas;
import com.tct.exchange.EasResponse;
import com.tct.exchange.eas.EasConnectionCache;
import com.tct.exchange.utility.CurlLogger;
import com.tct.exchange.utility.WbxmlResponseLogger;
import com.tct.mail.utils.LogUtils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpProcessor;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.cert.CertificateException;

//[FEATURE]-Add-BEGIN by TSNJ.qinglian.zhang,10/28/2014,FR 736417
//[FEATURE]-Add-END by TSNJ.qinglian.zhang

/**
 * Base class for communicating with an EAS server. Anything that needs to send messages to the
 * server can subclass this to get access to the {@link #sendHttpClientPost} family of functions.
 * TODO: This class has a regrettable name. It's not a connection, but rather a task that happens
 * to have (and use) a connection to the server.
 */
public class EasServerConnection {
    /** Logging tag. */
    private static final String TAG = Eas.LOG_TAG;

    /**
     * Timeout for establishing a connection to the server.
     */
    private static final long CONNECTION_TIMEOUT = 20 * DateUtils.SECOND_IN_MILLIS;

    /**
     * Timeout for http requests after the connection has been established.
     */
    protected static final long COMMAND_TIMEOUT = 30 * DateUtils.SECOND_IN_MILLIS;

    // TS: zheng.zou 2015-12-15 EXCHANGE TASK-1170293 MOD_S
    private static final String DEVICE_MODEL = TextUtils.isEmpty(SystemProperties.get("ro.email.config.EasUA", "")) ?
            SystemProperties.get("ro.product.model", "") : SystemProperties.get("ro.email.config.EasUA", "");// TS: zheng.zou 2016-3-2 Email BUGFIX_1717686 MOD
    private static final String DEVICE_TYPE = "Android";
    private static final String USER_AGENT = TextUtils.isEmpty(DEVICE_MODEL) ? DEVICE_TYPE + '/' + Build.VERSION.RELEASE + '-' +
            Eas.CLIENT_VERSION : DEVICE_MODEL + '/' + Build.VERSION.RELEASE;
    // TS: zheng.zou 2015-12-15 EXCHANGE TASK-1170293 MOD_E

    /** Message MIME type for EAS version 14 and later. */
    private static final String EAS_14_MIME_TYPE = "application/vnd.ms-sync.wbxml";

    /**
     * Value for {@link #mStoppedReason} when we haven't been stopped.
     */
    public static final int STOPPED_REASON_NONE = 0;

    /**
     * Passed to {@link #stop} to indicate that this stop request should terminate this task.
     */
    public static final int STOPPED_REASON_ABORT = 1;

    /**
     * Passed to {@link #stop} to indicate that this stop request should restart this task (e.g. in
     * order to reload parameters).
     */
    public static final int STOPPED_REASON_RESTART = 2;

    private static final String[] ACCOUNT_SECURITY_KEY_PROJECTION =
            { EmailContent.AccountColumns.SECURITY_SYNC_KEY };

    private static String sDeviceId = null;

    protected final Context mContext;
    // TODO: Make this private if possible. Subclasses must be careful about altering the HostAuth
    // to not screw up any connection caching (use redirectHostAuth).
    protected final HostAuth mHostAuth;
    protected final Account mAccount;
    private final long mAccountId;

    // Bookkeeping for interrupting a request. This is primarily for use by Ping (there's currently
    // no mechanism for stopping a sync).
    // Access to these variables should be synchronized on this.
    private HttpUriRequest mPendingRequest = null;
    private boolean mStopped = false;
    private int mStoppedReason = STOPPED_REASON_NONE;

    /** The protocol version to use, as a double. */
    private double mProtocolVersion = 0.0d;
    /** Whether {@link #setProtocolVersion} was last called with a non-null value. */
    private boolean mProtocolVersionIsSet = false;

    /**
     * The client for any requests made by this object. This is created lazily, and cleared
     * whenever our host auth is redirected.
     */
    private HttpClient mClient;

    /**
     * This is used only to check when our client needs to be refreshed.
     */
    private EmailClientConnectionManager mClientConnectionManager;

    public EasServerConnection(final Context context, final Account account,
                               final HostAuth hostAuth) {
        mContext = context;
        mHostAuth = hostAuth;
        mAccount = account;
        mAccountId = account.mId;
        setProtocolVersion(account.mProtocolVersion);
    }

    protected EmailClientConnectionManager getClientConnectionManager()
        throws CertificateException {
        final EmailClientConnectionManager connManager =
                EasConnectionCache.instance().getConnectionManager(mContext, mHostAuth);
        if (mClientConnectionManager != connManager) {
            mClientConnectionManager = connManager;
            mClient = null;
        }
        return connManager;
    }

    public void redirectHostAuth(final String newAddress) {
        mClient = null;
        mHostAuth.mAddress = newAddress;
        if (mHostAuth.isSaved()) {
            EasConnectionCache.instance().uncacheConnectionManager(mHostAuth);
            final ContentValues cv = new ContentValues(1);
            cv.put(EmailContent.HostAuthColumns.ADDRESS, newAddress);
            mHostAuth.update(mContext, cv);
        }
    }

    private HttpClient getHttpClient(final long timeout) throws CertificateException {
        if (mClient == null) {
            final HttpParams params = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(params, (int)(CONNECTION_TIMEOUT));
            HttpConnectionParams.setSoTimeout(params, (int)(timeout));
            HttpConnectionParams.setSocketBufferSize(params, 8192);
            mClient = new DefaultHttpClient(getClientConnectionManager(), params) {
                @Override
                protected BasicHttpProcessor createHttpProcessor() {
                    final BasicHttpProcessor processor = super.createHttpProcessor();
                    processor.addRequestInterceptor(new CurlLogger());
                    processor.addResponseInterceptor(new WbxmlResponseLogger());
                    return processor;
                }
            };
        }
       //[FEATURE]-Add-BEGIN by TSNJ.qinglian.zhang,10/28/2014,FR 736417
       if (EmailContent.USE_STATIC_PROXY) {
           if (EmailContent.USE_PROXY) {
               HttpHost proxy = new HttpHost(EmailContent.PROXY_ADDRESS,EmailContent.PROXY_PORT);
               mClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY,proxy);
               if (EmailContent.PROXY_USERNAME != null && !EmailContent.PROXY_USERNAME.trim().equals("")) {
                   ((DefaultHttpClient) mClient).getCredentialsProvider().setCredentials(
                           new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT),
                           new UsernamePasswordCredentials(EmailContent.PROXY_USERNAME, EmailContent.PROXY_USERPASS));
               }
           }
       } else if (mHostAuth != null) {
           //android.util.Log.d("ccxccx","mHostAuth.mIsUseproxy : " +mHostAuth.mIsUseproxy +" mHostAuth.mProxyAddr :" +mHostAuth.mProxyAddr +" mHostAuth.mProxyPort :"+mHostAuth.mProxyPort);
           if (mHostAuth.mIsUseproxy) {
               HttpHost proxy = new HttpHost(mHostAuth.mProxyAddr,mHostAuth.mProxyPort);
               mClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY,proxy);
               if (mHostAuth.mProxyUsername != null && !mHostAuth.mProxyUsername.trim().equals("")) {
                   ((DefaultHttpClient) mClient).getCredentialsProvider().setCredentials(
                           new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT),
                           new UsernamePasswordCredentials(mHostAuth.mProxyUsername, mHostAuth.mProxyUserpass));
               }
           }
       }
       //[FEATURE]-Add-END by TSNJ.qinglian.zhang
        return mClient;
    }

    private String makeAuthString() {
        final String cs = mHostAuth.mLogin + ":" + mHostAuth.mPassword;
        return "Basic " + Base64.encodeToString(cs.getBytes(), Base64.NO_WRAP);
    }

    private String makeUserString() {
        if (sDeviceId == null) {
            sDeviceId = new AccountServiceProxy(mContext).getDeviceId();
            if (sDeviceId == null) {
                LogUtils.e(TAG, "Could not get device id, defaulting to '0'");
                sDeviceId = "0";
            }
        }
        return "&User=" + Uri.encode(mHostAuth.mLogin) + "&DeviceId=" +
                sDeviceId + "&DeviceType=" + DEVICE_TYPE;
    }

    // TS: zheng.zou 2015-06-12 EXCHANGE BUGFIX-1014490 MOD_S
    private String makeBaseUriString() {
        //[FEATURE]-Mod-BEGIN by TSNJ.qinglian.zhang ,10/28/2014,FR 736417
        /*return EmailClientConnectionManager.makeScheme(mHostAuth.shouldUseSsl(),
                mHostAuth.shouldTrustAllServerCerts(), mHostAuth.mClientCertAlias) +
                "://" + mHostAuth.mAddress + "/Microsoft-Server-ActiveSync";*/

//        String scheme = EmailClientConnectionManager.makeScheme(mHostAuth.shouldUseSsl(),
//                mHostAuth.shouldTrustAllServerCerts(), mHostAuth.mClientCertAlias) ;
//        String port = "";
//        if (scheme.equalsIgnoreCase("http")) {
//            port = ":80";
//        } else if (scheme.equalsIgnoreCase("https")) {
//            port = ":443";
//        } else if (scheme.equalsIgnoreCase("httpts")) {
//            port = ":443";
//        }
//        return scheme + "://" + mHostAuth.mAddress + port + "/Microsoft-Server-ActiveSync";
        //[FEATURE]-Mod-END by TSNJ.qinglian.zhang

        //[BUGFIX]-Add-BEGIN by TCTNB.caixia.chen,04/08/2015,PR 944356,
        //optimize bug 736417 codes
        if (EmailContent.USE_STATIC_PROXY) {
            if (EmailContent.USE_PROXY) {
                return makeBaseUriStringWithPort();
            }
        } else if (mHostAuth != null) {
            if (mHostAuth.mIsUseproxy) {
                return makeBaseUriStringWithPort();
            }
        }
        //[BUGFIX]-Add-END by TCTNB.caixia.chen
        if (mHostAuth == null) {
            return null;
        }
        //[BUGFIX]-Add-BEGIN by TCTNB.caixia.chen,12/04/2014,PR 859922
        /** M: Add for ipv6 address, it should be enclosed in square brackets @{ */
        String host = mHostAuth.mAddress;
        try {
            URI uri = new URI("http", null, host, -1,
                    null, // path
                    null, // query
                    null);
            host = uri.getHost();
        } catch (URISyntaxException e) {
            LogUtils.e(TAG, "Host: URISyntaxException %s", e);
        }
        /** @} */
        //[BUGFIX]-Add-END by TCTNB.caixia.chen
        return EmailClientConnectionManager.makeScheme(mHostAuth.shouldUseSsl(),
                mHostAuth.shouldTrustAllServerCerts(), mHostAuth.mClientCertAlias) +
                "://" + host + "/Microsoft-Server-ActiveSync";
    }

    private String makeBaseUriStringWithPort() {
        //[BUGFIX]-Add-BEGIN by TCTNB.caixia.chen,12/04/2014,PR 859922
        /** M: Add for ipv6 address, it should be enclosed in square brackets @{ */
        String host = mHostAuth.mAddress;
        try {
            URI uri = new URI("http", null, host, -1,
                    null, // path
                    null, // query
                    null);
            host = uri.getHost();
        } catch (URISyntaxException e) {
            LogUtils.e(TAG, "Host: URISyntaxException %s", e);
        }
        /** @} */
        //[BUGFIX]-Add-END by TCTNB.caixia.chen
        //[FEATURE]-Mod-BEGIN by TSNJ.qinglian.zhang ,10/28/2014,FR 736417
        /*return EmailClientConnectionManager.makeScheme(mHostAuth.shouldUseSsl(),
                mHostAuth.shouldTrustAllServerCerts(), mHostAuth.mClientCertAlias) +
                "://" + mHostAuth.mAddress + "/Microsoft-Server-ActiveSync";*/

        String scheme = EmailClientConnectionManager.makeScheme(mHostAuth.shouldUseSsl(),
                mHostAuth.shouldTrustAllServerCerts(), mHostAuth.mClientCertAlias) ;
        String port = "";
        if (scheme.equalsIgnoreCase("http")) {
            port = ":80";
        } else if (scheme.equalsIgnoreCase("https")) {
            port = ":443";
        } else if (scheme.equalsIgnoreCase("httpts")) {
            port = ":443";
        }
        //[BUGFIX]-Mod-BEGIN by TCTNB.caixia.chen,12/04/2014,PR 859922
        return scheme + "://" + host + port + "/Microsoft-Server-ActiveSync";
        //[BUGFIX]-Mod-END by TCTNB.caixia.chen
        //[FEATURE]-Mod-END by TSNJ.qinglian.zhang
    }
    // TS: zheng.zou 2015-06-12 EXCHANGE BUGFIX-1014490 MOD_S

    public String makeUriString(final String cmd) {
        String uriString = makeBaseUriString();
        if (cmd != null) {
            uriString += "?Cmd=" + cmd + makeUserString();
        }
        return uriString;
    }

    private String makeUriString(final String cmd, final String extra) {
        return makeUriString(cmd) + extra;
    }

    /**
     * If a sync causes us to update our protocol version, this function must be called so that
     * subsequent calls to {@link #getProtocolVersion()} will do the right thing.
     * @return Whether the protocol version changed.
     */
    public boolean setProtocolVersion(String protocolVersionString) {
        mProtocolVersionIsSet = (protocolVersionString != null);
        if (protocolVersionString == null) {
            protocolVersionString = Eas.DEFAULT_PROTOCOL_VERSION;
        }
        final double oldProtocolVersion = mProtocolVersion;
        mProtocolVersion = Eas.getProtocolVersionDouble(protocolVersionString);
        return (oldProtocolVersion != mProtocolVersion);
    }

    /**
     * @return The protocol version for this connection.
     */
    public double getProtocolVersion() {
        return mProtocolVersion;
    }

    /**
     * @return The useragent string for our client.
     */
    public final String getUserAgent() {
        return USER_AGENT;
    }

    /**
     * Send an http OPTIONS request to server.
     * @return The {@link EasResponse} from the Exchange server.
     * @throws IOException
     */
    protected EasResponse sendHttpClientOptions() throws IOException, CertificateException {
        // For OPTIONS, just use the base string and the single header
        final HttpOptions method = new HttpOptions(URI.create(makeBaseUriString()));
        method.setHeader("Authorization", makeAuthString());
        method.setHeader("User-Agent", getUserAgent());
        return EasResponse.fromHttpRequest(getClientConnectionManager(),
                getHttpClient(COMMAND_TIMEOUT), method);
    }

    protected void resetAuthorization(final HttpPost post) {
        post.removeHeaders("Authorization");
        post.setHeader("Authorization", makeAuthString());
    }

    /**
     * Make an {@link HttpPost} for a specific request.
     * @param uri The uri for this request, as a {@link String}.
     * @param entity The {@link HttpEntity} for this request.
     * @param contentType The Content-Type for this request.
     * @param usePolicyKey Whether or not a policy key should be sent.
     * @return
     */
    public HttpPost makePost(final String uri, final HttpEntity entity, final String contentType,
            final boolean usePolicyKey) {
        final HttpPost post = new HttpPost(uri);
        post.setHeader("Authorization", makeAuthString());
        post.setHeader("MS-ASProtocolVersion", String.valueOf(mProtocolVersion));
        post.setHeader("User-Agent", getUserAgent());
        post.setHeader("Accept-Encoding", "gzip");
        // If there is no entity, we should not be setting a content-type since this will
        // result in a 400 from the server in the case of loading an attachment.
        if (contentType != null && entity != null) {
            post.setHeader("Content-Type", contentType);
        }
        if (usePolicyKey) {
            // If there's an account in existence, use its key; otherwise (we're creating the
            // account), send "0".  The server will respond with code 449 if there are policies
            // to be enforced
            final String key;
            final String accountKey;
            if (mAccountId == Account.NO_ACCOUNT) {
                accountKey = null;
            } else {
               accountKey = Utility.getFirstRowString(mContext,
                        ContentUris.withAppendedId(Account.CONTENT_URI, mAccountId),
                        ACCOUNT_SECURITY_KEY_PROJECTION, null, null, null, 0);
            }
            if (!TextUtils.isEmpty(accountKey)) {
                key = accountKey;
            } else {
                key = "0";
            }
            post.setHeader("X-MS-PolicyKey", key);
        }
        post.setEntity(entity);
        return post;
    }

    public HttpGet makeGet(final String uri) {
        final HttpGet get = new HttpGet(uri);
        return get;
    }

    /**
     * Make an {@link HttpOptions} request for this connection.
     * @return The {@link HttpOptions} object.
     */
    public HttpOptions makeOptions() {
        final HttpOptions method = new HttpOptions(URI.create(makeBaseUriString()));
        method.setHeader("Authorization", makeAuthString());
        method.setHeader("User-Agent", getUserAgent());
        return method;
    }

    /**
     * Send a POST request to the server.
     * @param cmd The command we're sending to the server.
     * @param entity The {@link HttpEntity} containing the payload of the message.
     * @param timeout The timeout for this POST.
     * @return The response from the Exchange server.
     * @throws IOException
     */
    protected EasResponse sendHttpClientPost(String cmd, final HttpEntity entity,
            final long timeout) throws IOException, CertificateException {
        final boolean isPingCommand = cmd.equals("Ping");

        // Split the mail sending commands
        // TODO: This logic should not be here, the command should be generated correctly
        // in a subclass of EasOperation.
        String extra = null;
        boolean msg = false;
        if (cmd.startsWith("SmartForward&") || cmd.startsWith("SmartReply&")) {
            final int cmdLength = cmd.indexOf('&');
            extra = cmd.substring(cmdLength);
            cmd = cmd.substring(0, cmdLength);
            msg = true;
        } else if (cmd.startsWith("SendMail&")) {
            msg = true;
        }

        // Send the proper Content-Type header; it's always wbxml except for messages when
        // the EAS protocol version is < 14.0
        // If entity is null (e.g. for attachments), don't set this header
        final String contentType;
        if (msg && (getProtocolVersion() < Eas.SUPPORTED_PROTOCOL_EX2010_DOUBLE)) {
            contentType = MimeUtility.MIME_TYPE_RFC822;
        } else if (entity != null) {
            contentType = EAS_14_MIME_TYPE;
        } else {
            contentType = null;
        }
        final String uriString;
        if (extra == null) {
            uriString = makeUriString(cmd);
        } else {
            uriString = makeUriString(cmd, extra);
        }
        final HttpPost method = makePost(uriString, entity, contentType, !isPingCommand);
        // NOTE
        // The next lines are added at the insistence of $VENDOR, who is seeing inappropriate
        // network activity related to the Ping command on some networks with some servers.
        // This code should be removed when the underlying issue is resolved
        if (isPingCommand) {
            method.setHeader("Connection", "close");
        }
        return executeHttpUriRequest(method, timeout);
    }

    public EasResponse sendHttpClientPost(final String cmd, final byte[] bytes,
            final long timeout) throws IOException, CertificateException {
        final ByteArrayEntity entity;
        if (bytes == null) {
            entity = null;
        } else {
            entity = new ByteArrayEntity(bytes);
        }
        return sendHttpClientPost(cmd, entity, timeout);
    }

    protected EasResponse sendHttpClientPost(final String cmd, final byte[] bytes)
            throws IOException, CertificateException {
        return sendHttpClientPost(cmd, bytes, COMMAND_TIMEOUT);
    }

    /**
     * Executes an {@link HttpUriRequest}.
     * Note: this function must not be called by multiple threads concurrently. Only one thread may
     * send server requests from a particular object at a time.
     * @param method The post to execute.
     * @param timeout The timeout to use.
     * @return The response from the Exchange server.
     * @throws IOException
     */
    public EasResponse executeHttpUriRequest(final HttpUriRequest method, final long timeout)
            throws IOException, CertificateException {
        LogUtils.d(TAG, "EasServerConnection about to make request %s", method.getRequestLine());
        // The synchronized blocks are here to support the stop() function, specifically to handle
        // when stop() is called first. Notably, they are NOT here in order to guard against
        // concurrent access to this function, which is not supported.
        synchronized (this) {
            if (mStopped) {
                mStopped = false;
                // If this gets stopped after the POST actually starts, it throws an IOException.
                // Therefore if we get stopped here, let's throw the same sort of exception, so
                // callers can equate IOException with "this POST got killed for some reason".
                throw new IOException("Command was stopped before POST");
            }
           mPendingRequest = method;
        }
        boolean postCompleted = false;
        try {
            final EasResponse response = EasResponse.fromHttpRequest(getClientConnectionManager(),
                    getHttpClient(timeout), method);
            postCompleted = true;
            return response;
        } finally {
            synchronized (this) {
                mPendingRequest = null;
                if (postCompleted) {
                    mStoppedReason = STOPPED_REASON_NONE;
                }
            }
        }
    }

    protected EasResponse executePost(final HttpPost method)
            throws IOException, CertificateException {
        return executeHttpUriRequest(method, COMMAND_TIMEOUT);
    }

    /**
     * If called while this object is executing a POST, interrupt it with an {@link IOException}.
     * Otherwise cause the next attempt to execute a POST to be interrupted with an
     * {@link IOException}.
     * @param reason The reason for requesting a stop. This should be one of the STOPPED_REASON_*
     *               constants defined in this class, other than {@link #STOPPED_REASON_NONE} which
     *               is used to signify that no stop has occurred.
     *               This class simply stores the value; subclasses are responsible for checking
     *               this value when catching the {@link IOException} and responding appropriately.
     */
    public synchronized void stop(final int reason) {
        // Only process legitimate reasons.
        if (reason >= STOPPED_REASON_ABORT && reason <= STOPPED_REASON_RESTART) {
            final boolean isMidPost = (mPendingRequest != null);
            LogUtils.i(TAG, "%s with reason %d", (isMidPost ? "Interrupt" : "Stop next"), reason);
            mStoppedReason = reason;
            if (isMidPost) {
                // TS: chaozhang 2015-03-12 EXCHANGE BUGFIX-946537 ADD_S
                //NOTE:When kill service by other 3rdapps,the EasService will call to stop all pings and
                //network operations,here it it,after 4.4,android do not allow user to call network operations
                //directly in mainThread,here to avoid it.
                try {
                    if (mPendingRequest != null) {
                        mPendingRequest.abort();
                    }
                } catch (NetworkOnMainThreadException e) {
                    LogUtils.e(TAG, "Exception thrown:", e);
                    new Thread() {
                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            if (mPendingRequest != null) {
                                mPendingRequest.abort();
                            }
                        }
                    }.start();
                }catch (Exception e1){
                    LogUtils.e(TAG, "Exception thrown:", e1);
                }
                // TS: chaozhang 2015-03-12 EXCHANGE BUGFIX-946537 ADD_E
            } else {
                mStopped = true;
            }
        }
    }

    /**
     * @return The reason supplied to the last call to {@link #stop}, or
     *         {@link #STOPPED_REASON_NONE} if {@link #stop} hasn't been called since the last
     *         successful POST.
     */
    public synchronized int getStoppedReason() {
        return mStoppedReason;
    }

    /**
     * Try to register our client certificate, if needed.
     * @return True if we succeeded or didn't need a client cert, false if we failed to register it.
     */
    public boolean registerClientCert() {
        if (mHostAuth.mClientCertAlias != null) {
            try {
                getClientConnectionManager().registerClientCert(mContext, mHostAuth);
            } catch (final CertificateException e) {
                // The client certificate the user specified is invalid/inaccessible.
                return false;
            }
        }
        return true;
    }

    /**
     * @return Whether {@link #setProtocolVersion} was last called with a non-null value. Note that
     *         at construction time it is set to whatever protocol version is in the account.
     */
    public boolean isProtocolVersionSet() {
        return mProtocolVersionIsSet;
    }
}
