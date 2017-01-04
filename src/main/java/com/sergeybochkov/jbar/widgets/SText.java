package com.sergeybochkov.jbar.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public final class SText {

    private final Text text;

    public SText(Composite composite) {
        this(composite, "");
    }

    public SText(Composite composite, String initText) {
        this.text = new Text(composite, SWT.BORDER);
        this.text.setText(initText);
        this.text.setLayoutData(new GridData());
    }

    public SText fill() {
        GridData data = (GridData) text.getLayoutData();
        data.horizontalAlignment = GridData.FILL;
        data.grabExcessHorizontalSpace = true;
        text.setLayoutData(data);
        return this;
    }

    public Text text() {
        return text;
    }
}
