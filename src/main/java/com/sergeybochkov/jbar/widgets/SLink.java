package com.sergeybochkov.jbar.widgets;

import java.util.function.Consumer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Link;

public final class SLink implements SControl<Link, SLink> {

    private final Link link;

    public SLink(Composite composite, String text, Consumer<SelectionEvent> consumer) {
        this.link = new Link(composite, SWT.NONE);
        this.link.setLayoutData(new GridData());
        this.link.setText(text);
        this.link.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                consumer.accept(e);
            }
        });
    }

    @Override
    public SLink parent() {
        return this;
    }

    @Override
    public Link widget() {
        return link;
    }
}
