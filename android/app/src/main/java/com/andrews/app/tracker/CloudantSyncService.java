/*
 * Copyright (c) 2017 Bartek Fabiszewski
 * http://www.fabiszewski.net
 *
 * This file is part of μlogger-android.
 * Licensed under GPL, either version 3, or any later.
 * See <http://www.gnu.org/licenses/>
 */

package com.andrews.app.tracker;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.cloudant.http.interceptors.BasicAuthInterceptor;
import com.cloudant.sync.documentstore.DocumentStore;
import com.cloudant.sync.documentstore.DocumentStoreException;
import com.cloudant.sync.documentstore.DocumentStoreNotOpenedException;
import com.cloudant.sync.event.Subscribe;
import com.cloudant.sync.event.notifications.ReplicationCompleted;
import com.cloudant.sync.event.notifications.ReplicationErrored;
import com.cloudant.sync.replication.Replicator;
import com.cloudant.sync.replication.ReplicatorBuilder;
import com.worklight.jsonstore.exceptions.JSONStoreCloseAllException;
import com.worklight.jsonstore.exceptions.JSONStoreFileAccessException;
import com.worklight.jsonstore.exceptions.JSONStoreInvalidPasswordException;
import com.worklight.jsonstore.exceptions.JSONStoreInvalidSchemaException;
import com.worklight.jsonstore.exceptions.JSONStoreMigrationException;
import com.worklight.jsonstore.exceptions.JSONStoreSchemaMismatchException;
import com.worklight.jsonstore.exceptions.JSONStoreTransactionDuringInitException;

import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.NoRouteToHostException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;


import static android.app.PendingIntent.FLAG_ONE_SHOT;

/**
 * Service synchronizing local database positions with remote server.
 *
 */

public class CloudantSyncService extends IntentService {

    private static final String TAG = CloudantSyncService.class.getSimpleName();
    public static final String BROADCAST_SYNC_FAILED = "net.fabiszewski.ulogger.broadcast.sync_failed";
    public static final String BROADCAST_SYNC_DONE = "net.fabiszewski.ulogger.broadcast.sync_done";
    String deviceId;
    private LocationAdapter mTaskAdapter;
    private DbAccess db;
    private static boolean isAuthorized = false;
    private static PendingIntent pi = null;

    final private static int FIVE_MINUTES = 1000 * 60 * 5;
    static final String PARAM_TRACKID = "trackid";
    static final String PARAM_TIME = "time";
    static final String PARAM_LAT = "lat";
    static final String PARAM_LON = "lon";
    static final String PARAM_ALT = "altitude";
    static final String PARAM_SPEED = "speed";
    static final String PARAM_BEARING = "bearing";
    static final String PARAM_ACCURACY = "accuracy";
    static final String PARAM_PROVIDER = "provider";
    /**
     * Constructor
     */
    public CloudantSyncService() {
        super("CloudantSyncService");
    }
    // Main data model object
    private DocumentStore mDocumentStore;

    private static LocationsModel sTasks;
    private static final String DOCUMENT_STORE_DIR = "data";
    private static final String DOCUMENT_STORE_NAME = "tasks";
    @SuppressLint("HardwareIds")
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "[CloudantSyncService create]");
        deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        db = DbAccess.getInstance();
        db.open(this);
        if (sTasks == null) {
            // Model needs to stay in existence for lifetime of app.
            try {
                this.sTasks = new LocationsModel(this.getApplicationContext());
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
       this.sTasks.setReplicationListener(this);
//         Username/password are supplied in the URL and can be Cloudant API keys
                File path2 = this.getApplicationContext().getDir(
                DOCUMENT_STORE_DIR,
                Context.MODE_PRIVATE
        );


        try {
            this.mDocumentStore = DocumentStore.getInstance(new File(path2, DOCUMENT_STORE_NAME));
        } catch (DocumentStoreNotOpenedException e) {
            Log.e(TAG, "Unable to open DocumentStore", e);
       }


        URI uri2 = null;
        try {
            uri2 = new URI("https://50c7e1e9-f42d-4571-8264-6db36cec19e6-bluemix:96b969836e91f94661cba0fbc9e4fa38e0984cc16de8818e23d87af1ff91c482@50c7e1e9-f42d-4571-8264-6db36cec19e6-bluemix.cloudant.com/raw_location_updates");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
//        Create the pull replicator
        Replicator pullReplicator = ReplicatorBuilder.pull().from(uri2).to(   this.mDocumentStore).build();

// Create the push replicator
        Replicator pushReplicator = ReplicatorBuilder.push().to(uri2).from(   this.mDocumentStore).build();

// Use a latch starting at 2 as we're waiting for two replications to finish
        CountDownLatch latch = new CountDownLatch(2);
       Listener listener = new  Listener(latch);


// Set the listener and start for both pull and push replications
        pullReplicator.getEventBus().register(listener);
        pullReplicator.start();
        pushReplicator.getEventBus().register(listener);
        pushReplicator.start();

// Wait for both replications to complete, decreasing the latch via listeners
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

// Unsubscribe the listeners
        pullReplicator.getEventBus().unregister(listener);
        pushReplicator.getEventBus().unregister(listener);

        if (pullReplicator.getState() != Replicator.State.COMPLETE) {
            System.err.println("Error replicating FROM remote");
            System.err.println(listener.errors);
        } else if (pushReplicator.getState() != Replicator.State.COMPLETE) {
            System.err.println("Error replicating TO remote");
            System.err.println(listener.errors);
        } else {
            System.out.println(String.format("Replicated %d documents in %d batches",
                    listener.documentsReplicated, listener.batchesReplicated));
        }

    }

    /**
     * Handle synchronization intent
     * @param intent Intent
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "[CloudantSyncService start]");

        if (pi != null) {
            // cancel pending alarm
            if (Logger.DEBUG) { Log.d(TAG, "[websync cancel alarm]"); }
            AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if (am != null) {
                am.cancel(pi);
            }
            pi = null;
        }

//        if (!isAuthorized) {
//            try {
//                parse.authorize();
//            } catch (WebAuthException|IOException |JSONException e) {
//                handleError(e);
//                return;
//            }

            isAuthorized = true;
//        }

        // get track id
        int trackId = getTrackId();
        if (trackId > 0) {
            doSync(trackId);
        }
    }

    /**
     * Get track id
     * If the track hasn't been registered on server yet,
     * get set up new track on the server and get new id
     * @return Track id
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    private int getTrackId() {
        int trackId = db.getTrackId();
        if (trackId == 0) {
            String trackName = db.getTrackName();
                trackId = 1;
            db.setTrackId(trackId);
            return trackId;
        }
        return trackId;

    }
    private void createNewTask(Map<String, String> params) {
        Location t = new Location(params);
        sTasks.createDocument(t);
        reloadTasksFromModel();
    }

    /**
     * Synchronize all positions in database.
     * Skips already synchronized, uploads new ones
     * @param trackId Current track id
     */
    private void doSync(int trackId) {
        Log.d(TAG, "do sync");
        Cursor cursor = db.getUnsynced();
        try {
            while (cursor.moveToNext()) {
                int rowId = cursor.getInt(cursor.getColumnIndex(DbContract.Positions._ID));
                Map<String, String> params = cursorToMap(cursor);
                params.put(PARAM_TRACKID, String.valueOf(trackId));
                params.put("deviceId", deviceId);
                createNewTask(params);
                db.setSynced(rowId);
                Intent intent = new Intent(BROADCAST_SYNC_DONE);
                sendBroadcast(intent);
            }
        } catch (Exception e) {
            if (Logger.DEBUG) { Log.d(TAG, "[websync io exception: " + e + "]"); }
            isAuthorized = false;
                isAuthorized = true;
                doSync(trackId);
        } finally {
            cursor.close();
        }
    }

    /**
     * Actions performed in case of synchronization error.
     * Send broadcast to main activity, schedule retry if tracking is on.
     *
     * @param e Exception
     */
    private void handleError(Exception e) {
        String message;
        if (e instanceof UnknownHostException) {
//            message = getString(R.string.e_unknown_host, e.getMessage());
        } else if (e instanceof MalformedURLException || e instanceof URISyntaxException) {
//            message = getString(R.string.e_bad_url, e.getMessage());
        } else if (e instanceof ConnectException || e instanceof NoRouteToHostException) {
//            message = getString(R.string.e_connect, e.getMessage());
        } else {
            message = e.getMessage();
        }
        message = e.getMessage();
        if (Logger.DEBUG) { Log.d(TAG, "[websync retry: " + message + "]"); }

        db.setError(message);
        Intent intent = new Intent(BROADCAST_SYNC_FAILED);
        intent.putExtra("message", message);
        sendBroadcast(intent);
        // retry only if tracking is on
        if (LoggerService.isRunning()) {
            if (Logger.DEBUG) { Log.d(TAG, "[websync set alarm]"); }
            AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Intent syncIntent = new Intent(getApplicationContext(), CloudantSyncService.class);
            pi = PendingIntent.getService(this, 0, syncIntent, FLAG_ONE_SHOT);
            if (am != null) {
                am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + FIVE_MINUTES, pi);
            }
        }
    }

    /**
     * Convert cursor to map of request parameters
     *
     * @param cursor Cursor
     * @return Map of parameters
     */
    private Map<String, String> cursorToMap(Cursor cursor) {
        Map<String, String> params = new HashMap<>();
        params.put(PARAM_TIME, DbAccess.getTime(cursor));
        params.put(PARAM_LAT, DbAccess.getLatitude(cursor));
        params.put(PARAM_LON, DbAccess.getLongitude(cursor));
        if (DbAccess.hasAltitude(cursor)) {
            params.put(PARAM_ALT, DbAccess.getAltitude(cursor));
        }
        if (DbAccess.hasSpeed(cursor)) {
            params.put(PARAM_SPEED, DbAccess.getSpeed(cursor));
        }
        if (DbAccess.hasBearing(cursor)) {
            params.put(PARAM_BEARING, DbAccess.getBearing(cursor));
        }
        if (DbAccess.hasAccuracy(cursor)) {
            params.put(PARAM_ACCURACY, DbAccess.getAccuracy(cursor));
        }
        if (DbAccess.hasProvider(cursor)) {
            params.put(PARAM_PROVIDER, DbAccess.getProvider(cursor));
        }
        return params;
    }

    /**
     * Cleanup
     */
    @Override
    public void onDestroy() {
        if (Logger.DEBUG) { Log.d(TAG, "[websync stop]"); }
        if (db != null) {
            db.close();
        }
        super.onDestroy();
    }


    private void reloadTasksFromModel() {
        try {
            List<Location> tasks = this.sTasks.allTasks();
            this.mTaskAdapter = new LocationAdapter(this, tasks);
//            this.setListAdapter(this.mTaskAdapter);
        } catch (DocumentStoreException e) {
            throw new RuntimeException(e);
        }
    }
    //
    // HELPER METHODS
    //

//    private void reloadReplicationSettings() {
//        try {
//            this.sTasks.reloadReplicationSettings();
//        } catch (URISyntaxException e) {
//            Log.e(TAG, "Unable to construct remote URI from configuration", e);
//
//        }
//    }

    /**
     * Called by TasksModel when it receives a replication complete callback.
     * TasksModel takes care of calling this on the main thread.
     */
    void replicationComplete() {
        reloadTasksFromModel();
//        Toast.makeText(getApplicationContext(),
//                R.string.replication_completed,
//                Toast.LENGTH_LONG).show();
//        dismissDialog(DIALOG_PROGRESS);
    }

    /**
     * Called by TasksModel when it receives a replication error callback.
     * TasksModel takes care of calling this on the main thread.
     */
    void replicationError() {
        Log.i(TAG, "error()");
        reloadTasksFromModel();

    }

}
