/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.entity.player;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Either;
import fun.kubik.Load;
import fun.kubik.events.api.EventManager;
import fun.kubik.events.main.misc.EventPush;
import fun.kubik.events.main.player.EventLivingUpdate;
import fun.kubik.events.main.player.EventSwimming;
import fun.kubik.helpers.animation.Animation;
import fun.kubik.modules.movement.AutoSprint;
import fun.kubik.modules.player.FreeCam;
import fun.kubik.modules.player.NameProtect;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import lombok.Generated;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.RespawnAnchorBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.Pose;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.boss.dragon.EnderDragonPartEntity;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.entity.item.BoatEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.passive.StriderEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.MerchantOffers;
import net.minecraft.item.ShootableItem;
import net.minecraft.item.SwordItem;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SEntityVelocityPacket;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectUtils;
import net.minecraft.potion.Effects;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.tileentity.CommandBlockLogic;
import net.minecraft.tileentity.CommandBlockTileEntity;
import net.minecraft.tileentity.JigsawTileEntity;
import net.minecraft.tileentity.SignTileEntity;
import net.minecraft.tileentity.StructureBlockTileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.CachedBlockInfo;
import net.minecraft.util.CooldownTracker;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.FoodStats;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.Unit;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public abstract class PlayerEntity
        extends LivingEntity {
    public static final EntitySize STANDING_SIZE = EntitySize.flexible(0.6f, 1.8f);
    private static final ImmutableMap<Object, Object> SIZE_BY_POSE = ImmutableMap.builder().put(Pose.STANDING, STANDING_SIZE).put(Pose.SLEEPING, SLEEPING_SIZE).put(Pose.FALL_FLYING, EntitySize.flexible(0.6f, 0.6f)).put(Pose.SWIMMING, EntitySize.flexible(0.6f, 0.6f)).put(Pose.SPIN_ATTACK, EntitySize.flexible(0.6f, 0.6f)).put(Pose.CROUCHING, EntitySize.flexible(0.6f, 1.5f)).put(Pose.DYING, EntitySize.fixed(0.2f, 0.2f)).build();
    private static final DataParameter<Float> ABSORPTION = EntityDataManager.createKey(PlayerEntity.class, DataSerializers.FLOAT);
    private static final DataParameter<Integer> PLAYER_SCORE = EntityDataManager.createKey(PlayerEntity.class, DataSerializers.VARINT);
    protected static final DataParameter<Byte> PLAYER_MODEL_FLAG = EntityDataManager.createKey(PlayerEntity.class, DataSerializers.BYTE);
    protected static final DataParameter<Byte> MAIN_HAND = EntityDataManager.createKey(PlayerEntity.class, DataSerializers.BYTE);
    protected static final DataParameter<CompoundNBT> LEFT_SHOULDER_ENTITY = EntityDataManager.createKey(PlayerEntity.class, DataSerializers.COMPOUND_NBT);
    protected static final DataParameter<CompoundNBT> RIGHT_SHOULDER_ENTITY = EntityDataManager.createKey(PlayerEntity.class, DataSerializers.COMPOUND_NBT);
    private long timeEntitySatOnShoulder;
    public final PlayerInventory inventory = new PlayerInventory(this);
    protected EnderChestInventory enterChestInventory = new EnderChestInventory();
    public final PlayerContainer container;
    public Container openContainer;
    protected FoodStats foodStats = new FoodStats();
    protected int flyToggleTimer;
    public float prevCameraYaw;
    public float cameraYaw;
    public int xpCooldown;
    public double prevChasingPosX;
    public double prevChasingPosY;
    public double prevChasingPosZ;
    public double chasingPosX;
    public double chasingPosY;
    public double chasingPosZ;
    private int sleepTimer;
    protected boolean eyesInWaterPlayer;
    public final PlayerAbilities abilities = new PlayerAbilities();
    public int experienceLevel;
    public int experienceTotal;
    public float experience;
    protected int xpSeed;
    protected final float speedInAir = 0.02f;
    private int lastXPSound;
    private final Animation friendAnimation = new Animation();
    private final GameProfile gameProfile;
    private boolean hasReducedDebug;
    private ItemStack itemStackMainHand = ItemStack.EMPTY;
    private final CooldownTracker cooldownTracker = this.createCooldownTracker();
    @Nullable
    public FishingBobberEntity fishingBobber;
    private final Animation animation = new Animation();

    public PlayerEntity(World p_i241920_1_, BlockPos p_i241920_2_, float p_i241920_3_, GameProfile p_i241920_4_) {
        super((EntityType<? extends LivingEntity>)EntityType.PLAYER, p_i241920_1_);
        this.setUniqueId(PlayerEntity.getUUID(p_i241920_4_));
        this.gameProfile = p_i241920_4_;
        this.container = new PlayerContainer(this.inventory, !p_i241920_1_.isRemote, this);
        this.openContainer = this.container;
        this.setLocationAndAngles((double)p_i241920_2_.getX() + 0.5, p_i241920_2_.getY() + 1, (double)p_i241920_2_.getZ() + 0.5, p_i241920_3_, 0.0f);
        this.unused180 = 180.0f;
    }

    public boolean blockActionRestricted(World worldIn, BlockPos pos, GameType gameMode) {
        if (!gameMode.hasLimitedInteractions()) {
            return false;
        }
        if (gameMode == GameType.SPECTATOR) {
            return true;
        }
        if (this.isAllowEdit()) {
            return false;
        }
        ItemStack itemstack = this.getHeldItemMainhand();
        return itemstack.isEmpty() || !itemstack.canDestroy(worldIn.getTags(), new CachedBlockInfo(worldIn, pos, false));
    }

    public static AttributeModifierMap.MutableAttribute func_234570_el_() {
        return LivingEntity.registerAttributes().createMutableAttribute(Attributes.ATTACK_DAMAGE, 1.0).createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.1f).createMutableAttribute(Attributes.ATTACK_SPEED).createMutableAttribute(Attributes.LUCK);
    }

    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(ABSORPTION, Float.valueOf(0.0f));
        this.dataManager.register(PLAYER_SCORE, 0);
        this.dataManager.register(PLAYER_MODEL_FLAG, (byte)0);
        this.dataManager.register(MAIN_HAND, (byte)1);
        this.dataManager.register(LEFT_SHOULDER_ENTITY, new CompoundNBT());
        this.dataManager.register(RIGHT_SHOULDER_ENTITY, new CompoundNBT());
    }

    @Override
    public void tick() {
        this.noClip = this.isSpectator() || ((FreeCam)Load.getInstance().getHooks().getModuleManagers().findClass(FreeCam.class)).isToggled();
        EventLivingUpdate eventLivingUpdate = new EventLivingUpdate();
        EventManager.call(eventLivingUpdate);
        if (this.isSpectator()) {
            this.onGround = false;
        }
        if (this.xpCooldown > 0) {
            --this.xpCooldown;
        }
        if (this.isSleeping()) {
            ++this.sleepTimer;
            if (this.sleepTimer > 100) {
                this.sleepTimer = 100;
            }
            if (!this.world.isRemote && this.world.isDaytime()) {
                this.stopSleepInBed(false, true);
            }
        } else if (this.sleepTimer > 0) {
            ++this.sleepTimer;
            if (this.sleepTimer >= 110) {
                this.sleepTimer = 0;
            }
        }
        this.updateEyesInWaterPlayer();
        super.tick();
        if (!this.world.isRemote && this.openContainer != null && !this.openContainer.canInteractWith(this)) {
            this.closeScreen();
            this.openContainer = this.container;
        }
        this.updateCape();
        if (!this.world.isRemote) {
            this.foodStats.tick(this);
            this.addStat(Stats.PLAY_ONE_MINUTE);
            if (this.isAlive()) {
                this.addStat(Stats.TIME_SINCE_DEATH);
            }
            if (this.isDiscrete()) {
                this.addStat(Stats.SNEAK_TIME);
            }
            if (!this.isSleeping()) {
                this.addStat(Stats.TIME_SINCE_REST);
            }
        }
        int i = 29999999;
        double d0 = MathHelper.clamp(this.getPosX(), -2.9999999E7, 2.9999999E7);
        double d1 = MathHelper.clamp(this.getPosZ(), -2.9999999E7, 2.9999999E7);
        if (d0 != this.getPosX() || d1 != this.getPosZ()) {
            this.setPosition(d0, this.getPosY(), d1);
        }
        ++this.ticksSinceLastSwing;
        ItemStack itemstack = this.getHeldItemMainhand();
        if (!ItemStack.areItemStacksEqual(this.itemStackMainHand, itemstack)) {
            if (!ItemStack.areItemsEqualIgnoreDurability(this.itemStackMainHand, itemstack)) {
                this.resetCooldown();
            }
            this.itemStackMainHand = itemstack.copy();
        }
        this.updateTurtleHelmet();
        this.cooldownTracker.tick();
        this.updatePose();
    }

    public boolean isSecondaryUseActive() {
        return this.isSneaking();
    }

    protected boolean wantsToStopRiding() {
        return this.isSneaking();
    }

    protected boolean isStayingOnGroundSurface() {
        return this.isSneaking();
    }

    protected boolean updateEyesInWaterPlayer() {
        this.eyesInWaterPlayer = this.areEyesInFluid(FluidTags.WATER);
        return this.eyesInWaterPlayer;
    }

    private void updateTurtleHelmet() {
        ItemStack itemstack = this.getItemStackFromSlot(EquipmentSlotType.HEAD);
        if (itemstack.getItem() == Items.TURTLE_HELMET && !this.areEyesInFluid(FluidTags.WATER)) {
            this.addPotionEffect(new EffectInstance(Effects.WATER_BREATHING, 200, 0, false, false, true));
        }
    }

    protected CooldownTracker createCooldownTracker() {
        return new CooldownTracker();
    }

    protected void updateCape() {
        this.prevChasingPosX = this.chasingPosX;
        this.prevChasingPosY = this.chasingPosY;
        this.prevChasingPosZ = this.chasingPosZ;
        double d0 = this.getPosX() - this.chasingPosX;
        double d1 = this.getPosY() - this.chasingPosY;
        double d2 = this.getPosZ() - this.chasingPosZ;
        double d3 = 10.0;
        if (d0 > 10.0) {
            this.prevChasingPosX = this.chasingPosX = this.getPosX();
        }
        if (d2 > 10.0) {
            this.prevChasingPosZ = this.chasingPosZ = this.getPosZ();
        }
        if (d1 > 10.0) {
            this.prevChasingPosY = this.chasingPosY = this.getPosY();
        }
        if (d0 < -10.0) {
            this.prevChasingPosX = this.chasingPosX = this.getPosX();
        }
        if (d2 < -10.0) {
            this.prevChasingPosZ = this.chasingPosZ = this.getPosZ();
        }
        if (d1 < -10.0) {
            this.prevChasingPosY = this.chasingPosY = this.getPosY();
        }
        this.chasingPosX += d0 * 0.25;
        this.chasingPosZ += d2 * 0.25;
        this.chasingPosY += d1 * 0.25;
    }

    protected void updatePose() {
        if (this.isPoseClear(Pose.SWIMMING)) {
            Pose pose = this.isElytraFlying() ? Pose.FALL_FLYING : (this.isSleeping() ? Pose.SLEEPING : (this.isSwimming() ? Pose.SWIMMING : (this.isSpinAttacking() ? Pose.SPIN_ATTACK : (this.isSneaking() && !this.abilities.isFlying ? Pose.CROUCHING : Pose.STANDING))));
            Pose pose1 = !(this.isSpectator() || this.isPassenger() || this.isPoseClear(pose) || ((FreeCam)Load.getInstance().getHooks().getModuleManagers().findClass(FreeCam.class)).isToggled()) ? (this.isPoseClear(Pose.CROUCHING) ? Pose.CROUCHING : Pose.SWIMMING) : pose;
            this.setPose(pose1);
        }
    }

    @Override
    public int getMaxInPortalTime() {
        return this.abilities.disableDamage ? 1 : 80;
    }

    @Override
    protected SoundEvent getSwimSound() {
        return SoundEvents.ENTITY_PLAYER_SWIM;
    }

    @Override
    protected SoundEvent getSplashSound() {
        return SoundEvents.ENTITY_PLAYER_SPLASH;
    }

    @Override
    protected SoundEvent getHighspeedSplashSound() {
        return SoundEvents.ENTITY_PLAYER_SPLASH_HIGH_SPEED;
    }

    @Override
    public int getPortalCooldown() {
        return 10;
    }

    @Override
    public void playSound(SoundEvent soundIn, float volume, float pitch) {
        this.world.playSound(this, this.getPosX(), this.getPosY(), this.getPosZ(), soundIn, this.getSoundCategory(), volume, pitch);
    }

    public void playSound(SoundEvent p_213823_1_, SoundCategory p_213823_2_, float p_213823_3_, float p_213823_4_) {
    }

    @Override
    public SoundCategory getSoundCategory() {
        return SoundCategory.PLAYERS;
    }

    @Override
    protected int getFireImmuneTicks() {
        return 20;
    }

    @Override
    public void handleStatusUpdate(byte id) {
        if (id == 9) {
            this.onItemUseFinish();
        } else if (id == 23) {
            this.hasReducedDebug = false;
        } else if (id == 22) {
            this.hasReducedDebug = true;
        } else if (id == 43) {
            this.addParticlesAroundSelf(ParticleTypes.CLOUD);
        } else {
            super.handleStatusUpdate(id);
        }
    }

    private void addParticlesAroundSelf(IParticleData p_213824_1_) {
        for (int i = 0; i < 5; ++i) {
            double d0 = this.rand.nextGaussian() * 0.02;
            double d1 = this.rand.nextGaussian() * 0.02;
            double d2 = this.rand.nextGaussian() * 0.02;
            this.world.addParticle(p_213824_1_, this.getPosXRandom(1.0), this.getPosYRandom() + 1.0, this.getPosZRandom(1.0), d0, d1, d2);
        }
    }

    protected void closeScreen() {
        this.openContainer = this.container;
    }

    @Override
    public void updateRidden() {
        if (this.wantsToStopRiding() && this.isPassenger()) {
            this.stopRiding();
            this.setSneaking(false);
        } else {
            double d0 = this.getPosX();
            double d1 = this.getPosY();
            double d2 = this.getPosZ();
            super.updateRidden();
            this.prevCameraYaw = this.cameraYaw;
            this.cameraYaw = 0.0f;
            this.addMountedMovementStat(this.getPosX() - d0, this.getPosY() - d1, this.getPosZ() - d2);
        }
    }

    @Override
    public void preparePlayerToSpawn() {
        this.setPose(Pose.STANDING);
        super.preparePlayerToSpawn();
        this.setHealth(this.getMaxHealth());
        this.deathTime = 0;
    }

    @Override
    protected void updateEntityActionState() {
        super.updateEntityActionState();
        this.updateArmSwingProgress();
        this.rotationYawHead = this.rotationYaw;
        this.rotationPitchHead = this.rotationPitch;
    }

    @Override
    public void livingTick() {
        if (this.flyToggleTimer > 0) {
            --this.flyToggleTimer;
        }
        if (this.world.getDifficulty() == Difficulty.PEACEFUL && this.world.getGameRules().getBoolean(GameRules.NATURAL_REGENERATION)) {
            if (this.getHealth() < this.getMaxHealth() && this.ticksExisted % 20 == 0) {
                this.heal(1.0f);
            }
            if (this.foodStats.needFood() && this.ticksExisted % 10 == 0) {
                this.foodStats.setFoodLevel(this.foodStats.getFoodLevel() + 1);
            }
        }
        this.inventory.tick();
        this.prevCameraYaw = this.cameraYaw;
        super.livingTick();
        this.jumpMovementFactor = 0.02f;
        if (this.isSprinting()) {
            this.jumpMovementFactor = (float)((double)this.jumpMovementFactor + 0.005999999865889549);
        }
        this.setAIMoveSpeed((float)this.getAttributeValue(Attributes.MOVEMENT_SPEED));
        float f = this.onGround && !this.getShouldBeDead() && !this.isSwimming() ? Math.min(0.1f, MathHelper.sqrt(PlayerEntity.horizontalMag(this.getMotion()))) : 0.0f;
        this.cameraYaw += (f - this.cameraYaw) * 0.4f;
        if (this.getHealth() > 0.0f && !this.isSpectator()) {
            AxisAlignedBB axisalignedbb = this.isPassenger() && !this.getRidingEntity().removed ? this.getBoundingBox().union(this.getRidingEntity().getBoundingBox()).grow(1.0, 0.0, 1.0) : this.getBoundingBox().grow(1.0, 0.5, 1.0);
            List<Entity> list = this.world.getEntitiesWithinAABBExcludingEntity(this, axisalignedbb);
            for (int i = 0; i < list.size(); ++i) {
                Entity entity = list.get(i);
                if (entity.removed) continue;
                this.collideWithPlayer(entity);
            }
        }
        this.playShoulderEntityAmbientSound(this.getLeftShoulderEntity());
        this.playShoulderEntityAmbientSound(this.getRightShoulderEntity());
        if (!this.world.isRemote && (this.fallDistance > 0.5f || this.isInWater()) || this.abilities.isFlying || this.isSleeping()) {
            this.spawnShoulderEntities();
        }
    }

    private void playShoulderEntityAmbientSound(@Nullable CompoundNBT p_192028_1_) {
        if (!(p_192028_1_ == null || p_192028_1_.contains("Silent") && p_192028_1_.getBoolean("Silent") || this.world.rand.nextInt(200) != 0)) {
            String s = p_192028_1_.getString("id");
            EntityType.byKey(s).filter(p_213830_0_ -> p_213830_0_ == EntityType.PARROT).ifPresent(p_213834_1_ -> {
                if (!ParrotEntity.playMimicSound(this.world, this)) {
                    this.world.playSound(null, this.getPosX(), this.getPosY(), this.getPosZ(), ParrotEntity.func_234212_a_(this.world, this.world.rand), this.getSoundCategory(), 1.0f, ParrotEntity.getPitch(this.world.rand));
                }
            });
        }
    }

    private void collideWithPlayer(Entity entityIn) {
        entityIn.onCollideWithPlayer(this);
    }

    public int getScore() {
        return this.dataManager.get(PLAYER_SCORE);
    }

    public void setScore(int scoreIn) {
        this.dataManager.set(PLAYER_SCORE, scoreIn);
    }

    public void addScore(int scoreIn) {
        int i = this.getScore();
        this.dataManager.set(PLAYER_SCORE, i + scoreIn);
    }

    @Override
    public void onDeath(DamageSource cause) {
        super.onDeath(cause);
        this.recenterBoundingBox();
        if (!this.isSpectator()) {
            this.spawnDrops(cause);
        }
        if (cause != null) {
            this.setMotion(-MathHelper.cos((this.attackedAtYaw + this.rotationYaw) * ((float)Math.PI / 180)) * 0.1f, 0.1f, -MathHelper.sin((this.attackedAtYaw + this.rotationYaw) * ((float)Math.PI / 180)) * 0.1f);
        } else {
            this.setMotion(0.0, 0.1, 0.0);
        }
        this.addStat(Stats.DEATHS);
        this.takeStat(Stats.CUSTOM.get(Stats.TIME_SINCE_DEATH));
        this.takeStat(Stats.CUSTOM.get(Stats.TIME_SINCE_REST));
        this.extinguish();
        this.setFlag(0, false);
    }

    @Override
    protected void dropInventory() {
        super.dropInventory();
        if (!this.world.getGameRules().getBoolean(GameRules.KEEP_INVENTORY)) {
            this.destroyVanishingCursedItems();
            this.inventory.dropAllItems();
        }
    }

    protected void destroyVanishingCursedItems() {
        for (int i = 0; i < this.inventory.getSizeInventory(); ++i) {
            ItemStack itemstack = this.inventory.getStackInSlot(i);
            if (itemstack.isEmpty() || !EnchantmentHelper.hasVanishingCurse(itemstack)) continue;
            this.inventory.removeStackFromSlot(i);
        }
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        if (damageSourceIn == DamageSource.ON_FIRE) {
            return SoundEvents.ENTITY_PLAYER_HURT_ON_FIRE;
        }
        if (damageSourceIn == DamageSource.DROWN) {
            return SoundEvents.ENTITY_PLAYER_HURT_DROWN;
        }
        return damageSourceIn == DamageSource.SWEET_BERRY_BUSH ? SoundEvents.ENTITY_PLAYER_HURT_SWEET_BERRY_BUSH : SoundEvents.ENTITY_PLAYER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_PLAYER_DEATH;
    }

    public boolean drop(boolean p_225609_1_) {
        return this.dropItem(this.inventory.decrStackSize(this.inventory.currentItem, p_225609_1_ && !this.inventory.getCurrentItem().isEmpty() ? this.inventory.getCurrentItem().getCount() : 1), false, true) != null;
    }

    @Nullable
    public ItemEntity dropItem(ItemStack itemStackIn, boolean unused) {
        return this.dropItem(itemStackIn, false, unused);
    }

    @Nullable
    public ItemEntity dropItem(ItemStack droppedItem, boolean dropAround, boolean traceItem) {
        if (droppedItem.isEmpty()) {
            return null;
        }
        if (this.world.isRemote) {
            this.swingArm(Hand.MAIN_HAND);
        }
        double d0 = this.getPosYEye() - (double)0.3f;
        ItemEntity itementity = new ItemEntity(this.world, this.getPosX(), d0, this.getPosZ(), droppedItem);
        itementity.setPickupDelay(40);
        if (traceItem) {
            itementity.setThrowerId(this.getUniqueID());
        }
        if (dropAround) {
            float f = this.rand.nextFloat() * 0.5f;
            float f1 = this.rand.nextFloat() * ((float)Math.PI * 2);
            itementity.setMotion(-MathHelper.sin(f1) * f, 0.2f, MathHelper.cos(f1) * f);
        } else {
            float f7 = 0.3f;
            float f8 = MathHelper.sin(this.rotationPitch * ((float)Math.PI / 180));
            float f2 = MathHelper.cos(this.rotationPitch * ((float)Math.PI / 180));
            float f3 = MathHelper.sin(this.rotationYaw * ((float)Math.PI / 180));
            float f4 = MathHelper.cos(this.rotationYaw * ((float)Math.PI / 180));
            float f5 = this.rand.nextFloat() * ((float)Math.PI * 2);
            float f6 = 0.02f * this.rand.nextFloat();
            itementity.setMotion((double)(-f3 * f2 * 0.3f) + Math.cos(f5) * (double)f6, -f8 * 0.3f + 0.1f + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.1f, (double)(f4 * f2 * 0.3f) + Math.sin(f5) * (double)f6);
        }
        return itementity;
    }

    public float getDigSpeed(BlockState state) {
        float f = this.inventory.getDestroySpeed(state);
        if (f > 1.0f) {
            int i = EnchantmentHelper.getEfficiencyModifier(this);
            ItemStack itemstack = this.getHeldItemMainhand();
            if (i > 0 && !itemstack.isEmpty()) {
                f += (float)(i * i + 1);
            }
        }
        if (EffectUtils.hasMiningSpeedup(this)) {
            f *= 1.0f + (float)(EffectUtils.getMiningSpeedup(this) + 1) * 0.2f;
        }
        if (this.isPotionActive(Effects.MINING_FATIGUE)) {
            f *= (switch (this.getActivePotionEffect(Effects.MINING_FATIGUE).getAmplifier()) {
                case 0 -> 0.3f;
                case 1 -> 0.09f;
                case 2 -> 0.0027f;
                default -> 8.1E-4f;
            });
        }
        if (this.areEyesInFluid(FluidTags.WATER) && !EnchantmentHelper.hasAquaAffinity(this)) {
            f /= 5.0f;
        }
        if (!this.onGround) {
            f /= 5.0f;
        }
        return f;
    }

    public boolean func_234569_d_(BlockState p_234569_1_) {
        return !p_234569_1_.getRequiresTool() || this.inventory.getCurrentItem().canHarvestBlock(p_234569_1_);
    }

    @Override
    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        this.setUniqueId(PlayerEntity.getUUID(this.gameProfile));
        ListNBT listnbt = compound.getList("Inventory", 10);
        this.inventory.read(listnbt);
        this.inventory.currentItem = compound.getInt("SelectedItemSlot");
        this.sleepTimer = compound.getShort("SleepTimer");
        this.experience = compound.getFloat("XpP");
        this.experienceLevel = compound.getInt("XpLevel");
        this.experienceTotal = compound.getInt("XpTotal");
        this.xpSeed = compound.getInt("XpSeed");
        if (this.xpSeed == 0) {
            this.xpSeed = this.rand.nextInt();
        }
        this.setScore(compound.getInt("Score"));
        this.foodStats.read(compound);
        this.abilities.read(compound);
        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(this.abilities.getWalkSpeed());
        if (compound.contains("EnderItems", 9)) {
            this.enterChestInventory.read(compound.getList("EnderItems", 10));
        }
        if (compound.contains("ShoulderEntityLeft", 10)) {
            this.setLeftShoulderEntity(compound.getCompound("ShoulderEntityLeft"));
        }
        if (compound.contains("ShoulderEntityRight", 10)) {
            this.setRightShoulderEntity(compound.getCompound("ShoulderEntityRight"));
        }
    }

    @Override
    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putInt("DataVersion", SharedConstants.getVersion().getWorldVersion());
        compound.put("Inventory", this.inventory.write(new ListNBT()));
        compound.putInt("SelectedItemSlot", this.inventory.currentItem);
        compound.putShort("SleepTimer", (short)this.sleepTimer);
        compound.putFloat("XpP", this.experience);
        compound.putInt("XpLevel", this.experienceLevel);
        compound.putInt("XpTotal", this.experienceTotal);
        compound.putInt("XpSeed", this.xpSeed);
        compound.putInt("Score", this.getScore());
        this.foodStats.write(compound);
        this.abilities.write(compound);
        compound.put("EnderItems", this.enterChestInventory.write());
        if (!this.getLeftShoulderEntity().isEmpty()) {
            compound.put("ShoulderEntityLeft", this.getLeftShoulderEntity());
        }
        if (!this.getRightShoulderEntity().isEmpty()) {
            compound.put("ShoulderEntityRight", this.getRightShoulderEntity());
        }
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        if (super.isInvulnerableTo(source)) {
            return true;
        }
        if (source == DamageSource.DROWN) {
            return !this.world.getGameRules().getBoolean(GameRules.DROWNING_DAMAGE);
        }
        if (source == DamageSource.FALL) {
            return !this.world.getGameRules().getBoolean(GameRules.FALL_DAMAGE);
        }
        if (source.isFireDamage()) {
            return !this.world.getGameRules().getBoolean(GameRules.FIRE_DAMAGE);
        }
        return false;
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        }
        if (this.abilities.disableDamage && !source.canHarmInCreative()) {
            return false;
        }
        this.idleTime = 0;
        if (this.getShouldBeDead()) {
            return false;
        }
        this.spawnShoulderEntities();
        if (source.isDifficultyScaled()) {
            if (this.world.getDifficulty() == Difficulty.PEACEFUL) {
                amount = 0.0f;
            }
            if (this.world.getDifficulty() == Difficulty.EASY) {
                amount = Math.min(amount / 2.0f + 1.0f, amount);
            }
            if (this.world.getDifficulty() == Difficulty.HARD) {
                amount = amount * 3.0f / 2.0f;
            }
        }
        return amount == 0.0f ? false : super.attackEntityFrom(source, amount);
    }

    @Override
    protected void blockUsingShield(LivingEntity entityIn) {
        super.blockUsingShield(entityIn);
        if (entityIn.getHeldItemMainhand().getItem() instanceof AxeItem) {
            this.disableShield(true);
        }
    }

    public boolean canAttackPlayer(PlayerEntity other) {
        Team team = this.getTeam();
        Team team1 = other.getTeam();
        if (team == null) {
            return true;
        }
        return !team.isSameTeam(team1) ? true : team.getAllowFriendlyFire();
    }

    @Override
    protected void damageArmor(DamageSource damageSource, float damage) {
        this.inventory.func_234563_a_(damageSource, damage);
    }

    @Override
    protected void damageShield(float damage) {
        if (this.activeItemStack.getItem() == Items.SHIELD) {
            if (!this.world.isRemote) {
                this.addStat(Stats.ITEM_USED.get(this.activeItemStack.getItem()));
            }
            if (damage >= 3.0f) {
                int i = 1 + MathHelper.floor(damage);
                Hand hand = this.getActiveHand();
                this.activeItemStack.damageItem(i, this, p_213833_1_ -> p_213833_1_.sendBreakAnimation(hand));
                if (this.activeItemStack.isEmpty()) {
                    if (hand == Hand.MAIN_HAND) {
                        this.setItemStackToSlot(EquipmentSlotType.MAINHAND, ItemStack.EMPTY);
                    } else {
                        this.setItemStackToSlot(EquipmentSlotType.OFFHAND, ItemStack.EMPTY);
                    }
                    this.activeItemStack = ItemStack.EMPTY;
                    this.playSound(SoundEvents.ITEM_SHIELD_BREAK, 0.8f, 0.8f + this.world.rand.nextFloat() * 0.4f);
                }
            }
        }
    }

    @Override
    protected void damageEntity(DamageSource damageSrc, float damageAmount) {
        if (!this.isInvulnerableTo(damageSrc)) {
            damageAmount = this.applyArmorCalculations(damageSrc, damageAmount);
            damageAmount = this.applyPotionDamageCalculations(damageSrc, damageAmount);
            float f2 = Math.max(damageAmount - this.getAbsorptionAmount(), 0.0f);
            this.setAbsorptionAmount(this.getAbsorptionAmount() - (damageAmount - f2));
            float f = damageAmount - f2;
            if (f > 0.0f && f < 3.4028235E37f) {
                this.addStat(Stats.DAMAGE_ABSORBED, Math.round(f * 10.0f));
            }
            if (f2 != 0.0f) {
                this.addExhaustion(damageSrc.getHungerDamage());
                float f1 = this.getHealth();
                this.setHealth(this.getHealth() - f2);
                this.getCombatTracker().trackDamage(damageSrc, f1, f2);
                if (f2 < 3.4028235E37f) {
                    this.addStat(Stats.DAMAGE_TAKEN, Math.round(f2 * 10.0f));
                }
            }
        }
    }

    @Override
    protected boolean func_230296_cM_() {
        return !this.abilities.isFlying && super.func_230296_cM_();
    }

    public void openSignEditor(SignTileEntity signTile) {
    }

    public void openMinecartCommandBlock(CommandBlockLogic commandBlock) {
    }

    public void openCommandBlock(CommandBlockTileEntity commandBlock) {
    }

    public void openStructureBlock(StructureBlockTileEntity structure) {
    }

    public void openJigsaw(JigsawTileEntity p_213826_1_) {
    }

    public void openHorseInventory(AbstractHorseEntity horse, IInventory inventoryIn) {
    }

    public OptionalInt openContainer(@Nullable INamedContainerProvider p_213829_1_) {
        return OptionalInt.empty();
    }

    public void openMerchantContainer(int containerId, MerchantOffers offers, int level, int xp, boolean p_213818_5_, boolean p_213818_6_) {
    }

    public void openBook(ItemStack stack, Hand hand) {
    }

    public ActionResultType interactOn(Entity entityToInteractOn, Hand hand) {
        if (this.isSpectator()) {
            if (entityToInteractOn instanceof INamedContainerProvider) {
                this.openContainer((INamedContainerProvider)((Object)entityToInteractOn));
            }
            return ActionResultType.PASS;
        }
        ItemStack itemstack = this.getHeldItem(hand);
        ItemStack itemstack1 = itemstack.copy();
        ActionResultType actionresulttype = entityToInteractOn.processInitialInteract(this, hand);
        if (actionresulttype.isSuccessOrConsume()) {
            if (this.abilities.isCreativeMode && itemstack == this.getHeldItem(hand) && itemstack.getCount() < itemstack1.getCount()) {
                itemstack.setCount(itemstack1.getCount());
            }
            return actionresulttype;
        }
        if (!itemstack.isEmpty() && entityToInteractOn instanceof LivingEntity) {
            ActionResultType actionresulttype1;
            if (this.abilities.isCreativeMode) {
                itemstack = itemstack1;
            }
            if ((actionresulttype1 = itemstack.interactWithEntity(this, (LivingEntity)entityToInteractOn, hand)).isSuccessOrConsume()) {
                if (itemstack.isEmpty() && !this.abilities.isCreativeMode) {
                    this.setHeldItem(hand, ItemStack.EMPTY);
                }
                return actionresulttype1;
            }
        }
        return ActionResultType.PASS;
    }

    @Override
    public double getYOffset() {
        return -0.35;
    }

    @Override
    public void dismount() {
        super.dismount();
        this.rideCooldown = 0;
    }

    @Override
    protected boolean isMovementBlocked() {
        return super.isMovementBlocked() || this.isSleeping();
    }

    @Override
    public boolean func_241208_cS_() {
        return !this.abilities.isFlying;
    }

    @Override
    protected Vector3d maybeBackOffFromEdge(Vector3d vec, MoverType mover) {
        if (!this.abilities.isFlying && (mover == MoverType.SELF || mover == MoverType.PLAYER) && this.isStayingOnGroundSurface() && this.func_242375_q()) {
            double d0 = vec.x;
            double d1 = vec.z;
            double d2 = 0.05;
            while (d0 != 0.0 && this.world.hasNoCollisions(this, this.getBoundingBox().offset(d0, -this.stepHeight, 0.0))) {
                if (d0 < 0.05 && d0 >= -0.05) {
                    d0 = 0.0;
                    continue;
                }
                if (d0 > 0.0) {
                    d0 -= 0.05;
                    continue;
                }
                d0 += 0.05;
            }
            while (d1 != 0.0 && this.world.hasNoCollisions(this, this.getBoundingBox().offset(0.0, -this.stepHeight, d1))) {
                if (d1 < 0.05 && d1 >= -0.05) {
                    d1 = 0.0;
                    continue;
                }
                if (d1 > 0.0) {
                    d1 -= 0.05;
                    continue;
                }
                d1 += 0.05;
            }
            while (d0 != 0.0 && d1 != 0.0 && this.world.hasNoCollisions(this, this.getBoundingBox().offset(d0, -this.stepHeight, d1))) {
                d0 = d0 < 0.05 && d0 >= -0.05 ? 0.0 : (d0 > 0.0 ? (d0 -= 0.05) : (d0 += 0.05));
                if (d1 < 0.05 && d1 >= -0.05) {
                    d1 = 0.0;
                    continue;
                }
                if (d1 > 0.0) {
                    d1 -= 0.05;
                    continue;
                }
                d1 += 0.05;
            }
            vec = new Vector3d(d0, vec.y, d1);
        }
        return vec;
    }

    private boolean func_242375_q() {
        return this.onGround || this.fallDistance < this.stepHeight && !this.world.hasNoCollisions(this, this.getBoundingBox().offset(0.0, this.fallDistance - this.stepHeight, 0.0));
    }

    public void attackTargetEntityWithCurrentItem(Entity targetEntity) {
        if (targetEntity.canBeAttackedWithItem() && !targetEntity.hitByEntity(this)) {
            float f = (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE);
            float f1 = targetEntity instanceof LivingEntity ? EnchantmentHelper.getModifierForCreature(this.getHeldItemMainhand(), ((LivingEntity)targetEntity).getCreatureAttribute()) : EnchantmentHelper.getModifierForCreature(this.getHeldItemMainhand(), CreatureAttribute.UNDEFINED);
            float f2 = this.getCooledAttackStrength(0.5f);
            f1 *= f2;
            this.resetCooldown();
            if ((f *= 0.2f + f2 * f2 * 0.8f) > 0.0f || f1 > 0.0f) {
                ItemStack itemstack;
                boolean flag = f2 > 0.9f;
                boolean flag1 = false;
                int i = 0;
                i += EnchantmentHelper.getKnockbackModifier(this);
                if (this.isSprinting() && flag) {
                    this.world.playSound(null, this.getPosX(), this.getPosY(), this.getPosZ(), SoundEvents.ENTITY_PLAYER_ATTACK_KNOCKBACK, this.getSoundCategory(), 1.0f, 1.0f);
                    ++i;
                    flag1 = true;
                }
                boolean flag2 = flag && this.fallDistance > 0.0f && !this.onGround && !this.isOnLadder() && !this.isInWater() && !this.isPotionActive(Effects.BLINDNESS) && !this.isPassenger() && targetEntity instanceof LivingEntity;
                boolean bl = flag2 = flag2 && !this.isSprinting();
                if (flag2) {
                    f *= 1.5f;
                }
                f += f1;
                boolean flag3 = false;
                double d0 = this.distanceWalkedModified - this.prevDistanceWalkedModified;
                if (flag && !flag2 && !flag1 && this.onGround && d0 < (double)this.getAIMoveSpeed() && (itemstack = this.getHeldItem(Hand.MAIN_HAND)).getItem() instanceof SwordItem) {
                    flag3 = true;
                }
                float f4 = 0.0f;
                boolean flag4 = false;
                int j = EnchantmentHelper.getFireAspectModifier(this);
                if (targetEntity instanceof LivingEntity) {
                    f4 = ((LivingEntity)targetEntity).getHealth();
                    if (j > 0 && !targetEntity.isBurning()) {
                        flag4 = true;
                        targetEntity.setFire(1);
                    }
                }
                Vector3d vector3d = targetEntity.getMotion();
                boolean flag5 = targetEntity.attackEntityFrom(DamageSource.causePlayerDamage(this), f);
                if (flag5) {
                    if (i > 0) {
                        if (targetEntity instanceof LivingEntity) {
                            ((LivingEntity)targetEntity).applyKnockback((float)i * 0.5f, MathHelper.sin(this.rotationYaw * ((float)Math.PI / 180)), -MathHelper.cos(this.rotationYaw * ((float)Math.PI / 180)));
                        } else {
                            targetEntity.addVelocity(-MathHelper.sin(this.rotationYaw * ((float)Math.PI / 180)) * (float)i * 0.5f, 0.1, MathHelper.cos(this.rotationYaw * ((float)Math.PI / 180)) * (float)i * 0.5f);
                        }
                        AutoSprint autoSprint = (AutoSprint)Load.getInstance().getHooks().getModuleManagers().findClass(AutoSprint.class);
                        if (!autoSprint.mode.getSelected("Rage")) {
                            this.setMotion(this.getMotion().mul(0.6, 1.0, 0.6));
                        }
                        this.setSprinting(false);
                    }
                    if (flag3) {
                        float f3 = 1.0f + EnchantmentHelper.getSweepingDamageRatio(this) * f;
                        for (LivingEntity livingentity : this.world.getEntitiesWithinAABB(LivingEntity.class, targetEntity.getBoundingBox().grow(1.0, 0.25, 1.0))) {
                            if (livingentity == this || livingentity == targetEntity || this.isOnSameTeam(livingentity) || livingentity instanceof ArmorStandEntity && ((ArmorStandEntity)livingentity).hasMarker() || !(this.getDistanceSq(livingentity) < 9.0)) continue;
                            livingentity.applyKnockback(0.4f, MathHelper.sin(this.rotationYaw * ((float)Math.PI / 180)), -MathHelper.cos(this.rotationYaw * ((float)Math.PI / 180)));
                            livingentity.attackEntityFrom(DamageSource.causePlayerDamage(this), f3);
                        }
                        this.world.playSound(null, this.getPosX(), this.getPosY(), this.getPosZ(), SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, this.getSoundCategory(), 1.0f, 1.0f);
                        this.spawnSweepParticles();
                    }
                    if (targetEntity instanceof ServerPlayerEntity && targetEntity.velocityChanged) {
                        ((ServerPlayerEntity)targetEntity).connection.sendPacket(new SEntityVelocityPacket(targetEntity));
                        targetEntity.velocityChanged = false;
                        targetEntity.setMotion(vector3d);
                    }
                    if (flag2) {
                        this.world.playSound(null, this.getPosX(), this.getPosY(), this.getPosZ(), SoundEvents.ENTITY_PLAYER_ATTACK_CRIT, this.getSoundCategory(), 1.0f, 1.0f);
                        this.onCriticalHit(targetEntity);
                    }
                    if (!flag2 && !flag3) {
                        if (flag) {
                            this.world.playSound(null, this.getPosX(), this.getPosY(), this.getPosZ(), SoundEvents.ENTITY_PLAYER_ATTACK_STRONG, this.getSoundCategory(), 1.0f, 1.0f);
                        } else {
                            this.world.playSound(null, this.getPosX(), this.getPosY(), this.getPosZ(), SoundEvents.ENTITY_PLAYER_ATTACK_WEAK, this.getSoundCategory(), 1.0f, 1.0f);
                        }
                    }
                    if (f1 > 0.0f) {
                        this.onEnchantmentCritical(targetEntity);
                    }
                    this.setLastAttackedEntity(targetEntity);
                    if (targetEntity instanceof LivingEntity) {
                        EnchantmentHelper.applyThornEnchantments((LivingEntity)targetEntity, this);
                    }
                    EnchantmentHelper.applyArthropodEnchantments(this, targetEntity);
                    ItemStack itemstack1 = this.getHeldItemMainhand();
                    Entity entity = targetEntity;
                    if (targetEntity instanceof EnderDragonPartEntity) {
                        entity = ((EnderDragonPartEntity)targetEntity).dragon;
                    }
                    if (!this.world.isRemote && !itemstack1.isEmpty() && entity instanceof LivingEntity) {
                        itemstack1.hitEntity((LivingEntity)entity, this);
                        if (itemstack1.isEmpty()) {
                            this.setHeldItem(Hand.MAIN_HAND, ItemStack.EMPTY);
                        }
                    }
                    if (targetEntity instanceof LivingEntity) {
                        float f5 = f4 - ((LivingEntity)targetEntity).getHealth();
                        this.addStat(Stats.DAMAGE_DEALT, Math.round(f5 * 10.0f));
                        if (j > 0) {
                            targetEntity.setFire(j * 4);
                        }
                        if (this.world instanceof ServerWorld && f5 > 2.0f) {
                            int k = (int)((double)f5 * 0.5);
                            ((ServerWorld)this.world).spawnParticle(ParticleTypes.DAMAGE_INDICATOR, targetEntity.getPosX(), targetEntity.getPosYHeight(0.5), targetEntity.getPosZ(), k, 0.1, 0.0, 0.1, 0.2);
                        }
                    }
                    this.addExhaustion(0.1f);
                } else {
                    this.world.playSound(null, this.getPosX(), this.getPosY(), this.getPosZ(), SoundEvents.ENTITY_PLAYER_ATTACK_NODAMAGE, this.getSoundCategory(), 1.0f, 1.0f);
                    if (flag4) {
                        targetEntity.extinguish();
                    }
                }
            }
        }
    }

    @Override
    protected void spinAttack(LivingEntity p_204804_1_) {
        this.attackTargetEntityWithCurrentItem(p_204804_1_);
    }

    public void disableShield(boolean p_190777_1_) {
        float f = 0.25f + (float)EnchantmentHelper.getEfficiencyModifier(this) * 0.05f;
        if (p_190777_1_) {
            f += 0.75f;
        }
        if (this.rand.nextFloat() < f) {
            this.getCooldownTracker().setCooldown(Items.SHIELD, 100);
            this.resetActiveHand();
            this.world.setEntityState(this, (byte)30);
        }
    }

    public void onCriticalHit(Entity entityHit) {
    }

    public void onEnchantmentCritical(Entity entityHit) {
    }

    public void spawnSweepParticles() {
        double d0 = -MathHelper.sin(this.rotationYaw * ((float)Math.PI / 180));
        double d1 = MathHelper.cos(this.rotationYaw * ((float)Math.PI / 180));
        if (this.world instanceof ServerWorld) {
            ((ServerWorld)this.world).spawnParticle(ParticleTypes.SWEEP_ATTACK, this.getPosX() + d0, this.getPosYHeight(0.5), this.getPosZ() + d1, 0, d0, 0.0, d1, 0.0);
        }
    }

    public void respawnPlayer() {
    }

    @Override
    public void remove() {
        super.remove();
        this.container.onContainerClosed(this);
        if (this.openContainer != null) {
            this.openContainer.onContainerClosed(this);
        }
    }

    public boolean isUser() {
        return false;
    }

    public GameProfile getGameProfile() {
        return this.gameProfile;
    }

    public Either<SleepResult, Unit> trySleep(BlockPos at) {
        this.startSleeping(at);
        this.sleepTimer = 0;
        return Either.right(Unit.INSTANCE);
    }

    public void stopSleepInBed(boolean p_225652_1_, boolean p_225652_2_) {
        super.wakeUp();
        if (this.world instanceof ServerWorld && p_225652_2_) {
            ((ServerWorld)this.world).updateAllPlayersSleepingFlag();
        }
        this.sleepTimer = p_225652_1_ ? 0 : 100;
    }

    @Override
    public void wakeUp() {
        this.stopSleepInBed(true, true);
    }

    public static Optional<Vector3d> func_242374_a(ServerWorld p_242374_0_, BlockPos p_242374_1_, float p_242374_2_, boolean p_242374_3_, boolean p_242374_4_) {
        BlockState blockstate = p_242374_0_.getBlockState(p_242374_1_);
        Block block = blockstate.getBlock();
        if (block instanceof RespawnAnchorBlock && blockstate.get(RespawnAnchorBlock.CHARGES) > 0 && RespawnAnchorBlock.doesRespawnAnchorWork(p_242374_0_)) {
            Optional<Vector3d> optional = RespawnAnchorBlock.findRespawnPoint(EntityType.PLAYER, p_242374_0_, p_242374_1_);
            if (!p_242374_4_ && optional.isPresent()) {
                p_242374_0_.setBlockState(p_242374_1_, (BlockState)blockstate.with(RespawnAnchorBlock.CHARGES, blockstate.get(RespawnAnchorBlock.CHARGES) - 1), 3);
            }
            return optional;
        }
        if (block instanceof BedBlock && BedBlock.doesBedWork(p_242374_0_)) {
            return BedBlock.func_242652_a(EntityType.PLAYER, p_242374_0_, p_242374_1_, p_242374_2_);
        }
        if (!p_242374_3_) {
            return Optional.empty();
        }
        boolean flag = block.canSpawnInBlock();
        boolean flag1 = p_242374_0_.getBlockState(p_242374_1_.up()).getBlock().canSpawnInBlock();
        return flag && flag1 ? Optional.of(new Vector3d((double)p_242374_1_.getX() + 0.5, (double)p_242374_1_.getY() + 0.1, (double)p_242374_1_.getZ() + 0.5)) : Optional.empty();
    }

    public boolean isPlayerFullyAsleep() {
        return this.isSleeping() && this.sleepTimer >= 100;
    }

    public int getSleepTimer() {
        return this.sleepTimer;
    }

    public void sendStatusMessage(ITextComponent chatComponent, boolean actionBar) {
    }

    public void addStat(ResourceLocation stat) {
        this.addStat(Stats.CUSTOM.get(stat));
    }

    public void addStat(ResourceLocation p_195067_1_, int p_195067_2_) {
        this.addStat(Stats.CUSTOM.get(p_195067_1_), p_195067_2_);
    }

    public void addStat(Stat<?> stat) {
        this.addStat(stat, 1);
    }

    public void addStat(Stat<?> stat, int amount) {
    }

    public void takeStat(Stat<?> stat) {
    }

    public int unlockRecipes(Collection<IRecipe<?>> p_195065_1_) {
        return 0;
    }

    public void unlockRecipes(ResourceLocation[] p_193102_1_) {
    }

    public int resetRecipes(Collection<IRecipe<?>> p_195069_1_) {
        return 0;
    }

    @Override
    public void jump() {
        super.jump();
        this.addStat(Stats.JUMP);
        if (this.isSprinting()) {
            this.addExhaustion(0.2f);
        } else {
            this.addExhaustion(0.05f);
        }
    }

    @Override
    public void travel(Vector3d travelVector) {
        double d0 = this.getPosX();
        double d1 = this.getPosY();
        double d2 = this.getPosZ();
        if (this.isSwimming() && !this.isPassenger()) {
            double d4;
            EventSwimming eventSwimming = new EventSwimming(this.rotationPitch, this.rotationYaw);
            EventManager.call(eventSwimming);
            double d3 = this.getVectorForRotation((float)eventSwimming.getPitch(), (float)eventSwimming.getYaw()).y;
            double d = d4 = d3 < -0.2 ? 0.085 : 0.06;
            if (d3 <= 0.0 || this.isJumping || !this.world.getBlockState(new BlockPos(this.getPosX(), this.getPosY() + 1.0 - 0.1, this.getPosZ())).getFluidState().isEmpty()) {
                Vector3d vector3d1 = this.getMotion();
                this.setMotion(vector3d1.add(0.0, (d3 - vector3d1.y) * d4, 0.0));
            }
        }
        if (this.abilities.isFlying && !this.isPassenger()) {
            double d5 = this.getMotion().y;
            float f = this.jumpMovementFactor;
            this.jumpMovementFactor = this.abilities.getFlySpeed() * (float)(this.isSprinting() ? 2 : 1);
            super.travel(travelVector);
            Vector3d vector3d = this.getMotion();
            this.setMotion(vector3d.x, d5 * 0.6, vector3d.z);
            this.jumpMovementFactor = f;
            this.fallDistance = 0.0f;
            this.setFlag(7, false);
        } else {
            super.travel(travelVector);
        }
        this.addMovementStat(this.getPosX() - d0, this.getPosY() - d1, this.getPosZ() - d2);
    }

    @Override
    public void updateSwimming() {
        if (this.abilities.isFlying) {
            this.setSwimming(false);
        } else {
            super.updateSwimming();
        }
    }

    protected boolean isNormalCube(BlockPos pos) {
        return !this.world.getBlockState(pos).isSuffocating(this.world, pos);
    }

    @Override
    public float getAIMoveSpeed() {
        return (float)this.getAttributeValue(Attributes.MOVEMENT_SPEED);
    }

    public void addMovementStat(double p_71000_1_, double p_71000_3_, double p_71000_5_) {
        if (!this.isPassenger()) {
            if (this.isSwimming()) {
                int i = Math.round(MathHelper.sqrt(p_71000_1_ * p_71000_1_ + p_71000_3_ * p_71000_3_ + p_71000_5_ * p_71000_5_) * 100.0f);
                if (i > 0) {
                    this.addStat(Stats.SWIM_ONE_CM, i);
                    this.addExhaustion(0.01f * (float)i * 0.01f);
                }
            } else if (this.areEyesInFluid(FluidTags.WATER)) {
                int j = Math.round(MathHelper.sqrt(p_71000_1_ * p_71000_1_ + p_71000_3_ * p_71000_3_ + p_71000_5_ * p_71000_5_) * 100.0f);
                if (j > 0) {
                    this.addStat(Stats.WALK_UNDER_WATER_ONE_CM, j);
                    this.addExhaustion(0.01f * (float)j * 0.01f);
                }
            } else if (this.isInWater()) {
                int k = Math.round(MathHelper.sqrt(p_71000_1_ * p_71000_1_ + p_71000_5_ * p_71000_5_) * 100.0f);
                if (k > 0) {
                    this.addStat(Stats.WALK_ON_WATER_ONE_CM, k);
                    this.addExhaustion(0.01f * (float)k * 0.01f);
                }
            } else if (this.isOnLadder()) {
                if (p_71000_3_ > 0.0) {
                    this.addStat(Stats.CLIMB_ONE_CM, (int)Math.round(p_71000_3_ * 100.0));
                }
            } else if (this.onGround) {
                int l = Math.round(MathHelper.sqrt(p_71000_1_ * p_71000_1_ + p_71000_5_ * p_71000_5_) * 100.0f);
                if (l > 0) {
                    if (this.isSprinting()) {
                        this.addStat(Stats.SPRINT_ONE_CM, l);
                        this.addExhaustion(0.1f * (float)l * 0.01f);
                    } else if (this.isCrouching()) {
                        this.addStat(Stats.CROUCH_ONE_CM, l);
                        this.addExhaustion(0.0f * (float)l * 0.01f);
                    } else {
                        this.addStat(Stats.WALK_ONE_CM, l);
                        this.addExhaustion(0.0f * (float)l * 0.01f);
                    }
                }
            } else if (this.isElytraFlying()) {
                int i1 = Math.round(MathHelper.sqrt(p_71000_1_ * p_71000_1_ + p_71000_3_ * p_71000_3_ + p_71000_5_ * p_71000_5_) * 100.0f);
                this.addStat(Stats.AVIATE_ONE_CM, i1);
            } else {
                int j1 = Math.round(MathHelper.sqrt(p_71000_1_ * p_71000_1_ + p_71000_5_ * p_71000_5_) * 100.0f);
                if (j1 > 25) {
                    this.addStat(Stats.FLY_ONE_CM, j1);
                }
            }
        }
    }

    private void addMountedMovementStat(double p_71015_1_, double p_71015_3_, double p_71015_5_) {
        int i;
        if (this.isPassenger() && (i = Math.round(MathHelper.sqrt(p_71015_1_ * p_71015_1_ + p_71015_3_ * p_71015_3_ + p_71015_5_ * p_71015_5_) * 100.0f)) > 0) {
            Entity entity = this.getRidingEntity();
            if (entity instanceof AbstractMinecartEntity) {
                this.addStat(Stats.MINECART_ONE_CM, i);
            } else if (entity instanceof BoatEntity) {
                this.addStat(Stats.BOAT_ONE_CM, i);
            } else if (entity instanceof PigEntity) {
                this.addStat(Stats.PIG_ONE_CM, i);
            } else if (entity instanceof AbstractHorseEntity) {
                this.addStat(Stats.HORSE_ONE_CM, i);
            } else if (entity instanceof StriderEntity) {
                this.addStat(Stats.field_232862_C_, i);
            }
        }
    }

    @Override
    public boolean onLivingFall(float distance, float damageMultiplier) {
        if (this.abilities.allowFlying) {
            return false;
        }
        if (distance >= 2.0f) {
            this.addStat(Stats.FALL_ONE_CM, (int)Math.round((double)distance * 100.0));
        }
        return super.onLivingFall(distance, damageMultiplier);
    }

    public boolean tryToStartFallFlying() {
        ItemStack itemstack;
        if (!(this.onGround || this.isElytraFlying() || this.isInWater() || this.isPotionActive(Effects.LEVITATION) || (itemstack = this.getItemStackFromSlot(EquipmentSlotType.CHEST)).getItem() != Items.ELYTRA || !ElytraItem.isUsable(itemstack))) {
            this.startFallFlying();
            return true;
        }
        return false;
    }

    public void startFallFlying() {
        this.setFlag(7, true);
    }

    public void stopFallFlying() {
        this.setFlag(7, true);
        this.setFlag(7, false);
    }

    @Override
    protected void doWaterSplashEffect() {
        if (!this.isSpectator()) {
            super.doWaterSplashEffect();
        }
    }

    @Override
    protected SoundEvent getFallSound(int heightIn) {
        return heightIn > 4 ? SoundEvents.ENTITY_PLAYER_BIG_FALL : SoundEvents.ENTITY_PLAYER_SMALL_FALL;
    }

    @Override
    public void func_241847_a(ServerWorld p_241847_1_, LivingEntity p_241847_2_) {
        this.addStat(Stats.ENTITY_KILLED.get(p_241847_2_.getType()));
    }

    @Override
    public void setMotionMultiplier(BlockState state, Vector3d motionMultiplierIn) {
        if (!this.abilities.isFlying) {
            super.setMotionMultiplier(state, motionMultiplierIn);
        }
    }

    public void giveExperiencePoints(int p_195068_1_) {
        this.addScore(p_195068_1_);
        this.experience += (float)p_195068_1_ / (float)this.xpBarCap();
        this.experienceTotal = MathHelper.clamp(this.experienceTotal + p_195068_1_, 0, Integer.MAX_VALUE);
        while (this.experience < 0.0f) {
            float f = this.experience * (float)this.xpBarCap();
            if (this.experienceLevel > 0) {
                this.addExperienceLevel(-1);
                this.experience = 1.0f + f / (float)this.xpBarCap();
                continue;
            }
            this.addExperienceLevel(-1);
            this.experience = 0.0f;
        }
        while (this.experience >= 1.0f) {
            this.experience = (this.experience - 1.0f) * (float)this.xpBarCap();
            this.addExperienceLevel(1);
            this.experience /= (float)this.xpBarCap();
        }
    }

    public int getXPSeed() {
        return this.xpSeed;
    }

    public void onEnchant(ItemStack enchantedItem, int cost) {
        this.experienceLevel -= cost;
        if (this.experienceLevel < 0) {
            this.experienceLevel = 0;
            this.experience = 0.0f;
            this.experienceTotal = 0;
        }
        this.xpSeed = this.rand.nextInt();
    }

    public void addExperienceLevel(int levels) {
        this.experienceLevel += levels;
        if (this.experienceLevel < 0) {
            this.experienceLevel = 0;
            this.experience = 0.0f;
            this.experienceTotal = 0;
        }
        if (levels > 0 && this.experienceLevel % 5 == 0 && (float)this.lastXPSound < (float)this.ticksExisted - 100.0f) {
            float f = this.experienceLevel > 30 ? 1.0f : (float)this.experienceLevel / 30.0f;
            this.world.playSound(null, this.getPosX(), this.getPosY(), this.getPosZ(), SoundEvents.ENTITY_PLAYER_LEVELUP, this.getSoundCategory(), f * 0.75f, 1.0f);
            this.lastXPSound = this.ticksExisted;
        }
    }

    public int xpBarCap() {
        if (this.experienceLevel >= 30) {
            return 112 + (this.experienceLevel - 30) * 9;
        }
        return this.experienceLevel >= 15 ? 37 + (this.experienceLevel - 15) * 5 : 7 + this.experienceLevel * 2;
    }

    public void addExhaustion(float exhaustion) {
        if (!this.abilities.disableDamage && !this.world.isRemote) {
            this.foodStats.addExhaustion(exhaustion);
        }
    }

    public FoodStats getFoodStats() {
        return this.foodStats;
    }

    public boolean canEat(boolean ignoreHunger) {
        return this.abilities.disableDamage || ignoreHunger || this.foodStats.needFood();
    }

    public boolean shouldHeal() {
        return this.getHealth() > 0.0f && this.getHealth() < this.getMaxHealth();
    }

    public boolean isAllowEdit() {
        return this.abilities.allowEdit;
    }

    public boolean canPlayerEdit(BlockPos pos, Direction facing, ItemStack stack) {
        if (this.abilities.allowEdit) {
            return true;
        }
        BlockPos blockpos = pos.offset(facing.getOpposite());
        CachedBlockInfo cachedblockinfo = new CachedBlockInfo(this.world, blockpos, false);
        return stack.canPlaceOn(this.world.getTags(), cachedblockinfo);
    }

    @Override
    protected int getExperiencePoints(PlayerEntity player) {
        if (!this.world.getGameRules().getBoolean(GameRules.KEEP_INVENTORY) && !this.isSpectator()) {
            int i = this.experienceLevel * 7;
            return i > 100 ? 100 : i;
        }
        return 0;
    }

    @Override
    protected boolean isPlayer() {
        return true;
    }

    @Override
    public boolean getAlwaysRenderNameTagForRender() {
        return true;
    }

    @Override
    protected boolean canTriggerWalking() {
        return !this.abilities.isFlying && (!this.onGround || !this.isDiscrete());
    }

    public void sendPlayerAbilities() {
    }

    public void setGameType(GameType gameType) {
    }

    @Override
    public ITextComponent getName() {
        NameProtect nameProtect = (NameProtect)Load.getInstance().getHooks().getModuleManagers().findClass(NameProtect.class);
        if (nameProtect.isToggled() && this == Minecraft.getInstance().player) {
            return new StringTextComponent((String)nameProtect.getPlayerName().getValue());
        }
        if (nameProtect.isToggled() && ((Boolean)nameProtect.getFriends().getValue()).booleanValue() && Load.getInstance().getHooks().getFriendManagers().is(this.gameProfile.getName())) {
            return new StringTextComponent((String)nameProtect.getPlayerName().getValue());
        }
        return new StringTextComponent(this.gameProfile.getName());
    }

    public EnderChestInventory getInventoryEnderChest() {
        return this.enterChestInventory;
    }

    @Override
    public ItemStack getItemStackFromSlot(EquipmentSlotType slotIn) {
        if (slotIn == EquipmentSlotType.MAINHAND) {
            return this.inventory.getCurrentItem();
        }
        if (slotIn == EquipmentSlotType.OFFHAND) {
            return this.inventory.offHandInventory.get(0);
        }
        return slotIn.getSlotType() == EquipmentSlotType.Group.ARMOR ? this.inventory.armorInventory.get(slotIn.getIndex()) : ItemStack.EMPTY;
    }

    @Override
    public void setItemStackToSlot(EquipmentSlotType slotIn, ItemStack stack) {
        if (slotIn == EquipmentSlotType.MAINHAND) {
            this.playEquipSound(stack);
            this.inventory.mainInventory.set(this.inventory.currentItem, stack);
        } else if (slotIn == EquipmentSlotType.OFFHAND) {
            this.playEquipSound(stack);
            this.inventory.offHandInventory.set(0, stack);
        } else if (slotIn.getSlotType() == EquipmentSlotType.Group.ARMOR) {
            this.playEquipSound(stack);
            this.inventory.armorInventory.set(slotIn.getIndex(), stack);
        }
    }

    public boolean addItemStackToInventory(ItemStack p_191521_1_) {
        this.playEquipSound(p_191521_1_);
        return this.inventory.addItemStackToInventory(p_191521_1_);
    }

    @Override
    public Iterable<ItemStack> getHeldEquipment() {
        return Lists.newArrayList(this.getHeldItemMainhand(), this.getHeldItemOffhand());
    }

    @Override
    public Iterable<ItemStack> getArmorInventoryList() {
        return this.inventory.armorInventory;
    }

    public boolean addShoulderEntity(CompoundNBT p_192027_1_) {
        if (!this.isPassenger() && this.onGround && !this.isInWater()) {
            if (this.getLeftShoulderEntity().isEmpty()) {
                this.setLeftShoulderEntity(p_192027_1_);
                this.timeEntitySatOnShoulder = this.world.getGameTime();
                return true;
            }
            if (this.getRightShoulderEntity().isEmpty()) {
                this.setRightShoulderEntity(p_192027_1_);
                this.timeEntitySatOnShoulder = this.world.getGameTime();
                return true;
            }
            return false;
        }
        return false;
    }

    protected void spawnShoulderEntities() {
        if (this.timeEntitySatOnShoulder + 20L < this.world.getGameTime()) {
            this.spawnShoulderEntity(this.getLeftShoulderEntity());
            this.setLeftShoulderEntity(new CompoundNBT());
            this.spawnShoulderEntity(this.getRightShoulderEntity());
            this.setRightShoulderEntity(new CompoundNBT());
        }
    }

    private void spawnShoulderEntity(CompoundNBT p_192026_1_) {
        if (!this.world.isRemote && !p_192026_1_.isEmpty()) {
            EntityType.loadEntityUnchecked(p_192026_1_, this.world).ifPresent(p_226562_1_ -> {
                if (p_226562_1_ instanceof TameableEntity) {
                    ((TameableEntity)p_226562_1_).setOwnerId(this.entityUniqueID);
                }
                p_226562_1_.setPosition(this.getPosX(), this.getPosY() + (double)0.7f, this.getPosZ());
                ((ServerWorld)this.world).summonEntity((Entity)p_226562_1_);
            });
        }
    }

    @Override
    public abstract boolean isSpectator();

    @Override
    public boolean isSwimming() {
        return !this.abilities.isFlying && !this.isSpectator() && super.isSwimming();
    }

    public abstract boolean isCreative();

    @Override
    public boolean isPushedByWater() {
        EventPush eventPush = new EventPush(EventPush.PushType.Water);
        EventManager.call(eventPush);
        if (eventPush.isCancelled()) {
            return false;
        }
        return !this.abilities.isFlying;
    }

    public Scoreboard getWorldScoreboard() {
        return this.world.getScoreboard();
    }

    public ITextComponent getPrefix() {
        ScorePlayerTeam team = (ScorePlayerTeam)this.getTeam();
        if (team != null) {
            return team.getPrefix();
        }
        return StringTextComponent.EMPTY;
    }

    public ITextComponent getSuffix() {
        ScorePlayerTeam team = (ScorePlayerTeam)this.getTeam();
        if (team != null) {
            return team.getSuffix();
        }
        return StringTextComponent.EMPTY;
    }

    @Override
    public ITextComponent getDisplayName() {
        IFormattableTextComponent iformattabletextcomponent = ScorePlayerTeam.func_237500_a_(this.getTeam(), this.getName());
        return this.addTellEvent(iformattabletextcomponent);
    }

    private IFormattableTextComponent addTellEvent(IFormattableTextComponent p_208016_1_) {
        String s = this.getGameProfile().getName();
        return p_208016_1_.modifyStyle(p_234565_2_ -> p_234565_2_.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tell " + s + " ")).setHoverEvent(this.getHoverEvent()).setInsertion(s));
    }

    @Override
    public String getScoreboardName() {
        return this.getGameProfile().getName();
    }

    @Override
    public float getStandingEyeHeight(Pose poseIn, EntitySize sizeIn) {
        switch (poseIn) {
            case SWIMMING:
            case FALL_FLYING:
            case SPIN_ATTACK: {
                return 0.4f;
            }
            case CROUCHING: {
                return 1.27f;
            }
        }
        return 1.62f;
    }

    @Override
    public void setAbsorptionAmount(float amount) {
        if (amount < 0.0f) {
            amount = 0.0f;
        }
        this.getDataManager().set(ABSORPTION, Float.valueOf(amount));
    }

    @Override
    public float getAbsorptionAmount() {
        return this.getDataManager().get(ABSORPTION).floatValue();
    }

    public static UUID getUUID(GameProfile profile) {
        UUID uuid = profile.getId();
        if (uuid == null) {
            uuid = PlayerEntity.getOfflineUUID(profile.getName());
        }
        return uuid;
    }

    public static UUID getOfflineUUID(String username) {
        return UUID.nameUUIDFromBytes(("OfflinePlayer:" + username).getBytes(StandardCharsets.UTF_8));
    }

    public boolean isWearing(PlayerModelPart part) {
        return (this.getDataManager().get(PLAYER_MODEL_FLAG) & part.getPartMask()) == part.getPartMask();
    }

    @Override
    public boolean replaceItemInInventory(int inventorySlot, ItemStack itemStackIn) {
        if (inventorySlot >= 0 && inventorySlot < this.inventory.mainInventory.size()) {
            this.inventory.setInventorySlotContents(inventorySlot, itemStackIn);
            return true;
        }
        EquipmentSlotType equipmentslottype = inventorySlot == 100 + EquipmentSlotType.HEAD.getIndex() ? EquipmentSlotType.HEAD : (inventorySlot == 100 + EquipmentSlotType.CHEST.getIndex() ? EquipmentSlotType.CHEST : (inventorySlot == 100 + EquipmentSlotType.LEGS.getIndex() ? EquipmentSlotType.LEGS : (inventorySlot == 100 + EquipmentSlotType.FEET.getIndex() ? EquipmentSlotType.FEET : null)));
        if (inventorySlot == 98) {
            this.setItemStackToSlot(EquipmentSlotType.MAINHAND, itemStackIn);
            return true;
        }
        if (inventorySlot == 99) {
            this.setItemStackToSlot(EquipmentSlotType.OFFHAND, itemStackIn);
            return true;
        }
        if (equipmentslottype == null) {
            int i = inventorySlot - 200;
            if (i >= 0 && i < this.enterChestInventory.getSizeInventory()) {
                this.enterChestInventory.setInventorySlotContents(i, itemStackIn);
                return true;
            }
            return false;
        }
        if (!itemStackIn.isEmpty() && (!(itemStackIn.getItem() instanceof ArmorItem) && !(itemStackIn.getItem() instanceof ElytraItem) ? equipmentslottype != EquipmentSlotType.HEAD : MobEntity.getSlotForItemStack(itemStackIn) != equipmentslottype)) {
            return false;
        }
        this.inventory.setInventorySlotContents(equipmentslottype.getIndex() + this.inventory.mainInventory.size(), itemStackIn);
        return true;
    }

    public boolean hasReducedDebug() {
        return this.hasReducedDebug;
    }

    public void setReducedDebug(boolean reducedDebug) {
        this.hasReducedDebug = reducedDebug;
    }

    @Override
    public void forceFireTicks(int ticks) {
        super.forceFireTicks(this.abilities.disableDamage ? Math.min(ticks, 1) : ticks);
    }

    @Override
    public HandSide getPrimaryHand() {
        return this.dataManager.get(MAIN_HAND) == 0 ? HandSide.LEFT : HandSide.RIGHT;
    }

    public void setPrimaryHand(HandSide hand) {
        this.dataManager.set(MAIN_HAND, (byte)(hand != HandSide.LEFT ? 1 : 0));
    }

    public CompoundNBT getLeftShoulderEntity() {
        return this.dataManager.get(LEFT_SHOULDER_ENTITY);
    }

    protected void setLeftShoulderEntity(CompoundNBT tag) {
        this.dataManager.set(LEFT_SHOULDER_ENTITY, tag);
    }

    public CompoundNBT getRightShoulderEntity() {
        return this.dataManager.get(RIGHT_SHOULDER_ENTITY);
    }

    protected void setRightShoulderEntity(CompoundNBT tag) {
        this.dataManager.set(RIGHT_SHOULDER_ENTITY, tag);
    }

    public float getCooldownPeriod() {
        return (float)(1.0 / this.getAttributeValue(Attributes.ATTACK_SPEED) * 20.0);
    }

    public float getCooledAttackStrength(float adjustTicks) {
        return MathHelper.clamp(((float)this.ticksSinceLastSwing + adjustTicks) / this.getCooldownPeriod(), 0.0f, 1.0f);
    }

    public void resetCooldown() {
        this.ticksSinceLastSwing = 0;
    }

    public CooldownTracker getCooldownTracker() {
        return this.cooldownTracker;
    }

    @Override
    protected float getSpeedFactor() {
        return !this.abilities.isFlying && !this.isElytraFlying() ? super.getSpeedFactor() : 1.0f;
    }

    public float getLuck() {
        return (float)this.getAttributeValue(Attributes.LUCK);
    }

    public boolean canUseCommandBlock() {
        return this.abilities.isCreativeMode && this.getPermissionLevel() >= 2;
    }

    @Override
    public boolean canPickUpItem(ItemStack itemstackIn) {
        EquipmentSlotType equipmentslottype = MobEntity.getSlotForItemStack(itemstackIn);
        return this.getItemStackFromSlot(equipmentslottype).isEmpty();
    }

    @Override
    public EntitySize getSize(Pose poseIn) {
        return (EntitySize) SIZE_BY_POSE.getOrDefault((Object)poseIn, STANDING_SIZE);
    }

    @Override
    public ImmutableList<Pose> getAvailablePoses() {
        return ImmutableList.of(Pose.STANDING, Pose.CROUCHING, Pose.SWIMMING);
    }

    @Override
    public ItemStack findAmmo(ItemStack shootable) {
        if (!(shootable.getItem() instanceof ShootableItem)) {
            return ItemStack.EMPTY;
        }
        Predicate<ItemStack> predicate = ((ShootableItem)shootable.getItem()).getAmmoPredicate();
        ItemStack itemstack = ShootableItem.getHeldAmmo(this, predicate);
        if (!itemstack.isEmpty()) {
            return itemstack;
        }
        predicate = ((ShootableItem)shootable.getItem()).getInventoryAmmoPredicate();
        for (int i = 0; i < this.inventory.getSizeInventory(); ++i) {
            ItemStack itemstack1 = this.inventory.getStackInSlot(i);
            if (!predicate.test(itemstack1)) continue;
            return itemstack1;
        }
        return this.abilities.isCreativeMode ? new ItemStack(Items.ARROW) : ItemStack.EMPTY;
    }

    @Override
    public ItemStack onFoodEaten(World p_213357_1_, ItemStack p_213357_2_) {
        this.getFoodStats().consume(p_213357_2_.getItem(), p_213357_2_);
        this.addStat(Stats.ITEM_USED.get(p_213357_2_.getItem()));
        p_213357_1_.playSound(null, this.getPosX(), this.getPosY(), this.getPosZ(), SoundEvents.ENTITY_PLAYER_BURP, SoundCategory.PLAYERS, 0.5f, p_213357_1_.rand.nextFloat() * 0.1f + 0.9f);
        if (this instanceof ServerPlayerEntity) {
            CriteriaTriggers.CONSUME_ITEM.trigger((ServerPlayerEntity)this, p_213357_2_);
        }
        return super.onFoodEaten(p_213357_1_, p_213357_2_);
    }

    @Override
    protected boolean func_230295_b_(BlockState p_230295_1_) {
        return this.abilities.isFlying || super.func_230295_b_(p_230295_1_);
    }

    @Override
    public Vector3d getLeashPosition(float partialTicks) {
        float f2;
        double d0 = 0.22 * (this.getPrimaryHand() == HandSide.RIGHT ? -1.0 : 1.0);
        float f = MathHelper.lerp(partialTicks * 0.5f, this.rotationPitch, this.prevRotationPitch) * ((float)Math.PI / 180);
        float f1 = MathHelper.lerp(partialTicks, this.prevRenderYawOffset, this.renderYawOffset) * ((float)Math.PI / 180);
        if (!this.isElytraFlying() && !this.isSpinAttacking()) {
            if (this.isActualySwimming()) {
                return this.func_242282_l(partialTicks).add(new Vector3d(d0, 0.2, -0.15).rotatePitch(-f).rotateYaw(-f1));
            }
            double d5 = this.getBoundingBox().getYSize() - 1.0;
            double d6 = this.isCrouching() ? -0.2 : 0.07;
            return this.func_242282_l(partialTicks).add(new Vector3d(d0, d5, d6).rotateYaw(-f1));
        }
        Vector3d vector3d = this.getLook(partialTicks);
        Vector3d vector3d1 = this.getMotion();
        double d1 = Entity.horizontalMag(vector3d1);
        double d2 = Entity.horizontalMag(vector3d);
        if (d1 > 0.0 && d2 > 0.0) {
            double d3 = (vector3d1.x * vector3d.x + vector3d1.z * vector3d.z) / Math.sqrt(d1 * d2);
            double d4 = vector3d1.x * vector3d.z - vector3d1.z * vector3d.x;
            f2 = (float)(Math.signum(d4) * Math.acos(d3));
        } else {
            f2 = 0.0f;
        }
        return this.func_242282_l(partialTicks).add(new Vector3d(d0, -0.11, 0.85).rotateRoll(-f2).rotatePitch(-f).rotateYaw(-f1));
    }

    @Generated
    public Animation getFriendAnimation() {
        return this.friendAnimation;
    }

    @Generated
    public Animation getAnimation() {
        return this.animation;
    }

    public static enum SleepResult {
        NOT_POSSIBLE_HERE,
        NOT_POSSIBLE_NOW(new TranslationTextComponent("block.minecraft.bed.no_sleep")),
        TOO_FAR_AWAY(new TranslationTextComponent("block.minecraft.bed.too_far_away")),
        OBSTRUCTED(new TranslationTextComponent("block.minecraft.bed.obstructed")),
        OTHER_PROBLEM,
        NOT_SAFE(new TranslationTextComponent("block.minecraft.bed.not_safe"));

        @Nullable
        private final ITextComponent message;

        private SleepResult() {
            this.message = null;
        }

        private SleepResult(ITextComponent msg) {
            this.message = msg;
        }

        @Nullable
        public ITextComponent getMessage() {
            return this.message;
        }
    }
}

