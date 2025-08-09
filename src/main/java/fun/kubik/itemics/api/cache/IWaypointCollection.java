/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.itemics.api.cache;

import java.util.Set;

public interface IWaypointCollection {
    public void addWaypoint(IWaypoint var1);

    public void removeWaypoint(IWaypoint var1);

    public IWaypoint getMostRecentByTag(IWaypoint.Tag var1);

    public Set<IWaypoint> getByTag(IWaypoint.Tag var1);

    public Set<IWaypoint> getAllWaypoints();
}

