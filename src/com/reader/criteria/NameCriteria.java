package com.reader.criteria;

import com.reader.file.GenericDropboxFile;

/*
 * Clase que define el criterio de busqueda basado en el nombre del fichero.
 * Todos los cirterios estan basados en el tipo de fichero que estamos
 * manejando, obtenido, del directorio remoto de Dropbox.
 * 
 * Added by: Javier Rodriguez.
 */

public class NameCriteria extends AbstractCriteria 
{
	public NameCriteria()
	{
		super(true);
	}
	
	public NameCriteria(boolean isDescendent) 
	{
		super(isDescendent);
	}

	/*
	 * Devuelve el criterio de ordenacion especifico para la ordenacion.
	 */
	@Override
	public String getCriteriaValue(GenericDropboxFile file)
	{
		return file.getName();
	}
}
