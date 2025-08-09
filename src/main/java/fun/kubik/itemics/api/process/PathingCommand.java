/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.itemics.api.process;

import fun.kubik.itemics.api.pathing.goals.Goal;

import java.util.Objects;

public class PathingCommand {
    public final Goal goal;
    public final PathingCommandType commandType;

    public PathingCommand(Goal goal, PathingCommandType commandType) {
        Objects.requireNonNull(commandType);
        this.goal = goal;
        this.commandType = commandType;
    }

    public String toString() {
        return String.valueOf((Object)this.commandType) + " " + String.valueOf(this.goal);
    }
}

