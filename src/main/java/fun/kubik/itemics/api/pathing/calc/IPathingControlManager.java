/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.itemics.api.pathing.calc;

import fun.kubik.itemics.api.process.IItemicsProcess;
import fun.kubik.itemics.api.process.PathingCommand;
import java.util.Optional;

public interface IPathingControlManager {
    public void registerProcess(IItemicsProcess var1);

    public Optional<IItemicsProcess> mostRecentInControl();

    public Optional<PathingCommand> mostRecentCommand();
}

