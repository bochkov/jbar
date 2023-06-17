package com.sergeybochkov.jbar.widgets;

import java.util.Collection;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

public final class SCombo {

    private final Combo combo;

    public SCombo(Composite composite, boolean editable) {
        combo = new Combo(composite, SWT.DROP_DOWN | (editable ? SWT.READ_ONLY : SWT.NO));
        combo.setLayoutData(new GridData());
    }

    public SCombo fill() {
        GridData data = (GridData) combo.getLayoutData();
        data.horizontalAlignment = GridData.FILL;
        data.grabExcessHorizontalSpace = true;
        combo.setLayoutData(data);
        return this;
    }

    public SCombo items(String... items) {
        combo.setItems(items);
        return this;
    }

    public SCombo items(Collection<String> items) {
        combo.setItems(items.toArray(String[]::new));
        return this;
    }

    public SCombo select(int index) {
        combo.select(index);
        return this;
    }

    public SCombo set(String text) {
        combo.setText(text);
        return this;
    }

    public Combo combo() {
        return combo;
    }
}
