package com.sergeybochkov.jbar.widgets;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Control;

public interface SControl<T extends Control, K> {

    K parent();

    T widget();

    default K left() {
        T widget = widget();
        GridData data = (GridData) widget.getLayoutData();
        data.horizontalAlignment = GridData.BEGINNING;
        widget.setLayoutData(data);
        return parent();
    }

    default K center() {
        T widget = widget();
        GridData data = (GridData) widget.getLayoutData();
        data.horizontalAlignment = GridData.CENTER;
        widget.setLayoutData(data);
        return parent();
    }

    default K right() {
        T widget = widget();
        GridData data = (GridData) widget.getLayoutData();
        data.horizontalAlignment = GridData.END;
        widget.setLayoutData(data);
        return parent();
    }

    default K hFill() {
        T widget = widget();
        GridData data = (GridData) widget.getLayoutData();
        data.horizontalAlignment = GridData.FILL;
        data.grabExcessHorizontalSpace = true;
        widget.setLayoutData(data);
        return parent();
    }

    default K vFill() {
        T widget = widget();
        GridData data = (GridData) widget.getLayoutData();
        data.verticalAlignment = GridData.FILL;
        data.grabExcessVerticalSpace = true;
        widget.setLayoutData(data);
        return parent();
    }

    default K span(int span) {
        T widget = widget();
        GridData data = (GridData) widget.getLayoutData();
        data.horizontalSpan = span;
        widget.setLayoutData(data);
        return parent();
    }

    default K pad(int h, int v) {
        T widget = widget();
        GridData data = (GridData) widget.getLayoutData();
        if (h >= 0)
            data.horizontalIndent = h;
        if (v >= 0)
            data.verticalIndent = v;
        widget.setLayoutData(data);
        return parent();
    }
}
