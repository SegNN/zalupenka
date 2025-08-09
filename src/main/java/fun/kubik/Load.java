/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik;

import fun.kubik.events.api.EventManager;
import fun.kubik.helpers.interfaces.IFastAccess;
import fun.kubik.managers.config.main.AltConfig;
import fun.kubik.managers.config.main.ClientConfig;
import fun.kubik.managers.config.main.DraggableConfig;
import fun.kubik.managers.config.main.FriendConfig;
import fun.kubik.managers.config.main.MacroConfig;
import fun.kubik.managers.config.main.ModuleConfig;
import fun.kubik.managers.config.main.StaffConfig;
import fun.kubik.managers.hook.HookManagers;
import fun.kubik.ui.alt.AltScreen;
import fun.kubik.ui.autobuy.BuyScreen;
import fun.kubik.ui.screen.UIScreen;
import fun.kubik.utils.ServerTPS;
import fun.kubik.utils.discord.rpc.DiscordManager;
import java.io.File;
import lombok.Generated;
import ru.kotopushka.j2c.sdk.annotations.NativeInclude;

public class Load
implements IFastAccess {
    private static final double startTime = System.currentTimeMillis();
    private static Load instance;
    private final HookManagers hooks = new HookManagers();
    private final UIScreen uiScreen;
    private final AltScreen altScreen;
    private final DiscordManager discordManager;
    private final BuyScreen buyScreen;

    public Load() {
        instance = this;
        this.discordManager = new DiscordManager();
        this.uiScreen = new UIScreen();
        this.buyScreen = new BuyScreen();
        this.altScreen = new AltScreen();
        this.autoLoad();
        // Регистрируем ServerTPS в EventManager для получения пакетов
        EventManager.register(ServerTPS.getInstance());
        System.out.println("[Load] ServerTPS registered in EventManager");
    }

    private void autoLoad() {
        ((ClientConfig)this.hooks.getConfigManagers().findClass(ClientConfig.class)).fastLoad();
        ((FriendConfig)this.hooks.getConfigManagers().findClass(FriendConfig.class)).fastLoad();
        ((StaffConfig)this.hooks.getConfigManagers().findClass(StaffConfig.class)).fastLoad();
        ((ModuleConfig)this.hooks.getConfigManagers().findClass(ModuleConfig.class)).fastLoad();
        ((DraggableConfig)this.hooks.getConfigManagers().findClass(DraggableConfig.class)).fastLoad();
        ((MacroConfig)this.hooks.getConfigManagers().findClass(MacroConfig.class)).fastLoad();
        ((AltConfig)Load.getInstance().getHooks().getConfigManagers().findClass(AltConfig.class)).fastLoad();
    }

    private void autoSave() {
        ((FriendConfig)this.hooks.getConfigManagers().findClass(FriendConfig.class)).fastSave();
        ((ModuleConfig)this.hooks.getConfigManagers().findClass(ModuleConfig.class)).setName("module");
        ((ModuleConfig)this.hooks.getConfigManagers().findClass(ModuleConfig.class)).setPath(new File("fun/kubik/configs"));
        ((ModuleConfig)this.hooks.getConfigManagers().findClass(ModuleConfig.class)).fastSave();
        ((DraggableConfig)this.hooks.getConfigManagers().findClass(DraggableConfig.class)).fastSave();
        ((ClientConfig)this.hooks.getConfigManagers().findClass(ClientConfig.class)).fastSave();
        ((AltConfig)Load.getInstance().getHooks().getConfigManagers().findClass(AltConfig.class)).fastSave();
    }

    @NativeInclude
    public void shutDown() {
        System.out.println("babax");
        this.autoSave();
    }

    @Generated
    public static double getStartTime() {
        return startTime;
    }

    @Generated
    public static Load getInstance() {
        return instance;
    }

    @Generated
    public HookManagers getHooks() {
        return this.hooks;
    }

    @Generated
    public UIScreen getUiScreen() {
        return this.uiScreen;
    }

    @Generated
    public AltScreen getAltScreen() {
        return this.altScreen;
    }

    @Generated
    public DiscordManager getDiscordManager() {
        return this.discordManager;
    }

    @Generated
    public BuyScreen getBuyScreen() {
        return this.buyScreen;
    }
}

