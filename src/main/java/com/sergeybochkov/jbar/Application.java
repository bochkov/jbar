package com.sergeybochkov.jbar;

import com.sergeybochkov.jbar.widgets.SMenu;
import com.sergeybochkov.jbar.widgets.SMenuItem;
import com.sergeybochkov.jbar.widgets.SMessageBox;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public final class Application implements ShieldTarget {

    private static final String INI_FILE = "jbar.ini";

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
        mainShell.setImage(new Image(display, Application.class.getResourceAsStream("/images/about_main.png")));
        gui = new Gui(mainShell, this);
        aboutDialog = new AboutDialog(mainShell);
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

    private void fillTemplates() throws Exception {
        File dir = new File("templates");
        if (dir.exists()) {
            File[] children = dir.listFiles(file ->
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
        new SMenuItem(fileMenu, "Выход\tAlt+F4", mainShell::close);

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
                Program.launch(selectedTemplate.generate(shields).getName());
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
