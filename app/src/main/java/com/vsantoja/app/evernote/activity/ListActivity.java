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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.evernote.client.android.EvernoteSession;
import com.evernote.client.android.asyncclient.EvernoteCallback;
import com.evernote.client.android.asyncclient.EvernoteSearchHelper;
import com.evernote.edam.notestore.NoteFilter;
import com.evernote.edam.notestore.NoteMetadata;
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
	private NoteFilter noteFilter;
    private RecyclerView recyclerView;
	private Spinner spinner;
	private RecyclerViewAdapter recyclerViewAdapter;
	private Boolean moreNotes = true;
	private int lastFirstVisible = -1;
	private int lastVisibleCount = -1;
	private int lastItemCount = -1;
	private int offset = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

	    notes = new LinkedList<>();

	    spinner = (Spinner) findViewById(R.id.options_spinner);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_notes);
        add = (FloatingActionButton) findViewById(R.id.add);

	    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.option_spinner, android.R.layout.simple_spinner_item);
	    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    spinner.setAdapter(adapter);

	    final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
	    recyclerView.setLayoutManager(linearLayoutManager);
	    recyclerViewAdapter = new RecyclerViewAdapter();
	    recyclerView.setAdapter(recyclerViewAdapter);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "New note");
            }
        });

        if (!EvernoteSession.getInstance().isLoggedIn()) {
            return;
        }

	    noteFilter = new NoteFilter();

	    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
		    @Override
		    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
		    {
			    Log.d(TAG, "Position:" + position);
			    switch (position) {
				    case 0:
					    noteFilter.setOrder(NoteSortOrder.TITLE.getValue());
					    noteFilter.setAscending(true);
					    notes.clear();
					    recyclerViewAdapter.notifyDataSetChanged();
					    moreNotes = true;
					    lastFirstVisible = -1;
					    lastVisibleCount = -1;
					    lastItemCount = -1;
					    offset = 0;
					    getNotes();
					    break;
				    case 1:
					    noteFilter.setOrder(NoteSortOrder.CREATED.getValue());
					    noteFilter.setAscending(false);
					    notes.clear();
					    recyclerViewAdapter.notifyDataSetChanged();
					    moreNotes = true;
					    lastFirstVisible = -1;
					    lastVisibleCount = -1;
					    lastItemCount = -1;
					    offset = 0;
					    getNotes();
					    break;
			    }
		    }

		    @Override
		    public void onNothingSelected(AdapterView<?> parent) {}
	    });

	    recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener()
	    {
		    @Override
		    public void onScrolled(RecyclerView recyclerView, int dx, int dy)
		    {
			    LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
				int firstVisible = layoutManager.findFirstVisibleItemPosition();
				int visibleCount = Math.abs(firstVisible - layoutManager.findLastVisibleItemPosition());
				int itemCount = recyclerView.getAdapter().getItemCount();

				if (firstVisible != lastFirstVisible || visibleCount != lastVisibleCount
						|| itemCount != lastItemCount) {
					lastFirstVisible = firstVisible;
					lastVisibleCount = visibleCount;
					lastItemCount = itemCount;
					if (moreNotes)
						getNotes();
				}
		    }
	    });
    }

	public void getNotes()
	{
		EvernoteSearchHelper.Search mSearch = new EvernoteSearchHelper.Search()
				.setOffset(offset)
				.setMaxNotes(offset + Constants.MAX_NOTES)
				.setNoteFilter(noteFilter);

		offset += Constants.MAX_NOTES;

		EvernoteSession.getInstance().getEvernoteClientFactory().getEvernoteSearchHelper().executeAsync(mSearch, new EvernoteCallback<EvernoteSearchHelper.Result>() {
			@Override
			public void onSuccess(EvernoteSearchHelper.Result result)
			{
				if( result.getAllAsNoteRef().size() > 0 )
				{
					/*
						First I use getAllAsNotes but this method return all notes without offset and maxnotes.
						Looking the logs and using debug, I have found that the first object of the
							list PersonalResult are the notes that would correct (we ordered EvernoteSearchHelper)
					 */
					Log.d(TAG, "Result: " + result.getPersonalResults().get(0).getNotesSize());
					for (NoteMetadata noteRef : result.getPersonalResults().get(0).getNotes()) {
						NoteEvernote noteEvernote = new NoteEvernote();
						noteEvernote.setGuid(noteRef.getGuid());
						noteEvernote.setGuidNoteBook(noteRef.getNotebookGuid());
						noteEvernote.setTitle(noteRef.getTitle());
						notes.add(noteEvernote);
					}

					recyclerViewAdapter.notifyItemRangeInserted(offset, offset + Constants.MAX_NOTES);
				} else {
					Log.d(TAG, "No more notes");
					moreNotes = false;
				}
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

			itemView.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
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
			if( notes.size() > 0 )
				return notes.size();
			else
				return 0;
		}
	}
}