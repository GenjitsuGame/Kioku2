package com.fragile.kioku2;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.fragile.kioku2.scrobbling.ScrobblerService;
import com.fragile.utils.fragments.QuerySubmitListFragment;
import com.fragile.utils.views.DrawerLayout;

import fm.last.api.Session;
import fm.last.api.Track;

public class MainActivity extends Activity implements DrawerLayout.DrawerListener, LoginFragment.LoginFragmentListener,
        RecentUserTracksFragment.ChansonRecenteFragmentListener, QuerySubmitListFragment.OnNoResultFoundListener, QuerySubmitListFragment.OnQuerySubmitListener {

    private final String[] welcome = new String[]{LOGIN};
    private final String[] mainOptions = new String[]{PROFILE, SONGS, ARTISTS, FRIENDS, EVENTS, LOGOUT};
    private com.fragile.utils.views.DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private Runnable mPendingRunnable;
    private Handler mHandler;
    public static final String LOGIN = "Connexion";
    public static final String PROFILE = "Profil";
    public static final String SONGS = "Chansons";
    public static final String FRIENDS = "Amis";
    public static final String ARTISTS = "Artistes";
    public static final String EVENTS = "Events";
    public static final String LOGOUT = "Deconnexion";

    private Context context = this;
    private String currentUsername;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mHandler = new Handler();

        final AccountManager accountManager = AccountManager.get(this);
        Account[] accounts = accountManager.getAccountsByType(this.getString(R.string.ACCOUNT_TYPE));

        if (accounts != null && accounts.length > 0) {
            KiokuApplication.setSession(new Session(accounts[0].name, accountManager.getUserData(accounts[0], "SESSION_TOKEN"), accountManager.getUserData(accounts[0], "SUBSCRIBER")));
            this.currentUsername = KiokuApplication.getSession().getName();
            Intent scrobblingService = new Intent(this, ScrobblerService.class);
            startService(scrobblingService);
            setDrawerContent(mainOptions);
            startFragmentWrapper(SONGS, 2);
        } else {
            setDrawerContent(welcome);
            startFragmentWrapper(LOGIN, 0);
        }

        mDrawerLayout.setDrawerListener(this);
        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
            mPendingRunnable = null;
        }

    }

    @Override
    public void setTitle(CharSequence title) {
        CharSequence mTitle = title;
        getActionBar().setTitle(mTitle);
    }

    private void setDrawerContent(String[] content) {

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // Set the adapter for the list view
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, content));
        // Set the list's click listener
        mDrawerList.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {


                String clickedItem = (String) adapterView.getItemAtPosition(i);
                if (clickedItem.equals(LOGOUT)) {
                    final AccountManager accountManager = AccountManager.get(context);
                    Account[] accounts = accountManager.getAccountsByType(context.getString(R.string.ACCOUNT_TYPE));
                    accountManager.removeAccount(accounts[0], null, null);
                    KiokuApplication.setSession(null);
                    setDrawerContent(welcome);
                    startFragmentWrapper(LOGIN, 0);
                } else
                    startFragmentWrapper(clickedItem, i);
            }


        });
    }

    private void startFragment(Class<? extends Fragment> clazz, Bundle args) {
        try {
            Fragment fragment = clazz.newInstance();

            if (args != null) {
                fragment.setArguments(args);
            }
            // Insert the fragment by replacing any existing fragment
            getFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, fragment)
                    .commit();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void startFragmentWrapper(final String clickedItem, int position) {
        mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                Fragment fragment;
                FragmentManager fragmentManager;
                Bundle args;
                if (clickedItem.equals(LOGIN)) {
                    startFragment(LoginFragment.class, null);
                } else if (clickedItem.equals(SONGS)) {
                    args = new Bundle();
                    args.putString("USERNAME", currentUsername);
                    startFragment(RecentUserTracksFragment.class, args);
                } else if (clickedItem.equals(EVENTS)) {
                    args = new Bundle();
                    args.putString("USERNAME", currentUsername);
                    startFragment(EventsFragment.class, args);
                } else if (clickedItem.equals(FRIENDS)) {
                    args = new Bundle();
                    args.putString("USERNAME", currentUsername);
                    startFragment(UserFriendFragment.class, args);
                } else if (clickedItem.equals(ARTISTS)) {
                    args = new Bundle();
                    args.putString("USERNAME", currentUsername);
                    startFragment(ArtistFragment.class, args);
                } else if (clickedItem.equals(PROFILE)) {
                    startFragment(ProfilFragment.class, null);
                }
            }
        };
        // Highlight the selected item, update the title, and close the drawer
        mDrawerList.setItemChecked(position, true);
        setTitle(clickedItem);
        mDrawerLayout.closeDrawer(mDrawerList);

    }

    @Override
    public void onResume() {
        super.onResume();
        if (KiokuApplication.getSession() == null) {
            startFragmentWrapper(LOGIN, 0);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onLoggingResult(boolean isLogged) {
        if (isLogged) {
            Intent scrobblingService = new Intent(this, ScrobblerService.class);
            this.currentUsername = KiokuApplication.getSession().getName();
            startService(scrobblingService);
            setDrawerContent(mainOptions);
            Bundle args = new Bundle();
            args.putString("USERNAME", currentUsername);
            startFragment(RecentUserTracksFragment.class, args);
            mDrawerList.setItemChecked(2, true);
            setTitle(SONGS);
        } else {
            Toast.makeText(this, "ERREUR LORS DU LOGGING", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {

    }

    @Override
    public void onDrawerOpened(View drawerView) {

    }

    @Override
    public void onDrawerClosed(View view) {
        invalidateOptionsMenu();

        // If mPendingRunnable is not null, then add to the message queue
        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
            mPendingRunnable = null;
        }
    }

    @Override
    public void onDrawerStateChanged(int newState) {

    }

    @Override
    public void onTrackSelected(Track track) {
        Fragment fragment = new TrackInfoFragment();
        Bundle args = new Bundle();
        args.putString("TITLE", track.getName());
        args.putString("ARTIST", track.getArtist().getName());
        args.putString("ALBUM", track.getAlbum().getTitle());

        fragment.setArguments(args);
        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragment)
                .commit();
    }

    @Override
    public void onNoResultFound(String query) {
        Fragment fragment = new ErrorFragment();
        Bundle args = new Bundle();
        args.putString("MESSAGE", "Aucun résultat pour : " + query);
        fragment.setArguments(args);
        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragment)
                .commit();
    }


    @Override
    public void onQuerySubmitted(String query) {
        this.currentUsername = query;
    }


}
