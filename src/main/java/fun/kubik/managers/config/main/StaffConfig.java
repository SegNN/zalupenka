/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.managers.config.main;

import fun.kubik.Load;
import fun.kubik.managers.config.api.Config;

import java.io.IOException;

public class StaffConfig
extends Config {
    public StaffConfig() {
        super("staff", "fun/kubik/client");
    }

    @Override
    protected void save() throws IOException {
        this.write(Load.getInstance().getHooks().getStaffManagers().get().toString().replace("[", "").replace("]", ""));
    }

    @Override
    protected void load() throws IOException {
        String info = this.read();
        String[] staffs = info.split(", ");
        Load.getInstance().getHooks().getStaffManagers().clear();
        for (String s : staffs) {
            Load.getInstance().getHooks().getStaffManagers().add(s);
        }
    }
}

