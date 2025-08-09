/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.itemics.api.utils;

import com.google.gson.annotations.SerializedName;

public class MyChunkPos {
    @SerializedName(value="x")
    public int x;
    @SerializedName(value="z")
    public int z;

    public String toString() {
        return this.x + ", " + this.z;
    }
}

