package com.reader.criteria;

import com.reader.file.GenericDropboxFile;

/*
 * Clase abstracta que define el super tipo con los atributos comunes 
 * a todos los criterios de ordenacion. El metodo abstracto es especifico
 * para cada clase "hijo" y debe ser, por lo tanto, implementado por
 * dicha clase.
 * 
 * La idea de encapsudar los criterios de esta manera es que, de esta forma,
 * podemo reutilizar este codigo, mas adelante, para utilizar esto como criterio
 * de busqueda u otra cosa que necesitemos.
 * 
 * El criterio tambien contiene informacion a cerca del tipo de ordenacion que
 * se va a realizar (ascedente o descendente).
 * 
 * Todos los cirterios estan basados en el tipo de fichero que estamos
 * manejando, obtenido, del directorio remoto de Dropbox.
 * 
 * Added by: Javier Rodriguez.
 */

public abstract class AbstractCriteria 
{
	private boolean isDescendent;
	
	public AbstractCriteria()
	{
		isDescendent = true;
	}
	
	public AbstractCriteria(boolean isDescendent)
	{
		this.isDescendent = isDescendent;
	}
		
	public void setDescendent(boolean value)
	{
		this.isDescendent = value;
	}
	
	public boolean isDecendent()
	{
		return this.isDescendent;
	}
	
	/*
	 * Devuelve el criterio de ordenacion especifico para la ordenacion.
	 * Definido como abstracto para ser implementado por todas las clases
	 * que hereden de esta.
	 */
	public abstract String getCriteriaValue(GenericDropboxFile file);
}
