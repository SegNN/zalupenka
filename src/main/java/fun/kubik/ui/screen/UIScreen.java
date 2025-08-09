package fun.kubik.ui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import fun.kubik.Load;
import fun.kubik.helpers.animation.Animation;
import fun.kubik.helpers.animation.EasingList;
import fun.kubik.helpers.interfaces.IFastAccess;
import fun.kubik.helpers.interfaces.IManager;
import fun.kubik.helpers.interfaces.ITranslate;
import fun.kubik.helpers.render.ColorHelpers;
import fun.kubik.helpers.render.GLHelpers;
import fun.kubik.helpers.render.ScreenHelpers;
import fun.kubik.helpers.visual.StencilHelpers;
import fun.kubik.helpers.visual.VisualHelpers;
import fun.kubik.managers.module.main.Category;
import fun.kubik.modules.render.ClickGui;
import fun.kubik.ui.option.OptionScreen;
import fun.kubik.ui.screen.component.Component;
import fun.kubik.ui.screen.panel.Panel;
import fun.kubik.utils.time.TimerUtils;
import lombok.Generated;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.EditSignScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector4f;
import net.minecraft.util.text.StringTextComponent;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class UIScreen extends Screen implements IFastAccess, ITranslate, IManager<Component> {
    private final List<Component> components = new ArrayList<>();
    private final Animation animation = new Animation();
    private final Animation backgroundAnimation = new Animation();
    private float marginY = 5.0f;
    private float marginX = 5.0f;
    private float lastX;
    private float lastY;
    private final float width = mc.getMainWindow().getWidth();
    private final float height = mc.getMainWindow().getHeight();
    private final float x = this.width / 2.0f;
    private final float y = this.height / 2.0f;
    private boolean isPressed = false;
    private boolean control = false;
    private float scrollingX = 0.0f;
    private float scrollingOutX;
    private String textSearch = "";
    private String ideas;
    private boolean searching = false;
    private final Animation searchAnimation = new Animation();
    private Category category;
    private boolean update = true;
    private final List<Panel> panels = new ArrayList<>();
    private final TimerUtils timer = new TimerUtils();
    private boolean closing = false;

    public UIScreen() {
        super(new StringTextComponent("PoweredByNirficusAndRlezz"));
        for (Category category : Category.values()) {
            this.panels.add(new Panel(this.x, this.y, this.width, this.height, this, category));
        }
    }

    @Override
    public void init() {
    }

    @Override
    public void tick() {
        this.searchAnimation.update(this.searching);
        this.animation.update(this.update);
        this.backgroundAnimation.update(this.update);
        if (UIScreen.mc.player != null) {
            KeyBinding[] pressedKeys = new KeyBinding[]{
                    UIScreen.mc.gameSettings.keyBindForward,
                    UIScreen.mc.gameSettings.keyBindBack,
                    UIScreen.mc.gameSettings.keyBindLeft,
                    UIScreen.mc.gameSettings.keyBindRight,
                    UIScreen.mc.gameSettings.keyBindJump
            };

            if (UIScreen.mc.currentScreen instanceof ChatScreen || UIScreen.mc.currentScreen instanceof EditSignScreen) {
                return;
            }

            if (!this.searching) {
                for (KeyBinding keyBinding : pressedKeys) {
                    boolean isKeyPressed = InputMappings.isKeyDown(mc.getMainWindow().getHandle(), keyBinding.getDefault().getKeyCode());
                    keyBinding.setPressed(isKeyPressed);
                }
            }
        }

        for (Panel panel : this.panels) {
            panel.tick();
        }
    }

    @Override
    public void render(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        ClickGui clickGui = (ClickGui) Load.getInstance().getHooks().getModuleManagers().findClass(ClickGui.class);
        if (clickGui == null) {
            System.out.println("[DEBUG] ClickGui module not found");
            return;
        }

        Vector2f fixedMouse = GLHelpers.INSTANCE.normalizeCords(mouseX, mouseY, 1.0f);
        this.backgroundAnimation.animate(0.0f, 1.0f, 0.125f, EasingList.NONE, mc.getTimer().renderPartialTicks);

        // Render background
        if (clickGui.better.getSelected("Darkening Background")) {
            VisualHelpers.drawRoundedRect(matrixStack, -1000.0f, -1000.0f,
                    mc.getMainWindow().getWidth() + 2000,
                    mc.getMainWindow().getHeight() + 2000,
                    0.0f,
                    ColorHelpers.rgba(0, 0, 0, (int)(150.0f * this.backgroundAnimation.getAnimationValue())));
        }
        if (clickGui.better.getSelected("Colorful Background")) {
            VisualHelpers.drawRoundedGradientRect(matrixStack, -10.0f, -10.0f,
                    mc.getMainWindow().getWidth() + 20,
                    mc.getMainWindow().getHeight() - 100,
                    0.0f,
                    ColorHelpers.rgba(0, 0, 0, 0),
                    ColorHelpers.rgba(0, 0, 0, 0),
                    ColorHelpers.getColorWithAlpha(ColorHelpers.getTheme(45), 255.0f * this.backgroundAnimation.getAnimationValue()),
                    ColorHelpers.getColorWithAlpha(ColorHelpers.getTheme(90), 255.0f * this.backgroundAnimation.getAnimationValue()));
        }

        GLHelpers.INSTANCE.rescale(1.0f);
        this.animation.animate(closing ? 0.0f : 1.0f, closing ? 0.0f : 1.0f, 0.2f, closing ? EasingList.BACK_IN : EasingList.BACK_OUT, mc.getTimer().renderPartialTicks);

        float panelWidth = 200.0f;
        float panelHeight = clickGui.size.getSelected("Big") ? 651.0f : 500.0f;
        float x = mc.getMainWindow().getWidth() / 2.0f - (panelWidth + 8.0f) * Category.values().length / 2.0f;
        float y = mc.getMainWindow().getHeight() / 2.0f - panelHeight / 2.0f;
        this.scrollingOutX = Animation.animate(this.scrollingOutX, this.scrollingX);
        float panelOff = 0.0f;

        // Apply blur only to panels if needed
        if (clickGui.panelDesign.getSelected("Transparent")) {
            BLUR_RUNNABLES.add(() -> {
                for (Panel panel : this.panels) {
                    VisualHelpers.drawRoundedRect(matrixStack, panel.getX(), panel.getY(),
                            panel.getWidth(), panel.getHeight(),
                            12.0f,
                            -1);
                }
            });
            this.blurSetting(partialTicks, 10.0f, clickGui.compression.getValue());
        }

        // Render panels
        for (Panel panel : this.panels) {
            panel.setX(x + panelOff + this.scrollingOutX);
            panel.setY(y);
            panel.setWidth(panelWidth);
            panel.setHeight(panelHeight);

            if (clickGui.panelDesign.getSelected("Transparent")) {
                VisualHelpers.drawRoundedRect(matrixStack, panel.getX(), panel.getY(),
                        panel.getWidth(), panel.getHeight(),
                        12.0f,
                        ColorHelpers.rgba(15, 15, 15, 100));
                VisualHelpers.drawRoundedRect(matrixStack, panel.getX(), panel.getY(),
                        panel.getWidth(), panel.getHeight(),
                        12.0f,
                        ColorHelpers.getColorWithAlpha(ColorHelpers.getThemeColor(1), 40.0f * this.animation.getAnimationValue()));
            } else {
                VisualHelpers.drawRoundedRect(matrixStack, panel.getX(), panel.getY(),
                        panel.getWidth(), panel.getHeight(),
                        12.0f,
                        ColorHelpers.rgba(15, 15, 15, 255));
            }

            panel.render(matrixStack, (int)fixedMouse.x, (int)fixedMouse.y, partialTicks);
            panelOff += panelWidth + 8.0f;
        }

        GLHelpers.INSTANCE.rescaleMC();
        this.renderSetting(matrixStack, (int)fixedMouse.x, (int)fixedMouse.y, partialTicks);
        this.renderSearch(matrixStack, (int)fixedMouse.x, (int)fixedMouse.y, partialTicks);
        this.renderIdea(matrixStack, (int)fixedMouse.x, (int)fixedMouse.y, partialTicks);

        if (this.animation.getPrevValue() == 0.0f && this.animation.getValue() == 0.0f && closing) {
            closing = false;
            this.update = true;
            super.closeScreen();
            return;
        }
    }

    private boolean isMouseOverPanels(float mouseX, float mouseY) {
        ClickGui clickGui = (ClickGui) Load.getInstance().getHooks().getModuleManagers().findClass(ClickGui.class);
        if (clickGui == null) return false;
        float panelWidth = 200.0f;
        float panelHeight = clickGui.size.getSelected("Big") ? 651.0f : 500.0f;
        float x = (float)mc.getMainWindow().getWidth() / 2.0f - (panelWidth + 8.0f) * (float)Category.values().length / 2.0f;
        float y = (float)mc.getMainWindow().getHeight() / 2.0f - panelHeight / 2.0f;
        float totalWidth = (panelWidth + 8.0f) * Category.values().length - 8.0f;
        return ScreenHelpers.isHovered(mouseX, mouseY, x, y, totalWidth, panelHeight);
    }

    private boolean isMouseOverThemeConfig(float mouseX, float mouseY) {
        float width = 200.0f;
        float height = 70.0f;
        float x = (float)mc.getMainWindow().getWidth() - width + 130.0f;
        float settingY = 0.0f;
        return ScreenHelpers.isHovered(mouseX, mouseY, x - this.marginX, settingY, width, height);
    }

    private void renderSetting(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        GLHelpers.INSTANCE.rescale(1.0f);
        this.searchAnimation.animate(0.0f, 1.0f, 0.2f, EasingList.CIRC_OUT, UIScreen.mc.getTimer().renderPartialTicks);
        float width = 200.0f;
        float height = 70.0f;
        float x = (float)mc.getMainWindow().getWidth() - width + 130.0f;
        float settingY = 0.0f;
        boolean hovered = ScreenHelpers.isHovered(mouseX, mouseY, x, settingY, 70.0f, height);
        this.marginX = Animation.animate(this.marginX, hovered ? 130.0f : 0.0f);
        VisualHelpers.drawRoundedRect(matrixStack, x - this.marginX, settingY, width, height, new Vector4f(0.0f, 0.0f, 20.0f, 20.0f), ColorHelpers.rgba(15, 15, 15, 255), ColorHelpers.rgba(5, 5, 5, 255), ColorHelpers.rgba(15, 15, 15, 255), ColorHelpers.rgba(5, 5, 5, 255));
        sf_semibold.drawText(matrixStack, "Theme and Config", x - this.marginX + 10.0f, settingY + height / 2.0f - sf_semibold.getHeight(18.0f) / 2.0f, ColorHelpers.rgba(255, 255, 255, 255), 18.0f);
        GLHelpers.INSTANCE.rescaleMC();
    }

    private void renderSearch(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        GLHelpers.INSTANCE.rescale(1.0f);
        float sWidth = 240.0f;
        float sHeight = 50.0f;
        float xSearch = (float)mc.getMainWindow().getWidth() / 2.0f - sWidth / 2.0f;
        float ySearch = mc.getMainWindow().getHeight();
        float animY = ySearch - (sHeight + 5.0f) * this.searchAnimation.getAnimationValue();
        int back = ColorHelpers.rgba(15, 15, 15, 255.0f * this.searchAnimation.getAnimationValue());
        int outline = ColorHelpers.rgba(190, 190, 190, (int)(15.3 * this.searchAnimation.getAnimationValue()));
        int indicator = ColorHelpers.getColorWithAlpha(ColorHelpers.getThemeColor(1), 255.0f * this.searchAnimation.getAnimationValue());
        int glow = ColorHelpers.getColorWithAlpha(ColorHelpers.getThemeColor(1), (int)(61.2 * this.searchAnimation.getAnimationValue()));
        int text = ColorHelpers.rgba(255, 255, 255, (int)(183.6 * this.searchAnimation.getAnimationValue()));
        VisualHelpers.drawRoundedRect(matrixStack, xSearch, animY, sWidth, sHeight, 12.0f, back);
        VisualHelpers.drawRoundedOutline(matrixStack, xSearch, animY, sWidth, sHeight, 12.0f, 2.0f, outline);
        StencilHelpers.init();
        VisualHelpers.drawRoundedRect(matrixStack, xSearch, animY, sWidth, sHeight, 12.0f, -1);
        StencilHelpers.read(1);
        VisualHelpers.drawGlow(matrixStack, xSearch, animY, sWidth, 48.0f, 40.0f, glow);
        suisse_intl.drawText(matrixStack, this.textSearch, xSearch + 8.0f, animY + sHeight / 2.0f - suisse_intl.getHeight(14.0f) / 2.0f, text, 14.0f);
        StencilHelpers.uninit();
        GLHelpers.INSTANCE.rescaleMC();
    }

    private void renderIdea(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        GLHelpers.INSTANCE.rescale(1.0f);
        suisse_intl.drawCenteredText(matrixStack, this.ideas, (float)mc.getMainWindow().getWidth() / 2.0f, 0.0f, ColorHelpers.rgba(190, 190, 190, 255), 14.0f);
        GLHelpers.INSTANCE.rescaleMC();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouse) {
        ClickGui clickGui = (ClickGui) Load.getInstance().getHooks().getModuleManagers().findClass(ClickGui.class);
        if (clickGui == null) return false;
        Vector2f fixedMouse = GLHelpers.INSTANCE.normalizeCords(mouseX, mouseY, 1.0f);
        if (clickGui.size.getSelected("Small") && !isMouseOverPanels(fixedMouse.x, fixedMouse.y) && !isMouseOverThemeConfig(fixedMouse.x, fixedMouse.y)) {
            return false;
        }
        for (Panel panel : this.panels) {
            panel.mouseClicked(fixedMouse.x, fixedMouse.y, mouse);
        }
        float width = 200.0f;
        float height = 70.0f;
        float x = (float)mc.getMainWindow().getWidth() - width + 130.0f;
        float settingY = 0.0f;
        boolean onClick = ScreenHelpers.isHovered(fixedMouse.x, fixedMouse.y, x - this.marginX, settingY, width, height);
        if (onClick && mouse == 0) {
            mc.displayGuiScreen(new OptionScreen());
        }
        return super.mouseClicked(mouseX, mouseY, mouse);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        ClickGui clickGui = (ClickGui) Load.getInstance().getHooks().getModuleManagers().findClass(ClickGui.class);
        if (clickGui == null) return false;
        Vector2f fixedMouse = GLHelpers.INSTANCE.normalizeCords(mouseX, mouseY, 1.0f);
        if (clickGui.size.getSelected("Small") && !isMouseOverPanels(fixedMouse.x, fixedMouse.y) && !isMouseOverThemeConfig(fixedMouse.x, fixedMouse.y)) {
            return false;
        }
        if (this.isPressed) {
            this.scrollingX += (float)(delta * 15.0);
        }
        for (Panel panel : this.panels) {
            panel.mouseScrolled(fixedMouse.x, fixedMouse.y, delta);
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        Vector2f fixedMouse = GLHelpers.INSTANCE.normalizeCords(mouseX, mouseY, 1.0f);
        for (Panel panel : this.panels) {
            panel.mouseReleased(fixedMouse.x, fixedMouse.y, button);
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        ClickGui clickGui = (ClickGui) Load.getInstance().getHooks().getModuleManagers().findClass(ClickGui.class);
        if (clickGui == null) return false;
        // Toggle search with CTRL+F
        if (keyCode == 70 && net.minecraft.client.gui.screen.Screen.hasControlDown()) {
            this.searching = !this.searching; // Toggle search state
            if (!this.searching) {
                this.textSearch = ""; // Clear search text when closing
            }
            return true;
        }
        // Handle backspace for deleting search text
        if (this.searching && keyCode == 259) { // 259 is the keycode for backspace
            if (!this.textSearch.isEmpty()) {
                this.textSearch = this.textSearch.substring(0, this.textSearch.length() - 1);
            }
            return true;
        }
        for (Panel panel : this.panels) {
            panel.keyPressed(keyCode, scanCode, modifiers);
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        this.isPressed = false;
        if (keyCode == 341) {
            this.control = false;
        }
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (this.searching) {
            this.textSearch = this.textSearch + codePoint;
            return true;
        }
        for (Panel panel : this.panels) {
            panel.charTyped(codePoint, modifiers);
        }
        return super.charTyped(codePoint, modifiers);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void closeScreen() {
        if (!closing) {
            closing = true;
            this.animation.animate(this.animation.getValue(), 0.0f, 0.2f, EasingList.BACK_IN, mc.getTimer().renderPartialTicks);
            return;
        }
        this.isPressed = false;
        for (Panel panel : this.panels) {
            panel.exit();
        }
        this.update = false;
        this.searching = false;
        this.textSearch = "";
    }

    @Override
    public void resize(Minecraft mc, int width, int height) {
        super.resize(mc, width, height);
    }

    @Override
    public void register(Component component) {
        this.components.add(component);
    }

    public void translate() {
        for (Panel panel : this.panels) {
            panel.translate();
        }
        this.ideas = this.getTranslation("To open or close search, press CTRL + F");
    }

    @Generated
    public Animation getAnimation() {
        return this.animation;
    }

    @Generated
    public Animation getBackgroundAnimation() {
        return this.backgroundAnimation;
    }

    @Generated
    public String getTextSearch() {
        return this.textSearch;
    }

    @Generated
    public boolean isSearching() {
        return this.searching;
    }

    @Generated
    public void setSearching(boolean searching) {
        this.searching = searching;
    }
}