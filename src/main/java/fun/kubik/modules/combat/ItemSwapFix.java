package fun.kubik.modules.combat;

import fun.kubik.events.api.EventHook;

import fun.kubik.events.main.EventPacket;
import fun.kubik.managers.module.Module;
import fun.kubik.managers.module.main.Category;
import net.minecraft.network.play.client.CHeldItemChangePacket;
import net.minecraft.network.play.server.SHeldItemChangePacket;

public class ItemSwapFix extends Module {

    public ItemSwapFix() {
        super("ItemSwapFix", Category.COMBAT);
    }

    @EventHook
    private void onPacket(EventPacket e) {
        if (mc.player == null) return;

        if (e.getPacket() instanceof SHeldItemChangePacket) {
            SHeldItemChangePacket packet = (SHeldItemChangePacket) e.getPacket();
            int serverSlot = packet.getHeldItemHotbarIndex();

            if (serverSlot != mc.player.inventory.currentItem) {
                mc.player.connection.sendPacket(
                        new CHeldItemChangePacket(Math.max(mc.player.inventory.currentItem - 1, 0))
                );
                mc.player.connection.sendPacket(
                        new CHeldItemChangePacket(mc.player.inventory.currentItem)
                );
                e.isCancelled();
            }
        }
    }
}