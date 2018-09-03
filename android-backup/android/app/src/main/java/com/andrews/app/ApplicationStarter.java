package com.andrews.app;

import android.app.Application;
import android.util.Log;

import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;


public class ApplicationStarter extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);

        // Add your initialization code here
        Parse.initialize(new Parse.Configuration.Builder(getApplicationContext())
                .applicationId("z")
                // if defined
//                .clientKey("somemasterkey")
                .server("http://74.192.104.240:1337/parse")
                .build()
        );
//        Parse.initialize(new Parse.Configuration.Builder(getApplicationContext())
//                .applicationId("1gdHYU45DMPBp5r5fDx8Cx3EWgmoCz4qQ7zqwUGH")
//                // if defined
//                .clientKey("zcInbgcy6d7SZgjJxOFoba14PN3EF9j4gYmUltJA")
//                .server("https://parseapi.back4app.com")
//                .build()
//        );
        ParseUser.enableAutomaticUser();

        ParseInstallation.getCurrentInstallation().saveInBackground();
        ParseACL defaultACL = new ParseACL();
        defaultACL.setPublicReadAccess(true);
        defaultACL.setPublicWriteAccess(true);
        ParseACL.setDefaultACL(defaultACL, true);
        ParseUser.logInInBackground("cooldude6", "asdfasdf", new LogInCallback() {
            public void done(ParseUser user, ParseException e) {
                if (user != null) {
                   Log.d("asdfx", user.getUsername());

                } else {
                    Log.d("asdfx err", e.getMessage());
                }
            }
        });
        ParseObject gameScore = new ParseObject("Androidx");
        gameScore.put("score", 1337);
        gameScore.put("playerName", "Sean Plott");
        gameScore.put("cheatMode", false);
        gameScore.saveInBackground();
    }
}
