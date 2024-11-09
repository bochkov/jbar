package com.sergeybochkov.jbar.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import java.util.function.Consumer;

public final class SButton implements SControl<Button, SButton> {

    private final Button button;

    public SButton(Composite composite, String text, final Consumer<SelectionEvent> consumer) {
        button = new Button(composite, SWT.PUSH);
        button.setText(text);
        button.setLayoutData(new GridData());
        button.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                consumer.accept(e);
            }
        });
    }

    @Override
    public SButton parent() {
        return this;
    }

    @Override
    public Button widget() {
        return button;
    }
}
