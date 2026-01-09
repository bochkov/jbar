package com.sergeybochkov.jbar;

import com.sergeybochkov.jbar.model.Shield;
import com.sergeybochkov.jbar.model.ShieldTableModel;
import com.sergeybochkov.jbar.service.Template;
import com.sergeybochkov.jbar.widgets.*;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.*;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
public final class ShlMain {

    private final Shell self;

    private final Text department;
    private final Text count;
    private final Combo month;
    private final Combo year;
    private final Combo verifier;
    private final STableModel<Shield> model;
    private final SConstLabel status;

    private final Set<String> verifiers = new TreeSet<>();
    private final AtomicReference<Template> selectedTemplate = new AtomicReference<>();

    private final Button quickBtn;
    private final Button planBtn;
    private final Button genBtn;

    public ShlMain(Display display) {
        this.self = new Shell(display, SWT.MIN | SWT.CLOSE);
        this.self.setText(AppProps.TITLE);
        this.self.setImage(new Image(display, Application.class.getResourceAsStream("/images/barcode.png")));
        this.self.setLayout(new GridLayout(2, false));

        // MENU
        Menu menu = new Menu(self, SWT.BAR);
        self.setMenuBar(menu);

        Menu fileMenu = new SMenu(menu, "Файл").menu();
        new SMenuItem(fileMenu, "Выход", e -> self.close());

        Menu editMenu = new SMenu(menu, "Правка").menu();
        Menu templateMenu = new SMenu(editMenu, "Шаблон").menu();
        for (Template template : AppProps.getInstance().templates()) {
            new SMenuItem(templateMenu, SWT.RADIO,
                    template.description(),
                    e -> this.selectedTemplate.set(template)
            ).checked(template.index() == 0);
        }

        new MenuItem(editMenu, SWT.SEPARATOR);
        new SMenuItem(editMenu, "Удалить выбранные", e -> removeSelected());
        new SMenuItem(editMenu, "Очистить", e -> removeAll());

        Menu helpMenu = new SMenu(menu, "Помощь").menu();
        new SMenuItem(helpMenu, "О программе", e -> new DlgAbout(self).open());

        // WIDGETS
        SourceValidation sourceValidation = new SourceValidation();
        Group group = new SGroup(self, new GridLayout(2, false), "Исходные данные")
                .hFill().span(2).widget();

        new SLabel(group, "Подразделение").right();
        department = new SText(group).hFill().widget();
        department.addModifyListener(sourceValidation);

        new SLabel(group, "Количество").right();
        count = new SText(group).hFill().widget();
        count.setTextLimit(2);
        count.addModifyListener(sourceValidation);

        new SLabel(group, "Месяц").right();
        month = new SCombo(group, true)
                .hFill()
                .items("Январь", "Февраль", "Март", "Апрель", "Май", "Июнь",
                        "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь")
                .select(LocalDate.now().getMonth().ordinal())
                .widget();
        month.addModifyListener(sourceValidation);

        new SLabel(group, "Год").right();
        year = new SCombo(group, false)
                .hFill()
                .items(yearItems())
                .set(String.valueOf(LocalDate.now().getYear() + 1))
                .widget();
        year.addModifyListener(sourceValidation);

        new SLabel(group, "Поверитель").right();
        verifiers.addAll(AppProps.getInstance().verifiers());
        verifier = new SCombo(group, false)
                .hFill()
                .items(verifiers).widget();
        verifier.addModifyListener(sourceValidation);

        Composite composite = new SComposite(group, new GridLayout(2, false))
                .span(2).right()
                .widget();
        planBtn = new SButton(composite, "В список", this::add)
                .widget();
        quickBtn = new SButton(composite, "Быстрая печать",
                e -> generate(Collections.singletonList(shield()))
        ).widget();

        TableItemListener tableItemListener = new TableItemListener();
        Table table = new STable(self,
                SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION,
                "Подразделение", "Дата след. поверки", "Поверитель", "Количество"
        ).hFill().vFill().span(2).widget();
        model = new ShieldTableModel(table);
        model.addItemListener(tableItemListener);

        new SButton(self, "Очистить", this::defaults)
                .left();
        genBtn = new SButton(self, "Сгенерировать",
                e -> generate(model.items())
        ).right().widget();

        status = new SConstLabel(self, "Всего наклеек:")
                .left().hFill().span(2);
        status.setText("0");

        sourceValidation.modifyText(null);
        tableItemListener.itemsChanged(null);

        self.pack();
        self.setSize(450, 600);
    }

    public List<String> yearItems() {
        List<String> items = new ArrayList<>();
        int curYear = LocalDate.now().getYear();
        for (int i = curYear - 2; i <= curYear + 3; ++i)
            items.add(String.format("%s", i));
        return items;
    }

    private Shield shield() {
        verifiers.add(verifier.getText());
        AppProps.getInstance().set(AppProps.VERIFIERS, verifiers);
        LocalDate date = LocalDate.of(
                Integer.parseInt(year.getText()),
                month.getSelectionIndex() + 1,
                1
        );
        return new Shield(
                date,
                verifier.getText(),
                department.getText(),
                Integer.parseInt(count.getText())
        );
    }

    private void add(SelectionEvent e) {
        try {
            Shield shield = shield();
            model.add(shield);
            status.setText(String.valueOf(model.count()));
        } catch (Exception ex) {
            new SMessageBox(self, SWT.ICON_ERROR).message(ex);
        }
    }

    public void removeSelected() {
        model.removeSelected();
        status.setText(String.valueOf(model.count()));
    }

    public void removeAll() {
        model.clear();
        status.setText(String.valueOf(model.count()));
    }

    public void defaults(SelectionEvent e) {
        department.setText("");
        count.setText("");
        month.select(LocalDate.now().getMonth().ordinal());
        year.setText(String.valueOf(LocalDate.now().getYear()));
        verifier.setText("");
        model.clear();
        status.setText(String.valueOf(model.count()));
    }

    public void generate(List<Shield> shields) {
        Display.getCurrent().syncExec(() -> {
            try {
                if (selectedTemplate.get() == null)
                    throw new IOException("Шаблон не выбран");
                if (shields.isEmpty())
                    throw new IOException("Список не заполнен");

                File outFile = AppProps.OUT_FILE;
                selectedTemplate.get()
                        .generate(shields)
                        .toFile(outFile);
                Program.launch(outFile.getAbsolutePath());
            } catch (Exception ex) {
                LOG.warn(ex.getMessage(), ex);
                new SMessageBox(self, SWT.ICON_ERROR, "Ошибка").message(ex);
            }
        });
    }

    public Shell open() {
        Rectangle bounds = self.getDisplay().getBounds();
        Point dialogSize = self.getSize();
        Point center = new Point(
                bounds.x + (bounds.width - dialogSize.x) / 2,
                bounds.y + (bounds.height - dialogSize.y) / 2
        );
        self.setLocation(center);
        self.open();
        return self;
    }

    private final class SourceValidation implements ModifyListener {
        @Override
        public void modifyText(ModifyEvent e) {
            boolean isInputValid = !department.getText().isEmpty() &&
                    !count.getText().isEmpty() && count.getText().matches("\\d+") &&
                    (verifier.getSelectionIndex() > 0 || !verifier.getText().isEmpty()) &&
                    month.getSelectionIndex() >= 0 &&
                    (year.getSelectionIndex() >= 0 || !year.getText().isEmpty()) && year.getText().matches("\\d{4}");
            quickBtn.setEnabled(isInputValid);
            planBtn.setEnabled(isInputValid);
        }
    }

    private final class TableItemListener implements ItemListener {
        @Override
        public void itemsChanged(ItemsEvent ev) {
            genBtn.setEnabled(ev != null && ev.total() > 0);
        }
    }
}
