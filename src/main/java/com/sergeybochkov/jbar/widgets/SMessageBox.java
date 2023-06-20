package com.sergeybochkov.jbar.widgets;

import com.sergeybochkov.jbar.AppProps;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class SMessageBox {

    private final MessageBox messageBox;

    public SMessageBox(Shell shell, int style) {
        this(shell, style, "");
        String title = switch (style) {
            case SWT.ICON_ERROR -> "Ошибка";
            case SWT.ICON_WARNING -> "Предупреждение";
            default -> AppProps.TITLE;
        };
        messageBox.setText(title);
    }

    public SMessageBox(Shell shell, int style, String title) {
        messageBox = new MessageBox(shell, style);
        messageBox.setText(title);
    }

    public void message(Throwable throwable) {
        message(throwable.toString());
    }

    public void message(String message) {
        messageBox.setMessage(message);
        messageBox.open();
    }
}
