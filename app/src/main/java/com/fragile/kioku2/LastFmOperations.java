package com.fragile.kioku2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.fragile.utils.loaders.AsyncLoader;
import com.fragile.utils.loaders.OnFinishedDataLoaderWrapper;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import fm.last.android.AndroidLastFmServerFactory;
import fm.last.api.Artist;
import fm.last.api.Event;
import fm.last.api.Friends;
import fm.last.api.LastFmServer;
import fm.last.api.Tag;
import fm.last.api.Track;
import fm.last.api.User;
import fm.last.api.WSError;


public class LastFmOperations {

    private final static LastFmServer server = AndroidLastFmServerFactory.getServer();
    private final static LastFmServer secureServer = AndroidLastFmServerFactory.getSecureServer();


    public static final class Connection extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... strings) {
            String login = strings[0];
            String password = strings[1];

            try {
                KiokuApplication.setSession(secureServer.getMobileSession(login, password));
                return true;
            } catch (IOException e) {
                return false;
            } catch (WSError e) {
                return false;
            }
        }
    }

    public static final class Scrobble extends AsyncTask<Bundle, Void, Void> {
        @Override
        protected Void doInBackground(Bundle... bundles) {
            try {
                for (int i = 0; i < bundles.length; ++i) {
                    secureServer.scrobbleTrack(bundles[i].getString("ARTIST"), bundles[i].getString("TRACK"), bundles[i].getString("ALBUM"),
                            bundles[i].getLong("TIMESTAMP"), bundles[i].getLong("DURATION", 0), KiokuApplication.APPLICATION_NAME, null, KiokuApplication.getSession().getKey());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    public static final class UserFriends extends AsyncLoader<ArrayList<User>> {

        private String username;
        private String recentTracks;
        private String limit;

        public UserFriends(Context context, Bundle args) {
            super(context);
            this.username = args.getString("USERNAME");
            this.recentTracks = args.getString("RECENT_TRACKS");
            this.limit = args.getString("LIMIT");
        }

        @Override
        public ArrayList<User> loadInBackground() {
            ArrayList<User> ret = new ArrayList<User>();
            try {
                ret.addAll(Arrays.asList(server.getFriends(username, recentTracks, limit).getFriends()));
            } catch (IOException e) {
                return null;
            } catch (WSError e) {
                return null;
            }
            return  ret;
        }
    }

   public static final class UserNeighbours extends AsyncTask<Bundle,Void, Void> {
       @Override
       protected Void doInBackground(Bundle... bundles) {

           return null;
       }
   }

    public static final class UserTopArtists extends AsyncLoader<List<Artist>> {

        private final String username;

        private final String period;

        private final String limit;

        public UserTopArtists(Context context, Bundle args) {
            super(context);
            username = args.getString("USERNAME");
            period = args.getString("PERIOD");
            limit = args.getString("LIMIT");
        }

        @Override
        public List<Artist> loadInBackground() {
            ArrayList<Artist> ret = new ArrayList<Artist>();
            try {
                ret.addAll(Arrays.asList(server.getUserTopArtists(username, period)));
            } catch (IOException e) {
                return null;
            } catch (WSError e) {
                return null;
            }
            return ret;
        }
    }

    public static final class UserInfo extends AsyncTask<String, Void, User> {

        @Override
        protected User doInBackground(String... strings) {
            User ret = null;
            try {
                ret = server.getUserInfo(strings[0], KiokuApplication.getSession().getKey());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return ret;
        }
    }

    public static final class UpdateNowPlaying extends AsyncTask<Bundle, Void, Void> {
        @Override
        protected Void doInBackground(Bundle... bundles) {
            try {
                for (int i = 0; i < bundles.length; ++i) {
                    secureServer.updateNowPlaying(bundles[i].getString("ARTIST"), bundles[i].getString("TRACK"), bundles[i].getString("ALBUM"),
                            bundles[i].getLong("DURATION"), KiokuApplication.APPLICATION_NAME, KiokuApplication.getSession().getKey());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    public static class UserRecentTracks extends AsyncLoader<List<Track>> {

        private String username;
        private int limit;

        public UserRecentTracks(Context context, String username) {
            super(context);
            this.username = username;
            this.limit = 20;
        }

        public UserRecentTracks(Context context, String username, int limit) {
            super(context);
            this.username = username;
            this.limit = limit;
        }

        @Override
        public ArrayList<Track> loadInBackground() {

            ArrayList<Track> ret = new ArrayList<Track>();
            try {
               ret.addAll(Arrays.asList( server.getUserRecentTracks(username, limit, 0, true)));
            } catch (IOException e) {
                return null;
            } catch (WSError e) {
                return null;
            }
            return ret;
        }
    }

    public static class FetchImageTask extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... strings) {
            String url = strings[0];
            Bitmap bm = null;
            try {
                if (url == null || url.length() == 0) {
                    return null;
                }

                URL aURL = new URL(url);
                URLConnection conn = aURL.openConnection();
                conn.connect();
                InputStream is = conn.getInputStream();
                BufferedInputStream bis = new BufferedInputStream(is);
                bm = BitmapFactory.decodeStream(bis);
                bis.close();
                is.close();

            } catch (IOException e) {
                Log.e(null, "Error getting bitmap", e);
            }
            return bm;
        }
    }

    public static class FetchImage extends OnFinishedDataLoaderWrapper<Bitmap> {

        private String url;

        public FetchImage(Context context, String url, Bundle onFinishData) {
            super(context, onFinishData);
            this.url = url;
        }

        @Override
        public Bitmap loadInBackground() {
            Bitmap bm = null;
            try {
                if (url == null || url.length() == 0) {
                    return null;
                }

                URL aURL = new URL(url);
                URLConnection conn = aURL.openConnection();
                conn.connect();
                InputStream is = conn.getInputStream();
                BufferedInputStream bis = new BufferedInputStream(is);
                bm = BitmapFactory.decodeStream(bis);
                bis.close();
                is.close();

            } catch (IOException e) {
                Log.e(null, "Error getting bitmap", e);
            }
            return bm;
        }
    }

    public static class retrieveTrackInfo extends AsyncLoader<HashMap<String, Object>> {

        private Track track;

        public retrieveTrackInfo(Context context, Track track) {
            super(context);
            this.track = track;
        }

        @Override
        public HashMap<String, Object> loadInBackground() {

            HashMap<String, Object> result = new HashMap<String, Object>();
            try {
                if (KiokuApplication.isSessionSet()) {
                    track = server.getTrackInfo(track.getName(), track.getArtist().getName(), null, KiokuApplication.getSession().getName(), true);
                }
                User[] topFans = server.getTrackTopFans(track.getName(), track.getArtist().getName(), null);
                Tag[] topTags = server.getTrackTags(track.getName(), track.getArtist().getName(), null);

                User[] top3Fans = Arrays.copyOf(topFans, 3);
                Tag[] top5Tags = Arrays.copyOf(topTags, 5);
                result.put("TOP_FANS", top3Fans);
                result.put("TOP_TAGS", top5Tags);
                result.put("TRACK", track);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

    }

    public static class UserEvents extends AsyncLoader<List<Event>> {

        private String username;

        public UserEvents(Context context, String username) {
            super(context);
            this.username = username;
        }

        @Override
        public ArrayList<Event> loadInBackground() {
            ArrayList<Event> ret = new ArrayList<Event>();
            try {
                Collections.addAll(ret, server.getUserEvents(username));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return ret;
        }
    }


}
