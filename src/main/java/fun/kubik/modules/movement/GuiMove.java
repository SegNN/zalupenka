/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.modules.movement;

import fun.kubik.events.api.EventHook;
import fun.kubik.events.main.EventUpdate;
import fun.kubik.managers.module.Module;
import fun.kubik.managers.module.main.Category;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.EditSignScreen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;

public class GuiMove
extends Module {
    public boolean update = true;

    public GuiMove() {
        super("GuiMove", Category.MOVEMENT);
    }

    @EventHook
    public void update(EventUpdate eventUpdate) {
        if (GuiMove.mc.player != null) {
            KeyBinding[] pressedKeys = new KeyBinding[]{GuiMove.mc.gameSettings.keyBindForward, GuiMove.mc.gameSettings.keyBindBack, GuiMove.mc.gameSettings.keyBindLeft, GuiMove.mc.gameSettings.keyBindRight, GuiMove.mc.gameSettings.keyBindJump};
            if (GuiMove.mc.currentScreen instanceof ChatScreen || GuiMove.mc.currentScreen instanceof EditSignScreen) {
                return;
            }
            if (this.update) {
                for (KeyBinding keyBinding : pressedKeys) {
                    boolean isKeyPressed = InputMappings.isKeyDown(mc.getMainWindow().getHandle(), keyBinding.getDefault().getKeyCode());
                    keyBinding.setPressed(isKeyPressed);
                }
            }
        }
    }
}

