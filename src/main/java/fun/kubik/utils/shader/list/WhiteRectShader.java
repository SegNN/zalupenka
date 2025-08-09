/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.utils.shader.list;

import com.mojang.blaze3d.systems.IRenderCall;
import fun.kubik.helpers.visual.StencilHelpers;
import fun.kubik.helpers.visual.VisualHelpers;
import fun.kubik.utils.shader.AbstractShader;
import java.util.concurrent.ConcurrentLinkedQueue;

public class WhiteRectShader
extends AbstractShader {
    @Override
    public void run(float partialTicks, ConcurrentLinkedQueue<IRenderCall> runnable) {
        if (mc.getMainWindow().isClosed()) {
            return;
        }
        this.setActive(this.isActive() || !runnable.isEmpty());
        if (this.isActive()) {
            StencilHelpers.init();
            runnable.forEach(IRenderCall::execute);
            StencilHelpers.read(1);
            VisualHelpers.drawRect(0.0f, 0.0f, mc.getMainWindow().getFramebufferWidth(), mc.getMainWindow().getFramebufferHeight(), -1);
            StencilHelpers.uninit();
        }
    }

    @Override
    public void update() {
    }
}

