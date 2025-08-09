package fun.kubik.helpers.module.interfaces;

import com.mojang.blaze3d.matrix.MatrixStack;
import fun.kubik.events.main.render.EventRender2D;
import fun.kubik.helpers.render.ColorHelpers;
import fun.kubik.helpers.visual.VisualHelpers;
import fun.kubik.modules.combat.ElytraTarget;
import fun.kubik.modules.render.Interface;


import static fun.kubik.helpers.interfaces.IFastAccess.mc;
import static fun.kubik.helpers.interfaces.IFont.suisse_intl;

public class AlwaNotif {


    private float animationValue = 0.0f;

    public void renderNotification(EventRender2D.Pre event, ElytraTarget elytraTarget) {
        if (elytraTarget == null || !elytraTarget.isToggled()) return;

        MatrixStack matrixStack = event.getMatrixStack();
        double bps = Interface.calculateBPS();
        boolean isTurboEnabled = isTurboEnabled(elytraTarget);

        boolean shouldShow = bps > 38.0;

        if (this.animationValue <= 0.01f) return;

        String bpsText = String.format("%.1f BPS", bps);
        String warningText = "TURBO ENABLED";

        float bpsTextWidth = suisse_intl.getWidth(bpsText, 12.0f);
        float warningTextWidth = isTurboEnabled ? suisse_intl.getWidth(warningText, 12.0f) : 0;
        float maxWidth = Math.max(bpsTextWidth, warningTextWidth);

        float rectWidth = maxWidth + 20.0f;
        float rectHeight = isTurboEnabled ? 35.0f : 25.0f;

        float screenWidth = mc.getMainWindow().getScaledWidth();
        float x = (screenWidth - rectWidth) / 2.0f;
        float y = rectHeight - 50.0f;

        int alpha = (int)(255.0f * this.animationValue);
        int backColor = ColorHelpers.rgba(15, 15, 15, alpha);
        int borderColor = ColorHelpers.rgba(190, 190, 190, (int)(15.3f * this.animationValue));
        int textColor = ColorHelpers.rgba(255, 255, 255, alpha);
        int warningColor = ColorHelpers.rgba(255, 50, 50, alpha);

        if (this.animationValue > 0.1f) {
            VisualHelpers.drawRoundedRect(matrixStack, x, y, rectWidth, rectHeight, 8.0f, backColor);
            VisualHelpers.drawRoundedOutline(matrixStack, x, y, rectWidth, rectHeight, 8.0f, 1.5f, borderColor);

            if (isTurboEnabled) {
                suisse_intl.drawText(matrixStack, warningText,
                        x + (rectWidth - warningTextWidth) / 2.0f,
                        y + 5.0f,
                        warningColor, 12.0f);
            }

            suisse_intl.drawText(matrixStack, bpsText,
                    x + (rectWidth - bpsTextWidth) / 2.0f,
                    isTurboEnabled ? y + 20.0f : y + 6.0f,
                    textColor, 12.0f);
        }
    }

    private boolean isTurboEnabled(ElytraTarget elytraTarget) {
        try {
            return elytraTarget.getPredictOption().getSelected("Elytra Predict");
        } catch (Exception e) {
            return false;
        }
    }
}