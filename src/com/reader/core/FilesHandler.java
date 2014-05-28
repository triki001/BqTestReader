package com.reader.core;

import java.util.ArrayList;
import java.util.List;

import com.dropbox.sync.android.DbxAccount;
import com.dropbox.sync.android.DbxException;
import com.dropbox.sync.android.DbxException.Unauthorized;
import com.dropbox.sync.android.DbxFileInfo;
import com.dropbox.sync.android.DbxFileSystem;
import com.dropbox.sync.android.DbxPath;
import com.dropbox.sync.android.DbxPath.InvalidPathException;
import com.reader.file.GenericDropboxFile;

/*
 * Esta clase sera la encargada de manerar los ficheros que obtengamos del directorio
 * remoto de dropbox. 
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
	private void list(DbxPath path)
	{
		List<DbxFileInfo> list;
		try 
		{
			list = fs.listFolder(path);
			
			for(DbxFileInfo file : list)
			{
				if(file.isFolder)
					list(new DbxPath(path,file.path.getName()));
				else
					files.add(new GenericDropboxFile(file));
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
		int iCt = 0,iSize;
		GenericDropboxFile file;
		
		//eliminamos todos los elementos del array.
		files.clear();
		
		//empezamos a buscar.
		list(DbxPath.ROOT);
		
		/*
		 * Cargamos en una variable cuantos elementos hay dentro del array.
		 * Aunque gastamos un poco mas de memoria (4 bytes), ahorramos tiempo
		 * de ejecucion al ahorrarnos la llamada al método size() cada vez que
		 * se evalua la condicion del bucle for.
		 */
		
		iSize = files.size();
		
		//Una vez terminamos de buscar. Mostramos lo recuperado.
		//Esto es solo para debug.
		Debug.i("Listing files ["+iSize+" item(s)]");
		Debug.i("----------------");
		
		for(iCt=0;iCt<iSize;iCt++)
		{
			file = files.get(iCt);
			Debug.i("* "+file.getName()+" mod: "+file.getModifiedDate()+" ("+file.getModifiedDateAsUnixTimestamp()+")");
		}
		
		return files;
	}
}
