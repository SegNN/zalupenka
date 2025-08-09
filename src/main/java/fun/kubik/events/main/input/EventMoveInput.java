/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.events.main.input;

import fun.kubik.events.api.main.Event;
import lombok.Generated;

public class EventMoveInput
implements Event {
    private float forward;
    private float strafe;

    @Generated
    public void setForward(float forward) {
        this.forward = forward;
    }

    @Generated
    public void setStrafe(float strafe) {
        this.strafe = strafe;
    }

    @Generated
    public float getForward() {
        return this.forward;
    }

    @Generated
    public float getStrafe() {
        return this.strafe;
    }

    @Generated
    public EventMoveInput(float forward, float strafe) {
        this.forward = forward;
        this.strafe = strafe;
    }
}

