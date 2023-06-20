package com.sergeybochkov.jbar.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public final class SLabel implements SControl<Label, SLabel> {

    private final Label label;

    public SLabel(Composite composite, int style) {
        this(composite, style, "");
    }

    public SLabel(Composite composite, String text) {
        this(composite, text, "");
    }

    public SLabel(Composite composite, int style, String text) {
        this(composite, style, text, null);
    }

    public SLabel(Composite composite, String text, String tooltip) {
        this(composite, SWT.NONE, text, tooltip);
    }

    public SLabel(Composite composite, int style, String text, String tooltip) {
        label = new Label(composite, style);
        label.setText(text);
        label.setToolTipText(tooltip);
        label.setLayoutData(new GridData());
    }

    @Override
    public SLabel parent() {
        return this;
    }

    @Override
    public Label widget() {
        return label;
    }
}
