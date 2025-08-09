package fun.kubik.helpers.module.interfaces;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import fun.kubik.Load;
import fun.kubik.events.main.EventUpdate;
import fun.kubik.events.main.render.EventRender2D;
import fun.kubik.helpers.render.ColorHelpers;
import fun.kubik.managers.draggable.api.Component;
import fun.kubik.modules.render.Interface;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Vector2f;

import java.util.ArrayList;
import java.util.List;

public class ArmorHud extends Component {
    private static final Minecraft mc = Minecraft.getInstance();
    
    public ArmorHud() {
        super("ArmorHud", new Vector2f(0.0f, 0.0f), 22.0f, 88.0f);
        // ArmorHud статичен - позиция вычисляется динамически
    }

    @Override
    public void update(EventUpdate event) {
        Interface interfaceModule = Load.getInstance().getHooks().getModuleManagers().findClass(Interface.class);
        boolean show = interfaceModule.getElements().getSelected("ArmorHud") && interfaceModule.isToggled();
        this.getShowAnimation().update(show);
    }

    @Override
    public void render(EventRender2D.Pre event) {
        if (mc.player == null) return;

        MatrixStack matrixStack = event.getMatrixStack();

        // Получаем позицию для drag-and-drop
        Vector2f position = (Vector2f) this.getDraggableOption().getValue();
        float x = position.x;
        float y = position.y;

        // Получаем броню (слева направо: ботинки, штаны, нагрудник, шлем)
        List<ItemStack> armorItems = new ArrayList<>();
        mc.player.getArmorInventoryList().forEach(armorItems::add);

        float slotSize = 32.0f;
        float spacing = 6.0f;

        for (int i = 0; i < 4; i++) {
            float slotX = x + i * (slotSize + spacing);
            float slotY = y;

            if (i < armorItems.size()) {
                ItemStack armorItem = armorItems.get(i);
                if (!armorItem.isEmpty()) {
                    // Рисуем предмет
                    this.drawItemStack(armorItem, slotX, slotY, 2.0f);

                    // Процент прочности
                    if (armorItem.isDamageable()) {
                        int maxDamage = armorItem.getMaxDamage();
                        int currentDamage = armorItem.getDamage();
                        int durabilityPercent = (int)((float)(maxDamage - currentDamage) / maxDamage * 100);
                        String durabilityText = durabilityPercent + "%";
                        int greenColor = ColorHelpers.rgba(100, 255, 100, 255);
                        suisse_intl.drawText(matrixStack, durabilityText,
                            slotX + (slotSize - suisse_intl.getWidth(durabilityText, 11.0f)) / 2,
                            slotY - 12.0f, greenColor, 12.0f);
                    }
                }
            }
        }
        // Обновляем размеры для drag-and-drop
        this.getDraggableOption().setWidth(slotSize * 4 + spacing * 3);
        this.getDraggableOption().setHeight(slotSize);
    }
    
    /**
     * 🎨 Отрисовка предмета брони в слоте
     */
    private void drawItemStack(ItemStack stack, float x, float y, float scale) {
        if (stack.isEmpty()) return;
        
        RenderSystem.pushMatrix();
        RenderSystem.translatef(x, y, 0.0f);
        RenderSystem.scalef(scale, scale, scale);
        
        // Отрисовка предмета
        mc.getItemRenderer().renderItemAndEffectIntoGUI(stack, 0, 0);
        mc.getItemRenderer().renderItemOverlays(mc.fontRenderer, stack, 0, 0);
        
        RenderSystem.popMatrix();
    }
}