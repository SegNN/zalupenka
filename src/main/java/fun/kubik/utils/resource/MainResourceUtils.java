/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.utils.resource;

import fun.kubik.helpers.interfaces.IFastAccess;
import net.minecraft.resources.SimpleReloadableResourceManager;
import net.minecraft.util.ResourceLocation;

public class MainResourceUtils
extends ResourceLocation
implements IFastAccess {
    private static final String NAMESPACE = "main";

    public MainResourceUtils(String pathIn) {
        super(NAMESPACE, pathIn);
    }

    public static void registerResources() {
        SimpleReloadableResourceManager resourceManager = (SimpleReloadableResourceManager)mc.getResourceManager();
        MainPack customResourcePack = new MainPack(NAMESPACE);
        MainPack customResourcePack2 = new MainPack("cpm");
        resourceManager.addResourcePack(customResourcePack);
        resourceManager.addResourcePack(customResourcePack2);
    }
}

