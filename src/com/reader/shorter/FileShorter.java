package com.reader.shorter;

import java.util.ArrayList;
import java.util.Arrays;
import com.reader.criteria.AbstractCriteria;
import com.reader.file.GenericDropboxFile;

/*
 * Clase que implementa la funcion de ordenacion de los ficheros segun
 * el criterio definido por el usuario.
 * 
 * Added by: Javier Rodriguez.
 */

public class FileShorter 
{	
	public static ArrayList<GenericDropboxFile> do_short(ArrayList<GenericDropboxFile> list, AbstractCriteria criteria)
	{
		int i_ct;
		Integer[] indexes = null;
		int list_size = list.size();
		String [] criteria_list = new String[list_size];
		ArrayIndexComparator comparator;
		
		/*
		 * Primer paso, rellenamos un array con los datos a ordenar segun el criterio de ordenacion.
		 */
		
		for(i_ct = 0;i_ct<list_size;i_ct++)
		{
			criteria_list[i_ct] = criteria.getCriteriaValue(list.get(i_ct));
		}
		
		/*
		 * Segundo paso. Orden el array rellenado en el paso #1 utilizando una funcion de ordenacion
		 * ya establecida.
		 */
		
		comparator = new ArrayIndexComparator(criteria_list);
		indexes = comparator.createIndexArray();
		Arrays.sort(indexes, comparator);
		
		/*
		 * Tercer paso. Copiar los elementos ordenados en un nuevo array siguiendo el orden
		 * ascendente o descente en funcion de los definido por el criterio.
		 */
		if(criteria.isDecendent())
			return copy_ordered_descendent_elements(list,indexes);
		
		return copy_ordered_elements(list,indexes);
	}
	
	/*
	 * Metodos privados para la copia de los elementos ya ordenados.
	 * Se han definido como privados porque no queremos que nadie externo
	 * a esta clase pueda utilizarlas.
	 */
	
	private static ArrayList<GenericDropboxFile> copy_ordered_elements(ArrayList<GenericDropboxFile>list,Integer [] indexes)
	{
		int i_ct;
		int list_size = list.size();
		ArrayList<GenericDropboxFile> new_list = new ArrayList<GenericDropboxFile>(list_size);
		
		for(i_ct=0;i_ct<indexes.length;i_ct++)
		{
			new_list.add(i_ct,list.get(indexes[i_ct].intValue()));
		}
		
		return new_list;
	}
	
	private static ArrayList<GenericDropboxFile> copy_ordered_descendent_elements(ArrayList<GenericDropboxFile>list,Integer [] indexes)
	{
		int i_ct,offset;
		int list_size = list.size();
		ArrayList<GenericDropboxFile> new_list = new ArrayList<GenericDropboxFile>(list_size);
		
		offset = list_size-1;
		
		for(i_ct=0;i_ct<indexes.length;i_ct++)
		{
			new_list.add(i_ct,list.get(indexes[offset-i_ct].intValue()));
		}
		
		return new_list;
	}
}
