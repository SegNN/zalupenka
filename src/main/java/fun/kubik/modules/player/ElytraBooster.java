/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.modules.player;

import fun.kubik.Load;
import fun.kubik.events.api.EventHook;
import fun.kubik.events.main.movement.EventJump;
import fun.kubik.events.main.movement.EventStrafe;
import fun.kubik.events.main.player.EventElytra;
import fun.kubik.events.main.player.EventSwimming;
import fun.kubik.events.main.player.EventSync;
import fun.kubik.managers.module.Module;
import fun.kubik.managers.module.main.Category;
import fun.kubik.managers.module.option.main.CheckboxOption;
import fun.kubik.managers.module.option.main.SelectOption;
import fun.kubik.managers.module.option.main.SelectOptionValue;
import fun.kubik.managers.module.option.main.SliderOption;
import fun.kubik.modules.combat.Aura;
import fun.kubik.modules.combat.ElytraTarget;
import fun.kubik.utils.time.TimerUtils;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;

public class ElytraBooster
        extends Module {
    private final SelectOption mode = new SelectOption("Mode", 0, new SelectOptionValue("Max Speed"), new SelectOptionValue("Legit Speed"), new SelectOptionValue("Custom"));
    private final SliderOption speed = new SliderOption("Speed", 1.5f, 1.5f, 2.2f).increment(0.01f).visible(() -> this.mode.getSelected("Custom"));
    private final CheckboxOption antiTarget = new CheckboxOption("Anti Target", false);
    private final SliderOption antiTargetSpeed = new SliderOption("Anti Target Speed", 2.5f, 2.0f, 3.0f).increment(0.01f);
    private final TimerUtils timer = new TimerUtils();

    public ElytraBooster() {
        super("ElytraBooster", Category.PLAYER);
        this.settings(this.mode, this.speed, this.antiTarget, this.antiTargetSpeed);
    }

    @EventHook
    public void elytra(EventElytra eventElytra) {
        Aura aura = (Aura)Load.getInstance().getHooks().getModuleManagers().findClass(Aura.class);
        ElytraTarget elytraTarget = (ElytraTarget)Load.getInstance().getHooks().getModuleManagers().findClass(ElytraTarget.class);
        Vector3d vector3d1 = null;
        if (aura.getTarget() != null) {
            vector3d1 = aura.getTarget().getPositionVec().add(aura.getTarget().getForward().normalize().scale(5.0));
        }
        if (vector3d1 != null && ElytraBooster.mc.player.getDistanceSq(vector3d1) < 3.2) {
            eventElytra.setSpeed(1.2f);
            eventElytra.setYSpeed(1.2f);
        } else {
            eventElytra.setYSpeed(1.6f);
            if (((Boolean)this.antiTarget.getValue()).booleanValue() && ElytraBooster.mc.player.isElytraFlying()) {
                eventElytra.setSpeed(((Float)this.antiTargetSpeed.getValue()).floatValue());
                eventElytra.setPitch(-45.0f);
                eventElytra.setVisualPitch(-45.0f);
                float yaw = aura.isToggled() ? aura.selfRotation.x : ElytraBooster.mc.player.rotationYaw;
                float globalYaw = MathHelper.wrapDegrees(yaw);
                if (globalYaw >= 0.0f && globalYaw <= 90.0f) {
                    eventElytra.setYaw(45.0f);
                } else if (globalYaw >= 90.0f && globalYaw <= 180.0f) {
                    eventElytra.setYaw(135.0f);
                } else if (globalYaw >= -180.0f && globalYaw <= -90.0f) {
                    eventElytra.setYaw(-135.0f);
                } else if (globalYaw >= -90.0f && globalYaw <= 0.0f) {
                    eventElytra.setYaw(-45.0f);
                }
            } else if (this.mode.getSelected("Custom")) {
                eventElytra.setSpeed(((Float)this.speed.getValue()).floatValue());
            } else if (this.mode.getSelected("Max Speed")) {
                float yaw = aura.isToggled() ? aura.selfRotation.x : ElytraBooster.mc.player.rotationYaw;
                float pitch = aura.isToggled() ? aura.selfRotation.y : ElytraBooster.mc.player.rotationPitch;
                float movement = 0.0f;
                float movementY = 0.0f;
                if (this.isPitchRange(pitch, 10.0f, 6.0f)) {
                    movement -= 0.04f;
                }
                if (this.isPitchRange(pitch, -45.0f, 4.0f)) {
                    movementY += 0.4f;
                } else if (this.isPitchRange(pitch, -45.0f, 6.0f)) {
                    movementY += 0.3f;
                }
                if (this.isYawRange(yaw, 45.0f, 4.0f)) {
                    eventElytra.setSpeed(2.26f + movementY + movement);
                } else if (this.isYawRange(yaw, 45.0f, 6.0f)) {
                    eventElytra.setSpeed(2.22f + movementY + movement);
                } else if (this.isYawRange(yaw, 45.0f, 8.0f)) {
                    eventElytra.setSpeed(2.12f + movementY + movement);
                } else if (this.isYawRange(yaw, 45.0f, 10.0f)) {
                    eventElytra.setSpeed(2.06f + movementY);
                } else if (this.isYawRange(yaw, 45.0f, 12.0f)) {
                    eventElytra.setSpeed(1.96f + movementY);
                } else if (this.isYawRange(yaw, 45.0f, 14.0f)) {
                    eventElytra.setSpeed(1.9f + movementY);
                } else if (this.isYawRange(yaw, 45.0f, 16.0f)) {
                    eventElytra.setSpeed(1.86f + movementY);
                } else if (this.isYawRange(yaw, 45.0f, 19.0f)) {
                    eventElytra.setSpeed(1.82f + movementY);
                } else if (this.isYawRange(yaw, 45.0f, 22.0f)) {
                    eventElytra.setSpeed(1.77f + movementY);
                } else if (this.isYawRange(yaw, 45.0f, 24.0f)) {
                    eventElytra.setSpeed(1.74f + movementY);
                } else if (this.isYawRange(yaw, 45.0f, 26.0f)) {
                    eventElytra.setSpeed(1.72f + movementY);
                } else if (this.isYawRange(yaw, 45.0f, 28.0f)) {
                    eventElytra.setSpeed(1.7f + movementY);
                } else if (this.isYawRange(yaw, 45.0f, 30.0f)) {
                    eventElytra.setSpeed(1.66f + movementY);
                } else if (this.isYawRange(yaw, 45.0f, 34.0f)) {
                    eventElytra.setSpeed(1.65f + movementY);
                } else if (this.isYawRange(yaw, 45.0f, 38.0f)) {
                    eventElytra.setSpeed(1.63f + movementY);
                } else if (this.isYawRange(yaw, 45.0f, 45.0f)) {
                    eventElytra.setSpeed(1.61f + movementY);
                }
            } else {
                float pitch;
                float yaw = aura.isToggled() ? aura.selfRotation.x : ElytraBooster.mc.player.rotationYaw;
                float f = pitch = aura.isToggled() ? aura.selfRotation.y : ElytraBooster.mc.player.rotationPitch;
                if (this.isYawRange(yaw, 45.0f, 22.0f)) {
                    eventElytra.setSpeed(1.8f);
                } else if (this.isYawRange(yaw, 45.0f, 24.0f)) {
                    eventElytra.setSpeed(1.74f);
                } else if (this.isYawRange(yaw, 45.0f, 26.0f)) {
                    eventElytra.setSpeed(1.72f);
                } else if (this.isYawRange(yaw, 45.0f, 28.0f)) {
                    eventElytra.setSpeed(1.7f);
                } else if (this.isYawRange(yaw, 45.0f, 30.0f)) {
                    eventElytra.setSpeed(1.66f);
                } else if (this.isYawRange(yaw, 45.0f, 34.0f)) {
                    eventElytra.setSpeed(1.65f);
                } else if (this.isYawRange(yaw, 45.0f, 38.0f)) {
                    eventElytra.setSpeed(1.63f);
                } else if (this.isYawRange(yaw, 45.0f, 45.0f)) {
                    eventElytra.setSpeed(1.61f);
                }
                if (this.isPitchRange(pitch, 45.0f, 1.0f)) {
                    eventElytra.setSpeed(2.37f);
                } else if (this.isPitchRange(pitch, 45.0f, 5.0f)) {
                    eventElytra.setSpeed(2.35f);
                } else if (this.isPitchRange(pitch, 45.0f, 8.0f)) {
                    eventElytra.setSpeed(2.33f);
                } else if (this.isPitchRange(pitch, 45.0f, 10.0f)) {
                    eventElytra.setSpeed(2.32f);
                } else if (this.isPitchRange(pitch, 45.0f, 12.0f)) {
                    eventElytra.setSpeed(2.26f);
                } else if (this.isPitchRange(pitch, 45.0f, 14.0f)) {
                    eventElytra.setSpeed(2.1f);
                } else if (this.isPitchRange(pitch, 45.0f, 16.0f)) {
                    eventElytra.setSpeed(2.05f);
                } else if (this.isPitchRange(pitch, 45.0f, 19.0f)) {
                    eventElytra.setSpeed(2.03f);
                }
            }
        }
    }

    @EventHook
    public void rotation(EventSync eventSync) {
        if (((Boolean)this.antiTarget.getValue()).booleanValue() && ElytraBooster.mc.player.isElytraFlying()) {
            Aura aura = (Aura)Load.getInstance().getHooks().getModuleManagers().findClass(Aura.class);
            float yaw = aura.isToggled() ? aura.selfRotation.x : ElytraBooster.mc.player.rotationYaw;
            float globalYaw = MathHelper.wrapDegrees(yaw);
            if (globalYaw >= 0.0f && globalYaw <= 90.0f) {
                eventSync.setYaw(45.0f);
            } else if (globalYaw >= 90.0f && globalYaw <= 180.0f) {
                eventSync.setYaw(135.0f);
            } else if (globalYaw >= -180.0f && globalYaw <= -90.0f) {
                eventSync.setYaw(-135.0f);
            } else if (globalYaw >= -90.0f && globalYaw <= 0.0f) {
                eventSync.setYaw(-45.0f);
            }
            ElytraBooster.mc.player.rotationPitchHead = -45.0f;
            eventSync.setPitch(-45.0f);
        }
    }

    @EventHook
    public void strafe(EventStrafe eventStrafe) {
        if (((Boolean)this.antiTarget.getValue()).booleanValue() && ElytraBooster.mc.player.isElytraFlying()) {
            Aura aura = (Aura)Load.getInstance().getHooks().getModuleManagers().findClass(Aura.class);
            float yaw = aura.isToggled() ? aura.selfRotation.x : ElytraBooster.mc.player.rotationYaw;
            float globalYaw = MathHelper.wrapDegrees(yaw);
            if (globalYaw >= 0.0f && globalYaw <= 90.0f) {
                eventStrafe.setYaw(45.0f);
            } else if (globalYaw >= 90.0f && globalYaw <= 180.0f) {
                eventStrafe.setYaw(135.0f);
            } else if (globalYaw >= -180.0f && globalYaw <= -90.0f) {
                eventStrafe.setYaw(-135.0f);
            } else if (globalYaw >= -90.0f && globalYaw <= 0.0f) {
                eventStrafe.setYaw(-45.0f);
            }
        }
    }

    @EventHook
    public void jump(EventJump eventJump) {
        if (((Boolean)this.antiTarget.getValue()).booleanValue() && ElytraBooster.mc.player.isElytraFlying()) {
            Aura aura = (Aura)Load.getInstance().getHooks().getModuleManagers().findClass(Aura.class);
            float yaw = aura.isToggled() ? aura.selfRotation.x : ElytraBooster.mc.player.rotationYaw;
            float globalYaw = MathHelper.wrapDegrees(yaw);
            if (globalYaw >= 0.0f && globalYaw <= 90.0f) {
                eventJump.setYaw(45.0f);
            } else if (globalYaw >= 90.0f && globalYaw <= 180.0f) {
                eventJump.setYaw(135.0f);
            } else if (globalYaw >= -180.0f && globalYaw <= -90.0f) {
                eventJump.setYaw(-135.0f);
            } else if (globalYaw >= -90.0f && globalYaw <= 0.0f) {
                eventJump.setYaw(-45.0f);
            }
        }
    }

    @EventHook
    public void swimming(EventSwimming eventSwimming) {
        if (((Boolean)this.antiTarget.getValue()).booleanValue() && ElytraBooster.mc.player.isElytraFlying()) {
            eventSwimming.setPitch(-45.0f);
            Aura aura = (Aura)Load.getInstance().getHooks().getModuleManagers().findClass(Aura.class);
            float yaw = aura.isToggled() ? aura.selfRotation.x : ElytraBooster.mc.player.rotationYaw;
            float globalYaw = MathHelper.wrapDegrees(yaw);
            if (globalYaw >= 0.0f && globalYaw <= 90.0f) {
                eventSwimming.setYaw(45.0f);
            } else if (globalYaw >= 90.0f && globalYaw <= 180.0f) {
                eventSwimming.setYaw(135.0f);
            } else if (globalYaw >= -180.0f && globalYaw <= -90.0f) {
                eventSwimming.setYaw(-135.0f);
            } else if (globalYaw >= -90.0f && globalYaw <= 0.0f) {
                eventSwimming.setYaw(-45.0f);
            }
        }
    }

    private boolean isPitchRange(float pitch, float value, float radius) {
        float globalPitch = pitch;
        float min = MathHelper.wrapDegrees(value - radius);
        float max = MathHelper.wrapDegrees(value + radius);
        return globalPitch >= min && globalPitch <= max;
    }

    private boolean isYawRange(float yaw, float value, float radius) {
        float globalYaw = MathHelper.wrapDegrees(yaw);
        if (globalYaw >= 0.0f && globalYaw <= 90.0f) {
            float max;
            float yaws = MathHelper.wrapDegrees(yaw);
            float min = MathHelper.wrapDegrees(value - radius);
            if (min < (max = MathHelper.wrapDegrees(value + radius))) {
                return yaws >= min && yaws <= max;
            }
            return yaws >= min || yaws <= max;
        }
        if (globalYaw >= 90.0f && globalYaw <= 180.0f) {
            float max;
            float yaws = MathHelper.wrapDegrees(yaw - 90.0f);
            float min = MathHelper.wrapDegrees(value - radius);
            if (min < (max = MathHelper.wrapDegrees(value + radius))) {
                return yaws >= min && yaws <= max;
            }
            return yaws >= min || yaws <= max;
        }
        if (globalYaw >= -180.0f && globalYaw <= -90.0f) {
            float max;
            float yaws = MathHelper.wrapDegrees(yaw - 180.0f);
            float min = MathHelper.wrapDegrees(value - radius);
            if (min < (max = MathHelper.wrapDegrees(value + radius))) {
                return yaws >= min && yaws <= max;
            }
            return yaws >= min || yaws <= max;
        }
        if (globalYaw >= -90.0f && globalYaw <= 0.0f) {
            float max;
            float yaws = MathHelper.wrapDegrees(yaw - 270.0f);
            float min = MathHelper.wrapDegrees(value - radius);
            if (min < (max = MathHelper.wrapDegrees(value + radius))) {
                return yaws >= min && yaws <= max;
            }
            return yaws >= min || yaws <= max;
        }
        return false;
    }
}

