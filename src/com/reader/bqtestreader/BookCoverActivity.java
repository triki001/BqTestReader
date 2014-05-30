package com.reader.bqtestreader;

import com.reader.core.Debug;
import com.reader.exception.NoImageLoadedException;
import com.reader.file.EpubDropboxFile;

import android.app.Activity;
import android.graphics.Bitmap;
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
		super.onResume();
		
		ImageView view = (ImageView) findViewById(R.id.imageView);
		Bundle parameters;
		
		parameters = getIntent().getExtras();
		
		EpubDropboxFile file = (EpubDropboxFile) parameters.getSerializable("file");

		if(file.hastImage())
		{

			Bitmap image;
			try 
			{
				image = file.getFrontImage();
				view.setImageBitmap(image);
			}
			catch (NoImageLoadedException e) 
			{
				e.printStackTrace();
				Debug.showToast(this.getApplicationContext(), e.getMessage());
			}
		}
	}
}
