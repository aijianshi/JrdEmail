/*
 * Copyright (C) 2012 Google Inc.
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
 *==========================================================================
 *HISTORY
 *
 *Tag            Date              Author        Description
 *============== ============ =============== ==============================
 *BUGFIX-859985  2014/12/18   junwei-xu       [Android5.0][Email][UI]The Email UI display abnormal after tapping Star icon
 *BUGFIX-882004  2014/12/24   xiaolin.li      [Android5.0][Email][Crash]Email crash when opening an unread mail in combined view.
 *BUGFIX-887553  2014/12/30   xiaolin.li      [Email]Quick horizontal sliding flash back in the mail details interface
 *BUGFIX-882241  2015/01/03   wenggangjin     [Android5.0][Email][REG]Embedded picture covers mail content after rotating screen
 *BUGFIX_915771  2015-02-02   gengkexue       [Android5.0][Email]The save icon of attachments will move in combined view
 *BUGFIX-921154  2015-02-06   wenggangjin     [Android5.0][Email]The star icon in Trash folder is not reasonable
 *BUGFIX-946809  2015-03-27   zhaotianyong    [Monitor][Email]Attachemnt icon has overlap with email body when the email has long body and long subject contents
 *BUGFIX-978961  2015-04-21   jian.xu         [Email][Widget]Can not starred email when open mail from combined view unread widget
 *BUGFIX-1032392 2015/7/8   yanhua.chen       [Email]Flash star icon when tap a mail on trash list screen
 *BUGFIX-526255  2015-09-01   zheng.zou       CR:swipe to delete or star unstar email
 *===========================================================================
 */

package com.tct.mail.browse;

import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.tct.email.R;
import com.tct.mail.providers.Message;
import com.tct.mail.utils.LogTag;
import com.tct.mail.utils.LogUtils;
import com.tct.mail.browse.ConversationViewAdapter.ConversationHeaderItem;
import com.tct.mail.providers.Conversation;
import com.tct.mail.providers.Folder;
import com.tct.mail.providers.UIProvider;
import com.tct.mail.ui.ConversationUpdater;
import com.tct.mail.utils.Utils;

/**
 * A view for the subject and folders in the conversation view. This container
 * makes an attempt to combine subject and folders on the same horizontal line if
 * there is enough room to fit both without wrapping. If they overlap, it
 * adjusts the layout to position the folders below the subject.
 */
public class ConversationViewHeader extends LinearLayout implements OnClickListener {

    public interface ConversationViewHeaderCallbacks {
        /**
         * Called in response to a click on the folders region.
         */
        void onFoldersClicked();

        /**
         * Called when the height of the {@link ConversationViewHeader} changes.
         *
         * @param newHeight the new height in px
         */
        void onConversationViewHeaderHeightChange(int newHeight);
        // AM: Kexue.Geng 2015-02-02 EMAIL BUGFIX_915771 MOD_S
        void setStarred(boolean star);
        // AM: Kexue.Geng 2015-02-02 EMAIL BUGFIX_915771 MOD_E
    }

    private static final String LOG_TAG = LogTag.getLogTag();
    private SubjectAndFolderView mSubjectAndFolderView;
    //TS: junwei-xu 2015-09-02 EMAIL BUGFIX-546917 MOD-S
    //private StarView mStarView;
    private ImageView mPriorityIcon;
    //TS: junwei-xu 2015-09-02 EMAIL BUGFIX-546917 MOD-E
    private ConversationViewHeaderCallbacks mCallbacks;
    private ConversationAccountController mAccountController;
    private ConversationUpdater mConversationUpdater;
    private ConversationHeaderItem mHeaderItem;
    private Conversation mConversation;
    // TS: xiaolin.li 2014-12-30 EMAIL BUGFIX-887553 MOD_S
    public static boolean isClickStar = false;
    // TS: xiaolin.li 2014-12-30 EMAIL BUGFIX-887553 MOD_E

    /**
     * Instantiated from this layout: conversation_view_header.xml
     * @param context
     */
    public ConversationViewHeader(Context context) {
        this(context, null);
    }

    public ConversationViewHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mSubjectAndFolderView =
                (SubjectAndFolderView) findViewById(R.id.subject_and_folder_view);
        //TS: junwei-xu 2015-09-02 EMAIL BUGFIX-546917 DEL-S
        mPriorityIcon = (ImageView) findViewById(R.id.conversation_priority_icon);
        mPriorityIcon.setImageLevel(Message.FLAG_PRIORITY_NORMAL);
        /*
        mStarView = (StarView) findViewById(R.id.conversation_header_star);
        mStarView.setOnClickListener(this);
        */
        //TS: junwei-xu 2015-09-02 EMAIL BUGFIX-546917 DEL-E
    }

    public void setCallbacks(ConversationViewHeaderCallbacks callbacks,
            ConversationAccountController accountController,
            ConversationUpdater conversationUpdater) {
        mCallbacks = callbacks;
        mAccountController = accountController;
        mConversationUpdater = conversationUpdater;
    }

    public void setSubject(final String subject) {
        mSubjectAndFolderView.setSubject(subject);
    }

    public void setFolders(Conversation conv) {
    // TS: jian.xu 2015-04-21 EMAIL BUGFIX-978961 MOD_s
        if(mConversation == null) {
             mConversation = conv;
        }
    // TS: jian.xu 2015-04-21 EMAIL BUGFIX-978961 MOD_E
        mSubjectAndFolderView.setFolders(mCallbacks, mAccountController.getAccount(), conv);
    }

    //TS: junwei-xu 2015-09-02 EMAIL BUGFIX-546917 ADD-S
    public void setConversationPriority(int priority) {
        if (mPriorityIcon == null) {
            return;
        }
        switch (priority) {
            case Message.FLAG_PRIORITY_HIGH:
                mPriorityIcon.setImageLevel(Message.FLAG_PRIORITY_HIGH);
                break;
            case Message.FLAG_PRIORITY_LOW:
                mPriorityIcon.setImageLevel(Message.FLAG_PRIORITY_LOW);
                break;
            case Message.FLAG_PRIORITY_NORMAL:
                mPriorityIcon.setImageLevel(Message.FLAG_PRIORITY_NORMAL);
                break;
        }
    }
    //TS: junwei-xu 2015-09-02 EMAIL BUGFIX-546917 ADD-E

    //TS: junwei-xu 2015-09-02 EMAIL BUGFIX-546917 DEL-S
    /*
    public void setStarred(boolean isStarred) {
        //TS: yanhua.chen 2015-7-8 EMAIL BUGFIX_1032392 MOD_S
        //Note Invisible the starView when trash folder
        if(isTrashFolder()){
            mStarView.setVisibility(View.INVISIBLE);
        }else{
            mStarView.setStarred(isStarred);
            mStarView.setVisibility(View.VISIBLE);
        }
        //TS: yanhua.chen 2015-7-8 EMAIL BUGFIX_1032392 MOD_E
    }
    */
    //TS: junwei-xu 2015-09-02 EMAIL BUGFIX-546917 DEL-E

    public void bind(ConversationHeaderItem headerItem) {
        mHeaderItem = headerItem;
      //TS: wenggangjin 2015-01-03 EMAIL BUGFIX_882241 MOD_S
        if(mConversation != null && mHeaderItem.mConversation.starred != mConversation.starred){
            mHeaderItem.mConversation.starred = mConversation.starred;
        }
        if(mConversation != null)
        mConversation = mHeaderItem.mConversation;
      //TS: wenggangjin 2015-01-03 EMAIL BUGFIX_882241 MOD_E
        if (mSubjectAndFolderView != null) {
            mSubjectAndFolderView.bind(headerItem);
        }
        //TS: junwei-xu 2015-09-02 EMAIL BUGFIX-546917 MOD-S
        setConversationPriority(mConversation.flagPriority);
        /*
        //TS: yanhua.chen 2015-7-8 EMAIL BUGFIX_1032392 MOD_S
        //Note Invisible the starView when trash folder
        mStarView.setVisibility(mConversation != null && !isTrashFolder() ? View.VISIBLE : View.INVISIBLE);
        //TS: yanhua.chen 2015-7-8 EMAIL BUGFIX_1032392 MOD_E
        */
        //TS: junwei-xu 2015-09-02 EMAIL BUGFIX-546917 MOD-E
    }

    private int measureHeight() {
        ViewGroup parent = (ViewGroup) getParent();
        if (parent == null) {
            LogUtils.e(LOG_TAG, "Unable to measure height of conversation header");
            return getHeight();
        }
        final int h = Utils.measureViewHeight(this, parent);
        return h;
    }

    //TS: yanhua.chen 2015-7-8 EMAIL BUGFIX_1032392 ADD_S
    /**
     * Judge the currentConversation whether in the trash folder
     */
    private boolean isTrashFolder(){
        boolean flag = false;
        int type = -1;
        if(mConversation == null){
            return false;
        }
        List<Folder> list = mConversation.getRawFolders();
        if(list.size() != 1){
            return false;
        }
        for(Folder f : list){
            if(f == null ){
                return false;
            }
            type = f.type;
        }
        if(type != -1 && type == UIProvider.FolderType.TRASH){
            flag = true;
        }
        return flag;
    }
    //TS: yanhua.chen 2015-7-8 EMAIL BUGFIX_1032392 ADD_E

    /**
     * Update the conversation view header to reflect the updated conversation.
     */
    public void onConversationUpdated(Conversation conv) {
        // The only things we have to worry about when the conversation changes
        // in the conversation header are the folders, priority indicators, and starred state.
        // Updating these will resize the space for the header.
        //[BUGFIX]-Add-BEGIN by TSNJ Zhenhua.Fan,21/11/2014,PR 871926 + JrdApp PR 859985
        // TS: xiaolin.li 2014-12- EMAIL BUGFIX-882004 MOD_S
        //TS: zheng.zou 2015-09-01 EMAIL BUGFIX_526255 DEL_S
//        if( mConversation != null ){
//                if(conv.starred != mConversation.starred)
//                {
//                    conv.starred = mConversation.starred;
//                }
//        }
        //TS: zheng.zou 2015-09-01 EMAIL BUGFIX_526255 DEL_E
        // TS: xiaolin.li 2014-12- EMAIL BUGFIX-882004 MOD_E
        //[BUGFIX]-Add-END by TSNJ Zhenhua.Fan
        mConversation = conv;
        setSubject(conv.subject);
        setFolders(conv);
        //TS: junwei-xu 2015-09-02 EMAIL BUGFIX-546917 MOD-S
        setConversationPriority(conv.flagPriority);
        //setStarred(conv.starred);
        //TS: junwei-xu 2015-09-02 EMAIL BUGFIX-546917 MOD-E
        if (mHeaderItem != null) {
            final int h = measureHeight();
         // TS: zhaotianyong 2015-03-27 EMAIL BUGFIX-946809 MOD_S
//            if (mHeaderItem.setHeight(h)) {
            mHeaderItem.setHeight(h);
            mCallbacks.onConversationViewHeaderHeightChange(h);
//            }
         // TS: zhaotianyong 2015-03-27 EMAIL BUGFIX-946809 MOD_E
        }
    }

    @Override
    public void onClick(View v) {
        //TS: junwei-xu 2015-09-02 EMAIL BUGFIX-546917 DEL-S
        /*
        final int id = v.getId();
        if (mConversation != null && id == R.id.conversation_header_star) {
            mConversation.starred = !mConversation.starred;
            setStarred(mConversation.starred);
            // AM: Kexue.Geng 2015-02-02 EMAIL BUGFIX_915771 MOD_S
            if(mCallbacks != null) {
                mCallbacks.setStarred(mConversation.starred);
            }
            // AM: Kexue.Geng 2015-02-02 EMAIL BUGFIX_915771 MOD_E
            //[BUGFIX]-DEL-BEGIN by TSNJ Zhenhua.Fan,21/11/2014,PR 871926 + JrdApp PR 859985
            //mConversationUpdater.updateConversation(Conversation.listOf(mConversation),
            //        UIProvider.ConversationColumns.STARRED, mConversation.starred);
            //[BUGFIX]-DEL-EDN by TSNJ Zhenhua.Fan
         // TS: xiaolin.li 2014-12-30 EMAIL BUGFIX-887553 MOD_S
            isClickStar = true;
         // TS: xiaolin.li 2014-12-30 EMAIL BUGFIX-887553 MOD_E
        }
        */
        //TS: junwei-xu 2015-09-02 EMAIL BUGFIX-546917 DEL-E
    }
  //TS: yanhua.chen 2015-7-8 EMAIL BUGFIX_1032392 DEL_S
  //TS: wenggangjin 2015-02-03 EMAIL BUGFIX_-921154 ADD_S
    /*public void setStarTrash() {
        mStarView.setVisibility(View.GONE);
    }*/
  //TS: wenggangjin 2015-02-03 EMAIL BUGFIX_-921154 ADD_E
  //TS: yanhua.chen 2015-7-8 EMAIL BUGFIX_1032392 DEL_E
}
