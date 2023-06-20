package com.sergeybochkov.jbar.model;

import com.sergeybochkov.jbar.widgets.STableModel;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Decorations;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;

public final class ShieldTableModel extends STableModel<Shield> {

    public ShieldTableModel(Table table) {
        super(table);
        table.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent keyEvent) {
                if (keyEvent.keyCode == SWT.DEL) {
                    removeSelected();
                }
            }
        });

        Decorations composite = (Decorations) table.getParent();
        Menu menu = new Menu(composite, SWT.POP_UP);
        MenuItem item = new MenuItem(menu, SWT.PUSH);
        item.setText("Удалить выбранные");
        item.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent selectionEvent) {
                removeSelected();
            }
        });
        table.setMenu(menu);
    }

    @Override
    public int count() {
        int count = 0;
        for (Shield shield : items()) {
            count += shield.count();
        }
        return count;
    }
}
