/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.events.main.render;

import fun.kubik.events.api.main.Event;
import fun.kubik.events.api.main.callables.EventCancellable;
import lombok.Generated;

public class EventGameOverlay
extends EventCancellable
implements Event {
    private final OverlayType overlayType;

    @Generated
    public OverlayType getOverlayType() {
        return this.overlayType;
    }

    @Generated
    public EventGameOverlay(OverlayType overlayType) {
        this.overlayType = overlayType;
    }

    public static enum OverlayType {
        Hurt,
        PumpkinOverlay,
        TotemPop,
        CameraBounds,
        Fire,
        Light,
        BossBar,
        Fog,
        WaterFog,
        LavaFog,
        Blindness,
        Scoreboard,
        Block,
        Nausea,
        Hologram;

    }
}

