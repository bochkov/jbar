package com.sergeybochkov.jbar.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Layout;

public final class SComposite implements SControl<Composite, SComposite> {

    private final Composite composite;

    public SComposite(Composite composite, Layout layout) {
        this.composite = new Composite(composite, SWT.NONE);
        this.composite.setLayout(layout);
        this.composite.setLayoutData(new GridData());
    }

    @Override
    public SComposite parent() {
        return this;
    }

    @Override
    public Composite widget() {
        return composite;
    }
}
