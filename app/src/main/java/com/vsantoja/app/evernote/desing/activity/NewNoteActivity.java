package com.vsantoja.app.evernote.desing.activity;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
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
import com.googlecode.tesseract.android.TessBaseAPI;
import com.vsantoja.app.evernote.R;
import com.vsantoja.app.evernote.desing.dialogFragment.DrawNoteDialogFragment;
import com.vsantoja.app.evernote.listener.DrawListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class NewNoteActivity extends AppCompatActivity implements DrawListener
{
	private final static String TAG = NewNoteActivity.class.getName();
	public static final String DATA_PATH = Environment.getExternalStorageDirectory().toString() + "/Evernote/";
	private DrawListener drawListener;
	private EditText editTextNote;
	private EditText editTextTitle;
	private Button buttonNote;
	private Button buttonDraw;
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
		buttonDraw          = (Button) findViewById(R.id.buttonDraw);
		imageViewCamera     = (ImageView) findViewById(R.id.imageViewCamera);

		if (!EvernoteSession.getInstance().isLoggedIn()) {
			return;
		}

		drawListener = this;

		String[] paths = new String[] { DATA_PATH, DATA_PATH + "tessdata/" };

		for (String path : paths) {
			File dir = new File(path);
			if (!dir.exists()) {
				if (!dir.mkdirs()) {
					Log.v(TAG, "ERROR: Creation of directory " + path + " on sdcard failed");
					return;
				} else {
					Log.v(TAG, "Created directory " + path + " on sdcard");
				}
			}
		}

		if (!(new File(DATA_PATH + "tessdata/eng.traineddata")).exists()) {
			try {
				AssetManager assetManager = getAssets();
				InputStream in = assetManager.open("tessdata/eng.traineddata");
				OutputStream out = new FileOutputStream(DATA_PATH + "tessdata/eng.traineddata");

				byte[] buf = new byte[1024];
				int len;
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
				in.close();
				out.close();

				Log.v(TAG, "Copied eng traineddata");
			} catch (IOException e) {
				Log.e(TAG, "Was unable to copy eng traineddata " + e.toString());
			}
		}

		if (!(new File(DATA_PATH + "tessdata/spa.traineddata")).exists()) {
			try {
				AssetManager assetManager = getAssets();
				InputStream in = assetManager.open("tessdata/spa.traineddata");
				OutputStream out = new FileOutputStream(DATA_PATH + "tessdata/spa.traineddata");

				byte[] buf = new byte[1024];
				int len;
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
				in.close();
				out.close();

				Log.v(TAG, "Copied spa traineddata");
			} catch (IOException e) {
				Log.e(TAG, "Was unable to copy spa traineddata " + e.toString());
			}
		}

		imageViewCamera.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
				startActivityForResult(intent, 0);
			}
		});

		buttonDraw.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				FragmentManager fm = getSupportFragmentManager();
				DrawNoteDialogFragment drawNoteDialogFragment = DrawNoteDialogFragment.newInstance(drawListener);
				drawNoteDialogFragment.show(fm, "Dibuja Nota");
			}
		});

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

	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
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
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}


	@Override
	public void finish(Bitmap bitmap)
	{
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
	}

	public Bitmap toGrayscale(Bitmap bmpOriginal)
	{
		int width, height;
		height = bmpOriginal.getHeight();
		width = bmpOriginal.getWidth();

		Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		Canvas c = new Canvas(bmpGrayscale);
		Paint paint = new Paint();
		ColorMatrix cm = new ColorMatrix();
		cm.setSaturation(0);
		ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
		paint.setColorFilter(f);
		c.drawBitmap(bmpOriginal, 0, 0, paint);
		return bmpGrayscale;
	}
}