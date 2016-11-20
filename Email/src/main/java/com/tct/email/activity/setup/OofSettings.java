/******************************************************************************/
/*                                                               Date:08/2013 */
/*                                PRESENTATION                                */
/*                                                                            */
/*       Copyright 2013 TCL Communication Technology Holdings Limited.        */
/*                                                                            */
/* This material is company confidential, cannot be reproduced in any form    */
/* without the written permission of TCL Communication Technology Holdings    */
/* Limited.                                                                   */
/*                                                                            */
/* -------------------------------------------------------------------------- */
/*  Author :  chen caixia                                                     */
/*  Email  :  caixia.chen@tcl-mobile.com                                      */
/*  Role   :                                                                  */
/*  Reference documents :                                                     */
/* -------------------------------------------------------------------------- */
/*  Comments :                                                                */
/*  File     : src/com/android/email/activity/setup/OofSettings.java          */
/*  Labels   :                                                                */
/* -------------------------------------------------------------------------- */
/* ========================================================================== */
/*     Modifications on Features list / Changes Request / Problems Report     */
/* -------------------------------------------------------------------------- */
/*    date   |        author        |         Key          |     comment      */
/* ----------|----------------------|----------------------|----------------- */
/* 04/15/2014|     zhonghua.tuo     |   FR 635145(porting  |Email] OUT OF OF- */
/*           |                      |    from 476662)      |FICE setting      */
/* ----------|----------------------|----------------------|----------------- */
/* 04/28/2014|     zhonghua.tuo     |      CR 631895       |[Russia][Exchange]*/
/*           |                      | (porting from 561809)|Out-of-office auto*/
/*           |                      |                      |-reply parameters */
/*           |                      |                      |should be localized*/
/* ----------|----------------------|----------------------|----------------- */
/* 07/18/2014|     zhonghua.tuo     |      PR 738479       |[HOMO][TanWan][Em */
/*           |                      |                      |ail] Should popup */
/*           |                      |                      |some warming msg  */
/* ----------|----------------------|----------------------|----------------- */
/******************************************************************************/
/*
 ==========================================================================
 *HISTORY
 *
 *Tag            Date         Author          Description
 *============== ============ =============== ==============================
 *BUGFIX-962591  2015/04/27   zhaotianyong    [Email]“Out of Office”function not work when rotated screen.
 *BUGFIX-988208  2015/04/28   zhaotianyong    [Email]“Send Out of Office auto-replies to external senders”function not work when exit the "Out of office settings".
 *BUGFIX-622657  2015/11/02   junwei-xu       [Android L][Email]Out of office sync can't load the server settings when it is disable
 *BUGFIX-1887874  2016/04/08  yang.mei        [Email]The End time cannot set successfully in out of office options.
 *BUGFIX-1938891  2016/04/15  lin-zhou        [Android M][Email]"End time" and "Start time" may be the same in Out of Office settings
 ===========================================================================
 */

package com.tct.email.activity.setup;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TimePicker;
import android.text.format.DateFormat;

import com.tct.email.R;
import com.tct.emailcommon.service.IEmailService;
import com.tct.email.service.EmailServiceUtils;
import com.tct.mail.utils.LogUtils;
import com.tct.mail.utils.PLFUtils;
import com.tct.mail.utils.Utils;

import android.os.RemoteException;
//[BUGFIX]-Add-BEGIN by TSCD.zhonghua.tuo,07/18/2014 for PR 738479
import android.widget.Toast;
//[BUGFIX]-Add-END by TSCD.zhonghua.tuo
//[BUGFIX]-ADD by SCDTABLET.shujing.jin@tcl.com,08/05/2016,2635083
import com.tct.emailcommon.utility.Utility;

public class OofSettings extends Activity implements View.OnTouchListener,
        CheckBox.OnCheckedChangeListener {

    public static final String COMMAND_FETCH_SETTINGS = "FetchSettings";
    public static final String COMMAND_TURNON_OOF = "TurnOnOof";
    public static final String COMMAND_TURNOFF_OOF = "TurnOffOof";

    private Context mContext;
    private Switch mAutoReplyCB;
    private CheckBox mInternalCB;
    private CheckBox mExternalCB;

    // TS: Gantao 2015-12-08 EMAIL BUGFIX-1044843 MOD_S
    //Don't use Time, just use long var to save the time.
//    private Time mStartTime;
//    private Time mEndTime;
    private long mStartTimeMillis;
    private long mEndTimeMillis;
    private static final String START_TIME_MILLIS = "startTimeMillis";
    private static final String END_TIME_MILLIS = "endTimeMillis";
    // TS: Gantao 2015-12-08 EMAIL BUGFIX-1044843 MOD_E

    private EditText mStartTimeText;
    private EditText mStartDateText;
    private EditText mEndTimeText;
    private EditText mEndDateText;
    private EditText mInternalMessage;
    private EditText mExternalMessage;

    private RadioGroup mRadioGroup;
    private RadioButton mRadioContacts;
    private RadioButton mRadioOrg;

    private long mAccountId;
    private Bundle mContent;

    // TS: zhaotianyong 2015-04-27 EMAIL BUGFIX-962591 ADD_S
    private boolean mAutoReplyCBChecked = false;
    private boolean mInternalCBChecked = false;
    private boolean mExternalCBChecked = false;
    private boolean mSettingFetched = false;
    private static final String STATE_KEY_AUTO_REPLAY_CB = "OofSettings.autoReplayCB";
    private static final String STATE_KEY_INTERNAL_CB = "OofSettings.internalCB";
    private static final String STATE_KEY_EXTERNAL_CB = "OofSettings.externalCB";
    private static final String STATE_KEY_OOF_SETTING_FETCHED = "OofSettings.fetched";
    // TS: zhaotianyong 2015-04-27 EMAIL BUGFIX-962591 ADD_E

    // TS: Gantao 2016-1-06 EMAIL BUGFIX-1246418 ADD_S
    private boolean mStartDateClicked = false;
    private boolean mStartTimeClicked = false;
    private boolean mEndDateClicked = false;
    private boolean mEndTimeClicked = false;
    private static final String STATE_KEY_START_DATE_CLICKED = "startDateClicked";
    private static final String STATE_KEY_START_TIME_CLICKED = "startTimeClicked";
    private static final String STATE_KEY_END_DATE_CLICKED = "endDateClicked";
    private static final String STATE_KEY_END_TIME_CLICKED = "endTImeClicked";
    // TS: Gantao 2016-1-06 EMAIL BUGFIX-1246418 ADD_E

    // TS: zhaotianyong 2015-04-27 EMAIL BUGFIX-988208 ADD_S
    private ProgressDialog mOofDialog;
    // TS: zhaotianyong 2015-04-27 EMAIL BUGFIX-988208 ADD_E


    //[FEATURE]-Add-BEGIN by CDTS.zhonghua.tuo, 2014/04/28, CR-631895
    private boolean mEnabledLocalizedDateTime = false;
    private SimpleDateFormat mRussiaDateFormat = new SimpleDateFormat("dd MMM yyyy, E");
    private SimpleDateFormat mRussiaTimeFormat = new SimpleDateFormat("HH:mm");
    //[FEATURE]-Add-END by CDTS.zhonghua.tuo

    class OofSyncTask extends AsyncTask<Bundle, Integer, Bundle> {
        private String mCommand;

        public OofSyncTask(String command) {
            mCommand = command;
        }

        @Override
        protected Bundle doInBackground(Bundle... arg0) {
            return syncOof(mAccountId,mCommand, mContent);
        }

        @Override
        protected void onPostExecute(Bundle result) {
            // TS: zhaotianyong 2015-04-27 EMAIL BUGFIX-988208 MOD_S
            dismissOofDialog();
            if (mCommand == COMMAND_FETCH_SETTINGS) {
                mContent = result;
                updateViewFromContent();
            } else {
                if(result==null){
                    showsyncMessage(R.string.out_of_office_fail);
                }else{
                    showsyncMessage(R.string.out_of_office_success);
                    finish();
                }
            }
            // TS: zhaotianyong 2015-04-27 EMAIL BUGFIX-988208 MOD_E
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.oof_settings);
        mContext = this;
        mAccountId = getIntent().getLongExtra("account_id", -1);
        //[FEATURE]-Add-BEGIN by CDTS.zhonghua.tuo, 2014/04/28, CR-631895
        // TS: xiaolin.li 2014-11-25 EMAIL READ_PLF MOD_S
        //if (this.getResources().getBoolean(R.bool.feature_email_localizedDateTime_on)) {
        if (PLFUtils.getBoolean(mContext, "feature_email_localizedDateTime_on")) {
        // TS: xiaolin.li 2014-11-25 EMAIL READ_PLF MOD_E
            mEnabledLocalizedDateTime = true;
        }
        //[FEATURE]-Add-END by CDTS.zhonghua.tuo

        // TS: Gantao 2015-12-08 EMAIL BUGFIX-1044843 MOD_S
//        mStartTime = new Time();
//        mEndTime = new Time();
//        mStartTime.setToNow();
//        mEndTime.setToNow();
        mStartTimeMillis = System.currentTimeMillis();
        mEndTimeMillis = System.currentTimeMillis();
        //TS: Gantao 2015-12-08 EMAIL BUGFIX-1044843 MOD_E

        // TS: zhaotianyong 2015-04-27 EMAIL BUGFIX-962591 MOD_S
        if (savedInstanceState != null) {
            mAutoReplyCBChecked = savedInstanceState.getBoolean(STATE_KEY_AUTO_REPLAY_CB,false);
            mInternalCBChecked = savedInstanceState.getBoolean(STATE_KEY_INTERNAL_CB, false);
            mExternalCBChecked = savedInstanceState.getBoolean(STATE_KEY_EXTERNAL_CB, false);
            mSettingFetched = savedInstanceState.getBoolean(STATE_KEY_OOF_SETTING_FETCHED, false);
            // TS: Gantao 2016-1-06 EMAIL BUGFIX-1246418 ADD_S
            mStartTimeClicked = savedInstanceState.getBoolean(STATE_KEY_START_TIME_CLICKED, false);
            mStartDateClicked = savedInstanceState.getBoolean(STATE_KEY_START_DATE_CLICKED, false);
            mEndTimeClicked = savedInstanceState.getBoolean(STATE_KEY_END_TIME_CLICKED, false);
            mEndDateClicked = savedInstanceState.getBoolean(STATE_KEY_END_DATE_CLICKED, false);
            mStartTimeMillis = savedInstanceState.getLong(START_TIME_MILLIS, System.currentTimeMillis());
            mEndTimeMillis = savedInstanceState.getLong(END_TIME_MILLIS, System.currentTimeMillis());
            // TS: Gantao 2016-1-06 EMAIL BUGFIX-1246418 ADD_E
        }
        initViews();
        getActionBar().setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP,
                ActionBar.DISPLAY_HOME_AS_UP);
        if (!mSettingFetched) {
            mContent = new Bundle();
            getUserOofSettings();
        }
        // TS: zhaotianyong 2015-04-27 EMAIL BUGFIX-962591 MOD_E
    }

    private void initViews() {
        mAutoReplyCB = (Switch) findViewById(R.id.auto_reply);
        mInternalCB = (CheckBox) findViewById(R.id.auto_replies_period);
        mExternalCB = (CheckBox) findViewById(R.id.auto_replies_external);
        // TS: zhaotianyong 2015-04-27 EMAIL BUGFIX-962591 ADD_S
        if (mAutoReplyCBChecked) {
            mAutoReplyCB.setChecked(true);
        }
        if (mInternalCBChecked) {
            mInternalCB.setChecked(true);
        }
        if (mExternalCBChecked) {
            mExternalCB.setChecked(true);
        }
        // TS: zhaotianyong 2015-04-27 EMAIL BUGFIX-962591 ADD_E

        mStartTimeText = (EditText) findViewById(R.id.start_time);
        mStartDateText = (EditText) findViewById(R.id.start_date);
        mEndTimeText = (EditText) findViewById(R.id.end_time);
        mEndDateText = (EditText) findViewById(R.id.end_date);
        mInternalMessage = (EditText) findViewById(R.id.internal_message);
        mExternalMessage = (EditText) findViewById(R.id.external_message);

        // TS: Gantao 2016-1-5 EMAIL BUGFIX-1271227 ADD_S
        //Set the hint for the EditText,we don't set it in xml because we want to custom the size of hint
        String internalHint = getResources().getString(R.string.hint_text_of_internal_message);
        String externalHint = getResources().getString(R.string.hint_text_of_external_message);
        Utils.setEditTextHint(mInternalMessage, internalHint, 13);
        Utils.setEditTextHint(mExternalMessage, externalHint, 13);
        // TS: Gantao 2016-1-5 EMAIL BUGFIX-1271227 ADD_E

        mRadioGroup = (RadioGroup) findViewById(R.id.oof_radio);
        mRadioContacts = (RadioButton) findViewById(R.id.oof_radio_contacts);
        mRadioOrg = (RadioButton) findViewById(R.id.oof_radio_organisation);

        mAutoReplyCB.setOnCheckedChangeListener(this);
        mInternalCB.setOnCheckedChangeListener(this);
        mExternalCB.setOnCheckedChangeListener(this);
        mStartTimeText.setOnTouchListener(this);
        mStartDateText.setOnTouchListener(this);
        mEndTimeText.setOnTouchListener(this);
        mEndDateText.setOnTouchListener(this);

        setInternalEnabled();
        setExternalEnabled();
        findViewById(R.id.internal_message_title).setEnabled(mAutoReplyCB.isChecked());
        mInternalMessage.setEnabled(mAutoReplyCB.isChecked());
        mInternalCB.setEnabled(mAutoReplyCB.isChecked());
        mExternalCB.setEnabled(mAutoReplyCB.isChecked());
        findViewById(R.id.auto_replies_period_title).setEnabled(
                mAutoReplyCB.isChecked());
        findViewById(R.id.auto_replies_external_title).setEnabled(
                mAutoReplyCB.isChecked());

        // TS: Gantao 2016-1-06 EMAIL BUGFIX-1246418 ADD_S
        //Judge show which one dialog.
        if(mStartTimeClicked) {
            onStartTimeClick();
        }
        if (mStartDateClicked) {
            onStartDateClick();
        }
        if(mEndTimeClicked) {
            onEndTimeClick();
        }
        if(mEndDateClicked) {
            onEndDateClick();
        }
        // TS: Gantao 2016-1-06 EMAIL BUGFIX-1246418 ADD_E
    }

    public void onStartTimeClick() {
        mStartTimeClicked = true;
        final Calendar calendar = Calendar.getInstance();
        // TS: Gantao 2015-12-08 EMAIL BUGFIX-1044843 MOD_S
//        calendar.setTimeInMillis(mStartTime.toMillis(true));
        calendar.setTimeInMillis(mStartTimeMillis);
        // TS: Gantao 2015-12-08 EMAIL BUGFIX-1044843 MOD_E
        Dialog dlg = new TimePickerDialog(mContext,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                            int minute) {
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);
                        long when = calendar.getTimeInMillis();
                        //[BUGFIX]-Add-BEGIN by TSCD.zhonghua.tuo,07/18/2014 for PR 738479
                        Calendar nowCalendar = Calendar.getInstance();
                        long now = nowCalendar.getTimeInMillis();
                        Calendar endCalendar = Calendar.getInstance();
                        // TS: Gantao 2015-12-08 EMAIL BUGFIX-1044843 MOD_S
//                        endCalendar.setTimeInMillis(mEndTime.toMillis(true));
                        endCalendar.setTimeInMillis(mEndTimeMillis);
                        // TS: Gantao 2015-12-08 EMAIL BUGFIX-1044843 MOD_E
                        long end = endCalendar.getTimeInMillis();
                        //TS:lin-zhou 2016-04-15 EMAIL BUGFIX-1938891 MOD_S
                        if(when > now && when >= end){
                            //TS:lin-zhou 2016-04-15 EMAIL BUGFIX-1938891 MOD_E
                            calendar.add(Calendar.HOUR_OF_DAY,1);
                            long endtime = calendar.getTimeInMillis();
                            // TS: Gantao 2015-12-08 EMAIL BUGFIX-1044843 MOD_S
//                            mEndTime.set(endtime);
                            mEndTimeMillis = endtime;
                            // TS: Gantao 2015-12-08 EMAIL BUGFIX-1044843 MOD_E
                            if (mEnabledLocalizedDateTime){
                                mEndDateText.setText(mRussiaDateFormat.format(calendar.getTime()));
                                mEndTimeText.setText(mRussiaTimeFormat.format(calendar.getTime()));
                            } else {
                                mEndDateText.setText(DateFormat.getDateFormat(mContext).format(endtime));
                                mEndTimeText.setText(DateFormat.getTimeFormat(mContext).format(endtime));
                            }
                            // TS: Gantao 2015-12-08 EMAIL BUGFIX-1044843 MOD_S
//                            mStartTime.set(when);
                            mStartTimeMillis = when;
                            // TS: Gantao 2015-12-08 EMAIL BUGFIX-1044843 MOD_E
                            calendar.add(Calendar.HOUR_OF_DAY,-1);
                            if (mEnabledLocalizedDateTime){
                                mStartTimeText.setText(mRussiaTimeFormat.format(calendar.getTime()));
                            } else {
                                mStartTimeText.setText(DateFormat.getTimeFormat(mContext).format(when));
                            }
                        } else{
                            // TS: Gantao 2015-12-08 EMAIL BUGFIX-1044843 MOD_S
//                            mStartTime.set(when);
                            mStartTimeMillis = when;
                            // TS: Gantao 2015-12-08 EMAIL BUGFIX-1044843 MOD_E
                            //[FEATURE]-Add-BEGIN by CDTS.zhonghua.tuo, 2014/04/28, CR-631895
                            if (mEnabledLocalizedDateTime){
                                mStartTimeText.setText(mRussiaTimeFormat.format(calendar.getTime()));
                            } else {
                                mStartTimeText.setText(DateFormat.getTimeFormat(mContext).format(when));
                            }
                            //[FEATURE]-Add-END by CDTS.zhonghua.tuo
                        }
                      //[BUGFIX]-Add-END by TSCD.zhonghua.tuo
                    }
                }, calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                DateFormat.is24HourFormat(mContext));
        // TS: Gantao 2016-1-06 EMAIL BUGFIX-1246418 ADD_S
        dlg.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mStartTimeClicked = false;
            }
        });
        // TS: Gantao 2016-1-06 EMAIL BUGFIX-1246418 ADD_E
        dlg.show();
    }

    public void onStartDateClick() {
        mStartDateClicked = true;
        final Calendar calendar = Calendar.getInstance();
        // TS: Gantao 2015-12-08 EMAIL BUGFIX-1044843 MOD_S
//        calendar.setTimeInMillis(mStartTime.toMillis(true));
        calendar.setTimeInMillis(mStartTimeMillis);
        // TS: Gantao 2015-12-08 EMAIL BUGFIX-1044843 MOD_E
        Dialog dlg = new DatePickerDialog(mContext,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                            int monthOfYear, int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, monthOfYear);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        long when = calendar.getTimeInMillis();
                        //[BUGFIX]-Add-BEGIN by TSCD.zhonghua.tuo,07/18/2014 for PR 738479
                        Calendar nowCalendar = Calendar.getInstance();
                        long now = nowCalendar.getTimeInMillis();
                        Calendar endCalendar = Calendar.getInstance();
                        // TS: Gantao 2015-12-08 EMAIL BUGFIX-1044843 MOD_S
//                        endCalendar.setTimeInMillis(mEndTime.toMillis(true));
                        endCalendar.setTimeInMillis(mEndTimeMillis);
                        // TS: Gantao 2015-12-08 EMAIL BUGFIX-1044843 MOD_E
                        long end = endCalendar.getTimeInMillis();
                        //TS:lin-zhou 2016-04-15 EMAIL BUGFIX-1938891 MOD_S
                        if(when > now && when >= end){
                            //TS:lin-zhou 2016-04-15 EMAIL BUGFIX-1938891 MOD_E
                            calendar.add(Calendar.HOUR_OF_DAY, 1);
                            long endtime = calendar.getTimeInMillis();
                            // TS: Gantao 2015-12-08 EMAIL BUGFIX-1044843 MOD_S
//                            mEndTime.set(endtime);
                            mEndTimeMillis = endtime;
                            // TS: Gantao 2015-12-08 EMAIL BUGFIX-1044843 MOD_E
                            if (mEnabledLocalizedDateTime){
                                mEndDateText.setText(mRussiaDateFormat.format(calendar.getTime()));
                                mEndTimeText.setText(mRussiaTimeFormat.format(calendar.getTime()));
                            } else {
                                mEndDateText.setText(DateFormat.getDateFormat(mContext).format(endtime));
                                mEndTimeText.setText(DateFormat.getTimeFormat(mContext).format(endtime));
                            }
                            // TS: Gantao 2015-12-08 EMAIL BUGFIX-1044843 MOD_S
//                            mStartTime.set(when);
                            mStartTimeMillis = when;
                            // TS: Gantao 2015-12-08 EMAIL BUGFIX-1044843 MOD_E
                            calendar.add(Calendar.HOUR_OF_DAY,-1);
                            if (mEnabledLocalizedDateTime){
                                mStartDateText.setText(mRussiaDateFormat.format(calendar.getTime()));
                            } else {
                                mStartDateText.setText(DateFormat.getDateFormat(mContext).format(when));
                            }
                        } else{
                            // TS: Gantao 2015-12-08 EMAIL BUGFIX-1044843 MOD_S
//	                        mStartTime.set(when);
                            mStartTimeMillis = when;
                            // TS: Gantao 2015-12-08 EMAIL BUGFIX-1044843 MOD_E
	                        //[FEATURE]-Add-BEGIN by CDTS.zhonghua.tuo, 2014/04/28, CR-631895
	                        if (mEnabledLocalizedDateTime){
	                            mStartDateText.setText(mRussiaDateFormat.format(calendar.getTime()));
	                        } else {
	                            mStartDateText.setText(DateFormat.getDateFormat(mContext).format(when));
	                        }
	                        //[FEATURE]-Add-END by CDTS.zhonghua.tuo
                        }
                        //[BUGFIX]-Add-END by TSCD.zhonghua.tuo
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        // TS: Gantao 2016-1-06 EMAIL BUGFIX-1246418 ADD_S
        dlg.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mStartDateClicked = false;
            }
        });
        // TS: Gantao 2016-1-06 EMAIL BUGFIX-1246418 ADD_E
        dlg.show();
    }

    public void onEndTimeClick() {
        mEndTimeClicked = true;
        final Calendar calendar = Calendar.getInstance();
        // TS: Gantao 2015-12-08 EMAIL BUGFIX-1044843 MOD_S
//        calendar.setTimeInMillis(mEndTime.toMillis(true));
        calendar.setTimeInMillis(mEndTimeMillis);
        // TS: Gantao 2015-12-08 EMAIL BUGFIX-1044843 MOD_E
        Dialog dlg = new TimePickerDialog(mContext,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                            int minute) {
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);
                        long when = calendar.getTimeInMillis();
                        //[BUGFIX]-Add-BEGIN by TSCD.zhonghua.tuo,07/18/2014 for PR 738479
                        long whenMinute = getMinute(calendar);
                        Calendar startCalendar = Calendar.getInstance();
                        long nowMinute = getMinute(startCalendar);
                        // TS: Gantao 2015-12-08 EMAIL BUGFIX-1044843 MOD_S
//                        startCalendar.setTimeInMillis(mStartTime.toMillis(true));
                        startCalendar.setTimeInMillis(mStartTimeMillis);
                        // TS: Gantao 2015-12-08 EMAIL BUGFIX-1044843 MOD_E
                        long startMinute = getMinute(startCalendar);
                        if(whenMinute > nowMinute){
                            // TS: Gantao 2015-12-08 EMAIL BUGFIX-1044843 MOD_S
//	                        mEndTime.set(when);
                            mEndTimeMillis = when;
                            // TS: Gantao 2015-12-08 EMAIL BUGFIX-1044843 MOD_E
	                        //[FEATURE]-Add-BEGIN by CDTS.zhonghua.tuo, 2014/04/28, CR-631895
	                        if (mEnabledLocalizedDateTime){
	                            mEndTimeText.setText(mRussiaTimeFormat.format(calendar.getTime()));
	                        } else {
	                            mEndTimeText.setText(DateFormat.getTimeFormat(mContext).format(when));
	                        }
	                        //[FEATURE]-Add-END by CDTS.zhonghua.tuo
	                        if(whenMinute <= startMinute){
	                            calendar.add(Calendar.HOUR_OF_DAY,-1);
	                            long startTime = calendar.getTimeInMillis();
                                // TS: Gantao 2015-12-08 EMAIL BUGFIX-1044843 MOD_S
//	                            mStartTime.set(startTime);
                                mStartTimeMillis = startTime;
                                // TS: Gantao 2015-12-08 EMAIL BUGFIX-1044843 MOD_E
	                            if (mEnabledLocalizedDateTime){
	                                mStartDateText.setText(mRussiaDateFormat.format(calendar.getTime()));
	                                mStartTimeText.setText(mRussiaTimeFormat.format(calendar.getTime()));
	                            } else {
	                                mStartDateText.setText(DateFormat.getDateFormat(mContext).format(startTime));
	                                mStartTimeText.setText(DateFormat.getTimeFormat(mContext).format(startTime));
	                            }
	                            calendar.add(Calendar.HOUR_OF_DAY,1);
	                        }
	                    } else {
	                        //[BUGFIX]-MOD by SCDTABLET.shujing.jin@tcl.com,08/05/2016,2635083
	                        Utility.showShortToast(OofSettings.this,R.string.account_oof_error_tip_past);
	                        //Toast.makeText(OofSettings.this,R.string.account_oof_error_tip_past,Toast.LENGTH_SHORT).show();
	                    }
	                    //[BUGFIX]-Add-END by TSCD.zhonghua.tuo
                    }
                }, calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                DateFormat.is24HourFormat(mContext));
        // TS: Gantao 2016-1-06 EMAIL BUGFIX-1246418 ADD_S
        dlg.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mEndTimeClicked = false;
            }
        });
        // TS: Gantao 2016-1-06 EMAIL BUGFIX-1246418 ADD_E
        dlg.show();
    }

    public void onEndDateClick() {
        mEndDateClicked = true;
        final Calendar calendar = Calendar.getInstance();
        // TS: Gantao 2015-12-08 EMAIL BUGFIX-1044843 MOD_S
//        calendar.setTimeInMillis(mEndTime.toMillis(true));
        calendar.setTimeInMillis(mEndTimeMillis);
        // TS: Gantao 2015-12-08 EMAIL BUGFIX-1044843 MOD_E
        Dialog dlg = new DatePickerDialog(
                mContext,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                            int monthOfYear, int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, monthOfYear);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        long when = calendar.getTimeInMillis();
                        //[BUGFIX]-Add-BEGIN by TSCD.zhonghua.tuo,07/18/2014 for PR 738479
                        long whenMinute = getMinute(calendar);
                        Calendar startCalendar = Calendar.getInstance();
                        long nowMinute = getMinute(startCalendar);
                        // TS: Gantao 2015-12-08 EMAIL BUGFIX-1044843 MOD_S
//                        startCalendar.setTimeInMillis(mStartTime.toMillis(true));
                        startCalendar.setTimeInMillis(mStartTimeMillis);
                        // TS: Gantao 2015-12-08 EMAIL BUGFIX-1044843 MOD_E
                        long startMinute = getMinute(startCalendar);
                        if(whenMinute > nowMinute){
                            // TS: Gantao 2015-12-08 EMAIL BUGFIX-1044843 MOD_S
//		                    mEndTime.set(when);
                            mEndTimeMillis = when;
                            // TS: Gantao 2015-12-08 EMAIL BUGFIX-1044843 MOD_E
		                    //[FEATURE]-Add-BEGIN by CDTS.zhonghua.tuo, 2014/04/28, CR-631895
		                    if (mEnabledLocalizedDateTime){
		                        mEndDateText.setText(mRussiaDateFormat.format(calendar.getTime()));
		                    } else {
		                        mEndDateText.setText(DateFormat.getDateFormat(mContext).format(when));
		                    }
		                    //[FEATURE]-Add-END by CDTS.zhonghua.tuo
                            if(whenMinute <= startMinute){
                                calendar.add(Calendar.HOUR_OF_DAY,-1);
                                long startTime = calendar.getTimeInMillis();
                                // TS: Gantao 2015-12-08 EMAIL BUGFIX-1044843 MOD_S
//                                mStartTime.set(startTime);
                                mStartTimeMillis = startTime;
                                // TS: Gantao 2015-12-08 EMAIL BUGFIX-1044843 MOD_E
                                if (mEnabledLocalizedDateTime){
                                    mStartDateText.setText(mRussiaDateFormat.format(calendar.getTime()));
                                    mStartTimeText.setText(mRussiaTimeFormat.format(calendar.getTime()));
                                } else {
                                    mStartDateText.setText(DateFormat.getDateFormat(mContext).format(startTime));
                                    mStartTimeText.setText(DateFormat.getTimeFormat(mContext).format(startTime));
                                }
                                calendar.add(Calendar.HOUR_OF_DAY,1);
                            }
                        } else {
                            //[BUGFIX]-MOD by SCDTABLET.shujing.jin@tcl.com,08/05/2016,2635083
                            Utility.showShortToast(OofSettings.this,R.string.account_oof_error_tip_past);
                            //Toast.makeText(OofSettings.this,R.string.account_oof_error_tip_past,Toast.LENGTH_SHORT).show();
                        }
                        //[BUGFIX]-Add-END by TSCD.zhonghua.tuo
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        // TS: Gantao 2016-1-06 EMAIL BUGFIX-1246418 ADD_S
        dlg.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mEndDateClicked = false;
            }
        });
        // TS: Gantao 2016-1-06 EMAIL BUGFIX-1246418 ADD_E
        dlg.show();
    }

    //[BUGFIX]-Add-BEGIN by TSCD.zhonghua.tuo,07/18/2014 for PR 738479
    // TS: yang.mei 2016-04-08 EMAIL BUGFIX-1887874 MOD_S
    private long getMinute(Calendar calendar){
        if(calendar != null){
//            int year = calendar.get(Calendar.YEAR);
//            int month = calendar.get(Calendar.MONTH);
//            int day = calendar.get(Calendar.DAY_OF_MONTH);
//            int hour = calendar.get(Calendar.HOUR_OF_DAY);
//            int minute = calendar.get(Calendar.MINUTE);
//            return year*518400+(month+1)*43200+day*1440+hour*60+minute;
            return calendar.getTimeInMillis()/(1000*60);
        }
        return 0;
    }
    // TS: yang.mei 2016-04-08 EMAIL BUGFIX-1887874 MOD_E
    private void setOofTime(){
        Calendar oofCalendar = Calendar.getInstance();
        long startTime = oofCalendar.getTimeInMillis();
//        mStartTime.set(startTime);
        if (mEnabledLocalizedDateTime){
            mStartDateText.setText(mRussiaDateFormat.format(oofCalendar.getTime()));
            mStartTimeText.setText(mRussiaTimeFormat.format(oofCalendar.getTime()));
        } else {
            mStartDateText.setText(DateFormat.getDateFormat(mContext).format(startTime));
            mStartTimeText.setText(DateFormat.getTimeFormat(mContext).format(startTime));
        }
        oofCalendar.add(Calendar.DAY_OF_MONTH, 1);
        long endTime = oofCalendar.getTimeInMillis();
//        mEndTime.set(endTime);
        if (mEnabledLocalizedDateTime){
            mEndDateText.setText(mRussiaDateFormat.format(oofCalendar.getTime()));
            mEndTimeText.setText(mRussiaTimeFormat.format(oofCalendar.getTime()));
        } else {
            mEndDateText.setText(DateFormat.getDateFormat(mContext).format(endTime));
            mEndTimeText.setText(DateFormat.getTimeFormat(mContext).format(endTime));
        }
    }

    private void judgeOrSetOofSettings(){
        Calendar startCalendar = Calendar.getInstance();
        // TS: Gantao 2015-12-08 EMAIL BUGFIX-1044843 MOD_S
//        startCalendar.setTimeInMillis(mStartTime.toMillis(true));
        startCalendar.setTimeInMillis(mStartTimeMillis);
        Calendar endCalendar = Calendar.getInstance();
//        endCalendar.setTimeInMillis(mEndTime.toMillis(true));
        endCalendar.setTimeInMillis(mEndTimeMillis);
        // TS: Gantao 2015-12-08 EMAIL BUGFIX-1044843 MOD_E
        long startTime = getMinute(startCalendar);
        long endTime = getMinute(endCalendar);
        if (mAutoReplyCB.isChecked() && mInternalCB.isChecked() && endTime <= startTime) {
			//[BUGFIX]-MOD by SCDTABLET.shujing.jin@tcl.com,08/05/2016,2635083
			Utility.showShortToast(OofSettings.this, R.string.account_oof_error_tip_earlier);
            //Toast.makeText(OofSettings.this, R.string.account_oof_error_tip_earlier, Toast.LENGTH_SHORT).show();
        } else {
            setUserOofSettings();
         // TS: zhaotianyong 2015-04-27 EMAIL BUGFIX-988208 DEL_S
//            finish();
         // TS: zhaotianyong 2015-04-27 EMAIL BUGFIX-988208 DEL_E
        }
    }
    //[BUGFIX]-Add-END by TSCD.zhonghua.tuo

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.account_settings_oof_option, menu);
        return true;
    }

    private void setInternalEnabled() {
        boolean enable = mAutoReplyCB.isChecked() & mInternalCB.isChecked();
        mStartTimeText.setEnabled(enable);
        mStartDateText.setEnabled(enable);
        mEndTimeText.setEnabled(enable);
        mEndDateText.setEnabled(enable);
        findViewById(R.id.start_time_title).setEnabled(enable);
        findViewById(R.id.end_time_title).setEnabled(enable);
    }

    private void setExternalEnabled() {
        boolean enable = mAutoReplyCB.isChecked() & mExternalCB.isChecked();
        mRadioGroup.setEnabled(enable);
        mRadioContacts.setEnabled(enable);
        mRadioOrg.setEnabled(enable);
        mExternalMessage.setEnabled(enable);
        findViewById(R.id.external_message_title).setEnabled(enable);
    }

    private void updateContentFromView() {
        if (mContent == null) {
            mContent = new Bundle();
        }
        int oofState = 1;
        if (mInternalCB.isChecked()) {
            oofState = 2;
        }
        mContent.putInt("OofState", oofState);
        // TS: Gantao 2015-12-08 EMAIL BUGFIX-1044843 MOD_S
//        String startTime = GetUTCTime(mStartTime.toMillis(true));
        String startTime = GetUTCTime(mStartTimeMillis);
        mContent.putString("StartTime", startTime);
//        String endTime = GetUTCTime(mEndTime.toMillis(true));
        String endTime = GetUTCTime(mEndTimeMillis);
        // TS: Gantao 2015-12-08 EMAIL BUGFIX-1044843 MOD_E
        mContent.putString("EndTime", endTime);
        mContent.putString("InternalMessage", mInternalMessage.getText()
                .toString());
        int externalState = 0;
        if (!mExternalCB.isChecked()) {
            externalState = 0;
        } else if (mRadioContacts.isChecked()) {
            externalState = 1;
        } else {
            externalState = 2;
        }
        mContent.putInt("ExternalState", externalState);
        mContent.putString("ExternalMessage", mExternalMessage.getText()
                .toString());
    }

    private void updateViewFromContent() {
        try {
            if (mContent == null) {
                // TS: zhaotianyong 2015-04-27 EMAIL BUGFIX-988208 ADD_S
                showsyncMessage(R.string.out_of_office_fail);
                // TS: zhaotianyong 2015-04-27 EMAIL BUGFIX-988208 ADD_E
                return;
            }
            int oofState = mContent.getInt("OofState");
            if (oofState == 0) {
                mAutoReplyCB.setChecked(false);
                //TS: junwei-xu 2015-11-02 EMAIL BUGFIX-622657 DEL_S
                //NOTE: Also need to load internal message when state is 0.
                //return;
                //TS: junwei-xu 2015-11-02 EMAIL BUGFIX-622657 DEL_E
            } else if (oofState == 1) {
                mAutoReplyCB.setChecked(true);
                mInternalCB.setChecked(false);
            } else {
                 mAutoReplyCB.setChecked(true);
                 mInternalCB.setChecked(true);
                //TS: junwei-xu 2015-11-02 EMAIL BUGFIX-622657 DEL_S
                //Note: date and time also need to be load for all state
                /*
                String startTime = mContent.getString("StartTime");
                long oofStartTime = String2UTC(startTime);
                mStartTime.set(oofStartTime);
                mStartTimeText.setText(DateFormat.getTimeFormat(this).format(oofStartTime));
                mStartDateText.setText(DateFormat.getDateFormat(this).format(oofStartTime));
                String endTime = mContent.getString("EndTime");
                long oofEndTime = String2UTC(endTime);
                mEndTime.set(oofEndTime);
                mEndTimeText.setText(DateFormat.getTimeFormat(this).format(oofEndTime));
                mEndDateText.setText(DateFormat.getDateFormat(this).format(oofEndTime));
                */
                //TS: junwei-xu 2015-11-02 EMAIL BUGFIX-622657 DEL_E
            }
            //TS: junwei-xu 2015-11-02 EMAIL BUGFIX-622657 ADD_S
            //Note: date and time also need to be load for all state
            String startTime = mContent.getString("StartTime");
            if(!TextUtils.isEmpty(startTime)) {
                long oofStartTime = String2UTC(startTime);
                // TS: Gantao 2015-12-08 EMAIL BUGFIX-1044843 MOD_S
//                mStartTime.set(oofStartTime);
                mStartTimeMillis = oofStartTime;
                mStartTimeText.setText(DateFormat.getTimeFormat(this).format(oofStartTime));
                mStartDateText.setText(DateFormat.getDateFormat(this).format(oofStartTime));
            }

            String endTime = mContent.getString("EndTime");
            if(!TextUtils.isEmpty(endTime)) {
                long oofEndTime = String2UTC(endTime);
//                mEndTime.set(oofEndTime);
                mEndTimeMillis = oofEndTime;
                // TS: Gantao 2015-12-08 EMAIL BUGFIX-1044843 MOD_E
                mEndTimeText.setText(DateFormat.getTimeFormat(this).format(oofEndTime));
                mEndDateText.setText(DateFormat.getDateFormat(this).format(oofEndTime));
            }

            //TS: junwei-xu 2015-11-02 EMAIL BUGFIX-622657 ADD_E
            String internalMessage = mContent.getString("InternalMessage");
            mInternalMessage.setText(internalMessage);
            int externalState = mContent.getInt("ExternalState");
            if (externalState == 0) {
                mExternalCB.setChecked(false);
            } else if (externalState == 1) {
                mExternalCB.setChecked(true);
                mRadioContacts.setChecked(true);
            } else {
                mExternalCB.setChecked(true);
                mRadioOrg.setChecked(true);
            }
            String externalMessage = mContent.getString("ExternalMessage");
            mExternalMessage.setText(externalMessage);
        } catch (Exception e) {
            //Log.e("oof", "OofParser Exception" + e.getMessage());
        }
    }

    public void onAutoRepleyClicked(boolean checked) {
        mInternalMessage.setEnabled(checked);
        findViewById(R.id.internal_message_title).setEnabled(checked);
        mInternalCB.setEnabled(checked);
        findViewById(R.id.auto_replies_period_title).setEnabled(checked);
        mExternalCB.setEnabled(checked);
        findViewById(R.id.auto_replies_external_title).setEnabled(checked);
        setInternalEnabled();
        setExternalEnabled();
    }

    private void getUserOofSettings() {
        //[BUGFIX]-Add-BEGIN by TSCD.zhonghua.tuo,07/18/2014 for PR 738479
        //setOofTime();
        //[BUGFIX]-Add-END by TSCD.zhonghua.tuo
        // TS: zhaotianyong 2015-04-27 EMAIL BUGFIX-962591 ADD_S
        mSettingFetched = true;
        // TS: zhaotianyong 2015-04-27 EMAIL BUGFIX-962591 ADD_E
        // TS: zhaotianyong 2015-04-27 EMAIL BUGFIX-988208 ADD_S
        showOofDialog();
        // TS: zhaotianyong 2015-04-27 EMAIL BUGFIX-988208 ADD_E
        OofSyncTask task = new OofSyncTask(COMMAND_FETCH_SETTINGS);
        task.execute(mContent);
    }

    private void setUserOofSettings() {
        // TS: zhaotianyong 2015-04-27 EMAIL BUGFIX-988208 ADD_S
        showOofDialog();
        // TS: zhaotianyong 2015-04-27 EMAIL BUGFIX-988208 ADD_S
        updateContentFromView();
        if (mAutoReplyCB.isChecked()) {
            OofSyncTask task = new OofSyncTask(COMMAND_TURNON_OOF);
            task.execute(mContent);
        } else {
            OofSyncTask task = new OofSyncTask(COMMAND_TURNOFF_OOF);
            task.execute(mContent);
        }
    }

    public void doDone() {
        if ((mAutoReplyCB.isChecked() && "".endsWith(mInternalMessage.getText()
                .toString()))
                || (mAutoReplyCB.isChecked() && mExternalCB.isChecked() && ""
                        .endsWith(mExternalMessage.getText().toString()))) {
             AlertDialog dialog = new AlertDialog.Builder(mContext)
                    .setTitle(R.string.account_setting_oof_label)
                    .setMessage(R.string.oof_done_message_attention)
                    .setPositiveButton(getResources().getString(R.string.oof_continue).toUpperCase(),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0,
                                        int arg1) {
                                    //[BUGFIX]-Add-BEGIN by TSCD.zhonghua.tuo,07/18/2014 for PR 738479
                                    judgeOrSetOofSettings();
                                    //[BUGFIX]-Add-END by TSCD.zhonghua.tuo
                                }
                            })
                    .setNegativeButton(getResources().getString(R.string.cancel).toUpperCase(),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0,
                                        int arg1) {
                                    }
                                }).create();
            dialog.show();
        } else {
            //[BUGFIX]-Add-BEGIN by TSCD.zhonghua.tuo,07/18/2014 for PR 738479
            judgeOrSetOofSettings();
            //[BUGFIX]-Add-END by TSCD.zhonghua.tuo
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:

            onBackPressed();
            break;
        case R.id.done:
            doDone();
            break;

        // TS: Gantao 2015-11-27 EMAIL FEATURE-988794 DEL_S
        //We remove the cancel option, click back key do the cation
//        case R.id.cancel:
//            finish();
//            break;
            // TS: Gantao 2015-11-27 EMAIL FEATURE-988794 DEL_E
        default:
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (v.getId()) {
            case R.id.start_time:
                onStartTimeClick();
                break;
            case R.id.start_date:
                onStartDateClick();
                break;
            case R.id.end_time:
                onEndTimeClick();
                break;
            case R.id.end_date:
                onEndDateClick();
                break;
            }
            return true;
        }
        return false;
    }

    @Override
    public void onCheckedChanged(CompoundButton v, boolean checked) {
        switch (v.getId()) {
        case R.id.auto_reply:
            onAutoRepleyClicked(checked);
            break;
        case R.id.auto_replies_period:
            setInternalEnabled();
            break;
        case R.id.auto_replies_external:
            setExternalEnabled();
            break;
        }
    }

    private long String2UTC(String time) {
        SimpleDateFormat format = new SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTimeInMillis(format.parse(time).getTime());
            return calendar.getTimeInMillis() + calendar.getTimeZone().getRawOffset();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private String GetUTCTime(long timeMillis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeMillis);
        SimpleDateFormat format = new SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        try {
            return format.format(calendar.getTimeInMillis() - calendar.getTimeZone().getRawOffset());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
    //[FEATURE]-Add-BEGIN by CDTS.zhonghua.tuo,04/15/2014,FR 635145
    public Bundle syncOof(final long accountId, final String command, final Bundle content){
        final IEmailService proxy = EmailServiceUtils.getServiceForAccount(this, accountId);
        if (proxy != null) {
            // Service implementation
            try {
                return proxy.syncOof(accountId, command,content);
            } catch (RemoteException e) {
                // TODO Change exception handling to be consistent with however this method
                // is implemented for other protocols
            }
        }
        return null;
    }
    //[FEATURE]-Add-END by CDTS.zhonghua.tuo

    // TS: zhaotianyong 2015-04-27 EMAIL BUGFIX-962591 ADD_S
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mAutoReplyCB != null ) {
            outState.putBoolean(STATE_KEY_AUTO_REPLAY_CB, mAutoReplyCB.isChecked());
        }
        if (mInternalCB != null) {
            outState.putBoolean(STATE_KEY_EXTERNAL_CB, mInternalCB.isChecked());
        }
        if (mExternalCB != null) {
            outState.putBoolean(STATE_KEY_INTERNAL_CB, mExternalCB.isChecked());
        }
        outState.putBoolean(STATE_KEY_OOF_SETTING_FETCHED, mSettingFetched);
        // TS: Gantao 2016-1-06 EMAIL BUGFIX-1246418 ADD_S
        //Save the time picker state, so we can still show time picker dialog if user change the screen orientation.
        outState.putBoolean(STATE_KEY_START_TIME_CLICKED, mStartTimeClicked);
        outState.putBoolean(STATE_KEY_START_DATE_CLICKED, mStartDateClicked);
        outState.putBoolean(STATE_KEY_END_TIME_CLICKED, mEndTimeClicked);
        outState.putBoolean(STATE_KEY_END_DATE_CLICKED, mEndDateClicked);
        outState.putLong(START_TIME_MILLIS, mStartTimeMillis);
        outState.putLong(END_TIME_MILLIS, mEndTimeMillis);
        // TS: Gantao 2016-1-06 EMAIL BUGFIX-1246418 ADD_E
    }
    // TS: zhaotianyong 2015-04-27 EMAIL BUGFIX-962591 ADD_E

    // TS: zhaotianyong 2015-04-27 EMAIL BUGFIX-988208 ADD_S
    public void showOofDialog(){
        String messageString=getResources().getString(R.string.out_of_office_syncing);
        mOofDialog=ProgressDialog.show(this, null, messageString, true, false, null);
    }
    public void dismissOofDialog(){
        try {
            mOofDialog.dismiss();
        } catch (IllegalArgumentException e) {
            LogUtils.e(LogUtils.TAG,"process dialog not attached to window manager");
        }
    }
    public void showsyncMessage(int text){
		//[BUGFIX]-MOD by SCDTABLET.shujing.jin@tcl.com,08/05/2016,2635083
		Utility.showShortToast(this, text);
        //Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }
    // TS: zhaotianyong 2015-04-27 EMAIL BUGFIX-988208 ADD_E

}
