/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.itemics.api.utils;

import fun.kubik.itemics.api.behavior.IBehavior;
import fun.kubik.itemics.api.utils.input.Input;

public interface IInputOverrideHandler
extends IBehavior {
    public boolean isInputForcedDown(Input var1);

    public void setInputForceState(Input var1, boolean var2);

    public void clearAllKeys();
}

