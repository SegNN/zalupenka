/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.modules.movement;

import fun.kubik.events.api.EventHook;
import fun.kubik.events.main.EventUpdate;
import fun.kubik.managers.module.Module;
import fun.kubik.managers.module.main.Category;
import fun.kubik.managers.module.option.main.SelectOption;
import fun.kubik.managers.module.option.main.SelectOptionValue;
import fun.kubik.utils.time.TimerUtils;
import ru.kotopushka.j2c.sdk.annotations.NativeInclude;

public class Speed
extends Module {
    private boolean boost;
    private final SelectOption mode = new SelectOption("Mode", 0, new SelectOptionValue("ReallyWorld"));
    private final TimerUtils timer = new TimerUtils();

    public Speed() {
        super("Speed", Category.MOVEMENT);
        this.settings(this.mode);
    }

    @EventHook
    public void update(EventUpdate event) {
        switch (((SelectOptionValue)this.mode.getValue()).getName()) {
            case "ReallyWorld": {
                this.reallyWorld();
            }
        }
    }

    @NativeInclude
    private void reallyWorld() {
        if (this.timer.hasTimeElapsed(1150L)) {
            this.boost = true;
        }
        if (this.timer.hasTimeElapsed(7000L)) {
            this.boost = false;
            this.timer.reset();
        }
        if (this.boost) {
            if (Speed.mc.player.isOnGround() && !Speed.mc.gameSettings.keyBindJump.isPressed()) {
                Speed.mc.player.jump();
            }
            mc.getTimer().setSpeed(Speed.mc.player.ticksExisted % 2 == 0 ? 1.5f : 1.2f);
        } else {
            mc.getTimer().setSpeed(0.05f);
        }
    }

    @Override
    @NativeInclude
    public void onEnabled() {
        this.boost = false;
    }

    @Override
    @NativeInclude
    public void onDisabled() {
        mc.getTimer().setSpeed(1.0f);
    }
}

