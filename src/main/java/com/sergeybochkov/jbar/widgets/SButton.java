package com.sergeybochkov.jbar.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public final class SButton {

    private final Button button;

    public SButton(Composite composite, String text) {
        this(composite, text, () -> {});
    }

    public SButton(Composite composite, String text, final Selected selected) {
        button = new Button(composite, SWT.PUSH);
        button.setText(text);
        button.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent selectionEvent) {
                selected.select();
            }
        });
        button.setLayoutData(new GridData());
    }

    public SButton right() {
        GridData data = (GridData) button.getLayoutData();
        data.horizontalAlignment = GridData.END;
        button.setLayoutData(data);
        return this;
    }

    public SButton left() {
        GridData data = (GridData) button.getLayoutData();
        data.horizontalAlignment = GridData.BEGINNING;
        button.setLayoutData(data);
        return this;
    }

    public SButton span(int span) {
        GridData data = (GridData) button.getLayoutData();
        data.horizontalSpan = span;
        button.setLayoutData(data);
        return this;
    }

    public Button button() {
        return button;
    }
}
