/*
 * Copyright (C) 2011 The Android Open Source Project
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
/* 06/09/2014|     Zhenhua Fan      |      FR 622609       |[Orange][Android  */
/*           |                      |                      |guidelines]IMAP   */
/*           |                      |                      |support           */
/* ----------|----------------------|----------------------|----------------- */
/******************************************************************************/
/*
 ==========================================================================
 *HISTORY
 *
 *Tag		 Date	      Author		  Description
 *============== ============ =============== ==============================
 *TASK-869664  2015/11/25   zheng.zou     [Email]Android M Permission Upgrade
 *TASK-869664  2015/11/30   zheng.zou     [Email]Android M Permission Upgrade
 *BUGFIX-1030520    2015/12/02    zheng.zou     [Android 6.0][Email][Force close]Email force close when share contact with no contact permission if no account created
 ===========================================================================
 */
package com.tct.email;

import android.*;
import android.Manifest;
import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.tct.email.activity.ComposeActivityEmail;
import com.tct.email.activity.EventViewer;
import com.tct.email.activity.setup.AccountSetupFinal;
import com.tct.email2.ui.MailActivityEmail;
import com.tct.mail.browse.EmlViewerActivity;
import com.tct.mail.utils.LogTag;
import com.tct.mail.utils.PLFUtils;
import com.tct.email.activity.setup.EmailPreferenceActivity;
import com.tct.email.preferences.EmailPreferenceMigrator;
import com.tct.mail.browse.ConversationMessage;
import com.tct.mail.browse.InlineAttachmentViewIntentBuilder;
import com.tct.mail.browse.InlineAttachmentViewIntentBuilderCreator;
import com.tct.mail.browse.InlineAttachmentViewIntentBuilderCreatorHolder;
import com.tct.mail.preferences.BasePreferenceMigrator;
import com.tct.mail.preferences.PreferenceMigratorHolder;
import com.tct.mail.preferences.PreferenceMigratorHolder.PreferenceMigratorCreator;
import com.tct.mail.providers.Account;
import com.tct.mail.ui.settings.PublicPreferenceActivity;
import com.tct.mail.utils.SortHelper;
import com.tct.permission.PermissionBlockActivity;
import com.tct.permission.PermissionUtil;
import com.tct.email.provider.Utilities;

public class EmailApplication extends Application {
    private static final String LOG_TAG = "Email";

    //[FEATURE]-Add-BEGIN by 622609
    private static boolean isOrangeImapFeatureOn;
    //[FEATURE]-Add-END by TSNJ.Zhenhua.Fan
    //[FRETURE]-ADD-BEGIN by TSNJ.wei huang 10/27/2014 PR739323
    private static boolean isSFRImapFeatureOn;
    //[FEATURE]-ADD-END by TSNJ.wei huang
    static {
        LogTag.setLogTag(LOG_TAG);

        PreferenceMigratorHolder.setPreferenceMigratorCreator(new PreferenceMigratorCreator() {
            @Override
            public BasePreferenceMigrator createPreferenceMigrator() {
                return new EmailPreferenceMigrator();
            }
        });

        InlineAttachmentViewIntentBuilderCreatorHolder.setInlineAttachmentViewIntentCreator(
                new InlineAttachmentViewIntentBuilderCreator() {
                    @Override
                    public InlineAttachmentViewIntentBuilder
                    createInlineAttachmentViewIntentBuilder(Account account, long conversationId) {
                        return new InlineAttachmentViewIntentBuilder() {
                            @Override
                            public Intent createInlineAttachmentViewIntent(Context context,
                                    String url, ConversationMessage message) {
                                return null;
                            }
                        };
                    }
                });

        PublicPreferenceActivity.sPreferenceActivityClass = EmailPreferenceActivity.class;
        //TS: zheng.zou 2015-11-25 EMAIL TASK_996919 ADD_S
        //TS: zheng.zou 2015-12-03 EMAIL BUGFIX_1030520 MOD_S
        //TS: zheng.zou 2016-1-14 EMAIL TASK_1427138 MOD_S
        //[BUGFIX]-Mod-BEGIN by SCDTABLET.yingjie.chen@tcl.com,03/07/2016,1746249,
        //Can't login Email prompt permissson issue.
        PermissionUtil.permissionMap.put(MailActivityEmail.class.getName(), new String[]{android.Manifest.permission.READ_CONTACTS, Manifest.permission.READ_PHONE_STATE});
        PermissionUtil.permissionMap.put(PermissionBlockActivity.class.getName(), new String[]{android.Manifest.permission.READ_CONTACTS, Manifest.permission.READ_PHONE_STATE});
        PermissionUtil.permissionMap.put(ComposeActivityEmail.class.getName(), new String[]{android.Manifest.permission.READ_CONTACTS, Manifest.permission.READ_PHONE_STATE});
        PermissionUtil.permissionMap.put(AccountSetupFinal.class.getName(), new String[]{android.Manifest.permission.READ_CONTACTS, Manifest.permission.READ_PHONE_STATE});
        //[BUGFIX]-Mod-END by SCDTABLET.yingjie.chen@tcl.com
        //TS: zheng.zou 2016-1-14 EMAIL TASK_1427138 MOD_E
        PermissionUtil.permissionMap.put(EmlViewerActivity.class.getName(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}); // TS: zheng.zou 2015-12-23 EMAIL BUGFIX-1209662 ADD
        PermissionUtil.permissionMap.put(EventViewer.class.getName(), new String[]{android.Manifest.permission.READ_CALENDAR});

        PermissionUtil.entranceActivitySet.add(MailActivityEmail.class.getName());
        PermissionUtil.entranceActivitySet.add(ComposeActivityEmail.class.getName());
        PermissionUtil.entranceActivitySet.add(EventViewer.class.getName());   //TS: zheng.zou 2015-11-30 EMAIL TASK_869664 ADD
        //TS: zheng.zou 2015-11-25 EMAIL TASK_996919 ADD_E
        PermissionUtil.entranceActivitySet.add(AccountSetupFinal.class.getName());   //TS: zheng.zou 2015-11-30 EMAIL TASK_869664 ADD
        //TS: zheng.zou 2015-12-03 EMAIL BUGFIX_1030520 MOD_E

    }
    @Override
    public void onCreate() {
        super.onCreate();
        // TS: xiaolin.li 2014-11-25 EMAIL READ_PLF MOD_S
        //[FEATURE]-Add-BEGIN by TSNJ.Zhenhua.Fan,09/06/2014,FR 622609
        //isOrangeImapFeatureOn = this.getResources().getBoolean(R.bool.feature_email_orangeImap_on);
        isOrangeImapFeatureOn = PLFUtils.getBoolean(this, "feature_email_orangeImap_on");
        //[FEATURE]-Add-END by TSNJ.Zhenhua.Fan
        //[FRETURE]-ADD-BEGIN by TSNJ.wei huang 10/27/2014 PR739323
        //isSFRImapFeatureOn = this.getResources().getBoolean(R.bool.feature_email_sfrImap_on);
        isSFRImapFeatureOn = PLFUtils.getBoolean(this, "feature_email_sfrImap_on");
        //[FEATURE]-ADD-END by TSNJ.wei huang
        // TS: xiaolin.li 2014-11-25 EMAIL READ_PLF MOD_E
        SortHelper.loadPlfSetting(this);    //TS: zheng.zou 2016-1-14 EMAIL TASK_1431225 ADD
    }

    //[FEATURE]-Add-BEGIN by TSNJ.Zhenhua.Fan,09/06/2014,FR 622609
    public static boolean isOrangeImapFeatureOn() {
        return isOrangeImapFeatureOn;
    }
    //[FEATURE]-Add-END by TSNJ.Zhenhua.Fan
    //[FRETURE]-ADD-BEGIN by TSNJ.wei huang 10/27/2014 PR739323
    public static boolean isSfrImapFeatureOn(){ return  isSFRImapFeatureOn; }
    //[FEATURE]-ADD-END by TSNJ.wei huang
}
