/*
 ==========================================================================
 *HISTORY
 *
 *Tag		 	 Date	      Author		  Description
 *============== ============ =============== ==============================
 *CONFLICT-50001 2014/10/23   zhaotianyong	  Modify the package conflict
 ============================================================================ 
 */
package com.tct.fw.ex.photo;

import android.graphics.drawable.Drawable;


/**
 * Wrapper activity for an action bar. This wraps either a {@link android.app.ActionBar} or
 * {@link android.support.v7.app.ActionBar}.
 */
public interface ActionBarInterface {

    public interface OnMenuVisibilityListener {
        /**
         * Called when an action bar menu is shown or hidden. Applications may want to use
         * this to tune auto-hiding behavior for the action bar or pause/resume video playback,
         * gameplay, or other activity within the main content area.
         *
         * @param isVisible True if an action bar menu is now visible, false if no action bar
         *                  menus are visible.
         */
        public void onMenuVisibilityChanged(boolean isVisible);
    }

    public void setDisplayHomeAsUpEnabled(boolean showHomeAsUp);

    public void addOnMenuVisibilityListener(OnMenuVisibilityListener listener);

    /**
     * Wrapper for {@code setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE,
     * ActionBar.DISPLAY_SHOW_TITLE)}.
     */
    public void setDisplayOptionsShowTitle();

    public CharSequence getTitle();

    public void setTitle(CharSequence title);

    public void setSubtitle(CharSequence subtitle);

    public void show();

    public void hide();

    public void setLogo(Drawable logo);
}
