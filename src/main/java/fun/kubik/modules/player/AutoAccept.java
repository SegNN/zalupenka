/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.modules.player;

import fun.kubik.Load;
import fun.kubik.events.api.EventHook;
import fun.kubik.events.main.packet.EventReceivePacket;
import fun.kubik.managers.friend.api.Friend;
import fun.kubik.managers.module.Module;
import fun.kubik.managers.module.main.Category;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.server.SChatPacket;

public class AutoAccept
extends Module {
    public AutoAccept() {
        super("AutoAccept", Category.PLAYER);
    }

    @EventHook
    public void receive(EventReceivePacket e) {
        SChatPacket p;
        String raw;
        IPacket<?> test;
        if (AutoAccept.mc.player != null && AutoAccept.mc.world != null && (test = e.getPacket()) instanceof SChatPacket && ((raw = (p = (SChatPacket)test).getChatComponent().getString()).contains("\u0442\u0435\u043b\u0435\u043f\u043e\u0440\u0442\u0438\u0440\u043e\u0432\u0430\u0442\u044c\u0441\u044f") || raw.contains("has requested teleport") || raw.contains("\u043f\u0440\u043e\u0441\u0438\u0442 \u043a \u0432\u0430\u043c \u0442\u0435\u043b\u0435\u043f\u043e\u0440\u0442\u0438\u0440\u043e\u0432\u0430\u0442\u044c\u0441\u044f"))) {
            boolean tpAccept = false;
            for (Friend friend : Load.getInstance().getHooks().getFriendManagers()) {
                if (!raw.contains(friend.getName())) continue;
                tpAccept = true;
                break;
            }
            if (!tpAccept) {
                return;
            }
            AutoAccept.mc.player.sendChatMessage("/tpaccept");
        }
    }
}

