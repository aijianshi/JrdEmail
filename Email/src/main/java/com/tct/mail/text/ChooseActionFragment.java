/*
 ==========================================================================
 *HISTORY
 *
 *Tag            Date         Author          Description
 *============== ============ =============== ==============================
 *BUGFIX-1951808 2016/04/19   rong-tang       [Email]Can not add address to contact in Email
 ============================================================================
 */

package com.tct.mail.text;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Intents;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.QuickContact;

import com.tct.email.R;
import com.tct.email2.ui.MailActivityEmail;
import com.tct.mail.ContactInfo;
import com.tct.mail.ContactInfoSource;
import com.tct.mail.compose.ComposeActivity;
import com.tct.mail.providers.Account;
import com.tct.mail.utils.LogUtils;

public class ChooseActionFragment extends DialogFragment {

    private String[] mChooseActions = new String[3];
    private static Account mAccount;
    private static String mEmailAddress;
    private static ContactInfoSource mContactInfoSource;
    public final static String FRAGMENT_TAG = "CHOOSE_ACTION";
    private final static String KEY_ACCOUNT = "current_account";
    private final static String KEY_EMAIL_ADDRESS = "email_address";

    public static ChooseActionFragment newInstance(Account account, String address,ContactInfoSource contactInfoSource) {
        mContactInfoSource = contactInfoSource;
        Bundle bundle = new Bundle();
        bundle.putString(KEY_EMAIL_ADDRESS, address);
        bundle.putParcelable(KEY_ACCOUNT, account);
        ChooseActionFragment dialog = new ChooseActionFragment();
        dialog.setArguments(bundle);
        return dialog;
    }

    private void restoreParameters() {
        Bundle bundle = this.getArguments();
        mEmailAddress = bundle.getString(KEY_EMAIL_ADDRESS);
        mAccount = bundle.getParcelable(KEY_ACCOUNT);
        mChooseActions[0] = getString(R.string.choose_action_contact, mEmailAddress);
        mChooseActions[1] = getString(R.string.choose_action_email, mEmailAddress);
        mChooseActions[2] = getString(R.string.choose_action_message, mEmailAddress);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        restoreParameters();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()).setTitle(R.string.choose_action_title)
            .setSingleChoiceItems(mChooseActions, 0, null).setNegativeButton(R.string.cancel_action, null)
            .setPositiveButton(R.string.okay_action, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    int position = ((AlertDialog)dialog).getListView().getCheckedItemPosition();
                    switch (position) {
                        case 0:
                            operateAddContact();
                            break;
                        case 1:
                            operateEmail();
                            break;
                        case 2:
                            operateMessage();
                            break;
                    }
                }
            });
        return builder.create();
    }

    /**
     * Use Contact app to add a person
     */
    private void operateAddContact() {
       //TS: yanhua.chen 2016-2-22 EMAIL BUGFIX_1136368 MOD_S
       ContactInfo info = mContactInfoSource.getContactInfo(mEmailAddress);
       if (info != null) {
           if(info.contactUri != null){
               try {
                   final Intent intent = new Intent(QuickContact.ACTION_QUICK_CONTACT);
                   intent.setData(info.contactUri);
                   startActivity(intent);
               } catch (Exception e) {
                   LogUtils.e(MailActivityEmail.LOG_TAG, e, "Activity not found");
               }
           } else {
              try{
                  final Intent intent = new Intent(Intent.ACTION_INSERT_OR_EDIT);
                  intent.setType(ContactsContract.Contacts.CONTENT_ITEM_TYPE);
                  intent.putExtra(Intents.Insert.EMAIL, mEmailAddress);
                  intent.putExtra(Intents.Insert.EMAIL_TYPE, Email.TYPE_WORK);
                  startActivity(intent);
               } catch (Exception e) {
                  LogUtils.e(MailActivityEmail.LOG_TAG, e, "Activity not found");
               }
           }
       }
     //TS: yanhua.chen 2016-2-22 EMAIL BUGFIX_1136368 MOD_E
    }

    /**
     * Use Email app to compose
     */
    private void operateEmail() {
        ComposeActivity.composeToAddress(getActivity(), mAccount, mEmailAddress);
    }

    /**
     * Use Message app to compose
     */
    private void operateMessage() {
        Uri uri = Uri.parse("smsto:" + mEmailAddress);
        Intent sendIntent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(sendIntent);
    }
}