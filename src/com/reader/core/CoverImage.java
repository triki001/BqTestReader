package com.reader.core;

import java.io.Serializable;

/*
 * Se ha creado esta clase con el fin de pasar los datos de la imagen
 * desde la activity ListBooksActivity a la activity BookCoverActivity.
 * 
 * Por lo general, es mejor encapsularlo dentro de una clase de este
 * estilo que pasar el byte[].
 * 
 * Se implementa la interfaz Serializable porque es necesario para
 * pasarla entre las activities al usar el putExtra().
 * 
 * Added By: Javier Rodriguez.
 */

@SuppressWarnings("serial")
public class CoverImage implements Serializable
{
	private byte[] data;
	private String book_title;
	private String filename;
	
	public CoverImage(byte[] data,String book_title,String filename)
	{
		this.data = data;
		this.book_title = book_title;
		this.filename = filename;
	}
	
	public byte[] getImageData()
	{
		return data;
	}
	
	public String getBookTitle()
	{
		return book_title;
	}
	
	public String getFilename()
	{
		return filename;
	}
}
