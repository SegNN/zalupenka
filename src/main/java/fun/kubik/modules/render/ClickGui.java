package fun.kubik.modules.render;

import fun.kubik.Load;
import fun.kubik.managers.client.ClientManagers;
import fun.kubik.managers.module.Module;
import fun.kubik.managers.module.main.Category;
import fun.kubik.managers.module.option.main.MultiOption;
import fun.kubik.managers.module.option.main.MultiOptionValue;
import fun.kubik.managers.module.option.main.SelectOption;
import fun.kubik.managers.module.option.main.SelectOptionValue;
import fun.kubik.managers.module.option.main.SliderOption;

public class ClickGui extends Module {
    public final MultiOption better = new MultiOption("Betters",
            new MultiOptionValue("Darkening Background", true),
            new MultiOptionValue("Colorful Background", true));
    public final SelectOption size = new SelectOption("Size", 0, new SelectOptionValue("Big"), new SelectOptionValue("Small"));
    public final SelectOption panelDesign = new SelectOption("Panel Design", 0, new SelectOptionValue("Standard"), new SelectOptionValue("Transparent"));
    public final SliderOption compression = new SliderOption("Blur Compression", 1.0f, 1.0f, 8.0f).visible(() -> this.panelDesign.getSelected("Transparent")).increment(1.0f);

    public ClickGui() {
        super("ClickGui", Category.RENDER);
        this.setCurrentKey(344);
        this.settings(this.better, this.size, this.panelDesign, this.compression);
    }

    @Override
    public void toggle() {
        if (!this.isToggled() && ClickGui.mc.currentScreen != Load.getInstance().getUiScreen() && !ClientManagers.isUnHook()) {
            mc.displayGuiScreen(Load.getInstance().getUiScreen());
        }
    }
}