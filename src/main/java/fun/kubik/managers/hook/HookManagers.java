/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.managers.hook;

import fun.kubik.managers.blockesp.BlockESPManagers;
import fun.kubik.managers.config.ConfigManagers;
import fun.kubik.managers.draggable.DraggableManagers;
import fun.kubik.managers.draggable.api.Component;
import fun.kubik.managers.draggable.main.Controller;
import fun.kubik.managers.friend.FriendManagers;
import fun.kubik.managers.hook.main.CommandManagers;
import fun.kubik.managers.hook.main.ModuleManagers;
import fun.kubik.managers.macro.MacroManagers;
import fun.kubik.managers.notification.NotificationManagers;
import fun.kubik.managers.staff.StaffManagers;
import fun.kubik.managers.theme.main.ThemeManagers;
import lombok.Generated;

public class HookManagers {
    private final ModuleManagers moduleManagers = new ModuleManagers();
    private final ThemeManagers themeManagers = new ThemeManagers();
    private final NotificationManagers notificationManagers = new NotificationManagers();
    private final FriendManagers friendManagers = new FriendManagers();
    private final StaffManagers staffManagers = new StaffManagers();
    private final MacroManagers macroManagers = new MacroManagers();
    private final ConfigManagers configManagers = new ConfigManagers();
    private final CommandManagers commandManagers = new CommandManagers();
    private final DraggableManagers draggableManagers = new DraggableManagers();
    private final Controller draggableController = new Controller();
    private final BlockESPManagers blockESPManagers = new BlockESPManagers();

    public HookManagers() {
        for (Component component : this.draggableManagers) {
            component.initSettings();
        }
        System.out.println("1");
    }

    @Generated
    public ModuleManagers getModuleManagers() {
        return this.moduleManagers;
    }

    @Generated
    public ThemeManagers getThemeManagers() {
        return this.themeManagers;
    }

    @Generated
    public NotificationManagers getNotificationManagers() {
        return this.notificationManagers;
    }

    @Generated
    public FriendManagers getFriendManagers() {
        return this.friendManagers;
    }

    @Generated
    public StaffManagers getStaffManagers() {
        return this.staffManagers;
    }

    @Generated
    public MacroManagers getMacroManagers() {
        return this.macroManagers;
    }

    @Generated
    public ConfigManagers getConfigManagers() {
        return this.configManagers;
    }

    @Generated
    public CommandManagers getCommandManagers() {
        return this.commandManagers;
    }

    @Generated
    public DraggableManagers getDraggableManagers() {
        return this.draggableManagers;
    }

    @Generated
    public Controller getDraggableController() {
        return this.draggableController;
    }

    @Generated
    public BlockESPManagers getBlockESPManagers() {
        return this.blockESPManagers;
    }
}

