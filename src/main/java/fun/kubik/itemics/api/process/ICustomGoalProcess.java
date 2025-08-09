/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.itemics.api.process;

import fun.kubik.itemics.api.pathing.goals.Goal;

public interface ICustomGoalProcess
extends IItemicsProcess {
    public void setGoal(Goal var1);

    public void path();

    public Goal getGoal();

    default public void setGoalAndPath(Goal goal) {
        this.setGoal(goal);
        this.path();
    }
}

