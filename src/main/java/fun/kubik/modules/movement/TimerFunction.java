package fun.kubik.modules.movement;

import fun.kubik.events.api.EventHook;
import fun.kubik.events.main.EventUpdate;
import fun.kubik.managers.module.Module;
import fun.kubik.managers.module.main.Category;
import fun.kubik.managers.module.option.main.SelectOption;
import fun.kubik.managers.module.option.main.SelectOptionValue;
import fun.kubik.managers.module.option.main.SliderOption;
import net.minecraft.client.Minecraft;

public class TimerFunction extends Module {
    private final SelectOption mode = new SelectOption("Mode", 0, new SelectOptionValue("Matrix"), new SelectOptionValue("Grim"));
    private final SliderOption timerAmount = new SliderOption("Speed", 2.0f, 1.0f, 10.0f).increment(0.05f);
    private final Minecraft mc = Minecraft.getInstance();

    public TimerFunction() {
        super("Timer", Category.MOVEMENT);
        this.settings(this.mode, this.timerAmount);
    }

    @EventHook
    public void update(EventUpdate event) {
        if (mc.player == null || mc.world == null) return;
        String selected = ((SelectOptionValue) mode.getValue()).getName();
        if ("Grim".equals(selected)) {
            // Grim logic
            mc.getTimer().timerSpeed = timerAmount.getValue().floatValue();
        } else {
            // Matrix logic
            mc.getTimer().timerSpeed = timerAmount.getValue().floatValue();
        }
    }

    @Override
    public void onDisabled() {
        mc.getTimer().timerSpeed = 1.0f;
        super.onDisabled();
    }

    @Override
    public void onEnabled() {
        mc.getTimer().timerSpeed = 1.0f;
        super.onEnabled();
    }
}