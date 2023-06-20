package com.sergeybochkov.jbar.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public final class SText implements SControl<Text, SText> {

    private final Text text;

    public SText(Composite composite) {
        this(composite, "");
    }

    public SText(Composite composite, String initText) {
        this.text = new Text(composite, SWT.BORDER);
        this.text.setText(initText);
        this.text.setLayoutData(new GridData());
    }

    @Override
    public SText parent() {
        return this;
    }

    @Override
    public Text widget() {
        return text;
    }
}
