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
/*
 ==========================================================================
 *HISTORY
 *
 *Tag            Date         Author          Description
 *============== ============ =============== ==============================
 *CONFLICT-20001 2014/10/24   wenggangjin	Modify the package conflict
 *BUGFIX-932277  2015/02/15   ke.ma         [Arabic][Email]Cannot find new email icon
 *BUGFIX-935900  2015/02/28   zheng.zou     [REG][Force close][Email]Email force close when tap "Add authentication" in smtp server
 *BUGFIX-947843  2015/03/30   ke.ma           [5.0][Email] account setupwizard last screen 2.6 not according to ergo
 ===========================================================================
 */
package com.tct.email.activity.setup;

import android.app.Fragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.tct.email.R;
import com.tct.email.activity.UiUtilities;
//TS: MOD by wenggangjin for CONFLICT_20001 START
//import com.google.common.annotations.VisibleForTesting;
import com.tct.fw.google.common.annotations.VisibleForTesting;
//TS: MOD by wenggangjin for CONFLICT_20001 END
/**
 * Superclass for setup UI fragments.
 * Currently holds a super-interface for the callbacks, as well as the state it was launched for so
 * we can unwind things correctly when the user navigates the back stack.
 */
public class AccountSetupFragment extends Fragment implements View.OnClickListener {
    private static final String SAVESTATE_STATE = "AccountSetupFragment.state";
    private int mState;

    protected View mNextButton;
    protected View mPreviousButton;

    //TS: ke.ma 2015-02-15 EMAIL BUGFIX-932277 ADD_S
    protected Button nextButton;
    //TS: ke.ma 2015-02-15 EMAIL BUGFIX-932277 ADD_E

    public interface Callback {
        void onNextButton();
        void onBackPressed();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mState = savedInstanceState.getInt(SAVESTATE_STATE);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SAVESTATE_STATE, mState);
    }

    public void setState(int state) {
        mState = state;
    }

    public int getState() {
        return mState;
    }

    /**
     * This method wraps the given content layout with the chrome appropriate for the account setup
     * flow. It also attaches itself as a click handler to the previous and next buttons.
     *
     * @param inflater LayoutInflater scoped to the appropriate context
     * @param container ViewGroup to inflate the view into
     * @param contentLayout Resource ID of the main content layout to insert into the template
     * @param headline Resource ID of the headline string
     * @return Fully inflated view hierarchy.
     */
    protected View inflateTemplatedView(final LayoutInflater inflater, final ViewGroup container,
            final int contentLayout, final int headline) {
        final View template = inflater.inflate(R.layout.account_setup_template, container, false);

        TextView headlineView = UiUtilities.getView(template, R.id.headline);
        if (headline > 0) {
            headlineView.setText(headline);
            headlineView.setVisibility(View.VISIBLE);
            headlineView.setTypeface(Typeface.DEFAULT);//TS: yanhua.chen 2015-1-31 EMAIL BUGFIX_1538680 ADD
        } else {
            headlineView.setVisibility(View.GONE);
        }

        final ViewGroup contentContainer =
                (ViewGroup) template.findViewById(R.id.setup_fragment_content);
        inflater.inflate(contentLayout, contentContainer, true);

        mNextButton = UiUtilities.getView(template, R.id.next);
        mNextButton.setOnClickListener(this);
        mPreviousButton = UiUtilities.getView(template, R.id.previous);
        mPreviousButton.setOnClickListener(this);
        //TS: ke.ma 2015-02-15 EMAIL BUGFIX-932277 ADD_S
        nextButton=(Button) template.findViewById(R.id.next);
        //TS: ke.ma 2015-02-15 EMAIL BUGFIX-932277 ADD_E
        return template;
    }

    @Override
    public void onClick(View v) {
        final int viewId = v.getId();
        final Callback callback = (Callback) getActivity();

        if (viewId == R.id.next) {
            callback.onNextButton();
        } else if (viewId == R.id.previous) {
            callback.onBackPressed();
        }
    }

    public void setNextButtonEnabled(boolean enabled) {
      //TS: tao.gan 2015-09-28 EMAIL BUGFIX-1094674 ADD_S
        if(getActivity() == null) {
            return;
        }
      //TS: tao.gan 2015-09-28 EMAIL BUGFIX-1094674 ADD_E
        if (mNextButton != null) {
            mNextButton.setEnabled(enabled);
            //TS: zheng.zou 2015-02-28 EMAIL BUGFIX-935900 MOD_S
            if (nextButton != null) {
                //TS: ke.ma 2015-02-15 EMAIL BUGFIX-932277 ADD_S
                if(enabled){
                    nextButton.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_chevron_right_enable), null);
                }else{
                    nextButton.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_chevron_right), null);
                }
                //TS: ke.ma 2015-02-15 EMAIL BUGFIX-932277 ADD_E
            }
            //TS: zheng.zou 2015-02-28 EMAIL BUGFIX-935900 MOD_E
        }
    }

    public boolean isNextButtonEnabled() {
        return mNextButton.isEnabled();
    }


    /**
     * Set visibility of the "previous" button
     * @param visibility {@link View#INVISIBLE}, {@link View#VISIBLE}, {@link View#GONE}
     */
    public void setPreviousButtonVisibility(int visibility) {
        mPreviousButton.setVisibility(visibility);
    }

    /**
     * Set the visibility of the "next" button
     * @param visibility {@link View#INVISIBLE}, {@link View#VISIBLE}, {@link View#GONE}
     */
    public void setNextButtonVisibility(int visibility) {
        mNextButton.setVisibility(visibility);
    }

    //TS: ke.ma 2015-03-30 EMAIL BUGFIX-947843 ADD_S
    public void setNextButtonText(int strId) {
        nextButton.setText(strId);
    }
    //TS: ke.ma 2015-03-30 EMAIL BUGFIX-947843 ADD_E
}
