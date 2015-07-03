package com.sergeybochkov.jbar;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

/** Список всех однотипных наклеек */
public class ShieldCollection {
    /** Конструктор класса */
    public ShieldCollection(){
        shields = new LinkedList<>();
    }

    /**
     * Возвращает коллекцию однотипных наклеек
     * @return Collection
     */
    public Collection getShields(){
        return Collections.unmodifiableCollection(shields);
    }

    /**
     * Добавляет однотипные наклейки в список
     * @param shield наклейка
     */
    public void add(Shield shield){
        shields.add(shield);
    }

    /**
     * Удаляет набор наклеек из коллекции
     * @param indices индексы
     */
    public void remove(int[] indices){
        Object[] array = shields.toArray();
        for (int i : indices){
            Shield obj = (Shield)Array.get(array, i);
            shields.remove(obj);
        }
    }

    /** Очищает список наклеек */
    public void clear(){
        shields.clear();
    }

    /**
     * Возвращает общее количество печатаемых наклеек
     * @return int
     */
    public int getCollSize(){
        int size = 0;
        for (Shield shield : shields)
            size += shield.getNum();
        return size;
    }

    private Collection<Shield> shields;
}
