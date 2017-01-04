package com.sergeybochkov.jbar.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;

public final class SGroup {

    private final Group group;

    public SGroup(Shell shell) {
        group = new Group(shell, SWT.SHADOW_ETCHED_IN);
        group.setText("Исходные данные");
        group.setLayout(new GridLayout(2, false));
        GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
        //gridData.horizontalAlignment = GridData.FILL_HORIZONTAL;
        gridData.horizontalSpan = 2;
        group.setLayoutData(gridData);
    }

    public Group group() {
        return group;
    }
}
