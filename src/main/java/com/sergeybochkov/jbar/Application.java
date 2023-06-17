package com.sergeybochkov.jbar;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import com.sergeybochkov.jbar.widgets.SMenu;
import com.sergeybochkov.jbar.widgets.SMenuItem;
import com.sergeybochkov.jbar.widgets.SMessageBox;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
public final class Application implements ShieldTarget {

    private static final File APP_DIR = new File(System.getProperty("user.home"), ".jbar");
    private static final File TEMPLATE_DIR = new File(APP_DIR, "templates");
    private static final File INI_FILE = new File(APP_DIR, "jbar.ini");
    private static final File OUT_FILE = new File(APP_DIR, "main.svg");

    private static final String[] TEMPLATE_NAMES = new String[]{"logo.png", "re.xml", "tt.xml"};

    private final Display display;
    private final Shell mainShell;
    private final Gui gui;

    private final List<Template> templates = new ArrayList<>();
    private final AtomicReference<Template> selectedTemplate = new AtomicReference<>();

    public Application() {
        display = new Display();
        mainShell = new Shell(display, SWT.MIN | SWT.CLOSE);
        mainShell.setText("JBar");
        mainShell.setImage(new Image(display, Application.class.getResourceAsStream("/images/barcode.png")));
        gui = new Gui(mainShell, this);

        createProgramFolder();
        fillTemplates();
        createMenu();
    }

    private List<String> readVerifiers() {
        try (FileReader reader = new FileReader(INI_FILE, StandardCharsets.UTF_8)) {
            return IOUtils.readLines(reader);
        } catch (IOException ex) {
            LOG.warn(ex.getMessage(), ex);
        }
        return Collections.emptyList();
    }

    private void saveVerifiers(Collection<String> verifiers) {
        try (FileWriter writer = new FileWriter(INI_FILE, StandardCharsets.UTF_8)) {
            IOUtils.writeLines(verifiers, "\n", writer);
        } catch (IOException ex) {
            LOG.warn(ex.getMessage(), ex);
        }
    }

    private void createProgramFolder() {
        try {
            if (!APP_DIR.exists() && !APP_DIR.mkdirs()) {
                throw new IOException("Папка программы отсутствует и не может быть создана");
            }
            if (!TEMPLATE_DIR.exists() && !TEMPLATE_DIR.mkdirs()) {
                throw new IOException("Папка шаблонов отстутствует и не может быть создана");
            }
            for (String fn : TEMPLATE_NAMES) {
                URL url = Application.class.getResource(String.format("/templates/%s", fn));
                if (url != null) {
                    IOUtils.copy(url, new File(TEMPLATE_DIR, fn));
                }
            }
        } catch (Exception ex) {
            new SMessageBox(mainShell, SWT.ICON_WARNING)
                    .title("Ошибка копирования шаблонов")
                    .message(ex)
                    .open();
        }
    }

    private void fillTemplates() {
        if (TEMPLATE_DIR.exists()) {
            File[] children = TEMPLATE_DIR.listFiles(file ->
                    !file.isDirectory() && file.getName().toLowerCase().endsWith(".xml"));
            if (children != null) {
                for (File file : children) {
                    try {
                        Template tmpl = Template.fromFile(file);
                        templates.add(tmpl);
                        LOG.info("registered template file='{}', description='{}'", file.getAbsolutePath(), tmpl.description());
                    } catch (Exception ex) {
                        LOG.warn(ex.getMessage(), ex);
                    }
                }
            }
        }
    }

    private void createMenu() {
        Menu menu = new Menu(mainShell, SWT.BAR);
        mainShell.setMenuBar(menu);

        Menu fileMenu = new SMenu(menu, "Файл").menu();
        new SMenuItem(fileMenu, "Выход", mainShell::close);

        Menu editMenu = new SMenu(menu, "Правка").menu();
        Menu templateMenu = new SMenu(editMenu, "Шаблон").menu();
        for (Template template : templates)
            new SMenuItem(templateMenu, SWT.RADIO, template.description(), () -> this.selectedTemplate.set(template))
                    .checked(templates.indexOf(template) == 0);
        new MenuItem(editMenu, SWT.SEPARATOR);
        new SMenuItem(editMenu, "Удалить выбранные", gui::removeSelected);
        new SMenuItem(editMenu, "Очистить", gui::removeAll);

        Menu helpMenu = new SMenu(menu, "Помощь").menu();
        new SMenuItem(helpMenu, "О программе", () -> new AboutDialog(mainShell).open());
    }

    @Override
    public void generate(List<Shield> shields) {
        Display.getCurrent().syncExec(() -> {
            try {
                if (selectedTemplate.get() == null)
                    throw new IOException("Шаблон не выбран");
                if (shields.isEmpty())
                    throw new IOException("Список не заполнен");

                selectedTemplate.get()
                        .generate(shields)
                        .toFile(OUT_FILE);
                Program.launch(OUT_FILE.getAbsolutePath());
            } catch (Exception ex) {
                new SMessageBox(mainShell, SWT.ICON_ERROR)
                        .title("Ошибка")
                        .message(ex)
                        .open();
            }
        });
    }

    private Point center() {
        Rectangle bounds = (mainShell.getParent() == null) ?
                mainShell.getDisplay().getBounds() :
                mainShell.getParent().getBounds();
        Point dialogSize = mainShell.getSize();
        return new Point(
                bounds.x + (bounds.width - dialogSize.x) / 2,
                bounds.y + (bounds.height - dialogSize.y) / 2
        );
    }

    public void mainLoop() {
        gui.updateVerifiers(readVerifiers());
        mainShell.setLocation(center());
        mainShell.open();
        while (!mainShell.isDisposed())
            if (!display.readAndDispatch())
                display.sleep();
        saveVerifiers(gui.verifiers());
        display.dispose();
    }

    public static void main(String[] args) {
        LOG.info("Application started");
        new Application().mainLoop();
    }
}
