/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.modules.misc;

import fun.kubik.Load;
import fun.kubik.events.api.EventHook;
import fun.kubik.events.main.EventUpdate;
import fun.kubik.helpers.render.ColorHelpers;
import fun.kubik.managers.module.Module;
import fun.kubik.managers.module.main.Category;
import fun.kubik.managers.module.option.main.CheckboxOption;
import fun.kubik.managers.module.option.main.MultiOption;
import fun.kubik.managers.module.option.main.MultiOptionValue;
import fun.kubik.managers.module.option.main.SelectOption;
import fun.kubik.managers.module.option.main.SelectOptionValue;
import fun.kubik.managers.module.option.main.SliderOption;
// import fun.kubik.managers.module.option.main.StringOption;
import fun.kubik.modules.combat.AntiBot;
import java.util.Comparator;
import java.util.Locale;
import java.util.regex.Pattern;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ClientBossInfo;
import net.minecraft.client.gui.overlay.PlayerTabOverlayGui;
import net.minecraft.client.network.play.NetworkPlayerInfo;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Team;
import fun.kubik.helpers.module.swap.SwapHelpers;
import fun.kubik.utils.time.TimerUtils;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.network.play.client.CHeldItemChangePacket;
import net.minecraft.network.play.client.CPlayerTryUseItemPacket;
import net.minecraft.util.text.ITextComponent;
import java.lang.reflect.Field;
import ru.kotopushka.j2c.sdk.annotations.NativeInclude;

public class AutoLeave
extends Module {
    private final Pattern namePattern = Pattern.compile("^\\w{3,16}$");
    private final Pattern prefixMatches = Pattern.compile(".*(mod|der|adm|help|wne|taf|curat|dev|supp|\ua521|yt|\ua513|\ua517|\ua525|\ua529|\ua533|\ua537|\ua505|\ua501).*");
    private final SelectOption type = new SelectOption("Type", 0,
            new SelectOptionValue("Spawn"),
            new SelectOptionValue("Hub"),
            new SelectOptionValue("Disconnect")
    );
    private final CheckboxOption rejoin = new CheckboxOption("Rejoin", false).visible(() -> this.type.getSelected("Disconnect"));
    private final SliderOption rejoinDelayMs = new SliderOption("Rejoin Delay (ms)", 3000.0f, 500.0f, 60000.0f)
            .increment(500.0f)
            .visible(() -> this.type.getSelected("Disconnect") && ((Boolean)this.rejoin.getValue()).booleanValue());
    private final SliderOption grief = new SliderOption("Grief", 1.0f, 1.0f, 54.0f)
            .increment(1.0f)
            .visible(() -> this.type.getSelected("Hub"));
    private final SliderOption hubDelayMs = new SliderOption("Join Delay (ms)", 3000.0f, 0.0f, 60000.0f)
            .increment(100.0f)
            .visible(() -> this.type.getSelected("Hub"));
    private final SliderOption distance = new SliderOption("Distance", 15.0f, 1.0f, 100.0f).increment(1.0f);
    private final MultiOption targets = new MultiOption("Targets", new MultiOptionValue("Players", true), new MultiOptionValue("Moder", true));
    private final CheckboxOption autoDisable = new CheckboxOption("Auto Disable", true);
    private final CheckboxOption onlyVanish = new CheckboxOption("Only Vanish", false).visible(() -> this.targets.getSelected("Moder"));

    private final TimerUtils hubTimer = new TimerUtils();
    private final SwapHelpers hubSwap = new SwapHelpers();
    private boolean hubPending = false;
    private int hubStage = 0; // 0: wait delay, 1: ensure lobby & open compass, 2: try /hub grief-N, 3: click survival, 4: click grief
    private long lastHubCmdMs = 0L;
    private long lastGriefCmdMs = 0L;

    public AutoLeave() {
        super("AutoLeave", Category.MISC);
        this.settings(this.type, this.distance, this.targets, this.autoDisable, this.onlyVanish, this.rejoin, this.rejoinDelayMs, this.grief, this.hubDelayMs);
    }

    private boolean isInLobbyByTab() {
        try {
            if (mc == null || mc.ingameGUI == null) return false;
            PlayerTabOverlayGui tab = mc.ingameGUI.getTabList();
            Field headerF = PlayerTabOverlayGui.class.getDeclaredField("header");
            Field footerF = PlayerTabOverlayGui.class.getDeclaredField("footer");
            headerF.setAccessible(true);
            footerF.setAccessible(true);
            ITextComponent header = (ITextComponent) headerF.get(tab);
            ITextComponent footer = (ITextComponent) footerF.get(tab);
            String hs = header != null ? header.getString().toLowerCase(java.util.Locale.ROOT) : "";
            String fs = footer != null ? footer.getString().toLowerCase(java.util.Locale.ROOT) : "";
            return hs.contains("lobby") || fs.contains("lobby");
        } catch (Throwable ignored) {
            return false;
        }
    }

    @EventHook
    public void update(EventUpdate event) {
        // Handle delayed join flow for Hub type similar to GriefJoiner
        if (this.type.getSelected("Hub") && this.hubPending) {
            long joinDelay = ((Float)this.hubDelayMs.getValue()).longValue();
            long retryDelay = 200L;
            if (this.hubStage == 0) {
                if (this.hubTimer.hasTimeElapsed(joinDelay)) {
                    this.hubStage = 1;
                    this.hubTimer.reset();
                }
                return;
            }
            if (this.hubStage == 1) {
                if (!this.isInLobbyByTab()) {
                    long now = System.currentTimeMillis();
                    if (now - this.lastHubCmdMs > 2000L && mc.player != null) {
                        mc.player.sendChatMessage("/hub");
                        this.lastHubCmdMs = now;
                    }
                    return;
                }
                // we are in lobby → first, try direct grief join by command (more stable), then fallback to GUI
                if (this.hubTimer.hasTimeElapsed(retryDelay)) {
                    long now = System.currentTimeMillis();
                    if (now - this.lastGriefCmdMs > 2000L && mc.player != null) {
                        int target = ((Float)this.grief.getValue()).intValue();
                        mc.player.sendChatMessage("/hub grief-" + target);
                        this.lastGriefCmdMs = now;
                    }
                    this.hubStage = 2; // move to survival GUI fallback next tick
                    this.hubTimer.reset();
                }
                return;
            }
            if (this.hubStage == 2) {
                // fallback via GUI
                if (this.hubTimer.hasTimeElapsed(retryDelay) && mc.currentScreen instanceof net.minecraft.client.gui.screen.inventory.ContainerScreen && mc.player != null && mc.playerController != null && mc.player.openContainer != null) {
                    int survival = this.hubSwap.find("\u0413\u0420\u0418\u0424\u0415\u0420\u0421\u041a\u041e\u0415 \u0412\u042b\u0416\u0418\u0412\u0410\u041d\u0418\u0415 (1");
                    if (survival != -1) {
                        mc.playerController.windowClick(mc.player.openContainer.windowId, survival, 0, ClickType.QUICK_MOVE, mc.player);
                        this.hubStage = 3;
                        this.hubTimer.reset();
                        return;
                    }
                    // fallback: try grief directly
                    int target = ((Float)this.grief.getValue()).intValue();
                    int griefName = this.hubSwap.find("\u0413\u0420\u0418\u0424 #" + target + " (1");
                    if (griefName != -1) {
                        mc.playerController.windowClick(mc.player.openContainer.windowId, griefName, 0, ClickType.QUICK_MOVE, mc.player);
                        this.hubPending = false;
                        this.hubStage = 0;
                        this.hubTimer.reset();
                        return;
                    }
                }
                // if GUI not open yet, try open compass repeatedly
                if (this.hubTimer.hasTimeElapsed(retryDelay) && !(mc.currentScreen instanceof net.minecraft.client.gui.screen.inventory.ContainerScreen)) {
                    int slot = this.hubSwap.find(Items.COMPASS);
                    if (this.hubSwap.haveHotBar(slot) && slot != -1 && mc.player != null && mc.player.connection != null) {
                        mc.player.connection.sendPacket(new CHeldItemChangePacket(this.hubSwap.format(slot)));
                        mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
                        this.hubTimer.reset();
                    }
                }
                return;
            }
            if (this.hubStage == 3) {
                if (this.hubTimer.hasTimeElapsed(retryDelay) && mc.currentScreen instanceof net.minecraft.client.gui.screen.inventory.ContainerScreen && mc.player != null && mc.playerController != null && mc.player.openContainer != null) {
                    int target = ((Float)this.grief.getValue()).intValue();
                    int griefName = this.hubSwap.find("\u0413\u0420\u0418\u0424 #" + target + " (1");
                    if (griefName != -1) {
                        mc.playerController.windowClick(mc.player.openContainer.windowId, griefName, 0, ClickType.QUICK_MOVE, mc.player);
                        this.hubPending = false;
                        this.hubStage = 0;
                        this.hubTimer.reset();
                        return;
                    }
                }
                return;
            }
        }
        if (this.hubPending) {
            return;
        }
        if (this.targets.getSelected("Players") && this.nonPvp()) {
            if (AutoLeave.mc.world == null || AutoLeave.mc.player == null) return;
            for (PlayerEntity playerEntity : AutoLeave.mc.world.getPlayers()) {
                if (!(AutoLeave.mc.player.getDistance(playerEntity) <= ((Float)this.distance.getValue()).floatValue()) || Load.getInstance().getHooks().getFriendManagers().is(playerEntity.getGameProfile().getName()) || playerEntity == AutoLeave.mc.player || AntiBot.checkBot(playerEntity)) continue;
                this.quit(playerEntity.getName().getString());
            }
        }
        if (this.targets.getSelected("Moder") && this.nonPvp()) {
            if (AutoLeave.mc.world == null || AutoLeave.mc.player == null) return;
            for (ScorePlayerTeam scorePlayerTeam : AutoLeave.mc.world.getScoreboard().getTeams().stream().sorted(Comparator.comparing(Team::getName)).toList()) {
                String name = scorePlayerTeam.getMembershipCollection().toString().replaceAll("[\\[\\]]", "").replace(" ", "");
                boolean vanish = true;
                for (NetworkPlayerInfo info : mc.getConnection().getPlayerInfoMap()) {
                    if (!info.getGameProfile().getName().equals(name)) continue;
                    vanish = false;
                }
                if (!this.namePattern.matcher(name).matches() || name.equals(AutoLeave.mc.player.getName().getString())) continue;
                if (!vanish && ((Boolean)this.onlyVanish.getValue()).booleanValue() && (this.prefixMatches.matcher(scorePlayerTeam.getPrefix().getString().toLowerCase(Locale.ROOT)).matches() || Load.getInstance().getHooks().getStaffManagers().is(name))) {
                    this.quit(scorePlayerTeam.getName());
                }
                if (!vanish || scorePlayerTeam.getPrefix().getString().isEmpty()) continue;
                this.quit(scorePlayerTeam.getName());
            }
        }
    }

    private boolean nonPvp() {
        for (ClientBossInfo bossInfo : AutoLeave.mc.ingameGUI.getBossOverlay().getMapBossInfos().values()) {
            String bossName = bossInfo.getName().getString();
            if (!bossName.contains("\u041f\u0412\u041f") && !bossName.contains("PVP")) continue;
            return false;
        }
        return true;
    }

    @NativeInclude
    private void quit(String name) {
        if (this.type.getSelected("Spawn")) {
            if (AutoLeave.mc.player != null) AutoLeave.mc.player.sendChatMessage("/spawn");
            System.out.println(name);
        } else if (this.type.getSelected("Hub")) {
            // Do not spam /hub if TAB already shows Lobby
            if (!this.isInLobbyByTab() && AutoLeave.mc.player != null) {
                AutoLeave.mc.player.sendChatMessage("/hub");
            }
            this.hubPending = true;
            this.hubStage = 0;
            this.hubTimer.reset();
        } else {
            boolean wantRejoin = ((Boolean)this.rejoin.getValue()).booleanValue();
            int delay = ((Float)this.rejoinDelayMs.getValue()).intValue();
            // close channel (disconnect), guarding nulls
            if (AutoLeave.mc.player != null && AutoLeave.mc.player.connection != null && AutoLeave.mc.player.connection.getNetworkManager() != null) {
                AutoLeave.mc.player.connection.getNetworkManager().closeChannel(ColorHelpers.gradient("Near player: ", ColorHelpers.getThemeColor(1), ColorHelpers.getThemeColor(2)));
            }
            if (wantRejoin) {
                // Schedule reconnect to the same server after delay
                new Thread(() -> {
                    try { Thread.sleep(Math.max(0, delay)); } catch (InterruptedException ignored) {}
                    Minecraft mc = AutoLeave.mc;
                    if (mc == null) return;
                    net.minecraft.client.multiplayer.ServerData data = mc.getCurrentServerData();
                    if (data != null) {
                        mc.execute(() -> mc.displayGuiScreen(new net.minecraft.client.gui.screen.ConnectingScreen(new net.minecraft.client.gui.screen.MultiplayerScreen(new net.minecraft.client.gui.screen.MainMenuScreen()), mc, data)));
                    }
                }, "AutoLeave-Rejoin").start();
            }
        }
        // Do not auto-disable the module after triggering actions
    }
}

