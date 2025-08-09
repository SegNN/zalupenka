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
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector4f;
import org.lwjgl.opengl.GL11;

public class ItemESP
        extends Module {
    private final HashMap<Entity, Vector4f> positions = new HashMap();
    private final CheckboxOption renderIcon = new CheckboxOption("Render Icon", false);

    public ItemESP() {
        super("ItemESP", Category.RENDER);
        this.settings(this.renderIcon);
    }

    @EventHook
    public void render(EventRender2D.Post eventRender2D) {
        if (ItemESP.mc.world == null) {
            return;
        }
        this.positions.clear();
        for (Entity entity : ItemESP.mc.world.getAllEntities()) {
            if (!this.isValid(entity) || !(entity instanceof ItemEntity)) continue;
            double x = MathUtils.interpolate(entity.getPosX(), entity.lastTickPosX, (double)eventRender2D.getPartialTicks());
            double y = MathUtils.interpolate(entity.getPosY(), entity.lastTickPosY, (double)eventRender2D.getPartialTicks());
            double z = MathUtils.interpolate(entity.getPosZ(), entity.lastTickPosZ, (double)eventRender2D.getPartialTicks());
            Vector3d size = new Vector3d(entity.getBoundingBox().maxX - entity.getBoundingBox().minX, entity.getBoundingBox().maxY - entity.getBoundingBox().minY, entity.getBoundingBox().maxZ - entity.getBoundingBox().minZ);
            AxisAlignedBB aabb = new AxisAlignedBB(x - size.x / 2.0, y, z - size.z / 2.0, x + size.x / 2.0, y + size.y, z + size.z / 2.0);
            Vector4f position = null;
            for (int i = 0; i < 8; ++i) {
                Vector2f vector = ScreenHelpers.worldToScreen(i % 2 == 0 ? aabb.minX : aabb.maxX, i / 2 % 2 == 0 ? aabb.minY : aabb.maxY, i / 4 % 2 == 0 ? aabb.minZ : aabb.maxZ);
                if (position == null) {
                    position = new Vector4f(vector.x, vector.y, 1.0f, 1.0f);
                    continue;
                }
                position.x = Math.min(vector.x, position.x);
                position.y = Math.min(vector.y, position.y);
                position.z = Math.max(vector.x, position.z);
                position.w = Math.max(vector.y, position.w);
            }
            this.positions.put(entity, position);
        }
        for (Map.Entry entry : this.positions.entrySet()) {
            Entity entity = (Entity)entry.getKey();
            if (!(entity instanceof ItemEntity)) continue;
            ItemEntity itemEntity = (ItemEntity)entity;
            Vector4f position = (Vector4f)entry.getValue();
            ItemStack itemStack = itemEntity.getItem();
            int itemCount = itemStack.getCount();
            float width = position.z - position.x;
            double x = position.x;
            double y = position.y;
            float length = sf_semibold.getWidth(itemStack.getDisplayName().getString() + (String)(itemCount == 1 ? "" : " x" + itemCount), 13.0f);
            // Определяем цвет на основе редкости предмета
            int color = getRarityColor(itemStack.getRarity());
            GL11.glPushMatrix();
            this.glCenteredScale(position.x + width / 2.0f - length / 2.0f, position.y - 8.0f, length, 10.0f, 0.5f);
            if (((Boolean)this.renderIcon.getValue()).booleanValue()) {
                VisualHelpers.drawRoundedRect((float)(x + (double)(width / 2.0f) - 8.75 - 3.5), (float)(y - 51.5), 26.0f, 26.0f, 0.0f, ColorHelpers.rgba(3, 3, 3, 175));
                MathUtils.scaleElements((float)(x + (double)(width / 2.0f) - 7.0 - 3.5), (float)y, 1.35f, () -> ESP.drawItemStack(new ItemStack(itemStack.getItem()), (float)(x + (double)(width / 2.0f) - 7.0 - 3.5), y - 37.0, null, false));
            }
            VisualHelpers.drawRoundedRect((float)(x + (double)(width / 2.0f) - (double)(length / 2.0f) - 3.5), (float)(y - 26.0), length + 7.0f, 20.5f, 0.0f, ColorHelpers.rgba(10, 10, 10, 140));
            sf_semibold.drawText(eventRender2D.getMatrixStack(), itemStack.getDisplayName().getString(), (float)(x + (double)(width / 2.0f) - (double)(length / 2.0f)), (float)(y - 22.0), color, 13.0f);
            sf_semibold.drawText(eventRender2D.getMatrixStack(), (String)(itemCount == 1 ? "" : " x" + itemCount), (float)(x + (double)(width / 2.0f) - (double)(length / 2.0f) + (double)sf_semibold.getWidth(itemStack.getDisplayName().getString(), 13.0f)), (float)(y - 22.0), 0xFFFFFFFF, 13.0f);
            GL11.glPopMatrix();
        }
    }

    private int getRarityColor(Rarity rarity) {
        switch (rarity) {
            case COMMON:
                return 0xFFFFFFFF; // Белый (TextFormatting.WHITE)
            case UNCOMMON:
                return 0xFFFFFF55; // Желтый (TextFormatting.YELLOW)
            case RARE:
                return 0xFF55FFFF; // Голубой (TextFormatting.AQUA)
            case EPIC:
                return 0xFFAA00AA; // Светло-фиолетовый (TextFormatting.LIGHT_PURPLE)
            default:
                return 0xFFFFFFFF; // Белый по умолчанию
        }
    }

    public boolean isInView(Entity ent) {
        if (mc.getRenderViewEntity() == null) {
            return false;
        }
        WorldRenderer.frustum.setCameraPosition(ItemESP.mc.getRenderManager().info.getProjectedView().x, ItemESP.mc.getRenderManager().info.getProjectedView().y, ItemESP.mc.getRenderManager().info.getProjectedView().z);
        return WorldRenderer.frustum.isBoundingBoxInFrustum(ent.getBoundingBox()) || ent.ignoreFrustumCheck;
    }

    public boolean isValid(Entity e) {
        return this.isInView(e);
    }

    public void glCenteredScale(float x, float y, float w, float h, float f) {
        GL11.glTranslatef(x + w / 2.0f, y + h / 2.0f, 0.0f);
        GL11.glScalef(f, f, 1.0f);
        GL11.glTranslatef(-x - w / 2.0f, -y - h / 2.0f, 0.0f);
    }
}