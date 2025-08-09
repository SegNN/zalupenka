/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.entity.player;

import com.mojang.authlib.GameProfile;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;

public class RemoteClientPlayerEntity
        extends AbstractClientPlayerEntity {
    public RemoteClientPlayerEntity(ClientWorld world, GameProfile profile) {
        super(world, profile);
        this.stepHeight = 1.0f;
        this.noClip = true;
    }

    @Override
    public boolean isInRangeToRenderDist(double distance) {
        double d0 = this.getBoundingBox().getAverageEdgeLength() * 10.0;
        if (Double.isNaN(d0)) {
            d0 = 1.0;
        }
        return distance < (d0 = d0 * 64.0 * RemoteClientPlayerEntity.getRenderDistanceWeight()) * d0;
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        return true;
    }

    @Override
    public void tick() {
        super.tick();
        this.func_233629_a_(this, false);
    }

    @Override
    public void livingTick() {
        if (this.newPosRotationIncrements > 0) {
            double d0 = this.getPosX() + (this.interpTargetX - this.getPosX()) / (double)this.newPosRotationIncrements;
            double d1 = this.getPosY() + (this.interpTargetY - this.getPosY()) / (double)this.newPosRotationIncrements;
            double d2 = this.getPosZ() + (this.interpTargetZ - this.getPosZ()) / (double)this.newPosRotationIncrements;
            this.rotationYaw = (float)((double)this.rotationYaw + MathHelper.wrapDegrees(this.interpTargetYaw - (double)this.rotationYaw) / (double)this.newPosRotationIncrements);
            this.rotationPitch = (float)((double)this.rotationPitch + (this.interpTargetPitch - (double)this.rotationPitch) / (double)this.newPosRotationIncrements);
            --this.newPosRotationIncrements;
            this.setPosition(d0, d1, d2);
            this.setRotation(this.rotationYaw, this.rotationPitch);
        }
        if (this.interpTicksHead > 0) {
            this.rotationYawHead = (float)((double)this.rotationYawHead + MathHelper.wrapDegrees(this.interpTargetHeadYaw - (double)this.rotationYawHead) / (double)this.interpTicksHead);
            --this.interpTicksHead;
        }
        this.prevCameraYaw = this.cameraYaw;
        this.updateArmSwingProgress();
        float f1 = this.onGround && !this.getShouldBeDead() ? Math.min(0.1f, MathHelper.sqrt(RemoteClientPlayerEntity.horizontalMag(this.getMotion()))) : 0.0f;
        if (!this.onGround && !this.getShouldBeDead()) {
            float f = (float)Math.atan(-this.getMotion().y * (double)0.2f) * 15.0f;
        } else {
            float f = 0.0f;
        }
        this.cameraYaw += (f1 - this.cameraYaw) * 0.4f;
        this.world.getProfiler().startSection("push");
        this.collideWithNearbyEntities();
        this.world.getProfiler().endSection();
    }

    @Override
    protected void updatePose() {
    }

    @Override
    public void sendMessage(ITextComponent component, UUID senderUUID) {
        Minecraft minecraft = Minecraft.getInstance();
        if (!minecraft.cannotSendChatMessages(senderUUID)) {
            minecraft.ingameGUI.getChatGUI().printChatMessage(component);
        }
    }
}

