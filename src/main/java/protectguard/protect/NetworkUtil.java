package protectguard.protect;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class NetworkUtil {
    public static String getExternalIP() {
        try {
            URL url = new URL("https://api.ipify.org");
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            return in.readLine();
        } catch (Exception e) {
            return "unknown";
        }
    }
}