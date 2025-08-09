/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.itemics.api.utils;

import fun.kubik.utils.math.GCDUtils;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.util.math.MathHelper;

public class Rotation {
    private float yaw;
    private float pitch;

    public Rotation(float yaw, float pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
        if (Float.isInfinite(yaw) || Float.isNaN(yaw) || Float.isInfinite(pitch) || Float.isNaN(pitch)) {
            throw new IllegalStateException(yaw + " " + pitch);
        }
    }

    public float getYaw() {
        return GCDUtils.getFixedRotation(this.yaw + ThreadLocalRandom.current().nextFloat(-0.1f, 0.1f));
    }

    public float getPitch() {
        return GCDUtils.getFixedRotation(MathHelper.clamp(this.pitch + ThreadLocalRandom.current().nextFloat(-0.1f, 0.1f), -90.0f, 90.0f));
    }

    public Rotation add(Rotation other) {
        return new Rotation(this.yaw + other.yaw, this.pitch + other.pitch);
    }

    public Rotation subtract(Rotation other) {
        return new Rotation(this.yaw - other.yaw, this.pitch - other.pitch);
    }

    public Rotation clamp() {
        return new Rotation(this.yaw, Rotation.clampPitch(this.pitch));
    }

    public Rotation normalize() {
        return new Rotation(Rotation.normalizeYaw(this.yaw), this.pitch);
    }

    public Rotation normalizeAndClamp() {
        return new Rotation(Rotation.normalizeYaw(this.yaw), Rotation.clampPitch(this.pitch));
    }

    public boolean isReallyCloseTo(Rotation other) {
        return this.yawIsReallyClose(other) && (double)Math.abs(this.pitch - other.pitch) < 0.01;
    }

    public boolean yawIsReallyClose(Rotation other) {
        float yawDiff = Math.abs(Rotation.normalizeYaw(this.yaw) - Rotation.normalizeYaw(other.yaw));
        return (double)yawDiff < 0.01 || (double)yawDiff > 359.99;
    }

    public static float clampPitch(float pitch) {
        return Math.max(-90.0f, Math.min(90.0f, pitch));
    }

    public static float normalizeYaw(float yaw) {
        float newYaw = yaw % 360.0f;
        if (newYaw < -180.0f) {
            newYaw += 360.0f;
        }
        if (newYaw > 180.0f) {
            newYaw -= 360.0f;
        }
        return newYaw;
    }

    public String toString() {
        return "Yaw: " + this.yaw + ", Pitch: " + this.pitch;
    }
}

