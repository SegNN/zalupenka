/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.managers.notification;

import com.mojang.blaze3d.matrix.MatrixStack;
import fun.kubik.events.main.render.EventRender2D;
import fun.kubik.helpers.animation.EasingList;
import fun.kubik.helpers.interfaces.IFastAccess;
import fun.kubik.helpers.interfaces.IManager;
import fun.kubik.managers.notification.api.Notification;
import java.util.ArrayList;

public class NotificationManagers
extends ArrayList<Notification>
implements IManager<Notification>,
IFastAccess {
    public void update() {
        for (Notification notification : this) {
            boolean update = notification.getOldTime() + notification.getTime() >= System.currentTimeMillis();
            notification.getAnimation().update(update);
            if (notification.getAnimation().getPrevValue() != 0.0f || notification.getAnimation().getValue() != 0.0f || update) continue;
            this.remove(notification);
        }
    }

    public void render(EventRender2D.Pre event) {
        MatrixStack matrixStack = new MatrixStack();
        float offset = 0.0f;
        for (Notification notification : this) {
            notification.getAnimation().animate(0.0f, 1.0f, 0.15f, EasingList.EASE_IN_OUT_CUBIC, NotificationManagers.mc.getTimer().renderPartialTicks);
            float x = (float)mc.getMainWindow().getWidth() / 2.0f - notification.getWidth() / 2.0f;
            float y = (float)mc.getMainWindow().getHeight() / 2.0f;
            notification.setX(x);
            notification.setY(y + (offset += (notification.getHeight() + 1.0f) * notification.getAnimation().getAnimationValue()));
            notification.setAlpha(notification.getAnimation().getAnimationValue());
            notification.render(event);
        }
    }

    @Override
    public void register(Notification notification) {
        this.add(notification);
    }
}

