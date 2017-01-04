package com.sergeybochkov.jbar.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

public final class SMenuItem {

    private final MenuItem menuItem;
    private final Selected  selected;

    public SMenuItem(Menu menu, String text, Selected selected) {
        this(menu, SWT.NONE, text, selected);
    }

    public SMenuItem(Menu menu, int style, String text, Selected selected) {
        menuItem = new MenuItem(menu, style);
        menuItem.setText(text);
        this.selected = selected;
        menuItem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent selectionEvent) {
                selected.select();
            }
        });
    }

    public SMenuItem checked(boolean checked) {
        menuItem.setSelection(checked);
        if (checked)
            selected.select();
        return this;
    }

    public MenuItem menuItem() {
        return menuItem;
    }
}
