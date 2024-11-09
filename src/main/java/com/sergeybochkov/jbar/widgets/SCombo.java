package com.sergeybochkov.jbar.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

import java.util.Collection;

public final class SCombo implements SControl<Combo, SCombo> {

    private final Combo combo;

    public SCombo(Composite composite, boolean editable) {
        combo = new Combo(composite, SWT.DROP_DOWN | (editable ? SWT.READ_ONLY : SWT.SIMPLE));
        combo.setLayoutData(new GridData());
    }

    @Override
    public SCombo parent() {
        return this;
    }

    @Override
    public Combo widget() {
        return combo;
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
}
