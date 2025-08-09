package fun.kubik.managers.hook.main;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import fun.kubik.Load;
import fun.kubik.helpers.interfaces.IFinderModules;
import fun.kubik.helpers.interfaces.IManager;
import fun.kubik.managers.client.ClientManagers;
import fun.kubik.managers.command.api.Command;
import fun.kubik.managers.command.main.*;
//import fun.kubik.modules.misc.IRC;
import fun.kubik.managers.command.main.*;
import fun.kubik.utils.client.ChatUtils;
import java.util.ArrayList;
import lombok.Generated;
import net.minecraft.command.ISuggestionProvider;

public class CommandManagers
        extends ArrayList<Command>
        implements IManager<Command>,
        IFinderModules<Command> {
    private boolean message;
    private final CommandDispatcher<ISuggestionProvider> commandDispatcher = new CommandDispatcher();

    public CommandManagers() {
        this.init();
    }

    @Override
    public void init() {
        this.register(new ConfigCommand());
        this.register(new FriendCommand());
        this.register(new VClipCommand());
        this.register(new TeleportCommand());
        this.register(new MacroCommand());
//        this.register(new IRCCommand());
        this.register(new GpsCommand());
        this.register(new StaffCommand());
        this.register(new AutoRegion());
        this.register(new HelpCommand());
//        this.register(new AdminCommands());
        this.register(new BindCommand());
        this.register(new BlockESPCommand());
    }

//    public void registerAdminCommands() {
//        if (Load.getInstance() == null) {
//            ChatUtils.addClientMessage("§cОшибка: Load instance не инициализирован, AdminCommands не зарегистрированы");
//            return;
//        }
//        IRC ircModule = (IRC) Load.getInstance().getHooks().getModuleManagers().findClass(IRC.class);
//        if (ircModule != null && ircModule.getIRCClient() != null && ircModule.getIrcBot() != null) {
//            Command existingAdminCommand = findClass(AdminCommands.class);
//            if (existingAdminCommand == null) {
//                this.register(new AdminCommands(ircModule.getIrcBot()));
//                ChatUtils.addClientMessage("§aAdminCommands успешно зарегистрированы");
//            }
//        } else {
//            ChatUtils.addClientMessage("§cПредупреждение: IRC модуль не доступен, AdminCommands не зарегистрированы");
//        }
//    }

    @Override
    public <T extends Command> T findName(String name) {
        return (T)((Command)this.stream().filter(command -> command.getName().equals(name)).findAny().orElse(null));
    }

    @Override
    public <T extends Command> T findClass(Class<T> clazz) {
        return (T)((Command)this.stream().filter(command -> command.getClass() == clazz).findAny().orElse(null));
    }

    @Override
    public void register(Command command) {
        this.add(command);
    }

    public void run(String message) {
        if (ClientManagers.isUnHook()) {
            this.setMessage(false);
            return;
        }
        if (message.startsWith(".")) {
            if (Load.getInstance() == null) {
                ChatUtils.addClientMessage("§cОшибка: Load не инициализирован, команды недоступны");
                this.setMessage(true);
                return;
            }
            for (Command command : Load.getInstance().getHooks().getCommandManagers()) {
                for (String name : command.getName()) {
                    if (!message.startsWith("." + name)) continue;
                    try {
                        command.run(message.split(" "));
                    } catch (Exception ex) {
                        command.error();
                        ex.printStackTrace();
                    }
                    this.setMessage(true);
                    return;
                }
            }
            ChatUtils.addClientMessage("Данной команды не существует!");
            this.setMessage(true);
        } else {
            this.setMessage(false);
        }
    }

    public void registerRun(String message) {
        if (message.startsWith(".")) {
            if ((message = message.substring(1)).isEmpty()) {
                return;
            }
            try {
                this.commandDispatcher.execute(message, null);
            } catch (CommandSyntaxException commandSyntaxException) {
                // empty catch block
            }
        }
    }

    @Generated
    public void setMessage(boolean message) {
        this.message = message;
    }

    @Generated
    public boolean isMessage() {
        return this.message;
    }

    @Generated
    public CommandDispatcher<ISuggestionProvider> getCommandDispatcher() {
        return this.commandDispatcher;
    }
}