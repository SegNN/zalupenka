/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.modules.movement;

import fun.kubik.Load;
import fun.kubik.events.api.EventHook;
import fun.kubik.events.main.player.EventElytra;
import fun.kubik.managers.module.Module;
import fun.kubik.managers.module.main.Category;
import fun.kubik.modules.combat.Aura;
import java.util.concurrent.atomic.AtomicReference;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.vector.Vector3d;

public class SuperFirework
extends Module {
    public float speeder = 1.5f;
    public Vector3d vector3d;

    public SuperFirework() {
        super("SuperFirework", Category.MOVEMENT);
    }

    @EventHook
    private void onFirework(EventElytra e) {
        float yaw = this.getAuraYaw();
        float pitch = this.getAuraPitch();
        AtomicReference<Float> speed = new AtomicReference<Float>(Float.valueOf(1.615f));
        if (SuperFirework.isYawInRange(yaw, 45.0f, 13.0f)) {
            if (pitch >= 1.0f && pitch <= 90.0f) {
                speed.set(Float.valueOf(1.86f));
            } else {
                speed.set(Float.valueOf(1.94f));
            }
        }
        if (SuperFirework.isYawInRange(yaw, 135.0f, 13.0f)) {
            if (pitch >= 1.0f && pitch <= 90.0f) {
                speed.set(Float.valueOf(1.86f));
            } else {
                speed.set(Float.valueOf(1.94f));
            }
        }
        if (SuperFirework.isYawInRange(yaw, 225.0f, 13.0f)) {
            if (pitch >= 1.0f && pitch <= 90.0f) {
                speed.set(Float.valueOf(1.86f));
            } else {
                speed.set(Float.valueOf(1.94f));
            }
        }
        if (SuperFirework.isYawInRange(yaw, 315.0f, 13.0f)) {
            if (pitch >= 1.0f && pitch <= 90.0f) {
                speed.set(Float.valueOf(1.86f));
            } else {
                speed.set(Float.valueOf(1.94f));
            }
        }
        if (SuperFirework.isYawInRange(yaw, 45.0f, 14.0f) && !SuperFirework.isYawInRange(yaw, 45.0f, 13.0f)) {
            speed.set(Float.valueOf(1.91f));
        }
        if (SuperFirework.isYawInRange(yaw, 45.0f, 18.0f) && !SuperFirework.isYawInRange(yaw, 45.0f, 14.0f)) {
            speed.set(Float.valueOf(1.85f));
        }
        if (SuperFirework.isYawInRange(yaw, 45.0f, 19.0f) && !SuperFirework.isYawInRange(yaw, 45.0f, 18.0f)) {
            speed.set(Float.valueOf(1.83f));
        }
        if (SuperFirework.isYawInRange(yaw, 45.0f, 20.0f) && !SuperFirework.isYawInRange(yaw, 45.0f, 19.0f)) {
            speed.set(Float.valueOf(1.8f));
        }
        if (SuperFirework.isYawInRange(yaw, 45.0f, 22.0f) && !SuperFirework.isYawInRange(yaw, 45.0f, 20.0f)) {
            speed.set(Float.valueOf(1.78f));
        }
        if (SuperFirework.isYawInRange(yaw, 45.0f, 23.0f) && !SuperFirework.isYawInRange(yaw, 45.0f, 22.0f)) {
            speed.set(Float.valueOf(1.76f));
        }
        if (SuperFirework.isYawInRange(yaw, 45.0f, 25.0f) && !SuperFirework.isYawInRange(yaw, 45.0f, 24.0f)) {
            speed.set(Float.valueOf(1.73f));
        }
        if (SuperFirework.isYawInRange(yaw, 45.0f, 26.0f) && !SuperFirework.isYawInRange(yaw, 45.0f, 25.0f)) {
            speed.set(Float.valueOf(1.72f));
        }
        if (SuperFirework.isYawInRange(yaw, 45.0f, 29.0f) && !SuperFirework.isYawInRange(yaw, 45.0f, 26.0f)) {
            speed.set(Float.valueOf(1.7f));
        }
        if (SuperFirework.isYawInRange(yaw, 135.0f, 14.0f) && !SuperFirework.isYawInRange(yaw, 135.0f, 13.0f)) {
            speed.set(Float.valueOf(1.91f));
        }
        if (SuperFirework.isYawInRange(yaw, 135.0f, 17.0f) && !SuperFirework.isYawInRange(yaw, 135.0f, 14.0f)) {
            speed.set(Float.valueOf(1.85f));
        }
        if (SuperFirework.isYawInRange(yaw, 135.0f, 18.0f) && !SuperFirework.isYawInRange(yaw, 135.0f, 17.0f)) {
            speed.set(Float.valueOf(1.82f));
        }
        if (SuperFirework.isYawInRange(yaw, 135.0f, 19.0f) && !SuperFirework.isYawInRange(yaw, 135.0f, 18.0f)) {
            speed.set(Float.valueOf(1.8f));
        }
        if (SuperFirework.isYawInRange(yaw, 135.0f, 23.0f) && !SuperFirework.isYawInRange(yaw, 135.0f, 19.0f)) {
            speed.set(Float.valueOf(1.77f));
        }
        if (SuperFirework.isYawInRange(yaw, 135.0f, 24.0f) && !SuperFirework.isYawInRange(yaw, 135.0f, 23.0f)) {
            speed.set(Float.valueOf(1.75f));
        }
        if (SuperFirework.isYawInRange(yaw, 135.0f, 28.0f) && !SuperFirework.isYawInRange(yaw, 135.0f, 24.0f)) {
            speed.set(Float.valueOf(1.7f));
        }
        if (SuperFirework.isYawInRange(yaw, 225.0f, 14.0f) && !SuperFirework.isYawInRange(yaw, 225.0f, 13.0f)) {
            speed.set(Float.valueOf(1.91f));
        }
        if (SuperFirework.isYawInRange(yaw, 225.0f, 17.0f) && !SuperFirework.isYawInRange(yaw, 225.0f, 14.0f)) {
            speed.set(Float.valueOf(1.85f));
        }
        if (SuperFirework.isYawInRange(yaw, 225.0f, 18.0f) && !SuperFirework.isYawInRange(yaw, 225.0f, 17.0f)) {
            speed.set(Float.valueOf(1.82f));
        }
        if (SuperFirework.isYawInRange(yaw, 225.0f, 19.0f) && !SuperFirework.isYawInRange(yaw, 225.0f, 18.0f)) {
            speed.set(Float.valueOf(1.8f));
        }
        if (SuperFirework.isYawInRange(yaw, 225.0f, 23.0f) && !SuperFirework.isYawInRange(yaw, 225.0f, 19.0f)) {
            speed.set(Float.valueOf(1.77f));
        }
        if (SuperFirework.isYawInRange(yaw, 225.0f, 24.0f) && !SuperFirework.isYawInRange(yaw, 225.0f, 23.0f)) {
            speed.set(Float.valueOf(1.75f));
        }
        if (SuperFirework.isYawInRange(yaw, 225.0f, 28.0f) && !SuperFirework.isYawInRange(yaw, 225.0f, 24.0f)) {
            speed.set(Float.valueOf(1.7f));
        }
        if (SuperFirework.isYawInRange(yaw, 315.0f, 14.0f) && !SuperFirework.isYawInRange(yaw, 315.0f, 13.0f)) {
            speed.set(Float.valueOf(1.91f));
        }
        if (SuperFirework.isYawInRange(yaw, 315.0f, 17.0f) && !SuperFirework.isYawInRange(yaw, 315.0f, 14.0f)) {
            speed.set(Float.valueOf(1.85f));
        }
        if (SuperFirework.isYawInRange(yaw, 315.0f, 18.0f) && !SuperFirework.isYawInRange(yaw, 315.0f, 17.0f)) {
            speed.set(Float.valueOf(1.82f));
        }
        if (SuperFirework.isYawInRange(yaw, 315.0f, 19.0f) && !SuperFirework.isYawInRange(yaw, 315.0f, 18.0f)) {
            speed.set(Float.valueOf(1.8f));
        }
        if (SuperFirework.isYawInRange(yaw, 315.0f, 23.0f) && !SuperFirework.isYawInRange(yaw, 315.0f, 19.0f)) {
            speed.set(Float.valueOf(1.77f));
        }
        if (SuperFirework.isYawInRange(yaw, 315.0f, 24.0f) && !SuperFirework.isYawInRange(yaw, 315.0f, 23.0f)) {
            speed.set(Float.valueOf(1.75f));
        }
        if (SuperFirework.isYawInRange(yaw, 315.0f, 28.0f) && !SuperFirework.isYawInRange(yaw, 315.0f, 24.0f)) {
            speed.set(Float.valueOf(1.7f));
        }
        if (pitch <= -30.0f || pitch >= 30.0f) {
            speed.set(Float.valueOf(1.615f));
        }
        if (this.isInTargetRange()) {
            e.setSpeed(1.5f);
        } else {
            e.setSpeed(speed.get().floatValue());
        }
    }

    private boolean isCardinalDirection(float yaw) {
        float[] cardinals;
        for (float base : cardinals = new float[]{0.0f, 90.0f, 180.0f, 270.0f}) {
            if (!SuperFirework.isYawInRange(yaw, base, 5.0f)) continue;
            return true;
        }
        return false;
    }

    private boolean isDiagonalDirection(float yaw) {
        float[] diagonals;
        for (float base : diagonals = new float[]{45.0f, 135.0f, 225.0f, 315.0f}) {
            if (!SuperFirework.isYawInRange(yaw, base, 10.0f)) continue;
            return true;
        }
        return false;
    }

    public static Vector3d getGrimElytraVelocity(Vector3d motion, Vector3d look, float yaw, boolean isNewVersion, double gravity) {
        double d5;
        double yRotRadians = Math.toRadians(yaw);
        double horizontalSqrt = Math.sqrt(look.x * look.x + look.z * look.z);
        double horizontalLength = Math.sqrt(motion.x * motion.x + motion.z * motion.z);
        double length = look.length();
        double vertCosRotation = isNewVersion ? Math.cos(yRotRadians) : Math.cos(yRotRadians);
        vertCosRotation = vertCosRotation * vertCosRotation * Math.min(1.0, length / 0.4);
        Vector3d newMotion = motion.add(0.0, gravity * (-1.0 + vertCosRotation * 0.75), 0.0);
        if (newMotion.y < 0.0 && horizontalSqrt > 0.0) {
            d5 = newMotion.y * -0.1 * vertCosRotation;
            newMotion = newMotion.add(look.x * d5 / horizontalSqrt, d5, look.z * d5 / horizontalSqrt);
        }
        if (yRotRadians < 0.0 && horizontalSqrt > 0.0) {
            d5 = horizontalLength * -Math.sin(yRotRadians) * 0.04;
            newMotion = newMotion.add(-look.x * d5 / horizontalSqrt, d5 * 3.2, -look.z * d5 / horizontalSqrt);
        }
        if (horizontalSqrt > 0.0) {
            newMotion = newMotion.add((look.x / horizontalSqrt * horizontalLength - newMotion.x) * 0.1, 0.0, (look.z / horizontalSqrt * horizontalLength - newMotion.z) * 0.1);
        }
        return newMotion;
    }

    private void setSpeedForYawRange(AtomicReference<Float> speed, float yaw, int base, float[] radii, float[] speeds) {
        for (int i = 0; i < radii.length; ++i) {
            if (!SuperFirework.isYawInRange(yaw, base, (int)radii[i]) || i != 0 && SuperFirework.isYawInRange(yaw, base, (int)radii[i - 1])) continue;
            speed.set(Float.valueOf(speeds[i]));
            break;
        }
    }

    private double get3DSpeed(Entity entity) {
        double dx = entity.getPosX() - entity.prevPosX;
        double dy = entity.getPosY() - entity.prevPosY;
        double dz = entity.getPosZ() - entity.prevPosZ;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    private float getAuraYaw() {
        Aura ka = (Aura)Load.getInstance().getHooks().getModuleManagers().findClass(Aura.class);
        return ka.isToggled() && ka.getDistance() != null ? ka.selfRotation.x : SuperFirework.mc.player.rotationYaw;
    }

    private float getAuraPitch() {
        Aura ka = (Aura)Load.getInstance().getHooks().getModuleManagers().findClass(Aura.class);
        return ka.isToggled() && ka.getTarget() != null ? ka.selfRotation.y : SuperFirework.mc.player.rotationPitch;
    }

    private boolean isInTargetRange() {
        Aura ka = (Aura)Load.getInstance().getHooks().getModuleManagers().findClass(Aura.class);
        return ka.isToggled() && ka.getTarget() != null && this.vector3d != null && SuperFirework.mc.player.getEyePosition(1.0f).distanceTo(this.vector3d) < 0.0;
    }

    public static boolean isYawInRange(float yaw, float firstValue, float radiusValue) {
        yaw = (yaw % 360.0f + 360.0f) % 360.0f;
        float minValue = ((firstValue = (firstValue % 360.0f + 360.0f) % 360.0f) - radiusValue + 360.0f) % 360.0f;
        float maxValue = (firstValue + radiusValue) % 360.0f;
        if (minValue < maxValue) {
            return yaw >= minValue && yaw <= maxValue;
        }
        return yaw >= minValue || yaw <= maxValue;
    }
}

