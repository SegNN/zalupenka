/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.managers.macro.api;

import lombok.Generated;

public class Macro {
    private String message;
    private int key;

    public Macro(String message, int key) {
        this.message = message;
        this.key = key;
    }

    @Generated
    public String getMessage() {
        return this.message;
    }

    @Generated
    public int getKey() {
        return this.key;
    }

    @Generated
    public void setMessage(String message) {
        this.message = message;
    }

    @Generated
    public void setKey(int key) {
        this.key = key;
    }
}

