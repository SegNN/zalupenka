    /*
     * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
     */
    package fun.kubik.modules.render;

    import com.mojang.blaze3d.matrix.MatrixStack;
    import com.mojang.blaze3d.systems.RenderSystem;
    import fun.kubik.Load;
    import fun.kubik.events.api.EventHook;
    import fun.kubik.events.main.EventUpdate;
    import fun.kubik.events.main.render.EventNameRender;
    import fun.kubik.events.main.render.EventRender2D;
    import fun.kubik.helpers.animation.EasingList;
    import fun.kubik.helpers.render.ColorHelpers;
    import fun.kubik.helpers.render.ScreenHelpers;
    import fun.kubik.helpers.visual.VisualHelpers;
    import fun.kubik.managers.module.Module;
    import fun.kubik.managers.module.main.Category;
    import fun.kubik.managers.module.option.main.MultiOption;
    import fun.kubik.managers.module.option.main.MultiOptionValue;
    import fun.kubik.modules.player.FixHP;
    import fun.kubik.utils.client.StringUtils;
    import fun.kubik.utils.math.MathUtils;
    import java.util.ArrayList;
    import java.util.Arrays;
    import java.util.HashMap;
    import java.util.List;
    import java.util.Map;
    import java.util.concurrent.atomic.AtomicInteger;
    import net.minecraft.client.renderer.WorldRenderer;
    import net.minecraft.scoreboard.ScorePlayerTeam;
    import net.minecraft.enchantment.EnchantmentHelper;
    import net.minecraft.enchantment.Enchantments;
    import net.minecraft.entity.Entity;
    import net.minecraft.entity.player.PlayerEntity;
    import net.minecraft.item.AirItem;
    import net.minecraft.item.ArmorItem;
    import net.minecraft.item.AxeItem;
    import net.minecraft.item.BowItem;
    import net.minecraft.item.Item;
    import net.minecraft.item.ItemStack;
    import net.minecraft.item.SkullItem;
    import net.minecraft.item.SwordItem;
    import net.minecraft.item.ToolItem;
    import net.minecraft.scoreboard.Score;
    import net.minecraft.util.math.AxisAlignedBB;
    import net.minecraft.util.math.vector.Vector2f;
    import net.minecraft.util.math.vector.Vector3d;
    import net.minecraft.util.math.vector.Vector4f;
    import net.minecraft.util.text.IFormattableTextComponent;
    import net.minecraft.util.text.ITextComponent;
    import net.minecraft.util.text.StringTextComponent;
    import net.minecraft.util.text.Style;
    import net.minecraft.util.text.TextComponent;
    import net.minecraft.util.text.TextFormatting;

    public class ESP
            extends Module {
        private final HashMap<PlayerEntity, Vector4f> positions = new HashMap();
        private final MultiOption elements = new MultiOption("Elements", new MultiOptionValue("Armor", true));
        private final MultiOption armorElements = new MultiOption("Armor Elements", new MultiOptionValue("Enchantments", true)).visible(() -> this.elements.getSelected("Armor"));

        public ESP() {
            super("ESP", Category.RENDER);
            this.settings(this.elements, this.armorElements);
        }

        @EventHook
        public void tag(EventNameRender event) {
            event.setCancelled(true);
        }

        @EventHook
        public void render(EventRender2D.Post event) {
            if (ESP.mc.world == null) {
                return;
            }
            this.positions.clear();
            for (PlayerEntity playerEntity : ESP.mc.world.getPlayers()) {
                if (!this.isInView(playerEntity) || playerEntity == ESP.mc.player) continue;
                double x = MathUtils.interpolate(playerEntity.getPosX(), playerEntity.lastTickPosX, (double)event.getPartialTicks());
                double y = MathUtils.interpolate(playerEntity.getPosY(), playerEntity.lastTickPosY, (double)event.getPartialTicks());
                double z = MathUtils.interpolate(playerEntity.getPosZ(), playerEntity.lastTickPosZ, (double)event.getPartialTicks());
                Vector3d size = new Vector3d(playerEntity.getBoundingBox().maxX - playerEntity.getBoundingBox().minX, playerEntity.getBoundingBox().maxY - playerEntity.getBoundingBox().minY, playerEntity.getBoundingBox().maxZ - playerEntity.getBoundingBox().minZ);
                AxisAlignedBB aabb = new AxisAlignedBB(x - size.x / 2.0, y, z - size.z / 2.0, x + size.x / 2.0, y + size.y, z + size.z / 2.0);
                Vector4f position = null;
                for (int i = 0; i < 8; ++i) {
                    Vector2f vector = ScreenHelpers.worldToScreen(i % 2 == 0 ? aabb.minX : aabb.maxX, i / 2 % 2 == 0 ? aabb.minY : aabb.maxY, i / 4 % 2 == 0 ? aabb.minZ : aabb.maxZ);
                    if (position == null) {
                        position = new Vector4f(vector.x, vector.y, 1.0f, 1.0f);
                        continue;
                    }
                    position.x = Math.min(vector.x, position.x);
                    position.y = Math.min(vector.y, position.y);
                    position.z = Math.max(vector.x, position.z);
                    position.w = Math.max(vector.y, position.w);
                }
                this.positions.put(playerEntity, position);
            }
            for (Map.Entry entry : this.positions.entrySet()) {
                PlayerEntity player = (PlayerEntity)entry.getKey();
                Vector4f position = (Vector4f)entry.getValue();
                this.renderTags(event.getMatrixStack(), position.x, position.y, position.z, position.w, player);
                if (!this.elements.getSelected("Armor")) continue;
                float maxOffsetY = 0.0f;
                maxOffsetY += 25.0f;
                ArrayList<ItemStack> stacks = new ArrayList<ItemStack>(Arrays.asList(player.getHeldItemMainhand(), player.getHeldItemOffhand()));
                player.getArmorInventoryList().forEach(stacks::add);
                stacks.removeIf(w -> w.getItem() instanceof AirItem);
                int totalSize = stacks.size() * 10;
                AtomicInteger iterable = new AtomicInteger();
                float finalMaxOffsetY = maxOffsetY += 19.0f;
                double endX = position.z;
                double endY = position.w;
                MathUtils.scaleElements((position.x + position.z) / 2.0f, position.y - maxOffsetY - 5.0f, 0.7f, () -> this.renderArmorAndEnchantment(stacks, event.getMatrixStack(), position.x, position.z, position.y, finalMaxOffsetY, totalSize, iterable));
            }
        }

        @EventHook
        public void update(EventUpdate event) {
            for (Map.Entry<PlayerEntity, Vector4f> entry : this.positions.entrySet()) {
                entry.getKey().getFriendAnimation().update(Load.getInstance().getHooks().getFriendManagers().is(entry.getKey().getGameProfile().getName()));
            }
        }

        private void renderTags(MatrixStack matrixStack, float posX, float posY, float endPosX, float endPosY, PlayerEntity player) {
            // Определяем цвет игрока
            ScorePlayerTeam team = ESP.mc.world.getScoreboard().getPlayersTeam(player.getScoreboardName());
            TextFormatting playerColor = TextFormatting.WHITE;
            if (team != null && team.getColor() != null && team.getColor() != TextFormatting.RESET) {
                playerColor = team.getColor();
            }

            IFormattableTextComponent name = new StringTextComponent(Load.getInstance().getHooks().getFriendManagers().is(player.getGameProfile().getName()) ? "[F] " : "").setStyle(Style.EMPTY.setFormatting(playerColor));
            if (StringUtils.prefix(player.getPrefix().getString().replace(" ", "")) != null) {
                ((TextComponent)name).append(StringUtils.prefix(player.getPrefix().getString().replace(" ", "")));
            } else {
                for (ITextComponent component : player.getPrefix().getSiblings()) {
                    if (!StringUtils.smallCaps(component.getString().replace(" ", "")).contains("null")) {
                        ((TextComponent)name).append(new StringTextComponent(StringUtils.smallCaps(component.getString().replace(" ", ""))).setStyle(component.getStyle()));
                        continue;
                    }
                    ((TextComponent)name).append(new StringTextComponent(component.getString()).setStyle(component.getStyle()));
                }
            }
            if (!player.getPrefix().getString().isEmpty()) {
                ((TextComponent)name).appendString("  ");
            }
            float hp = player.getHealth();
            Score score = ESP.mc.world.getScoreboard().getOrCreateScore(player.getScoreboardName(), ESP.mc.world.getScoreboard().getObjectiveInDisplaySlot(2));
            if (mc.getCurrentServerData() != null) {
                String serverIP = ESP.mc.getCurrentServerData().serverIP;
                if (((FixHP)Load.getInstance().getHooks().getModuleManagers().findClass(FixHP.class)).isToggled()) {
                    hp = score.getScorePoints();
                }
            }
            ((TextComponent)name).append(new StringTextComponent(player.getName().getString()).setStyle(Style.EMPTY.setFormatting(TextFormatting.WHITE)));
            try {
                if (player.getDisplayName().getString().length() > 5 && player.getDisplayName().getString().split(player.getGameProfile().getName())[1].length() >= 5) {
                    // Используем оригинальный суффикс команды напрямую для сохранения цвета
                    if (team != null && !team.getSuffix().getString().isEmpty()) {
                        // Добавляем оригинальный суффикс команды с его стилем
                        ((TextComponent)name).append(team.getSuffix().deepCopy());
                    } else {
                        // Fallback: извлекаем из displayName если команды нет
                        String suffixText = player.getDisplayName().getString().split(player.getGameProfile().getName())[1].replace("+", "");
                        ((TextComponent)name).append(new StringTextComponent(suffixText).setStyle(Style.EMPTY.setFormatting(playerColor)));
                    }
                }
            } catch (Exception serverIP) {
                // empty catch block
            }
            ((TextComponent)name).append(new StringTextComponent("  [" + (int)hp + " HP]").setStyle(Style.EMPTY.setFormatting(TextFormatting.RED)));
            if (player.getHeldItemOffhand().getItem() instanceof SkullItem) {
                Style firstStyle = Style.EMPTY;
                Style lastStyle = Style.EMPTY;
                for (ITextComponent component : player.getHeldItemOffhand().getDisplayName().getSiblings()) {
                    if (firstStyle == Style.EMPTY && component.getStyle() != null) {
                        firstStyle = component.getStyle();
                    }
                    if (component.getStyle() != null) {
                        lastStyle = component.getStyle();
                    }
                }
                ((TextComponent)name).append(new StringTextComponent("  [").setStyle(firstStyle));
                for (ITextComponent component : player.getHeldItemOffhand().getDisplayName().getSiblings()) {
                    ((TextComponent)name).append(component);
                }
                ((TextComponent)name).append(new StringTextComponent("]").setStyle(lastStyle));
            }
            if (player.getHeldItemMainhand().getItem() instanceof SkullItem) {
                ((TextComponent)name).appendString("  [");
                for (ITextComponent component : player.getHeldItemMainhand().getDisplayName().getSiblings()) {
                    ((TextComponent)name).append(component);
                }
                ((TextComponent)name).appendString("]");
            }
            float size = 7.0f;
            float width = sf_medium.getWidth(name, size) - 5.0f;
            float height = 10.0f;
            player.getFriendAnimation().animate(0.0f, 1.0f, 0.2f, EasingList.CIRC_OUT, ESP.mc.getTimer().renderPartialTicks);
            int color = ColorHelpers.interpolateColor(ColorHelpers.rgba(0, 0, 0, 120), ColorHelpers.rgba(0, 120, 0, 120), player.getFriendAnimation().getAnimationValue());
            VisualHelpers.drawRoundedRect(matrixStack, (posX + endPosX) / 2.0f - width / 2.0f, posY - height - 10.0f, width + 10.0f, height, 1.0f, color);
            sf_medium.drawText(matrixStack, name, (posX + endPosX) / 2.0f - width / 2.0f + 2.5f, posY - height - 8.5f, size, 255.0f);
        }

        public boolean isInView(Entity ent) {
            if (mc.getRenderViewEntity() == null) {
                return false;
            }
            WorldRenderer.frustum.setCameraPosition(ESP.mc.getRenderManager().info.getProjectedView().x, ESP.mc.getRenderManager().info.getProjectedView().y, ESP.mc.getRenderManager().info.getProjectedView().z);
            return WorldRenderer.frustum.isBoundingBoxInFrustum(ent.getBoundingBox()) || ent.ignoreFrustumCheck;
        }

        private void renderArmorAndEnchantment(List<ItemStack> stacks, MatrixStack matrixStack, float posX, float endPosX, float posY, float finalMaxOffsetY, int totalSize, AtomicInteger iterable) {
            for (ItemStack stack : stacks) {
                if (stack.isEmpty()) continue;
                ESP.drawItemStack(stack, posX + (endPosX - posX) / 2.0f + (float)(iterable.get() * 20) - (float)totalSize + 2.0f, posY - finalMaxOffsetY + 18.0f, null, false);
                iterable.getAndIncrement();
                ArrayList<String> enchantment = this.getEnchantment(stack);
                float center = posX + (endPosX - posX) / 2.0f + (float)(iterable.get() * 20) - (float)totalSize - 15.5f;
                int i = 0;
                if (!this.elements.getSelected("Armor") || !this.armorElements.getSelected("Enchantments")) continue;
                for (String text : enchantment) {
                    int finalI = i++;
                    MathUtils.scaleElements(center, posY - finalMaxOffsetY + 12.0f - (float)(finalI * 7), 0.45f, () -> {
                        if (text.contains("Sh6") || text.contains("Pr5")) {
                            sf_semibold.drawText(matrixStack, String.valueOf((Object)TextFormatting.RED) + text, center, posY - finalMaxOffsetY + 12.0f - (float)finalI * 7.5f, -1, 16.0f);
                        } else {
                            sf_semibold.drawText(matrixStack, text, center, posY - finalMaxOffsetY + 12.0f - (float)finalI * 7.5f, -1, 16.0f);
                        }
                    });
                }
            }
        }

        public static void drawItemStack(ItemStack stack, double x, double y, String altText, boolean withoutOverlay) {
            RenderSystem.translated(x, y, 0.0);
            mc.getItemRenderer().renderItemAndEffectIntoGUI(stack, 0, 0);
            if (!withoutOverlay) {
                mc.getItemRenderer().renderItemOverlayIntoGUI(ESP.mc.fontRenderer, stack, 0, 0, altText);
            }
            RenderSystem.translated(-x, -y, 0.0);
        }

        private void handleSwordEnchantments(ArrayList<String> list, ItemStack stack) {
            int sharpness = EnchantmentHelper.getEnchantmentLevel(Enchantments.SHARPNESS, stack);
            int fireAspect = EnchantmentHelper.getEnchantmentLevel(Enchantments.FIRE_ASPECT, stack);
            int unbreaking = EnchantmentHelper.getEnchantmentLevel(Enchantments.UNBREAKING, stack);
            int mending = EnchantmentHelper.getEnchantmentLevel(Enchantments.MENDING, stack);
            if (fireAspect > 0) {
                list.add("Fl" + fireAspect);
            }
            if (sharpness > 0) {
                list.add("Sh" + sharpness);
            }
            if (unbreaking > 0) {
                list.add("Un" + unbreaking);
            }
            if (mending > 0) {
                list.add("Me" + mending);
            }
        }

        private void handleToolEnchantments(ArrayList<String> list, ItemStack stack) {
            int unbreaking = EnchantmentHelper.getEnchantmentLevel(Enchantments.UNBREAKING, stack);
            int mending = EnchantmentHelper.getEnchantmentLevel(Enchantments.MENDING, stack);
            int efficiency = EnchantmentHelper.getEnchantmentLevel(Enchantments.EFFICIENCY, stack);
            if (unbreaking > 0) {
                list.add("Un" + unbreaking);
            }
            if (mending > 0) {
                list.add("Me" + mending);
            }
            if (efficiency > 0) {
                list.add("Eff" + efficiency);
            }
        }

        private void handleBowEnchantments(ArrayList<String> list, ItemStack stack) {
            int vanishingCurse = EnchantmentHelper.getEnchantmentLevel(Enchantments.VANISHING_CURSE, stack);
            int infinity = EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, stack);
            int power = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, stack);
            int punch = EnchantmentHelper.getEnchantmentLevel(Enchantments.PUNCH, stack);
            int mending = EnchantmentHelper.getEnchantmentLevel(Enchantments.MENDING, stack);
            int flame = EnchantmentHelper.getEnchantmentLevel(Enchantments.FLAME, stack);
            int unbreaking = EnchantmentHelper.getEnchantmentLevel(Enchantments.UNBREAKING, stack);
            if (infinity > 0) {
                list.add("In" + infinity);
            }
            if (power > 0) {
                list.add("Po" + power);
            }
            if (punch > 0) {
                list.add("Pu" + punch);
            }
            if (mending > 0) {
                list.add("Me" + mending);
            }
            if (flame > 0) {
                list.add("Fl" + flame);
            }
            if (unbreaking > 0) {
                list.add("Un" + unbreaking);
            }
        }

        private void handleAxeEnchantments(ArrayList<String> list, ItemStack stack) {
            int sharpness = EnchantmentHelper.getEnchantmentLevel(Enchantments.SHARPNESS, stack);
            int efficiency = EnchantmentHelper.getEnchantmentLevel(Enchantments.EFFICIENCY, stack);
            int unbreaking = EnchantmentHelper.getEnchantmentLevel(Enchantments.UNBREAKING, stack);
            if (sharpness > 0) {
                list.add("Sh" + sharpness);
            }
            if (efficiency > 0) {
                list.add("Eff" + efficiency);
            }
            if (unbreaking > 0) {
                list.add("Un" + unbreaking);
            }
        }

        private void handleArmorEnchantments(ArrayList<String> list, ItemStack stack) {
            int unbreaking = EnchantmentHelper.getEnchantmentLevel(Enchantments.UNBREAKING, stack);
            int mending = EnchantmentHelper.getEnchantmentLevel(Enchantments.MENDING, stack);
            int feather = EnchantmentHelper.getEnchantmentLevel(Enchantments.FEATHER_FALLING, stack);
            int depth = EnchantmentHelper.getEnchantmentLevel(Enchantments.DEPTH_STRIDER, stack);
            int protection = EnchantmentHelper.getEnchantmentLevel(Enchantments.PROTECTION, stack);
            int aquaF = EnchantmentHelper.getEnchantmentLevel(Enchantments.AQUA_AFFINITY, stack);
            if (aquaF > 0) {
                list.add("Aq" + aquaF);
            }
            if (depth > 0) {
                list.add("De" + depth);
            }
            if (feather > 0) {
                list.add("Fe" + feather);
            }
            if (protection > 0) {
                list.add("Pr" + protection);
            }
            if (mending > 0) {
                list.add("Me" + mending);
            }
            if (unbreaking > 0) {
                list.add("Un" + unbreaking);
            }
        }

        private ArrayList<String> getEnchantment(ItemStack stack) {
            ArrayList<String> list = new ArrayList<String>();
            Item item = stack.getItem();
            if (item instanceof AxeItem) {
                this.handleAxeEnchantments(list, stack);
            } else if (item instanceof ArmorItem) {
                this.handleArmorEnchantments(list, stack);
            } else if (item instanceof BowItem) {
                this.handleBowEnchantments(list, stack);
            } else if (item instanceof SwordItem) {
                this.handleSwordEnchantments(list, stack);
            } else if (item instanceof ToolItem) {
                this.handleToolEnchantments(list, stack);
            }
            return list;
        }
    }

