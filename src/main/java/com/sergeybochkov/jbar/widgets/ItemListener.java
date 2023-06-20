package com.sergeybochkov.jbar.widgets;

import lombok.Data;
import org.eclipse.swt.internal.SWTEventListener;

public interface ItemListener extends SWTEventListener {

    @Data
    final class ItemsEvent {
        private final int total;
    }

    void itemsChanged(ItemsEvent ev);

}
