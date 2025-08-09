/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.entity.projectile;

import fun.kubik.events.api.EventManager;
import fun.kubik.events.main.player.EventElytra;
import java.util.OptionalInt;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IRendersAsItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SSpawnObjectPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class FireworkRocketEntity
        extends ProjectileEntity
        implements IRendersAsItem {
    private static final DataParameter<ItemStack> FIREWORK_ITEM = EntityDataManager.createKey(FireworkRocketEntity.class, DataSerializers.ITEMSTACK);
    private static final DataParameter<OptionalInt> BOOSTED_ENTITY_ID = EntityDataManager.createKey(FireworkRocketEntity.class, DataSerializers.OPTIONAL_VARINT);
    private static final DataParameter<Boolean> field_213895_d = EntityDataManager.createKey(FireworkRocketEntity.class, DataSerializers.BOOLEAN);
    private int fireworkAge;
    private int lifetime;
    private LivingEntity boostedEntity;

    public FireworkRocketEntity(EntityType<? extends FireworkRocketEntity> p_i50164_1_, World p_i50164_2_) {
        super((EntityType<? extends ProjectileEntity>)p_i50164_1_, p_i50164_2_);
    }

    public FireworkRocketEntity(World worldIn, double x, double y, double z, ItemStack givenItem) {
        super((EntityType<? extends ProjectileEntity>)EntityType.FIREWORK_ROCKET, worldIn);
        this.fireworkAge = 0;
        this.setPosition(x, y, z);
        int i = 1;
        if (!givenItem.isEmpty() && givenItem.hasTag()) {
            this.dataManager.set(FIREWORK_ITEM, givenItem.copy());
            i += givenItem.getOrCreateChildTag("Fireworks").getByte("Flight");
        }
        this.setMotion(this.rand.nextGaussian() * 0.001, 0.05, this.rand.nextGaussian() * 0.001);
        this.lifetime = 10 * i + this.rand.nextInt(6) + this.rand.nextInt(7);
    }

    public FireworkRocketEntity(World p_i231581_1_, @Nullable Entity p_i231581_2_, double p_i231581_3_, double p_i231581_5_, double p_i231581_7_, ItemStack p_i231581_9_) {
        this(p_i231581_1_, p_i231581_3_, p_i231581_5_, p_i231581_7_, p_i231581_9_);
        this.setShooter(p_i231581_2_);
    }

    public FireworkRocketEntity(World p_i47367_1_, ItemStack p_i47367_2_, LivingEntity p_i47367_3_) {
        this(p_i47367_1_, p_i47367_3_, p_i47367_3_.getPosX(), p_i47367_3_.getPosY(), p_i47367_3_.getPosZ(), p_i47367_2_);
        this.dataManager.set(BOOSTED_ENTITY_ID, OptionalInt.of(p_i47367_3_.getEntityId()));
        this.boostedEntity = p_i47367_3_;
    }

    public FireworkRocketEntity(World p_i50165_1_, ItemStack p_i50165_2_, double p_i50165_3_, double p_i50165_5_, double p_i50165_7_, boolean p_i50165_9_) {
        this(p_i50165_1_, p_i50165_3_, p_i50165_5_, p_i50165_7_, p_i50165_2_);
        this.dataManager.set(field_213895_d, p_i50165_9_);
    }

    public FireworkRocketEntity(World p_i231582_1_, ItemStack p_i231582_2_, Entity p_i231582_3_, double p_i231582_4_, double p_i231582_6_, double p_i231582_8_, boolean p_i231582_10_) {
        this(p_i231582_1_, p_i231582_2_, p_i231582_4_, p_i231582_6_, p_i231582_8_, p_i231582_10_);
        this.setShooter(p_i231582_3_);
    }

    @Override
    protected void registerData() {
        this.dataManager.register(FIREWORK_ITEM, ItemStack.EMPTY);
        this.dataManager.register(BOOSTED_ENTITY_ID, OptionalInt.empty());
        this.dataManager.register(field_213895_d, false);
    }

    @Override
    public boolean isInRangeToRenderDist(double distance) {
        return distance < 4096.0 && !this.isAttachedToEntity();
    }

    @Override
    public boolean isInRangeToRender3d(double x, double y, double z) {
        return super.isInRangeToRender3d(x, y, z) && !this.isAttachedToEntity();
    }

    @Override
    public void tick() {
        super.tick();
        if (this.isAttachedToEntity()) {
            if (this.boostedEntity == null) {
                this.dataManager.get(BOOSTED_ENTITY_ID).ifPresent(p_213891_1_ -> {
                    Entity entity = this.world.getEntityByID(p_213891_1_);
                    if (entity instanceof LivingEntity) {
                        this.boostedEntity = (LivingEntity)entity;
                    }
                });
            }
            if (this.boostedEntity != null) {
                if (this.boostedEntity.isElytraFlying()) {
                    float speed = 1.5f;
                    EventElytra eventElytra = new EventElytra(this.boostedEntity.rotationPitch, this.boostedEntity.rotationYaw, speed, speed);
                    EventManager.call(eventElytra);
                    Vector3d vector3d = this.getVectorForRotation(eventElytra.getPitch(), eventElytra.getYaw());
                    Vector3d vector3d1 = this.boostedEntity.getMotion();
                    this.boostedEntity.setMotion(vector3d1.add(vector3d.x * 0.1 + (vector3d.x * (double)eventElytra.getSpeed() - vector3d1.x) * 0.5, vector3d.y * 0.1 + (vector3d.y * (double)eventElytra.getYSpeed() - vector3d1.y) * 0.5, vector3d.z * 0.1 + (vector3d.z * (double)eventElytra.getSpeed() - vector3d1.z) * 0.5));
                }
                this.setPosition(this.boostedEntity.getPosX(), this.boostedEntity.getPosY(), this.boostedEntity.getPosZ());
                this.setMotion(this.boostedEntity.getMotion());
            }
        } else {
            if (!this.func_213889_i()) {
                double d2 = this.collidedHorizontally ? 1.0 : 1.15;
                this.setMotion(this.getMotion().mul(d2, 1.0, d2).add(0.0, 0.04, 0.0));
            }
            Vector3d vector3d2 = this.getMotion();
            this.move(MoverType.SELF, vector3d2);
            this.setMotion(vector3d2);
        }
        RayTraceResult raytraceresult = ProjectileHelper.func_234618_a_(this, this::func_230298_a_);
        if (!this.noClip) {
            this.onImpact(raytraceresult);
            this.isAirBorne = true;
        }
        this.func_234617_x_();
        if (this.fireworkAge == 0 && !this.isSilent()) {
            this.world.playSound(null, this.getPosX(), this.getPosY(), this.getPosZ(), SoundEvents.ENTITY_FIREWORK_ROCKET_LAUNCH, SoundCategory.AMBIENT, 3.0f, 1.0f);
        }
        ++this.fireworkAge;
        if (this.world.isRemote && this.fireworkAge % 2 < 2) {
            this.world.addParticle(ParticleTypes.FIREWORK, this.getPosX(), this.getPosY() - 0.3, this.getPosZ(), this.rand.nextGaussian() * 0.05, -this.getMotion().y * 0.5, this.rand.nextGaussian() * 0.05);
        }
        if (!this.world.isRemote && this.fireworkAge > this.lifetime) {
            this.func_213893_k();
        }
    }

    private void func_213893_k() {
        this.world.setEntityState(this, (byte)17);
        this.dealExplosionDamage();
        this.remove();
    }

    @Override
    protected void onEntityHit(EntityRayTraceResult p_213868_1_) {
        super.onEntityHit(p_213868_1_);
        if (!this.world.isRemote) {
            this.func_213893_k();
        }
    }

    @Override
    protected void func_230299_a_(BlockRayTraceResult p_230299_1_) {
        BlockPos blockpos = new BlockPos(p_230299_1_.getPos());
        this.world.getBlockState(blockpos).onEntityCollision(this.world, blockpos, this);
        if (!this.world.isRemote() && this.func_213894_l()) {
            this.func_213893_k();
        }
        super.func_230299_a_(p_230299_1_);
    }

    private boolean func_213894_l() {
        ItemStack itemstack = this.dataManager.get(FIREWORK_ITEM);
        CompoundNBT compoundnbt = itemstack.isEmpty() ? null : itemstack.getChildTag("Fireworks");
        ListNBT listnbt = compoundnbt != null ? compoundnbt.getList("Explosions", 10) : null;
        return listnbt != null && !listnbt.isEmpty();
    }

    private void dealExplosionDamage() {
        ListNBT listnbt;
        float f = 0.0f;
        ItemStack itemstack = this.dataManager.get(FIREWORK_ITEM);
        CompoundNBT compoundnbt = itemstack.isEmpty() ? null : itemstack.getChildTag("Fireworks");
        ListNBT listNBT = listnbt = compoundnbt != null ? compoundnbt.getList("Explosions", 10) : null;
        if (listnbt != null && !listnbt.isEmpty()) {
            f = 5.0f + (float)(listnbt.size() * 2);
        }
        if (f > 0.0f) {
            if (this.boostedEntity != null) {
                this.boostedEntity.attackEntityFrom(DamageSource.func_233548_a_(this, this.func_234616_v_()), 5.0f + (float)(listnbt.size() * 2));
            }
            double d0 = 5.0;
            Vector3d vector3d = this.getPositionVec();
            for (LivingEntity livingentity : this.world.getEntitiesWithinAABB(LivingEntity.class, this.getBoundingBox().grow(5.0))) {
                if (livingentity == this.boostedEntity || this.getDistanceSq(livingentity) > 25.0) continue;
                boolean flag = false;
                for (int i = 0; i < 2; ++i) {
                    Vector3d vector3d1 = new Vector3d(livingentity.getPosX(), livingentity.getPosYHeight(0.5 * (double)i), livingentity.getPosZ());
                    BlockRayTraceResult raytraceresult = this.world.rayTraceBlocks(new RayTraceContext(vector3d, vector3d1, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, this));
                    if (((RayTraceResult)raytraceresult).getType() != RayTraceResult.Type.MISS) continue;
                    flag = true;
                    break;
                }
                if (!flag) continue;
                float f1 = f * (float)Math.sqrt((5.0 - (double)this.getDistance(livingentity)) / 5.0);
                livingentity.attackEntityFrom(DamageSource.func_233548_a_(this, this.func_234616_v_()), f1);
            }
        }
    }

    private boolean isAttachedToEntity() {
        return this.dataManager.get(BOOSTED_ENTITY_ID).isPresent();
    }

    public boolean func_213889_i() {
        return this.dataManager.get(field_213895_d);
    }

    @Override
    public void handleStatusUpdate(byte id) {
        if (id == 17 && this.world.isRemote) {
            if (!this.func_213894_l()) {
                for (int i = 0; i < this.rand.nextInt(3) + 2; ++i) {
                    this.world.addParticle(ParticleTypes.POOF, this.getPosX(), this.getPosY(), this.getPosZ(), this.rand.nextGaussian() * 0.05, 0.005, this.rand.nextGaussian() * 0.05);
                }
            } else {
                ItemStack itemstack = this.dataManager.get(FIREWORK_ITEM);
                CompoundNBT compoundnbt = itemstack.isEmpty() ? null : itemstack.getChildTag("Fireworks");
                Vector3d vector3d = this.getMotion();
                this.world.makeFireworks(this.getPosX(), this.getPosY(), this.getPosZ(), vector3d.x, vector3d.y, vector3d.z, compoundnbt);
            }
        }
        super.handleStatusUpdate(id);
    }

    @Override
    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putInt("Life", this.fireworkAge);
        compound.putInt("LifeTime", this.lifetime);
        ItemStack itemstack = this.dataManager.get(FIREWORK_ITEM);
        if (!itemstack.isEmpty()) {
            compound.put("FireworksItem", itemstack.write(new CompoundNBT()));
        }
        compound.putBoolean("ShotAtAngle", this.dataManager.get(field_213895_d));
    }

    @Override
    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        this.fireworkAge = compound.getInt("Life");
        this.lifetime = compound.getInt("LifeTime");
        ItemStack itemstack = ItemStack.read(compound.getCompound("FireworksItem"));
        if (!itemstack.isEmpty()) {
            this.dataManager.set(FIREWORK_ITEM, itemstack);
        }
        if (compound.contains("ShotAtAngle")) {
            this.dataManager.set(field_213895_d, compound.getBoolean("ShotAtAngle"));
        }
    }

    @Override
    public ItemStack getItem() {
        ItemStack itemstack = this.dataManager.get(FIREWORK_ITEM);
        return itemstack.isEmpty() ? new ItemStack(Items.FIREWORK_ROCKET) : itemstack;
    }

    @Override
    public boolean canBeAttackedWithItem() {
        return false;
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return new SSpawnObjectPacket(this);
    }
}

