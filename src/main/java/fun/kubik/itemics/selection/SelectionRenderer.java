/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.itemics.selection;

import com.mojang.blaze3d.matrix.MatrixStack;
import fun.kubik.itemics.Itemics;
import fun.kubik.itemics.api.event.events.RenderEvent;
import fun.kubik.itemics.api.event.listener.AbstractGameEventListener;
import fun.kubik.itemics.api.selection.ISelection;
import fun.kubik.itemics.utils.IRenderer;
import java.awt.Color;
import net.minecraft.util.math.AxisAlignedBB;

public class SelectionRenderer
implements IRenderer,
AbstractGameEventListener {
    public static final double SELECTION_BOX_EXPANSION = 0.005;
    private final SelectionManager manager;

    SelectionRenderer(Itemics itemics, SelectionManager manager) {
        this.manager = manager;
        itemics.getGameEventHandler().registerEventListener(this);
    }

    public static void renderSelections(MatrixStack stack, ISelection[] selections) {
        float opacity = ((Float)SelectionRenderer.settings.selectionOpacity.value).floatValue();
        boolean ignoreDepth = (Boolean)SelectionRenderer.settings.renderSelectionIgnoreDepth.value;
        float lineWidth = ((Float)SelectionRenderer.settings.selectionLineWidth.value).floatValue();
        if (!((Boolean)SelectionRenderer.settings.renderSelection.value).booleanValue()) {
            return;
        }
        IRenderer.startLines((Color)SelectionRenderer.settings.colorSelection.value, opacity, lineWidth, ignoreDepth);
        for (ISelection selection : selections) {
            IRenderer.drawAABB(stack, selection.aabb(), 0.005);
        }
        if (((Boolean)SelectionRenderer.settings.renderSelectionCorners.value).booleanValue()) {
            IRenderer.glColor((Color)SelectionRenderer.settings.colorSelectionPos1.value, opacity);
            for (ISelection selection : selections) {
                IRenderer.drawAABB(stack, new AxisAlignedBB(selection.pos1(), selection.pos1().add(1, 1, 1)));
            }
            IRenderer.glColor((Color)SelectionRenderer.settings.colorSelectionPos2.value, opacity);
            for (ISelection selection : selections) {
                IRenderer.drawAABB(stack, new AxisAlignedBB(selection.pos2(), selection.pos2().add(1, 1, 1)));
            }
        }
        IRenderer.endLines(ignoreDepth);
    }

    @Override
    public void onRenderPass(RenderEvent event) {
        SelectionRenderer.renderSelections(event.getModelViewStack(), this.manager.getSelections());
    }
}

