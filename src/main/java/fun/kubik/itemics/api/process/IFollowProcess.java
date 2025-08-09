/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.itemics.api.process;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.entity.Entity;

public interface IFollowProcess
extends IItemicsProcess {
    public void follow(Predicate<Entity> var1);

    public List<Entity> following();

    public Predicate<Entity> currentFilter();

    default public void cancel() {
        this.onLostControl();
    }
}

