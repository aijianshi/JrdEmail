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
/*  File     : exchange2/src/com/android/exchange/adapter/OofParser.java      */
/*  Labels   :                                                                */
/* -------------------------------------------------------------------------- */
/* ========================================================================== */
/*     Modifications on Features list / Changes Request / Problems Report     */
/* -------------------------------------------------------------------------- */
/*    date   |        author        |         Key          |     comment      */
/* ----------|----------------------|----------------------|----------------- */
/* 08/30/2013|     chen caixia      |      FR 476662       |Email] OUT OF OF- */
/*           |                      |                      |FICE setting      */
/* ----------|----------------------|----------------------|----------------- */
/******************************************************************************/

package com.tct.exchange.adapter;

import android.os.Bundle;
import android.util.Log;

import com.tct.exchange.eas.OofOperation;

import java.io.IOException;
import java.io.InputStream;

/**
 * Parse the result of a Settings command.
 *
 * We only send the Settings command in EAS 14.0 after sending a Provision command for the first
 * time.  parse() returns true in the normal case; false if access to the account is denied due
 * to the actual settings (e.g. if a particular device type isn't allowed by the server)
 */
public class OofParser extends Parser {
    private final OofOperation mOperation;
    private boolean mIsSettingsOK;
    private boolean mIsOofOK;
    private Bundle mOofSettings;

    public OofParser(InputStream in, OofOperation operation) throws IOException {
        super(in);
        mOperation = operation;
        mOofSettings = new Bundle();
    }

    @Override
    public boolean parse() throws IOException {
        if (nextTag(START_DOCUMENT) != Tags.SETTINGS_SETTINGS) {
            throw new IOException();
        }
        while (nextTag(START_DOCUMENT) != END_DOCUMENT) {
            if (tag == Tags.SETTINGS_STATUS && getValueInt() == 1) {
                mIsSettingsOK = true;
            } else if (tag == Tags.SETTINGS_OOF) {
                parseOof();
            } else {
                skipTag();
            }

//            try {
//                String t = Tags.pages[Tags.SETTINGS][tag - 5 - Tags.SETTINGS_PAGE];
//                String value = "";
//                switch(tag) {
//                case Tags.SETTINGS_STATUS:
//                case Tags.SETTINGS_OOF_STATE:
//                case Tags.SETTINGS_START_TIME:
//                case Tags.SETTINGS_END_TIME:
//                case Tags.SETTINGS_ENABLED:
//                case Tags.SETTINGS_REPLY_MESSAGE:
//                case Tags.SETTINGS_BODY_TYPE:
//                    value = getValue();
//                }
//                Log.d("oof", "Tag: <" + t + ">" + ("".equals(value.trim()) ? value : ", Value: " + value));
//            } catch(IOException ex) {
//                ex.printStackTrace();
//                continue;
//            }
        }

        boolean res = mIsSettingsOK && mIsOofOK;
        if (res) {
            mOofSettings.putBoolean("SyncOofOK", true);
        }

        mOperation.setOofSettings(mOofSettings);

        return res;
    }

    private void parseOof() throws IOException {
        while (nextTag(Tags.SETTINGS_OOF) != END) {
            if (tag == Tags.SETTINGS_STATUS && getValueInt() == 1) {
                mIsOofOK = true;
            } else if (tag == Tags.SETTINGS_GET) {
                parseGet();
            } else {
                skipTag();
            }
        }
    }

    private void parseGet() throws IOException {
        while (nextTag(Tags.SETTINGS_GET) != END) {
            switch(tag) {
            case Tags.SETTINGS_OOF_STATE:
                mOofSettings.putInt("OofState", getValueInt());
                break;
            case Tags.SETTINGS_START_TIME:
                mOofSettings.putString("StartTime", getValue());
                break;
            case Tags.SETTINGS_END_TIME:
                mOofSettings.putString("EndTime", getValue());
                break;
            case Tags.SETTINGS_OOF_MESSAGE:
                parseOofMessage();
                break;
            default: skipTag();
            }
        }
    }

    private void parseOofMessage() throws IOException {
        String messageType = "";
        String messageValue = "";
        int replyType = 0;
        int enabled = 0;

        while (nextTag(Tags.SETTINGS_OOF_MESSAGE) != END) {
            switch(tag) {
            case Tags.SETTINGS_APPLIES_TO_INTERNAL:
                messageType = "InternalMessage";
                replyType = 1;
                break;
            case Tags.SETTINGS_APPLIES_TO_EXTERNAL_KNOWN:
                messageType = "ExternalMessage";
                replyType = 2;
                break;
            case Tags.SETTINGS_APPLIES_TO_EXTERNAL_UNKNOWN:
                messageType = "ExternalMessage";
                replyType = 3;
                break;
            case Tags.SETTINGS_ENABLED:
                enabled = getValueInt();
                break;
            case Tags.SETTINGS_REPLY_MESSAGE:
                messageValue = getValue();
                break;
            case Tags.SETTINGS_BODY_TYPE:
                mOofSettings.putString("BodyType", getValue());
                break;
            default: skipTag();
            }
        }
        if (replyType == 1 && enabled == 1) {
            mOofSettings.putInt("ExternalState", 0);
        } else if (replyType == 2 && enabled == 1) {
            mOofSettings.putInt("ExternalState", 1);
        } else if (replyType == 3 && enabled == 1) {
            mOofSettings.putInt("ExternalState", 2);
        }
        mOofSettings.putString(messageType, messageValue);
    }
}
