
package fun.kubik.modules.render;

import fun.kubik.events.api.EventHook;
import fun.kubik.events.main.render.EventRender2D;
import fun.kubik.helpers.render.ColorHelpers;
import fun.kubik.helpers.render.ScreenHelpers;
import fun.kubik.helpers.visual.VisualHelpers;
import fun.kubik.managers.module.Module;
import fun.kubik.managers.module.main.Category;
import fun.kubik.managers.module.option.main.CheckboxOption;
import fun.kubik.utils.math.MathUtils;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import org.lwjgl.opengl.GL11;
import java.util.HashMap;
import java.util.Map;

public class FireworkESP extends Module {
    private final HashMap<Integer, Vector3d> fireworkPositions = new HashMap<>();
    private final CheckboxOption renderIcon = new CheckboxOption("Render Icon", true);
    private final float size = 18.0f; // Фиксированный размер

    public FireworkESP() {
        super("FireworkESP", Category.RENDER);
        this.settings(renderIcon);
    }

    @EventHook
    public void render(EventRender2D.Post eventRender2D) {
        if (mc.world == null) {
            return;
        }

        // Очистка позиций
        fireworkPositions.clear();

        // Сбор позиций фейерверков
        for (Entity entity : mc.world.getAllEntities()) {
            if (entity instanceof FireworkRocketEntity && isValid(entity)) {
                int entityId = entity.getEntityId();
                double x = MathUtils.interpolate(entity.getPosX(), entity.lastTickPosX, eventRender2D.getPartialTicks());
                double y = MathUtils.interpolate(entity.getPosY(), entity.lastTickPosY, eventRender2D.getPartialTicks());
                double z = MathUtils.interpolate(entity.getPosZ(), entity.lastTickPosZ, eventRender2D.getPartialTicks());
                fireworkPositions.put(entityId, new Vector3d(x, y + 0.6, z));
            }
        }

        // Отрисовка
        for (Map.Entry<Integer, Vector3d> entry : fireworkPositions.entrySet()) {
            int entityId = entry.getKey();
            Vector3d pos = entry.getValue();
            Vector2f screenPos = ScreenHelpers.worldToScreen(pos.x, pos.y, pos.z);
            if (screenPos == null) continue;

            float x = screenPos.x;
            float y = screenPos.y;

            GL11.glPushMatrix();
            VisualHelpers.drawRoundedRect(x - size / 2.0f, y - size / 2.0f, size, size, 8.0f, ColorHelpers.rgba(0, 0, 0, 250));
            if (renderIcon.getValue()) {
                VisualHelpers.drawImage(new ResourceLocation("minecraft:textures/item/firework_rocket.png"),
                        x - size / 1.9f, y - size / 2.0f, size, size, -1);
            }
            GL11.glPopMatrix();
        }
    }

    public boolean isInView(Entity ent) {
        if (mc.getRenderViewEntity() == null) {
            return false;
        }
        WorldRenderer.frustum.setCameraPosition(
                mc.getRenderManager().info.getProjectedView().x,
                mc.getRenderManager().info.getProjectedView().y,
                mc.getRenderManager().info.getProjectedView().z
        );
        return WorldRenderer.frustum.isBoundingBoxInFrustum(ent.getBoundingBox()) || ent.ignoreFrustumCheck;
    }

    public boolean isValid(Entity e) {
        return isInView(e);
    }
}
