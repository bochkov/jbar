package com.sergeybochkov.jbar.widgets;

import java.util.Arrays;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

public final class STable implements SControl<Table, STable> {

    private final Table table;

    public STable(Composite composite, int style, String... columns) {
        table = new Table(composite, style);
        for (String col : columns)
            new TableColumn(table, SWT.RIGHT).setText(col);
        table.getVerticalBar().setVisible(true);
        table.getHorizontalBar().setVisible(true);
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        table.setLayoutData(new GridData());
        Arrays.stream(table.getColumns()).forEach(TableColumn::pack);
    }

    @Override
    public STable parent() {
        return this;
    }

    @Override
    public Table widget() {
        return table;
    }

}
