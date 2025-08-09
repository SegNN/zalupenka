/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.modules.combat;

import fun.kubik.events.api.EventHook;
import fun.kubik.events.main.EventUpdate;
import fun.kubik.managers.module.Module;
import fun.kubik.managers.module.main.Category;
import fun.kubik.managers.module.option.main.MultiOption;
import fun.kubik.managers.module.option.main.MultiOptionValue;
import fun.kubik.managers.module.option.main.SelectOption;
import fun.kubik.managers.module.option.main.SelectOptionValue;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;

public class AntiBot
extends Module {
    private final SelectOption mode = new SelectOption("Mode", 0, new SelectOptionValue("ReallyWorld"), new SelectOptionValue("Custom")); //new SelectOptionValue("Matrix")
    private final MultiOption custom = new MultiOption("Checks", new MultiOptionValue("Armor Check", false), new MultiOptionValue("Enchant Check", false), new MultiOptionValue("Armor Damaged Check", false), new MultiOptionValue("Off Hand Check", false), new MultiOptionValue("Equip Check", false), new MultiOptionValue("Main Hand Check", false), new MultiOptionValue("Food Check", false), new MultiOptionValue("Unique Check", false)).visible(() -> this.mode.getSelected("Custom"));
    public static List<Entity> bots = new ArrayList<Entity>();

    public AntiBot() {
        super("AntiBot", Category.COMBAT);
        this.settings(this.mode, this.custom);
    }

    @EventHook
    public void update(EventUpdate eventUpdate) {
        if (this.mode.getSelected("ReallyWorld")) {
            this.reallyWorldCheck();
        }
//        if (this.mode.getSelected("Matrix")) {
//            this.matrixCheck();
//        }
        if (this.mode.getSelected("Custom")) {
            this.customCheck();
        }
    }

//    private void matrixCheck() {
//        for (PlayerEntity playerEntity : AntiBot.mc.world.getPlayers()) {
//            boolean botCheck;
//            boolean uniqueCheck = !playerEntity.getUniqueID().equals(PlayerEntity.getOfflineUUID(playerEntity.getName().getString()));
//            boolean bl = botCheck = !bots.contains(playerEntity);
//            if (!uniqueCheck || !botCheck) continue;
//            bots.add(playerEntity);
//        }
//    }

    private void reallyWorldCheck() {
        for (PlayerEntity playerEntity : AntiBot.mc.world.getPlayers()) {
            boolean armorCheck = false;
            boolean enchantCheck = false;
            boolean armorDamagedCheck = false;
            boolean offHandCheck = playerEntity.getHeldItemOffhand().getItem() == Items.AIR;
            boolean equipCheck = playerEntity.inventory.armorInventory.get(0).getItem() == Items.LEATHER_BOOTS || playerEntity.inventory.armorInventory.get(1).getItem() == Items.LEATHER_LEGGINGS || playerEntity.inventory.armorInventory.get(2).getItem() == Items.LEATHER_CHESTPLATE || playerEntity.inventory.armorInventory.get(3).getItem() == Items.LEATHER_HELMET || playerEntity.inventory.armorInventory.get(0).getItem() == Items.IRON_BOOTS || playerEntity.inventory.armorInventory.get(1).getItem() == Items.IRON_LEGGINGS || playerEntity.inventory.armorInventory.get(2).getItem() == Items.IRON_CHESTPLATE || playerEntity.inventory.armorInventory.get(3).getItem() == Items.IRON_HELMET;
            boolean mainHandCheck = playerEntity.getHeldItemMainhand().getItem() != Items.AIR;
            boolean foodCheck = playerEntity.getFoodStats().getFoodLevel() == 20;
            for (int i = 0; i < 4; ++i) {
                if (!armorCheck) {
                    boolean bl = armorCheck = playerEntity.inventory.armorInventory.get(i).getItem() != Items.AIR;
                }
                if (!enchantCheck) {
                    enchantCheck = playerEntity.inventory.armorInventory.get(i).isEnchantable();
                }
                if (armorDamagedCheck) continue;
                armorDamagedCheck = !playerEntity.inventory.armorInventory.get(i).isDamaged();
            }
            if (AntiBot.mc.player != playerEntity && armorCheck && enchantCheck && offHandCheck && equipCheck && mainHandCheck && armorDamagedCheck && foodCheck) {
                if (!bots.contains(playerEntity)) {
                    bots.add(playerEntity);
                }
                return;
            }
            bots.remove(playerEntity);
        }
    }

    private void customCheck() {
        for (PlayerEntity playerEntity : AntiBot.mc.world.getPlayers()) {
            boolean armorCheck = false;
            boolean enchantCheck = false;
            boolean armorDamagedCheck = false;
            boolean offHandCheck = playerEntity.getHeldItemOffhand().getItem() == Items.AIR || !this.custom.getSelected("Off Hand Check");
            boolean equipCheck = playerEntity.inventory.armorInventory.get(0).getItem() == Items.LEATHER_BOOTS || playerEntity.inventory.armorInventory.get(1).getItem() == Items.LEATHER_LEGGINGS || playerEntity.inventory.armorInventory.get(2).getItem() == Items.LEATHER_CHESTPLATE || playerEntity.inventory.armorInventory.get(3).getItem() == Items.LEATHER_HELMET || playerEntity.inventory.armorInventory.get(0).getItem() == Items.IRON_BOOTS || playerEntity.inventory.armorInventory.get(1).getItem() == Items.IRON_LEGGINGS || playerEntity.inventory.armorInventory.get(2).getItem() == Items.IRON_CHESTPLATE || playerEntity.inventory.armorInventory.get(3).getItem() == Items.IRON_HELMET || !this.custom.getSelected("Equip Check");
            boolean mainHandCheck = playerEntity.getHeldItemMainhand().getItem() != Items.AIR || !this.custom.getSelected("Main Hand Check");
            boolean foodCheck = playerEntity.getFoodStats().getFoodLevel() == 20 || !this.custom.getSelected("Food Check");
            boolean uniqueCheck = !playerEntity.getUniqueID().equals(PlayerEntity.getOfflineUUID(playerEntity.getName().getString())) || !this.custom.getSelected("Unique Check");
            for (int i = 0; i < 4; ++i) {
                if (!armorCheck) {
                    boolean bl = armorCheck = playerEntity.inventory.armorInventory.get(i).getItem() != Items.AIR || !this.custom.getSelected("Armor Check");
                }
                if (!enchantCheck) {
                    boolean bl = enchantCheck = playerEntity.inventory.armorInventory.get(i).isEnchantable() || !this.custom.getSelected("Enchant Check");
                }
                if (armorDamagedCheck) continue;
                armorDamagedCheck = !playerEntity.inventory.armorInventory.get(i).isDamaged() || !this.custom.getSelected("Armor Damaged Check");
            }
            if (AntiBot.mc.player != playerEntity && armorCheck && enchantCheck && armorDamagedCheck && offHandCheck && equipCheck && mainHandCheck && foodCheck && uniqueCheck) {
                if (!bots.contains(playerEntity)) {
                    bots.add(playerEntity);
                }
                return;
            }
            bots.remove(playerEntity);
        }
    }

    public static boolean checkBot(LivingEntity entity) {
        return entity instanceof PlayerEntity && bots.contains(entity);
    }

    @Override
    public void onDisabled() {
        bots.clear();
        super.onDisabled();
    }
}

