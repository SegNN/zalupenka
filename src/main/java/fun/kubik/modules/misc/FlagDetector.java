/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.modules.misc;

import fun.kubik.events.api.EventHook;
import fun.kubik.events.main.packet.EventReceivePacket;
import fun.kubik.events.main.packet.EventSendPacket;
import fun.kubik.managers.module.Module;
import fun.kubik.managers.module.main.Category;
import fun.kubik.utils.client.ChatUtils;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.client.CConfirmTeleportPacket;
import net.minecraft.network.play.server.SConfirmTransactionPacket;

public class FlagDetector
extends Module {
    public FlagDetector() {
        super("FlagDetector", Category.MISC);
    }

    @EventHook
    public void receive(EventReceivePacket event) {
        IPacket<?> iPacket = event.getPacket();
        if (iPacket instanceof SConfirmTransactionPacket) {
            SConfirmTransactionPacket packet = (SConfirmTransactionPacket)iPacket;
            event.setCancelled(true);
        }
    }

    @EventHook
    public void send(EventSendPacket event) {
        IPacket iPacket = event.getPacket();
        if (iPacket instanceof CConfirmTeleportPacket) {
            CConfirmTeleportPacket packet = (CConfirmTeleportPacket)iPacket;
            ChatUtils.addClientMessage("Simulation");
        }
    }
}

