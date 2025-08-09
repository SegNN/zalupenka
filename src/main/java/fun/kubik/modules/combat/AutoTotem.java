/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.modules.combat;

import fun.kubik.Load;
import fun.kubik.events.api.EventHook;
import fun.kubik.events.main.EventUpdate;
import fun.kubik.helpers.module.swap.SwapHelpers;
import fun.kubik.managers.module.Module;
import fun.kubik.managers.module.main.Category;
import fun.kubik.managers.module.option.main.CheckboxOption;
import fun.kubik.managers.module.option.main.MultiOption;
import fun.kubik.managers.module.option.main.MultiOptionValue;
import fun.kubik.managers.module.option.main.SelectOption;
import fun.kubik.managers.module.option.main.SelectOptionValue;
import fun.kubik.managers.module.option.main.SliderOption;
import fun.kubik.modules.movement.GuiMove;
import fun.kubik.utils.player.MoveUtils;
import fun.kubik.utils.time.TimerUtils;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EnderCrystalEntity;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.item.AirItem;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.Items;
import net.minecraft.item.SkullItem;
import net.minecraft.network.play.client.CCloseWindowPacket;
import net.minecraft.network.play.client.CHeldItemChangePacket;
import net.minecraft.network.play.client.CPlayerDiggingPacket;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import ru.kotopushka.j2c.sdk.annotations.NativeInclude;

public class AutoTotem
extends Module {
    private final SelectOption mode = new SelectOption("Mode", 0, new SelectOptionValue("ReallyWorld"), new SelectOptionValue("LegendsGrief"), new SelectOptionValue("FunTime"), new SelectOptionValue("Grim"));
    private final MultiOption options = new MultiOption("Options", new MultiOptionValue("Elytra Health", true), new MultiOptionValue("Ender Crystal", true));
    private final SliderOption health = new SliderOption("Health", 5.0f, 0.0f, 20.0f).increment(0.5f);
    private final SliderOption elytraHealth = new SliderOption("Elytra Health", 9.0f, 0.0f, 20.0f).visible(() -> this.options.getSelected("Elytra Health"));
    private final SliderOption crystalDistance = new SliderOption("Crystal Distance", 6.0f, 1.0f, 10.0f).increment(0.5f).visible(() -> this.options.getSelected("Ender Crystal"));
    private final SliderOption delay = new SliderOption("Delay", 200.0f, 0.0f, 1000.0f).increment(50.0f).visible(() -> this.mode.getSelected("FunTime"));
    private final CheckboxOption noBall = new CheckboxOption("No Swap Shar", true);
    private final SwapHelpers swap = new SwapHelpers();
    private final TimerUtils timer = new TimerUtils();
    int oldSlot = -1;

    public AutoTotem() {
        super("AutoTotem", Category.COMBAT);
        this.settings(this.mode, this.options, this.health, this.elytraHealth, this.crystalDistance, this.delay, this.noBall);
    }

    @EventHook
    public void update(EventUpdate eventUpdate) {
        if (this.mode.getSelected("FunTime")) {
            this.funTime();
        }
        if (this.mode.getSelected("LegendsGrief")) {
            this.legendsGrief();
        }
        if (this.mode.getSelected("ReallyWorld")) {
            this.reallyWorld();
        }
        if (this.mode.getSelected("Grim")) {
            this.grim();
        }
    }

    @NativeInclude
    private void legendsGrief() {
        boolean totemInHand;
        int slot = this.swap.find(Items.TOTEM_OF_UNDYING);
        boolean bl = totemInHand = AutoTotem.mc.player.getHeldItemMainhand().getItem().equals(Items.TOTEM_OF_UNDYING) || AutoTotem.mc.player.getHeldItemOffhand().getItem().equals(Items.TOTEM_OF_UNDYING);
        if (this.canSwap()) {
            if (slot >= 0 && !totemInHand) {
                KeyBinding[] pressedKeys;
                for (KeyBinding keyBinding : pressedKeys = new KeyBinding[]{AutoTotem.mc.gameSettings.keyBindForward, AutoTotem.mc.gameSettings.keyBindBack, AutoTotem.mc.gameSettings.keyBindLeft, AutoTotem.mc.gameSettings.keyBindRight, AutoTotem.mc.gameSettings.keyBindJump, AutoTotem.mc.gameSettings.keyBindSprint}) {
                    keyBinding.setPressed(false);
                    ((GuiMove)Load.getInstance().getHooks().getModuleManagers().findClass(GuiMove.class)).update = false;
                }
                if (!MoveUtils.isMoving()) {
                    AutoTotem.mc.playerController.windowClick(0, slot, 40, ClickType.SWAP, AutoTotem.mc.player);
                    if (AutoTotem.mc.currentScreen == null) {
                        AutoTotem.mc.player.connection.sendPacket(new CCloseWindowPacket());
                        for (KeyBinding keyBinding : pressedKeys) {
                            boolean isKeyPressed = InputMappings.isKeyDown(mc.getMainWindow().getHandle(), keyBinding.getDefault().getKeyCode());
                            keyBinding.setPressed(isKeyPressed);
                        }
                    }
                    ((GuiMove)Load.getInstance().getHooks().getModuleManagers().findClass(GuiMove.class)).update = true;
                    this.oldSlot = slot;
                }
            }
        } else if (this.oldSlot != -1) {
            KeyBinding[] pressedKeys;
            for (KeyBinding keyBinding : pressedKeys = new KeyBinding[]{AutoTotem.mc.gameSettings.keyBindForward, AutoTotem.mc.gameSettings.keyBindBack, AutoTotem.mc.gameSettings.keyBindLeft, AutoTotem.mc.gameSettings.keyBindRight, AutoTotem.mc.gameSettings.keyBindJump, AutoTotem.mc.gameSettings.keyBindSprint}) {
                keyBinding.setPressed(false);
                ((GuiMove)Load.getInstance().getHooks().getModuleManagers().findClass(GuiMove.class)).update = false;
            }
            if (!MoveUtils.isMoving()) {
                AutoTotem.mc.playerController.windowClick(0, this.oldSlot, 40, ClickType.SWAP, AutoTotem.mc.player);
                if (AutoTotem.mc.currentScreen == null) {
                    AutoTotem.mc.player.connection.sendPacket(new CCloseWindowPacket());
                    for (KeyBinding keyBinding : pressedKeys) {
                        boolean isKeyPressed = InputMappings.isKeyDown(mc.getMainWindow().getHandle(), keyBinding.getDefault().getKeyCode());
                        keyBinding.setPressed(isKeyPressed);
                    }
                }
                ((GuiMove)Load.getInstance().getHooks().getModuleManagers().findClass(GuiMove.class)).update = true;
                this.oldSlot = -1;
            }
        }
    }

    @NativeInclude
    private void funTime() {
        int slot = this.swap.find(Items.TOTEM_OF_UNDYING);
        boolean totemInHand = AutoTotem.mc.player.getHeldItemMainhand().getItem().equals(Items.TOTEM_OF_UNDYING) || AutoTotem.mc.player.getHeldItemOffhand().getItem().equals(Items.TOTEM_OF_UNDYING);
        boolean empty = AutoTotem.mc.player.inventory.getStackInSlot(8).getItem() instanceof AirItem;
        if (this.canSwap()) {
            if (slot >= 0 && !totemInHand) {
                if (!this.swap.haveHotBar(Items.TOTEM_OF_UNDYING)) {
                    KeyBinding[] pressedKeys;
                    for (KeyBinding keyBinding : pressedKeys = new KeyBinding[]{AutoTotem.mc.gameSettings.keyBindForward, AutoTotem.mc.gameSettings.keyBindBack, AutoTotem.mc.gameSettings.keyBindLeft, AutoTotem.mc.gameSettings.keyBindRight, AutoTotem.mc.gameSettings.keyBindJump, AutoTotem.mc.gameSettings.keyBindSprint}) {
                        keyBinding.setPressed(false);
                        ((GuiMove)Load.getInstance().getHooks().getModuleManagers().findClass(GuiMove.class)).update = false;
                    }
                    if (!MoveUtils.isMoving()) {
                        AutoTotem.mc.playerController.windowClick(0, slot, 8, ClickType.SWAP, AutoTotem.mc.player);
                        if (AutoTotem.mc.currentScreen == null) {
                            AutoTotem.mc.player.connection.sendPacket(new CCloseWindowPacket());
                            for (KeyBinding keyBinding : pressedKeys) {
                                boolean isKeyPressed = InputMappings.isKeyDown(mc.getMainWindow().getHandle(), keyBinding.getDefault().getKeyCode());
                                keyBinding.setPressed(isKeyPressed);
                            }
                        }
                        ((GuiMove)Load.getInstance().getHooks().getModuleManagers().findClass(GuiMove.class)).update = true;
                    }
                } else {
                    if (!this.timer.hasTimeElapsed(((Float)this.delay.getValue()).longValue())) {
                        return;
                    }
                    if (slot <= 44 && slot >= 36) {
                        AutoTotem.mc.player.connection.sendPacket(new CHeldItemChangePacket(slot - 36));
                    }
                    AutoTotem.mc.player.connection.sendPacket(new CPlayerDiggingPacket(CPlayerDiggingPacket.Action.SWAP_ITEM_WITH_OFFHAND, BlockPos.ZERO, Direction.DOWN));
                    AutoTotem.mc.player.connection.sendPacket(new CHeldItemChangePacket(AutoTotem.mc.player.inventory.currentItem));
                    if (this.oldSlot == -1) {
                        this.oldSlot = slot;
                    }
                    this.timer.reset();
                }
            }
        } else if (this.oldSlot != -1) {
            if (this.oldSlot <= 44 && this.oldSlot >= 36) {
                AutoTotem.mc.player.connection.sendPacket(new CHeldItemChangePacket(this.oldSlot - 36));
            }
            AutoTotem.mc.player.connection.sendPacket(new CPlayerDiggingPacket(CPlayerDiggingPacket.Action.SWAP_ITEM_WITH_OFFHAND, BlockPos.ZERO, Direction.DOWN));
            AutoTotem.mc.player.connection.sendPacket(new CHeldItemChangePacket(AutoTotem.mc.player.inventory.currentItem));
            this.oldSlot = -1;
        }
    }

    @NativeInclude
    private void grim() {
        int slot = this.swap.find(Items.TOTEM_OF_UNDYING);
        boolean totemInHand = AutoTotem.mc.player.getHeldItemMainhand().getItem().equals(Items.TOTEM_OF_UNDYING) || AutoTotem.mc.player.getHeldItemOffhand().getItem().equals(Items.TOTEM_OF_UNDYING);
        boolean empty = AutoTotem.mc.player.getHeldItemOffhand().getItem() instanceof AirItem;
        if (this.canSwap()) {
            if (slot >= 0 && !totemInHand) {
                AutoTotem.mc.playerController.windowClick(0, slot, 0, ClickType.PICKUP, AutoTotem.mc.player);
                AutoTotem.mc.playerController.windowClick(0, 45, 0, ClickType.PICKUP, AutoTotem.mc.player);
                if (!empty) {
                    AutoTotem.mc.playerController.windowClick(0, slot, 0, ClickType.PICKUP, AutoTotem.mc.player);
                    if (this.oldSlot == -1) {
                        this.oldSlot = slot;
                    }
                }
            }
        } else if (this.oldSlot != -1) {
            AutoTotem.mc.playerController.windowClick(0, this.oldSlot, 0, ClickType.PICKUP, AutoTotem.mc.player);
            AutoTotem.mc.playerController.windowClick(0, 45, 0, ClickType.PICKUP, AutoTotem.mc.player);
            if (!empty) {
                AutoTotem.mc.playerController.windowClick(0, this.oldSlot, 0, ClickType.PICKUP, AutoTotem.mc.player);
            }
            this.oldSlot = -1;
        }
    }

    @NativeInclude
    private void reallyWorld() {
        int slot = this.swap.find(Items.TOTEM_OF_UNDYING);
        boolean totemInHand = AutoTotem.mc.player.getHeldItemMainhand().getItem().equals(Items.TOTEM_OF_UNDYING) || AutoTotem.mc.player.getHeldItemOffhand().getItem().equals(Items.TOTEM_OF_UNDYING);
        boolean empty = AutoTotem.mc.player.getHeldItemOffhand().getItem() instanceof AirItem;
        if (this.canSwap()) {
            if (slot >= 0 && !AutoTotem.mc.player.getHeldItemOffhand().getItem().equals(Items.TOTEM_OF_UNDYING)) {
                AutoTotem.mc.playerController.windowClick(0, slot, 40, ClickType.SWAP, AutoTotem.mc.player);
                if (!empty && this.oldSlot == -1) {
                    this.oldSlot = slot;
                }
            }
        } else if (this.oldSlot != -1) {
            AutoTotem.mc.playerController.windowClick(0, this.oldSlot, 40, ClickType.SWAP, AutoTotem.mc.player);
            this.oldSlot = -1;
        }
    }

    private boolean canSwap() {
        boolean flag1 = this.elytraCheck();
        boolean flag2 = this.checkCrystal();
        boolean flag3 = AutoTotem.mc.player.getHealth() + this.getAbsorption() <= ((Float)this.health.getValue()).floatValue();
        return flag1 || flag2 || flag3;
    }

    private boolean elytraCheck() {
        boolean elytra = AutoTotem.mc.player.inventory.armorInventory.get(2).getItem() instanceof ElytraItem && this.options.getSelected("Elytra Health");
        return elytra && this.checkHealth();
    }

    private boolean checkHealth() {
        return AutoTotem.mc.player.getHealth() + this.getAbsorption() <= ((Float)this.elytraHealth.getValue()).floatValue();
    }

    private boolean checkCrystal() {
        for (Entity entity : AutoTotem.mc.world.getAllEntities()) {
            if (!(entity instanceof EnderCrystalEntity) || !(AutoTotem.mc.player.getDistance(entity) <= ((Float)this.crystalDistance.getValue()).floatValue()) || !this.options.getSelected("Ender Crystal")) continue;
            return !(AutoTotem.mc.player.getHeldItemOffhand().getItem() instanceof SkullItem) || (Boolean)this.noBall.getValue() == false;
        }
        return false;
    }

    private float getAbsorption() {
        return 0.0f;
    }

    private BlockPos getBlock(float distance, Block block) {
        return this.getSphere(this.getPlayerPosLocal(), distance, 6, false, true, 0).stream().filter(position -> AutoTotem.mc.world.getBlockState((BlockPos)position).getBlock() == block).min(Comparator.comparing(blockPos -> this.getDistanceOfEntityToBlock(AutoTotem.mc.player, (BlockPos)blockPos))).orElse(null);
    }

    private BlockPos getBlock(float distance) {
        return this.getSphere(this.getPlayerPosLocal(), distance, 6, false, true, 0).stream().filter(position -> AutoTotem.mc.world.getBlockState((BlockPos)position).getBlock() != Blocks.AIR).min(Comparator.comparing(blockPos -> this.getDistanceOfEntityToBlock(AutoTotem.mc.player, (BlockPos)blockPos))).orElse(null);
    }

    private BlockPos getBlockFlat(int distance) {
        BlockPos vec = this.getPlayerPosLocal().add(0, -1, 0);
        for (int x = vec.getX() - distance; x <= vec.getX() + distance; ++x) {
            for (int z = vec.getX() - distance; z <= vec.getZ() + distance; ++z) {
                if (AutoTotem.mc.world.getBlockState(new BlockPos(x, vec.getY(), z)).getBlock() == Blocks.AIR) continue;
                return new BlockPos(x, vec.getY(), z);
            }
        }
        return vec;
    }

    private List<BlockPos> getSphere(BlockPos blockPos, float n, int n2, boolean b, boolean b2, int n3) {
        ArrayList<BlockPos> list = new ArrayList<BlockPos>();
        int x = blockPos.getX();
        int y = blockPos.getY();
        int z = blockPos.getZ();
        int n4 = x - (int)n;
        while ((float)n4 <= (float)x + n) {
            int n5 = z - (int)n;
            while ((float)n5 <= (float)z + n) {
                int n6 = b2 ? y - (int)n : y;
                while (true) {
                    float f = n6;
                    float f2 = b2 ? (float)y + n : (float)(y + n2);
                    if (!(f < f2)) break;
                    double n7 = (x - n4) * (x - n4) + (z - n5) * (z - n5) + (b2 ? (y - n6) * (y - n6) : 0);
                    if (n7 < (double)(n * n) && (!b || n7 >= (double)((n - 1.0f) * (n - 1.0f)))) {
                        list.add(new BlockPos(n4, n6 + n3, n5));
                    }
                    ++n6;
                }
                ++n5;
            }
            ++n4;
        }
        return list;
    }

    private BlockPos getPlayerPosLocal() {
        if (AutoTotem.mc.player == null) {
            return BlockPos.ZERO;
        }
        return new BlockPos(Math.floor(AutoTotem.mc.player.getPosX()), Math.floor(AutoTotem.mc.player.getPosY()), Math.floor(AutoTotem.mc.player.getPosZ()));
    }

    private double getDistanceOfEntityToBlock(Entity entity, BlockPos blockPos) {
        return this.getDistance(entity.getPosX(), entity.getPosY(), entity.getPosZ(), blockPos.getX(), blockPos.getY(), blockPos.getZ());
    }

    private double getDistance(double n, double n2, double n3, double n4, double n5, double n6) {
        double n7 = n - n4;
        double n8 = n2 - n5;
        double n9 = n3 - n6;
        return MathHelper.sqrt(n7 * n7 + n8 * n8 + n9 * n9);
    }
}

