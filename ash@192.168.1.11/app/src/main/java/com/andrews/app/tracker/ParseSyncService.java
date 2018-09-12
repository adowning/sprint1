///*
// * Copyright (c) 2017 Bartek Fabiszewski
// * http://www.fabiszewski.net
// *
// * This file is part of μlogger-android.
// * Licensed under GPL, either version 3, or any later.
// * See <http://www.gnu.org/licenses/>
// */
//
//package com.andrews.app.tracker;
//
//import android.annotation.SuppressLint;
//import android.app.AlarmManager;
//import android.app.IntentService;
//import android.app.PendingIntent;
//import android.content.Context;
//import android.content.Intent;
//import android.database.Cursor;
//import android.icu.text.SimpleDateFormat;
//import android.icu.util.Calendar;
//import android.os.Build;
//import android.provider.Settings;
//import android.support.annotation.RequiresApi;
//import android.util.Log;
//
//import com.cloudant.sync.documentstore.DocumentStore;
//import com.cloudant.sync.documentstore.DocumentStoreException;
//import com.cloudant.sync.documentstore.DocumentStoreNotDeletedException;
//import com.cloudant.sync.documentstore.DocumentStoreNotOpenedException;
//import com.cloudant.sync.replication.Replicator;
//import com.cloudant.sync.replication.ReplicatorBuilder;
//import com.parse.Parse;
//import com.parse.ParseObject;
//
//import org.json.JSONException;
//
//import java.io.File;
//import java.io.IOException;
//import java.net.ConnectException;
//import java.net.MalformedURLException;
//import java.net.NoRouteToHostException;
//import java.net.URI;
//import java.net.URISyntaxException;
//import java.net.UnknownHostException;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import static android.app.PendingIntent.FLAG_ONE_SHOT;
//
///**
// * Service synchronizing local database positions with remote server.
// *
// */
//
//public class ParseSyncService extends IntentService {
//
//    private static final String TAG = ParseSyncService.class.getSimpleName();
//    public static final String BROADCAST_SYNC_FAILED = "net.fabiszewski.ulogger.broadcast.sync_failed";
//    public static final String BROADCAST_SYNC_DONE = "net.fabiszewski.ulogger.broadcast.sync_done";
//    String deviceId;
//
//    private DbAccess db;
//    private WebHelper web;
//    private ParseHelper parse;
//    private static boolean isAuthorized = false;
//    private static PendingIntent pi = null;
//
//    final private static int FIVE_MINUTES = 1000 * 60 * 5;
//
//
//    /**
//     * Constructor
//     */
//    public ParseSyncService() {
//        super("ParseSyncService");
//    }
//    // Main data model object
//    private DocumentStore mDocumentStore;
//
//    private static LocationsModel sTasks;
//    private static final String DOCUMENT_STORE_DIR = "data";
//    private static final String DOCUMENT_STORE_NAME = "tasks";
//    private LocationAdapter mTaskAdapter;
//    @SuppressLint("HardwareIds")
//    @Override
//    public void onCreate() {
//        super.onCreate();
//        Log.d(TAG, "[parseSync create]");
//      File path = getBaseContext().getDir("documentstores", Context.MODE_PRIVATE);
//
//        deviceId = Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
//        parse = new ParseHelper(this);
//        db = DbAccess.getInstance();
//        db.open(this);
//        if (sTasks == null) {
//            // Model needs to stay in existence for lifetime of app.
//            try {
//                this.sTasks = new LocationsModel(this.getApplicationContext());
//            } catch (URISyntaxException e) {
//                e.printStackTrace();
//            }
//        }
//
//        // Register this activity as the listener to replication updates
//        // while its active.
////        this.sTasks.setReplicationListener(this);
//        URI uri = null;
//        File path2 = this.getApplicationContext().getDir(
//                DOCUMENT_STORE_DIR,
//                Context.MODE_PRIVATE
//        );
//
//        try {
//            this.mDocumentStore = DocumentStore.getInstance(new File(path2, DOCUMENT_STORE_NAME));
//        } catch (DocumentStoreNotOpenedException e) {
//            Log.e(TAG, "Unable to open DocumentStore", e);
//        }
//        try {
//            Log.d(TAG, "uri xxxxx");
//            String username = "8e41cb6a-9660-44fc-a60e-3cbd4c952423-bluemix";
//            String dbName = "raw_location_updates";
//            String apiKey = "neciandervendisymandarda";
//            String apiSecret = "a52173490312d0b5aabdb38e3647138a898741a0";
//            String host = username + ".cloudant.com";
//
//            // We recommend always using HTTPS to talk to Cloudant.
//            uri = new  URI("https", apiKey + ":" + apiSecret, host, 443, "/" + dbName, null, null);
////            uri = new URI("http://74.192.104.240:8082/locationupdates");
//          //  uri = new URI("https://50c7e1e9-f42d-4571-8264-6db36cec19e6-bluemix:96b969836e91f94661cba0fbc9e4fa38e0984cc16de8818e23d87af1ff91c482@50c7e1e9-f42d-4571-8264-6db36cec19e6-bluemix.cloudant.com/raw_location_updates");
////        uri = new URI("https://iHknLlWH7Ep1p9wKHnNkuTi1rIQcnDldQ_rp4fBKnVx8:96b969836e91f94661cba0fbc9e4fa38e0984cc16de8818e23d87af1ff91c482@50c7e1e9-f42d-4571-8264-6db36cec19e6-bluemix.cloudant.com/raw_location_updates");
////            uri = new URI("https", "iHknLlWH7Ep1p9wKHnNkuTi1rIQcnDldQ_rp4fBKnVx8" + ":" + "96b969836e91f94661cba0fbc9e4fa38e0984cc16de8818e23d87af1ff91c482", "50c7e1e9-f42d-4571-8264-6db36cec19e6-bluemix.cloudant.com", 443, "/" + "raw_location_updates", null, null);
////          uri = new URI( "https://50c7e1e9-f42d-4571-8264-6db36cec19e6-bluemix:96b969836e91f94661cba0fbc9e4fa38e0984cc16de8818e23d87af1ff91c482@50c7e1e9-f42d-4571-8264-6db36cec19e6-bluemix.cloudant.com");
////            return new URI("https://iHknLlWH7Ep1p9wKHnNkuTi1rIQcnDldQ_rp4fBKnVx8:96b969836e91f94661cba0fbc9e4fa38e0984cc16de8818e23d87af1ff91c482@50c7e1e9-f42d-4571-8264-6db36cec19e6-bluemix.cloudant.com/raw_location_updates");
//
//        } catch (URISyntaxException e) {
//            e.printStackTrace();
//        }
//        Log.d(TAG, uri.toString());
//
//        Replicator replicator = ReplicatorBuilder.push()
//                .from(this.mDocumentStore)
//                .to(uri)
////                .iamApiKey("iHknLlWH7Ep1p9wKHnNkuTi1rIQcnDldQ_rp4fBKnVx8")
//                .build();
////        Replicator replicator = ReplicatorBuilder.push().from( this.mDocumentStore).to(uri).build();
//// Fire-and-forget (there are easy ways to monitor the state too)
////        replicator.start();
//    }
//
//    /**
//     * Handle synchronization intent
//     * @param intent Intent
//     */
//    @RequiresApi(api = Build.VERSION_CODES.N)
//    @Override
//    protected void onHandleIntent(Intent intent) {
//        Log.d(TAG, "[parseSync start]");
//
//        if (pi != null) {
//            // cancel pending alarm
//            if (Logger.DEBUG) { Log.d(TAG, "[websync cancel alarm]"); }
//            AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//            if (am != null) {
//                am.cancel(pi);
//            }
//            pi = null;
//        }
//
//        if (!isAuthorized) {
//            try {
//                parse.authorize();
//            } catch (WebAuthException|IOException |JSONException e) {
//                handleError(e);
//                return;
//            }
//
//            isAuthorized = true;
//        }
//
//        // get track id
//        int trackId = getTrackId();
//        if (trackId > 0) {
//            doSync(trackId);
//        }
//    }
//
//    /**
//     * Get track id
//     * If the track hasn't been registered on server yet,
//     * get set up new track on the server and get new id
//     * @return Track id
//     */
//    @RequiresApi(api = Build.VERSION_CODES.N)
//    private int getTrackId() {
//        int trackId = db.getTrackId();
//        if (trackId == 0) {
//            String trackName = db.getTrackName();
//                trackId = 1;
////                trackId = web.startTrack(trackName);
////            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
////            db.setTrackId(Integer.valueOf(timeStamp));
//            db.setTrackId(trackId);
////                db.setTrackId(trackId);
//
//            // schedule retry
////                handleError(e);
//
////                isAuthorized = false;
//
////                    // reauthorize and retry
////                    web.authorize();
////                    isAuthorized = true;
////                    trackId = web.startTrack(trackName);
////                    db.setTrackId(trackId);
////                } catch (WebAuthException|IOException|JSONException e2) {
////                    // schedule retry
////                    handleError(e2);
//
//
////             timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
//            return trackId;
////        return trackId;
//        }
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
//        return trackId;
//
////        return Integer.valueOf(timeStamp);
//
//    }
//    private void createNewTask(Map<String, String> params) {
//        Location t = new Location(params);
//        sTasks.createDocument(t);
//        reloadTasksFromModel();
//    }
//
//    /**
//     * Synchronize all positions in database.
//     * Skips already synchronized, uploads new ones
//     * @param trackId Current track id
//     */
//    private void doSync(int trackId) {
//        Log.d(TAG, "do sync");
//        // iterate over positions in db
//        Cursor cursor = db.getUnsynced();
//        // suppress as it requires target api 19
//        //noinspection TryFinallyCanBeTryWithResources
//        try {
//            while (cursor.moveToNext()) {
//                int rowId = cursor.getInt(cursor.getColumnIndex(DbContract.Positions._ID));
//                Map<String, String> params = cursorToMap(cursor);
//                params.put(WebHelper.PARAM_TRACKID, String.valueOf(trackId));
//                params.put("deviceId", deviceId);
//
////                web.postPosition(params);
//                parse.postPosition(params);
//                createNewTask(params);
//                db.setSynced(rowId);
//                Intent intent = new Intent(BROADCAST_SYNC_DONE);
//                sendBroadcast(intent);
//            }
//        } catch (Exception e) {
//            // handle web errors
//            if (Logger.DEBUG) { Log.d(TAG, "[websync io exception: " + e + "]"); }
//            // schedule retry
//         //   handleError(e);
//
////            if (Logger.DEBUG) { Log.d(TAG, "[websync auth exception: " + e + "]"); }
//            isAuthorized = false;
//
//                // reauthorize and retry
//          //      web.authorize();
//                isAuthorized = true;
//                doSync(trackId);
//
//                // schedule retry
//         //       handleError(e2);
//
//        } finally {
//            cursor.close();
//        }
//    }
//
//    /**
//     * Actions performed in case of synchronization error.
//     * Send broadcast to main activity, schedule retry if tracking is on.
//     *
//     * @param e Exception
//     */
//    private void handleError(Exception e) {
//        String message;
//        if (e instanceof UnknownHostException) {
////            message = getString(R.string.e_unknown_host, e.getMessage());
//        } else if (e instanceof MalformedURLException || e instanceof URISyntaxException) {
////            message = getString(R.string.e_bad_url, e.getMessage());
//        } else if (e instanceof ConnectException || e instanceof NoRouteToHostException) {
////            message = getString(R.string.e_connect, e.getMessage());
//        } else {
//            message = e.getMessage();
//        }
//        message = e.getMessage();
//        if (Logger.DEBUG) { Log.d(TAG, "[websync retry: " + message + "]"); }
//
//        db.setError(message);
//        Intent intent = new Intent(BROADCAST_SYNC_FAILED);
//        intent.putExtra("message", message);
//        sendBroadcast(intent);
//        // retry only if tracking is on
//        if (LoggerService.isRunning()) {
//            if (Logger.DEBUG) { Log.d(TAG, "[websync set alarm]"); }
//            AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//            Intent syncIntent = new Intent(getApplicationContext(), ParseSyncService.class);
//            pi = PendingIntent.getService(this, 0, syncIntent, FLAG_ONE_SHOT);
//            if (am != null) {
//                am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + FIVE_MINUTES, pi);
//            }
//        }
//    }
//
//    /**
//     * Convert cursor to map of request parameters
//     *
//     * @param cursor Cursor
//     * @return Map of parameters
//     */
//    private Map<String, String> cursorToMap(Cursor cursor) {
//        Map<String, String> params = new HashMap<>();
//        params.put(WebHelper.PARAM_TIME, DbAccess.getTime(cursor));
//        params.put(WebHelper.PARAM_LAT, DbAccess.getLatitude(cursor));
//        params.put(WebHelper.PARAM_LON, DbAccess.getLongitude(cursor));
//        if (DbAccess.hasAltitude(cursor)) {
//            params.put(WebHelper.PARAM_ALT, DbAccess.getAltitude(cursor));
//        }
//        if (DbAccess.hasSpeed(cursor)) {
//            params.put(WebHelper.PARAM_SPEED, DbAccess.getSpeed(cursor));
//        }
//        if (DbAccess.hasBearing(cursor)) {
//            params.put(WebHelper.PARAM_BEARING, DbAccess.getBearing(cursor));
//        }
//        if (DbAccess.hasAccuracy(cursor)) {
//            params.put(WebHelper.PARAM_ACCURACY, DbAccess.getAccuracy(cursor));
//        }
//        if (DbAccess.hasProvider(cursor)) {
//            params.put(WebHelper.PARAM_PROVIDER, DbAccess.getProvider(cursor));
//        }
//        return params;
//    }
//
//    /**
//     * Cleanup
//     */
//    @Override
//    public void onDestroy() {
//        if (Logger.DEBUG) { Log.d(TAG, "[websync stop]"); }
//        if (db != null) {
//            db.close();
//        }
//        super.onDestroy();
//    }
//
//
//    private void reloadTasksFromModel() {
//        try {
//            List<Location> tasks = this.sTasks.allTasks();
//            this.mTaskAdapter = new LocationAdapter(this, tasks);
////            this.setListAdapter(this.mTaskAdapter);
//        } catch (DocumentStoreException e) {
//            throw new RuntimeException(e);
//        }
//    }
//    //
//    // HELPER METHODS
//    //
//
////    private void reloadReplicationSettings() {
////        try {
////            this.sTasks.reloadReplicationSettings();
////        } catch (URISyntaxException e) {
////            Log.e(TAG, "Unable to construct remote URI from configuration", e);
////
////        }
////    }
//
//    /**
//     * Called by TasksModel when it receives a replication complete callback.
//     * TasksModel takes care of calling this on the main thread.
//     */
//    void replicationComplete() {
//        reloadTasksFromModel();
////        Toast.makeText(getApplicationContext(),
////                R.string.replication_completed,
////                Toast.LENGTH_LONG).show();
////        dismissDialog(DIALOG_PROGRESS);
//    }
//
//    /**
//     * Called by TasksModel when it receives a replication error callback.
//     * TasksModel takes care of calling this on the main thread.
//     */
//    void replicationError() {
//        Log.i(TAG, "error()");
//        reloadTasksFromModel();
////        Toast.makeText(getApplicationContext(),
////                R.string.replication_error,
////                Toast.LENGTH_LONG).show();
////        dismissDialog(DIALOG_PROGRESS);
//    }
//
//}
