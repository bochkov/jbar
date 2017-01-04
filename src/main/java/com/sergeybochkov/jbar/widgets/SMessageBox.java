package com.sergeybochkov.jbar.widgets;

import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class SMessageBox {

    private final MessageBox messageBox;

    public SMessageBox(Shell shell, int style) {
        messageBox = new MessageBox(shell, style);
    }

    public SMessageBox title(String title) {
        messageBox.setText(title);
        return this;
    }

    public SMessageBox message(String message) {
        messageBox.setMessage(message);
        return this;
    }

    public SMessageBox message(Throwable throwable) {
        messageBox.setMessage(throwable.toString());
        return this;
    }

    public void open() {
        messageBox.open();
    }
}
