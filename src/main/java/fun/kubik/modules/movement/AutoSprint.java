/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.modules.movement;

import fun.kubik.Load;
import fun.kubik.events.api.EventHook;
import fun.kubik.events.main.EventUpdate;
import fun.kubik.helpers.module.aura.AuraHelpers;
import fun.kubik.managers.module.Module;
import fun.kubik.managers.module.main.Category;
import fun.kubik.managers.module.option.main.SelectOption;
import fun.kubik.managers.module.option.main.SelectOptionValue;
import fun.kubik.modules.combat.Aura;
import fun.kubik.modules.combat.TriggerBot;
import fun.kubik.utils.player.MoveUtils;
import net.minecraft.potion.Effects;
import ru.kotopushka.j2c.sdk.annotations.NativeInclude;

public class AutoSprint
extends Module {
    private final AuraHelpers auraHelpers = new AuraHelpers();
    public final SelectOption mode = new SelectOption("Mode", 0, new SelectOptionValue("Legit"), new SelectOptionValue("Rage"));

    public AutoSprint() {
        super("AutoSprint", Category.MOVEMENT);
        this.settings(this.mode);
    }

    @EventHook
    @NativeInclude
    public void update(EventUpdate eventUpdate) {
        Aura aura = (Aura)Load.getInstance().getHooks().getModuleManagers().findClass(Aura.class);
        TriggerBot triggerBot = (TriggerBot)Load.getInstance().getHooks().getModuleManagers().findClass(TriggerBot.class);
        if (this.mode.getSelected("Legit")) {
            boolean reset;
            boolean bl = reset = aura.isToggled() && aura.options.getSelected("Only Crits") || triggerBot.isToggled();
            if (reset) {
                if (this.auraHelpers.sprint()) {
                    AutoSprint.mc.gameSettings.keyBindSprint.setPressed(true);
                } else {
                    AutoSprint.mc.gameSettings.keyBindSprint.setPressed(false);
                    AutoSprint.mc.player.setSprinting(false);
                }
            } else {
                AutoSprint.mc.gameSettings.keyBindSprint.setPressed(true);
            }
        }
        if (this.mode.getSelected("Rage") && this.canSprint()) {
            AutoSprint.mc.player.setSprinting(MoveUtils.isMoving());
        }
    }

    public boolean canSprint() {
        return !AutoSprint.mc.player.isSneaking() && !AutoSprint.mc.player.collidedHorizontally && AutoSprint.mc.player.movementInput.moveForward > 0.0f && !AutoSprint.mc.player.isCrouching() && !AutoSprint.mc.player.isEating() && !AutoSprint.mc.player.isPotionActive(Effects.SLOWNESS) && !AutoSprint.mc.player.isPotionActive(Effects.BLINDNESS) && !AutoSprint.mc.player.isVisuallySwimming() && !AutoSprint.mc.player.isHandActive();
    }

    @Override
    public void onDisabled() {
        AutoSprint.mc.gameSettings.keyBindSprint.setPressed(false);
    }
}

