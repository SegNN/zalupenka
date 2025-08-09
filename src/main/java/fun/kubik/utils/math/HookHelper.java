/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.utils.math;

import fun.kubik.events.api.EventHook;
import fun.kubik.events.main.player.EventRotate;
import fun.kubik.events.main.visual.EventCamera;
import fun.kubik.helpers.interfaces.IFastAccess;
import net.minecraft.util.math.MathHelper;

public class HookHelper
implements IFastAccess {
    private static boolean active;
    private static float freeYaw;
    private static float freePitch;

    @EventHook
    private void onLook(EventRotate event) {
        if (active) {
            this.rotateTowards(event.getYaw(), event.getPitch());
            event.setCancelled(true);
        }
    }

    @EventHook
    private void onRotation(EventCamera event) {
        if (active) {
            event.yaw = freeYaw;
            event.pitch = freePitch;
        } else {
            freeYaw = event.yaw;
            freePitch = event.pitch;
        }
    }

    public static void setActive(boolean state) {
        if (active != state) {
            active = state;
            HookHelper.resetRotation();
        }
    }

    private void rotateTowards(double yaw, double pitch) {
        double d0 = pitch * 0.15;
        double d1 = yaw * 0.15;
        freePitch = (float)((double)freePitch + d0);
        freeYaw = (float)((double)freeYaw + d1);
        freePitch = MathHelper.clamp(freePitch, -90.0f, 90.0f);
    }

    private static void resetRotation() {
        HookHelper.mc.player.rotationYaw = freeYaw;
        HookHelper.mc.player.rotationPitch = freePitch;
    }

    public static boolean isActive() {
        return active;
    }

    public static float getFreeYaw() {
        return freeYaw;
    }

    public static float getFreePitch() {
        return freePitch;
    }
}

