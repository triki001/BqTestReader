package com.reader.exception;

@SuppressWarnings("serial")
public class EpubHandlerBookException extends Exception 
{
	/*
	 * Clase que define la excepcion cuando ocurre que no se puede
	 * abrir un fichero epub.
	 * 
	 * Added by: Javier Rodriguez.
	 */
	
	public EpubHandlerBookException()
	{
		super("Error. Hubo un error cuando se intentaba recuperar el libro");
	}
}
