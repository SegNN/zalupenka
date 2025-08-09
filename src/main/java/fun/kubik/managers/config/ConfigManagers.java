/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.managers.config;

import fun.kubik.helpers.interfaces.IFinderModules;
import fun.kubik.helpers.interfaces.IManager;
import fun.kubik.managers.config.api.Config;
import fun.kubik.managers.config.main.AltConfig;
import fun.kubik.managers.config.main.ClientConfig;
import fun.kubik.managers.config.main.DraggableConfig;
import fun.kubik.managers.config.main.FriendConfig;
import fun.kubik.managers.config.main.MacroConfig;
import fun.kubik.managers.config.main.ModuleConfig;
import fun.kubik.managers.config.main.StaffConfig;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;

public class ConfigManagers
extends ArrayList<Config>
implements IManager<Config>,
IFinderModules<Config> {
    protected final String suffix = ".sk";

    public ConfigManagers() {
        this.init();
    }

    @Override
    public void init() {
        this.register(new ModuleConfig());
        this.register(new FriendConfig());
        this.register(new DraggableConfig());
        this.register(new ClientConfig());
        this.register(new MacroConfig());
        this.register(new MacroConfig());
        this.register(new StaffConfig());
        this.register(new AltConfig());
    }

    @Override
    public void register(Config config) {
        this.add(config);
    }

    public void load(String name) {
        ((ModuleConfig)this.findClass(ModuleConfig.class)).setName(name);
        this.forEach(Config::fastLoad);
    }

    public void save(String name) {
        ((ModuleConfig)this.findClass(ModuleConfig.class)).setName(name);
        this.forEach(Config::fastSave);
    }

    public void load(String name, String path) {
        ((ModuleConfig)this.findClass(ModuleConfig.class)).setName(name);
        ((ModuleConfig)this.findClass(ModuleConfig.class)).setPath(new File(path));
        this.forEach(Config::fastLoad);
    }

    public void save(String name, String path) {
        ((ModuleConfig)this.findClass(ModuleConfig.class)).setName(name);
        ((ModuleConfig)this.findClass(ModuleConfig.class)).setPath(new File(path));
        this.forEach(Config::fastSave);
    }

    @Override
    public <T extends Config> T findName(String name) {
        return (T)((Config)this.stream().filter(module -> module.getName().equalsIgnoreCase(name)).findAny().orElse(null));
    }

    @Override
    public <T extends Config> T findClass(Class<T> clazz) {
        return (T)((Config)this.stream().filter(module -> module.getClass() == clazz).findAny().orElse(null));
    }

    @Deprecated(forRemoval=true, since="3.0")
    public List<String> getConfigs() {
        File path = new File(Minecraft.getInstance().gameDir, "fun/kubik/configs/custom");
        File[] arrFiles = path.listFiles();
        assert (arrFiles != null);
        ArrayList<String> arrStrings = new ArrayList<String>();
        for (File file : arrFiles) {
            if (!file.getName().endsWith(".sk")) continue;
            String name = file.getName();
            arrStrings.add(name.substring(0, name.length() - 3));
        }
        return arrStrings;
    }
}

