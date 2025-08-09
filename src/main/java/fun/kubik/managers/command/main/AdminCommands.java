//package fun.kubik.managers.command.main;
//
//import com.mojang.brigadier.builder.LiteralArgumentBuilder;
//import fun.kubik.Load;
//import fun.kubik.managers.command.api.Command;
//import fun.kubik.modules.misc.IRC;
//import fun.kubik.utils.client.ChatUtils;
//import net.minecraft.command.ISuggestionProvider;
//import net.minecraft.client.Minecraft;
//import org.pircbotx.PircBotX;
//import protectguard.guard.api.Data;
//import protectguard.irc.model.IRCUser;
//import protectguard.irc.model.UserGroup;
//
//public class AdminCommands extends Command {
//    private static final String CHANNEL = "#birkkubikwhatthefuck";
//
//    public AdminCommands() {
//        super("IRC Admin", "ircadmin");
//    }
//
//    @Override
//    public void build(LiteralArgumentBuilder<ISuggestionProvider> builder) {
//        // Empty implementation, as Brigadier is not required for this command
//    }
//
//    @Override
//    public void run(String[] args) {
//        IRC irc = (IRC) Load.getInstance().getHooks().getModuleManagers().findClass(IRC.class);
//        if (irc == null || !irc.isToggled()) {
//            ChatUtils.addClientMessage("§cВключите модуль IRC!");
//            return;
//        }
//
//        PircBotX ircBot = irc.getIrcBot();
//        if (ircBot == null || !ircBot.isConnected()) {
//            ChatUtils.addClientMessage("§cIRC бот не подключен!");
//            return;
//        }
//
//        if (args.length <= 1) {
//            error();
//            return;
//        }
//
//        String currentUser = Data.getUsername() != null ? Data.getUsername() : getMinecraftUsername();
//        String role = Data.getRole() != null ? Data.getRole().toUpperCase() : "DEFAULT";
//        System.out.println("AdminCommands: currentUser=" + currentUser + ", role=" + role);
//        IRCUser executor = new IRCUser(currentUser, Data.getUID(), getUserGroup(role));
//        if (!executor.getGroup().isAdmin()) {
//            ChatUtils.addClientMessage("§cОшибка: У вас нет прав для выполнения этой команды");
//            return;
//        }
//
//        String subCommand = args[1].toLowerCase();
//        if (subCommand.equals("mute")) {
//            if (args.length < 4) {
//                error();
//                return;
//            }
//            try {
//                String username = args[2];
//                int minutes = Integer.parseInt(args[3]);
//                ircBot.sendRaw().rawLineNow("MODE " + CHANNEL + " +q " + username);
//                String formattedMessage = "[" + role + "] " + currentUser + ": User " + username + " has been muted for " + minutes + " minutes";
//                irc.sendIRCMessage(formattedMessage);
//                ChatUtils.addClientMessage("§aПользователь " + username + " замьючен на " + minutes + " минут");
//            } catch (NumberFormatException e) {
//                ChatUtils.addClientMessage("§cОшибка: Количество минут должно быть числом");
//            }
//        } else if (subCommand.equals("ban")) {
//            if (args.length < 4) {
//                error();
//                return;
//            }
//            String username = args[2];
//            String reason = String.join(" ", java.util.Arrays.copyOfRange(args, 3, args.length));
//            ircBot.sendRaw().rawLineNow("MODE " + CHANNEL + " +b " + username);
//            ircBot.sendRaw().rawLineNow("KICK " + CHANNEL + " " + username + " :" + reason);
//            String formattedMessage = "[" + role + "] " + currentUser + ": User " + username + " has been banned: " + reason;
//            irc.sendIRCMessage(formattedMessage);
//            ChatUtils.addClientMessage("§aПользователь " + username + " забанен по причине: " + reason);
//        } else {
//            error();
//        }
//    }
//
//    private String getMinecraftUsername() {
//        return Minecraft.getInstance().player != null
//                ? Minecraft.getInstance().player.getName().getString()
//                : "Unknown";
//    }
//
//    private UserGroup getUserGroup(String role) {
//        return switch (role.toLowerCase()) {
//            case "admin" -> UserGroup.ADMIN;
//            case "moderator" -> UserGroup.MODERATOR;
//            case "reseller" -> UserGroup.RESELLER;
//            case "guest", "user" -> UserGroup.DEFAULT;
//            default -> UserGroup.DEFAULT;
//        };
//    }
//
//    @Override
//    public void error() {
//        ChatUtils.addClientMessage("§cИспользование: .ircadmin <mute|ban> <user> [duration|reason]");
//    }
//}