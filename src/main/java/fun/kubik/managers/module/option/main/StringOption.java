/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.managers.module.option.main;

import fun.kubik.managers.module.option.api.Option;
import java.util.function.BooleanSupplier;

public class StringOption
extends Option<String> {
    public StringOption(String settingName, String value) {
        super(settingName, value);
    }

    public StringOption visible(BooleanSupplier visible) {
        this.setVisible(visible);
        return this;
    }

    public StringOption description(String settingDescription) {
        this.settingDescription = settingDescription;
        return this;
    }
}

