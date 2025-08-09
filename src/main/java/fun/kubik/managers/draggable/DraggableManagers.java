/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.managers.draggable;

import fun.kubik.helpers.interfaces.IFinderModules;
import fun.kubik.helpers.interfaces.IManager;
import fun.kubik.helpers.module.interfaces.ArmorHud;
import fun.kubik.helpers.module.interfaces.Information;
import fun.kubik.helpers.module.interfaces.KeyBinds;
import fun.kubik.helpers.module.interfaces.NearList;
import fun.kubik.helpers.module.interfaces.PotionList;
import fun.kubik.helpers.module.interfaces.StaffList;
import fun.kubik.helpers.module.interfaces.TargetHud;
import fun.kubik.helpers.module.interfaces.WaterMark;
import fun.kubik.managers.draggable.api.Component;
import java.util.ArrayList;

public class DraggableManagers
extends ArrayList<Component>
implements IManager<Component>,
IFinderModules<Component> {
    public DraggableManagers() {
        this.init();
    }

    @Override
    public void init() {
        this.register(new PotionList());
        this.register(new NearList());
        this.register(new KeyBinds());
        this.register(new StaffList());
        this.register(new WaterMark());
        this.register(new Information());
        this.register(new ArmorHud());
        this.register(new TargetHud());
    }

    @Override
    public void register(Component component) {
        this.add(component);
    }

    @Override
    public <T extends Component> T findName(String name) {
        return (T)((Component)this.stream().filter(module -> module.getName().equalsIgnoreCase(name)).findAny().orElse(null));
    }

    @Override
    public <T extends Component> T findClass(Class<T> clazz) {
        return (T)((Component)this.stream().filter(module -> module.getClass() == clazz).findAny().orElse(null));
    }
}

