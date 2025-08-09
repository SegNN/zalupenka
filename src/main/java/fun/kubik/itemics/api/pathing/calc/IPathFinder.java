/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.itemics.api.pathing.calc;

import fun.kubik.itemics.api.pathing.goals.Goal;
import fun.kubik.itemics.api.utils.PathCalculationResult;
import java.util.Optional;

public interface IPathFinder {
    public Goal getGoal();

    public PathCalculationResult calculate(long var1, long var3);

    public boolean isFinished();

    public Optional<IPath> pathToMostRecentNodeConsidered();

    public Optional<IPath> bestPathSoFar();
}

