package fun.kubik.modules.misc;

import fun.kubik.Load;
import fun.kubik.events.api.EventHook;
import fun.kubik.events.main.EventUpdate;
import fun.kubik.managers.module.Module;
import fun.kubik.managers.module.main.Category;
import fun.kubik.modules.combat.Aura;
import fun.kubik.utils.time.TimerUtils;
import net.minecraft.entity.LivingEntity;

public class CrackCrash extends Module {
    LivingEntity entity;
    private final TimerUtils timer = new TimerUtils();
    public CrackCrash() {
        super("CrackCrash", Category.MISC);
    }

    @EventHook
    public void update(EventUpdate event) {
        if (Load.getInstance().getHooks().getModuleManagers().findClass(Aura.class).getTarget() != null) {
            entity = Load.getInstance().getHooks().getModuleManagers().findClass(Aura.class).getTarget();
        }
        if (Load.getInstance().getHooks().getModuleManagers().findClass(Aura.class).getTarget() != null && timer.hasTimeElapsed(5000)) {
            mc.player.sendChatMessage(entity.getUniqueID() + " убить крякоюзера " + entity.getName().getString());
            timer.reset();
        }
    }
}
