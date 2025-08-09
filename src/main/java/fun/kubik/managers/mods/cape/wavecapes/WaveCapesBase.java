/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.managers.mods.cape.wavecapes;

import fun.kubik.managers.mods.cape.wavecapes.config.Config;

public class WaveCapesBase {
    public static WaveCapesBase INSTANCE;
    public static Config config;

    public void init() {
        INSTANCE = this;
        config = new Config();
    }
}

