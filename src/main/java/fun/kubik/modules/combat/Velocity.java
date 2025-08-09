/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.modules.combat;

import fun.kubik.Load;
import fun.kubik.events.api.EventHook;
import fun.kubik.events.main.EventUpdate;
import fun.kubik.events.main.packet.EventReceivePacket;
import fun.kubik.events.main.packet.EventSendPacket;
import fun.kubik.managers.module.Module;
import fun.kubik.managers.module.main.Category;
import fun.kubik.managers.module.option.main.SelectOption;
import fun.kubik.managers.module.option.main.SelectOptionValue;
import fun.kubik.utils.time.TimerUtils;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.client.CConfirmTransactionPacket;
import net.minecraft.network.play.server.SConfirmTransactionPacket;
import net.minecraft.network.play.server.SEntityVelocityPacket;
import ru.kotopushka.j2c.sdk.annotations.NativeInclude;

public class Velocity
extends Module {
    private final TimerUtils timer = new TimerUtils();
    private final SelectOption mode = new SelectOption("Mode", 0, new SelectOptionValue("Matrix"), new SelectOptionValue("ReallyWorld"));

    public Velocity() {
        super("Velocity", Category.COMBAT);
        this.settings(this.mode);
    }

    @EventHook
    public void update(EventUpdate event) {
        if (Velocity.mc.player.getDistance(((Aura)Load.getInstance().getHooks().getModuleManagers().findClass(Aura.class)).getTarget()) <= ((Float)((Aura)Load.getInstance().getHooks().getModuleManagers().findClass(Aura.class)).getDistance().getValue()).floatValue() && ((Aura)Load.getInstance().getHooks().getModuleManagers().findClass(Aura.class)).isToggled() && this.mode.getSelected("ReallyWorld")) {
            this.timer.reset();
        }
    }

    @EventHook
    @NativeInclude
    public void receive(EventReceivePacket event) {
        SEntityVelocityPacket packet;
        IPacket<?> iPacket;
        if (this.mode.getSelected("Matrix") && (iPacket = event.getPacket()) instanceof SEntityVelocityPacket && (packet = (SEntityVelocityPacket)iPacket).getEntityID() == Velocity.mc.player.getEntityId()) {
            event.setCancelled(true);
        }
        if (this.timer.hasTimeElapsed(400L) && this.mode.getSelected("ReallyWorld")) {
            iPacket = event.getPacket();
            if (iPacket instanceof SEntityVelocityPacket && (packet = (SEntityVelocityPacket)iPacket).getEntityID() == Velocity.mc.player.getEntityId()) {
                event.setCancelled(true);
            }
            if (event.getPacket() instanceof SConfirmTransactionPacket) {
                event.setCancelled(true);
            }
        }
    }

    @EventHook
    public void send(EventSendPacket event) {
        if (event.getPacket() instanceof CConfirmTransactionPacket && this.timer.hasTimeElapsed(400L) && this.mode.getSelected("ReallyWorld")) {
            event.setCancelled(true);
        }
    }
}

