package com.fragile.kioku2;


import android.content.Context;
import android.content.Loader;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fragile.utils.adapters.CustomArrayAdapter;
import com.fragile.utils.fragments.QuerySubmitListFragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;

import fm.last.api.Track;
import fm.last.api.User;


/**
 * A simple {@link Fragment} subclass.
 */
public class UserFriendFragment extends QuerySubmitListFragment {

    private static final int FRIENDS_LOADER = 2;

    private FriendListAdapter itemsAdapter;

    private ImageView profilePicture;

    private TextView username;

    private TextView songName;

    private TextView artistName;

    private TextView playCount;

    private TextView dateJoined;

    public UserFriendFragment() {
        // Required empty public constructor
    }

    private final class FriendListAdapter extends CustomArrayAdapter<User> {

        public FriendListAdapter(Context context) {
            super(context, R.layout.listview_layout);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view;
            if (convertView == null) {
                view = mInflater.inflate(R.layout.fragment_friends_layout, parent, false);
            } else {
                view = convertView;
            }

            profilePicture = (ImageView) view.findViewById(R.id.list_image);

            username = (TextView) view.findViewById(R.id.idFriendName);

            songName = (TextView) view.findViewById(R.id.idTitleSong);

            artistName = (TextView) view.findViewById(R.id.idArtistName);

            dateJoined = (TextView) view.findViewById(R.id.date_joined);

            playCount = (TextView) view.findViewById(R.id.play_count);

            User user = getItem(position);

            username.setText(user.getName());

            playCount.setText(user.getPlaycount() + " scrobbles");

            Calendar date = Calendar.getInstance();
            date.setTime(user.getJoinDate());
            dateJoined.setText("à rejoint le : " + date.get(Calendar.DAY_OF_MONTH) + "/" + date.get(Calendar.MONTH) + 1 + "/" + date.get(Calendar.YEAR));

            Track recentTrack = user.getRecentTrack();
            if (recentTrack != null) {
                songName.setText(recentTrack.getName());
                artistName.setText(recentTrack.getArtist().getName());
            } else {
                songName.setText(user.getName() + " ");
                artistName.setText("ne partage pas ses tracks.");
            }
            try {
                profilePicture.setImageBitmap(new LastFmOperations.FetchImageTask().execute(user.getURLforImageSize("large")).get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            return view;
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getArguments() != null) {
            this.query = (String) getArguments().get("USERNAME");
        }

        // Give some text to display if there is no data.  In a real
        // application this would come from a resource.
        setEmptyText("N/A");

        // We have a menu item to show in action bar.
        setHasOptionsMenu(true);

        // Create an empty adapter we will use to display the loaded data.
        itemsAdapter = new FriendListAdapter(getActivity());


        // Start out with a progress indicator.
        setListShown(false);
    }


    @Override
    public void onQueryTextSubmitting() {

    }

    @Override
    public Loader onCreateLoader(int i, Bundle bundle) {
        if (i == FRIENDS_LOADER) {
            return new LastFmOperations.UserFriends(getActivity(), bundle);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader loader, Object o) {
        if (loader.getId() == FRIENDS_LOADER) {
            if (o == null) {
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        onNoResultFoundListener.onNoResultFound(query);
                    }
                });
            }
            ArrayList<User> users = (ArrayList<User>) o;
            itemsAdapter.setData(users);
            setListAdapter(itemsAdapter);
            setListShown(true);
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {
        if (loader.getId() == FRIENDS_LOADER) {
            itemsAdapter.setData(null);
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        Bundle argsRecentTracks = new Bundle();
        argsRecentTracks.putString("USERNAME", query);
        argsRecentTracks.putString("RECENT_TRACKS", "TRUE");
        argsRecentTracks.putString("LIMIT", "20");
        getLoaderManager().initLoader(FRIENDS_LOADER, argsRecentTracks, this);
    }
}
