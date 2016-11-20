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
/* 06/11/2014|     Zhenhua Fan      |      FR 622609       |[Orange][Android  */
/*           |                      |                      |guidelines]IMAP   */
/*           |                      |                      |support           */
/* ----------|----------------------|----------------------|----------------- */
/* 05/21/2014|     zhonghua.tuo     |      FR 670064       |email search fun- */
/*           |                      |                      |ction             */
/* ----------|----------------------|----------------------|----------------- */
/* 04/08/2014|fu.zhang              |622697                |[HOMO][HOMO][Oran */
/*           |                      |                      |ge][Homologation] */
/*           |                      |                      | Exchange Active  */
/*           |                      |                      |Sync Priority     */
/* ----------|----------------------|----------------------|----------------- */
/*
=========================================================================================================
*HISTORY
*
*Tag            Date         Author          Description
*============== ============ =============== ============================================================
*CONFLICT-50009 2014/10/24   zhaotianyong    Modify the package conflict
*CONFLICT-50001 2014/10/24   zhaotianyong    Modify the package conflict
*CONFLICT-20001 2014/10/24   wenggangjin     Modify the package conflict
*BUGFIX-855270  2014/12/02   zhaotianyong    [Android5.0][Email] No Always bcc myself option in settings
*BUGFIX-851681  2014/12/04   zhaotianyong    [Email]The number of Starred box is not shown with Real-time.
*BUGFIX-852100  2014/12/10    wenggangjin    [Android5.0][Email]Can't display pictures in Email body
*BUGFIX-875367  2014/12/20   zhaotianyong    [Android5.0][Email][Crash] Email crash when saving a file in Sent/Outbox.
*BUGFIX-878947  12/24/2014   junwei-xu       [Email]Can not swipe to delete Email in Combined view conversation list
*BUGFIX-881366  2014/12/30   junwei-xu       [Email][Widget]Starred folder widget can not sync with Email
*BUGFIX-883410  2015/01/13   xiaolin.li      [Email]Search dysfunction;
*BUGFIX-881446  2015-01-15   wenggangjin     [Email]All attachment disappear after delete one attachment of Email in outbox
*BUGFIX-875230  2015-01-26   liming-wang     [Email]The reply icon will disappear when refreshing email
*BUGFIX-923513  2015-02-04   zhaotianyong    [HOMO][TMO EU 61564]It is not possible to put a comment in a response to a calendar invite.
*BUGFIX-917108  2015-02-11   gengkexue       [Email]The account can not be delete in Email settings after clear the recent email
*BUGFIX-929979  2015-02-13   gengkexue       [Email]Undo delete in Email combined view unsuccessfully
*BUGFIX-930462  2015/2/27    junwei-xu       [Email]Combined view folder wodget can not sync
*BUGFIX-936728  2015/3/3     zhonghua.tuo    [Email]Can not open vcs and apk file in draft box and outbox
*BUGFIX-940428  2015/03/06   zheng.zou       [Scenario Test][Email]Click Email starred invalid
*BUGFIX-944106  2015/03/12   peng-zhang      [FC][Email]Email FC when enter add contacts screen after delete the attachment from download.
*BUGFIX-945771  2015/03/13   zhaotianyong    [Email]Can not open vcs and apk file in draft box and outbox
*BUGFIX-944708  2015/03/18    zheng.zou      [Email]Can not stop search email when no network
*BUGFIX-930464  2015/3/23    junwei-xu       [Email]The icon of unread email is also displayed after the email account is removed
*BUGFIX_960193  2015/3/28    gengkexue       [Android5.0][Email]"From" field is wrong when compose with an email account which created by NOW SMS.
*BUGFIX_960875  2015/3/31    zhaotianyong    [SMC][email]com.tct.email happen crash due to java.lang.IllegalArgumentException
*BUGFIX_959895  2015/4/10    zhaotianyong    [REG][FC][Email]FC when open the special eml file in outbox/sent box add from downloads
*BUGFIX-978092  2015/04/16   zheng.zou       [Android5.0][Monitor][Crash][FOTA][Email]Email crash and pop up ANR message after input characters in search field.
*BUGFIX_980239  2015/04/23   junwei-xu       [REG][Email]"From" account is defult account when tap "add" button on another account widget
*BUGFIX_987931  2015/04/28   zhonghua.tuo    [SMC][email]com.tct.email happen crash due to java.lang.SecurityException
*BUGFIX_989306  2015/04/29   junwei-xu       [Email]The priority select option auto change to high after save email as attachment
*BUGFIX-947804  2015/05/22   zhonghua.tuo    [HOMO][ALWE] [Email] Email widget is not updated after some use
*BUGFIX-305581  2015/6/19    yanhua.chen     [Email]The attachment icon would not disappear after delete the attachment in the draft Edit Notification
*UGFIX-1006486  2015/6/23    chaozhang       [Monitor][Email]POP account can not receive new email sometimes
*UGFIX-1029647  2015/6/23    zheng.zou       [Email]Deleted mail display again when change between landscape and portrait
*BUGFIX-1029375  2015/7/23   Gantao          [Android 5.0][Email]Some mails with exchange account can't display completely after taping download remaining
*BUGFIX-1054379  2015/7/29    Gantao          [TMO][38283][Email]Can not edit respond before sending when accept Meeting reques
*BUGFIX-1063466 2015/08/07   jian.xu         [Email]When the user has "Delete email from server" turned off on the device, emails on the server will still be sent to the trash folder when deleted on the device.x
*BUGFIX-1064857  2015/8/10   tianjing.su    [Monitor][Android5.0][Email]Email Unread indicator keep displaying issue
*BUGFIX-1064857 2015/08/19   jian.xu         [Monitor][Android5.0][Email]Email Unread indicator keep displaying issue
*BUGFIX-1066136  2015/8/18   yanhua.chen     [Stability][Crash]Device crashed in email stability test #2
*FEATURE-526529      2015/08/12   Gantao         FEATURE--Always show pictures
*CR_585337      2015-09-16  chao.zhang       Exchange Email resend mechanism
*BUGFIX_1072954  2015/8/28   yanhua.chen     [Stability][Crash][Email]android.database.sqlite.SQLiteCantOpenDatabaseException: unable to open database file
*BUGFIX-570084  2015/09/19   Gantao         [Android L][Email]POP/IMAP account cannot display the attachment  which send from TCL account
*BUGFIX_667469 2015/09/25    zheng.zou       check multi user for upgrade to M
*BUGFIX-677793   2015-09-29   zheng.zou      [Email]Print test log to sd card[Email]Print test log to sd card
*BUGFIX-1079491 2015/09/06   jin.dong        [Android 5][Email]It not release data after archive all mail to local.
*BUGFIX_499604  2015/08/18   junwei-xu       [Email]The mail in drafts list displays abnormally after move to inbox
*BUGFIX-723337   2015-10-14   zheng.zou      [Email]Print test log to sd card
*BUGFIX-679699 2015/10/15     Gantao         [Android L][Email]The recipient can not receive the content when response a meeting invite again
*BUGFIX-721773 2015/08/19    jian.xu         [Android L][Email]The mail in draft can select multiple target folder to move
*BUGFIX-814477  2015/10/29   junwei-xu       [Android L][Email][UI]There are color bar in unread and starred box
*BUGFIX-839306  2015/10/29   jin.dong        [Android L][Email]The notification not disappear after remove the account
*BUGFIX-858353c 2015/11/03  zheng.zou       [Email]Optimize Exchange smart-forward/smart-reply
*BUGFIX-1102048   2015-10-28  zheng.zou      [Monitor][Email]TCL account setup is worked abnormal and the notification of "Security update required" is always existing
*BUGFIX-944797  2015-11-26   jian.xu         [Android L][Email]Retry notification not disappear after reconnect wifi
*BUGFIX-928059  2015-12-02   jian.xu         [Android L][Email][Monitor]Other account can choose exchange contacts to send mails
*BUGFIX-861247  2015-12-17   zheng.zou       Receive a mail with many times bell ring
*BUGFIX-1271032  2016/01/06  lin.zhou       [Andorid 6.0][Email][Ergo]Overflow page,folder selected style is different from GD
 *BUGFIX_1162996 2015/1/20   yanhua.chen    [Android 6.0][Email]TCL account pop up permission needed window continuously if disable contact/calendar permission of exchange
*BUGFIX-1539364  2016/02/18   junwei-xu      [Email]The unread point of Email icon will disappear after change the system language
*BUGFIX-1659885  2016/02/26   junwei-xu      [Android 6.0][Email]send a draft mail out and mail still displayed in draft
*FEATURE-1778597 2016/03/10   tianjing.su    [Email]Configuration message can't install
*BUGFIX-1739457  2016/03/19   chao-zhang     [Stability][Email]mail not sent in 2 mins
*BUGFIX-1886442  2016/04/01   junwei-xu      [GAPP][Force Close][Email]Email appear FC when save attachement wihch name's first character is "#".
=========================================================================================================
*/

/******************************************************************************/

package com.tct.email.provider;

import android.Manifest;
import android.accounts.AccountManager;
import android.appwidget.AppWidgetManager;
import android.content.ComponentCallbacks;
import android.content.ComponentName;
import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.PeriodicSync;
import android.content.SharedPreferences;
import android.content.UriMatcher;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.DatabaseUtils;
import android.database.MatrixCursor;
import android.database.MergeCursor;
import android.database.sqlite.SQLiteCantOpenDatabaseException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDiskIOException;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteStatement;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Looper;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.provider.BaseColumns;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Base64;
import android.util.Log;
import android.util.SparseArray;
import com.tct.email.EmailApplication;
import com.tct.email.LegacyConversions;
import com.tct.email.NotificationController;
import com.tct.email.Preferences;
import com.tct.email.SecurityPolicy;
import com.tct.email.activity.setup.AccountSettingsFragment;
import com.tct.email.activity.setup.AccountSettingsUtils;
import com.tct.email.activity.setup.HeadlessAccountSettingsLoader;
import com.tct.email.service.AttachmentService;
import com.tct.email.service.EmailServiceUtils;
import com.tct.email.service.EmailServiceUtils.EmailServiceInfo;
import com.tct.email2.ui.MailActivityEmail;
//TS: MOD by zhaotianyong for CONFLICT_50009 START
//import com.android.common.content.ProjectionMap;
import com.tct.fw.ex.common.content.ProjectionMap;
//TS: MOD by zhaotianyong for CONFLICT_50009 END
import com.tct.email.R;
import com.tct.emailcommon.Logging;
import com.tct.emailcommon.mail.Address;
import com.tct.emailcommon.provider.Account;
import com.tct.emailcommon.provider.Credential;
import com.tct.emailcommon.provider.EmailContent;
import com.tct.emailcommon.provider.EmailContent.AccountColumns;
import com.tct.emailcommon.provider.EmailContent.Attachment;
import com.tct.emailcommon.provider.EmailContent.AttachmentColumns;
import com.tct.emailcommon.provider.EmailContent.Body;
import com.tct.emailcommon.provider.EmailContent.BodyColumns;
import com.tct.emailcommon.provider.EmailContent.HostAuthColumns;
import com.tct.emailcommon.provider.EmailContent.MailboxColumns;
import com.tct.emailcommon.provider.EmailContent.Message;
import com.tct.emailcommon.provider.EmailContent.MessageColumns;
import com.tct.emailcommon.provider.EmailContent.PolicyColumns;
import com.tct.emailcommon.provider.EmailContent.QuickResponseColumns;
import com.tct.emailcommon.provider.EmailContent.SyncColumns;
import com.tct.emailcommon.provider.HostAuth;
import com.tct.emailcommon.provider.Mailbox;
import com.tct.emailcommon.provider.MailboxUtilities;
import com.tct.emailcommon.provider.MessageChangeLogTable;
import com.tct.emailcommon.provider.MessageMove;
import com.tct.emailcommon.provider.MessageStateChange;
import com.tct.emailcommon.provider.Policy;
import com.tct.emailcommon.provider.QuickResponse;
import com.tct.emailcommon.service.EmailServiceProxy;
import com.tct.emailcommon.service.EmailServiceStatus;
import com.tct.emailcommon.service.IEmailService;
import com.tct.emailcommon.service.SearchParams;
import com.tct.emailcommon.utility.AttachmentUtilities;
import com.tct.emailcommon.utility.EmailAsyncTask;
import com.tct.emailcommon.utility.StringCompressor;
import com.tct.emailcommon.utility.Utility;

//TS:MOD by zhaotianyong for CONFLICT_50001 START
//import com.tct.ex.photo.provider.PhotoContract;
import com.tct.fw.ex.photo.provider.PhotoContract;
//TS:MOD by zhaotianyong for CONFLICT_50001 START

import com.tct.mail.MailIntentService;
import com.tct.mail.utils.*;
//TS: MOD by wenggangjin for CONFLICT_20001 START
//import com.google.common.collect.ImmutableMap;
//import com.google.common.collect.ImmutableSet;
//import com.google.common.collect.Sets;
import com.tct.fw.google.common.collect.ImmutableMap;
import com.tct.fw.google.common.collect.ImmutableSet;
import com.tct.fw.google.common.collect.Sets;
import com.tct.mail.preferences.MailPrefs;
import com.tct.mail.preferences.MailPrefs.PreferenceKeys;
import com.tct.mail.providers.Folder;
import com.tct.mail.providers.FolderList;
import com.tct.mail.providers.Settings;
import com.tct.mail.providers.UIProvider;
import com.tct.mail.providers.UIProvider.AccountCapabilities;
import com.tct.mail.providers.UIProvider.AccountCursorExtraKeys;
import com.tct.mail.providers.UIProvider.ConversationPriority;
import com.tct.mail.providers.UIProvider.DraftType;
import com.tct.mail.providers.UIProvider.AccountColumns.SettingsColumns;
//TS: xiaolin.li 2014-12-10 EMAIL READ_PLF MOD_S
//TS: xiaolin.li 2014-12-10 EMAIL READ_PLF MOD_E
import com.tct.mail.widget.BaseWidgetProvider;
//TS: MOD by wenggangjin for CONFLICT_20001 END
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

//[FEATURE]-Add-BEGIN by CDTS.zhonghua.tuo,05/21/2014,FR 670064
import com.tct.emailcommon.mail.MessagingException;
//[FEATURE]-Add-END by CDTS.zhonghua.tuo

//[FEATURE]-Add-BEGIN by TSNJ.qinglian.zhang,10/28/2014,FR 736417
import com.tct.emailcommon.provider.EmailContent.AccountInfo;
import com.tct.permission.PermissionUtil;
//[FEATURE]-Add-END by TSNJ.qinglian.zhang

public class EmailProvider extends ContentProvider
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = LogTag.getLogTag();

    // Time to delay upsync requests.
    public static final long SYNC_DELAY_MILLIS = 30 * DateUtils.SECOND_IN_MILLIS;

    public static String EMAIL_APP_MIME_TYPE;

    // exposed for testing
    public static final String DATABASE_NAME = "EmailProvider.db";
    public static final String BODY_DATABASE_NAME = "EmailProviderBody.db";

    // We don't back up to the backup database anymore, just keep this constant here so we can
    // delete the old backups and trigger a new backup to the account manager
    @Deprecated
    private static final String BACKUP_DATABASE_NAME = "EmailProviderBackup.db";
    private static final String ACCOUNT_MANAGER_JSON_TAG = "accountJson";

    /**
     * Notifies that changes happened. Certain UI components, e.g., widgets, can register for this
     * {@link android.content.Intent} and update accordingly. However, this can be very broad and
     * is NOT the preferred way of getting notification.
     */
    private static final String ACTION_NOTIFY_MESSAGE_LIST_DATASET_CHANGED =
        "com.tct.email.MESSAGE_LIST_DATASET_CHANGED";

    private static final String EMAIL_MESSAGE_MIME_TYPE =
        "vnd.android.cursor.item/email-message";
    private static final String EMAIL_ATTACHMENT_MIME_TYPE =
        "vnd.android.cursor.item/email-attachment";

    /** Appended to the notification URI for delete operations */
    private static final String NOTIFICATION_OP_DELETE = "delete";
    /** Appended to the notification URI for insert operations */
    private static final String NOTIFICATION_OP_INSERT = "insert";
    /** Appended to the notification URI for update operations */
    private static final String NOTIFICATION_OP_UPDATE = "update";

    /** The query string to trigger a folder refresh. */
    protected static String QUERY_UIREFRESH = "uirefresh";

    // Definitions for our queries looking for orphaned messages
    private static final String[] ORPHANS_PROJECTION
        = new String[] {MessageColumns._ID, MessageColumns.MAILBOX_KEY};
    private static final int ORPHANS_ID = 0;
    private static final int ORPHANS_MAILBOX_KEY = 1;

    private static final String WHERE_ID = BaseColumns._ID + "=?";

    private static final int ACCOUNT_BASE = 0;
    private static final int ACCOUNT = ACCOUNT_BASE;
    private static final int ACCOUNT_ID = ACCOUNT_BASE + 1;
    private static final int ACCOUNT_CHECK = ACCOUNT_BASE + 2;
    private static final int ACCOUNT_PICK_TRASH_FOLDER = ACCOUNT_BASE + 3;
    private static final int ACCOUNT_PICK_SENT_FOLDER = ACCOUNT_BASE + 4;

    private static final int MAILBOX_BASE = 0x1000;
    private static final int MAILBOX = MAILBOX_BASE;
    private static final int MAILBOX_ID = MAILBOX_BASE + 1;
    private static final int MAILBOX_NOTIFICATION = MAILBOX_BASE + 2;
    private static final int MAILBOX_MOST_RECENT_MESSAGE = MAILBOX_BASE + 3;
    private static final int MAILBOX_MESSAGE_COUNT = MAILBOX_BASE + 4;
    private static final int MAILBOX_MOST_RECENT_UNREAD_MESSAGE = MAILBOX_BASE + 5;

    private static final int MESSAGE_BASE = 0x2000;
    private static final int MESSAGE = MESSAGE_BASE;
    private static final int MESSAGE_ID = MESSAGE_BASE + 1;
    private static final int SYNCED_MESSAGE_ID = MESSAGE_BASE + 2;
    private static final int MESSAGE_SELECTION = MESSAGE_BASE + 3;
    private static final int MESSAGE_MOVE = MESSAGE_BASE + 4;
    private static final int MESSAGE_STATE_CHANGE = MESSAGE_BASE + 5;

    private static final int ATTACHMENT_BASE = 0x3000;
    private static final int ATTACHMENT = ATTACHMENT_BASE;
    private static final int ATTACHMENT_ID = ATTACHMENT_BASE + 1;
    private static final int ATTACHMENTS_MESSAGE_ID = ATTACHMENT_BASE + 2;
    private static final int ATTACHMENTS_CACHED_FILE_ACCESS = ATTACHMENT_BASE + 3;

    private static final int HOSTAUTH_BASE = 0x4000;
    private static final int HOSTAUTH = HOSTAUTH_BASE;
    private static final int HOSTAUTH_ID = HOSTAUTH_BASE + 1;

    private static final int UPDATED_MESSAGE_BASE = 0x5000;
    private static final int UPDATED_MESSAGE = UPDATED_MESSAGE_BASE;
    private static final int UPDATED_MESSAGE_ID = UPDATED_MESSAGE_BASE + 1;

    private static final int DELETED_MESSAGE_BASE = 0x6000;
    private static final int DELETED_MESSAGE = DELETED_MESSAGE_BASE;
    private static final int DELETED_MESSAGE_ID = DELETED_MESSAGE_BASE + 1;

    private static final int POLICY_BASE = 0x7000;
    private static final int POLICY = POLICY_BASE;
    private static final int POLICY_ID = POLICY_BASE + 1;

    private static final int QUICK_RESPONSE_BASE = 0x8000;
    private static final int QUICK_RESPONSE = QUICK_RESPONSE_BASE;
    private static final int QUICK_RESPONSE_ID = QUICK_RESPONSE_BASE + 1;
    private static final int QUICK_RESPONSE_ACCOUNT_ID = QUICK_RESPONSE_BASE + 2;

    private static final int UI_BASE = 0x9000;
    private static final int UI_FOLDERS = UI_BASE;
    private static final int UI_SUBFOLDERS = UI_BASE + 1;
    private static final int UI_MESSAGES = UI_BASE + 2;
    private static final int UI_MESSAGE = UI_BASE + 3;
    private static final int UI_UNDO = UI_BASE + 4;
    private static final int UI_FOLDER_REFRESH = UI_BASE + 5;
    private static final int UI_FOLDER = UI_BASE + 6;
    private static final int UI_ACCOUNT = UI_BASE + 7;
    private static final int UI_ACCTS = UI_BASE + 8;
    private static final int UI_ATTACHMENTS = UI_BASE + 9;
    private static final int UI_ATTACHMENT = UI_BASE + 10;
    private static final int UI_ATTACHMENT_BY_CID = UI_BASE + 11;
    private static final int UI_SEARCH = UI_BASE + 12;
    private static final int UI_ACCOUNT_DATA = UI_BASE + 13;
    private static final int UI_FOLDER_LOAD_MORE = UI_BASE + 14;
    private static final int UI_CONVERSATION = UI_BASE + 15;
    private static final int UI_RECENT_FOLDERS = UI_BASE + 16;
    private static final int UI_DEFAULT_RECENT_FOLDERS = UI_BASE + 17;
    private static final int UI_FULL_FOLDERS = UI_BASE + 18;
    private static final int UI_ALL_FOLDERS = UI_BASE + 19;
    private static final int UI_PURGE_FOLDER = UI_BASE + 20;
    private static final int UI_INBOX = UI_BASE + 21;
    private static final int UI_ACCTSETTINGS = UI_BASE + 22;
    /// TCT: local search
    private static final int UI_LOCAL_SEARCH = UI_BASE + 24;
    //TS: junwei-xu 2016-02-18 EMAIL BUGFIX-1539364 ADD_S
    private static final int UI_ALL_UNREAD_MESSAGES = UI_BASE + 25;
    //TS: junwei-xu 2016-02-18 EMAIL BUGFIX-1539364 ADD_E

    private static final int BODY_BASE = 0xA000;
    private static final int BODY = BODY_BASE;
    private static final int BODY_ID = BODY_BASE + 1;
    private static final int BODY_HTML = BODY_BASE + 2;
    private static final int BODY_TEXT = BODY_BASE + 3;

    // TS: Gantao 2015-07-23 EMAIL BUGFIX-1029375 ADD_S
    private static final int BODY_LARGE = BODY_BASE + 4;
    // TS: Gantao 2015-07-23 EMAIL BUGFIX-1029375 ADD_E

    private static final int CREDENTIAL_BASE = 0xB000;
    private static final int CREDENTIAL = CREDENTIAL_BASE;
    private static final int CREDENTIAL_ID = CREDENTIAL_BASE + 1;

    private static final int BASE_SHIFT = 12;  // 12 bits to the base type: 0, 0x1000, 0x2000, etc.

    private static final SparseArray<String> TABLE_NAMES;

    //[FEATURE]-Add-BEGIN by TSNJ.qinglian.zhang,10/28/2014,FR 736417
    private static final int EXTRAL_TABLE_BASE = BODY_BASE;
    private static final int ACCOUNT_INFO_BASE = EXTRAL_TABLE_BASE + 0x1000;
    private static final int ACCOUNT_INFO = ACCOUNT_INFO_BASE;
    //[FEATURE]-Add-END by TSNJ.qinglian.zhang

    //TS: junwei-xu 2015-09-17 EMAIL BUGFIX-569939 ADD-S
    private static final int RECIPIENT_BASE = 0xC000;
    private static final int RECIPIENT = RECIPIENT_BASE;
    private static final int RECIPIENT_ID = RECIPIENT_BASE + 1;
    //TS: junwei-xu 2015-09-17 EMAIL BUGFIX-569939 ADD-E

    private static final int SEARCH_BODY_INSERT_LIMIT = 1024 * 1024 / 4;//TS: zheng.zou 2015-04-16 EMAIL BUGFIX_978092 ADD
    static {
        SparseArray<String> array = new SparseArray<String>(11);
        array.put(ACCOUNT_BASE >> BASE_SHIFT, Account.TABLE_NAME);
        array.put(MAILBOX_BASE >> BASE_SHIFT, Mailbox.TABLE_NAME);
        array.put(MESSAGE_BASE >> BASE_SHIFT, Message.TABLE_NAME);
        array.put(ATTACHMENT_BASE >> BASE_SHIFT, Attachment.TABLE_NAME);
        array.put(HOSTAUTH_BASE >> BASE_SHIFT, HostAuth.TABLE_NAME);
        array.put(UPDATED_MESSAGE_BASE >> BASE_SHIFT, Message.UPDATED_TABLE_NAME);
        array.put(DELETED_MESSAGE_BASE >> BASE_SHIFT, Message.DELETED_TABLE_NAME);
        array.put(POLICY_BASE >> BASE_SHIFT, Policy.TABLE_NAME);
        array.put(QUICK_RESPONSE_BASE >> BASE_SHIFT, QuickResponse.TABLE_NAME);
        array.put(UI_BASE >> BASE_SHIFT, null);
        array.put(BODY_BASE >> BASE_SHIFT, Body.TABLE_NAME);
        array.put(CREDENTIAL_BASE >> BASE_SHIFT, Credential.TABLE_NAME);
        //[FEATURE]-Add-BEGIN by TSNJ.qinglian.zhang,10/28/2014,FR 736417
        array.put(ACCOUNT_INFO_BASE >> BASE_SHIFT, AccountInfo.TABLE_NAME);
        //[FEATURE]-Add-END by TSNJ.qinglian.zhang
        //TS: junwei-xu 2015-09-17 EMAIL BUGFIX-569939 ADD-S
        array.put(RECIPIENT_BASE >> BASE_SHIFT, EmailContent.Recipient.TABLE_NAME);
        //TS: junwei-xu 2015-09-17 EMAIL BUGFIX-569939 ADD-E
        TABLE_NAMES = array;
    }

    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    /**
     * Functions which manipulate the database connection or files synchronize on this.
     * It's static because there can be multiple provider objects.
     * TODO: Do we actually need to synchronize across all DB access, not just connection creation?
     */
    private static final Object sDatabaseLock = new Object();

    /**
     * Let's only generate these SQL strings once, as they are used frequently
     * Note that this isn't relevant for table creation strings, since they are used only once
     */
    private static final String UPDATED_MESSAGE_INSERT = "insert or ignore into " +
        Message.UPDATED_TABLE_NAME + " select * from " + Message.TABLE_NAME + " where " +
        BaseColumns._ID + '=';

    private static final String UPDATED_MESSAGE_DELETE = "delete from " +
        Message.UPDATED_TABLE_NAME + " where " + BaseColumns._ID + '=';

    private static final String DELETED_MESSAGE_INSERT = "insert or replace into " +
        Message.DELETED_TABLE_NAME + " select * from " + Message.TABLE_NAME + " where " +
        BaseColumns._ID + '=';

    private static final String ORPHAN_BODY_MESSAGE_ID_SELECT =
            "select " + BodyColumns.MESSAGE_KEY + " from " + Body.TABLE_NAME +
                    " except select " + BaseColumns._ID + " from " + Message.TABLE_NAME;

    private static final String DELETE_ORPHAN_BODIES = "delete from " + Body.TABLE_NAME +
        " where " + BodyColumns.MESSAGE_KEY + " in " + '(' + ORPHAN_BODY_MESSAGE_ID_SELECT + ')';

    private static final String DELETE_BODY = "delete from " + Body.TABLE_NAME +
        " where " + BodyColumns.MESSAGE_KEY + '=';

    private static final ContentValues EMPTY_CONTENT_VALUES = new ContentValues();

    private static final String MESSAGE_URI_PARAMETER_MAILBOX_ID = "mailboxId";

    // For undo handling
    private int mLastSequence = -1;
    private final ArrayList<ContentProviderOperation> mLastSequenceOps =
            new ArrayList<ContentProviderOperation>();

    // Query parameter indicating the command came from UIProvider
    private static final String IS_UIPROVIDER = "is_uiprovider";

    private static final String SYNC_STATUS_CALLBACK_METHOD = "sync_status";
    /// TCT: The quicksearchbox application's package name.
    private static final String PACKAGE_QUICKSEARCHBOX = "com.android.quicksearchbox";

    private static final String[] MIME_TYPE_PROJECTION = new String[]{AttachmentColumns.MIME_TYPE};

    private static final String[] CACHED_FILE_QUERY_PROJECTION = new String[]
            { AttachmentColumns._ID, AttachmentColumns.FILENAME, AttachmentColumns.SIZE,
                    AttachmentColumns.CONTENT_URI };

    /**
     * Wrap the UriMatcher call so we can throw a runtime exception if an unknown Uri is passed in
     * @param uri the Uri to match
     * @return the match value
     */
    private static int findMatch(Uri uri, String methodName) {
        int match = sURIMatcher.match(uri);
        if (match < 0) {
            throw new IllegalArgumentException("Unknown uri: " + uri);
        } else if (Logging.LOGD) {
            LogUtils.v(TAG, methodName + ": uri=" + uri + ", match is " + match);
        }
        return match;
    }

    // exposed for testing
    public static Uri INTEGRITY_CHECK_URI;

    public static Uri ACCOUNT_BACKUP_URI;
    private static Uri FOLDER_STATUS_URI;

    private SQLiteDatabase mDatabase;
    private SQLiteDatabase mBodyDatabase;

    private Handler mDelayedSyncHandler;
    private final Set<SyncRequestMessage> mDelayedSyncRequests = new HashSet<SyncRequestMessage>();

    private static void reconcileAccountsAsync(final Context context) {
        if (context.getResources().getBoolean(R.bool.reconcile_accounts)) {
            EmailAsyncTask.runAsyncParallel(new Runnable() {
                @Override
                public void run() {
                    AccountReconciler.reconcileAccounts(context);
                }
            });
        }
    }

    //TS: zheng.zou 2015-09-30 EMAIL BUGFIX_677793 ADD_S
    private void cleanupLogfiles() {
        EmailAsyncTask.runAsyncSerial(new Runnable() {   //TS: zheng.zou 2015-10-14 EMAIL BUGFIX_723337 MOD
            @Override
            public void run() {
               FileUtils.cleanOldFile();
            }
        });
    }
    //TS: zheng.zou 2015-09-30 EMAIL BUGFIX_677793 ADD_E

    public static Uri uiUri(String type, long id) {
        return Uri.parse(uiUriString(type, id));
    }

    /**
     * Creates a URI string from a database ID (guaranteed to be unique).
     * @param type of the resource: uifolder, message, etc.
     * @param id the id of the resource.
     * @return uri string
     */
    public static String uiUriString(String type, long id) {
        return "content://" + EmailContent.AUTHORITY + "/" + type + ((id == -1) ? "" : ("/" + id));
    }

    /**
     * Orphan record deletion utility.  Generates a sqlite statement like:
     *  delete from <table> where <column> not in (select <foreignColumn> from <foreignTable>)
     * Exposed for testing.
     * @param db the EmailProvider database
     * @param table the table whose orphans are to be removed
     * @param column the column deletion will be based on
     * @param foreignColumn the column in the foreign table whose absence will trigger the deletion
     * @param foreignTable the foreign table
     */
    public static void deleteUnlinked(SQLiteDatabase db, String table, String column,
            String foreignColumn, String foreignTable) {
        int count = db.delete(table, column + " not in (select " + foreignColumn + " from " +
                foreignTable + ")", null);
        if (count > 0) {
            LogUtils.w(TAG, "Found " + count + " orphaned row(s) in " + table);
        }
    }
    //TS: tianjing.su 2016-03-10 EMAIL FEATURE-1778597 ADD_S
    public static boolean setServicesEnabledSync(Context context) {
        // Make sure we're initialized
        EmailContent.init(context);
        Cursor c = null;
        try {
            c = context.getContentResolver().query(
                    Account.CONTENT_URI,
                    Account.ID_PROJECTION,
                    null, null, null);
            boolean enable = (c != null ? c.getCount() > 0 : false);
            setServicesEnabled(context, enable);
            return enable;
        } finally {
            if (c != null) {
                c.close();
            }
        }
    }
    private static void setServicesEnabled(Context context, boolean enabled) {
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(
                new ComponentName(context, AttachmentService.class),
                enabled ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED :
                        PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);

        // TODO: Make sure that the AttachmentService responds to this request as it
        startOrStopService(enabled, context, new Intent(context, AttachmentService.class));
        final NotificationController controller =
                NotificationController.getInstance(context);

        if (controller != null) {
            controller.watchForMessages();
        }
    }
    private static void startOrStopService(boolean enabled, Context context, Intent intent) {
        if (enabled) {
            context.startService(intent);
        } else {
            context.stopService(intent);
        }
    }
     //TS: tianjing.su 2016-03-10 EMAIL FEATURE-1778597 ADD_E
    /**
     * Make sure that parentKeys match with parentServerId.
     * When we sync folders, we do two passes: First to create the mailbox rows, and second
     * to set the parentKeys. Two passes are needed because we won't know the parent's Id
     * until that row is inserted, and the order in which the rows are given is arbitrary.
     * If we crash while this operation is in progress, the parent keys can be left uninitialized.
     * @param db SQLiteDatabase to modify
     */
    private void fixParentKeys(SQLiteDatabase db) {
        LogUtils.d(TAG, "Fixing parent keys");

        // Update the parentKey for each mailbox row to match the _id of the row whose
        // serverId matches our parentServerId. This will leave parentKey blank for any
        // row that does not have a parentServerId

        // This is kind of a confusing sql statement, so here's the actual text of it,
        // for reference:
        //
        //   update mailbox set parentKey = (select _id from mailbox as b where
        //   mailbox.parentServerId=b.serverId and mailbox.parentServerId not null and
        //   mailbox.accountKey=b.accountKey)
        db.execSQL("update " + Mailbox.TABLE_NAME + " set " + MailboxColumns.PARENT_KEY + "="
                + "(select " + Mailbox._ID + " from " + Mailbox.TABLE_NAME + " as b where "
                + Mailbox.TABLE_NAME + "." + MailboxColumns.PARENT_SERVER_ID + "="
                + "b." + MailboxColumns.SERVER_ID + " and "
                + Mailbox.TABLE_NAME + "." + MailboxColumns.PARENT_SERVER_ID + " not null and "
                + Mailbox.TABLE_NAME + "." + MailboxColumns.ACCOUNT_KEY
                + "=b." + Mailbox.ACCOUNT_KEY + ")");

        // Top level folders can still have uninitialized parent keys. Update these
        // to indicate that the parent is -1.
        //
        //   update mailbox set parentKey = -1 where parentKey=0 or parentKey is null;
        db.execSQL("update " + Mailbox.TABLE_NAME + " set " + MailboxColumns.PARENT_KEY
                + "=" + Mailbox.NO_MAILBOX + " where " + MailboxColumns.PARENT_KEY
                + "=" + Mailbox.PARENT_KEY_UNINITIALIZED + " or " + MailboxColumns.PARENT_KEY
                + " is null");

    }

    // exposed for testing
    public SQLiteDatabase getDatabase(Context context) {
        synchronized (sDatabaseLock) {
            // Always return the cached database, if we've got one
            if (mDatabase != null) {
                return mDatabase;
            }

            // Whenever we create or re-cache the databases, make sure that we haven't lost one
            // to corruption
            checkDatabases();

            DBHelper.DatabaseHelper helper = new DBHelper.DatabaseHelper(context, DATABASE_NAME);
            mDatabase = helper.getWritableDatabase();
            DBHelper.BodyDatabaseHelper bodyHelper =
                    new DBHelper.BodyDatabaseHelper(context, BODY_DATABASE_NAME);
            mBodyDatabase = bodyHelper.getWritableDatabase();
            if (mBodyDatabase != null) {
                String bodyFileName = mBodyDatabase.getPath();
                mDatabase.execSQL("attach \"" + bodyFileName + "\" as BodyDatabase");
            }

            // Restore accounts if the database is corrupted...
            restoreIfNeeded(context, mDatabase);
            // Check for any orphaned Messages in the updated/deleted tables
            deleteMessageOrphans(mDatabase, Message.UPDATED_TABLE_NAME);
            deleteMessageOrphans(mDatabase, Message.DELETED_TABLE_NAME);
            // Delete orphaned mailboxes/messages/policies (account no longer exists)
            deleteUnlinked(mDatabase, Mailbox.TABLE_NAME, MailboxColumns.ACCOUNT_KEY,
                    AccountColumns._ID, Account.TABLE_NAME);
            deleteUnlinked(mDatabase, Message.TABLE_NAME, MessageColumns.ACCOUNT_KEY,
                    AccountColumns._ID, Account.TABLE_NAME);
            deleteUnlinked(mDatabase, Policy.TABLE_NAME, PolicyColumns._ID,
                    AccountColumns.POLICY_KEY, Account.TABLE_NAME);
            fixParentKeys(mDatabase);
            initUiProvider();
            return mDatabase;
        }
    }

    /**
     * Perform startup actions related to UI
     */
    private void initUiProvider() {
        // Clear mailbox sync status
        // TS: xiaolin.li 2015-01-13 EMAIL BUGFIX-883410
        mDatabase.execSQL("update " + Mailbox.TABLE_NAME + " set " + MailboxColumns.UI_SYNC_STATUS +
                "=" + com.tct.mail.utils.UIProvider.SyncStatus.NO_SYNC);
    }

    /**
     * Restore user Account and HostAuth data from our backup database
     */
    private static void restoreIfNeeded(Context context, SQLiteDatabase mainDatabase) {
        if (MailActivityEmail.DEBUG) {
            LogUtils.w(TAG, "restoreIfNeeded...");
        }
        // Check for legacy backup
        String legacyBackup = Preferences.getLegacyBackupPreference(context);
        // If there's a legacy backup, create a new-style backup and delete the legacy backup
        // In the 1:1000000000 chance that the user gets an app update just as his database becomes
        // corrupt, oh well...
        if (!TextUtils.isEmpty(legacyBackup)) {
            backupAccounts(context, mainDatabase);
            Preferences.clearLegacyBackupPreference(context);
            LogUtils.w(TAG, "Created new EmailProvider backup database");
            return;
        }

        // If there's a backup database (old style) delete it and trigger an account manager backup.
        // Roughly the same comment as above applies
        final File backupDb = context.getDatabasePath(BACKUP_DATABASE_NAME);
        if (backupDb.exists()) {
            backupAccounts(context, mainDatabase);
            context.deleteDatabase(BACKUP_DATABASE_NAME);
            LogUtils.w(TAG, "Migrated from backup database to account manager");
            return;
        }

        // If we have accounts, we're done
        if (DatabaseUtils.longForQuery(mainDatabase,
                                      "SELECT EXISTS (SELECT ? FROM " + Account.TABLE_NAME + " )",
                                      EmailContent.ID_PROJECTION) > 0) {
            if (MailActivityEmail.DEBUG) {
                LogUtils.w(TAG, "restoreIfNeeded: Account exists.");
            }
            return;
        }

        restoreAccounts(context);
    }

    /** {@inheritDoc} */
    @Override
    public void shutdown() {
        if (mDatabase != null) {
            mDatabase.close();
            mDatabase = null;
        }
        if (mBodyDatabase != null) {
            mBodyDatabase.close();
            mBodyDatabase = null;
        }
    }

    // exposed for testing
    public static void deleteMessageOrphans(SQLiteDatabase database, String tableName) {
        if (database != null) {
            // We'll look at all of the items in the table; there won't be many typically
            Cursor c = database.query(tableName, ORPHANS_PROJECTION, null, null, null, null, null);
            // Usually, there will be nothing in these tables, so make a quick check
            try {
                if (c.getCount() == 0) return;
                ArrayList<Long> foundMailboxes = new ArrayList<Long>();
                ArrayList<Long> notFoundMailboxes = new ArrayList<Long>();
                ArrayList<Long> deleteList = new ArrayList<Long>();
                String[] bindArray = new String[1];
                while (c.moveToNext()) {
                    // Get the mailbox key and see if we've already found this mailbox
                    // If so, we're fine
                    long mailboxId = c.getLong(ORPHANS_MAILBOX_KEY);
                    // If we already know this mailbox doesn't exist, mark the message for deletion
                    if (notFoundMailboxes.contains(mailboxId)) {
                        deleteList.add(c.getLong(ORPHANS_ID));
                    // If we don't know about this mailbox, we'll try to find it
                    } else if (!foundMailboxes.contains(mailboxId)) {
                        bindArray[0] = Long.toString(mailboxId);
                        Cursor boxCursor = database.query(Mailbox.TABLE_NAME,
                                Mailbox.ID_PROJECTION, WHERE_ID, bindArray, null, null, null);
                        try {
                            // If it exists, we'll add it to the "found" mailboxes
                            if (boxCursor.moveToFirst()) {
                                foundMailboxes.add(mailboxId);
                            // Otherwise, we'll add to "not found" and mark the message for deletion
                            } else {
                                notFoundMailboxes.add(mailboxId);
                                deleteList.add(c.getLong(ORPHANS_ID));
                            }
                        } finally {
                            boxCursor.close();
                        }
                    }
                }
                // Now, delete the orphan messages
                for (long messageId: deleteList) {
                    bindArray[0] = Long.toString(messageId);
                    database.delete(tableName, WHERE_ID, bindArray);
                }
            } finally {
                c.close();
            }
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        Log.d(TAG, "Delete: " + uri);
        final int match = findMatch(uri, "delete");
        final Context context = getContext();
        // Pick the correct database for this operation
        // If we're in a transaction already (which would happen during applyBatch), then the
        // body database is already attached to the email database and any attempt to use the
        // body database directly will result in a SQLiteException (the database is locked)
        final SQLiteDatabase db = getDatabase(context);
        final int table = match >> BASE_SHIFT;
        String id = "0";
        boolean messageDeletion = false;

        final String tableName = TABLE_NAMES.valueAt(table);
        int result = -1;

        try {
            if (match == MESSAGE_ID || match == SYNCED_MESSAGE_ID) {
                if (!uri.getBooleanQueryParameter(IS_UIPROVIDER, false)) {
                    notifyUIConversation(uri);
                }
            }
            switch (match) {
                case UI_MESSAGE:
                    return uiDeleteMessage(uri);
                case UI_ACCOUNT_DATA:
                    return uiDeleteAccountData(uri);
                case UI_ACCOUNT:
                    return uiDeleteAccount(uri);
                case UI_PURGE_FOLDER:
                    return uiPurgeFolder(uri);
                case MESSAGE_SELECTION:
                    Cursor findCursor = db.query(tableName, Message.ID_COLUMN_PROJECTION, selection,
                            selectionArgs, null, null, null);
                    try {
                        if (findCursor.moveToFirst()) {
                            return delete(ContentUris.withAppendedId(
                                    Message.CONTENT_URI,
                                    findCursor.getLong(Message.ID_COLUMNS_ID_COLUMN)),
                                    null, null);
                        } else {
                            return 0;
                        }
                    } finally {
                        findCursor.close();
                    }
                // These are cases in which one or more Messages might get deleted, either by
                // cascade or explicitly
                case MAILBOX_ID:
                case MAILBOX:
                case ACCOUNT_ID:
                case ACCOUNT:
                case MESSAGE:
                case SYNCED_MESSAGE_ID:
                case MESSAGE_ID:
                    // Handle lost Body records here, since this cannot be done in a trigger
                    // The process is:
                    //  1) Begin a transaction, ensuring that both databases are affected atomically
                    //  2) Do the requested deletion, with cascading deletions handled in triggers
                    //  3) End the transaction, committing all changes atomically
                    //
                    // Bodies are auto-deleted here;  Attachments are auto-deleted via trigger
                    messageDeletion = true;
                    db.beginTransaction();
                    break;
            }
            switch (match) {
                case BODY_ID:
                case DELETED_MESSAGE_ID:
                case SYNCED_MESSAGE_ID:
                case MESSAGE_ID:
                case UPDATED_MESSAGE_ID:
                case ATTACHMENT_ID:
                case MAILBOX_ID:
                case ACCOUNT_ID:
                case HOSTAUTH_ID:
                case POLICY_ID:
                case QUICK_RESPONSE_ID:
                case CREDENTIAL_ID:
                    id = uri.getPathSegments().get(1);
                    if (match == SYNCED_MESSAGE_ID) {
                        // For synced messages, first copy the old message to the deleted table and
                        // delete it from the updated table (in case it was updated first)
                        // Note that this is all within a transaction, for atomicity
                        db.execSQL(DELETED_MESSAGE_INSERT + id);
                        db.execSQL(UPDATED_MESSAGE_DELETE + id);
                    }

                    final long accountId;
                    if (match == MAILBOX_ID) {
                        accountId = Mailbox.getAccountIdForMailbox(context, id);
                    } else {
                        accountId = Account.NO_ACCOUNT;
                    }

                    //TS: jin.dong 2015-09-06 EMAIL BUGFIX_1079491 ADD_S
                    if (match == MESSAGE_ID) {
                        final long msgId = Long.valueOf(id);
                        final long msgAccountId = Account.getAccountIdForMessageId(context, msgId);
                        AttachmentUtilities.deleteAllAttachmentFiles(context, msgAccountId, Long.valueOf(id));
                    }
                    //TS: jin.dong 2015-09-06 EMAIL BUGFIX_1079491 ADD_E

                    result = db.delete(tableName, whereWithId(id, selection), selectionArgs);

                    if (match == ACCOUNT_ID) {
                        notifyUI(UIPROVIDER_ACCOUNT_NOTIFIER, id);
                        notifyUI(UIPROVIDER_ALL_ACCOUNTS_NOTIFIER, null);
                    } else if (match == MAILBOX_ID) {
                        notifyUIFolder(id, accountId);
                    } else if (match == ATTACHMENT_ID) {
                        notifyUI(UIPROVIDER_ATTACHMENT_NOTIFIER, id);
                    }
                    break;
                case ATTACHMENTS_MESSAGE_ID:
                    // All attachments for the given message
                    id = uri.getPathSegments().get(2);
                    result = db.delete(tableName,
                            whereWith(AttachmentColumns.MESSAGE_KEY + "=" + id, selection),
                            selectionArgs);
                    break;

                case BODY:
                case MESSAGE:
                case DELETED_MESSAGE:
                case UPDATED_MESSAGE:
                case ATTACHMENT:
                case MAILBOX:
                case ACCOUNT:
                case HOSTAUTH:
                case POLICY:
                    result = db.delete(tableName, selection, selectionArgs);
                  //TS: junwei-xu 2015-3-23 EMAIL BUGFIX_930464 ADD_S
                    boolean enableUnread = PLFUtils.getBoolean(getContext(), "feature_email_send_launcher_unread_on");
                    if(match == MAILBOX && enableUnread){
                        Utilities.sendUnreadCountToLauncher(getContext());
                    }
                  //TS: junwei-xu 2015-3-23 EMAIL BUGFIX_930464 ADD_E
                    break;
                case MESSAGE_MOVE:
                    db.delete(MessageMove.TABLE_NAME, selection, selectionArgs);
                    break;
                case MESSAGE_STATE_CHANGE:
                    db.delete(MessageStateChange.TABLE_NAME, selection, selectionArgs);
                    break;
                  //[FEATURE]-Add-BEGIN by TSNJ.qinglian.zhang,10/28/2014,FR 736417
                case ACCOUNT_INFO:
                    db.delete(tableName, selection, selectionArgs);
                    break;
                //[FEATURE]-Add-END by TSNJ.qingnlian.zhang
                //TS: junwei-xu 2015-12-02 EMAIL BUGFIX-928059  ADD-S
                case RECIPIENT:
                    db.delete(tableName, selection, selectionArgs);
                    break;
                //TS: junwei-xu 2015-12-02 EMAIL BUGFIX-928059  ADD-E
                default:
                    throw new IllegalArgumentException("Unknown URI " + uri);
            }
            if (messageDeletion) {
                if (match == MESSAGE_ID) {
                    // Delete the Body record associated with the deleted message
                    final ContentValues emptyValues = new ContentValues(2);
                    emptyValues.putNull(BodyColumns.HTML_CONTENT);
                    emptyValues.putNull(BodyColumns.TEXT_CONTENT);
                    final long messageId = Long.valueOf(id);
                    try {
                        writeBodyFiles(context, messageId, emptyValues);
                    } catch (final IllegalStateException e) {
                        LogUtils.v(LogUtils.TAG, e, "Exception while deleting bodies");
                    }
                    db.execSQL(DELETE_BODY + id);
                } else {
                    // Delete any orphaned Body records
                    final Cursor orphans = db.rawQuery(ORPHAN_BODY_MESSAGE_ID_SELECT, null);
                    try {
                        final ContentValues emptyValues = new ContentValues(2);
                        emptyValues.putNull(BodyColumns.HTML_CONTENT);
                        emptyValues.putNull(BodyColumns.TEXT_CONTENT);
                        while (orphans.moveToNext()) {
                            final long messageId = orphans.getLong(0);
                            try {
                                writeBodyFiles(context, messageId, emptyValues);
                            } catch (final IllegalStateException e) {
                                LogUtils.v(LogUtils.TAG, e, "Exception while deleting bodies");
                            }
                        }
                    } finally {
                        orphans.close();
                    }
                    db.execSQL(DELETE_ORPHAN_BODIES);
                }
                db.setTransactionSuccessful();
                // AM: Kexue.Geng 2015-02-11 EMAIL BUGFIX_917108 MOD_S
                if (match == ACCOUNT_ID) {
                    reconcileAccountsAsync(context);
                }
                // AM: Kexue.Geng 2015-02-11 EMAIL BUGFIX_917108 MOD_E
            }
        } catch (SQLiteException e) {
            checkDatabases();
            throw e;
        } finally {
            if (messageDeletion) {
                db.endTransaction();
            }
        }

        // Notify all notifier cursors
        sendNotifierChange(getBaseNotificationUri(match), NOTIFICATION_OP_DELETE, id);

        // Notify all email content cursors
        notifyUI(EmailContent.CONTENT_URI, null);
        return result;
    }

    @Override
    // Use the email- prefix because message, mailbox, and account are so generic (e.g. SMS, IM)
    public String getType(Uri uri) {
        int match = findMatch(uri, "getType");
        switch (match) {
            case BODY_ID:
                return "vnd.android.cursor.item/email-body";
            case BODY:
                return "vnd.android.cursor.dir/email-body";
            case UPDATED_MESSAGE_ID:
            case MESSAGE_ID:
                // NOTE: According to the framework folks, we're supposed to invent mime types as
                // a way of passing information to drag & drop recipients.
                // If there's a mailboxId parameter in the url, we respond with a mime type that
                // has -n appended, where n is the mailboxId of the message.  The drag & drop code
                // uses this information to know not to allow dragging the item to its own mailbox
                String mimeType = EMAIL_MESSAGE_MIME_TYPE;
                String mailboxId = uri.getQueryParameter(MESSAGE_URI_PARAMETER_MAILBOX_ID);
                if (mailboxId != null) {
                    mimeType += "-" + mailboxId;
                }
                return mimeType;
            case UPDATED_MESSAGE:
            case MESSAGE:
                return "vnd.android.cursor.dir/email-message";
            case MAILBOX:
                return "vnd.android.cursor.dir/email-mailbox";
            case MAILBOX_ID:
                return "vnd.android.cursor.item/email-mailbox";
            case ACCOUNT:
                return "vnd.android.cursor.dir/email-account";
            case ACCOUNT_ID:
                return "vnd.android.cursor.item/email-account";
            case ATTACHMENTS_MESSAGE_ID:
            case ATTACHMENT:
                return "vnd.android.cursor.dir/email-attachment";
            case ATTACHMENT_ID:
                return EMAIL_ATTACHMENT_MIME_TYPE;
            case HOSTAUTH:
                return "vnd.android.cursor.dir/email-hostauth";
            case HOSTAUTH_ID:
                return "vnd.android.cursor.item/email-hostauth";
            case ATTACHMENTS_CACHED_FILE_ACCESS: {
                SQLiteDatabase db = getDatabase(getContext());
                Cursor c = db.query(Attachment.TABLE_NAME, MIME_TYPE_PROJECTION,
                        AttachmentColumns.CACHED_FILE + "=?", new String[]{uri.toString()},
                        null, null, null, null);
                try {
                    if (c != null && c.moveToFirst()) {
                        return c.getString(0);
                    } else {
                        return null;
                    }
                } finally {
                    if (c != null) {
                        c.close();
                    }
                }
            }
            default:
                return null;
        }
    }

    // These URIs are used for specific UI notifications. We don't use EmailContent.CONTENT_URI
    // as the base because that gets spammed.
    // These can't be statically initialized because they depend on EmailContent.AUTHORITY
    private static Uri UIPROVIDER_CONVERSATION_NOTIFIER;
    private static Uri UIPROVIDER_FOLDER_NOTIFIER;
    private static Uri UIPROVIDER_FOLDERLIST_NOTIFIER;
    private static Uri UIPROVIDER_ACCOUNT_NOTIFIER;
    // Not currently used
    //public static Uri UIPROVIDER_SETTINGS_NOTIFIER;
    private static Uri UIPROVIDER_ATTACHMENT_NOTIFIER;
    private static Uri UIPROVIDER_ATTACHMENTS_NOTIFIER;
    public static Uri UIPROVIDER_ALL_ACCOUNTS_NOTIFIER;
    private static Uri UIPROVIDER_MESSAGE_NOTIFIER;
    private static Uri UIPROVIDER_RECENT_FOLDERS_NOTIFIER;

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Log.d(TAG, "Insert: " + uri);
        final int match = findMatch(uri, "insert");
        final Context context = getContext();

        // See the comment at delete(), above
        final SQLiteDatabase db = getDatabase(context);
        final int table = match >> BASE_SHIFT;
        String id = "0";
        long longId;

        // We do NOT allow setting of unreadCount/messageCount via the provider
        // These columns are maintained via triggers
        if (match == MAILBOX_ID || match == MAILBOX) {
            values.put(MailboxColumns.UNREAD_COUNT, 0);
            values.put(MailboxColumns.MESSAGE_COUNT, 0);
        }

        final Uri resultUri;

        try {
            switch (match) {
                case BODY:
                    final ContentValues dbValues = new ContentValues(values);
                    // Prune out the content we don't want in the DB
                    dbValues.remove(BodyColumns.HTML_CONTENT);
                    /// TCT: save text body to db for local search
//                    dbValues.remove(BodyColumns.TEXT_CONTENT);
                    //TS: zheng.zou 2015-04-16 EMAIL BUGFIX_978092 ADD_S
                    //limit the size of content to be inserted into body db for loacal search
                    try {
                        if (dbValues.containsKey(BodyColumns.TEXT_CONTENT)) {
                            String text = dbValues.getAsString(BodyColumns.TEXT_CONTENT);
                            //do not insert bodytext larger than 1MB(about 1024*1024/4 characters)
                            if (text.length() > SEARCH_BODY_INSERT_LIMIT) {
                                text = text.substring(0, SEARCH_BODY_INSERT_LIMIT);
                                dbValues.put(BodyColumns.TEXT_CONTENT, text);
                            }
                        }
                    } catch (Exception e) {
                        LogUtils.e(TAG, "substring error in body insert exception=" + e);
                    }
                    //TS: zheng.zou 2015-04-16 EMAIL BUGFIX_978092 ADD_E
                    // TODO: move this to the message table
                    longId = db.insert(Body.TABLE_NAME, "foo", dbValues);
                    resultUri = ContentUris.withAppendedId(uri, longId);
                    // Write content to the filesystem where appropriate
                    // This will look less ugly once the body table is folded into the message table
                    // and we can just use longId instead
                    if (!values.containsKey(BodyColumns.MESSAGE_KEY)) {
                        throw new IllegalArgumentException(
                                "Cannot insert body without MESSAGE_KEY");
                    }
                    final long messageId = values.getAsLong(BodyColumns.MESSAGE_KEY);
                    writeBodyFiles(getContext(), messageId, values);
                    //TS: zheng.zou 2016-1-14 EMAIL TASK_1431225 ADD_S
                    //updte message size
                    int size= 0;
                    if (values.containsKey(BodyColumns.HTML_CONTENT)){
                        size = values.getAsString(BodyColumns.HTML_CONTENT).length();
                    } else if (values.containsKey(BodyColumns.TEXT_CONTENT)){
                        size = values.getAsString(BodyColumns.TEXT_CONTENT).length();
                    }
                    ContentValues messageValues = new ContentValues();
                    messageValues.put(MessageColumns.MESSAGE_SIZE, size);
                    db.update(Message.TABLE_NAME,messageValues,MessageColumns._ID + "=?",new String[]{String.valueOf(messageId)});
                    //TS: zheng.zou 2016-1-14 EMAIL TASK_1431225 ADD_E
                    break;
                //TS: junwei-xu 2015-09-17 EMAIL BUGFIX-569939 ADD-S
                case RECIPIENT:
                    longId = db.insert(TABLE_NAMES.valueAt(table), "foo", values);
                    resultUri = ContentUris.withAppendedId(EmailContent.Recipient.CONTENT_URI, longId);
                    break;
                //TS: junwei-xu 2015-09-17 EMAIL BUGFIX-569939 ADD-E
                // NOTE: It is NOT legal for production code to insert directly into UPDATED_MESSAGE
                // or DELETED_MESSAGE; see the comment below for details
                case UPDATED_MESSAGE:
                case DELETED_MESSAGE:
                case MESSAGE:
                    decodeEmailAddresses(values);
                case ATTACHMENT:
                case MAILBOX:
                case ACCOUNT:
                case HOSTAUTH:
                case CREDENTIAL:
                case POLICY:
                case QUICK_RESPONSE:
                    longId = db.insert(TABLE_NAMES.valueAt(table), "foo", values);
                    resultUri = ContentUris.withAppendedId(uri, longId);
                    switch(match) {
                        case MESSAGE:
                            final long mailboxId = values.getAsLong(MessageColumns.MAILBOX_KEY);
                            if (!uri.getBooleanQueryParameter(IS_UIPROVIDER, false)) {
                                notifyUIConversationMailbox(mailboxId);
                            }
                            notifyUIFolder(mailboxId, values.getAsLong(MessageColumns.ACCOUNT_KEY));
                            break;
                        case MAILBOX:
                            if (values.containsKey(MailboxColumns.TYPE)) {
                                if (values.getAsInteger(MailboxColumns.TYPE) <
                                        Mailbox.TYPE_NOT_EMAIL) {
                                    // Notify the account when a new mailbox is added
                                    final Long accountId =
                                            values.getAsLong(MailboxColumns.ACCOUNT_KEY);
                                    if (accountId != null && accountId > 0) {
                                        notifyUI(UIPROVIDER_ACCOUNT_NOTIFIER, accountId);
                                        notifyUI(UIPROVIDER_ALL_ACCOUNTS_NOTIFIER, null); // AM: Kexue.Geng 2015-03-28 EMAIL BUGFIX_960193 MOD
                                        notifyUI(UIPROVIDER_FOLDERLIST_NOTIFIER, accountId);
                                    }
                                }
                            }
                            break;
                        case ACCOUNT:
                            updateAccountSyncInterval(longId, values);
                            if (!uri.getBooleanQueryParameter(IS_UIPROVIDER, false)) {
                                notifyUIAccount(longId);
                            }
                            notifyUI(UIPROVIDER_ALL_ACCOUNTS_NOTIFIER, null);
                            break;
                        case UPDATED_MESSAGE:
                        case DELETED_MESSAGE:
                            throw new IllegalArgumentException("Unknown URL " + uri);
                        case ATTACHMENT:
                            int flags = 0;
                            if (values.containsKey(AttachmentColumns.FLAGS)) {
                                flags = values.getAsInteger(AttachmentColumns.FLAGS);
                            }
                            // Report all new attachments to the download service
                            if (TextUtils.isEmpty(values.getAsString(AttachmentColumns.LOCATION))) {
                                LogUtils.w(TAG, new Throwable(), "attachment with blank location");
                            }
                            mAttachmentService.attachmentChanged(getContext(), longId, flags);
                            break;
                    }
                    break;
                case QUICK_RESPONSE_ACCOUNT_ID:
                    longId = Long.parseLong(uri.getPathSegments().get(2));
                    values.put(QuickResponseColumns.ACCOUNT_KEY, longId);
                    return insert(QuickResponse.CONTENT_URI, values);
                case MAILBOX_ID:
                    // This implies adding a message to a mailbox
                    // Hmm, a problem here is that we can't link the account as well, so it must be
                    // already in the values...
                    longId = Long.parseLong(uri.getPathSegments().get(1));
                    values.put(MessageColumns.MAILBOX_KEY, longId);
                    return insert(Message.CONTENT_URI, values); // Recurse
                case MESSAGE_ID:
                    // This implies adding an attachment to a message.
                    id = uri.getPathSegments().get(1);
                    longId = Long.parseLong(id);
                    values.put(AttachmentColumns.MESSAGE_KEY, longId);
                    return insert(Attachment.CONTENT_URI, values); // Recurse
                case ACCOUNT_ID:
                    // This implies adding a mailbox to an account.
                    longId = Long.parseLong(uri.getPathSegments().get(1));
                    values.put(MailboxColumns.ACCOUNT_KEY, longId);
                    return insert(Mailbox.CONTENT_URI, values); // Recurse
                case ATTACHMENTS_MESSAGE_ID:
                    longId = db.insert(TABLE_NAMES.valueAt(table), "foo", values);
                    resultUri = ContentUris.withAppendedId(Attachment.CONTENT_URI, longId);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown URL " + uri);
            }
        } catch (SQLiteException e) {
            checkDatabases();
            throw e;
        }

        // Notify all notifier cursors
        sendNotifierChange(getBaseNotificationUri(match), NOTIFICATION_OP_INSERT, id);

        // Notify all existing cursors.
        notifyUI(EmailContent.CONTENT_URI, null);
        return resultUri;
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        //[BUGFIX]-Mod-BEGIN by TCTNB.caixia.chen,01/07/2015,PR 893304
        EmailContent.init(context.getApplicationContext());
        init(context.getApplicationContext());
        // Do this last, so that EmailContent/EmailProvider are initialized
        MailActivityEmail.setServicesEnabledAsync(context.getApplicationContext());
        reconcileAccountsAsync(context.getApplicationContext());
        //[BUGFIX]-Mod-END by TCTNB.caixia.chen
        //TS: zheng.zou 2015-09-30 EMAIL BUGFIX_677793 ADD_S
        cleanupLogfiles();
        //TS: zheng.zou 2015-09-30 EMAIL BUGFIX_677793 ADD_E

        // Update widgets
        final Intent updateAllWidgetsIntent =
                new Intent(com.tct.mail.utils.Utils.ACTION_NOTIFY_DATASET_CHANGED);
        updateAllWidgetsIntent.putExtra(BaseWidgetProvider.EXTRA_UPDATE_ALL_WIDGETS, true);
        updateAllWidgetsIntent.setType(context.getString(R.string.application_mime_type));
        context.sendBroadcast(updateAllWidgetsIntent);

        // The combined account name changes on locale changes
        //[BUGFIX]-Mod-BEGIN by TCTNB.caixia.chen,01/07/2015,PR 893304
        final Configuration oldConfiguration =
                new Configuration(context.getApplicationContext().getResources().getConfiguration());
        //[BUGFIX]-Mod-END by TCTNB.caixia.chen
        context.registerComponentCallbacks(new ComponentCallbacks() {
            @Override
            public void onConfigurationChanged(Configuration configuration) {
                int delta = oldConfiguration.updateFrom(configuration);
                if (Configuration.needNewResources(delta, ActivityInfo.CONFIG_LOCALE)) {
                    notifyUIAccount(COMBINED_ACCOUNT_ID);
                }
            }

            @Override
            public void onLowMemory() {}
        });

        MailPrefs.get(context).registerOnSharedPreferenceChangeListener(this);
        /// TCT: Grant uri permission to global search
        grantUriPermission(getContext());
        return false;
    }

    private static void init(final Context context) {
        // Synchronize on the matcher rather than the class object to minimize risk of contention
        // & deadlock.
        synchronized (sURIMatcher) {
            // We use the existence of this variable as indicative of whether this function has
            // already run.
            if (INTEGRITY_CHECK_URI != null) {
                return;
            }
            INTEGRITY_CHECK_URI = Uri.parse("content://" + EmailContent.AUTHORITY +
                    "/integrityCheck");
            ACCOUNT_BACKUP_URI =
                    Uri.parse("content://" + EmailContent.AUTHORITY + "/accountBackup");
            FOLDER_STATUS_URI =
                    Uri.parse("content://" + EmailContent.AUTHORITY + "/status");
            EMAIL_APP_MIME_TYPE = context.getString(R.string.application_mime_type);

            final String uiNotificationAuthority =
                    EmailContent.EMAIL_PACKAGE_NAME + ".uinotifications";
            UIPROVIDER_CONVERSATION_NOTIFIER =
                    Uri.parse("content://" + uiNotificationAuthority + "/uimessages");
            UIPROVIDER_FOLDER_NOTIFIER =
                    Uri.parse("content://" + uiNotificationAuthority + "/uifolder");
            UIPROVIDER_FOLDERLIST_NOTIFIER =
                    Uri.parse("content://" + uiNotificationAuthority + "/uifolders");
            UIPROVIDER_ACCOUNT_NOTIFIER =
                    Uri.parse("content://" + uiNotificationAuthority + "/uiaccount");
            // Not currently used
            /* UIPROVIDER_SETTINGS_NOTIFIER =
                    Uri.parse("content://" + uiNotificationAuthority + "/uisettings");*/
            UIPROVIDER_ATTACHMENT_NOTIFIER =
                    Uri.parse("content://" + uiNotificationAuthority + "/uiattachment");
            UIPROVIDER_ATTACHMENTS_NOTIFIER =
                    Uri.parse("content://" + uiNotificationAuthority + "/uiattachments");
            UIPROVIDER_ALL_ACCOUNTS_NOTIFIER =
                    Uri.parse("content://" + uiNotificationAuthority + "/uiaccts");
            UIPROVIDER_MESSAGE_NOTIFIER =
                    Uri.parse("content://" + uiNotificationAuthority + "/uimessage");
            UIPROVIDER_RECENT_FOLDERS_NOTIFIER =
                    Uri.parse("content://" + uiNotificationAuthority + "/uirecentfolders");

            // All accounts
            sURIMatcher.addURI(EmailContent.AUTHORITY, "account", ACCOUNT);
            // A specific account
            // insert into this URI causes a mailbox to be added to the account
            sURIMatcher.addURI(EmailContent.AUTHORITY, "account/#", ACCOUNT_ID);
            sURIMatcher.addURI(EmailContent.AUTHORITY, "accountCheck/#", ACCOUNT_CHECK);

            // All mailboxes
            sURIMatcher.addURI(EmailContent.AUTHORITY, "mailbox", MAILBOX);
            // A specific mailbox
            // insert into this URI causes a message to be added to the mailbox
            // ** NOTE For now, the accountKey must be set manually in the values!
            sURIMatcher.addURI(EmailContent.AUTHORITY, "mailbox/*", MAILBOX_ID);
            sURIMatcher.addURI(EmailContent.AUTHORITY, "mailboxNotification/#",
                    MAILBOX_NOTIFICATION);
            sURIMatcher.addURI(EmailContent.AUTHORITY, "mailboxMostRecentUnread/#",
                    MAILBOX_MOST_RECENT_UNREAD_MESSAGE);    //TS:zheng.zou 2015-12-17 EMAIL BUGFIX_861247  ADD
            sURIMatcher.addURI(EmailContent.AUTHORITY, "mailboxMostRecentMessage/#",
                    MAILBOX_MOST_RECENT_MESSAGE);
            sURIMatcher.addURI(EmailContent.AUTHORITY, "mailboxCount/#", MAILBOX_MESSAGE_COUNT);

            // All messages
            sURIMatcher.addURI(EmailContent.AUTHORITY, "message", MESSAGE);
            // A specific message
            // insert into this URI causes an attachment to be added to the message
            sURIMatcher.addURI(EmailContent.AUTHORITY, "message/#", MESSAGE_ID);

            // A specific attachment
            sURIMatcher.addURI(EmailContent.AUTHORITY, "attachment", ATTACHMENT);
            // A specific attachment (the header information)
            sURIMatcher.addURI(EmailContent.AUTHORITY, "attachment/#", ATTACHMENT_ID);
            // The attachments of a specific message (query only) (insert & delete TBD)
            sURIMatcher.addURI(EmailContent.AUTHORITY, "attachment/message/#",
                    ATTACHMENTS_MESSAGE_ID);
            //TS: zhaotianyong 2014-12-20 EMAIL BUGFIX_875367 MOD_S
            sURIMatcher.addURI(EmailContent.AUTHORITY, "attachment/cachedFile/*",
                    ATTACHMENTS_CACHED_FILE_ACCESS);
            //TS: zhaotianyong 2014-12-20 EMAIL BUGFIX_875367 MOD_E

            // All mail bodies
            sURIMatcher.addURI(EmailContent.AUTHORITY, "body", BODY);
            // TS: Gantao 2015-07-23 EMAIL BUGFIX-1029375 ADD_S
            // All mail bodies for large size
            sURIMatcher.addURI(EmailContent.AUTHORITY, "bodyLarge", BODY_LARGE);
            // TS: Gantao 2015-07-23 EMAIL BUGFIX-1029375 ADD_E
            // A specific mail body
            sURIMatcher.addURI(EmailContent.AUTHORITY, "body/#", BODY_ID);
            // A specific HTML body part, for openFile
            sURIMatcher.addURI(EmailContent.AUTHORITY, "bodyHtml/#", BODY_HTML);
            // A specific text body part, for openFile
            sURIMatcher.addURI(EmailContent.AUTHORITY, "bodyText/#", BODY_TEXT);

            // All hostauth records
            sURIMatcher.addURI(EmailContent.AUTHORITY, "hostauth", HOSTAUTH);
            // A specific hostauth
            sURIMatcher.addURI(EmailContent.AUTHORITY, "hostauth/*", HOSTAUTH_ID);

            // All credential records
            sURIMatcher.addURI(EmailContent.AUTHORITY, "credential", CREDENTIAL);
            // A specific credential
            sURIMatcher.addURI(EmailContent.AUTHORITY, "credential/*", CREDENTIAL_ID);

            /**
             * THIS URI HAS SPECIAL SEMANTICS
             * ITS USE IS INTENDED FOR THE UI TO MARK CHANGES THAT NEED TO BE SYNCED BACK
             * TO A SERVER VIA A SYNC ADAPTER
             */
            sURIMatcher.addURI(EmailContent.AUTHORITY, "syncedMessage/#", SYNCED_MESSAGE_ID);
            sURIMatcher.addURI(EmailContent.AUTHORITY, "messageBySelection", MESSAGE_SELECTION);

            sURIMatcher.addURI(EmailContent.AUTHORITY, MessageMove.PATH, MESSAGE_MOVE);
            sURIMatcher.addURI(EmailContent.AUTHORITY, MessageStateChange.PATH,
                    MESSAGE_STATE_CHANGE);

            /**
             * THE URIs BELOW THIS POINT ARE INTENDED TO BE USED BY SYNC ADAPTERS ONLY
             * THEY REFER TO DATA CREATED AND MAINTAINED BY CALLS TO THE SYNCED_MESSAGE_ID URI
             * BY THE UI APPLICATION
             */
            // All deleted messages
            sURIMatcher.addURI(EmailContent.AUTHORITY, "deletedMessage", DELETED_MESSAGE);
            // A specific deleted message
            sURIMatcher.addURI(EmailContent.AUTHORITY, "deletedMessage/#", DELETED_MESSAGE_ID);

            // All updated messages
            sURIMatcher.addURI(EmailContent.AUTHORITY, "updatedMessage", UPDATED_MESSAGE);
            // A specific updated message
            sURIMatcher.addURI(EmailContent.AUTHORITY, "updatedMessage/#", UPDATED_MESSAGE_ID);

            sURIMatcher.addURI(EmailContent.AUTHORITY, "policy", POLICY);
            sURIMatcher.addURI(EmailContent.AUTHORITY, "policy/#", POLICY_ID);

            // All quick responses
            sURIMatcher.addURI(EmailContent.AUTHORITY, "quickresponse", QUICK_RESPONSE);
            // A specific quick response
            sURIMatcher.addURI(EmailContent.AUTHORITY, "quickresponse/#", QUICK_RESPONSE_ID);
            // All quick responses associated with a particular account id
            sURIMatcher.addURI(EmailContent.AUTHORITY, "quickresponse/account/#",
                    QUICK_RESPONSE_ACCOUNT_ID);

            //[FEATURE]-Add-BEGIN by TSNJ.qinglian.zhang,10/28/2014,FR 736417
            sURIMatcher.addURI(EmailContent.AUTHORITY, "accountinfo", ACCOUNT_INFO);
            //[FEATURE]-Add-END by TSNJ.qinglian.zhang
            //TS: junwei-xu 2015-09-17 EMAIL BUGFIX-569939 ADD-S
            sURIMatcher.addURI(EmailContent.AUTHORITY, "recipient", RECIPIENT);
            sURIMatcher.addURI(EmailContent.AUTHORITY, "recipient/#", RECIPIENT_ID);
            //TS: junwei-xu 2015-09-17 EMAIL BUGFIX-569939 ADD-E
            sURIMatcher.addURI(EmailContent.AUTHORITY, "uifolders/#", UI_FOLDERS);
            sURIMatcher.addURI(EmailContent.AUTHORITY, "uifullfolders/#", UI_FULL_FOLDERS);
            sURIMatcher.addURI(EmailContent.AUTHORITY, "uiallfolders/#", UI_ALL_FOLDERS);
            sURIMatcher.addURI(EmailContent.AUTHORITY, "uisubfolders/#", UI_SUBFOLDERS);
            sURIMatcher.addURI(EmailContent.AUTHORITY, "uimessages/#", UI_MESSAGES);
            sURIMatcher.addURI(EmailContent.AUTHORITY, "uimessage/#", UI_MESSAGE);
            sURIMatcher.addURI(EmailContent.AUTHORITY, "uiundo", UI_UNDO);
            sURIMatcher.addURI(EmailContent.AUTHORITY, QUERY_UIREFRESH + "/#", UI_FOLDER_REFRESH);
            // We listen to everything trailing uifolder/ since there might be an appVersion
            // as in Utils.appendVersionQueryParameter().
            sURIMatcher.addURI(EmailContent.AUTHORITY, "uifolder/*", UI_FOLDER);
            sURIMatcher.addURI(EmailContent.AUTHORITY, "uiinbox/#", UI_INBOX);
            sURIMatcher.addURI(EmailContent.AUTHORITY, "uiaccount/#", UI_ACCOUNT);
            sURIMatcher.addURI(EmailContent.AUTHORITY, "uiaccts", UI_ACCTS);
            sURIMatcher.addURI(EmailContent.AUTHORITY, "uiacctsettings", UI_ACCTSETTINGS);
            sURIMatcher.addURI(EmailContent.AUTHORITY, "uiattachments/#", UI_ATTACHMENTS);
            sURIMatcher.addURI(EmailContent.AUTHORITY, "uiattachment/#", UI_ATTACHMENT);
            sURIMatcher.addURI(EmailContent.AUTHORITY, "uiattachmentbycid/#/*",
                    UI_ATTACHMENT_BY_CID);
            sURIMatcher.addURI(EmailContent.AUTHORITY, "uisearch/#", UI_SEARCH);
            sURIMatcher.addURI(EmailContent.AUTHORITY, "uiaccountdata/#", UI_ACCOUNT_DATA);
            sURIMatcher.addURI(EmailContent.AUTHORITY, "uiloadmore/#", UI_FOLDER_LOAD_MORE);
            sURIMatcher.addURI(EmailContent.AUTHORITY, "uiconversation/#", UI_CONVERSATION);
            sURIMatcher.addURI(EmailContent.AUTHORITY, "uirecentfolders/#", UI_RECENT_FOLDERS);
            sURIMatcher.addURI(EmailContent.AUTHORITY, "uidefaultrecentfolders/#",
                    UI_DEFAULT_RECENT_FOLDERS);
            sURIMatcher.addURI(EmailContent.AUTHORITY, "pickTrashFolder/#",
                    ACCOUNT_PICK_TRASH_FOLDER);
            sURIMatcher.addURI(EmailContent.AUTHORITY, "pickSentFolder/#",
                    ACCOUNT_PICK_SENT_FOLDER);
            /// TCT: local search
            sURIMatcher.addURI(EmailContent.AUTHORITY, "uilocalsearch/#",
                    UI_LOCAL_SEARCH);
            sURIMatcher.addURI(EmailContent.AUTHORITY, "uipurgefolder/#", UI_PURGE_FOLDER);
            //TS: junwei-xu 2016-02-18 EMAIL BUGFIX-1539364 ADD_S
            sURIMatcher.addURI(EmailContent.AUTHORITY, "unreadNumTable/unreadNum", UI_ALL_UNREAD_MESSAGES);
            //TS: junwei-xu 2016-02-18 EMAIL BUGFIX-1539364 ADD_E
        }
    }

    /**
     * The idea here is that the two databases (EmailProvider.db and EmailProviderBody.db must
     * always be in sync (i.e. there are two database or NO databases).  This code will delete
     * any "orphan" database, so that both will be created together.  Note that an "orphan" database
     * will exist after either of the individual databases is deleted due to data corruption.
     */
    public void checkDatabases() {
        synchronized (sDatabaseLock) {
            // Uncache the databases
            if (mDatabase != null) {
                mDatabase = null;
            }
            if (mBodyDatabase != null) {
                mBodyDatabase = null;
            }
            // Look for orphans, and delete as necessary; these must always be in sync
            final File databaseFile = getContext().getDatabasePath(DATABASE_NAME);
            final File bodyFile = getContext().getDatabasePath(BODY_DATABASE_NAME);

            // TODO Make sure attachments are deleted
            if (databaseFile.exists() && !bodyFile.exists()) {
                LogUtils.w(TAG, "Deleting orphaned EmailProvider database...");
                getContext().deleteDatabase(DATABASE_NAME);
            } else if (bodyFile.exists() && !databaseFile.exists()) {
                LogUtils.w(TAG, "Deleting orphaned EmailProviderBody database...");
                getContext().deleteDatabase(BODY_DATABASE_NAME);
            }
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
            String sortOrder) {
        Cursor c = null;
        int match;
        try {
            match = findMatch(uri, "query");
        } catch (IllegalArgumentException e) {
            String uriString = uri.toString();
            // If we were passed an illegal uri, see if it ends in /-1
            // if so, and if substituting 0 for -1 results in a valid uri, return an empty cursor
            if (uriString != null && uriString.endsWith("/-1")) {
                uri = Uri.parse(uriString.substring(0, uriString.length() - 2) + "0");
                match = findMatch(uri, "query");
                switch (match) {
                    case BODY_ID:
                    case MESSAGE_ID:
                    case DELETED_MESSAGE_ID:
                    case UPDATED_MESSAGE_ID:
                    case ATTACHMENT_ID:
                    case MAILBOX_ID:
                    case ACCOUNT_ID:
                    case HOSTAUTH_ID:
                    case CREDENTIAL_ID:
                    case POLICY_ID:
                        return new MatrixCursorWithCachedColumns(projection, 0);
                }
            }
            throw e;
        }
        Context context = getContext();
        // See the comment at delete(), above
        SQLiteDatabase db = getDatabase(context);
        int table = match >> BASE_SHIFT;
        String limit = uri.getQueryParameter(EmailContent.PARAMETER_LIMIT);
        String id;

        String tableName = TABLE_NAMES.valueAt(table);
        try {
            switch (match) {
                // First, dispatch queries from UnifiedEmail
                case UI_SEARCH:
                    c = uiSearch(uri, projection);
                    return c;
                /// TCT: add for local search.@{
                case UI_LOCAL_SEARCH:
                    c = uiLocalSearch(uri, projection);
                    return c;
                /// @}
                case UI_ACCTS:
                    final String suppressParam =
                            uri.getQueryParameter(EmailContent.SUPPRESS_COMBINED_ACCOUNT_PARAM);
                    final boolean suppressCombined =
                            suppressParam != null && Boolean.parseBoolean(suppressParam);
                    c = uiAccounts(projection, suppressCombined);
                    return c;
                case UI_UNDO:
                    return uiUndo(projection);
                case UI_SUBFOLDERS:
                case UI_MESSAGES:
                case UI_MESSAGE:
                case UI_FOLDER:
                case UI_INBOX:
                case UI_ACCOUNT:
                case UI_ATTACHMENT:
                case UI_ATTACHMENTS:
                case UI_ATTACHMENT_BY_CID:
                case UI_CONVERSATION:
                case UI_RECENT_FOLDERS:
                case UI_FULL_FOLDERS:
                case UI_ALL_FOLDERS:
                    // For now, we don't allow selection criteria within these queries
                    if (selection != null || selectionArgs != null) {
                        throw new IllegalArgumentException("UI queries can't have selection/args");
                    }

                    final String seenParam = uri.getQueryParameter(UIProvider.SEEN_QUERY_PARAMETER);
                    final boolean unseenOnly =
                            seenParam != null && Boolean.FALSE.toString().equals(seenParam);

                    c = uiQuery(match, uri, projection, unseenOnly);
                    return c;
                case UI_FOLDERS:
                    c = uiFolders(uri, projection);
                    return c;
                case UI_FOLDER_LOAD_MORE:
                    c = uiFolderLoadMore(getMailbox(uri));
                    return c;
                case UI_FOLDER_REFRESH:
                    c = uiFolderRefresh(getMailbox(uri), 0);
                    return c;
                case MAILBOX_NOTIFICATION:
                    c = notificationQuery(uri);
                    return c;
                case MAILBOX_MOST_RECENT_MESSAGE:
                    c = mostRecentMessageQuery(uri);
                    return c;
                case MAILBOX_MESSAGE_COUNT:
                    c = getMailboxMessageCount(uri);
                    return c;
                //TS:zheng.zou 2015-12-17 EMAIL BUGFIX_861247  ADD_S
                case MAILBOX_MOST_RECENT_UNREAD_MESSAGE:
                    c = mostRecentUnreadMessageQuery(uri);
                    return c;
                //TS:zheng.zou 2015-12-17 EMAIL BUGFIX_861247  ADD_E
                case MESSAGE_MOVE:
                    return db.query(MessageMove.TABLE_NAME, projection, selection, selectionArgs,
                            null, null, sortOrder, limit);
                case MESSAGE_STATE_CHANGE:
                    return db.query(MessageStateChange.TABLE_NAME, projection, selection,
                            selectionArgs, null, null, sortOrder, limit);
                case MESSAGE:
                case UPDATED_MESSAGE:
                case DELETED_MESSAGE:
                case ATTACHMENT:
                case MAILBOX:
                case ACCOUNT:
                case HOSTAUTH:
                case CREDENTIAL:
                //[FEATURE]-Add-BEGIN by TSNJ.qinglian.zhang,10/28/2014,FR 736417
                    c = db.query(tableName, projection,
                    selection, selectionArgs, null, null, sortOrder);
                    break;
                //[FEATURE]-Add-END by TSNJ.qinglian.zhang
                //TS: junwei-xu 2015-09-17 EMAIL BUGFIX-569939 ADD-S
                case RECIPIENT:
                    c = db.query(tableName, projection, selection, selectionArgs, null, null, sortOrder);
                    break;
                //TS: junwei-xu 2015-09-17 EMAIL BUGFIX-569939 ADD-E
                case POLICY:
                    c = db.query(tableName, projection,
                            selection, selectionArgs, null, null, sortOrder, limit);
                    break;
                case QUICK_RESPONSE:
                    c = uiQuickResponse(projection);
                    break;
                case BODY:
                case BODY_ID: {
                    final ProjectionMap map = new ProjectionMap.Builder()
                            .addAll(projection)
                            .build();
                    /// TCT: let the query pass, when the query is triggered by local search
                    boolean isFromLocalSearch =
                            Body.LOCALSEARCH_CONTENT_PROJECTION.equals(projection);
                    if (map.containsKey(BodyColumns.HTML_CONTENT) ||
                            map.containsKey(BodyColumns.TEXT_CONTENT)&& !isFromLocalSearch) {
                        throw new IllegalArgumentException(
                                "Body content cannot be returned in the cursor");
                    }

                    final ContentValues cv = new ContentValues(2);
                    cv.put(BodyColumns.HTML_CONTENT_URI, "@" + uriWithColumn("bodyHtml",
                            BodyColumns.MESSAGE_KEY));
                    cv.put(BodyColumns.TEXT_CONTENT_URI, "@" + uriWithColumn("bodyText",
                            BodyColumns.MESSAGE_KEY));

                    final StringBuilder sb = genSelect(map, projection, cv);
                    sb.append(" FROM ").append(Body.TABLE_NAME);
                    if (match == BODY_ID) {
                        id = uri.getPathSegments().get(1);
                        sb.append(" WHERE ").append(whereWithId(id, selection));
                    } else if (!TextUtils.isEmpty(selection)) {
                        sb.append(" WHERE ").append(selection);
                    }
                    if (!TextUtils.isEmpty(sortOrder)) {
                        sb.append(" ORDER BY ").append(sortOrder);
                    }
                    if (!TextUtils.isEmpty(limit)) {
                        sb.append(" LIMIT ").append(limit);
                    }
                    c = db.rawQuery(sb.toString(), selectionArgs);
                    break;
                }
                case MESSAGE_ID:
                case DELETED_MESSAGE_ID:
                case UPDATED_MESSAGE_ID:
                case ATTACHMENT_ID:
                case MAILBOX_ID:
                case ACCOUNT_ID:
                case HOSTAUTH_ID:
                case CREDENTIAL_ID:
                case POLICY_ID:
                    id = uri.getPathSegments().get(1);
                    c = db.query(tableName, projection, whereWithId(id, selection),
                            selectionArgs, null, null, sortOrder, limit);
                    break;
                case QUICK_RESPONSE_ID:
                    id = uri.getPathSegments().get(1);
                    c = uiQuickResponseId(projection, id);
                    break;
                case ATTACHMENTS_MESSAGE_ID:
                    // All attachments for the given message
                    id = uri.getPathSegments().get(2);
                    c = db.query(Attachment.TABLE_NAME, projection,
                            whereWith(AttachmentColumns.MESSAGE_KEY + "=" + id, selection),
                            selectionArgs, null, null, sortOrder, limit);
                    break;
                case QUICK_RESPONSE_ACCOUNT_ID:
                    // All quick responses for the given account
                    id = uri.getPathSegments().get(2);
                    c = uiQuickResponseAccount(projection, id);
                    break;
//                // TS: zhaotianyong 2015-03-31 EMAIL BUGFIX-960875 ADD_S
//                case ATTACHMENTS_CACHED_FILE_ACCESS:
//                    // TS: zhaotianyong 2015-04-10 EMAIL BUGFIX-959895 DEL
//                    break;
//                // TS: zhaotianyong 2015-03-31 EMAIL BUGFIX-960875 ADD_E
                //TS: junwei-xu 2016-02-18 EMAIL BUGFIX-1539364 ADD_S
                case UI_ALL_UNREAD_MESSAGES:
                    c = uiCountUnreadMessages();
                    break;
                //TS: junwei-xu 2016-02-18 EMAIL BUGFIX-1539364 ADD_E
                case ATTACHMENTS_CACHED_FILE_ACCESS:
                    if (projection == null) {
                        projection =
                                new String[] {
                                        AttachmentUtilities.Columns._ID,
                                        AttachmentUtilities.Columns.DATA,
                                };
                    }
                    // Map the columns of our attachment table to the columns defined in
                    // AttachmentUtils. These are a superset of OpenableColumns.
                    // This mirrors similar code in AttachmentProvider.
                    c = db.query(Attachment.TABLE_NAME,
                            CACHED_FILE_QUERY_PROJECTION, AttachmentColumns.CACHED_FILE + "=?",
                            new String[]{uri.toString()}, null, null, null, null);
                    try {
                        if (c.getCount() > 1) {
                            LogUtils.e(TAG, "multiple results querying CACHED_FILE_ACCESS %s", uri);
                        }
                        if (c != null && c.moveToFirst()) {
                            MatrixCursor ret = new MatrixCursorWithCachedColumns(projection);
                            Object[] values = new Object[projection.length];
                            for (int i = 0, count = projection.length; i < count; i++) {
                                String column = projection[i];
                                if (AttachmentUtilities.Columns._ID.equals(column)) {
                                    values[i] = c.getLong(
                                            c.getColumnIndexOrThrow(AttachmentColumns._ID));
                                }
                                else if (AttachmentUtilities.Columns.DATA.equals(column)) {
                                    values[i] = c.getString(
                                            c.getColumnIndexOrThrow(AttachmentColumns.CONTENT_URI));
                                }
                                else if (AttachmentUtilities.Columns.DISPLAY_NAME.equals(column)) {
                                    values[i] = c.getString(
                                            c.getColumnIndexOrThrow(AttachmentColumns.FILENAME));
                                }
                                else if (AttachmentUtilities.Columns.SIZE.equals(column)) {
                                    values[i] = c.getInt(
                                            c.getColumnIndexOrThrow(AttachmentColumns.SIZE));
                                } else {
                                    LogUtils.e(TAG,
                                            "unexpected column %s requested for CACHED_FILE",
                                            column);
                                }
                            }
                            ret.addRow(values);
                            return ret;
                        }
                    } finally {
                        if (c !=  null) {
                            c.close();
                        }
                    }
                    return null;
                default:
                    throw new IllegalArgumentException("Unknown URI " + uri);
            }
        } catch (SQLiteException e) {
            checkDatabases();
            throw e;
        } catch (RuntimeException e) {
            checkDatabases();
            e.printStackTrace();
            throw e;
        } finally {
            if (c == null) {
                // This should never happen, but let's be sure to log it...
                // TODO: There are actually cases where c == null is expected, for example
                // UI_FOLDER_LOAD_MORE.
                // Demoting this to a warning for now until we figure out what to do with it.
                LogUtils.w(TAG, "Query returning null for uri: %s selection: %s", uri, selection);
            }
        }

        if ((c != null) && !isTemporary()) {
            c.setNotificationUri(getContext().getContentResolver(), uri);
        }
        return c;
    }

    private static String whereWithId(String id, String selection) {
        StringBuilder sb = new StringBuilder(256);
        sb.append("_id=");
        sb.append(id);
        if (selection != null) {
            sb.append(" AND (");
            sb.append(selection);
            sb.append(')');
        }
        return sb.toString();
    }

    /**
     * Combine a locally-generated selection with a user-provided selection
     *
     * This introduces risk that the local selection might insert incorrect chars
     * into the SQL, so use caution.
     *
     * @param where locally-generated selection, must not be null
     * @param selection user-provided selection, may be null
     * @return a single selection string
     */
    private static String whereWith(String where, String selection) {
        if (selection == null) {
            return where;
        }
        return where + " AND (" + selection + ")";
    }

    /**
     * Restore a HostAuth from a database, given its unique id
     * @param db the database
     * @param id the unique id (_id) of the row
     * @return a fully populated HostAuth or null if the row does not exist
     */
    private static HostAuth restoreHostAuth(SQLiteDatabase db, long id) {
        Cursor c = db.query(HostAuth.TABLE_NAME, HostAuth.CONTENT_PROJECTION,
                HostAuthColumns._ID + "=?", new String[] {Long.toString(id)}, null, null, null);
        try {
            if (c.moveToFirst()) {
                HostAuth hostAuth = new HostAuth();
                hostAuth.restore(c);
                return hostAuth;
            }
            return null;
        } finally {
            c.close();
        }
    }

    /**
     * Copy the Account and HostAuth tables from one database to another
     * @param fromDatabase the source database
     * @param toDatabase the destination database
     * @return the number of accounts copied, or -1 if an error occurred
     */
    private static int copyAccountTables(SQLiteDatabase fromDatabase, SQLiteDatabase toDatabase) {
        if (fromDatabase == null || toDatabase == null) return -1;

        // Lock both databases; for the "from" database, we don't want anyone changing it from
        // under us; for the "to" database, we want to make the operation atomic
        int copyCount = 0;
        fromDatabase.beginTransaction();
        try {
            toDatabase.beginTransaction();
            try {
                // Delete anything hanging around here
                toDatabase.delete(Account.TABLE_NAME, null, null);
                toDatabase.delete(HostAuth.TABLE_NAME, null, null);

                // Get our account cursor
                Cursor c = fromDatabase.query(Account.TABLE_NAME, Account.CONTENT_PROJECTION,
                        null, null, null, null, null);
                if (c == null) return 0;
                LogUtils.d(TAG, "fromDatabase accounts: " + c.getCount());
                try {
                    // Loop through accounts, copying them and associated host auth's
                    while (c.moveToNext()) {
                        Account account = new Account();
                        account.restore(c);

                        // Clear security sync key and sync key, as these were specific to the
                        // state of the account, and we've reset that...
                        // Clear policy key so that we can re-establish policies from the server
                        // TODO This is pretty EAS specific, but there's a lot of that around
                        account.mSecuritySyncKey = null;
                        account.mSyncKey = null;
                        account.mPolicyKey = 0;

                        // Copy host auth's and update foreign keys
                        HostAuth hostAuth = restoreHostAuth(fromDatabase,
                                account.mHostAuthKeyRecv);

                        // The account might have gone away, though very unlikely
                        if (hostAuth == null) continue;
                        account.mHostAuthKeyRecv = toDatabase.insert(HostAuth.TABLE_NAME, null,
                                hostAuth.toContentValues());

                        // EAS accounts have no send HostAuth
                        if (account.mHostAuthKeySend > 0) {
                            hostAuth = restoreHostAuth(fromDatabase, account.mHostAuthKeySend);
                            // Belt and suspenders; I can't imagine that this is possible,
                            // since we checked the validity of the account above, and the
                            // database is now locked
                            if (hostAuth == null) continue;
                            account.mHostAuthKeySend = toDatabase.insert(
                                    HostAuth.TABLE_NAME, null, hostAuth.toContentValues());
                        }

                        // Now, create the account in the "to" database
                        toDatabase.insert(Account.TABLE_NAME, null, account.toContentValues());
                        copyCount++;
                    }
                } finally {
                    c.close();
                }

                // Say it's ok to commit
                toDatabase.setTransactionSuccessful();
            } finally {
                toDatabase.endTransaction();
            }
        } catch (SQLiteException ex) {
            LogUtils.w(TAG, "Exception while copying account tables", ex);
            copyCount = -1;
        } finally {
            fromDatabase.endTransaction();
        }
        return copyCount;
    }

    /**
     * Backup account data, returning the number of accounts backed up
     */
    private static int backupAccounts(final Context context, final SQLiteDatabase db) {
        final AccountManager am = AccountManager.get(context);
        final Cursor accountCursor = db.query(Account.TABLE_NAME, Account.CONTENT_PROJECTION,
                null, null, null, null, null);
        int updatedCount = 0;
        try {
            while (accountCursor.moveToNext()) {
                final Account account = new Account();
                account.restore(accountCursor);
                EmailServiceInfo serviceInfo =
                        EmailServiceUtils.getServiceInfo(context, account.getProtocol(context));
                if (serviceInfo == null) {
                    LogUtils.d(LogUtils.TAG, "Could not find service info for account");
                    continue;
                }
                final String jsonString = account.toJsonString(context);
                final android.accounts.Account amAccount =
                        account.getAccountManagerAccount(serviceInfo.accountType);
                am.setUserData(amAccount, ACCOUNT_MANAGER_JSON_TAG, jsonString);
                updatedCount++;
            }
        } finally {
            accountCursor.close();
        }
        return updatedCount;
    }

    /**
     * Restore account data, returning the number of accounts restored
     */
    private static int restoreAccounts(final Context context) {
        final Collection<EmailServiceInfo> infos = EmailServiceUtils.getServiceInfoList(context);
        // Find all possible account types
        final Set<String> accountTypes = new HashSet<String>(3);
        for (final EmailServiceInfo info : infos) {
            if (!TextUtils.isEmpty(info.accountType)) {
                // accountType will be empty for the gmail stub entry
                accountTypes.add(info.accountType);
            }
        }
        // Find all accounts we own
        final List<android.accounts.Account> amAccounts = new ArrayList<android.accounts.Account>();
        final AccountManager am = AccountManager.get(context);
        for (final String accountType : accountTypes) {
            amAccounts.addAll(Arrays.asList(am.getAccountsByType(accountType)));
        }
        // Try to restore them from saved JSON
        int restoredCount = 0;
        for (final android.accounts.Account amAccount : amAccounts) {
            String jsonString = null;
                jsonString = am.getUserData(amAccount, ACCOUNT_MANAGER_JSON_TAG);
            if (TextUtils.isEmpty(jsonString)) {
                continue;
            }
            final Account account = Account.fromJsonString(jsonString);
            if (account != null) {
                AccountSettingsUtils.commitSettings(context, account);
                restoredCount++;
            }
        }
        return restoredCount;
    }

    private static final String MESSAGE_CHANGE_LOG_TABLE_INSERT_PREFIX = "insert into %s ("
            + MessageChangeLogTable.MESSAGE_KEY + "," + MessageChangeLogTable.SERVER_ID + ","
            + MessageChangeLogTable.ACCOUNT_KEY + "," + MessageChangeLogTable.STATUS + ",";

    private static final String MESSAGE_CHANGE_LOG_TABLE_VALUES_PREFIX = ") values (%s, "
            + "(select " + MessageColumns.SERVER_ID + " from " +
                    Message.TABLE_NAME + " where _id=%s),"
            + "(select " + MessageColumns.ACCOUNT_KEY + " from " +
                    Message.TABLE_NAME + " where _id=%s),"
            + MessageMove.STATUS_NONE_STRING + ",";

    /**
     * Formatting string to generate the SQL statement for inserting into MessageMove.
     * The formatting parameters are:
     * table name, message id x 4, destination folder id, message id, destination folder id.
     * Duplications are needed for sub-selects.
     */
    private static final String MESSAGE_MOVE_INSERT = MESSAGE_CHANGE_LOG_TABLE_INSERT_PREFIX
            + MessageMove.SRC_FOLDER_KEY + "," + MessageMove.DST_FOLDER_KEY + ","
            + MessageMove.SRC_FOLDER_SERVER_ID + "," + MessageMove.DST_FOLDER_SERVER_ID
            + MESSAGE_CHANGE_LOG_TABLE_VALUES_PREFIX
            + "(select " + MessageColumns.MAILBOX_KEY +
                    " from " + Message.TABLE_NAME + " where _id=%s)," + "%d,"
            + "(select " + Mailbox.SERVER_ID + " from " + Mailbox.TABLE_NAME + " where _id=(select "
            + MessageColumns.MAILBOX_KEY + " from " + Message.TABLE_NAME + " where _id=%s)),"
            + "(select " + Mailbox.SERVER_ID + " from " + Mailbox.TABLE_NAME + " where _id=%d))";

    /**
     * Insert a row into the MessageMove table when that message is moved.
     * @param db The {@link SQLiteDatabase}.
     * @param messageId The id of the message being moved.
     * @param dstFolderKey The folder to which the message is being moved.
     */
    private void addToMessageMove(final SQLiteDatabase db, final String messageId,
            final long dstFolderKey) {
        db.execSQL(String.format(Locale.US, MESSAGE_MOVE_INSERT, MessageMove.TABLE_NAME,
                messageId, messageId, messageId, messageId, dstFolderKey, messageId, dstFolderKey));
    }

    /**
     * Formatting string to generate the SQL statement for inserting into MessageStateChange.
     * The formatting parameters are:
     * table name, message id x 4, new flag read, message id, new flag favorite.
     * Duplications are needed for sub-selects.
     */
    private static final String MESSAGE_STATE_CHANGE_INSERT = MESSAGE_CHANGE_LOG_TABLE_INSERT_PREFIX
            + MessageStateChange.OLD_FLAG_READ + "," + MessageStateChange.NEW_FLAG_READ + ","
            + MessageStateChange.OLD_FLAG_FAVORITE + "," + MessageStateChange.NEW_FLAG_FAVORITE
            + MESSAGE_CHANGE_LOG_TABLE_VALUES_PREFIX
            + "(select " + MessageColumns.FLAG_READ +
            " from " + Message.TABLE_NAME + " where _id=%s)," + "%d,"
            + "(select " + MessageColumns.FLAG_FAVORITE +
            " from " + Message.TABLE_NAME + " where _id=%s)," + "%d)";

    private void addToMessageStateChange(final SQLiteDatabase db, final String messageId,
            final int newFlagRead, final int newFlagFavorite) {
        db.execSQL(String.format(Locale.US, MESSAGE_STATE_CHANGE_INSERT,
                MessageStateChange.TABLE_NAME, messageId, messageId, messageId, messageId,
                newFlagRead, messageId, newFlagFavorite));
    }

    // select count(*) from (select count(*) as dupes from Mailbox where accountKey=?
    // group by serverId) where dupes > 1;
    private static final String ACCOUNT_INTEGRITY_SQL =
            "select count(*) from (select count(*) as dupes from " + Mailbox.TABLE_NAME +
            " where accountKey=? group by " + MailboxColumns.SERVER_ID + ") where dupes > 1";


    // Query to get the protocol for a message. Temporary to switch between new and old upsync
    // behavior; should go away when IMAP gets converted.
    private static final String GET_MESSAGE_DETAILS = "SELECT"
            + " h." + HostAuthColumns.PROTOCOL + ","
            + " m." + MessageColumns.MAILBOX_KEY + ","
            + " a." + AccountColumns._ID
            + " FROM " + Message.TABLE_NAME + " AS m"
            + " INNER JOIN " + Account.TABLE_NAME + " AS a"
            + " ON m." + MessageColumns.ACCOUNT_KEY + "=a." + AccountColumns._ID
            + " INNER JOIN " + HostAuth.TABLE_NAME + " AS h"
            + " ON a." + AccountColumns.HOST_AUTH_KEY_RECV + "=h." + HostAuthColumns._ID
            + " WHERE m." + MessageColumns._ID + "=?";
    private static final int INDEX_PROTOCOL = 0;
    private static final int INDEX_MAILBOX_KEY = 1;
    private static final int INDEX_ACCOUNT_KEY = 2;

    /**
     * Query to get the protocol and email address for an account. Note that this uses
     * {@link #INDEX_PROTOCOL} and {@link #INDEX_EMAIL_ADDRESS} for its columns.
     */
    private static final String GET_ACCOUNT_DETAILS = "SELECT"
            + " h." + HostAuthColumns.PROTOCOL + ","
            + " a." + AccountColumns.EMAIL_ADDRESS + ","
            + " a." + AccountColumns.SYNC_KEY
            + " FROM " + Account.TABLE_NAME + " AS a"
            + " INNER JOIN " + HostAuth.TABLE_NAME + " AS h"
            + " ON a." + AccountColumns.HOST_AUTH_KEY_RECV + "=h." + HostAuthColumns._ID
            + " WHERE a." + AccountColumns._ID + "=?";
    private static final int INDEX_EMAIL_ADDRESS = 1;
    private static final int INDEX_SYNC_KEY = 2;

    /**
     * Restart push if we need it (currently only for Exchange accounts).
     * @param context A {@link Context}.
     * @param db The {@link SQLiteDatabase}.
     * @param id The id of the thing we're looking for.
     * @return Whether or not we sent a request to restart the push.
     */
    private static boolean restartPush(final Context context, final SQLiteDatabase db,
            final String id) {
        final Cursor c = db.rawQuery(GET_ACCOUNT_DETAILS, new String[] {id});
        if (c != null) {
            try {
                if (c.moveToFirst()) {
                    final String protocol = c.getString(INDEX_PROTOCOL);
                    // Only restart push for EAS accounts that have completed initial sync.
                    if (context.getString(R.string.protocol_eas).equals(protocol) &&
                            !EmailContent.isInitialSyncKey(c.getString(INDEX_SYNC_KEY))) {
                        final String emailAddress = c.getString(INDEX_EMAIL_ADDRESS);
                        final android.accounts.Account account =
                                getAccountManagerAccount(context, emailAddress, protocol);
                        if (account != null) {
                            restartPush(account);
                            return true;
                        }
                    }
                }
            } finally {
                c.close();
            }
        }
        return false;
    }

    /**
     * Restart push if a mailbox's settings change in a way that requires it.
     * @param context A {@link Context}.
     * @param db The {@link SQLiteDatabase}.
     * @param values The {@link ContentValues} that were updated for the mailbox.
     * @param accountId The id of the account for this mailbox.
     * @return Whether or not the push was restarted.
     */
    private static boolean restartPushForMailbox(final Context context, final SQLiteDatabase db,
            final ContentValues values, final String accountId) {
        if (values.containsKey(MailboxColumns.SYNC_LOOKBACK) ||
                values.containsKey(MailboxColumns.SYNC_INTERVAL)) {
            return restartPush(context, db, accountId);
        }
        return false;
    }

    /**
     * Restart push if an account's settings change in a way that requires it.
     * @param context A {@link Context}.
     * @param db The {@link SQLiteDatabase}.
     * @param values The {@link ContentValues} that were updated for the account.
     * @param accountId The id of the account.
     * @return Whether or not the push was restarted.
     */
    private static boolean restartPushForAccount(final Context context, final SQLiteDatabase db,
            final ContentValues values, final String accountId) {
        if (values.containsKey(AccountColumns.SYNC_LOOKBACK) ||
                values.containsKey(AccountColumns.SYNC_INTERVAL)) {
            return restartPush(context, db, accountId);
        }
        return false;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        LogUtils.d(TAG, "Update: " + uri);
        // Handle this special case the fastest possible way
        if (INTEGRITY_CHECK_URI.equals(uri)) {
            checkDatabases();
            return 0;
        } else if (ACCOUNT_BACKUP_URI.equals(uri)) {
            return backupAccounts(getContext(), getDatabase(getContext()));
        }

        // Notify all existing cursors, except for ACCOUNT_RESET_NEW_COUNT(_ID)
        Uri notificationUri = EmailContent.CONTENT_URI;

        final int match = findMatch(uri, "update");
        final Context context = getContext();
        // See the comment at delete(), above
        final SQLiteDatabase db = getDatabase(context);
        final int table = match >> BASE_SHIFT;
        int result;

        // We do NOT allow setting of unreadCount/messageCount via the provider
        // These columns are maintained via triggers
        if (match == MAILBOX_ID || match == MAILBOX) {
            values.remove(MailboxColumns.UNREAD_COUNT);
            values.remove(MailboxColumns.MESSAGE_COUNT);
        }

        final String tableName = TABLE_NAMES.valueAt(table);
        String id = "0";

        try {
            switch (match) {
                case ACCOUNT_PICK_TRASH_FOLDER:
                    return pickTrashFolder(uri);
                case ACCOUNT_PICK_SENT_FOLDER:
                    return pickSentFolder(uri);
                case UI_ACCTSETTINGS:
                    return uiUpdateSettings(context, values);
                case UI_FOLDER:
                    return uiUpdateFolder(context, uri, values);
                case UI_RECENT_FOLDERS:
                    return uiUpdateRecentFolders(uri, values);
                case UI_DEFAULT_RECENT_FOLDERS:
                    return uiPopulateRecentFolders(uri);
                case UI_ATTACHMENT:
                    return uiUpdateAttachment(uri, values);
                case UI_MESSAGE:
                    return uiUpdateMessage(uri, values);
                case ACCOUNT_CHECK:
                    id = uri.getLastPathSegment();
                    // With any error, return 1 (a failure)
                    int res = 1;
                    Cursor ic = null;
                    try {
                        ic = db.rawQuery(ACCOUNT_INTEGRITY_SQL, new String[] {id});
                        if (ic.moveToFirst()) {
                            res = ic.getInt(0);
                        }
                    } finally {
                        if (ic != null) {
                            ic.close();
                        }
                    }
                    // Count of duplicated mailboxes
                    return res;
                case MESSAGE_SELECTION:
                    Cursor findCursor = db.query(tableName, Message.ID_COLUMN_PROJECTION, selection,
                            selectionArgs, null, null, null);
                    try {
                        if (findCursor.moveToFirst()) {
                            return update(ContentUris.withAppendedId(
                                    Message.CONTENT_URI,
                                    findCursor.getLong(Message.ID_COLUMNS_ID_COLUMN)),
                                    values, null, null);
                        } else {
                            return 0;
                        }
                    } finally {
                        findCursor.close();
                    }

                case SYNCED_MESSAGE_ID:
                case UPDATED_MESSAGE_ID:
                case MESSAGE_ID:
                case ATTACHMENT_ID:
                case MAILBOX_ID:
                case ACCOUNT_ID:
                case HOSTAUTH_ID:
                case CREDENTIAL_ID:
                case QUICK_RESPONSE_ID:
                case POLICY_ID:
                    id = uri.getPathSegments().get(1);
                    if (match == SYNCED_MESSAGE_ID) {
                        // TODO: Migrate IMAP to use MessageMove/MessageStateChange as well.
                        boolean isEas = false;
                        //TS: jian.xu 2015-08-07 EMAIL BUGFIX-1063466 ADD_S
                        boolean isPop3 = false;
                        //TS: jian.xu 2015-08-07 EMAIL BUGFIX-1063466 ADD_E
                        long mailboxId = -1;
                        long accountId = -1;
                        final Cursor c = db.rawQuery(GET_MESSAGE_DETAILS, new String[] {id});
                        if (c != null) {
                            try {
                                if (c.moveToFirst()) {
                                    final String protocol = c.getString(INDEX_PROTOCOL);
                                    isEas = context.getString(R.string.protocol_eas)
                                            .equals(protocol);
                                    mailboxId = c.getLong(INDEX_MAILBOX_KEY);
                                    accountId = c.getLong(INDEX_ACCOUNT_KEY);
                                    //TS: jian.xu 2015-08-07 EMAIL BUGFIX-1063466 ADD_S
                                    isPop3 = context.getString(R.string.protocol_pop3).equals(protocol);
                                    //TS: jian.xu 2015-08-07 EMAIL BUGFIX-1063466 ADD_E
                                }
                            } finally {
                                c.close();
                            }
                        }

                        if (isEas) {
                            // EAS uses the new upsync classes.
                            Long dstFolderId = values.getAsLong(MessageColumns.MAILBOX_KEY);
                            if (dstFolderId != null) {
                                addToMessageMove(db, id, dstFolderId);
                            }
                            Integer flagRead = values.getAsInteger(MessageColumns.FLAG_READ);
                            Integer flagFavorite = values.getAsInteger(MessageColumns.FLAG_FAVORITE);
                            int flagReadValue = (flagRead != null) ?
                                    flagRead : MessageStateChange.VALUE_UNCHANGED;
                            int flagFavoriteValue = (flagFavorite != null) ?
                                    flagFavorite : MessageStateChange.VALUE_UNCHANGED;
                            if (flagRead != null || flagFavorite != null) {
                                addToMessageStateChange(db, id, flagReadValue, flagFavoriteValue);
                            }

                            // Request a sync for the messages mailbox so the update will upsync.
                            // This is normally done with ContentResolver.notifyUpdate() but doesn't
                            // work for Exchange because the Sync Adapter is declared as
                            // android:supportsUploading="false". Changing it to true is not trivial
                            // because that would require us to protect all calls to notifyUpdate()
                            // with syncToServer=false except in cases where we actually want to
                            // upsync.
                            // TODO: Look into making Exchange Sync Adapter supportsUploading=true
                            // Since we can't use the Sync Manager "delayed-sync" feature which
                            // applies only to UPLOAD syncs, we need to do this ourselves. The
                            // purpose of this is not to spam syncs when making frequent
                            // modifications.
                            final Handler handler = getDelayedSyncHandler();
                            final android.accounts.Account amAccount =
                                    getAccountManagerAccount(accountId);
                            if (amAccount != null) {
                                final SyncRequestMessage request = new SyncRequestMessage(
                                        uri.getAuthority(), amAccount, mailboxId);
                                synchronized (mDelayedSyncRequests) {
                                    if (!mDelayedSyncRequests.contains(request)) {
                                        mDelayedSyncRequests.add(request);
                                        final android.os.Message message =
                                                handler.obtainMessage(0, request);
                                        handler.sendMessageDelayed(message, SYNC_DELAY_MILLIS);
                                    }
                                }
                            } else {
                                LogUtils.d(TAG,
                                        "Attempted to start delayed sync for invalid account %d",
                                        accountId);
                            }
                        } else {
                            // Old way of doing upsync.
                            // For synced messages, first copy the old message to the updated table
                            // Note the insert or ignore semantics, guaranteeing that only the first
                            // update will be reflected in the updated message table; therefore this
                            // row will always have the "original" data
                            //TS: jian.xu 2015-08-07 EMAIL BUGFIX-1063466 MOD_S
                            boolean shouldUpdate = true;
                            if (isPop3 && (accountId != -1)) {
                                Long boxId = values.getAsLong(MessageColumns.MAILBOX_KEY);
                                if (boxId != null) {
                                    Account account = Account.restoreAccountWithId(context, accountId);
                                    int type = Mailbox.getMailboxType(context, boxId);
                                    if ((type == Mailbox.TYPE_TRASH) && (account != null) &&
                                            (account.getDeletePolicy() == Account.DELETE_POLICY_NEVER)) {
                                        shouldUpdate = false;
                                    }
                                }
                            }
                            if (shouldUpdate) {
                                db.execSQL(UPDATED_MESSAGE_INSERT + id);
                            }
                            //TS: jian.xu 2015-08-07 EMAIL BUGFIX-1063466 MOD_E
                        }
                    } else if (match == MESSAGE_ID) {
                        db.execSQL(UPDATED_MESSAGE_DELETE + id);
                    }
                    result = db.update(tableName, values, whereWithId(id, selection),
                            selectionArgs);
                    if (match == MESSAGE_ID || match == SYNCED_MESSAGE_ID) {
                        handleMessageUpdateNotifications(uri, id, values);
                    } else if (match == ATTACHMENT_ID) {
                        long attId = Integer.parseInt(id);
                        if (values.containsKey(AttachmentColumns.FLAGS)) {
                            int flags = values.getAsInteger(AttachmentColumns.FLAGS);
                            mAttachmentService.attachmentChanged(context, attId, flags);
                        }
                        // Notify UI if necessary; there are only two columns we can change that
                        // would be worth a notification
                        if (values.containsKey(AttachmentColumns.UI_STATE) ||
                                values.containsKey(AttachmentColumns.UI_DOWNLOADED_SIZE)) {
                            // Notify on individual attachment
                            notifyUI(UIPROVIDER_ATTACHMENT_NOTIFIER, id);
                            Attachment att = Attachment.restoreAttachmentWithId(context, attId);
                            if (att != null) {
                                // And on owning Message
                                notifyUI(UIPROVIDER_ATTACHMENTS_NOTIFIER, att.mMessageKey);
                            }
                        }
                    } else if (match == MAILBOX_ID) {
                        final long accountId = Mailbox.getAccountIdForMailbox(context, id);
                        notifyUIFolder(id, accountId);
                        restartPushForMailbox(context, db, values, Long.toString(accountId));
                    } else if (match == ACCOUNT_ID) {
                        updateAccountSyncInterval(Long.parseLong(id), values);
                        // Notify individual account and "all accounts"
                        notifyUI(UIPROVIDER_ACCOUNT_NOTIFIER, id);
                        notifyUI(UIPROVIDER_ALL_ACCOUNTS_NOTIFIER, null);
                        restartPushForAccount(context, db, values, id);
                    }
                    break;
                case BODY_ID: {
                    /// TCT: get the correct body id
                    id = uri.getPathSegments().get(1);
                    final ContentValues updateValues = new ContentValues(values);
                    updateValues.remove(BodyColumns.HTML_CONTENT);
                    /// TCT: update the text body in db for local search
//                    updateValues.remove(BodyColumns.TEXT_CONTENT);

                    result = db.update(tableName, updateValues, whereWithId(id, selection),
                            selectionArgs);

                    if (values.containsKey(BodyColumns.HTML_CONTENT) ||
                            values.containsKey(BodyColumns.TEXT_CONTENT)) {
                        final long messageId;
                        if (values.containsKey(BodyColumns.MESSAGE_KEY)) {
                            messageId = values.getAsLong(BodyColumns.MESSAGE_KEY);
                        } else {
                            final long bodyId = Long.parseLong(id);
                            final SQLiteStatement sql = db.compileStatement(
                                    "select " + BodyColumns.MESSAGE_KEY +
                                            " from " + Body.TABLE_NAME +
                                            " where " + BodyColumns._ID + "=" + Long
                                            .toString(bodyId)
                            );
                            messageId = sql.simpleQueryForLong();
                        }
                        writeBodyFiles(context, messageId, values);
                        /**
                         * TCT: notify the message's update , if the body's
                         * content changed. the Webview will miss the update of
                         * the body without the notification @{
                         */
                        notifyUIMessage(messageId);
                        /** @} */
                    }
                    break;
                }
                case BODY:
                 // TS: Gantao 2015-07-23 EMAIL BUGFIX-1029375 ADD_S
                case BODY_LARGE: {
                    if (match == BODY_LARGE) {
                        byte[] compressedBody;
                        compressedBody = values.getAsByteArray(BodyColumns.TEXT_CONTENT);
                        if (compressedBody != null) {
                            //Note:decompressBytes here is not very good,optimize it in the future
                            values.put(BodyColumns.TEXT_CONTENT, StringCompressor.decompressFromBytes(compressedBody));
                            compressedBody = null;
                        }
                        compressedBody = values.getAsByteArray(BodyColumns.HTML_CONTENT);
                        if (compressedBody != null) {
                            values.put(BodyColumns.HTML_CONTENT, StringCompressor.decompressFromBytes(compressedBody));
                        }
                    }
                 // TS: Gantao 2015-07-23 EMAIL BUGFIX-1029375 ADD_S
                    final ContentValues updateValues = new ContentValues(values);
                    updateValues.remove(BodyColumns.HTML_CONTENT);
                    // TCT: update text in db for local search
//                    updateValues.remove(BodyColumns.TEXT_CONTENT);

                    result = db.update(tableName, updateValues, selection, selectionArgs);

                    if (result == 0 && selection.equals(Body.SELECTION_BY_MESSAGE_KEY)) {
                        // TODO: This is a hack. Notably, the selection equality test above
                        // is hokey at best.
                        LogUtils.i(TAG, "Body Update to non-existent row, morphing to insert");
                        final ContentValues insertValues = new ContentValues(values);
                        insertValues.put(BodyColumns.MESSAGE_KEY, selectionArgs[0]);
                        insert(Body.CONTENT_URI, insertValues);
                    } else {
                        // possibly need to write new body values
                        if (values.containsKey(BodyColumns.HTML_CONTENT) ||
                                values.containsKey(BodyColumns.TEXT_CONTENT)) {
                            final long messageIds[];
                            if (values.containsKey(BodyColumns.MESSAGE_KEY)) {
                                messageIds = new long[] {values.getAsLong(BodyColumns.MESSAGE_KEY)};
                            } else if (values.containsKey(BodyColumns._ID)) {
                                final long bodyId = values.getAsLong(BodyColumns._ID);
                                final SQLiteStatement sql = db.compileStatement(
                                        "select " + BodyColumns.MESSAGE_KEY +
                                                " from " + Body.TABLE_NAME +
                                                " where " + BodyColumns._ID + "=" + Long
                                                .toString(bodyId)
                                );
                                messageIds = new long[] {sql.simpleQueryForLong()};
                            } else {
                                final String proj[] = {BodyColumns.MESSAGE_KEY};
                                final Cursor c = db.query(Body.TABLE_NAME, proj,
                                        selection, selectionArgs,
                                        null, null, null);
                                try {
                                    final int count = c.getCount();
                                    if (count == 0) {
                                        throw new IllegalStateException("Can't find body record");
                                    }
                                    messageIds = new long[count];
                                    int i = 0;
                                    while (c.moveToNext()) {
                                        messageIds[i++] = c.getLong(0);
                                    }
                                } finally {
                                    c.close();
                                }
                            }
                            // This is probably overkill
                            for (int i = 0; i < messageIds.length; i++) {
                                final long messageId = messageIds[i];
                                writeBodyFiles(context, messageId, values);
                            }
                        }
                    }
                    break;
                }
                case MESSAGE:
                    decodeEmailAddresses(values);
                case UPDATED_MESSAGE:
                case ATTACHMENT:
                case MAILBOX:
                case ACCOUNT:
                case HOSTAUTH:
                case CREDENTIAL:
                //[FEATURE]-Add-BEGIN by TSNJ.qinglian.zhang,10/28/2014,FR 736417
                    result = db.update(tableName, values, selection, selectionArgs);
                    break;
                //[FEATURE]-Add-END by TSNJ.qinglian.zhang
                case POLICY:
                    if (match == ATTACHMENT) {
                        if (values.containsKey(AttachmentColumns.LOCATION) &&
                                TextUtils.isEmpty(values.getAsString(AttachmentColumns.LOCATION))) {
                            LogUtils.w(TAG, new Throwable(), "attachment with blank location");
                        }
                    }
                    result = db.update(tableName, values, selection, selectionArgs);
                    break;
                case MESSAGE_MOVE:
                    result = db.update(MessageMove.TABLE_NAME, values, selection, selectionArgs);
                    break;
                case MESSAGE_STATE_CHANGE:
                    result = db.update(MessageStateChange.TABLE_NAME, values, selection,
                            selectionArgs);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown URI " + uri);
            }
        } catch (SQLiteException e) {
            checkDatabases();
            throw e;
        }

        // Notify all notifier cursors if some records where changed in the database
        if (result > 0) {
            sendNotifierChange(getBaseNotificationUri(match), NOTIFICATION_OP_UPDATE, id);
            notifyUI(notificationUri, null);
        }
        return result;
    }

    private void updateSyncStatus(final Bundle extras) {
        final long id = extras.getLong(EmailServiceStatus.SYNC_STATUS_ID);
        final int statusCode = extras.getInt(EmailServiceStatus.SYNC_STATUS_CODE);
        final Uri uri = ContentUris.withAppendedId(FOLDER_STATUS_URI, id);
        notifyUI(uri, null);
        final boolean inProgress = statusCode == EmailServiceStatus.IN_PROGRESS;
        if (inProgress) {
            RefreshStatusMonitor.getInstance(getContext()).setSyncStarted(id);
        } else {
            final int result = extras.getInt(EmailServiceStatus.SYNC_RESULT);
            final ContentValues values = new ContentValues();
            values.put(Mailbox.UI_LAST_SYNC_RESULT, result);
            mDatabase.update(
                    Mailbox.TABLE_NAME,
                    values,
                    WHERE_ID,
                    new String[] { String.valueOf(id) });
        }
    }

    @Override
    public Bundle call(String method, String arg, Bundle extras) {
        LogUtils.d(TAG, "EmailProvider#call(%s, %s)", method, arg);

        // Handle queries for the device friendly name.
        // TODO: This should eventually be a device property, not defined by the app.
        if (TextUtils.equals(method, EmailContent.DEVICE_FRIENDLY_NAME)) {
            final Bundle bundle = new Bundle(1);
            // TODO: For now, just use the model name since we don't yet have a user-supplied name.
            bundle.putString(EmailContent.DEVICE_FRIENDLY_NAME, Build.MODEL);
            return bundle;
        }

        // Handle sync status callbacks.
        if (TextUtils.equals(method, SYNC_STATUS_CALLBACK_METHOD)) {
            updateSyncStatus(extras);
            return null;
        }
        if (TextUtils.equals(method, MailboxUtilities.FIX_PARENT_KEYS_METHOD)) {
            fixParentKeys(getDatabase(getContext()));
            return null;
        }
        // TS: chao.zhang 2015-09-21 EMAIL FEATURE-585337 ADD_S
        //Handle failed notification
        if (TextUtils.equals(method, UIProvider.AccountCallMethods.SHOW_FAILED_NOTIFICATION)) {
            long accountId = extras.getLong("account_id");
            int failedCount = extras.getInt("faild_mails");
            createFailedNotificationsIntent(getContext(),accountId,failedCount);
            return null;
        // TS: jian.xu 2015-11-26 EMAIL BUGFIX-944797 ADD_S
        } else if (TextUtils.equals(method, UIProvider.AccountCallMethods.CANCEL_FAILED_NOTIFICATION)) {
            //Note: we need to cancel fail notification when there has no failed mail in outbox mailbox.
            long accountId = extras.getLong("account_id");
            NotificationController.getInstance(getContext()).cancelFailStatusNotification(accountId);
        }
        // TS: jian.xu 2015-11-26 EMAIL BUGFIX-944797 ADD_E

        //TS: yanhua.chen 2015-1-20 EMAIL BUGFIX_1162996 ADD_S
        if (TextUtils.equals(method,UIProvider.AccountCallMethods.SHOW_EXCHANGE_CALENDARORCONTACTS_NOTIFICATION)) {
            String packageName = extras.getString(PermissionUtil.EXTRA_PACKAGENAME);
            String permission = extras.getString(PermissionUtil.EXTRA_PERMISSIONS);

            if (android.Manifest.permission.WRITE_CALENDAR.equals(permission) | Manifest.permission.READ_CALENDAR.equals(permission)) {
                boolean isCalendarNeverAskAgain = MailPrefs.get(getContext()).getIgnoreExchangeCalendarPermission();
                if (!isCalendarNeverAskAgain) {
                    showCalendarNotification(getContext(), packageName);
                }
            } else if (Manifest.permission.READ_CONTACTS.equals(permission) | Manifest.permission.WRITE_CONTACTS.equals(permission)) {
                boolean isContactsNeverAskAgain = MailPrefs.get(getContext()).getIgnoreExchangeContactPermission();
                if (!isContactsNeverAskAgain) {
                    showContactsNotification(getContext(), packageName);
                }
            } else if (Manifest.permission.READ_EXTERNAL_STORAGE.equals(permission) | Manifest.permission.WRITE_EXTERNAL_STORAGE.equals(permission)) {
                boolean isStorageNeverAskAgain = MailPrefs.get(getContext()).getIgnoreExchangeStoragePermission();
                if (!isStorageNeverAskAgain) {
                    showStorageNotification(getContext(), packageName);
                }
            }
            return null;
        }
        //TS: yanhua.chen 2015-1-20 EMAIL BUGFIX_1162996 ADD_E

        // TS: chao.zhang 2015-09-21 EMAIL FEATURE-585337 ADD_E
        // Handle send & save.
        final Uri accountUri = Uri.parse(arg);
        final long accountId = Long.parseLong(accountUri.getPathSegments().get(1));

        Uri messageUri = null;

        if (TextUtils.equals(method, UIProvider.AccountCallMethods.SEND_MESSAGE)) {
            messageUri = uiSendDraftMessage(accountId, extras);
            Preferences.getPreferences(getContext()).setLastUsedAccountId(accountId);
        } else if (TextUtils.equals(method, UIProvider.AccountCallMethods.SAVE_MESSAGE)) {
            messageUri = uiSaveDraftMessage(accountId, extras);
        } else if (TextUtils.equals(method, UIProvider.AccountCallMethods.SET_CURRENT_ACCOUNT)) {
            LogUtils.d(TAG, "Unhandled (but expected) Content provider method: %s", method);
        } else {
            LogUtils.e(TAG, "Unexpected Content provider method: %s", method);
        }

        final Bundle result;
        if (messageUri != null) {
            result = new Bundle(1);
            result.putParcelable(UIProvider.MessageColumns.URI, messageUri);
        } else {
            result = null;
        }

        return result;
    }

    /**
     * Writes message bodies to disk, read from a set of ContentValues
     *
     * @param c Context for finding files
     * @param messageId id of message to write body for
     * @param cv {@link ContentValues} containing {@link BodyColumns#HTML_CONTENT} and/or
     *           {@link BodyColumns#TEXT_CONTENT}. Inserting a null or empty value will delete the
     *           associated text or html body file
     * @throws IllegalStateException
     */
    private static void writeBodyFiles(final Context c, final long messageId,
            final ContentValues cv) throws IllegalStateException {
        if (cv.containsKey(BodyColumns.HTML_CONTENT)) {
            final String htmlContent = cv.getAsString(BodyColumns.HTML_CONTENT);
            try {
                writeBodyFile(c, messageId, "html", htmlContent);
            } catch (final IOException e) {
                throw new IllegalStateException("IOException while writing html body " +
                        "for message id " + Long.toString(messageId), e);
            }
        }
        if (cv.containsKey(BodyColumns.TEXT_CONTENT)) {
            final String textContent = cv.getAsString(BodyColumns.TEXT_CONTENT);
            try {
                writeBodyFile(c, messageId, "txt", textContent);
            } catch (final IOException e) {
                throw new IllegalStateException("IOException while writing text body " +
                        "for message id " + Long.toString(messageId), e);
            }
        }
    }

    /**
     * Writes a message body file to disk
     *
     * @param c Context for finding files dir
     * @param messageId id of message to write body for
     * @param ext "html" or "txt"
     * @param content Body content to write to file, or null/empty to delete file
     * @throws IOException
     */
    private static void writeBodyFile(final Context c, final long messageId, final String ext,
            final String content) throws IOException {
        final File textFile = getBodyFile(c, messageId, ext);
        if (TextUtils.isEmpty(content)) {
            if (!textFile.delete()) {
                LogUtils.v(LogUtils.TAG, "did not delete text body for %d", messageId);
            }
        } else {
            final FileWriter w = new FileWriter(textFile);
            try {
                w.write(content);
            } finally {
                w.close();
            }
        }
    }

    /**
     * Returns a {@link java.io.File} object pointing to the body content file for the message
     *
     * @param c Context for finding files dir
     * @param messageId id of message to locate
     * @param ext "html" or "txt"
     * @return File ready for operating upon
     */
    protected static File getBodyFile(final Context c, final long messageId, final String ext)
            throws FileNotFoundException {
        if (!TextUtils.equals(ext, "html") && !TextUtils.equals(ext, "txt")) {
            throw new IllegalArgumentException("ext must be one of 'html' or 'txt'");
        }
        long l1 = messageId / 100 % 100;
        long l2 = messageId % 100;
        final File dir = new File(c.getFilesDir(),
                "body/" + Long.toString(l1) + "/" + Long.toString(l2) + "/");
        if (!dir.isDirectory() && !dir.mkdirs()) {
            throw new FileNotFoundException("Could not create directory for body file");
        }
        return new File(dir, Long.toString(messageId) + "." + ext);
    }

    @Override
    public ParcelFileDescriptor openFile(final Uri uri, final String mode)
            throws FileNotFoundException {
        if (LogUtils.isLoggable(TAG, LogUtils.DEBUG)) {
            LogUtils.d(TAG, "EmailProvider.openFile: %s", LogUtils.contentUriToString(TAG, uri));
        }

        final int match = findMatch(uri, "openFile");
        switch (match) {
            case ATTACHMENTS_CACHED_FILE_ACCESS:
                // Parse the cache file path out from the uri
                final String cachedFilePath =
                        uri.getQueryParameter(Attachment.CACHED_FILE_QUERY_PARAM);

                if (cachedFilePath != null) {
                    // clearCallingIdentity means that the download manager will
                    // check our permissions rather than the permissions of whatever
                    // code is calling us.
                    long binderToken = Binder.clearCallingIdentity();
                    try {
                        LogUtils.d(TAG, "Opening attachment %s", cachedFilePath);
                        return ParcelFileDescriptor.open(
                                new File(cachedFilePath), ParcelFileDescriptor.MODE_READ_ONLY);
                    } finally {
                        Binder.restoreCallingIdentity(binderToken);
                    }
                }
                break;
            case BODY_HTML: {
                final long messageKey = Long.valueOf(uri.getLastPathSegment());
                return ParcelFileDescriptor.open(getBodyFile(getContext(), messageKey, "html"),
                        Utilities.parseMode(mode));
            }
            case BODY_TEXT:{
                final long messageKey = Long.valueOf(uri.getLastPathSegment());
                return ParcelFileDescriptor.open(getBodyFile(getContext(), messageKey, "txt"),
                        Utilities.parseMode(mode));
            }
        }

        throw new FileNotFoundException("unable to open file");
    }


    /**
     * Returns the base notification URI for the given content type.
     *
     * @param match The type of content that was modified.
     */
    private static Uri getBaseNotificationUri(int match) {
        Uri baseUri = null;
        switch (match) {
            case MESSAGE:
            case MESSAGE_ID:
            case SYNCED_MESSAGE_ID:
                baseUri = Message.NOTIFIER_URI;
                break;
            case ACCOUNT:
            case ACCOUNT_ID:
                baseUri = Account.NOTIFIER_URI;
                break;
        }
        return baseUri;
    }

    /**
     * Sends a change notification to any cursors observers of the given base URI. The final
     * notification URI is dynamically built to contain the specified information. It will be
     * of the format <<baseURI>>/<<op>>/<<id>>; where <<op>> and <<id>> are optional depending
     * upon the given values.
     * NOTE: If <<op>> is specified, notifications for <<baseURI>>/<<id>> will NOT be invoked.
     * If this is necessary, it can be added. However, due to the implementation of
     * {@link ContentObserver}, observers of <<baseURI>> will receive multiple notifications.
     *
     * @param baseUri The base URI to send notifications to. Must be able to take appended IDs.
     * @param op Optional operation to be appended to the URI.
     * @param id If a positive value, the ID to append to the base URI. Otherwise, no ID will be
     *           appended to the base URI.
     */
    private void sendNotifierChange(Uri baseUri, String op, String id) {
        if (baseUri == null) return;

      //TS: zhonghua.tuo 2015-05-22 EMAIL BUGFIX_947804 ADD_S
        if (baseUri.equals(Message.NOTIFIER_URI)) {
            sendMessageListDataChangedNotification();
        }
      //TS: zhonghua.tuo 2015-05-22 EMAIL BUGFIX_947804 ADD_E
        // Append the operation, if specified
        if (op != null) {
            baseUri = baseUri.buildUpon().appendEncodedPath(op).build();
        }

        long longId = 0L;
        try {
            longId = Long.valueOf(id);
        } catch (NumberFormatException ignore) {}
        if (longId > 0) {
            notifyUI(baseUri, id);
        } else {
            notifyUI(baseUri, null);
        }
      //TS: zhonghua.tuo 2015-05-22 EMAIL BUGFIX_947804 ADD_S
//        // We want to send the message list changed notification if baseUri is Message.NOTIFIER_URI.
//        if (baseUri.equals(Message.NOTIFIER_URI)) {
//            sendMessageListDataChangedNotification();
//        }
      //TS: zhonghua.tuo 2015-05-22 EMAIL BUGFIX_947804 ADD_E
    }

    private void sendMessageListDataChangedNotification() {
        final Context context = getContext();
        final Intent intent = new Intent(ACTION_NOTIFY_MESSAGE_LIST_DATASET_CHANGED);
        // Ideally this intent would contain information about which account changed, to limit the
        // updates to that particular account.  Unfortunately, that information is not available in
        // sendNotifierChange().
      //TS: zhonghua.tuo 2015-05-22 EMAIL BUGFIX_947804 ADD_S
        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
      //TS: zhonghua.tuo 2015-05-22 EMAIL BUGFIX_947804 ADD_E
        context.sendBroadcast(intent);
    }

    // We might have more than one thread trying to make its way through applyBatch() so the
    // notification coalescing needs to be thread-local to work correctly.
    private final ThreadLocal<Set<Uri>> mTLBatchNotifications =
            new ThreadLocal<Set<Uri>>();

    private Set<Uri> getBatchNotificationsSet() {
        return mTLBatchNotifications.get();
    }

    private void setBatchNotificationsSet(Set<Uri> batchNotifications) {
        mTLBatchNotifications.set(batchNotifications);
    }

    @Override
    public ContentProviderResult[] applyBatch(ArrayList<ContentProviderOperation> operations)
            throws OperationApplicationException {
        /**
         * Collect notification URIs to notify at the end of batch processing.
         * These are populated by calls to notifyUI() by way of update(), insert() and delete()
         * calls made in super.applyBatch()
         */
        setBatchNotificationsSet(Sets.<Uri>newHashSet());
        Context context = getContext();
        SQLiteDatabase db = getDatabase(context);
        db.beginTransaction();
        try {
            ContentProviderResult[] results = super.applyBatch(operations);
            db.setTransactionSuccessful();
            return results;
        } finally {
            //TS: Gantao 2015-12-14 EMAIL BUGFIX_1114389 MOD_S
            //Resolve SQLiteDiskIOException error, low reproduce issue ,just catch it.
            try {
                db.endTransaction();
            }catch (SQLiteDiskIOException e){
                //TODO: do something when occur SQLiteDiskIOException
                LogUtils.e(TAG, "SQLiteDiskIOException while applyBatch endTransaction",e);
            }
            //TS: Gantao 2015-12-14 EMAIL BUGFIX_1114389 MOD_E
            final Set<Uri> notifications = getBatchNotificationsSet();
            setBatchNotificationsSet(null);
            for (final Uri uri : notifications) {
                context.getContentResolver().notifyChange(uri, null);
            }
        }
    }

    public static interface EmailAttachmentService {
        /**
         * Notify the service that an attachment has changed.
         */
        void attachmentChanged(final Context context, final long id, final int flags);
    }

    private final EmailAttachmentService DEFAULT_ATTACHMENT_SERVICE = new EmailAttachmentService() {
        @Override
        public void attachmentChanged(final Context context, final long id, final int flags) {
            // The default implementation delegates to the real service.
            AttachmentService.attachmentChanged(context, id, flags);
        }
    };
    private EmailAttachmentService mAttachmentService = DEFAULT_ATTACHMENT_SERVICE;

    // exposed for testing
    public void injectAttachmentService(final EmailAttachmentService attachmentService) {
        mAttachmentService =
            attachmentService == null ? DEFAULT_ATTACHMENT_SERVICE : attachmentService;
    }

    private Cursor notificationQuery(final Uri uri) {
        final SQLiteDatabase db = getDatabase(getContext());
        final String accountId = uri.getLastPathSegment();

        final String sql = "SELECT " + MessageColumns.MAILBOX_KEY + ", " +
                "SUM(CASE " + MessageColumns.FLAG_READ + " WHEN 0 THEN 1 ELSE 0 END), " +
                "SUM(CASE " + MessageColumns.FLAG_SEEN + " WHEN 0 THEN 1 ELSE 0 END)\n" +
                "FROM " + Message.TABLE_NAME + "\n" +
                "WHERE " + MessageColumns.ACCOUNT_KEY + " = ?\n" +
                "GROUP BY " + MessageColumns.MAILBOX_KEY;

        final String[] selectionArgs = {accountId};

        return db.rawQuery(sql, selectionArgs);
    }

    //TS:zheng.zou 2015-12-17 EMAIL BUGFIX_861247  ADD_S
    private Cursor mostRecentUnreadMessageQuery(final Uri uri){
        SQLiteDatabase db = getDatabase(getContext());
        String mailboxId = uri.getLastPathSegment();
        return db.rawQuery("select _id from Message where mailboxKey=? and flagRead=0 order by timeStamp desc limit 1",
                new String[] {mailboxId});
    }
    //TS:zheng.zou 2015-12-17 EMAIL BUGFIX_861247  ADD_E

    public Cursor mostRecentMessageQuery(Uri uri) {
        SQLiteDatabase db = getDatabase(getContext());
        String mailboxId = uri.getLastPathSegment();
        return db.rawQuery("select max(_id) from Message where mailboxKey=?",
                new String[] {mailboxId});
    }

    private Cursor getMailboxMessageCount(Uri uri) {
        SQLiteDatabase db = getDatabase(getContext());
        String mailboxId = uri.getLastPathSegment();
        return db.rawQuery("select count(*) from Message where mailboxKey=?",
                new String[] {mailboxId});
    }

    /**
     * Support for UnifiedEmail below
     */

    private static final String NOT_A_DRAFT_STRING =
        Integer.toString(UIProvider.DraftType.NOT_A_DRAFT);

    private static final String CONVERSATION_FLAGS =
            "CASE WHEN (" + MessageColumns.FLAGS + "&" + Message.FLAG_INCOMING_MEETING_INVITE +
                ") !=0 THEN " + UIProvider.ConversationFlags.CALENDAR_INVITE +
                " ELSE 0 END + " +
            "CASE WHEN (" + MessageColumns.FLAGS + "&" + Message.FLAG_FORWARDED +
                ") !=0 THEN " + UIProvider.ConversationFlags.FORWARDED +
                " ELSE 0 END + " +
             "CASE WHEN (" + MessageColumns.FLAGS + "&" + Message.FLAG_REPLIED_TO +
                ") !=0 THEN " + UIProvider.ConversationFlags.REPLIED +
                " ELSE 0 END";

    /**
     * Array of pre-defined account colors (legacy colors from old email app)
     */
    private static final int[] ACCOUNT_COLORS = new int[] {
        0xff71aea7, 0xff621919, 0xff18462f, 0xffbf8e52, 0xff001f79,
        0xffa8afc2, 0xff6b64c4, 0xff738359, 0xff9d50a4
    };

    private static final String CONVERSATION_COLOR =
            "@CASE (" + MessageColumns.ACCOUNT_KEY + " - 1) % " + ACCOUNT_COLORS.length +
                    " WHEN 0 THEN " + ACCOUNT_COLORS[0] +
                    " WHEN 1 THEN " + ACCOUNT_COLORS[1] +
                    " WHEN 2 THEN " + ACCOUNT_COLORS[2] +
                    " WHEN 3 THEN " + ACCOUNT_COLORS[3] +
                    " WHEN 4 THEN " + ACCOUNT_COLORS[4] +
                    " WHEN 5 THEN " + ACCOUNT_COLORS[5] +
                    " WHEN 6 THEN " + ACCOUNT_COLORS[6] +
                    " WHEN 7 THEN " + ACCOUNT_COLORS[7] +
                    " WHEN 8 THEN " + ACCOUNT_COLORS[8] +
            " END";

    private static final String ACCOUNT_COLOR =
            "@CASE (" + AccountColumns._ID + " - 1) % " + ACCOUNT_COLORS.length +
                    " WHEN 0 THEN " + ACCOUNT_COLORS[0] +
                    " WHEN 1 THEN " + ACCOUNT_COLORS[1] +
                    " WHEN 2 THEN " + ACCOUNT_COLORS[2] +
                    " WHEN 3 THEN " + ACCOUNT_COLORS[3] +
                    " WHEN 4 THEN " + ACCOUNT_COLORS[4] +
                    " WHEN 5 THEN " + ACCOUNT_COLORS[5] +
                    " WHEN 6 THEN " + ACCOUNT_COLORS[6] +
                    " WHEN 7 THEN " + ACCOUNT_COLORS[7] +
                    " WHEN 8 THEN " + ACCOUNT_COLORS[8] +
            " END";

     //[FEATURE]-Add-BEGIN by TCTNj.fu.zhang,04/08/2014,622697
    /**
     * Mapping of UIProvider columns to EmailProvider columns for the message list (called the
     * conversation list in UnifiedEmail)
     */
    private static ProjectionMap getMessageListMap() {
        if (sMessageListMap == null) {
            sMessageListMap = ProjectionMap.builder()
                .add(BaseColumns._ID, MessageColumns._ID)
                .add(UIProvider.ConversationColumns.URI, uriWithId("uimessage"))
                .add(UIProvider.ConversationColumns.MESSAGE_LIST_URI, uriWithId("uimessage"))
                .add(UIProvider.ConversationColumns.SUBJECT, MessageColumns.SUBJECT)
                .add(UIProvider.ConversationColumns.SNIPPET, MessageColumns.SNIPPET)
                .add(UIProvider.ConversationColumns.CONVERSATION_INFO, null)
                .add(UIProvider.ConversationColumns.DATE_RECEIVED_MS, MessageColumns.TIMESTAMP)
                .add(UIProvider.ConversationColumns.HAS_ATTACHMENTS, MessageColumns.FLAG_ATTACHMENT)
                .add(UIProvider.ConversationColumns.NUM_MESSAGES, "1")
                .add(UIProvider.ConversationColumns.NUM_DRAFTS, "0")
                .add(UIProvider.ConversationColumns.SENDING_STATE, MessageColumns.SENDING_STATUS) //want UIProvider use it that in MessageItemView and MessageHeaderView use it.
                .add(UIProvider.ConversationColumns.PRIORITY,
                        Integer.toString(ConversationPriority.LOW))
                .add(UIProvider.ConversationColumns.READ, MessageColumns.FLAG_READ)
                .add(UIProvider.ConversationColumns.SEEN, MessageColumns.FLAG_SEEN)
                .add(UIProvider.ConversationColumns.STARRED, MessageColumns.FLAG_FAVORITE)
                .add(UIProvider.ConversationColumns.FLAGS, CONVERSATION_FLAGS)
                .add(UIProvider.ConversationColumns.ACCOUNT_URI,
                        uriWithColumn("uiaccount", MessageColumns.ACCOUNT_KEY))
                .add(UIProvider.ConversationColumns.SENDER_INFO, MessageColumns.FROM_LIST)
                .add(UIProvider.ConversationColumns.ORDER_KEY, MessageColumns.TIMESTAMP)
                .add(UIProvider.ConversationColumns.FLAG_PRIORITY_COLUMN,MessageColumns.FLAG_PRIORITY)
                .add(UIProvider.ConversationColumns.MESSAGE_SIZE,MessageColumns.MESSAGE_SIZE)   //TS: zheng.zou 2016-1-14 EMAIL TASK_1431225 ADD
                .build();
        }
        return sMessageListMap;
    }//[FEATURE]-Add-END by TCTNB.fu.zhang
    private static ProjectionMap sMessageListMap;

    /**
     * Generate UIProvider draft type; note the test for "reply all" must come before "reply"
     */
    private static final String MESSAGE_DRAFT_TYPE =
        "CASE WHEN (" + MessageColumns.FLAGS + "&" + Message.FLAG_TYPE_ORIGINAL +
            ") !=0 THEN " + UIProvider.DraftType.COMPOSE +
        " WHEN (" + MessageColumns.FLAGS + "&" + Message.FLAG_TYPE_REPLY_ALL +
            ") !=0 THEN " + UIProvider.DraftType.REPLY_ALL +
        " WHEN (" + MessageColumns.FLAGS + "&" + Message.FLAG_TYPE_REPLY +
            ") !=0 THEN " + UIProvider.DraftType.REPLY +
        " WHEN (" + MessageColumns.FLAGS + "&" + Message.FLAG_TYPE_FORWARD +
            ") !=0 THEN " + UIProvider.DraftType.FORWARD +
            " ELSE " + UIProvider.DraftType.NOT_A_DRAFT + " END";

    private static final String MESSAGE_FLAGS =
            "CASE WHEN (" + MessageColumns.FLAGS + "&" + Message.FLAG_INCOMING_MEETING_INVITE +
            ") !=0 THEN " + UIProvider.MessageFlags.CALENDAR_INVITE +
            " ELSE 0 END";

    /**
     * Mapping of UIProvider columns to EmailProvider columns for a detailed message view in
     * UnifiedEmail
     */
    private static ProjectionMap getMessageViewMap() {
        if (sMessageViewMap == null) {
            sMessageViewMap = ProjectionMap.builder()
                .add(BaseColumns._ID, Message.TABLE_NAME + "." + MessageColumns._ID)
                .add(UIProvider.MessageColumns.SERVER_ID, SyncColumns.SERVER_ID)
                .add(UIProvider.MessageColumns.URI, uriWithFQId("uimessage", Message.TABLE_NAME))
                .add(UIProvider.MessageColumns.CONVERSATION_ID,
                        uriWithFQId("uimessage", Message.TABLE_NAME))
                .add(UIProvider.MessageColumns.SUBJECT, MessageColumns.SUBJECT)
                .add(UIProvider.MessageColumns.SNIPPET, MessageColumns.SNIPPET)
                .add(UIProvider.MessageColumns.FROM, MessageColumns.FROM_LIST)
                .add(UIProvider.MessageColumns.TO, MessageColumns.TO_LIST)
                .add(UIProvider.MessageColumns.CC, MessageColumns.CC_LIST)
                .add(UIProvider.MessageColumns.BCC, MessageColumns.BCC_LIST)
                .add(UIProvider.MessageColumns.REPLY_TO, MessageColumns.REPLY_TO_LIST)
                .add(UIProvider.MessageColumns.DATE_RECEIVED_MS, MessageColumns.TIMESTAMP)
                .add(UIProvider.MessageColumns.BODY_HTML, null) // Loaded in EmailMessageCursor
                .add(UIProvider.MessageColumns.BODY_TEXT, null) // Loaded in EmailMessageCursor
                .add(UIProvider.MessageColumns.REF_MESSAGE_ID, "0")
                .add(UIProvider.MessageColumns.DRAFT_TYPE, NOT_A_DRAFT_STRING)
                .add(UIProvider.MessageColumns.APPEND_REF_MESSAGE_CONTENT, "0")
                .add(UIProvider.MessageColumns.HAS_ATTACHMENTS, MessageColumns.FLAG_ATTACHMENT)
                .add(UIProvider.MessageColumns.ATTACHMENT_LIST_URI,
                        uriWithFQId("uiattachments", Message.TABLE_NAME))
                .add(UIProvider.MessageColumns.ATTACHMENT_BY_CID_URI,
                        uriWithFQId("uiattachmentbycid", Message.TABLE_NAME))
                .add(UIProvider.MessageColumns.MESSAGE_FLAGS, MESSAGE_FLAGS)
                .add(UIProvider.MessageColumns.DRAFT_TYPE, MESSAGE_DRAFT_TYPE)
                .add(UIProvider.MessageColumns.MESSAGE_ACCOUNT_URI,
                        uriWithColumn("uiaccount", MessageColumns.ACCOUNT_KEY))
                .add(UIProvider.MessageColumns.STARRED, MessageColumns.FLAG_FAVORITE)
                .add(UIProvider.MessageColumns.READ, MessageColumns.FLAG_READ)
                .add(UIProvider.MessageColumns.SEEN, MessageColumns.FLAG_SEEN)
                .add(UIProvider.MessageColumns.SPAM_WARNING_STRING, null)
                .add(UIProvider.MessageColumns.SPAM_WARNING_LEVEL,
                        Integer.toString(UIProvider.SpamWarningLevel.NO_WARNING))
                .add(UIProvider.MessageColumns.SPAM_WARNING_LINK_TYPE,
                        Integer.toString(UIProvider.SpamWarningLinkType.NO_LINK))
                .add(UIProvider.MessageColumns.VIA_DOMAIN, null)
                .add(UIProvider.MessageColumns.CLIPPED, "0")
                .add(UIProvider.MessageColumns.PERMALINK, null)
                //TS: junwei-xu 2015-04-29 EMAIL BUGFIX_989306 ADD_S
                .add(UIProvider.MessageColumns.MESSAGE_FLAG_PRIORITY_COLUMN, MessageColumns.FLAG_PRIORITY)
                //TS: junwei-xu 2015-04-29 EMAIL BUGFIX_989306 ADD_E
                 .add(UIProvider.ConversationColumns.SENDING_STATE, MessageColumns.SENDING_STATUS)//want UIProvider use it that in MessageItemView and MessageHeaderView use it.
                 // TS: Gantao 2015-12-1 EMAIL BUGFIX_1117109 ADD_S
                 .add(UIProvider.MessageColumns.MESSAGE_FLAG_LOADED, MessageColumns.FLAG_LOADED)
                 //TS: Gantao 2015-12-1 EMAIL BUGFIX_1117109 ADD_E
                 .add(UIProvider.MessageColumns.MESSAGE_SIZE, MessageColumns.MESSAGE_SIZE)
                .build();
        }
        return sMessageViewMap;
    }
    private static ProjectionMap sMessageViewMap;

    /**
     * Generate UIProvider folder capabilities from mailbox flags
     */
    private static final String FOLDER_CAPABILITIES =
        "CASE WHEN (" + MailboxColumns.FLAGS + "&" + Mailbox.FLAG_ACCEPTS_MOVED_MAIL +
            ") !=0 THEN " + UIProvider.FolderCapabilities.CAN_ACCEPT_MOVED_MESSAGES +
            " ELSE 0 END";

    /**
     * Convert EmailProvider type to UIProvider type
     */
    private static final String FOLDER_TYPE = "CASE " + MailboxColumns.TYPE
            + " WHEN " + Mailbox.TYPE_INBOX   + " THEN " + UIProvider.FolderType.INBOX
            + " WHEN " + Mailbox.TYPE_DRAFTS  + " THEN " + UIProvider.FolderType.DRAFT
            + " WHEN " + Mailbox.TYPE_OUTBOX  + " THEN " + UIProvider.FolderType.OUTBOX
            + " WHEN " + Mailbox.TYPE_SENT    + " THEN " + UIProvider.FolderType.SENT
            + " WHEN " + Mailbox.TYPE_TRASH   + " THEN " + UIProvider.FolderType.TRASH
            + " WHEN " + Mailbox.TYPE_JUNK    + " THEN " + UIProvider.FolderType.SPAM
            + " WHEN " + Mailbox.TYPE_STARRED + " THEN " + UIProvider.FolderType.STARRED
            + " WHEN " + Mailbox.TYPE_UNREAD + " THEN " + UIProvider.FolderType.UNREAD
            + " WHEN " + Mailbox.TYPE_SEARCH + " THEN "
                    + getFolderTypeFromMailboxType(Mailbox.TYPE_SEARCH)
            + " ELSE " + UIProvider.FolderType.DEFAULT + " END";
    // TS: lin.zhou 2016-01-06 EMAIL BUGFIX-1271032 MOD_S
    private static final String FOLDER_ICON = "CASE " + MailboxColumns.TYPE
            + " WHEN " + Mailbox.TYPE_INBOX   + " THEN " + R.drawable.ic_drawer_folder_inbox
            + " WHEN " + Mailbox.TYPE_DRAFTS  + " THEN " + R.drawable.ic_drawer_folder_drafts
            + " WHEN " + Mailbox.TYPE_OUTBOX  + " THEN " + R.drawable.ic_drawer_folder_outbox
            + " WHEN " + Mailbox.TYPE_SENT    + " THEN " + R.drawable.ic_drawer_folder_sent
            + " WHEN " + Mailbox.TYPE_TRASH   + " THEN " + R.drawable.ic_drawer_folder_trash
            + " WHEN " + Mailbox.TYPE_STARRED + " THEN " + R.drawable.ic_drawer_folder_starred
            + " ELSE " + R.drawable.ic_drawer_folder_folder + " END";
    // TS: lin.zhou 2016-01-06 EMAIL BUGFIX-1271032 MOD_S
    /**
     * Local-only folders set totalCount < 0; such folders should substitute message count for
     * total count.
     * TODO: IMAP and POP don't adhere to this convention yet so for now we force a few types.
     */
    private static final String TOTAL_COUNT = "CASE WHEN "
            + MailboxColumns.TOTAL_COUNT + "<0 OR "
            + MailboxColumns.TYPE + "=" + Mailbox.TYPE_DRAFTS + " OR "
            + MailboxColumns.TYPE + "=" + Mailbox.TYPE_OUTBOX + " OR "
            + MailboxColumns.TYPE + "=" + Mailbox.TYPE_TRASH
            + " THEN " + MailboxColumns.MESSAGE_COUNT
            + " ELSE " + MailboxColumns.TOTAL_COUNT + " END";

    private static ProjectionMap getFolderListMap() {
        if (sFolderListMap == null) {
            sFolderListMap = ProjectionMap.builder()
                .add(BaseColumns._ID, MailboxColumns._ID)
                .add(UIProvider.FolderColumns.PERSISTENT_ID, MailboxColumns.SERVER_ID)
                .add(UIProvider.FolderColumns.URI, uriWithId("uifolder"))
                .add(UIProvider.FolderColumns.NAME, "displayName")
                .add(UIProvider.FolderColumns.HAS_CHILDREN,
                        MailboxColumns.FLAGS + "&" + Mailbox.FLAG_HAS_CHILDREN)
                .add(UIProvider.FolderColumns.CAPABILITIES, FOLDER_CAPABILITIES)
                .add(UIProvider.FolderColumns.SYNC_WINDOW, "3")
                .add(UIProvider.FolderColumns.CONVERSATION_LIST_URI, uriWithId("uimessages"))
                .add(UIProvider.FolderColumns.CHILD_FOLDERS_LIST_URI, uriWithId("uisubfolders"))
                .add(UIProvider.FolderColumns.UNREAD_COUNT, MailboxColumns.UNREAD_COUNT)
                .add(UIProvider.FolderColumns.TOTAL_COUNT, TOTAL_COUNT)
                .add(UIProvider.FolderColumns.REFRESH_URI, uriWithId(QUERY_UIREFRESH))
                .add(UIProvider.FolderColumns.SYNC_STATUS, MailboxColumns.UI_SYNC_STATUS)
                .add(UIProvider.FolderColumns.LAST_SYNC_RESULT, MailboxColumns.UI_LAST_SYNC_RESULT)
                .add(UIProvider.FolderColumns.TYPE, FOLDER_TYPE)
                .add(UIProvider.FolderColumns.ICON_RES_ID, FOLDER_ICON)
                .add(UIProvider.FolderColumns.LOAD_MORE_URI, uriWithId("uiloadmore"))
                .add(UIProvider.FolderColumns.HIERARCHICAL_DESC, MailboxColumns.HIERARCHICAL_NAME)
                .add(UIProvider.FolderColumns.PARENT_URI, "case when " + MailboxColumns.PARENT_KEY
                        + "=" + Mailbox.NO_MAILBOX + " then NULL else " +
                        uriWithColumn("uifolder", MailboxColumns.PARENT_KEY) + " end")
                        /// TCT: local search.
                .add(UIProvider.FolderColumns.LOCAL_SEARCH_LIST_URI, uriWithId("uilocalsearch"))
                /**
                 * SELECT group_concat(fromList) FROM
                 * (SELECT fromList FROM message WHERE mailboxKey=? AND flagRead=0
                 *  GROUP BY fromList ORDER BY timestamp DESC)
                 */
                    .add(UIProvider.FolderColumns.UNREAD_SENDERS,
                            "(SELECT group_concat(" + MessageColumns.FROM_LIST + ") FROM " +
                                    "(SELECT " + MessageColumns.FROM_LIST + " FROM " + Message.TABLE_NAME +
                                    " WHERE " + MessageColumns.MAILBOX_KEY + "=" + Mailbox.TABLE_NAME + "." +
                                    MailboxColumns._ID + " AND " + MessageColumns.FLAG_READ + "=0" +
                                    " GROUP BY " + MessageColumns.FROM_LIST + " ORDER BY " +
                                    MessageColumns.TIMESTAMP + " DESC))")
                    .add(UIProvider.FolderColumns.SYNC_TIME, "syncTime")
                    .add(UIProvider.FolderColumns.ACCOUNT_KEY, "accountKey")
                .build();
        }
        return sFolderListMap;
    }
    private static ProjectionMap sFolderListMap;

    /**
     * Constructs the map of default entries for accounts. These values can be overridden in
     * {@link #genQueryAccount(String[], String)}.
     */
    private static ProjectionMap getAccountListMap(Context context) {
        if (sAccountListMap == null) {
            final ProjectionMap.Builder builder = ProjectionMap.builder()
                    .add(BaseColumns._ID, AccountColumns._ID)
                    .add(UIProvider.AccountColumns.FOLDER_LIST_URI, uriWithId("uifolders"))
                    .add(UIProvider.AccountColumns.FULL_FOLDER_LIST_URI, uriWithId("uifullfolders"))
                    .add(UIProvider.AccountColumns.ALL_FOLDER_LIST_URI, uriWithId("uiallfolders"))
                    .add(UIProvider.AccountColumns.NAME, AccountColumns.DISPLAY_NAME)
                    .add(UIProvider.AccountColumns.ACCOUNT_MANAGER_NAME,
                            AccountColumns.EMAIL_ADDRESS)
                    .add(UIProvider.AccountColumns.ACCOUNT_ID,
                            AccountColumns.EMAIL_ADDRESS)
                    .add(UIProvider.AccountColumns.SENDER_NAME,
                            AccountColumns.SENDER_NAME)
                    .add(UIProvider.AccountColumns.UNDO_URI,
                            ("'content://" + EmailContent.AUTHORITY + "/uiundo'"))
                    .add(UIProvider.AccountColumns.URI, uriWithId("uiaccount"))
                    .add(UIProvider.AccountColumns.SEARCH_URI, uriWithId("uisearch"))
                            // TODO: Is provider version used?
                    .add(UIProvider.AccountColumns.PROVIDER_VERSION, "1")
                    .add(UIProvider.AccountColumns.SYNC_STATUS, "0")
                    .add(UIProvider.AccountColumns.RECENT_FOLDER_LIST_URI,
                            uriWithId("uirecentfolders"))
                    .add(UIProvider.AccountColumns.DEFAULT_RECENT_FOLDER_LIST_URI,
                            uriWithId("uidefaultrecentfolders"))
                    .add(UIProvider.AccountColumns.SettingsColumns.SIGNATURE,
                            AccountColumns.SIGNATURE)
                    .add(UIProvider.AccountColumns.SettingsColumns.SNAP_HEADERS,
                            Integer.toString(UIProvider.SnapHeaderValue.ALWAYS))
                    .add(UIProvider.AccountColumns.SettingsColumns.CONFIRM_ARCHIVE, "0")
                    .add(UIProvider.AccountColumns.SettingsColumns.CONVERSATION_VIEW_MODE,
                            Integer.toString(UIProvider.ConversationViewMode.UNDEFINED))
                    /* MODIFIED-BEGIN by zheng.zou, 2016-03-21, BUG-1801683 */
                    .add(UIProvider.AccountColumns.SettingsColumns.VEILED_ADDRESS_PATTERN, null)
                    .add(UIProvider.AccountColumns.SettingsColumns.REPLY_TO, AccountColumns.REPLY_TO);//TS: zheng.zou 2016-03-21 EMAIL BUGFIX_1801683 ADD
                    /* MODIFIED-END by zheng.zou,BUG-1801683 */

            final String feedbackUri = context.getString(R.string.email_feedback_uri);
            if (!TextUtils.isEmpty(feedbackUri)) {
                // This string needs to be in single quotes, as it will be used as a constant
                // in a sql expression
                builder.add(UIProvider.AccountColumns.SEND_FEEDBACK_INTENT_URI,
                        "'" + feedbackUri + "'");
            }

            final String helpUri = context.getString(R.string.help_uri);
            if (!TextUtils.isEmpty(helpUri)) {
                // This string needs to be in single quotes, as it will be used as a constant
                // in a sql expression
                builder.add(UIProvider.AccountColumns.HELP_INTENT_URI,
                        "'" + helpUri + "'");
            }

            sAccountListMap = builder.build();
        }
        return sAccountListMap;
    }
    private static ProjectionMap sAccountListMap;

    private static ProjectionMap getQuickResponseMap() {
        if (sQuickResponseMap == null) {
            sQuickResponseMap = ProjectionMap.builder()
                    .add(UIProvider.QuickResponseColumns.TEXT, QuickResponseColumns.TEXT)
                    .add(UIProvider.QuickResponseColumns.URI,
                            "'" + combinedUriString("quickresponse", "") + "'||"
                                    + QuickResponseColumns._ID)
                    .build();
        }
        return sQuickResponseMap;
    }
    private static ProjectionMap sQuickResponseMap;

    /**
     * The "ORDER BY" clause for top level folders
     */
    private static final String MAILBOX_ORDER_BY = "CASE " + MailboxColumns.TYPE
        + " WHEN " + Mailbox.TYPE_INBOX   + " THEN 0"
        + " WHEN " + Mailbox.TYPE_DRAFTS  + " THEN 1"
        + " WHEN " + Mailbox.TYPE_OUTBOX  + " THEN 2"
        + " WHEN " + Mailbox.TYPE_SENT    + " THEN 3"
        + " WHEN " + Mailbox.TYPE_TRASH   + " THEN 4"
        + " WHEN " + Mailbox.TYPE_JUNK    + " THEN 5"
        // Other mailboxes (i.e. of Mailbox.TYPE_MAIL) are shown in alphabetical order.
        + " ELSE 10 END"
        + " ," + MailboxColumns.DISPLAY_NAME + " COLLATE LOCALIZED ASC";

    /**
     * Mapping of UIProvider columns to EmailProvider columns for a message's attachments
     */
    private static ProjectionMap getAttachmentMap() {
        if (sAttachmentMap == null) {
            sAttachmentMap = ProjectionMap.builder()
                .add(UIProvider.AttachmentColumns.NAME, AttachmentColumns.FILENAME)
                .add(UIProvider.AttachmentColumns.SIZE, AttachmentColumns.SIZE)
                .add(UIProvider.AttachmentColumns.URI, uriWithId("uiattachment"))
                .add(UIProvider.AttachmentColumns.CONTENT_TYPE, AttachmentColumns.MIME_TYPE)
                .add(UIProvider.AttachmentColumns.STATE, AttachmentColumns.UI_STATE)
                .add(UIProvider.AttachmentColumns.DESTINATION, AttachmentColumns.UI_DESTINATION)
                .add(UIProvider.AttachmentColumns.DOWNLOADED_SIZE,
                        AttachmentColumns.UI_DOWNLOADED_SIZE)
                .add(UIProvider.AttachmentColumns.CONTENT_URI, AttachmentColumns.CONTENT_URI)
                .add(UIProvider.AttachmentColumns.FLAGS, AttachmentColumns.FLAGS)
                //TS: wenggangjin 2014-12-10 EMAIL BUGFIX_852100 MOD_S
                .add(UIProvider.AttachmentColumns.CONTENT_ID, AttachmentColumns.CONTENT_ID)
                //TS: wenggangjin 2014-12-10 EMAIL BUGFIX_852100 MOD_E
                //TS: zhonghua.tuo 2015-3-3 EMAIL BUGFIX_936728 ADD_S
                .add(UIProvider.AttachmentColumns.REAL_URI,AttachmentColumns.REAL_URI)
                //TS: zhonghua.tuo 2015-3-3 EMAIL BUGFIX_936728 ADD_E
                //TS: tao.gan 2015-9-23 EMAIL FEATURE_526529 ADD_S
                .add(UIProvider.AttachmentColumns.ISINLINE, AttachmentColumns.ISINLINE)
                //TS: tao.gan 2015-9-23 EMAIL FEATURE_526529 ADD_E
                .build();
        }
        return sAttachmentMap;
    }
    private static ProjectionMap sAttachmentMap;

    /**
     * Generate the SELECT clause using a specified mapping and the original UI projection
     * @param map the ProjectionMap to use for this projection
     * @param projection the projection as sent by UnifiedEmail
     * @return a StringBuilder containing the SELECT expression for a SQLite query
     */
    private static StringBuilder genSelect(ProjectionMap map, String[] projection) {
        return genSelect(map, projection, EMPTY_CONTENT_VALUES);
    }

    private static StringBuilder genSelect(ProjectionMap map, String[] projection,
            ContentValues values) {
        final StringBuilder sb = new StringBuilder("SELECT ");
        boolean first = true;
        for (final String column: projection) {
            if (first) {
                first = false;
            } else {
                sb.append(',');
            }
            final String val;
            // First look at values; this is an override of default behavior
            if (values.containsKey(column)) {
                final String value = values.getAsString(column);
                if (value == null) {
                    val = "NULL AS " + column;
                } else if (value.startsWith("@")) {
                    val = value.substring(1) + " AS " + column;
                } else {
                    val = DatabaseUtils.sqlEscapeString(value) + " AS " + column;
                }
            } else {
                // Now, get the standard value for the column from our projection map
                final String mapVal = map.get(column);
                // If we don't have the column, return "NULL AS <column>", and warn
                if (mapVal == null) {
                    val = "NULL AS " + column;
                    // Apparently there's a lot of these, so don't spam the log with warnings
                    // LogUtils.w(TAG, "column " + column + " missing from projection map");
                } else {
                    val = mapVal;
                }
            }
            sb.append(val);
        }
        return sb;
    }

    /**
     * Convenience method to create a Uri string given the "type" of query; we append the type
     * of the query and the id column name (_id)
     *
     * @param type the "type" of the query, as defined by our UriMatcher definitions
     * @return a Uri string
     */
    private static String uriWithId(String type) {
        return uriWithColumn(type, BaseColumns._ID);
    }

    /**
     * Convenience method to create a Uri string given the "type" of query; we append the type
     * of the query and the passed in column name
     *
     * @param type the "type" of the query, as defined by our UriMatcher definitions
     * @param columnName the column in the table being queried
     * @return a Uri string
     */
    private static String uriWithColumn(String type, String columnName) {
        return "'content://" + EmailContent.AUTHORITY + "/" + type + "/' || " + columnName;
    }

    /**
     * Convenience method to create a Uri string given the "type" of query and the table name to
     * which it applies; we append the type of the query and the fully qualified (FQ) id column
     * (i.e. including the table name); we need this for join queries where _id would otherwise
     * be ambiguous
     *
     * @param type the "type" of the query, as defined by our UriMatcher definitions
     * @param tableName the name of the table whose _id is referred to
     * @return a Uri string
     */
    private static String uriWithFQId(String type, String tableName) {
        return "'content://" + EmailContent.AUTHORITY + "/" + type + "/' || " + tableName + "._id";
    }

    // Regex that matches start of img tag. '<(?i)img\s+'.
    private static final Pattern IMG_TAG_START_REGEX = Pattern.compile("<(?i)img\\s+");

    /**
     * Class that holds the sqlite query and the attachment (JSON) value (which might be null)
     */
    private static class MessageQuery {
        final String query;
        final String attachmentJson;

        MessageQuery(String _query, String _attachmentJson) {
            query = _query;
            attachmentJson = _attachmentJson;
        }
    }

    /**
     * Generate the "view message" SQLite query, given a projection from UnifiedEmail
     *
     * @param uiProjection as passed from UnifiedEmail
     * @return the SQLite query to be executed on the EmailProvider database
     */
    private MessageQuery genQueryViewMessage(String[] uiProjection, String id) {
        Context context = getContext();
        long messageId = Long.parseLong(id);
        Message msg = Message.restoreMessageWithId(context, messageId);
        ContentValues values = new ContentValues();
        String attachmentJson = null;
        if (msg != null) {
            Body body = Body.restoreBodyWithMessageId(context, messageId);
            if (body != null) {
                if (body.mHtmlContent != null) {
                    if (IMG_TAG_START_REGEX.matcher(body.mHtmlContent).find()) {
                        values.put(UIProvider.MessageColumns.EMBEDS_EXTERNAL_RESOURCES, 1);
                    }
                }
            }
            Address[] fromList = Address.fromHeader(msg.mFrom);
            int autoShowImages = 0;
            final MailPrefs mailPrefs = MailPrefs.get(context);
            for (Address sender : fromList) {
                final String email = sender.getAddress();
                if (mailPrefs.getDisplayImagesFromSender(email)) {
                    autoShowImages = 1;
                    break;
                }
            }
            // TS: tao.gan 2016-02-26 EMAIL BUGFIX-1680354 ADD_S
            //If we set the account ALWAYS_SHOW, also set the message auto show images.
            //It's useful for combined account view.
            Account account = Account.restoreAccountWithId(context, msg.mAccountKey);
            if(account != null && account.mInlineImages == Utility.ALWAYS_SHOW) {
                autoShowImages = 1;
            }
            // TS: tao.gan 2016-02-26 EMAIL BUGFIX-1680354 ADD_E
            values.put(UIProvider.MessageColumns.ALWAYS_SHOW_IMAGES, autoShowImages);
            // Add attachments...
            Attachment[] atts = Attachment.restoreAttachmentsWithMessageId(context, messageId);
            if (atts.length > 0) {
                ArrayList<com.tct.mail.providers.Attachment> uiAtts =
                        new ArrayList<com.tct.mail.providers.Attachment>();
                for (Attachment att : atts) {
                    // TODO: This code is intended to strip out any inlined attachments (which
                    // would have a non-null contentId) so that they will not display at the bottom
                    // along with the non-inlined attachments.
                    // The problem is that the UI_ATTACHMENTS query does not behave the same way,
                    // which causes crazy formatting.
                    // There is an open question here, should attachments that are inlined
                    // ALSO appear in the list of attachments at the bottom with the non-inlined
                    // attachments?
                    // Either way, the two queries need to behave the same way.
                    // As of now, they will. If we decide to stop this, then we need to enable
                    // the code below, and then also make the UI_ATTACHMENTS query behave
                    // the same way.
//
//                    if (att.mContentId != null && att.getContentUri() != null) {
//                        continue;
//                    }
                    com.tct.mail.providers.Attachment uiAtt =
                            new com.tct.mail.providers.Attachment();
                    uiAtt.setName(att.mFileName);
                    uiAtt.setContentType(att.mMimeType);
                    uiAtt.size = (int) att.mSize;
                    uiAtt.uri = uiUri("uiattachment", att.mId);
                    uiAtt.flags = att.mFlags;
                    // TS: tao.gan 2015-08-12 EMAIL FEATURE-ID ADD_S
                    //Add contentId to attachmentJson, so we can destinct between inline and others
                    // just from attachmentJson when we render attachments.
                    uiAtt.contentId = att.mContentId;
                    // TS: tao.gan 2015-08-12 EMAIL FEATURE-ID ADD_E
                 // TS: Gantao 2015-09-19 EMAIL BUGFIX_570084 ADD_S
                    //Add isInline to attachmentJson, so we can judge if it's inline attachment
                    //Only judge from contentId is not enough.
                    uiAtt.isInline = att.mIsInline;
                 // TS: Gantao 2015-09-19 EMAIL BUGFIX_570084 ADD_E
                    uiAtts.add(uiAtt);
                }
                values.put(UIProvider.MessageColumns.ATTACHMENTS, "@?"); // @ for literal
                attachmentJson = com.tct.mail.providers.Attachment.toJSONArray(uiAtts);
            }
            if (msg.mDraftInfo != 0) {
                values.put(UIProvider.MessageColumns.APPEND_REF_MESSAGE_CONTENT,
                        (msg.mDraftInfo & Message.DRAFT_INFO_APPEND_REF_MESSAGE) != 0 ? 1 : 0);
                values.put(UIProvider.MessageColumns.QUOTE_START_POS,
                        msg.mDraftInfo & Message.DRAFT_INFO_QUOTE_POS_MASK);
            }
            if ((msg.mFlags & Message.FLAG_INCOMING_MEETING_INVITE) != 0) {
                values.put(UIProvider.MessageColumns.EVENT_INTENT_URI,
                        "content://ui.email2.android.com/event/" + msg.mId);
            }
            /**
             * HACK: override the attachment uri to contain a query parameter
             * This forces the message footer to reload the attachment display when the message is
             * fully loaded.
             */
            final Uri attachmentListUri = uiUri("uiattachments", messageId).buildUpon()
                    .appendQueryParameter("MessageLoaded",
                            msg.mFlagLoaded == Message.FLAG_LOADED_COMPLETE ? "true" : "false")
                    .build();
            values.put(UIProvider.MessageColumns.ATTACHMENT_LIST_URI, attachmentListUri.toString());
        }
        StringBuilder sb = genSelect(getMessageViewMap(), uiProjection, values);
        sb.append(" FROM " + Message.TABLE_NAME + " LEFT JOIN " + Body.TABLE_NAME +
                " ON " + BodyColumns.MESSAGE_KEY + "=" + Message.TABLE_NAME + "." +
                        MessageColumns._ID +
                " WHERE " + Message.TABLE_NAME + "." + MessageColumns._ID + "=?");
        String sql = sb.toString();
        return new MessageQuery(sql, attachmentJson);
    }

    private static void appendConversationInfoColumns(final StringBuilder stringBuilder) {
        // TODO(skennedy) These columns are needed for the respond call for ConversationInfo :(
        // There may be a better way to do this, but since the projection is specified by the
        // unified UI code, it can't ask for these columns.
        //TS: chao.zhang 2015-09-14 EMAIL FEATURE-585337 MOD_S
        //NOTE: Why append here,casue Conversaion want query from Message,add the columns to SQL.
        stringBuilder.append(',').append(MessageColumns.DISPLAY_NAME)
                .append(',').append(MessageColumns.FROM_LIST)
                .append(',').append(MessageColumns.TO_LIST)
                .append(',').append(MessageColumns.SENDING_STATUS);
        //TS: chao.zhang 2015-09-14 EMAIL FEATURE-585337 MOD_E
    }

    /**
     * Generate the "message list" SQLite query, given a projection from UnifiedEmail
     *
     * @param uiProjection as passed from UnifiedEmail
     * @param unseenOnly <code>true</code> to only return unseen messages
     * @return the SQLite query to be executed on the EmailProvider database
     */
    private static String genQueryMailboxMessages(String[] uiProjection, final boolean unseenOnly,String selection, String order) {
        StringBuilder sb = genSelect(getMessageListMap(), uiProjection);  //TS: zheng.zou 2016-1-14 EMAIL TASK_1431225 MOD
        appendConversationInfoColumns(sb);
        sb.append(" FROM " + Message.TABLE_NAME + " WHERE " +
                Message.FLAG_LOADED_SELECTION + " ");
        if (unseenOnly) {
            sb.append("AND ").append(MessageColumns.FLAG_SEEN).append(" = 0 ");
            sb.append("AND ").append(MessageColumns.FLAG_READ).append(" = 0 ");
        }
        /// TCT: append local search selection. @{
        if (!TextUtils.isEmpty(selection)) {
            sb.append("AND ").append(selection);
        }
        /// @}
        sb.append("AND " + MessageColumns.MAILBOX_KEY + "=? ");
        sb.append("ORDER BY " + order + " ");
        sb.append("LIMIT " + UIProvider.CONVERSATION_PROJECTION_QUERY_CURSOR_WINDOW_LIMIT);
        return sb.toString();
    }

    /**
     * Generate various virtual mailbox SQLite queries, given a projection from UnifiedEmail
     *
     * @param uiProjection as passed from UnifiedEmail
     * @param mailboxId the id of the virtual mailbox
     * @param unseenOnly <code>true</code> to only return unseen messages
     * @param selection TCT: append local search selection
     * @return the SQLite query to be executed on the EmailProvider database
     */
    private static Cursor getVirtualMailboxMessagesCursor(SQLiteDatabase db, String[] uiProjection,
            long mailboxId, final boolean unseenOnly, String selection, String order) {
        ContentValues values = new ContentValues();
        //TS: junwei-xu 2015-10-29 EMAIL BUGFIX-814477 MOD-S
        //NOTE: Only combined mailbox need show conversation color.
        if (isCombinedMailbox(mailboxId)) {
            values.put(UIProvider.ConversationColumns.COLOR, CONVERSATION_COLOR);
        }
        //TS: junwei-xu 2015-10-29 EMAIL BUGFIX-814477 MOD-E
        final int virtualMailboxId = getVirtualMailboxType(mailboxId);
        final String[] selectionArgs;
        StringBuilder sb = genSelect(getMessageListMap(), uiProjection, values);
        appendConversationInfoColumns(sb);
        sb.append(" FROM " + Message.TABLE_NAME + " WHERE " +
                Message.FLAG_LOADED_SELECTION + " AND ");
        if (isCombinedMailbox(mailboxId)) {
            if (unseenOnly) {
                sb.append(MessageColumns.FLAG_SEEN).append("=0 AND ");
                sb.append(MessageColumns.FLAG_READ).append("=0 AND ");
            }
            selectionArgs = null;
        } else {
            if (virtualMailboxId == Mailbox.TYPE_INBOX) {
                throw new IllegalArgumentException("No virtual mailbox for: " + mailboxId);
            }
            sb.append(MessageColumns.ACCOUNT_KEY).append("=? AND ");
            selectionArgs = new String[]{getVirtualMailboxAccountIdString(mailboxId)};
        }
        int mailboxType = getVirtualMailboxType(mailboxId);
        switch (mailboxType) {
            case Mailbox.TYPE_INBOX:
                sb.append(MessageColumns.MAILBOX_KEY + " IN (SELECT " + MailboxColumns._ID +
                        " FROM " + Mailbox.TABLE_NAME + " WHERE " + MailboxColumns.TYPE +
                        "=" + Mailbox.TYPE_INBOX + ")");
                break;
            case Mailbox.TYPE_STARRED:
                //TS: zheng.zou 2015-06-26 EMAIL BUGFIX_1029647 MOD_S
                //note: filter out already deleted messages
//                sb.append(MessageColumns.FLAG_FAVORITE + "=1");
                sb.append(MessageColumns.FLAG_FAVORITE + "=1 AND " + MessageColumns.MAILBOX_KEY +
                        " NOT IN (SELECT " + MailboxColumns.ID + " FROM " + Mailbox.TABLE_NAME +
                        " WHERE " + MailboxColumns.TYPE + "=" + Mailbox.TYPE_TRASH + ")");
                //TS: zheng.zou 2015-06-26 EMAIL BUGFIX_1029647 MOD_E
                break;
            case Mailbox.TYPE_UNREAD:
                sb.append(MessageColumns.FLAG_READ + "=0 AND " + MessageColumns.MAILBOX_KEY +
                        " NOT IN (SELECT " + MailboxColumns._ID + " FROM " + Mailbox.TABLE_NAME +
                        " WHERE " + MailboxColumns.TYPE + "=" + Mailbox.TYPE_TRASH + ")");
                break;
            default:
                throw new IllegalArgumentException("No virtual mailbox for: " + mailboxId);
        }
        /// TCT: support local search, append customer selection. @{
        if (!TextUtils.isEmpty(selection)) {
            sb.append(" AND ").append(selection);
            LogUtils.logFeature(LogTag.SEARCH_TAG, " VirtualMailbox append local search selection [%s]", sb.toString());
        } else {
            LogUtils.logFeature(LogTag.SEARCH_TAG, " VirtualMailbox selection [%s]", sb.toString());
        }
        /// @}
        sb.append(" ORDER BY " + order);
        return db.rawQuery(sb.toString(), selectionArgs);
    }

    /**
     * Generate the "message list" SQLite query, given a projection from UnifiedEmail
     *
     * @param uiProjection as passed from UnifiedEmail
     * @return the SQLite query to be executed on the EmailProvider database
     */
    private static String genQueryConversation(String[] uiProjection) {
        StringBuilder sb = genSelect(getMessageListMap(), uiProjection);
        sb.append(" FROM " + Message.TABLE_NAME + " WHERE " + MessageColumns._ID + "=?");
        return sb.toString();
    }

    /**
     * Generate the "top level folder list" SQLite query, given a projection from UnifiedEmail
     *
     * @param uiProjection as passed from UnifiedEmail
     * @return the SQLite query to be executed on the EmailProvider database
     */
    private static String genQueryAccountMailboxes(String[] uiProjection) {
        StringBuilder sb = genSelect(getFolderListMap(), uiProjection);
        sb.append(" FROM " + Mailbox.TABLE_NAME + " WHERE " + MailboxColumns.ACCOUNT_KEY +
                "=? AND " + MailboxColumns.TYPE + " < " + Mailbox.TYPE_NOT_EMAIL +
                " AND " + MailboxColumns.TYPE + " != " + Mailbox.TYPE_SEARCH +
                " AND " + MailboxColumns.PARENT_KEY + " < 0 ORDER BY ");
        sb.append(MAILBOX_ORDER_BY);
        return sb.toString();
    }

    /**
     * Generate the "all folders" SQLite query, given a projection from UnifiedEmail.  The list is
     * sorted by the name as it appears in a hierarchical listing
     *
     * @param uiProjection as passed from UnifiedEmail
     * @return the SQLite query to be executed on the EmailProvider database
     */
    private static String genQueryAccountAllMailboxes(String[] uiProjection) {
        StringBuilder sb = genSelect(getFolderListMap(), uiProjection);
        // Use a derived column to choose either hierarchicalName or displayName
        sb.append(", case when " + MailboxColumns.HIERARCHICAL_NAME + " is null then " +
                MailboxColumns.DISPLAY_NAME + " else " + MailboxColumns.HIERARCHICAL_NAME +
                " end as h_name");
        // Order by the derived column
        sb.append(" FROM " + Mailbox.TABLE_NAME + " WHERE " + MailboxColumns.ACCOUNT_KEY +
                "=? AND " + MailboxColumns.TYPE + " < " + Mailbox.TYPE_NOT_EMAIL +
                " AND " + MailboxColumns.TYPE + " != " + Mailbox.TYPE_SEARCH +
                " ORDER BY h_name");
        return sb.toString();
    }

    /**
     * Generate the "recent folder list" SQLite query, given a projection from UnifiedEmail
     *
     * @param uiProjection as passed from UnifiedEmail
     * @return the SQLite query to be executed on the EmailProvider database
     */
    private static String genQueryRecentMailboxes(String[] uiProjection) {
        StringBuilder sb = genSelect(getFolderListMap(), uiProjection);
        sb.append(" FROM " + Mailbox.TABLE_NAME + " WHERE " + MailboxColumns.ACCOUNT_KEY +
                "=? AND " + MailboxColumns.TYPE + " < " + Mailbox.TYPE_NOT_EMAIL +
                " AND " + MailboxColumns.TYPE + " != " + Mailbox.TYPE_SEARCH +
                " AND " + MailboxColumns.PARENT_KEY + " < 0 AND " +
                MailboxColumns.LAST_TOUCHED_TIME + " > 0 ORDER BY " +
                MailboxColumns.LAST_TOUCHED_TIME + " DESC");
        return sb.toString();
    }

    private int getFolderCapabilities(EmailServiceInfo info, int mailboxType, long mailboxId) {
        // Special case for Search folders: only permit delete, do not try to give any other caps.
        if (mailboxType == Mailbox.TYPE_SEARCH) {
            return UIProvider.FolderCapabilities.DELETE;
        }

        // All folders support delete, except drafts.
        int caps = 0;
        if (mailboxType != Mailbox.TYPE_DRAFTS) {
            caps = UIProvider.FolderCapabilities.DELETE;
        }
        if (info != null && info.offerLookback) {
            // Protocols supporting lookback support settings
            caps |= UIProvider.FolderCapabilities.SUPPORTS_SETTINGS;
        }

        //TS: jian.xu 2015-10-17 EMAIL BUGFIX-721773 MOD_S
        //NOTE: Drafts folder must supports move messages
        if (mailboxType == Mailbox.TYPE_MAIL || mailboxType == Mailbox.TYPE_TRASH ||
                mailboxType == Mailbox.TYPE_JUNK || mailboxType == Mailbox.TYPE_INBOX ||
                mailboxType == Mailbox.TYPE_DRAFTS) {
            // If the mailbox can accept moved mail, report that as well
            caps |= UIProvider.FolderCapabilities.CAN_ACCEPT_MOVED_MESSAGES;
            caps |= UIProvider.FolderCapabilities.ALLOWS_REMOVE_CONVERSATION;
        }
        //TS: jian.xu 2015-10-17 EMAIL BUGFIX-721773 MOD_S

        // For trash, we don't allow undo
        if (mailboxType == Mailbox.TYPE_TRASH) {
            caps =  UIProvider.FolderCapabilities.CAN_ACCEPT_MOVED_MESSAGES |
                    UIProvider.FolderCapabilities.ALLOWS_REMOVE_CONVERSATION |
                    UIProvider.FolderCapabilities.DELETE |
                    UIProvider.FolderCapabilities.DELETE_ACTION_FINAL;
        }
        if (isVirtualMailbox(mailboxId)) {
            caps |= UIProvider.FolderCapabilities.IS_VIRTUAL;
        }

        // If we don't know the protocol or the protocol doesn't support it, don't allow moving
        // messages
        if (info == null || !info.offerMoveTo) {
            caps &= ~UIProvider.FolderCapabilities.CAN_ACCEPT_MOVED_MESSAGES &
                    ~UIProvider.FolderCapabilities.ALLOWS_REMOVE_CONVERSATION &
                    ~UIProvider.FolderCapabilities.ALLOWS_MOVE_TO_INBOX;
        }

        // If the mailbox stores outgoing mail, show recipients instead of senders
        // (however the Drafts folder shows neither senders nor recipients... just the word "Draft")
        if (mailboxType == Mailbox.TYPE_OUTBOX || mailboxType == Mailbox.TYPE_SENT) {
            caps |= UIProvider.FolderCapabilities.SHOW_RECIPIENTS;
        }

        return caps;
    }

    /**
     * Generate a "single mailbox" SQLite query, given a projection from UnifiedEmail
     *
     * @param uiProjection as passed from UnifiedEmail
     * @return the SQLite query to be executed on the EmailProvider database
     */
    private String genQueryMailbox(String[] uiProjection, String id) {
        long mailboxId = Long.parseLong(id);
        ContentValues values = new ContentValues(3);
        if (mSearchParams != null && mailboxId == mSearchParams.mSearchMailboxId) {
            // "load more" is valid for search results
            values.put(UIProvider.FolderColumns.LOAD_MORE_URI,
                    uiUriString("uiloadmore", mailboxId));
            values.put(UIProvider.FolderColumns.CAPABILITIES, UIProvider.FolderCapabilities.DELETE);
        } else {
            Context context = getContext();
            Mailbox mailbox = Mailbox.restoreMailboxWithId(context, mailboxId);
            // Make sure we can't get NPE if mailbox has disappeared (the result will end up moot)
            if (mailbox != null) {
                String protocol = Account.getProtocol(context, mailbox.mAccountKey);
                EmailServiceInfo info = EmailServiceUtils.getServiceInfo(context, protocol);
                // All folders support delete
                if (info != null && info.offerLoadMore) {
                    // "load more" is valid for protocols not supporting "lookback"
                    values.put(UIProvider.FolderColumns.LOAD_MORE_URI,
                            uiUriString("uiloadmore", mailboxId));
                }
                values.put(UIProvider.FolderColumns.CAPABILITIES,
                        getFolderCapabilities(info, mailbox.mType, mailboxId));
                // The persistent id is used to form a filename, so we must ensure that it doesn't
                // include illegal characters (such as '/'). Only perform the encoding if this
                // query wants the persistent id.
                boolean shouldEncodePersistentId = false;
                if (uiProjection == null) {
                    shouldEncodePersistentId = true;
                } else {
                    for (final String column : uiProjection) {
                        if (TextUtils.equals(column, UIProvider.FolderColumns.PERSISTENT_ID)) {
                            shouldEncodePersistentId = true;
                            break;
                        }
                    }
                }
                if (shouldEncodePersistentId) {
                    values.put(UIProvider.FolderColumns.PERSISTENT_ID,
                            Base64.encodeToString(mailbox.mServerId.getBytes(),
                                    Base64.URL_SAFE | Base64.NO_WRAP | Base64.NO_PADDING));
                }
             }
        }
        StringBuilder sb = genSelect(getFolderListMap(), uiProjection, values);
        sb.append(" FROM " + Mailbox.TABLE_NAME + " WHERE " + MailboxColumns._ID + "=?");
        return sb.toString();
    }

    public static final String LEGACY_AUTHORITY = "ui.email.android.com";
    private static final Uri BASE_EXTERNAL_URI = Uri.parse("content://" + LEGACY_AUTHORITY);

    private static final Uri BASE_EXTERAL_URI2 = Uri.parse("content://ui.email2.android.com");

    private static String getExternalUriString(String segment, String account) {
        return BASE_EXTERNAL_URI.buildUpon().appendPath(segment)
                .appendQueryParameter("account", account).build().toString();
    }

    private static String getExternalUriStringEmail2(String segment, String account) {
        return BASE_EXTERAL_URI2.buildUpon().appendPath(segment)
                .appendQueryParameter("account", account).build().toString();
    }

    private static String getBits(int bitField) {
        StringBuilder sb = new StringBuilder(" ");
        for (int i = 0; i < 32; i++, bitField >>= 1) {
            if ((bitField & 1) != 0) {
                sb.append(i)
                        .append(" ");
            }
        }
        return sb.toString();
    }

    private static int getCapabilities(Context context, long accountId) {
        final Account account = Account.restoreAccountWithId(context, accountId);
        if (account == null) {
            LogUtils.d(TAG, "Account %d not found during getCapabilities", accountId);
            return 0;
        }
        // Account capabilities are based on protocol -- different protocols (and, for EAS,
        // different protocol versions) support different feature sets.
        final String protocol = account.getProtocol(context);
        int capabilities;
        if (TextUtils.equals(context.getString(R.string.protocol_imap), protocol) ||
                TextUtils.equals(context.getString(R.string.protocol_legacy_imap), protocol)) {
            capabilities = AccountCapabilities.SYNCABLE_FOLDERS |
                    AccountCapabilities.SERVER_SEARCH |
                    AccountCapabilities.FOLDER_SERVER_SEARCH |
                    AccountCapabilities.UNDO |
                    AccountCapabilities.DISCARD_CONVERSATION_DRAFTS;
        } else if (TextUtils.equals(context.getString(R.string.protocol_pop3), protocol)) {
            capabilities = AccountCapabilities.UNDO |
                    AccountCapabilities.DISCARD_CONVERSATION_DRAFTS;
        } else if (TextUtils.equals(context.getString(R.string.protocol_eas), protocol)) {
            final String easVersion = account.mProtocolVersion;
            double easVersionDouble = 2.5D;
            if (easVersion != null) {
                try {
                    easVersionDouble = Double.parseDouble(easVersion);
                } catch (final NumberFormatException e) {
                    // Use the default (lowest) set of capabilities.
                }
            }
            if (easVersionDouble >= 12.0D) {
                capabilities = AccountCapabilities.SYNCABLE_FOLDERS |
                        AccountCapabilities.SERVER_SEARCH |
                        AccountCapabilities.FOLDER_SERVER_SEARCH |
                        AccountCapabilities.SMART_REPLY |
                        AccountCapabilities.SMART_FORWARD |          //TS: zheng.zou 2015-11-03 EMAIL BUGFIX_858353 ADD
                        AccountCapabilities.UNDO |
                        AccountCapabilities.DISCARD_CONVERSATION_DRAFTS;
            } else {
                capabilities = AccountCapabilities.SYNCABLE_FOLDERS |
                        AccountCapabilities.SMART_REPLY |
                        AccountCapabilities.UNDO |
                        AccountCapabilities.DISCARD_CONVERSATION_DRAFTS;
            }
        } else {
            LogUtils.w(TAG, "Unknown protocol for account %d", accountId);
            return 0;
        }
        LogUtils.d(TAG, "getCapabilities() for %d (protocol %s): 0x%x %s", accountId, protocol,
                capabilities, getBits(capabilities));

        // If the configuration states that feedback is supported, add that capability
        final Resources res = context.getResources();
        if (res.getBoolean(R.bool.feedback_supported)) {
            capabilities |= AccountCapabilities.SEND_FEEDBACK;
        }

        // If we can find a help URL then add the Help capability
        if (!TextUtils.isEmpty(context.getResources().getString(R.string.help_uri))) {
            capabilities |= AccountCapabilities.HELP_CONTENT;
        }

        capabilities |= AccountCapabilities.EMPTY_TRASH;

        // TODO: Should this be stored per-account, or some other mechanism?
        capabilities |= AccountCapabilities.NESTED_FOLDERS;

        // sanitization happens lazily in the EmailMessageCursor as HTML email bodies are requested
        capabilities |= UIProvider.AccountCapabilities.SANITIZED_HTML;

        return capabilities;
    }

    /**
     * Generate a "single account" SQLite query, given a projection from UnifiedEmail
     *
     * @param uiProjection as passed from UnifiedEmail
     * @param id account row ID
     * @return the SQLite query to be executed on the EmailProvider database
     */
    private String genQueryAccount(String[] uiProjection, String id) {
        final ContentValues values = new ContentValues();
        final long accountId = Long.parseLong(id);
        final Context context = getContext();

        EmailServiceInfo info = null;

        // TODO: If uiProjection is null, this will NPE. We should do everything here if it's null.
        final Set<String> projectionColumns = ImmutableSet.copyOf(uiProjection);

        if (projectionColumns.contains(UIProvider.AccountColumns.CAPABILITIES)) {
            // Get account capabilities from the service
            values.put(UIProvider.AccountColumns.CAPABILITIES, getCapabilities(context, accountId));
        }
        if (projectionColumns.contains(UIProvider.AccountColumns.SETTINGS_INTENT_URI)) {
            values.put(UIProvider.AccountColumns.SETTINGS_INTENT_URI,
                    getExternalUriString("settings", id));
        }
        if (projectionColumns.contains(UIProvider.AccountColumns.COMPOSE_URI)) {
            values.put(UIProvider.AccountColumns.COMPOSE_URI,
                    getExternalUriStringEmail2("compose", id));
        }
        if (projectionColumns.contains(UIProvider.AccountColumns.REAUTHENTICATION_INTENT_URI)) {
            values.put(UIProvider.AccountColumns.REAUTHENTICATION_INTENT_URI,
                    HeadlessAccountSettingsLoader.getIncomingSettingsUri(accountId)
                    .toString());
        }
        if (projectionColumns.contains(UIProvider.AccountColumns.MIME_TYPE)) {
            values.put(UIProvider.AccountColumns.MIME_TYPE, EMAIL_APP_MIME_TYPE);
        }
        if (projectionColumns.contains(UIProvider.AccountColumns.COLOR)) {
            values.put(UIProvider.AccountColumns.COLOR, ACCOUNT_COLOR);
        }

        // TODO: if we're getting the values out of MailPrefs then we don't need to be passing the
        // values this way
        final MailPrefs mailPrefs = MailPrefs.get(getContext());
        if (projectionColumns.contains(UIProvider.AccountColumns.SettingsColumns.CONFIRM_DELETE)) {
            values.put(UIProvider.AccountColumns.SettingsColumns.CONFIRM_DELETE,
                    mailPrefs.getConfirmDelete() ? "1" : "0");
        }
        if (projectionColumns.contains(UIProvider.AccountColumns.SettingsColumns.CONFIRM_SEND)) {
            values.put(UIProvider.AccountColumns.SettingsColumns.CONFIRM_SEND,
                    mailPrefs.getConfirmSend() ? "1" : "0");
        }
        if (projectionColumns.contains(UIProvider.AccountColumns.SettingsColumns.SWIPE)) {
            values.put(UIProvider.AccountColumns.SettingsColumns.SWIPE,
                    mailPrefs.getConversationListSwipeActionInteger(false));
        }
        if (projectionColumns.contains(
                UIProvider.AccountColumns.SettingsColumns.CONV_LIST_ICON)) {
            values.put(UIProvider.AccountColumns.SettingsColumns.CONV_LIST_ICON,
                    getConversationListIcon(mailPrefs));
        }
        if (projectionColumns.contains(UIProvider.AccountColumns.SettingsColumns.AUTO_ADVANCE)) {
            values.put(UIProvider.AccountColumns.SettingsColumns.AUTO_ADVANCE,
                    Integer.toString(mailPrefs.getAutoAdvanceMode()));
        }
        // Set default inbox, if we've got an inbox; otherwise, say initial sync needed
        final long inboxMailboxId =
                Mailbox.findMailboxOfType(context, accountId, Mailbox.TYPE_INBOX);
        if (projectionColumns.contains(UIProvider.AccountColumns.SettingsColumns.DEFAULT_INBOX) &&
                inboxMailboxId != Mailbox.NO_MAILBOX) {
            values.put(UIProvider.AccountColumns.SettingsColumns.DEFAULT_INBOX,
                    uiUriString("uifolder", inboxMailboxId));
        } else {
            values.put(UIProvider.AccountColumns.SettingsColumns.DEFAULT_INBOX,
                    uiUriString("uiinbox", accountId));
        }
        if (projectionColumns.contains(
                UIProvider.AccountColumns.SettingsColumns.DEFAULT_INBOX_NAME) &&
                inboxMailboxId != Mailbox.NO_MAILBOX) {
            values.put(UIProvider.AccountColumns.SettingsColumns.DEFAULT_INBOX_NAME,
                    Mailbox.getDisplayName(context, inboxMailboxId));
        }
        if (projectionColumns.contains(UIProvider.AccountColumns.SYNC_STATUS)) {
            if (inboxMailboxId != Mailbox.NO_MAILBOX) {
                // TS: xiaolin.li 2015-01-13 EMAIL BUGFIX-883410
                values.put(UIProvider.AccountColumns.SYNC_STATUS, com.tct.mail.utils.UIProvider.SyncStatus.NO_SYNC);
            } else {
                // TS: xiaolin.li 2015-01-13 EMAIL BUGFIX-883410
                values.put(UIProvider.AccountColumns.SYNC_STATUS,
                                com.tct.mail.utils.UIProvider.SyncStatus.INITIAL_SYNC_NEEDED);
            }
        }
        if (projectionColumns.contains(UIProvider.AccountColumns.UPDATE_SETTINGS_URI)) {
            values.put(UIProvider.AccountColumns.UPDATE_SETTINGS_URI,
                    uiUriString("uiacctsettings", -1));
        }
        if (projectionColumns.contains(UIProvider.AccountColumns.ENABLE_MESSAGE_TRANSFORMS)) {
            // Email is now sanitized, which grants the ability to inject beautifying javascript.
            values.put(UIProvider.AccountColumns.ENABLE_MESSAGE_TRANSFORMS, 1);
        }
        if (projectionColumns.contains(
                UIProvider.AccountColumns.SettingsColumns.IMPORTANCE_MARKERS_ENABLED)) {
            // Email doesn't support priority inbox, so always state importance markers disabled.
            values.put(UIProvider.AccountColumns.SettingsColumns.IMPORTANCE_MARKERS_ENABLED, "0");
        }
        if (projectionColumns.contains(
                UIProvider.AccountColumns.SettingsColumns.SHOW_CHEVRONS_ENABLED)) {
            // Email doesn't support priority inbox, so always state show chevrons disabled.
            values.put(UIProvider.AccountColumns.SettingsColumns.SHOW_CHEVRONS_ENABLED, "0");
        }
        if (projectionColumns.contains(
                UIProvider.AccountColumns.SettingsColumns.SETUP_INTENT_URI)) {
            // Set the setup intent if needed
            // TODO We should clarify/document the trash/setup relationship
            long trashId = Mailbox.findMailboxOfType(context, accountId, Mailbox.TYPE_TRASH);
            if (trashId == Mailbox.NO_MAILBOX) {
                info = EmailServiceUtils.getServiceInfoForAccount(context, accountId);
                if (info != null && info.requiresSetup) {
                    values.put(UIProvider.AccountColumns.SettingsColumns.SETUP_INTENT_URI,
                            getExternalUriString("setup", id));
                }
            }
        }
        if (projectionColumns.contains(UIProvider.AccountColumns.TYPE)) {
            final String type;
            if (info == null) {
                info = EmailServiceUtils.getServiceInfoForAccount(context, accountId);
            }
            if (info != null) {
                type = info.accountType;
            } else {
                type = "unknown";
            }

            values.put(UIProvider.AccountColumns.TYPE, type);
        }
        if (projectionColumns.contains(UIProvider.AccountColumns.SettingsColumns.MOVE_TO_INBOX) &&
                inboxMailboxId != Mailbox.NO_MAILBOX) {
            values.put(UIProvider.AccountColumns.SettingsColumns.MOVE_TO_INBOX,
                    uiUriString("uifolder", inboxMailboxId));
        }
        if (projectionColumns.contains(UIProvider.AccountColumns.SYNC_AUTHORITY)) {
            values.put(UIProvider.AccountColumns.SYNC_AUTHORITY, EmailContent.AUTHORITY);
        }
        if (projectionColumns.contains(UIProvider.AccountColumns.QUICK_RESPONSE_URI)) {
            values.put(UIProvider.AccountColumns.QUICK_RESPONSE_URI,
                    combinedUriString("quickresponse/account", id));
        }
        if (projectionColumns.contains(UIProvider.AccountColumns.SETTINGS_FRAGMENT_CLASS)) {
            values.put(UIProvider.AccountColumns.SETTINGS_FRAGMENT_CLASS,
                    AccountSettingsFragment.class.getName());
        }
        if (projectionColumns.contains(UIProvider.AccountColumns.SettingsColumns.REPLY_BEHAVIOR)) {
            values.put(UIProvider.AccountColumns.SettingsColumns.REPLY_BEHAVIOR,
                    mailPrefs.getDefaultReplyAll()
                            ? UIProvider.DefaultReplyBehavior.REPLY_ALL
                            : UIProvider.DefaultReplyBehavior.REPLY);
        }
        if (projectionColumns.contains(UIProvider.AccountColumns.SettingsColumns.SHOW_IMAGES)) {
            // TS: tao.gan 2015-08-12 EMAIL FEATURE-ID MOD_S
            //Now we can controll whether show inline images from account setting.
            int showImagesValue = getShowImagesValue(context, accountId);
            LogUtils.i(TAG,"Show images value from account setting : %d", showImagesValue);
            values.put(UIProvider.AccountColumns.SettingsColumns.SHOW_IMAGES, showImagesValue);
            // TS: tao.gan 2015-08-12 EMAIL FEATURE-ID MOD_E
        }

        final StringBuilder sb = genSelect(getAccountListMap(getContext()), uiProjection, values);
        sb.append(" FROM " + Account.TABLE_NAME + " WHERE " + AccountColumns._ID + "=?");
        return sb.toString();
    }

    // TS: tao.gan 2015-08-12 EMAIL FEATURE-ID ADD_S
    private int getShowImagesValue(Context context, long accountId) {
        Account account = Account.restoreAccountWithId(context, accountId);
        if (account == null) {
            LogUtils.d(TAG, "Account %d not found during getShowImagesValue", accountId);
            return 0;
        }
        return account.mInlineImages;
    }
    // TS: tao.gan 2015-08-12 EMAIL FEATURE-ID ADD_E
    /**
     * Generate a Uri string for a combined mailbox uri
     * @param type the uri command type (e.g. "uimessages")
     * @param id the id of the item (e.g. an account, mailbox, or message id)
     * @return a Uri string
     */
    private static String combinedUriString(String type, String id) {
        return "content://" + EmailContent.AUTHORITY + "/" + type + "/" + id;
    }

    public static final long COMBINED_ACCOUNT_ID = 0x10000000;

    /**
     * Generate an id for a combined mailbox of a given type
     * @param type the mailbox type for the combined mailbox
     * @return the id, as a String
     */
    private static String combinedMailboxId(int type) {
        return Long.toString(Account.ACCOUNT_ID_COMBINED_VIEW + type);
    }

    public static long getVirtualMailboxId(long accountId, int type) {
        return (accountId << 32) + type;
    }

    private static boolean isVirtualMailbox(long mailboxId) {
        return mailboxId >= 0x100000000L;
    }

    private static boolean isCombinedMailbox(long mailboxId) {
        return (mailboxId >> 32) == COMBINED_ACCOUNT_ID;
    }

    private static long getVirtualMailboxAccountId(long mailboxId) {
        return mailboxId >> 32;
    }

    private static String getVirtualMailboxAccountIdString(long mailboxId) {
        return Long.toString(mailboxId >> 32);
    }

    private static int getVirtualMailboxType(long mailboxId) {
        return (int)(mailboxId & 0xF);
    }

    private void addCombinedAccountRow(MatrixCursor mc) {
        final long lastUsedAccountId =
                Preferences.getPreferences(getContext()).getLastUsedAccountId();
        final long id = Account.getDefaultAccountId(getContext(), lastUsedAccountId);
        if (id == Account.NO_ACCOUNT) return;

        // Build a map of the requested columns to the appropriate positions
        final ImmutableMap.Builder<String, Integer> builder =
                new ImmutableMap.Builder<String, Integer>();
        final String[] columnNames = mc.getColumnNames();
        for (int i = 0; i < columnNames.length; i++) {
            builder.put(columnNames[i], i);
        }
        final Map<String, Integer> colPosMap = builder.build();

        final MailPrefs mailPrefs = MailPrefs.get(getContext());

        final Object[] values = new Object[columnNames.length];
        if (colPosMap.containsKey(BaseColumns._ID)) {
            values[colPosMap.get(BaseColumns._ID)] = 0;
        }
        if (colPosMap.containsKey(UIProvider.AccountColumns.CAPABILITIES)) {
            values[colPosMap.get(UIProvider.AccountColumns.CAPABILITIES)] =
                    AccountCapabilities.UNDO | AccountCapabilities.VIRTUAL_ACCOUNT;
        }
        if (colPosMap.containsKey(UIProvider.AccountColumns.FOLDER_LIST_URI)) {
            values[colPosMap.get(UIProvider.AccountColumns.FOLDER_LIST_URI)] =
                    combinedUriString("uifolders", COMBINED_ACCOUNT_ID_STRING);
        }
        if (colPosMap.containsKey(UIProvider.AccountColumns.NAME)) {
            values[colPosMap.get(UIProvider.AccountColumns.NAME)] = getContext().getString(
                    R.string.mailbox_list_account_selector_combined_view);
        }
        if (colPosMap.containsKey(UIProvider.AccountColumns.ACCOUNT_MANAGER_NAME)) {
            values[colPosMap.get(UIProvider.AccountColumns.ACCOUNT_MANAGER_NAME)] =
                    getContext().getString(R.string.mailbox_list_account_selector_combined_view);
        }
        if (colPosMap.containsKey(UIProvider.AccountColumns.ACCOUNT_ID)) {
            values[colPosMap.get(UIProvider.AccountColumns.ACCOUNT_ID)] = "Account Id";
        }
        if (colPosMap.containsKey(UIProvider.AccountColumns.TYPE)) {
            values[colPosMap.get(UIProvider.AccountColumns.TYPE)] = "unknown";
        }
        if (colPosMap.containsKey(UIProvider.AccountColumns.UNDO_URI)) {
            // AM: Kexue.Geng 2015-02-13 EMAIL BUGFIX_929979 MOD_S
            values[colPosMap.get(UIProvider.AccountColumns.UNDO_URI)] =
                    "content://" + EmailContent.AUTHORITY + "/uiundo";
            // AM: Kexue.Geng 2015-02-13 EMAIL BUGFIX_929979 MOD_E
        }
        if (colPosMap.containsKey(UIProvider.AccountColumns.URI)) {
            values[colPosMap.get(UIProvider.AccountColumns.URI)] =
                    combinedUriString("uiaccount", COMBINED_ACCOUNT_ID_STRING);
        }
        if (colPosMap.containsKey(UIProvider.AccountColumns.MIME_TYPE)) {
            values[colPosMap.get(UIProvider.AccountColumns.MIME_TYPE)] =
                    EMAIL_APP_MIME_TYPE;
        }
        if (colPosMap.containsKey(UIProvider.AccountColumns.SETTINGS_INTENT_URI)) {
            values[colPosMap.get(UIProvider.AccountColumns.SETTINGS_INTENT_URI)] =
                    getExternalUriString("settings", COMBINED_ACCOUNT_ID_STRING);
        }
        if (colPosMap.containsKey(UIProvider.AccountColumns.COMPOSE_URI)) {
            values[colPosMap.get(UIProvider.AccountColumns.COMPOSE_URI)] =
                    // TS: junwei-xu 2015-03-23 EMAIL BUGFIX_980239 MOD_S
                    //getExternalUriStringEmail2("compose", Long.toString(id));
                    getExternalUriStringEmail2("compose", COMBINED_ACCOUNT_ID_STRING);
                    // TS: junwei-xu 2015-04-23 EMAIL BUGFIX_980239 MOD_E

        }
        if (colPosMap.containsKey(UIProvider.AccountColumns.UPDATE_SETTINGS_URI)) {
            values[colPosMap.get(UIProvider.AccountColumns.UPDATE_SETTINGS_URI)] =
                    uiUriString("uiacctsettings", -1);
        }

        if (colPosMap.containsKey(UIProvider.AccountColumns.SettingsColumns.AUTO_ADVANCE)) {
            values[colPosMap.get(UIProvider.AccountColumns.SettingsColumns.AUTO_ADVANCE)] =
                    Integer.toString(mailPrefs.getAutoAdvanceMode());
        }
        if (colPosMap.containsKey(UIProvider.AccountColumns.SettingsColumns.SNAP_HEADERS)) {
            values[colPosMap.get(UIProvider.AccountColumns.SettingsColumns.SNAP_HEADERS)] =
                    Integer.toString(UIProvider.SnapHeaderValue.ALWAYS);
        }
        //.add(UIProvider.SettingsColumns.SIGNATURE, AccountColumns.SIGNATURE)
        if (colPosMap.containsKey(UIProvider.AccountColumns.SettingsColumns.REPLY_BEHAVIOR)) {
            values[colPosMap.get(UIProvider.AccountColumns.SettingsColumns.REPLY_BEHAVIOR)] =
                    Integer.toString(mailPrefs.getDefaultReplyAll()
                            ? UIProvider.DefaultReplyBehavior.REPLY_ALL
                            : UIProvider.DefaultReplyBehavior.REPLY);
        }
        if (colPosMap.containsKey(UIProvider.AccountColumns.SettingsColumns.CONV_LIST_ICON)) {
            values[colPosMap.get(UIProvider.AccountColumns.SettingsColumns.CONV_LIST_ICON)] =
                    getConversationListIcon(mailPrefs);
        }
        if (colPosMap.containsKey(UIProvider.AccountColumns.SettingsColumns.CONFIRM_DELETE)) {
            values[colPosMap.get(UIProvider.AccountColumns.SettingsColumns.CONFIRM_DELETE)] =
                    mailPrefs.getConfirmDelete() ? 1 : 0;
        }
        if (colPosMap.containsKey(UIProvider.AccountColumns.SettingsColumns.CONFIRM_ARCHIVE)) {
            values[colPosMap.get(
                    UIProvider.AccountColumns.SettingsColumns.CONFIRM_ARCHIVE)] = 0;
        }
        if (colPosMap.containsKey(UIProvider.AccountColumns.SettingsColumns.CONFIRM_SEND)) {
            values[colPosMap.get(UIProvider.AccountColumns.SettingsColumns.CONFIRM_SEND)] =
                    mailPrefs.getConfirmSend() ? 1 : 0;
        }
        if (colPosMap.containsKey(UIProvider.AccountColumns.SettingsColumns.DEFAULT_INBOX)) {
            values[colPosMap.get(UIProvider.AccountColumns.SettingsColumns.DEFAULT_INBOX)] =
                    combinedUriString("uifolder", combinedMailboxId(Mailbox.TYPE_INBOX));
        }
        if (colPosMap.containsKey(UIProvider.AccountColumns.SettingsColumns.MOVE_TO_INBOX)) {
            values[colPosMap.get(UIProvider.AccountColumns.SettingsColumns.MOVE_TO_INBOX)] =
                    combinedUriString("uifolder", combinedMailboxId(Mailbox.TYPE_INBOX));
        }
        if (colPosMap.containsKey(UIProvider.AccountColumns.SettingsColumns.SHOW_IMAGES)) {
            values[colPosMap.get(UIProvider.AccountColumns.SettingsColumns.SHOW_IMAGES)] =
                    Settings.ShowImages.ASK_FIRST;
        }
        //TS: junwei-xu  12/24/2014 EMAIL BUGFIX_878947 MOD_S
        if (colPosMap.containsKey(UIProvider.AccountColumns.SettingsColumns.SWIPE)) {
            values[colPosMap.get(UIProvider.AccountColumns.SettingsColumns.SWIPE)] =
                    mailPrefs.getConversationListSwipeActionInteger(false);
        }
        //TS: junwei-xu  12/24/2014 EMAIL BUGFIX_878947 MOD_E

        mc.addRow(values);
    }

    private static int getConversationListIcon(MailPrefs mailPrefs) {
        return mailPrefs.getShowSenderImages() ?
                UIProvider.ConversationListIcon.SENDER_IMAGE :
                UIProvider.ConversationListIcon.NONE;
    }

    private Cursor getVirtualMailboxCursor(long mailboxId, String[] projection) {
        MatrixCursor mc = new MatrixCursorWithCachedColumns(projection, 1);
        mc.addRow(getVirtualMailboxRow(getVirtualMailboxAccountId(mailboxId),
                getVirtualMailboxType(mailboxId), projection));
        return mc;
    }

    private Object[] getVirtualMailboxRow(long accountId, int mailboxType, String[] projection) {
        final long id = getVirtualMailboxId(accountId, mailboxType);
        final String idString = Long.toString(id);
        Object[] values = new Object[projection.length];
        // Not all column values are filled in here, as some are not applicable to virtual mailboxes
        // The remainder are left null
        for (int i = 0; i < projection.length; i++) {
            final String column = projection[i];
            if (column.equals(UIProvider.FolderColumns._ID)) {
                values[i] = id;
            } else if (column.equals(UIProvider.FolderColumns.URI)) {
                values[i] = combinedUriString("uifolder", idString);
            } else if (column.equals(UIProvider.FolderColumns.NAME)) {
                // default empty string since all of these should use resource strings
                values[i] = getFolderDisplayName(getFolderTypeFromMailboxType(mailboxType), "");
            } else if (column.equals(UIProvider.FolderColumns.HAS_CHILDREN)) {
                values[i] = 0;
            } else if (column.equals(UIProvider.FolderColumns.CAPABILITIES)) {
                values[i] = UIProvider.FolderCapabilities.DELETE
                        | UIProvider.FolderCapabilities.IS_VIRTUAL;
            } else if (column.equals(UIProvider.FolderColumns.CONVERSATION_LIST_URI)) {
                values[i] = combinedUriString("uimessages", idString);
                /// TCT: support local search.
            } else if (column.equals(UIProvider.FolderColumns.LOCAL_SEARCH_LIST_URI)) {
                values[i] = combinedUriString("uilocalsearch",
                        idString);
            } else if (column.equals(UIProvider.FolderColumns.UNREAD_COUNT)) {
                if (mailboxType == Mailbox.TYPE_INBOX && accountId == COMBINED_ACCOUNT_ID) {
                    final int unreadCount = EmailContent.count(getContext(), Message.CONTENT_URI,
                            MessageColumns.MAILBOX_KEY + " IN (SELECT " + MailboxColumns._ID
                            + " FROM " + Mailbox.TABLE_NAME + " WHERE " + MailboxColumns.TYPE
                            + "=" + Mailbox.TYPE_INBOX + ") AND " + MessageColumns.FLAG_READ + "=0",
                            null);
                    values[i] = unreadCount;
                } else if (mailboxType == Mailbox.TYPE_UNREAD) {
                    final String accountKeyClause;
                    final String[] whereArgs;
                    if (accountId == COMBINED_ACCOUNT_ID) {
                        accountKeyClause = "";
                        whereArgs = null;
                    } else {
                        accountKeyClause = MessageColumns.ACCOUNT_KEY + "= ? AND ";
                        whereArgs = new String[] { Long.toString(accountId) };
                    }
                    final int unreadCount = EmailContent.count(getContext(), Message.CONTENT_URI,
                            accountKeyClause + MessageColumns.FLAG_READ + "=0 AND "
                            + MessageColumns.MAILBOX_KEY + " NOT IN (SELECT " + MailboxColumns._ID
                            + " FROM " + Mailbox.TABLE_NAME + " WHERE " + MailboxColumns.TYPE + "="
                            + Mailbox.TYPE_TRASH + ")", whereArgs);
                    values[i] = unreadCount;
                } else if (mailboxType == Mailbox.TYPE_STARRED) {
                    final String accountKeyClause;
                    final String[] whereArgs;
                    if (accountId == COMBINED_ACCOUNT_ID) {
                        accountKeyClause = "";
                        whereArgs = null;
                    } else {
                        accountKeyClause = MessageColumns.ACCOUNT_KEY + "= ? AND ";
                        whereArgs = new String[] { Long.toString(accountId) };
                    }
                    //TS: zheng.zou 2015-06-26 EMAIL BUGFIX_1029647 MOD_S
                    //TS: zheng.zou 2015-03-06 EMAIL BUGFIX_940428 MOD_S
//                    final int starredCount = EmailContent.count(getContext(), Message.CONTENT_URI,
//                            accountKeyClause + MessageColumns.FLAG_FAVORITE + "=1"
//                            , whereArgs);//TS: junwei-xu 2015-2-27 EMAIL BUGFIX_930462 MOD
                    //TS: zheng.zou 2015-03-06 EMAIL BUGFIX_940428 MOD_E
                    //note: filter out already deleted messages
                    final String excludeTrashClause = " AND "  + MessageColumns.MAILBOX_KEY +
                            " NOT IN (SELECT " + MailboxColumns.ID + " FROM " + Mailbox.TABLE_NAME +
                            " WHERE " + MailboxColumns.TYPE + "=" + Mailbox.TYPE_TRASH + ")";
                    final int starredCount = EmailContent.count(getContext(), Message.CONTENT_URI,
                            accountKeyClause + MessageColumns.FLAG_FAVORITE + "=1" + excludeTrashClause, whereArgs);
                    //TS: zheng.zou 2015-06-26 EMAIL BUGFIX_1029647 MOD_E
                    values[i] = starredCount;
                    /// TCT: add for local search. @{
                } else {
                    LogUtils.w(TAG, "Not handle this kinds of VirtualMailbox types [%s]", mailboxType);
                    /// @}
                }
            } else if (column.equals(UIProvider.FolderColumns.ICON_RES_ID)) {
                    // TS: lin.zhou 2016-01-06 EMAIL BUGFIX-1271032 MOD_S
                if (mailboxType == Mailbox.TYPE_INBOX) {
                    values[i] = R.drawable.ic_drawer_folder_inbox;
                } else if (mailboxType == Mailbox.TYPE_UNREAD) {
                    values[i] = R.drawable.ic_drawer_folder_unread;
                } else if (mailboxType == Mailbox.TYPE_STARRED) {
                    values[i] = R.drawable.ic_drawer_folder_starred;
                    // TS: lin.zhou 2016-01-06 EMAIL BUGFIX-1271032 MOD_E
                    /// TCT: add for local search. @{
                } else {
                    LogUtils.w(TAG, "Not handle this kinds of VirtualMailbox types [%s]", mailboxType);
                    /// @}
                }
            //TS: junwei-xu 2015-09-02 EMAIL BUGFIX-546917 ADD-S
            // Add fold type to starred and unread mail box.
            } else if (column.equals(UIProvider.FolderColumns.TYPE)) {
                if (mailboxType == Mailbox.TYPE_STARRED) {
                    values[i] = UIProvider.FolderType.STARRED;
                } else if (mailboxType == Mailbox.TYPE_UNREAD) {
                    values[i] = UIProvider.FolderType.UNREAD;
                }
            }
            //TS: junwei-xu 2015-09-02 EMAIL BUGFIX-546917 ADD-E
        }
        return values;
    }

    private Cursor uiAccounts(String[] uiProjection, boolean suppressCombined) {
        final Context context = getContext();
        final SQLiteDatabase db = getDatabase(context);
        final Cursor accountIdCursor =
                db.rawQuery("select _id from " + Account.TABLE_NAME, new String[0]);
        final MatrixCursor mc;
        try {
            boolean combinedAccount = false;
            if (!suppressCombined && accountIdCursor.getCount() > 1) {
                combinedAccount = true;
            }
            final Bundle extras = new Bundle();
            // Email always returns the accurate number of accounts
            extras.putInt(AccountCursorExtraKeys.ACCOUNTS_LOADED, 1);
            mc = new MatrixCursorWithExtra(uiProjection, accountIdCursor.getCount(), extras);
            final Object[] values = new Object[uiProjection.length];
            while (accountIdCursor.moveToNext()) {
                final String id = accountIdCursor.getString(0);
                final Cursor accountCursor =
                        db.rawQuery(genQueryAccount(uiProjection, id), new String[] {id});
                try {
                    if (accountCursor.moveToNext()) {
                        for (int i = 0; i < uiProjection.length; i++) {
                            values[i] = accountCursor.getString(i);
                        }
                        mc.addRow(values);
                    }
                } finally {
                    accountCursor.close();
                }
            }
            if (combinedAccount) {
                addCombinedAccountRow(mc);
            }
        } catch (SQLiteException e){
            e.printStackTrace();;
            return null;
        } finally {
            accountIdCursor.close();
        }
        mc.setNotificationUri(context.getContentResolver(), UIPROVIDER_ALL_ACCOUNTS_NOTIFIER);

        return mc;
    }

    private Cursor uiQuickResponseAccount(String[] uiProjection, String account) {
        final Context context = getContext();
        final SQLiteDatabase db = getDatabase(context);
        final StringBuilder sb = genSelect(getQuickResponseMap(), uiProjection);
        sb.append(" FROM " + QuickResponse.TABLE_NAME);
        sb.append(" WHERE " + QuickResponse.ACCOUNT_KEY + "=?");
        final String query = sb.toString();
        return db.rawQuery(query, new String[] {account});
    }

    private Cursor uiQuickResponseId(String[] uiProjection, String id) {
        final Context context = getContext();
        final SQLiteDatabase db = getDatabase(context);
        final StringBuilder sb = genSelect(getQuickResponseMap(), uiProjection);
        sb.append(" FROM " + QuickResponse.TABLE_NAME);
        sb.append(" WHERE " + QuickResponse._ID + "=?");
        final String query = sb.toString();
        return db.rawQuery(query, new String[] {id});
    }

    private Cursor uiQuickResponse(String[] uiProjection) {
        final Context context = getContext();
        final SQLiteDatabase db = getDatabase(context);
        final StringBuilder sb = genSelect(getQuickResponseMap(), uiProjection);
        sb.append(" FROM " + QuickResponse.TABLE_NAME);
        final String query = sb.toString();
        return db.rawQuery(query, new String[0]);
    }

    /**
     * Generate the "attachment list" SQLite query, given a projection from UnifiedEmail
     *
     * @param uiProjection as passed from UnifiedEmail
     * @param contentTypeQueryParameters list of mimeTypes, used as a filter for the attachments
     * or null if there are no query parameters
     * @return the SQLite query to be executed on the EmailProvider database
     */
    private static String genQueryAttachments(String[] uiProjection,
            List<String> contentTypeQueryParameters) {
        // MAKE SURE THESE VALUES STAY IN SYNC WITH GEN QUERY ATTACHMENT
        ContentValues values = new ContentValues(1);
        values.put(UIProvider.AttachmentColumns.SUPPORTS_DOWNLOAD_AGAIN, 1);
        StringBuilder sb = genSelect(getAttachmentMap(), uiProjection, values);
        sb.append(" FROM ")
                .append(Attachment.TABLE_NAME)
                .append(" WHERE ")
                .append(AttachmentColumns.MESSAGE_KEY)
                .append(" =? ");

        // Filter for certain content types.
        // The filter works by adding LIKE operators for each
        // content type you wish to request. Content types
        // are filtered by performing a case-insensitive "starts with"
        // filter. IE, "image/" would return "image/png" as well as "image/jpeg".
        if (contentTypeQueryParameters != null && !contentTypeQueryParameters.isEmpty()) {
            final int size = contentTypeQueryParameters.size();
            sb.append("AND (");
            for (int i = 0; i < size; i++) {
                final String contentType = contentTypeQueryParameters.get(i);
                sb.append(AttachmentColumns.MIME_TYPE)
                        .append(" LIKE '")
                        .append(contentType)
                        .append("%'");

                if (i != size - 1) {
                    sb.append(" OR ");
                }
            }
            sb.append(")");
        }
        return sb.toString();
    }

    /**
     * Generate the "single attachment" SQLite query, given a projection from UnifiedEmail
     *
     * @param uiProjection as passed from UnifiedEmail
     * @return the SQLite query to be executed on the EmailProvider database
     */
    private String genQueryAttachment(String[] uiProjection) {
        // MAKE SURE THESE VALUES STAY IN SYNC WITH GEN QUERY ATTACHMENTS
        final ContentValues values = new ContentValues(2);
        values.put(AttachmentColumns.CONTENT_URI, createAttachmentUriColumnSQL());
        values.put(UIProvider.AttachmentColumns.SUPPORTS_DOWNLOAD_AGAIN, 1);

        return genSelect(getAttachmentMap(), uiProjection, values)
                .append(" FROM ").append(Attachment.TABLE_NAME)
                .append(" WHERE ")
                .append(AttachmentColumns._ID).append(" =? ")
                .toString();
    }

    /**
     * Generate the "single attachment by Content ID" SQLite query, given a projection from
     * UnifiedEmail
     *
     * @param uiProjection as passed from UnifiedEmail
     * @return the SQLite query to be executed on the EmailProvider database
     */
    private String genQueryAttachmentByMessageIDAndCid(String[] uiProjection) {
        final ContentValues values = new ContentValues(2);
        values.put(AttachmentColumns.CONTENT_URI, createAttachmentUriColumnSQL());
        values.put(UIProvider.AttachmentColumns.SUPPORTS_DOWNLOAD_AGAIN, 1);

        return genSelect(getAttachmentMap(), uiProjection, values)
                .append(" FROM ").append(Attachment.TABLE_NAME)
                .append(" WHERE ")
                .append(AttachmentColumns.MESSAGE_KEY).append(" =? ")
                .append(" AND ")
                .append(AttachmentColumns.CONTENT_ID).append(" =? ")
                .toString();
    }

    /**
     * @return a fragment of SQL that is the expression which, when evaluated for a particular
     *      Attachment row, produces the Content URI for the attachment
     */
    private static String createAttachmentUriColumnSQL() {
        final String uriPrefix = Attachment.ATTACHMENT_PROVIDER_URI_PREFIX;
        final String accountKey = AttachmentColumns.ACCOUNT_KEY;
        final String id = AttachmentColumns._ID;
        final String raw = AttachmentUtilities.FORMAT_RAW;
        final String contentUri = String.format("%s/' || %s || '/' || %s || '/%s", uriPrefix,
                accountKey, id, raw);

        return "@CASE " +
                "WHEN contentUri IS NULL THEN '" + contentUri + "' " +
                "WHEN contentUri IS NOT NULL THEN contentUri " +
                "END";
    }

    /**
     * Generate the "subfolder list" SQLite query, given a projection from UnifiedEmail
     *
     * @param uiProjection as passed from UnifiedEmail
     * @return the SQLite query to be executed on the EmailProvider database
     */
    private static String genQuerySubfolders(String[] uiProjection) {
        StringBuilder sb = genSelect(getFolderListMap(), uiProjection);
        sb.append(" FROM " + Mailbox.TABLE_NAME + " WHERE " + MailboxColumns.PARENT_KEY +
                " =? ORDER BY ");
        sb.append(MAILBOX_ORDER_BY);
        return sb.toString();
    }

    private static final String COMBINED_ACCOUNT_ID_STRING = Long.toString(COMBINED_ACCOUNT_ID);

    /**
     * Returns a cursor over all the folders for a specific URI which corresponds to a single
     * account.
     * @param uri uri to query
     * @param uiProjection projection
     * @return query result cursor
     */
    private Cursor uiFolders(final Uri uri, final String[] uiProjection) {
        final Context context = getContext();
        final SQLiteDatabase db = getDatabase(context);
        final String id = uri.getPathSegments().get(1);

        final Uri notifyUri =
                UIPROVIDER_FOLDERLIST_NOTIFIER.buildUpon().appendEncodedPath(id).build();

        final Cursor vc = uiVirtualMailboxes(id, uiProjection);
        vc.setNotificationUri(context.getContentResolver(), notifyUri);
        if (id.equals(COMBINED_ACCOUNT_ID_STRING)) {
            return vc;
        } else {
            Cursor c = db.rawQuery(genQueryAccountMailboxes(UIProvider.FOLDERS_PROJECTION),
                    new String[] {id});
            c = getFolderListCursor(c, Long.valueOf(id), uiProjection);
            c.setNotificationUri(context.getContentResolver(), notifyUri);
            if (c.getCount() > 0) {
                Cursor[] cursors = new Cursor[]{vc, c};
                return new MergeCursor(cursors);
            } else {
                return c;
            }
        }
    }

    private Cursor uiVirtualMailboxes(final String id, final String[] uiProjection) {
        final MatrixCursor mc = new MatrixCursorWithCachedColumns(uiProjection);

        if (id.equals(COMBINED_ACCOUNT_ID_STRING)) {
            mc.addRow(getVirtualMailboxRow(COMBINED_ACCOUNT_ID, Mailbox.TYPE_INBOX, uiProjection));
            mc.addRow(
                    getVirtualMailboxRow(COMBINED_ACCOUNT_ID, Mailbox.TYPE_STARRED, uiProjection));
            mc.addRow(getVirtualMailboxRow(COMBINED_ACCOUNT_ID, Mailbox.TYPE_UNREAD, uiProjection));
        } else {
            final long acctId = Long.parseLong(id);
            mc.addRow(getVirtualMailboxRow(acctId, Mailbox.TYPE_STARRED, uiProjection));
            mc.addRow(getVirtualMailboxRow(acctId, Mailbox.TYPE_UNREAD, uiProjection));
        }

        return mc;
    }

    /**
     * Returns an array of the default recent folders for a given URI which is unique for an
     * account. Some accounts might not have default recent folders, in which case an empty array
     * is returned.
     * @param id account id
     * @return array of URIs
     */
    private Uri[] defaultRecentFolders(final String id) {
        Uri[] recentFolders = new Uri[0];
        final SQLiteDatabase db = getDatabase(getContext());
        if (id.equals(COMBINED_ACCOUNT_ID_STRING)) {
            // We don't have default recents for the combined view.
            return recentFolders;
        }
        // We search for the types we want, and find corresponding IDs.
        final String[] idAndType = { BaseColumns._ID, UIProvider.FolderColumns.TYPE };

        // Sent, Drafts, and Starred are the default recents.
        final StringBuilder sb = genSelect(getFolderListMap(), idAndType);
        sb.append(" FROM ")
                .append(Mailbox.TABLE_NAME)
                .append(" WHERE ")
                .append(MailboxColumns.ACCOUNT_KEY)
                .append(" = ")
                .append(id)
                .append(" AND ")
                .append(MailboxColumns.TYPE)
                .append(" IN (")
                .append(Mailbox.TYPE_SENT)
                .append(", ")
                .append(Mailbox.TYPE_DRAFTS)
                .append(", ")
                .append(Mailbox.TYPE_STARRED)
                .append(")");
        LogUtils.d(TAG, "defaultRecentFolders: Query is %s", sb);
        final Cursor c = db.rawQuery(sb.toString(), null);
        try {
            if (c == null || c.getCount() <= 0 || !c.moveToFirst()) {
                return recentFolders;
            }
            // Read all the IDs of the mailboxes, and turn them into URIs.
            recentFolders = new Uri[c.getCount()];
            int i = 0;
            do {
                final long folderId = c.getLong(0);
                recentFolders[i] = uiUri("uifolder", folderId);
                LogUtils.d(TAG, "Default recent folder: %d, with uri %s", folderId,
                        recentFolders[i]);
                ++i;
            } while (c.moveToNext());
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return recentFolders;
    }

    /**
     * Convenience method to create a {@link Folder}
     * @param context to get a {@link ContentResolver}
     * @param mailboxId id of the {@link Mailbox} that we want
     * @return the {@link Folder} or null
     */
    public static Folder getFolder(Context context, long mailboxId) {
        final ContentResolver resolver = context.getContentResolver();
        final Cursor fc = resolver.query(EmailProvider.uiUri("uifolder", mailboxId),
                UIProvider.FOLDERS_PROJECTION, null, null, null);

        if (fc == null) {
            LogUtils.e(TAG, "Null folder cursor for mailboxId %d", mailboxId);
            return null;
        }

        Folder uiFolder = null;
        try {
            if (fc.moveToFirst()) {
                uiFolder = new Folder(fc);
            }
        } finally {
            fc.close();
        }
        return uiFolder;
    }

    static class AttachmentsCursor extends CursorWrapper {
        private final int mContentUriIndex;
        private final int mUriIndex;
        private final Context mContext;
        private final String[] mContentUriStrings;

        public AttachmentsCursor(Context context, Cursor cursor) {
            super(cursor);
            mContentUriIndex = cursor.getColumnIndex(UIProvider.AttachmentColumns.CONTENT_URI);
            mUriIndex = cursor.getColumnIndex(UIProvider.AttachmentColumns.URI);
            mContext = context;
            mContentUriStrings = new String[cursor.getCount()];
            if (mContentUriIndex == -1) {
                // Nothing to do here, move along
                return;
            }
            while (cursor.moveToNext()) {
                final int index = cursor.getPosition();
                final Uri uri = Uri.parse(getString(mUriIndex));
                final long id = Long.parseLong(uri.getLastPathSegment());
                final Attachment att = Attachment.restoreAttachmentWithId(mContext, id);

                if (att == null) {
                    mContentUriStrings[index] = "";
                    continue;
                }

                if (!TextUtils.isEmpty(att.getCachedFileUri())) {
                    mContentUriStrings[index] = att.getCachedFileUri();
                    continue;
                }

                final String contentUri;
                // Until the package installer can handle opening apks from a content:// uri, for
                // any apk that was successfully saved in external storage, return the
                // content uri from the attachment
                if (att.mUiDestination == UIProvider.AttachmentDestination.EXTERNAL &&
                        att.mUiState == UIProvider.AttachmentState.SAVED &&
                        TextUtils.equals(att.mMimeType, MimeType.ANDROID_ARCHIVE)) {
                    contentUri = att.getContentUri();
                } else {
                    final String attUriString = att.getContentUri();
                    final String authority;
                    if (!TextUtils.isEmpty(attUriString)) {
                        authority = Uri.parse(attUriString).getAuthority();
                    } else {
                        authority = null;
                    }
                    if (TextUtils.equals(authority, Attachment.ATTACHMENT_PROVIDER_AUTHORITY)) {
                        contentUri = attUriString;
                    } else {
                        contentUri = AttachmentUtilities.getAttachmentUri(att.mAccountKey, id)
                                .toString();
                    }
                }
                mContentUriStrings[index] = contentUri;

            }
            cursor.moveToPosition(-1);
        }

        @Override
        public String getString(int column) {
            if (column == mContentUriIndex) {
                return mContentUriStrings[getPosition()];
            } else {
                return super.getString(column);
            }
        }
    }

    /**
     * For debugging purposes; shouldn't be used in production code
     */
    @SuppressWarnings("unused")
    static class CloseDetectingCursor extends CursorWrapper {

        public CloseDetectingCursor(Cursor cursor) {
            super(cursor);
        }

        @Override
        public void close() {
            super.close();
            LogUtils.d(TAG, "Closing cursor", new Error());
        }
    }

    /**
     * Converts a mailbox in a row of the mailboxCursor into a row
     * in the supplied {@link MatrixCursor} in the format required for {@link Folder}.
     * As a convenience, the modified {@link MatrixCursor} is also returned.
     * @param mc the {@link MatrixCursor} into which the mailbox data will be converted
     * @param projectionLength the length of the projection for this Cursor
     * @param mailboxCursor the cursor supplying the mailbox data
     * @param nameColumn column in the cursor containing the folder name value
     * @param typeColumn column in the cursor containing the folder type value
     * @return the {@link MatrixCursor} containing the transformed data.
     */
    private Cursor getUiFolderCursorRowFromMailboxCursorRow(
            MatrixCursor mc, int projectionLength, Cursor mailboxCursor,
            int nameColumn, int typeColumn) {
        final MatrixCursor.RowBuilder builder = mc.newRow();
        for (int i = 0; i < projectionLength; i++) {
            // If we are at the name column, get the type
            // and use it to use a properly translated string
            // from resources instead of the display name.
            // This ignores display names for system mailboxes.
            if (nameColumn == i) {
                // We implicitly assume that if name is requested,
                // type has also been requested. If not, this will
                // error in unknown ways.
                final int type = mailboxCursor.getInt(typeColumn);
                builder.add(getFolderDisplayName(type, mailboxCursor.getString(i)));
            } else {
                builder.add(mailboxCursor.getString(i));
            }
        }
        return mc;
    }

    /**
     * Takes a uifolder cursor (that was generated with a full projection) and remaps values for
     * columns that are difficult to generate in the SQL query. This currently includes:
     * - Folder name (due to system folder localization).
     * - Capabilities (due to this varying by account protocol).
     * - Persistent id (due to needing to base64 encode it).
     * - Load more uri (due to this varying by account protocol).
     * TODO: This would be better as a CursorWrapper, rather than doing a copy.
     * @param inputCursor A cursor containing all columns of {@link UIProvider.FolderColumns}.
     *                    Strictly speaking doesn't need all, but simpler if we assume that.
     * @param outputCursor A MatrixCursor which this function will populate.
     * @param accountId The account id for the mailboxes in this query.
     * @param uiProjection The projection specified by the query.
     */
    private void remapFolderCursor(final Cursor inputCursor, final MatrixCursor outputCursor,
            final long accountId, final String[] uiProjection) {
        // Return early if our input cursor is empty.
        if (inputCursor == null || inputCursor.getCount() == 0) {
            return;
        }
        // Get the column indices for the columns we need during remapping.
        // While we currently could assume the column indices for UIProvider.FOLDERS_PROJECTION
        // and therefore avoid the calls to getColumnIndex, this at least tries to future-proof a
        // bit.
        // Note that id and type MUST be present for this function to work correctly.
        final int idColumn = inputCursor.getColumnIndex(BaseColumns._ID);
        final int typeColumn = inputCursor.getColumnIndex(UIProvider.FolderColumns.TYPE);
        final int nameColumn = inputCursor.getColumnIndex(UIProvider.FolderColumns.NAME);
        final int capabilitiesColumn =
                inputCursor.getColumnIndex(UIProvider.FolderColumns.CAPABILITIES);
        final int persistentIdColumn =
                inputCursor.getColumnIndex(UIProvider.FolderColumns.PERSISTENT_ID);
        final int loadMoreUriColumn =
                inputCursor.getColumnIndex(UIProvider.FolderColumns.LOAD_MORE_URI);

        // Get the EmailServiceInfo for the current account.
        final Context context = getContext();
        final String protocol = Account.getProtocol(context, accountId);
        final EmailServiceInfo info = EmailServiceUtils.getServiceInfo(context, protocol);

        // Build the return cursor. We iterate over all rows of the input cursor and construct
        // a row in the output using the columns in uiProjection.
        while (inputCursor.moveToNext()) {
            final MatrixCursor.RowBuilder builder = outputCursor.newRow();
            final int folderType = inputCursor.getInt(typeColumn);
            for (int i = 0; i < uiProjection.length; i++) {
                // Find the index in the input cursor corresponding the column requested in the
                // output projection.
                final int index = inputCursor.getColumnIndex(uiProjection[i]);
                if (index == -1) {
                    // We don't have this value, so put a blank in the output and move on.
                    builder.add(null);
                    continue;
                }
                final String value = inputCursor.getString(index);
                // remapped indicates whether we've written a value to the output for this column.
                final boolean remapped;
                if (nameColumn == index) {
                    // Remap folder name for system folders.
                    builder.add(getFolderDisplayName(folderType, value));
                    remapped = true;
                } else if (capabilitiesColumn == index) {
                    // Get the correct capabilities for this folder.
                    final long mailboxID = inputCursor.getLong(idColumn);
                    final int mailboxType = getMailboxTypeFromFolderType(folderType);
                    builder.add(getFolderCapabilities(info, mailboxType, mailboxID));
                    remapped = true;
                } else if (persistentIdColumn == index) {
                    // Hash the persistent id.
                    builder.add(Base64.encodeToString(value.getBytes(),
                            Base64.URL_SAFE | Base64.NO_WRAP | Base64.NO_PADDING));
                    remapped = true;
                } else if (loadMoreUriColumn == index && folderType != Mailbox.TYPE_SEARCH &&
                        (info == null || !info.offerLoadMore)) {
                    // Blank the load more uri for account types that don't offer it.
                    // Note that all account types permit load more for search results.
                    builder.add(null);
                    remapped = true;
                } else {
                    remapped = false;
                }
                // If the above logic didn't write some other value to the output, use the value
                // from the input cursor.
                if (!remapped) {
                    builder.add(value);
                }
            }
        }
    }

    private Cursor getFolderListCursor(final Cursor inputCursor, final long accountId,
            final String[] uiProjection) {
        final MatrixCursor mc = new MatrixCursorWithCachedColumns(uiProjection);
        if (inputCursor != null) {
            try {
                remapFolderCursor(inputCursor, mc, accountId, uiProjection);
            } finally {
                inputCursor.close();
            }
        }
        return mc;
    }

    /**
     * Returns a {@link String} from Resources corresponding
     * to the {@link UIProvider.FolderType} requested.
     * @param folderType {@link UIProvider.FolderType} value for the folder
     * @param defaultName a {@link String} to use in case the {@link UIProvider.FolderType}
     *                    provided is not a system folder.
     * @return a {@link String} to use as the display name for the folder
     */
    private String getFolderDisplayName(int folderType, String defaultName) {
        final int resId;
        switch (folderType) {
            case UIProvider.FolderType.INBOX:
                resId = R.string.mailbox_name_display_inbox;
                break;
            case UIProvider.FolderType.OUTBOX:
                resId = R.string.mailbox_name_display_outbox;
                break;
            case UIProvider.FolderType.DRAFT:
                resId = R.string.mailbox_name_display_drafts;
                break;
            case UIProvider.FolderType.TRASH:
                resId = R.string.mailbox_name_display_trash;
                break;
            case UIProvider.FolderType.SENT:
                resId = R.string.mailbox_name_display_sent;
                break;
            case UIProvider.FolderType.SPAM:
                resId = R.string.mailbox_name_display_junk;
                break;
            case UIProvider.FolderType.STARRED:
                resId = R.string.mailbox_name_display_starred;
                break;
            case UIProvider.FolderType.UNREAD:
                resId = R.string.mailbox_name_display_unread;
                break;
            default:
                return defaultName;
        }
        return getContext().getString(resId);
    }

    /**
     * Converts a {@link Mailbox} type value to its {@link UIProvider.FolderType}
     * equivalent.
     * @param mailboxType a {@link Mailbox} type
     * @return a {@link UIProvider.FolderType} value
     */
    private static int getFolderTypeFromMailboxType(int mailboxType) {
        switch (mailboxType) {
            case Mailbox.TYPE_INBOX:
                return UIProvider.FolderType.INBOX;
            case Mailbox.TYPE_OUTBOX:
                return UIProvider.FolderType.OUTBOX;
            case Mailbox.TYPE_DRAFTS:
                return UIProvider.FolderType.DRAFT;
            case Mailbox.TYPE_TRASH:
                return UIProvider.FolderType.TRASH;
            case Mailbox.TYPE_SENT:
                return UIProvider.FolderType.SENT;
            case Mailbox.TYPE_JUNK:
                return UIProvider.FolderType.SPAM;
            case Mailbox.TYPE_STARRED:
                return UIProvider.FolderType.STARRED;
            case Mailbox.TYPE_UNREAD:
                return UIProvider.FolderType.UNREAD;
            case Mailbox.TYPE_SEARCH:
                // TODO Can the DEFAULT type be removed from SEARCH folders?
                return UIProvider.FolderType.DEFAULT | UIProvider.FolderType.SEARCH;
            default:
                return UIProvider.FolderType.DEFAULT;
        }
    }

    /**
     * Converts a {@link UIProvider.FolderType} type value to its {@link Mailbox} equivalent.
     * @param folderType a {@link UIProvider.FolderType} type
     * @return a {@link Mailbox} value
     */
    private static int getMailboxTypeFromFolderType(int folderType) {
        switch (folderType) {
            case UIProvider.FolderType.DEFAULT:
                return Mailbox.TYPE_MAIL;
            case UIProvider.FolderType.INBOX:
                return Mailbox.TYPE_INBOX;
            case UIProvider.FolderType.OUTBOX:
                return Mailbox.TYPE_OUTBOX;
            case UIProvider.FolderType.DRAFT:
                return Mailbox.TYPE_DRAFTS;
            case UIProvider.FolderType.TRASH:
                return Mailbox.TYPE_TRASH;
            case UIProvider.FolderType.SENT:
                return Mailbox.TYPE_SENT;
            case UIProvider.FolderType.SPAM:
                return Mailbox.TYPE_JUNK;
            case UIProvider.FolderType.STARRED:
                return Mailbox.TYPE_STARRED;
            case UIProvider.FolderType.UNREAD:
                return Mailbox.TYPE_UNREAD;
            case UIProvider.FolderType.DEFAULT | UIProvider.FolderType.SEARCH:
                // TODO Can the DEFAULT type be removed from SEARCH folders?
                return Mailbox.TYPE_SEARCH;
            default:
                throw new IllegalArgumentException("Unable to map folder type: " + folderType);
        }
    }

    /**
     * We need a reasonably full projection for getFolderListCursor to work, but don't always want
     * to do the subquery needed for FolderColumns.UNREAD_SENDERS
     * @param uiProjection The projection we actually want
     * @return Full projection, possibly with or without FolderColumns.UNREAD_SENDERS
     */
    private String[] folderProjectionFromUiProjection(final String[] uiProjection) {
        final Set<String> columns = ImmutableSet.copyOf(uiProjection);
        if (columns.contains(UIProvider.FolderColumns.UNREAD_SENDERS)) {
            return UIProvider.FOLDERS_PROJECTION_WITH_UNREAD_SENDERS;
        } else {
            return UIProvider.FOLDERS_PROJECTION;
        }
    }

    /**
     * Handle UnifiedEmail queries here (dispatched from query())
     *
     * @param match the UriMatcher match for the original uri passed in from UnifiedEmail
     * @param uri the original uri passed in from UnifiedEmail
     * @param uiProjection the projection passed in from UnifiedEmail
     * @param unseenOnly <code>true</code> to only return unseen messages (where supported)
     * @return the result Cursor
     */
    private Cursor uiQuery(int match, Uri uri, String[] uiProjection, final boolean unseenOnly) {
        Context context = getContext();
        ContentResolver resolver = context.getContentResolver();
        SQLiteDatabase db = getDatabase(context);
        // Should we ever return null, or throw an exception??
        Cursor c = null;
        String id = uri.getPathSegments().get(1);
        Uri notifyUri = null;
        switch(match) {
            case UI_ALL_FOLDERS:
                notifyUri =
                        UIPROVIDER_FOLDERLIST_NOTIFIER.buildUpon().appendEncodedPath(id).build();
                final Cursor vc = uiVirtualMailboxes(id, uiProjection);
                if (id.equals(COMBINED_ACCOUNT_ID_STRING)) {
                    // There's no real mailboxes, so just return the virtual ones
                    c = vc;
                } else {
                    // Return real and virtual mailboxes alike
                    final Cursor rawc = db.rawQuery(genQueryAccountAllMailboxes(uiProjection),
                            new String[] {id});
                    rawc.setNotificationUri(context.getContentResolver(), notifyUri);
                    vc.setNotificationUri(context.getContentResolver(), notifyUri);
                    if (rawc.getCount() > 0) {
                        c = new MergeCursor(new Cursor[]{rawc, vc});
                    } else {
                        c = rawc;
                    }
                }
                break;
            case UI_FULL_FOLDERS: {
                // We need a full projection for getFolderListCursor
                final String[] folderProjection = folderProjectionFromUiProjection(uiProjection);
                c = db.rawQuery(genQueryAccountAllMailboxes(folderProjection), new String[] {id});
                c = getFolderListCursor(c, Long.valueOf(id), uiProjection);
                notifyUri =
                        UIPROVIDER_FOLDERLIST_NOTIFIER.buildUpon().appendEncodedPath(id).build();
                break;
            }
            case UI_RECENT_FOLDERS:
                c = db.rawQuery(genQueryRecentMailboxes(uiProjection), new String[] {id});
                notifyUri = UIPROVIDER_RECENT_FOLDERS_NOTIFIER.buildUpon().appendPath(id).build();
                break;
            case UI_SUBFOLDERS: {
                // We need a full projection for getFolderListCursor
                final String[] folderProjection = folderProjectionFromUiProjection(uiProjection);
                c = db.rawQuery(genQuerySubfolders(folderProjection), new String[] {id});
                c = getFolderListCursor(c, Mailbox.getAccountIdForMailbox(context, id),
                        uiProjection);
                // Get notifications for any folder changes on this account. This is broader than
                // we need but otherwise we'd need for every folder change to notify on all relevant
                // subtrees. For now we opt for simplicity.
                final long accountId = Mailbox.getAccountIdForMailbox(context, id);
                notifyUri = ContentUris.withAppendedId(UIPROVIDER_FOLDERLIST_NOTIFIER, accountId);
                break;
            }
            case UI_MESSAGES:
                long mailboxId = Long.parseLong(id);
                final Folder folder = getFolder(context, mailboxId);
                if (folder == null) {
                    // This mailboxId is bogus. Return an empty cursor
                    // TODO: Make callers of this query handle null cursors instead b/10819309
                    return new MatrixCursor(uiProjection);
                }
                //TS: junwei-xu 2015-09-02 EMAIL BUGFIX-546917 ADD-S
                String queryFavorite = uri.getQueryParameter(UIProvider.FLAG_FAVORITE_QUERY_PARAMETER);
                String selection = null;
                boolean addSelection = Boolean.TRUE.toString().equals(queryFavorite);
                if (addSelection) {
                    selection = MessageColumns.FLAG_FAVORITE + "=1 ";
                }
                //TS: zheng.zou 2016-1-14 EMAIL TASK_1431225 MOD_S
                String orderType = uri.getQueryParameter(UIProvider.ORDER_QUERY_PARAMETER);
                String oderSql = SortHelper.getSortOrderSqlByTypeStr(orderType);
                //TS: junwei-xu 2015-09-02 EMAIL BUGFIX-546917 ADD-E
                if (isVirtualMailbox(mailboxId)) {
                    c = getVirtualMailboxMessagesCursor(db, uiProjection, mailboxId, unseenOnly, selection,oderSql);
                } else {
                    c = db.rawQuery(
                            genQueryMailboxMessages(uiProjection, unseenOnly, selection,oderSql), new String[]{id});
                }
                //TS: zheng.zou 2016-1-14 EMAIL TASK_1431225 MOD_E
                notifyUri = UIPROVIDER_CONVERSATION_NOTIFIER.buildUpon().appendPath(id).build();
                c = new EmailConversationCursor(context, c, folder, mailboxId);
                break;
            case UI_MESSAGE:
                MessageQuery qq = genQueryViewMessage(uiProjection, id);
                String sql = qq.query;
                String attJson = qq.attachmentJson;
                // With attachments, we have another argument to bind
                if (attJson != null) {
                    c = db.rawQuery(sql, new String[] {attJson, id});
                } else {
                    c = db.rawQuery(sql, new String[] {id});
                }
                if (c != null) {
                    c = new EmailMessageCursor(getContext(), c, UIProvider.MessageColumns.BODY_HTML,
                            UIProvider.MessageColumns.BODY_TEXT);
                }
                notifyUri = UIPROVIDER_MESSAGE_NOTIFIER.buildUpon().appendPath(id).build();
                break;
            case UI_ATTACHMENTS:
                final List<String> contentTypeQueryParameters =
                        uri.getQueryParameters(PhotoContract.ContentTypeParameters.CONTENT_TYPE);
                c = db.rawQuery(genQueryAttachments(uiProjection, contentTypeQueryParameters),
                        new String[] {id});
                c = new AttachmentsCursor(context, c);
                notifyUri = UIPROVIDER_ATTACHMENTS_NOTIFIER.buildUpon().appendPath(id).build();
                break;
            case UI_ATTACHMENT:
                c = db.rawQuery(genQueryAttachment(uiProjection), new String[] {id});
                notifyUri = UIPROVIDER_ATTACHMENT_NOTIFIER.buildUpon().appendPath(id).build();
                break;
            case UI_ATTACHMENT_BY_CID:
                final String cid = uri.getPathSegments().get(2);
                final String[] selectionArgs = {id, cid};
                c = db.rawQuery(genQueryAttachmentByMessageIDAndCid(uiProjection), selectionArgs);

                // we don't have easy access to the attachment ID (which is buried in the cursor
                // being returned), so we notify on the parent message object
                notifyUri = UIPROVIDER_ATTACHMENTS_NOTIFIER.buildUpon().appendPath(id).build();
                break;
            case UI_FOLDER:
            case UI_INBOX:
                if (match == UI_INBOX) {
                    mailboxId = Mailbox.findMailboxOfType(context, Long.parseLong(id),
                            Mailbox.TYPE_INBOX);
                    if (mailboxId == Mailbox.NO_MAILBOX) {
                        LogUtils.d(LogUtils.TAG, "No inbox found for account %s", id);
                        return null;
                    }
                    LogUtils.d(LogUtils.TAG, "Found inbox id %d", mailboxId);
                } else {
                    mailboxId = Long.parseLong(id);
                }
                final String mailboxIdString = Long.toString(mailboxId);
                if (isVirtualMailbox(mailboxId)) {
                    c = getVirtualMailboxCursor(mailboxId, uiProjection);
                    notifyUri = UIPROVIDER_FOLDER_NOTIFIER.buildUpon().appendPath(mailboxIdString)
                            .build();
                } else {
                    //TS: yanhua.chen 2015-8-18 EMAIL BUGFIX_1066136 MOD_S
                    try{
                        c = db.rawQuery(genQueryMailbox(uiProjection, mailboxIdString),
                                new String[]{mailboxIdString});
                    }catch(SQLiteCantOpenDatabaseException e){
                        LogUtils.e(LogUtils.TAG,"Occur SQLiteCantOpenDatabaseException");
                        return null;
                    }
                    //TS: yanhua.chen 2015-8-18 EMAIL BUGFIX_1066136 MOD_E
                    final List<String> projectionList = Arrays.asList(uiProjection);
                    final int nameColumn = projectionList.indexOf(UIProvider.FolderColumns.NAME);
                    final int typeColumn = projectionList.indexOf(UIProvider.FolderColumns.TYPE);
                    if (c.moveToFirst()) {
                        final Cursor closeThis = c;
                        try {
                            c = getUiFolderCursorRowFromMailboxCursorRow(
                                    new MatrixCursorWithCachedColumns(uiProjection),
                                    uiProjection.length, c, nameColumn, typeColumn);
                        } finally {
                            closeThis.close();
                        }
                    }
                    notifyUri = UIPROVIDER_FOLDER_NOTIFIER.buildUpon().appendPath(mailboxIdString)
                            .build();
                }
                break;
            case UI_ACCOUNT:
                if (id.equals(COMBINED_ACCOUNT_ID_STRING)) {
                    MatrixCursor mc = new MatrixCursorWithCachedColumns(uiProjection, 1);
                    addCombinedAccountRow(mc);
                    c = mc;
                } else {
                    c = db.rawQuery(genQueryAccount(uiProjection, id), new String[] {id});
                }
                notifyUri = UIPROVIDER_ACCOUNT_NOTIFIER.buildUpon().appendPath(id).build();
                break;
            case UI_CONVERSATION:
                c = db.rawQuery(genQueryConversation(uiProjection), new String[] {id});
                break;
        }
        if (notifyUri != null) {
            c.setNotificationUri(resolver, notifyUri);
        }
        return c;
    }

    /**
     * Convert a UIProvider attachment to an EmailProvider attachment (for sending); we only need
     * a few of the fields
     * @param uiAtt the UIProvider attachment to convert
     * @param cachedFile the path to the cached file to
     * @return the EmailProvider attachment
     */
    // TODO(pwestbro): once the Attachment contains the cached uri, the second parameter can be
    // removed
    // TODO(mhibdon): if the UI Attachment contained the account key, the third parameter could
    // be removed.
    private static Attachment convertUiAttachmentToAttachment(
            com.tct.mail.providers.Attachment uiAtt, String cachedFile, long accountKey) {
        final Attachment att = new Attachment();

        att.setContentUri(uiAtt.contentUri.toString());

        if (!TextUtils.isEmpty(cachedFile)) {
            // Generate the content provider uri for this cached file
            //TS: zhaotianyong 2014-12-20 EMAIL BUGFIX_875367 MOD_S
            //TS: junwei-xu 2016-04-01 EMAIL BUGFIX-1886442 MOD_S
            //Note: sometimes attachment name has special character, must encode it before parse to uri.
            final Uri.Builder cachedFileBuilder = Uri.parse("content://" + EmailContent.AUTHORITY
                    + "/attachment/cachedFile/" + Uri.encode(uiAtt.getName())).buildUpon();
            //TS: junwei-xu 2016-04-01 EMAIL BUGFIX-1886442 MOD_E
            //TS: zhaotianyong 2014-12-20 EMAIL BUGFIX_875367 MOD_E
            cachedFileBuilder.appendQueryParameter(Attachment.CACHED_FILE_QUERY_PARAM, cachedFile);
            att.setCachedFileUri(cachedFileBuilder.build().toString());
        }
        att.mAccountKey = accountKey;
        att.mFileName = uiAtt.getName();
        att.mMimeType = uiAtt.getContentType();
        att.mSize = uiAtt.size;
        //TS: zhonghua.tuo 2015-3-3 EMAIL BUGFIX_936728 ADD_S
        att.mRealUri = uiAtt.realUri==null?null:uiAtt.realUri.toString();
        //TS: zhonghua.tuo 2015-3-3 EMAIL BUGFIX_936728 ADD_E
        //TS: zhaotianyong 2015-03-13 EMAIL BUGFIX_945771 ADD_S
        att.mUiState = uiAtt.getState();
        att.mUiDestination = uiAtt.getDestination();
        //TS: zhaotianyong 2015-03-13 EMAIL BUGFIX_945771 ADD_E
        return att;
    }

    /**
     * Create a mailbox given the account and mailboxType.
     */
    private Mailbox createMailbox(long accountId, int mailboxType) {
        Context context = getContext();
        Mailbox box = Mailbox.newSystemMailbox(context, accountId, mailboxType);
        // Make sure drafts and save will show up in recents...
        // If these already exist (from old Email app), they will have touch times
        switch (mailboxType) {
            case Mailbox.TYPE_DRAFTS:
                box.mLastTouchedTime = Mailbox.DRAFTS_DEFAULT_TOUCH_TIME;
                break;
            case Mailbox.TYPE_SENT:
                box.mLastTouchedTime = Mailbox.SENT_DEFAULT_TOUCH_TIME;
                break;
        }
        box.save(context);
        return box;
    }

    /**
     * Given an account name and a mailbox type, return that mailbox, creating it if necessary
     * @param accountId the account id to use
     * @param mailboxType the type of mailbox we're trying to find
     * @return the mailbox of the given type for the account in the uri, or null if not found
     */
    private Mailbox getMailboxByAccountIdAndType(final long accountId, final int mailboxType) {
        Mailbox mailbox = Mailbox.restoreMailboxOfType(getContext(), accountId, mailboxType);
        if (mailbox == null) {
            mailbox = createMailbox(accountId, mailboxType);
        }
        return mailbox;
    }

    /**
     * Given a mailbox and the content values for a message, create/save the message in the mailbox
     * @param mailbox the mailbox to use
     * @param extras the bundle containing the message fields
     * @return the uri of the newly created message
     * TODO(yph): The following fields are available in extras but unused, verify whether they
     *     should be respected:
     *     - UIProvider.MessageColumns.SNIPPET
     *     - UIProvider.MessageColumns.REPLY_TO
     *     - UIProvider.MessageColumns.FROM
     */
    private Uri uiSaveMessage(Message msg, Mailbox mailbox, Bundle extras) {
        final Context context = getContext();
        // Fill in the message
        final Account account = Account.restoreAccountWithId(context, mailbox.mAccountKey);
        if (account == null) {
            Log.d(TAG, "uiSaveMessage() -> account is null, and message will missing."); // AM: Kexue.Geng 2015-04-30 EMAIL BUGFIX_983211 MOD
            return null;
        }
        final String customFromAddress =
                extras.getString(UIProvider.MessageColumns.CUSTOM_FROM_ADDRESS);
        if (!TextUtils.isEmpty(customFromAddress)) {
            msg.mFrom = customFromAddress;
        } else {
            msg.mFrom = account.getEmailAddress();
        }
        msg.mTimeStamp = System.currentTimeMillis();
        msg.mTo = extras.getString(UIProvider.MessageColumns.TO);
        msg.mCc = extras.getString(UIProvider.MessageColumns.CC);
        msg.mBcc = extras.getString(UIProvider.MessageColumns.BCC);
        // TS: tao.gan 2015-12-25 EMAIL FEATURE-1239148 ADD_S
        msg.mReplyTo = extras.getString(UIProvider.MessageColumns.REPLY_TO);
        // TS: tao.gan 2015-12-25 EMAIL FEATURE-1239148 ADD_E
        msg.mSubject = extras.getString(UIProvider.MessageColumns.SUBJECT);
        msg.mText = extras.getString(UIProvider.MessageColumns.BODY_TEXT);
        msg.mHtml = extras.getString(UIProvider.MessageColumns.BODY_HTML);
        msg.mMailboxKey = mailbox.mId;
        msg.mAccountKey = mailbox.mAccountKey;
        msg.mDisplayName = msg.mFrom;     //TS: zheng.zou 2016-1-14 EMAIL TASK_1431225 MOD
        msg.mFlagLoaded = Message.FLAG_LOADED_COMPLETE;
        msg.mFlagRead = true;
        msg.mFlagSeen = true;
        msg.mQuotedTextStartPos = extras.getInt(UIProvider.MessageColumns.QUOTE_START_POS, 0);
        msg.mPriority=extras.getInt(UIProvider.MessageColumns.MESSAGE_FLAG_PRIORITY_COLUMN);//[FEATURE]-Add-BEGIN by TCTNj.fu.zhang,04/08/2014,622697
        int flags = 0;
        final int draftType = extras.getInt(UIProvider.MessageColumns.DRAFT_TYPE);
        switch(draftType) {
            case DraftType.FORWARD:
                flags |= Message.FLAG_TYPE_FORWARD;
                break;
            case DraftType.REPLY_ALL:
                flags |= Message.FLAG_TYPE_REPLY_ALL;
                //$FALL-THROUGH$
            case DraftType.REPLY:
                flags |= Message.FLAG_TYPE_REPLY;
                break;
            case DraftType.COMPOSE:
                flags |= Message.FLAG_TYPE_ORIGINAL;
                break;
        }
        int draftInfo = 0;
        if (extras.containsKey(UIProvider.MessageColumns.QUOTE_START_POS)) {
            draftInfo = extras.getInt(UIProvider.MessageColumns.QUOTE_START_POS);
            if (extras.getInt(UIProvider.MessageColumns.APPEND_REF_MESSAGE_CONTENT) != 0) {
                draftInfo |= Message.DRAFT_INFO_APPEND_REF_MESSAGE;
            }
        }
        if (!extras.containsKey(UIProvider.MessageColumns.APPEND_REF_MESSAGE_CONTENT)) {
            flags |= Message.FLAG_NOT_INCLUDE_QUOTED_TEXT;
        }
        msg.mDraftInfo = draftInfo;
        msg.mFlags = flags;

        final String ref = extras.getString(UIProvider.MessageColumns.REF_MESSAGE_ID);
        if (ref != null && msg.mQuotedTextStartPos >= 0) {
            String refId = Uri.parse(ref).getLastPathSegment();
            try {
                msg.mSourceKey = Long.parseLong(refId);
            } catch (NumberFormatException e) {
                // This will be zero; the default
            }
        }

        // Get attachments from the ContentValues
        final List<com.tct.mail.providers.Attachment> uiAtts =
                com.tct.mail.providers.Attachment.fromJSONArray(
                        extras.getString(UIProvider.MessageColumns.ATTACHMENTS));
        final ArrayList<Attachment> atts = new ArrayList<Attachment>();
        boolean hasUnloadedAttachments = false;
        Bundle attachmentFds =
                extras.getParcelable(UIProvider.SendOrSaveMethodParamKeys.OPENED_FD_MAP);
        for (com.tct.mail.providers.Attachment uiAtt: uiAtts) {
            final Uri attUri = uiAtt.uri;
            if (attUri != null && attUri.getAuthority().equals(EmailContent.AUTHORITY)) {
                // If it's one of ours, retrieve the attachment and add it to the list
                final long attId = Long.parseLong(attUri.getLastPathSegment());
                final Attachment att = Attachment.restoreAttachmentWithId(context, attId);
                if (att != null) {
                    // We must clone the attachment into a new one for this message; easiest to
                    // use a parcel here
                    final Parcel p = Parcel.obtain();
                    att.writeToParcel(p, 0);
                    p.setDataPosition(0);
                    final Attachment attClone = new Attachment(p);
                    p.recycle();
                    // Clear the messageKey (this is going to be a new attachment)
                    attClone.mMessageKey = 0;
                    // If we're sending this, it's not loaded, and we're not smart forwarding
                    // add the download flag, so that ADS will start up
                    if (mailbox.mType == Mailbox.TYPE_OUTBOX && att.getContentUri() == null &&
                            ((account.mFlags & Account.FLAGS_SUPPORTS_SMART_FORWARD) == 0)) {
                        attClone.mFlags |= Attachment.FLAG_DOWNLOAD_FORWARD;
                        hasUnloadedAttachments = true;
                    }
                    atts.add(attClone);
                }
                //TS: wenggangjin 2015-01-15 EMAIL BUGFIX_881446 MOD_S
                else {
                  //TS: zhonghua.tuo 2015-04-28 EMAIL BUGFIX_987931 MOD_S
                    try {
                        final String cachedFileUri =
                                AttachmentUtils.cacheAttachmentUri(context, uiAtt, attachmentFds);

                        // Convert external attachment to one of ours and add to the list
                        atts.add(convertUiAttachmentToAttachment(uiAtt, cachedFileUri, msg.mAccountKey));
                    } catch(SecurityException e) {
                        Log.d(TAG, "SecurityException in uiSaveMessage1");
                    }
                  //TS: zhonghua.tuo 2015-04-28 EMAIL BUGFIX_987931 MOD_E
                }
              //TS: wenggangjin 2015-01-15 EMAIL BUGFIX_881446 MOD_E
            } else {
                // Cache the attachment.  This will allow us to send it, if the permissions are
                // revoked.
                //AM: peng-zhang 2015-03-12 EMAIL BUGFIX_944106 MOD_S
                if(attachmentFds.isEmpty()){
                     Attachment attachment = convertUiAttachmentToAttachment(uiAtt, null, msg.mAccountKey);
                     atts.add(attachment);
                }else{
                  //TS: zhonghua.tuo 2015-04-28 EMAIL BUGFIX_987931 MOD_S
                    try {
                         final String cachedFileUri =
                                AttachmentUtils.cacheAttachmentUri(context, uiAtt, attachmentFds);

                         // Convert external attachment to one of ours and add to the list
                         atts.add(convertUiAttachmentToAttachment(uiAtt, cachedFileUri, msg.mAccountKey));
                    } catch(SecurityException e) {
                        Log.d(TAG, "SecurityException in uiSaveMessage2");
                    }
                  //TS: zhonghua.tuo 2015-04-28 EMAIL BUGFIX_987931 MOD_E
                }
                //AM: peng-zhang 2015-03-12 EMAIL BUGFIX_944106 MOD_E
            }
        }
        //TS: yanhua.chen 2015-6-19 EMAIL BUGFIX_305581 MOD_S
        if (!atts.isEmpty()) {
            msg.mAttachments = atts;
            msg.mFlagAttachment = true;
            if (hasUnloadedAttachments) {
                Utility.showToast(context, R.string.message_view_attachment_background_load);
            }
        }else{
            msg.mAttachments = null;
            msg.mFlagAttachment = false;
        }
       //TS: yanhua.chen 2015-6-19 EMAIL BUGFIX_305581 MOD_E
        // Save it or update it...
        if (!msg.isSaved()) {
            msg.save(context);
        } else {
            // This is tricky due to how messages/attachments are saved; rather than putz with
            // what's changed, we'll delete/re-add them
            final ArrayList<ContentProviderOperation> ops =
                    new ArrayList<ContentProviderOperation>();
            // Delete all existing attachments
            ops.add(ContentProviderOperation.newDelete(
                    ContentUris.withAppendedId(Attachment.MESSAGE_ID_URI, msg.mId))
                    .build());
            // Delete the body
            ops.add(ContentProviderOperation.newDelete(Body.CONTENT_URI)
                    .withSelection(BodyColumns.MESSAGE_KEY + "=?",
                            new String[] {Long.toString(msg.mId)})
                    .build());
            // Add the ops for the message, atts, and body
            msg.addSaveOps(ops);
            // Do it!
            try {
                applyBatch(ops);
            } catch (OperationApplicationException e) {
                LogUtils.d(TAG, "applyBatch exception");
            }
        }
        notifyUIMessage(msg.mId);

        // TS: chao.zhang 2015-09-14 EMAIL FEATURE-585337 MOD_S
        //NOTE: before start sending, just change the new comer status to queued
        //In order reduce frequently request sync,just update db.
        if (mailbox.mType == Mailbox.TYPE_OUTBOX) {
            ContentResolver resolver = context.getContentResolver();
                ContentValues updateStatus = new ContentValues();
                updateStatus.put(MessageColumns.SENDING_STATUS, Message.MAIL_IN_QUEUE_STATUS);
                resolver.update(ContentUris.withAppendedId(Message.CONTENT_URI, msg.mId),
                        updateStatus, null, null);
            // TS: chao.zhang 2015-09-14 EMAIL FEATURE-585337 MOD_S
            startSync(mailbox, 0);
            final long originalMsgId = msg.mSourceKey;
            if (originalMsgId != 0) {
                final Message originalMsg = Message.restoreMessageWithId(context, originalMsgId);
                // If the original message exists, set its forwarded/replied to flags
                if (originalMsg != null) {
                    final ContentValues cv = new ContentValues();
                    flags = originalMsg.mFlags;
                    switch(draftType) {
                        case DraftType.FORWARD:
                            flags |= Message.FLAG_FORWARDED;
                            break;
                        case DraftType.REPLY_ALL:
                        case DraftType.REPLY:
                            flags |= Message.FLAG_REPLIED_TO;
                            break;
                    }
                    cv.put(MessageColumns.FLAGS, flags);
                    resolver.update(ContentUris.withAppendedId(
                                //AM: liming-wang 2015-01-26 EMAIL BUGFIX_875230 MOD_S
//                            Message.CONTENT_URI, originalMsgId), cv, null, null);
                                Message.SYNCED_CONTENT_URI, originalMsgId), cv, null, null);
                                //AM: liming-wang 2015-01-26 EMAIL BUGFIX_875230 MOD_E
                }
            }
        }
        return uiUri("uimessage", msg.mId);
    }

    // TS: chao.zhang 2015-09-14 EMAIL FEATURE-585337 ADD_S
    /**
     * query the mailbox(just OUTBOX) is empty? just query it.
     * @param context
     * @param boxId
     * @return
     */
    private static boolean haveMessageInOutbox(Context context, long boxId) {
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(EmailContent.Message.CONTENT_URI,
                    EmailContent.Message.ID_COLUMN_PROJECTION, MessageColumns.MAILBOX_KEY + "=?",
                    new String[] {
                        Long.toString(boxId)
                    }, null);
            return cursor.getCount() > 0;
        } catch (Exception e) {
            LogUtils.e(LogUtils.TAG,e, "Exception happen during queue the outbox mails in haveMessageInOutbox ");
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return false;
    }
    // TS: chao.zhang 2015-09-14 EMAIL FEATURE-585337 ADD_E
    // TS: chao.zhang 2015-09-25 EMAIL FEATURE-585337 ADD_S
    /**
     * Give the chance to restore the mail's status,here we restore to queue status
     * @param context
     * @param boxId is the outbox's id
     */
    public static void restoreMailStatus(Context context, long boxId) {
        Cursor cursor = null;
        ContentResolver resolver = context.getContentResolver();
        ArrayList<Long> pendingSendMails = new ArrayList<Long>();
        ContentValues value = new ContentValues();
        // query the failed mails
        try {
            cursor = resolver.query(Message.CONTENT_URI,
                    Message.ID_COLUMN_WITH_STATUS_PROJECTION,
                    MessageColumns.SENDING_STATUS
                            + "=?" + " AND " + MessageColumns.MAILBOX_KEY + "=?",
                    new String[] {
                            Long.toString(Message.MAIL_IN_FAILED_STATUS),
                            Long.toString(boxId)
                    }, null);
            while (cursor.moveToNext()) {
                pendingSendMails.add(cursor.getLong(0));
            }
        } catch (Exception e) {
            LogUtils.d(LogUtils.TAG, e,
                    "Exception happen during queue the failed mails in restoreMailStatus ");
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        // update the mails status
        if (pendingSendMails.size() > 0) {
            for (long id : pendingSendMails) {
                value.clear();
                Uri uri = ContentUris.withAppendedId(Message.CONTENT_URI, id);
                value.put(MessageColumns.SENDING_STATUS, Message.MAIL_IN_QUEUE_STATUS);
                resolver.update(uri, value, null, null);
            }
        }
        pendingSendMails.clear();
    }
    // TS: chao.zhang 2015-09-25 EMAIL FEATURE-585337 ADD_S

    private Uri uiSaveDraftMessage(final long accountId, final Bundle extras) {
        Mailbox mailbox ;
        //[FEATURE]-Mod-BEGIN by TSNJ,Zhenhua.Fan,06/11/2014,FR-622609
        if (EmailApplication.isOrangeImapFeatureOn() && Account.isOrangeImapAccount(getContext(), accountId)) {
            final long mailboxId = Mailbox.getOrangeImapDraftboxId(getContext(),accountId);
            mailbox = Mailbox.restoreMailboxWithId(getContext(),mailboxId);
        } else {
            mailbox = getMailboxByAccountIdAndType(accountId, Mailbox.TYPE_DRAFTS);
        }
        //[FEATURE]-Mod-END by TSNJ,Zhenhua.Fan
        if (mailbox == null) return null;
        Message msg = null;
        if (extras.containsKey(BaseColumns._ID)) {
            final long messageId = extras.getLong(BaseColumns._ID);
            msg = Message.restoreMessageWithId(getContext(), messageId);
        }
        if (msg == null) {
            msg = new Message();
        }
        return uiSaveMessage(msg, mailbox, extras);
    }

    private Uri uiSendDraftMessage(final long accountId, final Bundle extras) {
        final Message msg;
        if (extras.containsKey(BaseColumns._ID)) {
            final long messageId = extras.getLong(BaseColumns._ID);
            msg = Message.restoreMessageWithId(getContext(), messageId);
        } else {
            msg = new Message();
        }

        if (msg == null) {
            Log.d(TAG, "uiSendDraftMessage() -> msg is null, and message will missing."); // AM: Kexue.Geng 2015-04-30 EMAIL BUGFIX_983211 MOD
            return null;
        }
        final Mailbox mailbox = getMailboxByAccountIdAndType(accountId, Mailbox.TYPE_OUTBOX);
        if (mailbox == null) {
            Log.d(TAG, "uiSendDraftMessage() -> outMailbox is null, and message will missing."); // AM: Kexue.Geng 2015-04-30 EMAIL BUGFIX_983211 MOD
            return null;
        }
        // Make sure the sent mailbox exists, since it will be necessary soon.
        // TODO(yph): move system mailbox creation to somewhere sane.
        final Mailbox sentMailbox = getMailboxByAccountIdAndType(accountId, Mailbox.TYPE_SENT);
        if (sentMailbox == null) {
            Log.d(TAG, "uiSendDraftMessage() -> sentMailbox is null, and message will missing."); // AM: Kexue.Geng 2015-04-30 EMAIL BUGFIX_983211 MOD
            return null;
        }
        final Uri messageUri = uiSaveMessage(msg, mailbox, extras);
        // AM: Kexue.Geng 2015-04-30 EMAIL BUGFIX_983211 MOD_S
        if(messageUri == null) {
            Log.d(TAG, "uiSendDraftMessage() -> messageUri is null, and message will missing.");
        }
        //TS: junwei-xu 2016-02-26 EMAIL BUGFIX-1659885 ADD_S
        //Note: notify draft mailbox to update conversation list.
        Mailbox draftMailbox;
        if (EmailApplication.isOrangeImapFeatureOn() &&
                Account.isOrangeImapAccount(getContext(), accountId)) {
            final long mailboxId = Mailbox.getOrangeImapDraftboxId(getContext(), accountId);
            draftMailbox = Mailbox.restoreMailboxWithId(getContext(), mailboxId);
        } else {
            draftMailbox = getMailboxByAccountIdAndType(accountId, Mailbox.TYPE_DRAFTS);
        }
        if (draftMailbox != null) {
            notifyUI(UIPROVIDER_CONVERSATION_NOTIFIER, draftMailbox.mId);
        }
        //TS: junwei-xu 2016-02-26 EMAIL BUGFIX-1659885 ADD_E
        // AM: Kexue.Geng 2015-04-30 EMAIL BUGFIX_983211 MOD_E
        // Kick observers
        notifyUI(Mailbox.CONTENT_URI, null);
        return messageUri;
    }

    private static void putIntegerLongOrBoolean(ContentValues values, String columnName,
            Object value) {
        if (value instanceof Integer) {
            Integer intValue = (Integer)value;
            values.put(columnName, intValue);
        } else if (value instanceof Boolean) {
            Boolean boolValue = (Boolean)value;
            values.put(columnName, boolValue ? 1 : 0);
        } else if (value instanceof Long) {
            Long longValue = (Long)value;
            values.put(columnName, longValue);
        }
    }

    /**
     * Update the timestamps for the folders specified and notifies on the recent folder URI.
     * @param folders array of folder Uris to update
     * @return number of folders updated
     */
    private int updateTimestamp(final Context context, String id, Uri[] folders){
        int updated = 0;
        final long now = System.currentTimeMillis();
        final ContentResolver resolver = context.getContentResolver();
        final ContentValues touchValues = new ContentValues(1);
        for (final Uri folder : folders) {
            touchValues.put(MailboxColumns.LAST_TOUCHED_TIME, now);
            LogUtils.d(TAG, "updateStamp: %s updated", folder);
            updated += resolver.update(folder, touchValues, null, null);
        }
        final Uri toNotify =
                UIPROVIDER_RECENT_FOLDERS_NOTIFIER.buildUpon().appendPath(id).build();
        LogUtils.d(TAG, "updateTimestamp: Notifying on %s", toNotify);
        notifyUI(toNotify, null);
        return updated;
    }

    /**
     * Updates the recent folders. The values to be updated are specified as ContentValues pairs
     * of (Folder URI, access timestamp). Returns nonzero if successful, always.
     * @param uri provider query uri
     * @param values uri, timestamp pairs
     * @return nonzero value always.
     */
    private int uiUpdateRecentFolders(Uri uri, ContentValues values) {
        final int numFolders = values.size();
        final String id = uri.getPathSegments().get(1);
        final Uri[] folders = new Uri[numFolders];
        final Context context = getContext();
        int i = 0;
        for (final String uriString : values.keySet()) {
            folders[i] = Uri.parse(uriString);
        }
        return updateTimestamp(context, id, folders);
    }

    /**
     * Populates the recent folders according to the design.
     * @param uri provider query uri
     * @return the number of recent folders were populated.
     */
    private int uiPopulateRecentFolders(Uri uri) {
        final Context context = getContext();
        final String id = uri.getLastPathSegment();
        final Uri[] recentFolders = defaultRecentFolders(id);
        final int numFolders = recentFolders.length;
        if (numFolders <= 0) {
            return 0;
        }
        final int rowsUpdated = updateTimestamp(context, id, recentFolders);
        LogUtils.d(TAG, "uiPopulateRecentFolders: %d folders changed", rowsUpdated);
        return rowsUpdated;
    }

    private int uiUpdateAttachment(Uri uri, ContentValues uiValues) {
        int result = 0;
        Integer stateValue = uiValues.getAsInteger(UIProvider.AttachmentColumns.STATE);
        if (stateValue != null) {
            // This is a command from UIProvider
            long attachmentId = Long.parseLong(uri.getLastPathSegment());
            Context context = getContext();
            Attachment attachment =
                    Attachment.restoreAttachmentWithId(context, attachmentId);
            if (attachment == null) {
                // Went away; ah, well...
                return result;
            }
            int state = stateValue;
            ContentValues values = new ContentValues();
            if (state == UIProvider.AttachmentState.NOT_SAVED
                    || state == UIProvider.AttachmentState.REDOWNLOADING) {
                // Set state, try to cancel request
                values.put(AttachmentColumns.UI_STATE, UIProvider.AttachmentState.NOT_SAVED);
                values.put(AttachmentColumns.FLAGS,
                        attachment.mFlags &= ~Attachment.FLAG_DOWNLOAD_USER_REQUEST);
                attachment.update(context, values);
                result = 1;
            }
            if (state == UIProvider.AttachmentState.DOWNLOADING
                    || state == UIProvider.AttachmentState.REDOWNLOADING
                    || state == UIProvider.AttachmentState.READYDOWNLOADING) {// TS: Gantao 2015-12-24 EMAIL BUGFIX-1175829 MOD
                // Set state and destination; request download
                // TS: Gantao 2015-12-24 EMAIL BUGFIX-1175829 MOD_S
                //When we start download attachment, set the state as ready_downloading first.
                values.put(AttachmentColumns.UI_STATE, UIProvider.AttachmentState.READYDOWNLOADING);
                // TS: Gantao 2015-12-24 EMAIL BUGFIX-1175829 MOD_E
                Integer destinationValue =
                        uiValues.getAsInteger(UIProvider.AttachmentColumns.DESTINATION);
                values.put(AttachmentColumns.UI_DESTINATION,
                        destinationValue == null ? 0 : destinationValue);
                values.put(AttachmentColumns.FLAGS,
                        attachment.mFlags | Attachment.FLAG_DOWNLOAD_USER_REQUEST);

                if (values.containsKey(AttachmentColumns.LOCATION) &&
                        TextUtils.isEmpty(values.getAsString(AttachmentColumns.LOCATION))) {
                    LogUtils.w(TAG, new Throwable(), "attachment with blank location");
                }

                attachment.update(context, values);
                result = 1;
            }
            if (state == UIProvider.AttachmentState.SAVED) {
                // If this is an inline attachment, notify message has changed
                if (!TextUtils.isEmpty(attachment.mContentId)) {
                    notifyUI(UIPROVIDER_MESSAGE_NOTIFIER, attachment.mMessageKey);
                }
                result = 1;
            }
        }
        return result;
    }

    private int uiUpdateFolder(final Context context, Uri uri, ContentValues uiValues) {
        // We need to mark seen separately
        if (uiValues.containsKey(UIProvider.ConversationColumns.SEEN)) {
            final int seenValue = uiValues.getAsInteger(UIProvider.ConversationColumns.SEEN);

            if (seenValue == 1) {
                final String mailboxId = uri.getLastPathSegment();
                final int rows = markAllSeen(context, mailboxId);

                if (uiValues.size() == 1) {
                    // Nothing else to do, so return this value
                    return rows;
                }
            }
        }

        final Uri ourUri = convertToEmailProviderUri(uri, Mailbox.CONTENT_URI, true);
        if (ourUri == null) return 0;
        ContentValues ourValues = new ContentValues();
        // This should only be called via update to "recent folders"
        for (String columnName: uiValues.keySet()) {
            if (columnName.equals(MailboxColumns.LAST_TOUCHED_TIME)) {
                ourValues.put(MailboxColumns.LAST_TOUCHED_TIME, uiValues.getAsLong(columnName));
            }
        }
        return update(ourUri, ourValues, null, null);
    }

    private int uiUpdateSettings(final Context c, final ContentValues uiValues) {
        final MailPrefs mailPrefs = MailPrefs.get(c);

        if (uiValues.containsKey(SettingsColumns.AUTO_ADVANCE)) {
            mailPrefs.setAutoAdvanceMode(uiValues.getAsInteger(SettingsColumns.AUTO_ADVANCE));
        }
        if (uiValues.containsKey(SettingsColumns.CONVERSATION_VIEW_MODE)) {
            final int value = uiValues.getAsInteger(SettingsColumns.CONVERSATION_VIEW_MODE);
            final boolean overviewMode = value == UIProvider.ConversationViewMode.OVERVIEW;
            mailPrefs.setConversationOverviewMode(overviewMode);
        }

        c.getContentResolver().notifyChange(UIPROVIDER_ALL_ACCOUNTS_NOTIFIER, null, false);

        return 1;
    }

    private int markAllSeen(final Context context, final String mailboxId) {
        final SQLiteDatabase db = getDatabase(context);
        final String table = Message.TABLE_NAME;
        final ContentValues values = new ContentValues(1);
        values.put(MessageColumns.FLAG_SEEN, 1);
        final String whereClause = MessageColumns.MAILBOX_KEY + " = ?";
        final String[] whereArgs = new String[] {mailboxId};

        return db.update(table, values, whereClause, whereArgs);
    }

    private ContentValues convertUiMessageValues(Message message, ContentValues values) {
        final ContentValues ourValues = new ContentValues();
        for (String columnName : values.keySet()) {
            final Object val = values.get(columnName);
            if (columnName.equals(UIProvider.ConversationColumns.STARRED)) {
                putIntegerLongOrBoolean(ourValues, MessageColumns.FLAG_FAVORITE, val);
            } else if (columnName.equals(UIProvider.ConversationColumns.READ)) {
                putIntegerLongOrBoolean(ourValues, MessageColumns.FLAG_READ, val);
            } else if (columnName.equals(UIProvider.ConversationColumns.SEEN)) {
                putIntegerLongOrBoolean(ourValues, MessageColumns.FLAG_SEEN, val);
            } else if (columnName.equals(MessageColumns.MAILBOX_KEY)) {
                putIntegerLongOrBoolean(ourValues, MessageColumns.MAILBOX_KEY, val);
            } else if (columnName.equals(UIProvider.ConversationOperations.FOLDERS_UPDATED)) {
                // Skip this column, as the folders will also be specified  the RAW_FOLDERS column
            } else if (columnName.equals(UIProvider.ConversationColumns.RAW_FOLDERS)) {
                // Convert from folder list uri to mailbox key
                final FolderList flist = FolderList.fromBlob(values.getAsByteArray(columnName));
                if (flist.folders.size() != 1) {
                    LogUtils.e(TAG,
                            "Incorrect number of folders for this message: Message is %s",
                            message.mId);
                } else {
                    final Folder f = flist.folders.get(0);
                    final Uri uri = f.folderUri.fullUri;
                    final Long mailboxId = Long.parseLong(uri.getLastPathSegment());
                    putIntegerLongOrBoolean(ourValues, MessageColumns.MAILBOX_KEY, mailboxId);
                }
            } else if (columnName.equals(UIProvider.MessageColumns.ALWAYS_SHOW_IMAGES)) {
                Address[] fromList = Address.fromHeader(message.mFrom);
                final MailPrefs mailPrefs = MailPrefs.get(getContext());
                for (Address sender : fromList) {
                    final String email = sender.getAddress();
                    mailPrefs.setDisplayImagesFromSender(email, null);
                }
            } else if (columnName.equals(UIProvider.ConversationColumns.VIEWED) ||
                    columnName.equals(UIProvider.ConversationOperations.Parameters.SUPPRESS_UNDO)) {
                // Ignore for now
            } else if (UIProvider.ConversationColumns.CONVERSATION_INFO.equals(columnName)) {
                // Email's conversation info is generated, not stored, so just ignore this update
            } else if(columnName.equals(MessageColumns.SENDING_STATUS)){
             // Ignore for now
            }
            else {
                throw new IllegalArgumentException("Can't update " + columnName + " in message");
            }
        }
        return ourValues;
    }

    private static Uri convertToEmailProviderUri(Uri uri, Uri newBaseUri, boolean asProvider) {
        final String idString = uri.getLastPathSegment();
        try {
            final long id = Long.parseLong(idString);
            Uri ourUri = ContentUris.withAppendedId(newBaseUri, id);
            if (asProvider) {
                ourUri = ourUri.buildUpon().appendQueryParameter(IS_UIPROVIDER, "true").build();
            }
            return ourUri;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Message getMessageFromLastSegment(Uri uri) {
        long messageId = Long.parseLong(uri.getLastPathSegment());
        return Message.restoreMessageWithId(getContext(), messageId);
    }

    /**
     * Add an undo operation for the current sequence; if the sequence is newer than what we've had,
     * clear out the undo list and start over
     * @param uri the uri we're working on
     * @param op the ContentProviderOperation to perform upon undo
     */
    private void addToSequence(Uri uri, ContentProviderOperation op) {
        String sequenceString = uri.getQueryParameter(UIProvider.SEQUENCE_QUERY_PARAMETER);
        if (sequenceString != null) {
            int sequence = Integer.parseInt(sequenceString);
            if (sequence > mLastSequence) {
                // Reset sequence
                mLastSequenceOps.clear();
                mLastSequence = sequence;
            }
            // TODO: Need something to indicate a change isn't ready (undoable)
            mLastSequenceOps.add(op);
        }
    }

    // TODO: This should depend on flags on the mailbox...
    private static boolean uploadsToServer(Context context, Mailbox m) {
        if (m.mType == Mailbox.TYPE_DRAFTS || m.mType == Mailbox.TYPE_OUTBOX ||
                m.mType == Mailbox.TYPE_SEARCH) {
            return false;
        }
        String protocol = Account.getProtocol(context, m.mAccountKey);
        EmailServiceInfo info = EmailServiceUtils.getServiceInfo(context, protocol);
        return (info != null && info.syncChanges);
    }

    private int uiUpdateMessage(Uri uri, ContentValues values) {
        return uiUpdateMessage(uri, values, false);
    }

    private int uiUpdateMessage(Uri uri, ContentValues values, boolean forceSync) {
        Context context = getContext();
        Message msg = getMessageFromLastSegment(uri);
        if (msg == null) return 0;
        Mailbox mailbox = Mailbox.restoreMailboxWithId(context, msg.mMailboxKey);
        if (mailbox == null) return 0;
        Uri ourBaseUri =
                (forceSync || uploadsToServer(context, mailbox)) ? Message.SYNCED_CONTENT_URI :
                    Message.CONTENT_URI;
        Uri ourUri = convertToEmailProviderUri(uri, ourBaseUri, true);
        if (ourUri == null) return 0;

        // Special case - meeting response
        if (values.containsKey(UIProvider.MessageOperations.RESPOND_COLUMN)) {
         // TS: zhaotianyong 2015-02-04 EXCHANGE BUGFIX-923513 ADD_S
          //NOTE:For the CR,we get the comment from editText,and the put it to messageBody
            //that during eas sending the response,we also send the comment too.
            if(values.containsKey(UIProvider.MessageOperations.RESPOND_CONTENT)){
                String bodyContent = values.getAsString(UIProvider.MessageOperations.RESPOND_CONTENT);
                //Just use android.utils.Log in case of FormatFlagsConversionMismatchException.
//                LogUtils.w(TAG,"The response coment is:"+bodyContent);
                Log.i(TAG,"The response content is : " + bodyContent);
             // TS: Gantao 2015-07-29 EXCHANGE BUGFIX-1054379 MOD
                if(bodyContent != null){//even the response content is "",we also upate it to body.
                ContentValues value = new ContentValues();
                value.put(BodyColumns.TEXT_CONTENT, bodyContent);
                Body.updateBodyWithMessageId(context, msg.mId, value);
                }
            }
         // TS: zhaotianyong 2015-02-04 EXCHANGE BUGFIX-923513 ADD_E
            final EmailServiceProxy service =
                    EmailServiceUtils.getServiceForAccount(context, mailbox.mAccountKey);
            try {
                service.sendMeetingResponse(msg.mId,
                        values.getAsInteger(UIProvider.MessageOperations.RESPOND_COLUMN));
             // TS: Gantao 2015-10-15 EXCHANGE BUGFIX-679699 MOD_S
                if(mailbox.mType != Mailbox.TYPE_TRASH) {//Don't delete the event mail in the trash box
                    // TS: zheng.zou 2015-12-22 EXCHANGE BUGFIX-1123842 MOD_S
                 // Delete the message immediately
//                    uiDeleteMessage(uri);
                 // Notify box has changed so the deletion is reflected in the UI
//                    notifyUIConversationMailbox(mailbox.mId);
                    // TS: zheng.zou 2015-12-22 EXCHANGE BUGFIX-1123842 MOD_E
                }
                Utility.showToast(context, R.string.confirm_response);
             // TS: Gantao 2015-10-15 EXCHANGE BUGFIX-679699 MOD_E
            } catch (RemoteException e) {
                LogUtils.d(TAG, "Remote exception while sending meeting response");
            }
            return 1;
        }

        // Another special case - deleting a draft.
        final String operation = values.getAsString(
                UIProvider.ConversationOperations.OPERATION_KEY);
        // TODO: for now let's just default to delete for MOVE_FAILED_TO_DRAFT operation
        if (UIProvider.ConversationOperations.DISCARD_DRAFTS.equals(operation) ||
                UIProvider.ConversationOperations.MOVE_FAILED_TO_DRAFTS.equals(operation)) {
            uiDeleteMessage(uri);
            return 1;
        }

        ContentValues undoValues = new ContentValues();
        ContentValues ourValues = convertUiMessageValues(msg, values);
        for (String columnName: ourValues.keySet()) {
            if (columnName.equals(MessageColumns.MAILBOX_KEY)) {
                undoValues.put(MessageColumns.MAILBOX_KEY, msg.mMailboxKey);
            } else if (columnName.equals(MessageColumns.FLAG_READ)) {
                undoValues.put(MessageColumns.FLAG_READ, msg.mFlagRead);
            } else if (columnName.equals(MessageColumns.FLAG_SEEN)) {
                undoValues.put(MessageColumns.FLAG_SEEN, msg.mFlagSeen);
            } else if (columnName.equals(MessageColumns.FLAG_FAVORITE)) {
                undoValues.put(MessageColumns.FLAG_FAVORITE, msg.mFlagFavorite);
            } else if(columnName.equals(MessageColumns.SENDING_STATUS)){//NOTE:in UNDO, restore the status to orginal.
                undoValues.put(MessageColumns.SENDING_STATUS,msg.mSendStatus);
            }
        }
        if (undoValues.size() == 0) {
            return -1;
        }
        final Boolean suppressUndo =
                values.getAsBoolean(UIProvider.ConversationOperations.Parameters.SUPPRESS_UNDO);
        if (suppressUndo == null || !suppressUndo) {
            final ContentProviderOperation op =
                    ContentProviderOperation.newUpdate(convertToEmailProviderUri(
                            uri, ourBaseUri, false))
                            .withValues(undoValues)
                            .build();
            addToSequence(uri, op);
        }
        //NOTE:for Uidelete,also resotore the status to NONE status.
        //ONLY care the Delete operation,flag forceSync says Delete.
        if (forceSync && mailbox.mType == Mailbox.TYPE_OUTBOX) {
            ourValues.put(MessageColumns.SENDING_STATUS, Message.MAIL_IN_NONE_STATUS);
        }
        //TS: junwei-xu 2015-08-18 EMAIL BUGFIX_499604 MOD_S
        //Note: should notify the source mail box if update folder message
        int ret = update(ourUri, ourValues, null, null);
        if (values.containsKey(UIProvider.ConversationOperations.FOLDERS_UPDATED)) {
            String foldersUpdated = values.getAsString(UIProvider.ConversationOperations.FOLDERS_UPDATED);
            if (foldersUpdated != null && !foldersUpdated.equals("")) {
                //TS:kaifeng.lu 2015-10-14 EMAIL BUGFIX_716697 MOD_S
                //replace "," to UIProvider.ConversationOperations.FOLDERS_UPDATED_SPLIT_PATTERN
                String[] uris = foldersUpdated.split(UIProvider.ConversationOperations.FOLDERS_UPDATED_SPLIT_PATTERN);
                //TS:kaifeng.lu 2015-10-14 EMAIL BUGFIX_716697 MOD_E
                if (uris != null && uris.length > 0) {
                    long sourceFolderId = -1;
                    long dstFolderId = ourValues.getAsLong(MessageColumns.MAILBOX_KEY);
                    String id = null;
                    for (String uriTemp : uris) {
                        List<String> segments = Uri.parse(uriTemp).getPathSegments();
                        if (segments != null && segments.size() > 1) {
                            id = segments.get(1);
                            try {
                                if (Long.parseLong(id) != dstFolderId) {
                                    sourceFolderId = Long.parseLong(id);
                                    break;
                                }
                            } catch (NumberFormatException e) {
                                //Do nothing
                            }
                        }
                    }
                    if (sourceFolderId != -1) {
                        notifyUIFolder(sourceFolderId, mailbox.mAccountKey);
                    }
                }
            }
        }
        //NOTE:After user clear the outbox,also auto clear the FailStatus notification.
        if (forceSync && mailbox.mType == Mailbox.TYPE_OUTBOX) {
            if (!haveMessageInOutbox(context, mailbox.mId)) {
                NotificationController.getInstance(context).cancelFailStatusNotification(mailbox.mAccountKey);
            }
        }
        return ret;
        //TS: junwei-xu 2015-08-18 EMAIL BUGFIX_499604 MOD_E
    }

    /**
     * Projection for use with getting mailbox & account keys for a message.
     */
    private static final String[] MESSAGE_KEYS_PROJECTION =
            { MessageColumns.MAILBOX_KEY, MessageColumns.ACCOUNT_KEY };
    private static final int MESSAGE_KEYS_MAILBOX_KEY_COLUMN = 0;
    private static final int MESSAGE_KEYS_ACCOUNT_KEY_COLUMN = 1;

    /**
     * Notify necessary UI components in response to a message update.
     * @param uri The {@link Uri} for this message update.
     * @param messageId The id of the message that's been updated.
     * @param values The {@link ContentValues} that were updated in the message.
     */
    private void handleMessageUpdateNotifications(final Uri uri, final String messageId,
            final ContentValues values) {
        if (!uri.getBooleanQueryParameter(IS_UIPROVIDER, false)) {
            notifyUIConversation(uri);
        }
        notifyUIMessage(messageId);
        // TODO: Ideally, also test that the values actually changed.
        if (values.containsKey(MessageColumns.FLAG_READ) ||
                values.containsKey(MessageColumns.MAILBOX_KEY) ||
                values.containsKey(MessageColumns.FLAG_FAVORITE)) {// TS: zhaotianyong 2014-12-04 EMAIL BUGFIX-851681 ADD
            final Cursor c = query(
                    Message.CONTENT_URI.buildUpon().appendEncodedPath(messageId).build(),
                    MESSAGE_KEYS_PROJECTION, null, null, null);
            if (c != null) {
                try {
                    if (c.moveToFirst()) {
                        notifyUIFolder(c.getLong(MESSAGE_KEYS_MAILBOX_KEY_COLUMN),
                                c.getLong(MESSAGE_KEYS_ACCOUNT_KEY_COLUMN));
                    }
                } finally {
                    c.close();
                }
            }
        }
    }

    /**
     * Perform a "Delete" operation
     * @param uri message to delete
     * @return number of rows affected
     */
    private int uiDeleteMessage(Uri uri) {
        final Context context = getContext();
        Message msg = getMessageFromLastSegment(uri);
        if (msg == null) return 0;
        Mailbox mailbox = Mailbox.restoreMailboxWithId(context, msg.mMailboxKey);
        if (mailbox == null) return 0;
        boolean isInOrangeTrashbox = false;
        //[FEATURE]-Add-BEGIN by TSNJ.Zhenhua.Fan,09/06/2014,FR 622609
        if (EmailApplication.isOrangeImapFeatureOn() && Account.isOrangeImapAccount(context, mailbox.mAccountKey))
        {
            long trashMailboxId = Mailbox.getOrangeImapTrashboxId(context, mailbox.mAccountKey);
            if(mailbox.mId == trashMailboxId)
                isInOrangeTrashbox = true;
        }
        //[FEATURE]-Add-END by TSNJ.Zhenhua.Fan
        if (mailbox.mType == Mailbox.TYPE_TRASH || mailbox.mType == Mailbox.TYPE_DRAFTS||isInOrangeTrashbox) {
            // We actually delete these, including attachments
            AttachmentUtilities.deleteAllAttachmentFiles(context, msg.mAccountKey, msg.mId);
            final int r = context.getContentResolver().delete(
                    ContentUris.withAppendedId(Message.SYNCED_CONTENT_URI, msg.mId), null, null);
            notifyUIFolder(mailbox.mId, mailbox.mAccountKey);
            notifyUIMessage(msg.mId);
            return r;
        }
        Mailbox trashMailbox =
                Mailbox.restoreMailboxOfType(context, msg.mAccountKey, Mailbox.TYPE_TRASH);
        if (trashMailbox == null) {
            return 0;
        }
        ContentValues values = new ContentValues();
        //NOTE:Actually,Not useful here,but we must triggle the UNDO status resotore in uiUpdateMessage.
        if (mailbox.mType == Mailbox.TYPE_OUTBOX) {
            values.put(MessageColumns.SENDING_STATUS, Message.MAIL_IN_NONE_STATUS);
        }
        //[FEATURE]-Mod-BEGIN by TSNJ.Zhenhua.Fan,09/06/2014,FR 622609
        if (EmailApplication.isOrangeImapFeatureOn() && Account.isOrangeImapAccount(context, mailbox.mAccountKey))
        {
            long trashMailboxId = Mailbox.getOrangeImapTrashboxId(context, mailbox.mAccountKey);
            values.put(MessageColumns.MAILBOX_KEY, trashMailboxId);
        }
        else{
            values.put(MessageColumns.MAILBOX_KEY, trashMailbox.mId);
        }
        //[FEATURE]-Mod-END by TSNJ.Zhenhua.Fan
        final int r = uiUpdateMessage(uri, values, true);
        notifyUIFolder(mailbox.mId, mailbox.mAccountKey);
        notifyUIMessage(msg.mId);
        return r;
    }

    /**
     * Hard delete all synced messages in a particular mailbox
     * @param uri Mailbox to empty (Trash, or maybe Spam/Junk later)
     * @return number of rows affected
     */
    private int uiPurgeFolder(Uri uri) {
        final Context context = getContext();
        final long mailboxId = Long.parseLong(uri.getLastPathSegment());
        final SQLiteDatabase db = getDatabase(context);

        // Find the account ID (needed in a few calls)
        final Cursor mailboxCursor = db.query(
                Mailbox.TABLE_NAME, new String[] { MailboxColumns.ACCOUNT_KEY },
                Mailbox._ID + "=" + mailboxId, null, null, null, null);
        if (mailboxCursor == null || !mailboxCursor.moveToFirst()) {
            LogUtils.wtf(LogUtils.TAG, "Null or empty cursor when trying to purge mailbox %d",
                    mailboxId);
            return 0;
        }
        final long accountId = mailboxCursor.getLong(mailboxCursor.getColumnIndex(
                MailboxColumns.ACCOUNT_KEY));

        //[BUGFIX]-Mod-BEGIN by TCTNB.caixia.chen,01/07/2015,PR 893304,close cursor
        if (mailboxCursor != null) {
            mailboxCursor.close();
        }
        //[BUGFIX]-Mod-END by TCTNB.caixia.chen

        // Find all the messages in the mailbox
        final String[] messageProjection =
                new String[] { MessageColumns._ID };
        final String messageWhere = MessageColumns.MAILBOX_KEY + "=" + mailboxId;
        final Cursor messageCursor = db.query(Message.TABLE_NAME, messageProjection, messageWhere,
                null, null, null, null);
        int deletedCount = 0;

        // Kill them with fire
        while (messageCursor != null && messageCursor.moveToNext()) {
            final long messageId = messageCursor.getLong(messageCursor.getColumnIndex(
                    MessageColumns._ID));
            AttachmentUtilities.deleteAllAttachmentFiles(context, accountId, messageId);
            deletedCount += context.getContentResolver().delete(
                    ContentUris.withAppendedId(Message.SYNCED_CONTENT_URI, messageId), null, null);
            notifyUIMessage(messageId);
        }

        //[BUGFIX]-Mod-BEGIN by TCTNB.caixia.chen,01/07/2015,PR 893304,close cursor
        if (messageCursor != null) {
            messageCursor.close();
        }
        //[BUGFIX]-Mod-END by TCTNB.caixia.chen

        notifyUIFolder(mailboxId, accountId);
        return deletedCount;
    }

    public static final String PICKER_UI_ACCOUNT = "picker_ui_account";
    public static final String PICKER_MAILBOX_TYPE = "picker_mailbox_type";
    // Currently unused
    //public static final String PICKER_MESSAGE_ID = "picker_message_id";
    public static final String PICKER_HEADER_ID = "picker_header_id";

    private int pickFolder(Uri uri, int type, int headerId) {
        Context context = getContext();
        Long acctId = Long.parseLong(uri.getLastPathSegment());
        // For push imap, for example, we want the user to select the trash mailbox
        Cursor ac = query(uiUri("uiaccount", acctId), UIProvider.ACCOUNTS_PROJECTION,
                null, null, null);
        try {
            if (ac.moveToFirst()) {
                final com.tct.mail.providers.Account uiAccount =
                        com.tct.mail.providers.Account.builder().buildFrom(ac);
                Intent intent = new Intent(context, FolderPickerActivity.class);
                intent.putExtra(PICKER_UI_ACCOUNT, uiAccount);
                intent.putExtra(PICKER_MAILBOX_TYPE, type);
                intent.putExtra(PICKER_HEADER_ID, headerId);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                return 1;
            }
            return 0;
        } finally {
            ac.close();
        }
    }

    private int pickTrashFolder(Uri uri) {
        return pickFolder(uri, Mailbox.TYPE_TRASH, R.string.trash_folder_selection_title);
    }

    private int pickSentFolder(Uri uri) {
        return pickFolder(uri, Mailbox.TYPE_SENT, R.string.sent_folder_selection_title);
    }

    private Cursor uiUndo(String[] projection) {
        // First see if we have any operations saved
        // TODO: Make sure seq matches
        if (!mLastSequenceOps.isEmpty()) {
            try {
                // TODO Always use this projection?  Or what's passed in?
                // Not sure if UI wants it, but I'm making a cursor of convo uri's
                MatrixCursor c = new MatrixCursorWithCachedColumns(
                        new String[] {UIProvider.ConversationColumns.URI},
                        mLastSequenceOps.size());
                for (ContentProviderOperation op: mLastSequenceOps) {
                    c.addRow(new String[] {op.getUri().toString()});
                }
                // Just apply the batch and we're done!
                applyBatch(mLastSequenceOps);
                // But clear the operations
                mLastSequenceOps.clear();
                return c;
            } catch (OperationApplicationException e) {
                LogUtils.d(TAG, "applyBatch exception");
            }
        }
        return new MatrixCursorWithCachedColumns(projection, 0);
    }

    private void notifyUIConversation(Uri uri) {
        String id = uri.getLastPathSegment();
        //TS: yanhua.chen 2015-8-28 EMAIL BUGFIX_1072954 MOD_S
        Message msg = null;
        try{
            msg = Message.restoreMessageWithId(getContext(), Long.parseLong(id));
        }catch(SQLiteCantOpenDatabaseException e){
            LogUtils.e(TAG, "RestoreMessageWithId occur SQLiteCantOpenDatabaseException");
        }
        //TS: yanhua.chen 2015-8-28 EMAIL BUGFIX_1072954 MOD_E
        if (msg != null) {
            notifyUIConversationMailbox(msg.mMailboxKey);
        }
    }

    /**
     * Notify about the Mailbox id passed in
     * @param id the Mailbox id to be notified
     */
    private void notifyUIConversationMailbox(long id) {
        notifyUI(UIPROVIDER_CONVERSATION_NOTIFIER, Long.toString(id));
        Mailbox mailbox = Mailbox.restoreMailboxWithId(getContext(), id);
        if (mailbox == null) {
            LogUtils.w(TAG, "No mailbox for notification: " + id);
            return;
        }
        // Notify combined inbox...
        if (mailbox.mType == Mailbox.TYPE_INBOX) {
            notifyUI(UIPROVIDER_CONVERSATION_NOTIFIER,
                    EmailProvider.combinedMailboxId(Mailbox.TYPE_INBOX));
        }
        notifyWidgets(id);
    }

    /**
     * Notify about the message id passed in
     * @param id the message id to be notified
     */
    private void notifyUIMessage(long id) {
        notifyUI(UIPROVIDER_MESSAGE_NOTIFIER, id);
    }

    /**
     * Notify about the message id passed in
     * @param id the message id to be notified
     */
    private void notifyUIMessage(String id) {
        notifyUI(UIPROVIDER_MESSAGE_NOTIFIER, id);
    }

    /**
     * Notify about the Account id passed in
     * @param id the Account id to be notified
     */
    private void notifyUIAccount(long id) {
        // Notify on the specific account
        notifyUI(UIPROVIDER_ACCOUNT_NOTIFIER, Long.toString(id));

        // Notify on the all accounts list
        notifyUI(UIPROVIDER_ALL_ACCOUNTS_NOTIFIER, null);
    }

    // TODO: temporary workaround for ConversationCursor
    @Deprecated
    private static final int NOTIFY_FOLDER_LOOP_MESSAGE_ID = 0;
    @Deprecated
    private Handler mFolderNotifierHandler;

    /**
     * Notify about a folder update. Because folder changes can affect the conversation cursor's
     * extras, the conversation must also be notified here.
     * @param folderId the folder id to be notified
     * @param accountId the account id to be notified (for folder list notification).
     */
    private void notifyUIFolder(final String folderId, final long accountId) {
        notifyUI(UIPROVIDER_CONVERSATION_NOTIFIER, folderId);
        notifyUI(UIPROVIDER_FOLDER_NOTIFIER, folderId);
        if (accountId != Account.NO_ACCOUNT) {
            notifyUI(UIPROVIDER_FOLDERLIST_NOTIFIER, accountId);
        }

        // Notify for combined account too
        // TODO: might be nice to only notify when an inbox changes
        notifyUI(UIPROVIDER_FOLDER_NOTIFIER,
                getVirtualMailboxId(COMBINED_ACCOUNT_ID, Mailbox.TYPE_INBOX));
        //TS: junwei-xu 2015-2-27 EMAIL BUGFIX_930462 ADD_S
        notifyUI(UIPROVIDER_FOLDER_NOTIFIER,getVirtualMailboxId(COMBINED_ACCOUNT_ID, Mailbox.TYPE_UNREAD));
        //TS: junwei-xu 2015-2-27 EMAIL BUGFIX_930462 ADD_E
        notifyUI(UIPROVIDER_FOLDERLIST_NOTIFIER, COMBINED_ACCOUNT_ID);
        // TS: zhaotianyong 2014-12-04 EMAIL BUGFIX-851681 ADD_S
        notifyUI(UIPROVIDER_FOLDER_NOTIFIER, getVirtualMailboxId(accountId, Mailbox.TYPE_STARRED));
        notifyUI(UIPROVIDER_FOLDER_NOTIFIER,getVirtualMailboxId(COMBINED_ACCOUNT_ID, Mailbox.TYPE_STARRED));
        // TS: zhaotianyong 2014-12-04 EMAIL BUGFIX-851681 ADD_E
        // TS: junwei-xu 2014-12-30 EMAIL BUGFIX-881366 ADD_S
        notifyUI(UIPROVIDER_CONVERSATION_NOTIFIER,getVirtualMailboxId(accountId, Mailbox.TYPE_STARRED));
        // TS: junwei-xu 2014-12-30 EMAIL BUGFIX-881366 ADD_E
        //[FRETURE]-ADD-BEGIN by TSNJ.wei huang 11/20/2014 FR841757
        notifyUI(UIPROVIDER_FOLDER_NOTIFIER, getVirtualMailboxId(accountId, Mailbox.TYPE_UNREAD));
        notifyUI(UIPROVIDER_CONVERSATION_NOTIFIER,getVirtualMailboxId(accountId, Mailbox.TYPE_UNREAD));
        //TS: junwei-xu 2015-2-27 EMAIL BUGFIX_930462 ADD_S
        notifyUI(UIPROVIDER_CONVERSATION_NOTIFIER,getVirtualMailboxId(COMBINED_ACCOUNT_ID, Mailbox.TYPE_INBOX));
        notifyUI(UIPROVIDER_CONVERSATION_NOTIFIER,getVirtualMailboxId(COMBINED_ACCOUNT_ID, Mailbox.TYPE_STARRED));
        notifyUI(UIPROVIDER_CONVERSATION_NOTIFIER,getVirtualMailboxId(COMBINED_ACCOUNT_ID, Mailbox.TYPE_UNREAD));
        //TS: junwei-xu 2015-2-27 EMAIL BUGFIX_930462 ADD_E
        //[FEATURE]-ADD-END by TSNJ.wei huang
        //[FRETURE]-ADD-BEGIN by TSNJ.wei huang 12/01/2014 FR845868
        //TS: xiaolin.li 2014-12-10 EMAIL READ_PLF MOD_S
        //if(getContext().getResources().getBoolean(R.bool.feature_email_send_launcher_unread_on)){
        if(PLFUtils.getBoolean(getContext(), "feature_email_send_launcher_unread_on")){
            Utilities.sendUnreadCountToLauncher(getContext());
        }
        //TS: xiaolin.li 2014-12-10 EMAIL READ_PLF MOD_E
        //[FEATURE]-ADD-END by TSNJ.wei huang

        // TODO: temporary workaround for ConversationCursor
        synchronized (this) {
            if (mFolderNotifierHandler == null) {
                mFolderNotifierHandler = new Handler(Looper.getMainLooper(),
                        new Callback() {
                            @Override
                            public boolean handleMessage(final android.os.Message message) {
                                final String folderId = (String) message.obj;
                                LogUtils.d(TAG, "Notifying conversation Uri %s twice", folderId);
                                notifyUI(UIPROVIDER_CONVERSATION_NOTIFIER, folderId);
                                return true;
                            }
                        });
            }
        }
        mFolderNotifierHandler.removeMessages(NOTIFY_FOLDER_LOOP_MESSAGE_ID);
        android.os.Message message = android.os.Message.obtain(mFolderNotifierHandler,
                NOTIFY_FOLDER_LOOP_MESSAGE_ID);
        message.obj = folderId;
        mFolderNotifierHandler.sendMessageDelayed(message, 2000);
    }

    private void notifyUIFolder(final long folderId, final long accountId) {
        notifyUIFolder(Long.toString(folderId), accountId);
    }

    private void notifyUI(final Uri uri, final String id) {
        final Uri notifyUri = (id != null) ? uri.buildUpon().appendPath(id).build() : uri;
        final Set<Uri> batchNotifications = getBatchNotificationsSet();
        if (batchNotifications != null) {
            batchNotifications.add(notifyUri);
        } else {
            getContext().getContentResolver().notifyChange(notifyUri, null);
        }
    }

    private void notifyUI(Uri uri, long id) {
        notifyUI(uri, Long.toString(id));
    }

    private Mailbox getMailbox(final Uri uri) {
        final long id = Long.parseLong(uri.getLastPathSegment());
        return Mailbox.restoreMailboxWithId(getContext(), id);
    }

    /**
     * Create an android.accounts.Account object for this account.
     * @param accountId id of account to load.
     * @return an android.accounts.Account for this account, or null if we can't load it.
     */
    private android.accounts.Account getAccountManagerAccount(final long accountId) {
        final Context context = getContext();
        final Account account = Account.restoreAccountWithId(context, accountId);
        if (account == null) return null;
        return getAccountManagerAccount(context, account.mEmailAddress,
                account.getProtocol(context));
    }

    /**
     * Create an android.accounts.Account object for an emailAddress/protocol pair.
     * @param context A {@link Context}.
     * @param emailAddress The email address we're interested in.
     * @param protocol The protocol we're intereted in.
     * @return an {@link android.accounts.Account} for this info.
     */
    private static android.accounts.Account getAccountManagerAccount(final Context context,
            final String emailAddress, final String protocol) {
        final EmailServiceInfo info = EmailServiceUtils.getServiceInfo(context, protocol);
        if (info == null) {
            return null;
        }
        return new android.accounts.Account(emailAddress, info.accountType);
    }

    /**
     * Update an account's periodic sync if the sync interval has changed.
     * @param accountId id for the account to update.
     * @param values the ContentValues for this update to the account.
     */
    private void updateAccountSyncInterval(final long accountId, final ContentValues values) {
        final Integer syncInterval = values.getAsInteger(AccountColumns.SYNC_INTERVAL);
        if (syncInterval == null) {
            // No change to the sync interval.
            return;
        }
        final android.accounts.Account account = getAccountManagerAccount(accountId);
        if (account == null) {
            // Unable to load the account, or unknown protocol.
            return;
        }

        LogUtils.d(TAG, "Setting sync interval for account %s to %d minutes",
                accountId, syncInterval);

        // First remove all existing periodic syncs.
        final List<PeriodicSync> syncs =
                ContentResolver.getPeriodicSyncs(account, EmailContent.AUTHORITY);
        for (final PeriodicSync sync : syncs) {
            ContentResolver.removePeriodicSync(account, EmailContent.AUTHORITY, sync.extras);
        }

        // Only positive values of sync interval indicate periodic syncs. The value is in minutes,
        // while addPeriodicSync expects its time in seconds.
        if (syncInterval > 0) {
            ContentResolver.addPeriodicSync(account, EmailContent.AUTHORITY, Bundle.EMPTY,
                    syncInterval * DateUtils.MINUTE_IN_MILLIS / DateUtils.SECOND_IN_MILLIS);
        }
    }

    /**
     * Request a sync.
     * @param account The {@link android.accounts.Account} we want to sync.
     * @param mailboxId The mailbox id we want to sync (or one of the special constants in
     *                  {@link Mailbox}).
     * @param deltaMessageCount If we're requesting a load more, the number of additional messages
     *                          to sync.
     */
    private static void startSync(final android.accounts.Account account, final long mailboxId,
            final int deltaMessageCount) {
        final Bundle extras = Mailbox.createSyncBundle(mailboxId);
        extras.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        extras.putBoolean(ContentResolver.SYNC_EXTRAS_DO_NOT_RETRY, true);
        extras.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        if (deltaMessageCount != 0) {
            extras.putInt(Mailbox.SYNC_EXTRA_DELTA_MESSAGE_COUNT, deltaMessageCount);
        }
        extras.putString(EmailServiceStatus.SYNC_EXTRAS_CALLBACK_URI,
                EmailContent.CONTENT_URI.toString());
        extras.putString(EmailServiceStatus.SYNC_EXTRAS_CALLBACK_METHOD,
                SYNC_STATUS_CALLBACK_METHOD);
        // TS: zheng.zou 2015-05-26 EMAIL add-log-s-1009974
        boolean isPending = ContentResolver.isSyncPending(account, EmailContent.AUTHORITY);
        boolean isSyncActive = ContentResolver.isSyncActive(account, EmailContent.AUTHORITY);
        LogUtils.d(TAG, "test----startSync  isPending=" + isPending + " isSyncActive=" + isSyncActive +" mailboxId="+mailboxId + " account=" + account);
        // TS: zheng.zou 2015-05-26 EMAIL add-log-e-1009974
        ContentResolver.requestSync(account, EmailContent.AUTHORITY, extras);
        LogUtils.i(TAG, "requestSync EmailProvider startSync %s, %s", account.toString(),
                extras.toString());
    }

    /**
     * Request a sync.
     * @param mailbox The {@link Mailbox} we want to sync.
     * @param deltaMessageCount If we're requesting a load more, the number of additional messages
     *                          to sync.
     */
    private void startSync(final Mailbox mailbox, final int deltaMessageCount) {
        final android.accounts.Account account = getAccountManagerAccount(mailbox.mAccountKey);
        if (account != null) {
            startSync(account, mailbox.mId, deltaMessageCount);
        }
    }

    /**
     * Restart any push operations for an account.
     * @param account The {@link android.accounts.Account} we're interested in.
     */
    private static void restartPush(final android.accounts.Account account) {
        final Bundle extras = new Bundle();
        extras.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        extras.putBoolean(ContentResolver.SYNC_EXTRAS_DO_NOT_RETRY, true);
        extras.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        extras.putBoolean(Mailbox.SYNC_EXTRA_PUSH_ONLY, true);
        extras.putString(EmailServiceStatus.SYNC_EXTRAS_CALLBACK_URI,
                EmailContent.CONTENT_URI.toString());
        extras.putString(EmailServiceStatus.SYNC_EXTRAS_CALLBACK_METHOD,
                SYNC_STATUS_CALLBACK_METHOD);
        ContentResolver.requestSync(account, EmailContent.AUTHORITY, extras);
        LogUtils.i(TAG, "requestSync EmailProvider startSync %s, %s", account.toString(),
                extras.toString());
    }

    private Cursor uiFolderRefresh(final Mailbox mailbox, final int deltaMessageCount) {
        if (mailbox != null) {
            RefreshStatusMonitor.getInstance(getContext())
                    .monitorRefreshStatus(mailbox.mId, new RefreshStatusMonitor.Callback() {
                @Override
                public void onRefreshCompleted(long mailboxId, int result) {
                    final ContentValues values = new ContentValues();
                    // TS: xiaolin.li 2015-01-13 EMAIL BUGFIX-883410
                    values.put(Mailbox.UI_SYNC_STATUS, com.tct.mail.utils.UIProvider.SyncStatus.NO_SYNC);
                    values.put(Mailbox.UI_LAST_SYNC_RESULT, result);
                    mDatabase.update(
                            Mailbox.TABLE_NAME,
                            values,
                            WHERE_ID,
                            new String[] { String.valueOf(mailboxId) });
                    notifyUIFolder(mailbox.mId, mailbox.mAccountKey);
                }

                @Override
                public void onTimeout(long mailboxId) {
                    // todo
                    // TS: zheng.zou 2015-05-26 EMAIL add-log-s-1009974
                    LogUtils.d(TAG, "test----RefreshStatusMonitor onTimeout mailboxId=" + mailboxId);
                    final android.accounts.Account account = getAccountManagerAccount(mailbox.mAccountKey);
                    if (account != null) {
                        boolean isPending = ContentResolver.isSyncPending(account, EmailContent.AUTHORITY);
                        boolean isSyncActive = ContentResolver.isSyncActive(account, EmailContent.AUTHORITY);
                        LogUtils.d(TAG, "test----RefreshStatusMonitor onTimeout isPending=" + isPending + " isSyncActive=" + isSyncActive + " account=" + account);
                        // TS: chaozhang 2015-06-23 EMAIL BUGFIX-1006486 ADD_S
                        //The sync ProgressBar will always display,Even if sync timeout,and we must info syncManager to cancel the timeout sync.
                        //It's better to cancel timeOut sync,cause if user always refresh folderlist,may cause last's of syncRequest panding in SyncManager.
                        //The bad result is that it some syncing is onging and cost long time,the other syncing always quere here.
                        ContentResolver.cancelSync(account,EmailContent.AUTHORITY);
                        // TS: chaozhang 2015-06-23 EMAIL BUGFIX-1006486 ADD_E
                    } else {
                        LogUtils.d(TAG, "test----RefreshStatusMonitor onTimeout account is null");
                    }
                    // TS: zheng.zou 2015-05-26 EMAIL add-log-e-1009974
                }
            });
            // TS: chao.zhang 2015-09-24 EMAIL FEATURE-585337 ADD_S
            //NOTE: give the chance to restore the messages status to default when refresh OUBOX.
            Context context = getContext();
            if (mailbox.mType == Mailbox.TYPE_OUTBOX) {
                NotificationController.getInstance(getContext()).cancelFailStatusNotification(mailbox.mAccountKey);
                restoreMailStatus(context, mailbox.mId);
            }
            // TS: chao.zhang 2015-09-24 EMAIL FEATURE-585337 ADD_E
            // TS: chao.zhang 2016-03-19 EMAIL BUGFIX-1739457 ADD_S
            if (mailbox.mType == Mailbox.TYPE_OUTBOX) {
                Account account = Account.restoreAccountWithId(context, mailbox.mAccountKey);
                if(account ==null) {
                    LogUtils.i(LogUtils.TAG, "account null when requestSync,maybe account removed");
                    return null;
                }
                String protocol = account.getProtocol(context);
                if (TextUtils.equals(protocol, "pop3")) {
                    boolean isMsgSending = haveMessageSending(context, mailbox.mId);
                    if (isMsgSending) {
                        LogUtils.i(LogUtils.TAG, "message is sending, just remove new coming refresh");
                        return null;
                    }
                }
            }
            // TS: chao.zhang 2016-03-19 EMAIL BUGFIX-1739457 ADD_E
            startSync(mailbox, deltaMessageCount);
        }
        return null;
    }

    // TS: chao.zhang 2016-03-19 EMAIL BUGFIX-1739457 ADD_S
    /**
     * query the mailbox(just OUTBOX) is have mail sending.
     * @param context
     * @param outboxId
     * @return
     */
    private static boolean haveMessageSending(Context context, long outboxId) {
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(
                    EmailContent.Message.CONTENT_URI,
                    EmailContent.Message.ID_COLUMN_WITH_STATUS_PROJECTION,
                    MessageColumns.SENDING_STATUS
                            + "=?" + " AND " + MessageColumns.MAILBOX_KEY + "=?",
                    new String[] {
                            Long.toString(Message.MAIL_IN_SENDING_STATUS),
                            Long.toString(outboxId)
                    }, null);
            boolean messageSending = cursor != null && cursor.getCount() > 0;
            LogUtils.i(LogUtils.TAG, "Good, it have message sending in outbox--"+messageSending +", and oubox is "+outboxId);
            return messageSending;
        } catch (Exception e) {
            LogUtils.e(LogUtils.TAG, e, "Exception happen during queue the outbox sending mails");
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return false;
    }
    // TS: chao.zhang 2016-03-19 EMAIL BUGFIX-1739457 ADD_E

    //Number of additional messages to load when a user selects "Load more..." in POP/IMAP boxes
    public static final int VISIBLE_LIMIT_INCREMENT = 10;
    //Number of additional messages to load when a user selects "Load more..." in a search
    public static final int SEARCH_MORE_INCREMENT = 10;

    private Cursor uiFolderLoadMore(final Mailbox mailbox) {
        if (mailbox == null) return null;
        if (mailbox.mType == Mailbox.TYPE_SEARCH) {
            // Ask for 10 more messages
            mSearchParams.mOffset += SEARCH_MORE_INCREMENT;
            runSearchQuery(getContext(), mailbox.mAccountKey, mailbox.mId);
        } else {
            uiFolderRefresh(mailbox, VISIBLE_LIMIT_INCREMENT);
        }
        return null;
    }

    private static final String SEARCH_MAILBOX_SERVER_ID = "__search_mailbox__";
    private SearchParams mSearchParams;

    /**
     * Returns the search mailbox for the specified account, creating one if necessary
     * @return the search mailbox for the passed in account
     */
    private Mailbox getSearchMailbox(long accountId) {
        Context context = getContext();
        Mailbox m = Mailbox.restoreMailboxOfType(context, accountId, Mailbox.TYPE_SEARCH);
        if (m == null) {
            m = new Mailbox();
            m.mAccountKey = accountId;
            m.mServerId = SEARCH_MAILBOX_SERVER_ID;
            m.mFlagVisible = false;
            m.mDisplayName = SEARCH_MAILBOX_SERVER_ID;
            m.mSyncInterval = 0;
            m.mType = Mailbox.TYPE_SEARCH;
            m.mFlags = Mailbox.FLAG_HOLDS_MAIL;
            m.mParentKey = Mailbox.NO_MAILBOX;
            m.save(context);
        }
        return m;
    }

    private void runSearchQuery(final Context context, final long accountId,
            final long searchMailboxId) {
        LogUtils.d(TAG, "runSearchQuery. account: %d mailbox id: %d",
                accountId, searchMailboxId);

        // Start the search running in the background
        new AsyncTask<Void, Void, Void>() {
            @Override
            public Void doInBackground(Void... params) {
                final EmailServiceProxy service =
                        EmailServiceUtils.getServiceForAccount(context, accountId);
                if (service != null) {
//                    try {
//                        //[FEATURE]-Mod-BEGIN by CDTS.zhonghua.tuo,05/21/2014,FR 670064
//                        int totalCount = 0;
//                        //[BUGFIX]-Add-BEGIN by TSNJ Zhenhua.Fan,23/12/2014,PR 871385 and BUGFIX-883410
//                        final ContentValues statusValues = new ContentValues(1);
//                        final Mailbox searchMailbox = Mailbox.restoreMailboxWithId(context, searchMailboxId);
//                        statusValues.put(Mailbox.UI_SYNC_STATUS, com.tct.mail.utils.UIProvider.SyncStatus.LIVE_QUERY);
//                        searchMailbox.update(context, statusValues);
//                        //[BUGFIX]-Add-END by TSNJ Zhenhua.Fan
//                                if(ActionBarController.SERVICE_SEARCH_MODE) {
//                                    totalCount = service.searchMessages(accountId, mSearchParams, searchMailboxId);
//                                }else {
//                                    totalCount = searchLocalMessages(context,accountId, mSearchParams.mFilter,
//                                            mSearchParams.mMailboxId,ConversationListFragment.localSearchField,searchMailboxId);
//                                }
//                              //[FEATURE]-Mod-END by CDTS.zhonghua.tuo
//                        // Save away the total count
//                        final ContentValues cv = new ContentValues(1);
//                        cv.put(MailboxColumns.TOTAL_COUNT, totalCount);
//                        update(ContentUris.withAppendedId(Mailbox.CONTENT_URI, searchMailboxId), cv,
//                                null, null);
//                        LogUtils.d(TAG, "EmailProvider#runSearchQuery. TotalCount to UI: %d",
//                                totalCount);
//                    } catch (RemoteException e) {
//                        LogUtils.e("searchMessages", "RemoteException", e);
//                    }catch (MessagingException e) {//[FEATURE]-Add-BEGIN by CDTS.zhonghua.tuo,05/21/2014,FR 670064
//                        LogUtils.e("searchMessages", "MessagingException", e);
//                    }//[FEATURE]-Add-END by CDTS.zhonghua.tuo

                    try {
                        final int totalCount =
                                service.searchMessages(accountId, mSearchParams, searchMailboxId);

                        // Save away the total count
                        /**
                         * TCT: Internal exchange server issue: After get 100 search result,
                         * the internal exchange server would not return total count and search result.
                         * And then the exchange search service would return 0 as search TotalCount.
                         */
                        if (mSearchParams.mOffset == 0 || totalCount > 0) {
                            final ContentValues cv = new ContentValues(1);
                            cv.put(MailboxColumns.TOTAL_COUNT, totalCount);
                            update(ContentUris.withAppendedId(Mailbox.CONTENT_URI, searchMailboxId),
                                    cv, null, null);
                            LogUtils.d(TAG, "EmailProvider#runSearchQuery. TotalCount to UI: %d",
                                    totalCount);
                        } else {
                            LogUtils.d(TAG, "EmailProvider#runSearchQuery. no need update UI. ");
                        }
                    } catch (RemoteException e) {
                        LogUtils.e("searchMessages", "RemoteException", e);
                    }
                }
                return null;
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
    //[FEATURE]-Add-BEGIN by CDTS.zhonghua.tuo,05/21/2014,FR 670064
    private static int searchLocalMessages(final Context context,final long accountId,
        final String query, final long mailboxId,final Integer searchFiedId,final long destMailboxId) throws MessagingException{
        final Account account = Account.restoreAccountWithId(context, accountId);
        final Mailbox mailbox = Mailbox.restoreMailboxWithId(context, mailboxId);
        final    ArrayList<Long> msgIds=new ArrayList<Long>();
        final Mailbox destMailbox = Mailbox.restoreMailboxWithId(context, destMailboxId);
        if (account == null || mailbox == null || destMailbox == null) {
            LogUtils.d(Logging.LOG_TAG, "Attempted search for "
            + " but account or mailbox information was missing");
            return 0;
        }
        // Tell UI that we're loading messages
        final ContentValues statusValues = new ContentValues(2);
        // TS: xiaolin.li 2015-01-13 EMAIL BUGFIX-883410
        statusValues.put(Mailbox.UI_SYNC_STATUS, com.tct.mail.utils.UIProvider.SyncStatus.LIVE_QUERY);
        destMailbox.update(context, statusValues);
        String selection = EmailContent.Message.buildLocalSearchSelection(context,accountId, mailboxId, query, searchFiedId);
        Cursor cursor =context.getContentResolver().query(Message.CONTENT_URI,EmailContent.Message.CONTENT_PROJECTION,
        selection,null,null);
        try{
            while(cursor.moveToNext()){
                long msgId = cursor.getInt(0);
                msgIds.add(msgId);
            }
        } finally{
            if(cursor!=null){
                cursor.close();
            }
        }
        for(int i=0;i<msgIds.size();i++){
            EmailContent.Message localMessage = EmailContent.Message.restoreMessageWithId(context,msgIds.get(i));
            com.tct.emailcommon.mail.Message msg = LegacyConversions.makeMessage(context,localMessage);
            Utilities.copyOneMessageToProvider(context, msg, account, destMailbox,
            EmailContent.Message.FLAG_LOADED_COMPLETE);
        }
        // Tell UI that we're done loading messages
        statusValues.put(Mailbox.SYNC_TIME, System.currentTimeMillis());
        // TS: xiaolin.li 2015-01-13 EMAIL BUGFIX-883410
        statusValues.put(Mailbox.UI_SYNC_STATUS, com.tct.mail.utils.UIProvider.SyncStatus.NO_SYNC);
        destMailbox.update(context, statusValues);
        return msgIds.size();
    }
    //[FEATURE]-Add-END by CDTS.zhonghua.tuo

    // This handles an initial search query. More results are loaded using uiFolderLoadMore.
    private Cursor uiSearch(Uri uri, String[] projection) {
        LogUtils.logFeature(LogTag.SEARCH_TAG, "runSearchQuery in search %s", uri);
        final long accountId = Long.parseLong(uri.getLastPathSegment());

        // TODO: Check the actual mailbox
        Mailbox inbox = Mailbox.restoreMailboxOfType(getContext(), accountId, Mailbox.TYPE_INBOX);
        if (inbox == null) {
            LogUtils.logFeature(LogTag.SEARCH_TAG, "In uiSearch, inbox doesn't exist for account "
                    + accountId);

            return null;
        }

        String filter = uri.getQueryParameter(UIProvider.SearchQueryParameters.QUERY);
        if (filter == null) {
            throw new IllegalArgumentException("No query parameter in search query");
        }

        /// TCT: add search field for remote search, if not set search field, search all field. @{
        String field = uri.getQueryParameter(SearchParams.BUNDLE_QUERY_FIELD);
        if (TextUtils.isEmpty(field)) {
            LogUtils.logFeature(LogTag.SEARCH_TAG, "Not set search field, will search all fields");
            field = SearchParams.SEARCH_FIELD_ALL;
        }
        /// @}

        /// TCT: Delete the previous remote search box. The previous search is meaningless and
        /// should be discarded while we start a new remote search.
        delete(Mailbox.CONTENT_URI, Mailbox.TYPE + "=" + Mailbox.TYPE_SEARCH
                + " and " + MailboxColumns.ACCOUNT_KEY + "=" + accountId, null);

        // Find/create our search mailbox
        Mailbox searchMailbox = getSearchMailbox(accountId);
        final long searchMailboxId = searchMailbox.mId;

        mSearchParams = new SearchParams(inbox.mId, filter,field, searchMailboxId);

        final Context context = getContext();
        if (mSearchParams.mOffset == 0) {
            // TODO: This conditional is unnecessary, just two lines earlier we created
            // mSearchParams using a constructor that never sets mOffset.
            LogUtils.d(TAG, "deleting existing search results.");

            // Delete existing contents of search mailbox
            ContentResolver resolver = context.getContentResolver();
            resolver.delete(Message.CONTENT_URI, MessageColumns.MAILBOX_KEY + "=" + searchMailboxId,
                    null);
            final ContentValues cv = new ContentValues(1);
            // For now, use the actual query as the name of the mailbox
            cv.put(Mailbox.DISPLAY_NAME, mSearchParams.mFilter);
            //TS: zheng.zou 2015-03-18 EMAIL BUGFIX_744708 ADD_S
            cv.put(Mailbox.UI_LAST_SYNC_RESULT, UIProvider.LastSyncResult.SUCCESS);
            //TS: zheng.zou 2015-03-18 EMAIL BUGFIX_744708 ADD_E
            resolver.update(ContentUris.withAppendedId(Mailbox.CONTENT_URI, searchMailboxId),
                    cv, null, null);
        }

        // Start the search running in the background
        runSearchQuery(context, accountId, searchMailboxId);

        // This will look just like a "normal" folder
        return uiQuery(UI_FOLDER, ContentUris.withAppendedId(Mailbox.CONTENT_URI,
                searchMailbox.mId), projection, false);
    }

    /**
     *
     * TCT: support local search.
     */
    private Cursor uiLocalSearch(Uri uri, String[] uiProjection) {
        LogUtils.logFeature(LogTag.SEARCH_TAG, ">> uiLocalSearch uri %s", uri);
        Context context = getContext();
        SQLiteDatabase db = getDatabase(context);
        // Should we ever return null, or throw an exception??
        Cursor c = null;
        String id = uri.getPathSegments().get(1);
        Uri notifyUri = null;

        final String seenParam = uri.getQueryParameter(UIProvider.SEEN_QUERY_PARAMETER);
        final boolean unseenOnly =
                seenParam != null && Boolean.FALSE.toString().equals(seenParam);

        // Check input params validation.
        final String quertTerm = uri.getQueryParameter(SearchParams.BUNDLE_QUERY_TERM);
        final String quertField = uri.getQueryParameter(SearchParams.BUNDLE_QUERY_FIELD);
        if (TextUtils.isEmpty(quertTerm) || TextUtils.isEmpty(quertField)) {
            LogUtils.logFeature(
                    LogTag.SEARCH_TAG,
                    "search failed,return empty for input query [%s] or filed [%s] ",
                    quertTerm, quertField);
            return new MatrixCursor(uiProjection);
        }

        // Check mailbox.
        long mailboxId = Long.parseLong(id);
        final Folder folder = getFolder(context, mailboxId);
        if (folder == null) {
            LogUtils.logFeature(
                    LogTag.SEARCH_TAG,
                    "search failed,return empty for can not get folder from id [%s]",
                    mailboxId);
            return new MatrixCursor(uiProjection);
        }

        // build local search selection.
        String selection = Message.buildLocalSearchSelection(context, mailboxId, quertTerm, quertField);
        //TS: junwei-xu 2015-09-02 EMAIL BUGFIX-546917 ADD-S
        //Note: add select for star toggle status
        String queryFavorite = uri.getQueryParameter(UIProvider.FLAG_FAVORITE_QUERY_PARAMETER);
        boolean addSelection = Boolean.TRUE.toString().equals(queryFavorite);
        if (addSelection) {
            selection += " AND " + MessageColumns.FLAG_FAVORITE + "=1 ";
        }
        //TS: junwei-xu 2015-09-02 EMAIL BUGFIX-546917 ADD-E

        if (null == selection) {
            LogUtils.logFeature(LogTag.SEARCH_TAG, "Local Search Selection is null, return an empty result");
            return new MatrixCursor(uiProjection);
        }
        LogUtils.logFeature(LogTag.SEARCH_TAG, ">> uiLocalSearch build selection [%s] ", selection);

        if (isVirtualMailbox(mailboxId)) {
            //TS: zheng.zou 2016-1-14 EMAIL TASK_1431225 MOD_S
            c = getVirtualMailboxMessagesCursor(db, uiProjection, mailboxId,
                    unseenOnly, selection,SortHelper.DEFAUT_ORDER_SQL);
        } else {
            c = db.rawQuery(
                    genQueryMailboxMessages(uiProjection, unseenOnly, selection,SortHelper.DEFAUT_ORDER_SQL),
                    new String[]{id});
            //TS: zheng.zou 2016-1-14 EMAIL TASK_1431225 MOD_E
        }
        c = new EmailConversationCursor(context, c, folder, mailboxId);

        // generates notify uri.
        notifyUri = UIPROVIDER_CONVERSATION_NOTIFIER.buildUpon()
                .appendPath(id).build();
        // set notify uri for local search, we need redo local search query when messages changed.
        if (notifyUri != null) {
            c.setNotificationUri(context.getContentResolver(), notifyUri);
        }
        return c;
    }

    private static final String MAILBOXES_FOR_ACCOUNT_SELECTION = MailboxColumns.ACCOUNT_KEY + "=?";
    //TS: junwei-xu 2015-12-02 EMAIL BUGFIX-928059  ADD-S
    private static final String RECIPIENTS_FOR_ACCOUNT_SELECTION = EmailContent.RecipientColumns.ACCOUNT_KEY + "=?";
    //TS: junwei-xu 2015-12-02 EMAIL BUGFIX-928059  ADD-E

    /**
     * Delete an account and clean it up
     */
    private int uiDeleteAccount(Uri uri) {
        Context context = getContext();
        long accountId = Long.parseLong(uri.getLastPathSegment());
        try {
            // Get the account URI.
            final Account account = Account.restoreAccountWithId(context, accountId);
            if (account == null) {
                return 0; // Already deleted?
            }

            deleteAccountData(context, accountId);

            // Now delete the account itself
            uri = ContentUris.withAppendedId(Account.CONTENT_URI, accountId);
            context.getContentResolver().delete(uri, null, null);

            // Clean up
            AccountBackupRestore.backup(context);
            SecurityPolicy.getInstance(context).reducePolicies();
            MailActivityEmail.setServicesEnabledSync(context);
            //TS: jin.dong 2015-10-29 EMAIL BUGFIX-839306  ADD-S
            NotificationController.getInstance(context).cancelFailStatusNotification(accountId);
            //TS: jin.dong 2015-10-29 EMAIL BUGFIX-839306  ADD-E
            //TS: zheng.zou 2015-10-28 EMAIL BUGFIX_1102048 ADD_S
            // Cancel all notifications for this account
            NotificationController.cancelNotifications(context, account);
            //TS: zheng.zou 2015-10-28 EMAIL BUGFIX_1102048 ADD_E
            //TS: yanhua.chen 2015-1-20 EMAIL BUGFIX_1162996 ADD_S
            NotificationController.getInstance(context).cancelCalendarOrContactsNotification();
            //TS: yanhua.chen 2015-1-20 EMAIL BUGFIX_1162996 ADD_E
            // TODO: We ought to reconcile accounts here, but some callers do this in a loop,
            // which would be a problem when the first account reconciliation shuts us down.
            return 1;
        } catch (Exception e) {
            LogUtils.w(Logging.LOG_TAG, "Exception while deleting account", e);
        }
        return 0;
    }

    private int uiDeleteAccountData(Uri uri) {
        Context context = getContext();
        long accountId = Long.parseLong(uri.getLastPathSegment());
        // Get the account URI.
        final Account account = Account.restoreAccountWithId(context, accountId);
        if (account == null) {
            return 0; // Already deleted?
        }
        deleteAccountData(context, accountId);
        return 1;
    }

    /**
     * The method will no longer be needed after platform L releases. As emails are received from
     * various protocols the email addresses are decoded and intended to be stored in the database
     * in decoded form. The problem is that Exchange is a separate .apk and the old Exchange .apk
     * still attempts to store <strong>encoded</strong> email addresses. So, we decode here at the
     * Provider before writing to the database to ensure the addresses are written in decoded form.
     *
     * @param values the values to be written into the Message table
     */
    private static void decodeEmailAddresses(ContentValues values) {
        if (values.containsKey(Message.MessageColumns.TO_LIST)) {
            final String to = values.getAsString(Message.MessageColumns.TO_LIST);
            values.put(Message.MessageColumns.TO_LIST, Address.fromHeaderToString(to));
        }

        if (values.containsKey(Message.MessageColumns.FROM_LIST)) {
            final String from = values.getAsString(Message.MessageColumns.FROM_LIST);
            values.put(Message.MessageColumns.FROM_LIST, Address.fromHeaderToString(from));
        }

        if (values.containsKey(Message.MessageColumns.CC_LIST)) {
            final String cc = values.getAsString(Message.MessageColumns.CC_LIST);
            values.put(Message.MessageColumns.CC_LIST, Address.fromHeaderToString(cc));
        }

        if (values.containsKey(Message.MessageColumns.BCC_LIST)) {
            final String bcc = values.getAsString(Message.MessageColumns.BCC_LIST);
            values.put(Message.MessageColumns.BCC_LIST, Address.fromHeaderToString(bcc));
        }

        if (values.containsKey(Message.MessageColumns.REPLY_TO_LIST)) {
            final String replyTo = values.getAsString(Message.MessageColumns.REPLY_TO_LIST);
            values.put(Message.MessageColumns.REPLY_TO_LIST,
                    Address.fromHeaderToString(replyTo));
        }
    }

    /** Projection used for getting email address for an account. */
    private static final String[] ACCOUNT_EMAIL_PROJECTION = { AccountColumns.EMAIL_ADDRESS };

    private static void deleteAccountData(Context context, long accountId) {
        // We will delete PIM data, but by the time the asynchronous call to do that happens,
        // the account may have been deleted from the DB. Therefore we have to get the email
        // address now and send that, rather than the account id.
        final String emailAddress = Utility.getFirstRowString(context, Account.CONTENT_URI,
                ACCOUNT_EMAIL_PROJECTION, Account.ID_SELECTION,
                new String[] {Long.toString(accountId)}, null, 0);
        if (emailAddress == null) {
            LogUtils.e(TAG, "Could not find email address for account %d", accountId);
        }

        // Delete synced attachments
        AttachmentUtilities.deleteAllAccountAttachmentFiles(context, accountId);

        // Delete all mailboxes.
        ContentResolver resolver = context.getContentResolver();
        String[] accountIdArgs = new String[] { Long.toString(accountId) };
        resolver.delete(Mailbox.CONTENT_URI, MAILBOXES_FOR_ACCOUNT_SELECTION, accountIdArgs);

        //TS: junwei-xu 2015-12-02 EMAIL BUGFIX-928059  ADD-S
        // Delete all recipients
        resolver.delete(EmailContent.Recipient.CONTENT_URI, RECIPIENTS_FOR_ACCOUNT_SELECTION, accountIdArgs);
        //TS: junwei-xu 2015-12-02 EMAIL BUGFIX-928059  ADD-E

        // Delete account sync key.
        final ContentValues cv = new ContentValues();
        cv.putNull(AccountColumns.SYNC_KEY);
        resolver.update(Account.CONTENT_URI, cv, Account.ID_SELECTION, accountIdArgs);

        // Delete PIM data (contacts, calendar), stop syncs, etc. if applicable
        if (emailAddress != null) {
            final IEmailService service =
                    EmailServiceUtils.getServiceForAccount(context, accountId);
            if (service != null) {
                try {
                    service.deleteExternalAccountPIMData(emailAddress);
                } catch (final RemoteException e) {
                    // Can't do anything about this
                }
            }
        }
    }

    private int[] mSavedWidgetIds = new int[0];
    private final ArrayList<Long> mWidgetNotifyMailboxes = new ArrayList<Long>();
    private AppWidgetManager mAppWidgetManager;
    private ComponentName mEmailComponent;

    private void notifyWidgets(long mailboxId) {
        Context context = getContext();
        // Lazily initialize these
        if (mAppWidgetManager == null) {
            mAppWidgetManager = AppWidgetManager.getInstance(context);
            mEmailComponent = new ComponentName(context, WidgetProvider.getProviderName(context));
        }

        // See if we have to populate our array of mailboxes used in widgets
        int[] widgetIds = mAppWidgetManager.getAppWidgetIds(mEmailComponent);
        if (!Arrays.equals(widgetIds, mSavedWidgetIds)) {
            mSavedWidgetIds = widgetIds;
            String[][] widgetInfos = BaseWidgetProvider.getWidgetInfo(context, widgetIds);
            // widgetInfo now has pairs of account uri/folder uri
            mWidgetNotifyMailboxes.clear();
            for (String[] widgetInfo: widgetInfos) {
                try {
                    if (widgetInfo == null || TextUtils.isEmpty(widgetInfo[1])) continue;
                    long id = Long.parseLong(Uri.parse(widgetInfo[1]).getLastPathSegment());
                    if (!isCombinedMailbox(id)) {
                        // For a regular mailbox, just add it to the list
                        if (!mWidgetNotifyMailboxes.contains(id)) {
                            mWidgetNotifyMailboxes.add(id);
                        }
                    } else {
                        switch (getVirtualMailboxType(id)) {
                            // We only handle the combined inbox in widgets
                            case Mailbox.TYPE_INBOX:
                                Cursor c = query(Mailbox.CONTENT_URI, Mailbox.ID_PROJECTION,
                                        MailboxColumns.TYPE + "=?",
                                        new String[] {Integer.toString(Mailbox.TYPE_INBOX)}, null);
                                try {
                                    while (c.moveToNext()) {
                                        mWidgetNotifyMailboxes.add(
                                                c.getLong(Mailbox.ID_PROJECTION_COLUMN));
                                    }
                                } finally {
                                    c.close();
                                }
                                break;
                        }
                    }
                } catch (NumberFormatException e) {
                    // Move along
                }
            }
        }

        // If our mailbox needs to be notified, do so...
        if (mWidgetNotifyMailboxes.contains(mailboxId)) {
            Intent intent = new Intent(Utils.ACTION_NOTIFY_DATASET_CHANGED);
            intent.putExtra(Utils.EXTRA_FOLDER_URI, uiUri("uifolder", mailboxId));
            intent.setType(EMAIL_APP_MIME_TYPE);
            context.sendBroadcast(intent);
         }
    }

    @Override
    public void dump(FileDescriptor fd, PrintWriter writer, String[] args) {
        Context context = getContext();
        writer.println("Installed services:");
        for (EmailServiceInfo info: EmailServiceUtils.getServiceInfoList(context)) {
            writer.println("  " + info);
        }
        writer.println();
        writer.println("Accounts: ");
        Cursor cursor = query(Account.CONTENT_URI, Account.CONTENT_PROJECTION, null, null, null);
        if (cursor.getCount() == 0) {
            writer.println("  None");
        }
        try {
            while (cursor.moveToNext()) {
                Account account = new Account();
                account.restore(cursor);
                writer.println("  Account " + account.mDisplayName);
                HostAuth hostAuth =
                        HostAuth.restoreHostAuthWithId(context, account.mHostAuthKeyRecv);
                if (hostAuth != null) {
                    writer.println("    Protocol = " + hostAuth.mProtocol +
                            (TextUtils.isEmpty(account.mProtocolVersion) ? "" : " version " +
                                    account.mProtocolVersion));
                }
            }
        } finally {
            cursor.close();
        }
    }

    synchronized public Handler getDelayedSyncHandler() {
        if (mDelayedSyncHandler == null) {
            mDelayedSyncHandler = new Handler(getContext().getMainLooper(), new Callback() {
                @Override
                public boolean handleMessage(android.os.Message msg) {
                    synchronized (mDelayedSyncRequests) {
                        final SyncRequestMessage request = (SyncRequestMessage) msg.obj;
                        // TODO: It's possible that the account is deleted by the time we get here
                        // It would be nice if we could validate it before trying to sync
                        final android.accounts.Account account = request.mAccount;
                        final Bundle extras = Mailbox.createSyncBundle(request.mMailboxId);
                        ContentResolver.requestSync(account, request.mAuthority, extras);
                        LogUtils.i(TAG, "requestSync getDelayedSyncHandler %s, %s",
                                account.toString(), extras.toString());
                        mDelayedSyncRequests.remove(request);
                        return true;
                    }
                }
            });
        }
        return mDelayedSyncHandler;
    }

    private class SyncRequestMessage {
        private final String mAuthority;
        private final android.accounts.Account mAccount;
        private final long mMailboxId;

        private SyncRequestMessage(final String authority, final android.accounts.Account account,
                final long mailboxId) {
            mAuthority = authority;
            mAccount = account;
            mMailboxId = mailboxId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            SyncRequestMessage that = (SyncRequestMessage) o;

            return mAccount.equals(that.mAccount)
                    && mMailboxId == that.mMailboxId
                    && mAuthority.equals(that.mAuthority);
        }

        @Override
        public int hashCode() {
            int result = mAuthority.hashCode();
            result = 31 * result + mAccount.hashCode();
            result = 31 * result + (int) (mMailboxId ^ (mMailboxId >>> 32));
            return result;
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (PreferenceKeys.REMOVAL_ACTION.equals(key) ||
                PreferenceKeys.CONVERSATION_LIST_SWIPE.equals(key) ||
                PreferenceKeys.SHOW_SENDER_IMAGES.equals(key) ||
                PreferenceKeys.DEFAULT_REPLY_ALL.equals(key) ||
                PreferenceKeys.CONVERSATION_OVERVIEW_MODE.equals(key) ||
                PreferenceKeys.AUTO_ADVANCE_MODE.equals(key) ||
                PreferenceKeys.SNAP_HEADER_MODE.equals(key) ||
                PreferenceKeys.CONFIRM_DELETE.equals(key) ||
                PreferenceKeys.CONFIRM_ARCHIVE.equals(key) ||
                PreferenceKeys.CONFIRM_SEND.equals(key) ||
                Preferences.BCC_MYSELF_KEY.equals(key)) {//TS: zhaotianyong 2014-12-02 EMAIL BUGFIX-ID ADD
            notifyUI(UIPROVIDER_ALL_ACCOUNTS_NOTIFIER, null);
        }
    }

    //TS: junwei-xu 2016-02-18 EMAIL BUGFIX-1539364 ADD_S
    /**
     * Query current all account's unread messages numbers.
     * NOTE that trash box not include in query.
     */
    private Cursor uiCountUnreadMessages() {
        Cursor cursor = getContext().getContentResolver().
                query(Account.CONTENT_URI, new String[] {AccountColumns._ID}, null, null, null);
        try {
            if (cursor != null && cursor.getCount() != 0) {
                //Note:get all trash box id, remove these mail counts when statistics of the total unread numbers
                StringBuffer accountIds = new StringBuffer("(");
                StringBuffer trashBoxIds = new StringBuffer("(");
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    long accountId = cursor.getLong(0);
                    cursor.moveToNext();
                    boolean isAfterLast = cursor.isAfterLast();
                    long trashBoxId = Mailbox.findMailboxOfType(getContext(), accountId, Mailbox.TYPE_TRASH);
                    accountIds.append(accountId);
                    trashBoxIds.append(trashBoxId);
                    if (!isAfterLast) {
                        accountIds.append(",");
                        trashBoxIds.append(",");
                    }
                }
                accountIds.append(")");
                trashBoxIds.append(")");

                //Note: unread count must except unread mails which in Trash box
                return getContext().getContentResolver().query(
                        Message.CONTENT_URI,
                        EmailContent.COUNT_COLUMNS,
                        MessageColumns.FLAG_READ + "=0 AND accountKey in " + accountIds.toString() + " AND mailboxKey NOT IN " + trashBoxIds.toString(),
                        null,
                        null);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }
    //TS: junwei-xu 2016-02-18 EMAIL BUGFIX-1539364 ADD_E

    //PR-897845 by yi-liu@tcl.com begin
    /**
     * TCT: Grant the read permission to the specified application.
     * @param context
     * @param toPackage
     * @param uri
     * @param flag
     */
    private void grantUriPermission(Context context) {
        // grant qsb the message table query permission.
        try {
            context.grantUriPermission(PACKAGE_QUICKSEARCHBOX,
                    EmailContent.Message.CONTENT_URI, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            // grant qsb the body table query permission.
            context.grantUriPermission(PACKAGE_QUICKSEARCHBOX,
                    EmailContent.Body.CONTENT_URI, Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } catch (Exception e) {
            LogUtils.e(TAG,"can not grant uri permission to "+PACKAGE_QUICKSEARCHBOX);
        }
    }

    /**
     * TCT: update virtual mailbox.
     * TODO: try to send different notifications for different operations:
     * Insert/Delete: check message stared true or unread true.
     * Update: update fields contains stared/unread column.
     */
    private void notifiyVirtualMailbox(long accountId) {
        // started folder
        notifiyVirtualMailbox(accountId, Mailbox.TYPE_STARRED);
        // unread folder
        notifiyVirtualMailbox(accountId, Mailbox.TYPE_UNREAD);
    }

    /**
     * TCT: Notify account virtualMailbox.
     * 1. accountId & type.
     * 2. combinedId & type.
     */
    private void notifiyVirtualMailbox(long accountId, int type) {
        LogUtils.d(TAG, "notfiyVirtualMailbox accountId %s, type %s ", accountId, type);

        notifyUI(UIPROVIDER_CONVERSATION_NOTIFIER,
                getVirtualMailboxId(accountId, type));
        notifyUI(UIPROVIDER_CONVERSATION_NOTIFIER,
                getVirtualMailboxId(COMBINED_ACCOUNT_ID, type));
        notifyUI(UIPROVIDER_FOLDER_NOTIFIER,
                getVirtualMailboxId(accountId, type));
    }
    //PR-897845 by yi-liu@tcl end
    // TS: chao.zhang 2015-09-21 EMAIL FEATURE-585337 ADD_S
    /**
     * Only can be handled by MailIntentService,and then
     * NotificationController#showMailSendFaildNotification to show notification.
     * @param context
     * @param accountId the current account which failed
     * @param failedCount the count can be show in notification
     */
    private static void createFailedNotificationsIntent(Context context,long accountId,int failedCount) {
        final Intent intent = new Intent(MailIntentService.ACTION_SEND__MAIL_FAILED_NOTIFICATIONS);
        intent.setPackage(context.getPackageName()); // Make sure we only deliver this to ourselves
        intent.putExtra(NotificationUtils.EXTRA_FAILED_COUNT, failedCount);
        intent.putExtra(NotificationUtils.EXTRA_ACCOUNT_ID, accountId);
        //Just start IntentService,after finished,the service will destory.
        context.startService(intent);
    }
    // TS: chao.zhang 2015-09-21 EMAIL FEATURE-585337 ADD_E

    //TS: yanhua.chen 2015-1-20 EMAIL BUGFIX_1162996 ADD_S
    /**
     * Only can be handled by MailIntentService,and then
     * NotificationController#showCalendarNotification to show notification.
     * @param context
     * @param packageName the show notification packageName
     */
    private static void showCalendarNotification(Context context,String packageName){
        final Intent intent = new Intent(MailIntentService.ACTION_SEND_CALENDAR_NOTIFICATIONS);
        intent.setPackage(context.getPackageName());
        intent.putExtra(PermissionUtil.EXTRA_PACKAGENAME,packageName);
        context.startService(intent);
    }

    /**
     * Only can be handled by MailIntentService,and then
     * NotificationController#showContactsNotification to show notification.
     * @param context
     * @param packageName the show notification packageName
     */
    private static void showContactsNotification(Context context,String packageName){
        final Intent intent = new Intent(MailIntentService.ACTION_SEND_CONTACTS_NOTIFICATIONS);
        intent.setPackage(context.getPackageName());
        intent.putExtra(PermissionUtil.EXTRA_PACKAGENAME,packageName);
        context.startService(intent);
    }
    //TS: yanhua.chen 2015-1-20 EMAIL BUGFIX_1162996 ADD_E

    //TS: zheng.zou 2016-1-22 EMAIL BUGFIX-1431088 ADD_S
    /**
     * Only can be handled by MailIntentService,and then
     * NotificationController#showContactsNotification to show notification.
     * @param context
     * @param packageName the show notification packageName
     */
    private static void showStorageNotification(Context context,String packageName){
        final Intent intent = new Intent(MailIntentService.ACTION_SEND_STORAGE_NOTIFICATIONS);
        intent.setPackage(context.getPackageName());
        intent.putExtra(PermissionUtil.EXTRA_PACKAGENAME,packageName);
        context.startService(intent);
    }
    //TS: zheng.zou 2016-1-22 EMAIL BUGFIX-1431088 ADD_E
}
