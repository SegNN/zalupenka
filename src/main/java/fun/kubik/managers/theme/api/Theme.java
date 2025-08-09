/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.managers.theme.api;

import fun.kubik.helpers.render.ColorHelpers;

public class Theme {
    public String name;
    public int[] colors;

    public Theme(String name, int ... colors) {
        this.name = name;
        this.colors = colors;
    }

    public int getColor(int index) {
        return ColorHelpers.gradient(5, index, this.colors);
    }
}

