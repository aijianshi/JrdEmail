/*
 ==========================================================================
 *HISTORY
 *
 *Tag            Date         Author        Description
 *============== ============ =============== ==============================
 *BUGFIX-932385  2015/03/09   ke.ma           Phone does not give option to select predefined email account
 *BUGFIX-954524  2015/03/23   ke.ma           [Android5.0][Email]Pre-defined account list can't display completely
 *BUGFIX-955397  2015/03/25   ke.ma           [America][SSV]When enter into email,can't find "iClaro"display.
 *BUGFIX-963981  2015/03/31   ke.ma           [SSV][AmericaMoivl]add SSV SDM for MAIL CONFIGURATION
 *BUGFIX-968870  2015/04/03   ke.ma           [America][SSV]Email occur force close.
 *BUGFIX-996086  2015/05/08   ke.ma           The handset in the email app has not the option to set an "@itelcel" account.
 *BUGFIX-996086  2015/05/08   ke.ma           The handset in the email app has not the option to set an "@itelcel" account.
 *BUGFIX-996086  2015/05/09   ke.ma           The handset in the email app has not the option to set an "@itelcel" account.
 *BUGFIX-998426  2015/05/14   junwei-xu       [HOMO][HOMO][TMO ] T-Mobile FOC requirement on ranged product but our open marketing version.
 *BUGFIX-1009055 2015/05/26   junwei-xu       [HOMO][SSV - AMX] Please change default email accounts order.
 *BUGFIX-1067925 2015/08/18   junwei-xu       [America][SSV]Create email account when there is no priority hint.
 *BUGFIX-1075075 2015/08/26   jian.xu       [America][SSV]Email account order is wrong, should customized order.
 *BUGFIX-523794  2015/08/31   Gantao          [Android 5.0][Email]The predefined account can't display fully if the name is long
 *FEATURE-664213 2015/09/23   tao.gan         [Email]Idol4 ssv new plan
 *BUGFIX-1072139 2015/09/01   junwei-xu       [HOMO][O2 UK][Email] Make top-level domains ("com", "org", "info", "de") country codes not relevant.
 ===========================================================================
 */
package com.tct.email.activity.setup;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Configuration;
import android.os.SystemProperties;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.tct.email.R;
import com.tct.email.provider.Utilities;
import com.tct.emailcommon.VendorPolicyLoader.Provider;
import com.tct.mail.utils.LogUtils;
import com.tct.mail.utils.PLFUtils;

//TS: ke.ma 2015-03-09 EMAIL BUGFIX-932385 ADD_S
public class EmailAutoCompleteTextView extends AutoCompleteTextView {

    private StringBuffer[] emailSufixs=null;
    //TS: junwei-xu 2015-08-18 EMAIL BUGFIX-1067925 ADD_S
    private static ArrayList<String> iclaroDomains = new ArrayList<String>();
    //TS: junwei-xu 2015-08-18 EMAIL BUGFIX-1067925 ADD_E

    //TS: ke.ma 2015-03-25 EMAIL BUGFIX-955397 MOD_S
    public EmailAutoCompleteTextView(Context context) {
        super(context);
        //TS: junwei-xu 2015-09-01 EMAIL BUGFIX-1072139 MOD_S
        boolean supportPrompt = PLFUtils.getBoolean(context, "feature_email_associate_prompt_on");
        if (supportPrompt) {
            emailSufixs = getSSVStringBuffer(context);
            init(context);
        }
        //TS: junwei-xu 2015-09-01 EMAIL BUGFIX-1072139 MOD_E
    }

    public EmailAutoCompleteTextView(Context context,AttributeSet attrs) {
        super(context, attrs);
        //TS: junwei-xu 2015-09-01 EMAIL BUGFIX-1072139 MOD_S
        boolean supportPrompt = PLFUtils.getBoolean(context, "feature_email_associate_prompt_on");
        if (supportPrompt) {
            emailSufixs = getSSVStringBuffer(context);
            init(context);
        }
        //TS: junwei-xu 2015-09-01 EMAIL BUGFIX-1072139 MOD_E
    }

    public EmailAutoCompleteTextView(Context context,AttributeSet attrs,int defStyle) {
        super(context, attrs, defStyle);
        //TS: junwei-xu 2015-09-01 EMAIL BUGFIX-1072139 MOD_S
        boolean supportPrompt = PLFUtils.getBoolean(context, "feature_email_associate_prompt_on");
        if (supportPrompt) {
            emailSufixs = getSSVStringBuffer(context);
            init(context);
        }
        //TS: junwei-xu 2015-09-01 EMAIL BUGFIX-1072139 MOD_E
    }
    //TS: ke.ma 2015-03-25 EMAIL BUGFIX-955397 MOD_E

    public void setAdapterStringBuffer(StringBuffer[] es){
        if(es!=null&&es.length>0){
            this.emailSufixs=es;
        }
    }

 // TS: tao.gan 2015-09-23 EMAIL FEATURE_664213 MOD_S
    private static void initIclaroDomains(ArrayList<Provider> providerList) {
        for (int i =0 ;i< providerList.size(); i++) {
            iclaroDomains.add("@" + providerList.get(i).domain);
        }
    }
 // TS: tao.gan 2015-09-23 EMAIL FEATURE_664213 MOD_E

    //TS: ke.ma 2015-03-31 EMAIL BUGFIX-963981 MOD_S
    //TS: ke.ma 2015-03-25 EMAIL BUGFIX-955397 ADD_S
    private static StringBuffer[] getSSVStringBuffer(Context context){
        StringBuffer[] ssvStringBuffer=null,ssvStringBufferTemp=null;

     // TS: tao.gan 2015-09-23 EMAIL FEATURE_664213 MOD_S
        boolean ssvEnabled = Utilities.ssvEnabled();
        LogUtils.i(LogUtils.TAG, "ssvEnabled   : " + ssvEnabled);
        ArrayList<Provider> providerList=null;
        if(ssvEnabled) {
            providerList = PLFUtils.getSSVProviderList(context);
            initIclaroDomains(providerList);
        } else {
            providerList=null;
        }
     // TS: tao.gan 2015-09-23 EMAIL FEATURE_664213 ADD_E
        if(providerList!=null){
            ssvStringBuffer=new StringBuffer[providerList.size()];
            for(int i=0;i<providerList.size();i++){
                ssvStringBuffer[i]=new StringBuffer("@"+providerList.get(i).domain);
            }
            ssvStringBufferTemp=AccountSettingsUtils.removeRepeat(ssvStringBuffer);
        }else{
            ssvStringBufferTemp=AccountSettingsUtils.getAutoCompleteDomains(context);
        }

        //TS: junwei-xu 2015-05-26 EMAIL BUGFIX-1009055 ADD_S
        //TS: ke.ma 2015-05-08 EMAIL BUGFIX-996086 ADD_S
        if (ssvStringBufferTemp != null) {
            //TS: jian.xu 2015-08-26 EMAIL BUGFIX-1075075 MOD_S
            String sortDomain = null;
         // TS: tao.gan 2015-09-23 EMAIL FEATURE_664213 MOD_S
            if(ssvEnabled && providerList!= null) {//providerList!= null indicate that we are doing ssv configureation
                sortDomain = PLFUtils.getString(context, "def_email_ssv_topRankDomainValue");
            } else {
                sortDomain = PLFUtils.getString(context, "def_isp_topRankDomainValue");
            }
         // TS: tao.gan 2015-09-23 EMAIL FEATURE_664213 MOD_E
            //TS: jian.xu 2015-08-26 EMAIL BUGFIX-1075075 MOD_E
            String[] sortDomains = null;
            if (!TextUtils.isEmpty(sortDomain) && !"@".equals(sortDomain)){
                if (sortDomain.contains("@")) {
                    sortDomains = sortDomain.split("@");
                } else {
                    sortDomains = new String[]{sortDomain};
                }

                if (sortDomains != null) {
                    // check these domains is existed in ssvStringBufferTemp
                    List<String> domiansCutInvalidList = new ArrayList<String>();
                    for (int i = 0; i < sortDomains.length; i++) {
                        boolean isExit = false;
                        for (StringBuffer tempSSVDomain : ssvStringBufferTemp) {
                            if (tempSSVDomain.toString().equals("@" + sortDomains[i])) {
                                isExit = true;
                                break;
                            }
                            //TS: junwei-xu 2015-08-18 EMAIL BUGFIX-1067925 ADD_S
                            //Note: for iclaro account, we don't which sim card user use, so we can not ensure which
                            //iclaro domain exactly. so use fuzzy matching.
                            if (tempSSVDomain.toString().contains(sortDomains[i]) &&
                                    iclaroDomains.contains(tempSSVDomain.toString())) {
                                isExit = true;
                                sortDomains[i] = tempSSVDomain.toString();
                                break;
                            }
                            //TS: junwei-xu 2015-08-18 EMAIL BUGFIX-1067925 ADD_E
                        }
                        if (isExit) {
                            domiansCutInvalidList.add(sortDomains[i]);
                        }
                    }
                    StringBuffer[] domiansCutInvalidArray = new StringBuffer[domiansCutInvalidList.size()];
                    for (int k = 0; k < domiansCutInvalidList.size(); k++) {
                        if (!TextUtils.isEmpty(domiansCutInvalidList.get(k))) {
                            //TS: junwei-xu 2015-08-18 EMAIL BUGFIX-1067925 MOD_S
                            if (domiansCutInvalidList.get(k).startsWith("@")) {
                                domiansCutInvalidArray[k] = new StringBuffer(domiansCutInvalidList.get(k));
                            } else {
                                domiansCutInvalidArray[k] = new StringBuffer("@" + domiansCutInvalidList.get(k));
                            }
                            //TS: junwei-xu 2015-08-18 EMAIL BUGFIX-1067925 MOD_E
                        }
                    }

                    int sortDomainsLength = domiansCutInvalidArray.length;
                    int ssvStringLength = ssvStringBufferTemp.length;
                    StringBuffer[] total = new StringBuffer[sortDomainsLength + ssvStringLength];
                    System.arraycopy(domiansCutInvalidArray, 0, total, 0, sortDomainsLength);
                    System.arraycopy(ssvStringBufferTemp, 0, total, sortDomainsLength, ssvStringLength);

                    ssvStringBufferTemp = removeRepeat(total);
                }
            }
        }
        //TS: ke.ma 2015-05-08 EMAIL BUGFIX-996086 ADD_E
        //TS: junwei-xu 2015-05-26 EMAIL BUGFIX-1009055 ADD_E

        return ssvStringBufferTemp;
    }
    //TS: ke.ma 2015-03-25 EMAIL BUGFIX-955397 ADD_E
    //TS: ke.ma 2015-03-31 EMAIL BUGFIX-963981 MOD_E

    private void init(Context context) {
      //TS: Gantao 2015-08-31 EMAIL BUGFIX-523794 MOD_S
        //We want to set the "ellipsize" as middle,so use custom layout,instead of
        //android default layout.
        this.setAdapter(new EmailAutoCompleteAdapter(context,R.layout.account_dropdown_item_1line,emailSufixs));
      //TS: Gantao 2015-08-31 EMAIL BUGFIX-523794 MOD_E
        this.setThreshold(1);
        this.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // TODO Auto-generated method stub
                if(hasFocus){
                    String text=EmailAutoCompleteTextView.this.getText().toString();
                    if(!text.isEmpty()){
                        performFiltering(text, 0);
                    }
                }
            }
        });
    }

    @Override
    protected void replaceText(CharSequence text) {
        // TODO Auto-generated method stub
        String t=this.getText().toString();
        int index=t.indexOf("@");
        if(index!=-1){
            t=t.substring(0,index);
            super.replaceText(t+text);
        }
    }

    @Override
    protected void performFiltering(CharSequence text, int keyCode) {
        // TODO Auto-generated method stub
        String t=text.toString();
        //[BUGFIX]-Del by SCDTABLET.weiwei.huang,04/21/2016,1986355,
        // [Email]Preset accounts cannot display in landscape mode.
//        if(getResources().getConfiguration().orientation==Configuration.ORIENTATION_PORTRAIT){
            int index=t.indexOf("@");
            if(index==-1){
                if(t.matches("^[a-zA-Z0-9_]+$")&&t.contains("@")){
                    super.performFiltering("@", keyCode);
                }else{
                    this.dismissDropDown();
                }
            }else{
                super.performFiltering(t.substring(index), keyCode);
            }
        //[BUGFIX]-Del by SCDTABLET.weiwei.huang,04/21/2016,1986355,
        // [Email]Preset accounts cannot display in landscape mode.
//        }
    }

    private class EmailAutoCompleteAdapter extends ArrayAdapter<StringBuffer>{

        public EmailAutoCompleteAdapter(Context context, int resource,
                StringBuffer[] objects) {
            super(context, resource, objects);
            // TODO Auto-generated constructor stub
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            View view=convertView;
            if(view==null){
              //TS: Gantao 2015-08-31 EMAIL BUGFIX-523794 MOD_S
                //We want to set the "ellipsize" as middle,so use custom layout,instead of
                //android default layout.
                view=LayoutInflater.from(getContext()).inflate(R.layout.account_dropdown_item_1line, null);
              //TS: Gantao 2015-08-31 EMAIL BUGFIX-523794 MOD_E
            }
            TextView tv=(TextView) view.findViewById(R.id.dropdown_text);
            //TS: ke.ma 2015-03-23 EMAIL BUGFIX-954524 MOD_S
            final float scale = getContext().getResources().getDisplayMetrics().density;
            tv.setHeight((int)(30 * scale + 0.5f));
            //TS: ke.ma 2015-03-23 EMAIL BUGFIX-954524 MOD_E
            String t=EmailAutoCompleteTextView.this.getText().toString();
            int index=t.indexOf("@");
            if(index!=-1){
                t=t.substring(0, index);
            }
            tv.setText(t+getItem(position));
            return view;
        }
    }

    //TS: ke.ma 2015-05-08 EMAIL BUGFIX-996086 ADD_S
    public static StringBuffer[] removeRepeat(StringBuffer[] str){
         List<StringBuffer> strs=new ArrayList<StringBuffer>();
         for(int i=0;i<str.length;i++){
            if(!TextUtils.isEmpty(str[i])&&!AccountSettingsUtils.isExsit(strs,str[i])){
                strs.add(str[i]);
            }
         }

         StringBuffer[] resultBuffer=new StringBuffer[strs.size()];
         for(int k=0;k<strs.size();k++){
            resultBuffer[k]=new StringBuffer(strs.get(k));
         }
         return resultBuffer;
     }
     //TS: ke.ma 2015-05-08 EMAIL BUGFIX-996086 ADD_E
}
//TS: ke.ma 2015-03-09 EMAIL BUGFIX-932385 ADD_E
