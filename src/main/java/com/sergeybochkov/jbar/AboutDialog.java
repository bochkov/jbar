package com.sergeybochkov.jbar;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import java.util.Calendar;
import java.util.GregorianCalendar;

/** Диалоговое окно "О программе" */
public class AboutDialog extends Dialog {
    /**
     * Конструктор диалогового окна
     * @param shell родитель
     */
    public AboutDialog(Shell shell){
        super(shell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
        image = new Image(null, this.getClass().getResourceAsStream("/images/about_main.png"));
    }

    /** Отображение окна */
    public void open(){
        Shell shell = new Shell(getParent(), getStyle());
        shell.setText("О программе");
        shell.setImage(getParent().getImage());
        createContents(shell);
        shell.pack();

        Application.centerShell(shell);
        shell.open();
    }

    /**
     * Создание элементов диалогового окна
     * @param shell shell
     */
    private void createContents(final Shell shell){
        shell.setLayout(new GridLayout(1, true));
        if (image != null) {
            Label iLabel = new Label(shell, SWT.CENTER);
            iLabel.setImage(image);
            iLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        }

        Label title = new Label(shell, SWT.CENTER);
        title.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        title.setText("\nJBar v. 0.9");
        title.setFont(new Font(getParent().getDisplay(), "Sans", 9, SWT.BOLD));

        new Label(shell, SWT.NONE).setText("Программа для печати поверительных клейм");
        int year = new GregorianCalendar().get(Calendar.YEAR);
        String str = "2011";
        if (year > 2011)
            str += ("-" + year);
        Label copy = new Label(shell, SWT.CENTER);
        copy.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        copy.setText("© Бочков Сергей Александрович, " + str + "\n");

        new Label(shell, SWT.NONE).setText("\nИспользованы:\nSWT\n" +
                "barbecue\niHarder Base64\n\n");

        Button ok = new Button(shell, SWT.PUSH);
        ok.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER));
        ok.setText("Закрыть");
        ok.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent selectionEvent) {
                shell.close();
            }
        });
    }

    private Image image;
}
