package com.vsantoja.app.evernote;

import android.util.Log;

import com.evernote.client.android.EvernoteSession;
import com.vsantoja.app.evernote.utils.Constants;
import com.vsantoja.app.evernote.utils.LoginChecker;

/**
 * Created by vsantoja on 28/02/16.
 */
public class Application extends android.app.Application
{
    public static final String TAG = Application.class.getName();

    @Override
    public void onCreate()
    {
        super.onCreate();

        Log.d(TAG, "On Create");

        new EvernoteSession.Builder(this)
                .setEvernoteService(Constants.EVERNOTE_SERVICE)
                .setForceAuthenticationInThirdPartyApp(true)
                .setSupportAppLinkedNotebooks(true)
                .build(Constants.CONSUMER_KEY, Constants.CONSUMER_SECRET)
                .asSingleton();

        registerActivityLifecycleCallbacks(new LoginChecker());
    }
}
