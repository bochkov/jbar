package com.sergeybochkov.jbar;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;

import com.sergeybochkov.jbar.widgets.SButton;
import com.sergeybochkov.jbar.widgets.SLabel;
import com.sergeybochkov.jbar.widgets.SLink;
import org.apache.commons.io.IOUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public final class DlgAbout extends Dialog {

    private static final int START_YEAR = 2011;

    private final Shell shell;

    public DlgAbout(Shell parent) {
        super(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
        shell = new Shell(getParent(), getStyle());
        shell.setText("О программе");
        shell.setImage(getParent().getImage());
        shell.setLayout(new GridLayout(1, true));

        Label iLabel = new SLabel(shell, SWT.CENTER)
                .hFill().widget();
        iLabel.setImage(new Image(null, DlgAbout.class.getResourceAsStream("/images/barcode.png")));

        Label title = new SLabel(shell, SWT.CENTER, AppProps.TITLE)
                .hFill().widget();
        title.setFont(new Font(getParent().getDisplay(), "Sans", 16, SWT.BOLD));

        Label version = new SLabel(shell, SWT.CENTER)
                .hFill().widget();
        version.setFont(new Font(getParent().getDisplay(), "Sans", 12, SWT.NORMAL));
        try {
            version.setText("v." + IOUtils.resourceToString("/version.txt", StandardCharsets.UTF_8));
        } catch (IOException ex) {
            version.setText("");
        }

        new SLabel(shell, SWT.CENTER, "Программа для печати поверительных клейм")
                .hFill();
        new SLabel(shell, SWT.CENTER, "© Бочков С.А.")
                .hFill();

        new SLink(
                shell,
                "<a>bochkov.sa@gmail.com</a>",
                ev -> Program.launch(String.format("mailto:%s?subject=JBar", ev.text))
        ).hFill().center();

        new SLabel(shell, SWT.CENTER, copyStr())
                .hFill();

        Button okButton = new SButton(shell, "Закрыть", ev -> shell.close())
                .hFill().pad(-1, 10).center().widget();

        shell.setDefaultButton(okButton);
        okButton.forceFocus();
    }

    public void open() {
        shell.pack();
        shell.setLocation(center());
        shell.open();
    }

    private String copyStr() {
        int year = Calendar.getInstance().get(Calendar.YEAR);
        return year > START_YEAR ?
                String.format("%s-%s", START_YEAR, year) :
                String.format("%s", START_YEAR);
    }

    private Point center() {
        Rectangle bounds = shell.getParent().getBounds();
        Point dialogSize = shell.getSize();
        int x = bounds.x + (bounds.width - dialogSize.x) / 2;
        int y = bounds.y + (bounds.height - dialogSize.y) / 2;
        return new Point(x, y);
    }
}
