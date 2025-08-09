/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.modules.player;

import fun.kubik.events.api.EventHook;
import fun.kubik.events.main.EventUpdate;
import fun.kubik.managers.module.Module;
import fun.kubik.managers.module.main.Category;
import fun.kubik.managers.module.option.main.SelectOption;
import fun.kubik.managers.module.option.main.SelectOptionValue;
import fun.kubik.managers.module.option.main.SliderOption;
import ru.kotopushka.j2c.sdk.annotations.NativeInclude;

public class FastBreak
extends Module {
    private final SelectOption mode = new SelectOption("Mode", 0, new SelectOptionValue("Default"), new SelectOptionValue("Custom"));
    private final SliderOption customSpeed = new SliderOption("Custom Speed", 1.0f, 0.0f, 1.0f).increment(0.01f).visible(() -> this.mode.getSelected("Custom"));

    public FastBreak() {
        super("FastBreak", Category.PLAYER);
        this.settings(this.mode, this.customSpeed);
    }

    @EventHook
    @NativeInclude
    public void update(EventUpdate event) {
        FastBreak.mc.playerController.resetBlockRemoving();
        FastBreak.mc.playerController.setBlockHitDelay(0);
        if (this.mode.getSelected("Custom")) {
            FastBreak.mc.playerController.setCurBlockDamageMP(((Float)this.customSpeed.getValue()).floatValue());
        }
    }
}

