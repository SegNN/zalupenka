/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.modules.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import fun.kubik.events.api.EventHook;
import fun.kubik.events.main.render.EventRender2D;
import fun.kubik.events.main.render.EventRender3D;
import fun.kubik.helpers.render.ColorHelpers;
import fun.kubik.helpers.render.ScreenHelpers;
import fun.kubik.helpers.visual.StencilHelpers;
import fun.kubik.helpers.visual.VisualHelpers;
import fun.kubik.managers.module.Module;
import fun.kubik.managers.module.main.Category;
import fun.kubik.managers.module.option.main.CheckboxOption;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EnderPearlEntity;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector4f;
import org.lwjgl.opengl.GL11;

public class PearlPrediction
extends Module {
    private final CheckboxOption tag = new CheckboxOption("Tag", true);

    public PearlPrediction() {
        super("Pearl Prediction", Category.RENDER);
        this.settings(this.tag);
    }

    @EventHook
    public void render(EventRender3D.Post event) {
        RenderSystem.pushMatrix();
        RenderSystem.translated(-mc.getRenderManager().renderPosX(), -mc.getRenderManager().renderPosY(), -mc.getRenderManager().renderPosZ());
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.disableDepthTest();
        GL11.glEnable(2848);
        RenderSystem.lineWidth(2.0f);
        Tessellator.getInstance().getBuffer().begin(1, DefaultVertexFormats.POSITION_COLOR);
        for (Entity entity : PearlPrediction.mc.world.getAllEntities()) {
            if (!(entity instanceof EnderPearlEntity)) continue;
            EnderPearlEntity pearl = (EnderPearlEntity)entity;
            this.renderLine(pearl);
        }
        Tessellator.getInstance().getBuffer().finishDrawing();
        WorldVertexBufferUploader.draw(Tessellator.getInstance().getBuffer());
        RenderSystem.enableDepthTest();
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
        GL11.glDisable(2848);
        RenderSystem.popMatrix();
    }

    @EventHook
    public void render(EventRender2D.Post event) {
        MatrixStack matrixStack = event.getMatrixStack();
        for (Entity entity : PearlPrediction.mc.world.getAllEntities()) {
            if (!(entity instanceof EnderPearlEntity)) continue;
            EnderPearlEntity pearl = (EnderPearlEntity)entity;
            if (!((Boolean)this.tag.getValue()).booleanValue()) continue;
            Vector2f position = ScreenHelpers.worldToScreen(this.calculateLandingPosition(pearl));
            float width = 60.0f;
            float height = 15.0f;
            float x = position.x - width / 2.0f;
            float y = position.y;
            double time = this.calculateTimeToFall(pearl);
            String name = "Ender Pearl " + String.format("%.1f", time);
            int glow = ColorHelpers.setAlpha(ColorHelpers.getThemeColor(1), 30);
            int back = ColorHelpers.rgba(15, 15, 15, 255);
            VisualHelpers.drawRoundedRect(matrixStack, x, y, width, height, 3.0f, back);
            VisualHelpers.drawRoundedOutline(matrixStack, x, y, width, height, 3.0f, 1.0f, ColorHelpers.rgba(190, 190, 190, 15.299999999999999));
            StencilHelpers.init();
            VisualHelpers.drawRoundedRect(matrixStack, x, y, width, height, 3.0f, -1);
            StencilHelpers.read(1);
            VisualHelpers.drawGlow(matrixStack, x, y, width, 192.0f, 40.0f, glow);
            VisualHelpers.drawRoundedRect(matrixStack, x + width - 12.0f, y, 12.0f, 2.0f, new Vector4f(0.0f, 2.0f, 2.0f, 0.0f), ColorHelpers.getThemeColor(1));
            suisse_intl.drawText(matrixStack, name, x + width / 2.0f - suisse_intl.getWidth(name, 7.0f) / 2.0f, y + height / 2.0f - suisse_intl.getHeight(7.0f) / 2.0f, ColorHelpers.rgba(255, 255, 255, 255), 7.0f);
            StencilHelpers.uninit();
        }
    }

    private void renderLine(EnderPearlEntity pearl) {
        Vector3d pearlPosition = pearl.getPositionVec().add(0.0, 0.0, 0.0);
        Vector3d pearlMotion = pearl.getMotion();
        for (int i = 0; i <= 300; ++i) {
            Vector3d lastPosition = pearlPosition;
            pearlPosition = pearlPosition.add(pearlMotion);
            pearlMotion = this.updatePearlMotion(pearl, pearlMotion);
            if (this.shouldEntityHit(pearlPosition.add(0.0, 1.0, 0.0), lastPosition.add(0.0, 1.0, 0.0)) || pearlPosition.y <= 0.0) break;
            float[] colors = this.getLineColor(i);
            Tessellator.getInstance().getBuffer().pos(lastPosition.x, lastPosition.y, lastPosition.z).color(colors[0], colors[1], colors[2], 1.0f).endVertex();
            Tessellator.getInstance().getBuffer().pos(pearlPosition.x, pearlPosition.y, pearlPosition.z).color(colors[0], colors[1], colors[2], 1.0f).endVertex();
        }
    }

    private Vector3d updatePearlMotion(EnderPearlEntity pearl, Vector3d originalPearlMotion) {
        Vector3d pearlMotion = originalPearlMotion;
        pearlMotion = pearl.isInWater() ? pearlMotion.scale(0.8f) : pearlMotion.scale(0.99f);
        if (!pearl.hasNoGravity()) {
            pearlMotion.y -= (double)pearl.getGravityVelocity();
        }
        return pearlMotion;
    }

    private boolean shouldEntityHit(Vector3d pearlPosition, Vector3d lastPosition) {
        RayTraceContext rayTraceContext = new RayTraceContext(lastPosition, pearlPosition, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, PearlPrediction.mc.player);
        BlockRayTraceResult blockHitResult = PearlPrediction.mc.world.rayTraceBlocks(rayTraceContext);
        return blockHitResult.getType() == RayTraceResult.Type.BLOCK;
    }

    private float[] getLineColor(int index) {
        int color = ColorHelpers.getTheme(index * 2);
        return ColorHelpers.rgb(color);
    }

    private Vector3d calculateLandingPosition(EnderPearlEntity pearl) {
        Vector3d pearlPosition = pearl.getPositionVec().add(0.0, 0.0, 0.0);
        Vector3d pearlMotion = pearl.getMotion();
        for (int i = 0; i <= 300; ++i) {
            Vector3d lastPosition = pearlPosition;
            pearlPosition = pearlPosition.add(pearlMotion);
            pearlMotion = this.updatePearlMotion(pearl, pearlMotion);
            if (this.shouldEntityHit(pearlPosition.add(0.0, 1.0, 0.0), lastPosition.add(0.0, 1.0, 0.0)) || pearlPosition.y <= 0.0) break;
        }
        return pearlPosition;
    }

    private double calculateTimeToFall(EnderPearlEntity pearl) {
        Vector3d pearlPosition = pearl.getPositionVec().add(0.0, 0.0, 0.0);
        Vector3d pearlMotion = pearl.getMotion();
        double time = 0.0;
        for (int i = 0; i <= 300; ++i) {
            Vector3d lastPosition = pearlPosition;
            pearlPosition = pearlPosition.add(pearlMotion);
            pearlMotion = this.updatePearlMotion(pearl, pearlMotion);
            time += 0.05;
            if (this.shouldEntityHit(pearlPosition.add(0.0, 1.0, 0.0), lastPosition.add(0.0, 1.0, 0.0)) || pearlPosition.y <= 0.0) break;
        }
        return time;
    }
}

