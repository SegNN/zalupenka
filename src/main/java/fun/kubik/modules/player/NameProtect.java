/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.modules.player;

import fun.kubik.events.api.EventHook;
import fun.kubik.events.main.EventUpdate;
import fun.kubik.managers.module.Module;
import fun.kubik.managers.module.main.Category;
import fun.kubik.managers.module.option.main.CheckboxOption;
import fun.kubik.managers.module.option.main.StringOption;
import lombok.Generated;
import net.minecraft.client.Minecraft;

public class NameProtect
extends Module {
    private final CheckboxOption friends = new CheckboxOption("Friends", true);
    private final StringOption playerName = new StringOption("Name", "");

    public NameProtect() {
        super("NameProtect", Category.PLAYER);
        this.settings(this.playerName, this.friends);
    }

    @EventHook
    public void update(EventUpdate event) {
    }

    public String patch(String text) {
        String out = text;
        if (this.isToggled()) {
            out = text.replaceAll(Minecraft.getInstance().session.getUsername(), (String)this.playerName.getValue());
        }
        return out;
    }

    @Generated
    public CheckboxOption getFriends() {
        return this.friends;
    }

    @Generated
    public StringOption getPlayerName() {
        return this.playerName;
    }
}

