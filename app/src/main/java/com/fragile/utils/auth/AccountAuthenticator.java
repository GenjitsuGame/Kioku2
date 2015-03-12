package com.fragile.utils.auth;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.fragile.kioku2.LoginActivity;
import com.fragile.kioku2.R;

public class AccountAuthenticator extends AbstractAccountAuthenticator {
    private Context mContext;

    public AccountAuthenticator(Context context) {
        super(context);
        mContext = context;
        // TODO Auto-generated constructor stub
    }

    @Override
    public Bundle addAccount(AccountAuthenticatorResponse response, String accountType, String authTokenType, String[] requiredFeatures, Bundle options)
            throws NetworkErrorException {
        final Bundle result;
        final Intent intent;

        intent = new Intent(this.mContext, LoginActivity.class);
        intent.putExtra(mContext.getString(R.string.ACCOUNT_TYPE), authTokenType);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);

        result = new Bundle();
        result.putParcelable(AccountManager.KEY_INTENT, intent);

        return result;
    }

    @Override
    public Bundle confirmCredentials(AccountAuthenticatorResponse response, Account account, Bundle options) throws NetworkErrorException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Bundle editProperties(AccountAuthenticatorResponse response, String accountType) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getAuthTokenLabel(String authTokenType) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Bundle hasFeatures(AccountAuthenticatorResponse response, Account account, String[] features) throws NetworkErrorException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Bundle updateCredentials(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {
        // TODO Auto-generated method stub
        return null;
    }
}
