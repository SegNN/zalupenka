package fun.kubik.itemics.command.defaults;

import fun.kubik.itemics.Itemics;
import fun.kubik.itemics.api.IItemics;
import fun.kubik.itemics.api.cache.IWaypoint;
import fun.kubik.itemics.api.cache.IWorldData;
import fun.kubik.itemics.api.cache.Waypoint;
import fun.kubik.itemics.api.command.Command;
import fun.kubik.itemics.api.command.IItemicsChatControl;
import fun.kubik.itemics.api.command.argument.IArgConsumer;
import fun.kubik.itemics.api.command.datatypes.ForWaypoints;
import fun.kubik.itemics.api.command.datatypes.RelativeBlockPos;
import fun.kubik.itemics.api.command.exception.CommandException;
import fun.kubik.itemics.api.command.exception.CommandInvalidStateException;
import fun.kubik.itemics.api.command.exception.CommandInvalidTypeException;
import fun.kubik.itemics.api.command.helpers.Paginator;
import fun.kubik.itemics.api.command.helpers.TabCompleteHelper;
import fun.kubik.itemics.api.pathing.goals.GoalBlock;
import fun.kubik.itemics.api.utils.BetterBlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WaypointsCommand extends Command {
    private final Map<IWorldData, List<IWaypoint>> deletedWaypoints = new HashMap<>();

    public WaypointsCommand(IItemics itemics) {
        super(itemics, "waypoints", "waypoint", "wp");
    }

    @Override
    public void execute(String label, IArgConsumer args) throws CommandException {
        Action action = args.hasAny() ? Action.getByName(args.getString()) : Action.LIST;
        if (action == null) {
            throw new CommandInvalidTypeException(args.consumed(), "an action");
        }

        BiFunction<IWaypoint, Action, ITextComponent> toComponent = (waypoint, act) -> {
            StringTextComponent component = new StringTextComponent("");
            StringTextComponent tagComponent = new StringTextComponent(waypoint.getTag().name() + " ");
            tagComponent.setStyle(tagComponent.getStyle().setFormatting(TextFormatting.GRAY));
            String name = waypoint.getName();
            StringTextComponent nameComponent = new StringTextComponent(!name.isEmpty() ? name : "<empty>");
            nameComponent.setStyle(nameComponent.getStyle().setFormatting(!name.isEmpty() ? TextFormatting.GRAY : TextFormatting.DARK_GRAY));
            StringTextComponent timestamp = new StringTextComponent(" @ " + new Date(waypoint.getCreationTimestamp()));
            timestamp.setStyle(timestamp.getStyle().setFormatting(TextFormatting.DARK_GRAY));
            component.append(tagComponent).append(nameComponent).append(timestamp);
            component.setStyle(component.getStyle()
                    .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new StringTextComponent("Click to select")))
                    .setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                            String.format("%s%s %s %s @ %d", IItemicsChatControl.FORCE_COMMAND_PREFIX, label, act.names[0], waypoint.getTag().getName(), waypoint.getCreationTimestamp()))));
            return component;
        };

        Function<IWaypoint, ITextComponent> transform = waypoint -> toComponent.apply(waypoint, action == Action.LIST ? Action.INFO : action);

        if (action == Action.LIST) {
            IWaypoint.Tag tag = args.hasAny() ? IWaypoint.Tag.getByName(args.peekString()) : null;
            if (tag != null) {
                args.get();
            }
            IWaypoint[] waypoints = tag != null ? ForWaypoints.getWaypointsByTag(this.itemics, tag) : ForWaypoints.getWaypoints(this.itemics);
            if (waypoints.length > 0) {
                args.requireMax(1);
                Paginator.paginate(args, waypoints, () -> this.logDirect(tag != null ? String.format("All waypoints by tag %s:", tag.name()) : "All waypoints:"), transform,
                        String.format("%s%s %s%s", IItemicsChatControl.FORCE_COMMAND_PREFIX, label, action.names[0], tag != null ? " " + tag.getName() : ""));
            } else {
                args.requireMax(0);
                throw new CommandInvalidStateException(tag != null ? "No waypoints found by that tag" : "No waypoints found");
            }
        } else if (action == Action.SAVE) {
            IWaypoint.Tag tag = args.hasAny() ? IWaypoint.Tag.getByName(args.peekString()) : null;
            if (tag == null) {
                tag = IWaypoint.Tag.USER;
            } else {
                args.get();
            }
            String name = args.hasExactlyOne() || args.hasExactly(4) ? args.getString() : "";
            BetterBlockPos pos = args.hasAny() ? args.getDatatypePost(RelativeBlockPos.INSTANCE, this.ctx.playerFeet()) : this.ctx.playerFeet();
            args.requireMax(0);
            IWaypoint waypoint = new Waypoint(name, tag, pos);
            ForWaypoints.waypoints(this.itemics).addWaypoint(waypoint);
            StringTextComponent component = new StringTextComponent("Waypoint added: ");
            component.setStyle(component.getStyle().setFormatting(TextFormatting.GRAY));
            component.append(toComponent.apply(waypoint, Action.INFO));
            this.logDirect(new ITextComponent[]{component});
        } else if (action == Action.CLEAR) {
            args.requireMax(1);
            IWaypoint.Tag tag = IWaypoint.Tag.getByName(args.getString());
            IWaypoint[] waypoints = ForWaypoints.getWaypointsByTag(this.itemics, tag);
            for (IWaypoint waypoint : waypoints) {
                ForWaypoints.waypoints(this.itemics).removeWaypoint(waypoint);
            }
            deletedWaypoints.computeIfAbsent(this.itemics.getWorldProvider().getCurrentWorld(), k -> new ArrayList<>()).addAll(Arrays.asList(waypoints));
            StringTextComponent textComponent = new StringTextComponent(String.format("Cleared %d waypoints, click to restore them", waypoints.length));
            textComponent.setStyle(textComponent.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                    String.format("%s%s restore @ %s", IItemicsChatControl.FORCE_COMMAND_PREFIX, label, Stream.of(waypoints)
                            .map(IWaypoint::getCreationTimestamp)
                            .map(String::valueOf)
                            .collect(Collectors.joining(" "))))));
            this.logDirect(new ITextComponent[]{textComponent});
        } else if (action == Action.RESTORE) {
            List<IWaypoint> waypoints = new ArrayList<>();
            List<IWaypoint> deleted = this.deletedWaypoints.getOrDefault(this.itemics.getWorldProvider().getCurrentWorld(), Collections.emptyList());
            if (args.peekString().equals("@")) {
                args.get();
                while (args.hasAny()) {
                    Long timestamp = args.getAs(Long.class);
                    deleted.stream()
                            .filter(waypoint -> waypoint.getCreationTimestamp() == timestamp)
                            .forEach(waypoints::add);
                }
            } else {
                args.requireExactly(1);
                int size = deleted.size();
                int amount = Math.min(size, args.getAs(Integer.class));
                waypoints = new ArrayList<>(deleted.subList(size - amount, size));
            }
            waypoints.forEach(ForWaypoints.waypoints(this.itemics)::addWaypoint);
            deleted.removeAll(waypoints);
            this.logDirect(String.format("Restored %d waypoints", waypoints.size()));
        } else {
            IWaypoint[] waypoints = args.getDatatypeFor(ForWaypoints.INSTANCE);
            IWaypoint waypoint = null;
            if (args.hasAny() && args.peekString().equals("@")) {
                args.requireExactly(2);
                args.get();
                Long timestamp = args.getAs(Long.class);
                waypoint = Arrays.stream(waypoints)
                        .filter(wp -> wp.getCreationTimestamp() == timestamp)
                        .findFirst()
                        .orElseThrow(() -> new CommandInvalidStateException("Timestamp was specified but no waypoint was found"));
            } else {
                if (waypoints.length == 0) {
                    throw new CommandInvalidStateException("No waypoints found");
                } else if (waypoints.length == 1) {
                    waypoint = waypoints[0];
                }
            }
            if (waypoint == null) {
                args.requireMax(1);
                Paginator.paginate(args, waypoints, () -> this.logDirect("Multiple waypoints were found:"), transform,
                        String.format("%s%s %s %s", IItemicsChatControl.FORCE_COMMAND_PREFIX, label, action.names[0], args.consumedString()));
            } else if (action == Action.INFO) {
                this.logDirect(new ITextComponent[]{transform.apply(waypoint)});
                this.logDirect(String.format("Position: %s", waypoint.getLocation()));
                StringTextComponent deleteComponent = new StringTextComponent("Click to delete this waypoint");
                deleteComponent.setStyle(deleteComponent.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                        String.format("%s%s delete %s @ %d", IItemicsChatControl.FORCE_COMMAND_PREFIX, label, waypoint.getTag().getName(), waypoint.getCreationTimestamp()))));
                StringTextComponent goalComponent = new StringTextComponent("Click to set goal to this waypoint");
                goalComponent.setStyle(goalComponent.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                        String.format("%s%s goal %s @ %d", IItemicsChatControl.FORCE_COMMAND_PREFIX, label, waypoint.getTag().getName(), waypoint.getCreationTimestamp()))));
                StringTextComponent recreateComponent = new StringTextComponent("Click to show a command to recreate this waypoint");
                recreateComponent.setStyle(recreateComponent.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,
                        String.format("%s%s save %s %s %s %s %s", Itemics.settings().prefix.value, label, waypoint.getTag().getName(), waypoint.getName(),
                                waypoint.getLocation().x, waypoint.getLocation().y, waypoint.getLocation().z))));
                StringTextComponent backComponent = new StringTextComponent("Click to return to the waypoints list");
                backComponent.setStyle(backComponent.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                        String.format("%s%s list", IItemicsChatControl.FORCE_COMMAND_PREFIX, label))));
                this.logDirect(new ITextComponent[]{deleteComponent, goalComponent, recreateComponent, backComponent});
            } else if (action == Action.DELETE) {
                ForWaypoints.waypoints(this.itemics).removeWaypoint(waypoint);
                deletedWaypoints.computeIfAbsent(this.itemics.getWorldProvider().getCurrentWorld(), k -> new ArrayList<>()).add(waypoint);
                StringTextComponent textComponent = new StringTextComponent("That waypoint has successfully been deleted, click to restore it");
                textComponent.setStyle(textComponent.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                        String.format("%s%s restore @ %s", IItemicsChatControl.FORCE_COMMAND_PREFIX, label, waypoint.getCreationTimestamp()))));
                this.logDirect(new ITextComponent[]{textComponent});
            } else if (action == Action.GOAL) {
                GoalBlock goal = new GoalBlock(waypoint.getLocation());
                this.itemics.getCustomGoalProcess().setGoal(goal);
                this.logDirect(String.format("Goal: %s", goal));
            } else if (action == Action.GOTO) {
                GoalBlock goal = new GoalBlock(waypoint.getLocation());
                this.itemics.getCustomGoalProcess().setGoalAndPath(goal);
                this.logDirect(String.format("Going to: %s", goal));
            }
        }
    }

    @Override
    public Stream<String> tabComplete(String label, IArgConsumer args) throws CommandException {
        if (!args.hasAny()) {
            return Stream.empty();
        }
        if (args.hasExactlyOne()) {
            return new TabCompleteHelper()
                    .append(Action.getAllNames())
                    .sortAlphabetically()
                    .filterPrefix(args.getString())
                    .stream();
        }
        Action action = Action.getByName(args.getString());
        if (args.hasExactlyOne()) {
            if (action == Action.LIST || action == Action.SAVE || action == Action.CLEAR) {
                return new TabCompleteHelper()
                        .append(IWaypoint.Tag.getAllNames())
                        .sortAlphabetically()
                        .filterPrefix(args.getString())
                        .stream();
            }
            if (action == Action.RESTORE) {
                return Stream.empty();
            }
            return args.tabCompleteDatatype(ForWaypoints.INSTANCE);
        }
        if (args.has(3) && action == Action.SAVE) {
            args.get();
            args.get();
            return args.tabCompleteDatatype(RelativeBlockPos.INSTANCE);
        }
        return Stream.empty();
    }

    @Override
    public String getShortDesc() {
        return "Manage waypoints";
    }

    @Override
    public List<String> getLongDesc() {
        return Arrays.asList(
                "The waypoint command allows you to manage Itemics's waypoints.",
                "",
                "Waypoints can be used to mark positions for later. Waypoints are each given a tag and an optional name.",
                "",
                "Note that the info, delete, and goal commands let you specify a waypoint by tag. If there is more than one waypoint with a certain tag, then they will let you select which waypoint you mean.",
                "",
                "Missing arguments for the save command use the USER tag, creating an unnamed waypoint and your current position as defaults.",
                "",
                "Usage:",
                "> wp [l/list] - List all waypoints.",
                "> wp <l/list> <tag> - List all waypoints by tag.",
                "> wp <s/save> - Save an unnamed USER waypoint at your current position",
                "> wp <s/save> [tag] [name] [pos] - Save a waypoint with the specified tag, name and position.",
                "> wp <i/info/show> <tag/name> - Show info on a waypoint by tag or name.",
                "> wp <d/delete> <tag/name> - Delete a waypoint by tag or name.",
                "> wp <restore> <n> - Restore the last n deleted waypoints.",
                "> wp <c/clear> <tag> - Delete all waypoints with the specified tag.",
                "> wp <g/goal> <tag/name> - Set a goal to a waypoint by tag or name.",
                "> wp <goto> <tag/name> - Set a goal to a waypoint by tag or name and start pathing.");
    }

    private enum Action {
        LIST("list", "get", "l"),
        CLEAR("clear", "c"),
        SAVE("save", "s"),
        INFO("info", "show", "i"),
        DELETE("delete", "d"),
        RESTORE("restore"),
        GOAL("goal", "g"),
        GOTO("goto");

        private final String[] names;

        Action(String... names) {
            this.names = names;
        }

        public static Action getByName(String name) {
            for (Action action : values()) {
                for (String alias : action.names) {
                    if (alias.equalsIgnoreCase(name)) {
                        return action;
                    }
                }
            }
            return null;
        }

        public static String[] getAllNames() {
            return Arrays.stream(values())
                    .flatMap(action -> Arrays.stream(action.names))
                    .distinct()
                    .toArray(String[]::new);
        }
    }
}