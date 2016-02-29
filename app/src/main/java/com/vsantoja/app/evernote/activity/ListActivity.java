package com.vsantoja.app.evernote.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.evernote.client.android.EvernoteSession;
import com.evernote.client.android.asyncclient.EvernoteCallback;
import com.evernote.client.android.asyncclient.EvernoteSearchHelper;
import com.evernote.client.android.type.NoteRef;
import com.evernote.edam.notestore.NoteFilter;
import com.evernote.edam.type.NoteSortOrder;
import com.vsantoja.app.evernote.Constants;
import com.vsantoja.app.evernote.R;
import com.vsantoja.app.evernote.bean.NoteEvernote;

import java.util.LinkedList;
import java.util.List;

public class ListActivity extends AppCompatActivity
{
    private static final String TAG = ListActivity.class.getName();

    private FloatingActionButton add;
	private List<NoteEvernote> notes;
    private RecyclerView recyclerView;
	private RecyclerViewAdapter recyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

	    notes = new LinkedList<>();

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_notes);
        add = (FloatingActionButton) findViewById(R.id.add);

	    final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
	    recyclerView.setLayoutManager(linearLayoutManager);
	    recyclerViewAdapter = new RecyclerViewAdapter();

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
	    noteFilter.setOrder(NoteSortOrder.TITLE.getValue());
	    EvernoteSearchHelper.Search mSearch = new EvernoteSearchHelper.Search()
			    .setOffset(0)
			    .setMaxNotes(Constants.OFFSET_NOTES)
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

			    recyclerView.setAdapter(recyclerViewAdapter);
		    }

		    @Override
		    public void onException(Exception exception)
		    {
			    Log.d(TAG,"Error: " + exception.getMessage());
		    }
	    });
    }

	public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
	{
		class ViewHolder extends RecyclerView.ViewHolder {
			TextView textView;

			public ViewHolder(View itemView) {
				super(itemView);
				textView = (TextView) itemView.findViewById(R.id.textViewList);
			}

			public void insertView(String result) {
				textView.setText(result);
			}
		}

		@Override
		public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
		{
			View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);
			RecyclerView.ViewHolder viewHolder = new ViewHolder(itemView);

			itemView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Log.d(TAG, "Click note");
				}
			});

			return viewHolder;
		}

		@Override
		public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
		{
			((ViewHolder) holder).insertView((notes.get(position).getTitle()));
		}

		@Override
		public int getItemCount() {
			return notes.size();
		}
	}
}