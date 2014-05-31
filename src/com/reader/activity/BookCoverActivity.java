package com.reader.activity;

import com.reader.bqtestreader.R;
import com.reader.bqtestreader.R.id;
import com.reader.bqtestreader.R.layout;
import com.reader.core.CoverImage;
import com.reader.core.Debug;
import com.reader.exception.NoImageLoadedException;
import com.reader.file.EpubDropboxFile;
import com.reader.file.GenericDropboxFile;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

public class BookCoverActivity extends Activity 
{
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_book_cover);
	}
	
	public void onResume()
	{
		CoverImage image = null;
		byte[] raw_data = null;
		
		super.onResume();
		
		ImageView view = (ImageView) findViewById(R.id.imageView);			
		image = (CoverImage) getIntent().getSerializableExtra("file");
		raw_data = image.getImageData();
		view.setImageBitmap(BitmapFactory.decodeByteArray(raw_data, 0, raw_data.length));
	}
	
	public void onBackPressed ()
	{
		super.onBackPressed();
		//Finalizamos la activity.
		finish();
	}
}
