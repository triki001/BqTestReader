package com.reader.exception;

/*
 * Excepcion que trata de reflejar el comportamiento anomalo del
 * programa cuando la imagen no ha sido cargada correctamente.
 * 
 * Added by: Javier Rodriguez.
 */

@SuppressWarnings("serial")
public class NoImageLoadedException extends Exception 
{
	public NoImageLoadedException()
	{
		super("Error. No hay cargada una imagen para esa portada");
	}
}
