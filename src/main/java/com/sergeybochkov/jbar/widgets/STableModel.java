package com.sergeybochkov.jbar.widgets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

@Slf4j
@RequiredArgsConstructor
public abstract class STableModel<T extends TableItemizable> {

    private final Table table;
    private final List<T> items = new ArrayList<>();
    private final List<ItemListener> itemListeners = new ArrayList<>();

    public void addItemListener(ItemListener listener) {
        itemListeners.add(listener);
    }

    private void notifyListeners() {
        for (ItemListener listener : itemListeners) {
            listener.itemsChanged(
                    new ItemListener.ItemsEvent(
                            items.size()
                    )
            );
        }
    }

    private void fireTableDataChanged() {
        table.removeAll();
        for (T item : items) {
            new TableItem(table, SWT.NONE).setText(item.toItem());
        }
        notifyListeners();
    }

    public int count() {
        return this.items.size();
    }

    public void add(T item) {
        this.items.add(item);
        fireTableDataChanged();
    }

    public void removeSelected() {
        List<T> sel = Arrays.stream(table.getSelectionIndices())
                .mapToObj(items::get)
                .toList();
        this.items.removeAll(sel);
        fireTableDataChanged();
    }

    public List<T> items() {
        return items;
    }

    public void clear() {
        this.items.clear();
        fireTableDataChanged();
    }

}
