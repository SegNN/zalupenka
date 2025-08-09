/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.helpers.render;

import fun.kubik.helpers.interfaces.IFastAccess;
import lombok.Generated;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;

public final class ScreenHelpers
implements IFastAccess {
    public static Vector3d screenToWorld(float screenX, float screenY) {
        Vector3d camera_pos = ScreenHelpers.mc.getRenderManager().info.getProjectedView();
        Quaternion cameraRotation = mc.getRenderManager().getCameraOrientation().copy();
        float halfWidth = (float)mc.getMainWindow().getWidth() / 2.0f;
        float halfHeight = (float)mc.getMainWindow().getHeight() / 2.0f;
        float normalizedX = (screenX - halfWidth) / halfWidth;
        float normalizedY = (halfHeight - screenY) / halfHeight;
        double fov = ScreenHelpers.mc.gameRenderer.getFOVModifier(ScreenHelpers.mc.getRenderManager().info, mc.getRenderPartialTicks(), true);
        float scaleFactor = (float)((double)mc.getMainWindow().getHeight() / (2.0 * Math.tan(Math.toRadians(fov / 2.0))));
        Vector3f screenVector = new Vector3f(normalizedX * scaleFactor, normalizedY * scaleFactor, 1.0f);
        screenVector.transform(cameraRotation);
        return new Vector3d(camera_pos.x - (double)screenVector.getX(), camera_pos.y - (double)screenVector.getY(), camera_pos.z - (double)screenVector.getZ());
    }

    public static Vector2f worldToScreen(Vector3d pos) {
        return ScreenHelpers.worldToScreen(pos.x, pos.y, pos.z);
    }

    public static Vector2f worldToScreen(double x, double y, double z) {
        Entity renderViewEntity;
        Vector3d camera_pos = ScreenHelpers.mc.getRenderManager().info.getProjectedView();
        Quaternion cameraRotation = mc.getRenderManager().getCameraOrientation().copy();
        cameraRotation.conjugate();
        Vector3f result3f = new Vector3f((float)(camera_pos.x - x), (float)(camera_pos.y - y), (float)(camera_pos.z - z));
        result3f.transform(cameraRotation);
        if (ScreenHelpers.mc.gameSettings.viewBobbing && (renderViewEntity = mc.getRenderViewEntity()) instanceof PlayerEntity) {
            PlayerEntity playerentity = (PlayerEntity)renderViewEntity;
            ScreenHelpers.calculateViewBobbing(playerentity, result3f);
        }
        double fov = ScreenHelpers.mc.gameRenderer.getFOVModifier(ScreenHelpers.mc.getRenderManager().info, mc.getRenderPartialTicks(), true);
        return ScreenHelpers.calculateScreenPosition(result3f, fov);
    }

    private static void calculateViewBobbing(PlayerEntity playerentity, Vector3f result3f) {
        float walked = playerentity.distanceWalkedModified;
        float f = walked - playerentity.prevDistanceWalkedModified;
        float f1 = -(walked + f * mc.getRenderPartialTicks());
        float f2 = MathHelper.lerp(mc.getRenderPartialTicks(), playerentity.prevCameraYaw, playerentity.cameraYaw);
        Quaternion quaternion = new Quaternion(Vector3f.XP, Math.abs(MathHelper.cos(f1 * (float)Math.PI - 0.2f) * f2) * 5.0f, true);
        quaternion.conjugate();
        result3f.transform(quaternion);
        Quaternion quaternion1 = new Quaternion(Vector3f.ZP, MathHelper.sin(f1 * (float)Math.PI) * f2 * 3.0f, true);
        quaternion1.conjugate();
        result3f.transform(quaternion1);
        Vector3f bobTranslation = new Vector3f(MathHelper.sin(f1 * (float)Math.PI) * f2 * 0.5f, -Math.abs(MathHelper.cos(f1 * (float)Math.PI) * f2), 0.0f);
        bobTranslation.setY(-bobTranslation.getY());
        result3f.add(bobTranslation);
    }

    private static Vector2f calculateScreenPosition(Vector3f result3f, double fov) {
        float halfHeight = (float)mc.getMainWindow().getScaledHeight() / 2.0f;
        float scaleFactor = halfHeight / (result3f.getZ() * (float)Math.tan(Math.toRadians(fov / 2.0)));
        if (result3f.getZ() < 0.0f) {
            return new Vector2f(-result3f.getX() * scaleFactor + (float)mc.getMainWindow().getScaledWidth() / 2.0f, (float)mc.getMainWindow().getScaledHeight() / 2.0f - result3f.getY() * scaleFactor);
        }
        return new Vector2f(Float.MAX_VALUE, Float.MAX_VALUE);
    }

    public static boolean isHovered(int mX, int mY, float x, float y, float width, float height) {
        return (float)mX > x && (float)mX < x + width && (float)mY > y && (float)mY < y + height;
    }

    public static boolean isHovered(double mX, double mY, float x, float y, float width, float height) {
        return mX > (double)x && mX < (double)(x + width) && mY > (double)y && mY < (double)(y + height);
    }

    public static boolean isHovered(float mX, float mY, float x, float y, float width, float height) {
        return mX > x && mX < x + width && mY > y && mY < y + height;
    }

    @Generated
    private ScreenHelpers() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}

