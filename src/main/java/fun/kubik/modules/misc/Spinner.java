/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.modules.misc;

import fun.kubik.events.api.EventHook;
import fun.kubik.events.main.player.EventSync;
import fun.kubik.managers.module.Module;
import fun.kubik.managers.module.main.Category;
import fun.kubik.managers.module.option.main.SliderOption;

public class Spinner
extends Module {
    private final SliderOption speed = new SliderOption("Speed", 30.0f, 1.0f, 100.0f).increment(1.0f);
    private final SliderOption angle = new SliderOption("Angle", 0.0f, -90.0f, 90.0f).increment(1.0f);
    private long rotationTime = 0L;
    private float rotation = 0.0f;

    public Spinner() {
        super("Spinner", Category.MISC);
        this.settings(this.speed, this.angle);
    }

    @EventHook
    public void sync(EventSync event) {
        if (System.currentTimeMillis() - this.rotationTime >= 0L) {
            this.rotation -= ((Float)this.speed.getValue()).floatValue();
            Spinner.mc.player.rotationYawHead = this.rotation;
            Spinner.mc.player.renderYawOffset = this.rotation;
            Spinner.mc.player.rotationPitchHead = ((Float)this.angle.getValue()).floatValue();
            this.rotationTime = System.currentTimeMillis();
        }
    }
}

