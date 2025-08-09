/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.modules.combat;

import fun.kubik.Load;
import fun.kubik.events.api.EventHook;
import fun.kubik.events.main.EventUpdate;
import fun.kubik.events.main.input.EventInput;
import fun.kubik.helpers.module.swap.SwapHelpers;
import fun.kubik.managers.module.Module;
import fun.kubik.managers.module.main.Category;
import fun.kubik.managers.module.option.main.BindOption;
import fun.kubik.managers.module.option.main.SelectOption;
import fun.kubik.managers.module.option.main.SelectOptionValue;
import fun.kubik.modules.movement.GuiMove;
import fun.kubik.utils.player.MoveUtils;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.network.play.client.CCloseWindowPacket;

public class AutoSwap
extends Module {
    private final SelectOption mode = new SelectOption("Mode", 0, new SelectOptionValue("ReallyWorld"), new SelectOptionValue("FunTime"));
    private final SelectOption item = new SelectOption("First Item", 0, new SelectOptionValue("Ball"), new SelectOptionValue("Golden Apple"), new SelectOptionValue("Shield"));
    private final SelectOption item2 = new SelectOption("Second Item", 0, new SelectOptionValue("Ball"), new SelectOptionValue("Golden Apple"), new SelectOptionValue("Shield"));
    private final BindOption bind = new BindOption("Key", -1);
    private final SwapHelpers swaps = new SwapHelpers();
    private boolean swap;
    private boolean hand;

    public AutoSwap() {
        super("AutoSwap", Category.COMBAT);
        this.settings(this.mode, this.item, this.item2, this.bind);
    }

    @EventHook
    public void update(EventUpdate event) {
        if (this.swap && this.hand) {
            if (this.item.getSelected("Ball")) {
                this.swap(Items.PLAYER_HEAD);
            }
            if (this.item.getSelected("Golden Apple")) {
                this.swap(Items.GOLDEN_APPLE);
            }
            if (this.item.getSelected("Shield")) {
                this.swap(Items.SHIELD);
            }
            this.hand = false;
        }
        if (this.swap) {
            if (this.item2.getSelected("Ball")) {
                this.swap(Items.PLAYER_HEAD);
            }
            if (this.item2.getSelected("Golden Apple")) {
                this.swap(Items.GOLDEN_APPLE);
            }
            if (this.item2.getSelected("Shield")) {
                this.swap(Items.SHIELD);
            }
            this.hand = true;
        }
    }

    @EventHook
    public void input(EventInput event) {
        this.swap = event.getKey() == this.bind.getKey();
    }

    private void swap(Item item) {
        int slot;
        if (this.mode.getSelected("ReallyWorld")) {
            slot = this.swaps.find(item);
            if (slot != -1) {
                AutoSwap.mc.playerController.windowClick(0, slot < 9 ? slot + 36 : slot, 40, ClickType.SWAP, AutoSwap.mc.player);
            }
            this.swap = false;
        }
        if (this.mode.getSelected("FunTime")) {
            KeyBinding[] pressedKeys;
            slot = this.swaps.find(item);
            for (KeyBinding keyBinding : pressedKeys = new KeyBinding[]{AutoSwap.mc.gameSettings.keyBindForward, AutoSwap.mc.gameSettings.keyBindBack, AutoSwap.mc.gameSettings.keyBindLeft, AutoSwap.mc.gameSettings.keyBindRight, AutoSwap.mc.gameSettings.keyBindJump, AutoSwap.mc.gameSettings.keyBindSprint}) {
                keyBinding.setPressed(false);
                ((GuiMove)Load.getInstance().getHooks().getModuleManagers().findClass(GuiMove.class)).update = false;
            }
            if (slot != -1 && !MoveUtils.isMoving()) {
                AutoSwap.mc.playerController.windowClick(0, slot < 9 ? slot + 36 : slot, 40, ClickType.SWAP, AutoSwap.mc.player);
                if (AutoSwap.mc.currentScreen == null) {
                    AutoSwap.mc.player.connection.sendPacket(new CCloseWindowPacket());
                    for (KeyBinding keyBinding : pressedKeys) {
                        boolean isKeyPressed = InputMappings.isKeyDown(mc.getMainWindow().getHandle(), keyBinding.getDefault().getKeyCode());
                        keyBinding.setPressed(isKeyPressed);
                    }
                }
                ((GuiMove)Load.getInstance().getHooks().getModuleManagers().findClass(GuiMove.class)).update = true;
                this.swap = false;
            }
        }
    }
}

