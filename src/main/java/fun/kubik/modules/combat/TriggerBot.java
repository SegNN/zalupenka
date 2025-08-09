/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.modules.combat;

import fun.kubik.Load;
import fun.kubik.events.api.EventHook;
import fun.kubik.events.main.EventUpdate;
import fun.kubik.managers.module.Module;
import fun.kubik.managers.module.main.Category;
import fun.kubik.managers.module.option.main.CheckboxOption;
import fun.kubik.modules.movement.AutoSprint;
import net.minecraft.block.Blocks;
import net.minecraft.network.play.client.CEntityActionPacket;
import net.minecraft.potion.Effects;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Hand;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;

public class TriggerBot
extends Module {
    private final CheckboxOption onlyCritical = new CheckboxOption("Only Crits", true);
    private final CheckboxOption onlySpaceCritical = new CheckboxOption("Only With Space", false).visible(this.onlyCritical::getValue);
    private long cpsLimit = 0L;

    public TriggerBot() {
        super("TriggerBot", Category.COMBAT);
        this.settings(this.onlyCritical, this.onlySpaceCritical);
    }

    @EventHook
    public void update(EventUpdate e) {
        if (this.cpsLimit > System.currentTimeMillis()) {
            --this.cpsLimit;
        }
        if (TriggerBot.mc.objectMouseOver.getType() == RayTraceResult.Type.ENTITY && this.whenFalling() && this.cpsLimit <= System.currentTimeMillis()) {
            this.cpsLimit = System.currentTimeMillis() + 550L;
            if (TriggerBot.mc.objectMouseOver.getType() == RayTraceResult.Type.ENTITY) {
                AutoSprint autoSprint = (AutoSprint)Load.getInstance().getHooks().getModuleManagers().findClass(AutoSprint.class);
                if (autoSprint.mode.getSelected("Rage") && CEntityActionPacket.lastUpdatedSprint && autoSprint.canSprint() && !TriggerBot.mc.player.isInWater()) {
                    TriggerBot.mc.player.connection.sendPacket(new CEntityActionPacket(TriggerBot.mc.player, CEntityActionPacket.Action.STOP_SPRINTING));
                }
                TriggerBot.mc.playerController.attackEntity(TriggerBot.mc.player, ((EntityRayTraceResult)TriggerBot.mc.objectMouseOver).getEntity());
                TriggerBot.mc.player.swingArm(Hand.MAIN_HAND);
                if (autoSprint.mode.getSelected("Rage") && autoSprint.canSprint() && !TriggerBot.mc.player.isInWater()) {
                    TriggerBot.mc.player.connection.sendPacket(new CEntityActionPacket(TriggerBot.mc.player, CEntityActionPacket.Action.START_SPRINTING));
                }
            }
        }
    }

    public boolean whenFalling() {
        boolean reasonForAttack;
        boolean onSpace = (Boolean)this.onlySpaceCritical.getValue() != false && TriggerBot.mc.player.isOnGround() && !TriggerBot.mc.gameSettings.keyBindJump.isKeyDown();
        boolean water = !TriggerBot.mc.gameSettings.keyBindJump.isKeyDown() && TriggerBot.mc.player.isInWater() || TriggerBot.mc.player.isInWater() && TriggerBot.mc.player.areEyesInFluid(FluidTags.WATER);
        boolean bl = reasonForAttack = TriggerBot.mc.player.isPotionActive(Effects.BLINDNESS) || TriggerBot.mc.player.isOnLadder() || TriggerBot.mc.player.getBlockState().isIn(Blocks.COBWEB) || water || TriggerBot.mc.player.isRidingHorse() || TriggerBot.mc.player.abilities.isFlying || TriggerBot.mc.player.isElytraFlying() || TriggerBot.mc.player.isInLava() && TriggerBot.mc.player.areEyesInFluid(FluidTags.LAVA) || TriggerBot.mc.player.isPotionActive(Effects.LEVITATION) || TriggerBot.mc.player.isPassenger();
        if (!reasonForAttack && ((Boolean)this.onlyCritical.getValue()).booleanValue() && TriggerBot.mc.player.getCooledAttackStrength(1.5f) >= 0.93f) {
            return onSpace || !TriggerBot.mc.player.isOnGround() && TriggerBot.mc.player.fallDistance > 0.0f;
        }
        return true;
    }
}

