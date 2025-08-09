package fun.kubik.modules.combat;

import fun.kubik.events.api.EventHook;
import fun.kubik.events.main.EventUpdate;
import fun.kubik.managers.module.Module;
import fun.kubik.managers.module.main.Category;
import fun.kubik.managers.module.option.main.*;
import fun.kubik.managers.module.option.main.CheckboxOption;
import fun.kubik.managers.module.option.main.MultiOption;
import fun.kubik.managers.module.option.main.MultiOptionValue;
import fun.kubik.managers.module.option.main.SliderOption;
import net.minecraft.entity.*;
import net.minecraft.item.*;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AimBot extends Module {
    private final MultiOption weapons = new MultiOption("оружия",
            new MultiOptionValue("лук", true),
            new MultiOptionValue("арбалет", true),
            new MultiOptionValue("трезубес", true)
    );

    private final CheckboxOption prediction = new CheckboxOption("Prediction", true);
    private final SliderOption fov = new SliderOption("FOV", 180f, 1f, 360f);
    private final CheckboxOption circleFov = new CheckboxOption("Circle FOV", false);
    private final CheckboxOption autoWall = new CheckboxOption("Auto Wall", false);

    private LivingEntity target;
    private Vector2f rotation;

    public AimBot() {
        super("AimBot", Category.COMBAT);
        this.settings(weapons, prediction, fov, circleFov, autoWall);
    }

    @EventHook
    public void onUpdate(EventUpdate event) {
        if (mc.player == null || mc.world == null) {
            reset();
            return;
        }

        if (isUsingWeapon()) {
            updateTarget();

            if (target != null && isInFov(target)) {
                aimAtTarget();
            }
        } else {
            reset();
        }
    }

    private boolean isUsingWeapon() {
        ItemStack stack = mc.player.getHeldItemMainhand();
        if (stack.isEmpty()) return false;

        if (stack.getItem() instanceof BowItem) return weapons.getSelected("лук");
        if (stack.getItem() instanceof CrossbowItem) return weapons.getSelected("арбалет");
        if (stack.getItem() instanceof TridentItem) return weapons.getSelected("трезубес");

        return false;
    }

    private void updateTarget() {
        List<LivingEntity> targets = new ArrayList<>();

        for (Entity entity : mc.world.getAllEntities()) {
            if (isValidTarget(entity)) {
                targets.add((LivingEntity) entity);
            }
        }

        targets.sort(Comparator.comparingDouble(e -> mc.player.getDistanceSq(e)));
        target = targets.isEmpty() ? null : targets.get(0);
    }

    private boolean isValidTarget(Entity entity) {
        if (!(entity instanceof LivingEntity)) return false;
        if (entity == mc.player) return false;
        if (!entity.isAlive()) return false;
        if (!autoWall.getValue() && !canSeeEntity(entity)) return false;

        return true;
    }

    private boolean canSeeEntity(Entity entity) {
        return mc.player.canEntityBeSeen(entity);
    }

    private boolean isInFov(Entity entity) {
        Vector2f angles = getAnglesToEntity(entity);
        float currentYaw = mc.player.rotationYaw;
        float currentPitch = mc.player.rotationPitch;

        float yawDiff = MathHelper.wrapDegrees(angles.x - currentYaw);
        float pitchDiff = MathHelper.wrapDegrees(angles.y - currentPitch);

        if (circleFov.getValue()) {
            // ������� FOV
            return Math.sqrt(yawDiff * yawDiff + pitchDiff * pitchDiff) <= fov.getValue();
        } else {
            // ���������� FOV
            return Math.abs(yawDiff) <= fov.getValue()/2 && Math.abs(pitchDiff) <= fov.getValue()/2;
        }
    }

    private Vector2f getAnglesToEntity(Entity entity) {
        Vector3d eyesPos = mc.player.getEyePosition(1.0F);
        Vector3d targetPos = entity.getBoundingBox().getCenter();

        if (prediction.getValue()) {
            targetPos = predictPosition(entity);
        }

        Vector3d diff = targetPos.subtract(eyesPos);
        double dist = diff.length();

        float yaw = (float)Math.toDegrees(Math.atan2(diff.z, diff.x)) - 90F;
        float pitch = (float)-Math.toDegrees(Math.atan2(diff.y, Math.sqrt(diff.x * diff.x + diff.z * diff.z)));

        return new Vector2f(yaw, pitch);
    }

    private Vector3d predictPosition(Entity entity) {
        Vector3d currentPos = entity.getBoundingBox().getCenter();

        if (entity instanceof LivingEntity) {
            LivingEntity living = (LivingEntity)entity;
            Vector3d motion = living.getMotion();

            if (motion.lengthSquared() > 0.001) {
                double distance = currentPos.distanceTo(mc.player.getEyePosition(1.0F));
                double speed = getProjectileSpeed();
                double predictionTime = distance / speed;

                return currentPos.add(motion.scale(predictionTime));
            }
        }

        return currentPos;
    }

    private double getProjectileSpeed() {
        Item item = mc.player.getHeldItemMainhand().getItem();
        if (item instanceof BowItem) return 3.0;
        if (item instanceof CrossbowItem) return 3.15;
        if (item instanceof TridentItem) return 2.5;
        return 3.0;
    }

    private void aimAtTarget() {
        Vector2f targetAngles = getAnglesToEntity(target);

        // Silent ����� - ��������� ����
        rotation = targetAngles;

        // ��������� �������
        mc.player.rotationYaw = rotation.x;
        mc.player.rotationPitch = rotation.y;
        mc.player.rotationYawHead = rotation.x;
    }

    private void reset() {
        target = null;
        rotation = null;
    }

    @Override
    public void onEnabled() {
        reset();
    }

    @Override
    public void onDisabled() {
        reset();
    }
}