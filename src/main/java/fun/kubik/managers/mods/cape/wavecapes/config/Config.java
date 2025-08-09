/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.managers.mods.cape.wavecapes.config;

import fun.kubik.managers.mods.cape.wavecapes.CapeMovement;
import fun.kubik.managers.mods.cape.wavecapes.CapeStyle;
import fun.kubik.managers.mods.cape.wavecapes.WindMode;

public class Config {
    public WindMode windMode = WindMode.WAVES;
    public CapeStyle capeStyle = CapeStyle.SMOOTH;
    public CapeMovement capeMovement = CapeMovement.BASIC_SIMULATION;
    public int gravity = 25;
    public int heightMultiplier = 5;
    public int strafeMultiplier = 3;
}

