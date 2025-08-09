/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.modules.movement;

import fun.kubik.Load;
import fun.kubik.events.api.EventHook;
import fun.kubik.events.main.EventUpdate;
import fun.kubik.managers.module.Module;
import fun.kubik.managers.module.main.Category;
import fun.kubik.modules.player.FreeCam;
import fun.kubik.utils.player.MoveUtils;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;

public class NoWeb
extends Module {
    public NoWeb() {
        super("NoWeb", Category.MOVEMENT);
    }

    @EventHook
    public void update(EventUpdate eventUpdate) {
        if (!NoWeb.mc.player.isSneaking() || !NoWeb.mc.player.isOnGround()) {
            BlockPos aboveHeadPos;
            double z;
            double x;
            boolean headInWeb = false;
            boolean feetInWeb = false;
            for (x = -0.295; x <= 0.295; x += 0.05) {
                block1: for (z = -0.295; z <= 0.295; z += 0.05) {
                    for (double y = (double)NoWeb.mc.player.getEyeHeight(); y >= 1.0; y -= 0.1) {
                        BlockPos headPos = new BlockPos(NoWeb.mc.player.getPosX() + x, NoWeb.mc.player.getPosY() + y, NoWeb.mc.player.getPosZ() + z);
                        if (NoWeb.mc.world.getBlockState(headPos).getBlock() != Blocks.COBWEB) continue;
                        headInWeb = true;
                        continue block1;
                    }
                }
            }
            if (!headInWeb) {
                block3: for (x = -0.295; x <= 0.295; x += 0.05) {
                    for (z = -0.295; z <= 0.295; z += 0.05) {
                        BlockPos pos = new BlockPos(NoWeb.mc.player.getPosX() + x, NoWeb.mc.player.getPosY(), NoWeb.mc.player.getPosZ() + z);
                        if (NoWeb.mc.world.getBlockState(pos).getBlock() != Blocks.COBWEB) continue;
                        feetInWeb = true;
                        continue block3;
                    }
                }
            }
            if (!headInWeb && !feetInWeb && NoWeb.mc.world.getBlockState(aboveHeadPos = new BlockPos(NoWeb.mc.player.getPosX(), NoWeb.mc.player.getPosY() + (double)NoWeb.mc.player.getEyeHeight() + (double)0.2f, NoWeb.mc.player.getPosZ())).getBlock() == Blocks.COBWEB) {
                headInWeb = true;
            }
            if (!((FreeCam)Load.getInstance().getHooks().getModuleManagers().findClass(FreeCam.class)).isToggled() && (headInWeb || feetInWeb)) {
                if (NoWeb.mc.gameSettings.keyBindJump.isKeyDown()) {
                    NoWeb.mc.player.setMotion(new Vector3d(0.0, 0.8000000476837158, 0.0));
                }
                if (!NoWeb.mc.gameSettings.keyBindJump.isKeyDown()) {
                    NoWeb.mc.player.setMotion(new Vector3d(0.0, 0.0, 0.0));
                }
                if (NoWeb.mc.gameSettings.keyBindSneak.isKeyDown()) {
                    NoWeb.mc.player.setMotion(new Vector3d(0.0, -0.8000000476837158, 0.0));
                }
                MoveUtils.setMotion(0.21);
            }
        }
    }
}

