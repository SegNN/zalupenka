/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.itemics.api.command;

import fun.kubik.itemics.api.IItemics;
import fun.kubik.itemics.api.utils.IPlayerContext;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class Command
implements ICommand {
    protected IItemics itemics;
    protected IPlayerContext ctx;
    protected final List<String> names;

    protected Command(IItemics itemics, String ... names) {
        this.names = Collections.unmodifiableList(Stream.of(names).map(s -> s.toLowerCase(Locale.US)).collect(Collectors.toList()));
        this.itemics = itemics;
        this.ctx = itemics.getPlayerContext();
    }

    @Override
    public final List<String> getNames() {
        return this.names;
    }
}

