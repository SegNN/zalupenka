/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.itemics.api.process;

import java.nio.file.Path;

public interface IExploreProcess
extends IItemicsProcess {
    public void explore(int var1, int var2);

    public void applyJsonFilter(Path var1, boolean var2) throws Exception;
}

