package com.sergeybochkov.jbar.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public final class SLabel {

    private final Label label;

    public SLabel(Composite composite, String text) {
        this(composite, text, "");
    }

    public SLabel(Composite composite, String text, String tooltip) {
        label = new Label(composite, SWT.NONE);
        label.setText(text);
        label.setToolTipText(tooltip);
        label.setLayoutData(new GridData());
    }

    public SLabel right() {
        GridData data = (GridData) label.getLayoutData();
        data.horizontalAlignment = GridData.END;
        label.setLayoutData(data);
        return this;
    }

    public SLabel left() {
        GridData data = (GridData) label.getLayoutData();
        data.horizontalAlignment = GridData.BEGINNING;
        label.setLayoutData(data);
        return this;
    }

    public SLabel fill() {
        GridData data = (GridData) label.getLayoutData();
        data.horizontalAlignment = GridData.FILL;
        data.grabExcessHorizontalSpace = true;
        label.setLayoutData(data);
        return this;
    }

    public SLabel span(int span) {
        GridData data = (GridData) label.getLayoutData();
        data.horizontalSpan = span;
        label.setLayoutData(data);
        return this;
    }

    public Label label() {
        return label;
    }
}
