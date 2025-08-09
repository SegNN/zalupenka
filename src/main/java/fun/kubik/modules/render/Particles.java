/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.modules.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import fun.kubik.Load;
import fun.kubik.events.api.EventHook;
import fun.kubik.events.main.EventUpdate;
import fun.kubik.events.main.misc.AttackEvent;
import fun.kubik.events.main.render.EventRender3D;
import fun.kubik.helpers.animation.Animation;
import fun.kubik.helpers.animation.EasingList;
import fun.kubik.helpers.interfaces.IFastAccess;
import fun.kubik.helpers.render.ColorHelpers;
import fun.kubik.managers.module.Module;
import fun.kubik.managers.module.main.Category;
import fun.kubik.managers.module.option.main.CheckboxOption;
import fun.kubik.managers.module.option.main.MultiOption;
import fun.kubik.managers.module.option.main.MultiOptionValue;
import fun.kubik.managers.module.option.main.SelectOption;
import fun.kubik.managers.module.option.main.SelectOptionValue;
import fun.kubik.managers.module.option.main.SliderOption;
import fun.kubik.utils.math.MathUtils;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.EnderPearlEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import org.lwjgl.opengl.GL11;

public class Particles
extends Module {
    private static final SelectOption mode = new SelectOption("Mode", 0, new SelectOptionValue("SnowFlake"), new SelectOptionValue("Hearts"), new SelectOptionValue("Balls"), new SelectOptionValue("Stars"), new SelectOptionValue("Dollars"));
    public final MultiOption triggers = new MultiOption("Triggers", new MultiOptionValue("Hit", true), new MultiOptionValue("Totem Pop", true), new MultiOptionValue("World", false));
    private static final SliderOption size = new SliderOption("Count", 25.0f, 1.0f, 100.0f).increment(5.0f);
    private static final CheckboxOption rainbow = new CheckboxOption("Rainbow", true);
    MatrixStack matrixStack = new MatrixStack();
    public static final CopyOnWriteArrayList<ParticleTile> particles = new CopyOnWriteArrayList();
    private static final CopyOnWriteArrayList<WorldParticleTile> worldParticles = new CopyOnWriteArrayList();

    public Particles() {
        super("Particles", Category.RENDER);
        this.settings(mode, this.triggers, size, rainbow);
    }

    @EventHook
    public void update(EventUpdate e) {
        particles.removeIf(ParticleTile::tick);
        particles.removeIf(particleBase -> System.currentTimeMillis() - particleBase.time > 12000L);
        if (this.triggers.getSelected("World")) {
            int n = worldParticles.size();
            while ((float)n < 250.0f) {
                worldParticles.add(new WorldParticleTile((float)(Particles.mc.player.getPosX() + (double)MathUtils.randomInRange(-12, 12)), (float)(Particles.mc.player.getPosY() + (double)MathUtils.randomInRange(-20.0f, 12.0f)), (float)(Particles.mc.player.getPosZ() + (double)MathUtils.randomInRange(-12, 12)), MathUtils.randomInRange(-0.4f, 0.4f), MathUtils.randomInRange(-0.1f, 0.1f), MathUtils.randomInRange(-0.4f, 0.4f)));
                ++n;
            }
        }
        worldParticles.removeIf(WorldParticleTile::tick);
        worldParticles.removeIf(particleBase -> System.currentTimeMillis() - particleBase.time > 5000L);
    }

    @EventHook
    public void attack(AttackEvent e) {
        System.out.println(e.entity);
        Entity eEntity = e.entity;
        if (eEntity instanceof LivingEntity) {
            LivingEntity entity = (LivingEntity)eEntity;
            if ((double)entity.getHealth() <= 0.0) {
                return;
            }
            if (entity.hurtTime > 1) {
                return;
            }
        }
        if (this.triggers.getSelected("Hit")) {
            int i = 0;
            while (true) {
                if (!((float)i < ((Float)size.getValue()).floatValue())) break;
                particles.add(new ParticleTile((float)eEntity.getPosX() - eEntity.getWidth() / 4.0f, MathUtils.randomInRange((float)(eEntity.getPosY() + (double)eEntity.getHeight()), (float)(eEntity.getPosY() + (double)(eEntity.getHeight() / 2.0f))), (float)eEntity.getPosZ(), MathUtils.randomInRange(-0.2f, 0.2f), MathUtils.randomInRange(-1.0E-4f, 1.0E-4f), MathUtils.randomInRange(-0.2f, 0.2f)));
                ++i;
            }
        }
    }

    @EventHook
    public void world(EventRender3D.Post e) {
        Particles.onPreRender3D(this.matrixStack);
    }

    private static boolean isInView(Vector3d pos) {
        WorldRenderer.frustum.setCameraPosition(Particles.mc.getRenderManager().info.getProjectedView().x, Particles.mc.getRenderManager().info.getProjectedView().y, Particles.mc.getRenderManager().info.getProjectedView().z);
        return WorldRenderer.frustum.isBoundingBoxInFrustum(new AxisAlignedBB(pos.add(-0.2, -0.2, -0.2), pos.add(0.2, 0.2, 0.2)));
    }

    public static void onPreRender3D(MatrixStack matrixStack) {
        boolean light = GL11.glIsEnabled(2896);
        RenderSystem.pushMatrix();
        matrixStack.push();
        RenderSystem.enableBlend();
        RenderSystem.disableAlphaTest();
        RenderSystem.depthMask(false);
        RenderSystem.disableCull();
        if (light) {
            RenderSystem.disableLighting();
        }
        GL11.glShadeModel(7425);
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        particles.forEach(particleTile -> {
            BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
            bufferBuilder.begin(7, DefaultVertexFormats.POSITION_COLOR_TEX_LIGHTMAP);
            if (Particles.isInView(new Vector3d(particleTile.posX, particleTile.posY, particleTile.posZ))) {
                particleTile.render(bufferBuilder);
            }
            bufferBuilder.finishDrawing();
            WorldVertexBufferUploader.draw(bufferBuilder);
        });
        worldParticles.forEach(worldParticleTile -> {
            BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
            bufferBuilder.begin(7, DefaultVertexFormats.POSITION_COLOR_TEX_LIGHTMAP);
            if (Particles.isInView(new Vector3d(worldParticleTile.posX, worldParticleTile.posY, worldParticleTile.posZ))) {
                worldParticleTile.render(bufferBuilder);
            }
            bufferBuilder.finishDrawing();
            WorldVertexBufferUploader.draw(bufferBuilder);
        });
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.clearCurrentColor();
        GL11.glShadeModel(7424);
        if (light) {
            RenderSystem.enableLighting();
        }
        RenderSystem.enableCull();
        RenderSystem.depthMask(true);
        RenderSystem.enableAlphaTest();
        matrixStack.pop();
        RenderSystem.popMatrix();
    }

    public static Vector3d interpolatePos(float f, float f2, float f3, float f4, float f5, float f6) {
        double d = (double)(f + (f4 - f) * mc.getRenderPartialTicks()) - Particles.mc.getRenderManager().info.getProjectedView().getX();
        double d2 = (double)(f2 + (f5 - f2) * mc.getRenderPartialTicks()) - Particles.mc.getRenderManager().info.getProjectedView().getY();
        double d3 = (double)(f3 + (f6 - f3) * mc.getRenderPartialTicks()) - Particles.mc.getRenderManager().info.getProjectedView().getZ();
        return new Vector3d(d, d2, d3);
    }

    public class WorldParticleTile {
        public long time;
        protected float prevposX;
        protected float prevposY;
        protected float prevposZ;
        protected float posX;
        protected float posY;
        protected float posZ;
        protected float motionX;
        protected float motionY;
        protected float motionZ;
        protected int age;
        protected int maxAge;
        private final Animation animation;
        private float color;

        public WorldParticleTile(float f, float f2, float f3, float f4, float f5, float f6) {
            this.posX = f;
            this.posY = f2;
            this.posZ = f3;
            this.prevposX = f;
            this.prevposY = f2;
            this.prevposZ = f3;
            this.motionX = f4;
            this.motionY = f5;
            this.motionZ = f6;
            this.time = System.currentTimeMillis() + (long)MathUtils.randomInRange(100, 3500);
            this.maxAge = this.age = MathUtils.randomInRange(120, 200);
            this.animation = new Animation();
            this.color = (Boolean)rainbow.getValue() != false ? (float)ColorHelpers.random().getRGB() : (float)ColorHelpers.getTheme(90);
        }

        public void update() {
            this.animation.animate(0.0f, 1.0f, 0.2f, EasingList.NONE, IFastAccess.mc.getTimer().renderPartialTicks);
        }

        public boolean tick() {
            this.prevposX = this.posX;
            this.prevposY = this.posY;
            this.prevposZ = this.posZ;
            this.posX += this.motionX;
            this.posY += this.motionY;
            this.posZ += this.motionZ;
            this.motionX *= 0.9f;
            this.motionY *= 0.9f;
            this.motionZ *= 0.9f;
            this.motionY -= 0.001f;
            return false;
        }

        public void render(BufferBuilder bufferBuilder) {
            if (mode.getSelected("SnowFlake")) {
                ResourceLocation texture = new ResourceLocation("main/textures/images/snowflake.png");
                System.out.println("Binding texture: " + texture);
                IFastAccess.mc.getTextureManager().bindTexture(texture);
            }
            if (mode.getSelected("Hearts")) {
                IFastAccess.mc.getTextureManager().bindTexture(new ResourceLocation("main/textures/images/heart.png"));
            }
            if (mode.getSelected("Balls")) {
                IFastAccess.mc.getTextureManager().bindTexture(new ResourceLocation("main/textures/images/glow.png"));
            }
            if (mode.getSelected("Stars")) {
                IFastAccess.mc.getTextureManager().bindTexture(new ResourceLocation("main/textures/images/star.png"));
            }
            if (mode.getSelected("Dollars")) {
                IFastAccess.mc.getTextureManager().bindTexture(new ResourceLocation("main/textures/images/dollar.png"));
            }
            float size = 1.0f - (float)(System.currentTimeMillis() - this.time) / 5000.0f;
            this.update();
            ActiveRenderInfo camera = IFastAccess.mc.gameRenderer.getActiveRenderInfo();
            int color = ColorHelpers.setAlpha((int)this.color, (int)(size * 255.0f));
            Vector3d pos = Particles.interpolatePos(this.prevposX, this.prevposY, this.prevposZ, this.posX, this.posY, this.posZ);
            MatrixStack matrices = new MatrixStack();
            matrices.translate(pos.x, pos.y, pos.z);
            matrices.rotate(Vector3f.YP.rotationDegrees(-camera.getYaw()));
            matrices.rotate(Vector3f.XP.rotationDegrees(camera.getPitch()));
            Matrix4f matrix1 = matrices.getLast().getMatrix();
            bufferBuilder.pos(matrix1, 0.0f, -0.3f, 0.0f).color(color >> 16 & 0xFF, color >> 8 & 0xFF, color & 0xFF, color >>> 24).tex(0.0f, 1.0f).lightmap(0, 240).endVertex();
            bufferBuilder.pos(matrix1, -0.3f, -0.3f, 0.0f).color(color >> 16 & 0xFF, color >> 8 & 0xFF, color & 0xFF, color >>> 24).tex(1.0f, 1.0f).lightmap(0, 240).endVertex();
            bufferBuilder.pos(matrix1, -0.3f, 0.0f, 0.0f).color(color >> 16 & 0xFF, color >> 8 & 0xFF, color & 0xFF, color >>> 24).tex(1.0f, 0.0f).lightmap(0, 240).endVertex();
            bufferBuilder.pos(matrix1, 0.0f, 0.0f, 0.0f).color(color >> 16 & 0xFF, color >> 8 & 0xFF, color & 0xFF, color >>> 24).tex(0.0f, 0.0f).lightmap(0, 240).endVertex();
        }
    }

    public static class ParticleTile
    implements IFastAccess {
        public long time;
        protected float prevposX;
        protected float prevposY;
        protected float prevposZ;
        protected float posX;
        protected float posY;
        protected float posZ;
        protected float motionX;
        protected float motionY;
        protected float motionZ;
        protected int age;
        protected int maxAge;
        private final Animation animation;
        public int color;

        public ParticleTile(float f, float f2, float f3, float f4, float f5, float f6) {
            this.posX = f;
            this.posY = f2;
            this.posZ = f3;
            this.prevposX = f;
            this.prevposY = f2;
            this.prevposZ = f3;
            this.motionX = f4;
            this.motionY = f5;
            this.motionZ = f6;
            this.time = System.currentTimeMillis() + (long)MathUtils.randomInRange(100, 10000);
            this.maxAge = this.age = ThreadLocalRandom.current().nextInt(120, 200);
            this.animation = new Animation();
            Particles cfr_ignored_0 = (Particles)Load.getInstance().getHooks().getModuleManagers().findClass(Particles.class);
            this.color = (Boolean)rainbow.getValue() != false ? ColorHelpers.random().getRGB() : ColorHelpers.getTheme(90);
        }

        public void update() {
            this.animation.animate(0.0f, 1.0f, 0.2f, EasingList.NONE, ParticleTile.mc.getTimer().renderPartialTicks);
        }

        public boolean tick() {
            this.prevposX = this.posX;
            this.prevposY = this.posY;
            this.prevposZ = this.posZ;
            this.animation.update(true);
            this.posX += this.motionX;
            this.posY += this.motionY;
            this.posZ += this.motionZ;
            this.motionX *= 1.0f;
            this.motionY *= 1.0f;
            this.motionZ *= 1.0f;
            this.motionY -= 0.002f;
            RayTraceContext traceContextXZ = new RayTraceContext(new Vector3d(this.posX, this.posY, this.posZ), new Vector3d(this.posX + this.motionX, this.posY, this.posZ + this.motionY), RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, new EnderPearlEntity(ParticleTile.mc.world, 0.0, 0.0, 0.0));
            RayTraceContext traceContextY = new RayTraceContext(new Vector3d(this.posX, this.posY, this.posZ), new Vector3d(this.posX, this.posY + this.motionY, this.posZ), RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, new EnderPearlEntity(ParticleTile.mc.world, 0.0, 0.0, 0.0));
            BlockRayTraceResult traceResultXZ = ParticleTile.mc.world.rayTraceBlocks(traceContextXZ);
            BlockRayTraceResult traceResultY = ParticleTile.mc.world.rayTraceBlocks(traceContextY);
            if (traceResultXZ.getType() == RayTraceResult.Type.BLOCK && !traceResultXZ.isInside()) {
                this.motionX = -this.motionX;
                this.motionZ = -this.motionZ;
            }
            if (traceResultY.getType() == RayTraceResult.Type.BLOCK) {
                this.motionY = -this.motionY * new Random().nextFloat(0.91f, 0.96f);
            }
            return false;
        }

        public void render(BufferBuilder bufferBuilder) {
            if (mode.getSelected("SnowFlake")) {
                mc.getTextureManager().bindTexture(new ResourceLocation("main/textures/images/snowflake.png"));
            }
            if (mode.getSelected("Hearts")) {
                mc.getTextureManager().bindTexture(new ResourceLocation("main/textures/images/heart.png"));
            }
            if (mode.getSelected("Balls")) {
                mc.getTextureManager().bindTexture(new ResourceLocation("main/textures/images/glow.png"));
            }
            if (mode.getSelected("Stars")) {
                mc.getTextureManager().bindTexture(new ResourceLocation("main/textures/images/star.png"));
            }
            if (mode.getSelected("Dollars")) {
                mc.getTextureManager().bindTexture(new ResourceLocation("main/textures/images/dollar.png"));
            }
            float size = (float)((double)(1.0f - (float)(System.currentTimeMillis() - this.time) / 12000.0f) * (double)this.animation.getValue());
            float rotation = 1.0f - (float)(System.currentTimeMillis() - this.time) / 1000.0f;
            this.update();
            ActiveRenderInfo camera = ParticleTile.mc.gameRenderer.getActiveRenderInfo();
            int color = ColorHelpers.setAlpha(this.color, (int)(size * 255.0f));
            Vector3d pos = Particles.interpolatePos(this.prevposX, this.prevposY, this.prevposZ, this.posX, this.posY, this.posZ);
            MatrixStack matrices = new MatrixStack();
            matrices.translate(pos.x, pos.y, pos.z);
            matrices.rotate(Vector3f.YP.rotationDegrees(-camera.getYaw()));
            matrices.rotate(Vector3f.XP.rotationDegrees(camera.getPitch()));
            matrices.rotate(new Quaternion(new Vector3f(0.0f, 0.0f, 1.0f), rotation, false));
            Matrix4f matrix1 = matrices.getLast().getMatrix();
            bufferBuilder.pos(matrix1, 0.0f, -0.3f, 0.0f).color(color >> 16 & 0xFF, color >> 8 & 0xFF, color & 0xFF, color >>> 24).tex(0.0f, 1.0f).lightmap(0, 240).endVertex();
            bufferBuilder.pos(matrix1, -0.3f, -0.3f, 0.0f).color(color >> 16 & 0xFF, color >> 8 & 0xFF, color & 0xFF, color >>> 24).tex(1.0f, 1.0f).lightmap(0, 240).endVertex();
            bufferBuilder.pos(matrix1, -0.3f, 0.0f, 0.0f).color(color >> 16 & 0xFF, color >> 8 & 0xFF, color & 0xFF, color >>> 24).tex(1.0f, 0.0f).lightmap(0, 240).endVertex();
            bufferBuilder.pos(matrix1, 0.0f, 0.0f, 0.0f).color(color >> 16 & 0xFF, color >> 8 & 0xFF, color & 0xFF, color >>> 24).tex(0.0f, 0.0f).lightmap(0, 240).endVertex();
        }
    }
}

