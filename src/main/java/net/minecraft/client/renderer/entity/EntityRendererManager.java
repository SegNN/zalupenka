/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.entity;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import fun.kubik.itemics.utils.accessor.IEntityRenderManager;
import java.util.Collections;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.Atlases;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.EnderDragonPartEntity;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.optifine.Config;
import net.optifine.DynamicLights;
import net.optifine.EmissiveTextures;
import net.optifine.entity.model.CustomEntityModels;
import net.optifine.player.PlayerItemsLayer;
import net.optifine.reflect.Reflector;
import net.optifine.shaders.Shaders;

public class EntityRendererManager
        implements IEntityRenderManager {
    private static final RenderType SHADOW_RENDER_TYPE = RenderType.getEntityShadow(new ResourceLocation("textures/misc/shadow.png"));
    private final Map<EntityType, EntityRenderer> renderers = Maps.newHashMap();
    private final Map<String, PlayerRenderer> skinMap = Maps.newHashMap();
    private final PlayerRenderer playerRenderer;
    private final FontRenderer textRenderer;
    public final TextureManager textureManager;
    private World world;
    public ActiveRenderInfo info;
    private Quaternion cameraOrientation;
    public Entity pointedEntity;
    public final GameSettings options;
    private boolean renderShadow = true;
    private boolean debugBoundingBox;
    public EntityRenderer renderRender = null;

    public <E extends Entity> int getPackedLight(E entityIn, float partialTicks) {
        int i = this.getRenderer(entityIn).getPackedLight(entityIn, partialTicks);
        if (Config.isDynamicLights()) {
            i = DynamicLights.getCombinedLight(entityIn, i);
        }
        return i;
    }

    private <T extends Entity> void register(EntityType<T> entityTypeIn, EntityRenderer<? super T> entityRendererIn) {
        this.renderers.put(entityTypeIn, entityRendererIn);
    }

    private void registerRenderers(net.minecraft.client.renderer.ItemRenderer itemRendererIn, IReloadableResourceManager resourceManagerIn) {
        this.register(EntityType.AREA_EFFECT_CLOUD, new AreaEffectCloudRenderer(this));
        this.register(EntityType.ARMOR_STAND, new ArmorStandRenderer(this));
        this.register(EntityType.ARROW, new TippedArrowRenderer(this));
        this.register(EntityType.BAT, new BatRenderer(this));
        this.register(EntityType.BEE, new BeeRenderer(this));
        this.register(EntityType.BLAZE, new BlazeRenderer(this));
        this.register(EntityType.BOAT, new BoatRenderer(this));
        this.register(EntityType.CAT, new CatRenderer(this));
        this.register(EntityType.CAVE_SPIDER, new CaveSpiderRenderer(this));
        this.register(EntityType.CHEST_MINECART, new MinecartRenderer(this));
        this.register(EntityType.CHICKEN, new ChickenRenderer(this));
        this.register(EntityType.COD, new CodRenderer(this));
        this.register(EntityType.COMMAND_BLOCK_MINECART, new MinecartRenderer(this));
        this.register(EntityType.COW, new CowRenderer(this));
        this.register(EntityType.CREEPER, new CreeperRenderer(this));
        this.register(EntityType.DOLPHIN, new DolphinRenderer(this));
        this.register(EntityType.DONKEY, new ChestedHorseRenderer(this, 0.87f));
        this.register(EntityType.DRAGON_FIREBALL, new DragonFireballRenderer(this));
        this.register(EntityType.DROWNED, new DrownedRenderer(this));
        this.register(EntityType.EGG, new SpriteRenderer(this, itemRendererIn));
        this.register(EntityType.ELDER_GUARDIAN, new ElderGuardianRenderer(this));
        this.register(EntityType.END_CRYSTAL, new EnderCrystalRenderer(this));
        this.register(EntityType.ENDER_DRAGON, new EnderDragonRenderer(this));
        this.register(EntityType.ENDERMAN, new EndermanRenderer(this));
        this.register(EntityType.ENDERMITE, new EndermiteRenderer(this));
        this.register(EntityType.ENDER_PEARL, new SpriteRenderer(this, itemRendererIn));
        this.register(EntityType.EVOKER_FANGS, new EvokerFangsRenderer(this));
        this.register(EntityType.EVOKER, new EvokerRenderer(this));
        this.register(EntityType.EXPERIENCE_BOTTLE, new SpriteRenderer(this, itemRendererIn));
        this.register(EntityType.EXPERIENCE_ORB, new ExperienceOrbRenderer(this));
        this.register(EntityType.EYE_OF_ENDER, new SpriteRenderer(this, itemRendererIn, 1.0f, true));
        this.register(EntityType.FALLING_BLOCK, new FallingBlockRenderer(this));
        this.register(EntityType.FIREBALL, new SpriteRenderer(this, itemRendererIn, 3.0f, true));
        this.register(EntityType.FIREWORK_ROCKET, new FireworkRocketRenderer(this, itemRendererIn));
        this.register(EntityType.FISHING_BOBBER, new FishRenderer(this));
        this.register(EntityType.FOX, new FoxRenderer(this));
        this.register(EntityType.FURNACE_MINECART, new MinecartRenderer(this));
        this.register(EntityType.GHAST, new GhastRenderer(this));
        this.register(EntityType.GIANT, new GiantZombieRenderer(this, 6.0f));
        this.register(EntityType.GUARDIAN, new GuardianRenderer(this));
        this.register(EntityType.HOGLIN, new HoglinRenderer(this));
        this.register(EntityType.HOPPER_MINECART, new MinecartRenderer(this));
        this.register(EntityType.HORSE, new HorseRenderer(this));
        this.register(EntityType.HUSK, new HuskRenderer(this));
        this.register(EntityType.ILLUSIONER, new IllusionerRenderer(this));
        this.register(EntityType.IRON_GOLEM, new IronGolemRenderer(this));
        this.register(EntityType.ITEM, new ItemRenderer(this, itemRendererIn));
        this.register(EntityType.ITEM_FRAME, new ItemFrameRenderer(this, itemRendererIn));
        this.register(EntityType.LEASH_KNOT, new LeashKnotRenderer(this));
        this.register(EntityType.LIGHTNING_BOLT, new LightningBoltRenderer(this));
        this.register(EntityType.LLAMA, new LlamaRenderer(this));
        this.register(EntityType.LLAMA_SPIT, new LlamaSpitRenderer(this));
        this.register(EntityType.MAGMA_CUBE, new MagmaCubeRenderer(this));
        this.register(EntityType.MINECART, new MinecartRenderer(this));
        this.register(EntityType.MOOSHROOM, new MooshroomRenderer(this));
        this.register(EntityType.MULE, new ChestedHorseRenderer(this, 0.92f));
        this.register(EntityType.OCELOT, new OcelotRenderer(this));
        this.register(EntityType.PAINTING, new PaintingRenderer(this));
        this.register(EntityType.PANDA, new PandaRenderer(this));
        this.register(EntityType.PARROT, new ParrotRenderer(this));
        this.register(EntityType.PHANTOM, new PhantomRenderer(this));
        this.register(EntityType.PIG, new PigRenderer(this));
        this.register(EntityType.PIGLIN, new PiglinRenderer(this, false));
        this.register(EntityType.field_242287_aj, new PiglinRenderer(this, false));
        this.register(EntityType.PILLAGER, new PillagerRenderer(this));
        this.register(EntityType.POLAR_BEAR, new PolarBearRenderer(this));
        this.register(EntityType.POTION, new SpriteRenderer(this, itemRendererIn));
        this.register(EntityType.PUFFERFISH, new PufferfishRenderer(this));
        this.register(EntityType.RABBIT, new RabbitRenderer(this));
        this.register(EntityType.RAVAGER, new RavagerRenderer(this));
        this.register(EntityType.SALMON, new SalmonRenderer(this));
        this.register(EntityType.SHEEP, new SheepRenderer(this));
        this.register(EntityType.SHULKER_BULLET, new ShulkerBulletRenderer(this));
        this.register(EntityType.SHULKER, new ShulkerRenderer(this));
        this.register(EntityType.SILVERFISH, new SilverfishRenderer(this));
        this.register(EntityType.SKELETON_HORSE, new UndeadHorseRenderer(this));
        this.register(EntityType.SKELETON, new SkeletonRenderer(this));
        this.register(EntityType.SLIME, new SlimeRenderer(this));
        this.register(EntityType.SMALL_FIREBALL, new SpriteRenderer(this, itemRendererIn, 0.75f, true));
        this.register(EntityType.SNOWBALL, new SpriteRenderer(this, itemRendererIn));
        this.register(EntityType.SNOW_GOLEM, new SnowManRenderer(this));
        this.register(EntityType.SPAWNER_MINECART, new MinecartRenderer(this));
        this.register(EntityType.SPECTRAL_ARROW, new SpectralArrowRenderer(this));
        this.register(EntityType.SPIDER, new SpiderRenderer(this));
        this.register(EntityType.SQUID, new SquidRenderer(this));
        this.register(EntityType.STRAY, new StrayRenderer(this));
        this.register(EntityType.TNT_MINECART, new TNTMinecartRenderer(this));
        this.register(EntityType.TNT, new TNTRenderer(this));
        this.register(EntityType.TRADER_LLAMA, new LlamaRenderer(this));
        this.register(EntityType.TRIDENT, new TridentRenderer(this));
        this.register(EntityType.TROPICAL_FISH, new TropicalFishRenderer(this));
        this.register(EntityType.TURTLE, new TurtleRenderer(this));
        this.register(EntityType.VEX, new VexRenderer(this));
        this.register(EntityType.VILLAGER, new VillagerRenderer(this, resourceManagerIn));
        this.register(EntityType.VINDICATOR, new VindicatorRenderer(this));
        this.register(EntityType.WANDERING_TRADER, new WanderingTraderRenderer(this));
        this.register(EntityType.WITCH, new WitchRenderer(this));
        this.register(EntityType.WITHER, new WitherRenderer(this));
        this.register(EntityType.WITHER_SKELETON, new WitherSkeletonRenderer(this));
        this.register(EntityType.WITHER_SKULL, new WitherSkullRenderer(this));
        this.register(EntityType.WOLF, new WolfRenderer(this));
        this.register(EntityType.ZOGLIN, new ZoglinRenderer(this));
        this.register(EntityType.ZOMBIE_HORSE, new UndeadHorseRenderer(this));
        this.register(EntityType.ZOMBIE, new ZombieRenderer(this));
        this.register(EntityType.ZOMBIFIED_PIGLIN, new PiglinRenderer(this, true));
        this.register(EntityType.ZOMBIE_VILLAGER, new ZombieVillagerRenderer(this, resourceManagerIn));
        this.register(EntityType.STRIDER, new StriderRenderer(this));
    }

    public EntityRendererManager(TextureManager textureManagerIn, net.minecraft.client.renderer.ItemRenderer itemRendererIn, IReloadableResourceManager resourceManagerIn, FontRenderer fontRendererIn, GameSettings gameSettingsIn) {
        this.textureManager = textureManagerIn;
        this.textRenderer = fontRendererIn;
        this.options = gameSettingsIn;
        this.registerRenderers(itemRendererIn, resourceManagerIn);
        this.playerRenderer = new PlayerRenderer(this);
        this.skinMap.put("default", this.playerRenderer);
        this.skinMap.put("slim", new PlayerRenderer(this, true));
        PlayerItemsLayer.register(this.skinMap);
    }

    public void validateRendererExistence() {
        for (EntityType entityType : Registry.ENTITY_TYPE) {
            if (entityType == EntityType.PLAYER || this.renderers.containsKey(entityType)) continue;
            throw new IllegalStateException("No renderer registered for " + String.valueOf(Registry.ENTITY_TYPE.getKey(entityType)));
        }
    }

    public <T extends Entity> EntityRenderer<? super T> getRenderer(T entityIn) {
        if (entityIn instanceof AbstractClientPlayerEntity) {
            String s = ((AbstractClientPlayerEntity)entityIn).getSkinType();
            PlayerRenderer playerrenderer = this.skinMap.get(s);
            return (EntityRenderer<? super T>) (playerrenderer != null ? playerrenderer : this.playerRenderer);
        }
        return this.renderers.get(entityIn.getType());
    }

    public void cacheActiveRenderInfo(World worldIn, ActiveRenderInfo activeRenderInfoIn, Entity entityIn) {
        this.world = worldIn;
        this.info = activeRenderInfoIn;
        this.cameraOrientation = activeRenderInfoIn.getRotation();
        this.pointedEntity = entityIn;
    }

    public void setCameraOrientation(Quaternion quaternionIn) {
        this.cameraOrientation = quaternionIn;
    }

    public void setRenderShadow(boolean renderShadowIn) {
        this.renderShadow = renderShadowIn;
    }

    public void setDebugBoundingBox(boolean debugBoundingBoxIn) {
        this.debugBoundingBox = debugBoundingBoxIn;
    }

    public boolean isDebugBoundingBox() {
        return this.debugBoundingBox;
    }

    public <E extends Entity> boolean shouldRender(E entityIn, ClippingHelper frustumIn, double camX, double camY, double camZ) {
        EntityRenderer<E> entityrenderer = (EntityRenderer<E>) this.getRenderer(entityIn);
        return entityrenderer.shouldRender(entityIn, frustumIn, camX, camY, camZ);
    }

    public <E extends Entity> void renderEntityStatic(E entityIn, double xIn, double yIn, double zIn, float rotationYawIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        if (this.info != null) {
            EntityRenderer<E> entityrenderer = (EntityRenderer<E>) this.getRenderer(entityIn);
            try {
                double d1;
                float f;
                Vector3d vector3d = entityrenderer.getRenderOffset(entityIn, partialTicks);
                double d2 = xIn + vector3d.getX();
                double d3 = yIn + vector3d.getY();
                double d0 = zIn + vector3d.getZ();
                matrixStackIn.push();
                matrixStackIn.translate(d2, d3, d0);
                if (CustomEntityModels.isActive()) {
                    this.renderRender = entityrenderer;
                }
                if (EmissiveTextures.isActive()) {
                    EmissiveTextures.beginRender();
                }
                entityrenderer.render(entityIn, rotationYawIn, partialTicks, matrixStackIn, bufferIn, packedLightIn);
                if (EmissiveTextures.isActive()) {
                    if (EmissiveTextures.hasEmissive()) {
                        EmissiveTextures.beginRenderEmissive();
                        entityrenderer.render(entityIn, rotationYawIn, partialTicks, matrixStackIn, bufferIn, LightTexture.MAX_BRIGHTNESS);
                        EmissiveTextures.endRenderEmissive();
                    }
                    EmissiveTextures.endRender();
                }
                if (entityIn.canRenderOnFire()) {
                    this.renderFire(matrixStackIn, bufferIn, entityIn);
                }
                matrixStackIn.translate(-vector3d.getX(), -vector3d.getY(), -vector3d.getZ());
                if (this.options.entityShadows && this.renderShadow && entityrenderer.shadowSize > 0.0f && !entityIn.isInvisible() && (f = (float)((1.0 - (d1 = this.getDistanceToCamera(entityIn.getPosX(), entityIn.getPosY(), entityIn.getPosZ())) / 256.0) * (double)entityrenderer.shadowOpaque)) > 0.0f) {
                    EntityRendererManager.renderShadow(matrixStackIn, bufferIn, entityIn, f, partialTicks, this.world, entityrenderer.shadowSize);
                }
                if (this.debugBoundingBox && !entityIn.isInvisible() && !Minecraft.getInstance().isReducedDebug()) {
                    this.renderDebugBoundingBox(matrixStackIn, bufferIn.getBuffer(RenderType.getLines()), entityIn, partialTicks);
                }
                matrixStackIn.pop();
            } catch (Throwable throwable1) {
                CrashReport crashreport = CrashReport.makeCrashReport(throwable1, "Rendering entity in world");
                CrashReportCategory crashreportcategory = crashreport.makeCategory("Entity being rendered");
                entityIn.fillCrashReport(crashreportcategory);
                CrashReportCategory crashreportcategory1 = crashreport.makeCategory("Renderer details");
                crashreportcategory1.addDetail("Assigned renderer", entityrenderer);
                crashreportcategory1.addDetail("Location", CrashReportCategory.getCoordinateInfo(xIn, yIn, zIn));
                crashreportcategory1.addDetail("Rotation", Float.valueOf(rotationYawIn));
                crashreportcategory1.addDetail("Delta", Float.valueOf(partialTicks));
                throw new ReportedException(crashreport);
            }
        }
    }

    private void renderDebugBoundingBox(MatrixStack matrixStackIn, IVertexBuilder bufferIn, Entity entityIn, float partialTicks) {
        if (!Shaders.isShadowPass) {
            float f = entityIn.getWidth() / 2.0f;
            this.renderBoundingBox(matrixStackIn, bufferIn, entityIn, 1.0f, 1.0f, 1.0f);
            boolean flag = entityIn instanceof EnderDragonEntity;
            if (Reflector.IForgeEntity_isMultipartEntity.exists() && Reflector.IForgeEntity_getParts.exists()) {
                flag = Reflector.callBoolean(entityIn, Reflector.IForgeEntity_isMultipartEntity, new Object[0]);
            }
            if (flag) {
                EnderDragonPartEntity[] aentity;
                double d0 = -MathHelper.lerp((double)partialTicks, entityIn.lastTickPosX, entityIn.getPosX());
                double d1 = -MathHelper.lerp((double)partialTicks, entityIn.lastTickPosY, entityIn.getPosY());
                double d2 = -MathHelper.lerp((double)partialTicks, entityIn.lastTickPosZ, entityIn.getPosZ());
                for (EnderDragonPartEntity entity : aentity = Reflector.IForgeEntity_getParts.exists() ? (EnderDragonPartEntity[]) Reflector.call(entityIn, Reflector.IForgeEntity_getParts, new Object[0]) : ((EnderDragonEntity)entityIn).getDragonParts()) {
                    matrixStackIn.push();
                    double d3 = d0 + MathHelper.lerp((double)partialTicks, entity.lastTickPosX, entity.getPosX());
                    double d4 = d1 + MathHelper.lerp((double)partialTicks, entity.lastTickPosY, entity.getPosY());
                    double d5 = d2 + MathHelper.lerp((double)partialTicks, entity.lastTickPosZ, entity.getPosZ());
                    matrixStackIn.translate(d3, d4, d5);
                    this.renderBoundingBox(matrixStackIn, bufferIn, entity, 0.25f, 1.0f, 0.0f);
                    matrixStackIn.pop();
                }
            }
            if (entityIn instanceof LivingEntity) {
                float f1 = 0.01f;
                WorldRenderer.drawBoundingBox(matrixStackIn, bufferIn, -f, entityIn.getEyeHeight() - 0.01f, -f, f, entityIn.getEyeHeight() + 0.01f, f, 1.0f, 0.0f, 0.0f, 1.0f);
            }
            Vector3d vector3d = entityIn.getLook(partialTicks);
            Matrix4f matrix4f = matrixStackIn.getLast().getMatrix();
            bufferIn.pos(matrix4f, 0.0f, entityIn.getEyeHeight(), 0.0f).color(0, 0, 255, 255).endVertex();
            bufferIn.pos(matrix4f, (float)(vector3d.x * 2.0), (float)((double)entityIn.getEyeHeight() + vector3d.y * 2.0), (float)(vector3d.z * 2.0)).color(0, 0, 255, 255).endVertex();
        }
    }

    private void renderBoundingBox(MatrixStack matrixStackIn, IVertexBuilder bufferIn, Entity entityIn, float red, float green, float blue) {
        AxisAlignedBB axisalignedbb = entityIn.getBoundingBox().offset(-entityIn.getPosX(), -entityIn.getPosY(), -entityIn.getPosZ());
        WorldRenderer.drawBoundingBox(matrixStackIn, bufferIn, axisalignedbb, red, green, blue, 1.0f);
    }

    private void renderFire(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, Entity entityIn) {
        TextureAtlasSprite textureatlassprite = ModelBakery.LOCATION_FIRE_0.getSprite();
        TextureAtlasSprite textureatlassprite1 = ModelBakery.LOCATION_FIRE_1.getSprite();
        matrixStackIn.push();
        float f = entityIn.getWidth() * 1.4f;
        matrixStackIn.scale(f, f, f);
        float f1 = 0.5f;
        float f2 = 0.0f;
        float f3 = entityIn.getHeight() / f;
        float f4 = 0.0f;
        matrixStackIn.rotate(Vector3f.YP.rotationDegrees(-this.info.getYaw()));
        matrixStackIn.translate(0.0, 0.0, -0.3f + (float)((int)f3) * 0.02f);
        float f5 = 0.0f;
        int i = 0;
        IVertexBuilder ivertexbuilder = bufferIn.getBuffer(Atlases.getCutoutBlockType());
        if (Config.isMultiTexture()) {
            ivertexbuilder.setRenderBlocks(true);
        }
        MatrixStack.Entry matrixstack$entry = matrixStackIn.getLast();
        while (f3 > 0.0f) {
            TextureAtlasSprite textureatlassprite2 = i % 2 == 0 ? textureatlassprite : textureatlassprite1;
            ivertexbuilder.setSprite(textureatlassprite2);
            float f6 = textureatlassprite2.getMinU();
            float f7 = textureatlassprite2.getMinV();
            float f8 = textureatlassprite2.getMaxU();
            float f9 = textureatlassprite2.getMaxV();
            if (i / 2 % 2 == 0) {
                float f10 = f8;
                f8 = f6;
                f6 = f10;
            }
            EntityRendererManager.fireVertex(matrixstack$entry, ivertexbuilder, f1 - 0.0f, 0.0f - f4, f5, f8, f9);
            EntityRendererManager.fireVertex(matrixstack$entry, ivertexbuilder, -f1 - 0.0f, 0.0f - f4, f5, f6, f9);
            EntityRendererManager.fireVertex(matrixstack$entry, ivertexbuilder, -f1 - 0.0f, 1.4f - f4, f5, f6, f7);
            EntityRendererManager.fireVertex(matrixstack$entry, ivertexbuilder, f1 - 0.0f, 1.4f - f4, f5, f8, f7);
            f3 -= 0.45f;
            f4 -= 0.45f;
            f1 *= 0.9f;
            f5 += 0.03f;
            ++i;
        }
        matrixStackIn.pop();
    }

    private static void fireVertex(MatrixStack.Entry matrixEntryIn, IVertexBuilder bufferIn, float x, float y, float z, float texU, float texV) {
        bufferIn.pos(matrixEntryIn.getMatrix(), x, y, z).color(255, 255, 255, 255).tex(texU, texV).overlay(0, 10).lightmap(240).normal(matrixEntryIn.getNormal(), 0.0f, 1.0f, 0.0f).endVertex();
    }

    private static void renderShadow(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, Entity entityIn, float weightIn, float partialTicks, IWorldReader worldIn, float sizeIn) {
        if (!Config.isShaders() || !Shaders.shouldSkipDefaultShadow) {
            MobEntity mobentity;
            float f = sizeIn;
            if (entityIn instanceof MobEntity && (mobentity = (MobEntity)entityIn).isChild()) {
                f = sizeIn * 0.5f;
            }
            double d2 = MathHelper.lerp((double)partialTicks, entityIn.lastTickPosX, entityIn.getPosX());
            double d0 = MathHelper.lerp((double)partialTicks, entityIn.lastTickPosY, entityIn.getPosY());
            double d1 = MathHelper.lerp((double)partialTicks, entityIn.lastTickPosZ, entityIn.getPosZ());
            int i = MathHelper.floor(d2 - (double)f);
            int j = MathHelper.floor(d2 + (double)f);
            int k = MathHelper.floor(d0 - (double)f);
            int l = MathHelper.floor(d0);
            int i1 = MathHelper.floor(d1 - (double)f);
            int j1 = MathHelper.floor(d1 + (double)f);
            MatrixStack.Entry matrixstack$entry = matrixStackIn.getLast();
            IVertexBuilder ivertexbuilder = bufferIn.getBuffer(SHADOW_RENDER_TYPE);
            for (BlockPos blockpos : BlockPos.getAllInBoxMutable(new BlockPos(i, k, i1), new BlockPos(j, l, j1))) {
                EntityRendererManager.renderBlockShadow(matrixstack$entry, ivertexbuilder, worldIn, blockpos, d2, d0, d1, f, weightIn);
            }
        }
    }

    private static void renderBlockShadow(MatrixStack.Entry matrixEntryIn, IVertexBuilder bufferIn, IWorldReader worldIn, BlockPos blockPosIn, double xIn, double yIn, double zIn, float sizeIn, float weightIn) {
        float f;
        VoxelShape voxelshape;
        BlockPos blockpos = blockPosIn.down();
        BlockState blockstate = worldIn.getBlockState(blockpos);
        if (blockstate.getRenderType() != BlockRenderType.INVISIBLE && worldIn.getLight(blockPosIn) > 3 && blockstate.hasOpaqueCollisionShape(worldIn, blockpos) && !(voxelshape = blockstate.getShape(worldIn, blockPosIn.down())).isEmpty() && (f = (float)(((double)weightIn - (yIn - (double)blockPosIn.getY()) / 2.0) * 0.5 * (double)worldIn.getBrightness(blockPosIn))) >= 0.0f) {
            if (f > 1.0f) {
                f = 1.0f;
            }
            AxisAlignedBB axisalignedbb = voxelshape.getBoundingBox();
            double d0 = (double)blockPosIn.getX() + axisalignedbb.minX;
            double d1 = (double)blockPosIn.getX() + axisalignedbb.maxX;
            double d2 = (double)blockPosIn.getY() + axisalignedbb.minY;
            double d3 = (double)blockPosIn.getZ() + axisalignedbb.minZ;
            double d4 = (double)blockPosIn.getZ() + axisalignedbb.maxZ;
            float f1 = (float)(d0 - xIn);
            float f2 = (float)(d1 - xIn);
            float f3 = (float)(d2 - yIn);
            float f4 = (float)(d3 - zIn);
            float f5 = (float)(d4 - zIn);
            float f6 = -f1 / 2.0f / sizeIn + 0.5f;
            float f7 = -f2 / 2.0f / sizeIn + 0.5f;
            float f8 = -f4 / 2.0f / sizeIn + 0.5f;
            float f9 = -f5 / 2.0f / sizeIn + 0.5f;
            EntityRendererManager.shadowVertex(matrixEntryIn, bufferIn, f, f1, f3, f4, f6, f8);
            EntityRendererManager.shadowVertex(matrixEntryIn, bufferIn, f, f1, f3, f5, f6, f9);
            EntityRendererManager.shadowVertex(matrixEntryIn, bufferIn, f, f2, f3, f5, f7, f9);
            EntityRendererManager.shadowVertex(matrixEntryIn, bufferIn, f, f2, f3, f4, f7, f8);
        }
    }

    private static void shadowVertex(MatrixStack.Entry matrixEntryIn, IVertexBuilder bufferIn, float alphaIn, float xIn, float yIn, float zIn, float texU, float texV) {
        bufferIn.pos(matrixEntryIn.getMatrix(), xIn, yIn, zIn).color(1.0f, 1.0f, 1.0f, alphaIn).tex(texU, texV).overlay(OverlayTexture.NO_OVERLAY).lightmap(0xF000F0).normal(matrixEntryIn.getNormal(), 0.0f, 1.0f, 0.0f).endVertex();
    }

    public void setWorld(@Nullable World worldIn) {
        this.world = worldIn;
        if (worldIn == null) {
            this.info = null;
        }
    }

    public double squareDistanceTo(Entity entityIn) {
        return this.info.getProjectedView().squareDistanceTo(entityIn.getPositionVec());
    }

    public double getDistanceToCamera(double x, double y, double z) {
        return this.info.getProjectedView().squareDistanceTo(x, y, z);
    }

    public Quaternion getCameraOrientation() {
        return this.cameraOrientation;
    }

    public FontRenderer getFontRenderer() {
        return this.textRenderer;
    }

    public Map<EntityType, EntityRenderer> getEntityRenderMap() {
        return this.renderers;
    }

    public Map<String, PlayerRenderer> getSkinMap() {
        return Collections.unmodifiableMap(this.skinMap);
    }

    @Override
    public double renderPosX() {
        return this.info.getProjectedView().x;
    }

    @Override
    public double renderPosY() {
        return this.info.getProjectedView().y;
    }

    @Override
    public double renderPosZ() {
        return this.info.getProjectedView().z;
    }
}

