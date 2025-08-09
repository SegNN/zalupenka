/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.itemics.command.defaults;

import fun.kubik.itemics.api.IItemics;
import fun.kubik.itemics.api.command.Command;
import fun.kubik.itemics.api.command.ICommand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class DefaultCommands {
    private DefaultCommands() {
    }

    public static List<ICommand> createAll(IItemics itemics) {
        Objects.requireNonNull(itemics);
        ArrayList<Command> commands = new ArrayList<Command>(Arrays.asList(new HelpCommand(itemics), new SetCommand(itemics), new CommandAlias(itemics, Arrays.asList("modified", "mod", "itemics", "modifiedsettings"), "List modified settings", "set modified"), new CommandAlias(itemics, "reset", "Reset all settings or just one", "set reset"), new GoalCommand(itemics), new GotoCommand(itemics), new PathCommand(itemics), new ProcCommand(itemics), new ETACommand(itemics), new VersionCommand(itemics), new RepackCommand(itemics), new BuildCommand(itemics), new LitematicaCommand(itemics), new ComeCommand(itemics), new AxisCommand(itemics), new ForceCancelCommand(itemics), new GcCommand(itemics), new InvertCommand(itemics), new TunnelCommand(itemics), new RenderCommand(itemics), new FarmCommand(itemics), new FollowCommand(itemics), new ExploreFilterCommand(itemics), new ReloadAllCommand(itemics), new SaveAllCommand(itemics), new ExploreCommand(itemics), new BlacklistCommand(itemics), new FindCommand(itemics), new MineCommand(itemics), new ClickCommand(itemics), new SurfaceCommand(itemics), new ThisWayCommand(itemics), new WaypointsCommand(itemics), new CommandAlias(itemics, "sethome", "Sets your home waypoint", "waypoints save home"), new CommandAlias(itemics, "home", "Path to your home waypoint", "waypoints goto home"), new SelCommand(itemics)));
        ExecutionControlCommands prc = new ExecutionControlCommands(itemics);
        commands.add(prc.pauseCommand);
        commands.add(prc.resumeCommand);
        commands.add(prc.pausedCommand);
        commands.add(prc.cancelCommand);
        return Collections.unmodifiableList(commands);
    }
}

