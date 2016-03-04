package com.vsantoja.app.evernote.desing.dialogFragment;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vsantoja.app.evernote.R;
import com.vsantoja.app.evernote.bean.NoteEvernote;

/**
 * Created by vsantoja on 29/02/16.
 */
public class InfoNoteDialogFragment extends DialogFragment
{
	private NoteEvernote note;

	public static InfoNoteDialogFragment newInstance(NoteEvernote noteEvernote) {
		InfoNoteDialogFragment infoNoteDialogFragment = new InfoNoteDialogFragment();
		Bundle args = new Bundle();
		args.putSerializable("note", noteEvernote);
		infoNoteDialogFragment.setArguments(args);
		return infoNoteDialogFragment;
	}

	public InfoNoteDialogFragment() {}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if( getArguments() != null )
			note = (NoteEvernote) getArguments().getSerializable("note");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View rootView = inflater.inflate(R.layout.info_fragment, container, false);
		TextView textViewTitle = (TextView) rootView.findViewById(R.id.title_info);
		TextView textViewContent = (TextView) rootView.findViewById(R.id.content_info);

		textViewContent.setText(Html.fromHtml(note.getContent()));
		textViewTitle.setText(Html.fromHtml("Titulo: " + note.getTitle()));

		getDialog().setTitle("Información Nota");
		return rootView;
	}
}
