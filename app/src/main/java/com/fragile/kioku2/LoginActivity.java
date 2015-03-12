package com.fragile.kioku2;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.ExecutionException;

public class LoginActivity extends AccountAuthenticatorActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final AccountManager accountManager = AccountManager.get(this);
        Account[] accounts = accountManager.getAccountsByType(this.getString(R.string.ACCOUNT_TYPE));


        if (accounts != null && accounts.length > 0) {
            accountManager.removeAccount(accounts[0], null, null);
        }

        final Bundle accountInformations = getIntent().getExtras();
        String login = accountInformations.getString("LOGIN");
        String password = accountInformations.getString("PASSWORD");
        String sessionToken = accountInformations.getString("SESSION_TOKEN");
        String subsriber = accountInformations.getString("SUBSCRIBER");

        Bundle accountExtras = new Bundle();
        accountExtras.putString("SESSION_TOKEN", sessionToken);
        accountExtras.putString("SUBSCRIBER", subsriber);
        Account account = new Account(login, getString(R.string.ACCOUNT_TYPE));
        accountManager.addAccountExplicitly(account, password, accountExtras);
        final Intent intent = new Intent();
        intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, login);
        intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, getString(R.string.ACCOUNT_TYPE));
        setAccountAuthenticatorResult(intent.getExtras());
        setResult(RESULT_OK, intent);
        finish();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

}