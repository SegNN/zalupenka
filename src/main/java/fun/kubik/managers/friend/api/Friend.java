/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.managers.friend.api;

import fun.kubik.helpers.interfaces.IFastAccess;
import lombok.Generated;

public class Friend
implements IFastAccess {
    private String name;

    public Friend(String name) {
        this.name = name;
    }

    @Generated
    public String getName() {
        return this.name;
    }

    @Generated
    public void setName(String name) {
        this.name = name;
    }
}

