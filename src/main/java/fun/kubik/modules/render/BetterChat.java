package fun.kubik.modules.render;

import fun.kubik.events.api.EventHook;
import fun.kubik.events.main.packet.EventReceivePacket;
import fun.kubik.managers.module.Module;
import fun.kubik.managers.module.main.Category;
import fun.kubik.managers.module.option.main.CheckboxOption;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.gui.NewChatGui;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.server.SChatPacket;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.TextComponent;

public class BetterChat
        extends Module {
    public static CheckboxOption antiSpam = new CheckboxOption("хуйня", false);
    public static CheckboxOption history = new CheckboxOption("хуйня", false);
    private final Map<String, Integer> messageCounts = new HashMap<String, Integer>();
    private final Map<String, Integer> messageLines = new HashMap<String, Integer>();
    private int line;

    public BetterChat() {
        super("BetterChat", Category.RENDER);
        this.settings(antiSpam, history);
    }

    @EventHook
    public void receive(EventReceivePacket event) {
        SChatPacket sPacketChat;
        IPacket<?> packet;
        if (((Boolean) antiSpam.getValue()).booleanValue()
                && (packet = event.getPacket()) instanceof SChatPacket
                && (sPacketChat = (SChatPacket) packet).getType() != ChatType.GAME_INFO) {

            TextComponent message = (TextComponent) sPacketChat.getChatComponent();
            String rawMessage = message.getString();
            NewChatGui chatGui = BetterChat.mc.ingameGUI.getChatGUI();

            if (this.messageCounts.containsKey(rawMessage)) {
                int count = this.messageCounts.get(rawMessage) + 1;
                this.messageCounts.put(rawMessage, count);

                int lineNumber = this.messageLines.getOrDefault(rawMessage, -1);
                if (lineNumber != -1) {
                    chatGui.deleteChatLine(lineNumber);
                }

                TextComponent newMessage = (TextComponent) message.deepCopy();
                newMessage.appendString(" (зd" + count + "xеr)");
                chatGui.printChatMessage(newMessage);
                this.messageLines.put(rawMessage, this.line);
            } else {
                this.messageCounts.put(rawMessage, 1);
                chatGui.printChatMessage(message);
                this.messageLines.put(rawMessage, this.line);
            }

            if (++this.line >= 100) {
                this.line = 0;
            }
            event.setCancelled(true);
        }
    }
}