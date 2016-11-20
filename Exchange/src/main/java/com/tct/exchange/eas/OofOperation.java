/******************************************************************************/
/*                                                               Date:10/2014 */
/*                                PRESENTATION                                */
/*                                                                            */
/*       Copyright 2013 TCL Communication Technology Holdings Limited.        */
/*                                                                            */
/* This material is company confidential, cannot be reproduced in any form    */
/* without the written permission of TCL Communication Technology Holdings    */
/* Limited.                                                                   */
/*                                                                            */
/* -------------------------------------------------------------------------- */
/*  Author :  wu wenlu                                                     */
/*  Email  :  wenlu.wu@tcl-mobile.com                                      */
/*  Role   :                                                                  */
/*  Reference documents :                                                     */
/* -------------------------------------------------------------------------- */
/*  Comments :                                                                */
/*  File     :src/com/android/exchange/eas/OofOperation.java                  */
/*  Labels   :                                                                */
/* -------------------------------------------------------------------------- */
/* ========================================================================== */
/*     Modifications on Features list / Changes Request / Problems Report     */
/* -------------------------------------------------------------------------- */
/*    date   |        author        |         Key          |     comment      */
/* ----------|----------------------|----------------------|----------------- */
/* 10/20/2013|     wu wenlu         |      FR 719110       |Email] OUT OF OF- */
/*           |                      |                      |FICE setting      */
/* ----------|----------------------|----------------------|----------------- */
/******************************************************************************/

package com.tct.exchange.eas;

import android.os.Bundle;
import org.apache.http.HttpEntity;
import java.io.IOException;
import android.util.Log;
import android.content.Context;
import org.apache.http.HttpStatus;
import java.io.InputStream;

import com.tct.exchange.CommandStatusException;
import com.tct.exchange.EasResponse;
import com.tct.exchange.adapter.OofParser;
import com.tct.exchange.adapter.Serializer;
import com.tct.exchange.adapter.Tags;

public class OofOperation extends EasOperation {

    private Bundle mOofSettings;
    private String mCommand;
    private Bundle mContent;

    public final static int RESULT_OK = 1;
    public final static int RESULT_EMPTY_RESPONSE = 2;

    public OofOperation(final Context context, final long accountId, String command,
              Bundle content) {
        super(context, accountId);
        mCommand = command;
        mContent = content;
    }

    public Bundle getResultBundle() {
        return mOofSettings;
    }

    @Override
    protected String getCommand() {
        return "Settings";
    }

    @Override
    protected HttpEntity getRequestEntity() throws IOException {
        Serializer s;
        if ("TurnOnOof".equals(mCommand)) {
            s = turnOnOof(mContent);
        } else if ("TurnOffOof".equals(mCommand)) {
            s = turnOffOof();
        } else if ("FetchSettings".equals(mCommand)) {
            s = fetchSettings();
        } else {
            s = null;
        }
        return makeEntity(s);
    }

    @Override
    protected int handleResponse(final EasResponse response)
        throws IOException, CommandStatusException {
        if (response.isEmpty()) {
            return RESULT_EMPTY_RESPONSE;
        }
        int code = response.getStatus();
        if (code == HttpStatus.SC_OK) {
            InputStream is = response.getInputStream();
            try {
                OofParser parser = new OofParser(is, this);
                if (parser.parse()) {
                    Log.d("oof","parse ok");
                } else {
                    Log.d("oof","parse failed");
                }
            } finally {
                is.close();
            }
        }
        return RESULT_OK;
    }

    private Serializer turnOnOof(Bundle content) throws IOException {
        Serializer s = new Serializer();
        s.start(Tags.SETTINGS_SETTINGS);
        s.start(Tags.SETTINGS_OOF).start(Tags.SETTINGS_SET);

        int offState = content.getInt("OofState");
        s.data(Tags.SETTINGS_OOF_STATE, String.valueOf(offState));
        if (offState == 2) {
            s.data(Tags.SETTINGS_START_TIME, content.getString("StartTime"));
            s.data(Tags.SETTINGS_END_TIME, content.getString("EndTime"));
        }

        s.start(Tags.SETTINGS_OOF_MESSAGE);
        s.tag(Tags.SETTINGS_APPLIES_TO_INTERNAL);
        s.data(Tags.SETTINGS_ENABLED, "1");
        s.data(Tags.SETTINGS_REPLY_MESSAGE, content.getString("InternalMessage"));
        s.data(Tags.SETTINGS_BODY_TYPE, "TEXT");
        s.end();

        int externalState = content.getInt("ExternalState");  // 0: disabled external; 1: external known; 2: external unknown
        String externalMessage = content.getString("ExternalMessage");

        s.start(Tags.SETTINGS_OOF_MESSAGE);
        s.tag(Tags.SETTINGS_APPLIES_TO_EXTERNAL_KNOWN);
        s.data(Tags.SETTINGS_ENABLED, externalState > 0 ? "1" : "0");
        s.data(Tags.SETTINGS_REPLY_MESSAGE, externalMessage);
        s.data(Tags.SETTINGS_BODY_TYPE, "TEXT");
        s.end();

        s.start(Tags.SETTINGS_OOF_MESSAGE);
        s.tag(Tags.SETTINGS_APPLIES_TO_EXTERNAL_UNKNOWN);
        s.data(Tags.SETTINGS_ENABLED, externalState > 1 ? "1" : "0");
        s.data(Tags.SETTINGS_REPLY_MESSAGE, externalMessage);
        s.data(Tags.SETTINGS_BODY_TYPE, "TEXT");
        s.end();

        s.end().end().end().done();

        return s;
    }

    private Serializer turnOffOof() throws IOException {
        Serializer s = new Serializer();
        s.start(Tags.SETTINGS_SETTINGS);
        s.start(Tags.SETTINGS_OOF).start(Tags.SETTINGS_SET);

        s.data(Tags.SETTINGS_OOF_STATE, "0");

        s.end().end().end().done();

        return s;
    }

    private Serializer fetchSettings() throws IOException {
        Serializer s = new Serializer();
        s.start(Tags.SETTINGS_SETTINGS);
        s.start(Tags.SETTINGS_OOF).start(Tags.SETTINGS_GET);

        s.data(Tags.SETTINGS_BODY_TYPE, "TEXT");

        s.end().end().end().done();

        return s;
    }

    public void setOofSettings(Bundle oofSettings) {
        mOofSettings = oofSettings;
    }

}
