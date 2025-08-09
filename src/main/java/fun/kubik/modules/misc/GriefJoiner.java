/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.modules.misc;

import fun.kubik.events.api.EventHook;
import fun.kubik.events.main.EventUpdate;
import fun.kubik.helpers.module.swap.SwapHelpers;
import fun.kubik.managers.module.Module;
import fun.kubik.managers.module.main.Category;
import fun.kubik.managers.module.option.main.SelectOption;
import fun.kubik.managers.module.option.main.SelectOptionValue;
import fun.kubik.managers.module.option.main.SliderOption;
import fun.kubik.utils.time.TimerUtils;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.item.Items;
import net.minecraft.network.play.client.CHeldItemChangePacket;
import net.minecraft.network.play.client.CPlayerTryUseItemPacket;
import net.minecraft.util.Hand;
import ru.kotopushka.j2c.sdk.annotations.NativeInclude;

public class GriefJoiner
extends Module {
    private final SelectOption mode = new SelectOption("Mode", 0, new SelectOptionValue("Grief"), new SelectOptionValue("Mega Grief"));
    private final SliderOption grief = new SliderOption("Grief", 1.0f, 1.0f, 54.0f).increment(1.0f).visible(() -> this.mode.getSelected("Grief"));
    private final SliderOption delay = new SliderOption("Delay", 1000.0f, 10.0f, 3000.0f).increment(10.0f);
    private final TimerUtils timer = new TimerUtils();
    private final SwapHelpers swap = new SwapHelpers();

    public GriefJoiner() {
        super("GriefJoiner", Category.MISC);
        this.settings(this.mode, this.grief, this.delay);
    }

    @Override
    @NativeInclude
    public void onEnabled() {
        int slot = this.swap.find(Items.COMPASS);
        if (this.swap.haveHotBar(slot) && slot != -1) {
            GriefJoiner.mc.player.connection.sendPacket(new CHeldItemChangePacket(this.swap.format(slot)));
            GriefJoiner.mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
            this.timer.reset();
        }
    }

    @EventHook
    @NativeInclude
    public void update(EventUpdate event) {
        int mega;
        if (this.mode.getSelected("Grief")) {
            int griefName;
            int survival = this.swap.find("\u0413\u0420\u0418\u0424\u0415\u0420\u0421\u041a\u041e\u0415 \u0412\u042b\u0416\u0418\u0412\u0410\u041d\u0418\u0415 (1");
            if (survival != -1 && this.timer.hasTimeElapsed(((Float)this.delay.getValue()).longValue())) {
                GriefJoiner.mc.playerController.windowClick(GriefJoiner.mc.player.openContainer.windowId, survival, 0, ClickType.QUICK_MOVE, GriefJoiner.mc.player);
                this.timer.reset();
            }
            if ((griefName = this.swap.find("\u0413\u0420\u0418\u0424 #" + ((Float)this.grief.getValue()).intValue() + " (1")) != -1 && this.timer.hasTimeElapsed(((Float)this.delay.getValue()).longValue())) {
                GriefJoiner.mc.playerController.windowClick(GriefJoiner.mc.player.openContainer.windowId, griefName, 0, ClickType.QUICK_MOVE, GriefJoiner.mc.player);
                this.timer.reset();
            }
        } else if (this.mode.getSelected("Mega Grief") && (mega = this.swap.find("\u041c\u0415\u0413\u0410 \u0413\u0420\u0418\u0424")) != -1 && this.timer.hasTimeElapsed(((Float)this.delay.getValue()).longValue())) {
            GriefJoiner.mc.playerController.windowClick(GriefJoiner.mc.player.openContainer.windowId, mega, 0, ClickType.QUICK_MOVE, GriefJoiner.mc.player);
            this.timer.reset();
        }
    }
}

