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
 *Tag            Date         Author           Description
 *============== ============ =============== ==============================
 *CONFLICT-50001 2014/10/08   zhaotianyong     Modify the package conflict
 *BUGFIX-869494  2014/12/31   zhaotianyong    [Android5.0][Email][UE] Show attachments on top screen.
 *BUGFIX-883496  2015/01/03   wenggangjin     [Email]Auto download .tif attachfile
 *BUGFIX-1009030  2015/06/03  Gantao          [Android5.0][Email]Attachment cannot fetch when download again.(may be related to 1013191)
 *FEATURE-ID     2015/08/27   Gantao         Horizontal attachment
 ============================================================================ 
 */

package com.tct.mail.ui;

import android.R;
import android.content.ContentResolver;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

//TS: MOD by zhaotianyong for CONFLICT_50001 START
//import com.tct.ex.photo.util.Exif;
//import com.tct.ex.photo.util.ImageUtils;
import com.tct.fw.ex.photo.util.Exif;
import com.tct.fw.ex.photo.util.ImageUtils;
import com.tct.mail.browse.MessageAttachmentBar;
import com.tct.mail.providers.Attachment;
//TS: MOD by zhaotianyong for CONFLICT_50001 END

import com.tct.mail.utils.LogTag;
import com.tct.mail.utils.LogUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * Performs the load of a thumbnail bitmap in a background
 * {@link AsyncTask}. Available for use with any view that implements
 * the {@link AttachmentBitmapHolder} interface.
 */
public class ThumbnailLoadTask extends AsyncTask<Uri, Void, Bitmap> {
    private static final String LOG_TAG = LogTag.getLogTag();

    private AttachmentBitmapHolder attachmentBitMaipHolder;
    private ImageView mViewHolder;
    private ImageView mComposeViewHolder;
    private TextView mComposeTitleView;
    private int mWidth;
    private int mHeight;
    private MessageAttachmentBar mBarView;

    public static void setupThumbnailPreview(final AttachmentBitmapHolder holder,
            final Attachment attachment, final Attachment prevAttachment) {
        final int width = holder.getThumbnailWidth();
        final int height = holder.getThumbnailHeight();
        if (attachment == null || width == 0 || height == 0
                || !ImageUtils.isImageMimeType(attachment.getContentType())) {
            holder.setThumbnailToDefault();
            return;
        }

        final Uri thumbnailUri = attachment.thumbnailUri;
        final Uri contentUri = attachment.contentUri;
        final Uri uri = (prevAttachment == null) ? null : prevAttachment.getIdentifierUri();
        final Uri prevUri = (prevAttachment == null) ? null : prevAttachment.getIdentifierUri();
        // begin loading a thumbnail if this is an image and either the thumbnail or the original
        // content is ready (and different from any existing image)
        if ((thumbnailUri != null || contentUri != null)
                && (holder.bitmapSetToDefault() ||
                prevUri == null || !uri.equals(prevUri))) {
            final ThumbnailLoadTask task = new ThumbnailLoadTask(
                    holder, width, height);
            task.execute(thumbnailUri, contentUri);
        } else if (thumbnailUri == null && contentUri == null) {
            // not an image, or no thumbnail exists. fall back to default.
            // async image load must separately ensure the default appears upon load failure.
            holder.setThumbnailToDefault();
        }
    }

  //TS: Gantao 2015-08-27 EMAIL FEATURE_ID MOD_S
    //For mail content attachment's thumbnail load.
    public static void setupThumbnailPreview(final ImageView viewHolder,
            final Attachment attachment, final MessageAttachmentBar barView) {
        //TODO Get the width/height of the holder regularly
        final int width = 306;
        final int height = 210;
      //TS: wenggangjin 2015-01-03 EMAIL BUGFIX_883496 MOD_S
        if (attachment == null || width == 0 || height == 0
                || !ImageUtils.isImageMimeType(attachment.getContentType()) || attachment.getContentType().endsWith(".tif") 
                || attachment.getContentType().endsWith(".TIF")) {
            return;
        }
      //TS: wenggangjin 2015-01-03 EMAIL BUGFIX_883496 MOD_E
        final Uri thumbnailUri = attachment.thumbnailUri;
        final Uri contentUri = attachment.contentUri;
        // begin loading a thumbnail if this is an image and either the thumbnail or the original
        // content is ready (and different from any existing image)
        if ((thumbnailUri != null || contentUri != null)
                && barView.isThumbnailDefault())
                /* && (holder.bitmapSetToDefault() ||
                prevUri == null || !uri.equals(prevUri)))*/ {
            final ThumbnailLoadTask task = new ThumbnailLoadTask(
                    viewHolder,barView, width, height);
            task.execute(thumbnailUri, contentUri);
        } else if (thumbnailUri == null && contentUri == null) {
            // not an image, or no thumbnail exists. fall back to default.
            // async image load must separately ensure the default appears upon load failure.
//            barHolder.getmAttachmentIcon().setImageResource(R.drawable.ic_menu_attachment_holo_light);
            barView.setThumbnailDefault(true);
        }
    }
  //TS: Gantao 2015-08-27 EMAIL FEATURE_ID MOD_E

  //TS: Gantao 2015-08-27 EMAIL FEATURE_ID ADD_S
    //For compose attachment's thumbnail load.
    public static void setupThumbnailPreview(final ImageView holder,
            final Attachment attachment, final TextView titleView) {
        final int width = 306;
        final int height = 210;
        if (attachment == null || width == 0 || height == 0
                || !ImageUtils.isImageMimeType(attachment.getContentType())) {
            return;
        }

        final Uri thumbnailUri = attachment.thumbnailUri;
        final Uri contentUri = attachment.contentUri;
        // begin loading a thumbnail if this is an image and either the thumbnail or the original
        // content is ready (and different from any existing image)
        if ((thumbnailUri != null || contentUri != null)) {
            final ThumbnailLoadTask task = new ThumbnailLoadTask(
                    holder, titleView, width, height);
            task.execute(thumbnailUri, contentUri);
        }
    }
  //TS: Gantao 2015-08-27 EMAIL FEATURE_ID ADD_E

    public ThumbnailLoadTask(AttachmentBitmapHolder holder, int width, int height) {
        attachmentBitMaipHolder = holder;
        mWidth = width;
        mHeight = height;
    }

    //For mail content attachment's thumbnail load.
    public ThumbnailLoadTask(ImageView viewHolder, MessageAttachmentBar barView, int width, int height) {
        mViewHolder = viewHolder;
        mWidth = width;
        mHeight = height;
        mBarView = barView;
    }

  //TS: Gantao 2015-08-27 EMAIL FEATURE_ID ADD_S
    //For compose attachment's thumbnail load.
    public ThumbnailLoadTask(ImageView holder, TextView titleView, int width, int height) {
        mComposeViewHolder = holder;
        mComposeTitleView = titleView;
        mWidth = width;
        mHeight = height;
    }
  //TS: Gantao 2015-08-27 EMAIL FEATURE_ID ADD_E
    @Override
    protected Bitmap doInBackground(Uri... params) {
        Bitmap result = loadBitmap(params[0]);
        if (result == null) {
            result = loadBitmap(params[1]);
        }

        return result;
    }

  //TS: Gantao 2015-08-27 EMAIL FEATURE_ID MOD_S
    private Bitmap loadBitmap(final Uri thumbnailUri) {
        if (thumbnailUri == null) {
            LogUtils.e(LOG_TAG, "Attempting to load bitmap for null uri");
            return null;
        }

        final int orientation = getOrientation(thumbnailUri);

        AssetFileDescriptor fd = null;
        try {
            //Orignal attachment Bitmap holder
            if (attachmentBitMaipHolder != null){
                fd = attachmentBitMaipHolder.getResolver().openAssetFileDescriptor(thumbnailUri, "r");
                //Compose attachment Bitmap holder
            } else if (mComposeViewHolder != null){
                fd = mComposeViewHolder.getContext().getContentResolver().openAssetFileDescriptor(thumbnailUri, "r");
              //Mail content Bitmap holder
            } else {
                fd = mViewHolder.getContext().getContentResolver().openAssetFileDescriptor(thumbnailUri, "r");
            }
            if (isCancelled() || fd == null) {
                return null;
            }

            final BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;
            opts.inDensity = DisplayMetrics.DENSITY_LOW;

            BitmapFactory.decodeFileDescriptor(fd.getFileDescriptor(), null, opts);
            if (isCancelled() || opts.outWidth == -1 || opts.outHeight == -1) {
                return null;
            }

            opts.inJustDecodeBounds = false;
            // Shrink both X and Y (but do not over-shrink)
            // and pick the least affected dimension to ensure the thumbnail is fillable
            // (i.e. ScaleType.CENTER_CROP)
            final int wDivider = Math.max(opts.outWidth / mWidth, 1);
            final int hDivider = Math.max(opts.outHeight / mHeight, 1);
            opts.inSampleSize = Math.min(wDivider, hDivider);

            LogUtils.d(LOG_TAG, "in background, src w/h=%d/%d dst w/h=%d/%d, divider=%d",
                    opts.outWidth, opts.outHeight, mWidth, mHeight, opts.inSampleSize);

            final Bitmap originalBitmap = BitmapFactory.decodeFileDescriptor(
                    fd.getFileDescriptor(), null, opts);
            if (originalBitmap != null && orientation != 0) {
                final Matrix matrix = new Matrix();
                matrix.postRotate(orientation);
                return Bitmap.createBitmap(originalBitmap, 0, 0, originalBitmap.getWidth(),
                        originalBitmap.getHeight(), matrix, true);
            }
            return originalBitmap;
        } catch (Throwable t) {
            LogUtils.i(LOG_TAG, "Unable to decode thumbnail %s: %s %s", thumbnailUri,
                    t.getClass(), t.getMessage());
        } finally {
            if (fd != null) {
                try {
                    fd.close();
                } catch (IOException e) {
                    LogUtils.e(LOG_TAG, e, "");
                }
            }
        }

        return null;
    }

    private int getOrientation(final Uri thumbnailUri) {
        if (thumbnailUri == null) {
            return 0;
        }

        InputStream in = null;
        try {
            //TS: zhaotianyong 2014-12-31 EMAIL BUGFIX_869494 MOD_S
            ContentResolver resolver;
          //Orignal attachment Bitmap holder
            if(attachmentBitMaipHolder!=null){
                resolver = attachmentBitMaipHolder.getResolver();
              //Compose attachment Bitmap holder
            } else if (mComposeViewHolder != null) {
                resolver = mComposeViewHolder.getContext().getContentResolver();
              //Mail content Bitmap holder
            } else {
                //TS: Gantao 2015-06-03 EMAIL BUGFIX_1009030(1013191) MOD_S
                resolver = mViewHolder.getContext().getContentResolver();
                //TS: Gantao 2015-06-03 EMAIL BUGFIX_1009030(1013191) MOD_E
            }
            //TS: zhaotianyong 2014-12-31 EMAIL BUGFIX_869494 MOD_E
            in = resolver.openInputStream(thumbnailUri);
            return Exif.getOrientation(in, -1);
        } catch (Throwable t) {
            LogUtils.i(LOG_TAG, "Unable to get orientation of thumbnail %s: %s %s", thumbnailUri,
                    t.getClass(), t.getMessage());
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    LogUtils.e(LOG_TAG, e, "error attemtping to close input stream");
                }
            }
        }

        return 0;
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        if (result == null) {
            LogUtils.d(LOG_TAG, "back in UI thread, decode failed or file does not exist");
            if(attachmentBitMaipHolder!=null){
                attachmentBitMaipHolder.thumbnailLoadFailed();
            } else if (mComposeViewHolder != null) {
                mComposeTitleView.setVisibility(View.VISIBLE);
                LogUtils.e(LOG_TAG, "back in UI thread ,decode failed or file dose not exts when compose");
            } else {
                mBarView.setTitleVisibility(View.VISIBLE);
                mBarView.setThumbnailDefault(true);
            }
            return;
        }

        LogUtils.d(LOG_TAG, "back in UI thread, decode success, w/h=%d/%d", result.getWidth(),
                result.getHeight());
      //Orignal attachment Bitmap holder
        if(attachmentBitMaipHolder!=null){
            attachmentBitMaipHolder.setThumbnail(result);
          //Compose attachment Bitmap holder
        } else if (mComposeViewHolder != null ) {
            mComposeViewHolder.setImageBitmap(result);
          //If the attachment is image, we don't show its name
            mComposeTitleView.setVisibility(View.GONE);
          //Mail content Bitmap holder
        }  else {
            mViewHolder.setImageBitmap(result);
          //If the attachment is image and it's downloaded, we don't show its name
            mBarView.setTitleVisibility(View.GONE);
            mBarView.setThumbnailDefault(false);
        }
    }
  //TS: Gantao 2015-08-27 EMAIL FEATURE_ID MOD_E

}
