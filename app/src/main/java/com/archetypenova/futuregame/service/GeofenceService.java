package com.archetypenova.futuregame.service;

import android.app.IntentService;
import android.content.Intent;

/**
 * Created by USER on 2015/09/05.
 */
public class GeofenceService extends IntentService{

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public GeofenceService(String name) {
        super(name);
    }

    public GeofenceService(){
        super("AreaHack");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }

}
