/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.events.main.movement;

import fun.kubik.events.api.main.callables.EventCancellable;
import lombok.Generated;

public class EventJump
extends EventCancellable {
    private float jumpMotion;
    private float yaw;

    @Generated
    public float getJumpMotion() {
        return this.jumpMotion;
    }

    @Generated
    public float getYaw() {
        return this.yaw;
    }

    @Generated
    public void setJumpMotion(float jumpMotion) {
        this.jumpMotion = jumpMotion;
    }

    @Generated
    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    @Generated
    public EventJump(float jumpMotion, float yaw) {
        this.jumpMotion = jumpMotion;
        this.yaw = yaw;
    }
}

