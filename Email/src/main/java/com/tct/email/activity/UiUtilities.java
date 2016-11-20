/*
 * Copyright (C) 2011 The Android Open Source Project
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

package com.tct.email.activity;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.view.View;
//[FRETURE]-ADD-BEGIN by TSNJ.wei huang 10/27/2014 PR739323
import com.tct.emailcommon.mail.Address;
//[FEATURE]-ADD-END by TSNJ.wei huang

import com.tct.email.R;

public class UiUtilities {
    private UiUtilities() {
    }

    /**
     * Formats the given size as a String in bytes, kB, MB or GB.  Ex: 12,315,000 = 11 MB
     */
    public static String formatSize(Context context, long size) {
        final Resources res = context.getResources();
        final long KB = 1024;
        final long MB = (KB * 1024);
        final long GB  = (MB * 1024);

        int resId;
        int value;

        if (size < KB) {
            resId = R.plurals.message_view_attachment_bytes;
            value = (int) size;
        } else if (size < MB) {
            resId = R.plurals.message_view_attachment_kilobytes;
            value = (int) (size / KB);
        } else if (size < GB) {
            resId = R.plurals.message_view_attachment_megabytes;
            value = (int) (size / MB);
        } else {
            resId = R.plurals.message_view_attachment_gigabytes;
            value = (int) (size / GB);
        }
        return res.getQuantityString(resId, value, value);
    }

    public static String getMessageCountForUi(Context context, int count,
            boolean replaceZeroWithBlank) {
        if (replaceZeroWithBlank && (count == 0)) {
            return "";
        } else if (count > 999) {
            return context.getString(R.string.more_than_999);
        } else {
            return Integer.toString(count);
        }
    }

    /**
     * Same as {@link View#findViewById}, but crashes if there's no view.
     */
    @SuppressWarnings("unchecked")
    public static <T extends View> T getView(View parent, int viewId) {
        return (T) checkView(parent.findViewById(viewId));
    }

    private static View checkView(View v) {
        if (v == null) {
            throw new IllegalArgumentException("View doesn't exist");
        }
        return v;
    }

    /**
     * Same as {@link View#setVisibility(int)}, but doesn't crash even if {@code view} is null.
     */
    public static void setVisibilitySafe(View v, int visibility) {
        if (v != null) {
            v.setVisibility(visibility);
        }
    }

    /**
     * Same as {@link View#setVisibility(int)}, but doesn't crash even if {@code view} is null.
     */
    public static void setVisibilitySafe(Activity parent, int viewId, int visibility) {
        setVisibilitySafe(parent.findViewById(viewId), visibility);
    }

    /**
     * Same as {@link View#setVisibility(int)}, but doesn't crash even if {@code view} is null.
     */
    public static void setVisibilitySafe(View parent, int viewId, int visibility) {
        setVisibilitySafe(parent.findViewById(viewId), visibility);
    }

    //[FRETURE]-ADD-BEGIN by TSNJ.wei huang 10/27/2014 PR739323
     /*
     * Computes a short string indicating the destination of the message based on To, Cc, Bcc.
     * If only one address appears, returns the friendly form of that address.
     * Otherwise returns the friendly form of the first address appended with "and N others".
     */
    public static String makeDisplayName(Context context, String packedTo, String packedCc, String packedBcc) {
        Address first = null;
        int nRecipients = 0;
        for (String packed: new String[] {packedTo, packedCc, packedBcc}) {
            Address[] addresses = Address.fromHeader(packed);
            nRecipients += addresses.length;
            if (first == null && addresses.length > 0) {
                first = addresses[0];
            }
        }
        if (nRecipients == 0) {
            return "";
        }
        String friendly = first.toFriendly();
        if (nRecipients == 1) {
            return friendly;
        }
        return context.getString(R.string.message_compose_display_name, friendly, nRecipients - 1);
    }
    //[FEATURE]-ADD-END by TSNJ.wei huang
}
