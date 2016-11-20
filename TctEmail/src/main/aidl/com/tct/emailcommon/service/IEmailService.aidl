/*
 * Copyright (C) 2008-2010 Marc Blank
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

package com.tct.emailcommon.service;

import com.tct.emailcommon.service.HostAuthCompat;
import com.tct.emailcommon.service.IEmailServiceCallback;
import com.tct.emailcommon.service.SearchParams;

import android.os.Bundle;

interface IEmailService {
    // Core email operations.
    // Many of these functions return status codes. The valid status codes are defined in
    // EmailServiceStatus.java
    oneway void loadAttachment(IEmailServiceCallback cb, long accountId, long attachmentId,
            boolean background);

    void updateFolderList(long accountId);

    // TODO: For Eas, sync() will also sync the outbox. We should make IMAP and POP work the same
    // way and get rid of sendMail().
    void sendMail(long accountId);

    int sync(long accountId, inout Bundle syncExtras);

    // Push-related functionality.

    // Notify the service that the push configuration has changed for an account.
    void pushModify(long accountId);

    // Other email operations.
    Bundle validate(in HostAuthCompat hostauth);

    int searchMessages(long accountId, in SearchParams params, long destMailboxId);

    // PIM functionality (not strictly EAS specific).
    oneway void sendMeetingResponse(long messageId, int response);

    // [FEATURE]-ADD-BEGIN by TSNJ,wenlu.wu,10/20/2014,FR-719110
    Bundle syncOof(long accountId, String command, in Bundle bundle);
    // [FEATURE]-ADD-END by TSNJ,wenlu.wu,10/20/2014,FR-719110

    // Specific to EAS protocol.
    // TODO: this passes a HostAuth back in the bundle. We should be using a HostAuthCom for that.
    Bundle autoDiscover(String userName, String password);

    // Service control operations (i.e. does not generate a client-server message).
    // TODO: We should store the logging flags in the contentProvider, and this call should just
    // trigger the service to reload the flags.
    oneway void setLogging(int flags);

    void deleteExternalAccountPIMData(String emailAddress);

    int getApiVersion();
    // TS: wenggangjing 2015-05-19 EMAIL BUGFIX-993643 ADD_S
    // M: Added for Exchange Partial download request
    int fetchMessage(long messageId);
    // TS: wenggangjing 2015-05-19 EMAIL BUGFIX-993643 ADD_E
}
