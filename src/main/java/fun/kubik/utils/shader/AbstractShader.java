/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.utils.shader;

import com.mojang.blaze3d.systems.IRenderCall;
import fun.kubik.helpers.interfaces.IFastAccess;
import java.util.concurrent.ConcurrentLinkedQueue;
import lombok.Generated;

public abstract class AbstractShader
implements IFastAccess {
    private boolean active;
    private float radius;
    private float compression;
    private float glow;

    public abstract void run(float var1, ConcurrentLinkedQueue<IRenderCall> var2);

    public abstract void update();

    @Generated
    public boolean isActive() {
        return this.active;
    }

    @Generated
    public float getRadius() {
        return this.radius;
    }

    @Generated
    public float getCompression() {
        return this.compression;
    }

    @Generated
    public float getGlow() {
        return this.glow;
    }

    @Generated
    public void setActive(boolean active) {
        this.active = active;
    }

    @Generated
    public void setRadius(float radius) {
        this.radius = radius;
    }

    @Generated
    public void setCompression(float compression) {
        this.compression = compression;
    }

    @Generated
    public void setGlow(float glow) {
        this.glow = glow;
    }
}

