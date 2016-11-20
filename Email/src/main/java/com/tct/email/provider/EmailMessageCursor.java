/*
 * Copyright (C) 2014 The Android Open Source Project
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
/**
*===================================================================================================================
*HISTORY
*
*Tag             Date        Author          Description
*============== ============ =============== =======================================================================
*BUGFIX-852100 2014/12/10    wenggangjin     [Android5.0][Email]Can't display pictures in Email body
*BUGFIX-964325 2015/04/05    zhaotianyong    [Android5.0][Email][REG]Cannot show background color of a mail.
*BUGFIX-845079 2015/11/03    jian.xu         [Android L][Email]Email will ANR or exit automatically when checking the email with US-ASCII charset.
*BUGFIX-1039046  2015/12/25   chao.zhang     [Android L][Email][Force close][Monitor]Swipe mail to read next one,force close happened
 *BUGFIX-1291746  2016/01/05   chao.zhang    [Android 6.0][Email][Force close]forward a big mail,Email force close
====================================================================================================================
*/
package com.tct.email.provider;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.Html;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.util.Linkify;
import android.util.SparseArray;

import com.tct.emailcommon.provider.EmailContent.Body;
import com.tct.mail.providers.UIProvider;
import com.tct.mail.utils.LogUtils;
import com.tct.mail.utils.HtmlSanitizer;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * This class wraps a cursor for the purpose of bypassing the CursorWindow object for the
 * potentially over-sized body content fields. The CursorWindow has a hard limit of 2MB and so a
 * large email message can exceed that limit and cause the cursor to fail to load.
 *
 * To get around this, we load null values in those columns, and then in this wrapper we directly
 * load the content from the provider, skipping the cursor window.
 *
 * This will still potentially blow up if this cursor gets wrapped in a CrossProcessCursorWrapper
 * which uses a CursorWindow to shuffle results between processes. Since we're only using this for
 * passing a cursor back to UnifiedEmail this shouldn't be an issue.
 */
public class EmailMessageCursor extends CursorWrapper {

    private final SparseArray<String> mTextParts;
    private final SparseArray<String> mHtmlParts;
    private final int mTextColumnIndex;
    private final int mHtmlColumnIndex;
    //TS: jian.xu 2015-11-03 EMAIL BUGFIX-845079 ADD_S
    private final int mHtmlLinkifyColumnIndex;
    private final SparseArray<String> mHtmlLinkifyParts;
    private final int mTextLinkifyColumnIndex;
    private final SparseArray<String> mTextLinkifyParts;
    //TS: jian.xu 2015-11-03 EMAIL BUGFIX-845079 ADD_E

    public EmailMessageCursor(final Context c, final Cursor cursor, final String htmlColumn,
            final String textColumn) {
        super(cursor);
        mHtmlColumnIndex = cursor.getColumnIndex(htmlColumn);
        mTextColumnIndex = cursor.getColumnIndex(textColumn);
        final int cursorSize = cursor.getCount();
        mHtmlParts = new SparseArray<String>(cursorSize);
        mTextParts = new SparseArray<String>(cursorSize);
        //TS: jian.xu 2015-11-03 EMAIL BUGFIX-845079 ADD_S
        mHtmlLinkifyColumnIndex = cursor.getColumnIndex(UIProvider.MessageColumns.BODY_HTML_LINKIFY);
        mHtmlLinkifyParts = new SparseArray<String>(cursorSize);
        mTextLinkifyColumnIndex = cursor.getColumnIndex(UIProvider.MessageColumns.BODY_TEXT_LINKIFY);
        mTextLinkifyParts = new SparseArray<String>(cursorSize);
        //TS: jian.xu 2015-11-03 EMAIL BUGFIX-845079 ADD_E

        final ContentResolver cr = c.getContentResolver();

        while (cursor.moveToNext()) {
            final int position = cursor.getPosition();
            final long messageId = cursor.getLong(cursor.getColumnIndex(BaseColumns._ID));
            // TS: chao.zhang 2015-09-14 EMAIL BUGFIX-1039046  MOD_S
            InputStream htmlIn = null;
            InputStream textIn = null;
            try {
                if (mHtmlColumnIndex != -1) {
                    final Uri htmlUri = Body.getBodyHtmlUriForMessageWithId(messageId);
                    //WARNING: Actually openInput will used 2 PIPE(FD) to connect,but if some exception happen during connect,
                    //such as fileNotFoundException,maybe the connection will not be closed. just a try!!!
                    htmlIn = cr.openInputStream(htmlUri);
                    final String underlyingHtmlString;
                    try {
                        underlyingHtmlString = IOUtils.toString(htmlIn);
                    } finally {
                        htmlIn.close();
                        htmlIn = null;
                    }
                    //TS: zhaotianyong 2015-04-05 EMAIL BUGFIX_964325 MOD_S
                    final String sanitizedHtml = HtmlSanitizer.sanitizeHtml(underlyingHtmlString);
                    mHtmlParts.put(position, sanitizedHtml);
                    //TS: zhaotianyong 2015-04-05 EMAIL BUGFIX_964325 MOD_E
                    //TS: jian.xu 2015-11-03 EMAIL BUGFIX-845079 ADD_S
                    //NOTE: add links for sanitized html
                    if (!TextUtils.isEmpty(sanitizedHtml)) {
                        final String linkifyHtml = com.tct.mail.utils.Linkify.addLinks(sanitizedHtml);
                        mHtmlLinkifyParts.put(position, linkifyHtml);
                    } else {
                        mHtmlLinkifyParts.put(position, "");
                    }
                    //TS: jian.xu 2015-11-03 EMAIL BUGFIX-845079 ADD_E
                }
            } catch (final IOException e) {
                LogUtils.v(LogUtils.TAG, e, "Did not find html body for message %d", messageId);
            } catch (final OutOfMemoryError oom) {
                LogUtils.v(LogUtils.TAG, oom, "Terrible,OOM happen durning query EmailMessageCursor in bodyHtml,current message %d", messageId);
                mHtmlLinkifyParts.put(position, "");
            }
            try {
                if (mTextColumnIndex != -1) {
                    final Uri textUri = Body.getBodyTextUriForMessageWithId(messageId);
                    textIn = cr.openInputStream(textUri);
                    final String underlyingTextString;
                    try {
                        underlyingTextString = IOUtils.toString(textIn);
                    } finally {
                        textIn.close();
                        textIn = null;
                    }
                    mTextParts.put(position, underlyingTextString);
                    //TS: jian.xu 2015-11-03 EMAIL BUGFIX-845079 ADD_S
                    //NOTE: add links for underlying text string
                    if (!TextUtils.isEmpty(underlyingTextString)) {
                        final SpannableString spannable = new SpannableString(underlyingTextString);
                        Linkify.addLinks(spannable, Linkify.EMAIL_ADDRESSES | Linkify.WEB_URLS | Linkify.PHONE_NUMBERS);
                        final String linkifyText = Html.toHtml(spannable);
                        mTextLinkifyParts.put(position, linkifyText);
                    } else {
                        mTextLinkifyParts.put(position, "");
                    }
                    //TS: jian.xu 2015-11-03 EMAIL BUGFIX-845079 ADD_E
                }
            } catch (final IOException e) {
                LogUtils.v(LogUtils.TAG, e, "Did not find text body for message %d", messageId);
            } catch (final OutOfMemoryError oom) {
                LogUtils.v(LogUtils.TAG, oom, "Terrible,OOM happen durning query EmailMessageCursor in bodyText,current message %d", messageId);
                mTextLinkifyParts.put(position, "");
            }
        //NOTE:Remember that this just a protective code,for better release Not used Resources.
        if (htmlIn != null) {
            try {
                htmlIn.close();
            } catch (IOException e1) {
                LogUtils.v(LogUtils.TAG, e1, "IOException happen while close the htmlInput connection ");
            }
        }
        if (textIn != null) {
            try {
                textIn.close();
            } catch (IOException e2) {
                LogUtils.v(LogUtils.TAG, e2, "IOException happen while close the textInput connection ");
            }
        }// TS: chao.zhang 2015-09-14 EMAIL BUGFIX-1039046  MOD_E
        }
        cursor.moveToPosition(-1);
    }

    @Override
    public String getString(final int columnIndex) {
        if (columnIndex == mHtmlColumnIndex) {
            return mHtmlParts.get(getPosition());
        } else if (columnIndex == mTextColumnIndex) {
            return mTextParts.get(getPosition());
        //TS: jian.xu 2015-11-03 EMAIL BUGFIX-845079 ADD_S
        } else if (columnIndex == mHtmlLinkifyColumnIndex) {
            return mHtmlLinkifyParts.get(getPosition());
        } else if (columnIndex == mTextLinkifyColumnIndex) {
            return mTextLinkifyParts.get(getPosition());
        }
        //TS: jian.xu 2015-11-03 EMAIL BUGFIX-845079 ADD_E
        return super.getString(columnIndex);
    }

    @Override
    public int getType(int columnIndex) {
        //TS: jian.xu 2015-11-03 EMAIL BUGFIX-845079 MOD_S
        if (columnIndex == mHtmlColumnIndex || columnIndex == mTextColumnIndex ||
                columnIndex == mHtmlLinkifyColumnIndex || columnIndex == mTextLinkifyColumnIndex) {
            // Need to force this, otherwise we might fall through to some other get*() method
            // instead of getString() if the underlying cursor has other ideas about this content
            return FIELD_TYPE_STRING;
        } else {
            return super.getType(columnIndex);
        }
        //TS: jian.xu 2015-11-03 EMAIL BUGFIX-845079 MOD_E
    }
}

