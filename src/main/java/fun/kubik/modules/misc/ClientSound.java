/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.modules.misc;

import fun.kubik.managers.module.Module;
import fun.kubik.managers.module.main.Category;
import fun.kubik.managers.module.option.main.MultiOption;
import fun.kubik.managers.module.option.main.MultiOptionValue;
import fun.kubik.managers.module.option.main.SliderOption;
import lombok.Generated;

public class ClientSound
extends Module {
    private static final MultiOption settings = new MultiOption("Settings", new MultiOptionValue("Scrolling", false), new MultiOptionValue("Module", false), new MultiOptionValue("Notification", false));
    public SliderOption volume = new SliderOption("Volume", 70.0f, 0.0f, 100.0f).increment(1.0f);

    public ClientSound() {
        super("ClientSound", Category.MISC);
        this.settings(this.volume, settings);
    }

    @Generated
    public static MultiOption getSettings() {
        return settings;
    }
}

