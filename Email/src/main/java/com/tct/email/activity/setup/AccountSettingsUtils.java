/*
 * Copyright (C) 2009 The Android Open Source Project
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
/* 04/14/2014|     Chao Zhang       |      FR 635028       |[Email]Download   */
/*           |                      |porting from(FR472914)|options to be im- */
/*           |                      |                      |plemented         */
/* ----------|----------------------|----------------------|----------------- */
/*
 ========================================================================== 
 *HISTORY
 *
 *Tag               Date        Author          Description
 *============== ============ =============== ==============================
 *CONFLICT-20001 2014/10/24    wenggangjin      Modify the package conflict
 **============== ============ =============== ==============================
 *BUGFIX-844469  2014/11/25    wenggangjin      [Android5.0][Email]Email crashes when input a hotmail accoun
 *BUGFIX-922404  2015/02/11    zhaotianyong    [TMO-EU][SSV]The configuration of email is not effective
 *BUGFIX-932385  2015/03/09    ke.ma           Phone does not give option to select predefined email account
 *BUGFIX-936393  2015/03/12    zhichuan.wei    [SDM][Email] “def_email_account2ReloginValue” SDM ID is not working
 *BUGFIX-951620  2015/03/17    ke.ma           [email]email feature requirements
 *BUGFIX-951782  2015/03/17    ke.ma           [Android5.0][Email]Undesired ISP display in the predefine-account.
 *BUGFIX-955397  2015/03/25    ke.ma           [America][SSV]When enter into email,can't find "iClaro"display.
 *BUGFIX-996086  2015/05/08    ke.ma           The handset in the email app has not the option to set an "@itelcel" account.
 *BUGFIX-969854  2015/05/26    junwei-xu       [HOMO][ALWE][E-Mail][Settings][Country-Profile][UI] Update needed for German e-mail provider Web.de and Gmx.de.
 *BUGFIX-1093309 2015/09/29    junwei-xu       <13340Track><26><CDR-EAS-030>Synchronization Scope—Calendar Events
 *BUGFIX-1912757 2016/04/06    rong-tang       [GPR][GAPP][Email]Must remove google/gmail account from per-configured inside TctEmail.
 *BUGFIX-1920545 2016/04/07    rong-tang       [Email] There is 'hotmail.com' which can not be removed by perso
 *===========================================================================  */
/******************************************************************************/
package com.tct.email.activity.setup;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.XmlResourceParser;
import android.net.Uri;
import android.text.Editable;
import android.text.TextUtils;
import android.widget.EditText;

import com.tct.email.R;
import com.tct.emailcommon.Logging;
import com.tct.emailcommon.VendorPolicyLoader;
import com.tct.emailcommon.VendorPolicyLoader.OAuthProvider;
import com.tct.emailcommon.VendorPolicyLoader.Provider;
import com.tct.emailcommon.provider.Account;
import com.tct.emailcommon.provider.EmailContent.AccountColumns;
import com.tct.emailcommon.provider.QuickResponse;
import com.tct.emailcommon.service.PolicyServiceProxy;
import com.tct.emailcommon.utility.Utility;
import com.tct.mail.utils.LogUtils;
import com.tct.mail.utils.PLFUtils;
import com.tct.email.SecurityPolicy;
import com.tct.email.provider.AccountBackupRestore;
//TS: MOD by wenggangjin for CONFLICT_20001 START
//import com.google.common.annotations.VisibleForTesting;
import com.tct.fw.google.common.annotations.VisibleForTesting;
//TS: MOD by wenggangjin for CONFLICT_20001 END

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
//[FEATURE]-Add-BEGIN by TSNJ.qinglian.zhang,10/28/2014,FR 736417
import com.tct.emailcommon.provider.EmailContent;
//[FEATURE]-Add-END by TSNJ.qinglian.zhang

public class AccountSettingsUtils {

    /** Pattern to match any part of a domain */
    private final static String WILD_STRING = "*";
    /** Will match any, single character */
    private final static char WILD_CHARACTER = '?';
    private final static String DOMAIN_SEPARATOR = "\\.";

    /**
     * Commits the UI-related settings of an account to the provider.  This is static so that it
     * can be used by the various account activities.  If the account has never been saved, this
     * method saves it; otherwise, it just saves the settings.
     * @param context the context of the caller
     * @param account the account whose settings will be committed
     */
    public static void commitSettings(Context context, Account account) {
        if (!account.isSaved()) {
            account.save(context);

            if (account.mPolicy != null) {
                // TODO: we need better handling for unsupported policies
                // For now, just clear the unsupported policies, as the server will (hopefully)
                // just reject our sync attempts if it's not happy with half-measures
                if (account.mPolicy.mProtocolPoliciesUnsupported != null) {
                    LogUtils.d(LogUtils.TAG, "Clearing unsupported policies "
                            + account.mPolicy.mProtocolPoliciesUnsupported);
                    account.mPolicy.mProtocolPoliciesUnsupported = null;
                }
                PolicyServiceProxy.setAccountPolicy2(context,
                        account.getId(),
                        account.mPolicy,
                        account.mSecuritySyncKey == null ? "" : account.mSecuritySyncKey,
                        false /* notify */);
            }
            //[FEATURE]-Add-BEGIN by TSNJ.qinglian.zhang,10/28/2014,FR 736417
            String protocol = account.mHostAuthRecv.mProtocol;
            if (protocol != null && protocol.startsWith("eas"))
                EmailContent.AccountInfo.insertProxyInfo(context, account.mId,
                        account.mHostAuthRecv.mIsUseproxy, account.mHostAuthRecv.mProxyAddr,
                        account.mHostAuthRecv.mProxyPort, account.mHostAuthRecv.mProxyUsername,
                        account.mHostAuthRecv.mProxyUserpass);
            //[FEATURE]-Add-END by TSNJ.qinglian.zhang

            // Set up default quick responses here...
            String[] defaultQuickResponses =
                context.getResources().getStringArray(R.array.default_quick_responses);
            ContentValues cv = new ContentValues();
            cv.put(QuickResponse.ACCOUNT_KEY, account.mId);
            ContentResolver resolver = context.getContentResolver();
            for (String quickResponse: defaultQuickResponses) {
                // Allow empty entries (some localizations may not want to have the maximum
                // number)
                if (!TextUtils.isEmpty(quickResponse)) {
                    cv.put(QuickResponse.TEXT, quickResponse);
                    resolver.insert(QuickResponse.CONTENT_URI, cv);
                }
            }
        } else {
            ContentValues cv = getAccountContentValues(account);
            account.update(context, cv);
        }

        // Update the backup (side copy) of the accounts
        AccountBackupRestore.backup(context);
    }

    /**
     * Returns a set of content values to commit account changes (not including the foreign keys
     * for the two host auth's and policy) to the database.  Does not actually commit anything.
     */
    public static ContentValues getAccountContentValues(Account account) {
        ContentValues cv = new ContentValues();
        cv.put(AccountColumns.IS_DEFAULT, account.mIsDefault);
        cv.put(AccountColumns.DISPLAY_NAME, account.getDisplayName());
        cv.put(AccountColumns.SENDER_NAME, account.getSenderName());
        cv.put(AccountColumns.SIGNATURE, account.getSignature());
        cv.put(AccountColumns.SYNC_INTERVAL, account.mSyncInterval);
        cv.put(AccountColumns.FLAGS, account.mFlags);
        cv.put(AccountColumns.SYNC_LOOKBACK, account.mSyncLookback);
        cv.put(AccountColumns.SECURITY_SYNC_KEY, account.mSecuritySyncKey);
        //[FEATURE]-Add-BEGIN by TSCD.Chao Zhang,04/14/2014,FR 631895(porting from FR 472914)
        cv.put(AccountColumns.DOWNLOAD_OPTIONS, account.mDownloadOptions);
        //[FEATURE]-Add-END by TSCD.Chao Zhang
        //TS: junwei-xu 2015-09-29 EMAIL FEATURE-1093309 ADD_S
        cv.put(AccountColumns.SYNC_CALENDAR_LOOKBACK, account.mSyncCalendarLookback);
        //TS: junwei-xu 2015-09-29 EMAIL FEATURE-1093309 ADD_E
        return cv;
    }

   /**
    * Create the request to get the authorization code.
    *
    * @param context
    * @param provider The OAuth provider to register with
    * @param emailAddress Email address to send as a hint to the oauth service.
    * @return
    */
   public static Uri createOAuthRegistrationRequest(final Context context,
           final OAuthProvider provider, final String emailAddress) {
       final Uri.Builder b = Uri.parse(provider.authEndpoint).buildUpon();
       b.appendQueryParameter("response_type", provider.responseType);
       b.appendQueryParameter("client_id", provider.clientId);
       b.appendQueryParameter("redirect_uri", provider.redirectUri);
       b.appendQueryParameter("scope", provider.scope);
       b.appendQueryParameter("state", provider.state);
       b.appendQueryParameter("login_hint", emailAddress);
       return b.build();
   }

   /**
    * Search for a single resource containing known oauth provider definitions.
    *
    * @param context
    * @param id String Id of the oauth provider.
    * @return The OAuthProvider if found, null if not.
    */
   public static OAuthProvider findOAuthProvider(final Context context, final String id) {
       return findOAuthProvider(context, id, R.xml.oauth);
   }

   public static List<OAuthProvider> getAllOAuthProviders(final Context context) {
       try {
           List<OAuthProvider> providers = new ArrayList<OAuthProvider>();
           final XmlResourceParser xml = context.getResources().getXml(R.xml.oauth);
           int xmlEventType;
           OAuthProvider provider = null;
           while ((xmlEventType = xml.next()) != XmlResourceParser.END_DOCUMENT) {
               if (xmlEventType == XmlResourceParser.START_TAG
                       && "provider".equals(xml.getName())) {
                   try {
                       provider = new OAuthProvider();
                       provider.id = getXmlAttribute(context, xml, "id");
                       provider.label = getXmlAttribute(context, xml, "label");
                       provider.authEndpoint = getXmlAttribute(context, xml, "auth_endpoint");
                       provider.tokenEndpoint = getXmlAttribute(context, xml, "token_endpoint");
                       provider.refreshEndpoint = getXmlAttribute(context, xml,
                               "refresh_endpoint");
                       provider.responseType = getXmlAttribute(context, xml, "response_type");
                       provider.redirectUri = getXmlAttribute(context, xml, "redirect_uri");
                       provider.scope = getXmlAttribute(context, xml, "scope");
                       provider.state = getXmlAttribute(context, xml, "state");
                       provider.clientId = getXmlAttribute(context, xml, "client_id");
                       provider.clientSecret = getXmlAttribute(context, xml, "client_secret");
                       providers.add(provider);
                   } catch (IllegalArgumentException e) {
                       LogUtils.w(Logging.LOG_TAG, "providers line: " + xml.getLineNumber() +
                               "; Domain contains multiple globals");
                   }
               }
           }
           return providers;
       } catch (Exception e) {
           LogUtils.e(Logging.LOG_TAG, "Error while trying to load provider settings.", e);
       }
       return null;
   }

   /**
    * Search for a single resource containing known oauth provider definitions.
    *
    * @param context
    * @param id String Id of the oauth provider.
    * @param resourceId ResourceId of the xml file to search.
    * @return The OAuthProvider if found, null if not.
    */
   public static OAuthProvider findOAuthProvider(final Context context, final String id,
           final int resourceId) {
       // TODO: Consider adding a way to cache this file during new account setup, so that we
       // don't need to keep loading the file over and over.
       // TODO: need a mechanism to get a list of all supported OAuth providers so that we can
       // offer the user a choice of who to authenticate with.
       try {
           final XmlResourceParser xml = context.getResources().getXml(resourceId);
           int xmlEventType;
           OAuthProvider provider = null;
           while ((xmlEventType = xml.next()) != XmlResourceParser.END_DOCUMENT) {
               if (xmlEventType == XmlResourceParser.START_TAG
                       && "provider".equals(xml.getName())) {
                   String providerId = getXmlAttribute(context, xml, "id");
                   try {
                       if (TextUtils.equals(id, providerId)) {
                           provider = new OAuthProvider();
                           provider.id = id;
                           provider.label = getXmlAttribute(context, xml, "label");
                           provider.authEndpoint = getXmlAttribute(context, xml, "auth_endpoint");
                           provider.tokenEndpoint = getXmlAttribute(context, xml, "token_endpoint");
                           provider.refreshEndpoint = getXmlAttribute(context, xml,
                                   "refresh_endpoint");
                           provider.responseType = getXmlAttribute(context, xml, "response_type");
                           provider.redirectUri = getXmlAttribute(context, xml, "redirect_uri");
                           provider.scope = getXmlAttribute(context, xml, "scope");
                           provider.state = getXmlAttribute(context, xml, "state");
                           provider.clientId = getXmlAttribute(context, xml, "client_id");
                           provider.clientSecret = getXmlAttribute(context, xml, "client_secret");
                           return provider;
                       }
                   } catch (IllegalArgumentException e) {
                       LogUtils.w(Logging.LOG_TAG, "providers line: " + xml.getLineNumber() +
                               "; Domain contains multiple globals");
                   }
               }
           }
       } catch (Exception e) {
           LogUtils.e(Logging.LOG_TAG, "Error while trying to load provider settings.", e);
       }
       return null;
   }

   /**
     * Search the list of known Email providers looking for one that matches the user's email
     * domain.  We check for vendor supplied values first, then we look in providers_product.xml,
     * and finally by the entries in platform providers.xml.  This provides a nominal override
     * capability.
     *
     * A match is defined as any provider entry for which the "domain" attribute matches.
     *
     * @param domain The domain portion of the user's email address
     * @return suitable Provider definition, or null if no match found
     */
    public static Provider findProviderForDomain(Context context, String domain) {
        Provider p = VendorPolicyLoader.getInstance(context).findProviderForDomain(domain);
        if (p == null) {
        // TS: zhaotianyong 2014-12-10 EMAIL BUGFIX_844469 MOD_S
        //  p = findProviderForDomain(context, domain ,R.xml.providers_product);
            p = PLFUtils.findProviderForDomain(context, domain);
        // TS: zhaotianyong 2014-12-10 EMAIL BUGFIX_844469 MOD_E
        }
        if (p == null) {
            p = findProviderForDomain(context, domain, R.xml.providers);
        }
        return p;
    }

    public static Provider findProviderForDomainAndProtocol(Context context, String domain,String protocol) throws URISyntaxException {
        Provider p;
        if (protocol != null) {
            p = PLFUtils.findProviderForDomainAndProtocol(context, domain, protocol);
            return p;
        }
        return null;
    }

    //[FEATURE]-Add-BEGIN by TSNJ,wei.huang,10/22/2014
    public static Provider findProviderForDomain(Context context, String domain,String label) {
        Provider p = VendorPolicyLoader.getInstance(context).findProviderForDomain(domain);
        if (label!=null) {
        // TS: zhaotianyong 2014-12-10 EMAIL BUGFIX_844469 MOD_S
        // p = findProviderForDomain(context, domain, label ,R.xml.providers_product);
           p = PLFUtils.findProviderForDomain(context, domain, label);
        // TS: zhaotianyong 2014-12-10 EMAIL BUGFIX_844469 MOD_E
        //[BUGFIX]-Add-BEGIN by TCTNJ.(zhichuan.wei),03/12/2015 for PR936393
        } else {
            p = PLFUtils.findProviderForDomain(context, domain);
        }
        //[BUGFIX]-Add-END by TCTNJ.(zhichuan.wei)
        //TS: wenggangjin 2014-11-25 EMAIL BUGFIX_844469 MOD_S
        try {
            String packageName = context.getResources().getString(R.string.intent_exchange_package);
            context.getApplicationContext().getPackageManager().getPackageInfo(packageName, 0);
            if (p == null) {
                // TS: zhaotianyong 2014-12-10 EMAIL BUGFIX_844469 MOD_S
                //  p = findProviderForDomain(context, domain ,R.xml.providers_product);
                    p = PLFUtils.findProviderForDomain(context, domain);
                // TS: zhaotianyong 2014-12-10 EMAIL BUGFIX_844469 MOD_E
            }
        } catch (NameNotFoundException e) {

            if (p == null) {
                p = findProviderForDomain(context, domain, R.xml.providers_product_noexchange);
            }
        }
        //TS: wenggangjin 2014-11-25 EMAIL BUGFIX_844469 MOD_E
        if (p == null) {
            p = findProviderForDomain(context, domain, R.xml.providers);
        }
        return p;
    }

    public static Provider findProviderForDomain(Context context,String domain,String label,int resourceId){
       try{
           XmlResourceParser xml = context.getResources().getXml(resourceId);
          int xmlEventType;
          Provider provider = null;
          while((xmlEventType = xml.next())!=XmlResourceParser.END_DOCUMENT){
               if(xmlEventType == XmlResourceParser.START_TAG && "provider".equals(xml.getName())){
                 String providerDomain = getXmlAttribute(context, xml, "domain");
                 try{
                    if(matchProvider(domain,providerDomain) && label.equalsIgnoreCase(getXmlAttribute(context, xml, "label"))){
                      provider = new Provider();
                      provider.id=getXmlAttribute(context,xml,"id");
                      provider.label=getXmlAttribute(context,xml,"label");
                      provider.domain=domain.toLowerCase();
                      provider.note=getXmlAttribute(context,xml,"note");
                      provider.relogin=getXmlAttribute(context,xml,"relogin");
                      if(provider.relogin==null){
                          provider.relogin=String.valueOf(1);
                      }
                    }
                 }catch(IllegalArgumentException e){

                 }
               }else if(xmlEventType == XmlResourceParser.START_TAG && "incoming".equals(xml.getName()) && provider !=null){
                       provider.incomingUriTemplate=getXmlAttribute(context,xml,"uri");
                       provider.incomingUsernameTemplate=getXmlAttribute(context,xml,"username");
               }else if(xmlEventType==XmlResourceParser.START_TAG && "outgoing".equals(xml.getName()) && provider != null) {
                       provider.outgoingUriTemplate = getXmlAttribute(context,xml, "uri");
                       provider.outgoingUsernameTemplate = getXmlAttribute(context, xml, "username");
               }else if(xmlEventType == XmlResourceParser.END_TAG && "provider".equals(xml.getName()) && provider != null) {
                       return provider;
               }
          }
          }catch (Exception e) {

          }
          return null;
    }
    //[FEATURE]-Add-END by TSNJ,wei.huang

    /**
     * Search a single resource containing known Email provider definitions.
     *
     * @param domain The domain portion of the user's email address
     * @param resourceId Id of the provider resource to scan
     * @return suitable Provider definition, or null if no match found
     */
    /*package*/ static Provider findProviderForDomain(
            Context context, String domain, int resourceId) {
        try {
            XmlResourceParser xml = context.getResources().getXml(resourceId);
            int xmlEventType;
            Provider provider = null;
            while ((xmlEventType = xml.next()) != XmlResourceParser.END_DOCUMENT) {
                if (xmlEventType == XmlResourceParser.START_TAG
                        && "provider".equals(xml.getName())) {
                    String providerDomain = getXmlAttribute(context, xml, "domain");
                    try {
                        if (matchProvider(domain, providerDomain)) {
                            provider = new Provider();
                            provider.id = getXmlAttribute(context, xml, "id");
                            provider.label = getXmlAttribute(context, xml, "label");
                            provider.domain = domain.toLowerCase();
                            provider.note = getXmlAttribute(context, xml, "note");
                            // TODO: Maybe this should actually do a lookup of the OAuth provider
                            // here, and keep a pointer to it rather than a textual key.
                            // To do this probably requires caching oauth.xml, otherwise the lookup
                            // is expensive and likely to happen repeatedly.
                            provider.oauth = getXmlAttribute(context, xml, "oauth");
                        }
                    } catch (IllegalArgumentException e) {
                        LogUtils.w(Logging.LOG_TAG, "providers line: " + xml.getLineNumber() +
                                "; Domain contains multiple globals");
                    }
                }
                else if (xmlEventType == XmlResourceParser.START_TAG
                        && "incoming".equals(xml.getName())
                        && provider != null) {
                    provider.incomingUriTemplate = getXmlAttribute(context, xml, "uri");
                    provider.incomingUsernameTemplate = getXmlAttribute(context, xml, "username");
                }
                else if (xmlEventType == XmlResourceParser.START_TAG
                        && "outgoing".equals(xml.getName())
                        && provider != null) {
                    provider.outgoingUriTemplate = getXmlAttribute(context, xml, "uri");
                    provider.outgoingUsernameTemplate = getXmlAttribute(context, xml, "username");
                }
                else if (xmlEventType == XmlResourceParser.START_TAG
                        && "incoming-fallback".equals(xml.getName())
                        && provider != null) {
                    provider.altIncomingUriTemplate = getXmlAttribute(context, xml, "uri");
                    provider.altIncomingUsernameTemplate =
                            getXmlAttribute(context, xml, "username");
                }
                else if (xmlEventType == XmlResourceParser.START_TAG
                        && "outgoing-fallback".equals(xml.getName())
                        && provider != null) {
                    provider.altOutgoingUriTemplate = getXmlAttribute(context, xml, "uri");
                    provider.altOutgoingUsernameTemplate =
                            getXmlAttribute(context, xml, "username");
                }
                else if (xmlEventType == XmlResourceParser.END_TAG
                        && "provider".equals(xml.getName())
                        && provider != null) {
                    return provider;
                }
            }
        }
        catch (Exception e) {
            LogUtils.e(Logging.LOG_TAG, "Error while trying to load provider settings.", e);
        }
        return null;
    }

    /**
     * Returns true if the string <code>s1</code> matches the string <code>s2</code>. The string
     * <code>s2</code> may contain any number of wildcards -- a '?' character -- and/or asterisk
     * characters -- '*'. Wildcards match any single character, while the asterisk matches a domain
     * part (i.e. substring demarcated by a period, '.')
     */
    @VisibleForTesting
    public static boolean matchProvider(String testDomain, String providerDomain) {
        String[] testParts = testDomain.split(DOMAIN_SEPARATOR);
        String[] providerParts = providerDomain.split(DOMAIN_SEPARATOR);
        if (testParts.length != providerParts.length) {
            return false;
        }
        for (int i = 0; i < testParts.length; i++) {
            String testPart = testParts[i].toLowerCase();
            String providerPart = providerParts[i].toLowerCase();
            if (!providerPart.equals(WILD_STRING) &&
                    !matchWithWildcards(testPart, providerPart)) {
                return false;
            }
        }
        return true;
    }

    private static boolean matchWithWildcards(String testPart, String providerPart) {
        int providerLength = providerPart.length();
        if (testPart.length() != providerLength){
            return false;
        }
        for (int i = 0; i < providerLength; i++) {
            char testChar = testPart.charAt(i);
            char providerChar = providerPart.charAt(i);
            if (testChar != providerChar && providerChar != WILD_CHARACTER) {
                return false;
            }
        }
        return true;
    }

    /**
     * Attempts to get the given attribute as a String resource first, and if it fails
     * returns the attribute as a simple String value.
     * @param xml
     * @param name
     * @return the requested resource
     */
    private static String getXmlAttribute(Context context, XmlResourceParser xml, String name) {
        int resId = xml.getAttributeResourceValue(null, name, 0);
        if (resId == 0) {
            return xml.getAttributeValue(null, name);
        }
        else {
            return context.getString(resId);
        }
    }

    /**
     * Infer potential email server addresses from domain names
     *
     * Incoming: Prepend "imap" or "pop3" to domain, unless "pop", "pop3",
     *          "imap", or "mail" are found.
     * Outgoing: Prepend "smtp" if domain starts with any in the host prefix array
     *
     * @param server name as we know it so far
     * @param incoming "pop3" or "imap" (or null)
     * @param outgoing "smtp" or null
     * @return the post-processed name for use in the UI
     */
    public static String inferServerName(Context context, String server, String incoming,
            String outgoing) {
        // Default values cause entire string to be kept, with prepended server string
        int keepFirstChar = 0;
        int firstDotIndex = server.indexOf('.');
        if (firstDotIndex != -1) {
            // look at first word and decide what to do
            String firstWord = server.substring(0, firstDotIndex).toLowerCase();
            String[] hostPrefixes =
                    context.getResources().getStringArray(R.array.smtp_host_prefixes);
            boolean canSubstituteSmtp = Utility.arrayContains(hostPrefixes, firstWord);
            boolean isMail = "mail".equals(firstWord);
            // Now decide what to do
            if (incoming != null) {
                // For incoming, we leave imap/pop/pop3/mail alone, or prepend incoming
                if (canSubstituteSmtp || isMail) {
                    return server;
                }
            } else {
                // For outgoing, replace imap/pop/pop3 with outgoing, leave mail alone, or
                // prepend outgoing
                if (canSubstituteSmtp) {
                    keepFirstChar = firstDotIndex + 1;
                } else if (isMail) {
                    return server;
                } else {
                    // prepend
                }
            }
        }
        return ((incoming != null) ? incoming : outgoing) + '.' + server.substring(keepFirstChar);
    }

    /**
     * Helper to set error status on password fields that have leading or trailing spaces
     */
    public static void checkPasswordSpaces(Context context, EditText passwordField) {
        Editable password = passwordField.getText();
        int length = password.length();
        if (length > 0) {
            if (password.charAt(0) == ' ' || password.charAt(length-1) == ' ') {
                passwordField.setError(context.getString(R.string.account_password_spaces_error));
            }
        }
    }

    //TS: ke.ma 2015-03-09 EMAIL BUGFIX-932385 ADD_S
    public static StringBuffer[] getAutoCompleteDomains(Context context){
          //TS: ke.ma 2015-03-17 EMAIL BUGFIX-951620 951782 DEL_S
//        XmlPullParser xmlPull=context.getResources().getXml(R.xml.providers);
          //TS: ke.ma 2015-03-17 EMAIL BUGFIX-951620 951782 DEL_E
        StringBuffer[] domainsFromISDM=PLFUtils.getDomainsFromISDM(context);
          //TS: ke.ma 2015-03-17 EMAIL BUGFIX-951620 951782 DEL_S
//        StringBuffer[] domainsFromXML=PLFUtils.getDomainsFromProviders(xmlPull);
//        int lengthISDM=domainsFromISDM.length;
//        int lengthXML=domainsFromXML.length;
//        StringBuffer[] domainsAll=new StringBuffer[lengthISDM+lengthXML];
//        System.arraycopy(domainsFromISDM, 0, domainsAll, 0, lengthISDM);
//        System.arraycopy(domainsFromXML, 0, domainsAll, lengthISDM, lengthXML);
          //TS: ke.ma 2015-03-17 EMAIL BUGFIX-951620 951782 DEL_E

        //TS: ke.ma 2015-03-17 EMAIL BUGFIX-951620 951782 MOD_S
        StringBuffer[] domainResult = removeRepeat(domainsFromISDM);
        //TS: ke.ma 2015-03-17 EMAIL BUGFIX-951620 951782 MOD_E
        //TS: rong-tang 2016-04-06 EMAIL BUGFIX-1912757 ADD_S
        //Note: remove the domains which required not display by project team.
        domainResult = removeNotDisplayValue(context, domainResult);
        //TS: rong-tang 2016-04-06 EMAIL BUGFIX-1912757 ADD_E
        return domainResult;
    }

    //TS: rong-tang 2016-04-06 EMAIL BUGFIX-1912757 ADD_S
    private static StringBuffer[] removeNotDisplayValue(Context context, StringBuffer[] domains) {
        if (domains == null) {
            return null;
        }
        String notDisplayDomain = PLFUtils.getString(context, "def_email_predefine_not_show_domain_value");
        String[] notDisplayDomains = null;
        if (!TextUtils.isEmpty(notDisplayDomain) && !"@".equals(notDisplayDomain)) {
            if (notDisplayDomain.contains("@")) {
                notDisplayDomains = notDisplayDomain.split("@");
            } else {
                notDisplayDomains = new String[] {notDisplayDomain};
            }
            if (notDisplayDomains != null) {
                List<StringBuffer> domainsRemovedNotDisplay = new ArrayList<StringBuffer>();
                for (int i = 0; i < domains.length; i++) {
                    boolean existedInNotDisplay = false;
                    for (String domainNotDisplay : notDisplayDomains) {
                        if (("@" + domainNotDisplay).equals(domains[i].toString())) {
                            existedInNotDisplay = true;
                            break;
                        }
                    }
                    if (!existedInNotDisplay) {
                        domainsRemovedNotDisplay.add(domains[i]);
                    }
                }
                StringBuffer[] resultBuffer = new StringBuffer[domainsRemovedNotDisplay.size()];
                for (int k = 0; k < domainsRemovedNotDisplay.size(); k++) {
                    resultBuffer[k] = new StringBuffer(domainsRemovedNotDisplay.get(k));
                }
                return resultBuffer;
            }
        }

        return domains;
    }
    //TS: rong-tang 2016-04-06 EMAIL BUGFIX-1912757 ADD_E

    //TS: ke.ma 2015-03-25 EMAIL BUGFIX-955397 MOD_S
    public static StringBuffer[] removeRepeat(StringBuffer[] str){
    //TS: ke.ma 2015-03-25 EMAIL BUGFIX-955397 MOD_E
        List<StringBuffer> strs=new ArrayList<StringBuffer>();
        for(int i=0;i<str.length;i++){
              //TS: ke.ma 2015-03-17 EMAIL BUGFIX-951620 951782 DEL_S
//            if(!str[i].toString().contains("*")&&!str[i].toString().contains("?")){
              //TS: ke.ma 2015-03-17 EMAIL BUGFIX-951620 951782 DEL_E
                if(!isExsit(strs,str[i])){
                    strs.add(str[i]);
                }
              //TS: ke.ma 2015-03-17 EMAIL BUGFIX-951620 951782 DEL_S
//            }
              //TS: ke.ma 2015-03-17 EMAIL BUGFIX-951620 951782 DEL_E
        }

        String[] result=new String[strs.size()];
        for(int j=0;j<strs.size();j++){
            if(strs.get(j).length()!=0){
                result[j]=strs.get(j).toString();
            }
        }
        Arrays.sort(result);

        StringBuffer[] resultBuffer=new StringBuffer[strs.size()];
        for(int k=0;k<strs.size();k++){
            resultBuffer[k]=new StringBuffer(result[k]);
        }

        return resultBuffer;
    }

    //TS: ke.ma 2015-05-08 EMAIL BUGFIX-996086 MOD_S
    public static boolean isExsit(List<StringBuffer> nowStr,StringBuffer matchStr){
    //TS: ke.ma 2015-05-08 EMAIL BUGFIX-996086 MOD_E
        boolean flag=false;
        for(int i=0;i<nowStr.size();i++){
            //TS: rong-tang 2016-04-07 EMAIL BUGFIX-1920545 MOD_S
            if(matchStr.toString().trim().equalsIgnoreCase(nowStr.get(i).toString().trim())){
            //TS: rong-tang 2016-04-07 EMAIL BUGFIX-1920545 MOD_E
                flag=true;
                break;
            }
        }
        return flag;
    }
    //TS: ke.ma 2015-03-09 EMAIL BUGFIX-932385 ADD_E
}
