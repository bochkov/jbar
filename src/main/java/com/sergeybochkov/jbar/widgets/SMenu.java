package com.sergeybochkov.jbar.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

public final class SMenu {

    private final Menu menu;

    public SMenu(Menu main, String text) {
        menu = new Menu(main);
        MenuItem item = new MenuItem(main, SWT.CASCADE);
        item.setText(text);
        item.setMenu(menu);
    }

    public Menu menu() {
        return menu;
    }
}
