package com.reader.exception;

public class RemoteFileNotOpenedException extends Exception 
{
	/*
	 * Clase que define la excepcion cuando no se puede abrir un 
	 * fichero en el directorio remoto de dropbox.
	 *
	 * Added by: Javier Rodriguez.
	 */
	public RemoteFileNotOpenedException()
	{
		super("Error. El fichero remoto no esta abierto");
	}
}
