package com.fragile.kioku2;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fragile.utils.adapters.CustomArrayAdapter;
import com.fragile.utils.fragments.QuerySubmitListFragment;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import fm.last.api.Event;

/**
 * A fragment representing a list of Items.
 */
public class EventsFragment extends QuerySubmitListFragment implements LoaderManager.LoaderCallbacks {

    private static final int EVENTS_LOADER = 1;

    private TextView eventName;

    private TextView date;

    private TextView place;

    private ImageView thumbnail;

    private EventListAdapter itemsAdapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public EventsFragment() {
    }



    @Override
    public Loader onCreateLoader(int i, Bundle bundle) {
        if (i == EVENTS_LOADER) {
            return new LastFmOperations.UserEvents(getActivity(), bundle.getString("USERNAME"));
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader loader, Object o) {
        if (loader.getId() == EVENTS_LOADER) {
            if(o == null) {
                onNoResultFoundListener.onNoResultFound(query);
            }
            ArrayList<Event> events = (ArrayList<Event>) o;
            itemsAdapter.setData(events);
            setListAdapter(itemsAdapter);
            if (isResumed()) {
                setListShown(true);
            } else {
                setListShownNoAnimation(true);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {
        if (loader.getId() == EVENTS_LOADER) {
            itemsAdapter.setData(null);
        }

    }

    private class EventListAdapter extends CustomArrayAdapter<Event> {

        public EventListAdapter(Context context) {
            super(context, R.layout.listview_layout);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view;

            if (convertView == null) {
                view = mInflater.inflate(R.layout.fragment_events_layout, parent, false);
            } else {
                view = convertView;
            }

            eventName = (TextView) view.findViewById(R.id.idTitle);

            date = (TextView) view.findViewById(R.id.idArtist);

            place = (TextView) view.findViewById(R.id.idAlbum);

            thumbnail = (ImageView) view.findViewById(R.id.list_image);

            Event event = getItem(position);

            eventName.setText(event.getTitle());

            date.setText(event.getStartDate().toString());

            place.setText(event.getVenue().getName() + " - " + event.getVenue().getLocation().getCity());

            try {
                Bitmap bm = new LastFmOperations.FetchImageTask().execute(event.getURLforImageSize("large")).get();
                thumbnail.setImageBitmap(bm);
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
        itemsAdapter = new EventListAdapter(getActivity());


        // Start out with a progress indicator.
        setListShown(false);
    }


    @Override
        public void onQueryTextSubmitting() {
        Bundle argsRecentTracks = new Bundle();
        argsRecentTracks.putString("USERNAME", query);
        getLoaderManager().destroyLoader(EVENTS_LOADER);
        getLoaderManager().restartLoader(EVENTS_LOADER, argsRecentTracks, this);
    }

    @Override
    public void onStart() {
        super.onStart();
        Bundle argsRecentTracks = new Bundle();
        argsRecentTracks.putString("USERNAME", query);
        getLoaderManager().initLoader(EVENTS_LOADER, argsRecentTracks, this);
    }

}
