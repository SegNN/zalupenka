/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.modules.player;

import fun.kubik.events.api.EventHook;
import fun.kubik.events.main.EventUpdate;
import fun.kubik.events.main.movement.EventJump;
import fun.kubik.events.main.movement.EventStrafe;
import fun.kubik.events.main.player.EventElytra;
import fun.kubik.events.main.player.EventSwimming;
import fun.kubik.events.main.player.EventSync;
import fun.kubik.managers.module.Module;
import fun.kubik.managers.module.main.Category;
import fun.kubik.managers.module.option.main.MultiOption;
import fun.kubik.managers.module.option.main.MultiOptionValue;
import fun.kubik.managers.module.option.main.SelectOption;
import fun.kubik.managers.module.option.main.SelectOptionValue;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.EnchantedGoldenAppleItem;
import net.minecraft.item.SkullItem;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector2f;

public class ItemFinder
extends Module {
    private Vector2f rotation;
    private final SelectOption mode = new SelectOption("Mode", 0, new SelectOptionValue("Silent Aim"), new SelectOptionValue("Focus Aim"));
    private final MultiOption elements = new MultiOption("Elements", new MultiOptionValue("Skull", true), new MultiOptionValue("Elytra", true), new MultiOptionValue("Enchanted Golden Apple", true));

    public ItemFinder() {
        super("ItemFinder", Category.PLAYER);
        this.settings(this.mode, this.elements);
    }

    @EventHook
    public void update(EventUpdate event) {
        this.rotation = new Vector2f(ItemFinder.mc.player.rotationYaw, ItemFinder.mc.player.rotationPitch);
        for (Entity entity : ItemFinder.mc.world.getAllEntities()) {
            if (!(entity instanceof ItemEntity)) continue;
            ItemEntity item = (ItemEntity)entity;
            if (item.getItem().getItem() instanceof SkullItem && this.elements.getSelected("Skull")) {
                this.rotation = this.itemPosition(entity);
            }
            if (item.getItem().getItem() instanceof ElytraItem && this.elements.getSelected("Elytra")) {
                this.rotation = this.itemPosition(entity);
            }
            if (!(item.getItem().getItem() instanceof EnchantedGoldenAppleItem) || !this.elements.getSelected("Enchanted Golden Apple")) continue;
            this.rotation = this.itemPosition(entity);
        }
    }

    @EventHook
    public void sync(EventSync event) {
        if (this.mode.getSelected("Silent Aim")) {
            ItemFinder.mc.player.rotationYawHead = this.rotation.x;
            ItemFinder.mc.player.renderYawOffset = this.rotation.x;
            ItemFinder.mc.player.rotationPitchHead = this.rotation.y;
            event.setYaw(this.rotation.x);
            event.setPitch(this.rotation.y);
        } else {
            ItemFinder.mc.player.rotationYaw = this.rotation.x;
            ItemFinder.mc.player.rotationPitch = this.rotation.y;
        }
    }

    @EventHook
    public void jump(EventJump event) {
        if (this.mode.getSelected("Silent Aim")) {
            event.setYaw(this.rotation.x);
        }
    }

    @EventHook
    public void swimming(EventSwimming event) {
        if (this.mode.getSelected("Silent Aim")) {
            event.setYaw(this.rotation.x);
            event.setPitch(this.rotation.y);
        }
    }

    @EventHook
    public void elytra(EventElytra event) {
        if (this.mode.getSelected("Silent Aim")) {
            event.setYaw(this.rotation.x);
            event.setPitch(this.rotation.y);
        }
    }

    @EventHook
    public void strafe(EventStrafe event) {
        if (this.mode.getSelected("Silent Aim")) {
            event.setYaw(this.rotation.x);
        }
    }

    private Vector2f itemPosition(Entity entity) {
        double x = entity.getPosX() - ItemFinder.mc.player.getPosX();
        double y = entity.getPosY() - ItemFinder.mc.player.getPosY();
        double z = entity.getPosZ() - ItemFinder.mc.player.getPosZ();
        double sqrt = MathHelper.sqrt(x * x + z * z);
        double mainX = MathHelper.atan2(z, x) * 57.29577951308232 - 90.0;
        double mainY = MathHelper.atan2(y, sqrt) * 57.29577951308232;
        return new Vector2f((float)mainX, (float)(-mainY));
    }
}

