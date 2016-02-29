package com.vsantoja.app.evernote.desing.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.evernote.client.android.EvernoteSession;
import com.evernote.client.android.login.EvernoteLoginFragment;
import com.vsantoja.app.evernote.R;

public class LoginActivity extends AppCompatActivity implements EvernoteLoginFragment.ResultCallback
{
    private static final String TAG = LoginActivity.class.getName();
    private Button mButton;

    public static void launch(Activity activity) {
        activity.startActivity(new Intent(activity, LoginActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mButton = (Button) findViewById(R.id.button_login);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Button Login");
                EvernoteSession.getInstance().authenticate(LoginActivity.this);
                mButton.setEnabled(false);
            }
        });
    }

    @Override
    public void onLoginFinished(boolean successful)
    {
        if( successful ) {
            Log.d(TAG, "Login Finish OK");
            finish();
        } else {
            Log.d(TAG, "Login Finish NOT OK");
            mButton.setEnabled(true);
        }
    }
}
