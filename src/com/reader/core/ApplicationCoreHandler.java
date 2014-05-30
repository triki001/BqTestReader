package com.reader.core;

import android.content.Context;

/*
 * Clase "core" que nos permitira manejarnos mas comodamente con los handlers
 * que definen, en gran medida, el funcionamiento del sistema.
 * 
 * Esta clase, al menos por ahora, no tiene una grandisima utilidad pero se esperaria
 * que en el futuro si que tenga mas. La idea es encapsular, en la medida de lo posible
 * el funcionamiento de la logica de la app para que el codigo dentro de las 
 * activities sea lo mas claro posible.
 */

public class ApplicationCoreHandler 
{
	/*
	 * Obtenemos, de un handler de conexion, el handler de ficheros
	 * asociado a esa cuenta.
	 */
	
	public static ConnectionHandler getConnectionHandler(Context context)
	{
		return ConnectionHandler.getInstance(context);
	}
	
	/*
	 * Obtenemos, de un contexto, el handler de ficheros
	 * asociado a esa cuenta.
	 */

	public static FilesHandler getFilesHandler(ConnectionHandler con)
	{
		return FilesHandler.getInstance(con.getDropboxAccount());
	}
}
