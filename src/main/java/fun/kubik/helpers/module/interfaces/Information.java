/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.helpers.module.interfaces;

import com.mojang.blaze3d.matrix.MatrixStack;
import fun.kubik.Load;
import fun.kubik.events.main.EventUpdate;
import fun.kubik.events.main.render.EventRender2D;
import fun.kubik.helpers.animation.Animation;
import fun.kubik.helpers.animation.EasingList;
import fun.kubik.helpers.render.ColorHelpers;
import fun.kubik.managers.draggable.api.Component;
import fun.kubik.modules.render.Interface;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.client.gui.screen.ChatScreen;

public class Information
        extends Component {
    private final Animation chatOpenAnimation = new Animation(); // Анимация подъема при открытии чата
    
    public Information() {
        super("Information", new Vector2f(350.0f, 46.0f), 145.0f, 66.0f);
        this.getDraggableOption().settings(this.getDesign(), this.getCompression());

    }

    @Override
    public void update(EventUpdate event) {
        boolean show = ((Interface)Load.getInstance().getHooks().getModuleManagers().findClass(Interface.class)).getElements().getSelected("Information") && ((Interface)Load.getInstance().getHooks().getModuleManagers().findClass(Interface.class)).isToggled();
        this.getShowAnimation().update(show);

        // Анимация подъема при открытии чата
        boolean chatOpen = Information.mc.currentScreen instanceof ChatScreen;
        this.chatOpenAnimation.update(chatOpen);
    }
    @Override
    public void render(EventRender2D.Pre event) {
        MatrixStack matrixStack = event.getMatrixStack();
        this.getShowAnimation().animate(0.0f, 1.0f, 0.2f, EasingList.CIRC_OUT, Information.mc.getTimer().renderPartialTicks);
        
        if (this.getShowAnimation().getAnimationValue() > 0.1) {
            // 📍 ПОЗИЦИЯ с плавной анимацией подъема при открытии чата
            float x = 10.0f; // Левый край
            float baseY = Information.mc.getMainWindow().getHeight() - 60.0f; // Базовая позицияя
            
            // 🔄 ПЛАВНАЯ анимация подъема при открытии чата
            this.chatOpenAnimation.animate(0.0f, 1.0f, 0.15f, EasingList.CIRC_OUT, Information.mc.getTimer().renderPartialTicks);
            float offsetY = -20.0f * this.chatOpenAnimation.getAnimationValue(); // Плавно поднимаем на 40px
            float y = baseY + offsetY;
            
            // Подготавливаем данные: разделяем переливающиеся слова от чисел
            String ticksValue = String.format("%.1f", Information.mc.getTimer().speed * 20.0f);
            String bpsValue = String.format("%.2f", Interface.calculateBPS());
            
            // Координаты
            int worldX = (int)Information.mc.player.getPosX();
            int worldY = (int)Information.mc.player.getPosY();
            int worldZ = (int)Information.mc.player.getPosZ();
            
            // Координаты ада (деление на 8)
            int netherX = worldX / 8;
            int netherZ = worldZ / 8;
            
            String coordsMain = " " + worldX + ", " + worldY + ", " + worldZ + " ";
            String coordsNether = "(" + netherX + "," + worldY + "," + netherZ + ")";
            
            // Цвета
            int whiteColor = ColorHelpers.rgba(255, 255, 255, (int)(255.0f * this.getShowAnimation().getAnimationValue())); // Белый для значений
            int greenColor = ColorHelpers.rgba(100, 255, 100, (int)(255.0f * this.getShowAnimation().getAnimationValue()));
            int redColor = ColorHelpers.rgba(255, 100, 100, (int)(255.0f * this.getShowAnimation().getAnimationValue()));
            
            // Увеличиваем размер шрифта
            float fontSize = 14.0f;
            float lineHeight = 16.0f;
            float time = (float)(System.currentTimeMillis() % 2000) / 2000.0f;
            
            // 🌈 ПЕРЕЛИВАЮЩИЙСЯ "TICKS" + обычное число
            float currentX = x;
            float currentY = y;
            String ticksLabel = "Ticks";
            
            for (int i = 0; i < ticksLabel.length(); i++) {
                float wave = (float)Math.sin(time * Math.PI * 2 + i * 0.3f) * 0.5f + 0.5f;
                int charColor = ColorHelpers.rgba(
                    (int)(ColorHelpers.getRed(ColorHelpers.getThemeColor(1)) * (1 - wave) + ColorHelpers.getRed(ColorHelpers.getThemeColor(2)) * wave),
                    (int)(ColorHelpers.getGreen(ColorHelpers.getThemeColor(1)) * (1 - wave) + ColorHelpers.getGreen(ColorHelpers.getThemeColor(2)) * wave),
                    (int)(ColorHelpers.getBlue(ColorHelpers.getThemeColor(1)) * (1 - wave) + ColorHelpers.getBlue(ColorHelpers.getThemeColor(2)) * wave),
                    255
                );
                sf_semibold.drawText(matrixStack, String.valueOf(ticksLabel.charAt(i)), currentX, currentY, ColorHelpers.setAlpha(charColor, (int)(255.0f * this.getShowAnimation().getAnimationValue())), fontSize);
                currentX += sf_semibold.getWidth(String.valueOf(ticksLabel.charAt(i)), fontSize);
            }
            // Добавляем значение TICKS белым цветом
            sf_semibold.drawText(matrixStack, " " + ticksValue, currentX, currentY, whiteColor, fontSize);
            
            // 🌈 ПЕРЕЛИВАЮЩИЙСЯ "BPS" + обычное число
            currentX = x;
            currentY = y + lineHeight;
            String bpsLabel = "BPS";
            
            for (int i = 0; i < bpsLabel.length(); i++) {
                float wave = (float)Math.sin(time * Math.PI * 2 + i * 0.3f + 1.0f) * 0.5f + 0.5f; // Смещение фазы
                int charColor = ColorHelpers.rgba(
                    (int)(ColorHelpers.getRed(ColorHelpers.getThemeColor(1)) * (1 - wave) + ColorHelpers.getRed(ColorHelpers.getThemeColor(2)) * wave),
                    (int)(ColorHelpers.getGreen(ColorHelpers.getThemeColor(1)) * (1 - wave) + ColorHelpers.getGreen(ColorHelpers.getThemeColor(2)) * wave),
                    (int)(ColorHelpers.getBlue(ColorHelpers.getThemeColor(1)) * (1 - wave) + ColorHelpers.getBlue(ColorHelpers.getThemeColor(2)) * wave),
                    255
                );
                sf_semibold.drawText(matrixStack, String.valueOf(bpsLabel.charAt(i)), currentX, currentY, ColorHelpers.setAlpha(charColor, (int)(255.0f * this.getShowAnimation().getAnimationValue())), fontSize);
                currentX += sf_semibold.getWidth(String.valueOf(bpsLabel.charAt(i)), fontSize);
            }
            // Добавляем значение BPS белым цветом
            sf_semibold.drawText(matrixStack, " " + bpsValue, currentX, currentY, whiteColor, fontSize);
            
            // 🌈 ПЕРЕЛИВАЮЩИЙСЯ "XYZ" + 🟢 зеленые координаты + 🔴 красные координаты ада
            currentX = x;
            currentY = y + lineHeight * 2;
            String xyzLabel = "XYZ";
            
            for (int i = 0; i < xyzLabel.length(); i++) {
                float wave = (float)Math.sin(time * Math.PI * 2 + i * 0.3f + 2.0f) * 0.5f + 0.5f; // Ещё одно смещение фазы
                int charColor = ColorHelpers.rgba(
                    (int)(ColorHelpers.getRed(ColorHelpers.getThemeColor(1)) * (1 - wave) + ColorHelpers.getRed(ColorHelpers.getThemeColor(2)) * wave),
                    (int)(ColorHelpers.getGreen(ColorHelpers.getThemeColor(1)) * (1 - wave) + ColorHelpers.getGreen(ColorHelpers.getThemeColor(2)) * wave),
                    (int)(ColorHelpers.getBlue(ColorHelpers.getThemeColor(1)) * (1 - wave) + ColorHelpers.getBlue(ColorHelpers.getThemeColor(2)) * wave),
                    255
                );
                sf_semibold.drawText(matrixStack, String.valueOf(xyzLabel.charAt(i)), currentX, currentY, ColorHelpers.setAlpha(charColor, (int)(255.0f * this.getShowAnimation().getAnimationValue())), fontSize);
                currentX += sf_semibold.getWidth(String.valueOf(xyzLabel.charAt(i)), fontSize);
            }
            // Основные координаты зеленым
            sf_semibold.drawText(matrixStack, coordsMain, currentX, currentY, greenColor, fontSize);
            currentX += sf_semibold.getWidth(coordsMain, fontSize);
            // Координаты ада красным
            sf_semibold.drawText(matrixStack, coordsNether, currentX, currentY, redColor, fontSize);
            
            // Устанавливаем размеры для drag-and-drop (хотя это статичная позиция)
            this.getDraggableOption().setWidth(200.0f);
            this.getDraggableOption().setHeight(50.0f);
        }
    }
}