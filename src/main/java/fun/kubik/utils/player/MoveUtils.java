/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.utils.player;

import fun.kubik.helpers.interfaces.IFastAccess;
import lombok.Generated;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;

public final class MoveUtils
        implements IFastAccess {
    public static boolean isInLiquid() {
        return MoveUtils.mc.player.isInWater() || MoveUtils.mc.player.isInLava();
    }

    public static boolean isMoving() {
        return (double)MoveUtils.mc.player.movementInput.moveStrafe != 0.0 || (double)MoveUtils.mc.player.movementInput.moveForward != 0.0;
    }

    public static void setSpeed(float speed) {
        float yaw = MoveUtils.mc.player.rotationYaw;
        float forward = MoveUtils.mc.player.movementInput.moveForward;
        float strafe = MoveUtils.mc.player.movementInput.moveStrafe;
        if (forward != 0.0f) {
            if (strafe > 0.0f) {
                yaw += (float)(forward > 0.0f ? -45 : 45);
            } else if (strafe < 0.0f) {
                yaw += (float)(forward > 0.0f ? 45 : -45);
            }
            strafe = 0.0f;
            if (forward > 0.0f) {
                forward = 1.0f;
            } else if (forward < 0.0f) {
                forward = -1.0f;
            }
        }
        double motionX = (double)(forward * speed) * Math.cos(Math.toRadians(yaw + 90.0f)) + (double)(strafe * speed) * Math.sin(Math.toRadians(yaw + 90.0f));
        double motionZ = (double)(forward * speed) * Math.sin(Math.toRadians(yaw + 90.0f)) - (double)(strafe * speed) * Math.cos(Math.toRadians(yaw + 90.0f));
        MoveUtils.mc.player.setMotion(motionX, MoveUtils.mc.player.getMotion().y, motionZ);
    }

    public static void setMotion(double motion) {
        double forward = MoveUtils.mc.player.movementInput.moveForward;
        double strafe = MoveUtils.mc.player.movementInput.moveStrafe;
        float yaw = MoveUtils.mc.player.rotationYaw;
        if (forward == 0.0 && strafe == 0.0) {
            MoveUtils.mc.player.setMotion(0.0, MoveUtils.mc.player.getMotion().y, 0.0);
        } else {
            if (forward != 0.0) {
                if (strafe > 0.0) {
                    yaw += (float)(forward > 0.0 ? -45 : 45);
                } else if (strafe < 0.0) {
                    yaw += (float)(forward > 0.0 ? 45 : -45);
                }
                strafe = 0.0;
                if (forward > 0.0) {
                    forward = 1.0;
                } else if (forward < 0.0) {
                    forward = -1.0;
                }
            }
            double motionX = forward * motion * (double)MathHelper.cos((float)Math.toRadians(yaw + 90.0f)) + strafe * motion * (double)MathHelper.sin((float)Math.toRadians(yaw + 90.0f));
            double motionZ = forward * motion * (double)MathHelper.sin((float)Math.toRadians(yaw + 90.0f)) - strafe * motion * (double)MathHelper.cos((float)Math.toRadians(yaw + 90.0f));
            MoveUtils.mc.player.setMotion(motionX, MoveUtils.mc.player.getMotion().y, motionZ);
        }
    }

    public static void setMotion(double motion, CameraUtils player) {
        double forward = player.movementInput.moveForward;
        double strafe = player.movementInput.moveStrafe;
        float yaw = player.rotationYaw;
        if (forward == 0.0 && strafe == 0.0) {
            player.setMotion(new Vector3d(0.0, player.getMotion().y, 0.0));
        } else {
            if (forward != 0.0) {
                if (strafe > 0.0) {
                    yaw += (float)(forward > 0.0 ? -45 : 45);
                } else if (strafe < 0.0) {
                    yaw += (float)(forward > 0.0 ? 45 : -45);
                }
                strafe = 0.0;
                if (forward > 0.0) {
                    forward = 1.0;
                } else if (forward < 0.0) {
                    forward = -1.0;
                }
            }
            double motionX = forward * motion * (double)MathHelper.cos((float)Math.toRadians(yaw + 90.0f)) + strafe * motion * (double)MathHelper.sin((float)Math.toRadians(yaw + 90.0f));
            double motionZ = forward * motion * (double)MathHelper.sin((float)Math.toRadians(yaw + 90.0f)) - strafe * motion * (double)MathHelper.cos((float)Math.toRadians(yaw + 90.0f));
            player.setMotion(new Vector3d(motionX, player.getMotion().y, motionZ));
        }
    }

    @Generated
    private MoveUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
