// Decompiled with: Procyon 0.6.0
// Class Version: 17
package fun.kubik.modules.render;

import fun.kubik.utils.math.MathUtils;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.Tessellator;
import org.lwjgl.opengl.GL11;
import net.minecraft.util.math.vector.Vector3d;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.util.ResourceLocation;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.util.math.vector.Vector3f;
import fun.kubik.helpers.animation.EasingList;
import fun.kubik.helpers.visual.VisualHelpers;
import fun.kubik.helpers.render.ColorHelpers;
import fun.kubik.events.main.render.EventRender3D;
import net.minecraft.entity.Entity;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.client.CUseEntityPacket;
import fun.kubik.events.main.packet.EventSendPacket;
import fun.kubik.events.api.EventHook;
import fun.kubik.Load;
import fun.kubik.modules.combat.Aura;
import fun.kubik.events.main.EventUpdate;
import fun.kubik.managers.module.option.main.SelectOptionValue;
import fun.kubik.managers.module.main.Category;
import net.minecraft.entity.LivingEntity;
import fun.kubik.managers.module.option.main.SelectOption;
import fun.kubik.helpers.animation.Animation;
import fun.kubik.managers.module.Module;

public class TargetESP extends Module
{
    private float x;
    private float y;
    private float z;
    private final Animation animation;
    private final Animation hurtAnimation;
    private final SelectOption mode;
    private float rotationAngle;
    private float rotationSpeed;
    private boolean isReversing;
    private long lastHitTime;
    Animation animHurtTime;
    private LivingEntity currentTarget;

    public TargetESP() {
        super("TargetESP", Category.RENDER);
        this.x = 0.0f;
        this.y = 0.0f;
        this.z = 0.0f;
        this.animation = new Animation();
        this.hurtAnimation = new Animation();
        this.mode = new SelectOption("Mode", 0, new SelectOptionValue[] { new SelectOptionValue("Circle"), new SelectOptionValue("Ghosts"), new SelectOptionValue("Cube") });
        this.rotationAngle = 0.0f;
        this.rotationSpeed = 0.0f;
        this.isReversing = false;
        this.lastHitTime = 0L;
        this.settings(this.mode);
    }

    @EventHook
    public void update(final EventUpdate eventUpdate) {
        if (Load.getInstance().getHooks().getModuleManagers().findClass(Aura.class).getTarget() != null) {
            this.currentTarget = Load.getInstance().getHooks().getModuleManagers().findClass(Aura.class).getTarget();
        }
        else if (this.animation.getAnimationValue() <= 0.1f) {
            this.currentTarget = null;
        }
        this.animation.update(Load.getInstance().getHooks().getModuleManagers().findClass(Aura.class).getTarget() != null);
        this.hurtAnimation.update(System.currentTimeMillis() - this.lastHitTime < 350L);
    }

    @EventHook
    public void packet(final EventSendPacket eventSendPacket) {
        final IPacket packet = eventSendPacket.getPacket();
        if (packet instanceof final CUseEntityPacket cUseEntityPacket) {
            if (cUseEntityPacket.getAction() == CUseEntityPacket.Action.ATTACK) {
                final Entity entityFromWorld = cUseEntityPacket.getEntityFromWorld(TargetESP.mc.world);
                if (TargetESP.mc.world != null && entityFromWorld != null) {
                    this.lastHitTime = System.currentTimeMillis();
                }
            }
        }
    }

    @EventHook
    public void render(final EventRender3D.Post post) {
        if (this.mode.getSelected("Circle")) {
            this.circle(post);
        }
        if (this.mode.getSelected("Ghosts")) {
            this.ghost(post);
        }
    }

    @EventHook
    public void eee(final EventRender3D.PreHand preHand) {
        if (this.mode.getSelected("Cube")) {
            this.renderCube(preHand);
        }
    }

    public void renderCube(final EventRender3D.PreHand preHand) {
        final MatrixStack matrixStack = preHand.getMatrixStack();
        final float n = 50.0f;
        final int themeColor = ColorHelpers.getThemeColor(2);
        final int themeColor2 = ColorHelpers.getThemeColor(1);
        if (this.currentTarget != null) {
            final Vector3d entityPosition = VisualHelpers.getEntityPosition(this.currentTarget, preHand.getPartialTicks());
            this.x = (float)entityPosition.x;
            this.y = (float)entityPosition.y + this.currentTarget.getHeight() / 2.0f;
            this.z = (float)entityPosition.z;
        }
        this.animation.animate(0.0f, 1.0f, 0.2f, EasingList.NONE, preHand.getPartialTicks());
        matrixStack.push();
        matrixStack.translate(this.x, this.y, this.z);
        matrixStack.scale(this.animation.getAnimationValue() * 0.025f);
        matrixStack.rotate(TargetESP.mc.getRenderManager().getCameraOrientation());
        matrixStack.rotate(Vector3f.ZP.rotationDegrees(this.rotationAngle));
        this.updateRotation();
        RenderSystem.disableDepthTest();
        VisualHelpers.drawTexture(matrixStack, new ResourceLocation("main/textures/images/target.png"), -n / 2.0f, -n / 2.0f, n, n, themeColor2, themeColor2, themeColor, themeColor);
        RenderSystem.enableDepthTest();
        matrixStack.pop();
    }

    public void updateRotation() {
        if (!this.isReversing) {
            this.rotationSpeed += 0.01f;
            if (this.rotationSpeed > 2.6500000953674316) {
                this.rotationSpeed = 2.65f;
                this.isReversing = true;
            }
        }
        else {
            this.rotationSpeed -= 0.01f;
            if (this.rotationSpeed < -2.6500000953674316) {
                this.rotationSpeed = -2.65f;
                this.isReversing = false;
            }
        }
        this.rotationAngle += this.rotationSpeed;
        this.rotationAngle = (this.rotationAngle + 360.0f) % 360.0f;
    }

    private void circle(final EventRender3D.Post post) {
        if (Load.getInstance().getHooks().getModuleManagers().findClass(Aura.class).getTarget() == null) {
            return;
        }
        final EntityRendererManager renderManager = TargetESP.mc.getRenderManager();
        final double n = this.currentTarget.lastTickPosX + (this.currentTarget.getPosX() - this.currentTarget.lastTickPosX) * post.getPartialTicks() - renderManager.info.getProjectedView().getX();
        final double n2 = this.currentTarget.lastTickPosY + (this.currentTarget.getPosY() - this.currentTarget.lastTickPosY) * post.getPartialTicks() - renderManager.info.getProjectedView().getY();
        final double n3 = this.currentTarget.lastTickPosZ + (this.currentTarget.getPosZ() - this.currentTarget.lastTickPosZ) * post.getPartialTicks() - renderManager.info.getProjectedView().getZ();
        final float height = this.currentTarget.getHeight();
        final double n4 = 2000.0;
        final double n5 = System.currentTimeMillis() % n4;
        final boolean b = n5 > n4 / 2.0;
        final double n6 = n5 / (n4 / 2.0);
        double n7;
        if (b) {
            n7 = n6 - 1.0;
        }
        else {
            n7 = 1.0 - n6;
        }
        final double n8 = (n7 < 0.5) ? (2.0 * n7 * n7) : (1.0 - Math.pow(-2.0 * n7 + 2.0, 2.0) / 2.0);
        final double n9 = height / 2.0f * ((n8 > 0.5) ? (1.0 - n8) : n8) * (b ? -1 : 1);
        RenderSystem.pushMatrix();
        GL11.glDepthMask(false);
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.disableAlphaTest();
        RenderSystem.shadeModel(7425);
        RenderSystem.disableCull();
        RenderSystem.lineWidth(2.0f);
        RenderSystem.color4f(-1.0f, -1.0f, -1.0f, -1.0f);
        Tessellator.getInstance().getBuffer().begin(8, DefaultVertexFormats.POSITION_COLOR);
        float[] rgbAf = null;
        for (int i = 0; i <= 360; ++i) {
            rgbAf = ColorHelpers.getRGBAf(ColorHelpers.getTheme(i));
            Tessellator.getInstance().getBuffer().pos(n + Math.cos(Math.toRadians(i)) * this.currentTarget.getWidth() * 0.800000011920929, n2 + height * n8, n3 + Math.sin(Math.toRadians(i)) * this.currentTarget.getWidth() * 0.800000011920929).color(rgbAf[0], rgbAf[1], rgbAf[2], 1.0f).endVertex();
            Tessellator.getInstance().getBuffer().pos(n + Math.cos(Math.toRadians(i)) * this.currentTarget.getWidth() * 0.800000011920929, n2 + height * n8 + n9, n3 + Math.sin(Math.toRadians(i)) * this.currentTarget.getWidth() * 0.800000011920929).color(rgbAf[0], rgbAf[1], rgbAf[2], 0.0f).endVertex();
        }
        Tessellator.getInstance().getBuffer().finishDrawing();
        WorldVertexBufferUploader.draw(Tessellator.getInstance().getBuffer());
        RenderSystem.color4f(-1.0f, -1.0f, -1.0f, -1.0f);
        Tessellator.getInstance().getBuffer().begin(2, DefaultVertexFormats.POSITION_COLOR);
        for (int j = 0; j <= 360; ++j) {
            Tessellator.getInstance().getBuffer().pos(n + Math.cos(Math.toRadians(j)) * this.currentTarget.getWidth() * 0.800000011920929, n2 + height * n8, n3 + Math.sin(Math.toRadians(j)) * this.currentTarget.getWidth() * 0.800000011920929).color(rgbAf[0], rgbAf[1], rgbAf[2], 1.0f).endVertex();
        }
        Tessellator.getInstance().getBuffer().finishDrawing();
        WorldVertexBufferUploader.draw(Tessellator.getInstance().getBuffer());
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
        RenderSystem.enableTexture();
        RenderSystem.enableAlphaTest();
        GL11.glDepthMask(true);
        GL11.glDisable(2848);
        GL11.glHint(3154, 4354);
        RenderSystem.shadeModel(7424);
        RenderSystem.popMatrix();
    }

    private void ghost(final EventRender3D.Post post) {
        final MatrixStack matrixStack = new MatrixStack();
        if (this.currentTarget != null) {
            this.x = (float)MathUtils.normalize(TargetESP.mc.getRenderPartialTicks(), this.currentTarget.lastTickPosX, this.currentTarget.getPosX());
            this.y = (float)(MathUtils.normalize(TargetESP.mc.getRenderPartialTicks(), this.currentTarget.lastTickPosY, this.currentTarget.getPosY()) + this.currentTarget.getHeight() / 1.6f);
            this.z = (float)MathUtils.normalize(TargetESP.mc.getRenderPartialTicks(), this.currentTarget.lastTickPosZ, this.currentTarget.getPosZ());
        }
        this.animation.animate(0.0f, 1.0f, 0.1f, EasingList.NONE, post.getPartialTicks());
        this.hurtAnimation.animate(0.0f, 1.0f, 0.2f, EasingList.NONE, post.getPartialTicks());
        if (this.animation.getAnimationValue() > 0.01) {
            this.renderGhost(matrixStack);
        }
    }

    private void renderGhost(final MatrixStack matrixStack) {
        float n = (float)((float)(System.currentTimeMillis() - Load.getStartTime()) / 1500.0f + Math.sin((float)(System.currentTimeMillis() - Load.getStartTime()) / 1500.0f) / 10.0);
        final double n2 = -TargetESP.mc.getRenderManager().info.getProjectedView().getX();
        final double n3 = -TargetESP.mc.getRenderManager().info.getProjectedView().getY();
        final double n4 = -TargetESP.mc.getRenderManager().info.getProjectedView().getZ();
        final float n5 = 0.5f * this.animation.getAnimationValue();
        float n6 = 0.0f;
        boolean b = true;
        for (int i = 0; i < 4; ++i) {
            for (float n7 = n * 360.0f; n7 < n * 360.0f + 90.0f; n7 += 2.0f) {
                final float normalize = MathUtils.normalize(n7, n * 360.0f - 45.0f, n * 360.0f + 90.0f);
                final int interpolateColorsBackAndForth = ColorHelpers.interpolateColorsBackAndForth(15, (int)(n7 * 2.0f + i * 32) + 1, ColorHelpers.getThemeColor(1), ColorHelpers.getThemeColor(1));
                final int interpolateColorsBackAndForth2 = ColorHelpers.interpolateColorsBackAndForth(15, (int)(n7 * 2.0f + i * 32 + 4.0f) + 1, ColorHelpers.getThemeColor(1), ColorHelpers.getThemeColor(2));
                System.currentTimeMillis();
                final int interpolateColor = ColorHelpers.interpolateColor(interpolateColorsBackAndForth, ColorHelpers.rgba(211, 16, 16, 220), this.hurtAnimation.getAnimationValue());
                final int interpolateColor2 = ColorHelpers.interpolateColor(interpolateColorsBackAndForth2, ColorHelpers.rgba(211, 16, 16, 220), this.hurtAnimation.getAnimationValue());
                final float n8 = 0.6f;
                final double radians = Math.toRadians(n7);
                final double n9 = n6 + Math.sin(radians * 1.15) * 0.4;
                matrixStack.push();
                matrixStack.translate(n2, n3, n4);
                matrixStack.translate(this.x, this.y, this.z);
                matrixStack.rotate(TargetESP.mc.getRenderManager().info.getRotation());
                RenderSystem.depthMask(false);
                final float n10 = ((!b) ? 0.25f : 0.15f) * (Math.max(b ? 0.25f : 0.15f, b ? normalize : ((1.0f + (0.4f - normalize)) / 2.0f)) + 0.45f) * 1.5f;
                VisualHelpers.drawGlowImage(matrixStack, new ResourceLocation("main/textures/images/glow.png"), Math.cos(radians) * n8 - n10 / 2.0f, n9 - 0.7, Math.sin(radians) * n8 - n10 / 2.0f, n10, n10, ColorHelpers.getColorWithAlpha(interpolateColor, n5 * 255.0f), ColorHelpers.getColorWithAlpha(interpolateColor2, n5 * 255.0f));
                GL11.glEnable(2929);
                RenderSystem.depthMask(true);
                matrixStack.pop();
            }
            n *= -1.2f;
            b = !b;
            n6 += 0.3f;
        }
    }
}
