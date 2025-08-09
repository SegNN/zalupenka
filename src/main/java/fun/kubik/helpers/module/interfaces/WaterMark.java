/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.helpers.module.interfaces;

import com.mojang.blaze3d.matrix.MatrixStack;
import fun.kubik.Load;
import fun.kubik.events.main.EventUpdate;
import fun.kubik.events.main.render.EventRender2D;
import fun.kubik.helpers.animation.EasingList;
import fun.kubik.helpers.render.ColorHelpers;
import fun.kubik.helpers.visual.VisualHelpers;
import fun.kubik.managers.draggable.api.Component;
import fun.kubik.managers.module.option.main.CheckboxOption;
import fun.kubik.modules.render.Interface;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.vector.Vector2f;

public class WaterMark
        extends Component {

    // Настройки для показа элементов
    private final CheckboxOption showUsername = new CheckboxOption("Show Username", true);
    private final CheckboxOption showFPS = new CheckboxOption("Show FPS", true);
    private final CheckboxOption showPing = new CheckboxOption("Show Ping", true);
    private final CheckboxOption showCoords = new CheckboxOption("Show Coordinates", true);

    public WaterMark() {
        super("WaterMark", new Vector2f(100.0f, 46.0f), 145.0f, 66.0f);
        this.getDraggableOption().settings(this.getDesign(), this.getCompression(),
                this.showUsername, this.showFPS, this.showPing, this.showCoords);
    }

    @Override
    public void update(EventUpdate event) {
        boolean show = ((Interface)Load.getInstance().getHooks().getModuleManagers().findClass(Interface.class)).getElements().getSelected("WaterMark") && ((Interface)Load.getInstance().getHooks().getModuleManagers().findClass(Interface.class)).isToggled();
        this.getShowAnimation().update(show);
    }

    @Override
    public void render(EventRender2D.Pre event) {
        MatrixStack matrixStack = event.getMatrixStack();
        this.getShowAnimation().animate(0.0f, 1.0f, 0.2f, EasingList.CIRC_OUT, WaterMark.mc.getTimer().renderPartialTicks);

        float x = ((Vector2f)this.getDraggableOption().getValue()).x;
        float y = ((Vector2f)this.getDraggableOption().getValue()).y;
        float height = 32.0f;

        this.drawRect(matrixStack, x, y, 0, height); // width будет вычислена в drawRect

        this.getDraggableOption().setWidth(200.0f); // Примерная ширина
        this.getDraggableOption().setHeight(24.0f); // Высота как в drawRect
    }

    private void drawRect(MatrixStack matrixStack, float x, float y, float width, float height) {
        if (this.getShowAnimation().getAnimationValue() > 0.1) {
            String title = "KubikClient";
            int ping = mc.getConnection() != null && mc.player != null && mc.getConnection().getPlayerInfo(mc.player.getUniqueID()) != null ? mc.getConnection().getPlayerInfo(mc.player.getUniqueID()).getResponseTime() : 0;
            int fps = Minecraft.debugFPS;
            String fpsText = fps + " fps";
            String pingText = ping + " ping";

            // 🎨 Уменьшаем размеры: меньший шрифт (12px вместо 16px) и высота (24px вместо 30px) 
            float fontSize = 12.0f;
            float titleWidth = suisse_intl.getWidth(title, fontSize);
            float fpsWidth = suisse_intl.getWidth(fpsText, fontSize);
            float pingWidth = suisse_intl.getWidth(pingText, fontSize);

            // Кружочки между элементами: KubikClient • fps • ping (БЕЗ кружочка в конце)
            float circleSpacing = 8.0f;
            float circleRadius = 2.0f;
            float totalWidth = titleWidth + circleSpacing*2 + fpsWidth + circleSpacing*2 + pingWidth + 16.0f;

            // 🎨 ДИЗАЙН как KeyBinds с свечением темы
            float totalHeight = 24.0f;

            // Свечение как в KeyBinds: blur radius 12, alpha 100.0f
            int glowColor = ColorHelpers.getColorWithAlpha(ColorHelpers.getThemeColor(1), 100.0f * this.getShowAnimation().getAnimationValue());
            VisualHelpers.drawShadow(x - 2, y - 2, totalWidth + 4, totalHeight + 4, 12, glowColor);

            // Один прямоугольник с градиентом (черный в темно-серый)
            int headerColor = ColorHelpers.rgba(0, 0, 0, (int)(255.0f * this.getShowAnimation().getAnimationValue()));
            int bindsColor = ColorHelpers.rgba(7, 7, 7, (int)(255.0f * this.getShowAnimation().getAnimationValue()));
            VisualHelpers.drawRoundedRect(x, y, totalWidth, totalHeight, 6.0f, headerColor, headerColor, bindsColor, bindsColor);

            // Цвета
            int textColor = ColorHelpers.rgba(255, 255, 255, (int)(255.0f * this.getShowAnimation().getAnimationValue()));
            int circleColor = ColorHelpers.getColorWithAlpha(ColorHelpers.getThemeColor(1), 255.0f * this.getShowAnimation().getAnimationValue());

            float currentX = x + 8;
            float textY = y + (totalHeight - fontSize) / 2; // Центрируем по вертикали

            // Переливающийся KubikClient
            float time = (float)(System.currentTimeMillis() % 2000) / 2000.0f;
            for (int i = 0; i < title.length(); i++) {
                float wave = (float)Math.sin(time * Math.PI * 2 + i * 0.5f) * 0.5f + 0.5f;
                int charColor = ColorHelpers.rgba(
                        (int)(ColorHelpers.getRed(ColorHelpers.getThemeColor(1)) * (1 - wave) + ColorHelpers.getRed(ColorHelpers.getThemeColor(2)) * wave),
                        (int)(ColorHelpers.getGreen(ColorHelpers.getThemeColor(1)) * (1 - wave) + ColorHelpers.getGreen(ColorHelpers.getThemeColor(2)) * wave),
                        (int)(ColorHelpers.getBlue(ColorHelpers.getThemeColor(1)) * (1 - wave) + ColorHelpers.getBlue(ColorHelpers.getThemeColor(2)) * wave),
                        255
                );
                suisse_intl.drawText(matrixStack, String.valueOf(title.charAt(i)), currentX, textY, ColorHelpers.setAlpha(charColor, (int)(255.0f * this.getShowAnimation().getAnimationValue())), fontSize);
                currentX += suisse_intl.getWidth(String.valueOf(title.charAt(i)), fontSize);
            }

            // 🔵 Первый кружочек после KubikClient
            currentX += circleSpacing;
            VisualHelpers.drawFilledCircleNoGL((int)currentX, (int)(y + totalHeight / 2), circleRadius, circleColor, 8);
            currentX += circleSpacing;

            // FPS (белый текст)
            suisse_intl.drawText(matrixStack, fpsText, currentX, textY, textColor, fontSize);
            currentX += fpsWidth;

            // 🔵 Второй кружочек после fps
            currentX += circleSpacing;
            VisualHelpers.drawFilledCircleNoGL((int)currentX, (int)(y + totalHeight / 2), circleRadius, circleColor, 8);
            currentX += circleSpacing;

            // Ping (белый текст) - БЕЗ кружочка после
            suisse_intl.drawText(matrixStack, pingText, currentX, textY, textColor, fontSize);
        }
    }
}