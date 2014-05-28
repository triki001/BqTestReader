package com.reader.file;

import java.util.Date;

import com.dropbox.sync.android.DbxFileInfo;

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
	private DbxFileInfo dbx_file;
	
	public GenericDropboxFile(DbxFileInfo file)
	{
		this.dbx_file = file;
	}

	protected DbxFileInfo getRawDropboxFile()
	{
		return dbx_file;
	}
	
	/*
	 * Aqui mostramos la ruta donde esta ubicado. 
	 * Por ejemplo, si el fichero esta en /home/javier/prueba.txt devolvemos prueba.txt
	 */
	public String getName()
	{
		return dbx_file.path.getName();
	}
	
	/*
	 * Aqui mostramos la ruta donde esta ubicado. 
	 * Por ejemplo, si el fichero esta en /home/javier/prueba.txt devolvemos /home/javier
	 */
	public String getPath()
	{
		return dbx_file.path.getName();
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
		return dbx_file.modifiedTime;
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
		return ""+dbx_file.modifiedTime.getTime();
	}
}
