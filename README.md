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
The notes list screen consists of two parts, a snipper to choose between Title and Date and recyclerview with notes. The snipper is used to sort the listing.
```
ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.option_spinner, android.R.layout.simple_spinner_item);
adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
spinner.setAdapter(adapter);
```
To obtain the notes with Evernote API, An object with class type EvernoteSearchHelper is created with the offset, the maxnotes and the filter notes (for filter order by title or by date).
```
NoteFilter noteFilter = new NoteFilter();
EvernoteSearchHelper.Search mSearch = new EvernoteSearchHelper.Search()
				.setOffset(offset)
				.setMaxNotes(offset + Constants.MAX_NOTES)
				.setNoteFilter(noteFilter);
```
For this purpose (get the notes) we use an asynchronous execution with mSearch object.
```
EvernoteSession.getInstance().getEvernoteClientFactory().getEvernoteSearchHelper().executeAsync(mSearch, new EvernoteCallback<EvernoteSearchHelper.Result>() {
		@Override
		public void onSuccess(EvernoteSearchHelper.Result result)
		{
		}
		@Override
		public void onException(Exception exception)
		{
		}
	});
}
```
The result is a object with information (including Personal Result).
```
result.getPersonalResults().get(0).getNotes();
```
**PROBLEM**
*First I use getAllAsNotes method but this method return all notes without offset and maxnotes.Looking the logs and using debug, I have found that the first object of the list that getPersonalResult method returns, are the notes that would correct*

When the recyclerview goes down they are asking for new notes (until we get more) for this we increase the offset and the object Maxnotes search. On the other hand, when a spinner option is selected the search filter is changed and makes a new petition.

```
 spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
		    @Override
		    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
		    {
			    switch (position) {
				    case 0:
					    noteFilter.setOrder(NoteSortOrder.TITLE.getValue());
					    noteFilter.setAscending(true);
					    break;
				    case 1:
					    noteFilter.setOrder(NoteSortOrder.CREATED.getValue());
					    noteFilter.setAscending(false);
					    break;
			    }
		    }
```

##Show Info Note


#New Note 
