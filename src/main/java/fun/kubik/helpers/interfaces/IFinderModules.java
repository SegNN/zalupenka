/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.helpers.interfaces;

public interface IFinderModules<V> {
    public <T extends V> T findName(String var1);

    public <T extends V> T findClass(Class<T> var1);
}

