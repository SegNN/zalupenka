/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.ui.screen.panel;

import com.mojang.blaze3d.matrix.MatrixStack;
import fun.kubik.Load;
import fun.kubik.helpers.animation.Animation;
import fun.kubik.helpers.interfaces.IFastAccess;
import fun.kubik.helpers.render.ColorHelpers;
import fun.kubik.helpers.render.GLHelpers;
import fun.kubik.helpers.render.ScreenHelpers;
import fun.kubik.helpers.visual.StencilHelpers;
import fun.kubik.helpers.visual.VisualHelpers;
import fun.kubik.managers.module.Module;
import fun.kubik.managers.module.main.Category;
import fun.kubik.ui.screen.UIScreen;
import fun.kubik.ui.screen.component.Component;
import fun.kubik.ui.screen.component.main.ModuleComponent;
import java.util.ArrayList;
import java.util.List;
import lombok.Generated;
import lombok.NonNull;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector4f;
import fun.kubik.modules.render.ClickGui;

public class Panel
extends Component
implements IFastAccess {
    private final Category category;
    private List<ModuleComponent> modules = new ArrayList<ModuleComponent>();
    private float scrolling = 0.0f;
    private float scrollingOut;

    public Panel(float x, float y, float width, float height, UIScreen parent, Category category) {
        super(x, y, width, height, parent);
        this.category = category;
        for (Module module : Load.getInstance().getHooks().getModuleManagers()) {
            if (module.getCategory() != category) continue;
            ModuleComponent component = new ModuleComponent(x, y, width, height, parent, module);
            this.modules.add(component);
        }
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.scrollingOut = Animation.animate(this.scrollingOut, this.scrolling);
        int back = ColorHelpers.rgba(15, 15, 15, getPanelAlpha());
        VisualHelpers.drawRoundedRect(matrixStack, this.x, this.y, this.width, this.height, 12.0f, back);
        VisualHelpers.drawRoundedOutline(matrixStack, this.x, this.y, this.width, this.height, 12.0f, 1.0f, ColorHelpers.rgba(190, 190, 190, 15.299999999999999));
        int glow = ColorHelpers.getColorWithAlpha(ColorHelpers.getThemeColor(1), 61.199999999999996);
        StencilHelpers.init();
        VisualHelpers.drawRoundedRect(matrixStack, this.x, this.y, this.width, this.height, 12.0f, -1);
        StencilHelpers.read(1);
        float h = 96.0f;
        VisualHelpers.drawGlow(matrixStack, this.x, this.y, this.width, h, 20.0f, glow);
        StencilHelpers.uninit();
        VisualHelpers.drawImage(matrixStack, this.category.getPath(), this.x + this.width / 2.0f - 14.0f - suisse_intl.getWidth(this.category.getName(), 16.0f) / 2.0f, this.y + 16.0f, 16.0f, 16.0f, ColorHelpers.getThemeColor(1));
        suisse_intl.drawCenteredText(matrixStack, this.category.getName(), this.x + this.width / 2.0f + 6.0f, this.y + 16.0f, ColorHelpers.rgba(255, 255, 255, 255), 16.0f);
        this.drawModules(matrixStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public void exit() {
        for (ModuleComponent component : this.modules) {
            component.exit();
        }
    }

    private void drawModules(@NonNull MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        if (stack == null) {
            throw new NullPointerException("stack is marked non-null but is null");
        }
        float offset = 0.0f;
        float scrollOffset = this.scrollingOut;
        StencilHelpers.init();
        VisualHelpers.drawRoundedRect(stack, this.x, this.y + 51.0f, this.width, this.height - 51.0f, new Vector4f(12.0f, 0.0f, 12.0f, 0.0f), -1);
        StencilHelpers.read(1);
        for (ModuleComponent component : this.modules) {
            if (!component.getModule().getName().toLowerCase().contains(Load.getInstance().getUiScreen().getTextSearch().toLowerCase())) continue;
            component.setX(this.x + 8.0f);
            component.setY(this.y + 51.0f + offset + scrollOffset);
            component.setHeight(32.0f);
            component.setWidth(this.getWidth() - 16.0f);
            Vector2f fixedMouse = GLHelpers.INSTANCE.normalizeCords(mouseX, mouseY, 1.0);
            component.renderWithCursorLogic(stack, (int)fixedMouse.x, (int)fixedMouse.y, partialTicks);
            offset += component.getHeight() + 8.0f;
        }
        StencilHelpers.uninit();
        this.scrolling = offset < this.height - 51.0f ? 0.0f : MathHelper.clamp(this.scrolling, -(offset - this.height + 51.0f), 0.0f);
    }

    @Override
    public void tick() {
        for (ModuleComponent component : this.modules) {
            if (!component.getModule().getName().toLowerCase().contains(Load.getInstance().getUiScreen().getTextSearch().toLowerCase())) continue;
            component.tick();
        }
    }

    @Override
    public void keyPressed(int keyCode, int scanCode, int modifiers) {
        for (ModuleComponent component : this.modules) {
            if (!component.getModule().getName().toLowerCase().contains(Load.getInstance().getUiScreen().getTextSearch().toLowerCase())) continue;
            component.keyPressed(keyCode, scanCode, modifiers);
        }
    }

    @Override
    public void keyReleased(int keyCode, int scanCode, int modifiers) {
    }

    @Override
    public void charTyped(char codePoint, int modifiers) {
        for (ModuleComponent component : this.modules) {
            if (!component.getModule().getName().toLowerCase().contains(Load.getInstance().getUiScreen().getTextSearch().toLowerCase())) continue;
            component.charTyped(codePoint, modifiers);
        }
    }

    @Override
    public void mouseClicked(double mouseX, double mouseY, int button) {
        for (ModuleComponent component : this.modules) {
            if (!component.getModule().getName().toLowerCase().contains(Load.getInstance().getUiScreen().getTextSearch().toLowerCase())) continue;
            component.mouseClicked(mouseX, mouseY, button);
        }
    }

    @Override
    public void mouseReleased(double mouseX, double mouseY, int button) {
        for (ModuleComponent component : this.modules) {
            if (!component.getModule().getName().toLowerCase().contains(Load.getInstance().getUiScreen().getTextSearch().toLowerCase())) continue;
            component.mouseReleased(mouseX, mouseY, button);
        }
    }

    @Override
    public void mouseScrolled(double mouseX, double mouseY, double delta) {
        if (ScreenHelpers.isHovered(mouseX, mouseY, this.x, this.y + 50.0f, this.width, this.height - 50.0f)) {
            this.scrolling += (float)(delta * 30.0);
        }
    }

    @Override
    public void translate() {
        for (ModuleComponent component : this.modules) {
            component.translate();
        }
    }

    @Generated
    public Category getCategory() {
        return this.category;
    }

    @Generated
    public List<ModuleComponent> getModules() {
        return this.modules;
    }

    @Generated
    public float getScrolling() {
        return this.scrolling;
    }

    @Generated
    public float getScrollingOut() {
        return this.scrollingOut;
    }

    private int getPanelAlpha() {
        UIScreen parentScreen = (UIScreen) this.parent;
        ClickGui clickGui = (ClickGui) Load.getInstance().getHooks().getModuleManagers().findClass(ClickGui.class);
        if (clickGui != null && clickGui.panelDesign.getSelected("Transparent")) {
            return 100;
        }
        return 255;
    }
}

