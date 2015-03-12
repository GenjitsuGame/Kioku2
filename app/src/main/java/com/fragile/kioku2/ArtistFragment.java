package com.fragile.kioku2;

import android.app.Activity;
import android.content.Context;
import android.content.Loader;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.fragile.utils.adapters.CustomArrayAdapter;
import com.fragile.utils.fragments.QuerySubmitListFragment;

import java.util.List;
import java.util.concurrent.ExecutionException;

import fm.last.api.Artist;
import fm.last.api.Track;


/**
 * A simple {@link android.app.Fragment} subclass.
 * Use the {@link com.fragile.kioku2.ArtistFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ArtistFragment extends QuerySubmitListFragment {

    private TextView artistName;

    private TextView nbListenings;

    private ImageView picture;

    private static final int ARTIST_LOADER = 2;

    private final class ArtistAdapter extends CustomArrayAdapter<Artist> {

        public ArtistAdapter(Context context) {
            super(context, R.layout.listview_layout);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view;

            if (convertView == null) {
                view = mInflater.inflate(R.layout.fragment_artists_layout, parent, false);
            } else {
                view = convertView;
            }
            artistName = (TextView) view.findViewById(R.id.idArtistName);
            nbListenings = (TextView) view.findViewById(R.id.idNbListening);


            picture = (ImageView) view.findViewById(R.id.idImageArtist);

            Artist artist = getItem(position);
            artistName.setText(artist.getName());
            nbListenings.setText("joué " + artist.getPlaycount() + " fois");

            try {
                Bitmap bm = new LastFmOperations.FetchImageTask().execute(artist.getURLforImageSize("large")).get();
                if (bm != null) {
                    picture.setImageBitmap(bm);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            return view;
        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_artists, menu);

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.overall:
                requeryWithPeriod("overall");
                return true;
            case R.id.weekly:
                requeryWithPeriod("7day");
                return true;
            case R.id.monthly:
                requeryWithPeriod("1month");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void requeryWithPeriod(String period) {
        getLoaderManager().destroyLoader(ARTIST_LOADER);
        Bundle args = new Bundle();
        args.putString("USERNAME", query);
        args.putString("PERIOD",period);
        getLoaderManager().restartLoader(ARTIST_LOADER, args, this);
    }

    @Override
    public void onQueryTextSubmitting() {
        Bundle argsRecentTracks = new Bundle();
        argsRecentTracks.putString("USERNAME", query);
        getLoaderManager().destroyLoader(ARTIST_LOADER);
        getLoaderManager().restartLoader(ARTIST_LOADER, argsRecentTracks, this);
    }

    private ArtistAdapter itemsAdapter;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ChansonsRecentesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RecentUserTracksFragment newInstance(String param1, String param2) {
        RecentUserTracksFragment fragment = new RecentUserTracksFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public ArtistFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getArguments() != null) {
            this.query = (String) getArguments().get("USERNAME");
        }

        // Give some text to display if there is no data.  In a real
        // application this would come from a resource.
        setEmptyText("Aucun artiste écoutés pendant cette période");

        // We have a menu item to show in action bar.
        setHasOptionsMenu(true);

        // Create an empty adapter we will use to display the loaded data.
        itemsAdapter = new ArtistAdapter(getActivity());

        // Start out with a progress indicator.
        setListShown(false);

    }

    @Override
    public void onStart() {
        super.onStart();
        Bundle argsTopArtists = new Bundle();
        argsTopArtists.putString("USERNAME", query);
        argsTopArtists.putString("PERIOD", "overall");
        getLoaderManager().initLoader(ARTIST_LOADER, argsTopArtists, this);
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        if (id == ARTIST_LOADER) {
            return new LastFmOperations.UserTopArtists(getActivity(), args);
        } else {
            return null;
        }
    }

    @Override
    public void onLoadFinished(Loader loader, Object o) {
        int i = loader.getId();
        if (i == ARTIST_LOADER) {
            if (o == null) {
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        onNoResultFoundListener.onNoResultFound(query);
                    }
                });
            }
            itemsAdapter.setData((List<Artist>) o);
            setListAdapter(itemsAdapter);

            setListShown(true);

        }
    }

    @Override
    public void onLoaderReset(Loader loader) {
        switch (loader.getId()) {
            case ARTIST_LOADER:
                itemsAdapter.setData(null);
                break;
        }
    }



}
