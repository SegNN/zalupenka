package fun.kubik.modules.movement;

import fun.kubik.Load;
import fun.kubik.events.api.EventHook;
import fun.kubik.events.main.EventUpdate;
import fun.kubik.utils.time.TimerUtils;
import fun.kubik.managers.module.Module;
import fun.kubik.managers.module.main.Category;
import fun.kubik.managers.module.option.main.CheckboxOption;
import fun.kubik.managers.module.option.main.SliderOption;
import net.minecraft.util.math.vector.Vector3d;

public class ElytraMotion extends Module {
    
    private final CheckboxOption hover = new CheckboxOption("Hover", true);
    private final CheckboxOption targetAttraction = new CheckboxOption("Target Attraction", true);
    private final SliderOption hoverHeight = new SliderOption("Hover Height", 2.0f, 0.5f, 10.0f).increment(0.1f);
    private final SliderOption attractionSpeed = new SliderOption("Attraction Speed", 0.3f, 0.1f, 1.0f).increment(0.01f);
    private final SliderOption attractionDistance = new SliderOption("Attraction Distance", 5.0f, 1.0f, 15.0f).increment(0.1f);
    
    private double originalY;
    private boolean wasOnGround;
    private final TimerUtils hoverTimer = new TimerUtils();

    public ElytraMotion() {
        super("ElytraMotion", Category.MOVEMENT);
        this.settings(this.hover, this.targetAttraction, this.hoverHeight, this.attractionSpeed, this.attractionDistance);
    }

    public void onEnable() {
        if (mc.player != null) {
            this.originalY = mc.player.getPosY();
            this.wasOnGround = mc.player.isOnGround();
        }
        this.hoverTimer.reset();
    }

    @EventHook
    public void onUpdate(EventUpdate event) {
        if (mc.player == null || mc.world == null) {
            return;
        }

        // Проверяем, что игрок летает на элитрах
        if (!mc.player.isElytraFlying()) {
            return;
        }

        try {
            if (this.hover.getValue()) {
                this.handleHover();
            }

            if (this.targetAttraction.getValue()) {
                this.handleTargetAttraction();
            }
        } catch (Exception e) {
            // Предотвращаем краши
            System.err.println("ElytraMotion error: " + e.getMessage());
        }
    }

    private void handleHover() {
        if (mc.player == null) return;
        
        // Обновляем originalY только если игрок был на земле
        if (this.wasOnGround && !mc.player.isOnGround()) {
            this.originalY = mc.player.getPosY();
            this.wasOnGround = false;
        } else if (mc.player.isOnGround()) {
            this.wasOnGround = true;
            return;
        }

        double targetY = this.originalY + this.hoverHeight.getValue();
        double currentY = mc.player.getPosY();
        double diff = targetY - currentY;

        // Плавное зависание с ограничением скорости
        if (Math.abs(diff) > 0.1 && this.hoverTimer.hasTimeElapsed(50)) {
            Vector3d motion = mc.player.getMotion();
            double newMotionY = Math.max(-0.5, Math.min(0.5, diff * 0.1));
            
            // Применяем более плавное движение
            mc.player.setMotion(motion.x, newMotionY, motion.z);
            this.hoverTimer.reset();
        }
    }

    private void handleTargetAttraction() {
        if (mc.player == null) return;
        
        // Получаем цель из Aura модуля (если есть)
        try {
            Object auraModule = Load.getInstance().getHooks().getModuleManagers().findName("Aura");
            if (auraModule != null) {
                // Используем рефлексию для безопасного доступа к target
                java.lang.reflect.Field targetField = auraModule.getClass().getDeclaredField("target");
                targetField.setAccessible(true);
                Object target = targetField.get(auraModule);
                
                if (target != null && target instanceof net.minecraft.entity.LivingEntity) {
                    net.minecraft.entity.LivingEntity livingTarget = (net.minecraft.entity.LivingEntity) target;
                    
                    double distance = mc.player.getDistanceSq(livingTarget);
                    if (distance > this.attractionDistance.getValue() * this.attractionDistance.getValue()) {
                        return;
                    }

                    Vector3d targetPos = livingTarget.getPositionVec();
                    Vector3d playerPos = mc.player.getPositionVec();
                    Vector3d direction = targetPos.subtract(playerPos).normalize();
                    
                    Vector3d motion = mc.player.getMotion();
                    double speed = this.attractionSpeed.getValue();
                    
                    // Плавное притяжение к цели
                    Vector3d newMotion = motion.add(
                        direction.x * speed * 0.1,
                        direction.y * speed * 0.05, // Меньше по Y для стабильности
                        direction.z * speed * 0.1
                    );
                    
                    // Ограничиваем скорость
                    double maxSpeed = 2.0;
                    if (newMotion.length() > maxSpeed) {
                        newMotion = newMotion.normalize().scale(maxSpeed);
                    }
                    
                    mc.player.setMotion(newMotion);
                }
            }
        } catch (Exception e) {
            // Безопасно игнорируем ошибки доступа к target
        }
    }

    public void onDisable() {
        if (mc.player != null) {
            // Сбрасываем движение при отключении для предотвращения зависания
            Vector3d motion = mc.player.getMotion();
            mc.player.setMotion(motion.x * 0.8, Math.max(-0.5, motion.y), motion.z * 0.8);
        }
    }
}
