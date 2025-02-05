package legitish.utils.render;

import legitish.utils.ColorUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class RRectUtils {
    private static final ShaderUtils roundedGradientShader = new ShaderUtils("legitish/shaders/rrectGradient.frag");
    public static ShaderUtils roundedShader = new ShaderUtils("legitish/shaders/rrect.frag");
    public static ShaderUtils roundedOutlineShader = new ShaderUtils("legitish/shaders/rrectOutline.frag");

    public static void drawGradientRoundCorner(double x, double y, double width, double height, double radius) {
        ColorUtils.resetColor();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        roundedGradientShader.init();
        setupRoundedRectUniforms(x, y, width, height, radius, roundedGradientShader);
        roundedGradientShader.setUniformf("color1", ColorUtils.getBackgroundColor(1).getRed() / 255f, ColorUtils.getBackgroundColor(1).getGreen() / 255f, ColorUtils.getBackgroundColor(1).getBlue() / 255f, ColorUtils.getBackgroundColor(1).getAlpha() / 255f);
        roundedGradientShader.setUniformf("color2", ColorUtils.getBackgroundColor(2).getRed() / 255f, ColorUtils.getBackgroundColor(2).getGreen() / 255f, ColorUtils.getBackgroundColor(2).getBlue() / 255f, ColorUtils.getBackgroundColor(2).getAlpha() / 255f);
        roundedGradientShader.setUniformf("color3", ColorUtils.getBackgroundColor(2).getRed() / 255f, ColorUtils.getBackgroundColor(2).getGreen() / 255f, ColorUtils.getBackgroundColor(2).getBlue() / 255f, ColorUtils.getBackgroundColor(2).getAlpha() / 255f);
        roundedGradientShader.setUniformf("color4", ColorUtils.getBackgroundColor(1).getRed() / 255f, ColorUtils.getBackgroundColor(1).getGreen() / 255f, ColorUtils.getBackgroundColor(1).getBlue() / 255f, ColorUtils.getBackgroundColor(1).getAlpha() / 255f);
        ShaderUtils.drawQuads(x - 1, y - 1, width + 2, height + 2);
        roundedGradientShader.unload();
        GlStateManager.disableBlend();
    }

    public static void drawRound(double x, double y, double width, double height, double radius, Color color) {
        GlStateManager.color(1, 1, 1, 1);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        roundedShader.init();

        setupRoundedRectUniforms(x, y, width, height, radius, roundedShader);
        roundedShader.setUniformf("color", color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);

        ShaderUtils.drawQuads(x - 1, y - 1, width + 2, height + 2);
        roundedShader.unload();
        GlStateManager.disableBlend();
    }

    public static void drawRoundOutline(double x, double y, double width, double height, double radius, double outlineThickness, Color color, Color outlineColor) {
        GlStateManager.color(1, 1, 1, 1);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        roundedOutlineShader.init();

        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        setupRoundedRectUniforms(x, y, width, height, radius, roundedOutlineShader);
        roundedOutlineShader.setUniformf("outlineThickness", outlineThickness * sr.getScaleFactor());
        roundedOutlineShader.setUniformf("color", color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);
        roundedOutlineShader.setUniformf("outlineColor", outlineColor.getRed() / 255f, outlineColor.getGreen() / 255f, outlineColor.getBlue() / 255f, outlineColor.getAlpha() / 255f);

        ShaderUtils.drawQuads(x - (2 + outlineThickness), y - (2 + outlineThickness), width + (4 + outlineThickness * 2), height + (4 + outlineThickness * 2));
        roundedOutlineShader.unload();
        GlStateManager.disableBlend();
    }

    private static void setupRoundedRectUniforms(double x, double y, double width, double height, double radius, ShaderUtils roundedTexturedShader) {
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        roundedTexturedShader.setUniformf("location", x * sr.getScaleFactor(), (Minecraft.getMinecraft().displayHeight - (height * sr.getScaleFactor())) - (y * sr.getScaleFactor()));
        roundedTexturedShader.setUniformf("rectSize", width * sr.getScaleFactor(), height * sr.getScaleFactor());
        roundedTexturedShader.setUniformf("radius", radius * sr.getScaleFactor());
    }
}
