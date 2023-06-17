package com.sergeybochkov.jbar;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;

import org.apache.commons.io.IOUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.*;

public final class AboutDialog extends Dialog {

    private static final int START_YEAR = 2011;

    private final Shell shell;

    public AboutDialog(Shell parent) {
        super(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
        shell = new Shell(getParent(), getStyle());
        shell.setText("О программе");
        shell.setImage(getParent().getImage());

        shell.setLayout(new GridLayout(1, true));
        Label iLabel = new Label(shell, SWT.CENTER);
        iLabel.setImage(new Image(null, AboutDialog.class.getResourceAsStream("/images/barcode.png")));
        iLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        Label title = new Label(shell, SWT.CENTER);
        title.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        title.setText("JBar");
        title.setFont(new Font(getParent().getDisplay(), "Sans", 16, SWT.BOLD));

        Label version = new Label(shell, SWT.CENTER);
        version.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        version.setFont(new Font(getParent().getDisplay(), "Sans", 12, SWT.NORMAL));
        try {
            version.setText("v." + IOUtils.resourceToString("/version.txt", StandardCharsets.UTF_8));
        } catch (IOException ex) {
            version.setText("");
        }

        Label description = new Label(shell, SWT.CENTER);
        description.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        description.setText("Программа для печати поверительных клейм");

        Label copy = new Label(shell, SWT.CENTER);
        copy.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        copy.setText("© Бочков С.А.");

        Link link = new Link(shell, SWT.NONE);
        link.setLayoutData(new GridData(SWT.CENTER, SWT.NONE, true, false));
        link.setText("<a>bochkov.sa@gmail.com</a>");
        link.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent selectionEvent) {
                Program.launch(String.format("mailto:%s?subject=JBar", selectionEvent.text));
            }
        });

        Label years = new Label(shell, SWT.CENTER);
        years.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        years.setText(copyStr());

        Label strut = new Label(shell, SWT.CENTER);
        strut.setText("");

        Button okButton = new Button(shell, SWT.PUSH);
        okButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER));
        okButton.setText("Закрыть");
        okButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent selectionEvent) {
                shell.close();
            }
        });
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
        Rectangle bounds = (shell.getParent() == null) ?
                shell.getDisplay().getBounds() :
                shell.getParent().getBounds();
        Point dialogSize = shell.getSize();
        int x = bounds.x + (bounds.width - dialogSize.x) / 2;
        int y = bounds.y + (bounds.height - dialogSize.y) / 2;
        return new Point(x, y);
    }
}
