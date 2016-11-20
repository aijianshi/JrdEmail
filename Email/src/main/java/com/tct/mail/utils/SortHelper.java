package com.tct.mail.utils;

import android.content.Context;
import android.text.TextUtils;

import com.tct.emailcommon.provider.EmailContent.MessageColumns;

/**
 * Created by user on 16-1-5.
 */
public class SortHelper {

    public static final int SORT_BY_DATE = -1;
    public static final int SORT_BY_DATE_OLDEST = 0;
    public static final int SORT_BY_FROM = 1;
    public static final int SORT_BY_FROM_Z_A = 2;
    public static final int SORT_BY_ATTACHMENTS = 3;
    public static final int SORT_BY_IMPORTANCE = 4;
    public static final int SORT_BY_SIZE = 5;
    public static final int SORT_BY_UNREAD = 6;
    public static final int SORT_BY_READ = 7;
    public static final int SORT_BY_FLAGGED = 8;


    private static final String SQL_ORDER_BY_DATE = MessageColumns.TIMESTAMP + " DESC";
    private static final String SQL_ORDER_BY_DATE_OLDEST = MessageColumns.TIMESTAMP + " ASC";
    private static final String SQL_ORDER_BY_FROM = MessageColumns.DISPLAY_NAME + " COLLATE LOCALIZED ASC"
            + " , " + MessageColumns.TIMESTAMP + " DESC";
    private static final String SQL_ORDER_BY_FROM_Z_A = MessageColumns.DISPLAY_NAME + " COLLATE LOCALIZED DESC"
            + " , " + MessageColumns.TIMESTAMP + " DESC";
    private static final String SQL_ORDER_BY_UNREAD = MessageColumns.FLAG_READ + " ASC"
            + " , " + MessageColumns.TIMESTAMP + " DESC";
    private static final String SQL_ORDER_BY_READ = MessageColumns.FLAG_READ + " DESC"
            + " , " + MessageColumns.TIMESTAMP + " DESC";
    private static final String SQL_ORDER_BY_SIZE = MessageColumns.MESSAGE_SIZE + " DESC"
            + " , " + MessageColumns.TIMESTAMP + " DESC";
    private static final String SQL_ORDER_BY_FLAGGED = MessageColumns.FLAG_FAVORITE + " DESC"
            + " , " + MessageColumns.TIMESTAMP + " DESC";
    private static final String SQL_ORDER_BY_ATTACHMENTS = MessageColumns.FLAG_ATTACHMENT + " DESC"
            + " , " + MessageColumns.TIMESTAMP + " DESC";
    private static final String SQL_ORDER_BY_IMPORTANCE = MessageColumns.FLAG_PRIORITY + " ASC"
            + " , " + MessageColumns.TIMESTAMP + " DESC";

    public static final String DEFAUT_ORDER_SQL = SQL_ORDER_BY_DATE;
    private static final int DEFAULT_SORT_ORDER = SORT_BY_DATE;
    private static boolean sEnableSort = false;
    private static int sCurrentSort = SORT_BY_DATE;


    public static String getSortOrderSqlByTypeStr(String orderType) {
        if (TextUtils.isEmpty(orderType)) {
            return DEFAUT_ORDER_SQL;
        }
        int order = Integer.valueOf(orderType);
        return getSortOrderSqlByTypeInt(order);
    }

    public static String getSortOrderSqlByTypeInt(int order) {
        switch (order) {
            case SORT_BY_DATE:
                return SQL_ORDER_BY_DATE;
            case SORT_BY_DATE_OLDEST:
                return SQL_ORDER_BY_DATE_OLDEST;
            case SORT_BY_FROM:
                return SQL_ORDER_BY_FROM;
            case SORT_BY_FROM_Z_A:
                return SQL_ORDER_BY_FROM_Z_A;
            case SORT_BY_UNREAD:
                return SQL_ORDER_BY_UNREAD;
            case SORT_BY_SIZE:
                return SQL_ORDER_BY_SIZE;
            case SORT_BY_READ:
                return SQL_ORDER_BY_READ;
            case SORT_BY_FLAGGED:
                return SQL_ORDER_BY_FLAGGED;
            case SORT_BY_ATTACHMENTS:
                return SQL_ORDER_BY_ATTACHMENTS;
            case SORT_BY_IMPORTANCE:
                return SQL_ORDER_BY_IMPORTANCE;
            default:
                return SQL_ORDER_BY_DATE;
        }
    }

    public static boolean isTimeOrder(int order){
        return order == SORT_BY_DATE ;
    }


    public static int getDefaultOrder(){
        return DEFAULT_SORT_ORDER;
    }

    public static boolean isSortEnabled(){
        return sEnableSort;
    }

    public static void setCurrentSort(int sort){
        sCurrentSort = sort;
    }

    public static int getCurrentSort(){
        return sCurrentSort;
    }


    public static void loadPlfSetting(Context context){
        sEnableSort =  PLFUtils.getBoolean(context,"def_enable_email_message_list_sorting");
    }

    public static void resetCurrentOrder(){
        sCurrentSort = DEFAULT_SORT_ORDER;
    }


}
