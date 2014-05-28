package com.reader.criteria;

import com.reader.file.EpubDropboxFile;
import com.reader.file.GenericDropboxFile;

/*
 * Clase que define el criterio de busqueda basado en el titulo del epub.
 * Todos los cirterios estan basados en el tipo de fichero que estamos
 * manejando, obtenido, del directorio remoto de Dropbox.
 * 
 * Added by: Javier Rodriguez.
 */

public class TitleCriteria extends AbstractCriteria 
{
	public TitleCriteria()
	{
		super(true);
	}
	
	public TitleCriteria(boolean isDescendent) 
	{
		super(isDescendent);
	}
	
	/*
	 * Devuelve el criterio de ordenacion especifico para la ordenacion.
	 */
	@Override
	public String getCriteriaValue(GenericDropboxFile file) 
	{
		if(file instanceof EpubDropboxFile)
			return ((EpubDropboxFile)file).getTitle();
		
		return file.getName();
	}
}
