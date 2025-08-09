/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.managers.theme.main;

import fun.kubik.helpers.render.ColorHelpers;
import fun.kubik.managers.theme.api.Theme;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.Generated;

public class ThemeManagers {
    public List<Theme> themes = new ArrayList<Theme>();
    private Theme currentTheme = null;

    public ThemeManagers() {
        this.init();
    }

    public void init() {
        this.themes.addAll(Arrays.asList(new Theme("Client", ColorHelpers.rgba(117, 93, 154, 255), ColorHelpers.rgba(40, 31, 52, 255)), new Theme("Kubik", ColorHelpers.rgba(48, 207, 151, 255), ColorHelpers.rgba(24, 103, 75, 255)), new Theme("Custom", ColorHelpers.rgba(255, 255, 255, 255), ColorHelpers.rgba(255, 255, 255, 255))));
        this.currentTheme = this.themes.get(0);
    }

    @Generated
    public Theme getCurrentTheme() {
        return this.currentTheme;
    }

    @Generated
    public void setCurrentTheme(Theme currentTheme) {
        this.currentTheme = currentTheme;
    }
}

