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
//import android.content.Context;
//import android.content.SharedPreferences;
//import android.os.Build;
//import android.preference.PreferenceManager;
//import android.provider.Settings;
//import android.support.annotation.NonNull;
//import android.util.Log;
//
//import com.cloudant.sync.documentstore.AttachmentException;
//import com.cloudant.sync.documentstore.ConflictException;
//import com.cloudant.sync.documentstore.DocumentBodyFactory;
//import com.cloudant.sync.documentstore.DocumentRevision;
//import com.cloudant.sync.documentstore.DocumentStore;
//import com.cloudant.sync.documentstore.DocumentStoreException;
//import com.cloudant.sync.documentstore.UnsavedFileAttachment;
//import com.google.gson.Gson;
//
//import com.getcapacitor.plugin.Storage;
//import com.parse.FindCallback;
//import com.parse.GetCallback;
//import com.parse.Parse;
//import com.parse.ParseException;
//import com.parse.ParseInstallation;
//import com.parse.ParseObject;
//import com.parse.ParseQuery;
//import com.parse.ParseUser;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.BufferedInputStream;
//import java.io.BufferedOutputStream;
//import java.io.BufferedReader;
//import java.io.File;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.io.OutputStream;
//import java.net.CookieHandler;
//import java.net.CookieManager;
//import java.net.CookieStore;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.net.URLEncoder;
//import java.util.Collection;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.List;
//import java.util.ListIterator;
//import java.util.Map;
//
//import javax.net.ssl.HttpsURLConnection;
//import javax.net.ssl.SSLSocketFactory;
//
///**
// * Web server communication
// *
// */
//
//class ParseHelper {
//    private static final String TAG = ParseHelper.class.getSimpleName();
//    private static CookieManager cookieManager = null;
//
//    private static String host;
//    private static String user;
//    private static String pass;
//    private static LocationsModel sTasks;
//
//    private static final String CLIENT_SCRIPT = "client/index.php";
//    private static final String PARAM_ACTION = "action";
//
//    // addpos
//    private static final String ACTION_ADDPOS = "addpos";
//    static final String PARAM_TIME = "time";
//    static final String PARAM_LAT = "lat";
//    static final String PARAM_LON = "lon";
//    static final String PARAM_ALT = "altitude";
//    static final String PARAM_SPEED = "speed";
//    static final String PARAM_BEARING = "bearing";
//    static final String PARAM_ACCURACY = "accuracy";
//    static final String PARAM_PROVIDER = "provider";
//    // todo add comments, images
////    static final String PARAM_COMMENT = "comment";
////    static final String PARAM_IMAGEID = "imageid";
//    static final String PARAM_TRACKID = "trackid";
//
//    // auth
//    private static final String ACTION_AUTH = "auth";
//    // todo adduser not implemented (do we need it?)
////    private static final String ACTION_ADDUSER = "adduser";
//    private static final String PARAM_USER = "user";
//    private static final String PARAM_PASS = "pass";
//
//    // addtrack
//    private static final String ACTION_ADDTRACK = "addtrack";
//    private static final String PARAM_TRACK = "track";
//
//    //    private final String userAgent;
//    private final Context context;
//
//    private static boolean tlsSocketInitialized = false;
//    // Socket timeout in milliseconds
//    static final int SOCKET_TIMEOUT = 30 * 1000;
//    private String deviceId;
//
//    /**
//     * Constructor
//     *
//     * @param ctx Context
//     */
//    ParseHelper(Context ctx) {
//
//        context = ctx;
//        deviceId = Settings.Secure.getString(ctx.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
////        File path = ctx.getDir("documentstores", Context.MODE_PRIVATE);
//
//            // Obtain reference to DocumentStore instance, creating it if doesn't exist
////            DocumentStore ds = DocumentStore.getInstance(new File(path, "my_document_store"));
////
////            // Create a document
////            DocumentRevision revision = new DocumentRevision();
////            Map<String, Object> body = new HashMap<String, Object>();
////            body.put("animal", "cat");
////            revision.setBody(DocumentBodyFactory.create(body));
////            DocumentRevision saved = ds.database().create(revision);
//
//            // Read a document
////            DocumentRevision aRevision = ds.database().read(updated.getId());
//
//
////        userAgent = context.getString(R.string.app_name_ascii) + "/" + BuildConfig.VERSION_NAME + "; " + System.getProperty("http.agent");
//
//        if (cookieManager == null) {
//            cookieManager = new CookieManager();
//            CookieHandler.setDefault(cookieManager);
//        }
//
//        // On API < 19 connection fails if SSL is disabled on server
//        // Try with TLS enabled socket
////        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT && !tlsSocketInitialized) {
////            try {
////                if (Logger.DEBUG) { Log.d(TAG, "[init TLS socket factory]"); }
////                SSLSocketFactory tlsFactory = new TlsSocketFactory(context);
////                HttpsURLConnection.setDefaultSSLSocketFactory(tlsFactory);
////                tlsSocketInitialized = true;
////            } catch (Exception e) {
////                if (Logger.DEBUG) { Log.d(TAG, "[TLS socket setup error (ignored): " + e.getMessage() + "]"); }
////            }
////
////        }
//    }
//
//    /**
//     * Send post request
//     * @param params Request parameters
//     * @return Server response
//     * @throws IOException Connection error
//     * @throws WebAuthException Authorization error
//     */
//    @SuppressWarnings("StringConcatenationInLoop")
//    private String postWithParams(Map<String, String> params) throws IOException, WebAuthException {
//        URL url = new URL(host + "/" + CLIENT_SCRIPT);
//        if (Logger.DEBUG) { Log.d(TAG, "[postWithParams: " + url + " : " + params + "]"); }
//        String response;
//
//        String dataString = "";
//        for (Map.Entry<String, String> p : params.entrySet()) {
//            String key = p.getKey();
//            String value = p.getValue();
//            if (dataString.length() > 0) {
//                dataString += "&";
//            }
//            dataString += URLEncoder.encode(key, "UTF-8") + "=";
//            dataString += URLEncoder.encode(value, "UTF-8");
//        }
//        byte[] data = dataString.getBytes();
//
//        HttpURLConnection connection = null;
//        InputStream in = null;
//        OutputStream out = null;
//        try {
//            boolean redirect;
//            int redirectTries = 5;
//            do {
//                redirect = false;
//                connection = (HttpURLConnection) url.openConnection();
//                connection.setDoOutput(true);
//                connection.setRequestMethod("POST");
//                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
//                connection.setRequestProperty("Content-Length", Integer.toString(data.length));
////                connection.setRequestProperty("User-Agent", userAgent);
//                connection.setInstanceFollowRedirects(false);
//                connection.setConnectTimeout(SOCKET_TIMEOUT);
//                connection.setReadTimeout(SOCKET_TIMEOUT);
//                connection.setUseCaches(true);
//
//                out = new BufferedOutputStream(connection.getOutputStream());
//                out.write(data);
//                out.flush();
//
//                int responseCode = connection.getResponseCode();
//                if (responseCode == HttpURLConnection.HTTP_MOVED_PERM
//                        || responseCode == HttpURLConnection.HTTP_MOVED_TEMP
//                        || responseCode == HttpURLConnection.HTTP_SEE_OTHER
//                        || responseCode == 307) {
//                    URL base = connection.getURL();
//                    String location = connection.getHeaderField("Location");
//                    if (Logger.DEBUG) { Log.d(TAG, "[postWithParams redirect: " + location + "]"); }
//                    if (location == null || redirectTries == 0) {
////                        throw new IOException(context.getString(R.string.e_illegal_redirect, responseCode));
//                    }
//                    redirect = true;
//                    redirectTries--;
//                    url = new URL(base, location);
//                    String h1 = base.getHost();
//                    String h2 = url.getHost();
//                    if (h1 != null && !h1.equalsIgnoreCase(h2)) {
////                        throw new IOException(context.getString(R.string.e_illegal_redirect, responseCode));
//                    }
//                    try {
//                        out.close();
//                        connection.getInputStream().close();
//                        connection.disconnect();
//                    } catch (final IOException e) {
//                        if (Logger.DEBUG) { Log.d(TAG, "[connection cleanup failed (ignored)]"); }
//                    }
//                }
//                else if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
////                    throw new WebAuthException(context.getString(R.string.e_auth_failure, responseCode));
//                }
//                else if (responseCode != HttpURLConnection.HTTP_OK) {
////                    throw new IOException(context.getString(R.string.e_http_code, responseCode));
//                }
//            } while (redirect);
//
//            in = new BufferedInputStream(connection.getInputStream());
//
//            StringBuilder sb = new StringBuilder();
//            BufferedReader br = new BufferedReader(new InputStreamReader(in));
//            String inputLine;
//            while ((inputLine = br.readLine()) != null) {
//                sb.append(inputLine);
//            }
//            response = sb.toString();
//        } finally {
//            try {
//                if (out != null) {
//                    out.close();
//                }
//                if (in != null) {
//                    in.close();
//                }
//                if (connection != null) {
//                    connection.disconnect();
//                }
//            } catch (final IOException e) {
//                if (Logger.DEBUG) { Log.d(TAG, "[connection cleanup failed (ignored)]"); }
//            }
//        }
//        if (Logger.DEBUG) { Log.d(TAG, "[postWithParams response: " + response + "]"); }
//        return response;
//    }
////    /**
////     * Send post request
////     * @param params Request parameters
////     * @return Server response
////     * @throws IOException Connection error
////     * @throws WebAuthException Authorization error
////     */
////    private String postWithParams(String params) throws IOException, WebAuthException {
////
////        return "ok";
////
////}
//private void createNewTask(Map<String, String> params) {
//    Location t = new Location(params);
//    sTasks.createDocument(t);
//
//}
//
//
//    /**
//     * Upload position to server
//     * @param params Map of parameters (position properties)
//     * @throws IOException Connection error
//     * @throws WebAuthException Authorization error
//     */
//    void postPosition(Map<String, String> params) throws IOException, WebAuthException {
//        if (Logger.DEBUG) { Log.d(TAG, "[postPosition] "+ deviceId); }
////        params.put(PARAM_ACTION, ACTION_ADDPOS);
//        // Convert a Map into JSON string.
////        Gson gson = new Gson();
////        String paramsj = gson.toJson(params);
////        System.out.println("json = " + paramsj)
////        String response = postWithParams(params);
//        ParseObject locationUpdate = new ParseObject("LocationUpdate");
//        for (Map.Entry<String, String> p : params.entrySet()) {
//            String key = p.getKey();
//            String value = p.getValue();
//            locationUpdate.put(key, value);
////            if (dataString.length() > 0) {
////                dataString += "&";
////            }
////            dataString += URLEncoder.encode(key, "UTF-8") + "=";
////            dataString += URLEncoder.encode(value, "UTF-8");
//        }
//
//        locationUpdate.put("deviceId", deviceId);
//        locationUpdate.saveInBackground();
////        createNewTask(params);
//        boolean error = true;
////        try {
////            JSONObject resp = new JSONObject(response);
////            error = resp.getBoolean("error");
////        } catch (JSONException e) {
////            if (Logger.DEBUG) { Log.d(TAG, "[postPosition json failed: " + e + "]"); }
////        }
////        if (error) {
//////            throw new IOException(context.getString(R.string.e_server_response));
////        }
//    }
//
//    /**
//     * Start new track on server
//     * @param name Track name
//     * @return Track id
//     * @throws IOException Connection error
//     * @throws WebAuthException Authorization error
//     */
//    int startTrack(String name) throws IOException, WebAuthException {
//        if (Logger.DEBUG) { Log.d(TAG, "[startTrack: " + name + "]"); }
//        Map<String, String> params = new HashMap<>();
//        params.put(PARAM_ACTION, ACTION_ADDTRACK);
//        params.put(PARAM_TRACK, name);
//        try {
//            String response = postWithParams(params);
//            JSONObject json = new JSONObject(response);
//            boolean error = json.getBoolean("error");
//            if (error) {
////                throw new IOException(context.getString(R.string.e_server_response));
//            } else {
//                return json.getInt("trackid");
//            }
//        } catch (JSONException e) {
//            if (Logger.DEBUG) { Log.d(TAG, "[startTrack json failed: " + e + "]"); }
//            throw new IOException(e);
//        }
//        return 0;
//    }
//
//    /**
//     * Authorize on server
//     * @throws IOException Connection error
//     * @throws WebAuthException Authorization error
//     * @throws JSONException Response parsing error
//     */
//    void authorize() throws IOException, WebAuthException, JSONException {
//        Log.d(TAG, "[authorize]");
////        ParseInstallation.getCurrentInstallation().saveInBackground();
//
////        Map<String, String> params = new HashMap<>();
////        params.put(PARAM_ACTION, ACTION_AUTH);
////        params.put(PARAM_USER, user);
////        params.put(PARAM_PASS, pass);
////         boolean b = false;
////        ParseQuery<ParseObject> query = ParseQuery.getQuery("GameScore");
////            query.getInBackground("AmDNbzsoVG", new GetCallback<ParseObject>() {
////                public void done(ParseObject object, ParseException e) {
////                    try {
////
////                        if (e == null) {
////                        Log.d(TAG, "[authorize] " + object.getObjectId());
////                    } else {
////                        Log.d(TAG, "[authorize] " + e.getMessage());
////                        throw new WebAuthException("unable to connect to parse server");
////                    }
////                    }catch(WebAuthException e2){
////                        Log.d(TAG, e2.getMessage());
////                    }
////                }
////            });
//
//    }
//
//    /**
//     * Remove authorization by removing session cookie
//     */
//    static void deauthorize() {
//        if (cookieManager != null) {
//            CookieStore store = cookieManager.getCookieStore();
//            store.removeAll();
//        }
//    }
//
//    /**
//     * Check whether given url is valid.
//     * Uses relaxed pattern (@see WebPatterns#WEB_URL_RELAXED)
//     * @param url URL
//     * @return True if valid, false otherwise
//     */
//    static boolean isValidURL(String url) {
//        return WebPatterns.WEB_URL_RELAXED.matcher(url).matches();
//    }
//
//}
