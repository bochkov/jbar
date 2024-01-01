package com.sergeybochkov.jbar;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.sergeybochkov.jbar.service.Template;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

@Slf4j
public final class AppProps extends Properties {

    public static final String VERIFIERS = "app.verifiers";

    public static final String TITLE = "JBar";

    public static final File APP_DIR = new File(System.getProperty("user.home"), ".config/jbar");
    public static final File TMPL_DIR = new File(APP_DIR, "templates");
    public static final File OUT_FILE = new File(APP_DIR, "main.svg");
    public static final File INI_FILE = new File(APP_DIR, "jbar.ini");

    private static AppProps instance;

    private final transient List<Template> templates = new ArrayList<>();

    private String[] templateNames() throws IOException {
        URL url = Application.class.getResource("/templates");
        if (url == null)
            throw new IOException("Cannot find template folder in resources");
        File resourceDir = new File(url.getFile());
        String[] files = resourceDir.list();
        if (files == null)
            throw new IOException("No files in resources dir");
        return files;
    }

    private void createProgramFolder() throws IOException {
        if (!APP_DIR.exists() && !APP_DIR.mkdirs()) {
            throw new IOException("Папка программы отсутствует и не может быть создана");
        }
        if (!TMPL_DIR.exists() && !TMPL_DIR.mkdirs()) {
            throw new IOException("Папка шаблонов отстутствует и не может быть создана");
        }
        for (String fn : templateNames()) {
            URL res = Application.class.getResource(String.format("/templates/%s", fn));
            if (res != null) {
                IOUtils.copy(res, new File(TMPL_DIR, fn));
            }
        }
    }

    private void fillTemplates() {
        if (TMPL_DIR.exists()) {
            File[] children = TMPL_DIR.listFiles(file ->
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
                templates.sort(Comparator.comparing(Template::description));
            }
        }
    }

    public void setup() throws IOException {
        createProgramFolder();
        fillTemplates();
    }

    public void load() {
        try (FileReader reader = new FileReader(INI_FILE, StandardCharsets.UTF_8)) {
            load(reader);
            LOG.info("props loaded");
        } catch (IOException ex) {
            LOG.warn(ex.getMessage(), ex);
        }
    }

    public void save() {
        try (FileWriter writer = new FileWriter(INI_FILE, StandardCharsets.UTF_8)) {
            store(writer, "jbar properties");
            LOG.info("props saved");
        } catch (IOException ex) {
            LOG.warn(ex.getMessage(), ex);
        }
    }

    public static AppProps getInstance() {
        if (instance == null) {
            instance = new AppProps();
        }
        return instance;
    }

    public static String get(String key, String def) {
        return instance.getProperty(key, def);
    }

    public <T> void set(String key, Collection<T> verifiers) {
        set(key, verifiers, Object::toString);
    }

    public <T> void set(String key, Collection<T> verifiers, Function<? super T, String> mapper) {
        setProperty(key, verifiers.stream().map(mapper).collect(Collectors.joining(";")));
    }

    public List<String> verifiers() {
        String[] verifiers = AppProps.get(AppProps.VERIFIERS, "").split(";");
        return Arrays.asList(verifiers);
    }

    public List<Template> templates() {
        return templates;
    }
}
