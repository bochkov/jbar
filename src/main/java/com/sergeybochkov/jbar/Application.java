package com.sergeybochkov.jbar;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

@Slf4j
public final class Application {

    public static void main(String[] args) throws Exception {
        LOG.info("application started");
        AppProps.getInstance().setup();
        AppProps.getInstance().load();
        Display display = new Display();
        Shell shell = new ShlMain(display).open();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
        AppProps.getInstance().save();
        display.dispose();
    }
}
