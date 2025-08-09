/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.ui.option.list;

import lombok.Generated;

public enum Type {
    THEME("Theme"),
    CONFIG("Config");

    private final String name;

    private Type(String name) {
        this.name = name;
    }

    @Generated
    public String getName() {
        return this.name;
    }
}

