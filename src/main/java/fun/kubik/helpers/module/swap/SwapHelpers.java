/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.helpers.module.swap;

import fun.kubik.events.api.EventHook;
import fun.kubik.events.main.packet.EventReceivePacket;
import fun.kubik.helpers.interfaces.IFastAccess;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.UseAction;
import net.minecraft.network.play.server.SHeldItemChangePacket;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;

public class SwapHelpers
implements IFastAccess {
    public int find(int id) {
        int slot = -1;
        block0: for (int i = 0; i < 36; ++i) {
            for (EffectInstance potion : PotionUtils.getEffectsFromStack(SwapHelpers.mc.player.inventory.getStackInSlot(i))) {
                if (potion.getPotion() != Effect.get(id) || SwapHelpers.mc.player.inventory.getStackInSlot(i).getItem() != Items.SPLASH_POTION) continue;
                slot = i;
                continue block0;
            }
        }
        if (slot < 9 && slot != -1) {
            slot += 36;
        }
        return slot;
    }

    public int find(Item item) {
        int slot = -1;
        for (ItemStack stack : SwapHelpers.mc.player.getArmorInventoryList()) {
            if (stack.getItem() != item) continue;
            return -2;
        }
        for (int i = 0; i < 36; ++i) {
            ItemStack stack;
            stack = SwapHelpers.mc.player.inventory.getStackInSlot(i);
            if (stack.getItem() != item) continue;
            slot = i;
            break;
        }
        if (slot < 9 && slot != -1) {
            slot += 36;
        }
        return slot;
    }

    public int find(UseAction action) {
        int slot = -1;
        for (ItemStack stack : SwapHelpers.mc.player.getArmorInventoryList()) {
            if (stack.getUseAction() != action) continue;
            return -2;
        }
        for (int i = 0; i < 36; ++i) {
            ItemStack stack;
            stack = SwapHelpers.mc.player.inventory.getStackInSlot(i);
            if (stack.getUseAction() != action) continue;
            slot = i;
            break;
        }
        if (slot < 9 && slot != -1) {
            slot += 36;
        }
        return slot;
    }

    public int find() {
        RayTraceResult rayTraceResult = SwapHelpers.mc.objectMouseOver;
        if (rayTraceResult instanceof BlockRayTraceResult) {
            BlockRayTraceResult blockRayTraceResult = (BlockRayTraceResult)rayTraceResult;
            Block block = SwapHelpers.mc.world.getBlockState(blockRayTraceResult.getPos()).getBlock();
            int bestSlot = -1;
            float bestSpeed = 1.0f;
            for (int slot = 0; slot < 9; ++slot) {
                ItemStack stack = SwapHelpers.mc.player.inventory.getStackInSlot(slot);
                float speed = stack.getDestroySpeed(block.getDefaultState());
                if (!(speed > bestSpeed)) continue;
                bestSpeed = speed;
                bestSlot = slot;
            }
            return bestSlot;
        }
        return -1;
    }

    public int find(ItemStack itemStack) {
        int slot = -1;
        for (ItemStack stack : SwapHelpers.mc.player.getArmorInventoryList()) {
            if (stack != itemStack) continue;
            return -2;
        }
        for (int i = 0; i < 36; ++i) {
            ItemStack stack;
            stack = SwapHelpers.mc.player.inventory.getStackInSlot(i);
            if (stack != itemStack) continue;
            slot = i;
            break;
        }
        if (slot < 9 && slot != -1) {
            slot += 36;
        }
        return slot;
    }

    public boolean haveHotBar(Item item) {
        for (int i = 0; i < 9; ++i) {
            SwapHelpers.mc.player.inventory.getStackInSlot(i);
            if (SwapHelpers.mc.player.inventory.getStackInSlot(i).getItem() != item) continue;
            return true;
        }
        return false;
    }

    public boolean haveHotBar(int index) {
        return index >= 36 && index <= 44;
    }

    public int format(int slot) {
        return slot - 36;
    }

    public int find(String name) {
        int slot = -1;
        ContainerScreen containerScreen = (ContainerScreen)SwapHelpers.mc.currentScreen;
        for (int i = 0; i < ((Container)containerScreen.getContainer()).inventorySlots.size(); ++i) {
            String itemName = ((Container)containerScreen.getContainer()).inventorySlots.get(i).getStack().getDisplayName().getString();
            if (!itemName.contains(name)) continue;
            return i;
        }
        return slot;
    }

    public static class Hand3 {
        public static boolean isEnabled;
        private boolean isChangingItem;
        private int originalSlot = -1;

        @EventHook
        public void onEventPacket(EventReceivePacket eventPacket) {
            if (eventPacket.getPacket() instanceof SHeldItemChangePacket) {
                this.isChangingItem = true;
            }
        }

        public void handleItemChange(boolean resetItem) {
            if (this.isChangingItem && this.originalSlot != -1) {
                isEnabled = true;
                Minecraft var10000 = IFastAccess.mc;
                IFastAccess.mc.player.inventory.currentItem = this.originalSlot;
                if (resetItem) {
                    this.isChangingItem = false;
                    this.originalSlot = -1;
                    isEnabled = false;
                }
            }
        }

        public void setOriginalSlot(int slot) {
            this.originalSlot = slot;
        }
    }
}

