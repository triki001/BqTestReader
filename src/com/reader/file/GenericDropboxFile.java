package com.reader.file;

import java.util.Date;

import com.dropbox.sync.android.DbxFile;
import com.dropbox.sync.android.DbxFileInfo;
import com.dropbox.sync.android.DbxPath;
import com.reader.exception.RemoteFileNotOpenedException;
import com.reader.util.SHA1;

/*
 * Clase define un tipo de fichero generico. La idea de hacer una clase
 * generica es hacer mas transparente el uso y manejo de ficheros. Para
 * la solucion que estoy desarrollando, solo existe el tipo especifico
 * epub pero en un futuro podria ser necesario utilizar mas tipos y de
 * esta forma, el funcionamiento es mucho mas transparente y resulta mas
 * comodo de implementar, haciendo a su vez, el codigo mas legible.
 * 
 * Added by: Javier Rodriguez.
 */

public class GenericDropboxFile
{
	private DbxFileInfo dbx_file_info;
	private DbxFile dbx_file;
	private boolean is_opened;
	private String file_hash;
	private boolean is_hashed;
	private boolean is_new;
	
	public GenericDropboxFile(DbxFileInfo file)
	{
		this.dbx_file_info = file;
		is_opened = false;
		is_hashed = false;
		is_new = true;
	}

	protected DbxFileInfo getRawDropboxFileInfo()
	{
		return dbx_file_info;
	}
	
	public void setRawDropboxFile(DbxFile file)
	{
		this.dbx_file = file;
		is_opened = true;
	}
	
	public DbxFile getRawDropboxFile() throws RemoteFileNotOpenedException
	{
		if(is_opened)
			return dbx_file;
		else
			throw new RemoteFileNotOpenedException();
	}
	
	/*
	 * Aqui el nombre del fichero. 
	 * Por ejemplo, si el fichero esta en /home/javier/prueba.txt devolvemos prueba.txt
	 */
	public String getName()
	{
		return dbx_file_info.path.getName();
	}
	
	/*
	 * Aqui mostramos el nombre del fichero sin extension. 
	 * Por ejemplo, si el fichero esta en /home/javier/prueba.txt devolvemos prueba
	 */
	public String getNameWithoutExtension()
	{
		int length = dbx_file_info.path.getName().lastIndexOf(".");
		return dbx_file_info.path.getName().substring(0, length);
	}
	
	/*
	 * Aqui mostramos la ruta donde esta ubicado. 
	 * Por ejemplo, si el fichero esta en /home/javier/prueba.txt devolvemos /home/javier
	 */
	public String getPath()
	{
		return dbx_file_info.path.getParent().toString();
	}
	
	/*
	 * Aqui mostramos el nombre con la ruta ya incluida.
	 */
	public String getCanonicalName()
	{
		return getPath()+"/"+getName();
	}
	
	/*
	 * En esta funcion cogemos la fecha de la ultima modificacion.
	 * El formato devuelto es tal cual lo cogemos del fichero en formato Date.
	 */
	public Date getModifiedDate()
	{
		return dbx_file_info.modifiedTime;
	}

	/*
	 * En esta funcion cogemos la fecha de la ultima modificacion.
	 * El formato devuelto es un long que devuelve el numero de segundos
	 * que han pasado desde el 1 de Enero de 1970 (Unix epoch).
	 * 
	 * NOTA: A la hora de utilizar este valor como parametro de ordenacion
	 * es recomendable usar esta funcion porque al devolver un valor numerico
	 * es mas facil realizar la ordenacion ya que solo hay que comparar caracteres.
	 */
	public String getModifiedDateAsUnixTimestamp()
	{
		/*
		 * Dividimos entre 1000 para pasar de milisegundos
		 * (formato que devuelve el metodo) a segundos.
		 */		
		return ""+(dbx_file_info.modifiedTime.getTime()/1000);
	}
	
	/*
	 * Funcion que carga, utilizando el API de Dropbox, el nombre del fichero.
	 * Esto lo necesitamos cuando abrimos el fichero epub.
	 */
	public DbxPath getFileAsPath()
	{
		return dbx_file_info.path;
	}
	
	/*
	 * Obtenemos el hash del fichero partiendo de su nombre completo
	 * (ruta + nombre), de esta forma, nos aseguramos que 2 ficheros
	 * que se llaman igual y que estan en distintas carpetas tienen
	 * hashes diferentes.
	 */
	public String getFileHash()
	{
		if(!is_hashed)
		{
			file_hash = SHA1.doHash(getCanonicalName());
			is_hashed = true;
		}
		
		return file_hash;
	}
	
	/*
	 * Funcion para verificar si el fichero es nuevo o no.
	 * Decimos que un fichero es nuevo cuando esta en remoto
	 * y aun no se han descargado su titulo e imagen de portada.
	 */
	
	public boolean isNew()
	{
		return is_new;
	}
	
	/*
	 * Funcion para marcar un fichero nuevo como ya actualizado.
	 */
	public void setUp2Date()
	{
		is_new = false;
	}
}
