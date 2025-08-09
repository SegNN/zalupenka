/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.managers.notification.api;

import lombok.Generated;

public enum Pattern {
    ENABLE("J"),
    DISABLE("K"),
    WARN(""),
    ERROR(""),
    NONE("");

    private final String text;

    @Generated
    public String getText() {
        return this.text;
    }

    @Generated
    private Pattern(String text) {
        this.text = text;
    }
}

