package com.vsantoja.app.evernote.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.evernote.client.android.EvernoteSession;
import com.evernote.client.android.asyncclient.EvernoteCallback;
import com.evernote.client.android.asyncclient.EvernoteSearchHelper;
import com.evernote.client.android.type.NoteRef;
import com.evernote.edam.error.EDAMNotFoundException;
import com.evernote.edam.error.EDAMSystemException;
import com.evernote.edam.error.EDAMUserException;
import com.evernote.edam.notestore.NoteFilter;
import com.evernote.thrift.TException;
import com.vsantoja.app.evernote.R;
import com.vsantoja.app.evernote.bean.NoteEvernote;

import java.util.List;

public class ListActivity extends AppCompatActivity
{
    private static final String TAG = ListActivity.class.getName();

    private FloatingActionButton add;
	private List<NoteEvernote> notes;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_notes);
        add = (FloatingActionButton) findViewById(R.id.add);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "New note");
            }
        });

        if (!EvernoteSession.getInstance().isLoggedIn()) {
            return;
        }

	    final NoteFilter noteFilter = new NoteFilter();
	    EvernoteSearchHelper.Search mSearch = new EvernoteSearchHelper.Search()
			    .setOffset(0)
			    .setMaxNotes(10)
			    .setNoteFilter(noteFilter);

	    EvernoteSession.getInstance().getEvernoteClientFactory().getEvernoteSearchHelper().executeAsync(mSearch, new EvernoteCallback<EvernoteSearchHelper.Result>() {
		    @Override
		    public void onSuccess(EvernoteSearchHelper.Result result)
		    {
			    Log.d(TAG,"Result: " + result.getAllAsNoteRef().size());
			    for( NoteRef noteRef: result.getAllAsNoteRef())
			    {
				    NoteEvernote noteEvernote = new NoteEvernote();
				    noteEvernote.setGuid(noteRef.getGuid());
				    noteEvernote.setGuidNoteBook(noteRef.getNotebookGuid());
				    noteEvernote.setTitle(noteRef.getTitle());
				    notes.add(noteEvernote);
			    }
		    }

		    @Override
		    public void onException(Exception exception) {

		    }
	    });
    }
}