#EVERNOTE APP

The application's a test of how to use the API Evernote (The developer api evernote https://sandbox.evernote.com).
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
}
```

##Show Info Note
When user taps on an item of recycler view, it opens a dialog with the information note (title and content)
```
itemView.setOnClickListener(new View.OnClickListener()
{
	@Override
	public void onClick(View v)
	{
		int position = recyclerView.getChildAdapterPosition(v);
		final NoteEvernote note = notes.get(position);
		EvernoteSession.getInstance().getEvernoteClientFactory().getNoteStoreClient().getNoteAsync(note.getGuid(), true, false, false, false, new EvernoteCallback<Note>() {
			@Override
			public void onSuccess(Note result) {
				note.setContent(result.getContent());
				FragmentManager fm = getSupportFragmentManager();
				InfoNoteDialogFragment infoNoteDialogFragment = InfoNoteDialogFragment.newInstance(note);
				infoNoteDialogFragment.show(fm, "Info Nota");
			}

			@Override
			public void onException(Exception exception) {}
		});
	}
});
```
##New Note 
The new note screen comprises two EditText, two buttons and an image (clickable). 

- EditText title
- EditText content
- Button sendNote (send Note to evernote api)
- Button drawNote
- Image (clickable) to open camera

The sendNote Button sends to Evernote API a note with title and content.
```
EvernoteNoteStoreClient noteStoreClient = EvernoteSession.getInstance().getEvernoteClientFactory().getNoteStoreClient();
Note note = new Note();
note.setTitle(editTextTitle.getText().toString());
note.setContent(EvernoteUtil.NOTE_PREFIX + editTextNote.getText().toString() + EvernoteUtil.NOTE_SUFFIX);

noteStoreClient.createNoteAsync(note, new EvernoteCallback<Note>() {
	@Override
	public void onSuccess(Note result)
	{
	}
	
	@Override
	public void onException(Exception exception) {
		Log.e(TAG, "Error creating note", exception);
	}
});
```
OCR (Optical Character Recognition) is used for character recognition, for this, it uses a library that add to build.gradle
```
compile 'com.rmtheis:tess-two:5.4.1'
```
Character recognition also needs language files, in this case we have two, English and Spanish (later chose one of them in the code). This files are a .trainneddata files and save this in the assets directory.
The OCR is used for the recognice a camera image and a picture draw.

If the user presses the camera image, the camera will open for a photo. After, using the OCR is transformed (this image) into text for the note.
```
if( data.getExtras().get("data") != null ) {
	Bitmap bp = (Bitmap) data.getExtras().get("data");

	TessBaseAPI baseApi = new TessBaseAPI();
	baseApi.init(DATA_PATH, "spa");
	baseApi.setImage(toGrayscale(bp));
	baseApi.setPageSegMode(TessBaseAPI.PageSegMode.PSM_AUTO_OSD);
	String recognizedText = baseApi.getUTF8Text();
	Log.d(TAG, "RecognizedText: " + recognizedText);
	baseApi.end();

	editTextNote.setText(recognizedText);
}
```
This works fine if the image is rotated and if there is more than a few words. That is, an image with complex text does not make it text. (A image to this http://oi64.tinypic.com/e9d64m.jpg)

The other way is drawing on a dialog fragment also be transformed into text using OCR.
```
ByteArrayOutputStream bytes = new ByteArrayOutputStream();
bitmap.compress(Bitmap.CompressFormat.JPEG, 40, bytes);

TessBaseAPI baseApi = new TessBaseAPI();
baseApi.setDebug(true);
baseApi.init(DATA_PATH, "spa");
baseApi.setImage(toGrayscale(bitmap));
baseApi.setPageSegMode(TessBaseAPI.PageSegMode.PSM_SPARSE_TEXT_OSD);
String recognizedText = baseApi.getUTF8Text();
Log.d(TAG, "RecognizedText: " + recognizedText);
baseApi.end();

editTextNote.setText(recognizedText);
```
**PROBLEM**
*This way I havn't been able to transform the drawing text. I have found information about this problem (recognizedText is empty) and I found more people happens the same problem as me.*
- http://stackoverflow.com/questions/29984673/android-recognized-text-from-tess-two-library-is-wrong
- http://stackoverflow.com/questions/21161352/tess-two-ocr-not-working
- http://stackoverflow.com/questions/30240780/tess-two-ocr-not-decoding-correctly
- http://stackoverflow.com/questions/11568085/improve-accuracy-of-android-tessbaseapi-tesseract-ocr

One of the things I've tried is that the image becomes into a grayscale (I found information saying that this could be recognized better), but this only improved recognition of the image captured by the camera and not the drawing.
I've also been testing with different properties TessBaseAPI to see which of them was better recognition.
