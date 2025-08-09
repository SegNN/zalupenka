/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.itemics.api.pathing.movement;

public enum MovementStatus {
    PREPPING(false),
    WAITING(false),
    RUNNING(false),
    SUCCESS(true),
    UNREACHABLE(true),
    FAILED(true),
    CANCELED(true);

    private final boolean complete;

    private MovementStatus(boolean complete) {
        this.complete = complete;
    }

    public final boolean isComplete() {
        return this.complete;
    }
}

