/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.modules.render;

import com.mojang.blaze3d.platform.GlStateManager;
import fun.kubik.Load;
import fun.kubik.events.api.EventHook;
import fun.kubik.events.main.EventUpdate;
import fun.kubik.events.main.render.EventRender2D;
import fun.kubik.helpers.animation.EasingList;
import fun.kubik.helpers.render.ColorHelpers;
import fun.kubik.helpers.visual.VisualHelpers;
import fun.kubik.managers.module.Module;
import fun.kubik.managers.module.main.Category;
import fun.kubik.managers.module.option.main.MultiOption;
import fun.kubik.managers.module.option.main.MultiOptionValue;
import fun.kubik.managers.module.option.main.SliderOption;
import java.util.regex.Pattern;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class Arrows
extends Module {
    private final MultiOption options = new MultiOption("Options", new MultiOptionValue("Name", true), new MultiOptionValue("Distance", true));
    private final SliderOption distance = new SliderOption("Distance", 30.0f, 20.0f, 60.0f).increment(1.0f);
    private long lastColorChangeTime = 0L;
    private int targetColor;
    private int currentColor;

    public Arrows() {
        super("Arrows", Category.RENDER);
        this.settings(this.options, this.distance);
    }

    @EventHook
    public void render(EventRender2D.Post event) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - this.lastColorChangeTime > 3000L) {
            this.targetColor = ColorHelpers.getThemeColor(1);
            this.lastColorChangeTime = currentTime;
        }
        float colorTransitionSpeed = 0.05f;
        this.currentColor = this.interpolateColor(this.currentColor, this.targetColor, colorTransitionSpeed);
        for (Entity entity : Arrows.mc.world.getAllEntities()) {
            if (entity == Arrows.mc.player || entity.getEntityId() == 1337 || !(entity instanceof PlayerEntity)) continue;
            PlayerEntity player = (PlayerEntity)entity;
            if (!Pattern.compile("^\\w{3,16}$").matcher(player.getGameProfile().getName()).matches()) continue;
            this.renderEntity(event, player, this.currentColor);
        }
    }

    @EventHook
    public void update(EventUpdate event) {
        for (Entity entity : Arrows.mc.world.getAllEntities()) {
            if (entity == Arrows.mc.player || !(entity instanceof PlayerEntity)) continue;
            PlayerEntity player = (PlayerEntity)entity;
            player.getFriendAnimation().update(Load.getInstance().getHooks().getFriendManagers().is(player.getGameProfile().getName()));
        }
    }

    private void renderEntity(EventRender2D.Post event, PlayerEntity entity, int color) {
        double x = entity.lastTickPosX + (entity.getPosX() - entity.lastTickPosX) * (double)mc.getRenderPartialTicks() - Arrows.mc.getRenderManager().info.getProjectedView().getX();
        double z = entity.lastTickPosZ + (entity.getPosZ() - entity.lastTickPosZ) * (double)mc.getRenderPartialTicks() - Arrows.mc.getRenderManager().info.getProjectedView().getZ();
        double cos = MathHelper.cos((float)((double)Arrows.mc.player.rotationYaw * (Math.PI / 180)));
        double sin = MathHelper.sin((float)((double)Arrows.mc.player.rotationYaw * (Math.PI / 180)));
        double rotY = -(z * cos - x * sin);
        double rotX = -(x * cos + z * sin);
        float angle = (float)(Math.atan2(rotY, rotX) * 180.0 / Math.PI);
        float x2 = ((Float)this.distance.getValue()).floatValue() * MathHelper.cos((float)Math.toRadians(angle)) + (float)mc.getMainWindow().getScaledWidth() / 2.0f;
        float y2 = ((Float)this.distance.getValue()).floatValue() * MathHelper.sin((float)Math.toRadians(angle)) + (float)mc.getMainWindow().getScaledHeight() / 2.0f;
        int distance = (int)Arrows.mc.player.getDistance(entity);
        float textWidth2 = suisse_intl.getWidth("" + distance, 6.0f);
        float textWidth = suisse_intl.getWidth(entity.getName().getString(), 6.0f);
        float centr = textWidth / 2.0f;
        float centr2 = textWidth2 / 2.0f;
        if (this.options.getSelected("Name")) {
            suisse_intl.drawText(event.getMatrixStack(), entity.getName().getString(), x2 - centr, y2 - 13.0f, ColorHelpers.rgba(255, 255, 255, 255), 6.0f);
        }
        if (this.options.getSelected("Distance")) {
            suisse_intl.drawCenteredText(event.getMatrixStack(), distance + "m", x2 - centr2 + 3.0f, y2 + 11.0f, ColorHelpers.rgba(255, 255, 255, 255), 6.0f);
        }
        GlStateManager.pushMatrix();
        GlStateManager.disableBlend();
        GlStateManager.translated(x2, y2, 0.0);
        GlStateManager.rotatef(angle, 0.0f, 0.0f, 1.0f);
        entity.getFriendAnimation().animate(0.0f, 1.0f, 0.2f, EasingList.CIRC_OUT, Arrows.mc.getTimer().renderPartialTicks);
        int clr = ColorHelpers.interpolateColor(-1, ColorHelpers.rgba(0, 255, 0, 255), entity.getFriendAnimation().getAnimationValue());
        VisualHelpers.drawImage(event.getMatrixStack(), new ResourceLocation("main/textures/images/triangle.png"), -9.0f, -10.0f, 18.0f, 20.0f, ColorHelpers.getThemeColor(1));
        GlStateManager.enableBlend();
        GlStateManager.popMatrix();
    }

    private int interpolateColor(int startColor, int endColor, float ratio) {
        int startAlpha = startColor >> 24 & 0xFF;
        int startRed = startColor >> 16 & 0xFF;
        int startGreen = startColor >> 8 & 0xFF;
        int startBlue = startColor & 0xFF;
        int endAlpha = endColor >> 24 & 0xFF;
        int endRed = endColor >> 16 & 0xFF;
        int endGreen = endColor >> 8 & 0xFF;
        int endBlue = endColor & 0xFF;
        int newAlpha = (int)((float)startAlpha + (float)(endAlpha - startAlpha) * ratio);
        int newRed = (int)((float)startRed + (float)(endRed - startRed) * ratio);
        int newGreen = (int)((float)startGreen + (float)(endGreen - startGreen) * ratio);
        int newBlue = (int)((float)startBlue + (float)(endBlue - startBlue) * ratio);
        return newAlpha << 24 | newRed << 16 | newGreen << 8 | newBlue;
    }
}

