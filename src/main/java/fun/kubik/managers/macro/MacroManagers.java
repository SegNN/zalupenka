package fun.kubik.managers.macro;

import fun.kubik.Load;
import fun.kubik.helpers.interfaces.IFinderModules;
import fun.kubik.helpers.interfaces.IManager;
import fun.kubik.managers.config.main.MacroConfig;
import fun.kubik.managers.macro.api.Macro;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;

public class MacroManagers extends ArrayList<Macro> implements IManager<Macro>, IFinderModules<Macro> {
    public void press(int key) {
        try {
            this.stream()
                    .filter(macro -> macro.getKey() == key)
                    .forEach(macro -> Minecraft.getInstance().player.sendChatMessage(macro.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void add(String message, int key) {
        this.register(new Macro(message, key));
        MacroConfig config = Load.getInstance().getHooks().getConfigManagers().findClass(MacroConfig.class);
        config.fastSave();
    }

    public void delete(int key) {
        this.removeIf(macro -> macro.getKey() == key);
        MacroConfig config = Load.getInstance().getHooks().getConfigManagers().findClass(MacroConfig.class);
        config.fastSave();
    }

    public void clears() {
        this.clear();
        MacroConfig config = Load.getInstance().getHooks().getConfigManagers().findClass(MacroConfig.class);
        config.fastSave();
    }

    @Override
    public <T extends Macro> T findName(String name) {
        return this.stream()
                .filter(macro -> macro.getMessage().equalsIgnoreCase(name))
                .findAny()
                .map(macro -> (T) macro)
                .orElse(null);
    }

    @Override
    public <T extends Macro> T findClass(Class<T> clazz) {
        return this.stream()
                .filter(macro -> macro.getClass() == clazz)
                .findAny()
                .map(macro -> (T) macro)
                .orElse(null);
    }

    @Override
    public void register(Macro macro) {
        this.add(macro);
    }
}