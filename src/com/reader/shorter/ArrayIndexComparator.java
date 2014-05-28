package com.reader.shorter;

import java.util.Comparator;

/*
 * Clase define un tipo de fichero epub. Es una clase especifica
 * de la super clase GenericDropboxFile. Ademas de los atributos
 * y metodos que tiene cualquier "file", tiene metodos especificos
 * para el manero de epubs.
 * 
 * Clase especifica para utilizar en el comparador que sera usado 
 * a la hora de ordenar.
 * 
 * Codigo extraido de stackoverflow.
 * 
 * http://stackoverflow.com/questions/4859261/get-the-indices-of-an-array-after-sorting
 * 
 * He decidido utilizarlo porque es una solucion elegante a el problema
 * que estoy tratando.
 * 
 * Added by: Javier Rodriguez.
 */

public class ArrayIndexComparator implements Comparator<Integer>
{
    private final String[] array;

    /*
     * Al constructor se le pasa el array que se desea.
     */
    public ArrayIndexComparator(String[] array)
    {
        this.array = array;
    }

    //Rellena el array numerando las posiciones.
    public Integer[] createIndexArray()
    {
        Integer[] indexes = new Integer[array.length];
        for (int i = 0; i < array.length; i++)
        {
            indexes[i] = i;
        }
        return indexes;
    }

    @Override
    public int compare(Integer index1, Integer index2)
    {
        return array[index1].compareTo(array[index2]);
    }
}