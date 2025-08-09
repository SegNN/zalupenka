/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.modules.player;

import fun.kubik.events.api.EventHook;
import fun.kubik.events.main.EventUpdate;
import fun.kubik.events.main.packet.EventReceivePacket;
import fun.kubik.events.main.packet.EventSendPacket;
import fun.kubik.events.main.player.EventLivingUpdate;
import fun.kubik.events.main.player.EventSync;
import fun.kubik.events.main.render.EventRender2D;
import fun.kubik.helpers.render.ColorHelpers;
import fun.kubik.managers.module.Module;
import fun.kubik.managers.module.main.Category;
import fun.kubik.managers.module.option.main.CheckboxOption;
import fun.kubik.managers.module.option.main.SliderOption;
import fun.kubik.utils.player.CameraUtils;
import fun.kubik.utils.player.MoveUtils;
import net.minecraft.item.BlockItem;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.network.play.client.CPlayerTryUseItemOnBlockPacket;
import net.minecraft.network.play.client.CUseEntityPacket;
import net.minecraft.network.play.server.SJoinGamePacket;
import net.minecraft.util.MovementInput;
import net.minecraft.util.MovementInputFromOptions;
import net.minecraft.util.math.vector.Vector3d;

public class FreeCam
extends Module {
    private final SliderOption speed = new SliderOption("\u0421\u043a\u043e\u0440\u043e\u0441\u0442\u044c \u043f\u043e XZ", 1.0f, 0.1f, 5.0f).increment(0.05f);
    private final CheckboxOption gamma = new CheckboxOption("Gamma", false);
    private Vector3d clientPosition = null;
    public CameraUtils player = null;
    boolean oldIsFlying;
    double gammasave = 0.0;

    public FreeCam() {
        super("FreeCam", Category.MOVEMENT);
        this.settings(this.speed, this.gamma);
    }

    @EventHook
    private void onPacket(EventSendPacket e) {
        IPacket iPacket;
        if (FreeCam.mc.world == null || FreeCam.mc.player == null || !FreeCam.mc.player.isAlive()) {
            this.toggle();
        }
        if (this.player != null || !FreeCam.mc.player.isAlive()) {
            if (e.getPacket() instanceof CUseEntityPacket && ((CUseEntityPacket)e.getPacket()).getEntityFromWorld(FreeCam.mc.world).getEntityId() == FreeCam.mc.player.getEntityId()) {
                e.setCancelled(true);
            }
            if (e.getPacket() instanceof CPlayerTryUseItemOnBlockPacket && !(FreeCam.mc.player.inventory.getCurrentItem().getItem() instanceof BlockItem)) {
                e.setCancelled(true);
            }
        }
        if ((iPacket = e.getPacket()) instanceof CPlayerPacket) {
            CPlayerPacket p = (CPlayerPacket)iPacket;
            if (this.player != null || !FreeCam.mc.player.isAlive()) {
                if (p.isMoving()) {
                    p.setX(this.player.getPosX());
                    p.setY(this.player.getPosY());
                    p.setZ(this.player.getPosZ());
                }
                p.setOnGround(this.player.isOnGround());
                if (p.isRotating()) {
                    p.setYaw(this.player.rotationYaw);
                    p.setPitch(this.player.rotationPitch);
                }
            }
            if (this.player == null) {
                this.toggle();
            }
        }
    }

    @EventHook
    public void lox(EventReceivePacket e) {
        if (e.getPacket() instanceof SJoinGamePacket) {
            this.toggle();
        }
    }

    @EventHook
    private void EventLiving(EventLivingUpdate livingUpdateEvent) {
        if (FreeCam.mc.world == null || FreeCam.mc.player == null || !FreeCam.mc.player.isAlive()) {
            this.toggle();
        }
        if (this.player != null) {
            this.player.noClip = true;
            this.player.setOnGround(false);
            FreeCam.mc.player.motion = Vector3d.ZERO;
            if (FreeCam.mc.gameSettings.keyBindJump.isKeyDown()) {
                this.player.setMotion(new Vector3d(0.0, ((Float)this.speed.getValue()).floatValue(), 0.0));
            }
            if (!FreeCam.mc.gameSettings.keyBindJump.isKeyDown()) {
                this.player.setMotion(new Vector3d(0.0, 0.0, 0.0));
            }
            if (FreeCam.mc.gameSettings.keyBindSneak.isKeyDown()) {
                this.player.setMotion(new Vector3d(0.0, -((Float)this.speed.getValue()).floatValue(), 0.0));
            }
            MoveUtils.setMotion(((Float)this.speed.getValue()).floatValue(), this.player);
            this.player.abilities.isFlying = true;
        }
    }

    @EventHook
    public void render(EventRender2D.Pre event) {
        suisse_intl.drawCenteredText(event.getMatrixStack(), "Vclip: " + (int)(this.player.getPosY() - FreeCam.mc.player.getPosY()), (float)mc.getMainWindow().getWidth() / 2.0f, (float)mc.getMainWindow().getHeight() / 2.0f - 30.0f, ColorHelpers.rgba(255, 255, 255, 255), 16.0f);
    }

    @EventHook
    private void onMotion(EventSync eventSync) {
        FreeCam.mc.player.motion = Vector3d.ZERO;
        eventSync.setCancelled(true);
    }

    @Override
    public void onEnabled() {
        if (((Boolean)this.gamma.getValue()).booleanValue()) {
            this.gammasave = FreeCam.mc.gameSettings.gamma;
        }
        if (FreeCam.mc.player != null && FreeCam.mc.world != null) {
            FreeCam.mc.player.setJumping(false);
            this.initializeFakePlayer();
            this.addFakePlayer();
            this.player.spawn();
            FreeCam.mc.player.movementInput = new MovementInput();
            FreeCam.mc.player.moveForward = 0.0f;
            FreeCam.mc.player.moveStrafing = 0.0f;
            mc.setRenderViewEntity(this.player);
        }
    }

    @Override
    public void onDisabled() {
        if (((Boolean)this.gamma.getValue()).booleanValue()) {
            FreeCam.mc.gameSettings.gamma = this.gammasave;
        }
        if (FreeCam.mc.player != null) {
            this.removeFakePlayer();
            mc.setRenderViewEntity(null);
            FreeCam.mc.player.movementInput = new MovementInputFromOptions(FreeCam.mc.gameSettings);
        }
    }

    @EventHook
    private void handleLivingUpdate(EventUpdate eventUpdate) {
        if (((Boolean)this.gamma.getValue()).booleanValue()) {
            FreeCam.mc.gameSettings.gamma = 1000.0;
        }
        this.player.noClip = true;
        this.player.setOnGround(false);
        MoveUtils.setMotion(((Float)this.speed.getValue()).floatValue(), this.player);
        this.oldIsFlying = this.player.abilities.isFlying;
        this.player.abilities.isFlying = true;
    }

    private void initializeFakePlayer() {
        this.clientPosition = FreeCam.mc.player.getPositionVec();
        this.player = new CameraUtils(1337);
        this.player.copyLocationAndAnglesFrom(FreeCam.mc.player);
        this.player.rotationYawHead = FreeCam.mc.player.rotationYawHead;
        this.player.rotationPitchHead = FreeCam.mc.player.rotationPitchHead;
    }

    private void addFakePlayer() {
        this.clientPosition = FreeCam.mc.player.getPositionVec();
        FreeCam.mc.world.addEntity(1337, this.player);
    }

    private void removeFakePlayer() {
        this.resetFlying();
        FreeCam.mc.world.removeEntityFromWorld(1337);
        this.player = null;
        this.clientPosition = null;
    }

    private void resetFlying() {
        if (this.oldIsFlying) {
            FreeCam.mc.player.abilities.isFlying = false;
            this.oldIsFlying = false;
        }
    }
}

