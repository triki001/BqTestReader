package com.reader.core;
import java.util.ArrayList;

import com.reader.file.GenericDropboxFile;

/*
 * Esta clase se ha creado para encapsular la funcionalidad
 * destinada a la gestion de la sincronizacion. Principalmente
 * la estamos utilizando para obtener los ficheros nuevos de una
 * lista y para marcar los ficheros nuevos como viejos.
 * 
 * Added by: Javier Rodriguez.
 */

public class FileListHandler 
{
	private ArrayList<GenericDropboxFile> list_file;
	
	public FileListHandler(ArrayList<GenericDropboxFile> list)
	{
		this.list_file = list;
	}
	
	/*
	 * Funcion para obtener todos los elementos cargados.
	 */
	
	public ArrayList<GenericDropboxFile> getAllElements()
	{
		return list_file;
	}
	
	/*
	 * Funcion para obtener todos los nuevos elementos.
	 */
	
	public ArrayList<GenericDropboxFile> getNewElements()
	{
		int i_ct,i_size;
		ArrayList<GenericDropboxFile> new_list = new ArrayList<GenericDropboxFile>();
		
		i_size = list_file.size();
		
		for(i_ct=0;i_ct<i_size;i_ct++)
		{
			if(list_file.get(i_ct).isNew())
				new_list.add(list_file.get(i_ct));
		}
		
		return new_list;
	}
	
	/*
	 * Funcion para obtener todos los nuevos elementos de una lista pasada como argumento.
	 */
	public ArrayList<GenericDropboxFile> getNewElements(ArrayList<GenericDropboxFile> list_file)
	{
		int i_ct,i_size;
		ArrayList<GenericDropboxFile> new_list = new ArrayList<GenericDropboxFile>();
		
		i_size = list_file.size();
		
		for(i_ct=0;i_ct<i_size;i_ct++)
		{
			if(list_file.get(i_ct).isNew())
				new_list.add(list_file.get(i_ct));
		}
		
		return new_list;
	}
	
	/*
	 * Funcion para actualizar todos los archivos de una lista pasada como argumento.
	 */
	public void setAllAsUpdated(ArrayList<GenericDropboxFile> list)
	{
		int i_ct,i_size;
		
		i_size = list.size();
		
		for(i_ct=0;i_ct<i_size;i_ct++)
		{
			setAsUpdated(list.get(i_ct));
		}
	}
	
	/*
	 * Funcion para actualizar un solo fichero.
	 */
	public void setAsUpdated(GenericDropboxFile file)
	{
		file.setUp2Date();
	}
}
