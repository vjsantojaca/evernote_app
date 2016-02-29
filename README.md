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

Define a application class for initialize the EvernoteSession (asSingleton) that has all of the information that we need to authenticate to Evernote. Initialize the session of the api in a class that extends of application class to avoid having to pass the object between the different activities.

```
mEvernoteSession = new EvernoteSession.Builder(this)
    .setEvernoteService(EVERNOTE_SERVICE)
    .build(consumerKey, consumerSecret)
    .asSingleton();
```

##Login in Evernote API


##List Notes


##Show Info Note


#New Note 
