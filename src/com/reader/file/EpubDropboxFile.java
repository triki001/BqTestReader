package com.reader.file;

import nl.siegmann.epublib.epub.EpubReader;

import com.dropbox.sync.android.DbxFile;
import com.dropbox.sync.android.DbxFileInfo;

/*
 * Clase define un tipo de fichero epub. Es una clase especifica
 * de la super clase GenericDropboxFile. Ademas de los atributos
 * y metodos que tiene cualquier "file", tiene metodos especificos
 * para el manero de epubs.
 * 
 * Added by: Javier Rodriguez.
 */

public class EpubDropboxFile extends GenericDropboxFile 
{
	public static final String extension = "epub";
	private String title;
	private byte[] front_image;
	
	public EpubDropboxFile(DbxFileInfo file) 
	{
		super(file);
		title = file.path.getName();
		front_image = null;
	}

	/*
	 * Esta funcion, especifica de los ficheros .epub sirve para obtener el titulo del libro
	 * guardado en ese formato. Esta funcion, de momento queda por implementar.
	 */
	
	public String getTitle()
	{
		return title;
	}
	
	public void setTitle(String title)
	{
		this.title = title;
	}
	
	public byte[] getFrontImage()
	{
		return front_image;
	}
	
	public void setFrontImage(byte[] img)
	{
		front_image = img.clone();
	}
	
	public static boolean isEpubFile(String filename)
	{
		if(filename == null)
			return false;
		
		return filename.endsWith(extension);
	}
}
