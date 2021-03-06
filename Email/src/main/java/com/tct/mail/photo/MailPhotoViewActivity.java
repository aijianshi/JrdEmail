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
 *Tag		 	 Date	      Author		  Description
 *============== ============ =============== ==============================
 *CONFLICT-50001 2014/10/08   zhaotianyong	  Modify the package conflict
 ============================================================================ 
 */

package com.tct.mail.photo;

import android.content.Context;
import android.content.Intent;

//TS: MOD by zhaotianyong for CONFLICT_50001 START
//import com.tct.ex.photo.Intents;
//import com.tct.ex.photo.PhotoViewActivity;
//import com.tct.ex.photo.PhotoViewController;
import com.tct.fw.ex.photo.Intents;
import com.tct.fw.ex.photo.PhotoViewActivity;
import com.tct.fw.ex.photo.PhotoViewController;
import com.tct.mail.browse.ConversationMessage;
import com.tct.mail.providers.UIProvider;
//TS: MOD by zhaotianyong for CONFLICT_50001 END

/**
 * Derives from {@link PhotoViewActivity} to allow customization.
 * Delegates all work to {@link MailPhotoViewController}.
 */
public class MailPhotoViewActivity extends PhotoViewActivity implements
        MailPhotoViewController.ActivityInterface {

    static final String EXTRA_ACCOUNT = MailPhotoViewActivity.class.getName() + "-acct";
    static final String EXTRA_MESSAGE = MailPhotoViewActivity.class.getName() + "-msg";
    static final String EXTRA_HIDE_EXTRA_OPTION_ONE =
            MailPhotoViewActivity.class.getName() + "-hide-extra-option-one";

    /**
     * Start a new MailPhotoViewActivity to view the given images.
     *
     * @param photoIndex The index of the photo to show first.
     */
    public static void startMailPhotoViewActivity(final Context context, final String account,
            final ConversationMessage msg, final int photoIndex) {
        final Intents.PhotoViewIntentBuilder builder =
                Intents.newPhotoViewIntentBuilder(context,
                        "com.tct.mail.photo.MailPhotoViewActivity");
        builder
                .setPhotosUri(msg.attachmentListUri.toString())
                .setProjection(UIProvider.ATTACHMENT_PROJECTION)
                .setPhotoIndex(photoIndex);

        context.startActivity(wrapIntent(builder.build(), account, msg));
    }

    /**
     * Start a new MailPhotoViewActivity to view the given images.
     *
     * @param initialPhotoUri The uri of the photo to show first.
     */
    public static void startMailPhotoViewActivity(final Context context, final String account,
            final ConversationMessage msg, final String initialPhotoUri) {
        context.startActivity(
                buildMailPhotoViewActivityIntent(context, account, msg, initialPhotoUri));
    }

    public static Intent buildMailPhotoViewActivityIntent(
            final Context context, final String account, final ConversationMessage msg,
            final String initialPhotoUri) {
        final Intents.PhotoViewIntentBuilder builder = Intents.newPhotoViewIntentBuilder(
                context, "com.tct.mail.photo.MailPhotoViewActivity");

        builder.setPhotosUri(msg.attachmentListUri.toString())
                .setProjection(UIProvider.ATTACHMENT_PROJECTION)
                .setInitialPhotoUri(initialPhotoUri);

        return wrapIntent(builder.build(), account, msg);
    }

    private static Intent wrapIntent(
            final Intent intent, final String account, final ConversationMessage msg) {
        intent.putExtra(EXTRA_MESSAGE, msg);
        intent.putExtra(EXTRA_ACCOUNT, account);
        intent.putExtra(EXTRA_HIDE_EXTRA_OPTION_ONE, msg.getConversation() == null);
        return intent;
    }

    @Override
    public PhotoViewController createController() {
        return new MailPhotoViewController(this);
    }
}
