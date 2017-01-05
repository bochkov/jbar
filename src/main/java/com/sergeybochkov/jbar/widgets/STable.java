package com.sergeybochkov.jbar.widgets;

import com.sergeybochkov.jbar.Shield;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class STable {

    private final Table table;
    private final List<Shield> shields = new ArrayList<>();

    public STable(Decorations composite, int style, String... columns) {
        table = new Table(composite, style);
        for (String col : columns)
            new TableColumn(table, SWT.RIGHT).setText(col);
        table.getVerticalBar().setVisible(true);
        table.getHorizontalBar().setVisible(true);
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        table.setMenu(popup(composite));
        table.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent keyEvent) {
                if (keyEvent.keyCode == SWT.DEL) {
                    removeSelected();
                }
            }
        });
        table.setLayoutData(new GridData());
        Arrays.stream(table.getColumns()).forEach(TableColumn::pack);
    }

    private Menu popup(Decorations composite) {
        Menu menu = new Menu(composite, SWT.POP_UP);
        MenuItem item = new MenuItem(menu, SWT.PUSH);
        item.setText("Удалить выбранные");
        item.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent selectionEvent) {
                removeSelected();
            }
        });
        return menu;
    }

    public STable fill() {
        GridData data = (GridData) table.getLayoutData();
        data.horizontalAlignment = GridData.FILL;
        data.verticalAlignment = GridData.FILL;
        data.grabExcessHorizontalSpace = true;
        data.grabExcessVerticalSpace = true;
        table.setLayoutData(data);
        return this;
    }

    public STable span(int span) {
        GridData data = (GridData) table.getLayoutData();
        data.horizontalSpan = span;
        table.setLayoutData(data);
        return this;
    }

    public void removeAll() {
        table.removeAll();
        shields.clear();
    }

    public void removeSelected() {
        table.remove(table.getSelectionIndices());
        Arrays.stream(table.getSelectionIndices()).forEach(shields::remove);
    }

    public void add(Shield shield) {
        new TableItem(table, SWT.NONE).setText(new String[]{
                shield.department(),
                shield.shortDate(),
                shield.verification(),
                String.valueOf(shield.count())
        });
        Arrays.stream(table.getColumns()).forEach(TableColumn::pack);
        shields.add(shield);
    }

    public List<Shield> items() {
        return shields;
    }

    public Table table() {
        return table;
    }
}
