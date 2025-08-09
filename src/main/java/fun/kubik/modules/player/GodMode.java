/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.modules.player;

import fun.kubik.events.api.EventHook;
import fun.kubik.events.main.EventUpdate;
import fun.kubik.events.main.packet.EventReceivePacket;
import fun.kubik.events.main.packet.EventSendPacket;
import fun.kubik.helpers.module.swap.SwapHelpers;
import fun.kubik.managers.module.Module;
import fun.kubik.managers.module.main.Category;
import fun.kubik.utils.time.TimerUtils;
import net.minecraft.client.gui.ClientBossInfo;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.network.play.server.SChatPacket;

public class GodMode
extends Module {
    private final SwapHelpers swap = new SwapHelpers();
    private final TimerUtils timer = new TimerUtils();
    private boolean closed = false;
    private boolean start = false;

    public GodMode() {
        super("GodMode", Category.PLAYER);
    }

    @Override
    public void onEnabled() {
        assert (GodMode.mc.player != null);
        GodMode.mc.player.sendChatMessage("/warps");
        this.timer.reset();
        this.closed = false;
        this.start = false;
    }

    @Override
    public void onDisabled() {
        this.closed = false;
        this.start = false;
    }

    @EventHook
    public void update(EventUpdate event) {
        if (GodMode.mc.player.openContainer != null && this.timer.hasTimeElapsed(500L) && !this.start) {
            GodMode.mc.playerController.windowClick(GodMode.mc.player.openContainer.windowId, 21, 0, ClickType.QUICK_MOVE, GodMode.mc.player);
            this.start = true;
        }
        if (this.timer.hasTimeElapsed(1000L) && !this.closed) {
            this.closeMenu();
        }
        if (this.start && this.isPvpMode() && GodMode.mc.player.openContainer != null) {
            GodMode.mc.playerController.windowClick(GodMode.mc.player.openContainer.windowId, 11, 0, ClickType.QUICK_MOVE, GodMode.mc.player);
        }
    }

    private void closeMenu() {
        mc.displayGuiScreen(null);
        GodMode.mc.mouseHelper.grabMouse();
        this.closed = true;
    }

    @EventHook
    public void send(EventSendPacket event) {
        if (event.getPacket() instanceof CPlayerPacket) {
            // empty if block
        }
    }

    @EventHook
    public void receive(EventReceivePacket event) {
        SChatPacket packet;
        IPacket<?> iPacket = event.getPacket();
        if (iPacket instanceof SChatPacket && ((packet = (SChatPacket)iPacket).getChatComponent().getString().equalsIgnoreCase("\u0412\u0430\u0440\u043f\u044b \u00bb \u0412\u044b \u0443\u0441\u043f\u0435\u0448\u043d\u043e \u0442\u0435\u043b\u0435\u043f\u043e\u0440\u0442\u0438\u0440\u043e\u0432\u0430\u043d\u044b \u043d\u0430 \u0432\u0430\u0440\u043f pvp!") || packet.getChatComponent().getString().equalsIgnoreCase("\u0410\u043d\u0442\u0438\u0420\u0435\u043b\u043e\u0433 \u00bb \u0412\u044b \u043d\u0435 \u043c\u043e\u0436\u0435\u0442\u0435 \u0442\u0435\u043b\u0435\u043f\u043e\u0440\u0442\u0438\u0440\u043e\u0432\u0430\u0442\u044c\u0441\u044f \u0432 PVP \u0440\u0435\u0436\u0438\u043c\u0435") || packet.getChatComponent().getString().equalsIgnoreCase("ReallyWorld \u00bb \u041d\u0435\u043b\u044c\u0437\u044f \u0442\u0435\u043b\u0435\u043f\u043e\u0440\u0442\u0438\u0440\u043e\u0432\u0430\u0442\u044c\u0441\u044f \u0438\u0437 \u043a\u0430\u0431\u0438\u043d\u043a\u0438."))) {
            event.setCancelled(true);
        }
    }

    private boolean isPvpMode() {
        for (ClientBossInfo bossInfo : GodMode.mc.ingameGUI.getBossOverlay().getMapBossInfos().values()) {
            String bossName = bossInfo.getName().getString();
            if (!bossName.contains("\u041f\u0412\u041f") && !bossName.contains("PVP")) continue;
            return true;
        }
        return false;
    }
}

