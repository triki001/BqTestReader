package com.reader.file;

import nl.siegmann.epublib.epub.EpubReader;

import com.dropbox.sync.android.DbxFile;
import com.dropbox.sync.android.DbxFileInfo;
import com.reader.exception.NoImageLoadedException;

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
	private boolean real_title;
	private boolean has_image;
	
	public EpubDropboxFile(DbxFileInfo file) 
	{
		super(file);
		title = file.path.getName();
		front_image = null;
		real_title = false;
		has_image = false;
	}

	/*
	 * Esta funcion coge el titulo del libro. Si el titulo no se ha definido
	 * previamente, cogemos el nombre del archivo sin extension, asi nos 
	 * aseguramos que siempre va a tener un titulo.
	 */
	
	public String getTitle()
	{
		if(real_title)
			return title;
		
		return getNameWithoutExtension();
	}
	
	/*
	 * Ponemos el titulo original, el que extraemos del libro.
	 */

	public void setTitle(String title)
	{
		this.title = title;
		real_title = true;
	}
	
	/*
	 * Verifica si ya tenemos definido un titulo o no.
	 */
	public boolean hasTitle()
	{
		return real_title;
	}
	
	/*
	 * Cogemos la imagen almacenada previamente. Tener en cuenta
	 * que si la imagen no existe, lanzara una excepcion indicando
	 * que no existe ninguna imagen.
	 */
	
	public byte[] getFrontImage() throws NoImageLoadedException
	{
		if(has_image)
			return front_image;
		else
			throw new NoImageLoadedException();
	}
	
	/*
	 * Cargamos una nueva imagen.
	 */
	
	public void setFrontImage(byte[] img)
	{
		front_image = img.clone();
		has_image = true;
	}
	
	/*
	 * Verifica si tenemos o no definida una imagen.
	 */
	
	public boolean hastImage()
	{
		return has_image;
	}
	
	/*
	 * Funcion que verifica si el String pasado como argumento
	 * correponde a un nombre de archivo con extension .epub.
	 */
	
	public static boolean isEpubFile(String filename)
	{
		if(filename == null)
			return false;
		
		return filename.endsWith(extension);
	}
}
