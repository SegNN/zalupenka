/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.itemics.api;

import fun.kubik.itemics.api.utils.SettingsUtil;

public final class ItemicsAPI {
    private static final IItemicsProvider provider;
    private static final Settings settings;

    public static IItemicsProvider getProvider() {
        return provider;
    }

    public static Settings getSettings() {
        return settings;
    }

    static {
        settings = new Settings();
        SettingsUtil.readAndApply(settings);
        try {
            provider = (IItemicsProvider)Class.forName("fun.kubik.itemics.ItemicsProvider").newInstance();
        } catch (ReflectiveOperationException ex) {
            throw new RuntimeException(ex);
        }
    }
}

