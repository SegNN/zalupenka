/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.managers.config.main;

import fun.kubik.Load;
import fun.kubik.managers.config.api.Config;
import fun.kubik.ui.alt.Account;

import java.io.IOException;

public class AltConfig
extends Config {
    public AltConfig() {
        super("alt", "fun/kubik/client");
    }

    @Override
    protected void save() {
        try {
            this.write(Load.getInstance().getAltScreen().get().toString().replace("[", "").replace("]", ""));
        } catch (IOException e) {
            e.printStackTrace(); // Логирование ошибки
            // Можно добавить пользовательскую обработку, например, уведомление
        }
    }

    @Override
    protected void load() {
        try {
            String info = this.read();
            String[] accounts = info.split(", ");
            Load.getInstance().getAltScreen().getAccounts().clear();
            if (!info.isEmpty()) {
                for (String s : accounts) {
                    Load.getInstance().getAltScreen().getAccounts().add(new Account(s));
                }
            }
        } catch (IOException e) {
            e.printStackTrace(); // Логирование ошибки
            // Можно добавить пользовательскую обработку
        }
    }
}

