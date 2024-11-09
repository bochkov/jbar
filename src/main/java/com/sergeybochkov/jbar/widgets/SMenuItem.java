package com.sergeybochkov.jbar.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import java.util.function.Consumer;

public final class SMenuItem implements SWidget {

    private final MenuItem menuItem;
    private final Consumer<SelectionEvent> selected;

    public SMenuItem(Menu menu, String text, Consumer<SelectionEvent> selected) {
        this(menu, SWT.NONE, text, selected);
    }

    public SMenuItem(Menu menu, int style, String text, Consumer<SelectionEvent> sel) {
        menuItem = new MenuItem(menu, style);
        menuItem.setText(text);
        this.selected = sel;
        menuItem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent ev) {
                selected.accept(ev);
            }
        });
    }

    public void checked(boolean checked) {
        menuItem.setSelection(checked);
        if (checked) {
            selected.accept(null);
        }
    }
}
