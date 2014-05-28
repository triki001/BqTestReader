package com.reader.criteria;

import com.reader.file.GenericDropboxFile;

/*
 * Clase que define el criterio de busqueda basado en la fecha de ultima modificacion.
 * Todos los cirterios estan basados en el tipo de fichero que estamos
 * manejando, obtenido, del directorio remoto de Dropbox.
 * 
 * Added by: Javier Rodriguez.
 */

public class DateCriteria extends AbstractCriteria 
{
	public DateCriteria()
	{
		super(true);
	}
	
	public DateCriteria(boolean isDescendent) 
	{
		super(isDescendent);
	}

	/*
	 * Devuelve el criterio de ordenacion especifico para la ordenacion.
	 */
	@Override
	public String getCriteriaValue(GenericDropboxFile file) 
	{
		return file.getModifiedDateAsUnixTimestamp();
	}
}
