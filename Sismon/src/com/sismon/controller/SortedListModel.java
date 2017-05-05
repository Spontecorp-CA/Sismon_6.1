package com.sismon.controller;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.swing.AbstractListModel;

/**
 * Clase que permite clasificar en un orden determinado el modelo de la lista
 * que usa ese modelo de datos. Este modelo es usado por algunas tablas en el
 * sistema.
 * 
 * @author jgcastillo
 */
public class SortedListModel extends AbstractListModel {

    // Define a SortedSet
    private SortedSet model;

    /**
     * El constructor inicializa un TreeSet a ser usado como repositorio 
     * temporal del modelo de datos
     */
    public SortedListModel() {
            // Create a TreeSet
        // Store it in SortedSet variable
        model = new TreeSet();
    }

    // ListModel methods
    /**
     * Obtiene el tamaño del modelo de datos
     * @return 
     */
    @Override
    public int getSize() {
        // Return the model size
        return model.size();
    }

    /**
     * Obtiene un elemento particular del modelo, referenciado por el índice 
     * dentro del modelo
     * @param index, el indice del elemento
     * @return Un Object que es el elemento indicado
     */
    @Override
    public Object getElementAt(int index) {
        // Return the appropriate element
        return model.toArray()[index];
    }

    // Other methods
    /**
     * Agrega un elemento al modelo
     * @param element ,el Object elemento a agregar al modelo
     */
    public void addElement(Object element) {
        if (model.add(element)) {
            fireContentsChanged(this, 0, getSize());
        }
    }

    /**
     * Agrega al modelo todos los elementos del arrglo que es pasado como
     * parámetro
     * @param elements, el arreglo Object[] que contienen los elementos a ser
     * agregados al modelo
     */
    public void addAll(Object[] elements) {
        Collection c = Arrays.asList(elements);
        model.addAll(c);
        fireContentsChanged(this, 0, getSize());
    }

    /**
     * Limpia el modelo de todos los elementos que contiene, al ejecutar
     * este método se dispara un evento fireContentsChanged(), indicando que
     * el contenido del modelo ha sido modificado.
     */
    public void clear() {
        model.clear();
        fireContentsChanged(this, 0, getSize());
    }

    /**
     * Método que evalúa si un elemento está contenido en el modelo. De estar
     * el elemento en el modelo se obtiene <b>true</b> como respuesta, de lo 
     * contrario se obtiene <b>false</b>.
     * @param element, en elemento a ser evaluado dentro del modelo
     * @return true, si está en el modelo; false en caso contrario
     */
    public boolean contains(Object element) {
        return model.contains(element);
    }

    /**
     * Retorna el primer elemento del modelo 
     * @return 
     */
    public Object firstElement() {
        // Return the appropriate element
        return model.first();
    }

    /**
     * Retorna un Iterator que permite recorrer el modelo, en caso de que se
     * desee observar su contenido.
     * @return 
     */
    public Iterator iterator() {
        return model.iterator();
    }

    /**
     * Retorna el último elemento del modelo
     *
     * @return
     */
    public Object lastElement() {
        // Return the appropriate element
        return model.last();
    }

    /**
     * Elimina el elemento indicado del modelo, retorna <b>true</b> si el
     * elemento es removido exitósamente. En caso contrario retorna <b>false</b>
     */
    public boolean removeElement(Object element) {
        boolean removed = model.remove(element);
        if (removed) {
            fireContentsChanged(this, 0, getSize());
        }
        return removed;
    }
}

