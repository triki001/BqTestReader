package com.reader.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.dropbox.sync.android.DbxAccount;
import com.dropbox.sync.android.DbxException;
import com.dropbox.sync.android.DbxException.Unauthorized;
import com.dropbox.sync.android.DbxFile;
import com.dropbox.sync.android.DbxFileInfo;
import com.dropbox.sync.android.DbxFileSystem;
import com.dropbox.sync.android.DbxPath;
import com.dropbox.sync.android.DbxPath.InvalidPathException;
import com.reader.criteria.AbstractCriteria;
import com.reader.criteria.CriteriaFactory;
import com.reader.exception.InvalidCriteriaException;
import com.reader.exception.RemoteFileNotOpenedException;
import com.reader.file.EpubDropboxFile;
import com.reader.file.GenericDropboxFile;
import com.reader.shorter.FileShorter;

/*
 * Esta clase sera la encargada de manerar los ficheros que obtengamos del directorio
 * remoto de dropbox. 
 * 
 * Aunque en un primer momento se decidio que esta clase no fuera singleton, debido
 * a la refactorizacion del codigo, se ha pensado que lo mejor es hacerla singleton
 * para que asi pueda ser mas facilmente accesible desde cualquier activity.
 * 
 * Added by: Javier Rodriguez.
 */

public class FilesHandler 
{
	private DbxAccount account;
	private DbxFileSystem fs;
	private boolean isReady;
	private ArrayList<GenericDropboxFile> files;
	private static FilesHandler instance = null;
	
	private FilesHandler(DbxAccount account)
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
	
	public boolean hasFiles()
	{
		if(files.size() > 0)
			return true;
		
		return false;
	}
	
	
	/*
	 * Al ser un proceso bastante lento, lo ideal seria meter esto dentro de un hilo para
	 * que la app no proteste al quedarse demasiado tiempo sin retornar el control al 
	 * Activity. 
	 * 
	 * NOTA: Funcion recursiva.
	 * 
	 */
	private void scanDir(ArrayList<GenericDropboxFile>files, DbxPath path)
	{
		List<DbxFileInfo> list;
		try 
		{
			list = fs.listFolder(path);
			
			for(DbxFileInfo file : list)
			{
				if(file.isFolder)
					scanDir(files,new DbxPath(path,file.path.getName()));
				else
				{
					if(EpubDropboxFile.isEpubFile(file.path.getName()))
						files.add(new EpubDropboxFile(file));
				}
			}
		} 
		catch (InvalidPathException e) 
		{
			return;
		} 
		catch (DbxException e) 
		{
			return;
		}
	}
	
	/*
	 * Funcion de entrada. Esta funcion obtiene todos los archivos del directorio remoto en Dropbox.
	 * 
	 * Si se ha subido un archivo nuevo al servidor y se vuelve a llamar a la funcion, esta es capaz
	 * de detectar que hay un nuevo fichero.
	 */
	
	public ArrayList<GenericDropboxFile> getFiles()
	{		
		ArrayList<GenericDropboxFile> remote_files = new ArrayList<GenericDropboxFile>();
			
		//obtenemos los ficheros remotos.
		scanDir(remote_files,DbxPath.ROOT);
		
		//Realizamos la sincronizacion entre local y remoto.
		files = doSync(files,remote_files);
		
		return files;
	}
	
	/*
	 * Funcion que, chequea los ficheros locales y remotos y hace una comparacion entre
	 * ambos siguiendo 3 reglas.
	 * 
	 * Si esta en local y remoto, se deja como esta.
	 * Si esta en local y no esta en remoto, entonces se borra de local.
	 * Si esta en remoto y no en local, se incluye.
	 * 
	 */
	
	public ArrayList<GenericDropboxFile> doSync(ArrayList<GenericDropboxFile> local,ArrayList<GenericDropboxFile> remote)
	{
		int i_ct,j_ct,j_size,i_size;
		GenericDropboxFile file = null;
		ArrayList<GenericDropboxFile> new_list = new ArrayList<GenericDropboxFile>();
		
		i_size = local.size();
		j_size = remote.size();
		
		//La lista local esta vacia, todo lo que llegue de remoto sera nuevo.
		if(local.isEmpty())
		{
			new_list.addAll(remote);
			return new_list;
		}
		//La lista remota esta vacia, hay que borrar todos los elementos que no esten en local.
		if(remote.isEmpty())
		{
			return new_list;
		}
		
		//Caso general
		for(i_ct=0;i_ct<i_size;i_ct++)
		{
			file = local.get(i_ct);
			
			//Buscamos el elemento local en la lista remota.
			for(j_ct=0;j_ct<j_size;j_ct++)
			{
				if(file.getFileHash().equals(remote.get(j_ct).getFileHash()))
					break;
			}
			/*
			 * Si hemos acabado antes de terminar el bucle
			 * Significa que ya existia en local. Asi que 
			 * no hacemos nada en local y marcamos como "viejo"
			 * en remoto.
			 * 
			 * Sino, significa que no esta ya en remoto asi que borramos
			 * de local.
			 */
			
			if(j_ct < j_size)
			{
				Debug.i("> [debug] Old element detect.");
				new_list.add(file);
				remote.get(j_ct).setUp2Date();
			}
		}
		
		/*
		 * Todos los que no han sido marcados como nuevos anteriormente
		 * signficaba que estaban ya en local, asi que los que ahora siguen
		 * estando como nuevos en remoto, sera nuevo en el repositorio, asi
		 * que directamente los metemos.
		 */
		for(j_ct=0;j_ct<j_size;j_ct++)
		{
			file = remote.get(j_ct);
			
			if(file.isNew())
				new_list.add(file);
		}
		
		return new_list;
	}
	
	/*
	 * Funcion de entrada. Esta funcion obtiene todos los archivos del directorio remoto en Dropbox.
	 * Asi mismo, devuelve los datos ya ordenados segun el criterio de ordenacion.
	 */
	
	public ArrayList<GenericDropboxFile> getFiles(AbstractCriteria criteria)
	{		
		ArrayList<GenericDropboxFile> remote_files = new ArrayList<GenericDropboxFile>();
		
		//empezamos a buscar.
		scanDir(remote_files,DbxPath.ROOT);

		//Realizamos la sincronizacion entre local y remoto.
		files = doSync(files,remote_files);
		
		return FileShorter.do_short(files, criteria);
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
	
	public void awaitFirstSync() throws DbxException
	{
		fs.awaitFirstSync();
	}
	
	public void sync() throws DbxException
	{
		fs.syncNowAndWait();
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
			Debug.i("* "+file.getName()+" hash: "+file.getFileHash());
		}
	}
	
	/*
	 * Funcion para recoger la instancia (o crearla si no existia previamente).
	 * Metodo obligatorio en la clase singleton.
	 */
	
	public static FilesHandler getInstance(DbxAccount account)
	{
		if(instance == null)
			return new FilesHandler(account);
		
		return instance;
	}
}
