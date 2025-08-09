package fun.kubik.modules.combat;

import fun.kubik.Load;
import fun.kubik.events.api.EventHook;
import fun.kubik.events.main.EventUpdate;
import fun.kubik.events.main.input.EventMoveInput;
import fun.kubik.events.main.movement.EventJump;
import fun.kubik.events.main.movement.EventStrafe;
import fun.kubik.events.main.player.EventElytra;
import fun.kubik.events.main.player.EventSwimming;
import fun.kubik.events.main.player.EventSync;
import fun.kubik.helpers.module.aura.AuraHelpers;
import fun.kubik.helpers.module.aura.RayTrace;
import fun.kubik.managers.module.Module;
import fun.kubik.managers.module.main.Category;
import fun.kubik.managers.module.option.main.CheckboxOption;
import fun.kubik.managers.module.option.main.MultiOption;
import fun.kubik.managers.module.option.main.MultiOptionValue;
import fun.kubik.managers.module.option.main.SelectOption;
import fun.kubik.managers.module.option.main.SelectOptionValue;
import fun.kubik.managers.module.option.main.SliderOption;
import fun.kubik.modules.movement.AutoSprint;
import lombok.Generated;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ShieldItem;
import net.minecraft.network.play.client.CEntityActionPacket;
import net.minecraft.network.play.client.CPlayerDiggingPacket;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector2f;

public class Aura extends Module {
    public Vector2f selfRotation;
    public Vector2f fakeRotation;
    public Vector2f targetRotation;
    public Vector2f fakeTargetRotation;
    private final SelectOption mode = new SelectOption("Mode Rotation", 0, new SelectOptionValue("ReallyWorld"), new SelectOptionValue("Snap"), new SelectOptionValue("CakeWorld"));
    public final MultiOption options = new MultiOption("Options", new MultiOptionValue("Only Crits", true), new MultiOptionValue("Shield Breaker", true), new MultiOptionValue("Unpress Shield", true), new MultiOptionValue("Dont Hit Eating", false), new MultiOptionValue("Dont Hit Walls", false), new MultiOptionValue("Random Hits", true), new MultiOptionValue("Only Jump", true));
    private final SelectOption wallsBypass = new SelectOption("Walls Bypass", 0, new SelectOptionValue("None"), new SelectOptionValue("V1")).visible(() -> !this.options.getSelected("Dont Hit Walls"));
    private final SliderOption distance = new SliderOption("Distance", 3.0f, 1.0f, 5.0f).increment(0.05f);
    private final SliderOption preDistance = new SliderOption("Pre Distance", 0.5f, 0.0f, 30.0f).increment(0.5f).visible(() -> !this.mode.getSelected("Snap"));
    private final SliderOption snapTicks = new SliderOption("Snap Ticks", 1.0f, 1.0f, 10.0f).increment(1.0f).visible(() -> this.mode.getSelected("Snap"));
    private final SliderOption lerpSpeed = new SliderOption("Lerp Speed", 6.0f, 1.0f, 20.0f).increment(1.0f).visible(() -> this.mode.getSelected("CakeWorld"));
    private final CheckboxOption moveFix = new CheckboxOption("Movement Fix", true);
    private final SelectOption correctionType = new SelectOption("Correction Type", 0, new SelectOptionValue("Silent"), new SelectOptionValue("Focus")).visible(this.moveFix::getValue);
    private final MultiOption targets = new MultiOption("Targets", new MultiOptionValue("Players", true), new MultiOptionValue("Creative", true), new MultiOptionValue("Mobs", false), new MultiOptionValue("Naked", true), new MultiOptionValue("Friends", false));
    private final AuraHelpers auraHelpers = new AuraHelpers();
    private LivingEntity target = null;
    private long cps = 0L;
    private int ticks;
    private long spin = -1L;

    public Aura() {
        super("Aura", Category.COMBAT);
        this.setCurrentKey(82);
        this.settings(this.mode, this.options, this.wallsBypass, this.distance, this.preDistance, this.snapTicks, this.lerpSpeed, this.moveFix, this.correctionType, this.targets);
    }

    @EventHook
    public void update(EventUpdate eventUpdate) {
        if (!this.isToggled()) {
            return;
        }
        this.updateTarget();
        if (this.target == null) {
            this.cps = System.currentTimeMillis();
            this.selfRotation = new Vector2f(Aura.mc.player.rotationYaw, Aura.mc.player.rotationPitch);
            this.fakeRotation = new Vector2f(Aura.mc.player.rotationYaw, Aura.mc.player.rotationPitch);
            AuraHelpers.setDistance(100.0f);
            return;
        }
        ElytraTarget elytraTarget = (ElytraTarget)Load.getInstance().getHooks().getModuleManagers().findClass(ElytraTarget.class);
        if (AuraHelpers.getDistance() < 1.0f && elytraTarget.getBypass().getSelected("Snap") && elytraTarget.isToggled()) {
            this.auraHelpers.getSnapBypass().reset();
        }
        this.updateRotation();
        this.attackTarget();
        this.fakeRotation();
        if (this.mode.getSelected("ReallyWorld")) {
            if (RayTrace.blockResult(((Float)this.distance.getValue()).floatValue(), this.selfRotation.x, this.selfRotation.y, Aura.mc.player).getType() != RayTraceResult.Type.BLOCK || Aura.mc.player.isElytraFlying() || this.wallsBypass.getSelected("None")) {
                this.fastRotation();
            } else if (this.ticks > 0) {
                this.fastRotation();
                --this.ticks;
            } else {
                this.selfRotation = new Vector2f(Aura.mc.player.rotationYaw, Aura.mc.player.rotationPitch);
            }
        }
        if (this.mode.getSelected("Snap")) {
            if (Aura.mc.player.isElytraFlying()) {
                this.fastRotation();
            } else if (this.ticks > 0) {
                this.fastRotation();
                --this.ticks;
            } else {
                this.selfRotation = new Vector2f(Aura.mc.player.rotationYaw, Aura.mc.player.rotationPitch);
            }
        }
        if (this.mode.getSelected("CakeWorld")) {
            if (RayTrace.blockResult(((Float)this.distance.getValue()).floatValue(), this.selfRotation.x, this.selfRotation.y, Aura.mc.player).getType() != RayTraceResult.Type.BLOCK || Aura.mc.player.isElytraFlying() || this.wallsBypass.getSelected("None")) {
                this.lerpRotation();
            } else if (this.ticks > 0) {
                this.lerpRotation();
                --this.ticks;
            } else {
                this.selfRotation = new Vector2f(Aura.mc.player.rotationYaw, Aura.mc.player.rotationPitch);
            }
        }



    }

    @EventHook
    public void sync(EventSync eventSync) {
        if (!this.isToggled() || this.selfRotation == null) {
            return;
        }
        eventSync.setYaw(this.selfRotation.x);
        eventSync.setPitch(this.selfRotation.y);
        Aura.mc.player.rotationYawHead = this.selfRotation.x;
        Aura.mc.player.renderYawOffset = this.selfRotation.x;
        Aura.mc.player.rotationPitchHead = this.selfRotation.y;
    }

    @EventHook
    public void elytra(EventElytra eventElytra) {
        if (!this.isToggled() || this.selfRotation == null) {
            return;
        }
        if (this.fakeRotation != null) {
            eventElytra.setVisualPitch(this.selfRotation.y);
        }
        eventElytra.setYaw(this.selfRotation.x);
        eventElytra.setPitch(this.selfRotation.y);
    }

    @EventHook
    public void input(EventMoveInput eventMoveInput) {
        if (!this.isToggled() || !this.moveFix.getValue() || this.selfRotation == null || !this.correctionType.getSelected("Silent")) {
            return;
        }
        this.auraHelpers.fixMovement(eventMoveInput, this.selfRotation.x);
    }

    @EventHook
    public void strafe(EventStrafe eventStrafe) {
        if (!this.isToggled() || !this.moveFix.getValue() || this.selfRotation == null) {
            return;
        }
        eventStrafe.setYaw(this.selfRotation.x);
    }

    @EventHook
    public void jump(EventJump eventJump) {
        if (!this.isToggled() || !this.moveFix.getValue() || this.selfRotation == null) {
            return;
        }
        eventJump.setYaw(this.selfRotation.x);
    }

    @EventHook
    public void swimming(EventSwimming eventSwimming) {
        if (!this.isToggled() || !this.moveFix.getValue() || this.selfRotation == null) {
            return;
        }
        eventSwimming.setYaw(this.selfRotation.x);
        eventSwimming.setPitch(this.selfRotation.y);
    }

    private void updateTarget() {
        if (((ElytraTarget)Load.getInstance().getHooks().getModuleManagers().findClass(ElytraTarget.class)).isToggled() && Aura.mc.player.isElytraFlying()) {
            this.target = this.auraHelpers.sortEntities(this.target, ((Float)((ElytraTarget)Load.getInstance().getHooks().getModuleManagers().findClass(ElytraTarget.class)).getDistance().getValue()).floatValue(), ((Float)((ElytraTarget)Load.getInstance().getHooks().getModuleManagers().findClass(ElytraTarget.class)).getPreDistance().getValue()).floatValue(), this.targets);
            return;
        }
        this.target = this.auraHelpers.sortEntities(this.target, ((Float)this.distance.getValue()).floatValue(), this.mode.getSelected("Snap") ? 0.0f : ((Float)this.preDistance.getValue()).floatValue(), this.targets);
    }

    private void updateRotation() {
        boolean predict = ((ElytraTarget)Load.getInstance().getHooks().getModuleManagers().findClass(ElytraTarget.class)).isToggled() && ((ElytraTarget)Load.getInstance().getHooks().getModuleManagers().findClass(ElytraTarget.class)).getTargetOptions().getSelected("Elytra Predict");
        this.fakeTargetRotation = this.auraHelpers.fakeRotationAngles(this.target);
        this.targetRotation = this.auraHelpers.rotationAngles(this.target, predict, (ElytraTarget)Load.getInstance().getHooks().getModuleManagers().findClass(ElytraTarget.class));
    }

    private void lerpRotation() {
        this.selfRotation = this.auraHelpers.applyRotation(this.auraHelpers.smoothRotation(this.selfRotation, this.targetRotation, ((Float)this.lerpSpeed.getValue()).floatValue() * 5.0f + this.getRandom(20.0f)));
    }

    private void snapLerpRotation() {
        this.selfRotation = this.auraHelpers.applyRotation(this.auraHelpers.snapSmoothRotation(this.selfRotation, this.targetRotation, 4));
    }

    private void legendsRotation() {
        this.selfRotation = this.auraHelpers.applyRotation(this.auraHelpers.legendsRotation(this.selfRotation, this.targetRotation));
    }

    private void fastRotation() {
        this.selfRotation = this.auraHelpers.applyRotation(this.auraHelpers.fastRotation(this.selfRotation, this.targetRotation));
    }

    private void fakeRotation() {
        this.fakeRotation = this.auraHelpers.applyRotation(this.auraHelpers.fakeRotation(this.fakeRotation, this.fakeTargetRotation));
    }

    private void snapRotation() {
        this.selfRotation = this.auraHelpers.applyRotation(this.auraHelpers.snapRotation(this.selfRotation, this.targetRotation, 2));
    }

    private void attackTarget() {
        AutoSprint autoSprint = (AutoSprint)Load.getInstance().getHooks().getModuleManagers().findClass(AutoSprint.class);
        if (!this.isToggled() || !this.auraHelpers.attack(this.target, this.options, ((Float)this.distance.getValue()).floatValue(), this.options.getSelected("Only Crits"), this.options.getSelected("Random Hits")) || this.cps > System.currentTimeMillis()) {
            return;
        }
        int n = this.ticks = this.wallsBypass.getSelected("V1") && RayTrace.blockResult(((Float)this.distance.getValue()).floatValue(), this.selfRotation.x, this.selfRotation.y, Aura.mc.player).getType() == RayTraceResult.Type.BLOCK ? 1 : ((Float)this.snapTicks.getValue()).intValue();
        if (Aura.mc.player.isHandActive() && Aura.mc.player.getActiveHand() == Hand.OFF_HAND && Aura.mc.player.getHeldItemOffhand().getItem() instanceof ShieldItem && this.options.getSelected("Unpress Shield")) {
            Aura.mc.player.connection.sendPacket(new CPlayerDiggingPacket(CPlayerDiggingPacket.Action.RELEASE_USE_ITEM, new BlockPos(0, 0, 0), Direction.DOWN));
        }
        if (autoSprint.mode.getSelected("Rage") && autoSprint.canSprint() && CEntityActionPacket.lastUpdatedSprint) {
            Aura.mc.player.connection.sendPacket(new CEntityActionPacket(Aura.mc.player, CEntityActionPacket.Action.STOP_SPRINTING));
        }
        Aura.mc.playerController.attackEntity(Aura.mc.player, this.target);
        Aura.mc.player.swingArm(Hand.MAIN_HAND);
        if (this.options.getSelected("Shield Breaker")) {
            this.auraHelpers.shieldBreaker(this.target);
        }
        if (autoSprint.mode.getSelected("Rage") && autoSprint.canSprint()) {
            Aura.mc.player.connection.sendPacket(new CEntityActionPacket(Aura.mc.player, CEntityActionPacket.Action.START_SPRINTING));
        }
        this.cps = System.currentTimeMillis() + (TPSSync.getInstance() != null ? TPSSync.getInstance().getAttackDelay() : 460L);
        this.spin = (long)((double)System.currentTimeMillis() + 432.0);
    }

    @Override
    public void onEnabled() {
        if (Aura.mc.player != null) {
            this.selfRotation = new Vector2f(Aura.mc.player.rotationYaw, Aura.mc.player.rotationPitch);
            this.fakeRotation = new Vector2f(Aura.mc.player.rotationYaw, Aura.mc.player.rotationPitch);
        }
    }

    @Override
    public void onDisabled() {
        this.target = null;
        this.cps = 0L;
        AuraHelpers.setDistance(100.0f);
    }

    @Generated
    public SelectOption getMode() {
        return this.mode;
    }

    @Generated
    public SliderOption getDistance() {
        return this.distance;
    }

    @Generated
    public LivingEntity getTarget() {
        return this.target;
    }

    @Generated
    public long getCps() {
        return this.cps;
    }
}