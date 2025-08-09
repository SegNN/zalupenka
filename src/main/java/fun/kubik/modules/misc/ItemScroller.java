/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.modules.misc;

import fun.kubik.managers.module.Module;
import fun.kubik.managers.module.main.Category;
import fun.kubik.managers.module.option.main.SliderOption;
import lombok.Generated;

public class ItemScroller
extends Module {
    private final SliderOption delay = new SliderOption("Delay", 20.0f, 0.0f, 100.0f).increment(5.0f);

    public ItemScroller() {
        super("ItemScroller", Category.MISC);
        this.settings(this.delay);
    }

    @Generated
    public SliderOption getDelay() {
        return this.delay;
    }
}

