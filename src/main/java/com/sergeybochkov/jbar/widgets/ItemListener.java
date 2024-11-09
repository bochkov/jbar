package com.sergeybochkov.jbar.widgets;

import org.eclipse.swt.internal.SWTEventListener;

public interface ItemListener extends SWTEventListener {

    record ItemsEvent(int total) {
    }

    void itemsChanged(ItemsEvent ev);

}
