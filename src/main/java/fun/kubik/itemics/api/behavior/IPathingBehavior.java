/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.itemics.api.behavior;

import fun.kubik.itemics.api.pathing.calc.IPath;
import fun.kubik.itemics.api.pathing.calc.IPathFinder;
import fun.kubik.itemics.api.pathing.goals.Goal;
import fun.kubik.itemics.api.pathing.path.IPathExecutor;
import java.util.Optional;

public interface IPathingBehavior
extends IBehavior {
    default public Optional<Double> ticksRemainingInSegment() {
        return this.ticksRemainingInSegment(true);
    }

    default public Optional<Double> ticksRemainingInSegment(boolean includeCurrentMovement) {
        IPathExecutor current = this.getCurrent();
        if (current == null) {
            return Optional.empty();
        }
        int start = includeCurrentMovement ? current.getPosition() : current.getPosition() + 1;
        return Optional.of(current.getPath().ticksRemainingFrom(start));
    }

    public Optional<Double> estimatedTicksToGoal();

    public Goal getGoal();

    public boolean isPathing();

    default public boolean hasPath() {
        return this.getCurrent() != null;
    }

    public boolean cancelEverything();

    public void forceCancel();

    default public Optional<IPath> getPath() {
        return Optional.ofNullable(this.getCurrent()).map(IPathExecutor::getPath);
    }

    public Optional<? extends IPathFinder> getInProgress();

    public IPathExecutor getCurrent();

    public IPathExecutor getNext();
}

