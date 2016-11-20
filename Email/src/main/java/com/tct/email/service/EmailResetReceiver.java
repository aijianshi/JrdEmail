/******************************************************************************/
/*                                                               Date:04/2013 */
/*                                PRESENTATION                                */
/*                                                                            */
/*       Copyright 2012 TCL Communication Technology Holdings Limited.        */
/*                                                                            */
/* This material is company confidential, cannot be reproduced in any form    */
/* without the written permission of TCL Communication Technology Holdings    */
/* Limited.                                                                   */
/*                                                                            */
/* -------------------------------------------------------------------------- */
/*  Author :  wen.zhuang                                                      */
/*  Email  :  wen.zhuang@tcl-mobile.com                                       */
/*  Role   :                                                                  */
/*  Reference documents : AT&T req doc                                        */
/* -------------------------------------------------------------------------- */
/*  Comments : This file is used to reset related settings menu configures    */
/*  File     : vendor/tct/source/packages/apps/ATTEmail/tct-src/com/          */
/*             android/email/EmailResetReceiver.java                          */
/*  Labels   :                                                                */
/* -------------------------------------------------------------------------- */
/* ========================================================================== */
/*     Modifications on Features list / Changes Request / Problems Report     */
/* -------------------------------------------------------------------------- */
/*    date   |     author           |         Key          |     comment      */
/* ----------|----------------------|----------------------|----------------- */
/* 04/08/2013| wen.zhuang           |FR392974              |Email configures  */
/*           |                      |                      |reset             */
/* ----------|----------------------|----------------------|----------------- */
/******************************************************************************/
/*
 ========================================================================================================
 *HISTORY
 *
 *Tag            Date         Author          Description
 *============== ============ =============== ===========================================================
 *BUGFIX-1099956 2015/10/23   junwei-xu       [Settings]Email settings not reset to default value after device reset
 *BUGFIX_1127823 2015/12/29   yanhua.chen     [Device wipe][Email]"sync frequency" not select any option after Device reset
 ========================================================================================================
 */

package com.tct.email.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.ContentUris;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.provider.ContactsContract;
import android.util.Log;
import com.tct.emailcommon.provider.EmailContent;
import com.tct.emailcommon.utility.Utility;
import com.tct.mail.preferences.FolderPreferences;
import com.tct.mail.preferences.MailPrefs;
import com.tct.mail.providers.UIProvider;
import com.tct.mail.providers.Folder;
import com.tct.mail.utils.AccountUtils;
import com.tct.mail.utils.PLFUtils;
import com.tct.email.service.EmailServiceUtils.EmailServiceInfo;
import com.tct.emailcommon.provider.Account;
import com.tct.emailcommon.provider.HostAuth;
import com.tct.emailcommon.provider.Mailbox;
import com.tct.emailcommon.provider.EmailContent.MailboxColumns;

import java.util.ArrayList;
import java.util.List;

public class EmailResetReceiver extends BroadcastReceiver {
    private final String TAG = "EmailConfig";
    static final String PREFERENCES_FILE = "AndroidMail.Main";
    //TS: junwei-xu 2015-10-23 Email FEATURE-1099956 ADD_S
    private final List<Folder> mFolders = new ArrayList<Folder>();
    //TS: junwei-xu 2015-10-23 Email FEATURE-1099956 ADD_E

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        String action = intent.getAction();

        int count = this.getResultCode() + 1;
        this.setResultCode(count);

        Log.i(TAG,"Receive " + action);

        SharedPreferences mPref = context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
        Editor editor = mPref.edit();
        editor.clear();
        editor.apply();

        SharedPreferences defPref = PreferenceManager.getDefaultSharedPreferences(context);
        Editor editor2 = defPref.edit();
        editor2.clear();
        editor2.apply();

        //TS: junwei-xu 2015-10-23 Email FEATURE-1099956 ADD_S
        //NOTE: Reset general settings
        MailPrefs mailPrefs = MailPrefs.get(context);
        mailPrefs.setConversationListSwipeEnabled(true);
        mailPrefs.setShowSenderImages(true);
        mailPrefs.setDefaultReplyAll(false);
        mailPrefs.setConversationOverviewMode(true);
        mailPrefs.setAutoAdvanceMode(UIProvider.AutoAdvance.DEFAULT);
        mailPrefs.setConfirmDelete(true);//TS: yanhua.chen 2016-3-2 EMAIL BUGFIX_1715530 MOD
        mailPrefs.setConfirmArchive(false);
        mailPrefs.setConfirmSend(false);
        mailPrefs.setAutoDownloadRemaining(false);
        mailPrefs.setAlwaysBccMyself(false);

        for (com.tct.mail.providers.Account account : AccountUtils.getAccounts(context)) {
            if (account.getEmailAddress().equals("Combined view")) {
                continue;
            }

            Account emailAccount = Account.restoreAccountWithAddress(context, account.getEmailAddress());
            if (emailAccount == null) {
                continue;
            }
            //TS: yanhua.chen 2015-12-29 EMAIL BUGFIX_1127823 MOD_S
            String protocol = null;
            protocol = emailAccount.getProtocol(context, emailAccount.getId());
            boolean eas = HostAuth.SCHEME_EAS.equals(protocol);
            boolean defaultDownloadAll = PLFUtils.getBoolean(context, "def_Email_download_option");
            int downloadOption = defaultDownloadAll ? Utility.ENTIRE_MAIL : Utility.HEAD_ONLY;
            int defaultIntervalValues;
            if (eas) {
                // Notes: custom default for Def_Email_check_Frequency_Push for EAS Account
                defaultIntervalValues = Integer.parseInt(PLFUtils.getString(context, "def_email_checkFrequencyPush_default"));
            } else {
                // Notes: This is the root case. Default for POP3 and IMAP Account
                //        Here we change the interval according to SDMID:Def_Email_check_Frequency,
                //        for EAS account, we handle above
                defaultIntervalValues = Integer.parseInt(PLFUtils.getString(context, "def_email_checkFrequency_default"));
            }
            emailAccount.setSyncInterval(defaultIntervalValues);
            //TS: yanhua.chen 2015-12-29 EMAIL BUGFIX_1127823 MOD_E
            emailAccount.setSyncLookback(2);
            emailAccount.setSyncCalendarLookback(1);
            emailAccount.setDownloadOptions(downloadOption);
            emailAccount.setFlags(Account.FLAGS_BACKGROUND_ATTACHMENTS);
            emailAccount.update(context, emailAccount.toContentValues());

            final Cursor folderCursor = context.getContentResolver().query(account.settings.defaultInbox,
                    UIProvider.FOLDERS_PROJECTION, null, null, null);
            try {
                if (folderCursor != null && folderCursor.moveToFirst()) {
                    FolderPreferences inboxFolderPreferences = new FolderPreferences(context,
                            emailAccount.getEmailAddress(), new Folder(folderCursor), true);
                    inboxFolderPreferences.setNotificationsEnabled(true);
                    inboxFolderPreferences.setNotificationRingtoneUri(
                            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION).toString());
                    inboxFolderPreferences.setNotificationVibrateEnabled(false);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (folderCursor != null) {
                    folderCursor.close();
                }
            }

            //NOTE: Clear sync settings
            EmailServiceInfo mServiceInfo = EmailServiceUtils.getServiceInfo(context, emailAccount.getProtocol(context));

            final android.accounts.Account androidAccount = new android.accounts.Account(
                    emailAccount.getEmailAddress(), mServiceInfo.accountType);
            ContentResolver.setSyncAutomatically(androidAccount, EmailContent.AUTHORITY, true);
            ContentResolver.setSyncAutomatically(androidAccount, ContactsContract.AUTHORITY, true);
            ContentResolver.setSyncAutomatically(androidAccount, CalendarContract.AUTHORITY, true);

            //NOTE: Clear all the folder
            mFolders.clear();
            Uri uri = Uri.parse("content://" + EmailContent.AUTHORITY + "/uifullfolders/" + emailAccount.getId());
            try {
                final Cursor foldersCursor = context.getContentResolver().query(
                        uri, UIProvider.FOLDERS_PROJECTION, null, null, null);
                if (foldersCursor != null && foldersCursor.getCount() > 0) {
                    while (foldersCursor.moveToNext()) {
                        final Folder folder = new Folder(foldersCursor);
                        if (!folder.supportsCapability(UIProvider.FolderCapabilities.IS_VIRTUAL) &&
                                !folder.isTrash() && !folder.isDraft() && !folder.isOutbox()) {
                            mFolders.add(folder);
                        }
                    }
                }

                if (foldersCursor != null) {
                    foldersCursor.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            for (final Folder f : mFolders) {
                final ContentValues cv = new ContentValues(2);
                cv.put(MailboxColumns.SYNC_INTERVAL, 1);
                cv.put(MailboxColumns.SYNC_LOOKBACK, "");

                uri = ContentUris.withAppendedId(Mailbox.CONTENT_URI, f.id);
                context.getContentResolver().update(uri, cv, null, null);
            }
        }
        //TS: junwei-xu 2015-10-23 Email FEATURE-1099956 ADD_E
    }
}
