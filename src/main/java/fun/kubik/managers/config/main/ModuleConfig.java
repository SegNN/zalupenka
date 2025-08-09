/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.managers.config.main;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fun.kubik.Load;
import fun.kubik.managers.config.api.Config;
import fun.kubik.managers.module.Module;
import fun.kubik.managers.theme.api.Theme;

import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

public class ModuleConfig
extends Config {
    public ModuleConfig() {
        super("module", "fun/kubik/configs");
    }

    @Override
    protected void load() throws IOException {
        JsonParser parser = new JsonParser();
        JsonObject jsonObject = (JsonObject)parser.parse(this.read());
        if (jsonObject.has("Theme")) {
            JsonObject themesObject = jsonObject.getAsJsonObject("Theme");
            for (Theme theme : Load.getInstance().getHooks().getThemeManagers().themes) {
                JsonObject themeObject = themesObject.getAsJsonObject(theme.name);
                String colors = themeObject.get("color").getAsString();
                boolean selected = themeObject.get("selected").getAsBoolean();
                if (selected) {
                    Load.getInstance().getHooks().getThemeManagers().setCurrentTheme(theme);
                }
                String[] colorArray = colors.split(", ");
                theme.colors = Arrays.stream(colorArray).mapToInt(Integer::parseInt).toArray();
            }
        }
        if (jsonObject.has("Modules")) {
            JsonObject modulesObject = jsonObject.getAsJsonObject("Modules");
            for (Module module : Load.getInstance().getHooks().getModuleManagers()) {
                if (module.isToggled()) {
                    module.toggle();
                }
                module.load(modulesObject.getAsJsonObject(module.getName()));
            }
        }
    }

    @Override
    protected void save() throws IOException {
        JsonObject modulesObject = new JsonObject();
        JsonObject themesObject = new JsonObject();
        JsonObject object = new JsonObject();
        for (Theme theme : Load.getInstance().getHooks().getThemeManagers().themes) {
            String colors = Arrays.stream(theme.colors).mapToObj(String::valueOf).collect(Collectors.joining(", "));
            JsonObject themeObject = new JsonObject();
            themeObject.addProperty("color", colors);
            themeObject.addProperty("selected", Load.getInstance().getHooks().getThemeManagers().getCurrentTheme() == theme);
            themesObject.add(theme.name, themeObject);
        }
        object.add("Theme", themesObject);
        for (Module module : Load.getInstance().getHooks().getModuleManagers()) {
            modulesObject.add(module.getName(), module.save());
        }
        object.add("Modules", modulesObject);
        String contentPrettyPrint = new GsonBuilder().setPrettyPrinting().create().toJson(object);
        this.write(contentPrettyPrint);
    }
}

