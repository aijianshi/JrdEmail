/*
 =================================================================================
 *HISTORY
 *
 *Tag            Date          Author          Description
 *============== ============ =============== ====================================
 *BUGFIX-844469  2014/12/10   zhaotianyong    Modify the provider loading method
 *BUGFIX-922404  2015/02/11   zhaotianyong    [TMO-EU][SSV]The configuration of email is not effective
 *BUGFIX-936272  2015/03/05   zhaotianyong    [America Movil][SSV]Add Email configuration in general setting.
 *BUGFIX-932385  2015/03/09   ke.ma           Phone does not give option to select predefined email account
 *BUGFIX-943810  2015/03/09   zhaotianyong    [TMO][SSV]Add AT and HR for SSV
 *BUGFIX-944442  2015/03/13   chaozhang    [Email]Can not login gmail account from Email
 *BUGFIX-951620  2015/03/17    ke.ma           [email]email feature requirements
 *BUGFIX-951782  2015/03/17    ke.ma           [Android5.0][Email]Undesired ISP display in the predefine-account.
 *BUGFIX-951620  2015/03/23    ke.ma           [email]email feature requirements
 *BUGFIX-955397  2015/03/25    ke.ma           [America][SSV]When enter into email,can't find "iClaro"display.
 *BUGFIX-985543  2015/04/28   zhaotianyong    [HOMO][Orange][04] 04-Sending-Reception - Gmail Account
 *BUGFIX-985156  2015/04/30   zheng.zou        [HOMO][Orange][17] Orange and Wanadoo email_incoming settings
 *BUGFIX-998426  2015/05/14    junwei-xu       [HOMO][HOMO][TMO ] T-Mobile FOC requirement on ranged product but our open marketing version.
 *BUGFIX-1000379  2015/05/15   zhaotianyong    Wrong proccess pressing next button in an email account setup
 *BUGFIX-969854  2015/05/26   junwei-xu       [HOMO][ALWE][E-Mail][Settings][Country-Profile][UI] Update needed for German e-mail provider Web.de and Gmx.de.
 *BUGFIX-1054500 2015/08/03   junwei-xu       [TMO-66505][FOC]It is not possible to set up any email account automatically via the setup wizard.
 *BUGFIX-1062845 2015/08/11   junwei-xu       [America][SSV] "def_Email_account_provider1_domain" is wrong when insert Puerto Rico card.
 *FEATURE-664213 2015/09/23   tao.gan         [Email]Idol4 ssv new plan
 =================================================================================
 */
package com.tct.mail.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

//import android.R;
import android.content.Context;
import android.content.res.Resources;
import android.util.Xml;

import com.tct.emailcommon.provider.Account;
import org.xmlpull.v1.*;

import com.tct.emailcommon.VendorPolicyLoader.Provider;

public class PLFUtils {
    private static final String TAG = "Utils";
    private static final String PATH = "/custpack/plf/Email/";
    private static final String FILE = "isdm_Email_defaults.xml";

    /** Pattern to match any part of a domain */
    private final static String WILD_STRING = "*";
    /** Will match any, single character */
    private final static char WILD_CHARACTER = '?';
    private final static String DOMAIN_SEPARATOR = "\\.";
    //TS: zhaotianyong 2015-04-28 EMAIL BUGFIX_985543 DEL_S
    //TS: chaozhang 2015-03-13 EMAIL BUGFIX_944442 ADD_S
//    private static final String GMAIL__SMTP_SERVER_URI = "smtp+tls+://smtp.gmail.com";
    //TS: chaozhang 2015-03-13 EMAIL BUGFIX_944442 ADD_E
    //TS: zhaotianyong 2015-04-28 EMAIL BUGFIX_985543 DEL_E

 // TS: tao.gan 2015-09-23 EMAIL FEATURE_664213 ADD_S
    //No ssv account find,indicate that we should do normal isp account configuration,
    //current sim card is not required ssv sim card
    public static final String NO_SSV_ACOUNT_FIND = "no ssv account find";
 // TS: tao.gan 2015-09-23 EMAIL FEATURE_664213 ADD_E

    /**
     * 1 indicate that we show the account preset, 0 indicate not show.
     */
    private static final String DISPLAY_ACCOUNT_PRESET = "1";
    /**
     * get isdm value which is bool
     *
     * @param mContext
     * @param def_name : the name of isdmID
     * @return
     */
    public static boolean getBoolean(Context mContext, String def_name) {
        Resources res = mContext.getResources();
        int id = res.getIdentifier(def_name, "bool", mContext.getPackageName());
        // get the native isdmID value
        boolean result = mContext.getResources().getBoolean(id);
        try {
            String bool_frameworks = getISDMString(new File(PATH + FILE), def_name, "bool");
            if (null != bool_frameworks) {
                result = Boolean.parseBoolean(bool_frameworks);
            }
        } catch (XmlPullParserException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }

    /**
     * get isdm value which is bool
     * 
     * @param mContext
     * @param def_name : the name of isdmID
     * @return
     */
    public static String getString(Context mContext, String def_name) {
        Resources res = mContext.getResources();
        int id = res.getIdentifier(def_name, "string", mContext.getPackageName());
        // get the native isdmID value
        String result = mContext.getResources().getString(id);
        try {
            String string_frameworks = getISDMString(new File(PATH + FILE), def_name, "string");
            if (null != string_frameworks) {
                result = string_frameworks;
            }
        } catch (XmlPullParserException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }

    /**
     * get isdm value which is integer
     *
     * @param mContext
     * @param def_name : the name of isdmID
     * @return
     */
    public static int getInteger(Context mContext, String def_name) {
        Resources res = mContext.getResources();
        int id = res.getIdentifier(def_name, "integer", mContext.getPackageName());
        // get the native isdmID value
        int result = mContext.getResources().getInteger(id);
        try {
            String bool_frameworks = getISDMString(new File(PATH + FILE), def_name, "integer");
            if (null != bool_frameworks) {
                result = Integer.parseInt(bool_frameworks);
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * parser the XML file to get the isdmID value
     * 
     * @param file : xml file
     * @param name : isdmID
     * @param type : isdmID type like bool and string
     * @return
     * @throws XmlPullParserException
     * @throws IOException
     */
    private static String getISDMString(File file, String name, String type)
            throws XmlPullParserException,
            IOException {
        if (!file.exists() || null == file) {
            return null;
        }
        String result = null;
        InputStream inputStream = new FileInputStream(file);
        XmlPullParser xmlParser = Xml.newPullParser();
        xmlParser.setInput(inputStream, "utf-8");

        int evtType = xmlParser.getEventType();
        boolean query_end = false;
        while (evtType != XmlPullParser.END_DOCUMENT && !query_end) {

            switch (evtType) {
                case XmlPullParser.START_TAG:

                    String start_tag = xmlParser.getAttributeValue(null, "name");
                    String start_type = xmlParser.getName();
                    if (null != start_tag && type.equals(start_type) && start_tag.equals(name)) {
                        result = xmlParser.nextText();
                        query_end = true;
                    }
                    break;

                case XmlPullParser.END_TAG:

                    break;

                default:
                    break;
            }
            // move to next node if not tail
            evtType = xmlParser.next();
        }
        inputStream.close();
        return result;
    }

    // TS: zhaotianyong 2014-12-10 EMAIL BUGFIX-844469 ADD_S
    private static ArrayList<Provider> getProviderList(Context context) {
        ArrayList<Provider> providerList = new ArrayList<Provider>();
        Provider provider[] = new Provider[25];
        for(int i=0;i<25;i++){
            int j = i + 1;
            provider[i]= new Provider();
            provider[i].id= getString(context,"def_email_account"+ j +"Id");
            provider[i].label = getString(context,"def_email_account"+ j +"LabelValue");
            provider[i].domain = getString(context,"def_email_account"+ j +"DomainValue");
            provider[i].relogin = getString(context,"def_email_account"+ j +"ReloginValue");
            provider[i].incomingUriTemplate = getString(context,"def_email_account"+ j +"IncomingServerValue");
            provider[i].incomingUsernameTemplate = getString(context,"def_email_account"+ j +"UsernameValue");
            provider[i].outgoingUriTemplate = getString(context,"def_email_account"+ j +"OutgoingServerValue");
            provider[i].outgoingUsernameTemplate = getString(context,"def_email_account"+ j +"UsernameValue");
            provider[i].display = getString(context, "def_email_account"+ j + "DisplayValue");
            providerList.add(provider[i]);
        }
        return providerList;
    }

    private static ArrayList<Provider> getDisplayProviderList(Context context) {
        ArrayList<Provider> providerListShow = new ArrayList<Provider>();

        //current provider list for account preset
        ArrayList<Provider> providerListCurrent = getProviderList(context);

        for(Provider provider : providerListCurrent) {
            if(provider.display.trim().equals(DISPLAY_ACCOUNT_PRESET)) {
                providerListShow.add(provider);
            }
        }
        return providerListShow;
    }

    public static Provider findProviderForDomain(Context context, String domain){
        ArrayList<Provider> providerList = getProviderList(context);
        Provider provider =null;
        for (Provider providerTemp:providerList){
            if(matchProvider(domain, providerTemp.domain)){
                provider = providerTemp;
                //TS: zhaotianyong 2015-04-28 EMAIL BUGFIX_985543 DEL_S
                //TS: chaozhang 2015-03-13 EMAIL BUGFIX_902637 ADD_S
                //For gmail imap account,can't login success with ssl+465,must use tls+857.
                //Here to avoid wrong perso.
//                if (domain != null && TextUtils.equals(domain.toLowerCase(), "gmail.com")) {
//                    provider.outgoingUriTemplate = GMAIL__SMTP_SERVER_URI;
//                }
                //TS: chaozhang 2015-03-13 EMAIL BUGFIX_902637 ADD_E
                //TS: zhaotianyong 2015-04-28 EMAIL BUGFIX_985543 DEL_E
                break;
            }
        }
        return provider;
    }

    public static Provider findProviderForDomain(Context context, String domain, String label){
        ArrayList<Provider> providerList = getProviderList(context);
        Provider provider =null;
        for(Provider providerTemp:providerList){
            if(matchProvider(domain, providerTemp.domain)&&label.equalsIgnoreCase(providerTemp.label)){
                provider = providerTemp;
                break;
            }
        }
        return provider;
    }

    //TS: junwei-xu 2015-05-26 EMAIL BUGFIX-969854 ADD_S
    public static Provider findProviderForDomainAndProtocol(Context context, String domain, String protocol) throws URISyntaxException {
        ArrayList<Provider> providerList = getProviderList(context);
        Provider provider = null;
        for(Provider accountProvider : providerList) {
            URI uri = new URI(accountProvider.incomingUriTemplate);
            String[] schemeParts = uri.getScheme().split("\\+");
            String protocolTemp = schemeParts[0];
            if(matchProvider(domain, accountProvider.domain) && protocol.equals(protocolTemp)){
                provider = accountProvider;
                break;
            }
        }
        return provider;
    }

    private static boolean matchProvider(String testDomain, String providerDomain) {
        String[] testParts = testDomain.split(DOMAIN_SEPARATOR);
        String[] providerParts = providerDomain.split(DOMAIN_SEPARATOR);
        if (testParts.length != providerParts.length) {
            return false;
        }
        for (int i = 0; i < testParts.length; i++) {
            String testPart = testParts[i].toLowerCase();
            String providerPart = providerParts[i].toLowerCase();
            if (!providerPart.equals(WILD_STRING) &&
                    !matchWithWildcards(testPart, providerPart)) {
                return false;
            }
        }
        return true;
    }

    private static boolean matchWithWildcards(String testPart, String providerPart) {
        int providerLength = providerPart.length();
        if (testPart.length() != providerLength){
            return false;
        }
        for (int i = 0; i < providerLength; i++) {
            char testChar = testPart.charAt(i);
            char providerChar = providerPart.charAt(i);
            if (testChar != providerChar && providerChar != WILD_CHARACTER) {
                return false;
            }
        }
        return true;
    }
    // TS: zhaotianyong 2014-12-10 EMAIL BUGFIX-844469 ADD_E

    //TS: ke.ma 2015-03-09 EMAIL BUGFIX-932385 ADD_S
    public static StringBuffer[] getDomainsFromISDM(Context context){
        //TS: ke.ma 2015-03-23 EMAIL BUGFIX-951620 951782 MOD_S
        ArrayList<Provider> providers=getDisplayProviderList(context);
        //TS: ke.ma 2015-03-23 EMAIL BUGFIX-951620 951782 MOD_E
        //TS: ke.ma 2015-03-17 EMAIL BUGFIX-951620 951782 MOD_S
        StringBuffer[] domains=new StringBuffer[providers.size()+5];
        //TS: ke.ma 2015-03-17 EMAIL BUGFIX-951620 951782 MOD_E

        //TS: ke.ma 2015-03-23 EMAIL BUGFIX-951620 951782 MOD_S
        if(providers.size()!=0){
            for(int i=0;i<providers.size();i++){
                domains[i]=new StringBuffer("@".concat(providers.get(i).domain));
           }
        }
        //TS: ke.ma 2015-03-23 EMAIL BUGFIX-951620 951782 MOD_E

        //TS: ke.ma 2015-03-17 EMAIL BUGFIX-951620 951782 MOD_S
        domains[providers.size()]=new StringBuffer("@outlook.com");
        domains[providers.size()+1]=new StringBuffer("@yahoo.com");
        domains[providers.size()+2]=new StringBuffer("@msn.com");
        domains[providers.size()+3]=new StringBuffer("@hotmail.com");
        domains[providers.size()+4]=new StringBuffer("@gmail.com");
        //TS: ke.ma 2015-03-17 EMAIL BUGFIX-951620 951782 MOD_E
        return domains;
    }


    //TS: zheng.zou 2015-4-30 EMAIL BUGFIX-985156, ADD_S
    public static int getCustomPopDeletePolicy(Context context, int defaultValue){
        String customDeletePolicy = getString(context, "def_email_popAccountDefaultDeletePolicy");
        int deletePolicy;
        if (customDeletePolicy.equals("0")) {
            deletePolicy = defaultValue;
        } else if (customDeletePolicy.equals("1")) {
            deletePolicy = Account.DELETE_POLICY_NEVER;
        } else if (customDeletePolicy.equals("2")) {
            deletePolicy = Account.DELETE_POLICY_ON_DELETE;
        } else {
            deletePolicy = defaultValue;
        }
        return deletePolicy;
    }
    //TS: zheng.zou 2015-4-30 EMAIL BUGFIX-985156, ADD_E

 // TS: tao.gan 2015-09-23 EMAIL FEATURE_664213 ADD_S
    /*
     * Find the preset account which match the domain user input
     */
    public static Provider findSSVProviderForDomain(Context context, String domain, String protocol) throws URISyntaxException {
        Provider ssvProvider = null;
        ArrayList<Provider> ssvProviderList = getSSVProviderList(context);
        //No ssv account find,indicate that we should do normal isp account configuration,
        //current sim card is not required ssv sim card
        if(ssvProviderList.size() == 0) {
            ssvProvider = new Provider();
            ssvProvider.domain = NO_SSV_ACOUNT_FIND;
            return ssvProvider;
        }
        for(Provider tempProvider : ssvProviderList) {
            if (protocol == null) {
                if(matchProvider(domain, tempProvider.domain)) {
                    ssvProvider = tempProvider;
                    LogUtils.i(TAG, "The account " + domain + " is matched");
                }
            } else {
                URI uri = new URI(tempProvider.incomingUriTemplate);
                String[] schemeParts = uri.getScheme().split("\\+");
                String protocolTemp = schemeParts[0];
                if (matchProvider(domain, tempProvider.domain) && protocol.equals(protocolTemp)) {
                    ssvProvider = tempProvider;
                    LogUtils.i(TAG, "The account " + domain + " protocol : " + protocolTemp + " is matched");
                }
            }
        }
        return ssvProvider;
    }

    /*
     * Get all the ssv acount which is personalized customized for one ssv sim card
     */
    public static ArrayList<Provider> getSSVProviderList(Context context) {
        ArrayList<Provider> ssvProviderList = new ArrayList<Provider> ();
        String num = getString(context, "feature_email_ssv_account_number_each");
        int count = 25;
        try {
            count = Integer.parseInt(num);
            if(count > 25) {
                //count can not exceed 25.
                count = 25;
            }
        } catch(NumberFormatException e) {
            LogUtils.e(TAG,"NumberFormatException while format the string : "+num);
        }
        LogUtils.i(TAG,"The total counts need to config for current sim card is " + count);
        for (int i =0 ;i<count;i++) {
            Provider provider = new Provider();
            //The order start from 1 to counts
            int j = i+1;
            //Get the string like def_email_accountXXX, such as"def_email_account1DomainValue",we cut it for convenience coding.
            provider.id= getString(context,"def_email_account"+ j +"Id");
            provider.label = getString(context,"def_email_account"+ j +"LabelValue");
            provider.domain = getString(context,"def_email_account"+ j +"DomainValue");
            provider.relogin = getString(context,"def_email_account"+ j +"ReloginValue");
            provider.incomingUriTemplate = getString(context,"def_email_account"+ j +"IncomingServerValue");
            provider.incomingUsernameTemplate = getString(context,"def_email_account"+ j +"UsernameValue");
            provider.outgoingUriTemplate = getString(context,"def_email_account"+ j +"OutgoingServerValue");
            provider.outgoingUsernameTemplate = getString(context,"def_email_account"+ j +"UsernameValue");
            ssvProviderList.add(provider);
        }
        return ssvProviderList;
    }
 // TS: tao.gan 2015-09-23 EMAIL FEATURE_664213 ADD_E
}
