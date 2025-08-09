/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.events.main.visual;

import fun.kubik.events.api.main.Event;
import lombok.Generated;
import net.minecraft.util.math.vector.Vector3d;

public class EventFog
implements Event {
    private Vector3d color;
    private float distance;

    public EventFog(Vector3d color) {
        this.color = color;
    }

    public EventFog(float distance) {
        this.distance = distance;
    }

    @Generated
    public Vector3d getColor() {
        return this.color;
    }

    @Generated
    public float getDistance() {
        return this.distance;
    }

    @Generated
    public void setColor(Vector3d color) {
        this.color = color;
    }

    @Generated
    public void setDistance(float distance) {
        this.distance = distance;
    }
}

