/**
 * Copyright (c) 2007, Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
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
 *Tag        Date         Author        Description
 *============== ============ =============== ==============================
 *FEATURE-ID     2015/08/27   Gantao         Horizontal attachment
 ===========================================================================
 */
package com.tct.mail.compose;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.tct.email.R;
import com.tct.mail.ui.AttachmentTile;
import com.tct.mail.ui.ThumbnailLoadTask;
import com.tct.mail.utils.LogTag;
import com.tct.mail.utils.LogUtils;
import com.tct.mail.utils.MimeType;
import com.tct.mail.providers.Attachment;
import com.tct.mail.utils.AttachmentUtils;

import org.json.JSONException;

/**
 * This view is used in the ComposeActivity to display an attachment along with its name/size
 * and a Remove button.
 */
//TS: Gantao 2015-08-27 EMAIL FEATURE_ID MOD
class AttachmentComposeView extends FrameLayout implements AttachmentDeletionInterface {
    private final Attachment mAttachment;
    private final static String LOG_TAG = LogTag.getLogTag();

    public AttachmentComposeView(Context c, Attachment attachment) {
        super(c);
        mAttachment = attachment;

        if (LogUtils.isLoggable(LOG_TAG, LogUtils.DEBUG)) {
            String attachStr = null;
            try {
                attachStr = attachment.toJSON().toString(2);
            } catch (JSONException e) {
                attachStr = attachment.toString();
            }
            LogUtils.d(LOG_TAG, "attachment view: %s", attachStr);
        }

        LayoutInflater factory = LayoutInflater.from(getContext());

      //TS: Gantao 2015-08-27 EMAIL FEATURE_ID MOD_S
        //<Horizontal attachment>require a new layout.
        factory.inflate(R.layout.attachment_compose_view, this);
      //TS: Gantao 2015-08-27 EMAIL FEATURE_ID MOD_E
        populateAttachmentData(c);
    }

    @Override
    public void addDeleteListener(OnClickListener clickListener) {
        ImageButton deleteButton = (ImageButton) findViewById(R.id.cancel_attachment);
        deleteButton.setOnClickListener(clickListener);
    }

  //TS: Gantao 2015-08-27 EMAIL FEATURE_ID MOD_S
    private void populateAttachmentData(Context context) {
        TextView titleView = (TextView) findViewById(R.id.attachment_title);
        titleView.setText(mAttachment.getName());
        ((TextView)findViewById(R.id.attachment_type)).setText(AttachmentUtils.getDisplayType(context, mAttachment));

        if (mAttachment.size > 0) {
            ((TextView) findViewById(R.id.attachment_size)).
                    setText(AttachmentUtils.convertToHumanReadableSize(context, mAttachment.size));
        } else {
            ((TextView) findViewById(R.id.attachment_size)).setVisibility(View.GONE);
        }

        //Load the image attachment's thunmbnail and show it.
        ImageView thunmbnailView = (ImageView) findViewById(R.id.attachment_thunmbnail);
        MimeType.setThumbnailBackground(thunmbnailView, mAttachment.getContentType());
         if(AttachmentTile.isTiledAttachment(mAttachment)) {
            ThumbnailLoadTask.setupThumbnailPreview(thunmbnailView, mAttachment, titleView);
        }
    }
  //TS: Gantao 2015-08-27 EMAIL FEATURE_ID MOD_E
}
