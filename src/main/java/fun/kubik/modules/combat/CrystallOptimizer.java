package fun.kubik.modules.combat;

import fun.kubik.events.api.EventHook;
import fun.kubik.events.main.EventUpdate;
import fun.kubik.events.main.misc.AttackEvent;

import fun.kubik.managers.module.Module;
import fun.kubik.managers.module.main.Category;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EnderCrystalEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;

public class CrystallOptimizer extends Module {

    public CrystallOptimizer() {
        super("CrystalOptimizer", Category.COMBAT);
    }

    @EventHook
    public void onUpdate(EventUpdate event) {
        if (mc.player == null || mc.world == null) {
            return;
        }

        // Проверяем, наводим ли мы на кристалл
        RayTraceResult rayTrace = mc.objectMouseOver;
        if (rayTrace instanceof EntityRayTraceResult) {
            EntityRayTraceResult entityRayTrace = (EntityRayTraceResult) rayTrace;
            Entity target = entityRayTrace.getEntity();

            if (target instanceof EnderCrystalEntity) {
                // Автоматически атакуем кристалл при наведении
                mc.playerController.attackEntity(mc.player, target);
                mc.player.swingArm(Hand.MAIN_HAND);
            }
        } else {
            // Дополнительная проверка кристаллов в радиусе курсора
            checkCrystalsInCrosshair();
        }
    }

    @EventHook
    public void onAttackEntity(AttackEvent event) {
        Entity target = event.entity;

        if (target instanceof EnderCrystalEntity) {
            // Убираем задержку на установку кристалла
            EnderCrystalEntity crystal = (EnderCrystalEntity) target;

            // Мгновенно уничтожаем кристалл
            crystal.remove();

            // Анимация удара
            if (mc.player != null) {
                mc.player.swingArm(Hand.MAIN_HAND);
            }

            // Отменяем стандартную обработку атаки
            event.setCancelled(true);
        }
    }

    private void checkCrystalsInCrosshair() {
        if (mc.player == null || mc.world == null) {
            return;
        }

        Vector3d start = mc.player.getEyePosition(1.0f);
        Vector3d look = mc.player.getLook(1.0f);
        Vector3d end = start.add(look.scale(6.0)); // 6 блоков дальность

        // Проверяем все кристаллы в мире
        for (Entity entity : mc.world.getAllEntities()) {
            if (entity instanceof EnderCrystalEntity) {
                AxisAlignedBB boundingBox = entity.getBoundingBox().grow(0.3);

                // Проверяем пересечение луча с хитбоксом кристалла
                if (boundingBox.rayTrace(start, end).isPresent()) {
                    // Атакуем кристалл
                    mc.playerController.attackEntity(mc.player, entity);
                    mc.player.swingArm(Hand.MAIN_HAND);
                    break; // Атакуем только один кристалл за раз
                }
            }
        }
    }

    @Override
    public void onEnabled() {
        super.onEnabled();
    }

    @Override
    public void onDisabled() {
        super.onDisabled();
    }
}