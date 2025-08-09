/* Decompiler 219ms, total 506ms, lines 299 */
package fun.kubik.helpers.render;

import com.mojang.blaze3d.systems.RenderSystem;
import fun.kubik.Load;
import fun.kubik.utils.math.MathUtils;
import java.awt.Color;
import java.nio.ByteBuffer;
import lombok.Generated;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

public final class ColorHelpers {
    public static int COLOR_NONE = rgbaFloat(1.0F, 1.0F, 1.0F, 1.0F);

    public static int setAlphaColor(int color, float alpha) {
        float red = (float)(color >> 16 & 255) / 255.0F;
        float green = (float)(color >> 8 & 255) / 255.0F;
        float blue = (float)(color & 255) / 255.0F;
        RenderSystem.color4f(red, green, blue, alpha);
        return color;
    }

    public static int setAlpha(int color, int alpha) {
        return MathHelper.clamp(alpha, 0, 255) << 24 | color & 16777215;
    }

    public static void setColor(int color) {
        setAlphaColor(color, (float)(color >> 24 & 255) / 255.0F);
    }

    public static float[] rgb(int color) {
        return new float[]{(float)(color >> 16 & 255) / 255.0F, (float)(color >> 8 & 255) / 255.0F, (float)(color & 255) / 255.0F, (float)(color >> 24 & 255) / 255.0F};
    }

    public static Color random() {
        return new Color(Color.HSBtoRGB((float)Math.random(), (float)(0.75D + Math.random() / 4.0D), (float)(0.75D + Math.random() / 4.0D)));
    }

    public static int getRed(int hex) {
        return hex >> 16 & 255;
    }

    public static int getGreen(int hex) {
        return hex >> 8 & 255;
    }

    public static int getBlue(int hex) {
        return hex & 255;
    }

    public static int getAlpha(int hex) {
        return hex >> 24 & 255;
    }

    public static StringTextComponent gradient(String message, int first, int end) {
        StringTextComponent text = new StringTextComponent("");

        for(int i = 0; i < message.length(); ++i) {
            text.append((new StringTextComponent(String.valueOf(message.charAt(i)))).setStyle(Style.EMPTY.setColor(new net.minecraft.util.text.Color(interpolateColor(first, end, (double)((float)i / (float)message.length()))))));
        }

        return text;
    }

    public static float[] getRGBAf(int color) {
        return new float[]{(float)(color >> 16 & 255) / 255.0F, (float)(color >> 8 & 255) / 255.0F, (float)(color & 255) / 255.0F, (float)(color >> 24 & 255) / 255.0F};
    }

    public static int getColorWithDarkness(int color, float darkness) {
        float[] rgb = getRGBAf(color);
        return rgba((int)(rgb[0] * 255.0F / darkness), (int)(rgb[1] * 255.0F / darkness), (int)(rgb[2] * 255.0F / darkness), (int)(rgb[3] * 255.0F));
    }

    public static int getColorWithAlpha(int color, float alpha) {
        float[] rgb = getRGBAf(color);
        return rgba((int)(rgb[0] * 255.0F), (int)(rgb[1] * 255.0F), (int)(rgb[2] * 255.0F), (int)alpha);
    }

    public static int getColorWithAlpha(int color, double alpha) {
        float[] rgb = getRGBAf(color);
        return rgba((int)(rgb[0] * 255.0F), (int)(rgb[1] * 255.0F), (int)(rgb[2] * 255.0F), (int)alpha);
    }

    public static int interpolateColor(int color1, int color2, double offset) {
        float[] rgba1 = getRGBAf(color1);
        float[] rgba2 = getRGBAf(color2);
        double r = (double)rgba1[0] + (double)(rgba2[0] - rgba1[0]) * offset;
        double g = (double)rgba1[1] + (double)(rgba2[1] - rgba1[1]) * offset;
        double b = (double)rgba1[2] + (double)(rgba2[2] - rgba1[2]) * offset;
        double a = (double)rgba1[3] + (double)(rgba2[3] - rgba1[3]) * offset;
        return rgba((int)(r * 255.0D), (int)(g * 255.0D), (int)(b * 255.0D), (int)(a * 255.0D));
    }

    public static int interpolateColorsBackAndForth(int speed, int index, int start, int end) {
        int angle = (int)((System.currentTimeMillis() / (long)speed + (long)index) % 360L);
        angle = (angle >= 180 ? 360 - angle : angle) * 2;
        return interpolateColor(start, end, (double)((float)angle / 360.0F));
    }

    public static int rgba(int r, int g, int b, int a) {
        return a << 24 | r << 16 | g << 8 | b;
    }

    public static int rgba(int r, int g, int b, float a) {
        return (int)a << 24 | r << 16 | g << 8 | b;
    }

    public static int rgba(int r, int g, int b, double a) {
        return (int)a << 24 | r << 16 | g << 8 | b;
    }

    public static int rgb(int r, int g, int b) {
        return -16777216 | r << 16 | g << 8 | b;
    }

    public static int rgbaFloat(float r, float g, float b, float a) {
        return (int)(MathUtils.clamp(a, 0.0F, 1.0F) * 255.0F) << 24 | (int)(MathUtils.clamp(r, 0.0F, 1.0F) * 255.0F) << 16 | (int)(MathUtils.clamp(g, 0.0F, 1.0F) * 255.0F) << 8 | (int)(MathUtils.clamp(b, 0.0F, 1.0F) * 255.0F);
    }

    public static int rgbFloat(float r, float g, float b) {
        return -16777216 | (int)(MathUtils.clamp(r, 0.0F, 1.0F) * 255.0F) << 16 | (int)(MathUtils.clamp(g, 0.0F, 1.0F) * 255.0F) << 8 | (int)(MathUtils.clamp(b, 0.0F, 1.0F) * 255.0F);
    }

    public static String RGBtoHEXString(int color) {
        return Integer.toHexString(color).substring(2);
    }

    public static int getColorFromPixel(int x, int y) {
        ByteBuffer rgb = BufferUtils.createByteBuffer(3);
        GL11.glReadPixels(x, y, 1, 1, 6407, 5121, rgb);
        return rgb(rgb.get(0) & 255, rgb.get(1) & 255, rgb.get(2) & 255);
    }

    public static int HUEtoRGB(int value) {
        float hue = (float)value / 360.0F;
        return Color.HSBtoRGB(hue, 1.0F, 1.0F);
    }

    public static int HSBtoRGB(float hue, float saturation, float brightness) {
        int r = 0;
        int g = 0;
        int b = 0;
        if (saturation == 0.0F) {
            r = g = b = (int)(brightness * 255.0F + 0.5F);
        } else {
            float h = (hue - (float)Math.floor((double)hue)) * 6.0F;
            float f = h - (float)Math.floor((double)h);
            float p = brightness * (1.0F - saturation);
            float q = brightness * (1.0F - saturation * f);
            float t = brightness * (1.0F - saturation * (1.0F - f));
            switch((int)h) {
                case 0:
                    r = (int)(brightness * 255.0F + 0.5F);
                    g = (int)(t * 255.0F + 0.5F);
                    b = (int)(p * 255.0F + 0.5F);
                    break;
                case 1:
                    r = (int)(q * 255.0F + 0.5F);
                    g = (int)(brightness * 255.0F + 0.5F);
                    b = (int)(p * 255.0F + 0.5F);
                    break;
                case 2:
                    r = (int)(p * 255.0F + 0.5F);
                    g = (int)(brightness * 255.0F + 0.5F);
                    b = (int)(t * 255.0F + 0.5F);
                    break;
                case 3:
                    r = (int)(p * 255.0F + 0.5F);
                    g = (int)(q * 255.0F + 0.5F);
                    b = (int)(brightness * 255.0F + 0.5F);
                    break;
                case 4:
                    r = (int)(t * 255.0F + 0.5F);
                    g = (int)(p * 255.0F + 0.5F);
                    b = (int)(brightness * 255.0F + 0.5F);
                    break;
                case 5:
                    r = (int)(brightness * 255.0F + 0.5F);
                    g = (int)(p * 255.0F + 0.5F);
                    b = (int)(q * 255.0F + 0.5F);
            }
        }

        return -16777216 | r << 16 | g << 8 | b;
    }

    public static void glHexColor(int hex, int alpha) {
        float red = (float)(hex >> 16 & 255) / 255.0F;
        float green = (float)(hex >> 8 & 255) / 255.0F;
        float blue = (float)(hex & 255) / 255.0F;
        RenderSystem.color4f(red, green, blue, (float)alpha / 255.0F);
    }

    public static void glHexColor(int hex, float alpha) {
        float red = (float)(hex >> 16 & 255) / 255.0F;
        float green = (float)(hex >> 8 & 255) / 255.0F;
        float blue = (float)(hex & 255) / 255.0F;
        RenderSystem.color4f(red, green, blue, alpha);
    }

    public static void glHexColor(int color) {
        glHexColor(color, (float)(color >> 24 & 255) / 255.0F);
    }

    public static float getSkyRainbow(float speed, int index) {
        int n = 0;
        int angle = (int)((System.currentTimeMillis() / (long)speed + (long)index) % 360L);
        float hue = (float)angle / 360.0F;
        angle = (int)((double)angle % 360.0D);
        return (float)Color.HSBtoRGB((double)((float)((double)n / 360.0D)) < 0.5D ? -((float)((double)angle / 360.0D)) : (float)((double)angle / 360.0D), 1.0F, 1.0F);
    }

    public static int astolfo(float yDist, float yTotal, float saturation, float speedt) {
        float speed = 1800.0F;

        float hue;
        for(hue = (float)(System.currentTimeMillis() % (long)((int)speed)) + (yTotal - yDist) * speedt; hue > speed; hue -= speed) {
        }

        hue /= speed;
        if ((double)hue > 0.5D) {
            hue = 0.5F - (hue - 0.5F);
        }

        hue += 0.5F;
        return Color.HSBtoRGB(hue, saturation, 1.0F);
    }

    public static int getThemeColor(int color) {
        return color > Load.getInstance().getHooks().getThemeManagers().getCurrentTheme().colors.length ? Load.getInstance().getHooks().getThemeManagers().getCurrentTheme().colors[Load.getInstance().getHooks().getThemeManagers().getCurrentTheme().colors.length - 1] : Load.getInstance().getHooks().getThemeManagers().getCurrentTheme().colors[color - 1];
    }

    public static int getTheme(int index) {
        return Load.getInstance().getHooks().getThemeManagers().getCurrentTheme().getColor(index);
    }

    public static int hexToRgb(String hex) {
        if (hex.startsWith("#")) {
            hex = hex.substring(1);
        }

        if (hex.length() != 6) {
            throw new IllegalArgumentException("Недопустимый формат HEX: " + hex);
        } else {
            int r = Integer.parseInt(hex.substring(0, 2), 16);
            int g = Integer.parseInt(hex.substring(2, 4), 16);
            int b = Integer.parseInt(hex.substring(4, 6), 16);
            return rgb(r, g, b);
        }
    }

    public static int gradient(int speed, int index, int... colors) {
        int angle = (int)((System.currentTimeMillis() / (long)speed + (long)index) % 360L);
        angle = (angle > 180 ? 360 - angle : angle) + 180;
        int colorIndex = (int)((float)angle / 360.0F * (float)colors.length);
        if (colorIndex == colors.length) {
            --colorIndex;
        }

        int color1 = colors[colorIndex];
        int color2 = colors[colorIndex == colors.length - 1 ? 0 : colorIndex + 1];
        return interpolateColor(color1, color2, (double)((float)angle / 360.0F * (float)colors.length - (float)colorIndex));
    }

    @Generated
    private ColorHelpers() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static class IntColor {
        public static float[] rgb(int color) {
            return new float[]{(float)(color >> 16 & 255) / 255.0F, (float)(color >> 8 & 255) / 255.0F, (float)(color & 255) / 255.0F, (float)(color >> 24 & 255) / 255.0F};
        }

        public static int rgba(int r, int g, int b, int a) {
            return a << 24 | r << 16 | g << 8 | b;
        }

        public static int getRed(int hex) {
            return hex >> 16 & 255;
        }

        public static int getGreen(int hex) {
            return hex >> 8 & 255;
        }

        public static int getBlue(int hex) {
            return hex & 255;
        }

        public static int getAlpha(int hex) {
            return hex >> 24 & 255;
        }
    }
}