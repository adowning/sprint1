package com.andrews.app;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.andrews.app.tracker.LoggerService;
import com.cloudant.client.api.ClientBuilder;
import com.cloudant.client.api.CloudantClient;
import com.cloudant.client.api.Database;
import com.getcapacitor.BridgeActivity;
import com.getcapacitor.LogUtils;
import com.getcapacitor.Plugin;
import com.parse.ParseInstallation;
import com.worklight.jsonstore.api.JSONStoreCollection;
import com.worklight.jsonstore.api.JSONStoreInitOptions;
import com.worklight.jsonstore.api.JSONStoreSyncListener;
import com.worklight.jsonstore.api.JSONStoreSyncPolicy;
import com.worklight.jsonstore.api.WLJSONStore;
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

import org.json.JSONObject;

import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.Response;

public class MainActivity extends BridgeActivity {
  private JSONStoreCollection people;
  private static final String PEOPLE_COLLECTION_NAME = "people";
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    // Initializes the Bridge
    WLClient.createInstance(MainActivity.this);

    this.init(savedInstanceState, new ArrayList<Class<? extends Plugin>>() {{
      // Additional plugins you've installed go here
      // Ex: add(TotallyAwesomePlugin.class);
      add(LocationPlugin.class);
    }});
  }
  @Override
  public void onResume() {
    super.onResume();
    Log.d(LogUtils.getCoreTag(), "App resumed");

  }
}
