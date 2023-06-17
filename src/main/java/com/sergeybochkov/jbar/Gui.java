package com.sergeybochkov.jbar;

import java.util.List;
import java.util.*;

import com.sergeybochkov.jbar.widgets.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

public final class Gui {

    private static final String[] MONTHS = {"Январь", "Февраль", "Март", "Апрель", "Май", "Июнь", "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"};
    private static final String[] FIELDS = {"Подразделение", "Дата след. поверки", "Поверитель", "Количество"};

    private final Shell shell;
    private final Text department;
    private final Text count;
    private final Combo month;
    private final Combo year;
    private final Combo verifier;
    private final STable table;
    private final Label status;

    private final Set<String> verifiers = new TreeSet<>();

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
        month = new SCombo(group, true)
                .fill()
                .items(MONTHS)
                .select(Calendar.getInstance().get(Calendar.MONTH))
                .combo();

        new SLabel(group, "Год")
                .right();
        year = new SCombo(group, false)
                .fill()
                .items(yearItems())
                .set(String.format("%s", Calendar.getInstance().get(Calendar.YEAR) + 1))
                .combo();

        new SLabel(group, "Поверитель")
                .right();
        verifier = new SCombo(group, false)
                .fill()
                .combo();

        Composite composite = new SComposite(group)
                .layout(new GridLayout(2, false))
                .span(2)
                .right()
                .composite();
        new SButton(composite, "В список", this::add);
        new SButton(composite, "Быстрая печать", () -> {
            try {
                target.generate(Collections.singletonList(shield()));
            } catch (Exception ex) {
                new SMessageBox(shell, SWT.ICON_ERROR)
                        .title("Ошибка")
                        .message(ex)
                        .open();
            }
        });

        table = new STable(shell, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION, FIELDS)
                .fill()
                .span(2);

        new SButton(shell, "Очистить", this::defaults)
                .left();
        new SButton(shell, "Сгенерировать", () -> target.generate(table.items()))
                .right();

        status = new SLabel(shell, "Всего наклеек: 0")
                .left()
                .fill()
                .span(2)
                .label();

        shell.pack();
        shell.setSize(450, 600);
    }

    public List<String> yearItems() {
        List<String> items = new ArrayList<>();
        int curYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = curYear - 5; i <= curYear + 5; ++i)
            items.add(String.format("%s", i));
        return items;
    }

    private Shield shield() throws NumberFormatException {
        return new Shield(
                verifier.getText(),
                month.getSelectionIndex(),
                Integer.parseInt(year.getText()),
                department.getText(),
                Integer.parseInt(count.getText())
        );
    }

    private void add() {
        try {
            Shield shield = shield();
            table.add(shield);
            verifiers.add(verifier.getText());
            status.setText(String.format("Всего наклеек: %s", table.shieldsCount()));
        } catch (Exception ex) {
            new SMessageBox(shell, SWT.ICON_ERROR)
                    .title("Ошибка")
                    .message(ex)
                    .open();
        }
    }

    public void removeSelected() {
        table.removeSelected();
        status.setText(String.format("Всего наклеек: %s", table.shieldsCount()));
    }

    public void removeAll() {
        table.removeAll();
        status.setText(String.format("Всего наклеек: %s", table.shieldsCount()));
    }

    public void updateVerifiers(List<String> verifiers) {
        this.verifiers.addAll(verifiers);
        this.verifier.setItems(this.verifiers.toArray(String[]::new));
    }

    public Set<String> verifiers() {
        return verifiers;
    }

    public void defaults() {
        department.setText("");
        count.setText("");
        month.select(Calendar.getInstance().get(Calendar.MONTH));
        year.setText(String.format("%s", Calendar.getInstance().get(Calendar.YEAR)));
        verifier.setText("");
        table.removeAll();
        status.setText("Всего наклеек: 0");
    }
}
