package com.andrews.app;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.andrews.app.tracker.LoggerService;
import com.getcapacitor.BridgeActivity;
import com.getcapacitor.Plugin;
import com.parse.ParseInstallation;

import java.util.ArrayList;

public class MainActivity extends BridgeActivity {

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    // Initializes the Bridge
    this.init(savedInstanceState, new ArrayList<Class<? extends Plugin>>() {{
      // Additional plugins you've installed go here
      // Ex: add(TotallyAwesomePlugin.class);
      add(LocationPlugin.class);
    }});
  }


  /**
   * Start logger service
   */
  private void startLogger() {
    // start tracking
    if (!LoggerService.isRunning()) {
        Intent intent = new Intent(MainActivity.this, LoggerService.class);
        startService(intent);
      }
    }

}
