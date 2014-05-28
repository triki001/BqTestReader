package com.reader.file;

import com.dropbox.sync.android.DbxFileInfo;

/*
 * Clase define un tipo de fichero epub. Es una clase especifica
 * de la super clase GenericDropboxFile. Ademas de los atributos
 * y metodos que tiene cualquier "file", tiene metodos especificos
 * para el manero de epubs.
 * 
 * Se va a utilizar un API especifico que he encontrado en internet
 * para el manejo de epubs y que puede ser util. 
 * 
 * La libreria esta incluida como .jar en el proyecto y el API de dicha
 * libreria esta aqui:
 * 
 * URI oficial:
 * http://www.siegmann.nl/epublib/android
 * 
 * API:
 * http://www.siegmann.nl/epublib/apidocs
 * 
 * Esta libreria requiere a su vez de otra libreria que se llama slf4j que
 * sirve para generar Log y que tambien se ha includo en el proyecto.
 * 
 * URI oficial:
 * http://www.slf4j.org/android/
 * 
 * TODO: Incluir metodos para cargar el titulo y la portada en (modo imagen).
 * 
 * Added by: Javier Rodriguez.
 */

public class EpubDropboxFile extends GenericDropboxFile 
{
	public static final String extension = "epub";
	
	public EpubDropboxFile(DbxFileInfo file) 
	{
		super(file);
	}

	/*
	 * Esta funcion, especifica de los ficheros .epub sirve para obtener el titulo del libro
	 * guardado en ese formato. Esta funcion, de momento queda por implementar.
	 */
	public String getTitle()
	{
		return super.getName();
	}
	
	public static boolean isEpubFile(String filename)
	{
		if(filename == null)
			return false;
		
		return filename.endsWith(extension);
	}
}
