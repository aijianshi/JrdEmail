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
 */

/*
 ==========================================================================
 *HISTORY
 *
 *Tag            Date              Author        Description
 *============== ============ =============== ==============================
 *BUGFIX-912393  2015/1/27    junwei-xu       [Email]Error loading the web page when view in calendar open wih browser
 *BUGFIX-1054379 2015/7/29    Gantao          [TMO][38283][Email]Can not edit respond before sending when accept Meeting reques
 *BUGFIX-537843  2015/09/01   junwei-xu       Message invite ui update.
 *BUGFIX-1080620 2015/09/12   tao.gan         [Email]The screen always display white after rotate phone
 *BUGFIX_1000793 2015/12/1    yanhua.chen     [Email][Force close]When click calendar in meeting invitation,it will happen force close.
 *BUGFIX-1739423 2016/03/07   junwei-xu       [onetouch feedback][VIP][com.google.android.calendar][Version  5.3.5-114092827-release][Other]
 *===========================================================================
 */
package com.tct.mail.browse;

import android.Manifest;
import android.app.AlertDialog;
import android.content.AsyncQueryHandler;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;

import android.widget.TextView;
import com.tct.email.R;
import com.tct.email.activity.EventViewer;
import com.tct.emailcommon.mail.MeetingInfo;
import com.tct.emailcommon.mail.PackedString;
import com.tct.emailcommon.provider.EmailContent;
import com.tct.emailcommon.utility.Utility;
import com.tct.mail.ui.ControllableActivity;
import com.tct.mail.utils.LogUtils;
import com.tct.mail.providers.Message;
import com.tct.mail.providers.UIProvider;
import com.tct.mail.utils.Utils;
import com.tct.permission.BaseActivity;
import com.tct.permission.PermissionUtil;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

public class MessageInviteView extends LinearLayout implements View.OnClickListener , PermissionUtil.OnPermissionResult{ //TS: jin.dong 2016-01-11 EMAIL BUGFIX-1376565 MOD

    private Message mMessage;
    private final Context mContext;
    private InviteCommandHandler mCommandHandler = new InviteCommandHandler();
    // TS: junwei-xu 2015-09-01 EMAIL BUGFIX_537843 ADD_S
    private TextView mMeetingTitle;
    private TextView mMeetingTime;
    private TextView mMeetingLocation;
    // TS: junwei-xu 2015-09-01 EMAIL BUGFIX_537843 ADD_E

    public MessageInviteView(Context c) {
        this(c, null);
    }

    public MessageInviteView(Context c, AttributeSet attrs) {
        super(c, attrs);
        mContext = c;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        findViewById(R.id.invite_calendar_view).setOnClickListener(this);
        findViewById(R.id.accept).setOnClickListener(this);
        findViewById(R.id.tentative).setOnClickListener(this);
        findViewById(R.id.decline).setOnClickListener(this);
        // TS: junwei-xu 2015-09-01 EMAIL BUGFIX_537843 ADD_S
        mMeetingTitle = (TextView) findViewById(R.id.invite_title);
        mMeetingTime = (TextView) findViewById(R.id.invite_datetime);
        mMeetingLocation = (TextView) findViewById(R.id.invite_location);
        // TS: junwei-xu 2015-09-01 EMAIL BUGFIX_537843 ADD_E
    }

    public void bind(Message message) {
        mMessage = message;
        // TS: junwei-xu 2015-09-01 EMAIL BUGFIX_537843 ADD_S
        renderInviteView();
        // TS: junwei-xu 2015-09-01 EMAIL BUGFIX_537843 ADD_E
    }

    // TS: junwei-xu 2015-09-01 EMAIL BUGFIX_537843 ADD_S
    private void renderInviteView() {
        if (mMessage == null) {
            return;
        }

        long messageId = mMessage.id;
        EmailContent.Message inviteMessage = EmailContent.Message.restoreMessageWithId(mContext, messageId);
        if (inviteMessage == null) {
            return;
        }

        PackedString info = new PackedString(inviteMessage.mMeetingInfo);
        String location = info.get(MeetingInfo.MEETING_LOCATION);
        String title = info.get(MeetingInfo.MEETING_TITLE);
        String startTime = info.get(MeetingInfo.MEETING_DTSTART);
        String endTime = info.get(MeetingInfo.MEETING_DTEND);
        mMeetingTitle.setText(title);
        mMeetingTime.setText(formatDateTime(startTime)+"-"+formatDateTime(endTime));
        mMeetingLocation.setText(location);
    }

    private String formatDateTime(String datetime) {
        //SimpleDateFormat format = new SimpleDateFormat("dd MMM HH:mm");
        try {
            long startTime = Utility.parseEmailDateTimeToMillis(datetime);
            String dateTimeString = DateFormat.getDateTimeInstance().format(new Date(startTime));
            return dateTimeString;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
    // TS: junwei-xu 2015-09-01 EMAIL BUGFIX_537843 ADD_E

 // TS: zhaotianyong 2015-02-04 EXCHANGE BUGFIX-923513 ADD_S
  //NOTE:we use AlertDialog for user to input the comment,and the send the comment.
    private AlertDialog InviteResponseDialog(final Integer command) {
        View view = LayoutInflater.from(mContext).inflate(
                R.layout.message_invite_response_view, null);
        final EditText response = (EditText) view.findViewById(R.id.response_content);
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        int title;
        switch (command) {
            case UIProvider.MessageOperations.RESPOND_ACCEPT:
                title = R.string.message_invite_accent_title;
                break;
            case UIProvider.MessageOperations.RESPOND_TENTATIVE:
                title = R.string.message_invite_tentative_title;
                break;
            case UIProvider.MessageOperations.RESPOND_DECLINE:
                title = R.string.message_invite_decline_title;
                break;
            default:
                title = R.string.message_invite_title;
        }
        builder.setTitle(title);
        builder.setPositiveButton(R.string.send, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                String content = response.getText().toString();
                if (command != null) {
                    ContentValues params = new ContentValues();
                    LogUtils.w("UnifiedEmail", "SENDING INVITE COMMAND, VALUE=%s", command);
                    params.put(UIProvider.MessageOperations.RESPOND_CONTENT, content);
                    params.put(UIProvider.MessageOperations.RESPOND_COLUMN, command);
                    mCommandHandler.sendCommand(params);
                  //TS: tao.gan 2015-09-12 EMAIL BUGFIX-1080620 ADD_S
                    //Here we exit the conversationviewFragment,back to listFragment.
                    if (mContext instanceof ControllableActivity) {
                        ControllableActivity cActivity = (ControllableActivity) mContext;
                        cActivity.backToList(null);
                    }
                  //TS: tao.gan 2015-09-12 EMAIL BUGFIX-1080620 ADD_E
                }
            }
        });

        builder.setView(view);
        AlertDialog dialog = builder.create();
        // TS: Gantao 2015-08-20 EMAIL BUGFIX-471376 ADD_S
        //Only portrait orientation we pop up soft input.
        if (dialog.getWindow() != null
                && mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            dialog.getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }
        // TS: Gantao 2015-08-20 EMAIL BUGFIX-471376 ADD_E
        return dialog;
    }
 // TS: zhaotianyong 2015-02-04 EXCHANGE BUGFIX-923513 ADD_E

    @Override
    public void onClick(View v) {
        Integer command = null;

        final int id = v.getId();

        if (id == R.id.invite_calendar_view) {
            //TS: jin.dong 2016-01-11 EMAIL BUGFIX-1376565 MOD_S
            //TS: yanhua.chen 2015-12-1 EMAIL BUGFIX_1000793 MOD_S
            if (PermissionUtil.checkAndRequestCalendarPermission(mContext)){
                viewInCalendar();
            } else {
                if (mContext instanceof BaseActivity){
                    ((BaseActivity) mContext).registerPermissionResultListener(this);
                }
            }
            //TS: jin.dong 2016-01-11 EMAIL BUGFIX-1376565 MOD_E
            //TS: yanhua.chen 2015-12-1 EMAIL BUGFIX_1000793 MOD_E
        } else if (id == R.id.accept) {
            command = UIProvider.MessageOperations.RESPOND_ACCEPT;
            // TS: Gantao 2015-07-29 EXCHANGE BUGFIX-1054379 MOD_S
            //NOTE:For accept/tentative/decline response,we popup dialog for user to input commend.
            AlertDialog dialog = InviteResponseDialog(command);
            dialog.show();
            return;
        } else if (id == R.id.tentative) {
         // TS: zhaotianyong 2015-02-04 EXCHANGE BUGFIX-923513 MOD_S
            command = UIProvider.MessageOperations.RESPOND_TENTATIVE;
            AlertDialog dialog = InviteResponseDialog(command);
            dialog.show();
            return;
        } else if (id == R.id.decline) {
            command = UIProvider.MessageOperations.RESPOND_DECLINE;
            AlertDialog dialog = InviteResponseDialog(command);
            dialog.show();
            return;
         // TS: zhaotianyong 2015-02-04 EXCHANGE BUGFIX-923513 MOD_E
        }
        // TS: Gantao 2015-07-29 EXCHANGE BUGFIX-1054379 MOD_E

    }

    //TS: jin.dong 2016-01-11 EMAIL BUGFIX-1376565 ADD_S
    @Override
    public void onPermissionResult(int requestCode, String permission, int result) {   //TS: zheng.zou 2016-1-22 EMAIL BUGFIX-1431088 ADD
        if (Manifest.permission.READ_CALENDAR.equals(permission) &&
                result == PackageManager.PERMISSION_GRANTED){

            viewInCalendar();

            if (mContext instanceof BaseActivity){
                ((BaseActivity) mContext).unRegisterPermissionResultListener(this);
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mContext instanceof BaseActivity){
            ((BaseActivity) mContext).unRegisterPermissionResultListener(this);
        }
    }

    private void viewInCalendar(){
        if (!Utils.isEmpty(mMessage.eventIntentUri)) {
            //TS: junwei-xu 2016-03-07 EMAIL BUGFIX-1739423 MOD_S
            Intent intent = new Intent(mContext, EventViewer.class);
            intent.setAction(Intent.ACTION_VIEW);
            //TS: junwei-xu 2015-1-27 EMAIL BUGFIX_912393 ADD_S
            intent.setPackage("com.tct.email");
            //TS: junwei-xu 2015-1-27 EMAIL BUGFIX_912393 ADD_E
            intent.setData(mMessage.eventIntentUri);
            //TS: junwei-xu 2016-03-07 EMAIL BUGFIX-1739423 MOD_E
            mContext.startActivity(intent);
        }
    }
    //TS: jin.dong 2016-01-11 EMAIL BUGFIX-1376565 ADD_E

    private class InviteCommandHandler extends AsyncQueryHandler {

        public InviteCommandHandler() {
            super(getContext().getContentResolver());
        }

        public void sendCommand(ContentValues params) {
            startUpdate(0, null, mMessage.uri, params, null, null);
        }

    }
}
