/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.modules.player;

import fun.kubik.managers.module.Module;
import fun.kubik.managers.module.main.Category;
//import de.maxhenkel.voicechat.VoicechatClient;
//import de.maxhenkel.voicechat.Voicechat;
//import de.maxhenkel.voicechat.gui.VoiceChatScreen;
//import de.maxhenkel.voicechat.voice.client.ClientManager;
//import de.maxhenkel.voicechat.voice.client.ClientVoicechat;


public class VoiceChat extends Module {
    private boolean voicechatInitialized = false;
    private boolean initializationAttempted = false;
    private int tickCounter = 0;

    public VoiceChat() {
        super("VoiceChat", Category.PLAYER);
    }
}
//
//    @Override
//    public void onEnabled() {
//        super.onEnabled();
//        // НЕ инициализируем VoiceChat сразу, делаем это в EventUpdate когда игра полностью загружена
//        System.out.println("[VoiceChat] Модуль активирован - инициализация будет выполнена позже");
//    }
//
//    @Override
///    public void onDisabled() {
//        super.onDisabled();
//        // Отключение Simple Voice Chat (клиент)
//        try {
//            System.out.println("[VoiceChat] Модуль деактивирован - отключаем VoiceChat");
//            if (voicechatInitialized) {
//               ClientVoicechat client = ClientManager.getClient();
//                if (client != null) {
//                    client.close();
//                    System.out.println("[VoiceChat] Клиент VoiceChat закрыт");
//                }
//               voicechatInitialized = false;
//            }
//            initializationAttempted = false;
//            tickCounter = 0; // Сбрасываем счётчик
//        } catch (Exception e) {
//            System.err.println("[VoiceChat] Ошибка при отключении: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }
//
//    @EventHook
//    public void update(EventUpdate event) {
//        if (isToggled()) {
//            tickCounter++;
//
//            // Отладка каждые 100 тиков (5 секунд)
//            if (tickCounter % 100 == 0) {
//                System.out.println("[VoiceChat] Tick #" + tickCounter + ", инициализирован: " + voicechatInitialized + ", попытка: " + initializationAttempted);
//            }
//        }
//
//        // Безопасная отложенная инициализация VoiceChat (через 2 секунды после включения)
//        if (!voicechatInitialized && !initializationAttempted && isToggled() && tickCounter > 40) { // 40 тиков = ~2 секунды
//            tryInitializeVoiceChat();
//        }
//
//        // Проверка хоткея V (с отладкой)
//        if (isToggled()) {
//            boolean vKeyPressed = GLFW.glfwGetKey(Minecraft.getInstance().getMainWindow().getHandle(), GLFW.GLFW_KEY_V) == GLFW.GLFW_PRESS;
//            boolean noScreen = Minecraft.getInstance().currentScreen == null;
//
//            // Отладка каждые 20 тиков (~1 секунда) если V нажата
//            if (vKeyPressed && tickCounter % 20 == 0) {
//                System.out.println("[VoiceChat] V нажата! VoiceChat инициализирован: " + voicechatInitialized + ", экран: " + (noScreen ? "null" : Minecraft.getInstance().currentScreen.getClass().getSimpleName()));
//            }
//
//            // Открытие GUI по хоткею (V) - только если VoiceChat инициализирован
//            if (voicechatInitialized && noScreen && vKeyPressed) {
//                try {
//                    System.out.println("[VoiceChat] Открываем VoiceChat GUI");
//                    Minecraft.getInstance().displayGuiScreen(new VoiceChatScreen());
//                } catch (Exception e) {
//                    System.err.println("[VoiceChat] Ошибка при открытии GUI: " + e.getMessage());
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
//
//    private void tryInitializeVoiceChat() {
//        initializationAttempted = true;
//
//        // Проверяем, что игра полностью загружена (главный экран или мир)
//        Minecraft mc = Minecraft.getInstance();
//        if (mc == null || mc.gameSettings == null || mc.getMainWindow() == null) {
//            System.out.println("[VoiceChat] Игра ещё не загружена (отсутствуют основные компоненты), пропускаем инициализацию");
//            initializationAttempted = false; // Попробуем ещё раз позже
//            return;
//        }
//
//        try {
//            System.out.println("[VoiceChat] Игра загружена - инициализируем VoiceChat");
//            ClientManager.instance(); // lazy init, запускает всё нужное
//            voicechatInitialized = true;
//            System.out.println("[VoiceChat] ClientManager инициализирован успешно");
//        } catch (Exception e) {
//            System.err.println("[VoiceChat] Ошибка при инициализации: " + e.getMessage());
//            System.err.println("[VoiceChat] Тип ошибки: " + e.getClass().getSimpleName());
//
//            // Проверяем конкретные типы ошибок
//            if (e.getMessage() != null && e.getMessage().contains("CommonCompatibilityManager")) {
//                System.err.println("[VoiceChat] Проблема с CommonCompatibilityManager - возможно, отсутствует VoiceChat mod");
//                System.err.println("[VoiceChat] Попробуем альтернативную инициализацию...");
//
//                // Альтернативная инициализация - просто помечаем как инициализированный
//                // чтобы хотя бы GUI можно было открыть
//                voicechatInitialized = true;
//                System.out.println("[VoiceChat] Инициализация в режиме совместимости завершена");
//            } else {
//                System.err.println("[VoiceChat] VoiceChat может быть не поддерживается в данной сборке");
//                e.printStackTrace();
//                // Не сбрасываем initializationAttempted, чтобы не пытаться снова
//            }
//        }
//    }
//}