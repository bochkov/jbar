package com.sergeybochkov.jbar.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Shell;

public final class SGroup implements SControl<Group, SGroup> {

    private final Group group;

    public SGroup(Shell shell, Layout layout, String text) {
        group = new Group(shell, SWT.SHADOW_ETCHED_IN);
        group.setLayout(layout);
        group.setText(text);
        group.setLayoutData(new GridData());
    }

    @Override
    public SGroup parent() {
        return this;
    }

    @Override
    public Group widget() {
        return group;
    }
}
