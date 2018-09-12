package com.andrews.app;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.parse.Parse;
import com.parse.ParseObject;

public class ParseService extends Service {


    @Override
    public void onCreate() {
        super.onCreate();
//        Parse.initialize(new Parse.Configuration.Builder(this)
//                .applicationId("z")
//                // if defined
////                .clientKey("YOUR_CLIENT_KEY")
//                .server("http://localhost:1337/parse/")
//                .build()
//        );
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
//    ParseObject gameScore = new ParseObject("GameScore");
//gameScore.put("score", 1337);
//        gameScore.put("playerName", "Sean Plott");
//        gameScore.put("cheatMode", false);
//        gameScore.saveEventually();



}
