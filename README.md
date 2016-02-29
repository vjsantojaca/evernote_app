#EVERNOTE APP

The application's a test of how to use the API Evernote.
This app can be divided in four parts.

- Login
- List Notes
- Show Info Note
- New Note

Add the library from API as dependency in your build.gradle file
```
dependencies {
    compile 'com.evernote:android-sdk:2.0.0-RC3'
}
```

Define yout app credential (Constans class in utils package) 

```
private static final String CONSUMER_KEY = "Your consumer key";
private static final String CONSUMER_SECRET = "Your consumer secret";
```

Initialize the session of the api (with the info to authentificate to Evernote) in a class that extends of application class to avoid having to pass the object between the different activities.

```
mEvernoteSession = new EvernoteSession.Builder(this)
    .setEvernoteService(EVERNOTE_SERVICE)
    .build(consumerKey, consumerSecret)
    .asSingleton();
```

##Login in Evernote API
In Application.class I register a callback
```
registerActivityLifecycleCallbacks(new LoginChecker());
```
This callback checks if already it logged into the application with a user account. If the user is already logged (in Evernote API), the main activity (ListActivity, list notes) will open, otherwise it'll start the activity to Login (LoginActivity).
```
EvernoteSession.getInstance().authenticate(LoginActivity.this);
```
In this activity, pressing the button, it'll open evernote page to authenticate the user.

##List Notes


##Show Info Note


#New Note 
