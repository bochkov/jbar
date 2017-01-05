package com.sergeybochkov.jbar.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Layout;

public final class SComposite {

    private final Composite composite;

    public SComposite(Composite composite) {
        this.composite = new Composite(composite, SWT.NONE);
        this.composite.setLayoutData(new GridData());
    }

    public SComposite layout(Layout layout) {
        this.composite.setLayout(layout);
        return this;
    }

    public SComposite right() {
        GridData data = (GridData) composite.getLayoutData();
        data.horizontalAlignment = GridData.END;
        data.grabExcessHorizontalSpace = true;
        composite.setLayoutData(data);
        return this;
    }

    public SComposite span(int span) {
        GridData data = (GridData) composite.getLayoutData();
        data.horizontalSpan = span;
        composite.setLayoutData(data);
        return this;
    }

    public Composite composite() {
        return composite;
    }
}
