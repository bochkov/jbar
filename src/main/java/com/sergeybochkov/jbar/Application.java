package com.sergeybochkov.jbar;

import com.sergeybochkov.jbar.widgets.SMenu;
import com.sergeybochkov.jbar.widgets.SMenuItem;
import com.sergeybochkov.jbar.widgets.SMessageBox;
import org.apache.commons.io.IOUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public final class Application implements ShieldTarget {

    public static final File APP_DIR = new File(System.getProperty("user.home"), ".jbar");
    public static final File TEMPLATE_DIR = new File(APP_DIR, "templates");
    public static final File INI_FILE = new File(APP_DIR, "jbar.ini");
    public static final File OUT_FILE = new File(APP_DIR, "main.svg");

    private final Display display;
    private final Shell mainShell;
    private final Gui gui;
    private final AboutDialog aboutDialog;

    private final List<Template> templates = new ArrayList<>();

    private Template selectedTemplate;

    public Application() {
        display = new Display();
        mainShell = new Shell(display, SWT.MIN | SWT.CLOSE);
        mainShell.setText("JBar");
        mainShell.setImage(new Image(display, Application.class.getResourceAsStream("/images/barcode.png")));
        gui = new Gui(mainShell, this);
        aboutDialog = new AboutDialog(mainShell);
        createProgramFolder();
        try {
            fillTemplates();
        }
        catch (Exception ex) {
            new SMessageBox(mainShell, SWT.ICON_WARNING)
                    .title("Ошибка")
                    .message(ex)
                    .open();
        }
        createMenu();
    }

    private List<String> readVerificators() {
        List<String> verificators = new ArrayList<>();
        try (BufferedReader in = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream(INI_FILE), "UTF-8"))) {
            String line;
            while ((line = in.readLine()) != null)
                verificators.add(line);
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
        return verificators;
    }

    private void saveVerificators(String... verificators) {
        try (BufferedWriter out = new BufferedWriter(
                    new OutputStreamWriter(
                            new FileOutputStream(INI_FILE), "UTF-8"))) {
            Arrays.stream(verificators)
                    .filter(verificator -> !verificator.isEmpty())
                    .forEach(verificator -> {
                        try {
                            out.write(verificator + '\n');
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void createProgramFolder() {
        if (!APP_DIR.exists()) {
            try {
                if (!TEMPLATE_DIR.exists() && !TEMPLATE_DIR.mkdirs())
                    throw new IOException(String.format("Cannot create file %s", TEMPLATE_DIR.getName()));
                for (String fn : new String[]{"logo.png", "re.xml", "tt.xml"})
                    IOUtils.copy(Application.class.getResourceAsStream(String.format("/templates/%s", fn)),
                            new FileOutputStream(new File(TEMPLATE_DIR, fn)));
            }
            catch (Exception ex) {
                new SMessageBox(mainShell, SWT.ICON_WARNING)
                        .title("Ошибка копирования шаблонов")
                        .message(ex)
                        .open();
            }
        }
    }

    private void fillTemplates() throws Exception {
        if (TEMPLATE_DIR.exists()) {
            File[] children = TEMPLATE_DIR.listFiles(file ->
                    !file.isDirectory() && file.getName().toLowerCase().endsWith(".xml"));
            if (children != null) {
                for (File file : children)
                    templates.add(new Template(file));
            }
        }
    }

    public void mainLoop() {
        gui.updateVerificators(readVerificators());
        mainShell.setLocation(center());
        mainShell.open();
        while (!mainShell.isDisposed())
            if (!display.readAndDispatch())
                display.sleep();
        saveVerificators(gui.verificators());
        display.dispose();
    }

    public static void main(String[] args) {
        new Application().mainLoop();
    }

    private Point center() {
        Rectangle bounds = (mainShell.getParent() == null) ?
                mainShell.getDisplay().getBounds() :
                mainShell.getParent().getBounds();
        Point dialogSize = mainShell.getSize();
        return new Point(
                bounds.x + (bounds.width - dialogSize.x) / 2,
                bounds.y + (bounds.height - dialogSize.y) / 2);
    }

    private void createMenu() {
        Menu menu = new Menu(mainShell, SWT.BAR);
        mainShell.setMenuBar(menu);

        Menu fileMenu = new SMenu(menu, "Файл").menu();
        new SMenuItem(fileMenu, "Выход", mainShell::close);

        Menu editMenu = new SMenu(menu, "Правка").menu();
        Menu templateMenu = new SMenu(editMenu, "Шаблон").menu();
        for (Template template : templates)
            new SMenuItem(templateMenu, SWT.RADIO, template.description(), () -> this.selectedTemplate = template)
                    .checked(templates.indexOf(template) == 0);
        new MenuItem(editMenu, SWT.SEPARATOR);
        new SMenuItem(editMenu, "Удалить выбранные", gui::removeSelected);
        new SMenuItem(editMenu, "Очистить", gui::removeAll);

        Menu helpMenu = new SMenu(menu, "Помощь").menu();
        new SMenuItem(helpMenu, "О программе", aboutDialog::open);
    }

    @Override
    public void generate(List<Shield> shields) {
        Display.getCurrent().syncExec(() -> {
            try {
                if (!shields.isEmpty()) {
                    selectedTemplate
                            .generate(shields)
                            .toFile(OUT_FILE);
                    Program.launch(OUT_FILE.getName());
                }
                else
                    throw new ShieldException("Список не заполнен");
            }
            catch (Exception ex) {
                new SMessageBox(mainShell, SWT.ICON_ERROR)
                        .title("Ошибка")
                        .message(ex)
                        .open();
            }
        });
    }

    @Override
    public void generateNow(Shield shield) {
        Display.getCurrent().syncExec(() -> {
            try {
                selectedTemplate
                        .generate(shield)
                        .toFile(OUT_FILE);
                Program.launch(OUT_FILE.getName());
            }
            catch (Exception ex) {
                new SMessageBox(mainShell, SWT.ICON_ERROR)
                        .title("Ошибка")
                        .message(ex)
                        .open();
            }
        });
    }
}
