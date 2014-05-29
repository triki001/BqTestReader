package com.reader.core;

import java.util.ArrayList;
import java.util.List;

import com.dropbox.sync.android.DbxAccount;
import com.dropbox.sync.android.DbxException;
import com.dropbox.sync.android.DbxException.Unauthorized;
import com.dropbox.sync.android.DbxFile;
import com.dropbox.sync.android.DbxFileInfo;
import com.dropbox.sync.android.DbxFileSystem;
import com.dropbox.sync.android.DbxPath;
import com.dropbox.sync.android.DbxPath.InvalidPathException;
import com.reader.exception.RemoteFileNotOpenedException;
import com.reader.file.EpubDropboxFile;
import com.reader.file.GenericDropboxFile;

/*
 * Esta clase sera la encargada de manerar los ficheros que obtengamos del directorio
 * remoto de dropbox. 
 * 
 * Dado que el account es obtenido mediante un singleton, no tiene mucho sentido utilizar
 * un singleton aqui.
 * 
 * Added by: Javier Rodriguez.
 */

public class FilesHandler 
{
	private DbxAccount account;
	private DbxFileSystem fs;
	private boolean isReady;
	private ArrayList<GenericDropboxFile> files;
	
	public FilesHandler(DbxAccount account)
	{
		this.account = account;
		isReady = false;
		files = new ArrayList<GenericDropboxFile>();
	}
	
	public boolean init()
	{
		if(account == null)
			return false;
		
		try 
		{
			fs = DbxFileSystem.forAccount(account);
			isReady = true;
		} 
		catch (Unauthorized e) 
		{
			isReady = false;
		}
		
		return isReady;
	}
	
	public boolean isReady()
	{
		return isReady;
	}
	
	/*
	 * Al ser un proceso bastante lento, lo ideal seria meter esto dentro de un hilo para
	 * que la app no proteste al quedarse demasiado tiempo sin retornar el control al 
	 * Activity. 
	 * 
	 * NOTA: Funcion recursiva.
	 * 
	 * TODO: Introducir la funcion recursiva dentro de un hilo.
	 */
	private void scanDir(DbxPath path)
	{
		List<DbxFileInfo> list;
		try 
		{
			list = fs.listFolder(path);
			
			for(DbxFileInfo file : list)
			{
				if(file.isFolder)
					scanDir(new DbxPath(path,file.path.getName()));
				else
				{
					if(EpubDropboxFile.isEpubFile(file.path.getName()))
						files.add(new EpubDropboxFile(file));
				}
			}
		} 
		catch (InvalidPathException e) 
		{

		} 
		catch (DbxException e) 
		{

		}
	}
	
	/*
	 * Funcion de entrada. Esta funcion obtiene todos los archivos del directorio remoto en Dropbox.
	 * 
	 * TODO: Incluir un wrapper para la clase DbxFileInfo, no es elegante utilizar esa clase
	 * dentro del activity. Podemos introducir metodos de acceso a la clase para controlar
	 * como se va a mostrar la informacion.
	 */
	
	public ArrayList<GenericDropboxFile> getFiles()
	{		
		//eliminamos todos los elementos del array.
		files.clear();
		
		//empezamos a buscar.
		scanDir(DbxPath.ROOT);

		return files;
	}
	
	/*
	 * Esta funcion abre un fichero alojado en el directorio remoto.
	 */
	public boolean openFile(GenericDropboxFile g_file)
	{
		DbxFile file = null;
		
		try 
		{
			file = fs.open(g_file.getFileAsPath());
			g_file.setRawDropboxFile(file);
			return true;
		} 
		catch (DbxException e) 
		{
			return false;
		}
	}
	
	/*
	 * Esta funcion cierra un fichero alojado en el directorio remoto.
	 */
	
	public boolean closeFile(GenericDropboxFile g_file)
	{
		try 
		{
			DbxFile file = g_file.getRawDropboxFile();
			file.close();
			return true;
		} 
		catch (RemoteFileNotOpenedException e) 
		{
			return false;
		}
	}
	
	/*
	 * Muestra la informacion de los ficheros almacenados en el artibuto files.
	 * Si se desea utilizar este metodo y que efectivamente muestre informacion
	 * debemos ejecutar previamente el metodo getFiles().
	 */
	
	public void printInformationFiles()
	{
		printInformationFiles(this.files);
	}
	
	/*
	 * Muestra la informacion de los ficheros en el parametro list_files.
	 */
	
	public static void printInformationFiles(ArrayList<GenericDropboxFile> list_files)
	{
		int iCt = 0,iSize;
		GenericDropboxFile file;
		
		/*
		 * Cargamos en una variable cuantos elementos hay dentro del array.
		 * Aunque gastamos un poco mas de memoria (4 bytes), ahorramos tiempo
		 * de ejecucion al ahorrarnos la llamada al método size() cada vez que
		 * se evalua la condicion del bucle for.
		 */
		
		iSize = list_files.size();
		
		//Una vez terminamos de buscar. Mostramos lo recuperado.
		//Esto es solo para debug.
		Debug.i("Listing files ["+iSize+" item(s)]");
		Debug.i("----------------");
		
		for(iCt=0;iCt<iSize;iCt++)
		{
			file = list_files.get(iCt);
			Debug.i("* "+file.getName()+" mod: "+file.getModifiedDate()+" ("+file.getModifiedDateAsUnixTimestamp()+")");
		}
	}
}
