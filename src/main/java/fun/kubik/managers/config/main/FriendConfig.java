/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.managers.config.main;

import fun.kubik.Load;
import fun.kubik.managers.config.api.Config;

import java.io.IOException;

public class FriendConfig
extends Config {
    public FriendConfig() {
        super("friend", "fun/kubik/client");
    }

    @Override
    protected void save() throws IOException {
        this.write(Load.getInstance().getHooks().getFriendManagers().get().toString().replace("[", "").replace("]", ""));
    }

    @Override
    protected void load() throws IOException {
        String info = this.read();
        String[] friends = info.split(", ");
        Load.getInstance().getHooks().getFriendManagers().clear();
        for (String s : friends) {
            Load.getInstance().getHooks().getFriendManagers().add(s);
        }
    }
}

