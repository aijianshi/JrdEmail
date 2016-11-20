// Copyright (C) 2014 Google Inc.

package com.tct.mail.ui;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.net.Uri;
import android.os.Bundle;

import com.tct.mail.content.CursorCreator;
import com.tct.mail.content.ObjectCursor;
import com.tct.mail.content.ObjectCursorLoader;
import com.tct.mail.providers.Account;
import com.tct.mail.providers.UIProvider;

/**
 * Callbacks for loading an account cursor.
 */
public class AccountLoadCallbacks implements LoaderManager.LoaderCallbacks<ObjectCursor<Account>> {

    public interface AccountLoadCallbackListener {
        void onAccountLoadCallbackFinished(ObjectCursor<Account> data);
    }

    private final Context mContext;
    private final Uri mAccountUri;
    private final AccountLoadCallbackListener mAccountLoadCallbackListener;

    public AccountLoadCallbacks(Context context, Uri accountUri,
                                AccountLoadCallbackListener accountLoadCallbackListener) {
        mContext = context;
        mAccountUri = accountUri;
        mAccountLoadCallbackListener = accountLoadCallbackListener;
    }

    @Override
    public Loader<ObjectCursor<Account>> onCreateLoader(int id, Bundle args) {
        final String[] projection = UIProvider.ACCOUNTS_PROJECTION;
        final CursorCreator<Account> factory = Account.FACTORY;
        return new ObjectCursorLoader<Account>(
                mContext, mAccountUri, projection, factory);
    }

    @Override
    public void onLoadFinished(Loader<ObjectCursor<Account>> loader,
            ObjectCursor<Account> data) {
        mAccountLoadCallbackListener.onAccountLoadCallbackFinished(data);
    }

    @Override
    public void onLoaderReset(Loader<ObjectCursor<Account>> loader) {
    }
}
