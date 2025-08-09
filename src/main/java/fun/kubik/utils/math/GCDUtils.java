/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.utils.math;

import fun.kubik.helpers.interfaces.IFastAccess;
import lombok.Generated;

public final class GCDUtils
implements IFastAccess {
    public static float getFixedRotation(float rot) {
        return GCDUtils.getDeltaMouse(rot) * GCDUtils.getGCDValue();
    }

    public static float getGCDValue() {
        return (float)((double)GCDUtils.getGCD() * 0.15);
    }

    public static float getGCD() {
        double var11 = GCDUtils.mc.gameSettings.mouseSensitivity / (double)0.15f / 8.0;
        double var9 = Math.cbrt(var11);
        float f1 = (float)((var9 - (double)0.2f) / (double)0.6f * 0.6 + 0.2);
        return f1 * f1 * f1 * 8.0f;
    }

    public static float getDeltaMouse(float delta) {
        return Math.round(delta / GCDUtils.getGCDValue());
    }

    @Generated
    private GCDUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}

