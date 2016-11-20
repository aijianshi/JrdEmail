/*
 * Copyright (C) 2013 Google Inc.
 * Licensed to The Android Open Source Project.
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

package com.tct.mail.browse;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.tct.emailcommon.TempDirectory;
import com.tct.emailcommon.internet.MimeMessage;
import com.tct.emailcommon.mail.MessagingException;
import com.tct.mail.utils.Base64InputStream; //MODIFIED by zheng.zou, 2016-03-31,BUG-1863915
import com.tct.mail.utils.LogTag;
import com.tct.mail.utils.LogUtils;
import com.tct.mail.ui.MailAsyncTaskLoader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Loader that builds a ConversationMessage from an EML file Uri.
 */
public class EmlMessageLoader extends MailAsyncTaskLoader<ConversationMessage> {
    private static final String LOG_TAG = LogTag.getLogTag();

    private Uri mEmlFileUri;

    public EmlMessageLoader(Context context, Uri emlFileUri) {
        super(context);
        mEmlFileUri = emlFileUri;
    }

    @Override
    public ConversationMessage loadInBackground() {
        final Context context = getContext();
        TempDirectory.setTempDirectory(context);
        final ContentResolver resolver = context.getContentResolver();
        final InputStream stream;
        try {
            stream = resolver.openInputStream(mEmlFileUri);
        } catch (FileNotFoundException e) {
            LogUtils.e(LOG_TAG, e, "Could not find eml file at uri: %s", mEmlFileUri);
            return null;
        }

        final MimeMessage mimeMessage;
        ConversationMessage convMessage;
        try {
            mimeMessage = new MimeMessage(stream);
            convMessage = new ConversationMessage(context, mimeMessage, mEmlFileUri);
        } catch (IOException e) {
            LogUtils.e(LOG_TAG, e, "Could not read eml file");
            return null;
        } catch (MessagingException e) {
            LogUtils.e(LOG_TAG, e, "Error in parsing eml file");
            return null;
        } finally {
            try {
                //[BUGFIX]-Add-BEGIN by SCDTABLET.yingjie.chen,04/29/2016, 2005129
                // [Monkey][Email]com.tct.email crash beacause of java.lang.NullPointerException,
                //virtual method ,void java.io.InputStream.close()
                if(null != stream){
                    stream.close();
                }
                //[BUGFIX]-Add-END by SCDTABLET.yingjie.chen
            } catch (IOException e) {
                convMessage = null;
            }

            // delete temp files created during parsing
            final File[] cacheFiles = TempDirectory.getTempDirectory().listFiles();
            for (final File file : cacheFiles) {
                if (file.getName().startsWith("body")) {
                    final boolean deleted = file.delete();
                    if (!deleted) {
                        LogUtils.d(LOG_TAG, "Failed to delete temp file" + file.getName());
                    }
                }
            }
        }

        //TS: zheng.zou 2016-03-31 EMAIL BUGFIX_1863915 ADD_S
        if (convMessage == null || mimeMessage.hasNoHeader()){
            //failed, try base64 encode
            convMessage = loadBase64Message();
        }
        //TS: zheng.zou 2016-03-31 EMAIL BUGFIX_1863915 ADD_E
        return convMessage;
    }

    //TS: zheng.zou 2016-03-31 EMAIL BUGFIX_1863915 ADD_S
    /**
     * load eml file with base64 encode
     * @return ConversationMessage
     */
    private ConversationMessage loadBase64Message() {
        final Context context = getContext();
        TempDirectory.setTempDirectory(context);
        final ContentResolver resolver = context.getContentResolver();
        final InputStream stream;
        try {
            stream = new Base64InputStream(resolver.openInputStream(mEmlFileUri));
        } catch (FileNotFoundException e) {
            LogUtils.e(LOG_TAG, e, "Could not find eml file at uri: %s", mEmlFileUri);
            return null;
        } catch (NullPointerException e) {
            LogUtils.e(LOG_TAG, e, "null stream, Could not find eml file at uri: %s", mEmlFileUri);
            return null;
        }

        final MimeMessage mimeMessage;
        ConversationMessage convMessage;
        try {
            mimeMessage = new MimeMessage(stream);
            convMessage = new ConversationMessage(context, mimeMessage, mEmlFileUri);
        } catch (IOException e) {
            LogUtils.e(LOG_TAG, e, "Could not read eml file");
            return null;
        } catch (MessagingException e) {
            LogUtils.e(LOG_TAG, e, "Error in parsing eml file");
            return null;
        } finally {
            try {
                stream.close();
            } catch (IOException e) {
                convMessage = null;
            }

            // delete temp files created during parsing
            final File[] cacheFiles = TempDirectory.getTempDirectory().listFiles();
            for (final File file : cacheFiles) {
                if (file.getName().startsWith("body")) {
                    final boolean deleted = file.delete();
                    if (!deleted) {
                        LogUtils.d(LOG_TAG, "Failed to delete temp file" + file.getName());
                    }
                }
            }
        }

        return convMessage;
    }
    //TS: zheng.zou 2016-03-31 EMAIL BUGFIX_1863915 ADD_E


    /**
     * Helper function to take care of releasing resources associated
     * with an actively loaded data set.
     */
    @Override
    protected void onDiscardResult(ConversationMessage message) {
        // if this eml message had attachments, start a service to clean up the cache files
        if (message.attachmentListUri != null) {
            final Intent intent = new Intent(Intent.ACTION_DELETE);
            intent.setClass(getContext(), EmlTempFileDeletionService.class);
            intent.setData(message.attachmentListUri);

            getContext().startService(intent);
        }
    }
}
