/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.itemics.command;

import fun.kubik.itemics.api.IItemics;
import fun.kubik.itemics.api.ItemicsAPI;
import fun.kubik.itemics.api.Settings;
import fun.kubik.itemics.api.command.IItemicsChatControl;
import fun.kubik.itemics.api.command.argument.ICommandArgument;
import fun.kubik.itemics.api.command.exception.CommandNotEnoughArgumentsException;
import fun.kubik.itemics.api.command.exception.CommandNotFoundException;
import fun.kubik.itemics.api.command.helpers.TabCompleteHelper;
import fun.kubik.itemics.api.command.manager.ICommandManager;
import fun.kubik.itemics.api.event.events.ChatEvent;
import fun.kubik.itemics.api.event.events.TabCompleteEvent;
import fun.kubik.itemics.api.event.listener.AbstractGameEventListener;
import fun.kubik.itemics.api.utils.Helper;
import fun.kubik.itemics.api.utils.SettingsUtil;
import fun.kubik.itemics.command.argument.ArgConsumer;
import fun.kubik.itemics.command.argument.CommandArguments;
import fun.kubik.itemics.command.manager.CommandManager;
import fun.kubik.itemics.utils.accessor.IGuiScreen;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;
import net.minecraft.util.Tuple;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

public class ExampleItemicsControl
implements Helper,
AbstractGameEventListener {
    private static final Settings settings = ItemicsAPI.getSettings();
    private final ICommandManager manager;

    public ExampleItemicsControl(IItemics itemics) {
        this.manager = itemics.getCommandManager();
        itemics.getGameEventHandler().registerEventListener(this);
    }

    @Override
    public void onSendChatMessage(ChatEvent event) {
        String msg = event.getMessage();
        String prefix = (String)ExampleItemicsControl.settings.prefix.value;
        boolean forceRun = msg.startsWith(IItemicsChatControl.FORCE_COMMAND_PREFIX);
        if (((Boolean)ExampleItemicsControl.settings.prefixControl.value).booleanValue() && msg.startsWith(prefix) || forceRun) {
            event.cancel();
            String commandStr = msg.substring(forceRun ? IItemicsChatControl.FORCE_COMMAND_PREFIX.length() : prefix.length());
            if (!this.runCommand(commandStr) && !commandStr.trim().isEmpty()) {
                new CommandNotFoundException(CommandManager.expand(commandStr).getA()).handle(null, null);
            }
        } else if ((((Boolean)ExampleItemicsControl.settings.chatControl.value).booleanValue() || ((Boolean)ExampleItemicsControl.settings.chatControlAnyway.value).booleanValue()) && this.runCommand(msg)) {
            event.cancel();
        }
    }

    private void logRanCommand(String command, String rest) {
        if (((Boolean)ExampleItemicsControl.settings.echoCommands.value).booleanValue()) {
            String msg = command + rest;
            String toDisplay = (Boolean)ExampleItemicsControl.settings.censorRanCommands.value != false ? command + " ..." : msg;
            StringTextComponent component = new StringTextComponent(String.format("> %s", toDisplay));
            component.setStyle(component.getStyle().setFormatting(TextFormatting.WHITE).setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new StringTextComponent("Click to rerun command"))).setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, IItemicsChatControl.FORCE_COMMAND_PREFIX + msg)));
            this.logDirect(component);
        }
    }

    public boolean runCommand(String msg) {
        if (msg.trim().equalsIgnoreCase("damn")) {
            this.logDirect("daniel");
            return false;
        }
        if (msg.trim().equalsIgnoreCase("orderpizza")) {
            try {
                ((IGuiScreen)((Object)ExampleItemicsControl.mc.currentScreen)).openLinkInvoker(new URI("https://www.dominos.com/en/pages/order/"));
            } catch (NullPointerException | URISyntaxException exception) {
                // empty catch block
            }
            return false;
        }
        if (msg.isEmpty()) {
            return this.runCommand("help");
        }
        Tuple<String, List<ICommandArgument>> pair = CommandManager.expand(msg);
        String command = pair.getA();
        String rest = msg.substring(pair.getA().length());
        ArgConsumer argc = new ArgConsumer(this.manager, pair.getB());
        if (!argc.hasAny()) {
            Settings.Setting<?> setting = ExampleItemicsControl.settings.byLowerName.get(command.toLowerCase(Locale.US));
            if (setting != null) {
                this.logRanCommand(command, rest);
                if (setting.getValueClass() == Boolean.class) {
                    this.manager.execute(String.format("set toggle %s", setting.getName()));
                } else {
                    this.manager.execute(String.format("set %s", setting.getName()));
                }
                return true;
            }
        } else if (argc.hasExactlyOne()) {
            for (Settings.Setting<?> setting : ExampleItemicsControl.settings.allSettings) {
                if (SettingsUtil.javaOnlySetting(setting) || !setting.getName().equalsIgnoreCase(pair.getA())) continue;
                this.logRanCommand(command, rest);
                try {
                    this.manager.execute(String.format("set %s %s", setting.getName(), argc.getString()));
                } catch (CommandNotEnoughArgumentsException commandNotEnoughArgumentsException) {
                    // empty catch block
                }
                return true;
            }
        }
        if (this.manager.getCommand(pair.getA()) != null) {
            this.logRanCommand(command, rest);
        }
        return this.manager.execute(pair);
    }

    @Override
    public void onPreTabComplete(TabCompleteEvent event) {
        if (!((Boolean)ExampleItemicsControl.settings.prefixControl.value).booleanValue()) {
            return;
        }
        String prefix = event.prefix;
        String commandPrefix = (String)ExampleItemicsControl.settings.prefix.value;
        if (!prefix.startsWith(commandPrefix)) {
            return;
        }
        String msg = prefix.substring(commandPrefix.length());
        List<ICommandArgument> args = CommandArguments.from(msg, true);
        Stream<String> stream = this.tabComplete(msg);
        if (args.size() == 1) {
            stream = stream.map(x -> commandPrefix + x);
        }
        event.completions = (String[])stream.toArray(String[]::new);
    }

    public Stream<String> tabComplete(String msg) {
        try {
            List<ICommandArgument> args = CommandArguments.from(msg, true);
            ArgConsumer argc = new ArgConsumer(this.manager, args);
            if (argc.hasAtMost(2)) {
                if (argc.hasExactly(1)) {
                    return new TabCompleteHelper().addCommands(this.manager).addSettings().filterPrefix(argc.getString()).stream();
                }
                Settings.Setting<?> setting = ExampleItemicsControl.settings.byLowerName.get(argc.getString().toLowerCase(Locale.US));
                if (setting != null && !SettingsUtil.javaOnlySetting(setting)) {
                    if (setting.getValueClass() == Boolean.class) {
                        TabCompleteHelper helper = new TabCompleteHelper();
                        if (((Boolean)setting.value).booleanValue()) {
                            helper.append("true", "false");
                        } else {
                            helper.append("false", "true");
                        }
                        return helper.filterPrefix(argc.getString()).stream();
                    }
                    return Stream.of(SettingsUtil.settingValueToString(setting));
                }
            }
            return this.manager.tabComplete(msg);
        } catch (CommandNotEnoughArgumentsException ignored) {
            return Stream.empty();
        }
    }
}

