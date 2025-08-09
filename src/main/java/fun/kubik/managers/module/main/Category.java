/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.managers.module.main;

import lombok.Generated;
import net.minecraft.util.ResourceLocation;

public enum Category {
    COMBAT("Combat", new ResourceLocation("main/textures/images/combat.png")),
    MOVEMENT("Movement", new ResourceLocation("main/textures/images/movement.png")),
    RENDER("Render", new ResourceLocation("main/textures/images/render.png")),
    PLAYER("Player", new ResourceLocation("main/textures/images/player.png")),
    MISC("Misc", new ResourceLocation("main/textures/images/other.png"));

    private final String name;
    private final ResourceLocation path;

    private Category(String name, ResourceLocation path) {
        this.name = name;
        this.path = path;
    }

    @Generated
    public String getName() {
        return this.name;
    }

    @Generated
    public ResourceLocation getPath() {
        return this.path;
    }
}

