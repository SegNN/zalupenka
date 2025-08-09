package fun.kubik.helpers.visual;

import com.jhlabs.image.GaussianFilter;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import fun.kubik.events.api.EventHook;
import fun.kubik.helpers.interfaces.IFastAccess;
import fun.kubik.helpers.interfaces.IShaderAccess;
import fun.kubik.helpers.render.ColorHelpers;
import fun.kubik.utils.math.MathUtils;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import lombok.Generated;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector4f;
import org.joml.Vector4i;
import org.lwjgl.opengl.GL11;

public final class VisualHelpers
        extends AbstractGui
        implements IFastAccess,
        IShaderAccess {
    private static final Tessellator TESSELLATOR = Tessellator.getInstance();
    public static final BufferBuilder BUILDER = TESSELLATOR.getBuffer();
    private static final HashMap<Integer, Integer> shadowCache = new HashMap();
    
    public static void clearShadowCache() {
        shadowCache.clear();
    }

    public static void drawRoundedRect(float x, float y, float width, float height, float round, int color) {
        VisualHelpers.drawRoundedGradientRect(x, y, width, height, round, color, color, color, color);
    }

    public static void drawRoundedRect(float x, float y, float width, float height, float round, int firstColor, int secondColor) {
        VisualHelpers.drawRoundedGradientRect(x, y, width, height, round, firstColor, firstColor, secondColor, secondColor);
    }

    public static void drawRoundedRect(float x, float y, float width, float height, float round, int firstColor, int secondColor, int thirdColor, int fourthColor) {
        VisualHelpers.drawRoundedGradientRect(x, y, width, height, round, firstColor, secondColor, thirdColor, fourthColor);
    }

    public static void drawRoundedRect(float x, float y, float width, float height, Vector4f round, int color) {
        VisualHelpers.drawRoundedGradientRect(x, y, width, height, round, color, color, color, color);
    }

    public static void drawRoundedRect(float x, float y, float width, float height, Vector4f round, int firstColor, int secondColor) {
        VisualHelpers.drawRoundedGradientRect(x, y, width, height, round, firstColor, firstColor, secondColor, secondColor);
    }

    public static void drawRoundedRect(float x, float y, float width, float height, Vector4f round, int firstColor, int secondColor, int thirdColor, int fourthColor) {
        VisualHelpers.drawRoundedGradientRect(x, y, width, height, round, firstColor, secondColor, thirdColor, fourthColor);
    }

    public static void drawRoundedRectWithOutline(float x, float y, float width, float height, float round, float thickness, int color, int outlineColor) {
        VisualHelpers.drawRoundedGradientRect(x, y, width, height, round, color, color, color, color);
        VisualHelpers.drawRoundedOutlineRect(x - 0.5f, y - 0.5f, width + 1.0f, height + 1.0f, round, thickness, outlineColor);
    }

    public static Vector3d getEntityPosition(Entity entity, float interpolationFactor) {
        double interpolatedX = MathUtils.interpolate(entity.getPosX(), entity.lastTickPosX, (double)interpolationFactor) - mc.getRenderManager().renderPosX();
        double interpolatedY = MathUtils.interpolate(entity.getPosY(), entity.lastTickPosY, (double)interpolationFactor) - mc.getRenderManager().renderPosY();
        double interpolatedZ = MathUtils.interpolate(entity.getPosZ(), entity.lastTickPosZ, (double)interpolationFactor) - mc.getRenderManager().renderPosZ();
        return new Vector3d(interpolatedX, interpolatedY, interpolatedZ);
    }

    public static void drawRoundedGradientRect(float x, float y, float width, float height, float round, int color1, int color2, int color3, int color4) {
        float[] c1 = ColorHelpers.getRGBAf(color1);
        float[] c2 = ColorHelpers.getRGBAf(color2);
        float[] c3 = ColorHelpers.getRGBAf(color3);
        float[] c4 = ColorHelpers.getRGBAf(color4);
        RenderSystem.color4f(0.0f, 0.0f, 0.0f, 0.0f);
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA.param, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA.param, GlStateManager.SourceFactor.ONE.param, GlStateManager.DestFactor.ZERO.param);
        ROUNDED_GRADIENT.useProgram();
        ROUNDED_GRADIENT.setupUniform2f("size", width, height);
        ROUNDED_GRADIENT.setupUniform4f("round", round, round, round, round);
        ROUNDED_GRADIENT.setupUniform4f("color1", c1[0], c1[1], c1[2], c1[3]);
        ROUNDED_GRADIENT.setupUniform4f("color2", c2[0], c2[1], c2[2], c2[3]);
        ROUNDED_GRADIENT.setupUniform4f("color3", c3[0], c3[1], c3[2], c3[3]);
        ROUNDED_GRADIENT.setupUniform4f("color4", c4[0], c4[1], c4[2], c4[3]);
        VisualHelpers.allocTextureRectangle(x, y, width, height);
        ROUNDED_GRADIENT.unloadProgram();
        RenderSystem.disableBlend();
    }

    public static void drawRoundedGradientRect(float x, float y, float width, float height, Vector4f round, int color1, int color2, int color3, int color4) {
        float[] c1 = ColorHelpers.getRGBAf(color1);
        float[] c2 = ColorHelpers.getRGBAf(color2);
        float[] c3 = ColorHelpers.getRGBAf(color3);
        float[] c4 = ColorHelpers.getRGBAf(color4);
        RenderSystem.color4f(0.0f, 0.0f, 0.0f, 0.0f);
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA.param, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA.param, GlStateManager.SourceFactor.ONE.param, GlStateManager.DestFactor.ZERO.param);
        ROUNDED_VECTOR_GRADIENT.useProgram();
        ROUNDED_VECTOR_GRADIENT.setupUniform2f("size", width, height);
        ROUNDED_VECTOR_GRADIENT.setupUniform4f("round", round.x, round.y, round.z, round.w);
        ROUNDED_VECTOR_GRADIENT.setupUniform4f("color1", c1[0], c1[1], c1[2], c1[3]);
        ROUNDED_VECTOR_GRADIENT.setupUniform4f("color2", c2[0], c2[1], c2[2], c2[3]);
        ROUNDED_VECTOR_GRADIENT.setupUniform4f("color3", c3[0], c3[1], c3[2], c3[3]);
        ROUNDED_VECTOR_GRADIENT.setupUniform4f("color4", c4[0], c4[1], c4[2], c4[3]);
        VisualHelpers.allocTextureRectangle(x, y, width, height);
        ROUNDED_VECTOR_GRADIENT.unloadProgram();
        RenderSystem.disableBlend();
    }

    public static void drawRoundedOutlineRect(float x, float y, float width, float height, float round, float thickness, int color) {
        float[] c = ColorHelpers.getRGBAf(color);
        RenderSystem.color4f(0.0f, 0.0f, 0.0f, 0.0f);
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA.param, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA.param, GlStateManager.SourceFactor.ONE.param, GlStateManager.DestFactor.ZERO.param);
        ROUNDED_OUTLINE.useProgram();
        ROUNDED_OUTLINE.setupUniform2f("size", width * 2.0f, height * 2.0f);
        ROUNDED_OUTLINE.setupUniform1f("round", round);
        ROUNDED_OUTLINE.setupUniform1f("thickness", thickness);
        ROUNDED_OUTLINE.setupUniform2f("smoothness", thickness - 1.5f, thickness);
        ROUNDED_OUTLINE.setupUniform4f("color", c[0], c[1], c[2], c[3]);
        VisualHelpers.allocTextureRectangle(x, y, width, height);
        ROUNDED_OUTLINE.unloadProgram();
        RenderSystem.disableBlend();
    }

    public static void drawCircle(float x, float y, float start, float end, float radius, int color, int linewidth) {
        RenderSystem.color4f(0.0f, 0.0f, 0.0f, 0.0f);
        if (start > end) {
            float endOffset = end;
            end = start;
            start = endOffset;
        }
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        VisualHelpers.enableSmoothLine(linewidth);
        RenderSystem.blendFuncSeparate(770, 771, 1, 0);
        GL11.glBegin(3);
        for (float i = end; i >= start; i -= 4.0f) {
            ColorHelpers.glHexColor(color, ColorHelpers.getRGBAf(color)[3] * 255.0f);
            float cos = (float)(Math.cos((double)i * Math.PI / 180.0) * (double)radius * 1.0);
            float sin = (float)(Math.sin((double)i * Math.PI / 180.0) * (double)radius * 1.0);
            GL11.glVertex2f(x + cos, y + sin);
        }
        GL11.glEnd();
        VisualHelpers.disableSmoothLine();
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }

    public static void drawLoadingCircle(float x, float y, float start, float end, float radius, int color, int linewidth) {
        RenderSystem.color4f(0.0f, 0.0f, 0.0f, 0.0f);
        if (start > end) {
            float endOffset = end;
            end = start;
            start = endOffset;
        }
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        VisualHelpers.enableSmoothLine(linewidth);
        RenderSystem.blendFuncSeparate(770, 771, 1, 0);
        GL11.glBegin(3);
        float alpha = ColorHelpers.getRGBAf(color)[3] * 255.0f;
        for (float i = end; i >= start; i -= 4.0f) {
            ColorHelpers.glHexColor(color, alpha -= 2.8f);
            float cos = (float)(Math.cos((double)i * Math.PI / 180.0) * (double)radius * 1.0);
            float sin = (float)(Math.sin((double)i * Math.PI / 180.0) * (double)radius * 1.0);
            GL11.glVertex2f(x + cos, y + sin);
        }
        GL11.glEnd();
        VisualHelpers.disableSmoothLine();
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }

    public static void drawImage(ResourceLocation tex, float x, float y, float width, float height) {
        mc.getTextureManager().bindTexture(tex);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        VisualHelpers.allocTextureRectangle(x, y, width, height);
        RenderSystem.bindTexture(0);
    }

    public static void drawImage(ResourceLocation tex, float x, float y, float width, float height, int color) {
        mc.getTextureManager().bindTexture(tex);
        float[] colorRGBA = ColorHelpers.getRGBAf(color);
        RenderSystem.color4f(colorRGBA[0], colorRGBA[1], colorRGBA[2], colorRGBA[3]);
        VisualHelpers.allocTextureRectangle(x, y, width, height);
        RenderSystem.bindTexture(0);
    }

    public static void drawImage2(ResourceLocation resourceLocation, float x, float y, float width, float height, int color) {
        RenderSystem.pushMatrix();
        RenderSystem.enableBlend();
        VisualHelpers.setColor(color);
        mc.getTextureManager().bindTexture(resourceLocation);
        AbstractGui.drawModalRectWithCustomSizedTexture(x, y, 0.0f, 0.0f, width, height, width, height);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.popMatrix();
    }

    public static void setColor(int color) {
        VisualHelpers.setColor(color, (float)(color >> 24 & 0xFF) / 255.0f);
    }

    public static void setColor(int color, float alpha) {
        float r = (float)(color >> 16 & 0xFF) / 255.0f;
        float g = (float)(color >> 8 & 0xFF) / 255.0f;
        float b = (float)(color & 0xFF) / 255.0f;
        RenderSystem.color4f(r, g, b, alpha);
    }

    public static void drawRoundedTexture(ResourceLocation tex, float x, float y, float width, float height, float round, float alpha) {
        RenderSystem.color4f(100.0f, 234.0f, 1.0f, 0.0f);
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA.param, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA.param, GlStateManager.SourceFactor.ONE.param, GlStateManager.DestFactor.ZERO.param);
        RenderSystem.disableAlphaTest();
        ROUNDED_TEXTURE.useProgram();
        ROUNDED_TEXTURE.setupUniform2f("size", (width - round) * 2.0f, (height - round) * 2.0f);
        ROUNDED_TEXTURE.setupUniform1f("round", round);
        ROUNDED_TEXTURE.setupUniform1f("alpha", alpha);
        VisualHelpers.drawImage(tex, x, y, width, height);
        ROUNDED_TEXTURE.unloadProgram();
        RenderSystem.disableBlend();
    }

    public static void drawRoundedTexture(ResourceLocation tex, float x, float y, float width, float height, float round, int color) {
        RenderSystem.color4f(100.0f, 234.0f, 1.0f, 0.0f);
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA.param, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA.param, GlStateManager.SourceFactor.ONE.param, GlStateManager.DestFactor.ZERO.param);
        RenderSystem.disableAlphaTest();
        float[] rgba = ColorHelpers.getRGBAf(color);
        ROUNDED_TEXTURE.useProgram();
        ROUNDED_TEXTURE.setupUniform2f("size", (width - round) * 2.0f, (height - round) * 2.0f);
        ROUNDED_TEXTURE.setupUniform1f("round", round);
        ROUNDED_TEXTURE.setupUniform1f("alpha", rgba[3]);
        VisualHelpers.drawImage(tex, x, y, width, height, color);
        ROUNDED_TEXTURE.unloadProgram();
        RenderSystem.disableBlend();
    }

    public static void drawRoundedTextureHead(ResourceLocation tex, float x, float y, float width, float height, float round, float alpha) {
        RenderSystem.color4f(100.0f, 234.0f, 1.0f, 0.0f);
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA.param, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA.param, GlStateManager.SourceFactor.ONE.param, GlStateManager.DestFactor.ZERO.param);
        RenderSystem.disableAlphaTest();
        ROUNDED_TEXTURE.useProgram();
        ROUNDED_TEXTURE.setupUniform2f("size", (width - round) * 2.0f, (height - round) * 2.0f);
        ROUNDED_TEXTURE.setupUniform1f("round", round);
        ROUNDED_TEXTURE.setupUniform1f("alpha", alpha);
        mc.getTextureManager().bindTexture(tex);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        VisualHelpers.allocTextureRectangle(x, y, width, height);
        VisualHelpers.drawScaledCustomSizeModalRect((int)x, (int)y, 8.0f, 8.0f, 8, 8, (int)width, (int)height, 64.0f, 64.0f);
        RenderSystem.bindTexture(0);
        ROUNDED_TEXTURE.unloadProgram();
        RenderSystem.disableBlend();
    }

    public static void allocTextureRectangle(float x, float y, float width, float height) {
        if (VisualHelpers.mc.gameSettings.ofFastRender) {
            return;
        }
        BUILDER.begin(7, DefaultVertexFormats.POSITION_TEX);
        BUILDER.pos(x, y, 0.0).tex(0.0f, 0.0f).endVertex();
        BUILDER.pos(x, y + height, 0.0).tex(0.0f, 1.0f).endVertex();
        BUILDER.pos(x + width, y + height, 0.0).tex(1.0f, 1.0f).endVertex();
        BUILDER.pos(x + width, y, 0.0).tex(1.0f, 0.0f).endVertex();
        TESSELLATOR.draw();
    }

    public static void drawFilledCircleNoGL(int x, int y, double r, int c, int quality) {
        float f = (float)(c >> 24 & 0xFF) / 255.0f;
        float f1 = (float)(c >> 16 & 0xFF) / 255.0f;
        float f2 = (float)(c >> 8 & 0xFF) / 255.0f;
        float f3 = (float)(c & 0xFF) / 255.0f;
        GL11.glColor4f(f1, f2, f3, f);
        GL11.glBegin(6);
        for (int i = 0; i <= 360 / quality; ++i) {
            double width = Math.sin((double)(i * quality) * Math.PI / 180.0) * r;
            double height = Math.cos((double)(i * quality) * Math.PI / 180.0) * r;
            GL11.glVertex2d((double)x + width, (double)y + height);
        }
        GL11.glEnd();
    }

    public static void drawGlowImage(MatrixStack stack, ResourceLocation image, double x, double y, double z, double width, double height, int color) {
        VisualHelpers.drawGlowImage(stack, image, x, y, z, width, height, color, color, color, color);
    }

    public static void drawGlowImage(MatrixStack stack, ResourceLocation image, double x, double y, double z, double width, double height, int color1, int color2) {
        VisualHelpers.drawGlowImage(stack, image, x, y, z, width, height, color1, color2, color2, color1);
    }

    public static void drawGlowImage(MatrixStack stack, ResourceLocation image, double x, double y, double z, double width, double height, int color1, int color2, int color3, int color4) {
        mc.getTextureManager().bindTexture(image);
        boolean blend = GL11.glIsEnabled(3042);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 1);
        GL11.glShadeModel(7425);
        GL11.glAlphaFunc(516, 0.0f);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        BUILDER.begin(7, DefaultVertexFormats.POSITION_COLOR_TEX_LIGHTMAP);
        BUILDER.pos(stack.getLast().getMatrix(), (float)x, (float)(y + height), (float)z).color(color1 >> 16 & 0xFF, color1 >> 8 & 0xFF, color1 & 0xFF, color1 >>> 24).tex(0.0f, 0.99f).lightmap(0, 240).endVertex();
        BUILDER.pos(stack.getLast().getMatrix(), (float)(x + width), (float)(y + height), (float)z).color(color2 >> 16 & 0xFF, color2 >> 8 & 0xFF, color2 & 0xFF, color2 >>> 24).tex(1.0f, 0.99f).lightmap(0, 240).endVertex();
        BUILDER.pos(stack.getLast().getMatrix(), (float)(x + width), (float)y, (float)z).color(color3 >> 16 & 0xFF, color3 >> 8 & 0xFF, color3 & 0xFF, color3 >>> 24).tex(1.0f, 0.0f).lightmap(0, 240).endVertex();
        BUILDER.pos(stack.getLast().getMatrix(), (float)x, (float)y, (float)z).color(color4 >> 16 & 0xFF, color4 >> 8 & 0xFF, color4 & 0xFF, color4 >>> 24).tex(0.0f, 0.0f).lightmap(0, 240).endVertex();
        TESSELLATOR.draw();
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glShadeModel(7424);
        GlStateManager.blendFunc(770, 0);
        if (!blend) {
            GlStateManager.disableBlend();
        }
    }
    @EventHook
    public static void drawTexture(MatrixStack matrixStack, ResourceLocation resourceLocation, float x, float y, float width, float height, int color1, int color2, int color3, int color4) {
        try {


            RenderSystem.pushMatrix();
            RenderSystem.enableBlend();

            RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA.param, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA.param, GlStateManager.SourceFactor.ONE.param, GlStateManager.DestFactor.ZERO.param);
            RenderSystem.shadeModel(7425);
            RenderSystem.disableAlphaTest();
            RenderSystem.depthMask(false);

            mc.getTextureManager().bindTexture(resourceLocation);

            Matrix4f matrix4f = matrixStack.getLast().getMatrix();


            BUILDER.begin(7, DefaultVertexFormats.POSITION_COLOR_TEX);
            BUILDER.pos(matrix4f, x, y, 0.0f).color((color1 >> 16 & 0xFF) / 255.0f, (color1 >> 8 & 0xFF) / 255.0f, (color1 & 0xFF) / 255.0f, (color1 >>> 24) / 255.0f).tex(0.0f, 0.0f).endVertex();
            BUILDER.pos(matrix4f, x, y + height, 0.0f).color((color2 >> 16 & 0xFF) / 255.0f, (color2 >> 8 & 0xFF) / 255.0f, (color2 & 0xFF) / 255.0f, (color2 >>> 24) / 255.0f).tex(0.0f, 1.0f).endVertex();
            BUILDER.pos(matrix4f, x + width, y + height, 0.0f).color((color3 >> 16 & 0xFF) / 255.0f, (color3 >> 8 & 0xFF) / 255.0f, (color3 & 0xFF) / 255.0f, (color3 >>> 24) / 255.0f).tex(1.0f, 1.0f).endVertex();
            BUILDER.pos(matrix4f, x + width, y, 0.0f).color((color4 >> 16 & 0xFF) / 255.0f, (color4 >> 8 & 0xFF) / 255.0f, (color4 & 0xFF) / 255.0f, (color4 >>> 24) / 255.0f).tex(1.0f, 0.0f).endVertex();
            TESSELLATOR.draw();



            RenderSystem.depthMask(true);
            RenderSystem.enableAlphaTest();
            RenderSystem.shadeModel(7424);
            RenderSystem.defaultBlendFunc();
            RenderSystem.disableBlend();
            RenderSystem.popMatrix();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void drawRect(float x, float y, float width, float height, int color) {
        VisualHelpers.drawGradientRect(x, y, width, height, color, color, color, color);
    }

    public static void drawVRect(float left, float top, float right, float bottom, int color) {
        if (left < right) {
            float i = left;
            left = right;
            right = i;
        }
        if (top < bottom) {
            float j = top;
            top = bottom;
            bottom = j;
        }
        float f3 = (float)(color >> 24 & 0xFF) / 255.0f;
        float f = (float)(color >> 16 & 0xFF) / 255.0f;
        float f1 = (float)(color >> 8 & 0xFF) / 255.0f;
        float f2 = (float)(color & 0xFF) / 255.0f;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA.param, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA.param, GlStateManager.SourceFactor.ONE.param, GlStateManager.DestFactor.ZERO.param);
        RenderSystem.color4f(f, f1, f2, f3);
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION);
        bufferbuilder.pos(left, bottom, 0.0).endVertex();
        bufferbuilder.pos(right, bottom, 0.0).endVertex();
        bufferbuilder.pos(right, top, 0.0).endVertex();
        bufferbuilder.pos(left, top, 0.0).endVertex();
        tessellator.draw();
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }

    public static void drawRoundedHead(MatrixStack matrixStack, float x, float y, float width, float height, float round, float alpha, AbstractClientPlayerEntity target) {
        try {
            ResourceLocation skin = target.getLocationSkin();
            mc.getTextureManager().bindTexture(skin);
            float hurtPercent = VisualHelpers.getHurtPercent(target);
            GL11.glPushMatrix();
            GL11.glEnable(3042);
            ROUNDED_HEAD.useProgram();
            ROUNDED_HEAD.setupUniform4f("round", round, round, round, round);
            ROUNDED_HEAD.setupUniform4f("pos", 0.125f, 0.125f, 0.125f, 0.125f);
            ROUNDED_HEAD.setupUniform2f("size", width, height);
            ROUNDED_HEAD.setupUniform1i("texture", 0);
            ROUNDED_HEAD.setupUniform1f("alpha", alpha);
            ROUNDED_HEAD.setupUniform1f("hurtPercent", hurtPercent);
            VisualHelpers.allocateTextureRect(matrixStack, x, y, width, height);
            ROUNDED_HEAD.unloadProgram();
            GL11.glPopMatrix();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void drawRoundedHead(MatrixStack matrixStack, float x, float y, float width, float height, float round, float alpha, ResourceLocation skin, float hurtPercent) {
        try {
            ROUNDED_HEAD.useProgram();
            ROUNDED_HEAD.setupUniform4f("round", round, round, round, round);
            ROUNDED_HEAD.setupUniform4f("pos", 0.125f, 0.125f, 0.125f, 0.125f);
            ROUNDED_HEAD.setupUniform2f("size", width, height);
            ROUNDED_HEAD.setupUniform1i("texture", 0);
            ROUNDED_HEAD.setupUniform1f("alpha", alpha);
            ROUNDED_HEAD.setupUniform1f("hurtPercent", hurtPercent);
            mc.getTextureManager().bindTexture(skin);
            VisualHelpers.allocateTextureRect(matrixStack, x, y, width, height);
            RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            RenderSystem.bindTexture(0);
            ROUNDED_HEAD.unloadProgram();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void drawFace(int alpha, float d, float y, float u, float v, float uWidth, float vHeight, float width, float height, float tileWidth, float tileHeight, AbstractClientPlayerEntity target) {
        try {
            GL11.glPushMatrix();
            GL11.glEnable(3042);
            ResourceLocation skin = target.getLocationSkin();
            Minecraft.getInstance().getTextureManager().bindTexture(skin);
            float hurtPercent = VisualHelpers.getHurtPercent(target);
            GL11.glColor4f(1.0f, 1.0f - hurtPercent, 1.0f - hurtPercent, (float)alpha / 255.0f);
            VisualHelpers.drawScaledCustomSizeModalRect((int)d, (int)y, u, v, (int)uWidth, (int)vHeight, (int)width, (int)height, (int)tileWidth, (int)tileHeight);
            RenderSystem.clearCurrentColor();
            GL11.glPopMatrix();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void drawScaledCustomSizeModalRect(int x, int y, float u, float v, int uWidth, int vHeight, int width, int height, float tileWidth, float tileHeight) {
        float f = 1.0f / tileWidth;
        float f1 = 1.0f / tileHeight;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos(x, y + height, 0.0).tex(u * f, (v + (float)vHeight) * f1).endVertex();
        bufferbuilder.pos(x + width, y + height, 0.0).tex((u + (float)uWidth) * f, (v + (float)vHeight) * f1).endVertex();
        bufferbuilder.pos(x + width, y, 0.0).tex((u + (float)uWidth) * f, v * f1).endVertex();
        bufferbuilder.pos(x, y, 0.0).tex(u * f, v * f1).endVertex();
        tessellator.draw();
    }

    private static float getRenderHurtTime(LivingEntity hurt) {
        return (float)hurt.hurtTime - (hurt.hurtTime != 0 ? VisualHelpers.mc.getTimer().renderPartialTicks : 0.0f);
    }

    private static float getHurtPercent(LivingEntity hurt) {
        return VisualHelpers.getRenderHurtTime(hurt) / 15.0f;
    }

    public static void drawCRect(float x, float y, float width, float height, int color) {
        VisualHelpers.drawGradientRect(x, y, width - x, height, color, color, color, color);
    }

    public static void drawGradientRect(float x, float y, float width, float height, int color1, int color2, int color3, int color4) {
        float[] c1 = ColorHelpers.getRGBAf(color1);
        float[] c2 = ColorHelpers.getRGBAf(color2);
        float[] c3 = ColorHelpers.getRGBAf(color3);
        float[] c4 = ColorHelpers.getRGBAf(color4);
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA.param, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA.param, GlStateManager.SourceFactor.ONE.param, GlStateManager.DestFactor.ZERO.param);
        RenderSystem.shadeModel(7425);
        BUILDER.begin(7, DefaultVertexFormats.POSITION_COLOR);
        BUILDER.pos(x, height + y, 0.0).color(c1[0], c1[1], c1[2], c1[3]).endVertex();
        BUILDER.pos(width + x, height + y, 0.0).color(c2[0], c2[1], c2[2], c2[3]).endVertex();
        BUILDER.pos(width + x, y, 0.0).color(c3[0], c3[1], c3[2], c3[3]).endVertex();
        BUILDER.pos(x, y, 0.0).color(c4[0], c4[1], c4[2], c4[3]).endVertex();
        TESSELLATOR.draw();
        RenderSystem.shadeModel(7424);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }

    public static void drawAnimatedGradientRect(float x, float y, float width, float height, int speed, int index, int color1, int color2, int color3, int color4) {
        float[] c1 = ColorHelpers.getRGBAf(ColorHelpers.interpolateColorsBackAndForth(speed, index, color1, color2));
        float[] c2 = ColorHelpers.getRGBAf(ColorHelpers.interpolateColorsBackAndForth(speed, index, color2, color3));
        float[] c3 = ColorHelpers.getRGBAf(ColorHelpers.interpolateColorsBackAndForth(speed, index, color4, color4));
        float[] c4 = ColorHelpers.getRGBAf(ColorHelpers.interpolateColorsBackAndForth(speed, index, color4, color1));
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA.param, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA.param, GlStateManager.SourceFactor.ONE.param, GlStateManager.DestFactor.ZERO.param);
        RenderSystem.shadeModel(7425);
        BUILDER.begin(7, DefaultVertexFormats.POSITION_COLOR);
        BUILDER.pos(x, height + y, 0.0).color(c1[0], c1[1], c1[2], c1[3]).endVertex();
        BUILDER.pos(width + x, height + y, 0.0).color(c2[0], c2[1], c2[2], c2[3]).endVertex();
        BUILDER.pos(width + x, y, 0.0).color(c3[0], c3[1], c3[2], c3[3]).endVertex();
        BUILDER.pos(x, y, 0.0).color(c4[0], c4[1], c4[2], c4[3]).endVertex();
        TESSELLATOR.draw();
        RenderSystem.shadeModel(7424);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }

    public static void drawRectForMac(float x, float y, float width, float height, int softness, int color) {
        float[] c = ColorHelpers.getRGBAf(color);
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        RenderSystem.disableTexture();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA.param, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA.param, GlStateManager.SourceFactor.ONE.param, GlStateManager.DestFactor.ZERO.param);
        RenderSystem.shadeModel(7425);
        BUILDER.begin(7, DefaultVertexFormats.POSITION_COLOR);
        BUILDER.pos(x, height + y, 0.0).color(c[0], c[1], c[2], c[3] - 150.0f).endVertex();
        BUILDER.pos(width + x, height + y, 0.0).color(c[0], c[1], c[2], c[3] - 170.0f).endVertex();
        BUILDER.pos(width + x, y, 0.0).color(c[0], c[1], c[2], c[3] - 170.0f).endVertex();
        BUILDER.pos(x, y, 0.0).color(c[0], c[1], c[2], c[3] - 170.0f).endVertex();
        TESSELLATOR.draw();
        RenderSystem.shadeModel(7424);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
        RenderSystem.disableDepthTest();
    }

    public static void drawTriangleSingle(float x, float z, float distance, int color) {
        float pt = mc.getRenderPartialTicks();
        float playerX = (float)(VisualHelpers.mc.player.prevPosX + (VisualHelpers.mc.player.getPosX() - VisualHelpers.mc.player.prevPosX) * (double)pt);
        float playerZ = (float)(VisualHelpers.mc.player.prevPosZ + (VisualHelpers.mc.player.getPosZ() - VisualHelpers.mc.player.prevPosZ) * (double)pt);
        float playerYaw = VisualHelpers.mc.player.prevRotationYaw + (VisualHelpers.mc.player.rotationYaw - VisualHelpers.mc.player.prevRotationYaw) * pt;
        float radian = (float)(Math.atan2(z - playerZ, x - playerX) - Math.toRadians(playerYaw + 180.0f));
        float degree = (float)Math.toDegrees(radian);
        float cos = MathHelper.cos(radian);
        float sin = MathHelper.sin(radian);
        float centerX = (float)mc.getMainWindow().getWidth() / 4.0f;
        float centerY = (float)mc.getMainWindow().getHeight() / 4.0f;
        RenderSystem.enableBlend();
        RenderSystem.pushMatrix();
        RenderSystem.translatef(centerX + distance * cos, centerY + distance * sin, 0.0f);
        RenderSystem.rotatef(degree + 90.0f, 0.0f, 0.0f, 1.0f);
        float[] colors = ColorHelpers.getRGBAf(color);
        float width = 6.0f;
        float height = 12.0f;
        RenderSystem.disableTexture();
        GL11.glEnable(2881);
        RenderSystem.shadeModel(7425);
        BUILDER.begin(4, DefaultVertexFormats.POSITION_COLOR);
        BUILDER.pos(0.0, 0.0f - height, 0.0).color(colors[0], colors[1], colors[2], colors[3]).endVertex();
        BUILDER.pos(0.0f - width, 0.0, 0.0).color(colors[0], colors[1], colors[2], colors[3]).endVertex();
        BUILDER.pos(0.0, -3.0, 0.0).color(colors[0], colors[1], colors[2], colors[3]).endVertex();
        float r = Math.max(colors[0] - 0.1f, 0.0f);
        float g = Math.max(colors[1] - 0.1f, 0.0f);
        float b = Math.max(colors[2] - 0.1f, 0.0f);
        BUILDER.pos(0.0, 0.0f - height, 0.0).color(r, g, b, colors[3]).endVertex();
        BUILDER.pos(0.0, -3.0, 0.0).color(r, g, b, colors[3]).endVertex();
        BUILDER.pos(0.0f + width, 0.0, 0.0).color(r, g, b, colors[3]).endVertex();
        TESSELLATOR.draw();
        RenderSystem.shadeModel(7424);
        GL11.glDisable(2881);
        RenderSystem.enableTexture();
        RenderSystem.popMatrix();
        RenderSystem.disableBlend();
    }

    public static void drawTriangleCustom(int color) {
        RenderSystem.enableBlend();
        float[] colors = ColorHelpers.getRGBAf(color);
        float width = 6.0f;
        float height = 12.0f;
        RenderSystem.disableTexture();
        GL11.glEnable(2881);
        RenderSystem.shadeModel(7425);
        BUILDER.begin(4, DefaultVertexFormats.POSITION_COLOR);
        BUILDER.pos(0.0, 0.0f - height, 0.0).color(colors[0], colors[1], colors[2], colors[3]).endVertex();
        BUILDER.pos(0.0f - width, 0.0, 0.0).color(colors[0], colors[1], colors[2], colors[3]).endVertex();
        BUILDER.pos(0.0, -3.0, 0.0).color(colors[0], colors[1], colors[2], colors[3]).endVertex();
        float r = Math.max(colors[0] - 0.1f, 0.0f);
        float g = Math.max(colors[1] - 0.1f, 0.0f);
        float b = Math.max(colors[2] - 0.1f, 0.0f);
        BUILDER.pos(0.0, 0.0f - height, 0.0).color(r, g, b, colors[3]).endVertex();
        BUILDER.pos(0.0, -3.0, 0.0).color(r, g, b, colors[3]).endVertex();
        BUILDER.pos(0.0f + width, 0.0, 0.0).color(r, g, b, colors[3]).endVertex();
        TESSELLATOR.draw();
        RenderSystem.shadeModel(7424);
        GL11.glDisable(2881);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }

    public static void drawHorizontalShadow(float x, float y, float width, float height, int glowRadius, int color1, int color2) {
        BufferedImage original = null;
        GaussianFilter op = null;
        GL11.glPushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableAlphaTest();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.shadeModel(7425);
        GlStateManager.alphaFunc(516, 0.01f);
        float _X = (x -= (float)glowRadius) - 0.25f;
        float _Y = (y -= (float)glowRadius) + 0.25f;
        int identifier = String.valueOf((width += (float)(glowRadius * 2)) * (height += (float)(glowRadius * 2)) + width + (float)(1000000000 * glowRadius) + (float)glowRadius).hashCode();
        GL11.glEnable(3553);
        GL11.glDisable(2884);
        GL11.glEnable(3008);
        GlStateManager.enableBlend();
        int texId = -1;
        if (shadowCache.containsKey(identifier)) {
            texId = shadowCache.get(identifier);
            GlStateManager.bindTexture(texId);
        } else {
            if (width <= 0.0f) {
                width = 1.0f;
            }
            if (height <= 0.0f) {
                height = 1.0f;
            }
            original = new BufferedImage((int)width, (int)height, 3);
            Graphics g = original.getGraphics();
            g.setColor(Color.white);
            g.fillRect(glowRadius, glowRadius, (int)width - glowRadius * 2, (int)height - glowRadius * 2);
            g.dispose();
            op = new GaussianFilter(glowRadius);
            BufferedImage blurred = op.filter(original, null);
            texId = TextureHelpers.uploadTextureImageAllocate(GlStateManager.genTexture(), blurred, true, false);
            shadowCache.put(identifier, texId);
        }
        GL11.glBegin(7);
        ColorHelpers.glHexColor(color1);
        GL11.glTexCoord2f(0.0f, 0.0f);
        GL11.glVertex2f(_X, _Y);
        GL11.glTexCoord2f(0.0f, 1.0f);
        GL11.glVertex2f(_X, _Y + height);
        ColorHelpers.glHexColor(color1,100);
        GL11.glTexCoord2f(1.0f, 1.0f);
        GL11.glVertex2f(_X + width, _Y + height);
        GL11.glTexCoord2f(1.0f, 0.0f);
        GL11.glVertex2f(_X + width, _Y);
        GL11.glEnd();
        GlStateManager.enableTexture();
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlphaTest();
        GlStateManager.clearCurrentColor();
        GL11.glEnable(2884);
        GL11.glPopMatrix();
    }

    public static void drawShadow(float x, float y, float width, float height, int glowRadius, int color) {
        BufferedImage original = null;
        GaussianFilter op = null;
        RenderSystem.pushMatrix();
        RenderSystem.alphaFunc(516, 0.01f);
        float _X = (x -= (float)glowRadius) - 0.25f;
        float _Y = (y -= (float)glowRadius) + 0.25f;
        int identifier = String.valueOf((width += (float)(glowRadius * 2)) * (height += (float)(glowRadius * 2)) + width + (float)(1000000000 * glowRadius) + (float)glowRadius).hashCode();
        RenderSystem.enableTexture();
        RenderSystem.disableCull();
        RenderSystem.enableAlphaTest();
        RenderSystem.enableBlend();
        int texId = -1;
        if (shadowCache.containsKey(identifier)) {
            texId = shadowCache.get(identifier);
            RenderSystem.bindTexture(texId);
        } else {
            if (width <= 0.0f) {
                width = 1.0f;
            }
            if (height <= 0.0f) {
                height = 1.0f;
            }
            if (original == null) {
                original = new BufferedImage((int)width, (int)height, 3);
            }
            Graphics g = original.getGraphics();
            g.setColor(Color.white);
            g.fillRect(glowRadius, glowRadius, (int)(width - (float)(glowRadius * 2)), (int)(height - (float)(glowRadius * 2)));
            g.dispose();
            if (op == null) {
                op = new GaussianFilter(glowRadius);
            }
            BufferedImage blurred = op.filter(original, null);
            texId = TextureHelpers.uploadTextureImageAllocate(GlStateManager.genTexture(), blurred, true, false);
            shadowCache.put(identifier, texId);
        }
        ColorHelpers.glHexColor(color);
        GL11.glBegin(7);
        GL11.glTexCoord2f(0.0f, 0.0f);
        GL11.glVertex2f(_X, _Y);
        GL11.glTexCoord2f(0.0f, 1.0f);
        GL11.glVertex2f(_X, _Y + height);
        GL11.glTexCoord2f(1.0f, 1.0f);
        GL11.glVertex2f(_X + width, _Y + height);
        GL11.glTexCoord2f(1.0f, 0.0f);
        GL11.glVertex2f(_X + width, _Y);
        GL11.glEnd();
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
        RenderSystem.clearCurrentColor();
        RenderSystem.enableCull();
        RenderSystem.popMatrix();
    }

    public static void drawRoundedOutline(float x, float y, float width, float height, float round, float outlineWidth, int color) {
        VisualHelpers.drawRoundedOutlineGradient(x, y, width, height, round, outlineWidth, color, color, color, color);
    }

    public static void drawRoundedOutlineHorLinearGradient(float x, float y, float width, float height, float round, float outlineWidth, int colorLeft, int colorRight) {
        VisualHelpers.drawRoundedOutlineGradient(x, y, width, height, round, outlineWidth, colorLeft, colorRight, colorRight, colorLeft);
    }

    public static void drawRoundedOutlineVerLinearGradient(float x, float y, float width, float height, float round, float outlineWidth, int colorTop, int colorBottom) {
        VisualHelpers.drawRoundedOutlineGradient(x, y, width, height, round, outlineWidth, colorTop, colorTop, colorBottom, colorBottom);
    }

    public static void drawRoundedOutlineDiagonalGradient(float x, float y, float width, float height, float round, float outlineWidth, int color1, int color2) {
        int cornerColor = ColorHelpers.interpolateColor(color1, color2, 0.5);
        VisualHelpers.drawRoundedOutlineGradient(x, y, width, height, round, outlineWidth, color1, cornerColor, color2, cornerColor);
    }

    public static void drawRoundedOutlineGradient(float x, float y, float width, float height, float round, float outlineWidth, int color1, int color2, int color3, int color4) {
        VisualHelpers.drawOutlineGradient(x, y, width, height, round, round, round, round, outlineWidth, color1, color2, color3, color4);
    }

    public static void drawOutlineGradient(float x, float y, float width, float height, float roundTopLeft, float roundTopRight, float roundBottomRight, float roundBottomLeft, float outlineWidth, int color1, int color2, int color3, int color4) {
        float[] c1 = ColorHelpers.getRGBAf(color1);
        float[] c2 = ColorHelpers.getRGBAf(color2);
        float[] c3 = ColorHelpers.getRGBAf(color4);
        float[] c4 = ColorHelpers.getRGBAf(color3);
        RenderSystem.color4f(0.0f, 0.0f, 0.0f, 0.0f);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableAlphaTest();
        RenderSystem.alphaFunc(516, 0.0f);
        ROUNDED_OUTLINE.useProgram();
        ROUNDED_OUTLINE.setupUniform2f("size", width, height);
        ROUNDED_OUTLINE.setupUniform4f("round", roundTopLeft, roundTopRight, roundBottomRight, roundBottomLeft);
        ROUNDED_OUTLINE.setupUniform4f("color1", c1[0], c1[1], c1[2], c1[3]);
        ROUNDED_OUTLINE.setupUniform4f("color2", c2[0], c2[1], c2[2], c2[3]);
        ROUNDED_OUTLINE.setupUniform4f("color3", c3[0], c3[1], c3[2], c3[3]);
        ROUNDED_OUTLINE.setupUniform4f("color4", c4[0], c4[1], c4[2], c4[3]);
        ROUNDED_OUTLINE.setupUniform1f("outlineWidth", outlineWidth);
        VisualHelpers.allocTextureRectangle(x, y, width, height);
        ROUNDED_OUTLINE.unloadProgram();
        RenderSystem.disableAlphaTest();
        RenderSystem.disableBlend();
    }

    public static void disableSmoothLine() {
        GL11.glEnable(3553);
        GL11.glEnable(2929);
        GL11.glDisable(3042);
        GL11.glEnable(3008);
        GL11.glDepthMask(true);
        GL11.glCullFace(1029);
        GL11.glDisable(2848);
        GL11.glHint(3154, 4352);
        GL11.glHint(3155, 4352);
    }

    public static void enableSmoothLine(float width) {
        GL11.glDisable(3008);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(3553);
        GL11.glDisable(2929);
        GL11.glDepthMask(false);
        GL11.glEnable(2884);
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        GL11.glHint(3155, 4354);
        GL11.glLineWidth(width);
    }

    public static void drawGradientRect(MatrixStack matrixStack, float x, float y, float width, float height, int colorFrom, int colorTo) {
        VisualHelpers.drawGradientRect(matrixStack, x, y, width, height, colorFrom, colorFrom, colorTo, colorTo);
    }

    public static void drawGradientRect(MatrixStack matrixStack, float x, float y, float width, float height, int colorFrom, int colorTo, int colorFrom2, int colorTo2) {
        matrixStack.push();
        matrixStack.translate(x, y, 0.0);
        matrixStack.scale(width, height, 1.0f);
        VisualHelpers.fillGradient(matrixStack, 0, 0, 1, 1, colorFrom, colorTo, colorFrom2, colorTo2);
        matrixStack.pop();
    }

    public static void drawRoundedRect(MatrixStack matrixStack, float x, float y, float width, float height, float round, int color) {
        VisualHelpers.drawRoundedGradientRect(matrixStack, x, y, width, height, round, color, color, color, color);
    }

    public static void drawRoundedRect(MatrixStack matrixStack, float x, float y, float width, float height, float round, int firstColor, int secondColor) {
        VisualHelpers.drawRoundedGradientRect(matrixStack, x, y, width, height, round, firstColor, firstColor, secondColor, secondColor);
    }

    public static void drawRoundedRect(MatrixStack matrixStack, float x, float y, float width, float height, float round, int firstColor, int secondColor, int thirdColor, int fourthColor) {
        VisualHelpers.drawRoundedGradientRect(matrixStack, x, y, width, height, round, firstColor, secondColor, thirdColor, fourthColor);
    }

    public static void drawRoundedRect(MatrixStack matrixStack, float x, float y, float width, float height, Vector4f round, int color) {
        VisualHelpers.drawRoundedGradientRect(matrixStack, x, y, width, height, round, color, color, color, color);
    }

    public static void drawRoundedRect(MatrixStack matrixStack, float x, float y, float width, float height, Vector4f round, int firstColor, int secondColor) {
        VisualHelpers.drawRoundedGradientRect(matrixStack, x, y, width, height, round, firstColor, firstColor, secondColor, secondColor);
    }

    public static void drawRoundedRect(MatrixStack matrixStack, float x, float y, float width, float height, Vector4f round, int firstColor, int secondColor, int thirdColor, int fourthColor) {
        VisualHelpers.drawRoundedGradientRect(matrixStack, x, y, width, height, round, firstColor, secondColor, thirdColor, fourthColor);
    }

    public static void drawRoundedHorLinearGradientRect(MatrixStack matrixStack, float x, float y, float width, float height, float round, int colorLeft, int colorRight) {
        VisualHelpers.drawRoundedGradientRect(matrixStack, x, y, width, height, round, colorLeft, colorRight, colorRight, colorLeft);
    }

    public static void drawRoundedVerLinearGradientRect(MatrixStack matrixStack, float x, float y, float width, float height, float round, int colorTop, int colorBottom) {
        VisualHelpers.drawRoundedGradientRect(matrixStack, x, y, width, height, round, colorTop, colorTop, colorBottom, colorBottom);
    }

    public static void drawRoundedDiagonalGradientRect(MatrixStack matrixStack, float x, float y, float width, float height, float round, int color1, int color2) {
        int cornerColor = ColorHelpers.interpolateColor(color1, color2, 0.5);
        VisualHelpers.drawRoundedGradientRect(matrixStack, x, y, width, height, round, color1, cornerColor, color2, cornerColor);
    }

    public static void drawRoundedGradientRect(MatrixStack matrixStack, float x, float y, float width, float height, float round, int color1, int color2, int color3, int color4) {
        float[] c1 = ColorHelpers.getRGBAf(color1);
        float[] c2 = ColorHelpers.getRGBAf(color2);
        float[] c3 = ColorHelpers.getRGBAf(color4);
        float[] c4 = ColorHelpers.getRGBAf(color3);
        RenderSystem.color4f(0.0f, 0.0f, 0.0f, 0.0f);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableAlphaTest();
        RenderSystem.alphaFunc(516, 0.0f);
        ROUNDED_GRADIENT.useProgram();
        ROUNDED_GRADIENT.setupUniform2f("size", width, height);
        ROUNDED_GRADIENT.setupUniform4f("round", round, round, round, round);
        ROUNDED_GRADIENT.setupUniform4f("color1", c1[0], c1[1], c1[2], c1[3]);
        ROUNDED_GRADIENT.setupUniform4f("color2", c2[0], c2[1], c2[2], c2[3]);
        ROUNDED_GRADIENT.setupUniform4f("color3", c3[0], c3[1], c3[2], c3[3]);
        ROUNDED_GRADIENT.setupUniform4f("color4", c4[0], c4[1], c4[2], c4[3]);
        VisualHelpers.allocateTextureRect(matrixStack, x, y, width, height);
        ROUNDED_GRADIENT.unloadProgram();
        RenderSystem.disableAlphaTest();
        RenderSystem.disableBlend();
    }

    public static void drawRoundedGradientRect(MatrixStack matrixStack, float x, float y, float width, float height, Vector4f round, int color1, int color2, int color3, int color4) {
        float[] c1 = ColorHelpers.getRGBAf(color1);
        float[] c2 = ColorHelpers.getRGBAf(color2);
        float[] c3 = ColorHelpers.getRGBAf(color4);
        float[] c4 = ColorHelpers.getRGBAf(color3);
        RenderSystem.color4f(0.0f, 0.0f, 0.0f, 0.0f);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableAlphaTest();
        RenderSystem.alphaFunc(516, 0.0f);
        ROUNDED_VECTOR_GRADIENT.useProgram();
        ROUNDED_VECTOR_GRADIENT.setupUniform2f("size", width, height);
        ROUNDED_VECTOR_GRADIENT.setupUniform4f("round", round.x, round.y, round.z, round.w);
        ROUNDED_VECTOR_GRADIENT.setupUniform4f("color1", c1[0], c1[1], c1[2], c1[3]);
        ROUNDED_VECTOR_GRADIENT.setupUniform4f("color2", c2[0], c2[1], c2[2], c2[3]);
        ROUNDED_VECTOR_GRADIENT.setupUniform4f("color3", c3[0], c3[1], c3[2], c3[3]);
        ROUNDED_VECTOR_GRADIENT.setupUniform4f("color4", c4[0], c4[1], c4[2], c4[3]);
        VisualHelpers.allocateTextureRect(matrixStack, x, y, width, height);
        ROUNDED_VECTOR_GRADIENT.unloadProgram();
        RenderSystem.disableAlphaTest();
        RenderSystem.disableBlend();
    }

    public static void drawRoundedOutline(MatrixStack matrixStack, float x, float y, float width, float height, float round, float outlineWidth, int color) {
        VisualHelpers.drawRoundedOutlineGradient(matrixStack, x, y, width, height, round, outlineWidth, color, color, color, color);
    }

    public static void drawRoundedOutlineHorLinearGradient(MatrixStack matrixStack, float x, float y, float width, float height, float round, float outlineWidth, int colorLeft, int colorRight) {
        VisualHelpers.drawRoundedOutlineGradient(matrixStack, x, y, width, height, round, outlineWidth, colorLeft, colorRight, colorRight, colorLeft);
    }

    public static void drawRoundedOutlineVerLinearGradient(MatrixStack matrixStack, float x, float y, float width, float height, float round, float outlineWidth, int colorTop, int colorBottom) {
        VisualHelpers.drawRoundedOutlineGradient(matrixStack, x, y, width, height, round, outlineWidth, colorTop, colorTop, colorBottom, colorBottom);
    }

    public static void drawRoundedOutlineDiagonalGradient(MatrixStack matrixStack, float x, float y, float width, float height, float round, float outlineWidth, int color1, int color2) {
        int cornerColor = ColorHelpers.interpolateColor(color1, color2, 0.5);
        VisualHelpers.drawRoundedOutlineGradient(matrixStack, x, y, width, height, round, outlineWidth, color1, cornerColor, color2, cornerColor);
    }

    public static void drawRoundedOutlineGradient(MatrixStack matrixStack, float x, float y, float width, float height, float round, float outlineWidth, int color1, int color2, int color3, int color4) {
        VisualHelpers.drawRoundedOutlineGradient(matrixStack, x, y, width, height, round, round, round, round, outlineWidth, color1, color2, color3, color4);
    }

    public static void drawGlow(MatrixStack matrixStack, float x, float y, float width, float height, float radius, int color) {
        VisualHelpers.drawRoundedRect(matrixStack, x + 0.5f - radius, y - height + 0.5f, width / 2.0f + radius, height, 0.0f, ColorHelpers.rgba(0, 0, 0, 0), ColorHelpers.rgba(0, 0, 0, 0), ColorHelpers.rgba(0, 0, 0, 0), color);
        VisualHelpers.drawRoundedRect(matrixStack, x + 0.5f, y - height + 0.5f, width / 2.0f + radius, height, 0.0f, ColorHelpers.rgba(0, 0, 0, 0), ColorHelpers.rgba(0, 0, 0, 0), color, ColorHelpers.rgba(0, 0, 0, 0));
        VisualHelpers.drawRoundedRect(matrixStack, x + 0.5f - radius, y - 0.5f, width / 2.0f + radius, height, 0.0f, ColorHelpers.rgba(0, 0, 0, 0), color, ColorHelpers.rgba(0, 0, 0, 0), ColorHelpers.rgba(0, 0, 0, 0));
        VisualHelpers.drawRoundedRect(matrixStack, x + width / 2.0f - 0.5f, y - 0.5f, width + radius, height, 0.0f, color, ColorHelpers.rgba(0, 0, 0, 0), ColorHelpers.rgba(0, 0, 0, 0), ColorHelpers.rgba(0, 0, 0, 0));
    }

    public static void drawRoundedOutlineGradient(MatrixStack matrixStack, float x, float y, float width, float height, float roundTopLeft, float roundTopRight, float roundBottomRight, float roundBottomLeft, float outlineWidth, int color1, int color2, int color3, int color4) {
        float[] c1 = ColorHelpers.getRGBAf(color1);
        float[] c2 = ColorHelpers.getRGBAf(color2);
        float[] c3 = ColorHelpers.getRGBAf(color4);
        float[] c4 = ColorHelpers.getRGBAf(color3);
        RenderSystem.color4f(0.0f, 0.0f, 0.0f, 0.0f);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableAlphaTest();
        RenderSystem.alphaFunc(516, 0.0f);
        ROUNDED_OUTLINE.useProgram();
        ROUNDED_OUTLINE.setupUniform2f("size", width, height);
        ROUNDED_OUTLINE.setupUniform4f("round", roundTopLeft, roundTopRight, roundBottomRight, roundBottomLeft);
        ROUNDED_OUTLINE.setupUniform4f("color1", c1[0], c1[1], c1[2], c1[3]);
        ROUNDED_OUTLINE.setupUniform4f("color2", c2[0], c2[1], c2[2], c2[3]);
        ROUNDED_OUTLINE.setupUniform4f("color3", c3[0], c3[1], c3[2], c3[3]);
        ROUNDED_OUTLINE.setupUniform4f("color4", c4[0], c4[1], c4[2], c4[3]);
        ROUNDED_OUTLINE.setupUniform1f("outlineWidth", outlineWidth);
        VisualHelpers.allocateTextureRect(matrixStack, x - outlineWidth / 2.0f, y - outlineWidth / 2.0f, width + outlineWidth, height + outlineWidth);
        ROUNDED_OUTLINE.unloadProgram();
        RenderSystem.disableAlphaTest();
        RenderSystem.disableBlend();
    }

    public static void drawRect(MatrixStack matrixStack, float x, float y, float width, float height, int color) {
        matrixStack.push();
        matrixStack.translate(x, y, 0.0);
        matrixStack.scale(width, height, 1.0f);
        VisualHelpers.fill(matrixStack, 0, 0, 1, 1, color);
        matrixStack.pop();
    }

    public static void drawRoundedTexture(MatrixStack matrixStack, ResourceLocation tex, float x, float y, float x2, float y2, float round, float alpha) {
        RenderSystem.color4f(100.0f, 234.0f, 1.0f, 0.0f);
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA.param, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA.param, GlStateManager.SourceFactor.ONE.param, GlStateManager.DestFactor.ZERO.param);
        RenderSystem.disableAlphaTest();
        ROUNDED_TEXTURE.useProgram();
        ROUNDED_TEXTURE.setupUniform2f("size", (x2 - round) * 2.0f, (y2 - round) * 2.0f);
        ROUNDED_TEXTURE.setupUniform1f("round", round);
        ROUNDED_TEXTURE.setupUniform1f("alpha", alpha);
        VisualHelpers.drawImage(matrixStack, tex, x, y, x2, y2);
        ROUNDED_TEXTURE.unloadProgram();
        RenderSystem.disableBlend();
    }

    public static void drawRoundedTexture(MatrixStack matrixStack, ResourceLocation tex, float x, float y, float x2, float y2, float round, int color) {
        float[] alpha = ColorHelpers.getRGBAf(color);
        RenderSystem.color4f(100.0f, 234.0f, 1.0f, 0.0f);
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA.param, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA.param, GlStateManager.SourceFactor.ONE.param, GlStateManager.DestFactor.ZERO.param);
        RenderSystem.disableAlphaTest();
        ROUNDED_TEXTURE.useProgram();
        ROUNDED_TEXTURE.setupUniform2f("size", (x2 - round) * 2.0f, (y2 - round) * 2.0f);
        ROUNDED_TEXTURE.setupUniform1f("round", round);
        ROUNDED_TEXTURE.setupUniform1f("alpha", 1.0f);
        VisualHelpers.drawImage(matrixStack, tex, x, y, x2, y2, color);
        ROUNDED_TEXTURE.unloadProgram();
        RenderSystem.disableBlend();
    }

    public static void drawBoxLineVer(double x, double y, double width, double height, int start, int end) {
        float f = (float)(start >> 24 & 0xFF) / 255.0f;
        float f1 = (float)(start >> 16 & 0xFF) / 255.0f;
        float f2 = (float)(start >> 8 & 0xFF) / 255.0f;
        float f3 = (float)(start & 0xFF) / 255.0f;
        float f4 = (float)(end >> 24 & 0xFF) / 255.0f;
        float f5 = (float)(end >> 16 & 0xFF) / 255.0f;
        float f6 = (float)(end >> 8 & 0xFF) / 255.0f;
        float f7 = (float)(end & 0xFF) / 255.0f;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.pos(x, height, 0.0).color(f1, f2, f3, f).endVertex();
        bufferbuilder.pos(width, height, 0.0).color(f1, f2, f3, f).endVertex();
        bufferbuilder.pos(width, y, 0.0).color(f5, f6, f7, f4).endVertex();
        bufferbuilder.pos(x, y, 0.0).color(f5, f6, f7, f4).endVertex();
    }

    public static void drawRectVerHor(double left, double top, double right, double bottom, int color) {
        if (left < right) {
            double i = left;
            left = right;
            right = i;
        }
        if (top < bottom) {
            double j = top;
            top = bottom;
            bottom = j;
        }
        float f3 = (float)(color >> 24 & 0xFF) / 255.0f;
        float f = (float)(color >> 16 & 0xFF) / 255.0f;
        float f1 = (float)(color >> 8 & 0xFF) / 255.0f;
        float f2 = (float)(color & 0xFF) / 255.0f;
        BufferBuilder bufferbuilder = Tessellator.getInstance().getBuffer();
        bufferbuilder.pos(left, bottom, 0.0).color(f, f1, f2, f3).endVertex();
        bufferbuilder.pos(right, bottom, 0.0).color(f, f1, f2, f3).endVertex();
        bufferbuilder.pos(right, top, 0.0).color(f, f1, f2, f3).endVertex();
        bufferbuilder.pos(left, top, 0.0).color(f, f1, f2, f3).endVertex();
    }

    public static void drawBoxLineHor(double x, double y, double width, double height, int start, int end) {
        float f = (float)(start >> 24 & 0xFF) / 255.0f;
        float f1 = (float)(start >> 16 & 0xFF) / 255.0f;
        float f2 = (float)(start >> 8 & 0xFF) / 255.0f;
        float f3 = (float)(start & 0xFF) / 255.0f;
        float f4 = (float)(end >> 24 & 0xFF) / 255.0f;
        float f5 = (float)(end >> 16 & 0xFF) / 255.0f;
        float f6 = (float)(end >> 8 & 0xFF) / 255.0f;
        float f7 = (float)(end & 0xFF) / 255.0f;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.pos(x, height, 0.0).color(f1, f2, f3, f).endVertex();
        bufferbuilder.pos(width, height, 0.0).color(f5, f6, f7, f4).endVertex();
        bufferbuilder.pos(width, y, 0.0).color(f5, f6, f7, f4).endVertex();
        bufferbuilder.pos(x, y, 0.0).color(f1, f2, f3, f).endVertex();
    }

    public static void drawColoredBox(double x, double y, double width, double height, double size, Vector4i colors) {
        VisualHelpers.drawBoxLineHor(x + size, y, width - size, y + size, colors.x, colors.z);
        VisualHelpers.drawBoxLineVer(x, y, x + size, height, colors.z, colors.x);
        VisualHelpers.drawBoxLineVer(width - size, y, width, height, colors.x, colors.z);
        VisualHelpers.drawBoxLineHor(x + size, height - size, width - size, height, colors.z, colors.x);
    }

    public static void drawNonColoredBox(double x, double y, double width, double height, double size, int color) {
        VisualHelpers.drawRectVerHor(x + size, y, width - size, y + size, color);
        VisualHelpers.drawRectVerHor(x, y, x + size, height, color);
        VisualHelpers.drawRectVerHor(width - size, y, width, height, color);
        VisualHelpers.drawRectVerHor(x + size, height - size, width - size, height, color);
    }

    public static void drawImage(MatrixStack matrixStack, ResourceLocation resourceLocation, float x, float y, float width, float height) {
        VisualHelpers.drawImage(matrixStack, resourceLocation, x, y, width, height, ColorHelpers.rgbaFloat(1.0f, 1.0f, 1.0f, 1.0f));
    }

    public static void drawImage(MatrixStack matrixStack, ResourceLocation resourceLocation, float x, float y, float width, float height, int color) {
        mc.getTextureManager().bindTexture(resourceLocation);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        float[] c = ColorHelpers.getRGBAf(color);
        RenderSystem.color4f(c[0], c[1], c[2], c[3]);
        VisualHelpers.allocateTextureRect(matrixStack, x, y, width, height);
        RenderSystem.disableBlend();
    }

    public static void drawImage(MatrixStack matrixStack, ResourceLocation resourceLocation, float x, float y, float width, float height, int color1, int color2, int color3, int color4) {
        float[] c1 = ColorHelpers.getRGBAf(color2);
        float[] c2 = ColorHelpers.getRGBAf(color1);
        float[] c3 = ColorHelpers.getRGBAf(color4);
        float[] c4 = ColorHelpers.getRGBAf(color3);
        Matrix4f matrix4f = matrixStack.getLast().getMatrix();
        RenderSystem.pushMatrix();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.shadeModel(7425);
        RenderSystem.enableDepthTest();
        RenderSystem.depthFunc(515);
        mc.getTextureManager().bindTexture(resourceLocation);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        bufferbuilder.pos(matrix4f, x, y, 0.0f).tex(0.0f, 0.0f).color(c1[0], c1[1], c1[2], c1[3]).endVertex();
        bufferbuilder.pos(matrix4f, x, y + height, 0.0f).tex(0.0f, 1.0f).color(c2[0], c2[1], c2[2], c2[3]).endVertex();
        bufferbuilder.pos(matrix4f, x + width, y + height, 0.0f).tex(1.0f, 1.0f).color(c3[0], c3[1], c3[2], c3[3]).endVertex();
        bufferbuilder.pos(matrix4f, x + width, y, 0.0f).tex(1.0f, 0.0f).color(c4[0], c4[1], c4[2], c4[3]).endVertex();
        tessellator.draw();
        RenderSystem.shadeModel(7424);
        RenderSystem.colorMask(true, true, true, true);
        RenderSystem.popMatrix();
    }

    public static void drawProgressBar(MatrixStack matrixStack, float x, float y, float width, float height, float progress, int color) {
        float[] c = ColorHelpers.getRGBAf(color);
        RenderSystem.color4f(0.0f, 0.0f, 0.0f, 0.0f);
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA.param, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA.param, GlStateManager.SourceFactor.ONE.param, GlStateManager.DestFactor.ZERO.param);
        PROGRESS_BAR.useProgram();
        PROGRESS_BAR.setupUniform2f("iResolution", width, height);
        PROGRESS_BAR.setupUniform1f("iTime", progress * 10.0f);
        PROGRESS_BAR.unloadProgram();
        RenderSystem.disableBlend();
    }

    private static void allocateTextureRect(MatrixStack matrixStack, float x, float y, float width, float height) {
        matrixStack.push();
        matrixStack.translate(x, y, 0.0);
        matrixStack.scale(width, height, 1.0f);
        VisualHelpers.blit(matrixStack, 0, 0, 0.0f, 0.0f, 1, 1, 1, 1);
        matrixStack.pop();
    }

    public static void drawBlockBox(BlockPos blockPos, int color) {
        VisualHelpers.drawBox(new AxisAlignedBB(blockPos).offset(-VisualHelpers.mc.getRenderManager().info.getProjectedView().x, -VisualHelpers.mc.getRenderManager().info.getProjectedView().y, -VisualHelpers.mc.getRenderManager().info.getProjectedView().z), color);
    }

    public static void drawBox(AxisAlignedBB bb, int color) {
        GL11.glPushMatrix();
        GL11.glDisable(3553);
        GL11.glDisable(2929);
        GL11.glEnable(2848);
        GL11.glLineWidth(1.0f);
        float[] rgb = ColorHelpers.getRGBAf(color);
        GlStateManager.color4f(rgb[0], rgb[1], rgb[2], rgb[3]);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder vertexbuffer = tessellator.getBuffer();
        vertexbuffer.begin(3, DefaultVertexFormats.POSITION);
        vertexbuffer.pos(bb.minX, bb.minY, bb.minZ).color(rgb[0], rgb[1], rgb[2], rgb[3]).endVertex();
        vertexbuffer.pos(bb.maxX, bb.minY, bb.minZ).color(rgb[0], rgb[1], rgb[2], rgb[3]).endVertex();
        vertexbuffer.pos(bb.maxX, bb.minY, bb.maxZ).color(rgb[0], rgb[1], rgb[2], rgb[3]).endVertex();
        vertexbuffer.pos(bb.minX, bb.minY, bb.maxZ).color(rgb[0], rgb[1], rgb[2], rgb[3]).endVertex();
        vertexbuffer.pos(bb.minX, bb.minY, bb.minZ).color(rgb[0], rgb[1], rgb[2], rgb[3]).endVertex();
        tessellator.draw();
        vertexbuffer.begin(3, DefaultVertexFormats.POSITION);
        vertexbuffer.pos(bb.minX, bb.maxY, bb.minZ).color(rgb[0], rgb[1], rgb[2], rgb[3]).endVertex();
        vertexbuffer.pos(bb.maxX, bb.maxY, bb.minZ).color(rgb[0], rgb[1], rgb[2], rgb[3]).endVertex();
        vertexbuffer.pos(bb.maxX, bb.maxY, bb.maxZ).color(rgb[0], rgb[1], rgb[2], rgb[3]).endVertex();
        vertexbuffer.pos(bb.minX, bb.maxY, bb.maxZ).color(rgb[0], rgb[1], rgb[2], rgb[3]).endVertex();
        vertexbuffer.pos(bb.minX, bb.maxY, bb.minZ).color(rgb[0], rgb[1], rgb[2], rgb[3]).endVertex();
        tessellator.draw();
        vertexbuffer.begin(1, DefaultVertexFormats.POSITION);
        vertexbuffer.pos(bb.minX, bb.minY, bb.minZ).color(rgb[0], rgb[1], rgb[2], rgb[3]).endVertex();
        vertexbuffer.pos(bb.minX, bb.maxY, bb.minZ).color(rgb[0], rgb[1], rgb[2], rgb[3]).endVertex();
        vertexbuffer.pos(bb.maxX, bb.minY, bb.minZ).color(rgb[0], rgb[1], rgb[2], rgb[3]).endVertex();
        vertexbuffer.pos(bb.maxX, bb.maxY, bb.minZ).color(rgb[0], rgb[1], rgb[2], rgb[3]).endVertex();
        vertexbuffer.pos(bb.maxX, bb.minY, bb.maxZ).color(rgb[0], rgb[1], rgb[2], rgb[3]).endVertex();
        vertexbuffer.pos(bb.maxX, bb.maxY, bb.maxZ).color(rgb[0], rgb[1], rgb[2], rgb[3]).endVertex();
        vertexbuffer.pos(bb.minX, bb.minY, bb.maxZ).color(rgb[0], rgb[1], rgb[2], rgb[3]).endVertex();
        vertexbuffer.pos(bb.minX, bb.maxY, bb.maxZ).color(rgb[0], rgb[1], rgb[2], rgb[3]).endVertex();
        tessellator.draw();
        GlStateManager.color4f(rgb[0], rgb[1], rgb[2], rgb[3]);
        GL11.glEnable(3553);
        GL11.glEnable(2929);
        GL11.glDisable(2848);
        GL11.glPopMatrix();
    }

    @Generated
    private VisualHelpers() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}