package com.vsantoja.app.evernote.desing.dialogFragment;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.vsantoja.app.evernote.R;
import com.vsantoja.app.evernote.listener.DrawListener;

/**
 * Created by vsantoja on 3/03/16.
 */
public class DrawNoteDialogFragment extends DialogFragment implements View.OnTouchListener
{
	private final static String TAG = DrawNoteDialogFragment.class.getName();

	private DrawListener drawListener;
	private ImageView imageView;
	private Button buttonAceptar;
	private Button buttonCancelar;
	private float downx = 0, downy = 0, upx = 0, upy = 0;
	private Bitmap bitmap;
	private Canvas canvas;
	private Paint paint;
	private int colorId = Color.BLACK;
	private int strokeId = 3;

	public static DrawNoteDialogFragment newInstance(DrawListener drawListener) {
		DrawNoteDialogFragment drawNoteDialogFragment = new DrawNoteDialogFragment();
		Bundle args = new Bundle();
		args.putSerializable("listener", drawListener);
		drawNoteDialogFragment.setArguments(args);
		return drawNoteDialogFragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		if( getArguments() != null )
			drawListener = (DrawListener) getArguments().getSerializable("listener");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View rootView = inflater.inflate(R.layout.draw_fragment, container, false);

		imageView        = (ImageView) rootView.findViewById(R.id.image_container);
		buttonAceptar    = (Button) rootView.findViewById(R.id.button_aceptar);
		buttonCancelar   = (Button) rootView.findViewById(R.id.button_cancelar);

		imageView.setOnTouchListener(this);

		Display display = getActivity().getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);

		bitmap = Bitmap.createBitmap(size.x, size.y, Bitmap.Config.ARGB_8888);
		canvas = new Canvas(bitmap);
		paint = new Paint();
		paint.setColor(colorId);
		paint.setStrokeWidth(strokeId);
		imageView.setImageBitmap(bitmap);

		buttonAceptar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				drawListener.finish(((BitmapDrawable)imageView.getDrawable()).getBitmap());
				getDialog().dismiss();
			}
		});

		buttonCancelar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getDialog().dismiss();
			}
		});

		return rootView;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event)
	{
		// draw something
		int action = MotionEventCompat.getActionMasked(event);
		switch (action) {
			case (MotionEvent.ACTION_DOWN):
				Log.d(TAG, "Action was DOWN");
				// init start point
				downx = event.getX();
				downy = event.getY();
				return true;
			case (MotionEvent.ACTION_MOVE):
				Log.d(TAG, "Action was MOVE");
				Log.d(TAG, "Action was UP");
				upx = event.getX();
				upy = event.getY();
				canvas.drawLine(downx, downy, upx, upy, paint);
				imageView.invalidate();
				// update start point
				downx = event.getX();
				downy = event.getY();
				return true;
			case (MotionEvent.ACTION_UP):
				return true;
			case (MotionEvent.ACTION_CANCEL):
				Log.d(TAG, "Action was CANCEL");
				return true;
			case (MotionEvent.ACTION_OUTSIDE):
				Log.d(TAG, "Movement occurred outside bounds of current screen element");
				return true;
			default:
				return getActivity().onTouchEvent(event);
		}
	}
}
