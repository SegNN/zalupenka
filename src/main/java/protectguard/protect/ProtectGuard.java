package protectguard.protect;

import org.json.JSONObject;
import java.util.HashMap;

public class ProtectGuard {
    public static boolean checkAndLog() {
        System.out.println("[ProtectGuard] Запуск проверки HWID...");
        String hwid = HWIDUtil.getHWID();
        String ip = NetworkUtil.getExternalIP();
        String username = System.getProperty("user.name");
        System.out.println("[ProtectGuard] HWID: " + hwid);
        System.out.println("[ProtectGuard] IP: " + ip);
        System.out.println("[ProtectGuard] Username: " + username);
        // String screenshot = ScreenshotUtil.captureBase64(); // если нужно

        // 1. Проверка HWID
        JSONObject response = Connect.post("https://monotron.fun/protectguard/protect/check.php", new HashMap<String, String>() {{
            put("hwid", hwid);
            put("ip", ip);
        }});
        System.out.println("[ProtectGuard] Ответ от check.php: " + (response == null ? "null" : response.toString()));
        if (response != null && response.optBoolean("success", false)) {
            UserInfo.username = response.optString("username", "");
            UserInfo.role = response.optString("role", "");
            UserInfo.uid = response.optString("id", "");
            UserInfo.discordUsername = response.optString("discord_username", "");
            UserInfo.discordId = response.optString("discord_id", "");

            // Ждём появления discordUsername/discordId (до 60 секунд) и только потом отправляем лог
            new Thread(() -> {
                int tries = 0;
                while ((UserInfo.discordUsername == null || UserInfo.discordUsername.isEmpty() || UserInfo.discordId == null || UserInfo.discordId.isEmpty()) && tries < 60) {
                    try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
                    tries++;
                }
                System.out.println("[ProtectGuard] Discord username/id для логирования: " + UserInfo.discordUsername + ", " + UserInfo.discordId);
                String screenshot = ScreenshotUtil.captureBase64();
                Connect.post("https://monotron.fun/protectguard/protect/log.php", new java.util.HashMap<String, String>() {{
                    put("hwid", hwid);
                    put("username", UserInfo.username);
                    put("role", UserInfo.role);
                    put("id", UserInfo.uid);
                    put("discord_username", UserInfo.discordUsername);
                    put("discord_id", UserInfo.discordId);
                    put("ip", ip);
                    put("screenshot", screenshot);
                }});
            }).start();
        }
        if (response == null || !response.optBoolean("success", false)) {
            System.exit(0);
            return false;
        }

        // 2. Логирование запуска
        System.out.println("[ProtectGuard] HWID найден, логируем запуск...");
        String screenshot = ScreenshotUtil.captureBase64();
        JSONObject logResp = Connect.post("https://monotron.fun/protectguard/protect/log.php", new HashMap<String, String>() {{
            put("hwid", hwid);
            put("username", UserInfo.username);
            put("role", UserInfo.role);
            put("id", UserInfo.uid);
            put("discord_username", UserInfo.discordUsername);
            put("discord_id", UserInfo.discordId);
            put("ip", ip);
            put("screenshot", screenshot);
        }});
        System.out.println("[ProtectGuard] Ответ от log.php: " + (logResp == null ? "null" : logResp.toString()));
        System.out.println("[ProtectGuard] Проверка и логирование завершены успешно.");
        return true;
    }
}