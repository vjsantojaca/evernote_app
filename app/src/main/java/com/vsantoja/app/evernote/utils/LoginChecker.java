package com.vsantoja.app.evernote.utils;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;

import com.evernote.client.android.EvernoteOAuthActivity;
import com.evernote.client.android.EvernoteSession;
import com.evernote.client.android.login.EvernoteLoginActivity;
import com.vsantoja.app.evernote.desing.activity.ListActivity;
import com.vsantoja.app.evernote.desing.activity.LoginActivity;

import java.util.Arrays;
import java.util.List;

/**
 * Created by vsantoja on 28/02/16.
 */
public class LoginChecker implements Application.ActivityLifecycleCallbacks
{
    private static final List<Class<? extends Activity>> IGNORED_ACTIVITIES = Arrays.asList(
            LoginActivity.class,
            EvernoteLoginActivity.class,
            EvernoteOAuthActivity.class
    );
    private Intent mCachedIntent;

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        if (!EvernoteSession.getInstance().isLoggedIn() && !isIgnored(activity)) {
            mCachedIntent = activity.getIntent();
            LoginActivity.launch(activity);
            activity.finish();
        }
    }

    @Override
    public void onActivityStarted(Activity activity) {
    }

    @Override
    public void onActivityResumed(Activity activity) {
    }

    @Override
    public void onActivityPaused(Activity activity) {
        if (activity instanceof LoginActivity && EvernoteSession.getInstance().isLoggedIn()) {
            if (mCachedIntent != null) {
                activity.startActivity(mCachedIntent);
                mCachedIntent = null;
            } else {
                activity.startActivity(new Intent(activity, ListActivity.class));
            }
        }
    }

    @Override
    public void onActivityStopped(Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
    }

    private boolean isIgnored(Activity activity) {
        return IGNORED_ACTIVITIES.contains(activity.getClass());
    }
}