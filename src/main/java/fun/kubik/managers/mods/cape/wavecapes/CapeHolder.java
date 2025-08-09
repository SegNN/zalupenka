/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.managers.mods.cape.wavecapes;

import fun.kubik.managers.mods.cape.wavecapes.math.Vector2;
import fun.kubik.managers.mods.cape.wavecapes.math.Vector3;
import fun.kubik.managers.mods.cape.wavecapes.sim.StickSimulation;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.util.math.MathHelper;

public interface CapeHolder {
    public StickSimulation getSimulation();

    default public void updateSimulation(AbstractClientPlayerEntity abstractClientPlayer, int partCount) {
        StickSimulation simulation = this.getSimulation();
        if (simulation == null) {
            return;
        }
        boolean dirty = simulation.init(partCount);
        if (dirty) {
            // Более плавная инициализация плаща
            simulation.applyMovement(new Vector3(0.5f, 0.5f, 0.0f));
            // Больше итераций для стабилизации
            for (int i = 0; i < 10; ++i) {
                this.simulate(abstractClientPlayer);
            }
            // Сбрасываем начальную скорость для плавного старта
            for (StickSimulation.Point point : simulation.getPoints()) {
                if (!point.locked) {
                    point.prevPosition.copy(point.position);
                }
            }
        }
    }

    default public void simulate(AbstractClientPlayerEntity abstractClientPlayer) {
        StickSimulation simulation = this.getSimulation();
        if (simulation == null || simulation.empty()) {
            return;
        }
        double d = abstractClientPlayer.chasingPosX - abstractClientPlayer.getPosX();
        double m = abstractClientPlayer.chasingPosZ - abstractClientPlayer.getPosZ();
        float n = abstractClientPlayer.prevRenderYawOffset + abstractClientPlayer.renderYawOffset - abstractClientPlayer.prevRenderYawOffset;
        double o = MathHelper.sin(n * ((float)Math.PI / 180));
        double p = -MathHelper.cos(n * ((float)Math.PI / 180));
        
        // Улучшенные параметры движения
        float heightMul = 6.0f; // Увеличили чувствительность
        float straveMul = 4.0f; // Увеличили боковое движение
        
        // Расчет скорости для динамической настройки
        double velocityX = abstractClientPlayer.getPosX() - abstractClientPlayer.prevPosX;
        double velocityZ = abstractClientPlayer.getPosZ() - abstractClientPlayer.prevPosZ;
        double velocity = Math.sqrt(velocityX * velocityX + velocityZ * velocityZ);
        if (abstractClientPlayer.canSwim()) {
            heightMul *= 2.0f;
        }
        double fallHack = MathHelper.clamp((abstractClientPlayer.prevPosY - abstractClientPlayer.getPosY()) * 10.0, 0.0, 1.0);
        // Динамическая настройка гравитации
        if (abstractClientPlayer.canSwim()) {
            simulation.setGravity(3.0f);
        } else if (abstractClientPlayer.isOnGround()) {
            simulation.setGravity(30.0f); // Сильнее на земле
        } else {
            simulation.setGravity(20.0f); // Меньше в воздухе
        }
        
        // Настройка воздушного сопротивления в зависимости от скорости
        float airRes = (float)(0.01f + velocity * 0.05f); // Чем быстрее, тем больше сопротивление
        simulation.setAirResistance(Math.min(airRes, 0.08f));
        
        // Настройка затухания
        float dampingValue = abstractClientPlayer.isCrouching() ? 0.95f : 0.98f;
        simulation.setDamping(dampingValue);
        
        // Симуляция ветра (легкое колыхание)
        double time = abstractClientPlayer.ticksExisted * 0.1;
        float windX = (float)(Math.sin(time) * 0.5f);
        float windZ = (float)(Math.cos(time * 0.7) * 0.3f);
        simulation.setWindForce(new Vector3(windX, 0.0f, windZ));
        Vector3 gravity = new Vector3(0.0f, -1.0f, 0.0f);
        Vector2 strave = new Vector2((float)(abstractClientPlayer.getPosX() - abstractClientPlayer.prevPosX), (float)(abstractClientPlayer.getPosZ() - abstractClientPlayer.prevPosZ));
        strave.rotateDegrees(-abstractClientPlayer.rotationYaw);
        // Улучшенные расчеты движения
        double changeX = d * o + m * p + fallHack + (double)(abstractClientPlayer.isCrouching() && !simulation.isSneaking() ? 4 : 0);
        double changeY = (abstractClientPlayer.getPosY() - abstractClientPlayer.prevPosY) * (double)heightMul + (double)(abstractClientPlayer.isCrouching() && !simulation.isSneaking() ? 1.5 : 0);
        double changeZ = -strave.x * straveMul;
        
        // Дополнительное движение при быстром повороте
        float yawDelta = Math.abs(abstractClientPlayer.rotationYaw - abstractClientPlayer.prevRotationYaw);
        if (yawDelta > 180) yawDelta = 360 - yawDelta; // Нормализация угла
        double rotationForce = yawDelta * 0.1; // Множитель для эффекта поворота
        changeZ += rotationForce;
        simulation.setSneaking(abstractClientPlayer.isCrouching());
        Vector3 change = new Vector3((float)changeX, (float)changeY, (float)changeZ);
        // Улучшенная логика для плавания
        if (abstractClientPlayer.isActualySwimming()) {
            float rotation = abstractClientPlayer.rotationPitch;
            gravity.rotateDegrees(rotation += 90.0f);
            change.rotateDegrees(rotation);
            // Уменьшаем интенсивность движения под водой
            change.mul(0.7f);
        } else if (!abstractClientPlayer.isOnGround()) {
            // Усиливаем эффект при прыжках/полете
            change.mul(1.2f);
        }
        simulation.setGravityDirection(gravity);
        simulation.applyMovement(change);
        simulation.simulate();
    }
}

