package fun.kubik.modules.combat;

import fun.kubik.managers.module.Module;
import fun.kubik.managers.module.main.Category;
import fun.kubik.utils.ServerTPS;
import fun.kubik.utils.client.ChatUtils;

    public class TPSSync extends Module {
    private static TPSSync instance;

    public TPSSync() {
        super("TPSSync", Category.COMBAT);
        instance = this;
    }

    public static TPSSync getInstance() {
        return instance;
    }

    @Override
    public void onEnabled() {
        super.onEnabled();
        // Инициализируем ServerTPS для получения актуального TPS
        ServerTPS.getInstance();
        ChatUtils.addClientMessage("�b���� �� ������ ������ 18 �����, ������ ����� �� ��������.");
    }

    @Override
    public void onDisabled() {
        super.onDisabled();
    }


    public long getAttackDelay() {
        return isToggled() ? 860L : 460L;
    }
}