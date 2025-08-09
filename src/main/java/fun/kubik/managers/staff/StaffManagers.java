/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.managers.staff;

import fun.kubik.Load;
import fun.kubik.helpers.interfaces.IFinderModules;
import fun.kubik.helpers.interfaces.IManager;
import fun.kubik.managers.config.main.StaffConfig;
import fun.kubik.managers.staff.api.Staff;
import java.util.ArrayList;
import java.util.List;

public class StaffManagers
extends ArrayList<Staff>
implements IManager<Staff>,
IFinderModules<Staff> {
    public StaffManagers() {
        this.init();
    }

    @Override
    public void init() {
    }

    public void add(String name) {
        this.register(new Staff(name));
        ((StaffConfig)Load.getInstance().getHooks().getConfigManagers().findClass(StaffConfig.class)).fastSave();
    }

    public void remove(String name) {
        this.removeIf(Staff2 -> Staff2.getName().equalsIgnoreCase(name));
        ((StaffConfig)Load.getInstance().getHooks().getConfigManagers().findClass(StaffConfig.class)).fastSave();
    }

    public boolean is(String name) {
        return this.stream().anyMatch(is -> is.getName().equals(name));
    }

    public void clears() {
        this.clear();
        ((StaffConfig)Load.getInstance().getHooks().getConfigManagers().findClass(StaffConfig.class)).fastSave();
    }

    @Deprecated(forRemoval=true, since="3.0")
    public List<String> get() {
        ArrayList<String> staffs = new ArrayList<String>();
        for (Staff staff : Load.getInstance().getHooks().getStaffManagers()) {
            String name = staff.getName();
            staffs.add(name);
        }
        return staffs;
    }

    @Override
    public <T extends Staff> T findName(String name) {
        return (T)((Staff)this.stream().filter(command -> command.getName().equals(name)).findAny().orElse(null));
    }

    @Override
    public <T extends Staff> T findClass(Class<T> clazz) {
        return (T)((Staff)this.stream().filter(command -> command.getClass() == clazz).findAny().orElse(null));
    }

    @Override
    public void register(Staff staff) {
        this.add(staff);
    }
}

