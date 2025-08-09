/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.managers.client;

import fun.kubik.Load;
import fun.kubik.events.api.EventManager;
import fun.kubik.events.main.chat.EventTranslate;
import fun.kubik.helpers.interfaces.IFastAccess;
import fun.kubik.managers.config.main.ClientConfig;
import fun.kubik.managers.hook.main.ModuleManagers;
import fun.kubik.managers.module.Module;
import fun.kubik.modules.misc.UnHook;
import fun.kubik.utils.math.RandomNumberUtils;
import java.io.File;
import lombok.Generated;
import net.minecraft.client.GameConfiguration;
import net.minecraft.client.Minecraft;
import net.optifine.shaders.Shaders;

public final class ClientManagers
implements IFastAccess {
    private static final RandomNumberUtils randomNumber = new RandomNumberUtils(1000.0f, 9999.0f, 1.0f);
    private static String random = "";
    private static boolean unHook = false;
    private static String language = "eng";

    public static void changeLanguage(String name) {
        ClientManagers.setLanguage(name);
        EventTranslate eventTranslate = new EventTranslate();
        EventManager.call(eventTranslate);
        Load.getInstance().getUiScreen().translate();
        Load.getInstance().getHooks().getDraggableController().translate();
    }

    public static void update() {
        ClientManagers.getRandomNumber().generate();
        random = String.format("%.0f", Float.valueOf(ClientManagers.getRandomNumber().getCurrent()));
    }

    public static void unhook() {
        ClientManagers.setUnHook(false);
        ModuleManagers modules = Load.getInstance().getHooks().getModuleManagers();
        for (Module module : modules) {
            if (!((UnHook)modules.findClass(UnHook.class)).getBackup().contains(module)) continue;
            module.toggle();
        }
        Minecraft.getInstance().fileResourcepacks = GameConfiguration.gameConfiguration.folderInfo.resourcePacksDir;
        Shaders.shaderPacksDir = new File(Minecraft.getInstance().gameDir, "shaderpacks");
        ((ClientConfig)Load.getInstance().getHooks().getConfigManagers().findClass(ClientConfig.class)).fastSave();
    }

    public static boolean isConnectedToServer(String ip) {
        return mc.getCurrentServerData() != null && ClientManagers.mc.getCurrentServerData().serverIP != null && ClientManagers.mc.getCurrentServerData().serverIP.contains(ip);
    }

    public static boolean isHolyWorld() {
        return ClientManagers.isConnectedToServer("holyworld");
    }

    public static boolean isReallyWorld() {
        return ClientManagers.isConnectedToServer("reallyworld");
    }

    public static boolean isFuntime() {
        return ClientManagers.isConnectedToServer("funtime");
    }

    public static boolean cake() {
        return ClientManagers.isConnectedToServer("cakeworld");
    }

    @Generated
    private ClientManagers() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    @Generated
    public static RandomNumberUtils getRandomNumber() {
        return randomNumber;
    }

    @Generated
    public static String getRandom() {
        return random;
    }

    @Generated
    public static void setRandom(String random) {
        ClientManagers.random = random;
    }

    @Generated
    public static void setUnHook(boolean unHook) {
        ClientManagers.unHook = unHook;
    }

    @Generated
    public static boolean isUnHook() {
        return unHook;
    }

    @Generated
    public static String getLanguage() {
        return language;
    }

    @Generated
    public static void setLanguage(String language) {
        ClientManagers.language = language;
    }
}

