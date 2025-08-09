package fun.kubik.helpers.module.interfaces;

import com.mojang.blaze3d.matrix.MatrixStack;
import fun.kubik.Load;
import fun.kubik.events.main.EventUpdate;
import fun.kubik.events.main.render.EventRender2D;
import fun.kubik.helpers.animation.Animation;
import fun.kubik.helpers.animation.EasingList;
import fun.kubik.helpers.interfaces.IFastAccess;
import fun.kubik.helpers.render.ColorHelpers;
import fun.kubik.helpers.visual.VisualHelpers;
import fun.kubik.managers.draggable.api.Component;
import fun.kubik.managers.module.option.main.CheckboxOption;
import fun.kubik.managers.staff.StaffManagers;
import fun.kubik.modules.render.Interface;
import fun.kubik.utils.client.StringUtils;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import lombok.Generated;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.network.play.NetworkPlayerInfo;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector4f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.GameType;

public class StaffList
        extends Component {
    private final Pattern namePattern = Pattern.compile("^\\w{3,16}$");
    private final Pattern prefixMatches = Pattern.compile(".*(mod|der|adm|help|wne|taf|curat|dev|supp|yt|\ua509|\ua513|\ua517|\ua525|\ua529|\ua521|\ua533|\ua537|\ua505|\ua501).*");
    private final List<Staff> staffPlayers = new ArrayList<Staff>();
    private final CheckboxOption hide = new CheckboxOption("Hide", true);
    private final CheckboxOption head = new CheckboxOption("Head", false);
    private float width = 0.0f;
    private float height = 0.0f;

    public StaffList() {
        super("Staff Statistics", new Vector2f(100.0f, 46.0f), 145.0f, 66.0f);
        this.getDraggableOption().settings(this.getDesign(), this.getCompression(), this.hide, this.head);
    }

    @Override
    public void update(EventUpdate event) {
        boolean show = (!this.staffPlayers.isEmpty() || StaffList.mc.currentScreen instanceof ChatScreen) && ((Interface)Load.getInstance().getHooks().getModuleManagers().findClass(Interface.class)).getElements().getSelected("StaffList") || (Boolean)this.hide.getValue() == false;
        this.getShowAnimation().update(show);
        this.staffPlayers.clear();
        for (ScorePlayerTeam team : StaffList.mc.world.getScoreboard().getTeams().stream().sorted(Comparator.comparing(Team::getName)).toList()) {
            Staff staff;
            String name = team.getMembershipCollection().toString().replaceAll("[\\[\\]]", "").replace(" ", "");
            boolean vanish = true;
            for (NetworkPlayerInfo info : mc.getConnection().getPlayerInfoMap()) {
                if (!info.getGameProfile().getName().equals(name)) continue;
                vanish = false;
            }
            if (!this.namePattern.matcher(name).matches() || name.equals(StaffList.mc.player.getName().getString())) continue;
            if (!vanish && (this.prefixMatches.matcher(team.getPrefix().getString().toLowerCase(Locale.ROOT)).matches() && !team.getPrefix().getString().contains("D.HELPER") && !team.getPrefix().getString().contains("HELPER") || Load.getInstance().getHooks().getStaffManagers().is(name))) {
                staff = new Staff(team.getPrefix(), name, false, Status.NONE);
                staff.updateStatus(); // Обновляем статус
                this.staffPlayers.add(staff);
            }
            if (!vanish || team.getPrefix().getString().isEmpty()) continue;
            staff = new Staff(team.getPrefix(), name, true, Status.VANISHED);
            staff.updateStatus(); // Обновляем статус
            this.staffPlayers.add(staff);
        }
    }

    @Override
    public void render(EventRender2D.Pre event) {
        float x = ((Vector2f)this.getDraggableOption().getValue()).x;
        float y = ((Vector2f)this.getDraggableOption().getValue()).y;
        MatrixStack matrixStack = event.getMatrixStack();
        float staticHeight = 36.0f;
        float staticWidth = 139.0f;
        float rowHeight = 20.0f;
        StaffManagers staffManagers = Load.getInstance().getHooks().getStaffManagers();
        boolean showExamples = this.staffPlayers.isEmpty() && StaffList.mc.currentScreen instanceof net.minecraft.client.gui.screen.ChatScreen;

        if (this.staffPlayers.isEmpty() && !showExamples) {
            String title = "Staff Statistics";
            float titleWidth = suisse_intl.getWidth(title, 13.0f);
            staticWidth = Math.max(staticWidth, titleWidth + 24.0f);
            staticHeight += rowHeight;
        }

        if (showExamples) {
            String[] exampleNames = {"MrDomer", "Smertnix", "KondrMS"};
            String[] examplePrefixKeys = {"ADMIN", "ML.MODER", "MODER"};
            staticHeight += rowHeight;
            for (int j = 0; j < exampleNames.length; j++) {
                String exampleName = exampleNames[j];
                StringTextComponent prefixComponent = new StringTextComponent("");
                if (StringUtils.prefix(examplePrefixKeys[j]) != null) {
                    prefixComponent.append(StringUtils.prefix(examplePrefixKeys[j]));
                    prefixComponent.appendString(" ");
                }
                prefixComponent.appendString(exampleName);
                float statusLetterWidth = 20.0f;
                float textWidth = suisse_intl.getWidth(prefixComponent, 12.0f);
                float totalRowWidth = statusLetterWidth + textWidth + 24.0f;
                staticWidth = Math.max(staticWidth, totalRowWidth);
            }
        }

        for (Staff staff : this.staffPlayers) {
            staticHeight += rowHeight;
            String playerName = staff.getName();
            float statusLetterWidth = 20.0f;
            float textWidth = 0.0f;
            if (staffManagers.is(playerName)) {
                String myNick = "Свой ник";
                float myNickWidth = suisse_intl.getWidth(myNick, 12.0f);
                StringTextComponent playerPrefix = new StringTextComponent("");
                if (StringUtils.prefix(staff.getPrefix().getString().replace(" ", "")) != null) {
                    playerPrefix.append(StringUtils.prefix(staff.getPrefix().getString().replace(" ", "")));
                    playerPrefix.appendString(" ");
                }
                playerPrefix.appendString(playerName);
                float playerNameWidth = suisse_intl.getWidth(playerPrefix, 12.0f);
                textWidth = myNickWidth + 6.0f + playerNameWidth;
            } else {
                StringTextComponent prefix = new StringTextComponent("");
                if (StringUtils.prefix(staff.getPrefix().getString().replace(" ", "")) != null) {
                    prefix.append(StringUtils.prefix(staff.getPrefix().getString().replace(" ", "")));
                    prefix.appendString(" ");
                }
                prefix.appendString(playerName);
                textWidth = suisse_intl.getWidth(prefix, 12.0f);
            }
            float totalRowWidth = statusLetterWidth + textWidth + 24.0f;
            staticWidth = Math.max(staticWidth, totalRowWidth);
        }
        this.width = Animation.animate(this.width, staticWidth);
        this.height = Animation.animate(this.height, staticHeight);
        this.getShowAnimation().animate(0.0f, 1.0f, 0.2f, EasingList.CIRC_OUT, StaffList.mc.getTimer().renderPartialTicks);
        float headerHeight = 28.0f;
        float extraHeight = 8.0f;
        float totalHeight = this.height + extraHeight;
        if ((double)this.getShowAnimation().getAnimationValue() > 0.1) {
            int glowColor = ColorHelpers.getColorWithAlpha(ColorHelpers.getThemeColor(1), 100.0f * this.getShowAnimation().getAnimationValue());
            VisualHelpers.drawShadow(x - 2, y - 2, this.width + 4, totalHeight + 4, 12, glowColor);
            float bindsHeight = totalHeight - headerHeight;
            int headerColor = ColorHelpers.rgba(0, 0, 0, (int)(255.0f * this.getShowAnimation().getAnimationValue()));
            VisualHelpers.drawRoundedRect(x, y, this.width, headerHeight + 1, new Vector4f(0.0f, 8.0f, 0.0f, 8.0f), headerColor);
            int bindsColor = ColorHelpers.rgba(7, 7, 7, (int)(255.0f * this.getShowAnimation().getAnimationValue()));
            VisualHelpers.drawRoundedRect(x, y + headerHeight - 1, this.width, bindsHeight + 1, new Vector4f(8.0f, 0.0f, 8.0f, 0.0f), bindsColor);
            float time = (float)(System.currentTimeMillis() % 2000) / 2000.0f;
            String title = "Staff Statistics";
            float titleWidth = suisse_intl.getWidth(title, 13.0f);
            float currentX = x + (this.width - titleWidth) / 2;
            for (int i = 0; i < title.length(); i++) {
                float wave = (float)Math.sin(time * Math.PI * 2 + i * 0.5f) * 0.5f + 0.5f;
                int charColor = ColorHelpers.rgba(
                        (int)(ColorHelpers.getRed(ColorHelpers.getThemeColor(1)) * (1 - wave) + ColorHelpers.getRed(ColorHelpers.getThemeColor(2)) * wave),
                        (int)(ColorHelpers.getGreen(ColorHelpers.getThemeColor(1)) * (1 - wave) + ColorHelpers.getGreen(ColorHelpers.getThemeColor(2)) * wave),
                        (int)(ColorHelpers.getBlue(ColorHelpers.getThemeColor(1)) * (1 - wave) + ColorHelpers.getBlue(ColorHelpers.getThemeColor(2)) * wave),
                        255
                );
                suisse_intl.drawText(matrixStack, String.valueOf(title.charAt(i)), currentX, y + 6, ColorHelpers.setAlpha(charColor, (int)(255.0f * this.getShowAnimation().getAnimationValue())), 13.0f);
                currentX += suisse_intl.getWidth(String.valueOf(title.charAt(i)), 13.0f);
            }
            float i = headerHeight + 6.0f;
            if (this.staffPlayers.isEmpty()) {
                if (showExamples) {
                    String[] exampleNames = {"AdminStaff", "ModerStaff", "HelperStaff"};
                    String[] examplePrefixKeys = {"ADMIN", "ML.MODER", "HELPER"};
                    String[] statusLetters = {"c", "c", "c"}; // Рядом, Активный, Спектатор
                    int[] statusColors = {
                        ColorHelpers.rgba(255, 255, 0, (int)(255.0f * this.getShowAnimation().getAnimationValue())), // Жёлтый - рядом
                        ColorHelpers.rgba(124, 252, 0, (int)(255.0f * this.getShowAnimation().getAnimationValue())), // Лайм - активный
                        ColorHelpers.rgba(255, 165, 0, (int)(255.0f * this.getShowAnimation().getAnimationValue())) // Оранжевый - спектатор
                    };
                    int currentExample = (int)(System.currentTimeMillis() / 2000) % exampleNames.length;
                    float rowY = y + i;
                    gui_ener.drawText(matrixStack, statusLetters[currentExample], x + 6, rowY + 2, statusColors[currentExample], 14.0f);
                    StringTextComponent examplePrefix = new StringTextComponent("");
                    if (StringUtils.prefix(examplePrefixKeys[currentExample]) != null) {
                        examplePrefix.append(StringUtils.prefix(examplePrefixKeys[currentExample]));
                        examplePrefix.appendString(" ");
                    }
                    examplePrefix.appendString(exampleNames[currentExample]);
                    float textX = x + 5 + 20;
                    suisse_intl.drawText(matrixStack, examplePrefix, textX, rowY + 2, 12.0f, 255.0f * this.getShowAnimation().getAnimationValue());
                    i += rowHeight;
                } else {
                    String emptyText = "Нет стаффов онлайн";
                    float emptyTextWidth = suisse_intl.getWidth(emptyText, 12.0f);
                    float emptyTextX = x + (this.width - emptyTextWidth) / 2;
                    int emptyTextColor = ColorHelpers.rgba(150, 150, 150, (int)(255.0f * this.getShowAnimation().getAnimationValue()));
                    suisse_intl.drawText(matrixStack, emptyText, emptyTextX, y + i + -5, emptyTextColor, 12.0f);
                }
            }
            for (Staff staff : this.staffPlayers) {
                String playerName = staff.getName();
                float rowY = y + i;
                String statusLetter = staff.getStatus().string;
                int statusColor = ColorHelpers.rgba(255, 255, 255, (int)(255.0f * this.getShowAnimation().getAnimationValue()));
                
                // Определяем цвет и символ статуса
                switch (staff.getStatus()) {
                    case VANISHED:
                        statusLetter = "c";
                        statusColor = ColorHelpers.rgba(255, 85, 85, (int)(255.0f * this.getShowAnimation().getAnimationValue())); // Красный
                        break;
                    case NEARBY:
                        statusLetter = "c";
                        statusColor = ColorHelpers.rgba(255, 255, 0, (int)(255.0f * this.getShowAnimation().getAnimationValue())); // Жёлтый
                        break;
                    case SPECTATOR:
                        statusLetter = "c";
                        statusColor = ColorHelpers.rgba(255, 165, 0, (int)(255.0f * this.getShowAnimation().getAnimationValue())); // Оранжевый
                        break;
                    case GAMEMODE3:
                        statusLetter = "c";
                        statusColor = ColorHelpers.rgba(255, 140, 0, (int)(255.0f * this.getShowAnimation().getAnimationValue())); // Тёмно-оранжевый
                        break;

                    case ACTIVE:
                        statusLetter = "c";
                        statusColor = ColorHelpers.rgba(124, 252, 0, (int)(255.0f * this.getShowAnimation().getAnimationValue())); // Лайм
                        break;
                    default:
                        statusLetter = "c";
                        statusColor = ColorHelpers.rgba(200, 200, 200, (int)(255.0f * this.getShowAnimation().getAnimationValue())); // Серый
                        break;
                }
                
                if (!statusLetter.isEmpty()) {
                    gui_ener.drawText(matrixStack, statusLetter, x + 6, rowY + 2, statusColor, 14.0f);
                }
                float textX = x + 6 + 20;
                if (staffManagers.is(playerName)) {
                    String myNick = "Свой ник";
                    float nickX = textX;
                    for (int j = 0; j < myNick.length(); j++) {
                        float wave = (float)Math.sin(time * Math.PI * 2 + j * 0.5f) * 0.5f + 0.5f;
                        int charColor = ColorHelpers.interpolateColor(ColorHelpers.getThemeColor(1), ColorHelpers.getThemeColor(2), wave);
                        suisse_intl.drawText(matrixStack, String.valueOf(myNick.charAt(j)), nickX, rowY + 2, charColor, 12.0f);
                        nickX += suisse_intl.getWidth(String.valueOf(myNick.charAt(j)), 12.0f);
                    }
                    StringTextComponent playerPrefix = new StringTextComponent("");
                    if (StringUtils.prefix(staff.getPrefix().getString().replace(" ", "")) != null) {
                        playerPrefix.append(StringUtils.prefix(staff.getPrefix().getString().replace(" ", "")));
                        playerPrefix.appendString(" ");
                    }
                    playerPrefix.appendString(playerName);
                    suisse_intl.drawText(matrixStack, playerPrefix, nickX + 6, rowY + 2, 12.0f, 255.0f * this.getShowAnimation().getAnimationValue());
                } else {
                    StringTextComponent prefix = new StringTextComponent("");
                    if (StringUtils.prefix(staff.getPrefix().getString().replace(" ", "")) != null) {
                        prefix.append(StringUtils.prefix(staff.getPrefix().getString().replace(" ", "")));
                        prefix.appendString(" ");
                    }
                    prefix.appendString(playerName);
                    suisse_intl.drawText(matrixStack, prefix, textX, rowY + 2, 12.0f, 255.0f * this.getShowAnimation().getAnimationValue());
                }
                i += rowHeight;
            }
            this.getDraggableOption().setWidth(this.width);
            this.getDraggableOption().setHeight(totalHeight);
        }
    }

    public static class Staff {
        ITextComponent prefix;
        String name;
        boolean isSpec;
        Status status;

        public void updateStatus() {
            // Проверяем если игрок онлайн
            for (NetworkPlayerInfo info : IFastAccess.mc.getConnection().getPlayerInfoMap()) {
                if (!info.getGameProfile().getName().equals(this.name)) continue;
                
                // Проверяем игровой режим
                if (info.getGameType() == GameType.SPECTATOR) {
                    this.status = Status.SPECTATOR;
                    return;
                } else if (info.getGameType() == GameType.CREATIVE) {
                    this.status = Status.GAMEMODE3; // Креатив как ГМ1, но будем считать как особый статус
                    return;
                }
                
                // Проверяем расстояние до игрока (если в мире)
                if (IFastAccess.mc.world != null && IFastAccess.mc.player != null) {
                    try {
                        PlayerEntity staffPlayer = null;
                        for (PlayerEntity player : IFastAccess.mc.world.getPlayers()) {
                            if (player.getName().getString().equals(this.name)) {
                                staffPlayer = player;
                                break;
                            }
                        }
                        
                        if (staffPlayer != null) {
                            double distance = IFastAccess.mc.player.getDistanceSq(staffPlayer);
                            if (distance <= 100.0) { // В радиусе 10 блоков (100 = 10^2)
                                this.status = Status.NEARBY;
                                return;
                            } else {
                                this.status = Status.ACTIVE; // Активный, но далеко
                                return;
                            }
                        }
                    } catch (Exception e) {
                        // Fallback
                    }
                }
                
                this.status = Status.ACTIVE; // По умолчанию активный
                return;
            }
            
            // Если не найден в списке игроков - скрытый
            this.status = Status.VANISHED;
        }

        @Generated
        public Staff(ITextComponent prefix, String name, boolean isSpec, Status status) {
            this.prefix = prefix;
            this.name = name;
            this.isSpec  = isSpec;
            this.status = status;
        }

        @Generated
        public ITextComponent getPrefix() {
            return this.prefix;
        }

        @Generated
        public String getName() {
            return this.name;
        }

        @Generated
        public boolean isSpec() {
            return this.isSpec;
        }

        @Generated
        public Status getStatus() {
            return this.status;
        }

        @Generated
        public void setPrefix(ITextComponent prefix) {
            this.prefix = prefix;
        }

        @Generated
        public void setName(String name) {
            this.name = name;
        }

        @Generated
        public void setSpec(boolean isSpec) {
            this.isSpec = isSpec;
        }

        @Generated
        public void setStatus(Status status) {
            this.status = status;
        }

        @Generated
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof Staff)) {
                return false;
            }
            Staff other = (Staff)o;
            if (!other.canEqual(this)) {
                return false;
            }
            if (this.isSpec() != other.isSpec()) {
                return false;
            }
            ITextComponent this$prefix = this.getPrefix();
            ITextComponent other$prefix = other.getPrefix();
            if (this$prefix == null ? other$prefix != null : !this$prefix.equals(other$prefix)) {
                return false;
            }
            String this$name = this.getName();
            String other$name = other.getName();
            if (this$name == null ? other$name != null : !this$name.equals(other$name)) {
                return false;
            }
            Status this$status = this.getStatus();
            Status other$status = other.getStatus();
            return !(this$status == null ? other$status != null : !((Object)((Object)this$status)).equals((Object)other$status));
        }

        @Generated
        protected boolean canEqual(Object other) {
            return other instanceof Staff;
        }

        @Generated
        public int hashCode() {
            int result = 1;
            result = result * 59 + (this.isSpec() ? 79 : 97);
            ITextComponent $prefix = this.getPrefix();
            result = result * 59 + ($prefix == null ? 43 : $prefix.hashCode());
            String $name = this.getName();
            result = result * 59 + ($name == null ? 43 : $name.hashCode());
            Status $status = this.getStatus();
            result = result * 59 + ($status == null ? 43 : ((Object)((Object)$status)).hashCode());
            return result;
        }

        @Generated
        public String toString() {
            return "StaffList.Staff(prefix=" + String.valueOf(this.getPrefix()) + ", name=" + this.getName() + ", isSpec=" + this.isSpec() + ", status=" + String.valueOf((Object)this.getStatus()) + ")";
        }
    }

    public static enum Status {
        NONE("", -1),
        VANISHED("s", ColorHelpers.rgb(254, 68, 68)),
        NEARBY("v", ColorHelpers.rgb(255, 255, 0)), // Жёлтый - рядом
        SPECTATOR("v", ColorHelpers.rgb(255, 165, 0)), // Оранжевый - спектатор
        GAMEMODE3("v", ColorHelpers.rgb(255, 140, 0)), // Тёмно-оранжевый - ГМ3
        ACTIVE("v", ColorHelpers.rgb(124, 252, 0)); // Лайм - активный

        public final String string;
        public final int color;

        private Status(String string2, int color) {
            this.string = string2;
            this.color = color;
        }
    }
}