package com.sergeybochkov.helpers;

/**
 * Класс пары чисел
 */
public class Pair {
    public Pair(int first, int second){
        this.first = first;
        this.second = second;
    }

    public int getFirst() { return first; }
    public int getSecond() { return second; }

    private int first;
    private int second;
}
