/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.modules.render;

import fun.kubik.managers.module.Module;
import fun.kubik.managers.module.main.Category;
import fun.kubik.managers.module.option.main.MultiOption;
import fun.kubik.managers.module.option.main.MultiOptionValue;
import fun.kubik.managers.module.option.main.SelectOption;
import fun.kubik.managers.module.option.main.SelectOptionValue;
import lombok.Generated;

public class CustomModel
extends Module {
    private final SelectOption mode = new SelectOption("Mode", 0, new SelectOptionValue("Rabbit"), new SelectOptionValue("Jeff Killer"), new SelectOptionValue("Demon"), new SelectOptionValue("White Demon"), new SelectOptionValue("Freddy Bear"), new SelectOptionValue("Chinchilla"));
    private final MultiOption elements = new MultiOption("Elements", new MultiOptionValue("Self", true), new MultiOptionValue("Friends", true), new MultiOptionValue("Others", true));

    public CustomModel() {
        super("CustomModel", Category.RENDER);
        this.settings(this.mode, this.elements);
    }

    @Generated
    public SelectOption getMode() {
        return this.mode;
    }

    @Generated
    public MultiOption getElements() {
        return this.elements;
    }
}

