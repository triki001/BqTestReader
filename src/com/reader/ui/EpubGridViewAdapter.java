package com.reader.ui;

import java.util.ArrayList;

import com.reader.bqtestreader.R;
import com.reader.file.EpubDropboxFile;
import com.reader.file.GenericDropboxFile;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/*
 * Clase adaptadora para gestionar una gridView basado todo en un array
 * de datos. Se ha definido un layout llamado book_layout que contiene
 * campo "imagen" y un campo "texto". En el campo "imagen" se guardara
 * el icono generico de la imagen y en el campo texto el titulo del libro.
 * 
 * Added by: Javier Rodriguez.
 */

public class EpubGridViewAdapter extends ArrayAdapter<GenericDropboxFile> 
{
	Context context;
	int layoutResourceId;
	ArrayList<GenericDropboxFile> data = new ArrayList<GenericDropboxFile>();

	public EpubGridViewAdapter(Context context, int layoutResourceId, ArrayList<GenericDropboxFile> data) 
	{
		super(context, layoutResourceId, data);
		this.layoutResourceId = layoutResourceId;
		this.context = context;
		this.data = data;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		RecordHolder holder = null;

		if (row == null) 
		{
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			row = inflater.inflate(layoutResourceId, parent, false);

			holder = new RecordHolder();
			holder.txtTitle = (TextView) row.findViewById(R.id.item_text);
			holder.imageItem = (ImageView) row.findViewById(R.id.item_image);
			row.setTag(holder);
		} 
		else 
		{
			holder = (RecordHolder) row.getTag();
		}

		GenericDropboxFile item = data.get(position);
		
		if(item instanceof EpubDropboxFile)
			holder.txtTitle.setText(((EpubDropboxFile)item).getTitle());
		else
			holder.txtTitle.setText(item.getName());
		
		holder.imageItem.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.epub_file));
		
		return row;

	}

	static class RecordHolder 
	{
		TextView txtTitle;
		ImageView imageItem;
	}
}
