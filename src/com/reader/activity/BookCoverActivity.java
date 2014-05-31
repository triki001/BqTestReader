package com.reader.activity;

import com.reader.bqtestreader.R;
import com.reader.core.CoverImage;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.os.Bundle;
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
		
		setTitle(image.getBookTitle());
		view.setImageBitmap(BitmapFactory.decodeByteArray(raw_data, 0, raw_data.length));
	}
	
	public void onBackPressed ()
	{
		super.onBackPressed();
		//Finalizamos la activity.
		finish();
	}
}
