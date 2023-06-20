package com.sergeybochkov.jbar.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public final class SConstLabel implements SControl<Label, SConstLabel> {

    private final Label label;
    private final String constStr;

    public SConstLabel(Composite composite, String constStr) {
        this.label = new Label(composite, SWT.NONE);
        this.label.setText(constStr);
        this.label.setLayoutData(new GridData());
        this.constStr = constStr;
    }

    public void setText(String string) {
        this.label.setText(String.format("%s %s", constStr, string));
    }

    @Override
    public SConstLabel parent() {
        return this;
    }

    @Override
    public Label widget() {
        return label;
    }
}
