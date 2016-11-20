/*
 ==========================================================================
 *HISTORY
 *
 *Tag            Date         Author          Description
 *============== ============ =============== ==============================
 *BUGFIX-539892  2015-09-01   zheng.zou       CR:in email list view, group email with time range and show time range in column
 *BUGFIX-571175  2015-09-18   jing.dong       [Android L][Email][Monitor]Some mails are classfied in the wrong time label
 ============================================================================
 */
package com.tct.mail.utils;

import java.util.Calendar;

/**
 * Created by user on 15-8-31.
 * time utility class
 */
public class TimeUtils {

    /**
     * get the time stamp of 0 clock of today
     *
     * @return time stamp
     */
    public static long getDayStartTime() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis() ;
    }



    /**
     * get the time stamp of 0 clock of the fist day of this month
     *
     * @return time stamp
     */
    public static long getMonthStartTime() {
        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), 1, 0, 0, 0);  //TS: jin.dong 2015-09-18 EMAIL BUGFIX-571175 MOD
        return cal.getTimeInMillis();
    }

    /**
     * get the time stamp of 0 clock of the first day of this year
     *
     * @return time stamp
     */
    public static long getYearStartTime() {
        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR), Calendar.JANUARY, 1, 0, 0, 0);   //TS: jin.dong 2015-09-18 EMAIL BUGFIX-571175 MOD
        return cal.getTimeInMillis();
    }
}
