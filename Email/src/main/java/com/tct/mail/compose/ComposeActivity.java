/**
 * Copyright (c) 2011, Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
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
/* 06/09/2014|     Zhenhua Fan      |      FR 622609       |[Orange][Android  */
/*           |                      |                      |guidelines]IMAP   */
/*           |                      |                      |support           */
/* ----------|----------------------|----------------------|----------------- */
/* 04/22/2014|     Chao Zhang       |      FR 631895        |bcc and auto dow- */
/*           |                      |porting from  FR487417|nload remaining   */
/* ----------|----------------------|----------------------|----------------- */
/* 04/17/2013|     Chao Zhang       |      FR 631895        |[HOMO][HOMO][Ora- */
/*           |                      |porting from FR514398 |nge][Homologatio- */
/*           |                      |                      |n] Exchange Acti- */
/*           |                      |                      |ve Sync Priority  */
/* ----------|----------------------|----------------------|----------------- */
/* 11/25/2014|       Hu Yang        |      PR-840141       |No respond click send */
/* ----------|----------------------|----------------------|----------------- */
/******************************************************************************/
/*
 ========================================================================================================
 *HISTORY
 *
 *Tag            Date         Author          Description
 *============== ============ =============== ===========================================================
 *CONFLICT-20001 2014/10/24   wenggangjin     Modify the package conflict
 *CONFLICT-20002 2014/10/24   wenggangjin     Modify the package conflict
 *CONFLICT-50012 2014/10/24   zhaotianyong    Modify the package conflict
 *BUGFIX-855270  2014/12/02   zhaotianyong    [Android5.0][Email] No Always bcc myself option in settings
 *BUGFIX-862341  2014/12/11   zhaotianyong    [Android5.0][Email] No Contacts entrance when creating a new mail
 *BUGFIX-881538  2015/01/03   chenyanhua      [Clone][Email]Replaced by another sender,have two bbc
 *BUGFIX-879794  2015/01/05   chenyanhua      [Email]Recipient smart add display error
 *BUGFIX-886455  2015/01/07   chenyanhua      [Android5.0][Email] Lost Bcc info when replace Reply with Reply all
 *BUGFIX-882161  2015-01-06   wenggangjin     [Email]Can't reply/forword/reply all some advertisement mail
 *BUGFIX-893877  2015/01/08   xiaolin.li      [Stability][Email] Cannot Send Email with monkeyrunner
 *BUGFIX-890424  2015/01/12   chenyanhua      [Android5.0][Email][Monitor]Display Sender as 'Combined view' when forward a mail
 *BUGFIX-886976  2015/01/26   xinlei.sheng    Pop up toast "Message saved as draft" after click contact icon after "TO"
 *BUGFIX-917401  2015/01/29   xiaolin.li      [Android5.0][Email][Monkey][Crash] com.tct.email crash caused by android.content.ActivityNotFoundException
 *BUGFIX-874020  2015/2/3     junwei-xu       [Android5.0][Email] Reply/Replay all/Forward does not work in landscape mode
 *BUGFIX-920087  2015-02-09   wenggangjin     [Clone][5][Email]DRM files can be added as an attachment to the message
 *BUGFIX-932308  2015/02/015   chenyanhua     [REG][Email]Priority always display previous select one
 *BUGFIX-930453  2015/02/15   peng-zhang      [Monkey][Crash]com.tct.email java.lang.IllegalStateException.
 *BUGFIX-933509  2015-02-16   zheng.zou       [REG][FC][Camera]History dimensional code sharing to email, email discovery prompted FC
 *BUGFIX-935421  2015-02-28   wenggangjin     [REG][Force close][Email]Email force close when add file attachment from Drive
 *BUGFIX-926303  2015/02/28   zhaotianyong    [ANR][Monitor][Email]Happen email ANR when add contact.
 *BUGFIX-927727  2015/03/02   chenyanhua      [REG][Email]No save notification when add recipients from contacts
 *BUGFIX-938039  2015/03/05   zhonghua.tuo    [Android5.0][Exchange] Editor interface will appear black screen after add a large number of recipients 
 *BUGFIX-927828  2015/03/06   gengkexue       [Email]The default account can't be marked
 *BUGFIX-948927  2015/03/16   zheng.zou       [Monitor][Email]Email Force Close after edit draft sometimes
 *BUGFIX-951700  2015/03/17   zhonghua.tuo    [Android5.0][Exchange][Monitor]One email in outbox and always can’t sent out
 *BUGFIX-949589  2015/03/21   zheng.zou       [Email]Share video over 5M by email has some iaaue
 *BUGFIX-957057  2015/03/17   zhonghua.tuo    [Android5.0][Email]We still can forward an email when attachments are not fully downloaded
 *BUGFIX-954496  2015/03/25   zhaotianyong    [Android5.0][Email]"Failed to send mail" not display when no network.
 *BUGFIX-958270  2015/03/25   junwen-xu       [Android5.0][Email]Always CC himself when reply all under combined view.
 *BUGFIX-958270  2015/03/27   peng-zhang      [Android5.0][Email]The address in bcc field will appear again when we remove it and rotate the screen.
 *BUGFIX-963249  2015/03/31   zhaotianyong    [REG][Android5.0][Email]"Failed to send mail" display when save an email as drafts.
 *BUGFIX-968060  2015/04/03   peng-zhang      [REG][Force close][Android5.0][Email]Share files when there is no account,email FC.
 *BUGFIX-963186  2015/4/16    yanhua.chen     Android5.0][Email] [UI] Status bar does not change when selecting characters when editing a mail
 *BUGFIX-978954  2015/04/16   zhaotianyong    [Email]Can forword some unsupport attachfile when it isn't downloaded
 *BUGFIX-974962  2015/04/17   gangjin.weng    Default account option not working at Email settings.
 *BUGFIX_980239  2015/04/23   junwei-xu       [REG][Email]"From" account is defult account when tap "add" button on another account widget
 *BUGFIX-989399  2015/4/30    yanhua.chen     [Android5.0][Email]The step of adding attach file is repetitive
 *BUGFIX-991264  2015/05/06   zhaotianyong    [Email] Fail to forward mail with inner picture
 *BUGFIX-995343  2015/05/07   zhaotianyong    [Android5.0][Email]Can forward email before download remaining content.
 *BUGFIX-988459  2015/05/08   zhaotianyong    [Email]Mail can not be forwarded if the attachment is not supported
 *BUGFIX-998470  2015/05/11   zhaotianyong    [Android5.0][Email]Update the ergo when reply/reply-all/forward email under header only mode.
 *BUGFIX_1000343 2015/05/13   junwei-xu       [Vodafone][VF13850][2 - Serious][Email]MS_Exchange: Recipients are wrongly shown under "Cc"
 *BUGFIX_998884 2015/05/20    zhaotianyong    [Android5.0][Email][REG] Attachments are lost after switched from reply to forward a mail from Status bar
 *BUGFIX_998884 2015/05/28    zheng.zou       [Email]Attachment auto add more when change between reply/reply all/forward after rotate phone
 *BUGFIX_996919 2015/06/04    zheng.zou       [Email](new) draft auto saving & discard ui change
 *BUGFIX_1006499 2015/06/05   jian.xu         [Email]Display half keyboard on contact match list UI
 *BUGFIX_1009174 2015/06/08   zheng.zou       [Android5.0][Email]The order of To field on Reply All screen is wrong.
 *CR-996908      2015/6/8     yanhua.chen     [Email]attachment size limit unblock with toast
 *BUGFIX_1019278 2015/06/09   Gantao       [Android5.0][Email]Reply myself when on combined view.
 *BUGFIX-1024081 2015/6/15    yanhua.chen     [Android5.0][Email]Change sound when "email sent"
 *BUGFIX_1015669 2015/6/15    jian.xu         [Monitor][Force Close][Email]Happen FC when add picture attachment.
 *BUGFIX_996919 2015/06/04    zheng.zou       [Email](new) draft auto saving & discard ui change
 *BUGFIX_1025192 2015/6/17    jian.xu         [SW][Email]Interface beat when writing email
 *BUGFIX-965608  2015/06/19   junwei-xu       [REG][Android5.0][Email]Priority doesn't saved after edit one draft
 *BUGFIX_1029027 2015/6/24    Gantao            [Android 5.0][Email][REG]Lost To info when change Reply to Reply all
 *BUGFIX_1013067 2015/6/26    xujian          [Android5.0][Email]In landscape the matched email addresses searched by initial search will overlap with status bar when dragging the searched email address
 *BUGFIX-1030195 2015/06/30   junwei-xu       [GAPP][Email]The priority icon disappear after rotate MS
 *BUGFIX-1034971 2015/07/02   junwei-xu       [Android 5.0][Email]Priority icon displayed after changing "Forward" to "Reply/Reply all"
 *BUGFIX-1037000 2015/07/10   xujian          [GAPP][Email]Quick response and signature diaplay connection after rotate MS
 *FEATURE-1033148  2015/7/13  Gantao          [Android5.0][Email]"Add attachment" not display when long touch on attachment icon.s
 *BUGFIX-1029180 2015/07/17   junwei-xu       [Android L][Email] 'Message saved as draft.' toast not pops up after exiting composing mail
 *BUGFIX-1053132 2015/7/29    yanhua.chen     [Android5.0][Email]Toast display abnormally when forward an email more than 20M
 *BUGFIX-1054431 2015/7/30    jin.dong        [Email]The screen will flash unfriendly when delete the attachemnts in forward edit screen.
 *BUGFIX-1063281  2015/8/7   kaifeng.lu       [Android5.0][Email][Monkey][Crash][Monitor]com.tct.email crash:java.lang.IndexOutOfBoundsException
 *BUGFIX-1067957 2015/8/14   kaifeng.lu      [Monitor][Force close][Email]Email force close when reply email from notification
 *BUGFIX-526192  2015-09-01   junwei-xu       From and reply all must be auto display.
 *CR-540045      2015/9/1     yanhua.chen     Compose ui adjustment
 *CD_551912      2015/9/1     yanhua.chen     Compose e-mail Hint
 *BUGFIX_568681  2015/9/11    yanhua.chen     [Android L][Email]"..." appear if user don't have a signature Edit Notification
 *BUGFIX-1047612 2015/07/27   zheng.zou       [Email]Can't display all pictures of the picture attachment after send it.
 *BUGFIX-622679  2015/09/19   junwei-xu       [Android][Email]Not bcc myself when forward an email.
 *BUGFIX_569665  2015/9/19    yanhua.chen     [Android][Email]"..." also display when the signature display
 *BUGFIX_1065369 2015/8/28    lin-zhou        [UE][Email]"There's no text in the message subject" doesn't pop up when there is no suject with attachments attachted
 *BUGFIX_666151  2015/9/28    lin-zhou        [Android L][Email]The signature is displayed after rotating the handset
 *BUGFIX_571435  2015/9/28    jin.dong        [Android L][Email][Force close]Share a file via POP/IMAP account Email force close hanpped
 *BUGFIX_673904  2015/9/30    kaifeng.lu      [Android L][Email]"Message saved as draft" display after adding mail address from contact
 *BUGFIX-708877  2015/07/10   xujian          [Android L][Email]The draft not save when share files as attachment with not change the sender
 *BUGFIX_1085945 2015/9/17    jian.xu         [Monkey][Crash] com.tct.email
 *BUGFIX_718388  2015/10/15   lin-zhou        [Android L][Email][Monitor]Domain disappeared after pop up can not connect server
 *BUGFIX_1100033 2015/10/19   jin.dong        [Android L][Email] [Monkey][Crash]com.tct.email crash by java.lang.NullPointerException
 *BUGFIX-894294  2015/10/28   kaifeng.lu      [Email][VDF]MTK Platform Compatibility：Email compose recipients contacts
 *BUGFIX-858353  2015/11/03   zheng.zou       [Email]Optimize Exchange smart-forward/smart-reply
 *BUGFIX-864427  2015/11/10   junwei-xu       [Android L][Email]Share a file via Email "From" not display current account
 *BUGFIX_861373  2015/11/11   yanhua.chen     [Android 5.0][Email]The signature display as three points display wrong place when respond inline
 *BUGFIX_821332  2015/11/12   jian.xu         [Android L][Email]The priority of draft not change in time
 *BUGFIX-863678  2015/11/13   jian.xu         [Android 5.0][Email]No detail information in foward message when forward the mail
 *TASK-869664    2015/11/30   zheng.zou       [Email]Android M Permission Upgrade
 *BUGFIX-1106881 2015/11/16   jian.xu         [Android L][Email][UE]The cursor back to beginning again after rotating screen when editing mail
 *BUGFIX-1030520 2015/12/02   zheng.zou       [Android 6.0][Email][Force close]Email force close when share contact with no contact permission if no account created
 *BUGFIX-1035228 2015/11/10   junwei-xu       [Android L][Email][SBS]The priority popup will disappear after rotate phone from portrait mode to landscape mode.
 *BUGFIX-1059178  2015/12/09  zheng.zou       [Email]"Save draft" is not gray in menu when compose email from widget.
 *BUGFIX-1181863 2015/12/22   junwei-xu       [Android 6.0][Email]The email addresses are invaild if add from contact on MTK platform
 *BUGFIX-1307962 2016/01/11   jian.xu         [Android 6.0][Email][Force close]Email will force close when share in download without storage and phone/contact permission
 *BUGFIX-1355979 2016/01/13   chao-zhang      [Android M][Email][Force close]Mutiple press UNDO Email force close
 *BUGFIX_1441004 2015/1/19    yang.mei        [Android 6.0][Email]Pop up "Message saved as draft" when add attachment without storage permission
 *BUGFIX-1496954 2016/01/25   junwei-xu       [Android 6.0][Email]contact with special character in name cannot be select
 *BUGFIX-1612750 2016/03/01   junwei-xu       [VF17110][2 - Serious][CTC][Email]'MS_Exchange: Adding contacts from phonebook as email recipients works faulty.
 *BUGFIX-1712549 2016/03/02   rong-tang       [Email][Force Close]Email happens stopped when opening one folder from image
 *BUGFIX-1761777 2016/03/16   tao.gan         [Monkey][ANR][Email]NOT RESPONDING: com.tct.email during system test
 *BUGFIX_1814252 2015/3/16    yanhua.chen     [Email]There is no prompt when share a file exceeds 10M via Email]  Edit Notification
 *BUGFIX-1783199 2016/03/23   xiangnan.zhou   [Email][Broswer]The screen will appears  half black when sharing browser page by email
 *BUGFIX_1841392 2016/3/25    kaifeng.lu      [Email][Ergo]Email actual result not consistent with ergo designed.
 *BUGFIX-1863457 2016/03/28   rong-tang       [Email]The number of receivers is incorrect when reply all.
 *BUGFIX-1877378 2016/03/31   yanhua.chen     [Email]Reply/Reply all/Forward function confusion
 *BUGFIX-1840992 2016/04/06   rong-tang       [Email]The number of receivers is incorrect when reply
 *BUGFIX-1909143 2016/04/06   kaifeng.lu      [Email]DUT prompt two signature when select "Photos".
========================================================================================================
 */
package com.tct.mail.compose;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.LoaderManager;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.content.ActivityNotFoundException;
import android.database.Cursor;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import com.tct.emailcommon.utility.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.ParcelFileDescriptor;
import android.provider.BaseColumns;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.RemoteInput;
import android.support.v7.app.ActionBar;
import android.text.Editable;
import android.text.Html;
import android.text.SpanWatcher;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.util.Rfc822Token;
import android.text.util.Rfc822Tokenizer;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.BaseInputConnection;
import android.view.inputmethod.EditorInfo;
//TS: junwei-xu 2015-2-3 EMAIL BUGFIX_874020 ADD_S
import android.view.inputmethod.InputMethodManager;
//TS: junwei-xu 2015-2-3 EMAIL BUGFIX_874020 ADD_E
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.provider.ContactsContract;
import android.widget.MultiAutoCompleteTextView;
import com.tct.drm.api.TctDrmManager;
import com.tct.email.EmailApplication;
import com.tct.email.Preferences;
//TS: MOD by zhaotianyong for CONFLICT_50012 START
//import com.android.common.Rfc822Validator;
//import com.android.common.contacts.DataUsageStatUpdater;
import com.tct.fw.ex.common.Rfc822Validator;
import com.tct.fw.ex.common.contacts.DataUsageStatUpdater;
//TS: MOD by zhaotianyong for CONFLICT_50012 END
import com.tct.emailcommon.mail.Address;
import com.tct.emailcommon.provider.EmailContent;
import com.tct.emailcommon.provider.EmailContent.Body;
import com.tct.emailcommon.utility.Utility;
//TS: MOD by wenggangjin for CONFLICT_20002 START
//import com.tct.ex.chips.BaseRecipientAdapter;
//import com.tct.ex.chips.DropdownChipLayouter;
//import com.tct.ex.chips.RecipientEditTextView;
import com.tct.fw.ex.chips.BaseRecipientAdapter;
import com.tct.fw.ex.chips.DropdownChipLayouter;
import com.tct.fw.ex.chips.RecipientEditTextView;
//TS: MOD by wenggangjin for CONFLICT_20002 END
import com.tct.email.R;
import com.tct.mail.ui.ActionableToastBar;
import com.tct.mail.ui.ProgressDialogFragment;
import com.tct.mail.ui.ToastBarOperation;
import com.tct.mail.utils.LogTag;
import com.tct.mail.utils.LogUtils;
import com.google.android.mail.common.html.parser.HtmlTree;
//TS: MOD by wenggangjin for CONFLICT_20001 START
//import com.google.common.annotations.VisibleForTesting;
//import com.google.common.collect.Lists;
//import com.google.common.collect.Sets;
import com.tct.fw.google.common.annotations.VisibleForTesting;
import com.tct.fw.google.common.collect.Lists;
import com.tct.mail.MailIntentService;
import com.tct.mail.analytics.Analytics;
import com.tct.mail.browse.ConfirmDialogFragment.ChangeForwardDialogFragment;
import com.tct.mail.browse.ConfirmDialogFragment.ChangeForwardDialogFragment.ChangeForwardCallback;
import com.tct.mail.browse.MessageHeaderView;
import com.tct.mail.compose.AttachmentsView.AttachmentAddedOrDeletedListener;
import com.tct.mail.compose.AttachmentsView.AttachmentFailureException;
import com.tct.mail.compose.FromAddressSpinner.OnAccountChangedListener;
import com.tct.mail.compose.QuotedTextView.RespondInlineListener;
import com.tct.mail.preferences.MailPrefs;
import com.tct.mail.providers.Account;
import com.tct.mail.providers.Attachment;
import com.tct.mail.providers.Folder;
import com.tct.mail.providers.MailAppProvider;
import com.tct.mail.providers.Message;
import com.tct.mail.providers.MessageModification;
import com.tct.mail.providers.ReplyFromAccount;
import com.tct.mail.providers.Settings;
import com.tct.mail.providers.UIProvider;
import com.tct.mail.providers.UIProvider.AccountCapabilities;
import com.tct.mail.providers.UIProvider.DraftType;
import com.tct.mail.ui.MailActivity;
import com.tct.mail.ui.WaitFragment;
import com.tct.mail.ui.AttachmentTile.AttachmentPreview;
import com.tct.mail.utils.AccountUtils;
import com.tct.mail.utils.AttachmentUtils;
import com.tct.mail.utils.ContentProviderTask;
import com.tct.mail.utils.HtmlUtils;
import com.tct.mail.utils.NotificationActionUtils;
import com.tct.mail.utils.PLFUtils;
import com.tct.mail.utils.Utils;
//TS: MOD by wenggangjin for CONFLICT_20001 END
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
//[FEATURE]-Add-BEGIN by  TSCD.chao zhang,04/17/2014,FR 631895(porting from FR514398)
import android.widget.ImageView;
//[FEATURE]-Add-END by TSCD.chao zhang

//[FEATURE]-Add-BEGIN by TSCD.chao zhang,04/21/2014,FR631895(porting from FR487417)
import android.widget.ImageButton;
import android.provider.ContactsContract.Contacts;

import java.util.Iterator;
//[FEATURE]-Add-END by TSCD.chao zhang

//TS: yanhua.chen 2015-4-16 EMAIL BUGFIX_963186 ADD_S
import android.view.Window;
//TS: yanhua.chen 2015-4-16 EMAIL BUGFIX_963186 ADD_E

//TS: jian.xu 2015-06-05 EMAIL BUGFIX-1006499 ADD_S
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.LinearLayout;
import com.tct.permission.BaseActivity;
import com.tct.permission.PermissionUtil;
//TS: jian.xu 2015-06-05 EMAIL BUGFIX-1006499 ADD_E

//[FEATURE]-Add-BEGIN by TSCD.chao zhang,04/22/2014,FR 631895(porting from  FR487417)

public class ComposeActivity extends BaseActivity   //TS: zheng.zou 2015-10-17 EMAIL permission check MOD
        implements OnClickListener, ActionBar.OnNavigationListener,
        RespondInlineListener, TextWatcher,
        AttachmentAddedOrDeletedListener, OnAccountChangedListener,
        LoaderManager.LoaderCallbacks<Cursor>, TextView.OnEditorActionListener,
        RecipientEditTextView.RecipientEntryItemClickedListener, View.OnFocusChangeListener,
        ChangeForwardCallback {//TS: zhaotianyong 2015-05-19 EMAIL BUGFIX_988459 MOD
    /**
     * An {@link Intent} action that launches {@link ComposeActivity}, but is handled as if the
     * {@link Activity} were launched with no special action.
     */
    private static final String ACTION_LAUNCH_COMPOSE =
            "com.tct.mail.intent.action.LAUNCH_COMPOSE";

    // Identifiers for which type of composition this is
    public static final int COMPOSE = -1;
    public static final int REPLY = 0;
    public static final int REPLY_ALL = 1;
    public static final int FORWARD = 2;
    public static final int EDIT_DRAFT = 3;

    // Integer extra holding one of the above compose action
    protected static final String EXTRA_ACTION = "action";

    private static final String EXTRA_SHOW_CC = "showCc";
    private static final String EXTRA_SHOW_BCC = "showBcc";
    private static final String EXTRA_RESPONDED_INLINE = "respondedInline";
    private static final String EXTRA_SAVE_ENABLED = "saveEnabled";

    private static final String UTF8_ENCODING_NAME = "UTF-8";

    private static final String MAIL_TO = "mailto";

    private static final String EXTRA_SUBJECT = "subject";

    private static final String EXTRA_BODY = "body";

    /**
     * Expected to be html formatted text.
     */
    private static final String EXTRA_QUOTED_TEXT = "quotedText";

    protected static final String EXTRA_FROM_ACCOUNT_STRING = "fromAccountString";

    private static final String EXTRA_ATTACHMENT_PREVIEWS = "attachmentPreviews";

    // Extra that we can get passed from other activities
    @VisibleForTesting
    protected static final String EXTRA_TO = "to";
    private static final String EXTRA_CC = "cc";
    private static final String EXTRA_BCC = "bcc";

    /**
     * An optional extra containing a {@link ContentValues} of values to be added to
     * {@link SendOrSaveMessage#mValues}.
     */
    public static final String EXTRA_VALUES = "extra-values";

    // List of all the fields
    static final String[] ALL_EXTRAS = { EXTRA_SUBJECT, EXTRA_BODY, EXTRA_TO, EXTRA_CC, EXTRA_BCC,
            EXTRA_QUOTED_TEXT };

    private static final String LEGACY_WEAR_EXTRA = "com.google.android.wearable.extras";

    /**
     * Constant value for the threshold to use for auto-complete suggestions
     * for the to/cc/bcc fields.
     */
    private static final int COMPLETION_THRESHOLD = 1;

    private static SendOrSaveCallback sTestSendOrSaveCallback = null;
    // Map containing information about requests to create new messages, and the id of the
    // messages that were the result of those requests.
    //
    // This map is used when the activity that initiated the save a of a new message, is killed
    // before the save has completed (and when we know the id of the newly created message).  When
    // a save is completed, the service that is running in the background, will update the map
    //
    // When a new ComposeActivity instance is created, it will attempt to use the information in
    // the previously instantiated map.  If ComposeActivity.onCreate() is called, with a bundle
    // (restoring data from a previous instance), and the map hasn't been created, we will attempt
    // to populate the map with data stored in shared preferences.
    // FIXME: values in this map are never read.
    private static ConcurrentHashMap<Integer, Long> sRequestMessageIdMap = null;
    /**
     * Notifies the {@code Activity} that the caller is an Email
     * {@code Activity}, so that the back behavior may be modified accordingly.
     *
     * @see #onAppUpPressed
     */
    public static final String EXTRA_FROM_EMAIL_TASK = "fromemail";
    //TS: zheng.zou 2015-12-09 EMAIL BUGFIX_1059178 ADD_S
    //notify the Intent is from Widget
    public static final String EXTRA_FROM_EMAIL_WIDGET = "fromwidget";
    //TS: zheng.zou 2015-12-09 EMAIL BUGFIX_1059178 ADD_E

    public static final String EXTRA_ATTACHMENTS = "attachments";

    /** If set, we will clear notifications for this folder. */
    public static final String EXTRA_NOTIFICATION_FOLDER = "extra-notification-folder";
    public static final String EXTRA_NOTIFICATION_CONVERSATION = "extra-notification-conversation";

    //  If this is a reply/forward then this extra will hold the original message
    private static final String EXTRA_IN_REFERENCE_TO_MESSAGE = "in-reference-to-message";
    // If this is a reply/forward then this extra will hold a uri we must query
    // to get the original message.
    protected static final String EXTRA_IN_REFERENCE_TO_MESSAGE_URI = "in-reference-to-message-uri";
    // If this is an action to edit an existing draft message, this extra will hold the
    // draft message
    private static final String ORIGINAL_DRAFT_MESSAGE = "original-draft-message";
    private static final String END_TOKEN = ", ";
    private static final String LOG_TAG = LogTag.getLogTag();
    // Request numbers for activities we start
    private static final int RESULT_PICK_ATTACHMENT = 1;
    private static final int RESULT_CREATE_ACCOUNT = 2;
    //[BUGFIX]-Mod-BEGIN by TSCD.chao zhang,05/29/2014,PR 682725 [Email]Can't add mail address from contact option.
    private static final int ACTIVITY_REQUEST_PICK_CONTACT_TO = 3;
    private static final int ACTIVITY_REQUEST_PICK_CONTACT_CC = 4;
    private static final int ACTIVITY_REQUEST_PICK_CONTACT_BCC = 5;

    //[FEATURE]-Add-BEGIN by TSCD.chao zhang,04/21/2014,FR631895(porting from FR487417)
    private static final int EMAIL_NAME_INDEX = 0;
    private static final int EMAIL_ADDRESS_INDEX = 1;
    private static final char EMAIL_QUOTE_START = '<';
    private static final char EMAIL_QUOTE_END = '>';
    private static final char EMAIL_SPACE = ' ';
    //TS: junwei-xu 2016-03-01 EMAIL BUGFIX-1612750 ADD_S
    private static final String ACTION_MULTI_PICK_EMAIL_QUALCOMM = "com.android.contacts.action.MULTI_PICK_EMAIL";
    private static final String ACTION_MULTI_PICK_EMAIL_MTK = "android.intent.action.contacts.list.PICKMULTIEMAILS";
    //TS: junwei-xu 2016-03-01 EMAIL BUGFIX-1612750 ADD_E
    //TS:kaifeng.lu 2015-10-28 EMAIL BUGFIX_824294 ADD_S
    public static final String EXTRA_PICK_DATA_RESULT = "com.mediatek.contacts.list.pickdataresult";
    //TS:kaifeng.lu 2015-10-28 EMAIL BUGFIX_824294 ADD_E
    private static final String ACTION_SAVE_EMAIL_TO_GROUP = "com.tct.email.MessageCompose.peopleActivity";
    public static final String EXTRA_PICK_EMAIL_BUNDLE = "contacts_extra_pick_email_bundle";
    //[FEATURE]-Add-END by TSCD.chao zhang

    //[FEATURE]-Add-BEGIN by TSCD.chao zhang,04/22/2014,FR 631895(porting from  FR487417)
    private boolean mAddBccBySetting = false;
    //[FEATURE]-Add-END by TSCD.chao zhang

    //[BUGFIX]-Mod-END by TSCD.chao zhang
    // TODO(mindyp) set mime-type for auto send?
    public static final String AUTO_SEND_ACTION = "com.tct.mail.action.AUTO_SEND";

    private static final String EXTRA_SELECTED_REPLY_FROM_ACCOUNT = "replyFromAccount";
    private static final String EXTRA_REQUEST_ID = "requestId";
    private static final String EXTRA_FOCUS_SELECTION_START = "focusSelectionStart";
    private static final String EXTRA_FOCUS_SELECTION_END = "focusSelectionEnd";
    private static final String EXTRA_MESSAGE = "extraMessage";
    private static final int REFERENCE_MESSAGE_LOADER = 0;
    private static final int LOADER_ACCOUNT_CURSOR = 1;
    private static final int INIT_DRAFT_USING_REFERENCE_MESSAGE = 2;
    private static final String EXTRA_SELECTED_ACCOUNT = "selectedAccount";
    private static final String TAG_WAIT = "wait-fragment";
    private static final String MIME_TYPE_ALL = "*/*";
    private static final String MIME_TYPE_PHOTO = "image/*";

    //[FEATURE]-Add-BEGIN by TSNJ,Zhenhua.Fan,06/11/2014,FR-622609
    /** If the intent is sent from draft view screen.  */
    public static final String EXTRA_FROM_DRAFT_VIEW = "from_view";
    //[FEATURE]-Add-END by TSNJ,Zhenhua.Fan
    private static final String KEY_INNER_SAVED_STATE = "compose_state";
    // TS: junwei-xu 2015-06-30 EMAIL BUGFIX-1030195 ADD_S
    private static final String KEY_PRIORITY_SAVED_STATE = "compose_priority_state";
    // TS: junwei-xu 2015-06-30 EMAIL BUGFIX-1030195 ADD_E

    // TS: junwei-xu 2015-07-17 EMAIL BUGFIX-1029180 ADD_S
    private static final String KEY_SAVED_STATE_TEXT_CHANGED = "compose_state_text_changed";
    private static final String KEY_SAVED_STATE_ATTACHMENT_CHANGED = "compose_state_attachment_changed";
    private static final String KEY_SAVED_STATE_REPLY_FROM_CHANGED = "compose_state_reply_from_changed";
    private static final String KEY_SAVED_STATE_PRIORITY_CHANGED = "compose_state_priority_changed";
    // TS: junwei-xu 2015-07-17 EMAIL BUGFIX-1029180 ADD_E
    //TS: yanhua.chen 2015-7-29 EMAIL BUGFIX_1053132 ADD_S
    private static final String KEY_SAVED_STATE_ATTLARGEWARNING_CHANGED = "compose_state_attLargeWarning_changed";
    //TS: yanhua.chen 2015-7-29 EMAIL BUGFIX_1053132 ADD_E
    //TS: yanhua.chen 2015-9-1 EMAIL CD_551912 ADD_S
    private static final String KEY_SAVED_STATE_ISCLICKICON_CHANGED = "compose_state_isClickIcon_changed";
    private static final String KEY_SAVED_STATE_CHANGEACCOUNT_CHANGED = "compose_state_changeAccount_changed";
    private static final String KEY_SAVED_STATE_EDITDRAFT_CHANGED = "compose_state_editDraft_changed";
    //TS: yanhua.chen 2015-9-1 EMAIL CD_551912 ADD_E
    //TS:kaifeng.lu 2015-10-28 EMAIL BUGFIX_824294 ADD_S
    private static final String PLATFORM_QUALCOMM="0";
    private static final String PLATFORM_MTK="1";
    //TS:kaifeng.lu 2015-10-28 EMAIL BUGFIX_824294 ADD_E

    /**
     * A single thread for running tasks in the backgnd.
     */
    private final static Handler SEND_SAVE_TASK_HANDLER;
    static {
        HandlerThread handlerThread = new HandlerThread("Send Message Task Thread");
        handlerThread.start();

        SEND_SAVE_TASK_HANDLER = new Handler(handlerThread.getLooper());
    }
    public static final String DRAFT_SAVED_ACTION = "com.tct.mail.action.DRAFT_SAVED_ACTION";    //TS: zheng.zou 2015-03-18 EMAIL FEATURE_996919 ADD

    private ScrollView mScrollView;
    private RecipientEditTextView mTo;
    private RecipientEditTextView mCc;
    private RecipientEditTextView mBcc;
    //TS: rong-tang 2016-04-06 EMAIL-1840992 ADD_S
    private ImageButton mToPicker;
    private ImageButton mCcPicker;
    private ImageButton mBccPicker;
    //TS: rong-tang 2016-04-06 EMAIL-1840992 ADD_E
    private View mCcBccButton;

    //[BUGFIX]-Add-BEGIN by SCDTABLET.weiwei.huang,05/03/2016,2013739,
    //[Email]Add mail contact icon display is not consistent
    private View bccButtonImg;
    private View ccButtonImg;
    //[BUGFIX]-Add-END by SCDTABLET.weiwei.huang

    private CcBccView mCcBccView;
    private AttachmentsView mAttachmentsView;
    protected Account mAccount;
    protected ReplyFromAccount mReplyFromAccount;
    private Settings mCachedSettings;
    private Rfc822Validator mValidator;
    private TextView mSubject;
    // TS: junwei-xu 2015-09-01 EMAIL BUGFIX-526192 ADD_S
    private LinearLayout mFromRow;
    private boolean mSupportReplyAll = false;
    // TS: junwei-xu 2015-09-01 EMAIL BUGFIX-526192 ADD_E

    private ComposeModeAdapter mComposeModeAdapter;
    protected int mComposeMode = -1;
    private boolean mForward;
    private QuotedTextView mQuotedTextView;
    protected EditText mBodyView;
    protected ImageButton mBodySignature;
    private View mFromStatic;
    private TextView mFromStaticText;
    private View mFromSpinnerWrapper;
    @VisibleForTesting
    protected FromAddressSpinner mFromSpinner;
    protected boolean mAddingAttachment;
    private boolean mAttachmentsChanged;
    private boolean mTextChanged;
    private boolean mReplyFromChanged;
    private MenuItem mSave;
    @VisibleForTesting
    protected Message mRefMessage;
    private long mDraftId = UIProvider.INVALID_MESSAGE_ID;
    private Message mDraft;
    private ReplyFromAccount mDraftAccount;
    private final Object mDraftLock = new Object();

    /**
     * Boolean indicating whether ComposeActivity was launched from a Gmail controlled view.
     */
    private boolean mLaunchedFromEmail = false;
    private RecipientTextWatcher mToListener;
    private RecipientTextWatcher mCcListener;
    private RecipientTextWatcher mBccListener;
    private Uri mRefMessageUri;
    private boolean mShowQuotedText = false;
    protected Bundle mInnerSavedState;
    private ContentValues mExtraValues = null;
    //[FEATURE]-Add-BEGIN by TSCD.chao zhang,04/17/2014,FR 631895(porting from FR514398)
    private boolean mProrityChanged;
    //[FEATURE]-Add-END by TSCD.chao zhang
    //[FEATURE]-Add-BEGIN by TSNJ,Zhenhua.Fan,06/11/2014,FR-622609
    private boolean isFromView;
    //[FEATURE]-Add-END by TSNJ,Zhenhua.Fan

    // Array of the outstanding send or save tasks.  Access is synchronized
    // with the object itself
    /* package for testing */
    @VisibleForTesting
    public final ArrayList<SendOrSaveTask> mActiveTasks = Lists.newArrayList();
    // FIXME: this variable is never read. related to sRequestMessageIdMap.
    private int mRequestId;
    private String mSignature;
    private Account[] mAccounts;
    private boolean mRespondedInline;
    private boolean mPerformedSendOrDiscard = false;
    //[FEATURE]-Add-BEGIN by TSCD.chao zhang,04/17/2014,FR 631895(porting from FR514398)
    private int mPriorityFlag = Message.FLAG_PRIORITY_NORMAL;
    private ImageView mPriorityIcon;
    //[BUGFIX]-Add-BEGIN by TCTNJ.wenlu.wu,12/03/2014,PR-857886
    private final int ADD_ATTACHMENT_MSG = 1001;
    // TS: jian.xu 2016-01-11 EMAIL BUGFIX-1307962 ADD_S
    private final int ADD_ATTACHMENT_MSG_ERROR = 1002;
    // TS: jian.xu 2016-01-11 EMAIL BUGFIX-1307962 ADD_E
    //TS: xinlei.sheng 2015-01-26 EMAIL FIXBUG_886976 ADD
    private boolean mLaunchContact = false;
    // TS: zhaotianyong 2015-02-28 EMAIL BUGFIX-926303 ADD_S
    public static final int RECIPIENT_MAX_NUMBER = 500;
    // TS: zhaotianyong 2015-02-28 EMAIL BUGFIX-926303 ADD_E
    // TS: zhaotianyong 2015-03-31 EMAIL BUGFIX-963249 ADD_S
    private boolean doSend = false;
    // TS: zhaotianyong 2015-03-31 EMAIL BUGFIX-963249 ADD_E
    // TS: zhaotianyong 2015-05-20 EMAIL BUGFIX-998884 ADD_S
    private boolean allAttachmentsLoad = false;
    // TS: zhaotianyong 2015-05-20 EMAIL BUGFIX-998884 ADD_E
    // TS: Gantao 2015-06-09 EMAIL BUGFIX-1019278 ADD_S
    private boolean mSelectedAccountUnusual = false;
    // TS: Gantao 2015-06-09 EMAIL BUGFIX-1019278 ADD_E

    // TS: junwei-xu 2015-03-25 EMAIL BUGFIX-958270 ADD_S
    private ReplyRecipientsListener mReplyRecipientslistener;
    private ActionableToastBar mToastBar;     //TS: jin.dong 2015-12-17 EMAIL BUGFIX_1170083 ADD

    private interface ReplyRecipientsListener {
        public void addCCRecipients();
    }
    // TS: junwei-xu 2015-03-25 EMAIL BUGFIX-958270 ADD_E

    // TS: jian.xu 2015-06-05 EMAIL BUGFIX-1006499 ADD_S
    private final int SET_LISTPOPUPWINDOW_HEIGHT=1003;
    // TS: jian.xu 2015-06-05 EMAIL BUGFIX-1006499 ADD_E

    // TS: Gantao 2015-06-16 EMAIL BUGFIX-1019238 ADD_S
    private ReplyToRecipientsListener mReplyToRecipientsLisnter;

    private interface ReplyToRecipientsListener {
        public void addToRecipients();
    }
    // TS: Gantao 2015-06-16 EMAIL BUGFIX-1019238 ADD_E

    //TS: yanhua.chen 2015-7-29 EMAIL BUGFIX_1053132 ADD_S
    private boolean attLargeWarning = true;
    //TS: yanhua.chen 2015-7-29 EMAIL BUGFIX_1053132 ADD_E
    //TS: yanhua.chen 2015-9-1 EMAIL CD_551912 ADD_S
    //private boolean mIsClickIcon = false;//[BUGFIX]-MOD by SCDTABLET.shujing.jin@tcl.com,05/06/2016,2013535
    private boolean mChangeAccount = false;
    //TS: yanhua.chen 2015-9-1 EMAIL CD_551912 ADD_E

    // TS: yanhua.chen 2015-9-19 EMAIL BUGFIX_569665 ADD_S
    private boolean mIsSaveDraft = false;
    private boolean mEditDraft = false;
    // TS: yanhua.chen 2015-9-19 EMAIL BUGFIX_569665 ADD_E

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            int id = msg.what;
            switch (id) {
            case ADD_ATTACHMENT_MSG:
                Attachment a = (Attachment) msg.obj;
                if (a != null) {
                    try {
                        long size = mAttachmentsView.addAttachment(mAccount, a, false);//TS: yanhua.chen 2015-6-8 EMAIL CR_996908 MOD
                        //TS: yanhua.chen 2016-3-16 EMAIL BUGFIX_1814252 ADD_S
                        if(size > Settings.DEFAULT_MID_ATTACHMENT_SIZE && attLargeWarning){
                            attLargeWarning = false;
                            showErrorToast(getResources().getString(R.string.too_large_to_mid_attach_additional,
                                    AttachmentUtils.convertToHumanReadableSize(ComposeActivity.this, Settings.DEFAULT_MID_ATTACHMENT_SIZE)));
                        }
                        //TS: yanhua.chen 2016-3-16 EMAIL BUGFIX_1814252 ADD_E
                        // TS: jian.xu 2015-10-12 EMAIL BUGFIX-708877 MOD_S
                        if (size > 0) {
                            mAttachmentsChanged = true;
                            updateSaveUi();
                            Analytics.getInstance().sendEvent("send_intent_attachment",
                                    Utils.normalizeMimeType(a.getContentType()), null, size);
                        }
                        // TS: jian.xu 2015-10-12 EMAIL BUGFIX-708877 MOD_E
                    } catch (AttachmentFailureException e) {
                        LogUtils.e(LOG_TAG, e, "Error adding attachment");
                        showAttachmentTooBigToast(e.getErrorRes());
                    }
                }
                break;
            // TS: jian.xu 2016-01-11 EMAIL BUGFIX-1307962 ADD_S
            case ADD_ATTACHMENT_MSG_ERROR:
                int errorRes = (int) msg.obj;
                showAttachmentTooBigToast(errorRes);
                break;
            // TS: jian.xu 2016-01-11 EMAIL BUGFIX-1307962 ADD_E
            // TS: jian.xu 2015-06-05 EMAIL BUGFIX-1006499 ADD_S
            case SET_LISTPOPUPWINDOW_HEIGHT:
             // TS: jian.xu 2015-06-17 EMAIL BUGFIX-1025192 ADD_S
//                int height = getListHeight();
//                mTo.setDropDownHeight(height);
//                mCc.setDropDownHeight(height);
//                mBcc.setDropDownHeight(height);
//                mTo.getAdapter().notifyDataSetChanged();
//                mCc.getAdapter().notifyDataSetChanged();
//                mBcc.getAdapter().notifyDataSetChanged();

             // TS: Gantao 2015-08-26 EMAIL BUGFIX-1075004 ADD_S
                if (mTo == null || mCc == null || mBcc == null) {
                    return;
                }
             // TS: Gantao 2015-08-26 EMAIL BUGFIX-1075004 ADD_E
                RecipientEditTextView textView = null;
                if(mTo.isFocused()){
                    textView = mTo;
                } else if(mCc.isFocused()){
                    textView = mCc;
                } else if(mBcc.isFocused()){
                    textView = mBcc;
                }
                if(textView == null) return;
                //TS: kaifeng.lu 2015-8-14 EMAIL BUGFIX_1067957 ADD_S
                if(textView.getAdapter() == null) return;
                //TS: kaifeng.lu 2015-8-14 EMAIL BUGFIX_1067957 ADD_E
                int count = textView.getAdapter().getCount();
                if(count==0){
                    return;
                }
                int height = getListHeight();
                textView.setDropDownHeight(height);
                textView.getAdapter().notifyDataSetChanged();
             // TS: jian.xu 2015-06-17 EMAIL BUGFIX-1025192 ADD_E
                break;
            // TS: jian.xu 2015-06-05 EMAIL BUGFIX-1006499 ADD_E
            default:
                break;
            }
        }
    };
    //[BUGFIX]-Add-END by TCTNJ.wenlu.wu,12/03/2014,PR-857886

    //TS: junwei-xu 2015-12-04 EMAIL BUGFIX-1035228 MOD_S
    public static class PrioritySelectFragment extends DialogFragment {

        public static final String TAG = "PRIORITY_SELECT_DIALOG";
        private static final String ARGS_PRIORITY_INDEX = "PRIORITY_INDEX";
        private int mPriorityIndex = 0;

        public static PrioritySelectFragment newInstance(int priorityIndex) {
            Bundle bundle = new Bundle();
            bundle.putInt(ARGS_PRIORITY_INDEX, priorityIndex);
            PrioritySelectFragment dialog = new PrioritySelectFragment();
            dialog.setArguments(bundle);
            return dialog;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            mPriorityIndex = this.getArguments().getInt(ARGS_PRIORITY_INDEX, 1);
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.set_priority_dialog_title)
                .setSingleChoiceItems(R.array.set_priority_dialog_options, mPriorityIndex,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if (getActivity() instanceof ComposeActivity) {
                                    ((ComposeActivity) getActivity()).changePriority(dialog, which);
                                }
                            }
                        }).setNegativeButton(R.string.cancel_action, null);
            return builder.create();
        }
    }

    public void changePriority(DialogInterface dialog, int which) {

        if (mDraft != null) {
            mDraft.mPriority = getPriorityOptionValue(which);
            // TS: Gantao 2015-12-17 EMAIL BUGFIX_1176396 MOD_S
            //Set the icon view by ourself avoid the UI issue
            //TODO : Set the icon view any where use the method.
//            mPriorityIcon.setImageLevel(mDraft.mPriority);
            setPriorityIcon(mDraft.mPriority);
        } else {
//            mPriorityIcon.setImageLevel(getPriorityOptionValue(which));
            setPriorityIcon(getPriorityOptionValue(which));
            // TS: Gantao 2015-12-17 EMAIL BUGFIX_1176396 MOD_E
        }
        mPriorityFlag = getPriorityOptionValue(which);
        mProrityChanged = true;
        // TS: jian.xu 2015-11-12 EMAIL BUGFIX-821332 ADD_S
        updateSaveUi();
        // TS: jian.xu 2015-11-12 EMAIL BUGFIX-821332 ADD_E
        // TS: junwei-xu 2015-06-30 EMAIL BUGFIX-1030195 DEL_S
        // Note: we don't use sharedpreferences to save priority, no need to save it
        /*
        //TS: junwei-xu 2015-2-3 EMAIL BUGFIX_874020 ADD_S
        SharedPreferences.Editor editor = getSharedPreferences("PriorityFlag", Context.MODE_PRIVATE).edit();
        editor.putInt("mPriorityFlag", mPriorityFlag);
        editor.commit();
        //TS: junwei-xu 2015-2-3 EMAIL BUGFIX_874020 ADD_E
        */
        //TS: junwei-xu 2015-06-30 EMAIL BUGFIX-1030195 DEL_E
        dialog.dismiss();
    }

    private void setPriority() {
        if (mDraft != null) {
            mPriorityFlag = mDraft.mPriority;
        }
        PrioritySelectFragment.newInstance(getPriorityOptionIndex(mPriorityFlag))
                .show(getFragmentManager(), PrioritySelectFragment.TAG);
    }
    //TS: junwei-xu 2015-12-04 EMAIL BUGFIX-1035228 MOD_E

    private int getPriorityOptionIndex(int value) {
        int[] values = getResources().getIntArray(R.array.set_priority_dialog_options_values);
        for (int i=0; i<values.length; i++) {
            if (values[i] == value) {
                return i;
            }
        }

        return -1;
    }

    private int getPriorityOptionValue(int index) {
        int[] values = getResources().getIntArray(R.array.set_priority_dialog_options_values);
        if ((index >=0) && (index < values.length)) {
            return values[index];
        } else {
            return -1;
        }
    }
    //[FEATURE]-Add-END by TSCD.chao zhang

    private final HtmlTree.ConverterFactory mSpanConverterFactory =
            new HtmlTree.ConverterFactory() {
            @Override
                public HtmlTree.Converter<Spanned> createInstance() {
                    return getSpanConverter();
                }
            };

    /**
     * Can be called from a non-UI thread.
     */
    public static void editDraft(Context launcher, Account account, Message message) {
        launch(launcher, account, message, EDIT_DRAFT, null, null, null, null,
                null /* extraValues */);
    }
    //[FEATURE]-Add-BEGIN by TSNJ,Zhenhua.Fan,06/11/2014,FR-622609
    public static void actionEditDraftFromDraftViewScreen(Context launcher, Account account, Message message) {

        Intent intent = new Intent(launcher, ComposeActivity.class);
        intent.putExtra(EXTRA_FROM_EMAIL_TASK, true);
        intent.putExtra(EXTRA_ACTION, EDIT_DRAFT);
        intent.putExtra(Utils.EXTRA_ACCOUNT, account);
        intent.putExtra(EXTRA_FROM_DRAFT_VIEW, true);
        intent.putExtra(ORIGINAL_DRAFT_MESSAGE, message);

        launcher.startActivity(intent);
    }
    //[FEATURE]-Add-END by TSNJ,Zhenhua.Fan
    /**
     * Can be called from a non-UI thread.
     */
    public static void compose(Context launcher, Account account) {
        launch(launcher, account, null, COMPOSE, null, null, null, null, null /* extraValues */);
    }

    /**
     * Can be called from a non-UI thread.
     */
    public static void composeToAddress(Context launcher, Account account, String toAddress) {
        launch(launcher, account, null, COMPOSE, toAddress, null, null, null,
                null /* extraValues */);
    }

    /**
     * Can be called from a non-UI thread.
     */
    public static void composeWithExtraValues(Context launcher, Account account,
            String subject, final ContentValues extraValues) {
        launch(launcher, account, null, COMPOSE, null, null, null, subject, extraValues);
    }

    /**
     * Can be called from a non-UI thread.
     */
    public static Intent createReplyIntent(final Context launcher, final Account account,
            final Uri messageUri, final boolean isReplyAll) {
        return createActionIntent(launcher, account, messageUri, isReplyAll ? REPLY_ALL : REPLY);
    }

    /**
     * Can be called from a non-UI thread.
     */
    public static Intent createForwardIntent(final Context launcher, final Account account,
            final Uri messageUri) {
        return createActionIntent(launcher, account, messageUri, FORWARD);
    }

    private static Intent createActionIntent(final Context context, final Account account,
            final Uri messageUri, final int action) {
        final Intent intent = new Intent(ACTION_LAUNCH_COMPOSE);
        intent.setPackage(context.getPackageName());

        updateActionIntent(account, messageUri, action, intent);

        return intent;
    }

    @VisibleForTesting
    static Intent updateActionIntent(Account account, Uri messageUri, int action, Intent intent) {
        intent.putExtra(EXTRA_FROM_EMAIL_TASK, true);
        intent.putExtra(EXTRA_ACTION, action);
        intent.putExtra(Utils.EXTRA_ACCOUNT, account);
        intent.putExtra(EXTRA_IN_REFERENCE_TO_MESSAGE_URI, messageUri);

        return intent;
    }

    /**
     * Can be called from a non-UI thread.
     */
    public static void reply(Context launcher, Account account, Message message) {
        launch(launcher, account, message, REPLY, null, null, null, null, null /* extraValues */);
    }

    /**
     * Can be called from a non-UI thread.
     */
    public static void replyAll(Context launcher, Account account, Message message) {
        launch(launcher, account, message, REPLY_ALL, null, null, null, null,
                null /* extraValues */);
    }

    /**
     * Can be called from a non-UI thread.
     */
    public static void forward(Context launcher, Account account, Message message) {
        launch(launcher, account, message, FORWARD, null, null, null, null, null /* extraValues */);
    }

    public static void reportRenderingFeedback(Context launcher, Account account, Message message,
            String body) {
        launch(launcher, account, message, FORWARD,
                "android-gmail-readability@google.com", body, null, null, null /* extraValues */);
    }

    private static void launch(Context context, Account account, Message message, int action,
            String toAddress, String body, String quotedText, String subject,
            final ContentValues extraValues) {
        //TS: xujian 2015-06-23 EMAIL BUGFIX_1015657 MOD_S
          if(message != null && message.bodyHtml != null && message.bodyHtml.length() > 1024*5){
              message.bodyHtml = "";
              LogUtils.d("Email","test---launch---bodyHtml set to empty");
          }
          if(message != null && message.bodyText != null && message.bodyText.length() > 1024*5){
              message.bodyText = "";
              LogUtils.d("Email","test---launch---bodyText set to empty");
          }
        //TS: xujian 2015-06-23 EMAIL BUGFIX_1015657 MOD_E
        //Note: intent can not take too much extra data, so not take these two string in extra data.
        if (message != null) {
            message.bodyHtmlLinkify = "";
            message.bodyTextLinkify = "";
        }
        Intent intent = new Intent(ACTION_LAUNCH_COMPOSE);
        intent.setPackage(context.getPackageName());
        intent.putExtra(EXTRA_FROM_EMAIL_TASK, true);
        intent.putExtra(EXTRA_ACTION, action);
        intent.putExtra(Utils.EXTRA_ACCOUNT, account);
        if (action == EDIT_DRAFT) {
            intent.putExtra(ORIGINAL_DRAFT_MESSAGE, message);
        } else {
            intent.putExtra(EXTRA_IN_REFERENCE_TO_MESSAGE, message);
        }
        if (message!=null){
            LogUtils.d("Email", String.format("test---launch---action=%d messageId=%d conversationUri=%s uri=%s subject=%s",
                    action, message.id, message.conversationUri, message.uri, message.subject));
        }
      //TS: wenggangjin 2015-01-06 EMAIL BUGFIX_882161 MOD_S
//        if(message != null && message.bodyHtml != null && message.bodyHtml.length() > 1024*10){
//            message.bodyHtml = "";
//            Log.d("Email","test---launch---bodyHtml set to empty");
//        }
      //TS: wenggangjin 2015-01-06 EMAIL BUGFIX_882161 MOD_E
        if (toAddress != null) {
            intent.putExtra(EXTRA_TO, toAddress);
        }
        if (body != null) {
            intent.putExtra(EXTRA_BODY, body);
        }
        if (quotedText != null) {
            intent.putExtra(EXTRA_QUOTED_TEXT, quotedText);
        }
        if (subject != null) {
            intent.putExtra(EXTRA_SUBJECT, subject);
        }
        if (extraValues != null) {
            LogUtils.d(LOG_TAG, "Launching with extraValues: %s", extraValues.toString());
            intent.putExtra(EXTRA_VALUES, extraValues);
        }
        if (action == COMPOSE) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        } else if (message != null) {
            //TS: jin.dong 2015-10-19 EMAIL BUGFIX_1100033 ADD_S
            if (message.uri == null) {
                LogUtils.e(LOG_TAG,
                        "what ? message's uri is null? want launch ComposeActivity ? do nothing is the right thing!!!");
                return;
            }
            //TS: jin.dong 2015-10-19 EMAIL BUGFIX_1100033 ADD_E
            //TS: yanhua.chen 2016-03-31 EMAIL BUGFIX_1877378 ADD_S
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            //TS: yanhua.chen 2016-03-31 EMAIL BUGFIX_1877378 ADD_E
            intent.setData(Utils.normalizeUri(message.uri));
        }

        // TS: xiaolin.li 2015-01-29 EMAIL BUGFIX-917401 ADD_S
        //context.startActivity(intent);
        try{
                context.startActivity(intent);
        }catch(Exception e){
                LogUtils.e(LOG_TAG, "Launch compose activity err.");
        }
        // TS: xiaolin.li 2015-01-29 EMAIL BUGFIX-917401 ADD_E
        }

    public static void composeMailto(Context context, Account account, Uri mailto) {
        final Intent intent = new Intent(Intent.ACTION_VIEW, mailto);
        intent.setPackage(context.getPackageName());
        intent.putExtra(EXTRA_FROM_EMAIL_TASK, true);
        intent.putExtra(Utils.EXTRA_ACCOUNT, account);
        if (mailto != null) {
            intent.setData(Utils.normalizeUri(mailto));
        }
        context.startActivity(intent);
    }

    //AM: peng-zhang 2015-02-27 EMAIL BUGFIX_955421 MOD_S
    private String bcc_text;
    private Boolean Changed = true;
    //AM: peng-zhang 2015-02-27 EMAIL BUGFIX_955421 MOD_E
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //TS: zheng.zou 2015-12-03 EMAIL BUGFIX_1030520 ADD_S
        if (mHasNoPermission) {
            return;
        }
        //TS: zheng.zou 2015-12-03 EMAIL BUGFIX_1030520 ADD_E
        //AM: peng-zhang 2015-02-27 EMAIL BUGFIX_955421 MOD_S
        if(savedInstanceState != null){
            bcc_text = savedInstanceState.getString("BCC_SAVE");
            Changed = savedInstanceState.getBoolean("BCC_CHANGED");
            // TS: junwei-xu 2015-06-30 EMAIL BUGFIX-1030195 ADD_S
            mPriorityFlag = savedInstanceState.getInt(KEY_PRIORITY_SAVED_STATE);
            // TS: junwei-xu 2015-06-30 EMAIL BUGFIX-1030195 ADD_E
            // TS: junwei-xu 2015-07-17 EMAIL BUGFIX-1029180 ADD_S
            mTextChanged = savedInstanceState.getBoolean(KEY_SAVED_STATE_TEXT_CHANGED);
            mAttachmentsChanged = savedInstanceState.getBoolean(KEY_SAVED_STATE_ATTACHMENT_CHANGED);
            mReplyFromChanged = savedInstanceState.getBoolean(KEY_SAVED_STATE_REPLY_FROM_CHANGED);
            mProrityChanged = savedInstanceState.getBoolean(KEY_SAVED_STATE_PRIORITY_CHANGED);
            // TS: junwei-xu 2015-07-17 EMAIL BUGFIX-1029180 ADD_E
            //TS: yanhua.chen 2015-7-29 EMAIL BUGFIX_1053132 ADD_S
            attLargeWarning = savedInstanceState.getBoolean(KEY_SAVED_STATE_ATTLARGEWARNING_CHANGED);
            //TS: yanhua.chen 2015-7-29 EMAIL BUGFIX_1053132 ADD_E
            //TS: yanhua.chen 2015-9-1 EMAIL CD_551912 ADD_S
            //mIsClickIcon = savedInstanceState.getBoolean(KEY_SAVED_STATE_ISCLICKICON_CHANGED);//[BUGFIX]-MOD by SCDTABLET.shujing.jin@tcl.com,05/06/2016,2013535
            mChangeAccount = savedInstanceState.getBoolean(KEY_SAVED_STATE_CHANGEACCOUNT_CHANGED);
            mEditDraft = savedInstanceState.getBoolean(KEY_SAVED_STATE_EDITDRAFT_CHANGED);
            //TS: yanhua.chen 2015-9-1 EMAIL CD_551912 ADD_E
        }
        //AM: peng-zhang 2015-02-27 EMAIL BUGFIX_955421 MOD_E
        setContentView(R.layout.compose);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Hide the app icon.
            actionBar.setIcon(null);
            actionBar.setDisplayUseLogoEnabled(false);
        }

        mInnerSavedState = (savedInstanceState != null) ?
                savedInstanceState.getBundle(KEY_INNER_SAVED_STATE) : null;

        //[FEATURE]-Add-BEGIN by TSNJ,Zhenhua.Fan,06/11/2014,FR-622609
        if (EmailApplication.isOrangeImapFeatureOn()) {
            Intent i = getIntent();
            if (i != null && i.getBooleanExtra(EXTRA_FROM_DRAFT_VIEW, false)) {
                isFromView = true;
            }
        }
        //[FEATURE]-Add-END by TSNJ,Zhenhua.Fan
        checkValidAccounts();
        // TS: jian.xu 2015-06-05 EMAIL BUGFIX-1006499 ADD_S
        LinearLayout tmp = (LinearLayout)findViewById(R.id.content);
        tmp.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener(){

            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                //get the cutrent visibale disrict.
                ComposeActivity.this.getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
                //get the screen height
                int screenHeight =  ComposeActivity.this.getWindow().getDecorView().getRootView().getHeight();

                Rect textRect = new Rect();
                int keyboardHeight = screenHeight - r.bottom;
                // the number 100 means that the keyboard is showing.
                if(keyboardHeight > 100){
                    android.os.Message msg = mHandler.obtainMessage(SET_LISTPOPUPWINDOW_HEIGHT);
                    mHandler.sendMessage(msg);
                }
            }
        });
        // TS: jian.xu 2015-06-05 EMAIL BUGFIX-1006499 ADD_E
    }

    // TS: jian.xu 2015-06-05 EMAIL BUGFIX-1006499 ADD_S
    private int getListHeight() {
        Rect r = new Rect();
        //get the cutrent visibale disrict.
        ComposeActivity.this.getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
     // TS: jian.xu 2015-06-26 EMAIL BUGFIX-1013067 MOD_S
        View textView = null;
        if (mTo.isFocused()) {
            textView = findViewById(R.id.to_content);
        } else if (mCc.isFocused()) {
            textView = findViewById(R.id.cc_content);
        } else if (mBcc.isFocused()) {
            textView = findViewById(R.id.bcc_content);
        }
        if (textView == null) {
            return 0;
        }
     // TS: jian.xu 2015-06-26 EMAIL BUGFIX-1013067 MOD_E
        Rect textRect = new Rect();
        textView.getGlobalVisibleRect(textRect);
        int listHeight = r.bottom - textRect.bottom;
        return listHeight;
    }
    // TS: jian.xu 2015-06-05 EMAIL BUGFIX-1006499 ADD_E

    private void finishCreate() {
        final Bundle savedState = mInnerSavedState;
        findViews();
        // TS: junwei-xu 2015-09-01 EMAIL BUGFIX-526192 ADD_S
        updateFromRowByAccounts();
        // TS: junwei-xu 2015-09-01 EMAIL BUGFIX-526192 ADD_E
        final Intent intent = getIntent();
        final Message message;
        final ArrayList<AttachmentPreview> previews;
        mShowQuotedText = false;
        final CharSequence quotedText;
        int action;
        // Check for any of the possibly supplied accounts.;
        final Account account;
        if (hadSavedInstanceStateMessage(savedState)) {
            action = savedState.getInt(EXTRA_ACTION, COMPOSE);
            account = savedState.getParcelable(Utils.EXTRA_ACCOUNT);
            message = savedState.getParcelable(EXTRA_MESSAGE);

            previews = savedState.getParcelableArrayList(EXTRA_ATTACHMENT_PREVIEWS);
            mRefMessage = savedState.getParcelable(EXTRA_IN_REFERENCE_TO_MESSAGE);
            quotedText = savedState.getCharSequence(EXTRA_QUOTED_TEXT);

            mExtraValues = savedState.getParcelable(EXTRA_VALUES);
        } else {
            account = obtainAccount(intent);
            action = intent.getIntExtra(EXTRA_ACTION, COMPOSE);
            // Initialize the message from the message in the intent
            message = intent.getParcelableExtra(ORIGINAL_DRAFT_MESSAGE);
            previews = intent.getParcelableArrayListExtra(EXTRA_ATTACHMENT_PREVIEWS);
            mRefMessage = intent.getParcelableExtra(EXTRA_IN_REFERENCE_TO_MESSAGE);
          //TS: wenggangjin 2015-01-06 EMAIL BUGFIX_882161 MOD_S
            if(mRefMessage != null && "".equals(mRefMessage.bodyHtml)){
                String htmlbody = Body.restoreBodyHtmlWithMessageId(this, mRefMessage.getId());
                mRefMessage.bodyHtml = htmlbody;
          //TS: xujian 2015-06-23 EMAIL BUGFIX_1015657 MOD_S
            } else if(message != null){
                if("".equals(message.bodyHtml)){
                    String htmlbody = Body.restoreBodyHtmlWithMessageId(this, message.getId());
                    message.bodyHtml = htmlbody;
                }
                if("".equals(message.bodyText)){
                    String body = Body.restoreBodyTextWithMessageId(this, message.getId());
                    message.bodyText = body;
                }
                //TS: xujian 2015-06-23 EMAIL BUGFIX_1015657 MOD_S
            }
          //TS: wenggangjin 2015-01-06 EMAIL BUGFIX_882161 MOD_E
            mRefMessageUri = intent.getParcelableExtra(EXTRA_IN_REFERENCE_TO_MESSAGE_URI);
            quotedText = null;

            if (Analytics.isLoggable()) {
                if (intent.getBooleanExtra(Utils.EXTRA_FROM_NOTIFICATION, false)) {
                    Analytics.getInstance().sendEvent(
                            "notification_action", "compose", getActionString(action), 0);
                }
            }
        }
      //TS: Gantao 2015-08-27 EMAIL FEATURE_ID DEL_S
//        mAttachmentsView.setAttachmentPreviews(previews);
      //TS: Gantao 2015-08-27 EMAIL FEATURE_ID DEL_E
        // TS: yanhua.chen 2015-9-19 EMAIL BUGFIX_569665 ADD_S
        if(action == EDIT_DRAFT){
            //mIsClickIcon = true;//[BUGFIX]-MOD by SCDTABLET.shujing.jin@tcl.com,05/06/2016,2013535
            mEditDraft = true;
        }
        // TS: yanhua.chen 2015-9-19 EMAIL BUGFIX_569665 ADD_E

        setAccount(account);
        if (mAccount == null) {
            return;
        }
          // TS: chenyanhua 2015-01-12 EMAIL BUGFIX-890424 MOD_S
//        initRecipients();
          // TS: chenyanhua 2015-01-12 EMAIL BUGFIX-890424 MOD_S
        // Clear the notification and mark the conversation as seen, if necessary
        final Folder notificationFolder =
                intent.getParcelableExtra(EXTRA_NOTIFICATION_FOLDER);

        if (notificationFolder != null) {
            final Uri conversationUri = intent.getParcelableExtra(EXTRA_NOTIFICATION_CONVERSATION);
            Intent actionIntent;
            if (conversationUri != null) {
                actionIntent = new Intent(MailIntentService.ACTION_RESEND_NOTIFICATIONS_WEAR);
                actionIntent.putExtra(Utils.EXTRA_CONVERSATION, conversationUri);
            } else {
                actionIntent = new Intent(MailIntentService.ACTION_CLEAR_NEW_MAIL_NOTIFICATIONS);
                actionIntent.setData(Utils.appendVersionQueryParameter(this,
                        notificationFolder.folderUri.fullUri));
            }
            actionIntent.setPackage(getPackageName());
            actionIntent.putExtra(Utils.EXTRA_ACCOUNT, account);
            actionIntent.putExtra(Utils.EXTRA_FOLDER, notificationFolder);

            startService(actionIntent);
        }

        if (intent.getBooleanExtra(EXTRA_FROM_EMAIL_TASK, false)) {
            mLaunchedFromEmail = true;
        } else if (Intent.ACTION_SEND.equals(intent.getAction())) {
            final Uri dataUri = intent.getData();
            if (dataUri != null) {
                final String dataScheme = intent.getData().getScheme();
                final String accountScheme = mAccount.composeIntentUri.getScheme();
                mLaunchedFromEmail = TextUtils.equals(dataScheme, accountScheme);
            }
        }

        if (mRefMessageUri != null) {
            mShowQuotedText = true;
            mComposeMode = action;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
                String wearReply = null;
                if (remoteInput != null) {
                    LogUtils.d(LOG_TAG, "Got remote input from new api");
                    CharSequence input = remoteInput.getCharSequence(
                            NotificationActionUtils.WEAR_REPLY_INPUT);
                    if (input != null) {
                        wearReply = input.toString();
                    }
                } else {
                    // TODO: remove after legacy code has been removed.
                    LogUtils.d(LOG_TAG,
                            "No remote input from new api, falling back to compatibility mode");
                    ClipData clipData = intent.getClipData();
                    if (clipData != null
                            && LEGACY_WEAR_EXTRA.equals(clipData.getDescription().getLabel())) {
                        Bundle extras = clipData.getItemAt(0).getIntent().getExtras();
                        if (extras != null) {
                            wearReply = extras.getString(NotificationActionUtils.WEAR_REPLY_INPUT);
                        }
                    }
                }

                if (!TextUtils.isEmpty(wearReply)) {
                    createWearReplyTask(this, mRefMessageUri, UIProvider.MESSAGE_PROJECTION,
                            mComposeMode, wearReply).execute();
                    finish();
                    return;
                } else {
                    LogUtils.w(LOG_TAG, "remote input string is null");
                }
            }

            getLoaderManager().initLoader(INIT_DRAFT_USING_REFERENCE_MESSAGE, null, this);
            return;
        } else if (message != null && action != EDIT_DRAFT) {
            initFromDraftMessage(message);
            initQuotedTextFromRefMessage(mRefMessage, action);
            mShowQuotedText = message.appendRefMessageContent;
            // if we should be showing quoted text but mRefMessage is null
            // and we have some quotedText, display that
            if (mShowQuotedText && mRefMessage == null) {
                if (quotedText != null) {
                    initQuotedText(quotedText, false /* shouldQuoteText */);
                } else if (mExtraValues != null) {
                    initExtraValues(mExtraValues);
                    return;
                }
            }
        } else if (action == EDIT_DRAFT) {
            if (message == null) {
                throw new IllegalStateException("Message must not be null to edit draft");
            }
            initFromDraftMessage(message);
            // Update the action to the draft type of the previous draft
            switch (message.draftType) {
                case UIProvider.DraftType.REPLY:
                    action = REPLY;
                    break;
                case UIProvider.DraftType.REPLY_ALL:
                    action = REPLY_ALL;
                    break;
                case UIProvider.DraftType.FORWARD:
                    action = FORWARD;
                    break;
                case UIProvider.DraftType.COMPOSE:
                default:
                    action = COMPOSE;
                    break;
            }
            LogUtils.d(LOG_TAG, "Previous draft had action type: %d", action);

            mShowQuotedText = message.appendRefMessageContent;
            //TS: Gantao 2015-07-28 EMAIL BUGFIX_1053829 MOD_S
            //Terrible original design,refMessageUri did not save to db,the value is always 0 here.
            //but the body's sourceKey is saved ,it points to the refMessage's id,so we can get
            //the refMessage from the body's sourceKey.
            long sourceKey = Body.restoreBodySourceKey(this, message.id);
            if (sourceKey != 0) {
                // If we're editing an existing draft that was in reference to an existing message,
                // still need to load that original message since we might need to refer to the
                // original sender and recipients if user switches "reply <-> reply-all".
                mRefMessageUri =Uri.parse("content://" + EmailContent.AUTHORITY + "/uimessage/" + sourceKey);
              //TS: Gantao 2015-07-28 EMAIL BUGFIX_1053829 MOD_E
                mComposeMode = action;
                getLoaderManager().initLoader(REFERENCE_MESSAGE_LOADER, null, this);
                return;
            }
        } else if ((action == REPLY || action == REPLY_ALL || action == FORWARD)) {
            if (mRefMessage != null) {
                initFromRefMessage(action);
                mShowQuotedText = true;
            }
        } else {
            if (initFromExtras(intent)) {
                return;
            }
        }

        mComposeMode = action;
        finishSetup(action, intent, savedState);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private static AsyncTask<Void, Void, Message> createWearReplyTask(
            final ComposeActivity composeActivity,
            final Uri refMessageUri, final String[] projection, final int action,
            final String wearReply) {
        return new AsyncTask<Void, Void, Message>() {
            private Intent mEmptyServiceIntent = new Intent(composeActivity, EmptyService.class);

            @Override
            protected void onPreExecute() {
                // Start service so we won't be killed if this app is put in the background.
                composeActivity.startService(mEmptyServiceIntent);
            }

            @Override
            protected Message doInBackground(Void... params) {
                Cursor cursor = composeActivity.getContentResolver()
                        .query(refMessageUri, projection, null, null, null, null);
                if (cursor != null) {
                    try {
                        cursor.moveToFirst();
                        return new Message(cursor);
                    } finally {
                        cursor.close();
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(Message message) {
                composeActivity.stopService(mEmptyServiceIntent);

                composeActivity.mRefMessage = message;
                composeActivity.initFromRefMessage(action);
                composeActivity.setBody(wearReply, false);
                composeActivity.finishSetup(action, composeActivity.getIntent(), null);
                composeActivity.sendOrSaveWithSanityChecks(false /* save */, true /* show  toast */,
                        false /* orientationChanged */, true /* autoSend */);
            }
        };
    }

    private void checkValidAccounts() {
        final Account[] allAccounts = AccountUtils.getAccounts(this);
        if (allAccounts == null || allAccounts.length == 0) {
            final Intent noAccountIntent = MailAppProvider.getNoAccountIntent(this);
            if (noAccountIntent != null) {
                mAccounts = null;
                startActivityForResult(noAccountIntent, RESULT_CREATE_ACCOUNT);
            }
        } else {
            // If none of the accounts are syncing, setup a watcher.
            boolean anySyncing = false;
            for (Account a : allAccounts) {
                if (a.isAccountReady()) {
                    anySyncing = true;
                    break;
                }
            }
            if (!anySyncing) {
                // There are accounts, but none are sync'd, which is just like having no accounts.
                mAccounts = null;
                getLoaderManager().initLoader(LOADER_ACCOUNT_CURSOR, null, this);
                return;
            }
        //  AM: zhiqiang.shao 2015-03-30 EMAIL BUGFIX_960434 MOD_S
            Account[] accounts = AccountUtils.getSyncingAccounts(this);
            ArrayList<Account> listAccount = new ArrayList<Account>(accounts.length);
            for (Account acct : accounts) {
                if (!acct.supportsCapability(UIProvider.AccountCapabilities.VIRTUAL_ACCOUNT)) {
                    listAccount.add(acct);
                }
            }
            mAccounts = listAccount.toArray(new Account[listAccount.size()]);
            //  AM: zhiqiang.shao 2015-03-30 EMAIL BUGFIX_960434 MOD_E
            finishCreate();
        }
    }

    private Account obtainAccount(Intent intent) {
        Account account = null;
        Object accountExtra = null;
        if (intent != null && intent.getExtras() != null) {
            accountExtra = intent.getExtras().get(Utils.EXTRA_ACCOUNT);
            if (accountExtra instanceof Account) {
                account = (Account) accountExtra;
                //TS: gangjin.weng 2015-04-17 EMAIL BUGFIX_974962 Add_S
                if (account != null && !account.getAccountId().equalsIgnoreCase("Account Id")) {
                    return account;
                } else {
                    account = null;
                }
                //TS: gangjin.weng 2015-04-17 EMAIL BUGFIX_974962 Add_E
            } else if (accountExtra instanceof String) {
                // This is the Account attached to the widget compose intent.
                account = Account.newInstance((String) accountExtra);
                //TS: gangjin.weng 2015-04-17 EMAIL BUGFIX_974962 Add_S
                if (account != null && !account.getAccountId().equalsIgnoreCase("Account Id")) {
                    return account;
                } else {
                    account = null;
                }
                //TS: gangjin.weng 2015-04-17 EMAIL BUGFIX_974962 Add_E
              //  AM: zhiqiang.shao 2015-03-30 EMAIL BUGFIX_957619,960434 MOD_S
//                if (account != null) {
//                    return account;
//                }
            }
            if (account != null) {
                if (!Address.isAllValid(account.getEmailAddress())
                        && mAccounts.length > 0) {
                    return account = mAccounts[0];
                }
                return account;
            }
            // AM: zhiqiang.shao 2015-03-30 EMAIL BUGFIX_957619,960434 MOD_E
            accountExtra = intent.hasExtra(Utils.EXTRA_ACCOUNT) ?
                    intent.getStringExtra(Utils.EXTRA_ACCOUNT) :
                        intent.getStringExtra(EXTRA_SELECTED_ACCOUNT);
        }

        // AM: Kexue.Geng 2015-03-06 EMAIL BUGFIX_927828 MOD_S
        /*
        MailAppProvider provider = MailAppProvider.getInstance();
        String lastAccountUri = provider.getLastSentFromAccount();
        if (TextUtils.isEmpty(lastAccountUri)) {
            lastAccountUri = provider.getLastViewedAccount();
        }
        if (!TextUtils.isEmpty(lastAccountUri)) {
            accountExtra = Uri.parse(lastAccountUri);
        }
        */

        boolean goOriginalPath = true;
        boolean defaultAccountEnable = PLFUtils.getBoolean(getApplicationContext(), "feature_email_defaultAccount_on");
        if (defaultAccountEnable) {
            // If we still have no account, try the default
            if (account == null) {
                long accountId = com.tct.emailcommon.provider.Account.getDefaultAccountId(this);
                if (accountId != com.tct.emailcommon.provider.Account.NO_ACCOUNT) {
                    // Make sure it exists...
                    com.tct.emailcommon.provider.Account defaultAccount = com.tct.emailcommon.provider.Account.restoreAccountWithId(getApplicationContext(), accountId);
                    if (defaultAccount != null) {
                        accountExtra = defaultAccount.getEmailAddress();
                        goOriginalPath = false;
                    }
                }
            }
        }

        if(goOriginalPath) {
            MailAppProvider provider = MailAppProvider.getInstance();
            // TS: junwei-xu 2015-11-10 EMAIL BUGFIX-864427 MOD_S
            //NOTE: We should use last seleceted account as current account.
            //String lastAccountUri = provider.getLastSentFromAccount();
            String lastAccountUri = provider.getLastViewedAccount();
            // TS: junwei-xu 2015-11-10 EMAIL BUGFIX-864427 MOD_E
            if (TextUtils.isEmpty(lastAccountUri)) {
                lastAccountUri = provider.getLastViewedAccount();
            }
            if (!TextUtils.isEmpty(lastAccountUri)) {
                accountExtra = Uri.parse(lastAccountUri);
            }
        }
        // AM: Kexue.Geng 2015-03-06 EMAIL BUGFIX_927828 MOD_E

        // TS: Gantao 2015-06-09 EMAIL BUGFIX-1019278 ADD_S
        // Note: If we go here,it says that the selected account is "combined view" or null,
        // anyway ,not normal selected,add a boolean var to avoid the issue when we reply a
        // mail from combined view.
        mSelectedAccountUnusual = true;
        if (mAccounts != null && mAccounts.length > 0) {
            if (accountExtra instanceof String && !TextUtils.isEmpty((String) accountExtra)) {
                // For backwards compatibility, we need to check account
                // names.
                for (Account a : mAccounts) {
                    if (a.getEmailAddress().equals(accountExtra)) {
                        account = a;
                    }
                }
            } else if (accountExtra instanceof Uri) {
                // The uri of the last viewed account is what is stored in
                // the current code base.
                for (Account a : mAccounts) {
                    if (a.uri.equals(accountExtra)) {
                        account = a;
                    }
                }
            }
            if (account == null) {
                account = mAccounts[0];
            }
        }
        return account;
    }

    protected void finishSetup(int action, Intent intent, Bundle savedInstanceState) {
        setFocus(action);
        // Don't bother with the intent if we have procured a message from the
        // intent already.
        if (!hadSavedInstanceStateMessage(savedInstanceState)) {
            initAttachmentsFromIntent(intent);
        }
        initActionBar();
        initFromSpinner(savedInstanceState != null ? savedInstanceState : intent.getExtras(),
                action);

        // If this is a draft message, the draft account is whatever account was
        // used to open the draft message in Compose.
        if (mDraft != null) {
            mDraftAccount = mReplyFromAccount;
        }

        initChangeListeners();

        // These two should be identical since we check CC and BCC the same way
        boolean showCc = !TextUtils.isEmpty(mCc.getText()) || (savedInstanceState != null &&
                savedInstanceState.getBoolean(EXTRA_SHOW_CC));
        boolean showBcc = !TextUtils.isEmpty(mBcc.getText()) || (savedInstanceState != null &&
                savedInstanceState.getBoolean(EXTRA_SHOW_BCC));
        mCcBccView.show(false /* animate */, showCc, showBcc);
        updateHideOrShowCcBcc();
        updateHideOrShowQuotedText(mShowQuotedText);

        mRespondedInline = mInnerSavedState != null &&
                mInnerSavedState.getBoolean(EXTRA_RESPONDED_INLINE);
        if (mRespondedInline) {
            mQuotedTextView.setVisibility(View.GONE);
        }
    }

    private static boolean hadSavedInstanceStateMessage(final Bundle savedInstanceState) {
        return savedInstanceState != null && savedInstanceState.containsKey(EXTRA_MESSAGE);
    }

    private void updateHideOrShowQuotedText(boolean showQuotedText) {
        mQuotedTextView.updateCheckedState(showQuotedText);
        mQuotedTextView.setUpperDividerVisible(mAttachmentsView.getAttachments().size() > 0);
    }

    private void setFocus(int action) {
        if (action == EDIT_DRAFT) {
            int type = mDraft.draftType;
            switch (type) {
                case UIProvider.DraftType.COMPOSE:
                case UIProvider.DraftType.FORWARD:
                    action = COMPOSE;
                    break;
                case UIProvider.DraftType.REPLY:
                case UIProvider.DraftType.REPLY_ALL:
                default:
                    action = REPLY;
                    break;
            }
        }
        switch (action) {
            case FORWARD:
            case COMPOSE:
                if (TextUtils.isEmpty(mTo.getText())) {
                    mTo.requestFocus();
                    break;
                }
                //$FALL-THROUGH$
            case REPLY:
            case REPLY_ALL:
            default:
                focusBody();
                break;
        }
    }

    /**
     * Focus the body of the message.
     */
    private void focusBody() {
        mBodyView.requestFocus();
        resetBodySelection();
    }

    private void resetBodySelection() {
        int length = mBodyView.getText().length();
        int signatureStartPos = getSignatureStartPosition(
                mSignature, mBodyView.getText().toString());
        if (signatureStartPos > -1) {
            // In case the user deleted the newlines...
            mBodyView.setSelection(signatureStartPos);
        } else if (length >= 0) {
            // Move cursor to the end.
            mBodyView.setSelection(length);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        Analytics.getInstance().activityStart(this);
    }

    @Override
    protected void onStop() {
        super.onStop();

        Analytics.getInstance().activityStop(this);
        hideInputSoftKeyboard();
    }

    //TS: xiangnan.zhou 2016-03-23 EMAIL BUGFIX-1783199 ADD_S
    /** hide input soft keyboard */
    private void hideInputSoftKeyboard() {
        View focusView = getCurrentFocus();
        if (focusView != null) {
            final InputMethodManager imm = (InputMethodManager)
                    getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(focusView.getWindowToken(), 0);
            }
        }
    }
    //TS: xiangnan.zhou 2016-03-23 EMAIL BUGFIX-1783199 ADD_E

    @Override
    protected void onResume() {
        super.onResume();
        //[FEATURE]-Add-BEGIN by TSNJ,Zhenhua.Fan,06/11/2014,FR-622609
        if (isFromView) {
            Intent intent = new Intent();
            intent.setAction("destroyDraftViewScreen");
            this.sendBroadcast(intent);
            isFromView = false;
        }
        //[FEATURE]-Add-END by TSNJ,Zhenhua.Fan
        // TS: junwei-xu 2015-09-01 EMAIL BUGFIX-526192 ADD_S
        //Note: check valid accounts and update ui
        updateFromRowByAccounts();
        // TS: junwei-xu 2015-09-01 EMAIL BUGFIX-526192 ADD_E
        // Update the from spinner as other accounts
        // may now be available.
        if (mFromSpinner != null && mAccount != null) {
            mFromSpinner.initialize(mComposeMode, mAccount, mAccounts, mRefMessage);
        }
        //TS: yanhua.chen 2015-9-22 EMAIL CD_551912 ADD_S
        //Note:when onResume,should restore the flag
        mIsSaveDraft = false;
        //TS: yanhua.chen 2015-9-22 EMAIL CD_551912 ADD_E
    }

    @Override
    protected void onPause() {
        super.onPause();

        // When the user exits the compose view, see if this draft needs saving.
        // Don't save unnecessary drafts if we are only changing the orientation.
        if (!isChangingConfigurations()) {
            saveIfNeeded();

            if (isFinishing() && !mPerformedSendOrDiscard && !isBlank()) {
                // log saving upon backing out of activity. (we avoid logging every sendOrSave()
                // because that method can be invoked many times in a single compose session.)
                logSendOrSave(true /* save */);
            }
        }
    }

    @Override
    protected void onActivityResult(int request, int result, Intent data) {
        if (request == RESULT_PICK_ATTACHMENT) {
            mAddingAttachment = false;
            if (result == RESULT_OK) {
                addAttachmentAndUpdateView(data);
            }
        } else if (request == RESULT_CREATE_ACCOUNT) {
            // We were waiting for the user to create an account
            if (result != RESULT_OK) {
                finish();
            } else {
                // Watch for accounts to show up!
                // restart the loader to get the updated list of accounts
                getLoaderManager().initLoader(LOADER_ACCOUNT_CURSOR, null, this);
                showWaitFragment(null);
            }//TS:kaifeng.lu 2015-10-28 EMAIL BUGFIX_824294 MOD_S
        } else if (result == RESULT_OK && request == ACTIVITY_REQUEST_PICK_CONTACT_TO) {
            //TS:kaifeng.lu 2015-10-28 EMAIL BUGFIX_824294 MOD_E
            // TS: junwei-xu 2015-07-17 EMAIL BUGFIX-1029180 ADD_S
            mLaunchContact = false;
            // TS: junwei-xu 2015-07-17 EMAIL BUGFIX-1029180 ADD_E
            //TS:kaifeng.lu 2015-10-28 EMAIL BUGFIX_824294 MOD_S
            String platform = PLFUtils.getString(this, "feature_email_platform");
            if (platform.equals(PLATFORM_QUALCOMM)) {
                addAddressFromData(mTo, data);
            } else if (platform.equals(PLATFORM_MTK)) {
                final long[] ids = data.getLongArrayExtra(EXTRA_PICK_DATA_RESULT);
                if(ids == null || ids.length <= 0) {
                    return;
                }
                addAddressesToList(ids, mTo);
            }
        } else if (result == RESULT_OK && request == ACTIVITY_REQUEST_PICK_CONTACT_CC) {
            //TS:kaifeng.lu 2015-10-28 EMAIL BUGFIX_824294 MOD_E
            // TS: junwei-xu 2015-07-17 EMAIL BUGFIX-1029180 ADD_S
            mLaunchContact = false;
            // TS: junwei-xu 2015-07-17 EMAIL BUGFIX-1029180 ADD_E
            //TS:kaifeng.lu 2015-10-28 EMAIL BUGFIX_824294 MOD_S
            String platform = PLFUtils.getString(this, "feature_email_platform");
            if (platform.equals(PLATFORM_QUALCOMM)) {
                addAddressFromData(mCc, data);
            } else if (platform.equals(PLATFORM_MTK)) {
                final long[] ids = data.getLongArrayExtra(EXTRA_PICK_DATA_RESULT);
                if (ids == null || ids.length <= 0) {
                    return;
                }
                addAddressesToList(ids, mCc);
            }
        } else if (result == RESULT_OK && request == ACTIVITY_REQUEST_PICK_CONTACT_BCC) {
            //TS:kaifeng.lu 2015-10-28 EMAIL BUGFIX_824294 MOD_E
            // TS: junwei-xu 2015-07-17 EMAIL BUGFIX-1029180 ADD_S
            mLaunchContact = false;
            // TS: junwei-xu 2015-07-17 EMAIL BUGFIX-1029180 ADD_E
            //TS:kaifeng.lu 2015-10-28 EMAIL BUGFIX_824294 MOD_S
            String platform = PLFUtils.getString(this, "feature_email_platform");
            if (platform.equals(PLATFORM_QUALCOMM)) {
                addAddressFromData(mBcc, data);
            } else if (platform.equals(PLATFORM_MTK)) {
                final long[] ids = data.getLongArrayExtra(EXTRA_PICK_DATA_RESULT);
                if (ids == null || ids.length <= 0) {
                    return;
                }
                addAddressesToList(ids, mBcc);
            }
            //TS:kaifeng.lu 2015-10-28 EMAIL BUGFIX_824294 MOD_E
        }
    }

    //TS: jin.dong 2015-12-17 EMAIL BUGFIX_1170083 ADD_S
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PermissionUtil.REQ_CODE_PERMISSION_ADD_ATTACHMENT) {
            if (!PermissionUtil.checkPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                showNeedPermissionToast(R.string.permission_needed_to_add_attachments);
            } else if(ACTION_LAUNCH_COMPOSE.equals(getIntent().getAction())){
                doAttach(MIME_TYPE_ALL);
            } else {
                Intent intent = getIntent();
                int size = getShareAttachmentSize(intent);
                if (size > 0) {
                    mAttachmentsChanged = true;
                    updateSaveUi();

                    Analytics.getInstance().sendEvent("send_intent_with_attachments",
                            Integer.toString(getAttachments().size()), null, size);
                }
            }
        }
    }
    //TS: jin.dong 2015-12-17 EMAIL BUGFIX_1170083 ADD_E

    //[FEATURE]-Add-BEGIN by TSCD.chao zhang,04/21/2014,FR631895(porting from FR487417)
    private void addAddressFromData(RecipientEditTextView view, Intent data) {
        //[FEATURE]-Mod-BEGIN by TSCD.chao zhang,04/22/2014,fix email crash during press back key during choosing nothing in Contacts.
        if (data ==null) {
           return;
        }
        //[FEATURE]-Mod-END  by TSCD.chao zhang
        Bundle b = data.getExtras();
        Bundle choiceSet = b.getBundle("result");
        Set<String> set = choiceSet.keySet();
        Iterator<String> i = set.iterator();
        //TS: junwei-xu 2016-01-25 EMAIL BUGFIX-1496954 MOD_S
        String itemName, itemAddress;
        Rfc822Token token;
        // TS: zhaotianyong 2015-02-28 EMAIL BUGFIX-926303 ADD_S
        int recipientsCounts = 0;
        // TS: zhaotianyong 2015-02-28 EMAIL BUGFIX-926303 ADD_E
        while (i.hasNext()) {
            // TS: zhaotianyong 2015-02-28 EMAIL BUGFIX-926303 ADD_S
            recipientsCounts++;
            if (recipientsCounts > RECIPIENT_MAX_NUMBER) {
                Utility.showToast(this, this.getString(
                        R.string.not_add_more_recipients, RECIPIENT_MAX_NUMBER));
                return;
            }
            // TS: zhaotianyong 2015-02-28 EMAIL BUGFIX-926303 ADD_E
            String key = i.next();
            String[] emails = choiceSet.getStringArray(key);
            itemName = emails[EMAIL_NAME_INDEX];
            itemAddress = emails[EMAIL_ADDRESS_INDEX];
            //TS: rong-tang 2016-03-28 EMAIL BUGFIX-1863457 MOD_S
            itemName = Rfc822Validator.fixInvalidName(itemName);
            //TS: rong-tang 2016-03-28 EMAIL BUGFIX-1863457 MOD_E
            token = new Rfc822Token(itemName, itemAddress, null);
            addAddressToList(token.toString(), view);
        }
        //TS: junwei-xu 2016-01-25 EMAIL BUGFIX-1496954 MOD_E
    }
    //[FEATURE]-Add-END by TSCD.chao zhang
    private static void addAddress(RecipientEditTextView  view, String address) {
        // TS: chenyanhua 2015-01-03 EMAIL BUGFIX-881538 ADD_S
        view.setText("");
        // TS: chenyanhua 2015-01-03 EMAIL BUGFIX_881538 ADD_E
        view.append(address + ", ");
    }
    //[FEATURE]-Add-BEGIN by TSCD.chao zhang,04/21/2014,FR631895(porting from FR487417)
    private  com.tct.emailcommon.mail.Address[] getAddressesIgnoreValid(TextView view) {
         com.tct.emailcommon.mail.Address[] addresses =  com.tct.emailcommon.mail.Address.parseIgnoreValid(view.getText().toString().trim());
        return addresses;
    }
    //[FEATURE]-Add-END by TSCD.chao zhang

    @Override
    protected final void onRestoreInstanceState(Bundle savedInstanceState) {
        final boolean hasAccounts = mAccounts != null && mAccounts.length > 0;
        if (hasAccounts) {
            clearChangeListeners();
        }
        super.onRestoreInstanceState(savedInstanceState);
        if (mInnerSavedState != null) {
            if (mInnerSavedState.containsKey(EXTRA_FOCUS_SELECTION_START)) {
                int selectionStart = mInnerSavedState.getInt(EXTRA_FOCUS_SELECTION_START);
                int selectionEnd = mInnerSavedState.getInt(EXTRA_FOCUS_SELECTION_END);
                // There should be a focus and it should be an EditText since we
                // only save these extras if these conditions are true.
                EditText focusEditText = (EditText) getCurrentFocus();
                final int length = focusEditText.getText().length();
                if (selectionStart < length && selectionEnd < length) {
                    focusEditText.setSelection(selectionStart, selectionEnd);
                }
            }
        }
        if (hasAccounts) {
            initChangeListeners();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        final Bundle inner = new Bundle();
        saveState(inner);
        state.putBundle(KEY_INNER_SAVED_STATE, inner);
        //AM: peng-zhang 2015-02-27 EMAIL BUGFIX_955421 MOD_S
        //AM: peng-zhang 2015-04-03 EMAIL BUGFIX_968060 MOD_S
        if(null != mBcc){
            String bcc_save= mBcc.getText().toString();
            boolean bcc_changed = mBcc.getText().toString().contains(mAccount.getEmailAddress());
            state.putString("BCC_SAVE", bcc_save);
            state.putBoolean("BCC_CHANGED", bcc_changed);
        }
        // TS: junwei-xu 2015-06-30 EMAIL BUGFIX-1030195 ADD_S
        state.putInt(KEY_PRIORITY_SAVED_STATE, mPriorityFlag);
        // TS: junwei-xu 2015-06-30 EMAIL BUGFIX-1030195 ADD_E
        // TS: junwei-xu 2015-07-17 EMAIL BUGFIX-1029180 ADD_S
        state.putBoolean(KEY_SAVED_STATE_TEXT_CHANGED, mTextChanged);
        state.putBoolean(KEY_SAVED_STATE_ATTACHMENT_CHANGED, mAttachmentsChanged);
        state.putBoolean(KEY_SAVED_STATE_REPLY_FROM_CHANGED, mReplyFromChanged);
        state.putBoolean(KEY_SAVED_STATE_PRIORITY_CHANGED, mProrityChanged);
        // TS: junwei-xu 2015-07-17 EMAIL BUGFIX-1029180 ADD_E
        //AM: peng-zhang 2015-04-03 EMAIL BUGFIX_968060 MOD_E
        //AM: peng-zhang 2015-02-27 EMAIL BUGFIX_955421 MOD_E
        //TS: yanhua.chen 2015-7-29 EMAIL BUGFIX_1053132 ADD_S
        state.putBoolean(KEY_SAVED_STATE_ATTLARGEWARNING_CHANGED, attLargeWarning);
        //TS: yanhua.chen 2015-7-29 EMAIL BUGFIX_1053132 ADD_E
        //TS: yanhua.chen 2015-9-1 EMAIL CD_551912 ADD_S
        //state.putBoolean(KEY_SAVED_STATE_ISCLICKICON_CHANGED, mIsClickIcon);//[BUGFIX]-MOD by SCDTABLET.shujing.jin@tcl.com,05/06/2016,2013535
        state.putBoolean(KEY_SAVED_STATE_CHANGEACCOUNT_CHANGED, mChangeAccount);
        state.putBoolean(KEY_SAVED_STATE_EDITDRAFT_CHANGED, mEditDraft);
        //TS: yanhua.chen 2015-9-1 EMAIL CD_551912 ADD_E
    }

    private void saveState(Bundle state) {
        //TS: lin-zhou 2015-9-28 EMAIL BUGFIX_666151 ADD_S
        //Note:When click doSend but not send and turn screen,restore the flag
        doSend = false;
        //TS: lin-zhou 2015-9-28 EMAIL BUGFIX_666151 ADD_E
        // We have no accounts so there is nothing to compose, and therefore, nothing to save.
        if (mAccounts == null || mAccounts.length == 0) {
            return;
        }
        // The framework is happy to save and restore the selection but only if it also saves and
        // restores the contents of the edit text. That's a lot of text to put in a bundle so we do
        // this manually.
        View focus = getCurrentFocus();
        if (focus != null && focus instanceof EditText) {
            EditText focusEditText = (EditText) focus;
            state.putInt(EXTRA_FOCUS_SELECTION_START, focusEditText.getSelectionStart());
            state.putInt(EXTRA_FOCUS_SELECTION_END, focusEditText.getSelectionEnd());
        }

        final List<ReplyFromAccount> replyFromAccounts = mFromSpinner.getReplyFromAccounts();
        final int selectedPos = mFromSpinner.getSelectedItemPosition();
        final ReplyFromAccount selectedReplyFromAccount = (replyFromAccounts != null
                && replyFromAccounts.size() > 0 && replyFromAccounts.size() > selectedPos) ?
                        replyFromAccounts.get(selectedPos) : null;
        if (selectedReplyFromAccount != null) {
            state.putString(EXTRA_SELECTED_REPLY_FROM_ACCOUNT, selectedReplyFromAccount.serialize()
                    .toString());
            state.putParcelable(Utils.EXTRA_ACCOUNT, selectedReplyFromAccount.account);
        } else {
            state.putParcelable(Utils.EXTRA_ACCOUNT, mAccount);
        }

        if (mDraftId == UIProvider.INVALID_MESSAGE_ID && mRequestId !=0) {
            // We don't have a draft id, and we have a request id,
            // save the request id.
            state.putInt(EXTRA_REQUEST_ID, mRequestId);
        }

        // We want to restore the current mode after a pause
        // or rotation.
        int mode = getMode();
        state.putInt(EXTRA_ACTION, mode);

        final Message message = createMessage(selectedReplyFromAccount, mRefMessage, mode,
                removeComposingSpans(mBodyView.getText()));
        if (mDraft != null) {
            message.id = mDraft.id;
            message.serverId = mDraft.serverId;
            message.uri = mDraft.uri;
        }
        state.putParcelable(EXTRA_MESSAGE, message);

        if (mRefMessage != null) {
            state.putParcelable(EXTRA_IN_REFERENCE_TO_MESSAGE, mRefMessage);
        } else if (message.appendRefMessageContent) {
            // If we have no ref message but should be appending
            // ref message content, we have orphaned quoted text. Save it.
            state.putCharSequence(EXTRA_QUOTED_TEXT, mQuotedTextView.getQuotedTextIfIncluded());
        }
        state.putBoolean(EXTRA_SHOW_CC, mCcBccView.isCcVisible());
        state.putBoolean(EXTRA_SHOW_BCC, mCcBccView.isBccVisible());
        state.putBoolean(EXTRA_RESPONDED_INLINE, mRespondedInline);
        state.putBoolean(EXTRA_SAVE_ENABLED, mSave != null && mSave.isEnabled());
      //TS: Gantao 2015-08-27 EMAIL FEATURE_ID DEL_S
//        state.putParcelableArrayList(
//                EXTRA_ATTACHMENT_PREVIEWS, mAttachmentsView.getAttachmentPreviews());
      //TS: Gantao 2015-08-27 EMAIL FEATURE_ID DEL_E

        state.putParcelable(EXTRA_VALUES, mExtraValues);
    }

    private int getMode() {
        int mode = ComposeActivity.COMPOSE;
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null
                && actionBar.getNavigationMode() == ActionBar.NAVIGATION_MODE_LIST) {
            // TS: junwei-xu 2015-09-01 EMAIL BUGFIX-526192 MOD_S
            //Note: get the truth compose mode from position
            //mode = actionBar.getSelectedNavigationIndex();
            mode = getComposeModeFromListPosition(actionBar.getSelectedNavigationIndex(), mSupportReplyAll);
            // TS: junwei-xu 2015-09-01 EMAIL BUGFIX-526192 MOD_E
        }
        return mode;
    }

    /**
     * This function might be called from a background thread, so be sure to move everything that
     * can potentially modify the UI to the main thread (e.g. removeComposingSpans for body).
     */
    private Message createMessage(ReplyFromAccount selectedReplyFromAccount, Message refMessage,
            int mode, Spanned body) {
        Message message = new Message();
        message.id = UIProvider.INVALID_MESSAGE_ID;
        message.serverId = null;
        message.uri = null;
        message.conversationUri = null;
        message.subject = mSubject.getText().toString();
        message.snippet = null;
        message.setTo(formatSenders(mTo.getText().toString()));
        message.setCc(formatSenders(mCc.getText().toString()));
        message.setBcc(formatSenders(mBcc.getText().toString()));
        // TS: tao.gan 2015-12-25 EMAIL FEATURE-1239148 ADD_S
        //message.setReplyTo(null);
        message.setReplyTo(getReplyToAddress());
        // TS: tao.gan 2015-12-25 EMAIL FEATURE-1239148 ADD_E
        message.dateReceivedMs = 0;
        // TS: xujian 2015-11-16 EMAIL BUGFIX-1106881 MOD_S
        // TS: xujian 2015-07-10 EMAIL BUGFIX-1037000 ADD_S
//        message.bodyHtml = spannedBodyToHtml(body, true);
        String body1 = mBodyView.getText().toString().replace("\n", "\n\r");
        SpannableString spannableString = new SpannableString(body1);
        message.bodyHtml = spannedBodyToHtml(spannableString, true);
        // TS: xujian 2015-07-10 EMAIL BUGFIX-1037000 ADD_E
        // TS: xujian 2015-11-16 EMAIL BUGFIX-1106881 MOD_E
        message.bodyText = mBodyView.getText().toString();
        message.embedsExternalResources = false;
        message.refMessageUri = mRefMessage != null ? mRefMessage.uri : null;
        message.appendRefMessageContent = mQuotedTextView.getQuotedTextIfIncluded() != null;
        ArrayList<Attachment> attachments = mAttachmentsView.getAttachments();
        message.hasAttachments = attachments != null && attachments.size() > 0;
        message.attachmentListUri = null;
        message.messageFlags = 0;
        message.alwaysShowImages = false;
        // TS: xujian 2015-06-15 EMAIL BUGFIX_1015669 MOD_S
        synchronized (attachments) {
            message.attachmentsJson = Attachment.toJSONArray(attachments);
        }
        // TS: xujian 2015-06-15 EMAIL BUGFIX_1015669 MOD_E
        CharSequence quotedText = mQuotedTextView.getQuotedText();
        message.quotedTextOffset = -1; // Just a default value.
        if (refMessage != null && !TextUtils.isEmpty(quotedText)) {
            if (!TextUtils.isEmpty(refMessage.bodyHtml)) {
                // We want the index to point to just the quoted text and not the
                // "On December 25, 2014..." part of it.
                message.quotedTextOffset =
                        QuotedTextView.getQuotedTextOffset(quotedText.toString());
            } else if (!TextUtils.isEmpty(refMessage.bodyText)) {
                // We want to point to the entire quoted text.
                message.quotedTextOffset = QuotedTextView.findQuotedTextIndex(quotedText);
            }
        }
        message.accountUri = null;
        message.setFrom(computeFromForAccount(selectedReplyFromAccount));
        message.draftType = getDraftType(mode);
        // TS: junwei-xu 2015-06-30 EMAIL BUGFIX-1030195 DEL_S
        // Note: we don't use sharedpreferences to save priority, no need to get it from sharedpreferences
        /*
        //TS: junwei-xu 2015-2-3 EMAIL BUGFIX_874020 ADD_S
        SharedPreferences perPreferences = getSharedPreferences("PriorityFlag", Context.MODE_PRIVATE);
        mPriorityFlag = perPreferences.getInt("mPriorityFlag", mPriorityFlag);
        //TS: junwei-xu 2015-2-3 EMAIL BUGFIX_874020 ADD_E
        */
        // TS: junwei-xu 2015-06-30 EMAIL BUGFIX-1030195 DEL_E
        message.mPriority=mPriorityFlag;//[FEATURE]-Add-BEGIN by TSCD.chao zhang,04/17/2014,FR 631895(porting from FR514398)
        return message;
    }

    protected String computeFromForAccount(ReplyFromAccount selectedReplyFromAccount) {
        final String email = selectedReplyFromAccount != null ? selectedReplyFromAccount.address
                : mAccount != null ? mAccount.getEmailAddress() : null;
        final String senderName = selectedReplyFromAccount != null ? selectedReplyFromAccount.name
                : mAccount != null ? mAccount.getSenderName() : null;
        final Address address = new Address(email, senderName);
        return address.toHeader();
    }

    private static String formatSenders(final String string) {
        if (!TextUtils.isEmpty(string) && string.charAt(string.length() - 1) == ',') {
            return string.substring(0, string.length() - 1);
        }
        return string;
    }

    @VisibleForTesting
    protected void setAccount(Account account) {
        if (account == null) {
            return;
        }
        if (!account.equals(mAccount)) {
            mAccount = account;
            mCachedSettings = mAccount.settings;
            //TS: yanhua.chen 2015-9-1 EMAIL CD_551912 MOD_S
            //Note:when change the account and have been clicked icon,set icon visible
            if(/*!mIsClickIcon&&*/mChangeAccount){//[BUGFIX]-MOD by SCDTABLET.shujing.jin@tcl.com,05/06/2016,2013535
                mBodySignature.setVisibility(View.VISIBLE);
            }
            // TS: yanhua.chen 2015-9-19 EMAIL BUGFIX_569665 MOD_S
            if(mEditDraft){
                appendSignature();
                mBodySignature.setVisibility(View.GONE);
            }else{
                //Note:when change the account,set mSignature the value to currentAccount's signature
                mSignature = mCachedSettings.signature;
            }

            //[BUGFIX]-MOD begin by SCDTABLET.shujing.jin@tcl.com,05/06/2016,2013535
            if(mBodySignature!=null&&!TextUtils.isEmpty(mSignature)){
                mBodySignature.setVisibility(View.GONE);
                //Note:when click icon,append the signature to body
                appendSignature();
            }
            //[BUGFIX]-MOD end by SCDTABLET.shujing.jin

            // TS: yanhua.chen 2015-9-19 EMAIL BUGFIX_569665 MOD_E
            //TS: yanhua.chen 2015-9-11 EMAIL BUGFIX_568681 ADD_S
            //Note:when account hasn't signature,set icon gone
            if(TextUtils.isEmpty(mSignature)){
                mBodySignature.setVisibility(View.GONE);
            }
            //TS: yanhua.chen 2015-9-11 EMAIL BUGFIX_568681 ADD_E
            //TS: yanhua.chen 2015-9-1 EMAIL CD_551912 MOD_E
        }
        if (mAccount != null) {
            MailActivity.setNfcMessage(mAccount.getEmailAddress());
        }
    }

    private void initFromSpinner(Bundle bundle, int action) {
        if (action == EDIT_DRAFT && mDraft.draftType == UIProvider.DraftType.COMPOSE) {
            action = COMPOSE;
        }
        mFromSpinner.initialize(action, mAccount, mAccounts, mRefMessage);

        if (bundle != null) {
            if (bundle.containsKey(EXTRA_SELECTED_REPLY_FROM_ACCOUNT)) {
                mReplyFromAccount = ReplyFromAccount.deserialize(mAccount,
                        bundle.getString(EXTRA_SELECTED_REPLY_FROM_ACCOUNT));
            } else if (bundle.containsKey(EXTRA_FROM_ACCOUNT_STRING)) {
                final String accountString = bundle.getString(EXTRA_FROM_ACCOUNT_STRING);
                mReplyFromAccount = mFromSpinner.getMatchingReplyFromAccount(accountString);
            }
        }
        if (mReplyFromAccount == null) {
            if (mDraft != null) {
                mReplyFromAccount = getReplyFromAccountFromDraft(mAccount, mDraft);
            } else if (mRefMessage != null) {
                mReplyFromAccount = getReplyFromAccountForReply(mAccount, mRefMessage);
            }
        }
        if (mReplyFromAccount == null) {
            mReplyFromAccount = getDefaultReplyFromAccount(mAccount);
        }

        mFromSpinner.setCurrentAccount(mReplyFromAccount);

        // TS: chenyanhua 2015-01-12 EMAIL BUGFIX-890424 ADD_S
        setAccount(mReplyFromAccount.account);
        // TS: Gantao 2015-06-24 EMAIL BUGFIX-1029207 ADD_S
        mSelectedAccountUnusual = false;
        // TS: Gantao 2015-06-24 EMAIL BUGFIX-1029207 ADD_E
        // TS: Gantao 2015-06-16 EMAIL BUGFIX-1019238 ADD_S
        if (mReplyToRecipientsLisnter != null) {
            mReplyToRecipientsLisnter.addToRecipients();
        }
        // TS: Gantao 2015-06-16 EMAIL BUGFIX-1019238 ADD_E
        // TS: junwei-xu 2015-03-25 EMAIL BUGFIX-958270 ADD_S
        if (mReplyRecipientslistener != null) {
            mReplyRecipientslistener.addCCRecipients();
        }
        // TS: junwei-xu 2015-03-25 EMAIL BUGFIX-958270 ADD_E
        initRecipients();
        // TS: chenyanhua 2015-01-12 EMAIL BUGFIX-890424 ADD_E
        if (mFromSpinner.getCount() > 1) {
            // If there is only 1 account, just show that account.
            // Otherwise, give the user the ability to choose which account to
            // send mail from / save drafts to.
            mFromStatic.setVisibility(View.GONE);
            mFromStaticText.setText(mReplyFromAccount.address);
            mFromSpinnerWrapper.setVisibility(View.VISIBLE);
        } else {
            mFromStatic.setVisibility(View.VISIBLE);
            mFromStaticText.setText(mReplyFromAccount.address);
            mFromSpinnerWrapper.setVisibility(View.GONE);
        }
    }

    // TS: junwei-xu 2015-09-01 EMAIL BUGFIX-526192 ADD_S
    /**
     * auto display From row according to mAccounts's size
     */
    private void updateFromRowByAccounts() {
        Account[] accounts = AccountUtils.getSyncingAccounts(this);
        ArrayList<Account> listAccount = new ArrayList<Account>(accounts.length);
        for (Account account : accounts) {
            if (!account.supportsCapability(UIProvider.AccountCapabilities.VIRTUAL_ACCOUNT)) {
                listAccount.add(account);
            }
        }
        mAccounts = listAccount.toArray(new Account[listAccount.size()]);
        // TS: jin.dong 2015-09-01 EMAIL BUGFIX-571435  MOD_S
        //NOTE: may launch from Gallery/fileManager shareing,so mFromRow not initialized.
        if (mAccounts == null || mAccounts.length == 0 || mFromRow == null) {
            return;
        }
        // TS: jin.dong 2015-09-01 EMAIL BUGFIX-571435  MOD_E
        boolean showFromRow = mAccounts.length > 1 ? true : false;
        if (showFromRow) {
            mFromRow.setVisibility(View.VISIBLE);
        } else {
            mFromRow.setVisibility(View.GONE);
        }
    }

    /**
     * check whether the message support Reply all
     */
    private boolean isSupportReplyAll(Message message) {
        boolean supprot = false;

        if (message != null) {
            String[] sendToAddresses = message.getToAddressesUnescaped();
            String[] ccAddresses = message.getCcAddressesUnescaped();
            String[] bccAddresses = message.getBccAddressesUnescaped();
            int sizeSendTo = sendToAddresses != null ? sendToAddresses.length : 0;
            int sizeCc = ccAddresses != null ? ccAddresses.length : 0;
            int sizeBcc = bccAddresses != null ? bccAddresses.length : 0;

            //when the summation for sendToAddresses, ccAddresses and bccAddresses's size over 1.
            //we think it supprot reply all.
            if (sizeSendTo + sizeCc + sizeBcc > 1) {
                supprot = true;
            }
        }

        return supprot;
    }

    /**
     * According to whether support reply all, return the truth list position.
     */
    private int getListPositionFromComposeMode(int composeMode, boolean isSupportReplyall) {
        int selectItem = 0;
        switch (composeMode) {
            case ComposeActivity.REPLY:
                selectItem = 0;
                break;
            case ComposeActivity.REPLY_ALL:
                selectItem = isSupportReplyall ? 1 : 0;
                break;
            case ComposeActivity.FORWARD:
                selectItem = isSupportReplyall ? 2 : 1;
                break;
        }
        return selectItem;
    }

    /**
     * According to whether support reply all, return the truth compose mode.
     */
    private int getComposeModeFromListPosition(int position, boolean isSupportReplyall) {
        int mode = ComposeActivity.REPLY;
        switch (position) {
            case 0:
                mode = ComposeActivity.REPLY;
                break;
            case 1:
                mode = isSupportReplyall ? ComposeActivity.REPLY_ALL : ComposeActivity.FORWARD;
                break;
            case 2:
                mode = ComposeActivity.FORWARD;
                break;
        }
        return mode;
    }
    // TS: junwei-xu 2015-09-01 EMAIL BUGFIX-526192 ADD_E

    private ReplyFromAccount getReplyFromAccountForReply(Account account, Message refMessage) {
        if (refMessage.accountUri != null) {
            // This must be from combined inbox.
            List<ReplyFromAccount> replyFromAccounts = mFromSpinner.getReplyFromAccounts();
            for (ReplyFromAccount from : replyFromAccounts) {
                if (from.account.uri.equals(refMessage.accountUri)) {
                    return from;
                }
            }
            return null;
        } else {
            return getReplyFromAccount(account, refMessage);
        }
    }

    /**
     * Given an account and the message we're replying to,
     * return who the message should be sent from.
     * @param account Account in which the message arrived.
     * @param refMessage Message to analyze for account selection
     * @return the address from which to reply.
     */
    public ReplyFromAccount getReplyFromAccount(Account account, Message refMessage) {
        // First see if we are supposed to use the default address or
        // the address it was sentTo.
        if (mCachedSettings.forceReplyFromDefault) {
            return getDefaultReplyFromAccount(account);
        } else {
            // If we aren't explicitly told which account to look for, look at
            // all the message recipients and find one that matches
            // a custom from or account.
            List<String> allRecipients = new ArrayList<String>();
            allRecipients.addAll(Arrays.asList(refMessage.getToAddressesUnescaped()));
            allRecipients.addAll(Arrays.asList(refMessage.getCcAddressesUnescaped()));
            return getMatchingRecipient(account, allRecipients);
        }
    }

    /**
     * Compare all the recipients of an email to the current account and all
     * custom addresses associated with that account. Return the match if there
     * is one, or the default account if there isn't.
     */
    protected ReplyFromAccount getMatchingRecipient(Account account, List<String> sentTo) {
        // Tokenize the list and place in a hashmap.
        ReplyFromAccount matchingReplyFrom = null;
        Rfc822Token[] tokens;
        HashSet<String> recipientsMap = new HashSet<String>();
        for (String address : sentTo) {
            tokens = Rfc822Tokenizer.tokenize(address);
            for (final Rfc822Token token : tokens) {
                recipientsMap.add(token.getAddress());
            }
        }

        int matchingAddressCount = 0;
        List<ReplyFromAccount> customFroms;
        customFroms = account.getReplyFroms();
        if (customFroms != null) {
            for (ReplyFromAccount entry : customFroms) {
                if (recipientsMap.contains(entry.address)) {
                    matchingReplyFrom = entry;
                    matchingAddressCount++;
                }
            }
        }
        if (matchingAddressCount > 1) {
            matchingReplyFrom = getDefaultReplyFromAccount(account);
        }
        return matchingReplyFrom;
    }

    private static ReplyFromAccount getDefaultReplyFromAccount(final Account account) {
        for (final ReplyFromAccount from : account.getReplyFroms()) {
            if (from.isDefault) {
                return from;
            }
        }
        return new ReplyFromAccount(account, account.uri, account.getEmailAddress(),
                account.getSenderName(), account.getEmailAddress(), true, false);
    }

    private ReplyFromAccount getReplyFromAccountFromDraft(final Account account,
            final Message msg) {
        final Address[] draftFroms = Address.parse(msg.getFrom());
        final String sender = draftFroms.length > 0 ? draftFroms[0].getAddress() : "";
        ReplyFromAccount replyFromAccount = null;
        List<ReplyFromAccount> replyFromAccounts = mFromSpinner.getReplyFromAccounts();
        if (TextUtils.equals(account.getEmailAddress(), sender)) {
            replyFromAccount = getDefaultReplyFromAccount(account);
        } else {
            for (ReplyFromAccount fromAccount : replyFromAccounts) {
                if (TextUtils.equals(fromAccount.address, sender)) {
                    replyFromAccount = fromAccount;
                    break;
                }
            }
        }
        return replyFromAccount;
    }

    private void findViews() {
        mScrollView = (ScrollView) findViewById(R.id.compose);
        mScrollView.setVisibility(View.VISIBLE);
        mCcBccButton = findViewById(R.id.add_cc_bcc);

        //[BUGFIX]-Add-BEGIN by SCDTABLET.weiwei.huang,05/03/2016,2013739,
        //[Email]Add mail contact icon display is not consistent
        bccButtonImg = findViewById(R.id.bcc_img);
        ccButtonImg= findViewById(R.id.cc_img);
        //[BUGFIX]-Add-END by SCDTABLET.weiwei.huang

        if (mCcBccButton != null) {
            mCcBccButton.setOnClickListener(this);
        }
        // TS: junwei-xu 2015-09-01 EMAIL BUGFIX-526192 ADD_S
        mFromRow = (LinearLayout) findViewById(R.id.compose_from_row);
        // TS: junwei-xu 2015-09-01 EMAIL BUGFIX-526192 ADD_E
        mCcBccView = (CcBccView) findViewById(R.id.cc_bcc_wrapper);
        mAttachmentsView = (AttachmentsView)findViewById(R.id.attachments);
        mTo = (RecipientEditTextView) findViewById(R.id.to);
        initializeRecipientEditTextView(mTo);
        mTo.setAlternatePopupAnchor(findViewById(R.id.compose_to_dropdown_anchor));
        mCc = (RecipientEditTextView) findViewById(R.id.cc);
        initializeRecipientEditTextView(mCc);
        mBcc = (RecipientEditTextView) findViewById(R.id.bcc);
        initializeRecipientEditTextView(mBcc);
        // TODO: add special chips text change watchers before adding
        // this as a text changed watcher to the to, cc, bcc fields.
        mSubject = (TextView) findViewById(R.id.subject);
        mSubject.setOnEditorActionListener(this);
        mSubject.setOnFocusChangeListener(this);
        mQuotedTextView = (QuotedTextView) findViewById(R.id.quoted_text_view);
        mQuotedTextView.setRespondInlineListener(this);
        mBodyView = (EditText) findViewById(R.id.body);
        mBodyView.setOnFocusChangeListener(this);
        //TS: yanhua.chen 2015-9-1 EMAIL CD_551912 ADD_S
        mBodySignature = (ImageButton) findViewById(R.id.body_signature);
        //Note:When turn screen and has been click icon,set icon gone
        //if(mIsClickIcon){//[BUGFIX]-MOD by SCDTABLET.shujing.jin@tcl.com,05/06/2016,2013535
            mBodySignature.setVisibility(View.GONE);
        //}
        mBodySignature.setOnClickListener(this);
        //TS: yanhua.chen 2015-9-1 EMAIL CD_551912 ADD_E
        mFromStatic = findViewById(R.id.static_from_content);
        mFromStaticText = (TextView) findViewById(R.id.from_account_name);
        mFromSpinnerWrapper = findViewById(R.id.spinner_from_content);
        mFromSpinner = (FromAddressSpinner) findViewById(R.id.from_picker);
        //[FEATURE]-Add-BEGIN by TSCD.chao zhang,04/17/2014,FR 631895(porting from FR514398)
        mPriorityIcon = (ImageView) findViewById(R.id.priority_icon);
        setPriorityIcon(Message.FLAG_PRIORITY_NORMAL);
        // TS: junwei-xu 2015-09-01 EMAIL BUGFIX-526192 ADD_S
        mPriorityIcon.setOnClickListener(this);
        // TS: junwei-xu 2015-09-01 EMAIL BUGFIX-526192 ADD_E
        //[FEATURE]-Add-END by TSCD.chao zhang

        //[FEATURE]-Add-BEGIN by TSCD.chao zhang,04/21/2014,FR631895(porting from FR487417)
        //TS: rong-tang 2016-04-06 EMAIL-1840992 MOD_S
        mToPicker = (ImageButton) findViewById(R.id.to_recipients_picker);
        mCcPicker = (ImageButton) findViewById(R.id.cc_recipients_picker);
        mBccPicker = (ImageButton)findViewById(R.id.bcc_recipients_picker);
        if (mToPicker != null && mCcPicker != null && mBccPicker != null) {
            // TS: xiaolin.li 2014-11-25 EMAIL READ_PLF MOD_S
            //if(getResources().getBoolean(R.bool.feature_email_recipients_picker_on)){
            if(PLFUtils.getBoolean(this, "feature_email_recipients_picker_on")){
            // TS: xiaolin.li 2014-11-25 EMAIL READ_PLF MOD_E
                mToPicker.setVisibility(View.VISIBLE);
                mToPicker.setOnClickListener(this);
                /**we handle the visibility of cc and bcc in another class(CcBccView.java)**/
                // TS: zhaotianyong 2014-12-11 EMAIL BUGFIX-862341 ADD_S
                mCcPicker.setVisibility(View.VISIBLE);
                mCcPicker.setOnClickListener(this);
                mBccPicker.setVisibility(View.VISIBLE);
                mBccPicker.setOnClickListener(this);
                // TS: zhaotianyong 2014-12-11 EMAIL BUGFIX-862341 ADD_E
            }
            else{
                mToPicker.setVisibility(View.GONE);
                // TS: zhaotianyong 2014-12-11 EMAIL BUGFIX-862341 ADD_S
                mCcPicker.setVisibility(View.GONE);
                mBccPicker.setVisibility(View.GONE);
                // TS: zhaotianyong 2014-12-11 EMAIL BUGFIX-862341 ADD_E
            }
        }
        //TS: rong-tang 2016-04-06 EMAIL-1840992 MOD_E
        //[FEATURE]-Add-END by TSCD.chao zhang
        mToastBar = (ActionableToastBar)findViewById(R.id.toast_bar);    //TS: jin.dong 2015-12-17 EMAIL BUGFIX_1170083 ADD
        //TS: jin.dong 2015-12-28 EMAIL BUGFIX_1193689 ADD_S
        //hide the compose btn
        View floatingComposeButton = findViewById(R.id.compose_button);
        if (floatingComposeButton != null) {
            floatingComposeButton.setVisibility(View.GONE);
        }
        //TS: jin.dong 2015-12-28 EMAIL BUGFIX_1193689 ADD_E
    }

    private void initializeRecipientEditTextView(RecipientEditTextView view) {
        view.setTokenizer(new Rfc822Tokenizer());
        view.setThreshold(COMPLETION_THRESHOLD);
     // TS: jian.xu 2015-06-05 EMAIL BUGFIX-1006499 ADD_S
        view.setDropDownVerticalOffset(0);
     // TS: jian.xu 2015-06-05 EMAIL BUGFIX-1006499 ADD_E
    }

    @Override
    public boolean onEditorAction(TextView view, int action, KeyEvent keyEvent) {
        if (action == EditorInfo.IME_ACTION_DONE) {
            focusBody();
            return true;
        }
        return false;
    }

    /**
     * Convert the body text (in {@link Spanned} form) to ready-to-send HTML format as a plain
     * String.
     *
     * @param body the body text including fancy style spans
     * @param removedComposing whether the function already removed composingSpans. Necessary
     *   because we cannot call removeComposingSpans from a background thread.
     * @return HTML formatted body that's suitable for sending or saving
     */
    private String spannedBodyToHtml(Spanned body, boolean removedComposing) {
        if (!removedComposing) {
            body = removeComposingSpans(body);
        }
        final HtmlifyBeginResult r = onHtmlifyBegin(body);
        return onHtmlifyEnd(Html.toHtml(r.result), r.extras);
    }

    /**
     * A hook for subclasses to convert custom spans in the body text prior to system HTML
     * conversion. That HTML conversion is lossy, so anything above and beyond its capability
     * has to be handled here.
     *
     * @param body
     * @return a copy of the body text with custom spans replaced with HTML
     */
    protected HtmlifyBeginResult onHtmlifyBegin(Spanned body) {
        return new HtmlifyBeginResult(body, null /* extras */);
    }

    protected String onHtmlifyEnd(String html, Object extras) {
        return html;
    }

    protected TextView getBody() {
        return mBodyView;
    }

    @VisibleForTesting
    public String getBodyHtml() {
        return spannedBodyToHtml(mBodyView.getText(), false);
    }

    @VisibleForTesting
    public Account getFromAccount() {
        return mReplyFromAccount != null && mReplyFromAccount.account != null ?
                mReplyFromAccount.account : mAccount;
    }

    private void clearChangeListeners() {
        mSubject.removeTextChangedListener(this);
        mBodyView.removeTextChangedListener(this);
        mTo.removeTextChangedListener(mToListener);
        mCc.removeTextChangedListener(mCcListener);
        mBcc.removeTextChangedListener(mBccListener);
        mFromSpinner.setOnAccountChangedListener(null);
        mAttachmentsView.setAttachmentChangesListener(null);
    }

    // Now that the message has been initialized from any existing draft or
    // ref message data, set up listeners for any changes that occur to the
    // message.
    private void initChangeListeners() {
        // Make sure we only add text changed listeners once!
        clearChangeListeners();
        mSubject.addTextChangedListener(this);
        mBodyView.addTextChangedListener(this);
        if (mToListener == null) {
            mToListener = new RecipientTextWatcher(mTo, this);
        }
        mTo.addTextChangedListener(mToListener);
        if (mCcListener == null) {
            mCcListener = new RecipientTextWatcher(mCc, this);
        }
        mCc.addTextChangedListener(mCcListener);
        if (mBccListener == null) {
            mBccListener = new RecipientTextWatcher(mBcc, this);
        }
        mBcc.addTextChangedListener(mBccListener);
        mFromSpinner.setOnAccountChangedListener(this);
        mAttachmentsView.setAttachmentChangesListener(this);
    }

    private void initActionBar() {
        LogUtils.d(LOG_TAG, "initializing action bar in ComposeActivity");
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar == null) {
            return;
        }
        if (mComposeMode == ComposeActivity.COMPOSE) {
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
            actionBar.setTitle(R.string.compose);
        } else {
            actionBar.setTitle(null);
            // TS: junwei-xu 2015-09-01 EMAIL BUGFIX-526192 MOD_S
            if (mComposeModeAdapter == null) {
                mSupportReplyAll = isSupportReplyAll(mRefMessage);
                //mComposeModeAdapter = new ComposeModeAdapter(actionBar.getThemedContext());
                mComposeModeAdapter = new ComposeModeAdapter(actionBar.getThemedContext(), mSupportReplyAll);
            }
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
            actionBar.setListNavigationCallbacks(mComposeModeAdapter, this);
            actionBar.setSelectedNavigationItem(getListPositionFromComposeMode(mComposeMode, mSupportReplyAll));
            /*
            switch (mComposeMode) {
                case ComposeActivity.REPLY:
                    actionBar.setSelectedNavigationItem(0);
                    break;
                case ComposeActivity.REPLY_ALL:
                    actionBar.setSelectedNavigationItem(1);
                    break;
                case ComposeActivity.FORWARD:
                    actionBar.setSelectedNavigationItem(2);
                    break;
            }
            */
            // TS: junwei-xu 2015-09-01 EMAIL BUGFIX-526192 MOD_E
        }
        actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP,
                ActionBar.DISPLAY_HOME_AS_UP);
        actionBar.setHomeButtonEnabled(true);
    }

    private void initFromRefMessage(int action) {
        setFieldsFromRefMessage(action);

        // Check if To: address and email body needs to be prefilled based on extras.
        // This is used for reporting rendering feedback.
        if (MessageHeaderView.ENABLE_REPORT_RENDERING_PROBLEM) {
            Intent intent = getIntent();
            if (intent.getExtras() != null) {
                String toAddresses = intent.getStringExtra(EXTRA_TO);
                if (toAddresses != null) {
                    addToAddresses(Arrays.asList(TextUtils.split(toAddresses, ",")));
                }
                String body = intent.getStringExtra(EXTRA_BODY);
                if (body != null) {
                    setBody(body, false /* withSignature */);
                }
            }
        }
    }

    private void setFieldsFromRefMessage(int action) {
        setSubject(mRefMessage, action);
        // Setup recipients
        if (action == FORWARD) {
            mForward = true;
            mPriorityFlag = mRefMessage.mPriority;
            setPriorityIcon(mPriorityFlag);
        }
        initRecipientsFromRefMessage(mRefMessage, action);
        //TS: jian.xu 2015-11-13 EMAIL BUGFIX-863678 MOD_S
        //NOTE: Also need init quoted text when switch from compose mode.
        //TS: Gantao 2015-07-28 EMAIL BUGFIX_1053829 MOD_S
        //If we are editing a draft,we don't init quoted text.
        //if (mDraft == null ) {
        //TS: chaozhang 2016-01-13 EMAIL BUGFIX_1355979 MOD_S
        try {
            initQuotedTextFromRefMessage(mRefMessage, action);
        }catch (OutOfMemoryError oom){
            LogUtils.e(LOG_TAG, "OOM happen during initRefMessage!!!", oom);
        }
        //TS: chaozhang 2015-01-13 EMAIL BUGFIX_1355979  MOD_E
        //}
        //TS: Gantao 2015-07-28 EMAIL BUGFIX_1053829 MOD_E
        //TS: jian.xu 2015-11-13 EMAIL BUGFIX-863678 MOD_E
        // TS: Gantao 2015-07-21 EMAIL BUGFIX_1047618 MOD_S
        //Note:very serious problem,only change to forward should initAttachments.
        if (action == ComposeActivity.FORWARD/* || mAttachmentsChanged*/) {
         // TS: Gantao 2015-07-21 EMAIL BUGFIX_1047618 MOD_E
            // TS: zhaotianyong 2015-05-08 EMAIL BUGFIX_988459 MOD_S
            initAttachments(mRefMessage, action == ComposeActivity.FORWARD);
            // TS: zhaotianyong 2015-05-08 EMAIL BUGFIX_988459 MOD_E
        }
    }

    protected HtmlTree.Converter<Spanned> getSpanConverter() {
        return new HtmlUtils.SpannedConverter();
    }

    private void initFromDraftMessage(Message message) {
        LogUtils.d(LOG_TAG, "Intializing draft from previous draft message: %s", message);

        mDraft = message;
        mDraftId = message.id;
        mSubject.setText(message.subject);
        mForward = message.draftType == UIProvider.DraftType.FORWARD;
        //[FEATURE]-Add-BEGIN by TSCD.chao zhang,04/17/2014,FR 631895(porting from FR514398)
        //[FEATURE]-Add-END by TSCD.chao zhang
        // TS: junwei-xu 2015-06-30 EMAIL BUGFIX-1030195 ADD_S
        // Note: we should initialize priority flag from draf message
        mPriorityFlag = message.mPriority;
        setPriorityIcon(mPriorityFlag);
        // TS: junwei-xu 2015-06-30 EMAIL BUGFIX-1030195 ADD_E
        final List<String> toAddresses = Arrays.asList(message.getToAddressesUnescaped());
        addToAddresses(toAddresses);
        addCcAddresses(Arrays.asList(message.getCcAddressesUnescaped()), toAddresses);
        addBccAddresses(Arrays.asList(message.getBccAddressesUnescaped()));
        if (message.hasAttachments) {
            List<Attachment> attachments = message.getAttachments();
            for (Attachment a : attachments) {
                addAttachmentAndUpdateView(a,true); //TS: zheng.zou 2015-05-28 EMAIL BUGFIX_-997631 MOD
            }
        }
        int quotedTextIndex = message.appendRefMessageContent ? message.quotedTextOffset : -1;
        // Set the body
        CharSequence quotedText = null;
        if (!TextUtils.isEmpty(message.bodyHtml)) {
            String body = message.bodyHtml;
            if (quotedTextIndex > -1) {
                // Find the offset in the html text of the actual quoted text and strip it out.
                // Note that the actual quotedTextOffset in the message has not changed as
                // this different offset is used only for display purposes. They point to different
                // parts of the original message.  Please see the comments in QuoteTextView
                // to see the differences.
                quotedTextIndex = QuotedTextView.findQuotedTextIndex(message.bodyHtml);
                if (quotedTextIndex > -1) {
                    body = message.bodyHtml.substring(0, quotedTextIndex);
                    quotedText = message.bodyHtml.subSequence(quotedTextIndex,
                            message.bodyHtml.length());
                }
            }
            new HtmlToSpannedTask().execute(body);
        } else {
            final String body = message.bodyText;
            final CharSequence bodyText;
            if (TextUtils.isEmpty(body)) {
                bodyText = "";
                quotedText = null;
            } else {
                if (quotedTextIndex > body.length()) {
                    // Sanity check to guarantee that we will not over index the String.
                    // If this happens there is a bigger problem. This should never happen hence
                    // the wtf logging.
                    quotedTextIndex = -1;
                    LogUtils.wtf(LOG_TAG, "quotedTextIndex (%d) > body.length() (%d)",
                            quotedTextIndex, body.length());
                }
                bodyText = quotedTextIndex > -1 ? body.substring(0, quotedTextIndex) : body;
                if (quotedTextIndex > -1) {
                    quotedText = body.substring(quotedTextIndex);
                }
            }
            mBodyView.setText(bodyText);
        }
        if (quotedTextIndex > -1 && quotedText != null) {
            mQuotedTextView.setQuotedTextFromDraft(quotedText, mForward);
        }
    }

    //[BUGFIX]-ADD-BEGIN by TSNJ,wenlu.wu,10/20/2014,FR-739335
    private boolean mBodyAlreadySet = false;
    //[BUGFIX]-ADD-END by TSNJ,wenlu.wu,10/20/2014,FR-739335

    /**
     * Fill all the widgets with the content found in the Intent Extra, if any.
     * Also apply the same style to all widgets. Note: if initFromExtras is
     * called as a result of switching between reply, reply all, and forward per
     * the latest revision of Gmail, and the user has already made changes to
     * attachments on a previous incarnation of the message (as a reply, reply
     * all, or forward), the original attachments from the message will not be
     * re-instantiated. The user's changes will be respected. This follows the
     * web gmail interaction.
     * @return {@code true} if the activity should not call {@link #finishSetup}.
     */
    public boolean initFromExtras(Intent intent) {
        // If we were invoked with a SENDTO intent, the value
        // should take precedence
        final Uri dataUri = intent.getData();
        if (dataUri != null) {
            if (MAIL_TO.equals(dataUri.getScheme())) {
                initFromMailTo(dataUri.toString());
            } else {
                if (!mAccount.composeIntentUri.equals(dataUri)) {
                    String toText = dataUri.getSchemeSpecificPart();
                    // TS: junwei-xu 2015-03-23 EMAIL BUGFIX_980239 MOD_S
                    //if (toText != null) {
                    if (Address.isAllValid(toText)) {
                    // TS: junwei-xu 2015-04-23 EMAIL BUGFIX_980239 MOD_E
                        mTo.setText("");
                        addToAddresses(Arrays.asList(TextUtils.split(toText, ",")));
                    }
                }
            }
        }

        String[] extraStrings = intent.getStringArrayExtra(Intent.EXTRA_EMAIL);
        if (extraStrings != null) {
            addToAddresses(Arrays.asList(extraStrings));
        }
        extraStrings = intent.getStringArrayExtra(Intent.EXTRA_CC);
        if (extraStrings != null) {
            addCcAddresses(Arrays.asList(extraStrings), null);
        }
        extraStrings = intent.getStringArrayExtra(Intent.EXTRA_BCC);
        if (extraStrings != null) {
            addBccAddresses(Arrays.asList(extraStrings));
        }

        String extraString = intent.getStringExtra(Intent.EXTRA_SUBJECT);
        if (extraString != null) {
            mSubject.setText(extraString);
        }

        for (String extra : ALL_EXTRAS) {
            if (intent.hasExtra(extra)) {
                String value = intent.getStringExtra(extra);
                if (EXTRA_TO.equals(extra)) {
                    addToAddresses(Arrays.asList(TextUtils.split(value, ",")));
                } else if (EXTRA_CC.equals(extra)) {
                    addCcAddresses(Arrays.asList(TextUtils.split(value, ",")), null);
                } else if (EXTRA_BCC.equals(extra)) {
                    addBccAddresses(Arrays.asList(TextUtils.split(value, ",")));
                } else if (EXTRA_SUBJECT.equals(extra)) {
                    mSubject.setText(value);
                } else if (EXTRA_BODY.equals(extra)) {
                    //[BUGFIX]-Add-BEGIN by SCDTABLET.yafang.wei,07/21/2016,2565329
                    // Modify to fix signature shows before body issue when share website by email
                    if (mBodyView.getText().toString().trim().equals(convertToPrintableSignature(mSignature).trim())) {
                        mBodyView.setText("");
                        setBody(value, true /* with signature */);
                        appendSignature();
                    } else {
                        setBody(value, true /* with signature */);
                    }
                    //[BUGFIX]-Add-END by SCDTABLET.yafang.wei
                } else if (EXTRA_QUOTED_TEXT.equals(extra)) {
                    initQuotedText(value, true /* shouldQuoteText */);
                }
            }
        }

        Bundle extras = intent.getExtras();
        //[BUGFIX]-MOD-BEGIN by TSNJ,wenlu.wu,10/20/2014,FR-739335
        if (extras != null && !mBodyAlreadySet) {
        //[BUGFIX]-MOD-END by TSNJ,wenlu.wu,10/20/2014,FR-739335
            CharSequence text = extras.getCharSequence(Intent.EXTRA_TEXT);
            //[BUGFIX]-Add-BEGIN by SCDTABLET.yafang.wei,07/21/2016,2565329
            // Modify to fix signature shows before body issue when share website by email
            if (mBodyView.getText().toString().trim().equals(convertToPrintableSignature(mSignature).trim())) {
                mBodyView.setText("");
                setBody((text != null) ? text : "", true /* with signature */);
                appendSignature();
            } else {
                setBody((text != null) ? text : "", true /* with signature */);
            }
            //[BUGFIX]-Add-END by SCDTABLET.yafang.wei

            // TODO - support EXTRA_HTML_TEXT
        }

        mExtraValues = intent.getParcelableExtra(EXTRA_VALUES);
        if (mExtraValues != null) {
            LogUtils.d(LOG_TAG, "Launched with extra values: %s", mExtraValues.toString());
            initExtraValues(mExtraValues);
            return true;
        }

        return false;
    }

    protected void initExtraValues(ContentValues extraValues) {
        // DO NOTHING - Gmail will override
    }


    @VisibleForTesting
    protected String decodeEmailInUri(String s) throws UnsupportedEncodingException {
        // TODO: handle the case where there are spaces in the display name as
        // well as the email such as "Guy with spaces <guy+with+spaces@gmail.com>"
        // as they could be encoded ambiguously.
        // Since URLDecode.decode changes + into ' ', and + is a valid
        // email character, we need to find/ replace these ourselves before
        // decoding.
        try {
            return URLDecoder.decode(replacePlus(s), UTF8_ENCODING_NAME);
        } catch (IllegalArgumentException e) {
            if (LogUtils.isLoggable(LOG_TAG, LogUtils.VERBOSE)) {
                LogUtils.e(LOG_TAG, "%s while decoding '%s'", e.getMessage(), s);
            } else {
                LogUtils.e(LOG_TAG, e, "Exception  while decoding mailto address");
            }
            return null;
        }
    }

    /**
     * Replaces all occurrences of '+' with "%2B", to prevent URLDecode.decode from
     * changing '+' into ' '
     *
     * @param toReplace Input string
     * @return The string with all "+" characters replaced with "%2B"
     */
    private static String replacePlus(String toReplace) {
        return toReplace.replace("+", "%2B");
    }

    /**
     * Replaces all occurrences of '%' with "%25", to prevent URLDecode.decode from
     * crashing on decoded '%' symbols
     *
     * @param toReplace Input string
     * @return The string with all "%" characters replaced with "%25"
     */
    private static String replacePercent(String toReplace) {
        return toReplace.replace("%", "%25");
    }

    /**
     * Helper function to encapsulate encoding/decoding string from Uri.getQueryParameters
     * @param content Input string
     * @return The string that's properly escaped to be shown in mail subject/content
     */
    private static String decodeContentFromQueryParam(String content) {
        try {
            return URLDecoder.decode(replacePlus(replacePercent(content)), UTF8_ENCODING_NAME);
        } catch (UnsupportedEncodingException e) {
            LogUtils.e(LOG_TAG, "%s while decoding '%s'", e.getMessage(), content);
            return "";  // Default to empty string so setText/setBody has same behavior as before.
        }
    }

    /**
     * Initialize the compose view from a String representing a mailTo uri.
     * @param mailToString The uri as a string.
     */
    public void initFromMailTo(String mailToString) {
        // We need to disguise this string as a URI in order to parse it
        // TODO:  Remove this hack when http://b/issue?id=1445295 gets fixed
        Uri uri = Uri.parse("foo://" + mailToString);
        int index = mailToString.indexOf("?");
        int length = "mailto".length() + 1;
        String to;
        try {
            // Extract the recipient after mailto:
            if (index == -1) {
                to = decodeEmailInUri(mailToString.substring(length));
            } else {
                to = decodeEmailInUri(mailToString.substring(length, index));
            }
            if (!TextUtils.isEmpty(to)) {
                addToAddresses(Arrays.asList(TextUtils.split(to, ",")));
            }
        } catch (UnsupportedEncodingException e) {
            if (LogUtils.isLoggable(LOG_TAG, LogUtils.VERBOSE)) {
                LogUtils.e(LOG_TAG, "%s while decoding '%s'", e.getMessage(), mailToString);
            } else {
                LogUtils.e(LOG_TAG, e, "Exception  while decoding mailto address");
            }
        }

        List<String> cc = uri.getQueryParameters("cc");
        addCcAddresses(Arrays.asList(cc.toArray(new String[cc.size()])), null);

        List<String> otherTo = uri.getQueryParameters("to");
        addToAddresses(Arrays.asList(otherTo.toArray(new String[otherTo.size()])));

        List<String> bcc = uri.getQueryParameters("bcc");
        addBccAddresses(Arrays.asList(bcc.toArray(new String[bcc.size()])));

        // NOTE: Uri.getQueryParameters already decodes % encoded characters
        List<String> subject = uri.getQueryParameters("subject");
        if (subject.size() > 0) {
            mSubject.setText(decodeContentFromQueryParam(subject.get(0)));
        }

        List<String> body = uri.getQueryParameters("body");
        if (body.size() > 0) {
            setBody(decodeContentFromQueryParam(body.get(0)), true /* with signature */);
            mBodyAlreadySet = true;//[BUGFIX]-ADD by TSNJ,wenlu.wu,10/20/2014,FR-739335
        }
    }

    // TS: zhaotianyong 2015-05-08 EMAIL BUGFIX_988459 MOD_S
    @VisibleForTesting
    protected void initAttachments(Message refMessage) {
        addAttachments(refMessage.getAttachments(), false);
    }

    protected void initAttachments (Message refMessage, boolean dropUnload) {
        addAttachments(refMessage.getAttachments(), dropUnload);
    }

    public long addAttachments(List<Attachment> attachments, boolean dropUnload) {
        long size = 0;
        AttachmentFailureException error = null;
        for (Attachment a : attachments) {
            try {
                // TS: zhaotianyong 2015-05-20 EMAIL BUGFIX-998884 ADD_S
                if (!supportSmartForward(mAccount) && !allAttachmentsLoad && dropUnload && !a.isPresentLocally()) {   //TS: zheng.zou 2015-11-03 EMAIL BUGFIX_858353 MOD
                // TS: zhaotianyong 2015-05-20 EMAIL BUGFIX-998884 ADD_E
                    continue;
                }
                size += mAttachmentsView.addAttachment(mAccount, a, false);//TS: yanhua.chen 2015-6-8 EMAIL CR_996908 MOD
                //TS: yanhua.chen 2015-7-29 EMAIL BUGFIX_1053132 ADD_S
                if(size > Settings.DEFAULT_MID_ATTACHMENT_SIZE && attLargeWarning){
                    attLargeWarning = false;
                    showErrorToast(getResources().getString(R.string.too_large_to_mid_attach_additional,
                            AttachmentUtils.convertToHumanReadableSize(this, Settings.DEFAULT_MID_ATTACHMENT_SIZE)));
                }
                //TS: yanhua.chen 2015-7-29 EMAIL BUGFIX_1053132 ADD_E
            } catch (AttachmentFailureException e) {
                error = e;
            }
        }
        if (error != null) {
            LogUtils.e(LOG_TAG, error, "Error adding attachment");
            if (attachments.size() > 1) {
                showAttachmentTooBigToast(R.string.too_large_to_attach_multiple);
            } else {
                showAttachmentTooBigToast(error.getErrorRes());
            }
        }
        return size;
    }
    // TS: zhaotianyong 2015-05-08 EMAIL BUGFIX_988459 MOD_E

    /**
     * When an attachment is too large to be added to a message, show a toast.
     * This method also updates the position of the toast so that it is shown
     * clearly above they keyboard if it happens to be open.
     */
    private void showAttachmentTooBigToast(int errorRes) {
        String maxSize = AttachmentUtils.convertToHumanReadableSize(
                getApplicationContext(), mAccount.settings.getMaxAttachmentSize());
        showErrorToast(getString(errorRes, maxSize));
    }
    //TS: kaifeng.lu 2016-3-25 EMAIL BUGFIX_1841392 MOD_S
    private void showErrorToast(String message) {
//        Toast t = Toast.makeText(this, message, Toast.LENGTH_LONG);
//        t.setText(message);
//        t.show();
        if(mToastBar != null) {
         mToastBar.show(null, message, 0, true, null);
        }
    }
    //TS: kaifeng.lu 2016-3-25 EMAIL BUGFIX_1841392 MOD_E

    private void initAttachmentsFromIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras == null) {
            extras = Bundle.EMPTY;
        }
        final String action = intent.getAction();
        if (!mAttachmentsChanged) {
            long totalSize = 0;
            if (extras.containsKey(EXTRA_ATTACHMENTS)) {
                String[] uris = (String[]) extras.getSerializable(EXTRA_ATTACHMENTS);
                for (String uriString : uris) {
                    final Uri uri = Uri.parse(uriString);
                    long size = 0;
                    try {
                        if (handleSpecialAttachmentUri(uri)) {
                            continue;
                        }

                        final Attachment a = mAttachmentsView.generateLocalAttachment(uri);
                        //TS: rong-tang 2016-03-02 EMAIL BUGFIX-1712549 ADD_S
                        if (a == null) {
                            continue;
                        }
                        //TS: rong-tang 2016-03-02 EMAIL BUGFIX-1712549 ADD_E
                        size = mAttachmentsView.addAttachment(mAccount, a, true);//TS: yanhua.chen 2015-6-8 EMAIL CR_996908 MOD

                        Analytics.getInstance().sendEvent("send_intent_attachment",
                                Utils.normalizeMimeType(a.getContentType()), null, size);

                    } catch (AttachmentFailureException e) {
                        LogUtils.e(LOG_TAG, e, "Error adding attachment");
                        showAttachmentTooBigToast(e.getErrorRes());
                    }
                    totalSize += size;
                }
            }
            if (extras.containsKey(Intent.EXTRA_STREAM)) {
                if (!PermissionUtil.checkAndRequestPermissionForResult(this,Manifest.permission.READ_EXTERNAL_STORAGE
                        ,PermissionUtil.REQ_CODE_PERMISSION_ADD_ATTACHMENT)){
                    return;
                }
                    if (Intent.ACTION_SEND_MULTIPLE.equals(action)) {
                    final ArrayList<Uri> uris = extras
                            .getParcelableArrayList(Intent.EXTRA_STREAM);
                    ArrayList<Attachment> attachments = new ArrayList<Attachment>();
                    for (Uri uri : uris) {
                        if (uri == null) {
                            continue;
                        }
                        try {
                            if (handleSpecialAttachmentUri(uri)) {
                                continue;
                            }

                            final Attachment a = mAttachmentsView.generateLocalAttachment(uri);
                            //TS: rong-tang 2016-03-02 EMAIL BUGFIX-1712549 ADD_S
                            if (a == null) {
                                continue;
                            }
                            //TS: rong-tang 2016-03-02 EMAIL BUGFIX-1712549 ADD_E
                            attachments.add(a);

                            Analytics.getInstance().sendEvent("send_intent_attachment",
                                    Utils.normalizeMimeType(a.getContentType()), null, a.size);

                        } catch (AttachmentFailureException e) {
                            LogUtils.e(LOG_TAG, e, "Error adding attachment");
                            String maxSize = AttachmentUtils.convertToHumanReadableSize(
                                    getApplicationContext(),
                                    mAccount.settings.getMaxAttachmentSize());
                            showErrorToast(getString
                                    (R.string.generic_attachment_problem, maxSize));
                        }
                    }
                    // TS: zhaotianyong 2015-05-08 EMAIL BUGFIX_988459 MOD_S
                    totalSize += addAttachments(attachments, false);
                    // TS: zhaotianyong 2015-05-08 EMAIL BUGFIX_988459 MOD_E
                } else {
                    final Uri uri = extras.getParcelable(Intent.EXTRA_STREAM);
                    if (uri != null) {
                        long size = 0;
                        //[BUGFIX]-Modified-BEGIN by TCTNJ.wenlu.wu,12/03/2014,PR-857886
                        if (!handleSpecialAttachmentUri(uri)) {

                            new AsyncTask<Void, Void, Void>() {
                                @Override
                                protected Void doInBackground(Void... params) {
                                    try {
                                        Attachment mAttachment = mAttachmentsView.generateLocalAttachment(uri);
                                        android.os.Message msg = new android.os.Message();
                                        msg.what = 1001;
                                        msg.obj = mAttachment;
                                        mHandler.sendMessage(msg);
                                    } catch (AttachmentFailureException e) {
                                        LogUtils.e(LOG_TAG, e, "Error adding attachment");
                                        showAttachmentTooBigToast(e.getErrorRes());
                                    }
                                    return null;
                                }

                                @Override
                                protected void onPostExecute(Void result) {

                                }
                            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        }
                        //[BUGFIX]-Modified-END by TCTNJ.wenlu.wu,12/03/2014,PR-857886

                        totalSize += size;
                    }
                }
            }

            if (totalSize > 0) {
                mAttachmentsChanged = true;
                updateSaveUi();

                Analytics.getInstance().sendEvent("send_intent_with_attachments",
                        Integer.toString(getAttachments().size()), null, totalSize);
            }
        }
    }

    private int getShareAttachmentSize(Intent intent){
        Bundle extras = intent.getExtras();
        if (extras == null) {
            extras = Bundle.EMPTY;
        }
        final String action = intent.getAction();
        int totalSize = 0;
        if (Intent.ACTION_SEND_MULTIPLE.equals(action)) {
            final ArrayList<Uri> uris = extras
                    .getParcelableArrayList(Intent.EXTRA_STREAM);
            ArrayList<Attachment> attachments = new ArrayList<Attachment>();
            for (Uri uri : uris) {
                if (uri == null) {
                    continue;
                }
                try {
                    if (handleSpecialAttachmentUri(uri)) {
                        continue;
                    }

                    final Attachment a = mAttachmentsView.generateLocalAttachment(uri);
                    //TS: rong-tang 2016-03-02 EMAIL BUGFIX-1712549 ADD_S
                    if (a == null) {
                        continue;
                    }
                    //TS: rong-tang 2016-03-02 EMAIL BUGFIX-1712549 ADD_E
                    attachments.add(a);

                    Analytics.getInstance().sendEvent("send_intent_attachment",
                            Utils.normalizeMimeType(a.getContentType()), null, a.size);

                } catch (AttachmentFailureException e) {
                    LogUtils.e(LOG_TAG, e, "Error adding attachment");
                    String maxSize = AttachmentUtils.convertToHumanReadableSize(
                            getApplicationContext(),
                            mAccount.settings.getMaxAttachmentSize());
                    showErrorToast(getString
                            (R.string.generic_attachment_problem, maxSize));
                }
            }
            // TS: zhaotianyong 2015-05-08 EMAIL BUGFIX_988459 MOD_S
            totalSize += addAttachments(attachments, false);
            // TS: zhaotianyong 2015-05-08 EMAIL BUGFIX_988459 MOD_E
        } else {
            final Uri uri = extras.getParcelable(Intent.EXTRA_STREAM);
            if (uri != null) {
                long size = 0;
                //[BUGFIX]-Modified-BEGIN by TCTNJ.wenlu.wu,12/03/2014,PR-857886
                if (!handleSpecialAttachmentUri(uri)) {

                    new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... params) {
                            try {
                                Attachment mAttachment = mAttachmentsView.generateLocalAttachment(uri);
                                android.os.Message msg = new android.os.Message();
                                msg.what = ADD_ATTACHMENT_MSG;
                                msg.obj = mAttachment;
                                mHandler.sendMessage(msg);
                            } catch (AttachmentFailureException e) {
                                LogUtils.e(LOG_TAG, e, "Error adding attachment");
                                // TS: jian.xu 2016-01-11 EMAIL BUGFIX-1307962 MOD_S
                                //Note: show toast must be on ui thread.
                                android.os.Message errorMsg = new android.os.Message();
                                errorMsg.what = ADD_ATTACHMENT_MSG_ERROR;
                                errorMsg.obj = e.getErrorRes();
                                mHandler.sendMessage(errorMsg);
                                //showAttachmentTooBigToast(e.getErrorRes());
                                // TS: jian.xu 2016-01-11 EMAIL BUGFIX-1307962 MOD_E
                            }
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void result) {

                        }
                    }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
                //[BUGFIX]-Modified-END by TCTNJ.wenlu.wu,12/03/2014,PR-857886

                totalSize += size;
            }
        }
        return totalSize;
    }
    protected void initQuotedText(CharSequence quotedText, boolean shouldQuoteText) {
        mQuotedTextView.setQuotedTextFromHtml(quotedText, shouldQuoteText);
        mShowQuotedText = true;
    }

    private void initQuotedTextFromRefMessage(Message refMessage, int action) {
        if (mRefMessage != null && (action == REPLY || action == REPLY_ALL || action == FORWARD)) {
            mQuotedTextView.setQuotedText(action, refMessage, action != FORWARD);
        }
    }

    private void updateHideOrShowCcBcc() {
        // Its possible there is a menu item OR a button.
        boolean ccVisible = mCcBccView.isCcVisible();
        boolean bccVisible = mCcBccView.isBccVisible();
        if (mCcBccButton != null) {
            if (!ccVisible || !bccVisible) {
                mCcBccButton.setVisibility(View.VISIBLE);

                //[BUGFIX]-Add-BEGIN by SCDTABLET.weiwei.huang,05/03/2016,2013739,
                //[Email]Add mail contact icon display is not consistent
                bccButtonImg.setVisibility(View.VISIBLE);
                ccButtonImg.setVisibility(View.VISIBLE);
                //[BUGFIX]-Add-END by SCDTABLET.weiwei.huang

            } else {
                mCcBccButton.setVisibility(View.GONE);

                //[BUGFIX]-Add-BEGIN by SCDTABLET.weiwei.huang,05/03/2016,2013739,
                //[Email]Add mail contact icon display is not consistent
                bccButtonImg.setVisibility(View.GONE);
                ccButtonImg.setVisibility(View.GONE);
                //[BUGFIX]-Add-END by SCDTABLET.weiwei.huang
            }
        }
    }

    /**
     * Add attachment and update the compose area appropriately.
     */
    private void addAttachmentAndUpdateView(Intent data) {
        if (data == null) {
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            final ClipData clipData = data.getClipData();
            if (clipData != null) {
                for (int i = 0, size = clipData.getItemCount(); i < size; i++) {
                    addAttachmentAndUpdateView(clipData.getItemAt(i).getUri());
                }
                return;
            }
        }

        addAttachmentAndUpdateView(data.getData());
    }

    private void addAttachmentAndUpdateView(Uri contentUri) {
        if (contentUri == null) {
            return;
        }
        try {
            //TS: zheng.zou 2015-3-21 EMAIL BUGFIX_949589 ADD_S
            if (handleLongRunAttachmentUri(contentUri)){
                return;
            }
            //TS: zheng.zou 2015-3-21 EMAIL BUGFIX_949589 ADD_E

            if (handleSpecialAttachmentUri(contentUri)) {
                return;
            }
            //TS: wenggangjin 2015-02-03 EMAIL BUGFIX_-920087 MOD_S
          //TS: wenggangjin 2015-02-28 EMAIL BUGFIX_-935421 MOD_S
//            if(!TctDrmManager.isAllowForward(getFilePath(contentUri))){
            if(getFilePath(contentUri) != null && !"".equals(getFilePath(contentUri)) && !TctDrmManager.isAllowForward(getFilePath(contentUri))){
                 //TS: kaifeng.lu 2016-3-25 EMAIL BUGFIX_1841392 MOD_S
//                Toast.makeText(this, R.string.drm_file_remind, Toast.LENGTH_LONG).show();
                showErrorToast(getResources().getString(R.string.drm_file_remind));
                //TS: kaifeng.lu 2016-3-25 EMAIL BUGFIX_1841392 MOD_E
                return;
            }
          //TS: wenggangjin 2015-02-28 EMAIL BUGFIX_-935421 MOD_E
            //TS: wenggangjin 2015-02-03 EMAIL BUGFIX_-920087 MOD_S
            addAttachmentAndUpdateView(mAttachmentsView.generateLocalAttachment(contentUri),false);   //TS: zheng.zou 2015-05-28 EMAIL BUGFIX_-997631 MOD
        } catch (AttachmentFailureException e) {
            LogUtils.e(LOG_TAG, e, "Error adding attachment");
            showErrorToast(getResources().getString(
                    e.getErrorRes(),
                    AttachmentUtils.convertToHumanReadableSize(
                            getApplicationContext(), mAccount.settings.getMaxAttachmentSize())));
        }
    }

    /**
     * Allow subclasses to implement custom handling of attachments.
     *
     * @param contentUri a passed-in URI from a pick intent
     * @return true iff handled
     */
    protected boolean handleSpecialAttachmentUri(final Uri contentUri) {
        return false;
    }

    //TS: zheng.zou 2015-3-21 EMAIL BUGFIX_949589 ADD_S
    protected boolean handleLongRunAttachmentUri(final Uri contentUri){
        if ("com.google.android.apps.photos.content".equals(contentUri.getAuthority())){
            new LoadAttachmentTask().execute(contentUri);
            return true;
        }
        return false;
    }

    class LoadAttachmentTask extends AsyncTask<Uri, Void, Integer> {

        private boolean isCanceled;
        private static final int RESULT_SUCCESS = 0;
        private static final int RESULT_CANCELED = -1;
        private static final int RESULT_FAIL_DRM = -2;

        @Override
        protected void onPreExecute() {
            ProgressDialogFragment dialog = ProgressDialogFragment.showDialog(getFragmentManager(), getString(R.string.add_file_attachment));
            dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    isCanceled = true;
                }
            });
        }

        @Override
        protected Integer doInBackground(Uri... params) {
            Uri contentUri = params[0];
            if (contentUri != null) {
                try {
                    if(getFilePath(contentUri) != null && !"".equals(getFilePath(contentUri)) && !TctDrmManager.isAllowForward(getFilePath(contentUri))){
                        return RESULT_FAIL_DRM;
                    }
                    Attachment mAttachment = mAttachmentsView.generateLocalAttachment(contentUri);
                    if (isCanceled) {
                        return RESULT_CANCELED;
                    }
                    android.os.Message msg = new android.os.Message();
                    msg.what = 1001;
                    msg.obj = mAttachment;
                    mHandler.sendMessage(msg);
                } catch (AttachmentFailureException e) {
                    LogUtils.e(LOG_TAG, e, "Error adding attachment");
                    showAttachmentTooBigToast(e.getErrorRes());
                }
            }
            return RESULT_SUCCESS;
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            ProgressDialogFragment.dismissDialog(getFragmentManager(), getString(R.string.add_file_attachment));
            if (result == RESULT_FAIL_DRM){
                //TS: kaifeng.lu 2016-3-25 EMAIL BUGFIX_1841392 MOD_S
//                Toast.makeText(ComposeActivity.this, R.string.drm_file_remind, Toast.LENGTH_LONG).show();
                showErrorToast(getResources().getString(R.string.drm_file_remind));
                //TS: kaifeng.lu 2016-3-25 EMAIL BUGFIX_1841392 MOD_E
            }

        }
    }
    //TS: zheng.zou 2015-3-21 EMAIL BUGFIX_949589 ADD_E

    private void addAttachmentAndUpdateView(Attachment attachment, boolean isInit) {  //TS: zheng.zou 2015-05-28 EMAIL BUGFIX_-997631 MOD
        try {
            //TS: rong-tang 2016-03-02 EMAIL BUGFIX-1712549 ADD_S
            if (attachment == null) {
                return;
            }
            //TS: rong-tang 2016-03-02 EMAIL BUGFIX-1712549 ADD_E
            long size = mAttachmentsView.addAttachment(mAccount, attachment, isInit);//TS: yanhua.chen 2015-6-8 EMAIL CR_996908 MOD
            if (size > 0) {
                //TS: zheng.zou 2015-05-28 EMAIL BUGFIX_-997631 MOD_S
                if (!isInit) {
                    mAttachmentsChanged = true;
                }
                //TS: zheng.zou 2015-05-28 EMAIL BUGFIX_-997631 MOD_E
                updateSaveUi();
            }
            //TS: yanhua.chen 2015-7-29 EMAIL BUGFIX_1053132 ADD_S
            long total = getTotalSize();
            if(total > Settings.DEFAULT_MID_ATTACHMENT_SIZE && attLargeWarning){
                attLargeWarning = false;
                showErrorToast(getResources().getString(R.string.too_large_to_mid_attach_additional,
                        AttachmentUtils.convertToHumanReadableSize(this, Settings.DEFAULT_MID_ATTACHMENT_SIZE)));
            }
            //TS: yanhua.chen 2015-7-29 EMAIL BUGFIX_1053132 ADD_E
        } catch (AttachmentFailureException e) {
            LogUtils.e(LOG_TAG, e, "Error adding attachment");
            showAttachmentTooBigToast(e.getErrorRes());
        }
    }

    void initRecipientsFromRefMessage(Message refMessage, int action) {
        // Don't populate the address if this is a forward.
        if (action == ComposeActivity.FORWARD) {
            return;
        }
        initReplyRecipients(refMessage, action);
    }

    // TODO: This should be private.  This method shouldn't be used by ComposeActivityTests, as
    // it doesn't setup the state of the activity correctly
    @VisibleForTesting
    void initReplyRecipients(final Message refMessage, final int action) {
        // TS: junwei-xu 2015-03-25 EMAIL BUGFIX-958270 ADD_S
        final String[] sentToAddresses = refMessage.getToAddressesUnescaped();
        // TS: junwei-xu 2015-03-25 EMAIL BUGFIX-958270 ADD_E
        final Collection<String> toAddresses;
        final String[] fromAddresses = refMessage.getFromAddressesUnescaped();
        final String fromAddress = fromAddresses.length > 0 ? fromAddresses[0] : null;
        final String[] replyToAddresses = getReplyToAddresses(
                refMessage.getReplyToAddressesUnescaped(), fromAddress);

        // If this is a reply, the Cc list is empty. If this is a reply-all, the
        // Cc list is the union of the To and Cc recipients of the original
        // message, excluding the current user's email address and any addresses
        // already on the To list.
        // TS: junwei-xu 2015-05-13 EMAIL BUGFIX-1000343 MOD_S
        if (action == ComposeActivity.REPLY) {
            toAddresses = initToRecipients(fromAddress, replyToAddresses, sentToAddresses, action);
            addToAddresses(toAddresses);
            // TS: chenyanhua 2015-01-07 EMAIL BUGFIX-879794 ADD_S
            addBccMySelf(mAccount);
            // TS: chenyanhua 2015-01-07 EMAIL BUGFIX-879794 ADD_E
        } else if (action == ComposeActivity.REPLY_ALL) {
            // TS: zheng.zou 2015-06-08 EMAIL BUGFIX-1009174 MOD_S
            //note: use list instead of set to keep the add order.
//            final Set<String> ccAddresses = Sets.newHashSet();
            final List<String> ccAddresses = new ArrayList<>();
            // TS: zheng.zou 2015-06-08 EMAIL BUGFIX-1009174 MOD_E
            toAddresses = initToRecipients(fromAddress, replyToAddresses, sentToAddresses, action);
            // TS: Gantao 2015-06-16 EMAIL BUGFIX-1019238 ADD_S
            // TS: Gantao 2015-06-24 EMAIL BUGFIX-1029207 MOD_S
            if (mSelectedAccountUnusual) {
             // delay to add to list
                mReplyToRecipientsLisnter = new ReplyToRecipientsListener() {
                    @Override
                    public void addToRecipients() {
                        addRecipients(toAddresses, sentToAddresses);
                        addToAddresses(toAddresses);
                    }
                };
            } else {
                addRecipients(toAddresses, sentToAddresses);
                addToAddresses(toAddresses);
            }
            // TS: Gantao 2015-06-24 EMAIL BUGFIX-1029207 MOD_E
            // TS: Gantao 2015-06-16 EMAIL BUGFIX-1019238 ADD_E
           // TS: junwei-xu 2015-03-25 EMAIL BUGFIX-958270 ADD_S
//          addRecipients(ccAddresses, sentToAddresses);
//          addRecipients(ccAddresses, refMessage.getCcAddressesUnescaped());
//          addCcAddresses(ccAddresses, toAddresses);
            if (mAccount != null && mSelectedAccountUnusual) {// TS: Gantao 2015-06-18 EMAIL BUGFIX-1020548 MOD
                // delay to add cc list
                mReplyRecipientslistener = new ReplyRecipientsListener() {
                    public void addCCRecipients() {
                        //addRecipients(ccAddresses, sentToAddresses);
                        addRecipients(ccAddresses, refMessage.getCcAddressesUnescaped());
                        addCcAddresses(ccAddresses, toAddresses);
                    }
                };
            } else {
                //addRecipients(ccAddresses, sentToAddresses);
                addRecipients(ccAddresses, refMessage.getCcAddressesUnescaped());
                addCcAddresses(ccAddresses, toAddresses);
            }
            // TS: junwei-xu 2015-03-25 EMAIL BUGFIX-958270 ADD_E
            // TS: chenyanhua 2015-01-07 EMAIL BUGFIX-879794 ADD_S
            addBccMySelf(mAccount);
            // TS: chenyanhua 2015-01-07 EMAIL BUGFIX-879794 ADD_E
        }
        // TS: junwei-xu 2015-05-13 EMAIL BUGFIX-1000343 MOD_E
    }

    // If there is no reply to address, the reply to address is the sender.
    private static String[] getReplyToAddresses(String[] replyTo, String from) {
        boolean hasReplyTo = false;
        for (final String replyToAddress : replyTo) {
            if (!TextUtils.isEmpty(replyToAddress)) {
                hasReplyTo = true;
            }
        }
        return hasReplyTo ? replyTo : new String[] {from};
    }

    private void addToAddresses(Collection<String> addresses) {
        addAddressesToList(addresses, mTo);
    }

    private void addCcAddresses(Collection<String> addresses, Collection<String> toAddresses) {
        addCcAddressesToList(tokenizeAddressList(addresses),
                toAddresses != null ? tokenizeAddressList(toAddresses) : null, mCc);
    }

    private void addBccAddresses(Collection<String> addresses) {
        addAddressesToList(addresses, mBcc);
    }

    @VisibleForTesting
    protected void addCcAddressesToList(List<Rfc822Token[]> addresses,
            List<Rfc822Token[]> compareToList, RecipientEditTextView list) {
        String address;

        if (compareToList == null) {
            for (final Rfc822Token[] tokens : addresses) {
                for (final Rfc822Token token : tokens) {
                    address = token.toString();
                    list.append(address + END_TOKEN);
                }
            }
        } else {
            HashSet<String> compareTo = convertToHashSet(compareToList);
            for (final Rfc822Token[] tokens : addresses) {
                for (final Rfc822Token token : tokens) {
                    address = token.toString();
                    // Check if this is a duplicate:
                    if (!compareTo.contains(token.getAddress())) {
                        // Get the address here
                        list.append(address + END_TOKEN);
                    }
                }
            }
        }
    }

    private static HashSet<String> convertToHashSet(final List<Rfc822Token[]> list) {
        final HashSet<String> hash = new HashSet<String>();
        for (final Rfc822Token[] tokens : list) {
            for (final Rfc822Token token : tokens) {
                hash.add(token.getAddress());
            }
        }
        return hash;
    }

    protected List<Rfc822Token[]> tokenizeAddressList(Collection<String> addresses) {
        @VisibleForTesting
        List<Rfc822Token[]> tokenized = new ArrayList<Rfc822Token[]>();

        for (String address: addresses) {
            tokenized.add(Rfc822Tokenizer.tokenize(address));
        }
        return tokenized;
    }

    @VisibleForTesting
    void addAddressesToList(Collection<String> addresses, RecipientEditTextView list) {
        for (String address : addresses) {
            addAddressToList(address, list);
        }
    }
    //TS:kaifeng.lu 2015-10-28 EMAIL BUGFIX_824294 ADD_S
    void addAddressesToList(final long[] contactIds, final RecipientEditTextView list) {
        list.requestFocus(); // request the focus
        new EmailContactTask(list, contactIds)
                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    class EmailContactTask extends AsyncTask<Long, ArrayList<String>, ArrayList<String>> {
        RecipientEditTextView mView;
        long[] mids;

        EmailContactTask(RecipientEditTextView view, long[] ids) {
            this.mView = view;
            this.mids = ids;
        }

        @Override
        protected ArrayList<String> doInBackground(Long... params) {
            // TODO Auto-generated method stub         1266
            if (mids == null || mids.length <= 0 || this.mView == null) {
                return null;
            }
            ArrayList<String> StringAddress = new ArrayList<String>();
            ArrayList<Rfc822Token> addresses = getEmailAddressesFromContacts(mids);

            for (Rfc822Token token : addresses) {
                if (token != null)
                    //TS: junwei-xu 2015-12-22 EMAIL BUGFIX-1181863 MOD_S
                    //Note: use structure of name<address>
                    StringAddress.add(token.toString());
                    //TS: junwei-xu 2015-12-22 EMAIL BUGFIX-1181863 MOD_E
            }

            return StringAddress;
        }

        @Override
        protected void onPostExecute(ArrayList<String> result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            if (result == null || result.size() <= 0) {
                return;
            }

            for (int i = 0; i < result.size(); i++) {
                if (this.mView instanceof MultiAutoCompleteTextView)
                    addAddressToList(result.get(i), mView);
            }

        }

        private ArrayList<Rfc822Token> getEmailAddressesFromContacts(long[] contactIds) {
            ArrayList<Rfc822Token> addresses = new ArrayList<Rfc822Token>();
            if (contactIds == null || contactIds.length <= 0) {
                return addresses;
            }
            StringBuilder selection = new StringBuilder();
            selection.append(ContactsContract.CommonDataKinds.Email._ID);
            selection.append(" IN (");
            selection.append(contactIds[0]);
            for (int i = 1; i < contactIds.length; i++) {
                selection.append(",");
                selection.append(contactIds[i]);
            }
            selection.append(")");
            Cursor c = null;
            try {
                c = getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                        new String[]{ContactsContract.CommonDataKinds.Email.ADDRESS, ContactsContract.Data.DISPLAY_NAME},
                        selection.toString(), null, null);
                if (c == null) {
                    return addresses;
                }
                String itemName, itemAddress;
                Rfc822Token token;
                while (c.moveToNext()) {
                    //TS: junwei-xu 2015-12-22 EMAIL BUGFIX-1181863 MOD_S
                    itemName = c.getString(1);
                    itemAddress = c.getString(0);
                    //TS: rong-tang 2016-03-28 EMAIL BUGFIX-1863457 MOD_S
                    itemName = Rfc822Validator.fixInvalidName(itemName);
                    //TS: rong-tang 2016-03-28 EMAIL BUGFIX-1863457 MOD_E
                    token = new Rfc822Token(itemName, itemAddress, null);
                    addresses.add(token);
                    //TS: junwei-xu 2015-12-22 EMAIL BUGFIX-1181863 MOD_E
                }
                return addresses;
            } finally {
                if (c != null) {
                    c.close();
                }
            }
        }

    }
    //TS:kaifeng.lu 2015-10-28 EMAIL BUGFIX_824294 ADD_E

    private static void addAddressToList(final String address, final RecipientEditTextView list) {
        if (address == null || list == null)
            return;

        final Rfc822Token[] tokens = Rfc822Tokenizer.tokenize(address);

        for (final Rfc822Token token : tokens) {
            list.append(token + END_TOKEN);
        }
    }

    // TS: junwei-xu 2015-05-13 EMAIL BUGFIX-1000343 ADD_S
    @VisibleForTesting
    protected Collection<String> initToRecipients(final String fullSenderAddress,
            final String[] replyToAddresses, final String[] inToAddresses, final int action) {
        // The To recipient is the reply-to address specified in the original
        // message, unless it is:
        // the current user OR a custom from of the current user, in which case
        // it's the To recipient list of the original message.
        // OR missing, in which case use the sender of the original message
        // TS: zheng.zou 2015-06-08 EMAIL BUGFIX-1009174 MOD_S
        //note: use list instead of set to keep the add order.
//        Set<String> toAddresses = Sets.newHashSet();
        List<String> toAddresses = new ArrayList<>();
        // TS: zheng.zou 2015-06-08 EMAIL BUGFIX-1009174 MOD_E
        for (final String replyToAddress : replyToAddresses) {
            if (!TextUtils.isEmpty(replyToAddress)
                    // TS: Gantao 2015-06-09 EMAIL BUGFIX-1019278 MOD
                    && !recipientMatchesThisAccount(replyToAddress) ||mSelectedAccountUnusual) {
                toAddresses.add(replyToAddress);
            }
        }
     // TS: Gantao 2015-06-16 EMAIL BUGFIX-1019238 DEL_S
//        if (action == ComposeActivity.REPLY_ALL) {
//            for (String address : inToAddresses) {
//                if (!TextUtils.isEmpty(address)
//                        && !recipientMatchesThisAccount(address)) {
//                    toAddresses.add(address);
//                }
//            }
//        }
     // TS: Gantao 2015-06-16 EMAIL BUGFIX-1019238 DEL_E
        if (toAddresses.size() == 0) {
            // In this case, the user is replying to a message in which their
            // current account or some of their custom from addresses are the only
            // recipients and they sent the original message.
            if (inToAddresses.length == 1 && recipientMatchesThisAccount(fullSenderAddress)
                    && recipientMatchesThisAccount(inToAddresses[0])) {
                toAddresses.add(inToAddresses[0]);
                return toAddresses;
            }
            // This happens if the user replies to a message they originally
            // wrote. In this case, "reply" really means "re-send," so we
            // target the original recipients. This works as expected even
            // if the user sent the original message to themselves.
            for (String address : inToAddresses) {
                if (!recipientMatchesThisAccount(address)) {
                    toAddresses.add(address);
                }
            }
        }
        return toAddresses;
    }
    // TS: junwei-xu 2015-05-13 EMAIL BUGFIX-1000343 ADD_E

    private void addRecipients(final Collection<String> recipients, final String[] addresses) {   // TS: zheng.zou 2015-06-08 EMAIL BUGFIX-1009174 MOD
        for (final String email : addresses) {
            // Do not add this account, or any of its custom from addresses, to
            // the list of recipients.
            final String recipientAddress = Address.getEmailAddress(email).getAddress();
            if (!recipientMatchesThisAccount(recipientAddress)) {
                recipients.add(email.replace("\"\"", ""));
            }
        }
    }

    /**
     * A recipient matches this account if it has the same address as the
     * currently selected account OR one of the custom from addresses associated
     * with the currently selected account.
     * @param recipientAddress address we are comparing with the currently selected account
     */
    protected boolean recipientMatchesThisAccount(String recipientAddress) {
        return ReplyFromAccount.matchesAccountOrCustomFrom(mAccount, recipientAddress,
                        mAccount.getReplyFroms());
    }

    /**
     * Returns a formatted subject string with the appropriate prefix for the action type.
     * E.g., "FWD: " is prepended if action is {@link ComposeActivity#FORWARD}.
     */
    public static String buildFormattedSubject(Resources res, String subject, int action) {
        final String prefix;
        final String correctedSubject;
        if (action == ComposeActivity.COMPOSE) {
            prefix = "";
        } else if (action == ComposeActivity.FORWARD) {
            prefix = res.getString(R.string.forward_subject_label);
        } else {
            prefix = res.getString(R.string.reply_subject_label);
        }

        if (TextUtils.isEmpty(subject)) {
            correctedSubject = prefix;
        } else {
            // Don't duplicate the prefix
            if (subject.toLowerCase().startsWith(prefix.toLowerCase())) {
                correctedSubject = subject;
            } else {
                correctedSubject = String.format(
                        res.getString(R.string.formatted_subject), prefix, subject);
            }
        }

        return correctedSubject;
    }

    private void setSubject(Message refMessage, int action) {
        mSubject.setText(buildFormattedSubject(getResources(), refMessage.subject, action));
    }

    private void initRecipients() {
        setupRecipients(mTo);
        setupRecipients(mCc);
        setupRecipients(mBcc);
    }

    private void setupRecipients(RecipientEditTextView view) {
        final DropdownChipLayouter layouter = getDropdownChipLayouter();
        if (layouter != null) {
            view.setDropdownChipLayouter(layouter);
        }
        view.setAdapter(getRecipientAdapter());
        view.setRecipientEntryItemClickedListener(this);
        if (mValidator == null) {
            final String accountName = mAccount.getEmailAddress();
            int offset = accountName.indexOf("@") + 1;
            String account = accountName;
            if (offset > 0) {
                account = account.substring(offset);
            }
            mValidator = new Rfc822Validator(account);
        }
        view.setValidator(mValidator);
      //[FEATURE]-Add-BEGIN by TSCD.chao zhang,04/25/2014,FR 631895(porting from  FR487417)
      addBccMySelf(mAccount);
      //after added the address,we display the cc and bcc field.
      if(mAddBccBySetting &&(mBcc!=null && view == mBcc) && !mCcBccView.isBccVisible()){
        showCcBccViews();
      }
      //[FEATURE]-Add-END by TSCD.chao zhang
    }

    /**
     * Derived classes should override if they wish to provide their own autocomplete behavior.
     */
    public BaseRecipientAdapter getRecipientAdapter() {
        return new RecipientAdapter(this, mAccount);
    }

    /**
     * Derived classes should override this to provide their own dropdown behavior.
     * If the result is null, the default {@link com.tct.ex.chips.DropdownChipLayouter}
     * is used.
     */
    public DropdownChipLayouter getDropdownChipLayouter() {
        return null;
    }

    @Override
    public void onClick(View v) {
        int requestCode = -1;//[FEATURE]-Add by TCTNB.chen caixia,09/11/2013,FR 487417
        final int id = v.getId();
        if (id == R.id.add_cc_bcc) {
            // Verify that cc/ bcc aren't showing.
            // Animate in cc/bcc.
            showCcBccViews();
        // TS: junwei-xu 2015-09-01 EMAIL BUGFIX-526192 ADD_S
        } else if (id == R.id.priority_icon) {
            setPriority();
        }
        // TS: junwei-xu 2015-09-01 EMAIL BUGFIX-526192 ADD_E
        //[FEATURE]-Add-BEGIN by TSCD.chao zhang,04/21/2014,FR631895(porting from FR487417)
        else if (id == R.id.to_recipients_picker) {
            //TS: rong-tang 2016-04-06 EMAIL-1840992 ADD_S
            //Note: when click picker, request focus to picker, recipient edit text view need to submit change.
            //[BUGFIX]-Mod-BEGIN by SCDTABLET.aijian.shi,09/07/2016,2848507,
            // Use clearFocus to avoid the improper focus after entering the recipient edit text view.
            //mToPicker.requestFocusFromTouch();
            mTo.clearFocus();
            //[BUGFIX]-Mod-END by SCDTABLET.aijian.shi
            //TS: rong-tang 2016-04-06 EMAIL-1840992 ADD_E
            mLaunchContact = true;   //TS: xinlei.sheng 2015-01-26 EMAIL FIXBUG_886976 ADD
            requestCode = ACTIVITY_REQUEST_PICK_CONTACT_TO;
            Intent toPickEmail = createAddContactIntent(mTo);
            //TS: jian.xu 2015-09-17 EMAIL-1085945 MOD_S
            try {
                startActivityForResult(toPickEmail, requestCode);
            } catch (ActivityNotFoundException e) {
                LogUtils.e(LOG_TAG, e, "Exception attempting to choose person from contact app");
            }
        } else if (id == R.id.cc_recipients_picker) {
            //TS: rong-tang 2016-04-06 EMAIL-1840992 ADD_S
            //Note: when click picker, request focus to picker, recipient edit text view need to submit change.
            //[BUGFIX]-Mod-BEGIN by SCDTABLET.aijian.shi,09/07/2016,2848507,
            // Use clearFocus to avoid the improper focus after entering the recipient edit text view.
            //mCcPicker.requestFocusFromTouch();
            mCc.clearFocus();
            //[BUGFIX]-Mod-END by SCDTABLET.aijian.shi
            //TS: rong-tang 2016-04-06 EMAIL-1840992 ADD_E
            mLaunchContact = true;   //TS: xinlei.sheng 2015-01-26 EMAIL FIXBUG_886976 ADD
            requestCode = ACTIVITY_REQUEST_PICK_CONTACT_CC;
            Intent ccPickEmail = createAddContactIntent(mCc);
            //TS: jian.xu 2015-09-17 EMAIL-1085945 MOD_S
            try {
                startActivityForResult(ccPickEmail, requestCode);
            } catch (ActivityNotFoundException e) {
                LogUtils.e(LOG_TAG, e, "Exception attempting to choose person from contact app");
            }
            //TS: jian.xu 2015-09-17 EMAIL-1085945 MOD_E
        } else if (id == R.id.bcc_recipients_picker) {
            //TS: rong-tang 2016-04-06 EMAIL-1840992 ADD_S
            //Note: when click picker, request focus to picker, recipient edit text view need to submit change.
            //[BUGFIX]-Mod-BEGIN by SCDTABLET.aijian.shi,09/07/2016,2848507,
            // Use  clearFocus to avoid the improper focus after entering the recipient edit text view.
            //mBccPicker.requestFocusFromTouch();
            mBcc.clearFocus();
            //[BUGFIX]-Mod-END by SCDTABLET.aijian.shi
            //TS: rong-tang 2016-04-06 EMAIL-1840992 ADD_E
            mLaunchContact = true;  //TS: xinlei.sheng 2015-01-26 EMAIL FIXBUG_886976 ADD
            requestCode = ACTIVITY_REQUEST_PICK_CONTACT_BCC;
            Intent bccPickEmail = createAddContactIntent(mBcc);
            //TS: jian.xu 2015-09-17 EMAIL-1085945 MOD_S
            try {
                startActivityForResult(bccPickEmail, requestCode);
            } catch (ActivityNotFoundException e) {
                LogUtils.e(LOG_TAG, e, "Exception attempting to choose person from contact app");
            }
            //TS: jian.xu 2015-09-17 EMAIL-1085945 MOD_E
        }
        //TS: yanhua.chen 2015-9-1 EMAIL CD_551912 ADD_S
        else if(id == R.id.body_signature){
            //mIsClickIcon = true;//[BUGFIX]-MOD by SCDTABLET.shujing.jin@tcl.com,05/06/2016,2013535
            mBodySignature.setVisibility(View.GONE);
            //Note:when click icon,append the signature to body
            appendSignature();
        }
        //TS: yanhua.chen 2015-9-1 EMAIL CD_551912 ADD_E
        //[FEATURE]-Add-END by TSCD.chao zhang
    }

    //[FEATURE]-Add-BEGIN by TSCD.chao zhang,04/21/2014,FR631895(porting from FR487417)
    private Intent createAddContactIntent(RecipientEditTextView textview) {
        //TS: junwei-xu 2016-03-01 EMAIL BUGFIX-1612750 ADD_S
        //Note: different intent for different platform.
        Intent intent = new Intent();
        String platform = PLFUtils.getString(this, "feature_email_platform");
        if (platform.equals(PLATFORM_QUALCOMM)) {
            intent.setAction(ACTION_MULTI_PICK_EMAIL_QUALCOMM);
            intent.setData(Contacts.CONTENT_URI);
        } else if (platform.equals(PLATFORM_MTK)) {
            intent.setAction(ACTION_MULTI_PICK_EMAIL_MTK);
            intent.setType(ContactsContract.CommonDataKinds.Email.CONTENT_TYPE);
        }
        //TS: junwei-xu 2016-03-01 EMAIL BUGFIX-1612750 ADD_E

        // We have to wait for the constructing complete.
         com.tct.emailcommon.mail.Address[] addresses = getAddressesIgnoreValid(textview);
        int addressCount = addresses.length;

        if (addressCount > 0) {
            Bundle bundle = new Bundle();
            Bundle mChoiceSet = new Bundle();
             com.tct.emailcommon.mail.Address address;
            for (int i = 0; i < addressCount; i++) {
                address = addresses[i];
                //[BUGFIX]-Mod-BEGIN by TSCD.chao zhang,05/29/2014,PR 682725,[Email]Can't add mail address from contact option.
                //fix Email address change to invalid when replay mail.
                String name = address.getPersonal();
                if (name != null && !name.equals(address.getAddress())) {
                    if (name.matches(".*[\\(\\)<>@,;:\\\\\".\\[\\]].*")) {
                        name = com.tct.emailcommon.mail.Address.ensureQuotedString(name);
                    }
                }
                mChoiceSet.putStringArray(String.valueOf(i),new String[] {name, address.getAddress()});
                //[BUGFIX]-Mod-END by TSCD.chao zhang
            }
            bundle.putBundle(EXTRA_PICK_EMAIL_BUNDLE, mChoiceSet);
            intent.putExtras(bundle);
        }
        return intent;
    }
    //[FEATURE]-Add-END by TSCD.chao zhang
    @Override
    public void onFocusChange (View v, boolean hasFocus) {
        final int id = v.getId();
         if (hasFocus && (id == R.id.subject || id == R.id.body)) {
           //PR961829 zhiqiang.shao begin
            boolean showCcBccEmpty=TextUtils.isEmpty(mCc.getText())&&TextUtils.isEmpty(mBcc.getText());
            if(showCcBccEmpty){
            mCcBccView.show(false /* animate */, false , false);
            mCcBccButton.setVisibility(View.VISIBLE);

            //[BUGFIX]-Add-BEGIN by SCDTABLET.weiwei.huang,05/03/2016,2013739,
            //[Email]Add mail contact icon display is not consistent
            bccButtonImg.setVisibility(View.VISIBLE);
            ccButtonImg.setVisibility(View.VISIBLE);
            //[BUGFIX]-Add-END by SCDTABLET.weiwei.huang

           }
            // Collapse cc/bcc iff both are empty
            final boolean showCcBccFields = !TextUtils.isEmpty(mCc.getText()) ||
                    !TextUtils.isEmpty(mBcc.getText());
//            mCcBccView.show(false /* animate */, showCcBccFields, showCcBccFields);
//            mCcBccButton.setVisibility(showCcBccFields ? View.GONE : View.VISIBLE);
          //PR961829 zhiqiang.shao end
            // On phones autoscroll down so that Cc aligns to the top if we are showing cc/bcc.
            if (getResources().getBoolean(R.bool.auto_scroll_cc) && showCcBccFields) {
                final int[] coords = new int[2];
                mCc.getLocationOnScreen(coords);

                // Subtract status bar and action bar height from y-coord.
                final Rect rect = new Rect();
                getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
                final int deltaY = coords[1] - getSupportActionBar().getHeight() - rect.top;

                // Only scroll down
                if (deltaY > 0) {
                    mScrollView.smoothScrollBy(0, deltaY);
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        final boolean superCreated = super.onCreateOptionsMenu(menu);
        // Don't render any menu items when there are no accounts.
        if (mAccounts == null || mAccounts.length == 0) {
            return superCreated;
        }
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.compose_menu, menu);

        /*
         * Start save in the correct enabled state.
         * 1) If a user launches compose from within gmail, save is disabled
         * until they add something, at which point, save is enabled, auto save
         * on exit; if the user empties everything, save is disabled, exiting does not
         * auto-save
         * 2) if a user replies/ reply all/ forwards from within gmail, save is
         * disabled until they change something, at which point, save is
         * enabled, auto save on exit; if the user empties everything, save is
         * disabled, exiting does not auto-save.
         * 3) If a user launches compose from another application and something
         * gets populated (attachments, recipients, body, subject, etc), save is
         * enabled, auto save on exit; if the user empties everything, save is
         * disabled, exiting does not auto-save
         */
        mSave = menu.findItem(R.id.save);
        String action = getIntent() != null ? getIntent().getAction() : null;
        boolean fromWidget = getIntent() != null && getIntent().getBooleanExtra(EXTRA_FROM_EMAIL_WIDGET, false);//TS: zheng.zou 2015-12-09 EMAIL BUGFIX_1059178 ADD
        enableSave(mInnerSavedState != null ?
                mInnerSavedState.getBoolean(EXTRA_SAVE_ENABLED)
                    : ((Intent.ACTION_SEND.equals(action) && !fromWidget) //TS: zheng.zou 2015-12-09 EMAIL BUGFIX_1059178 MOD
                            || Intent.ACTION_SEND_MULTIPLE.equals(action)
                            || Intent.ACTION_SENDTO.equals(action)
                            || shouldSave()));

        final MenuItem helpItem = menu.findItem(R.id.help_info_menu_item);
        final MenuItem sendFeedbackItem = menu.findItem(R.id.feedback_menu_item);
        //TS: Gantao 2015-7-13 EMAIL FEATURE_1033148 DEL_S
        //For feature:long click the attachment icon menu can show the menu discription,
        //we should remove its useless sub menu.
//        final MenuItem attachFromServiceItem = menu.findItem(R.id.attach_from_service_stub1);
        //TS: Gantao 2015-7-13 EMAIL FEATURE_1033148 DEL_E
        //[FEATURE]-Add-BEGIN by TSCD.chao zhang,04/17/2014,FR 631895(porting from FR514398)
        MenuItem priority = menu.findItem(R.id.priority);
        if (priority != null) {
            // TS: xiaolin.li 2014-11-25 EMAIL READ_PLF MOD_S
            //priority.setVisible(getResources().getBoolean(R.bool.feature_email_priority_on));
            priority.setVisible(PLFUtils.getBoolean(this, "feature_email_priority_on"));
            // TS: xiaolin.li 2014-11-25 EMAIL READ_PLF MOD_E
        }
        //[FEATURE]-Add-END by TSCD.chao zhang
        if (helpItem != null) {
            helpItem.setVisible(mAccount != null
                    && mAccount.supportsCapability(AccountCapabilities.HELP_CONTENT));
        }
        if (sendFeedbackItem != null) {
            sendFeedbackItem.setVisible(mAccount != null
                    && mAccount.supportsCapability(AccountCapabilities.SEND_FEEDBACK));
        }
        //TS: Gantao 2015-7-13 EMAIL FEATURE_1033148 DEL_S
        //For feature:long click the attachment icon menu can show the menu discription,
        //we should remove its useless sub menu.
//        if (attachFromServiceItem != null) {
//            attachFromServiceItem.setVisible(shouldEnableAttachFromServiceMenu(mAccount));
//        }
        //TS: Gantao 2015-7-13 EMAIL FEATURE_1033148 DEL_E

        //TS: yanhua.chen 2015-4-30 EMAIL BUGFIX_989399 MOD_S
        // Show attach picture on pre-K devices.
        //menu.findItem(R.id.add_photo_attachment).setVisible(!Utils.isRunningKitkatOrLater());
        //TS: yanhua.chen 2015-4-30 EMAIL BUGFIX_989399 MOD_E

        // TS: Gantao 2015-10-30 EMAIL FEATURE_1104470 ADD_S
        MenuItem saveGroupItem = menu.findItem(R.id.group);
        if(saveGroupItem!= null) {
            saveGroupItem.setVisible(PLFUtils.getBoolean(this, "feature_email_save_group"));
        }
        // TS: Gantao 2015-10-30 EMAIL FEATURE_1104470 ADD_E

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int id = item.getItemId();

        Analytics.getInstance().sendMenuItemEvent(Analytics.EVENT_CATEGORY_MENU_ITEM, id,
                "compose", 0);

        boolean handled = true;
        //TS: yanhua.chen 2015-4-30 EMAIL BUGFIX_989399 MOD_S
        //if (id == R.id.add_file_attachment) {
        //    doAttach(MIME_TYPE_ALL);
        //} else if (id == R.id.add_photo_attachment) {
        //    doAttach(MIME_TYPE_PHOTO);
        if(id ==R.id.add_attachment){
            doAttach(MIME_TYPE_ALL);
        //TS: yanhua.chen 2015-4-30 EMAIL BUGFIX_989399 MOD_E
        } else if (id == R.id.save) {
            doSave(true);
        } else if (id == R.id.send) {
            doSend();
        } else if (id == R.id.discard) {
            doDiscard();
        } else if (id == R.id.settings) {
            Utils.showSettings(this, mAccount);
        } else if (id == android.R.id.home) {
            onAppUpPressed();
        } else if (id == R.id.help_info_menu_item) {
            Utils.showHelp(this, mAccount, getString(R.string.compose_help_context));
        //[FEATURE]-Add-BEGIN by TSCD.chao zhang,04/17/2014,FR 631895(porting from FR514398)
        } else if(id == R.id.priority) {
            setPriority();
         //[FEATURE]-Add-END by TSCD.chao zhang
            // TS: Gantao 2015-10-30 EMAIL FEATURE_1104470 ADD_S
        } else if(id == R.id.group) {
            showSaveGroupDialog();
            // TS: Gantao 2015-10-30 EMAIL FEATURE_1104470 ADD_E
        } else {
            handled = false;
        }
        return handled || super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // If we are showing the wait fragment, just exit.
        if (getWaitFragment() != null) {
            finish();
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Carries out the "up" action in the action bar.
     */
    private void onAppUpPressed() {
        if (mLaunchedFromEmail) {
            // If this was started from Gmail, simply treat app up as the system back button, so
            // that the last view is restored.
            onBackPressed();
            return;
        }

        // Fire the main activity to ensure it launches the "top" screen of mail.
        // Since the main Activity is singleTask, it should revive that task if it was already
        // started.
        final Intent mailIntent = Utils.createViewInboxIntent(mAccount);
        mailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK |
                Intent.FLAG_ACTIVITY_TASK_ON_HOME);
        startActivity(mailIntent);
        finish();
    }

    private void doSend() {
        // TS: zhaotianyong 2015-03-31 EMAIL BUGFIX-963249 ADD_S
        doSend = true;
        // TS: zhaotianyong 2015-03-31 EMAIL BUGFIX-963249 ADD_E
        //TS: xiangnan.zhou 2016-03-23 EMAIL BUGFIX-1783199 ADD_S
        hideInputSoftKeyboard();
        //TS: xiangnan.zhou 2016-03-23 EMAIL BUGFIX-1783199 ADD_E
        sendOrSaveWithSanityChecks(false, true, false, false);
        logSendOrSave(false /* save */);
        mPerformedSendOrDiscard = true;
        // TS: junwei-xu 2015-06-30 EMAIL BUGFIX-1030195 DEL_S
        // Note: we don't use sharedpreferences to save priority, no need to reset it
        /*
     // TS: chenyanhua 2015-02-15 EMAIL BUGFIX-932308 ADD_S
        resetPriorityFlag();
     // TS: chenyanhua 2015-02-15 EMAIL BUGFIX-932308 ADD_E
        */
        // TS: junwei-xu 2015-06-30 EMAIL BUGFIX-1030195 DEL_E
    }
    // TS: junwei-xu 2015-06-30 EMAIL BUGFIX-1030195 DEL_S
    // Note: we don't use sharedpreferences to save priority, no need it
    /*
    // TS: chenyanhua 2015-02-15 EMAIL BUGFIX-932308 ADD_S
    private void resetPriorityFlag(){
        SharedPreferences.Editor editor = getSharedPreferences("PriorityFlag", Context.MODE_PRIVATE).edit();
        editor.putInt("mPriorityFlag", Message.FLAG_PRIORITY_NORMAL);
        editor.commit();
    }
    // TS: chenyanhua 2015-02-15 EMAIL BUGFIX-932308 ADD_E
    */
    // TS: junwei-xu 2015-06-30 EMAIL BUGFIX-1030195 DEL_E
    private void doSave(boolean showToast) {
        sendOrSaveWithSanityChecks(true, showToast, false, false);
        // TS: junwei-xu 2015-06-30 EMAIL BUGFIX-1030195 DEL_S
        // Note: we don't use sharedpreferences to save priority, no need to reset it
        /*
     // TS: chenyanhua 2015-02-15 EMAIL BUGFIX-932308 ADD_S
        resetPriorityFlag();
     // TS: chenyanhua 2015-02-15 EMAIL BUGFIX-932308 ADD_E
        */
        // TS: junwei-xu 2015-06-30 EMAIL BUGFIX-1030195 DEL_E
    }

    @Override
    public void onRecipientEntryItemClicked(int charactersTyped, int position) {
        // Send analytics of characters typed and position in dropdown selected.
        Analytics.getInstance().sendEvent(
                "suggest_click", Integer.toString(charactersTyped), Integer.toString(position), 0);
    }

    @VisibleForTesting
    public interface SendOrSaveCallback {
        void initializeSendOrSave(SendOrSaveTask sendOrSaveTask);
        void notifyMessageIdAllocated(SendOrSaveMessage sendOrSaveMessage, Message message);
        Message getMessage();
        void sendOrSaveFinished(SendOrSaveTask sendOrSaveTask, boolean success);
        void incrementRecipientsTimesContacted(List<String> recipients);
    }

    @VisibleForTesting
    public static class SendOrSaveTask implements Runnable {
        private final Context mContext;
        @VisibleForTesting
        public final SendOrSaveCallback mSendOrSaveCallback;
        @VisibleForTesting
        public final SendOrSaveMessage mSendOrSaveMessage;
        private ReplyFromAccount mExistingDraftAccount;

        public SendOrSaveTask(Context context, SendOrSaveMessage message,
                SendOrSaveCallback callback, ReplyFromAccount draftAccount) {
            mContext = context;
            mSendOrSaveCallback = callback;
            mSendOrSaveMessage = message;
            mExistingDraftAccount = draftAccount;
        }

        @Override
        public void run() {
            final SendOrSaveMessage sendOrSaveMessage = mSendOrSaveMessage;

            final ReplyFromAccount selectedAccount = sendOrSaveMessage.mAccount;
            Message message = mSendOrSaveCallback.getMessage();
            long messageId = message != null ? message.id : UIProvider.INVALID_MESSAGE_ID;
            // If a previous draft has been saved, in an account that is different
            // than what the user wants to send from, remove the old draft, and treat this
            // as a new message
            if (mExistingDraftAccount != null
                    && !selectedAccount.account.uri.equals(mExistingDraftAccount.account.uri)) {
                if (messageId != UIProvider.INVALID_MESSAGE_ID) {
                    ContentResolver resolver = mContext.getContentResolver();
                    ContentValues values = new ContentValues();
                    values.put(BaseColumns._ID, messageId);
                    if (mExistingDraftAccount.account.expungeMessageUri != null) {
                        new ContentProviderTask.UpdateTask()
                                .run(resolver, mExistingDraftAccount.account.expungeMessageUri,
                                        values, null, null);
                    } else {
                        // TODO(mindyp) delete the conversation.
                    }
                    // reset messageId to 0, so a new message will be created
                    messageId = UIProvider.INVALID_MESSAGE_ID;
                }
            }

            final long messageIdToSave = messageId;
            sendOrSaveMessage(messageIdToSave, sendOrSaveMessage, selectedAccount);

            if (!sendOrSaveMessage.mSave) {
                incrementRecipientsTimesContacted(
                        (String) sendOrSaveMessage.mValues.get(UIProvider.MessageColumns.TO),
                        (String) sendOrSaveMessage.mValues.get(UIProvider.MessageColumns.CC),
                        (String) sendOrSaveMessage.mValues.get(UIProvider.MessageColumns.BCC));
            }
            mSendOrSaveCallback.sendOrSaveFinished(SendOrSaveTask.this, true);
        }

        private void incrementRecipientsTimesContacted(
                final String toAddresses, final String ccAddresses, final String bccAddresses) {
            final List<String> recipients = Lists.newArrayList();
            addAddressesToRecipientList(recipients, toAddresses);
            addAddressesToRecipientList(recipients, ccAddresses);
            addAddressesToRecipientList(recipients, bccAddresses);
            mSendOrSaveCallback.incrementRecipientsTimesContacted(recipients);
        }

        private void addAddressesToRecipientList(
                final List<String> recipients, final String addressString) {
            if (recipients == null) {
                throw new IllegalArgumentException("recipientList cannot be null");
            }
            if (TextUtils.isEmpty(addressString)) {
                return;
            }
            final Rfc822Token[] tokens = Rfc822Tokenizer.tokenize(addressString);
            for (final Rfc822Token token : tokens) {
                recipients.add(token.getAddress());
            }
        }

        /**
         * Send or Save a message.
         */
        private void sendOrSaveMessage(final long messageIdToSave,
                final SendOrSaveMessage sendOrSaveMessage, final ReplyFromAccount selectedAccount) {
            final ContentResolver resolver = mContext.getContentResolver();
            final boolean updateExistingMessage = messageIdToSave != UIProvider.INVALID_MESSAGE_ID;

            final String accountMethod = sendOrSaveMessage.mSave ?
                    UIProvider.AccountCallMethods.SAVE_MESSAGE :
                    UIProvider.AccountCallMethods.SEND_MESSAGE;

            try {
                if (updateExistingMessage) {
                    sendOrSaveMessage.mValues.put(BaseColumns._ID, messageIdToSave);

                    callAccountSendSaveMethod(resolver,
                            selectedAccount.account, accountMethod, sendOrSaveMessage);
                } else {
                    Uri messageUri = null;
                    final Bundle result = callAccountSendSaveMethod(resolver,
                            selectedAccount.account, accountMethod, sendOrSaveMessage);
                    if (result != null) {
                        // If a non-null value was returned, then the provider handled the call
                        // method
                        messageUri = result.getParcelable(UIProvider.MessageColumns.URI);
                    }
                    if (sendOrSaveMessage.mSave && messageUri != null) {
                        final Cursor messageCursor = resolver.query(messageUri,
                                UIProvider.MESSAGE_PROJECTION, null, null, null);
                        if (messageCursor != null) {
                            try {
                                if (messageCursor.moveToFirst()) {
                                    // Broadcast notification that a new message has
                                    // been allocated
                                    mSendOrSaveCallback.notifyMessageIdAllocated(sendOrSaveMessage,
                                            new Message(messageCursor));
                                }
                            } finally {
                                messageCursor.close();
                            }
                        }
                    }
                }
            } finally {
                // Close any opened file descriptors
                closeOpenedAttachmentFds(sendOrSaveMessage);
            }
        }

        private static void closeOpenedAttachmentFds(final SendOrSaveMessage sendOrSaveMessage) {
            final Bundle openedFds = sendOrSaveMessage.attachmentFds();
            if (openedFds != null) {
                final Set<String> keys = openedFds.keySet();
                for (final String key : keys) {
                    final ParcelFileDescriptor fd = openedFds.getParcelable(key);
                    if (fd != null) {
                        try {
                            fd.close();
                        } catch (IOException e) {
                            // Do nothing
                        }
                    }
                }
            }
        }

        /**
         * Use the {@link ContentResolver#call} method to send or save the message.
         *
         * If this was successful, this method will return an non-null Bundle instance
         */
        private static Bundle callAccountSendSaveMethod(final ContentResolver resolver,
                final Account account, final String method,
                final SendOrSaveMessage sendOrSaveMessage) {
            // Copy all of the values from the content values to the bundle
            final Bundle methodExtras = new Bundle(sendOrSaveMessage.mValues.size());
            final Set<Entry<String, Object>> valueSet = sendOrSaveMessage.mValues.valueSet();

            for (Entry<String, Object> entry : valueSet) {
                final Object entryValue = entry.getValue();
                final String key = entry.getKey();
                if (entryValue instanceof String) {
                    methodExtras.putString(key, (String)entryValue);
                } else if (entryValue instanceof Boolean) {
                    methodExtras.putBoolean(key, (Boolean)entryValue);
                } else if (entryValue instanceof Integer) {
                    methodExtras.putInt(key, (Integer)entryValue);
                } else if (entryValue instanceof Long) {
                    methodExtras.putLong(key, (Long)entryValue);
                } else {
                    LogUtils.wtf(LOG_TAG, "Unexpected object type: %s",
                            entryValue.getClass().getName());
                }
            }

            // If the SendOrSaveMessage has some opened fds, add them to the bundle
            final Bundle fdMap = sendOrSaveMessage.attachmentFds();
            if (fdMap != null) {
                methodExtras.putParcelable(
                        UIProvider.SendOrSaveMethodParamKeys.OPENED_FD_MAP, fdMap);
            }

            return resolver.call(account.uri, method, account.uri.toString(), methodExtras);
        }
    }

    /**
     * Reports recipients that have been contacted in order to improve auto-complete
     * suggestions. Default behavior updates usage statistics in ContactsProvider.
     * @param recipients addresses
     */
    protected void incrementRecipientsTimesContacted(List<String> recipients) {
        final DataUsageStatUpdater statsUpdater = new DataUsageStatUpdater(this);
        statsUpdater.updateWithAddress(recipients);
    }

    @VisibleForTesting
    public static class SendOrSaveMessage {
        final ReplyFromAccount mAccount;
        final ContentValues mValues;
        final String mRefMessageId;
        @VisibleForTesting
        public final boolean mSave;
        final int mRequestId;
        private final Bundle mAttachmentFds;

        public SendOrSaveMessage(Context context, ReplyFromAccount account, ContentValues values,
                String refMessageId, List<Attachment> attachments, boolean save) {
            mAccount = account;
            mValues = values;
            mRefMessageId = refMessageId;
            mSave = save;
            mRequestId = mValues.hashCode() ^ hashCode();

            mAttachmentFds = initializeAttachmentFds(context, attachments);
        }

        int requestId() {
            return mRequestId;
        }

        Bundle attachmentFds() {
            return mAttachmentFds;
        }

        /**
         * Opens {@link ParcelFileDescriptor} for each of the attachments.  This method must be
         * called before the ComposeActivity finishes.
         * Note: The caller is responsible for closing these file descriptors.
         */
        private static Bundle initializeAttachmentFds(final Context context,
                final List<Attachment> attachments) {
            if (attachments == null || attachments.size() == 0) {
                return null;
            }

            final Bundle result = new Bundle(attachments.size());
            final ContentResolver resolver = context.getContentResolver();

            for (Attachment attachment : attachments) {
                if (attachment == null || Utils.isEmpty(attachment.contentUri)
                        || result.getParcelable(attachment.contentUri.toString()) != null) {  //TS: zheng.zou 2015-7-27 EMAIL BUGFIX_1047612 MOD
                    continue;
                }

                ParcelFileDescriptor fileDescriptor;
                try {
                    fileDescriptor = resolver.openFileDescriptor(attachment.contentUri, "r");
                } catch (FileNotFoundException e) {
                    LogUtils.e(LOG_TAG, e, "Exception attempting to open attachment");
                    fileDescriptor = null;
                } catch (SecurityException e) {
                    // We have encountered a security exception when attempting to open the file
                    // specified by the content uri.  If the attachment has been cached, this
                    // isn't a problem, as even through the original permission may have been
                    // revoked, we have cached the file.  This will happen when saving/sending
                    // a previously saved draft.
                    // TODO(markwei): Expose whether the attachment has been cached through the
                    // attachment object.  This would allow us to limit when the log is made, as
                    // if the attachment has been cached, this really isn't an error
                    LogUtils.e(LOG_TAG, e, "Security Exception attempting to open attachment");
                    // Just set the file descriptor to null, as the underlying provider needs
                    // to handle the file descriptor not being set.
                    fileDescriptor = null;
                } catch (IllegalArgumentException e){
                    LogUtils.e(LOG_TAG, e, "IllegalArgumentException Exception attempting to open attachment");
                    fileDescriptor = null;
                //TS: rong-tang 2016-03-02 EMAIL BUGFIX-1712549 ADD_S
                } catch (UnsupportedOperationException e) {
                    LogUtils.e(LOG_TAG, e, "UnsupportedOperationException while opening file to obtain size.");
                    fileDescriptor = null;
                }
                //TS: rong-tang 2016-03-02 EMAIL BUGFIX-1712549 ADD_E

                if (fileDescriptor != null) {
                    result.putParcelable(attachment.contentUri.toString(), fileDescriptor);
                }
            }

            return result;
        }
    }

    /**
     * Get the to recipients.
     */
    public String[] getToAddresses() {
        return getAddressesFromList(mTo);
    }

    /**
     * Get the cc recipients.
     */
    public String[] getCcAddresses() {
        return getAddressesFromList(mCc);
    }

    /**
     * Get the bcc recipients.
     */
    public String[] getBccAddresses() {
        return getAddressesFromList(mBcc);
    }

    public String[] getAddressesFromList(RecipientEditTextView list) {
        if (list == null) {
            return new String[0];
        }
        Rfc822Token[] tokens = Rfc822Tokenizer.tokenize(list.getText());
        int count = tokens.length;
        String[] result = new String[count];
        for (int i = 0; i < count; i++) {
            result[i] = tokens[i].toString();
        }
        return result;
    }

    /**
     * Check for invalid email addresses.
     * @param to String array of email addresses to check.
     * @param wrongEmailsOut Emails addresses that were invalid.
     */
    public void checkInvalidEmails(final String[] to, final List<String> wrongEmailsOut) {
        if (mValidator == null) {
            return;
        }
        for (final String email : to) {
            if (!mValidator.isValid(email)) {
                wrongEmailsOut.add(email);
            }
        }
    }

    public static class RecipientErrorDialogFragment extends DialogFragment {
        // Public no-args constructor needed for fragment re-instantiation
        public RecipientErrorDialogFragment() {}

        public static RecipientErrorDialogFragment newInstance(final String message) {
            final RecipientErrorDialogFragment frag = new RecipientErrorDialogFragment();
            final Bundle args = new Bundle(1);
            args.putString("message", message);
            frag.setArguments(args);
            return frag;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final String message = getArguments().getString("message");
            return new AlertDialog.Builder(getActivity())
                    .setMessage(message)
                    .setPositiveButton(
                            R.string.ok, new Dialog.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ((ComposeActivity)getActivity()).finishRecipientErrorDialog();
                        }
                    }).create();
        }
    }

    private void finishRecipientErrorDialog() {
        // after the user dismisses the recipient error
        // dialog we want to make sure to refocus the
        // recipient to field so they can fix the issue
        // easily
        if (mTo != null) {
            mTo.requestFocus();
        }
    }

    /**
     * Show an error because the user has entered an invalid recipient.
     */
    private void showRecipientErrorDialog(final String message) {
        final DialogFragment frag = RecipientErrorDialogFragment.newInstance(message);
        frag.show(getFragmentManager(), "recipient error");
    }

    /**
     * Update the state of the UI based on whether or not the current draft
     * needs to be saved and the message is not empty.
     */
    public void updateSaveUi() {
        if (mSave != null) {
            mSave.setEnabled((shouldSave() && !isBlank()));
        }
    }

    /**
     * Returns true if we need to save the current draft.
     */
    private boolean shouldSave() {
        synchronized (mDraftLock) {
            // The message should only be saved if:
            // It hasn't been sent AND
            // Some text has been added to the message OR
            // an attachment has been added or removed
            // AND there is actually something in the draft to save.
            //[FEATURE]-Add-BEGIN by TSCD.chao zhang,04/17/2014,FR 631895(porting from FR514398)
            return (mTextChanged || mAttachmentsChanged || mReplyFromChanged ||mProrityChanged)
                    && !isBlank();
        }
    }

    /**
     * Returns whether the "Attach from Drive" menu item should be visible.
     */
    protected boolean shouldEnableAttachFromServiceMenu(Account mAccount) {
        return false;
    }

    /**
     * Check if all fields are blank.
     * @return boolean
     */
    public boolean isBlank() {
        // Need to check for null since isBlank() can be called from onPause()
        // before findViews() is called
        if (mSubject == null || mBodyView == null || mTo == null || mCc == null ||
                mAttachmentsView == null) {
            LogUtils.w(LOG_TAG, "null views in isBlank check");
            return true;
        }
        return mSubject.getText().length() == 0
                && (mBodyView.getText().length() == 0 || getSignatureStartPosition(mSignature,
                        mBodyView.getText().toString()) == 0)
                && mTo.length() == 0
                //[FEATURE]-Mod-BEGIN by TSCD.chao zhang,04/25/2014,FR 631895(porting from  FR487417)
                && mCc.length() == 0 && isBccViewEmpty()//mBcc.length() == 0
                //[FEATURE]-Mod-END by TSCD.chao zhang
                && mAttachmentsView.getAttachments().size() == 0;
    }


    //[FEATURE]-Add-BEGIN by TSCD.chao zhang,04/25/2014,FR 631895(porting from  FR487417)
    private boolean isBccViewEmpty(){
        if (mBcc.length() == 0) {
            return true;
        } else {
            // TS: zhaotianyong 2014-12-02 EMAIL BUGFIX_855270 MOD_S
//            boolean bccMySelf = Preferences.getSharedPreferences(this)
//                    .getBoolean(Preferences.BCC_MYSELF_KEY, Preferences.BCC_MYSELF_DEFAULT);
            boolean bccMySelf = getSharedPreferences(MailPrefs.get(this).getSharedPreferencesName(), Context.MODE_PRIVATE).getBoolean(
                    Preferences.BCC_MYSELF_KEY, Preferences.BCC_MYSELF_DEFAULT);
            // TS: zhaotianyong 2014-12-02 EMAIL BUGFIX_855270 MOD_E
            String bccText = mBcc.getText().toString().trim();
            if (bccMySelf) {
                com.tct.emailcommon.mail.Address[] bcc = com.tct.emailcommon.mail.Address.parse(bccText);
                if (bcc.length == 1
                        && bcc[0].getAddress().equals(mAccount.getEmailAddress())) {
                    return true;
                }
            }
        }
        return false;
    }
    //[FEATURE]-Add-END by TSCD.chao zhang

    //[FEATURE]-Add-BEGIN by TTSCD.chao zhang,04/25/2014,FR 631895(porting from  FR487417)
    private void addBccMySelf(Account account) {
        // TS: zhaotianyong 2014-12-02 EMAIL BUGFIX_855270 MOD_S
//        boolean bccMySelf = Preferences.getSharedPreferences(this).getBoolean(
//                Preferences.BCC_MYSELF_KEY, Preferences.BCC_MYSELF_DEFAULT);
        boolean bccMySelf = getSharedPreferences(MailPrefs.get(this).getSharedPreferencesName(), Context.MODE_PRIVATE).getBoolean(
                Preferences.BCC_MYSELF_KEY, Preferences.BCC_MYSELF_DEFAULT);
        // TS: zhaotianyong 2014-12-02 EMAIL BUGFIX_855270 MOD_E
        if (account == null) {
            return;
        }
        if (bccMySelf) {
            if (!mBcc.getText().toString().contains(mAccount.getEmailAddress())) {
                com.tct.emailcommon.mail.Address a = new com.tct.emailcommon.mail.Address(mAccount.getEmailAddress());
                a.setPersonal(mAccount.getSenderName());
                //AM: peng-zhang 2015-02-27 EMAIL BUGFIX_955421 MOD_S
                //addAddress(mBcc, a.toString());
                if(Changed){
                    addAddress(mBcc, a.toString());
                }else{
                    addAddressToList(bcc_text,mBcc);
                }
                //AM: peng-zhang 2015-02-27 EMAIL BUGFIX_955421 MOD_E
                mAddBccBySetting = true;
            }
        }else {
            mAddBccBySetting = false;
        }
    }
    //[FEATURE]-Add-END by TSCD.chao zhang

    @VisibleForTesting
    protected int getSignatureStartPosition(String signature, String bodyText) {
        int startPos = -1;

        if (TextUtils.isEmpty(signature) || TextUtils.isEmpty(bodyText)) {
            return startPos;
        }

        int bodyLength = bodyText.length();
        int signatureLength = signature.length();
        String printableVersion = convertToPrintableSignature(signature);
        int printableLength = printableVersion.length();

        if (bodyLength >= printableLength
                && bodyText.substring(bodyLength - printableLength)
                .equals(printableVersion)) {
            startPos = bodyLength - printableLength;
        } else if (bodyLength >= signatureLength
                && bodyText.substring(bodyLength - signatureLength)
                .equals(signature)) {
            startPos = bodyLength - signatureLength;
        }
        return startPos;
    }

    /**
     * Allows any changes made by the user to be ignored. Called when the user
     * decides to discard a draft.
     */
    private void discardChanges() {
        mTextChanged = false;
        mAttachmentsChanged = false;
        mReplyFromChanged = false;
        //[FEATURE]-Add-BEGIN by TSCD.chao zhang,04/17/2014,FR 631895(porting from FR514398)
        mProrityChanged=false;
        //[FEATURE]-Add-END by TSCD.chao zhang
    }

    /**
     * @param save True to save, false to send
     * @param showToast True to show a toast once the message is sent/saved
     */
    protected void sendOrSaveWithSanityChecks(final boolean save, final boolean showToast,
            final boolean orientationChanged, final boolean autoSend) {
        if (mAccounts == null || mAccount == null) {
			//[BUGFIX]-MOD by SCDTABLET.shujing.jin@tcl.com,08/05/2016,2635083
			Utility.showShortToast(this, R.string.send_failed);
            //Toast.makeText(this, R.string.send_failed, Toast.LENGTH_SHORT).show();
            if (autoSend) {
                finish();
            }
            return;
        }

        final String[] to, cc, bcc;
        if (orientationChanged) {
            to = cc = bcc = new String[0];
        } else {
            to = getToAddresses();
            cc = getCcAddresses();
            bcc = getBccAddresses();
        }
        // TS: tao.gan 2016-03-16 EMAIL BUGFIX-1761777 MOD_S
        //NOTE: NO USED,but will increase timeconsume with lots of address data.
        /*final ArrayList<String> recipients = buildEmailAddressList(to);
        recipients.addAll(buildEmailAddressList(cc));
        recipients.addAll(buildEmailAddressList(bcc));*/

        final ArrayList<String> recipients = new ArrayList<String>();
        // TS: tao.gan 2015-03-16 EMAIL BUGFIX-1761777 MOD_E
        // Don't let the user send to nobody (but it's okay to save a message
        // with no recipients)
        if (!save && (to.length == 0 && cc.length == 0 && bcc.length == 0)) {
            showRecipientErrorDialog(getString(R.string.recipient_needed));
            return;
        }

        List<String> wrongEmails = new ArrayList<String>();
        if (!save) {
            checkInvalidEmails(to, wrongEmails);
            checkInvalidEmails(cc, wrongEmails);
            checkInvalidEmails(bcc, wrongEmails);
        }

        // Don't let the user send an email with invalid recipients
        if (wrongEmails.size() > 0) {
            String errorText = String.format(getString(R.string.invalid_recipient),
                    wrongEmails.get(0));
            showRecipientErrorDialog(errorText);
            return;
        }

        if (!save) {
            if (autoSend) {
                // Skip all further checks during autosend. This flow is used by Android Wear
                // and Google Now.
                sendOrSave(save, showToast);
                return;
            }

            //TS: lin-zhou 2015-8-28 EMAIL BUGFIX_1065369 ADD_S
            boolean warnAboutEmptySubject = isSubjectEmpty();
            // When we bring up a dialog warning the user about a send,
            // assume that they accept sending the message. If they do not,
            // the dialog listener is required to enable sending again.
            if (warnAboutEmptySubject) {
                showSendConfirmDialog(R.string.confirm_send_message_with_no_subject,
                        showToast, recipients);
                return;
            }
            //TS: lin-zhou 2015-8-28 EMAIL BUGFIX_1065369 ADD_E
            // Show a warning before sending only if there are no attachments, body, or subject.
            if (mAttachmentsView.getAttachments().isEmpty() && showEmptyTextWarnings()) {
                //TS: lin-zhou 2015-8-28 EMAIL BUGFIX_1065369 DEL_S
                //boolean warnAboutEmptySubject = isSubjectEmpty();
                //TS: lin-zhou 2015-8-28 EMAIL BUGFIX_1065369 DEL_E
                boolean emptyBody = TextUtils.getTrimmedLength(mBodyView.getEditableText()) == 0;

                // A warning about an empty body may not be warranted when
                // forwarding mails, since a common use case is to forward
                // quoted text and not append any more text.
                boolean warnAboutEmptyBody = emptyBody && (!mForward || isBodyEmpty());

                //TS: lin-zhou 2015-8-28 EMAIL BUGFIX_1065369 DEL_S
                /*// When we bring up a dialog warning the user about a send,
                // assume that they accept sending the message. If they do not,
                // the dialog listener is required to enable sending again.
                if (warnAboutEmptySubject) {
                    showSendConfirmDialog(R.string.confirm_send_message_with_no_subject,
                            showToast, recipients);
                    return;
                }*/
                //TS: lin-zhou 2015-8-28 EMAIL BUGFIX_1065369 DEL_E

                if (warnAboutEmptyBody) {
                    showSendConfirmDialog(R.string.confirm_send_message_with_no_body,
                            showToast, recipients);
                    return;
                }
            }
            // Ask for confirmation to send.
            if (showSendConfirmation()) {
                showSendConfirmDialog(R.string.confirm_send_message, showToast, recipients);
                return;
            }
        }

        performAdditionalSendOrSaveSanityChecks(save, showToast, recipients);
    }

    /**
     * Returns a boolean indicating whether warnings should be shown for empty
     * subject and body fields
     *
     * @return True if a warning should be shown for empty text fields
     */
    protected boolean showEmptyTextWarnings() {
        return mAttachmentsView.getAttachments().size() == 0;
    }

    /**
     * Returns a boolean indicating whether the user should confirm each send
     *
     * @return True if a warning should be on each send
     */
    protected boolean showSendConfirmation() {
        return mCachedSettings != null && mCachedSettings.confirmSend;
    }

    public static class SendConfirmDialogFragment extends DialogFragment
            implements DialogInterface.OnClickListener {

        private static final String MESSAGE_ID = "messageId";
        private static final String SHOW_TOAST = "showToast";
        private static final String RECIPIENTS = "recipients";

        private boolean mShowToast;

        private ArrayList<String> mRecipients;

        // Public no-args constructor needed for fragment re-instantiation
        public SendConfirmDialogFragment() {}

        public static SendConfirmDialogFragment newInstance(final int messageId,
                final boolean showToast, final ArrayList<String> recipients) {
            final SendConfirmDialogFragment frag = new SendConfirmDialogFragment();
            final Bundle args = new Bundle(3);
            args.putInt(MESSAGE_ID, messageId);
            args.putBoolean(SHOW_TOAST, showToast);
            args.putStringArrayList(RECIPIENTS, recipients);
            frag.setArguments(args);
            return frag;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final int messageId = getArguments().getInt(MESSAGE_ID);
            mShowToast = getArguments().getBoolean(SHOW_TOAST);
            mRecipients = getArguments().getStringArrayList(RECIPIENTS);

            final int confirmTextId = (messageId == R.string.confirm_send_message) ?
                    R.string.ok : R.string.send;

            return new AlertDialog.Builder(getActivity())
                    .setMessage(messageId)
                    .setPositiveButton(confirmTextId, this)
                    .setNegativeButton(R.string.cancel, null)
                    .create();
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            if (which == DialogInterface.BUTTON_POSITIVE) {
                ((ComposeActivity) getActivity()).finishSendConfirmDialog(mShowToast, mRecipients);
            }
        }
    }

    private void finishSendConfirmDialog(
            final boolean showToast, final ArrayList<String> recipients) {
        performAdditionalSendOrSaveSanityChecks(false /* save */, showToast, recipients);
    }

    // The list of recipients are used by the additional sendOrSave checks.
    // However, the send confirm dialog may be shown before performing
    // the additional checks. As a result, we need to plumb the recipient
    // list through the send confirm dialog so that
    // performAdditionalSendOrSaveChecks can be performed properly.
    private void showSendConfirmDialog(final int messageId,
            final boolean showToast, final ArrayList<String> recipients) {
        final DialogFragment frag = SendConfirmDialogFragment.newInstance(
                messageId, showToast, recipients);
        frag.show(getFragmentManager(), "send confirm");
    }

    /**
     * Returns whether the ComposeArea believes there is any text in the body of
     * the composition. TODO: When ComposeArea controls the Body as well, add
     * that here.
     */
    public boolean isBodyEmpty() {
        return !mQuotedTextView.isTextIncluded();
    }

    /**
     * Test to see if the subject is empty.
     *
     * @return boolean.
     */
    // TODO: this will likely go away when composeArea.focus() is implemented
    // after all the widget control is moved over.
    public boolean isSubjectEmpty() {
        return TextUtils.getTrimmedLength(mSubject.getText()) == 0;
    }

    @VisibleForTesting
    public String getSubject() {
        return mSubject.getText().toString();
    }

    private int sendOrSaveInternal(Context context, ReplyFromAccount replyFromAccount,
            Message message, final Message refMessage, final CharSequence quotedText,
            SendOrSaveCallback callback, Handler handler, boolean save, int composeMode,
            ReplyFromAccount draftAccount, final ContentValues extraValues) {
        final ContentValues values = new ContentValues();

        final String refMessageId = refMessage != null ? refMessage.uri.toString() : "";

        MessageModification.putToAddresses(values, message.getToAddresses());
        MessageModification.putCcAddresses(values, message.getCcAddresses());
        MessageModification.putBccAddresses(values, message.getBccAddresses());
        MessageModification.putCustomFromAddress(values, message.getFrom());
        //[FEATURE]-Add-BEGIN by TSCD.chao zhang,04/17/2014,FR 631895(porting from FR514398)
        MessageModification.putPriority(values,message.mPriority);
        //[FEATURE]-Add-END by TSCD.chao zhang
        MessageModification.putSubject(values, message.subject);
        // TS: tao.gan 2015-12-25 EMAIL FEATURE-1239148 ADD_S
        MessageModification.putRepylToAddress(values, message.getReplyTo());
        // TS: tao.gan 2015-12-25 EMAIL FEATURE-1239148 ADD_E

        // bodyHtml already have the composing spans removed.
        final String htmlBody = message.bodyHtml;
        final String textBody = message.bodyText;
        // fullbody will contain the actual body plus the quoted text.
        final String fullBody;
        final String quotedString;
        final boolean hasQuotedText = !TextUtils.isEmpty(quotedText);
        if (hasQuotedText) {
            // The quoted text is HTML at this point.
            quotedString = quotedText.toString();
            fullBody = htmlBody + quotedString;
            MessageModification.putForward(values, composeMode == ComposeActivity.FORWARD);
            MessageModification.putAppendRefMessageContent(values, true /* include quoted */);
        } else {
            fullBody = htmlBody;
            quotedString = null;
        }
        // Only take refMessage into account if either one of its html/text is not empty.
        if (refMessage != null && !(TextUtils.isEmpty(refMessage.bodyHtml) &&
                TextUtils.isEmpty(refMessage.bodyText))) {
            // The code below might need to be revisited. The quoted text position is different
            // between text/html and text/plain parts and they should be stored seperately and
            // the right version should be used in the UI. text/html should have preference
            // if both exist.  Issues like this made me file b/14256940 to make sure that we
            // properly handle the existing of both text/html and text/plain parts and to verify
            // that we are not making some assumptions that break if there is no text/html part.
            int quotedTextPos = -1;
        //[FEATURE]-Add-BEGIN by TSNJ,Zhenhua.Fan,06/11/2014,FR-622609 1471
        if (EmailApplication.isOrangeImapFeatureOn() && message.serverId != null) {
            values.put(UIProvider.MessageColumns.SERVER_ID, message.serverId);
        }
        //[FEATURE]-Add-END by TSNJ,Zhenhua.Fan
            if (!TextUtils.isEmpty(refMessage.bodyHtml)) {
                MessageModification.putBodyHtml(values, fullBody.toString());
                if (hasQuotedText) {
                    quotedTextPos = htmlBody.length() +
                            QuotedTextView.getQuotedTextOffset(quotedString);
                }
            }
            if (!TextUtils.isEmpty(refMessage.bodyText)) {
                MessageModification.putBody(values,
                        Utils.convertHtmlToPlainText(fullBody.toString()));
                if (hasQuotedText && (quotedTextPos == -1)) {
                    quotedTextPos = textBody.length();
                }
            }
            if (quotedTextPos != -1) {
                // The quoted text pos is the text/html version first and the text/plan version
                // if there is no text/html part. The reason for this is because preference
                // is given to text/html in the compose window if it exists. In the future, we
                // should calculate the index for both since the user could choose to compose
                // explicitly in text/plain.
                MessageModification.putQuoteStartPos(values, quotedTextPos);
            }
        } else {
            MessageModification.putBodyHtml(values, fullBody.toString());
            MessageModification.putBody(values, Utils.convertHtmlToPlainText(fullBody.toString()));
        }
        int draftType = getDraftType(composeMode);
        MessageModification.putDraftType(values, draftType);
        MessageModification.putAttachments(values, message.getAttachments());
        if (!TextUtils.isEmpty(refMessageId)) {
            MessageModification.putRefMessageId(values, refMessageId);
        }
        if (extraValues != null) {
            values.putAll(extraValues);
        }
        SendOrSaveMessage sendOrSaveMessage = new SendOrSaveMessage(context, replyFromAccount,
                values, refMessageId, message.getAttachments(), save);
        SendOrSaveTask sendOrSaveTask = new SendOrSaveTask(context, sendOrSaveMessage, callback,
                draftAccount);

        callback.initializeSendOrSave(sendOrSaveTask);
        // Do the send/save action on the specified handler to avoid possible
        // ANRs
        handler.post(sendOrSaveTask);

        return sendOrSaveMessage.requestId();
    }

    /**
     * Removes any composing spans from the specified string.  This will create a new
     * SpannableString instance, as to not modify the behavior of the EditText view.
     */
    private static SpannableString removeComposingSpans(Spanned body) {
        // TS: tao.gan 2015-09-18 EMAIL BUGFIX_1088747 MOD_S
        //Why init the SpanableString would throw IndexOutOfBoundsException?
        //Only catch it...
        SpannableString messageBody;
        try {
            messageBody = new SpannableString(body);
        } catch (IndexOutOfBoundsException e){
            messageBody = new SpannableString("");
            LogUtils.e(LogUtils.TAG,"IndexOutOfBoundsException while init the spannableString");
        }
        // TS: tao.gan 2015-09-18 EMAIL BUGFIX_1088747 MOD_S
        //TS: kaifeng.lu 2015-8-7 EMAIL BUGFIX_1063281 MOD_S
        try{
            BaseInputConnection.removeComposingSpans(messageBody);
        }catch(IndexOutOfBoundsException e){
            LogUtils.e(LOG_TAG, "Occur IndexOutOfBoundsException");
        }
        //TS: kaifeng.lu 2015-8-7 EMAIL BUGFIX_1063281 MOD_E

        // Remove watcher spans while we're at it, so any off-UI thread manipulation of these
        // spans doesn't trigger unexpected side-effects. This copy is essentially 100% detached
        // from the EditText.
        //
        // (must remove SpanWatchers first to avoid triggering them as we remove other spans)
        removeSpansOfType(messageBody, SpanWatcher.class);
        removeSpansOfType(messageBody, TextWatcher.class);

        return messageBody;
    }

    private static void removeSpansOfType(SpannableString str, Class<?> cls) {
        for (Object span : str.getSpans(0, str.length(), cls)) {
            str.removeSpan(span);
        }
    }

    private static int getDraftType(int mode) {
        int draftType = -1;
        switch (mode) {
            case ComposeActivity.COMPOSE:
                draftType = DraftType.COMPOSE;
                break;
            case ComposeActivity.REPLY:
                draftType = DraftType.REPLY;
                break;
            case ComposeActivity.REPLY_ALL:
                draftType = DraftType.REPLY_ALL;
                break;
            case ComposeActivity.FORWARD:
                draftType = DraftType.FORWARD;
                break;
        }
        return draftType;
    }

    /**
     * Derived classes should override this step to perform additional checks before
     * send or save. The default implementation simply calls {@link #sendOrSave(boolean, boolean)}.
     */
    protected void performAdditionalSendOrSaveSanityChecks(
            final boolean save, final boolean showToast, ArrayList<String> recipients) {
        sendOrSave(save, showToast);
    }

    protected void sendOrSave(final boolean save, final boolean showToast) {
        // Check if user is a monkey. Monkeys can compose and hit send
        // button but are not allowed to send anything off the device.
        // TS: xiaolin.li 2015-01-08 EMAIL BUGFIX-893877 DEL_S
        /*if (ActivityManager.isUserAMonkey()) {
            return;
        }*/
        // TS: xiaolin.li 2015-01-08 EMAIL BUGFIX-893877 DEL_E
        final SendOrSaveCallback callback = new SendOrSaveCallback() {
            // FIXME: unused
            private int mRestoredRequestId;

            @Override
            public void initializeSendOrSave(SendOrSaveTask sendOrSaveTask) {
                synchronized (mActiveTasks) {
                    int numTasks = mActiveTasks.size();
                    if (numTasks == 0) {
                        // Start service so we won't be killed if this app is
                        // put in the background.
                        startService(new Intent(ComposeActivity.this, EmptyService.class));
                    }

                    mActiveTasks.add(sendOrSaveTask);
                }
                if (sTestSendOrSaveCallback != null) {
                    sTestSendOrSaveCallback.initializeSendOrSave(sendOrSaveTask);
                }
            }

            @Override
            public void notifyMessageIdAllocated(SendOrSaveMessage sendOrSaveMessage,
                    Message message) {
                synchronized (mDraftLock) {
                    mDraftAccount = sendOrSaveMessage.mAccount;
                    mDraftId = message.id;
                    mDraft = message;
                    if (sRequestMessageIdMap != null) {
                        sRequestMessageIdMap.put(sendOrSaveMessage.requestId(), mDraftId);
                    }
                    // Cache request message map, in case the process is killed
                    saveRequestMap();
                }
                if (sTestSendOrSaveCallback != null) {
                    sTestSendOrSaveCallback.notifyMessageIdAllocated(sendOrSaveMessage, message);
                }
            }

            @Override
            public Message getMessage() {
                synchronized (mDraftLock) {
                    return mDraft;
                }
            }

            @Override
            public void sendOrSaveFinished(SendOrSaveTask task, boolean success) {
                // Update the last sent from account.
                if (mAccount != null) {
                    MailAppProvider.getInstance().setLastSentFromAccount(mAccount.uri.toString());
                }
                // TS: zhaotianyong 2015-03-25 EMAIL BUGFIX-954496 ADD_S
                // TS: zhaotianyong 2015-03-31 EMAIL BUGFIX-963249 ADD_S
                if ( doSend) {
                    ConnectivityManager mConnectivityManager = (ConnectivityManager) ComposeActivity.this
                            .getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo info = mConnectivityManager.getActiveNetworkInfo();
                    if (info == null) {
                        Utility.showToast(ComposeActivity.this, R.string.send_failed);
                    }
                }
                // TS: zhaotianyong 2015-03-31 EMAIL BUGFIX-963249 ADD_E
                // TS: zhaotianyong 2015-03-25 EMAIL BUGFIX-954496 ADD_E
                if (success) {
                    // Successfully sent or saved so reset change markers
                    discardChanges();
                    //TS: zheng.zou 2015-03-18 EMAIL FEATURE_996919 ADD_S
                    if (!doSend && showToast) {
                        Intent intent = new Intent(DRAFT_SAVED_ACTION);
                        intent.setPackage(getString(R.string.email_package_name));
                        intent.putExtra(BaseColumns._ID, mDraftId);
                        //send ordered broadcast to execute the event in sequence in different receivers,
                        //ordered by priority
                        sendOrderedBroadcast(intent,null);
                    }
                    //TS: zheng.zou 2015-03-18 EMAIL FEATURE_996919 ADD_E
                } else {
                    // A failure happened with saving/sending the draft
                    // TODO(pwestbro): add a better string that should be used
                    // when failing to send or save
                    //[BUGFIX]-MOD by SCDTABLET.shujing.jin@tcl.com,08/05/2016,2635083
                    Utility.showShortToast(ComposeActivity.this, R.string.send_failed);
                    //Toast.makeText(ComposeActivity.this, R.string.send_failed, Toast.LENGTH_SHORT)
                    //        .show();
                }

                int numTasks;
                synchronized (mActiveTasks) {
                    // Remove the task from the list of active tasks
                    mActiveTasks.remove(task);
                    numTasks = mActiveTasks.size();
                }

                if (numTasks == 0) {
                    // Stop service so we can be killed.
                    stopService(new Intent(ComposeActivity.this, EmptyService.class));
                }
                if (sTestSendOrSaveCallback != null) {
                    sTestSendOrSaveCallback.sendOrSaveFinished(task, success);
                }
            }

            @Override
            public void incrementRecipientsTimesContacted(final List<String> recipients) {
                ComposeActivity.this.incrementRecipientsTimesContacted(recipients);
            }
        };
        //TS: zheng.zou 2015-3-16 EMAIL BUGFIX_948927 Mod_S
        if (mReplyFromAccount == null && mAccount != null) {
            mReplyFromAccount = getDefaultReplyFromAccount(mAccount);
        }
        if (mReplyFromAccount != null) {
            setAccount(mReplyFromAccount.account);
        }
        //TS: zheng.zou 2015-3-16 EMAIL BUGFIX_948927 Mod_E
        // TS: yanhua.chen 2015-9-19 EMAIL BUGFIX_569665 ADD_S
        mIsSaveDraft = save;
        // TS: yanhua.chen 2015-9-19 EMAIL BUGFIX_569665 ADD_E

        final Spanned body = removeComposingSpans(mBodyView.getText());
        SEND_SAVE_TASK_HANDLER.post(new Runnable() {
            @Override
            public void run() {
                final Message msg = createMessage(mReplyFromAccount, mRefMessage, getMode(), body);
                // TS: kaifeng.lu 2016-4-6 EMAIL BUGFIX_1909143 ADD_S
                if(/*!mIsClickIcon &&*/ !mEditDraft && (mIsSaveDraft || doSend)){//[BUGFIX]-MOD by SCDTABLET.shujing.jin@tcl.com,05/06/2016,2013535
                    String body1 = mBodyView.getText().toString().replace("\n", "\n\r");
                    SpannableString spannableString = new SpannableString(body1);
                    StringBuffer bodySignature = new StringBuffer(body1);
                    //[BUGFIX]-DEL begin by SCDTABLET.shujing.jin@tcl.com,05/17/2016,2013535,2148647
                    //if(mCachedSettings != null){
                    //    bodySignature.append(convertToPrintableSignature(mCachedSettings.signature));
                    //}
                    //[BUGFIX]-DEL end by SCDTABLET.shujing.jin
                    spannableString = new SpannableString(bodySignature.toString());
                    msg.bodyHtml = spannedBodyToHtml(spannableString, true);
                    msg.bodyText = bodySignature.toString();
                }
                // TS: kaifeng.lu 2016-4-6 EMAIL BUGFIX_1909143 ADD_E
                mRequestId = sendOrSaveInternal(ComposeActivity.this, mReplyFromAccount, msg,
                        mRefMessage, mQuotedTextView.getQuotedTextIfIncluded(), callback,
                        SEND_SAVE_TASK_HANDLER, save, mComposeMode, mDraftAccount, mExtraValues);
            }
        });

        // Don't display the toast if the user is just changing the orientation,
        // but we still need to save the draft to the cursor because this is how we restore
        // the attachments when the configuration change completes.
        if (showToast && (getChangingConfigurations() & ActivityInfo.CONFIG_ORIENTATION) == 0) {
            //TS: xinlei.sheng 2015-01-26 EMAIL FIXBUG_886976 MOD_S
            if (mLaunchContact) {
                mLaunchContact = false;
            } else {
                //TS: zheng.zou 2015-03-18 EMAIL FEATURE_996919 MDD_S
                if (!save) {
					//[BUGFIX]-MOD by SCDTABLET.shujing.jin@tcl.com,08/05/2016,2635083
					Utility.showToast(this, R.string.sending_message);
                    //Toast.makeText(this, R.string.sending_message,
                    //        Toast.LENGTH_LONG).show();
                }
//                Toast.makeText(this, save ? R.string.message_saved : R.string.sending_message,
//                        Toast.LENGTH_LONG).show();
                //TS: zheng.zou 2015-03-18 EMAIL FEATURE_996919 MOD_E
            }
            //TS: xinlei.sheng 2015-01-26 EMAIL FIXBUG_886976 MOD_E
        }

        // Need to update variables here because the send or save completes
        // asynchronously even though the toast shows right away.
        discardChanges();
        updateSaveUi();

        // If we are sending, finish the activity
        if (!save) {
            finish();
            //TS: yanhua.chen 2015-6-15 EMAIL BUGFIX_1024081 ADD_S
            //TS: lin-zhou 2015-10-15 EMAIL BUGFIX_718388 MOD_S
            Uri soundUri = Uri.parse("android.resource://"+getApplicationContext().getPackageName()+"/"+R.raw.email_sent);
            MediaPlayer player = new MediaPlayer();
            try {
                if(soundUri != null){
                    player.setDataSource(getApplicationContext(), soundUri);
                }
                player.setAudioStreamType(AudioManager.STREAM_NOTIFICATION);
                player.prepare();
                player.start();
            } catch (IllegalArgumentException e) {
                LogUtils.e(LOG_TAG, "Send mail mediaPlayer get dataSource occur IllegalArgumentException");
            } catch (SecurityException e) {
                LogUtils.e(LOG_TAG, "Send mail mediaPlayer get dataSource occur SecurityException");
            } catch (IllegalStateException e) {
                LogUtils.e(LOG_TAG, "Send mail mediaPlayer get dataSource occur IllegalStateException");
            } catch (IOException e) {
                LogUtils.e(LOG_TAG, "Send mail mediaPlayer get dataSource occur IOException");
            }catch (NullPointerException e){
                LogUtils.e(LOG_TAG, "Send mail mediaPlayer get dataSource occur NullPointerException");
            }
            //TS: lin-zhou 2015-10-15 EMAIL BUGFIX_718388 MOD_E
            //TS: yanhua.chen 2015-6-15 EMAIL BUGFIX_1024081 ADD_E
        }
    }

    /**
     * Save the state of the request messageid map. This allows for the Gmail
     * process to be killed, but and still allow for ComposeActivity instances
     * to be recreated correctly.
     */
    private void saveRequestMap() {
        // TODO: store the request map in user preferences.
    }

    @SuppressLint("NewApi")
    private void doAttach(String type) {
        mAddingAttachment = true;//TS: yang.mei 2015-1-19 EMAIL BUGFIX_1441004 MOD
        //TS: zheng.zou 2015-11-30 EMAIL TASK_869664 ADD_S
        if (PermissionUtil.checkAndRequestPermissionForResult(this,Manifest.permission.READ_EXTERNAL_STORAGE
                ,PermissionUtil.REQ_CODE_PERMISSION_ADD_ATTACHMENT)){    //TS: jin.dong 2015-12-17 EMAIL BUGFIX_1170083 MOD
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            i.setType(type);
            startActivityForResult(Intent.createChooser(i, getText(R.string.select_attachment_type)),
                    RESULT_PICK_ATTACHMENT);

        }
        //TS: zheng.zou 2015-11-30 EMAIL TASK_869664 ADD_E
    }

    private void showCcBccViews() {
        mCcBccView.show(true, true, true);
        if (mCcBccButton != null) {
            mCcBccButton.setVisibility(View.GONE);

            //[BUGFIX]-Add-BEGIN by SCDTABLET.weiwei.huang,05/03/2016,2013739,
            //[Email]Add mail contact icon display is not consistent
            bccButtonImg.setVisibility(View.GONE);
            ccButtonImg.setVisibility(View.GONE);
            //[BUGFIX]-Add-END by SCDTABLET.weiwei.huang
        }
    }

    private static String getActionString(int action) {
        final String msgType;
        switch (action) {
            case COMPOSE:
                msgType = "new_message";
                break;
            case REPLY:
                msgType = "reply";
                break;
            case REPLY_ALL:
                msgType = "reply_all";
                break;
            case FORWARD:
                msgType = "forward";
                break;
            default:
                msgType = "unknown";
                break;
        }
        return msgType;
    }

    private void logSendOrSave(boolean save) {
        if (!Analytics.isLoggable() || mAttachmentsView == null) {
            return;
        }

        final String category = (save) ? "message_save" : "message_send";
        final int attachmentCount = getAttachments().size();
        final String msgType = getActionString(mComposeMode);
        final String label;
        final long value;
        if (mComposeMode == COMPOSE) {
            label = Integer.toString(attachmentCount);
            value = attachmentCount;
        } else {
            label = null;
            value = 0;
        }
        Analytics.getInstance().sendEvent(category, msgType, label, value);
    }

    // TS: zhaotianyong 2015-05-08 EMAIL BUGFIX_988459 MOD_S
    @Override
    public boolean onNavigationItemSelected(int position, long itemId) {
        int initialComposeMode = mComposeMode;
        // TS: junwei-xu 2015-09-01 EMAIL BUGFIX-526192 ADD_S
        //Note: get the truth compose mode from position
        position = getComposeModeFromListPosition(position, mSupportReplyAll);
        // TS: junwei-xu 2015-09-01 EMAIL BUGFIX-526192 ADD_E
        if (position == ComposeActivity.REPLY) {
            mComposeMode = ComposeActivity.REPLY;
            return afterNavigationItemSelected(initialComposeMode, mComposeMode);
        } else if (position == ComposeActivity.REPLY_ALL) {
            mComposeMode = ComposeActivity.REPLY_ALL;
            return afterNavigationItemSelected(initialComposeMode, mComposeMode);
        } else if (position == ComposeActivity.FORWARD) {
            // TS: zhaotianyong 2015-05-07 EMAIL BUGFIX_995343 MOD_S
            if (mRefMessage == null) {
                mComposeMode = ComposeActivity.FORWARD;
                return afterNavigationItemSelected(initialComposeMode, mComposeMode);
            } else {
                // TS: zhaotianyong 2015-05-11 EMAIL BUGFIX_998470 MOD_S
                // TS: zhaotianyong 2015-05-06 EMAIL BUGFIX_991264 MOD_S
                // TS: zhaotianyong 2015-04-16 EMAIL BUGFIX_978954 MOD_S
                EmailContent.Attachment[] atts = EmailContent.Attachment
                        .restoreAttachmentsWithMessageId(this, mRefMessage.id);
                if (initialComposeMode != FORWARD && !allAttachmentIsDownload(atts) && !supportSmartForward(mAccount)) {  //TS: zheng.zou 2015-11-03 EMAIL BUGFIX_858353 MOD
                    // We go here,set the bool allAttachmentsLoad false.
                    allAttachmentsLoad = false;
                    //TS: zhaotianyong 2015-05-19 EMAIL BUGFIX_988459 MOD_S
                    ChangeForwardDialogFragment f = ChangeForwardDialogFragment.newInstance(initialComposeMode);
                    //TS: zhaotianyong 2015-05-19 EMAIL BUGFIX_988459 MOD_E
                    f.displayDialog(getFragmentManager());
                } else {
                    // TS: zhaotianyong 2015-05-20 EMAIL BUGFIX-998884 ADD_S
                    allAttachmentsLoad = true;
                    // TS: zhaotianyong 2015-05-20 EMAIL BUGFIX-998884 ADD_E
                    mComposeMode = ComposeActivity.FORWARD;
                    return afterNavigationItemSelected(initialComposeMode,
                            mComposeMode);
                }
                // TS: zhaotianyong 2015-04-16 EMAIL BUGFIX_978954 MOD_E
                // TS: zhaotianyong 2015-05-06 EMAIL BUGFIX_991264 MOD_E
            }
            // TS: zhaotianyong 2015-05-11 EMAIL BUGFIX_998470 MOD_E
            // TS: zhaotianyong 2015-05-07 EMAIL BUGFIX_995343 MOD_E
        }
        return true;
    }

    private boolean afterNavigationItemSelected(int initialComposeMode, int composeMode) {
        clearChangeListeners();
        if (initialComposeMode != composeMode) {
            //TS: yanhua.chen 2015-7-29 EMAIL BUGFIX_1053132 ADD_S
            attLargeWarning = true;
            //TS: yanhua.chen 2015-7-29 EMAIL BUGFIX_1053132 ADD_E
            resetMessageForModeChange();
            if (mRefMessage != null) {
                setFieldsFromRefMessage(composeMode);
            }
            //TS: junwei-xu 2015-09-19 EMAIL BUGFIX-622679 ADD_S
            //Note: we need to initialize recipients after change reply mode
            initRecipients();
            //TS: junwei-xu 2015-09-19 EMAIL BUGFIX-622679 ADD_E
            boolean showCc = false;
            boolean showBcc = false;
            if (mDraft != null) {
                // Following desktop behavior, if the user has added a BCC
                // field to a draft, we show it regardless of compose mode.
                showBcc = !TextUtils.isEmpty(mDraft.getBcc());
                // Use the draft to determine what to populate.
                // If the Bcc field is showing, show the Cc field whether it is populated or not.
                showCc = showBcc
                        || (!TextUtils.isEmpty(mDraft.getCc()) && composeMode == REPLY_ALL);
            }
            if (mRefMessage != null) {
                showCc = !TextUtils.isEmpty(mCc.getText());
                showBcc = !TextUtils.isEmpty(mBcc.getText());
            }
            mCcBccView.show(false /* animate */, showCc, showBcc);
        }
        updateHideOrShowCcBcc();
        initChangeListeners();
        return true;
    }
    // TS: zhaotianyong 2015-05-08 EMAIL BUGFIX_988459 MOD_E

    @VisibleForTesting
    protected void resetMessageForModeChange() {
        // When switching between reply, reply all, forward,
        // follow the behavior of webview.
        // The contents of the following fields are cleared
        // so that they can be populated directly from the
        // ref message:
        // 1) Any recipient fields
        // 2) The subject
        // 3) The priority
        mTo.setText("");
        mCc.setText("");
        mBcc.setText("");
        // Any edits to the subject are replaced with the original subject.
        mSubject.setText("");
        //TS: junwei-xu 2015-07-02 EMAIL BUGFIX_1034971 ADD_S
        // clear the priority when switching between reply, reply all, forward
        mPriorityFlag = Message.FLAG_PRIORITY_NORMAL;
        setPriorityIcon(mPriorityFlag);
        //TS: junwei-xu 2015-07-02 EMAIL BUGFIX_1034971 ADD_E

        // Any changes to the contents of the following fields are kept:
        // 1) Body
        // 2) Attachments
        // If the user made changes to attachments, keep their changes.
        // TS: Gantao 2015-07-21 EMAIL BUGFIX_1047618 MOD_S
        // Note:cause very serious problem when switch between reply,reply all, forward,
        // we shouled delete All attachments if the mode changed.
//        if (!mAttachmentsChanged) {
            mAttachmentsView.deleteAllAttachments();
//        }
         // TS: Gantao 2015-07-21 EMAIL BUGFIX_1047618 MOD_E
    }

    private class ComposeModeAdapter extends ArrayAdapter<String> {

        private Context mContext;
        private LayoutInflater mInflater;

        public ComposeModeAdapter(Context context) {
            super(context, R.layout.compose_mode_item, R.id.mode, getResources()
                    .getStringArray(R.array.compose_modes));
            mContext = context;
        }

        // TS: junwei-xu 2015-09-01 EMAIL BUGFIX-526192 ADD_S
        public ComposeModeAdapter(Context context, boolean isSupportReplyAll) {
            super(context, R.layout.compose_mode_item, R.id.mode, getResources()
                    .getStringArray(isSupportReplyAll ? R.array.compose_modes : R.array.compose_modes_without_reply_all));
            mContext = context;
        }
        // TS: junwei-xu 2015-09-01 EMAIL BUGFIX-526192 ADD_E

        private LayoutInflater getInflater() {
            if (mInflater == null) {
                mInflater = LayoutInflater.from(mContext);
            }
            return mInflater;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getInflater().inflate(R.layout.compose_mode_display_item, null);
            }
            ((TextView) convertView.findViewById(R.id.mode)).setText(getItem(position));
            return super.getView(position, convertView, parent);
        }
    }

    @Override
    public void onRespondInline(String text) {
        //TS: yanhua.chen 2015-11-11 EMAIL BUGFIX_861373 MOD_S
        mRespondedInline = true;
        appendToBody(text, false);
        //hidden "..." icon
        //mIsClickIcon = true;//[BUGFIX]-MOD by SCDTABLET.shujing.jin@tcl.com,05/06/2016,2013535
        mBodySignature.setVisibility(View.GONE);
        //TS: yanhua.chen 2015-11-11 EMAIL BUGFIX_861373 MOD_E
        mQuotedTextView.setUpperDividerVisible(false);
        if (!mBodyView.hasFocus()) {
            mBodyView.requestFocus();
        }
    }

    /**
     * Append text to the body of the message. If there is no existing body
     * text, just sets the body to text.
     *
     * @param text Text to append
     * @param withSignature True to append a signature.
     */
    public void appendToBody(CharSequence text, boolean withSignature) {
        Editable bodyText = mBodyView.getEditableText();
        if (bodyText != null && bodyText.length() > 0) {
            //TS: yanhua.chen 2015-11-11 EMAIL BUGFIX_861373 ADD_S
            //if click respone inline,directly show signature,hidden "..." icon
            if(mRespondedInline && /*!mIsClickIcon &&*/ !TextUtils.isEmpty(mSignature)){//[BUGFIX]-MOD by SCDTABLET.shujing.jin@tcl.com,05/06/2016,2013535
                bodyText.append(convertToPrintableSignature(mSignature));
            }
            //TS: yanhua.chen 2015-11-11 EMAIL BUGFIX_861373 ADD_E
            bodyText.append(text);
        } else {
            setBody(text, withSignature);
        }
    }

    /**
     * Set the body of the message.
     *
     * @param text text to set
     * @param withSignature True to append a signature.
     */
    public void setBody(CharSequence text, boolean withSignature) {
        //TS: yanhua.chen 2015-11-11 EMAIL BUGFIX_861373 ADD_S
        //if click respone inline,directly show signature
        if(mRespondedInline && /*!mIsClickIcon &&*/ !TextUtils.isEmpty(mSignature)){//[BUGFIX]-MOD by SCDTABLET.shujing.jin@tcl.com,05/06/2016,2013535
            mBodyView.append(convertToPrintableSignature(mSignature));
        }
        //TS: yanhua.chen 2015-11-11 EMAIL BUGFIX_861373 ADD_E
        mBodyView.append(text);
        if (withSignature) {
            //TS: yanhua.chen 2015-9-1 EMAIL CD_551912 MOD_S
            //Note:when from widget compose and mailtoUri,don't append signature
            //Note:when change the account,set mSignature the value to currentAccount's signature
            //appendSignature();
            mSignature = mCachedSettings.signature;
            //TS: yanhua.chen 2015-9-1 EMAIL CD_551912 MOD_E
        }
    }

    private void appendSignature() {
        final String newSignature = mCachedSettings != null ? mCachedSettings.signature : null;
        final int signaturePos = getSignatureStartPosition(mSignature, mBodyView.getText().toString());
        if (!TextUtils.equals(newSignature, mSignature) || signaturePos < 0) {
            mSignature = newSignature;
            if (!TextUtils.isEmpty(mSignature)) {
                // Appending a signature does not count as changing text.
                mBodyView.removeTextChangedListener(this);
                mBodyView.append(convertToPrintableSignature(mSignature));
                mBodyView.addTextChangedListener(this);
            }
            resetBodySelection();
        }
    }

    private String convertToPrintableSignature(String signature) {
        String signatureResource = getResources().getString(R.string.signature);
        if (signature == null) {
            signature = "";
        }
        return String.format(signatureResource, signature);
    }

    @Override
    public void onAccountChanged() {
        mReplyFromAccount = mFromSpinner.getCurrentAccount();
     // TS: chenyanhua 2015-01-05 EMAIL BUGFIX-879794 ADD_S
        mValidator=null;
     // TS: chenyanhua 2015-01-05 EMAIL BUGFIX_879794 ADD_E
        if (!mAccount.equals(mReplyFromAccount.account)) {
            //TS: yanhua.chen 2015-9-1 EMAIL CD_551912 ADD_S
            //Note:when change the account,set the mIsClickIcon flag to the initial value
            mChangeAccount = true;
            //mIsClickIcon = false;//[BUGFIX]-MOD by SCDTABLET.shujing.jin@tcl.com,05/06/2016,2013535
            //TS: yanhua.chen 2015-9-1 EMAIL CD_551912 ADD_E
            // Clear a signature, if there was one.
            mBodyView.removeTextChangedListener(this);
            String oldSignature = mSignature;
            String bodyText = getBody().getText().toString();
            if (!TextUtils.isEmpty(oldSignature)) {
                int pos = getSignatureStartPosition(oldSignature, bodyText);
                if (pos > -1) {
                    mBodyView.setText(bodyText.substring(0, pos));
                }
            }
            setAccount(mReplyFromAccount.account);
            mBodyView.addTextChangedListener(this);
            // TODO: handle discarding attachments when switching accounts.
            // Only enable save for this draft if there is any other content
            // in the message.
            if (!isBlank()) {
                enableSave(true);
            }
            mReplyFromChanged = true;
            initRecipients();

            invalidateOptionsMenu();
        }
    }

    public void enableSave(boolean enabled) {
        if (mSave != null) {
            mSave.setEnabled(enabled);
        }
    }

    public static class DiscardConfirmDialogFragment extends DialogFragment {
        // Public no-args constructor needed for fragment re-instantiation
        public DiscardConfirmDialogFragment() {}

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return new AlertDialog.Builder(getActivity())
                    .setMessage(R.string.confirm_discard_text)
                    .setPositiveButton(R.string.discard,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ((ComposeActivity)getActivity()).doDiscardWithoutConfirmation();
                                }
                            })
                    .setNegativeButton(R.string.cancel, null)
                    .create();
        }
    }

    private void doDiscard() {
        final DialogFragment frag = new DiscardConfirmDialogFragment();
        //AM: peng-zhang 2015-02-15 EMAIL BUGFIX_930453 MOD_S
        //frag.show(getFragmentManager(), "discard confirm");
        try{
            frag.show(getFragmentManager(), "discard confirm");
        }catch(IllegalStateException e){
            LogUtils.e(LOG_TAG, "FragmentManager checkStateLoss!");
            e.printStackTrace();
        }
        //AM: peng-zhang 2015-02-15 EMAIL BUGFIX_930453 MOD_E
    }
    /**
     * Effectively discard the current message.
     *
     * This method is either invoked from the menu or from the dialog
     * once the user has confirmed that they want to discard the message.
     */
    private void doDiscardWithoutConfirmation() {
        synchronized (mDraftLock) {
            if (mDraftId != UIProvider.INVALID_MESSAGE_ID) {
                ContentValues values = new ContentValues();
                values.put(BaseColumns._ID, mDraftId);
                if (!mAccount.expungeMessageUri.equals(Uri.EMPTY)) {
                    getContentResolver().update(mAccount.expungeMessageUri, values, null, null);
                } else {
                    getContentResolver().delete(mDraft.uri, null, null);
                }
                // This is not strictly necessary (since we should not try to
                // save the draft after calling this) but it ensures that if we
                // do save again for some reason we make a new draft rather than
                // trying to resave an expunged draft.
                mDraftId = UIProvider.INVALID_MESSAGE_ID;
            }
        }

        // Display a toast to let the user know
        //[BUGFIX]-MOD by SCDTABLET.shujing.jin@tcl.com,08/05/2016,2635083
        Utility.showShortToast(this, R.string.message_discarded);
        //Toast.makeText(this, R.string.message_discarded, Toast.LENGTH_SHORT).show();

        // This prevents the draft from being saved in onPause().
        discardChanges();
        mPerformedSendOrDiscard = true;
        finish();
    }

    private void saveIfNeeded() {
        if (mAccount == null) {
            // We have not chosen an account yet so there's no way that we can save. This is ok,
            // though, since we are saving our state before AccountsActivity is activated. Thus, the
            // user has not interacted with us yet and there is no real state to save.
            return;
        }

        if (shouldSave()) {
            //TS:kaifeng.lu 2015-9-30 EMAIL BUGFIX_673904 MOD_S
            //add a judgment condition "!mLaunchContact"
            doSave(!mAddingAttachment &&!mLaunchContact /* show toast */);
            //TS:kaifeng.lu 2015-9-30 EMAIL BUGFIX_673904 MOD_E
        }
    }

    @Override
    public void onAttachmentDeleted() {
        mAttachmentsChanged = true;
        // If we are showing any attachments, make sure we have an upper
        // divider.
        mQuotedTextView.setUpperDividerVisible(mAttachmentsView.getAttachments().size() > 0);
        updateSaveUi();
        //TS: yanhua.chen 2015-7-29 EMAIL BUGFIX_1053132 ADD_S
        long total = getTotalSize();
        if(total < Settings.DEFAULT_MID_ATTACHMENT_SIZE){
            attLargeWarning = true;
        }
        //TS: yanhua.chen 2015-7-29 EMAIL BUGFIX_1053132 ADD_E

        //TS: jin.dong 2015-7-30 EMAIL BUGFIX_1053132 ADD_S
        mAttachmentsView.requestFocusFromTouch();
        //TS: jin.dong 2015-7-30 EMAIL BUGFIX_1053132 ADD_E
    }

    //TS: yanhua.chen 2015-7-29 EMAIL BUGFIX_1053132 ADD_S
    //get the attachment total size
    private long getTotalSize(){
        long totalSize = 0;
        if(mAttachmentsView.getAttachments().size()>0){
           for(Attachment att : mAttachmentsView.getAttachments()){
               totalSize += att.size;
           }
        }
        return totalSize;
    }
    //TS: yanhua.chen 2015-7-29 EMAIL BUGFIX_1053132 ADD_E

    @Override
    public void onAttachmentAdded() {
        mQuotedTextView.setUpperDividerVisible(mAttachmentsView.getAttachments().size() > 0);
        mAttachmentsView.focusLastAttachment();
    }

    /**
     * This is called any time one of our text fields changes.
     */
    @Override
    public void afterTextChanged(Editable s) {
        mTextChanged = true;
        updateSaveUi();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // Do nothing.
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        // Do nothing.
    }


    // There is a big difference between the text associated with an address changing
    // to add the display name or to format properly and a recipient being added or deleted.
    // Make sure we only notify of changes when a recipient has been added or deleted.
    private class RecipientTextWatcher implements TextWatcher {
        private HashMap<String, Integer> mContent = new HashMap<String, Integer>();

        private RecipientEditTextView mView;

        private TextWatcher mListener;

        public RecipientTextWatcher(RecipientEditTextView view, TextWatcher listener) {
            mView = view;
            mListener = listener;
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (hasChanged()) {
                mListener.afterTextChanged(s);
            }
            // TS: jian.xu 2015-06-05 EMAIL BUGFIX-1006499 ADD_S
            //TODO: this code is used to make sure that the popupwindow can also shows rightly
            //when the view's location was changed by keyboard's effect. it may be not a good way
            //to fix this issue. but now we can't found other way to fix this, so just use this code.
            //if there is better way, we will change the code.
            for(int i=1;i < 5; i++){
                android.os.Message msg1 = mHandler.obtainMessage(SET_LISTPOPUPWINDOW_HEIGHT);
                mHandler.sendMessageDelayed(msg1, i * 50);
            }
            // TS: jian.xu 2015-06-05 EMAIL BUGFIX-1006499 ADD_E
        }

        private boolean hasChanged() {
            final ArrayList<String> currRecips = buildEmailAddressList(getAddressesFromList(mView));
            int totalCount = currRecips.size();
            int totalPrevCount = 0;
            for (Entry<String, Integer> entry : mContent.entrySet()) {
                totalPrevCount += entry.getValue();
            }
            if (totalCount != totalPrevCount) {
                return true;
            }

            for (String recip : currRecips) {
                if (!mContent.containsKey(recip)) {
                    return true;
                } else {
                    int count = mContent.get(recip) - 1;
                    if (count < 0) {
                        return true;
                    } else {
                        mContent.put(recip, count);
                    }
                }
            }
            return false;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            final ArrayList<String> recips = buildEmailAddressList(getAddressesFromList(mView));
            for (String recip : recips) {
                if (!mContent.containsKey(recip)) {
                    mContent.put(recip, 1);
                } else {
                    mContent.put(recip, (mContent.get(recip)) + 1);
                }
            }
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // Do nothing.
        }
    }

    /**
     * Returns a list of email addresses from the recipients. List only contains
     * email addresses strips additional info like the recipient's name.
     */
    private static ArrayList<String> buildEmailAddressList(String[] recips) {
        // Tokenize them all and put them in the list.
        final ArrayList<String> recipAddresses = Lists.newArrayListWithCapacity(recips.length);
        for (int i = 0; i < recips.length; i++) {
            recipAddresses.add(Rfc822Tokenizer.tokenize(recips[i])[0].getAddress());
        }
        return recipAddresses;
    }

    public static void registerTestSendOrSaveCallback(SendOrSaveCallback testCallback) {
        if (sTestSendOrSaveCallback != null && testCallback != null) {
            throw new IllegalStateException("Attempting to register more than one test callback");
        }
        sTestSendOrSaveCallback = testCallback;
    }

    @VisibleForTesting
    protected ArrayList<Attachment> getAttachments() {
        return mAttachmentsView.getAttachments();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case INIT_DRAFT_USING_REFERENCE_MESSAGE:
                return new CursorLoader(this, mRefMessageUri, UIProvider.MESSAGE_PROJECTION, null,
                        null, null);
            case REFERENCE_MESSAGE_LOADER:
                return new CursorLoader(this, mRefMessageUri, UIProvider.MESSAGE_PROJECTION, null,
                        null, null);
            case LOADER_ACCOUNT_CURSOR:
                return new CursorLoader(this, MailAppProvider.getAccountsUri(),
                        UIProvider.ACCOUNTS_PROJECTION, null, null, null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        int id = loader.getId();
        switch (id) {
            case INIT_DRAFT_USING_REFERENCE_MESSAGE:
                if (data != null && data.moveToFirst()) {
                    mRefMessage = new Message(data);
                    Intent intent = getIntent();
                    initFromRefMessage(mComposeMode);
                    finishSetup(mComposeMode, intent, null);
                    if (mComposeMode != FORWARD) {
                        String to = intent.getStringExtra(EXTRA_TO);
                        if (!TextUtils.isEmpty(to)) {
                            mRefMessage.setTo(null);
                            mRefMessage.setFrom(null);
                            clearChangeListeners();
                            mTo.append(to);
                            initChangeListeners();
                        }
                    }
                } else {
                    finish();
                }
                break;
            case REFERENCE_MESSAGE_LOADER:
                // Only populate mRefMessage and leave other fields untouched.
                if (data != null && data.moveToFirst()) {
                    mRefMessage = new Message(data);
                }
                finishSetup(mComposeMode, getIntent(), mInnerSavedState);
                break;
            case LOADER_ACCOUNT_CURSOR:
                if (data != null && data.moveToFirst()) {
                    // there are accounts now!
                    Account account;
                    final ArrayList<Account> accounts = new ArrayList<Account>();
                    final ArrayList<Account> initializedAccounts = new ArrayList<Account>();
                    do {
                        account = Account.builder().buildFrom(data);
                        if (account.isAccountReady()) {
                            initializedAccounts.add(account);
                        }
                        accounts.add(account);
                    } while (data.moveToNext());
                    if (initializedAccounts.size() > 0) {
                        findViewById(R.id.wait).setVisibility(View.GONE);
                        getLoaderManager().destroyLoader(LOADER_ACCOUNT_CURSOR);
                        findViewById(R.id.compose).setVisibility(View.VISIBLE);
                        mAccounts = initializedAccounts.toArray(
                                new Account[initializedAccounts.size()]);

                        finishCreate();
                        invalidateOptionsMenu();
                    } else {
                        // Show "waiting"
                        account = accounts.size() > 0 ? accounts.get(0) : null;
                        showWaitFragment(account);
                    }
                }
                break;
        }
    }

    private void showWaitFragment(Account account) {
        WaitFragment fragment = getWaitFragment();
        if (fragment != null) {
            fragment.updateAccount(account);
        } else {
            findViewById(R.id.wait).setVisibility(View.VISIBLE);
            replaceFragment(WaitFragment.newInstance(account, false /* expectingMessages */),
                    FragmentTransaction.TRANSIT_FRAGMENT_OPEN, TAG_WAIT);
        }
    }

    private WaitFragment getWaitFragment() {
        return (WaitFragment) getFragmentManager().findFragmentByTag(TAG_WAIT);
    }

    private int replaceFragment(Fragment fragment, int transition, String tag) {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.setTransition(transition);
        fragmentTransaction.replace(R.id.wait, fragment, tag);
        final int transactionId = fragmentTransaction.commitAllowingStateLoss();
        return transactionId;
    }

    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {
        // Do nothing.
    }

    /**
     * Background task to convert the message's html to Spanned.
     */
    private class HtmlToSpannedTask extends AsyncTask<String, Void, Spanned> {

        @Override
        protected Spanned doInBackground(String... input) {
            return HtmlUtils.htmlToSpan(input[0], mSpanConverterFactory);
        }

        @Override
        protected void onPostExecute(Spanned spanned) {
            mBodyView.removeTextChangedListener(ComposeActivity.this);
            mBodyView.setText(spanned);
            // TS: junwei-xu 2015-07-17 EMAIL BUGFIX-1029180 DEL_S
            //mTextChanged = false;
            // TS: junwei-xu 2015-07-17 EMAIL BUGFIX-1029180 DEL_E
            mBodyView.addTextChangedListener(ComposeActivity.this);
            // TS: xujian 2015-11-16 EMAIL BUGFIX-1106881 ADD_S
            resetBodySelection();
            // TS: xujian 2015-11-16 EMAIL BUGFIX-1106881 ADD_E
        }
    }
    //TS: wenggangjin 2015-02-03 EMAIL BUGFIX_-920087 ADD_S
    @SuppressLint("NewApi")
    public String getFilePath(Uri uri) {
        Uri thisUri = null;
        String path = "";

        ContentResolver cr = this.getContentResolver();
        if (uri == null) {
            return path;
        }
        thisUri = uri;

        try {

            String scheme = thisUri.getScheme();

            if (scheme == null) {
                path = thisUri.toString();
            } else if (scheme.equals("file")) {
                path = thisUri.getPath();
                path = changeDrmFileSuffix(path);
            }

            else if (DocumentsContract.isDocumentUri(this, uri)){
                //ExternalStorageProvider
                if (isExternalStorageDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];
                    if ("primary".equalsIgnoreCase(type)) {
                        return Environment.getExternalStorageDirectory() + "/" + split[1];
                    }
                }
                // DownloadsProvider
                else if (isDownloadsDocument(uri)) {
                    final String id = DocumentsContract.getDocumentId(uri);
                    final Uri contentUri = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                    return getDataColumn(this, contentUri, null, null);
                }
                // MediaProvider
                else if (isMediaDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];
                    Uri contentUri = null;
                    if ("image".equals(type)) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if ("video".equals(type)) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if ("audio".equals(type)) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }
                    contentUri = contentUri.withAppendedPath(contentUri, split[1]);
                    return getDataColumn(this, contentUri, null, null);
                }
            }

            else if (scheme.equals("content")) {
                String[] projection = { "_data" };
                Cursor c = cr.query(thisUri, projection, null, null, null);
                if (c != null) {
                    try {
                        if (c.moveToFirst()) {
                            path = c.getString(0);
                        }
                    } finally {
                        c.close();
                    }
                }

                if (path.endsWith("RAW")) {
                    List<String> segments = thisUri.getPathSegments();
                    String dbName = segments.get(0);
                    String id = segments.get(1);
                    path = this.getDatabasePath(dbName + "_att") + "/" + id;
                }

            }
        } catch (Exception e) {
        }
        return path;
    }

    private String changeDrmFileSuffix(String filePath) {
        String constTmp = "/storage/sdcard0/Download/";
        String tmpEmulated = "/storage/emulated/0/Download/";
        if (null != filePath && filePath.startsWith("/storage/emulated/0/")) {
            String tmpStr = filePath.substring(tmpEmulated.length(), filePath.length());
            String tmp = constTmp + tmpStr;
            return tmp;
        }
        return filePath;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static String getDataColumn(Context context, Uri uri, String selection,
            String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
            column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                String path = cursor.getString(index);
                //[BUGFIX]-Add by TCTNJ,chuang.wang, 2014-07-12,PR728605 Begin
                if (path != null && path.endsWith("RAW")) {
                    //[BUGFIX]-Add by TCTNJ,chuang.wang, 2014-07-12,PR728605 END
                    List<String> segments = uri.getPathSegments();
                    String dbName = segments.get(0);
                    String id = segments.get(1);
                    path = context.getDatabasePath(dbName + "_att") + "/" + id;
                }
                return path;
            }
        //[BUGFIX]-Mod-BEGIN by TSNJ,yong.tao,1/27/2015, PR-913357
        } catch (Exception e) {
        //[BUGFIX]-Mod-END by TSNJ,yong.tao
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }
    //TS: wenggangjin 2015-02-03 EMAIL BUGFIX_-920087 ADD_E

  //TS: yanhua.chen 2015-4-16 EMAIL BUGFIX_963186 ADD_S
    @Override
    public void onSupportActionModeStarted(android.support.v7.view.ActionMode mode) {
        changeStatusBarColor();
        super.onSupportActionModeStarted(mode);
    }

    @Override
    public void onSupportActionModeFinished(android.support.v7.view.ActionMode mode) {
        restoreStatusBarColor();
        super.onSupportActionModeFinished(mode);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void changeStatusBarColor() {
        Window window = ComposeActivity.this.getWindow();
        if(window != null){
            window.setStatusBarColor(getResources().getColor(R.color.change_status_bar));
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void restoreStatusBarColor() {
        Window window = ComposeActivity.this.getWindow();
        if(window != null){
            window.setStatusBarColor(getResources().getColor(R.color.restore_status_bar));
        }
    }
  //TS: yanhua.chen 2015-4-16 EMAIL BUGFIX_963186 ADD_E

    //TS: zhaotianyong 2015-05-06 EMAIL BUGFIX_991264 MOD_S
    //TS: zhaotianyong 2015-04-16 EMAIL BUGFIX_978954 ADD_S
    /**
     * determine whether the attachments is all downloaded.
     */
    public static boolean allAttachmentIsDownload(EmailContent.Attachment[] atts) {
        for (EmailContent.Attachment attachment : atts) {
            if (attachment == null
                    || TextUtils.isEmpty(attachment.getContentUri())
                    || !(attachment.isSaved() && attachment.mUiState == UIProvider.AttachmentState.SAVED)) {
                return false;
            }
        }
        return true;
    }
    //TS: zhaotianyong 2015-04-16 EMAIL BUGFIX_978954 ADD_E
    //TS: zhaotianyong 2015-05-06 EMAIL BUGFIX_991264 MOD_E

    //TS: zheng.zou 2015-11-03 EMAIL BUGFIX_858353 ADD_S
    public static boolean supportSmartForward(Account account) {
        return account != null && account.supportsCapability(AccountCapabilities.SMART_FORWARD);
    }
    //TS: zheng.zou 2015-11-03 EMAIL BUGFIX_858353 ADD_E

    //TS: zhaotianyong 2015-05-19 EMAIL BUGFIX_988459 MOD_S
    @Override
    public void setComposeMode(int composeMode) {
      mComposeMode = composeMode;
      afterNavigationItemSelected(0, mComposeMode);
    }

    @Override
    public void setNavigationItem(int selectedItem) {
        getSupportActionBar().setSelectedNavigationItem(selectedItem);
    }
    //TS: zhaotianyong 2015-05-19 EMAIL BUGFIX_988459 MOD_E

    // TS: Gantao 2015-10-30 EMAIL FEATURE_1104470 ADD_S
    /*
     * Show save group dialogFragment
     */
    private void showSaveGroupDialog() {
        //Get string array from to/cc/bcc field
        String[] toMails = Address.getMailsArray(formatSenders(mTo.getText().toString()));
        String[] ccMails = Address.getMailsArray(formatSenders(mCc.getText().toString()));
        String[] BccMails = Address.getMailsArray(formatSenders(mBcc.getText().toString()));
        SaveGroupDialog dialog = SaveGroupDialog.newInstance(toMails, ccMails, BccMails);
        dialog.displayDialog(getFragmentManager());
    }
    // TS: Gantao 2015-10-30 EMAIL FEATURE_1104470 ADD_E

    // TS: Gantao 2015-12-17 EMAIL BUGFIX_1176396 ADD_S
    /**
     * Set image view according to the priority level
     * @param imageLevel  the priority level
     */
    private void setPriorityIcon(int imageLevel) {
        switch (imageLevel) {
            case Message.FLAG_PRIORITY_HIGH:
                mPriorityIcon.setImageResource(R.drawable.ic_high_priority);
                break;
            case Message.FLAG_PRIORITY_NORMAL:
                mPriorityIcon.setImageResource(R.drawable.ic_normal_priority);
                break;
            case Message.FLAG_PRIORITY_LOW:
                mPriorityIcon.setImageResource(R.drawable.ic_low_priority);
                break;
            default:
                mPriorityIcon.setImageResource(R.drawable.ic_normal_priority);
                break;
        }
    }
    // TS: Gantao 2015-12-17 EMAIL BUGFIX_1176396 ADD_E
    //TS: jin.dong 2015-12-17 EMAIL BUGFIX_1170083 ADD_S
    private void showNeedPermissionToast(int descId) {
        final ActionableToastBar.ActionClickedListener listener = new ActionableToastBar.ActionClickedListener() {
            @Override
            public void onActionClicked(Context context,int undoNum) {
                PermissionUtil.gotoSettings(context);
            }
        };
        mToastBar.show(listener, getString(descId), R.string.permission_grant_go_setting, true,
                new ToastBarOperation(1, 0, ToastBarOperation.INFO, false, null));

    }
    //TS: jin.dong 2015-12-17 EMAIL BUGFIX_1170083 ADD_E

    // TS: tao.gan 2015-12-25 EMAIL FEATURE-1239148 ADD_S

    /**
     * Get reply to address from account ,which is set in account settings
     * @return reply to address
     */
    private String getReplyToAddress() {
        //TS: zheng.zou 2016-03-21 EMAIL BUGFIX_1801683 MOD_S
        if(mAccount == null || mAccount.settings == null) {
            LogUtils.e(LOG_TAG, "Null account while get reply to address");
            return "";
        }
        return mAccount.settings.replyTo;
        //TS: zheng.zou 2016-03-21 EMAIL BUGFIX_1801683 MOD_E
    }
    // TS: tao.gan 2015-12-25 EMAIL FEATURE-1239148 ADD_E

}
