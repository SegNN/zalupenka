/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.itemics.api.event.events;

public final class SprintStateEvent {
    private Boolean state;

    public final void setState(boolean state) {
        this.state = state;
    }

    public final Boolean getState() {
        return this.state;
    }
}

