package com.vsantoja.app.evernote.listener;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by vsantoja on 3/03/16.
 */
public interface DrawListener extends Serializable
{
	public void finish(Bitmap bitmap);
}
