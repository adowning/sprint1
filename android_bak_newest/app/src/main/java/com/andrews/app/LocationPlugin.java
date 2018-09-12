package com.andrews.app;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import com.cloudant.client.api.ClientBuilder;
import com.cloudant.client.api.CloudantClient;
import com.cloudant.client.api.Database;
import com.getcapacitor.LogUtils;
import com.andrews.app.tracker.LoggerService;
import com.getcapacitor.JSObject;
import com.getcapacitor.NativePlugin;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.worklight.jsonstore.api.JSONStoreCollection;
import com.worklight.jsonstore.api.JSONStoreInitOptions;
import com.worklight.jsonstore.api.JSONStoreSyncListener;
import com.worklight.jsonstore.api.JSONStoreSyncPolicy;
import com.worklight.jsonstore.api.WLJSONStore;
import com.worklight.jsonstore.database.SearchFieldType;
import com.worklight.jsonstore.exceptions.JSONStoreCloseAllException;
import com.worklight.jsonstore.exceptions.JSONStoreException;
import com.worklight.jsonstore.exceptions.JSONStoreFileAccessException;
import com.worklight.jsonstore.exceptions.JSONStoreInvalidPasswordException;
import com.worklight.jsonstore.exceptions.JSONStoreInvalidSchemaException;
import com.worklight.jsonstore.exceptions.JSONStoreMigrationException;
import com.worklight.jsonstore.exceptions.JSONStoreSchemaMismatchException;
import com.worklight.jsonstore.exceptions.JSONStoreTransactionDuringInitException;
import com.worklight.wlclient.api.WLClient;
import com.worklight.wlclient.api.WLFailResponse;
import com.worklight.wlclient.api.WLResourceRequest;
import com.worklight.wlclient.api.WLResponse;
import com.worklight.wlclient.api.WLResponseListener;

import java.net.URI;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import com.worklight.common.*;

import org.json.JSONException;
import org.json.JSONObject;

@NativePlugin()
public class LocationPlugin extends Plugin {
    static final int REQUEST_IMAGE_CAPTURE = 12345;
    Intent serviceIntent = null;
    @NativePlugin(
            permissions={
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.RECEIVE_BOOT_COMPLETED,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,


            }
    )
    /**
     * Handle onStart
     */
    @Override
    protected void handleOnStart() {

                String s = String.valueOf(hasRequiredPermissions());
        Log.d("asdf", s);
        pluginRequestPermissions(new String[] {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.RECEIVE_BOOT_COMPLETED
        }, REQUEST_IMAGE_CAPTURE);
        if(hasRequiredPermissions()){
            serviceIntent = new Intent(getActivity(), LoggerService.class);
            getActivity().startService(serviceIntent);
        }else{
            pluginRequestPermissions(new String[] {
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.RECEIVE_BOOT_COMPLETED
            }, REQUEST_IMAGE_CAPTURE);
        }


    }
    @Override
    protected void handleRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.handleRequestPermissionsResult(requestCode, permissions, grantResults);

        Log.d("adsf", "handling request perms result");


        for(int result : grantResults) {
            if (result == PackageManager.PERMISSION_DENIED) {
//                savedCall.error("User denied permission");
                Log.d("adsf","No stored plugin call for permissions request result");

                return;
            }
        }
        if(hasRequiredPermissions()){
            serviceIntent = new Intent(getActivity(), LoggerService.class);
            getActivity().startService(serviceIntent);
        }else{
            pluginRequestPermissions(new String[] {
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.RECEIVE_BOOT_COMPLETED
            }, REQUEST_IMAGE_CAPTURE);
        }
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            Log.d("adsf","gottit");

            // We got the permission
        }
    }


}