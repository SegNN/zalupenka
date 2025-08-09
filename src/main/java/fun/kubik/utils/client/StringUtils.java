/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.utils.client;

import fun.kubik.managers.font.Font;
import java.util.HashMap;
import lombok.Generated;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponent;
import net.minecraft.util.text.TextFormatting;

public final class StringUtils {
    public static String trim(String text, float width, Font font, float size) {
        StringBuilder trimmedText = new StringBuilder();
        for (char c : text.toCharArray()) {
            trimmedText.append(c);
            if (!(font.getWidth(String.valueOf(trimmedText) + "...", size) > width)) continue;
            if (trimmedText.length() <= 3) break;
            trimmedText.setLength(trimmedText.length() - 3);
            if (trimmedText.charAt(trimmedText.length() - 1) == ',') {
                trimmedText.setLength(trimmedText.length() - 1);
            }
            if (trimmedText.charAt(trimmedText.length() - 1) == ' ') {
                trimmedText.setLength(trimmedText.length() - 1);
            }
            trimmedText.append("...");
            break;
        }
        return trimmedText.toString();
    }

    public static String smallCaps(String text) {
        HashMap<Character, Character> key = new HashMap<Character, Character>();
        key.put(Character.valueOf('\u1d00'), Character.valueOf('a'));
        key.put(Character.valueOf('\u0299'), Character.valueOf('b'));
        key.put(Character.valueOf('\u1d04'), Character.valueOf('c'));
        key.put(Character.valueOf('\u1d05'), Character.valueOf('d'));
        key.put(Character.valueOf('\u1d07'), Character.valueOf('e'));
        key.put(Character.valueOf('\ua730'), Character.valueOf('f'));
        key.put(Character.valueOf('\u0262'), Character.valueOf('g'));
        key.put(Character.valueOf('\u029c'), Character.valueOf('h'));
        key.put(Character.valueOf('\u026a'), Character.valueOf('i'));
        key.put(Character.valueOf('\u1d0a'), Character.valueOf('j'));
        key.put(Character.valueOf('\u1d0b'), Character.valueOf('k'));
        key.put(Character.valueOf('\u029f'), Character.valueOf('l'));
        key.put(Character.valueOf('\u1d0d'), Character.valueOf('m'));
        key.put(Character.valueOf('\u0274'), Character.valueOf('n'));
        key.put(Character.valueOf('\u1d0f'), Character.valueOf('o'));
        key.put(Character.valueOf('\u1d18'), Character.valueOf('p'));
        key.put(Character.valueOf('\u01eb'), Character.valueOf('q'));
        key.put(Character.valueOf('\u0280'), Character.valueOf('r'));
        key.put(Character.valueOf('\ua731'), Character.valueOf('s'));
        key.put(Character.valueOf('\u1d1b'), Character.valueOf('t'));
        key.put(Character.valueOf('\u1d1c'), Character.valueOf('u'));
        key.put(Character.valueOf('\u1d20'), Character.valueOf('v'));
        key.put(Character.valueOf('\u1d21'), Character.valueOf('w'));
        key.put(Character.valueOf('x'), Character.valueOf('x'));
        key.put(Character.valueOf('\u028f'), Character.valueOf('y'));
        key.put(Character.valueOf('\u1d22'), Character.valueOf('z'));
        StringBuilder builder = new StringBuilder();
        for (char c : text.toCharArray()) {
            builder.append(key.get(Character.valueOf(c)));
        }
        return builder.toString();
    }

    public static TextComponent prefix(String text) {
        HashMap<String, TextComponent> key = new HashMap<String, TextComponent>();
        key.put("\u00a7c\u25cf\u00a7f\u00a7f\ua500\u00a77\u00a77", (TextComponent)new StringTextComponent("PLAYER").mergeStyle(TextFormatting.GRAY));
        key.put("\u00a7a\u25cf\u00a7f\u00a7f\ua500\u00a77\u00a77", (TextComponent)new StringTextComponent("PLAYER").mergeStyle(TextFormatting.GRAY));
        key.put("\u00a7c\u25cf\u00a7f\u00a7f\ua504\u00a77\u00a77", (TextComponent)new StringTextComponent("HERO").mergeStyle(TextFormatting.BLUE));
        key.put("\u00a7a\u25cf\u00a7f\u00a7f\ua504\u00a77\u00a77", (TextComponent)new StringTextComponent("HERO").mergeStyle(TextFormatting.BLUE));
        key.put("\u00a7c\u25cf\u00a7f\u00a7f\ua508\u00a77\u00a77", (TextComponent)new StringTextComponent("TITAN").mergeStyle(TextFormatting.YELLOW));
        key.put("\u00a7a\u25cf\u00a7f\u00a7f\ua508\u00a77\u00a77", (TextComponent)new StringTextComponent("TITAN").mergeStyle(TextFormatting.YELLOW));
        key.put("\u00a7c\u25cf\u00a7f\u00a7f\ua512\u00a77\u00a77", (TextComponent)new StringTextComponent("AVENGER").mergeStyle(TextFormatting.GREEN));
        key.put("\u00a7a\u25cf\u00a7f\u00a7f\ua512\u00a77\u00a77", (TextComponent)new StringTextComponent("AVENGER").mergeStyle(TextFormatting.GREEN));
        key.put("\u00a7c\u25cf\u00a7f\u00a7f\ua516\u00a77\u00a77", (TextComponent)new StringTextComponent("OVERLORD").mergeStyle(TextFormatting.AQUA));
        key.put("\u00a7a\u25cf\u00a7f\u00a7f\ua516\u00a77\u00a77", (TextComponent)new StringTextComponent("OVERLORD").mergeStyle(TextFormatting.AQUA));
        key.put("\u00a7c\u25cf\u00a7f\u00a7f\ua520\u00a77\u00a77", (TextComponent)new StringTextComponent("MAGISTER").mergeStyle(TextFormatting.GOLD));
        key.put("\u00a7a\u25cf\u00a7f\u00a7f\ua520\u00a77\u00a77", (TextComponent)new StringTextComponent("MAGISTER").mergeStyle(TextFormatting.GOLD));
        key.put("\u00a7c\u25cf\u00a7f\u00a7f\ua524\u00a77\u00a77", (TextComponent)new StringTextComponent("IMPERATOR").mergeStyle(TextFormatting.RED));
        key.put("\u00a7a\u25cf\u00a7f\u00a7f\ua524\u00a77\u00a77", (TextComponent)new StringTextComponent("IMPERATOR").mergeStyle(TextFormatting.RED));
        key.put("\u00a7c\u25cf\u00a7f\u00a7f\ua528\u00a77\u00a77", (TextComponent)new StringTextComponent("DRAGON").mergeStyle(TextFormatting.LIGHT_PURPLE));
        key.put("\u00a7a\u25cf\u00a7f\u00a7f\ua528\u00a77\u00a77", (TextComponent)new StringTextComponent("DRAGON").mergeStyle(TextFormatting.LIGHT_PURPLE));
        key.put("\u00a7c\u25cf\u00a7f\u00a7f\ua532\u00a77\u00a77", (TextComponent)new StringTextComponent("BULL").mergeStyle(TextFormatting.DARK_PURPLE));
        key.put("\u00a7a\u25cf\u00a7f\u00a7f\ua532\u00a77\u00a77", (TextComponent)new StringTextComponent("BULL").mergeStyle(TextFormatting.DARK_PURPLE));
        key.put("\u00a7c\u25cf\u00a7f\u00a7f\ua536\u00a77\u00a77", (TextComponent)new StringTextComponent("TIGER").mergeStyle(TextFormatting.GOLD));
        key.put("\u00a7a\u25cf\u00a7f\u00a7f\ua536\u00a77\u00a77", (TextComponent)new StringTextComponent("TIGER").mergeStyle(TextFormatting.GOLD));
        key.put("\u00a7c\u25cf\u00a7f\u00a7f\ua540\u00a77\u00a77", (TextComponent)new StringTextComponent("HYDRA").mergeStyle(TextFormatting.DARK_GREEN));
        key.put("\u00a7a\u25cf\u00a7f\u00a7f\ua540\u00a77\u00a77", (TextComponent)new StringTextComponent("HYDRA").mergeStyle(TextFormatting.DARK_GREEN));
        key.put("\u00a7c\u25cf\u00a7f\u00a7f\ua544\u00a77\u00a77", (TextComponent)new StringTextComponent("DRACULA").mergeStyle(TextFormatting.DARK_RED));
        key.put("\u00a7a\u25cf\u00a7f\u00a7f\ua544\u00a77\u00a77", (TextComponent)new StringTextComponent("DRACULA").mergeStyle(TextFormatting.DARK_RED));
        key.put("\u00a7c\u25cf\u00a7f\u00a7f\ua548\u00a77\u00a77", (TextComponent)new StringTextComponent("COBRA").mergeStyle(TextFormatting.GREEN));
        key.put("\u00a7a\u25cf\u00a7f\u00a7f\ua548\u00a77\u00a77", (TextComponent)new StringTextComponent("COBRA").mergeStyle(TextFormatting.GREEN));
        key.put("\u00a7c\u25cf\u00a7f\u00a7f\ua552\u00a77\u00a77", (TextComponent)new StringTextComponent("RABBIT").mergeStyle(TextFormatting.WHITE));
        key.put("\u00a7a\u25cf\u00a7f\u00a7f\ua552\u00a77\u00a77", (TextComponent)new StringTextComponent("RABBIT").mergeStyle(TextFormatting.WHITE));
        key.put("\u00a7c\u25cf\u00a7f\u00a7f\ua556\u00a77\u00a77", (TextComponent)new StringTextComponent("BUNNY").mergeStyle(TextFormatting.BLACK));
        key.put("\u00a7a\u25cf\u00a7f\u00a7f\ua556\u00a77\u00a77", (TextComponent)new StringTextComponent("BUNNY").mergeStyle(TextFormatting.BLACK));
        key.put("\u00a7c\u25cf\u00a7f\u00a7f\ua560\u00a77\u00a77", (TextComponent)new StringTextComponent("D.HELPER").mergeStyle(TextFormatting.YELLOW));
        key.put("\u00a7a\u25cf\u00a7f\u00a7f\ua560\u00a77\u00a77", (TextComponent)new StringTextComponent("D.HELPER").mergeStyle(TextFormatting.YELLOW));
        key.put("\u00a7c\u25cf\u00a7f\u00a7f\ua509\u00a77\u00a77", (TextComponent)new StringTextComponent("HELPER").mergeStyle(TextFormatting.YELLOW));
        key.put("\u00a7a\u25cf\u00a7f\u00a7f\ua509\u00a77\u00a77", (TextComponent)new StringTextComponent("HELPER").mergeStyle(TextFormatting.YELLOW));
        key.put("\u00a7c\u25cf\u00a7f\u00a7f\ua513\u00a77\u00a77", (TextComponent)new StringTextComponent("ML.MODER").mergeStyle(TextFormatting.BLUE));
        key.put("\u00a7a\u25cf\u00a7f\u00a7f\ua513\u00a77\u00a77", (TextComponent)new StringTextComponent("ML.MODER").mergeStyle(TextFormatting.BLUE));
        key.put("\u00a7c\u25cf\u00a7f\u00a7f\ua517\u00a77\u00a77", (TextComponent)new StringTextComponent("MODER").mergeStyle(TextFormatting.BLUE));
        key.put("\u00a7a\u25cf\u00a7f\u00a7f\ua517\u00a77\u00a77", (TextComponent)new StringTextComponent("MODER").mergeStyle(TextFormatting.BLUE));
        key.put("\u00a7c\u25cf\u00a7f\u00a7f\ua521\u00a77\u00a77", (TextComponent)new StringTextComponent("MODER+").mergeStyle(TextFormatting.DARK_PURPLE));
        key.put("\u00a7a\u25cf\u00a7f\u00a7f\ua521\u00a77\u00a77", (TextComponent)new StringTextComponent("MODER+").mergeStyle(TextFormatting.DARK_PURPLE));
        key.put("\u00a7c\u25cf\u00a7f\u00a7f\ua525\u00a77\u00a77", (TextComponent)new StringTextComponent("ST.MODER").mergeStyle(TextFormatting.BLUE));
        key.put("\u00a7a\u25cf\u00a7f\u00a7f\ua525\u00a77\u00a77", (TextComponent)new StringTextComponent("ST.MODER").mergeStyle(TextFormatting.BLUE));
        key.put("\u00a7c\u25cf\u00a7f\u00a7f\ua529\u00a77\u00a77", (TextComponent)new StringTextComponent("GL.MODER").mergeStyle(TextFormatting.DARK_PURPLE));
        key.put("\u00a7a\u25cf\u00a7f\u00a7f\ua529\u00a77\u00a77", (TextComponent)new StringTextComponent("GL.MODER").mergeStyle(TextFormatting.DARK_PURPLE));
        key.put("\u00a7c\u25cf\u00a7f\u00a7f\ua533\u00a77\u00a77", (TextComponent)new StringTextComponent("ML.ADMIN").mergeStyle(TextFormatting.AQUA));
        key.put("\u00a7a\u25cf\u00a7f\u00a7f\ua533\u00a77\u00a77", (TextComponent)new StringTextComponent("ML.ADMIN").mergeStyle(TextFormatting.AQUA));
        key.put("\u00a7c\u25cf\u00a7f\u00a7f\ua537\u00a77\u00a77", (TextComponent)new StringTextComponent("ADMIN").mergeStyle(TextFormatting.RED));
        key.put("\u00a7a\u25cf\u00a7f\u00a7f\ua537\u00a77\u00a77", (TextComponent)new StringTextComponent("ADMIN").mergeStyle(TextFormatting.RED));
        key.put("\u00a7c\u25cf\u00a7f\u00a7f\ua501\u00a77\u00a77", (TextComponent)new StringTextComponent("MEDIA").mergeStyle(TextFormatting.DARK_PURPLE));
        key.put("\u00a7a\u25cf\u00a7f\u00a7f\ua501\u00a77\u00a77", (TextComponent)new StringTextComponent("MEDIA").mergeStyle(TextFormatting.DARK_PURPLE));
        StringTextComponent yt = new StringTextComponent("");
        yt.append(new StringTextComponent("Y").mergeStyle(TextFormatting.RED));
        yt.append(new StringTextComponent("T").mergeStyle(TextFormatting.WHITE));
        key.put("\u00a7c\u25cf\u00a7f\u00a7f\ua505\u00a77\u00a77", yt);
        key.put("\u00a7a\u25cf\u00a7f\u00a7f\ua505\u00a77\u00a77", yt);
        return (TextComponent)key.get(text);
    }

    @Generated
    private StringUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}

