package com.reader.core;

import java.io.IOException;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.epub.EpubReader;

import com.dropbox.sync.android.DbxException;
import com.dropbox.sync.android.DbxFile;
import com.reader.exception.EpubHandlerBookException;
import com.reader.exception.RemoteFileNotOpenedException;
import com.reader.file.EpubDropboxFile;

/*
 * Esta clase sirve como interfaz para obtener los datos que deseamos de un libro epub.
 * En nuestro caso, solo necesiamos el titulo del libro y la imagen de la portada.
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
 * Added by: Javier Rodriguez.
 */

public class EpubFileHandler 
{	
	/*
	 * Funcion para obtener el titulo de un fichero epub.
	 */
	public static boolean retrieveEpubTitle(FilesHandler files_handler, EpubDropboxFile epub_file)
	{
		Book book = null;
		
		try 
		{
			book = retrieveBook(files_handler,epub_file);
			epub_file.setTitle(book.getTitle());
			files_handler.closeFile(epub_file);
			return true;
		}
		catch (EpubHandlerBookException e) 
		{
			e.printStackTrace();
			return false;
		}
	}
	
	/*
	 * Funcion para obtener la imagen de portada de un fichero epub.
	 */
	
	public static boolean retrieveEpubFrontImage(FilesHandler files_handler, EpubDropboxFile epub_file)
	{
		Book book = null;
		
		try 
		{
			book = retrieveBook(files_handler,epub_file);
			epub_file.setFrontImage(book.getCoverImage().getData());
			return true;
		}
		catch (EpubHandlerBookException e) 
		{
			e.printStackTrace();
			return false;
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
			return false;
		}
	}
	
	/*
	 * Funcion para obtener el Book de un libro epub. El Book es una clase que
	 * da una vision global de lo que es un libro epub. Puedes acceder a sus 
	 * diferentes recursos, entre los que se destacan el titulo y la imagen de
	 * portada, pero siempre se hace a traves del Book.
	 */
	
	private static Book retrieveBook(FilesHandler files_handler, EpubDropboxFile epub_file) throws EpubHandlerBookException
	{
		DbxFile db_file = null;
		EpubReader reader = null;
		Book book;
		
		files_handler.openFile(epub_file);
		
		try 
		{
			db_file = epub_file.getRawDropboxFile();
			reader = new EpubReader();
			book = reader.readEpub(db_file.getReadStream());
			return book;
		}
		catch (RemoteFileNotOpenedException e) 
		{
			e.printStackTrace();
			throw new EpubHandlerBookException();
		} 
		catch (DbxException e) 
		{
			e.printStackTrace();
			throw new EpubHandlerBookException();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
			throw new EpubHandlerBookException();
		}
	}
	
}
