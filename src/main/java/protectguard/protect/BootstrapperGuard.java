package protectguard.protect;

public class BootstrapperGuard {
    public static void launchWithGuard(Runnable minecraftMain) {
        if (!ProtectGuard.checkAndLog()) {
            // Проверка не пройдена, Minecraft не запускаем
            return;
        }
        // Проверка пройдена, запускаем Minecraft
        minecraftMain.run();
    }
}