package com.reader.exception;

public class NoImageLoadedException extends Exception 
{
	public NoImageLoadedException()
	{
		super("Error. No hay cargada una imagen para esa portada");
	}
}
