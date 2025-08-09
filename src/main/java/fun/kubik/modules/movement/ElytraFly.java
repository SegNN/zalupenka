/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.modules.movement;

import fun.kubik.Load;
import fun.kubik.events.api.EventHook;
import fun.kubik.events.main.EventUpdate;
import fun.kubik.managers.module.Module;
import fun.kubik.managers.module.main.Category;
import fun.kubik.managers.module.option.main.CheckboxOption;
import fun.kubik.managers.module.option.main.SelectOption;
import fun.kubik.managers.module.option.main.SelectOptionValue;
import fun.kubik.managers.module.option.main.SliderOption;
import fun.kubik.modules.combat.Aura;
import fun.kubik.utils.time.TimerUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.play.client.CEntityActionPacket;
import net.minecraft.network.play.client.CHeldItemChangePacket;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.network.play.client.CPlayerTryUseItemPacket;
import net.minecraft.util.Hand;
import ru.kotopushka.j2c.sdk.annotations.NativeInclude;

public class ElytraFly
extends Module {
    private final TimerUtils timerUtil = new TimerUtils();
    private final TimerUtils timerUtil1 = new TimerUtils();
    private final TimerUtils timerUtil2 = new TimerUtils();
    private final SliderOption timerStartFireWork = new SliderOption("\u0417\u0430\u0434\u0435\u0440\u0436\u043a\u0430 \u0444\u0435\u0439\u0432\u0435\u0440\u043a\u0430", 400.0f, 50.0f, 1500.0f).increment(1.0f);
    private final CheckboxOption tpssync = new CheckboxOption("\u0421\u0438\u043d\u0445\u0440\u043e\u043d\u0438\u0437\u0430\u0446\u0438\u044f \u0441 \u0442\u043f\u0441", false);
    public final SelectOption mode = new SelectOption("Mode", 0, new SelectOptionValue("\u0421\u0442\u0430\u0440\u044b\u0439"), new SelectOptionValue("\u0420\u0438\u043b\u0438\u0412\u0438\u043b\u0438"));
    int oldItem = -1;
    public float spee = 0.0f;
    public static long lastStartFalling;

    public ElytraFly() {
        super("ElytraFly", Category.MOVEMENT);
        this.settings(this.mode, this.timerStartFireWork, this.tpssync);
    }

    @EventHook
    public void weslogovno(EventUpdate eventUpdate) {
        if (this.oldItem != -1 && ElytraFly.mc.player.inventory.armorInventory.get(2).getItem() == Items.ELYTRA && ElytraFly.mc.player.inventory.getStackInSlot(this.oldItem).getItem() instanceof ArmorItem && this.timerUtil2.hasTimeElapsed(550L)) {
            Minecraft var10005 = mc;
            ElytraFly.mc.playerController.windowClick(0, 6, this.oldItem, ClickType.SWAP, ElytraFly.mc.player);
            this.oldItem = -1;
            this.timerUtil2.reset();
            this.toggle();
            return;
        }
    }

    @EventHook
    public void update(EventUpdate eventUpdate) {
        if (ElytraFly.getItemSlot(Items.FIREWORK_ROCKET) == -1) {
            return;
        }
        int timeSwap = 610;
        if (((Boolean)this.tpssync.getValue()).booleanValue()) {
            timeSwap = 610;
        } else if (this.mode.getSelected("\u0421\u0442\u0430\u0440\u044b\u0439")) {
            timeSwap = 185;
        }
        for (int i = 0; i < 9; ++i) {
            if (ElytraFly.mc.player.inventory.getStackInSlot(i).getItem() != Items.ELYTRA || ElytraFly.mc.player.isOnGround() || ElytraFly.mc.player.isInWater() || ElytraFly.mc.player.isInLava() || ElytraFly.mc.player.isElytraFlying()) continue;
            if (this.timerUtil1.hasTimeElapsed(timeSwap)) {
                this.timerUtil2.reset();
                Minecraft var10005 = mc;
                ElytraFly.mc.playerController.windowClick(0, 6, i, ClickType.SWAP, ElytraFly.mc.player);
                ElytraFly.mc.player.startFallFlying();
                Minecraft var10003 = mc;
                ElytraFly.mc.player.connection.sendPacket(new CEntityActionPacket(ElytraFly.mc.player, CEntityActionPacket.Action.START_FALL_FLYING));
                var10005 = mc;
                ElytraFly.mc.playerController.windowClick(0, 6, i, ClickType.SWAP, ElytraFly.mc.player);
                this.oldItem = i;
                this.timerUtil1.reset();
            }
            if (!this.timerUtil.hasTimeElapsed(((Float)this.timerStartFireWork.getValue()).intValue()) || !ElytraFly.mc.player.isElytraFlying()) continue;
            if (ElytraFly.mc.player.isHandActive()) {
                int fireworkSlot = ElytraFly.getItemSlot(Items.FIREWORK_ROCKET);
                if (fireworkSlot != -1) {
                    ElytraFly.mc.playerController.windowClick(0, fireworkSlot, 40, ClickType.SWAP, ElytraFly.mc.player);
                    ElytraFly.mc.playerController.processRightClick(ElytraFly.mc.player, ElytraFly.mc.world, Hand.OFF_HAND);
                    ElytraFly.mc.playerController.windowClick(0, fireworkSlot, 40, ClickType.SWAP, ElytraFly.mc.player);
                }
            } else {
                ElytraFly.inventorySwapClick(Items.FIREWORK_ROCKET, false);
            }
            this.timerUtil.reset();
        }
    }

    @Override
    @NativeInclude
    public void onDisabled() {
        if (this.oldItem != -1) {
            if (ElytraFly.mc.player.inventory.armorInventory.get(2).getItem() == Items.ELYTRA && ElytraFly.mc.player.inventory.getStackInSlot(this.oldItem).getItem() instanceof ArmorItem) {
                Minecraft var10005 = mc;
                ElytraFly.mc.playerController.windowClick(0, 6, this.oldItem, ClickType.SWAP, ElytraFly.mc.player);
            }
            this.oldItem = -1;
        }
        ElytraFly.mc.gameSettings.keyBindSneak.setPressed(false);
    }

    public static int getItemSlot(Item input) {
        for (ItemStack stack : ElytraFly.mc.player.getArmorInventoryList()) {
            if (stack.getItem() != input) continue;
            return -2;
        }
        int slot = -1;
        for (int i = 0; i < 36; ++i) {
            ItemStack s = ElytraFly.mc.player.inventory.getStackInSlot(i);
            if (s.getItem() != input) continue;
            slot = i;
            break;
        }
        if (slot < 9 && slot != -1) {
            slot += 36;
        }
        return slot;
    }

    @NativeInclude
    public static void inventorySwapClick(Item item, boolean rotation) {
        if (InventoryHelper.getItemIndex(item) != -1) {
            Aura var10000;
            int i;
            if (ElytraFly.doesHotbarHaveItem(item)) {
                for (i = 0; i < 9; ++i) {
                    if (ElytraFly.mc.player.inventory.getStackInSlot(i).getItem() != item) continue;
                    if (i != ElytraFly.mc.player.inventory.currentItem) {
                        ElytraFly.mc.player.connection.sendPacket(new CHeldItemChangePacket(i));
                    }
                    if (rotation) {
                        var10000 = (Aura)Load.getInstance().getHooks().getModuleManagers().findClass(Aura.class);
                        if (((Aura)Load.getInstance().getHooks().getModuleManagers().findClass(Aura.class)).getTarget() != null) {
                            ElytraFly.mc.player.connection.sendPacket(new CPlayerPacket.RotationPacket(ElytraFly.mc.player.rotationYaw, ElytraFly.mc.player.rotationPitch, false));
                        }
                    }
                    ElytraFly.mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
                    if (i == ElytraFly.mc.player.inventory.currentItem) break;
                    ElytraFly.mc.player.connection.sendPacket(new CHeldItemChangePacket(ElytraFly.mc.player.inventory.currentItem));
                    break;
                }
            }
            if (!ElytraFly.doesHotbarHaveItem(item)) {
                for (i = 0; i < 36; ++i) {
                    if (ElytraFly.mc.player.inventory.getStackInSlot(i).getItem() != item) continue;
                    ElytraFly.mc.playerController.windowClick(0, i, ElytraFly.mc.player.inventory.currentItem % 8 + 1, ClickType.SWAP, ElytraFly.mc.player);
                    ElytraFly.mc.player.connection.sendPacket(new CHeldItemChangePacket(ElytraFly.mc.player.inventory.currentItem % 8 + 1));
                    if (rotation) {
                        var10000 = (Aura)Load.getInstance().getHooks().getModuleManagers().findClass(Aura.class);
                        if (((Aura)Load.getInstance().getHooks().getModuleManagers().findClass(Aura.class)).getTarget() != null) {
                            ElytraFly.mc.player.connection.sendPacket(new CPlayerPacket.RotationPacket(ElytraFly.mc.player.rotationYaw, ElytraFly.mc.player.rotationPitch, false));
                        }
                    }
                    ElytraFly.mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
                    ElytraFly.mc.player.connection.sendPacket(new CHeldItemChangePacket(ElytraFly.mc.player.inventory.currentItem));
                    ElytraFly.mc.playerController.windowClick(0, i, ElytraFly.mc.player.inventory.currentItem % 8 + 1, ClickType.SWAP, ElytraFly.mc.player);
                    break;
                }
            }
        }
    }

    public static boolean doesHotbarHaveItem(Item item) {
        for (int i = 0; i < 9; ++i) {
            ElytraFly.mc.player.inventory.getStackInSlot(i);
            if (ElytraFly.mc.player.inventory.getStackInSlot(i).getItem() != item) continue;
            return true;
        }
        return false;
    }
}

