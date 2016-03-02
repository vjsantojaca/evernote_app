package com.vsantoja.app.evernote.desing.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.evernote.client.android.EvernoteSession;
import com.evernote.client.android.EvernoteUtil;
import com.evernote.client.android.asyncclient.EvernoteCallback;
import com.evernote.client.android.asyncclient.EvernoteNoteStoreClient;
import com.evernote.edam.type.Note;
import com.vsantoja.app.evernote.R;

public class NewNoteActivity extends AppCompatActivity
{
	private final static String TAG = NewNoteActivity.class.getName();
	private EditText editTextNote;
	private EditText editTextTitle;
	private Button buttonNote;
	private ImageView imageViewCamera;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_note);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		editTextNote        = (EditText) findViewById(R.id.editTextNote);
		editTextTitle       = (EditText) findViewById(R.id.editTextTitle);
		buttonNote          = (Button) findViewById(R.id.buttonNote);
		imageViewCamera     = (ImageView) findViewById(R.id.imageViewCamera);

		if (!EvernoteSession.getInstance().isLoggedIn()) {
			return;
		}

		buttonNote.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (editTextNote.getText().length() > 0 && editTextTitle.getText().length() > 0)
				{
					EvernoteNoteStoreClient noteStoreClient = EvernoteSession.getInstance().getEvernoteClientFactory().getNoteStoreClient();
					Note note = new Note();
					note.setTitle(editTextTitle.getText().toString());
					note.setContent(EvernoteUtil.NOTE_PREFIX + editTextNote.getText().toString() + EvernoteUtil.NOTE_SUFFIX);

					noteStoreClient.createNoteAsync(note, new EvernoteCallback<Note>() {
						@Override
						public void onSuccess(Note result)
						{
							Intent intent = new Intent(getApplicationContext(), ListActivity.class);
							startActivity(intent);
							finish();
						}

						@Override
						public void onException(Exception exception) {
							Log.e(TAG, "Error creating note", exception);
						}
					});
				} else {
					Toast.makeText(getApplicationContext(), "Debe rellenar la nota (titulo y contenido) para guardarla", Toast.LENGTH_LONG).show();
				}
			}
		});
	}
}