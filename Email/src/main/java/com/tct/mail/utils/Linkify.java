/******************************************************************************/
/*                                                               Date:06/2014 */
/*                                PRESENTATION                                */
/*                                                                            */
/*       Copyright 2014 TCL Communication Technology Holdings Limited.        */
/*                                                                            */
/* This material is company confidential, cannot be reproduced in any form    */
/* without the written permission of TCL Communication Technology Holdings    */
/* Limited.                                                                   */
/*                                                                            */
/* -------------------------------------------------------------------------- */
/*  Author :  zhangyang                                                       */
/*  Email  :                                                                  */
/*  Role   :                                                                  */
/*  Reference documents :                                                     */
/* -------------------------------------------------------------------------- */
/*  Comments :                                                                */
/*  File     :                                                                */
/*  Labels   :                                                                */
/* -------------------------------------------------------------------------- */
/* ========================================================================== */
/*     Modifications on Features list / Changes Request / Problems Report     */
/* -------------------------------------------------------------------------- */
/*    date   |        author        |         Key          |     comment      */
/* ----------|----------------------|----------------------|----------------- */
/* 06/07/2014|      zhangyang       |        677772        |    [Email]URL add- */
/*           |                      |                      |ress can not be   */
/*           |                      |                      |distinguished in  */
/*           |                      |                      |mail              */
/* ----------|----------------------|----------------------|----------------- */
/******************************************************************************/
/*
 =========================================================================================================
 *HISTORY
 *
 *Tag            Date         Author          Description
 *============== ============ =============== ============================================================
 *BUGFIX-850554  2014/11/28   zhaotianyong    [Android5.0][Email] Email address display wrong in mail body.
 *BUGFIX-936928  2015/03/06   zheng.zou       [Email]Number and E-mail address can not be displayed as a link
 *BUGFIX-936928  2015/03/17   zheng.zou       [Email]Number and E-mail address can not be displayed as a link
 *BUGFIX-954737  2015/03/24   zheng.zou       [Android5.0][Email] Click a phone number in mail. '+' can not be added to dialer automatically.
 *BUGFIX-305594  2015/06/18   xujian          [Email]The address displays abnormally when tap the hyperlink of Email address in mail
 *BUGFIX-957636  2015/07/03   Gantao          [Android5.0][Email]Can't show pictures in eml file
 *BUGFIX-1552862 2016/02/19   jin.dong        [Emial]Switch to the tracking status webpage fail
 *BUGFIX-1821080 2016/03/22   xiangnan.zhou   [Force Colse][GAPP][Email v5.2.10.3.0213.0_0302]Open the email,prompt Unfortunately,Email has stopped
 =========================================================================================================
 */

package com.tct.mail.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Linkify {

    //TS: zheng.zou 2015-03-24 EMAIL BUGFIX_954737 MOD_S
    //TS: zheng.zou 2015-03-17 EMAIL BUGFIX_936928 MOD_S
     public static final Pattern PHONE
     = Pattern.compile(                                  // sdd = space, or dash
             "((\\+|(&#43;))[0-9]+[\\- ]*)?"                    // +<digits><sdd>*
             + "(\\([0-9]+\\)[\\- ]*)?"               // (<digits>)<sdd>*
             + "([0-9][0-9\\- ][0-9\\- ]+[0-9])"); // <digit><digit|sdd>+<digit>
    //TS: jin.dong 2016-02-19 EMAIL BUGFIX_1552862 MOD_S
    private static final Pattern PATTERN_HREF = Pattern.compile("(?<=href=\")[\\s\\S]+?(?=</)|(http|www)[\\s\\S]+?(?=<|\")");
    //TS: jin.dong 2016-02-19 EMAIL BUGFIX_1552862 MOD_E
    //TS: zheng.zou 2015-03-17 EMAIL BUGFIX_936928 MOD_E
    //TS: zheng.zou 2015-03-24 EMAIL BUGFIX_954737 MOD_E


  public static final Pattern EMAIL_ADDRESS
  = Pattern.compile(
      "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
      //TS: zheng.zou 2015-03-06 EMAIL BUGFIX_936928 MOD_S
      // TS: zhaotianyong 2014-11-28 EMAIL BUGFIX_850554 MOD_S
      "(\\@" +
      "|(&#64;))" +
      // TS: zhaotianyong 2014-11-28 EMAIL BUGFIX_850554 MOD_E
      //TS: zheng.zou 2015-03-06 EMAIL BUGFIX_936928 MOD_E
      "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
      "(" +
          "\\." +
          "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
      ")+"
  );

  public static final String GOOD_IRI_CHAR =
      "a-zA-Z0-9\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF";

  public static final String TOP_LEVEL_DOMAIN_STR_FOR_WEB_URL =
      "(?:"
      + "(?:aero|arpa|asia|a[cdefgilmnoqrstuwxz])"
      + "|(?:biz|b[abdefghijmnorstvwyz])"
      + "|(?:cat|com|coop|c[acdfghiklmnoruvxyz])"
      + "|d[ejkmoz]"
      + "|(?:edu|e[cegrstu])"
      + "|f[ijkmor]"
      + "|(?:gov|g[abdefghilmnpqrstuwy])"
      + "|h[kmnrtu]"
      + "|(?:info|int|i[delmnoqrst])"
      + "|(?:jobs|j[emop])"
      + "|k[eghimnprwyz]"
      + "|l[abcikrstuvy]"
      + "|(?:mil|mobi|museum|m[acdeghklmnopqrstuvwxyz])"
      + "|(?:name|net|n[acefgilopruz])"
      + "|(?:org|om)"
      + "|(?:pro|p[aefghklmnrstwy])"
      + "|qa"
      + "|r[eosuw]"
      + "|s[abcdeghijklmnortuvyz]"
      + "|(?:tel|travel|t[cdfghjklmnoprtvwz])"
      + "|u[agksyz]"
      + "|v[aceginu]"
      + "|w[fs]"
      + "|(?:\u03b4\u03bf\u03ba\u03b9\u03bc\u03ae|\u0438\u0441\u043f\u044b\u0442\u0430\u043d\u0438\u0435|\u0440\u0444|\u0441\u0440\u0431|\u05d8\u05e2\u05e1\u05d8|\u0622\u0632\u0645\u0627\u06cc\u0634\u06cc|\u0625\u062e\u062a\u0628\u0627\u0631|\u0627\u0644\u0627\u0631\u062f\u0646|\u0627\u0644\u062c\u0632\u0627\u0626\u0631|\u0627\u0644\u0633\u0639\u0648\u062f\u064a\u0629|\u0627\u0644\u0645\u063a\u0631\u0628|\u0627\u0645\u0627\u0631\u0627\u062a|\u0628\u06be\u0627\u0631\u062a|\u062a\u0648\u0646\u0633|\u0633\u0648\u0631\u064a\u0629|\u0641\u0644\u0633\u0637\u064a\u0646|\u0642\u0637\u0631|\u0645\u0635\u0631|\u092a\u0930\u0940\u0915\u094d\u0937\u093e|\u092d\u093e\u0930\u0924|\u09ad\u09be\u09b0\u09a4|\u0a2d\u0a3e\u0a30\u0a24|\u0aad\u0abe\u0ab0\u0aa4|\u0b87\u0ba8\u0bcd\u0ba4\u0bbf\u0baf\u0bbe|\u0b87\u0bb2\u0b99\u0bcd\u0b95\u0bc8|\u0b9a\u0bbf\u0b99\u0bcd\u0b95\u0baa\u0bcd\u0baa\u0bc2\u0bb0\u0bcd|\u0baa\u0bb0\u0bbf\u0b9f\u0bcd\u0b9a\u0bc8|\u0c2d\u0c3e\u0c30\u0c24\u0c4d|\u0dbd\u0d82\u0d9a\u0dcf|\u0e44\u0e17\u0e22|\u30c6\u30b9\u30c8|\u4e2d\u56fd|\u4e2d\u570b|\u53f0\u6e7e|\u53f0\u7063|\u65b0\u52a0\u5761|\u6d4b\u8bd5|\u6e2c\u8a66|\u9999\u6e2f|\ud14c\uc2a4\ud2b8|\ud55c\uad6d|xn\\-\\-0zwm56d|xn\\-\\-11b5bs3a9aj6g|xn\\-\\-3e0b707e|xn\\-\\-45brj9c|xn\\-\\-80akhbyknj4f|xn\\-\\-90a3ac|xn\\-\\-9t4b11yi5a|xn\\-\\-clchc0ea0b2g2a9gcd|xn\\-\\-deba0ad|xn\\-\\-fiqs8s|xn\\-\\-fiqz9s|xn\\-\\-fpcrj9c3d|xn\\-\\-fzc2c9e2c|xn\\-\\-g6w251d|xn\\-\\-gecrj9c|xn\\-\\-h2brj9c|xn\\-\\-hgbk6aj7f53bba|xn\\-\\-hlcj6aya9esc7a|xn\\-\\-j6w193g|xn\\-\\-jxalpdlp|xn\\-\\-kgbechtv|xn\\-\\-kprw13d|xn\\-\\-kpry57d|xn\\-\\-lgbbat1ad8j|xn\\-\\-mgbaam7a8h|xn\\-\\-mgbayh7gpa|xn\\-\\-mgbbh1a71e|xn\\-\\-mgbc0a9azcg|xn\\-\\-mgberp4a5d4ar|xn\\-\\-o3cw4h|xn\\-\\-ogbpf8fl|xn\\-\\-p1ai|xn\\-\\-pgbs0dh|xn\\-\\-s9brj9c|xn\\-\\-wgbh1c|xn\\-\\-wgbl6a|xn\\-\\-xkc2al3hye2a|xn\\-\\-xkc2dl3a5ee0h|xn\\-\\-yfro4i67o|xn\\-\\-ygbi2ammx|xn\\-\\-zckzah|xxx)"
      + "|y[et]"
      + "|z[amw]))";

  public static final Pattern WEB_URL = Pattern.compile(
            "((?:(http|https|Http|Https|rtsp|Rtsp):\\/\\/(?:(?:[a-zA-Z0-9\\$\\-\\_\\.\\+\\!\\*\\'\\(\\)"
            + "\\,\\;\\?\\&\\=]|(?:\\%[a-fA-F0-9]{2})){1,64}(?:\\:(?:[a-zA-Z0-9\\$\\-\\_"
            + "\\.\\+\\!\\*\\'\\(\\)\\,\\;\\?\\&\\=]|(?:\\%[a-fA-F0-9]{2})){1,25})?\\@)?)?"
            + "((?:(?:[" + GOOD_IRI_CHAR + "][" + GOOD_IRI_CHAR + "\\-]{0,64}\\.)+"   // named host
            + TOP_LEVEL_DOMAIN_STR_FOR_WEB_URL
            + "|(?:(?:25[0-5]|2[0-4]" // or ip address
            + "[0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9])\\.(?:25[0-5]|2[0-4][0-9]"
            + "|[0-1][0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(?:25[0-5]|2[0-4][0-9]|[0-1]"
            + "[0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(?:25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}"
            + "|[1-9][0-9]|[0-9])))"
            + "(?:\\:\\d{1,5})?)" // plus option port number
            + "(\\/(?:(?:[" + GOOD_IRI_CHAR + "\\;\\/\\?\\:\\@\\&\\=\\#\\~"  // plus option query params
            + "\\-\\.\\+\\!\\*\\'\\(\\)\\,\\_])|(?:\\%[a-fA-F0-9]{2}))*)?"
            + "(?:\\b|$)", Pattern.CASE_INSENSITIVE);

  private static final int PHONE_NUMBER_MINIMUM_DIGITS = 7;
  //[BUGFIX]-Add-BEGIN by TCTNB.fu.zhang,02/09/2014,597649,
  //[EMAIL]Some URL can't be recognized
  // Regex that matches Web URL protocol part as case insensitive.
  private static final Pattern WEB_URL_PROTOCOL = Pattern.compile("(?i)http|https://");
       public static String addLinks(String test) {
              ArrayList<Tem> list = new  ArrayList<Tem>();
           StringBuffer bf = new StringBuffer();
           //TS: zheng.zou 2015-03-06 EMAIL BUGFIX_936928 DEL_S
           //note: do not match urls
//           Matcher mWeb = WEB_URL.matcher(test);
//              while(mWeb.find())
//              {
//                  int start = mWeb.start();
//                  int end = mWeb.end();
//                  String web = mWeb.group();
//                  /*
//                   * WEB_URL may match domain part of email address. To detect
//                   * this false match, the character just before the matched string
//                   * should not be '@'.
//                   */
//                  if (start == 0 || test.charAt(start - 1) != '@') {
//                      String link;
//                      Matcher proto = WEB_URL_PROTOCOL.matcher(web);
//
//                      if (proto.find()) {
//                          // This is work around to force URL protocol part be lower case,
//                          // because WebView could follow only lower case protocol link.
//                          link = proto.group().toLowerCase() + web.substring(proto.end());
//                      } else {
//                          // Patterns.WEB_URL matches URL without protocol part,
//                          // so added default protocol to link.
//                          link = "http://" + web;
//                      }
//                  String href = String.format("<a href='%s' style='color:#3333ff'>%s</a>",link,web);
//                  Tem t = new Tem(start,end,href);
//                  list.add(t);
//                  }
//                  //[BUGFIX]-Add-END by TCTNB.fu.zhang
//              }
           //TS: zheng.zou 2015-03-06 EMAIL BUGFIX_936928 DEL_E
           //TS: zheng.zou 2015-03-17 EMAIL BUGFIX_936928 ADD_S
           List<Tem> linksWithNumber = new ArrayList<>();
           Matcher hrefMatcher = PATTERN_HREF.matcher(test);
           while (hrefMatcher.find()){
               String link = hrefMatcher.group();
               if (PHONE.matcher(link).find()){
                   int start = hrefMatcher.start();
                   int end = hrefMatcher.end();
                   Tem tem = new Tem(start,end,link);
                   linksWithNumber.add(tem);
               }
           }
           //TS: zheng.zou 2015-03-17 EMAIL BUGFIX_936928 ADD_E
              Matcher mEmail = EMAIL_ADDRESS.matcher(test);
              while(mEmail.find())
              {
                  int start = mEmail.start();
                  int end = mEmail.end();
                  String email = mEmail.group();
                //TS: xujian 2015-06-18 EMAIL BUGFIX_305594 MOD_S
                  if(checkIfHasLink(start, test) && !checkIfCid(start, test)){//TS: Gantao 2015-07-03 EMAIL BUGFIX_957636 MOD
                      String href = String.format("<a href='mailto:%s' style='color:#3333ff'>%s</a>",email,email);
                      Tem t = new Tem(start,end,href);
                      list.add(t);
                  }
                //TS: xujian 2015-06-18 EMAIL BUGFIX_305594 MOD_E
             }
              Matcher mPhone = PHONE.matcher(test);
              while(mPhone.find())
              {
                  int start = mPhone.start();
                  int end = mPhone.end();
                  String phone = mPhone.group();
                  //TS: zheng.zou 2015-03-06 EMAIL BUGFIX_936928 MOD_S
                  if(acceptMatch(test,start,end) && !isInsideLink(linksWithNumber,start,end)){  //TS: zheng.zou 2015-03-17 EMAIL BUGFIX_936928 MOD
                  //TS: zheng.zou 2015-03-06 EMAIL BUGFIX_936928 MOD_E
                      String href = String.format("<a href='tel:%s' style='color:#3333ff'>%s</a>",phone,phone);
                      Tem t = new Tem(start,end,href);
                      list.add(t);
                  }
              }
              pruneOverlaps(list);
              Tem avove = null;
              for (Tem t : list) {
                  if (avove == null) {
                      bf.append(test.substring(0, t.start)).append(t.url);
                      avove = t;
                  } else {
                      //TS: xiangnan.zhou 2016-03-22 EMAIL BUGFIX-1821080 MOD_S
                      try {
                          bf.append(test.substring(avove.end, t.start)).append(t.url);
                      } catch (StringIndexOutOfBoundsException e) {
                          LogUtils.e(LogUtils.TAG, e, "happened StringIndexOutOfBoundsException in addLike().");
                      }
                      //TS: xiangnan.zhou 2016-03-22 EMAIL BUGFIX-1821080 MOD_E
                      avove = t;
                  }
             }
             if (avove != null)
                  bf.append(test.substring(avove.end));
             else
                  bf.append(test);
            return bf.toString();
       }
     //TS: xujian 2015-06-18 EMAIL BUGFIX_305594 MOD_S
       private static boolean checkIfHasLink(int start, String text) {
           //<a href="mailto:testmail&#64;tcl-mobile.com">testmail</a>
           //used to avoid to add link for above data in the html content
           int loc = text.lastIndexOf("href=\"mailto:", start);
           if(loc == -1 ){
               return true;
           }
           if(start == loc + "href=\"mailto:".length() ){
               return false;
           }
           else if (start > loc + "href=\"mailto:".length()) {
               for(int i = loc + "href=\"mailto:".length() ; i< start; i++){
                   if( !Character.isWhitespace(text.charAt(i))){
                       return true;
                   }
               }
               return false;
           }
           return true;
       }
     //TS: xujian 2015-06-18 EMAIL BUGFIX_305594 MOD_E

       //TS: Gantao 2015-07-03 EMAIL BUGFIX_957636 ADD_S
       /**
        * Some times the inline picture's cid match the pattern EMAIL_ADDRESS,we shouldn't
        * add hyper link for this.
        * @param start  first index of the cid string
        * @param text  mail bodyhtml
        * @return true if it's cid string
        */
       private static boolean checkIfCid(int start, String text) {
           int loc = text.indexOf("src=\"cid:");
           if (loc == -1 || start < 9) {
               return false;
           }
           if (text.substring(start-9, start).equals("src=\"cid:")){
               return true;
           }
           return false;
       }
       //TS: Gantao 2015-07-03 EMAIL BUGFIX_957636 ADD_E

       public final static boolean acceptMatch(CharSequence s, int start, int end) {
           int digitCount = 0;
           for (int i = start; i < end; i++) {
              if (Character.isDigit(s.charAt(i))) {
                  digitCount++;
                  if (digitCount >= PHONE_NUMBER_MINIMUM_DIGITS) {
                     return true;
                 }
            }
        }
       return false;
    }

    //TS: zheng.zou 2015-03-17 EMAIL BUGFIX_936928 ADD_S
    private static boolean isInsideLink(List<Tem> linksWithNum ,int start,int end){
        if (!linksWithNum.isEmpty()){
            for (Tem linkTem:linksWithNum){
               if (linkTem.start <=start && linkTem.end >=end){
                   return true;
               }
            }
        }
        return false;
    }
    //TS: zheng.zou 2015-03-17 EMAIL BUGFIX_936928 ADD_E

    private static final void pruneOverlaps(ArrayList<Tem> links) {
        Comparator<Tem>  c = new Comparator<Tem>() {
            public final int compare(Tem a, Tem b) {
                if (a.start < b.start) {
                    return -1;
                }

                if (a.start > b.start) {
                    return 1;
                }

                if (a.end < b.end) {
                    return 1;
                }

                if (a.end > b.end) {
                    return -1;
                }

                return 0;
            }

            public final boolean equals(Object o) {
                return false;
            }
        };

        Collections.sort(links, c);

        int len = links.size();
        int i = 0;

        while (i < len - 1) {
             Tem a = links.get(i);
             Tem b = links.get(i + 1);
            int remove = -1;

            if ((a.start <= b.start) && (a.end > b.start)) {
                if (b.end <= a.end) {
                    remove = i + 1;
                } else if ((a.end - a.start) > (b.end - b.start)) {
                    remove = i + 1;
                } else if ((a.end - a.start) < (b.end - b.start)) {
                    remove = i;
                }
                if (remove != -1) {
                    links.remove(remove);
                    len--;
                    continue;
                }
            }
            i++;
        }
    }


}
class Tem
{
        int start;
        int end;
        String url;
        public Tem(int start,int end,String url)
        {
             this.start=start;
             this.end=end;
             this.url=url;
        }
}
