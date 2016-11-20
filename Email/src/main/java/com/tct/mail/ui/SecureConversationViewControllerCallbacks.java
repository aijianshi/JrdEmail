/*
 * Copyright (C) 2013 Google Inc.
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
package com.tct.mail.ui;

import android.app.Fragment;
import android.os.Handler;

import com.tct.emailcommon.mail.Address;
import com.tct.mail.ContactInfoSource;
import com.tct.mail.browse.ConversationAccountController;
import com.tct.mail.browse.ConversationViewHeader;
import com.tct.mail.browse.MessageHeaderView;

import java.util.Map;

/**
 * Callbacks for fragments that use the {@link SecureConversationViewController}.
 */
public interface SecureConversationViewControllerCallbacks {
    public Handler getHandler();
    public AbstractConversationWebViewClient getWebViewClient();
    public Fragment getFragment();
    public void setupConversationHeaderView(ConversationViewHeader headerView);
    public boolean isViewVisibleToUser();
    public ContactInfoSource getContactInfoSource();
    public ConversationAccountController getConversationAccountController();
    public Map<String, Address> getAddressCache();
    public void setupMessageHeaderVeiledMatcher(MessageHeaderView messageHeaderView);
    public void startMessageLoader();
    public String getBaseUri();
    public boolean isViewOnlyMode();
    public boolean shouldAlwaysShowImages();
    public void setEasCallFlag(boolean flag);  // TS: gangjin.weng 2015-04-20 EMAIL BUGFIX_940964 ADD
}
