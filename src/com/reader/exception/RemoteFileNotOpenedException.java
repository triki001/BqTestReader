package com.reader.exception;

/*
 * Esta excepcion se lanzara cuando la aplicacion intente
 * abrir un fichero remoto (en este caso, un epub) y falle.
 * 
 * Added by: Javier Rodriguez.
 */

@SuppressWarnings("serial")
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
