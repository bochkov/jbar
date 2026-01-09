package com.sergeybochkov.jbar.widgets;

public interface ItemListener {

    record ItemsEvent(int total) {
    }

    void itemsChanged(ItemsEvent ev);

}
