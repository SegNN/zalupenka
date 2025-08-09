/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.modules.misc;

import fun.kubik.Load;
import fun.kubik.events.api.EventHook;
import fun.kubik.events.main.input.EventInput;
import fun.kubik.managers.module.Module;
import fun.kubik.managers.module.main.Category;
import fun.kubik.managers.module.option.main.BindOption;
import fun.kubik.managers.module.option.main.MultiOption;
import fun.kubik.managers.module.option.main.MultiOptionValue;

public class AutoBuy
extends Module {
    public final MultiOption better = new MultiOption("Betters", new MultiOptionValue("Darkening Background", true), new MultiOptionValue("Colorful Background", true));
    public BindOption openKey = new BindOption("Open", -1);

    public AutoBuy() {
        super("AutoBuyTest", Category.MISC);
        this.settings(this.better, this.openKey);
    }

    @EventHook
    public void key(EventInput eventInput) {
        if (this.openKey.getKey() == eventInput.getKey()) {
            mc.displayGuiScreen(Load.getInstance().getBuyScreen());
        }
    }
}

