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

package com.tct.mail.browse;

import android.content.Context;
import android.content.Intent;

/**
 * Builds an intent to be used when the user long presses an
 * inline image and selects "View image".
 */
public interface InlineAttachmentViewIntentBuilder {

    /**
     * Creates an intent to be used when the user long presses an inline image and
     * selects "View image." Null should be returned if "View image" should not be
     * shown.
     * @param context Used to create the intent.
     * @param url The url of the image that was long-pressed.
     * @param message The message that owns this attachment.
     * @return An intent that should be used when the user long presses an
     * inline image and selects "View Image" or {@code null} if there should not
     * be a "View image" option for this url.
     */
    Intent createInlineAttachmentViewIntent(
            Context context, String url, ConversationMessage message);
}
