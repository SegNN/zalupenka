/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.modules.combat;

import fun.kubik.events.api.EventHook;
import fun.kubik.events.main.EventUpdate;
import fun.kubik.events.main.player.EventObsidianPlace;
import fun.kubik.events.main.player.EventSync;
import fun.kubik.managers.module.Module;
import fun.kubik.managers.module.main.Category;
import fun.kubik.managers.module.option.api.Option;
import fun.kubik.managers.module.option.main.CheckboxOption;
import fun.kubik.utils.math.MathUtils;
import fun.kubik.utils.time.TimerUtils;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EnderCrystalEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.SwordItem;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import ru.kotopushka.j2c.sdk.annotations.NativeInclude;

public class CrystalAura
extends Module {
    private final CheckboxOption safeYourSelf = new CheckboxOption("\u041d\u0435 \u0432\u0437\u0440\u044b\u0432\u0430\u0442\u044c \u041f\u0440\u0435\u0434\u043c\u0435\u0442\u044b", false);
    private Entity crystalEntity = null;
    private BlockPos obsidianPos = null;
    private int oldCurrentSlot = -1;
    private Vector2f rotationVector = new Vector2f(0.0f, 0.0f);
    TimerUtils attackStopWatch = new TimerUtils();
    int bestSlot = -1;
    int oldSlot = -1;

    public CrystalAura() {
        super("CrystalAura", Category.COMBAT);
        this.settings(new Option[0]);
    }

    @EventHook
    public void onObsidianPlace(EventObsidianPlace e) {
        boolean slotNotNull;
        BlockPos obsidianPos = e.getPos();
        boolean isOffHand = CrystalAura.mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL;
        int slotInInventory = CrystalAura.getSlotInInventoryOrHotbar(Items.END_CRYSTAL, false);
        int slotInHotBar = CrystalAura.getSlotInInventoryOrHotbar(Items.END_CRYSTAL, true);
        this.bestSlot = this.findBestSlotInHotBar();
        boolean bl = slotNotNull = CrystalAura.mc.player.inventory.getStackInSlot(this.bestSlot).getItem() != Items.AIR;
        if (isOffHand && obsidianPos != null) {
            this.setAndUseCrystal(this.bestSlot, obsidianPos);
            this.obsidianPos = obsidianPos;
        }
        if (slotInHotBar == -1 && slotInInventory != -1 && this.bestSlot != -1) {
            CrystalAura.moveItem(slotInInventory, this.bestSlot + 36, slotNotNull);
            if (slotNotNull && this.oldSlot == -1) {
                this.oldSlot = slotInInventory;
            }
            if (obsidianPos != null) {
                this.oldCurrentSlot = CrystalAura.mc.player.inventory.currentItem;
                this.setAndUseCrystal(this.bestSlot, obsidianPos);
                CrystalAura.mc.player.inventory.currentItem = this.oldCurrentSlot;
                this.obsidianPos = obsidianPos;
            }
            CrystalAura.mc.playerController.windowClick(0, this.oldSlot, 0, ClickType.PICKUP, CrystalAura.mc.player);
            CrystalAura.mc.playerController.windowClick(0, this.bestSlot + 36, 0, ClickType.PICKUP, CrystalAura.mc.player);
            CrystalAura.mc.playerController.windowClickFixed(0, this.oldSlot, 0, ClickType.PICKUP, CrystalAura.mc.player, 250);
        } else if (slotInHotBar != -1 && obsidianPos != null) {
            this.oldCurrentSlot = CrystalAura.mc.player.inventory.currentItem;
            this.setAndUseCrystal(slotInHotBar, obsidianPos);
            CrystalAura.mc.player.inventory.currentItem = this.oldCurrentSlot;
            this.obsidianPos = obsidianPos;
        }
    }

    public int findBestSlotInHotBar() {
        int emptySlot = this.findEmptySlot();
        return emptySlot != -1 ? emptySlot : this.findNonSwordSlot();
    }

    private int findNonSwordSlot() {
        for (int i = 0; i < 9; ++i) {
            if (CrystalAura.mc.player.inventory.getStackInSlot(i).getItem() instanceof SwordItem || CrystalAura.mc.player.inventory.getStackInSlot(i).getItem() instanceof ElytraItem || CrystalAura.mc.player.inventory.currentItem == i) continue;
            return i;
        }
        return -1;
    }

    private int findEmptySlot() {
        for (int i = 0; i < 9; ++i) {
            if (!CrystalAura.mc.player.inventory.getStackInSlot(i).isEmpty() || CrystalAura.mc.player.inventory.currentItem == i) continue;
            return i;
        }
        return -1;
    }

    public static void moveItem(int from, int to, boolean air) {
        if (from != to) {
            CrystalAura.pickupItem(from, 0);
            CrystalAura.pickupItem(to, 0);
            if (air) {
                CrystalAura.pickupItem(from, 0);
            }
        }
    }

    public static void pickupItem(int slot, int button) {
        CrystalAura.mc.playerController.windowClick(0, slot, button, ClickType.PICKUP, CrystalAura.mc.player);
    }

    @EventHook
    private void onUpdate(EventUpdate e) {
        if (this.obsidianPos != null) {
            this.findEnderCrystals(this.obsidianPos).forEach(this::attackCrystal);
        }
        if (this.crystalEntity != null && !this.crystalEntity.isAlive()) {
            this.reset();
        }
    }

    @EventHook
    private void onMotion(EventSync e) {
        if (this.isValid(this.crystalEntity)) {
            this.rotationVector = MathUtils.rotationToEntity(this.crystalEntity);
            e.setYaw(this.rotationVector.x);
            e.setPitch(this.rotationVector.y);
            CrystalAura.mc.player.renderYawOffset = this.rotationVector.x;
            CrystalAura.mc.player.rotationYawHead = this.rotationVector.x;
            CrystalAura.mc.player.rotationPitchHead = this.rotationVector.y;
        }
    }

    @Override
    public void onDisabled() {
        this.reset();
    }

    @NativeInclude
    private void attackCrystal(Entity entity) {
        if (this.isValid(entity) && CrystalAura.mc.player.getCooledAttackStrength(1.0f) >= 1.0f && this.attackStopWatch.hasTimeElapsed()) {
            this.attackStopWatch.setLastMS(500L);
            CrystalAura.mc.playerController.attackEntity(CrystalAura.mc.player, entity);
            CrystalAura.mc.player.swingArm(Hand.MAIN_HAND);
            this.crystalEntity = entity;
        }
        if (!entity.isAlive()) {
            this.reset();
        }
    }

    public static int getSlotInInventoryOrHotbar(Item item, boolean inHotBar) {
        int firstSlot = inHotBar ? 0 : 9;
        int lastSlot = inHotBar ? 9 : 36;
        int finalSlot = -1;
        for (int i = firstSlot; i < lastSlot; ++i) {
            if (CrystalAura.mc.player.inventory.getStackInSlot(i).getItem() != item) continue;
            finalSlot = i;
        }
        return finalSlot;
    }

    @NativeInclude
    private void setAndUseCrystal(int slot, BlockPos pos) {
        Hand hand;
        boolean isOffHand = CrystalAura.mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL;
        Vector3d center = new Vector3d((float)pos.getX() + 0.5f, (float)pos.getY() + 0.5f, (float)pos.getZ() + 0.5f);
        if (!isOffHand) {
            CrystalAura.mc.player.inventory.currentItem = slot;
        }
        Hand hand2 = hand = isOffHand ? Hand.OFF_HAND : Hand.MAIN_HAND;
        if (CrystalAura.mc.playerController.processRightClickBlock(CrystalAura.mc.player, CrystalAura.mc.world, hand, new BlockRayTraceResult(center, Direction.UP, pos, false)) == ActionResultType.SUCCESS) {
            CrystalAura.mc.player.swingArm(Hand.MAIN_HAND);
        }
    }

    private boolean isValid(Entity base) {
        if (base == null) {
            return false;
        }
        if (this.obsidianPos == null) {
            return false;
        }
        for (Entity entity : CrystalAura.mc.world.getAllEntities()) {
            if (!(entity instanceof ItemEntity)) continue;
            ItemEntity entity1 = (ItemEntity)entity;
            if (!((Boolean)this.safeYourSelf.getValue()).booleanValue() || !(entity1.getPosY() > (double)this.obsidianPos.getY())) continue;
            return false;
        }
        return this.isCorrectDistance();
    }

    private boolean isCorrectDistance() {
        if (this.obsidianPos == null) {
            return false;
        }
        return CrystalAura.mc.player.getPositionVec().distanceTo(new Vector3d(this.obsidianPos.getX(), this.obsidianPos.getY(), this.obsidianPos.getZ())) <= (double)CrystalAura.mc.playerController.getBlockReachDistance();
    }

    public List<Entity> findEnderCrystals(BlockPos position) {
        return CrystalAura.mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(position.getX(), position.getY(), position.getZ(), (double)position.getX() + 1.0, (double)position.getY() + 2.0, (double)position.getZ() + 1.0)).stream().filter(entity -> entity instanceof EnderCrystalEntity).collect(Collectors.toList());
    }

    private void reset() {
        this.crystalEntity = null;
        this.obsidianPos = null;
        this.rotationVector = new Vector2f(CrystalAura.mc.player.rotationYaw, CrystalAura.mc.player.rotationPitch);
        this.oldCurrentSlot = -1;
        this.bestSlot = -1;
    }
}

