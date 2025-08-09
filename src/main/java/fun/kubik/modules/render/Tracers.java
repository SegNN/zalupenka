/* Decompiler 38ms, total 219ms, lines 71 */
package fun.kubik.modules.render;

import com.mojang.blaze3d.platform.GlStateManager;
import fun.kubik.Load;
import fun.kubik.events.api.EventHook;
import fun.kubik.events.main.render.EventRender3D.Post;
import fun.kubik.helpers.render.ColorHelpers;
import fun.kubik.managers.module.Module;
import fun.kubik.managers.module.main.Category;
import fun.kubik.managers.module.option.api.Option;
import fun.kubik.managers.module.option.main.SliderOption;
import java.util.Iterator;
import net.minecraft.client.entity.player.RemoteClientPlayerEntity;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.settings.PointOfView;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Vector3d;
import org.lwjgl.opengl.GL11;

public class Tracers extends Module {
    private final SliderOption width = (new SliderOption("Width", 1.0F, 1.0F, 5.0F)).increment(0.1F);

    public Tracers() {
        super("Tracers", Category.RENDER);
        this.settings(new Option[]{this.width});
    }

    @EventHook
    public void render3d(Post event) {
        GL11.glPushMatrix();
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(3042);
        GL11.glLineWidth((Float)this.width.getValue());
        GL11.glDisable(3553);
        GL11.glDisable(2929);
        GL11.glDepthMask(false);
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        Vector3d vec = (new Vector3d(0.0D, 0.0D, 150.0D)).rotatePitch((float)(-Math.toRadians((double)mc.player.rotationPitch))).rotateYaw((float)(-Math.toRadians((double)mc.player.rotationYaw)));
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        float alpha = 1.0F;
        Iterator var5 = mc.world.getPlayers().iterator();

        while(var5.hasNext()) {
            PlayerEntity entity = (PlayerEntity)var5.next();
            if (entity instanceof RemoteClientPlayerEntity && mc.gameSettings.getPointOfView() == PointOfView.FIRST_PERSON) {
                int tracersColor = Load.getInstance().getHooks().getFriendManagers().is(entity.getGameProfile().getName()) ? ColorHelpers.rgba(0, 255, 0, 255) : ColorHelpers.rgba(255, 255, 255, 255);
                double x = entity.lastTickPosX + (entity.getPosX() - entity.lastTickPosX) * (double)mc.getRenderPartialTicks() - mc.getRenderManager().info.getProjectedView().getX();
                double y = entity.lastTickPosY + (entity.getPosY() - entity.lastTickPosY) * (double)mc.getRenderPartialTicks() - mc.getRenderManager().info.getProjectedView().getY();
                double z = entity.lastTickPosZ + (entity.getPosZ() - entity.lastTickPosZ) * (double)mc.getRenderPartialTicks() - mc.getRenderManager().info.getProjectedView().getZ();
                ColorHelpers.setColor(tracersColor);
                bufferBuilder.begin(3, DefaultVertexFormats.POSITION);
                bufferBuilder.pos(vec.x, vec.y, vec.z).endVertex();
                bufferBuilder.pos(x, y, z).endVertex();
                Tessellator.getInstance().draw();
            }
        }

        GL11.glHint(3154, 4352);
        GL11.glDisable(2848);
        GL11.glEnable(3553);
        GL11.glEnable(2929);
        GL11.glDepthMask(true);
        GL11.glDisable(3042);
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glPopMatrix();
    }
}