package com.fragile.kioku2;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.fragile.utils.adapters.CustomArrayAdapter;
import com.fragile.utils.fragments.QuerySubmitListFragment;
import com.fragile.utils.loaders.OnFinishedDataLoaderWrapper;

import java.util.List;
import java.util.concurrent.ExecutionException;

import fm.last.api.Track;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RecentUserTracksFragment.ChansonRecenteFragmentListener} interface
 * to handle interaction fragment_events_layout.
 * Use the {@link RecentUserTracksFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecentUserTracksFragment extends QuerySubmitListFragment {

    public interface ChansonRecenteFragmentListener {
        void onTrackSelected(Track track);
    }

    private ChansonRecenteFragmentListener chansonRecenteFragmentListener;


    private static final int USER_RECENT_TRACKS_LOADER = 2;


    private final class TrackListAdapter extends CustomArrayAdapter<Track> {

        public TrackListAdapter(Context context) {
            super(context, R.layout.listview_layout);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view;

            if (convertView == null) {
                view = mInflater.inflate(R.layout.chanson_recente_layout, parent, false);
            } else {
                view = convertView;
            }
            TextView firstLine = (TextView) view.findViewById(R.id.firstLine);
            TextView secondLine = (TextView) view.findViewById(R.id.secondLine);


            final ImageView albumArtPlaceHolder = (ImageView) view.findViewById(R.id.icon);

            Track track = getItem(position);
            String artist = track.getArtist().getName();
            String title = track.getName();
            firstLine.setText(artist + " - " + title);
            secondLine.setText(track.getAlbum().getTitle());

            try {
                Bitmap bm = new LastFmOperations.FetchImageTask().execute(track.getURLforImageSize("large")).get();
                if (bm != null) {
                    albumArtPlaceHolder.setImageBitmap(bm);
                } else {
                    albumArtPlaceHolder.setImageBitmap(new LastFmOperations.FetchImageTask().execute(track.getArtist().getURLforImageSize("large")).get());
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            /*
            if (albumArts[position] == null) {
                final Bundle argsAlbumArt = new Bundle();
                argsAlbumArt.putString("URL", track.getURLforImageSize("large"));
                argsAlbumArt.putInt("POSITION", position);

                getLoaderManager().initLoader(FETCH_ALBUM_ART_LOADER + position, argsAlbumArt, loaderCallbacks);
            } else {
                albumArtPlaceHolder.setImageBitmap(albumArts[position]);
            }
*/
            return view;
        }

    }

    @Override
    public void onQueryTextSubmitting() {
        Bundle argsRecentTracks = new Bundle();
        argsRecentTracks.putString("USERNAME", query);
        getLoaderManager().destroyLoader(USER_RECENT_TRACKS_LOADER);
        getLoaderManager().restartLoader(USER_RECENT_TRACKS_LOADER, argsRecentTracks, this);
    }

    private TrackListAdapter itemsAdapter;

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

    public RecentUserTracksFragment() {
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
        setEmptyText("N/A");

        // We have a menu item to show in action bar.
        setHasOptionsMenu(true);

        // Create an empty adapter we will use to display the loaded data.
        itemsAdapter = new TrackListAdapter(getActivity());

        // Start out with a progress indicator.
        setListShown(false);

    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Track selectedTrack = itemsAdapter.getItem(position);
        onTrackClick(selectedTrack);
        super.onListItemClick(l, v, position, id);
    }

    @Override
    public void onStart() {
        super.onStart();
        Bundle argsRecentTracks = new Bundle();
        argsRecentTracks.putString("USERNAME", query);
        getLoaderManager().initLoader(USER_RECENT_TRACKS_LOADER, argsRecentTracks, this);
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        if (id == USER_RECENT_TRACKS_LOADER) {
            return new LastFmOperations.UserRecentTracks(getActivity(), args.getString("USERNAME"));
        } else {
            return null;
        }
    }

    @Override
    public void onLoadFinished(Loader loader, Object o) {
        int i = loader.getId();
        if (i == USER_RECENT_TRACKS_LOADER) {
            if (o == null) {
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        onNoResultFoundListener.onNoResultFound(query);
                    }
                });
            }
            itemsAdapter.setData((List<Track>) o);
            setListAdapter(itemsAdapter);

            setListShown(true);
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {
        switch (loader.getId()) {
            case USER_RECENT_TRACKS_LOADER:
                itemsAdapter.setData(null);
                break;
        }
    }

    public void onTrackClick(Track track) {
        if (chansonRecenteFragmentListener != null) {
            chansonRecenteFragmentListener.onTrackSelected(track);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        chansonRecenteFragmentListener = (ChansonRecenteFragmentListener) activity;

    }

    @Override
    public void onDetach() {
        super.onDetach();
        chansonRecenteFragmentListener = null;
    }


}
