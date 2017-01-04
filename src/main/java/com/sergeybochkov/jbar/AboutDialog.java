package com.sergeybochkov.jbar;

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

import java.util.Calendar;

public final class AboutDialog extends Dialog {

    private static final int START_YEAR = 2011;

    private final Shell shell;

    public AboutDialog(Shell parent) {
        super(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
        shell = new Shell(getParent(), getStyle());
        shell.setText("О программе");
        shell.setImage(getParent().getImage());
        createContents();
    }

    public void open() {
        shell.pack();
        shell.setLocation(center());
        shell.open();
    }

    private void createContents() {
        shell.setLayout(new GridLayout(1, true));
        Label iLabel = new Label(shell, SWT.CENTER);
        iLabel.setImage(new Image(null, AboutDialog.class.getResourceAsStream("/images/about_main.png")));
        iLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        Label title = new Label(shell, SWT.CENTER);
        title.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        title.setText("JBar v. 0.9");
        title.setFont(new Font(getParent().getDisplay(), "Sans", 16, SWT.BOLD));

        Label description = new Label(shell, SWT.CENTER);
        description.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        description.setText("Программа для печати поверительных клейм");

        Label copy = new Label(shell, SWT.CENTER);
        copy.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        copy.setText(String.format("© Бочков С.А., %s", copyStr()));

        Link link = new Link(shell, SWT.CENTER);
        link.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        link.setText(" <a>bochkov.sa@gmail.com</a> ");
        link.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent selectionEvent) {
                Program.launch(String.format("mailto:%s?subject=JBar", selectionEvent.text));
            }
        });

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
