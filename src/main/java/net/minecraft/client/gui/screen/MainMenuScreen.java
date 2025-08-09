/* Decompiler 361ms, total 857ms, lines 529 */
package net.minecraft.client.gui.screen;

import com.google.common.util.concurrent.Runnables;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import fun.kubik.Load;
import fun.kubik.helpers.animation.Animation;
import fun.kubik.helpers.animation.EasingList;
import fun.kubik.helpers.interfaces.IFastAccess;
import fun.kubik.helpers.render.ColorHelpers;
import fun.kubik.helpers.render.GLHelpers;
import fun.kubik.helpers.visual.VisualHelpers;
import fun.kubik.managers.client.ClientManagers;
import java.io.IOException;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
import javax.annotation.Nullable;
import net.minecraft.client.gui.AccessibilityScreen;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.toasts.SystemToast;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.client.gui.widget.button.Button.ITooltip;
import net.minecraft.client.renderer.RenderSkybox;
import net.minecraft.client.renderer.RenderSkyboxCube;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.realms.RealmsBridgeScreen;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.DynamicRegistries.Impl;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.gen.settings.DimensionGeneratorSettings;
import net.minecraft.world.storage.SaveFormat;
import net.minecraft.world.storage.WorldSummary;
import net.minecraft.world.storage.SaveFormat.LevelSave;
import net.optifine.reflect.Reflector;
import net.optifine.reflect.ReflectorForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MainMenuScreen extends Screen {
    private static final Logger field_238656_b_ = LogManager.getLogger();
    public static final RenderSkyboxCube PANORAMA_RESOURCES = new RenderSkyboxCube(new ResourceLocation("textures/gui/title/background/panorama"));
    private static final ResourceLocation PANORAMA_OVERLAY_TEXTURES = new ResourceLocation("textures/gui/title/background/panorama_overlay.png");
    private static final ResourceLocation ACCESSIBILITY_TEXTURES = new ResourceLocation("textures/gui/accessibility.png");
    private final boolean showTitleWronglySpelled;
    @Nullable
    private String splashText;
    private Button buttonResetDemo;
    private static final ResourceLocation MINECRAFT_TITLE_TEXTURES = new ResourceLocation("textures/gui/title/minecraft.png");
    private static final ResourceLocation MINECRAFT_TITLE_EDITION = new ResourceLocation("textures/gui/title/edition.png");
    private boolean hasCheckedForRealmsNotification;
    private Screen realmsNotification;
    private int widthCopyright;
    private int widthCopyrightRest;
    private final RenderSkybox panorama;
    private final boolean showFadeInAnimation;
    private long firstRenderTime;
    private Screen modUpdateNotification;

    public MainMenuScreen() {
        this(false);
    }

    public MainMenuScreen(boolean fadeIn) {
        super(new TranslationTextComponent("narrator.screen.title"));
        this.panorama = new RenderSkybox(PANORAMA_RESOURCES);
        this.showFadeInAnimation = fadeIn;
        this.showTitleWronglySpelled = (double)(new Random()).nextFloat() < 1.0E-4D;
    }

    private boolean areRealmsNotificationsEnabled() {
        return this.minecraft.gameSettings.realmsNotifications && this.realmsNotification != null;
    }

    public void tick() {
        if (this.areRealmsNotificationsEnabled()) {
            this.realmsNotification.tick();
        }
        Iterator var1 = this.buttons.iterator();
        while(var1.hasNext()) {
            Widget widget = (Widget)var1.next();
            if (widget instanceof MainMenuScreen.CustomButton) {
                MainMenuScreen.CustomButton customButton = (MainMenuScreen.CustomButton)widget;
                customButton.tick();
            }
        }
    }

    public static CompletableFuture<Void> loadAsync(TextureManager texMngr, Executor backgroundExecutor) {
        return CompletableFuture.allOf(texMngr.loadAsync(MINECRAFT_TITLE_TEXTURES, backgroundExecutor), texMngr.loadAsync(MINECRAFT_TITLE_EDITION, backgroundExecutor), texMngr.loadAsync(PANORAMA_OVERLAY_TEXTURES, backgroundExecutor), PANORAMA_RESOURCES.loadAsync(texMngr, backgroundExecutor));
    }

    public boolean isPauseScreen() {
        return false;
    }

    public boolean shouldCloseOnEsc() {
        return false;
    }

    protected void init() {
        if (ClientManagers.isUnHook()) {
            if (this.splashText == null) {
                this.splashText = this.minecraft.getSplashes().getSplashText();
            }
            this.widthCopyright = this.font.getStringWidth("Copyright Mojang AB. Do not distribute!");
            this.widthCopyrightRest = this.width - this.widthCopyright - 2;
            int j = this.height / 4 + 48;
            Button button = null;
            if (this.minecraft.isDemo()) {
                this.addDemoButtons(j, 24);
            } else {
                this.addSingleplayerMultiplayerButtons(j, 24);
                if (Reflector.ModListScreen_Constructor.exists()) {
                    button = ReflectorForge.makeButtonMods(this, j, 24);
                    this.addButton(button);
                }
            }
            this.addButton(new ImageButton(this.width / 2 - 124, j + 72 + 12, 20, 20, 0, 106, 20, Button.WIDGETS_LOCATION, 256, 256, (p_lambda$init$0_1_) -> {
                this.minecraft.displayGuiScreen(new LanguageScreen(this, this.minecraft.gameSettings, this.minecraft.getLanguageManager()));
            }, new TranslationTextComponent("narrator.button.language")));
            this.addButton(new Button(this.width / 2 - 100, j + 72 + 12, 98, 20, new TranslationTextComponent("menu.options"), (p_lambda$init$1_1_) -> {
                this.minecraft.displayGuiScreen(new OptionsScreen(this, this.minecraft.gameSettings));
            }));
            this.addButton(new Button(this.width / 2 + 2, j + 72 + 12, 98, 20, new TranslationTextComponent("menu.quit"), (p_lambda$init$2_1_) -> {
                this.minecraft.shutdown();
            }));
            this.addButton(new ImageButton(this.width / 2 + 104, j + 72 + 12, 20, 20, 0, 0, 20, ACCESSIBILITY_TEXTURES, 32, 64, (p_lambda$init$3_1_) -> {
                this.minecraft.displayGuiScreen(new AccessibilityScreen(this, this.minecraft.gameSettings));
            }, new TranslationTextComponent("narrator.button.accessibility")));
            this.minecraft.setConnectedToRealms(false);
            if (this.minecraft.gameSettings.realmsNotifications && !this.hasCheckedForRealmsNotification) {
                RealmsBridgeScreen realmsbridgescreen = new RealmsBridgeScreen();
                this.realmsNotification = realmsbridgescreen.func_239555_b_(this);
                this.hasCheckedForRealmsNotification = true;
            }
            if (this.areRealmsNotificationsEnabled()) {
                this.realmsNotification.init(this.minecraft, this.width, this.height);
            }
            if (Reflector.NotificationModUpdateScreen_init.exists()) {
                this.modUpdateNotification = (Screen)Reflector.call(Reflector.NotificationModUpdateScreen_init, new Object[]{this, button});
            }
        } else {
            int buttonWidth = 258;
            int buttonHeight = 48;
            int x = (int)((double)this.minecraft.getMainWindow().getScaledWidth() * this.minecraft.getMainWindow().getGuiScaleFactor() / 2.0D - (double)((float)buttonWidth / 2.0F));
            int y = (int)((double)this.minecraft.getMainWindow().getScaledHeight() * this.minecraft.getMainWindow().getGuiScaleFactor() / 2.0D);
            int offset = (buttonHeight + 8) * -2;
            this.addButton(new MainMenuScreen.CustomButton(x - 18, y + offset + 54, buttonWidth, buttonHeight, new StringTextComponent("Singleplayer"), (p_onPress_1_) -> {
                this.minecraft.displayGuiScreen(new WorldSelectionScreen(this));
            }, false));
            offset += buttonHeight + 8;
            this.addButton(new MainMenuScreen.CustomButton(x - 18, y + offset + 36, buttonWidth, buttonHeight, new StringTextComponent("Multiplayer"), (p_onPress_1_) -> {
                this.minecraft.displayGuiScreen(new MultiplayerScreen(this));
            }, false));
            offset += buttonHeight + 8;
            this.addButton(new MainMenuScreen.CustomButton(x - 18, y + offset + 18, buttonWidth, buttonHeight, new StringTextComponent("AltManager"), (p_onPress_1_) -> {
                this.minecraft.displayGuiScreen(Load.getInstance().getAltScreen());
            }, false));
            offset += buttonHeight + 8;
            this.addButton(new MainMenuScreen.CustomButton(x - 18, y + offset, 202, buttonHeight, new StringTextComponent("Options"), (p_onPress_1_) -> {
                this.minecraft.displayGuiScreen(new OptionsScreen(this, this.minecraft.gameSettings));
            }, false));
            this.addButton(new MainMenuScreen.CustomButton(x + 210 - 18, y + offset, buttonHeight, buttonHeight, new StringTextComponent("Quit"), (p_onPress_1_) -> {
                this.minecraft.shutdownMinecraftApplet();
            }, true));
        }
    }

    private void addSingleplayerMultiplayerButtons(int yIn, int rowHeightIn) {
        this.addButton(new Button(this.width / 2 - 100, yIn, 200, 20, new TranslationTextComponent("menu.singleplayer"), (p_lambda$addSingleplayerMultiplayerButtons$4_1_) -> {
            this.minecraft.displayGuiScreen(new WorldSelectionScreen(this));
        }));
        boolean flag = this.minecraft.isMultiplayerEnabled();
        ITooltip button$itooltip = flag ? Button.field_238486_s_ : (p_lambda$addSingleplayerMultiplayerButtons$5_1_, p_lambda$addSingleplayerMultiplayerButtons$5_2_, p_lambda$addSingleplayerMultiplayerButtons$5_3_, p_lambda$addSingleplayerMultiplayerButtons$5_4_) -> {
            if (!p_lambda$addSingleplayerMultiplayerButtons$5_1_.active) {
                this.renderTooltip(p_lambda$addSingleplayerMultiplayerButtons$5_2_, this.minecraft.fontRenderer.trimStringToWidth(new TranslationTextComponent("title.multiplayer.disabled"), Math.max(this.width / 2 - 43, 170)), p_lambda$addSingleplayerMultiplayerButtons$5_3_, p_lambda$addSingleplayerMultiplayerButtons$5_4_);
            }
        };
        ((Button)this.addButton(new Button(this.width / 2 - 100, yIn + rowHeightIn * 1, 200, 20, new TranslationTextComponent("menu.multiplayer"), (p_lambda$addSingleplayerMultiplayerButtons$6_1_) -> {
            Screen screen = this.minecraft.gameSettings.skipMultiplayerWarning ? new MultiplayerScreen(this) : new MultiplayerWarningScreen(this);
            this.minecraft.displayGuiScreen((Screen)screen);
        }, button$itooltip))).active = flag;
        ((Button)this.addButton(new Button(this.width / 2 - 100, yIn + rowHeightIn * 2, 200, 20, new TranslationTextComponent("menu.online"), (p_lambda$addSingleplayerMultiplayerButtons$7_1_) -> {
            this.switchToRealms();
        }, button$itooltip))).active = flag;
        if (Reflector.ModListScreen_Constructor.exists() && this.buttons.size() > 0) {
            Widget widget = (Widget)this.buttons.get(this.buttons.size() - 1);
            widget.x = this.width / 2 + 2;
            widget.setWidth(98);
        }
    }

    private void addDemoButtons(int yIn, int rowHeightIn) {
        boolean flag = this.func_243319_k();
        this.addButton(new Button(this.width / 2 - 100, yIn, 200, 20, new TranslationTextComponent("menu.playdemo"), (p_lambda$addDemoButtons$8_2_) -> {
            if (flag) {
                this.minecraft.loadWorld("Demo_World");
            } else {
                Impl dynamicregistries$impl = DynamicRegistries.func_239770_b_();
                this.minecraft.createWorld("Demo_World", MinecraftServer.DEMO_WORLD_SETTINGS, dynamicregistries$impl, DimensionGeneratorSettings.func_242752_a(dynamicregistries$impl));
            }
        }));
        this.buttonResetDemo = (Button)this.addButton(new Button(this.width / 2 - 100, yIn + rowHeightIn * 1, 200, 20, new TranslationTextComponent("menu.resetdemo"), (p_lambda$addDemoButtons$9_1_) -> {
            SaveFormat saveformat = this.minecraft.getSaveLoader();
            try {
                LevelSave saveformat$levelsave = saveformat.getLevelSave("Demo_World");
                try {
                    WorldSummary worldsummary = saveformat$levelsave.readWorldSummary();
                    if (worldsummary != null) {
                        this.minecraft.displayGuiScreen(new ConfirmScreen(this::deleteDemoWorld, new TranslationTextComponent("selectWorld.deleteQuestion"), new TranslationTextComponent("selectWorld.deleteWarning", new Object[]{worldsummary.getDisplayName()}), new TranslationTextComponent("selectWorld.deleteButton"), DialogTexts.GUI_CANCEL));
                    }
                } catch (Throwable var7) {
                    if (saveformat$levelsave != null) {
                        try {
                            saveformat$levelsave.close();
                        } catch (Throwable var6) {
                            var7.addSuppressed(var6);
                        }
                    }
                    throw var7;
                }
                if (saveformat$levelsave != null) {
                    saveformat$levelsave.close();
                }
            } catch (IOException var8) {
                SystemToast.func_238535_a_(this.minecraft, "Demo_World");
                field_238656_b_.warn("Failed to access demo world", var8);
            }
        }));
        this.buttonResetDemo.active = flag;
    }

    private boolean func_243319_k() {
        try {
            LevelSave saveformat$levelsave = this.minecraft.getSaveLoader().getLevelSave("Demo_World");
            boolean var2;
            try {
                var2 = saveformat$levelsave.readWorldSummary() != null;
            } catch (Throwable var5) {
                if (saveformat$levelsave != null) {
                    try {
                        saveformat$levelsave.close();
                    } catch (Throwable var4) {
                        var5.addSuppressed(var4);
                    }
                }
                throw var5;
            }
            if (saveformat$levelsave != null) {
                saveformat$levelsave.close();
            }
            return var2;
        } catch (IOException var6) {
            SystemToast.func_238535_a_(this.minecraft, "Demo_World");
            field_238656_b_.warn("Failed to read demo world data", var6);
            return false;
        }
    }

    private void switchToRealms() {
        RealmsBridgeScreen realmsbridgescreen = new RealmsBridgeScreen();
        realmsbridgescreen.func_231394_a_(this);
    }

    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        if (!ClientManagers.isUnHook()) {
            Vector2f fixedCoords = GLHelpers.INSTANCE.normalizeCords((double)mouseX, (double)mouseY, 1.0D);
            GLHelpers.INSTANCE.rescale(1.0D);
            VisualHelpers.drawRoundedTexture(matrixStack, new ResourceLocation("main/textures/images/mainmenu.png"), 0.0F, 0.0F, (float)this.minecraft.getMainWindow().getWidth(), (float)this.minecraft.getMainWindow().getHeight(), 0.0F, -1);
            super.render(matrixStack, (int)fixedCoords.x, (int)fixedCoords.y, partialTicks);
            GLHelpers.INSTANCE.rescaleMC();
        } else {
            if (this.firstRenderTime == 0L && this.showFadeInAnimation) {
                this.firstRenderTime = Util.milliTime();
            }
            float f = this.showFadeInAnimation ? (float)(Util.milliTime() - this.firstRenderTime) / 1000.0F : 1.0F;
            GlStateManager.disableDepthTest();
            fill(matrixStack, 0, 0, this.width, this.height, -1);
            this.panorama.render(partialTicks, MathHelper.clamp(f, 0.0F, 1.0F));
            int j = this.width / 2 - 137;
            this.minecraft.getTextureManager().bindTexture(PANORAMA_OVERLAY_TEXTURES);
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, this.showFadeInAnimation ? (float)MathHelper.ceil(MathHelper.clamp(f, 0.0F, 1.0F)) : 1.0F);
            blit(matrixStack, 0, 0, this.width, this.height, 0.0F, 0.0F, 16, 128, 16, 128);
            float f1 = this.showFadeInAnimation ? MathHelper.clamp(f - 1.0F, 0.0F, 1.0F) : 1.0F;
            int l = MathHelper.ceil(f1 * 255.0F) << 24;
            if ((l & -67108864) != 0) {
                this.minecraft.getTextureManager().bindTexture(MINECRAFT_TITLE_TEXTURES);
                RenderSystem.color4f(1.0F, 1.0F, 1.0F, f1);
                if (this.showTitleWronglySpelled) {
                    this.blitBlackOutline(j, 30, (p_lambda$render$10_2_, p_lambda$render$10_3_) -> {
                        this.blit(matrixStack, p_lambda$render$10_2_ + 0, p_lambda$render$10_3_, 0, 0, 99, 44);
                        this.blit(matrixStack, p_lambda$render$10_2_ + 99, p_lambda$render$10_3_, 129, 0, 27, 44);
                        this.blit(matrixStack, p_lambda$render$10_2_ + 99 + 26, p_lambda$render$10_3_, 126, 0, 3, 44);
                        this.blit(matrixStack, p_lambda$render$10_2_ + 99 + 26 + 3, p_lambda$render$10_3_, 99, 0, 26, 44);
                        this.blit(matrixStack, p_lambda$render$10_2_ + 155, p_lambda$render$10_3_, 0, 45, 155, 44);
                    });
                } else {
                    this.blitBlackOutline(j, 30, (p_lambda$render$11_2_, p_lambda$render$11_3_) -> {
                        this.blit(matrixStack, p_lambda$render$11_2_ + 0, p_lambda$render$11_3_, 0, 0, 155, 44);
                        this.blit(matrixStack, p_lambda$render$11_2_ + 155, p_lambda$render$11_3_, 0, 45, 155, 44);
                    });
                }
                this.minecraft.getTextureManager().bindTexture(MINECRAFT_TITLE_EDITION);
                blit(matrixStack, j + 88, 67, 0.0F, 0.0F, 98, 14, 128, 16);
                if (Reflector.ForgeHooksClient_renderMainMenu.exists()) {
                    Reflector.callVoid(Reflector.ForgeHooksClient_renderMainMenu, new Object[]{this, matrixStack, this.font, this.width, this.height, l});
                }
                if (this.splashText != null) {
                    RenderSystem.pushMatrix();
                    RenderSystem.translatef((float)(this.width / 2 + 90), 70.0F, 0.0F);
                    RenderSystem.rotatef(-20.0F, 0.0F, 0.0F, 1.0F);
                    float f2 = 1.8F - MathHelper.abs(MathHelper.sin((float)(Util.milliTime() % 1000L) / 1000.0F * 6.2831855F) * 0.1F);
                    f2 = f2 * 100.0F / (float)(this.font.getStringWidth(this.splashText) + 32);
                    RenderSystem.scalef(f2, f2, f2);
                    drawCenteredString(matrixStack, this.font, this.splashText, 0, -8, 16776960 | l);
                    RenderSystem.popMatrix();
                }
                String s = "Minecraft " + SharedConstants.getVersion().getName();
                if (this.minecraft.isDemo()) {
                    s = s + " Demo";
                } else {
                    s = s + ("release".equalsIgnoreCase(this.minecraft.getVersionType()) ? "" : "/" + this.minecraft.getVersionType());
                }
                if (this.minecraft.isModdedClient()) {
                    s = s + I18n.format("menu.modded", new Object[0]);
                }
                if (Reflector.BrandingControl.exists()) {
                    BiConsumer biconsumer1;
                    if (Reflector.BrandingControl_forEachLine.exists()) {
                        biconsumer1 = (p_lambda$render$12_3_, p_lambda$render$12_4_) -> {
                            drawString(matrixStack, this.font, (String)p_lambda$render$12_4_, 2, this.height - (10 + (Integer)p_lambda$render$12_3_ * 10), 16777215 | l);
                        };
                        Reflector.call(Reflector.BrandingControl_forEachLine, new Object[]{true, true, biconsumer1});
                    }
                    if (Reflector.BrandingControl_forEachAboveCopyrightLine.exists()) {
                        biconsumer1 = (p_lambda$render$13_3_, p_lambda$render$13_4_) -> {
                            drawString(matrixStack, this.font, (String)p_lambda$render$13_4_, this.width - this.font.getStringWidth((String)p_lambda$render$13_4_), this.height - (10 + ((Integer)p_lambda$render$13_3_ + 1) * 10), 16777215 | l);
                        };
                        Reflector.call(Reflector.BrandingControl_forEachAboveCopyrightLine, new Object[]{biconsumer1});
                    }
                } else {
                    drawString(matrixStack, this.font, s, 2, this.height - 10, 16777215 | l);
                }
                drawString(matrixStack, this.font, "Copyright Mojang AB. Do not distribute!", this.widthCopyrightRest, this.height - 10, 16777215 | l);
                if (mouseX > this.widthCopyrightRest && mouseX < this.widthCopyrightRest + this.widthCopyright && mouseY > this.height - 10 && mouseY < this.height) {
                    fill(matrixStack, this.widthCopyrightRest, this.height - 1, this.widthCopyrightRest + this.widthCopyright, this.height, 16777215 | l);
                }
                Iterator var16 = this.buttons.iterator();
                while(var16.hasNext()) {
                    Widget widget = (Widget)var16.next();
                    widget.setAlpha(f1);
                }
                super.render(matrixStack, mouseX, mouseY, partialTicks);
                if (this.areRealmsNotificationsEnabled() && f1 >= 1.0F) {
                    this.realmsNotification.render(matrixStack, mouseX, mouseY, partialTicks);
                }
            }
            if (this.modUpdateNotification != null) {
                this.modUpdateNotification.render(matrixStack, mouseX, mouseY, partialTicks);
            }
        }
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!ClientManagers.isUnHook()) {
            Vector2f fixedCoords = GLHelpers.INSTANCE.normalizeCords(mouseX, mouseY, 1.0D);
            mouseX = (double)fixedCoords.x;
            mouseY = (double)fixedCoords.y;
        }
        if (super.mouseClicked(mouseX, mouseY, button)) {
            return true;
        } else if (this.areRealmsNotificationsEnabled() && this.realmsNotification.mouseClicked(mouseX, mouseY, button)) {
            return true;
        } else {
            if (mouseX > (double)this.widthCopyrightRest && mouseX < (double)(this.widthCopyrightRest + this.widthCopyright) && mouseY > (double)(this.height - 10) && mouseY < (double)this.height) {
                this.minecraft.displayGuiScreen(new WinGameScreen(false, Runnables.doNothing()));
            }
            return false;
        }
    }

    public void onClose() {
        if (this.realmsNotification != null) {
            this.realmsNotification.onClose();
        }
    }

    private void deleteDemoWorld(boolean p_213087_1_) {
        if (p_213087_1_) {
            try {
                LevelSave saveformat$levelsave = this.minecraft.getSaveLoader().getLevelSave("Demo_World");
                try {
                    saveformat$levelsave.deleteSave();
                } catch (Throwable var6) {
                    if (saveformat$levelsave != null) {
                        try {
                            saveformat$levelsave.close();
                        } catch (Throwable var5) {
                            var6.addSuppressed(var5);
                        }
                    }
                    throw var6;
                }
                if (saveformat$levelsave != null) {
                    saveformat$levelsave.close();
                }
            } catch (IOException var7) {
                SystemToast.func_238538_b_(this.minecraft, "Demo_World");
                field_238656_b_.warn("Failed to delete demo world", var7);
            }
        }
        this.minecraft.displayGuiScreen(this);
    }

    public static class CustomButton extends Button implements IFastAccess {
        private final boolean alternative;
        private final Animation animation;

        public CustomButton(int x, int y, int width, int height, ITextComponent title, IPressable pressedAction, boolean alternative) {
            this(x, y, width, height, title, pressedAction, Button.field_238486_s_, alternative);
        }

        public CustomButton(int x, int y, int width, int height, ITextComponent title, IPressable pressedAction, ITooltip onTooltip, boolean alternative) {
            super(x, y, width, height, title, pressedAction, onTooltip);
            this.animation = new Animation();
            this.alternative = alternative;
        }

        public void tick() {
            this.animation.update(this.isHovered());
        }

        public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
            this.animation.animate(0.0F, 1.0F, 0.2F, EasingList.CIRC_OUT, partialTicks);
            int back = ColorHelpers.interpolateColor(ColorHelpers.rgba(190, 190, 190, 10.200000000000001D), ColorHelpers.rgba(48, 207, 151, 30.599999999999998D), (double)this.animation.getAnimationValue());
            int outline = ColorHelpers.interpolateColor(ColorHelpers.rgba(190, 190, 190, 15.299999999999999D), ColorHelpers.rgba(48, 207, 151, 30.599999999999998D), (double)this.animation.getAnimationValue());
            int text = ColorHelpers.rgba(255, 255, 255, 122.39999999999999D + 132.6D * (double)this.animation.getAnimationValue());
            int image = ColorHelpers.interpolateColor(ColorHelpers.rgba(255, 255, 255, 61.199999999999996D), ColorHelpers.rgba(48, 207, 151, 255), (double)this.animation.getAnimationValue());
            BLUR_RUNNABLES.add(() -> {
                VisualHelpers.drawRoundedRect(matrixStack, (float)this.x, (float)this.y, (float)this.width, (float)this.height, 8.0F, -1);
            });
            this.blurSetting(partialTicks, 12.0F, 1.0F);
            VisualHelpers.drawRoundedRect(matrixStack, (float)this.x, (float)this.y, (float)this.width, (float)this.height / 1.5f, 8.0F, back);
            VisualHelpers.drawRoundedOutline(matrixStack, (float)this.x, (float)this.y, (float)this.width, (float)this.height / 1.5f, 8.0F, 1.0F, outline);
            if (!this.alternative) {
                VisualHelpers.drawImage(matrixStack, new ResourceLocation("main/textures/images/menu/" + this.getMessage().getString().toLowerCase() + ".png"), (float)this.x + (float)this.width / 2.0F - suisse_intl.getWidth(this.getMessage().getString(), 14.0F) / 2.0F - 11.0F, (float)this.y + (float)this.height / 2.0F - 7.0F- 7, 14.0F, 14.0F, image);
                suisse_intl.drawCenteredText(matrixStack, this.getMessage().getString(), (float)this.x + (float)this.width / 2.0F + 11.0F, (float)this.y + (float)this.height / 2.0F - suisse_intl.getHeight(14.0F) / 2.0F - 7, text, 14.0F);
            } else {
                VisualHelpers.drawImage(matrixStack, new ResourceLocation("main/textures/images/menu/" + this.getMessage().getString().toLowerCase() + ".png"), (float)this.x + (float)this.width / 2.0F - 7.0F, (float)this.y + (float)this.height / 2.0F - 7.0F- 7, 14.0F, 14.0F, image);
            }
        }
    }
}