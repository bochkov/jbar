package com.sergeybochkov.jbar;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/** Приложение */
public class Application {
    /** Конструктор */
    public Application(){
        collection = new ShieldCollection();

        // чтение информации о поверителях
        verifs = new ArrayList<>();
        try {
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(new FileInputStream("jbar.ini"), "UTF-8"));
            String line;
            while ((line = in.readLine()) != null)
                verifs.add(line);
        }
        catch (IOException e) { e.printStackTrace(); }

        Display display = new Display();
        Shell shell = new Shell(display, SWT.MIN | SWT.CLOSE);

        shell.setText("JBar");
		Image image = new Image(shell.getDisplay(),
                this.getClass().getResourceAsStream("/images/main.png"));
		shell.setImage(image);

        createContents(shell);
        createMenu(shell);
        shell.pack();
        shell.setSize(450, 600);

        centerShell(shell);
        shell.open();

        while (!shell.isDisposed())
            if (!display.readAndDispatch())
                display.sleep();

        // после завершения работы приложения
        // запись в файл сведений о поверителях
        if (verifsChanged)
            try {
                BufferedWriter out = new BufferedWriter(
                        new OutputStreamWriter(new FileOutputStream("jbar.ini"), "UTF-8"));
                for (String name : verifs)
                    if (!name.equals(""))
                        out.write(name + '\n');
                out.close();
            }
            catch (IOException e) { e.printStackTrace(); }

        display.dispose();
    }

    /**
     * создание элементов окна
     * @param shell окно
     */
    private void createContents(final Shell shell){
        shell.setLayout(new GridLayout(2, false));

        Group group = new Group(shell, SWT.SHADOW_ETCHED_IN);
        group.setText("Исходные данные");
        group.setLayout(new GridLayout(2, true));
        GridData data = new GridData(GridData.FILL_HORIZONTAL);
        data.horizontalSpan = 2;
        group.setLayoutData(data);

        new Label(group, SWT.NONE).setText("Подразделение");
        depText = new Text(group, SWT.BORDER);
        depText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        new Label(group, SWT.NONE).setText("Количество");
        numText = new Text(group, SWT.BORDER);
        numText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        new Label(group, SWT.NONE).setText("Месяц");
        monthCombo = new Combo(group, SWT.DROP_DOWN | SWT.READ_ONLY);
        monthCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        new Label(group, SWT.NONE).setText("Год");
        yearCombo = new Combo(group, SWT.DROP_DOWN);
        yearCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        new Label(group, SWT.NONE).setText("Поверитель");
        verCombo = new Combo(group, SWT.DROP_DOWN);
        verCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        refreshSorces();

        Button addButton = new Button(group, SWT.NONE);
        addButton.setText("Добавить");
        addButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent selectionEvent) {
                Shield shield = null;
                try {
                    String name = verCombo.getText();
                    if (name.equals(""))
                        throw new NumberFormatException();
                    if (!verifs.contains(name)) {
                        verifs.add(name);
                        verifsChanged = true;
                    }
                    int month = monthCombo.getSelectionIndex();
                    int year = Integer.parseInt(yearCombo.getText());
                    int code = Integer.parseInt(depText.getText());
                    int num = Integer.parseInt(numText.getText());
                    shield = new Shield(name, month, year, code, num);
                }
                catch (NumberFormatException e) {
                    showError("Видимо введены не все данные");
                }
                if (shield != null){
                    collection.add(shield);
                    refreshSorces();
                    refreshView();
                }
            }
        });
        shell.setDefaultButton(addButton);

        table = new Table(shell, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION);
        for (String field : FIELDS)
            new TableColumn(table, SWT.RIGHT).setText(field);
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        GridData tableData = new GridData(GridData.FILL_BOTH);
        tableData.horizontalSpan = 2;
        table.setLayoutData(tableData);
        table.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent keyEvent) {
                if (keyEvent.keyCode == SWT.DEL){
                    removeSelected();
                }
            }
        });

        Menu menu = new Menu(shell, SWT.POP_UP);
        table.setMenu(menu);
        MenuItem item = new MenuItem(menu, SWT.PUSH);
        item.setText("Удалить выбранные");
        item.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent selectionEvent) {
                removeSelected();
            }
        });

        Button clear = new Button(shell, SWT.NONE);
        clear.setText("Очистить");
        clear.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent selectionEvent) {
                clear();
            }
        });

        generate = new Button(shell, SWT.NONE);
        generate.setText("Сгенерировать");
        generate.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent selectionEvent) {
                ImageMaker maker = new ImageMaker(collection, template);
                Display.getCurrent().syncExec(maker);
                Program.launch(maker.generateFile().getName());
            }
        });

        status = new Label(shell, SWT.NONE);
        status.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        refreshView();
    }

    /**
     * Создание меню
     * @param shell окно
     */
    private void createMenu(final Shell shell){
        Menu menu = new Menu(shell, SWT.BAR);

        MenuItem fileItem = new MenuItem(menu, SWT.CASCADE);
        fileItem.setText("Файл");
        Menu fileMenu = new Menu(menu);
        fileItem.setMenu(fileMenu);

        MenuItem exitItem = new MenuItem(fileMenu, SWT.NONE);
        exitItem.setText("Выход\tAlt+F4");
        exitItem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent selectionEvent) {
                shell.close();
            }
        });

        MenuItem editItem = new MenuItem(menu, SWT.CASCADE);
        editItem.setText("Правка");
        Menu editMenu = new Menu(menu);
        editItem.setMenu(editMenu);

        MenuItem templateItem = new MenuItem(editMenu, SWT.CASCADE);
        templateItem.setText("Шаблон");
        Menu templateMenu = new Menu(menu);
        templateItem.setMenu(templateMenu);

        File dir = new File("templates");
        if (dir.exists()){
            File[] children = dir.listFiles(new FileFilter() {
                public boolean accept(File file) {
                    return !file.isDirectory() && file.getName().toLowerCase().endsWith(".xml");
                }
            });

            for (final File file : children){
                boolean first = templateMenu.getItemCount() == 0;
                MenuItem item = new MenuItem(templateMenu, SWT.RADIO);
                String name = Parser.getDesc(file);
                item.setText(name == null ? file.getName() : name);
                if (first) {
                    item.setSelection(true);
                    setTemplate(file);
                }
                item.addSelectionListener(new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent selectionEvent) {
                        setTemplate(file);
                    }
                });
            }
        }

        new MenuItem(editMenu, SWT.SEPARATOR);

        MenuItem removeSelItem = new MenuItem(editMenu, SWT.NONE);
        removeSelItem.setText("Удалить выбранные");
        removeSelItem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent selectionEvent) {
                removeSelected();
            }
        });

        MenuItem clearItem = new MenuItem(editMenu, SWT.NONE);
        clearItem.setText("Очистить");
        clearItem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent selectionEvent) {
                clear();
            }
        });

        MenuItem helpItem = new MenuItem(menu, SWT.CASCADE);
        helpItem.setText("Помощь");
        Menu helpMenu = new Menu(menu);
        helpItem.setMenu(helpMenu);

        MenuItem aboutItem = new MenuItem(helpMenu, SWT.NONE);
        aboutItem.setText("О программе");
        aboutItem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent selectionEvent) {
                AboutDialog dialog = new AboutDialog(shell);
                dialog.open();
            }
        });

        shell.setMenuBar(menu);
    }

    /**
     * Обновляет исходные данные
     */
    private void refreshSorces(){
        depText.setText("");
        numText.setText("");

		int index = verCombo.getSelectionIndex();
        verCombo.removeAll();
        for (String name : verifs) verCombo.add(name);
        if (index == -1) verCombo.select(0);
		else verCombo.select(index);

        monthCombo.setItems(MONTHS);
        monthCombo.select(new GregorianCalendar().get(Calendar.MONTH));
        int year = new GregorianCalendar().get(Calendar.YEAR);
        for (int i = year - 2; i < year + 5; ++i)
            yearCombo.add(String.valueOf(i));
        yearCombo.select(yearCombo.indexOf(String.valueOf(year + 1)));

        depText.setFocus();
    }

    /**
     * Обновляет содержимое таблицы
     */
    private void refreshView(){
        table.removeAll();
        for (Object obj : collection.getShields()){
            Shield shield = (Shield)obj;
            TableItem item = new TableItem(table, SWT.NONE);
            item.setText(0, shield.getDep());
            item.setText(1, shield.getDateNum());
            item.setText(2, shield.getName());
            item.setText(3, String.valueOf(shield.getNum()));
        }

        for (TableColumn col : table.getColumns())
            col.pack();

        status.setText("Всего: " + collection.getCollSize());
        generate.setEnabled(table.getItemCount() > 0);
    }

    /**
     * Очистка окна
     */
    public void clear(){
        collection.clear();
        refreshSorces();
        refreshView();
    }

    /**
     * Удаляет выделенные строки из таблицы и из коллекции
     */
    private void removeSelected(){
        collection.remove(table.getSelectionIndices());
        refreshView();
    }

    /**
     * Устанавливает файл шаблона
     * @param template шаблон
     */
    private void setTemplate(File template){
        this.template = template;
    }

    /**
     * Показывает сообщение об ошибке
     * @param text текст сообщения
     */
    public static void showError(String text){
        MessageBox msg = new MessageBox(Display.getCurrent().getActiveShell(), SWT.ERROR);
        msg.setText("Ошибка");
        msg.setMessage(text);
        msg.open();
    }

    /**
     * Перемещает окно в центр родительского окна
     * @param shell окно
     */
    public static void centerShell(Shell shell){
        Rectangle bounds;
        if (shell.getParent() == null)
            bounds = shell.getDisplay().getBounds();
        else
            bounds = shell.getParent().getBounds();

		Point dialogSize = shell.getSize();
		int x = bounds.x + (bounds.width - dialogSize.x) / 2;
		int y = bounds.y + (bounds.height - dialogSize.y) / 2;
		shell.setLocation(x, y);
    }

    private static final String[] FIELDS = { "Подразделение", "Дата след. поверки",
            "Поверитель", "Количество" };
    public static final String[] MONTHS = { "Январь", "Февраль", "Март", "Апрель",
        "Май", "Июнь", "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь" };
    public static final String[] MONTHS_TO = { "Января", "Февраля", "Марта", "Апреля",
        "Мая", "Июня", "Июля", "Августа", "Сентября", "Октября", "Ноября", "Декабря" };

    private File template;

    private Text depText;
    private Text numText;
    private Combo verCombo;
    private Combo monthCombo;
    private Combo yearCombo;
    private Table table;
    private Label status;

    private Button generate;

    private List<String> verifs;
    boolean verifsChanged = false;
    private ShieldCollection collection;
}
