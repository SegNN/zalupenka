/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.events.main.visual;

import fun.kubik.events.api.main.Event;
import fun.kubik.events.api.main.callables.EventCancellable;
import lombok.Generated;

public class EventCamera
extends EventCancellable
implements Event {
    public float yaw;
    public float pitch;
    public float partialTicks;

    public EventCamera() {
    }

    @Generated
    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    @Generated
    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    @Generated
    public void setPartialTicks(float partialTicks) {
        this.partialTicks = partialTicks;
    }

    @Generated
    public float getYaw() {
        return this.yaw;
    }

    @Generated
    public float getPitch() {
        return this.pitch;
    }

    @Generated
    public float getPartialTicks() {
        return this.partialTicks;
    }

    @Generated
    public EventCamera(float yaw, float pitch, float partialTicks) {
        this.yaw = yaw;
        this.pitch = pitch;
        this.partialTicks = partialTicks;
    }
}

