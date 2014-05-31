package com.reader.exception;

/*
 * Clase que define la excepcion que puede darse en caso de que el usuario
 * introduzca mal el criterio de ordenacion que esta buscando.
 * 
 * Added by: Javier Rodriguez.
 */

@SuppressWarnings("serial")
public class InvalidCriteriaException extends Exception 
{
	public InvalidCriteriaException()
	{
		super("Error. El criterio de ordenacion selecionado no es valido.");
	}
}
