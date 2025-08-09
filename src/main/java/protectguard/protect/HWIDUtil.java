package protectguard.protect;

import java.net.NetworkInterface;
import java.security.MessageDigest;
import java.util.Collections;

public class HWIDUtil {
    public static String getHWID() {
        try {
            StringBuilder sb = new StringBuilder();
            // MAC-адреса
            for (NetworkInterface ni : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                byte[] mac = ni.getHardwareAddress();
                if (mac != null) {
                    for (byte b : mac) sb.append(String.format("%02X", b));
                }
            }
            // Имя компа и пользователя
            sb.append(System.getProperty("user.name"));
            sb.append(System.getProperty("os.name"));
            sb.append(System.getenv("COMPUTERNAME"));
            // Серийник диска (Windows)
            try {
                Process process = Runtime.getRuntime().exec(new String[]{"wmic", "diskdrive", "get", "serialnumber"});
                java.util.Scanner s = new java.util.Scanner(process.getInputStream()).useDelimiter("\\A");
                if (s.hasNext()) sb.append(s.next());
            } catch (Exception ignored) {}
            // Хэшируем всё это
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] hash = md.digest(sb.toString().getBytes("UTF-8"));
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) hex.append(String.format("%02x", b));
            return hex.toString();
        } catch (Exception e) {
            return "unknown";
        }
    }
}