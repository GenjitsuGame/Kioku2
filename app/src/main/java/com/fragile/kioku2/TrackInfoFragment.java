package com.fragile.kioku2;

import android.app.Activity;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.concurrent.ExecutionException;


/**
 * A simple {@link Fragment} subclass.
 * to handle interaction fragment_events_layout.
 */
public class TrackInfoFragment extends Fragment {

    private TextView trackName;
    private TextView trackArtist;
    private TextView trackAlbum;
    private ImageView albumArt;
    private TextView lastTimePlayed;
    private TextView playCount;
    private TextView friendListening;


    public TrackInfoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.song_detail, container, false);

        if (getArguments() == null) {
            return view;
        }

        Bundle args = getArguments();

        trackName = (TextView) view.findViewById(R.id.idTitle);

        trackArtist = (TextView) view.findViewById(R.id.idArtist);

        trackAlbum = (TextView) view.findViewById(R.id.idAlbum);

        albumArt = (ImageView) view.findViewById(R.id.cover);

        trackName.setText(args.getString("TITLE"));

        trackArtist.setText(args.getString("ARTIST"));

        trackAlbum.setText(args.getString("ALBUM"));

        try {
            Bitmap bm = new LastFmOperations.FetchImageTask().execute(args.getString("IMAGE_URL")).get();
            if (bm != null) {
                albumArt.setImageBitmap(bm);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }


}
