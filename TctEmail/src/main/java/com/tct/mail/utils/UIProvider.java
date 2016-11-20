/*
 ==========================================================================
 *HISTORY
 *
 *Tag            Date         Author          Description
 *============== ============ =============== ==============================
 *CONFLICT-50003 2014/11/04   zhaotianyong    Modify the package conflict
 *BUGFIX-883410  2015/01/13   xiaolin.li      [Email]Search dysfunction;
 *BUGFIX-1031608 2015/06/30  Gantao     [Email]No prompt pop up after download again the attachment.
 *CR_585337      2015-09-21  chao.zhang       Exchange Email resend mechanism
 *BUGFIX-944797  2015-11-26   jian.xu         [Android L][Email]Retry notification not disappear after reconnect wifi
 *BUGFIX_1162996 2015/1/20    yanhua.chen     [Android 6.0][Email]TCL account pop up permission needed window continuously if disable contact/calendar permission of exchange
 ============================================================================
 */
package com.tct.mail.utils;

public class UIProvider {

	/**
     * The attachment will be or is already saved to the app-private cache partition.
     */
    public static final int UIPROVIDER_ATTACHMENTDESTINATION_CACHE = 0;
    // TS: Gantao 2015-06-30 EMAIL BUGFIX-1031608 ADD_S
    /*
     * The attachment will be or is already saved to the external storage.
     */
    // TS: Gantao 2015-06-30 EMAIL BUGFIX-1031608 ADD_E
    public static final int UIPROVIDER_ATTACHMENTDESTINATION_EXTERNAL = 1;
     /**
     * The attachment was successfully downloaded to the destination in
     * {@link AttachmentColumns#DESTINATION}. If a provider later detects
     * that a download is missing, it should reset the state to
     * {@link #NOT_SAVED}. This state may not be used as a command on its
     * own. To move a file from cache to external, update
     * {@link AttachmentColumns#DESTINATION}.
     * <p>
     * Valid next states: {@link #NOT_SAVED}, {@link #PAUSED}
     */
	public static final int UIPROVIDER_ATTACHMENTSTATE_SAVED = 3;
	 /**
     * The most recent attachment download attempt failed. The current UI
     * design does not require providers to persist this state, but
     * providers must return this state at least once after a download
     * failure occurs. This state may not be used as a command.
     * <p>
     * Valid next states: {@link #DOWNLOADING}
     */
	public static final int UIPROVIDER_ATTACHMENTSTATE_FAILED = 1;
	
	/**
     * Values for the current state of a Folder/Account; note that it's possible that more than one
     * sync is in progress
     */
    /**
     * No sync in progress
     */
    public static final int UIPROVIDER_SYNCSTATUS_NO_SYNC = 0;
    /**
     * A user-requested sync/refresh is in progress. This occurs when the user taps on the
     * refresh icon in the action bar.
     */
    public static final int UIPROVIDER_SYNCSTATUS_USER_REFRESH = 1<<0;
    /**
     * A background sync is in progress. This happens on <b>no</b> user interaction.
     */
    public static final int UIPROVIDER_SYNCSTATUS_BACKGROUND_SYNC = 1<<2;
    /**
     * A user-requested live query is in progress. This occurs when the user goes past the end
     * of the fetched results in the conversation list.
     */
    public static final int UIPROVIDER_SYNCSTATUS_LIVE_QUERY = 1<<1;
    
    /**
     * Values for the result of the last attempted sync of a Folder/Account
     */
    /** The sync completed successfully */
    public static final int UIPROVIDER_LASTSYNCRESULT_SUCCESS = 0;
    /** The sync wasn't completed due to an authentication error */
    public static final int UIPROVIDER_LASTSYNCRESULT_AUTH_ERROR = 2;
    /** The sync wasn't completed due to a security error */
    public static final int UIPROVIDER_LASTSYNCRESULTAUTH_SECURITY_ERROR = 3;
    /** The sync wasn't completed due to a connection error */
    public static final int UIPROVIDER_LASTSYNCRESULTAUTH_CONNECTION_ERROR = 1;
    /** The sync wasn't completed due to an internal error/exception */
    public static final int UIPROVIDER_LASTSYNCRESULTAUTH_INTERNAL_ERROR = 5;

    public static int UIPROVIDER_LOCAL_SEARCH_ALL = 0;
    public static int UIPROVIDER_LOCAL_SEARCH_FROM = 1;
    public static int UIPROVIDER_LOCAL_SEARCH_TO = 2;
    public static int UIPROVIDER_LOCAL_SEARCH_SUBJECT = 3;

    public static final int UIPROVIDER_MESSAGEOPERATION_RESPOND_ACCEPT = 1;
    public static final int UIPROVIDER_MESSAGEOPERATION_RESPOND_TENTATIVE = 2;
    public static final int UIPROVIDER_MESSAGEOPERATION_RESPOND_DECLINE = 3;
    // TS: chao.zhang 2015-09-21 EMAIL FEATURE-585337 ADD_S
    //NOTE: EmailProvider#call will use it
    public static final String SHOW_FAILED_NOTIFICATION = "show_faild_notification";
    // TS: jian.xu 2015-11-26 EMAIL BUGFIX-944797 ADD_S
    public static final String CANCEL_FAILED_NOTIFICATION = "cancel_faild_notification";
    // TS: jian.xu 2015-11-26 EMAIL BUGFIX-944797 ADD_E
    // TS: chao.zhang 2015-09-21 EMAIL FEATURE-585337 ADD_E
    //TS: yanhua.chen 2015-1-20 EMAIL BUGFIX_1162996 ADD_S
    public static final String SHOW_EXCHANGE_CALENDARORCONTACTS_NOTIFICATION = "show_exchange_calendarOrContacts_notification";
    //TS: yanhua.chen 2015-1-20 EMAIL BUGFIX_1162996 ADD_E
    // TS: xiaolin.li 2015-01-13 EMAIL BUGFIX-883410 ADD_S
    /**
     * Values for the current state of a Folder/Account; note that it's possible that more than one
     * sync is in progress
     */
    public static final class SyncStatus {
        /**
         * No sync in progress
         */
        public static final int NO_SYNC = 0;
        /**
         * A user-requested sync/refresh is in progress. This occurs when the user taps on the
         * refresh icon in the action bar.
         */
        public static final int USER_REFRESH = 1<<0;
        /**
         * A user-requested live query is in progress. This occurs when the user goes past the end
         * of the fetched results in the conversation list.
         */
        public static final int LIVE_QUERY = 1<<1;
        /** Please use the constant {@link #LIVE_QUERY} instead. */
        @Deprecated
        public static final int USER_QUERY = 1<<1;
        /**
         * A background sync is in progress. This happens on <b>no</b> user interaction.
         */
        public static final int BACKGROUND_SYNC = 1<<2;
        /**
         * An initial sync is needed for this Account/Folder to be used. This is account-wide, when
         * the user has added an account, and the first sync has not completed successfully.
         */
        public static final int INITIAL_SYNC_NEEDED = 1<<3;
        /**
         * Manual sync is required. This is account-wide, when the user has disabled sync on the
         * Gmail account.
         */
        public static final int MANUAL_SYNC_REQUIRED = 1<<4;
        /**
         * Account initialization is required.
         */
        public static final int ACCOUNT_INITIALIZATION_REQUIRED = 1<<5;

        public static boolean isSyncInProgress(int syncStatus) {
            return 0 != (syncStatus & (BACKGROUND_SYNC |
                    USER_REFRESH |
                    LIVE_QUERY));
        }
    }
    // TS: xiaolin.li 2015-01-13 EMAIL BUGFIX-883410 ADD_E
}
