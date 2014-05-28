package com.reader.criteria;

import com.reader.exception.InvalidCriteriaException;

/*
 * Clase que implementa el Patron factoria. Como se pedia realizar diferentes
 * ordenaciones siguiendo unos criterios, he decidido crear una jerarquía de 
 * clases con el fin de encapsular la funcionalidad e implementar una solucion
 * mas elegante y que nos permitiria, mas adelante incluir nuevos criterios de
 * ordenacion de una manera rapida y eficiente.
 * 
 * La clase factoria solo instancia el criterio que se le indique como parametro
 * de entrada. Si el criterio de busqueda no existe, la funcion lanza una excepcion.
 * 
 * Added by: Javier Rodriguez.
 */

public class CriteriaFactory 
{
	public static final int BY_NAME = 0;
	public static final int BY_DATE = 1;
	public static final int BY_TITLE = 2;
	
	public static AbstractCriteria getShortCriteria(int criteria) throws InvalidCriteriaException
	{
		switch(criteria)
		{
			case BY_NAME:
				return new NameCriteria();
			case BY_DATE:
				return new DateCriteria();
			case BY_TITLE:
				return new TitleCriteria();
			default:
				throw new InvalidCriteriaException();
		}
	}
}
