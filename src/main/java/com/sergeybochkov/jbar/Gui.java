package com.sergeybochkov.jbar;

import com.sergeybochkov.jbar.widgets.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import java.util.*;
import java.util.List;

public final class Gui {

    private static final String[] MONTHS = {"Январь", "Февраль", "Март", "Апрель",
            "Май", "Июнь", "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"};
    private static final String[] FIELDS = {"Подразделение", "Дата след. поверки",
            "Поверитель", "Количество"};

    private final Shell shell;

    private final List<String> verificatorList = new ArrayList<>();

    private final Text department;
    private final Text count;
    private final Combo months;
    private final Combo years;
    private final Combo verificators;
    private final STable table;
    private final Label status;

    public Gui(Shell shell, ShieldTarget target) {
        this.shell = shell;
        shell.setLayout(new GridLayout(2, false));
        Group group = new SGroup(shell).group();

        new SLabel(group, "Подразделение")
                .right();
        department = new SText(group)
                .fill()
                .text();

        new SLabel(group, "Количество")
                .right();
        count = new SText(group)
                .fill()
                .text();

        new SLabel(group, "Месяц")
                .right();
        months = new SCombo(group, true)
                .fill()
                .items(MONTHS)
                .select(Calendar.getInstance().get(Calendar.MONTH))
                .combo();

        new SLabel(group, "Год")
                .right();
        years = new SCombo(group, false)
                .fill()
                .items(years())
                .set(String.format("%s", Calendar.getInstance().get(Calendar.YEAR) + 1))
                .combo();

        new SLabel(group, "Поверитель")
                .right();
        verificators = new SCombo(group, false)
                .fill()
                .combo();

        Button addButton = new SButton(group, "Добавить", this::add)
                .right()
                .span(2)
                .button();
        shell.setDefaultButton(addButton);

        table = new STable(shell, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION, FIELDS)
                .fill()
                .span(2);

        new SButton(shell, "Очистить", table::removeAll)
                .left()
                .button();
        new SButton(shell, "Сгенерировать", () -> target.generate(table.items()))
                .right()
                .button();

        status = new SLabel(shell, "")
                .left()
                .fill()
                .span(2)
                .label();

        shell.pack();
        shell.setSize(450, 600);
    }

    public String[] years() {
        java.util.List<String> years = new ArrayList<>();
        int curYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = curYear - 5; i <= curYear + 5; ++i)
            years.add(String.format("%s", i));
        return years.toArray(new String[years.size()]);
    }

    private void add() {
        try {
            Shield shield = new Shield(
                    verificators.getText(),
                    months.getSelectionIndex(),
                    Integer.parseInt(years.getText()),
                    department.getText(),
                    Integer.parseInt(count.getText()));
            table.add(shield);
            if (!verificatorList.contains(verificators.getText())) {
                verificators.add(verificators.getText());
                verificatorList.add(verificators.getText());
            }
            status.setText(String.format("Всего наклеек: %s", shield.count()));
        }
        catch (Exception ex) {
            new SMessageBox(shell, SWT.ICON_ERROR)
                    .title("Ошибка")
                    .message(ex)
                    .open();
        }
    }

    public void removeSelected() {
        table.removeSelected();
    }

    public void removeAll() {
        table.removeAll();
    }

    public void updateVerificators(List<String> verificators) {
        this.verificatorList.addAll(verificators);
        this.verificators.setItems(verificators.toArray(new String[verificatorList.size()]));
    }

    public String[] verificators() {
        return verificatorList.toArray(new String[verificatorList.size()]);
    }
}
