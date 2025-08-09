/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.modules.player;

import fun.kubik.events.api.EventHook;
import fun.kubik.events.main.EventUpdate;
import fun.kubik.events.main.player.EventSwimming;
import fun.kubik.events.main.player.EventSync;
import fun.kubik.managers.module.Module;
import fun.kubik.managers.module.main.Category;
import fun.kubik.managers.module.option.main.SelectOption;
import fun.kubik.managers.module.option.main.SelectOptionValue;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;

public class NoClip
extends Module {
    private final SelectOption mode = new SelectOption("Mode", 0, new SelectOptionValue("ReallyWorld"));

    public NoClip() {
        super("NoClip", Category.PLAYER);
        this.settings(this.mode);
    }

    @EventHook
    public void update(EventUpdate event) {
        if (this.mode.getSelected("ReallyWorld") && NoClip.mc.gameSettings.keyBindSneak.isKeyDown()) {
            NoClip.mc.playerController.onPlayerDamageBlock(new BlockPos(NoClip.mc.player.getPosX(), NoClip.mc.player.getPosY() - 1.0, NoClip.mc.player.getPosZ()), NoClip.mc.player.getHorizontalFacing());
            NoClip.mc.player.swingArm(Hand.MAIN_HAND);
        }
    }

    @EventHook
    public void sync(EventSync event) {
        event.setPitch(90.0f);
        NoClip.mc.player.rotationPitchHead = 90.0f;
    }

    @EventHook
    public void swim(EventSwimming event) {
        event.setPitch(90.0f);
    }
}

