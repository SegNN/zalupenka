/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package com.mojang.blaze3d.platform;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.systems.RenderSystem;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.shader.FramebufferConstants;
import net.minecraft.client.util.LWJGLMemoryUntracker;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.math.vector.Vector4f;
import net.optifine.Config;
import net.optifine.SmartAnimations;
import net.optifine.render.GlAlphaState;
import net.optifine.render.GlBlendState;
import net.optifine.render.GlCullState;
import net.optifine.shaders.Shaders;
import net.optifine.util.LockCounter;
import org.lwjgl.opengl.ARBCopyBuffer;
import org.lwjgl.opengl.ARBDrawBuffersBlend;
import org.lwjgl.opengl.ARBFramebufferObject;
import org.lwjgl.opengl.EXTFramebufferBlit;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;
import org.lwjgl.opengl.GL42;
import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.system.MemoryUtil;

public class GlStateManager {
    private static final FloatBuffer MATRIX_BUFFER = GLX.make(MemoryUtil.memAllocFloat(16), p_lambda$static$0_0_ -> LWJGLMemoryUntracker.untrack(MemoryUtil.memAddress(p_lambda$static$0_0_)));
    private static final AlphaState ALPHA_TEST = new AlphaState();
    private static final BooleanState LIGHTING = new BooleanState(2896);
    private static final BooleanState[] LIGHT_ENABLE = (BooleanState[])IntStream.range(0, 8).mapToObj(p_lambda$static$1_0_ -> new BooleanState(16384 + p_lambda$static$1_0_)).toArray(BooleanState[]::new);
    private static final ColorMaterialState COLOR_MATERIAL = new ColorMaterialState();
    private static final BlendState BLEND = new BlendState();
    private static final DepthState DEPTH = new DepthState();
    private static final FogState FOG = new FogState();
    private static final CullState CULL = new CullState();
    private static final PolygonOffsetState POLY_OFFSET = new PolygonOffsetState();
    private static final ColorLogicState COLOR_LOGIC = new ColorLogicState();
    private static final TexGenState TEX_GEN = new TexGenState();
    private static final StencilState STENCIL = new StencilState();
    private static final ScissorState field_244591_n = new ScissorState();
    private static final FloatBuffer FLOAT_4_BUFFER = GLAllocation.createDirectFloatBuffer(4);
    private static int activeTexture;
    private static final TextureState[] TEXTURES;
    private static int shadeModel;
    private static final BooleanState RESCALE_NORMAL;
    private static final ColorMask COLOR_MASK;
    private static final Color COLOR;
    private static FramebufferExtension fboMode;
    private static SupportType supportType;
    private static LockCounter alphaLock;
    private static GlAlphaState alphaLockState;
    private static LockCounter blendLock;
    private static GlBlendState blendLockState;
    private static LockCounter cullLock;
    private static GlCullState cullLockState;
    private static boolean clientStateLocked;
    private static int clientActiveTexture;
    private static boolean creatingDisplayList;
    public static float lastBrightnessX;
    public static float lastBrightnessY;
    public static boolean openGL31;
    public static boolean vboRegions;
    public static int GL_COPY_READ_BUFFER;
    public static int GL_COPY_WRITE_BUFFER;
    public static int GL_ARRAY_BUFFER;
    public static int GL_STATIC_DRAW;
    private static boolean fogAllowed;
    public static final int GL_QUADS = 7;
    public static final int GL_TRIANGLES = 4;
    public static final int GL_TEXTURE0 = 33984;
    public static final int GL_TEXTURE1 = 33985;
    public static final int GL_TEXTURE2 = 33986;
    private static int framebufferRead;
    private static int framebufferDraw;
    private static final int[] IMAGE_TEXTURES;

    @Deprecated
    public static void pushLightingAttributes() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glPushAttrib(8256);
    }

    @Deprecated
    public static void pushTextureAttributes() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glPushAttrib(270336);
    }

    @Deprecated
    public static void popAttributes() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glPopAttrib();
    }

    @Deprecated
    public static void disableAlphaTest() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        if (alphaLock.isLocked()) {
            alphaLockState.setDisabled();
        } else {
            GlStateManager.ALPHA_TEST.test.disable();
        }
    }

    @Deprecated
    public static void enableAlphaTest() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        if (alphaLock.isLocked()) {
            alphaLockState.setEnabled();
        } else {
            GlStateManager.ALPHA_TEST.test.enable();
        }
    }

    @Deprecated
    public static void alphaFunc(int func, float ref) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        if (alphaLock.isLocked()) {
            alphaLockState.setFuncRef(func, ref);
        } else if (func != GlStateManager.ALPHA_TEST.func || ref != GlStateManager.ALPHA_TEST.ref) {
            GlStateManager.ALPHA_TEST.func = func;
            GlStateManager.ALPHA_TEST.ref = ref;
            GL11.glAlphaFunc(func, ref);
        }
    }

    @Deprecated
    public static void enableLighting() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        LIGHTING.enable();
    }

    @Deprecated
    public static void disableLighting() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        LIGHTING.disable();
    }

    @Deprecated
    public static void enableLight(int light) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        LIGHT_ENABLE[light].enable();
    }

    @Deprecated
    public static void enableColorMaterial() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GlStateManager.COLOR_MATERIAL.colorMaterial.enable();
    }

    @Deprecated
    public static void disableColorMaterial() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GlStateManager.COLOR_MATERIAL.colorMaterial.disable();
    }

    @Deprecated
    public static void colorMaterial(int face, int mode) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        if (face != GlStateManager.COLOR_MATERIAL.face || mode != GlStateManager.COLOR_MATERIAL.mode) {
            GlStateManager.COLOR_MATERIAL.face = face;
            GlStateManager.COLOR_MATERIAL.mode = mode;
            GL11.glColorMaterial(face, mode);
        }
    }

    @Deprecated
    public static void light(int light, int pname, FloatBuffer params) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glLightfv(light, pname, params);
    }

    @Deprecated
    public static void lightModel(int pname, FloatBuffer params) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glLightModelfv(pname, params);
    }

    @Deprecated
    public static void normal3f(float nx, float ny, float nz) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glNormal3f(nx, ny, nz);
    }

    public static void func_244593_j() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GlStateManager.field_244591_n.field_244595_a.disable();
    }

    public static void func_244594_k() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GlStateManager.field_244591_n.field_244595_a.enable();
    }

    public static void func_244592_a(int p_244592_0_, int p_244592_1_, int p_244592_2_, int p_244592_3_) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GL20.glScissor(p_244592_0_, p_244592_1_, p_244592_2_, p_244592_3_);
    }

    public static void disableDepthTest() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GlStateManager.DEPTH.test.disable();
    }

    public static void enableDepthTest() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GlStateManager.DEPTH.test.enable();
    }

    public static void depthFunc(int depthFunc) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        if (depthFunc != GlStateManager.DEPTH.func) {
            GlStateManager.DEPTH.func = depthFunc;
            GL11.glDepthFunc(depthFunc);
        }
    }

    public static void depthMask(boolean flagIn) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        if (flagIn != GlStateManager.DEPTH.mask) {
            GlStateManager.DEPTH.mask = flagIn;
            GL11.glDepthMask(flagIn);
        }
    }

    public static void disableBlend() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        if (blendLock.isLocked()) {
            blendLockState.setDisabled();
        } else {
            GlStateManager.BLEND.blend.disable();
        }
    }

    public static void enableBlend() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        if (blendLock.isLocked()) {
            blendLockState.setEnabled();
        } else {
            GlStateManager.BLEND.blend.enable();
        }
    }

    public static void blendFunc(int srcFactor, int dstFactor) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        if (blendLock.isLocked()) {
            blendLockState.setFactors(srcFactor, dstFactor);
        } else if (srcFactor != GlStateManager.BLEND.srcFactorRgb || dstFactor != GlStateManager.BLEND.dstFactorRgb || srcFactor != GlStateManager.BLEND.srcFactorAlpha || dstFactor != GlStateManager.BLEND.dstFactorAlpha) {
            GlStateManager.BLEND.srcFactorRgb = srcFactor;
            GlStateManager.BLEND.dstFactorRgb = dstFactor;
            GlStateManager.BLEND.srcFactorAlpha = srcFactor;
            GlStateManager.BLEND.dstFactorAlpha = dstFactor;
            if (Config.isShaders()) {
                Shaders.uniform_blendFunc.setValue(srcFactor, dstFactor, srcFactor, dstFactor);
            }
            GL11.glBlendFunc(srcFactor, dstFactor);
        }
    }

    public static void blendFuncSeparate(int srcFactor, int dstFactor, int srcFactorAlpha, int dstFactorAlpha) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        if (blendLock.isLocked()) {
            blendLockState.setFactors(srcFactor, dstFactor, srcFactorAlpha, dstFactorAlpha);
        } else if (srcFactor != GlStateManager.BLEND.srcFactorRgb || dstFactor != GlStateManager.BLEND.dstFactorRgb || srcFactorAlpha != GlStateManager.BLEND.srcFactorAlpha || dstFactorAlpha != GlStateManager.BLEND.dstFactorAlpha) {
            GlStateManager.BLEND.srcFactorRgb = srcFactor;
            GlStateManager.BLEND.dstFactorRgb = dstFactor;
            GlStateManager.BLEND.srcFactorAlpha = srcFactorAlpha;
            GlStateManager.BLEND.dstFactorAlpha = dstFactorAlpha;
            if (Config.isShaders()) {
                Shaders.uniform_blendFunc.setValue(srcFactor, dstFactor, srcFactorAlpha, dstFactorAlpha);
            }
            GlStateManager.glBlendFuncSeparate(srcFactor, dstFactor, srcFactorAlpha, dstFactorAlpha);
        }
    }

    public static void blendColor(float red, float green, float blue, float alpha) {
        GL14.glBlendColor(red, green, blue, alpha);
    }

    public static void blendEquation(int blendEquation) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL14.glBlendEquation(blendEquation);
    }

    public static String init(GLCapabilities glCapabilities) {
        RenderSystem.assertThread(RenderSystem::isInInitPhase);
        Config.initDisplay();
        openGL31 = glCapabilities.OpenGL31;
        if (openGL31) {
            GL_COPY_READ_BUFFER = 36662;
            GL_COPY_WRITE_BUFFER = 36663;
        } else {
            GL_COPY_READ_BUFFER = 36662;
            GL_COPY_WRITE_BUFFER = 36663;
        }
        if (glCapabilities.OpenGL15) {
            GL_ARRAY_BUFFER = 34962;
            GL_STATIC_DRAW = 35044;
        } else {
            GL_ARRAY_BUFFER = 34962;
            GL_STATIC_DRAW = 35044;
        }
        boolean flag = openGL31 || glCapabilities.GL_ARB_copy_buffer;
        boolean flag1 = glCapabilities.OpenGL14;
        boolean bl = vboRegions = flag && flag1;
        if (!vboRegions) {
            ArrayList<Object> list = new ArrayList<Object>();
            if (!flag) {
                list.add("OpenGL 1.3, ARB_copy_buffer");
            }
            if (!flag1) {
                list.add("OpenGL 1.4");
            }
            String s = "VboRegions not supported, missing: " + Config.listToString(list);
            Config.dbg(s);
            list.add(s);
        }
        supportType = glCapabilities.OpenGL30 ? SupportType.BASE : (glCapabilities.GL_EXT_framebuffer_blit ? SupportType.EXT : SupportType.NONE);
        if (glCapabilities.OpenGL30) {
            fboMode = FramebufferExtension.BASE;
            FramebufferConstants.GL_FRAMEBUFFER = 36160;
            FramebufferConstants.GL_RENDERBUFFER = 36161;
            FramebufferConstants.GL_COLOR_ATTACHMENT0 = 36064;
            FramebufferConstants.GL_DEPTH_ATTACHMENT = 36096;
            FramebufferConstants.GL_FRAMEBUFFER_COMPLETE = 36053;
            FramebufferConstants.GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT = 36054;
            FramebufferConstants.GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT = 36055;
            FramebufferConstants.GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER = 36059;
            FramebufferConstants.GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER = 36060;
            return "OpenGL 3.0";
        }
        if (glCapabilities.GL_ARB_framebuffer_object) {
            fboMode = FramebufferExtension.ARB;
            FramebufferConstants.GL_FRAMEBUFFER = 36160;
            FramebufferConstants.GL_RENDERBUFFER = 36161;
            FramebufferConstants.GL_COLOR_ATTACHMENT0 = 36064;
            FramebufferConstants.GL_DEPTH_ATTACHMENT = 36096;
            FramebufferConstants.GL_FRAMEBUFFER_COMPLETE = 36053;
            FramebufferConstants.GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT = 36055;
            FramebufferConstants.GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT = 36054;
            FramebufferConstants.GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER = 36059;
            FramebufferConstants.GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER = 36060;
            return "ARB_framebuffer_object extension";
        }
        if (glCapabilities.GL_EXT_framebuffer_object) {
            fboMode = FramebufferExtension.EXT;
            FramebufferConstants.GL_FRAMEBUFFER = 36160;
            FramebufferConstants.GL_RENDERBUFFER = 36161;
            FramebufferConstants.GL_COLOR_ATTACHMENT0 = 36064;
            FramebufferConstants.GL_DEPTH_ATTACHMENT = 36096;
            FramebufferConstants.GL_FRAMEBUFFER_COMPLETE = 36053;
            FramebufferConstants.GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT = 36055;
            FramebufferConstants.GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT = 36054;
            FramebufferConstants.GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER = 36059;
            FramebufferConstants.GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER = 36060;
            return "EXT_framebuffer_object extension";
        }
        throw new IllegalStateException("Could not initialize framebuffer support.");
    }

    public static int getProgram(int program, int pname) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        return GL20.glGetProgrami(program, pname);
    }

    public static void attachShader(int program, int shaderIn) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL20.glAttachShader(program, shaderIn);
    }

    public static void deleteShader(int shaderIn) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL20.glDeleteShader(shaderIn);
    }

    public static int createShader(int type) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        return GL20.glCreateShader(type);
    }

    public static void shaderSource(int shaderIn, CharSequence source) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL20.glShaderSource(shaderIn, source);
    }

    public static void compileShader(int shaderIn) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL20.glCompileShader(shaderIn);
    }

    public static int getShader(int shaderIn, int pname) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        return GL20.glGetShaderi(shaderIn, pname);
    }

    public static void useProgram(int program) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL20.glUseProgram(program);
    }

    public static int createProgram() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        return GL20.glCreateProgram();
    }

    public static void deleteProgram(int program) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL20.glDeleteProgram(program);
    }

    public static void linkProgram(int program) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL20.glLinkProgram(program);
    }

    public static int getUniformLocation(int program, CharSequence name) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        return GL20.glGetUniformLocation(program, name);
    }

    public static void uniform1i(int location, IntBuffer value) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL20.glUniform1iv(location, value);
    }

    public static void uniform1i(int location, int value) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL20.glUniform1i(location, value);
    }

    public static void uniform1f(int location, FloatBuffer value) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL20.glUniform1fv(location, value);
    }

    public static void uniform2i(int location, IntBuffer value) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL20.glUniform2iv(location, value);
    }

    public static void uniform2f(int location, FloatBuffer value) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL20.glUniform2fv(location, value);
    }

    public static void uniform3i(int location, IntBuffer value) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL20.glUniform3iv(location, value);
    }

    public static void uniform3f(int location, FloatBuffer value) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL20.glUniform3fv(location, value);
    }

    public static void uniform4i(int location, IntBuffer value) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL20.glUniform4iv(location, value);
    }

    public static void uniform4f(int location, FloatBuffer value) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL20.glUniform4fv(location, value);
    }

    public static void uniformMatrix2f(int location, boolean transpose, FloatBuffer value) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL20.glUniformMatrix2fv(location, transpose, value);
    }

    public static void uniformMatrix3f(int location, boolean transpose, FloatBuffer value) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL20.glUniformMatrix3fv(location, transpose, value);
    }

    public static void uniformMatrix4f(int location, boolean transpose, FloatBuffer value) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL20.glUniformMatrix4fv(location, transpose, value);
    }

    public static int getAttribLocation(int program, CharSequence name) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        return GL20.glGetAttribLocation(program, name);
    }

    public static int genBuffers() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        return GL15.glGenBuffers();
    }

    public static void bindBuffer(int target, int buffer) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GL15.glBindBuffer(target, buffer);
    }

    public static void bufferData(int target, ByteBuffer data, int usage) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GL15.glBufferData(target, data, usage);
    }

    public static void deleteBuffers(int buffer) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL15.glDeleteBuffers(buffer);
    }

    public static void copySubImage(int target, int level, int xOffset, int yOffset, int x, int y, int width, int height) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GL20.glCopyTexSubImage2D(target, level, xOffset, yOffset, x, y, width, height);
    }

    public static void bindFramebuffer(int target, int framebufferIn) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        if (target == 36160) {
            if (framebufferRead == framebufferIn && framebufferDraw == framebufferIn) {
                return;
            }
            framebufferRead = framebufferIn;
            framebufferDraw = framebufferIn;
        } else if (target == 36008) {
            if (framebufferRead == framebufferIn) {
                return;
            }
            framebufferRead = framebufferIn;
        }
        if (target == 36009) {
            if (framebufferDraw == framebufferIn) {
                return;
            }
            framebufferDraw = framebufferIn;
        }
        switch (fboMode) {
            case BASE: {
                GL30.glBindFramebuffer(target, framebufferIn);
                break;
            }
            case ARB: {
                ARBFramebufferObject.glBindFramebuffer(target, framebufferIn);
                break;
            }
            case EXT: {
                EXTFramebufferObject.glBindFramebufferEXT(target, framebufferIn);
            }
        }
    }

    public static int getFrameBufferAttachmentParam() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        switch (fboMode) {
            case BASE: {
                if (GL30.glGetFramebufferAttachmentParameteri(36160, 36096, 36048) != 5890) break;
                return GL30.glGetFramebufferAttachmentParameteri(36160, 36096, 36049);
            }
            case ARB: {
                if (ARBFramebufferObject.glGetFramebufferAttachmentParameteri(36160, 36096, 36048) != 5890) break;
                return ARBFramebufferObject.glGetFramebufferAttachmentParameteri(36160, 36096, 36049);
            }
            case EXT: {
                if (EXTFramebufferObject.glGetFramebufferAttachmentParameteriEXT(36160, 36096, 36048) != 5890) break;
                return EXTFramebufferObject.glGetFramebufferAttachmentParameteriEXT(36160, 36096, 36049);
            }
        }
        return 0;
    }

    public static void blitFramebuffer(int srcX0, int srcY0, int srcX1, int srcY1, int dstX0, int dstY0, int dstX1, int dstY1, int mask, int filter) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        switch (supportType) {
            case BASE: {
                GL30.glBlitFramebuffer(srcX0, srcY0, srcX1, srcY1, dstX0, dstY0, dstX1, dstY1, mask, filter);
                break;
            }
            case EXT: {
                EXTFramebufferBlit.glBlitFramebufferEXT(srcX0, srcY0, srcX1, srcY1, dstX0, dstY0, dstX1, dstY1, mask, filter);
            }
        }
    }

    public static void deleteFramebuffers(int frameBuffer) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        switch (fboMode) {
            case BASE: {
                GL30.glDeleteFramebuffers(frameBuffer);
                break;
            }
            case ARB: {
                ARBFramebufferObject.glDeleteFramebuffers(frameBuffer);
                break;
            }
            case EXT: {
                EXTFramebufferObject.glDeleteFramebuffersEXT(frameBuffer);
            }
        }
    }

    public static int genFramebuffers() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        switch (fboMode) {
            case BASE: {
                return GL30.glGenFramebuffers();
            }
            case ARB: {
                return ARBFramebufferObject.glGenFramebuffers();
            }
            case EXT: {
                return EXTFramebufferObject.glGenFramebuffersEXT();
            }
        }
        return -1;
    }

    public static int checkFramebufferStatus(int target) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        switch (fboMode) {
            case BASE: {
                return GL30.glCheckFramebufferStatus(target);
            }
            case ARB: {
                return ARBFramebufferObject.glCheckFramebufferStatus(target);
            }
            case EXT: {
                return EXTFramebufferObject.glCheckFramebufferStatusEXT(target);
            }
        }
        return -1;
    }

    public static void framebufferTexture2D(int target, int attachment, int texTarget, int texture, int level) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        switch (fboMode) {
            case BASE: {
                GL30.glFramebufferTexture2D(target, attachment, texTarget, texture, level);
                break;
            }
            case ARB: {
                ARBFramebufferObject.glFramebufferTexture2D(target, attachment, texTarget, texture, level);
                break;
            }
            case EXT: {
                EXTFramebufferObject.glFramebufferTexture2DEXT(target, attachment, texTarget, texture, level);
            }
        }
    }

    @Deprecated
    public static int getActiveTextureId() {
        return GlStateManager.TEXTURES[GlStateManager.activeTexture].textureName;
    }

    public static void glActiveTexture(int textureIn) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL13.glActiveTexture(textureIn);
    }

    @Deprecated
    public static void clientActiveTexture(int texture) {
        if (texture != clientActiveTexture) {
            RenderSystem.assertThread(RenderSystem::isOnRenderThread);
            GL13.glClientActiveTexture(texture);
            clientActiveTexture = texture;
        }
    }

    @Deprecated
    public static void multiTexCoord2f(int texture, float s, float t) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL13.glMultiTexCoord2f(texture, s, t);
        if (texture == 33986) {
            lastBrightnessX = s;
            lastBrightnessY = t;
        }
    }

    public static void glBlendFuncSeparate(int sFactorRGB, int dFactorRGB, int sFactorAlpha, int dFactorAlpha) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL14.glBlendFuncSeparate(sFactorRGB, dFactorRGB, sFactorAlpha, dFactorAlpha);
    }

    public static String getShaderInfoLog(int shader, int maxLength) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        return GL20.glGetShaderInfoLog(shader, maxLength);
    }

    public static String getProgramInfoLog(int program, int maxLength) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        return GL20.glGetProgramInfoLog(program, maxLength);
    }

    public static void setupOutline() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GlStateManager.texEnv(8960, 8704, 34160);
        GlStateManager.color(7681, 34168);
    }

    public static void teardownOutline() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GlStateManager.texEnv(8960, 8704, 8448);
        GlStateManager.color(8448, 5890, 34168, 34166);
    }

    public static void setupOverlayColor(int texture, int bitSpace) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GlStateManager.activeTexture(33985);
        GlStateManager.enableTexture();
        GlStateManager.matrixMode(5890);
        GlStateManager.loadIdentity();
        float f = 1.0f / (float)(bitSpace - 1);
        GlStateManager.scalef(f, f, f);
        GlStateManager.matrixMode(5888);
        GlStateManager.bindTexture(texture);
        GlStateManager.texParameter(3553, 10241, 9728);
        GlStateManager.texParameter(3553, 10240, 9728);
        GlStateManager.texParameter(3553, 10242, 10496);
        GlStateManager.texParameter(3553, 10243, 10496);
        GlStateManager.texEnv(8960, 8704, 34160);
        GlStateManager.color(34165, 34168, 5890, 5890);
        GlStateManager.alpha(7681, 34168);
        GlStateManager.activeTexture(33984);
    }

    public static void teardownOverlayColor() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GlStateManager.activeTexture(33985);
        GlStateManager.disableTexture();
        GlStateManager.activeTexture(33984);
    }

    private static void color(int color1, int color2) {
        GlStateManager.texEnv(8960, 34161, color1);
        GlStateManager.texEnv(8960, 34176, color2);
        GlStateManager.texEnv(8960, 34192, 768);
    }

    private static void color(int red, int green, int blue, int alpha) {
        GlStateManager.texEnv(8960, 34161, red);
        GlStateManager.texEnv(8960, 34176, green);
        GlStateManager.texEnv(8960, 34192, 768);
        GlStateManager.texEnv(8960, 34177, blue);
        GlStateManager.texEnv(8960, 34193, 768);
        GlStateManager.texEnv(8960, 34178, alpha);
        GlStateManager.texEnv(8960, 34194, 770);
    }

    private static void alpha(int alpha1, int alpha2) {
        GlStateManager.texEnv(8960, 34162, alpha1);
        GlStateManager.texEnv(8960, 34184, alpha2);
        GlStateManager.texEnv(8960, 34200, 770);
    }

    public static void setupLighting(Vector3f lightingVector1, Vector3f lightingVector2, Matrix4f matrix) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GlStateManager.pushMatrix();
        GlStateManager.loadIdentity();
        GlStateManager.enableLight(0);
        GlStateManager.enableLight(1);
        Vector4f vector4f = new Vector4f(lightingVector1);
        vector4f.transform(matrix);
        GlStateManager.light(16384, 4611, GlStateManager.getBuffer(vector4f.getX(), vector4f.getY(), vector4f.getZ(), 0.0f));
        float f = 0.6f;
        GlStateManager.light(16384, 4609, GlStateManager.getBuffer(0.6f, 0.6f, 0.6f, 1.0f));
        GlStateManager.light(16384, 4608, GlStateManager.getBuffer(0.0f, 0.0f, 0.0f, 1.0f));
        GlStateManager.light(16384, 4610, GlStateManager.getBuffer(0.0f, 0.0f, 0.0f, 1.0f));
        Vector4f vector4f1 = new Vector4f(lightingVector2);
        vector4f1.transform(matrix);
        GlStateManager.light(16385, 4611, GlStateManager.getBuffer(vector4f1.getX(), vector4f1.getY(), vector4f1.getZ(), 0.0f));
        GlStateManager.light(16385, 4609, GlStateManager.getBuffer(0.6f, 0.6f, 0.6f, 1.0f));
        GlStateManager.light(16385, 4608, GlStateManager.getBuffer(0.0f, 0.0f, 0.0f, 1.0f));
        GlStateManager.light(16385, 4610, GlStateManager.getBuffer(0.0f, 0.0f, 0.0f, 1.0f));
        GlStateManager.shadeModel(7424);
        float f1 = 0.4f;
        GlStateManager.lightModel(2899, GlStateManager.getBuffer(0.4f, 0.4f, 0.4f, 1.0f));
        GlStateManager.popMatrix();
    }

    public static void setupScaledLighting(Vector3f lighting1, Vector3f lighting2) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        Matrix4f matrix4f = new Matrix4f();
        matrix4f.setIdentity();
        matrix4f.mul(Matrix4f.makeScale(1.0f, -1.0f, 1.0f));
        matrix4f.mul(Vector3f.YP.rotationDegrees(-22.5f));
        matrix4f.mul(Vector3f.XP.rotationDegrees(135.0f));
        GlStateManager.setupLighting(lighting1, lighting2, matrix4f);
    }

    public static void setupGui3DMatrix(Vector3f lightingVector1, Vector3f lightingVector2) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        Matrix4f matrix4f = new Matrix4f();
        matrix4f.setIdentity();
        matrix4f.mul(Vector3f.YP.rotationDegrees(62.0f));
        matrix4f.mul(Vector3f.XP.rotationDegrees(185.5f));
        matrix4f.mul(Matrix4f.makeScale(1.0f, -1.0f, 1.0f));
        matrix4f.mul(Vector3f.YP.rotationDegrees(-22.5f));
        matrix4f.mul(Vector3f.XP.rotationDegrees(135.0f));
        GlStateManager.setupLighting(lightingVector1, lightingVector2, matrix4f);
    }

    private static FloatBuffer getBuffer(float float1, float float2, float float3, float float4) {
        FLOAT_4_BUFFER.clear();
        FLOAT_4_BUFFER.put(float1).put(float2).put(float3).put(float4);
        FLOAT_4_BUFFER.flip();
        return FLOAT_4_BUFFER;
    }

    public static void setupEndPortalTexGen() {
        GlStateManager.texGenMode(TexGen.S, 9216);
        GlStateManager.texGenMode(TexGen.T, 9216);
        GlStateManager.texGenMode(TexGen.R, 9216);
        GlStateManager.texGenParam(TexGen.S, 9474, GlStateManager.getBuffer(1.0f, 0.0f, 0.0f, 0.0f));
        GlStateManager.texGenParam(TexGen.T, 9474, GlStateManager.getBuffer(0.0f, 1.0f, 0.0f, 0.0f));
        GlStateManager.texGenParam(TexGen.R, 9474, GlStateManager.getBuffer(0.0f, 0.0f, 1.0f, 0.0f));
        GlStateManager.enableTexGen(TexGen.S);
        GlStateManager.enableTexGen(TexGen.T);
        GlStateManager.enableTexGen(TexGen.R);
    }

    public static void clearTexGen() {
        GlStateManager.disableTexGen(TexGen.S);
        GlStateManager.disableTexGen(TexGen.T);
        GlStateManager.disableTexGen(TexGen.R);
    }

    public static void mulTextureByProjModelView() {
        GlStateManager.getMatrix(2983, MATRIX_BUFFER);
        GlStateManager.multMatrix(MATRIX_BUFFER);
        GlStateManager.getMatrix(2982, MATRIX_BUFFER);
        GlStateManager.multMatrix(MATRIX_BUFFER);
    }

    @Deprecated
    public static void enableFog() {
        if (fogAllowed) {
            RenderSystem.assertThread(RenderSystem::isOnRenderThread);
            GlStateManager.FOG.fog.enable();
        }
    }

    @Deprecated
    public static void disableFog() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GlStateManager.FOG.fog.disable();
    }

    @Deprecated
    public static void fogMode(int fogMode) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        if (fogMode != GlStateManager.FOG.mode) {
            GlStateManager.FOG.mode = fogMode;
            GlStateManager.fogi(2917, fogMode);
            if (Config.isShaders()) {
                Shaders.setFogMode(fogMode);
            }
        }
    }

    @Deprecated
    public static void fogDensity(float param) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        if (param < 0.0f) {
            param = 0.0f;
        }
        if (param != GlStateManager.FOG.density) {
            GlStateManager.FOG.density = param;
            GL11.glFogf(2914, param);
            if (Config.isShaders()) {
                Shaders.setFogDensity(param);
            }
        }
    }

    @Deprecated
    public static void fogStart(float param) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        if (param != GlStateManager.FOG.start) {
            GlStateManager.FOG.start = param;
            GL11.glFogf(2915, param);
        }
    }

    @Deprecated
    public static void fogEnd(float param) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        if (param != GlStateManager.FOG.end) {
            GlStateManager.FOG.end = param;
            GL11.glFogf(2916, param);
        }
    }

    @Deprecated
    public static void fog(int pname, float[] param) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glFogfv(pname, param);
    }

    @Deprecated
    public static void fogi(int pname, int param) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glFogi(pname, param);
    }

    public static void enableCull() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        if (cullLock.isLocked()) {
            cullLockState.setEnabled();
        } else {
            GlStateManager.CULL.cullFace.enable();
        }
    }

    public static void disableCull() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        if (cullLock.isLocked()) {
            cullLockState.setDisabled();
        } else {
            GlStateManager.CULL.cullFace.disable();
        }
    }

    public static void polygonMode(int face, int mode) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glPolygonMode(face, mode);
    }

    public static void enablePolygonOffset() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GlStateManager.POLY_OFFSET.polyOffset.enable();
    }

    public static void disablePolygonOffset() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GlStateManager.POLY_OFFSET.polyOffset.disable();
    }

    public static void enableLineOffset() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GlStateManager.POLY_OFFSET.lineOffset.enable();
    }

    public static void disableLineOffset() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GlStateManager.POLY_OFFSET.lineOffset.disable();
    }

    public static void polygonOffset(float factor, float units) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        if (factor != GlStateManager.POLY_OFFSET.factor || units != GlStateManager.POLY_OFFSET.units) {
            GlStateManager.POLY_OFFSET.factor = factor;
            GlStateManager.POLY_OFFSET.units = units;
            GL11.glPolygonOffset(factor, units);
        }
    }

    public static void enableColorLogicOp() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GlStateManager.COLOR_LOGIC.colorLogicOp.enable();
    }

    public static void disableColorLogicOp() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GlStateManager.COLOR_LOGIC.colorLogicOp.disable();
    }

    public static void logicOp(int logicOperation) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        if (logicOperation != GlStateManager.COLOR_LOGIC.logicOpcode) {
            GlStateManager.COLOR_LOGIC.logicOpcode = logicOperation;
            GL11.glLogicOp(logicOperation);
        }
    }

    @Deprecated
    public static void enableTexGen(TexGen texGen) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GlStateManager.getTexGen((TexGen)texGen).textureGen.enable();
    }

    @Deprecated
    public static void disableTexGen(TexGen texGen) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GlStateManager.getTexGen((TexGen)texGen).textureGen.disable();
    }

    @Deprecated
    public static void texGenMode(TexGen texGen, int mode) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        TexGenCoord glstatemanager$texgencoord = GlStateManager.getTexGen(texGen);
        if (mode != glstatemanager$texgencoord.mode) {
            glstatemanager$texgencoord.mode = mode;
            GL11.glTexGeni(glstatemanager$texgencoord.coord, 9472, mode);
        }
    }

    @Deprecated
    public static void texGenParam(TexGen texGen, int pname, FloatBuffer params) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glTexGenfv(GlStateManager.getTexGen((TexGen)texGen).coord, pname, params);
    }

    @Deprecated
    private static TexGenCoord getTexGen(TexGen texGen) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        switch (texGen) {
            case S: {
                return GlStateManager.TEX_GEN.s;
            }
            case T: {
                return GlStateManager.TEX_GEN.t;
            }
            case R: {
                return GlStateManager.TEX_GEN.r;
            }
            case Q: {
                return GlStateManager.TEX_GEN.q;
            }
        }
        return GlStateManager.TEX_GEN.s;
    }

    public static void activeTexture(int textureIn) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        if (activeTexture != textureIn - 33984) {
            activeTexture = textureIn - 33984;
            GlStateManager.glActiveTexture(textureIn);
        }
    }

    public static void enableTexture() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GlStateManager.TEXTURES[GlStateManager.activeTexture].texture2DState.enable();
    }

    public static void disableTexture() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GlStateManager.TEXTURES[GlStateManager.activeTexture].texture2DState.disable();
    }

    @Deprecated
    public static void texEnv(int target, int parameterName, int parameters) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glTexEnvi(target, parameterName, parameters);
    }

    public static void texParameter(int target, int parameterName, float parameter) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GL11.glTexParameterf(target, parameterName, parameter);
    }

    public static void texParameter(int target, int parameterName, int parameter) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GL11.glTexParameteri(target, parameterName, parameter);
    }

    public static int getTexLevelParameter(int target, int level, int parameterName) {
        RenderSystem.assertThread(RenderSystem::isInInitPhase);
        return GL11.glGetTexLevelParameteri(target, level, parameterName);
    }

    public static int genTexture() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        return GL11.glGenTextures();
    }

    public static void genTextures(int[] textures) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GL11.glGenTextures(textures);
    }

    public static void deleteTexture(int textureIn) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        if (textureIn != 0) {
            for (int i = 0; i < IMAGE_TEXTURES.length; ++i) {
                if (IMAGE_TEXTURES[i] != textureIn) continue;
                GlStateManager.IMAGE_TEXTURES[i] = 0;
            }
            GL11.glDeleteTextures(textureIn);
            for (TextureState glstatemanager$texturestate : TEXTURES) {
                if (glstatemanager$texturestate.textureName != textureIn) continue;
                glstatemanager$texturestate.textureName = 0;
            }
        }
    }

    public static void deleteTextures(int[] textures) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        for (TextureState glstatemanager$texturestate : TEXTURES) {
            for (int i : textures) {
                if (glstatemanager$texturestate.textureName != i) continue;
                glstatemanager$texturestate.textureName = -1;
            }
        }
        GL11.glDeleteTextures(textures);
    }

    public static void bindTexture(int textureIn) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        if (textureIn != GlStateManager.TEXTURES[GlStateManager.activeTexture].textureName) {
            GlStateManager.TEXTURES[GlStateManager.activeTexture].textureName = textureIn;
            GL11.glBindTexture(3553, textureIn);
            if (SmartAnimations.isActive()) {
                SmartAnimations.textureRendered(textureIn);
            }
        }
    }

    public static void texImage2D(int target, int level, int internalFormat, int width, int height, int border, int format, int type, @Nullable IntBuffer pixels) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GL11.glTexImage2D(target, level, internalFormat, width, height, border, format, type, pixels);
    }

    public static void texSubImage2D(int target, int level, int xOffset, int yOffset, int width, int height, int format, int type, long pixels) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GL11.glTexSubImage2D(target, level, xOffset, yOffset, width, height, format, type, pixels);
    }

    public static void getTexImage(int tex, int level, int format, int type, long pixels) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glGetTexImage(tex, level, format, type, pixels);
    }

    @Deprecated
    public static void shadeModel(int mode) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        if (mode != shadeModel) {
            shadeModel = mode;
            GL11.glShadeModel(mode);
        }
    }

    @Deprecated
    public static void enableRescaleNormal() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        RESCALE_NORMAL.enable();
    }

    @Deprecated
    public static void disableRescaleNormal() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        RESCALE_NORMAL.disable();
    }

    public static void viewport(int x, int y, int width, int height) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        Viewport.INSTANCE.x = x;
        Viewport.INSTANCE.y = y;
        Viewport.INSTANCE.w = width;
        Viewport.INSTANCE.h = height;
        GL11.glViewport(x, y, width, height);
    }

    public static void colorMask(boolean red, boolean green, boolean blue, boolean alpha) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        if (red != GlStateManager.COLOR_MASK.red || green != GlStateManager.COLOR_MASK.green || blue != GlStateManager.COLOR_MASK.blue || alpha != GlStateManager.COLOR_MASK.alpha) {
            GlStateManager.COLOR_MASK.red = red;
            GlStateManager.COLOR_MASK.green = green;
            GlStateManager.COLOR_MASK.blue = blue;
            GlStateManager.COLOR_MASK.alpha = alpha;
            GL11.glColorMask(red, green, blue, alpha);
        }
    }

    public static void stencilFunc(int func, int ref, int mask) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        if (func != GlStateManager.STENCIL.func.func || func != GlStateManager.STENCIL.func.ref || func != GlStateManager.STENCIL.func.mask) {
            GlStateManager.STENCIL.func.func = func;
            GlStateManager.STENCIL.func.ref = ref;
            GlStateManager.STENCIL.func.mask = mask;
            GL11.glStencilFunc(func, ref, mask);
        }
    }

    public static void stencilMask(int mask) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        if (mask != GlStateManager.STENCIL.mask) {
            GlStateManager.STENCIL.mask = mask;
            GL11.glStencilMask(mask);
        }
    }

    public static void stencilOp(int sfail, int dpfail, int dppass) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        if (sfail != GlStateManager.STENCIL.sfail || dpfail != GlStateManager.STENCIL.dpfail || dppass != GlStateManager.STENCIL.dppass) {
            GlStateManager.STENCIL.sfail = sfail;
            GlStateManager.STENCIL.dpfail = dpfail;
            GlStateManager.STENCIL.dppass = dppass;
            GL11.glStencilOp(sfail, dpfail, dppass);
        }
    }

    public static void clearDepth(double depth) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GL11.glClearDepth(depth);
    }

    public static void clearColor(float red, float green, float blue, float alpha) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GL11.glClearColor(red, green, blue, alpha);
    }

    public static void clearStencil(int index) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glClearStencil(index);
    }

    public static void clear(int mask, boolean checkError) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GL11.glClear(mask);
        if (checkError) {
            GlStateManager.getError();
        }
    }

    @Deprecated
    public static void matrixMode(int mode) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GL11.glMatrixMode(mode);
    }

    @Deprecated
    public static void loadIdentity() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GL11.glLoadIdentity();
    }

    @Deprecated
    public static void pushMatrix() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glPushMatrix();
    }

    @Deprecated
    public static void popMatrix() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glPopMatrix();
    }

    @Deprecated
    public static void getMatrix(int pname, FloatBuffer params) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glGetFloatv(pname, params);
    }

    @Deprecated
    public static void ortho(double left, double right, double bottom, double top, double zNear, double zFar) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glOrtho(left, right, bottom, top, zNear, zFar);
    }

    @Deprecated
    public static void rotatef(float angle, float x, float y, float z) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glRotatef(angle, x, y, z);
    }

    @Deprecated
    public static void scalef(float x, float y, float z) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glScalef(x, y, z);
    }

    @Deprecated
    public static void scaled(double x, double y, double z) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glScaled(x, y, z);
    }

    @Deprecated
    public static void translatef(float x, float y, float z) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glTranslatef(x, y, z);
    }

    @Deprecated
    public static void translated(double x, double y, double z) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glTranslated(x, y, z);
    }

    @Deprecated
    public static void multMatrix(FloatBuffer matrix) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glMultMatrixf(matrix);
    }

    @Deprecated
    public static void multMatrix(Matrix4f matrix) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        matrix.write(MATRIX_BUFFER);
        MATRIX_BUFFER.rewind();
        GlStateManager.multMatrix(MATRIX_BUFFER);
    }

    @Deprecated
    public static void color4f(float red, float green, float blue, float alpha) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        if (red != GlStateManager.COLOR.red || green != GlStateManager.COLOR.green || blue != GlStateManager.COLOR.blue || alpha != GlStateManager.COLOR.alpha) {
            GlStateManager.COLOR.red = red;
            GlStateManager.COLOR.green = green;
            GlStateManager.COLOR.blue = blue;
            GlStateManager.COLOR.alpha = alpha;
            GL11.glColor4f(red, green, blue, alpha);
        }
    }

    @Deprecated
    public static void clearCurrentColor() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GlStateManager.COLOR.red = -1.0f;
        GlStateManager.COLOR.green = -1.0f;
        GlStateManager.COLOR.blue = -1.0f;
        GlStateManager.COLOR.alpha = -1.0f;
    }

    @Deprecated
    public static void normalPointer(int type, int stride, long pointer) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glNormalPointer(type, stride, pointer);
    }

    @Deprecated
    public static void texCoordPointer(int size, int type, int stride, long pointer) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glTexCoordPointer(size, type, stride, pointer);
    }

    @Deprecated
    public static void vertexPointer(int size, int type, int stride, long pointer) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glVertexPointer(size, type, stride, pointer);
    }

    @Deprecated
    public static void colorPointer(int size, int type, int stride, long pointer) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glColorPointer(size, type, stride, pointer);
    }

    public static void vertexAttribPointer(int index, int size, int type, boolean normalized, int stride, long pointer) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL20.glVertexAttribPointer(index, size, type, normalized, stride, pointer);
    }

    @Deprecated
    public static void enableClientState(int cap) {
        if (!clientStateLocked) {
            RenderSystem.assertThread(RenderSystem::isOnRenderThread);
            GL11.glEnableClientState(cap);
        }
    }

    @Deprecated
    public static void disableClientState(int cap) {
        if (!clientStateLocked) {
            RenderSystem.assertThread(RenderSystem::isOnRenderThread);
            GL11.glDisableClientState(cap);
        }
    }

    public static void enableVertexAttribArray(int index) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL20.glEnableVertexAttribArray(index);
    }

    public static void glEnableVertexAttribArray(int index) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL20.glEnableVertexAttribArray(index);
    }

    public static void drawArrays(int mode, int first, int count) {
        int i;
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glDrawArrays(mode, first, count);
        if (Config.isShaders() && !creatingDisplayList && (i = Shaders.activeProgram.getCountInstances()) > 1) {
            for (int j = 1; j < i; ++j) {
                Shaders.uniform_instanceId.setValue(j);
                GL11.glDrawArrays(mode, first, count);
            }
            Shaders.uniform_instanceId.setValue(0);
        }
    }

    public static void lineWidth(float width) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glLineWidth(width);
    }

    public static void pixelStore(int pname, int param) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GL11.glPixelStorei(pname, param);
    }

    public static void pixelTransfer(int param, float value) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glPixelTransferf(param, value);
    }

    public static void readPixels(int x, int y, int width, int height, int format, int type, ByteBuffer pixels) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glReadPixels(x, y, width, height, format, type, pixels);
    }

    public static int getError() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        return GL11.glGetError();
    }

    public static String getString(int name) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        return GL11.glGetString(name);
    }

    public static int getInteger(int pname) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        return GL11.glGetInteger(pname);
    }

    public static boolean isFabulous() {
        return supportType != SupportType.NONE;
    }

    public static int getActiveTextureUnit() {
        return 33984 + activeTexture;
    }

    public static void bindCurrentTexture() {
        GL11.glBindTexture(3553, GlStateManager.TEXTURES[GlStateManager.activeTexture].textureName);
    }

    public static int getBoundTexture() {
        return GlStateManager.TEXTURES[GlStateManager.activeTexture].textureName;
    }

    public static void checkBoundTexture() {
        if (Config.isMinecraftThread()) {
            int i = GL11.glGetInteger(34016);
            int j = GL11.glGetInteger(32873);
            int k = GlStateManager.getActiveTextureUnit();
            int l = GlStateManager.getBoundTexture();
            if (l > 0 && (i != k || j != l)) {
                Config.dbg("checkTexture: act: " + k + ", glAct: " + i + ", tex: " + l + ", glTex: " + j);
            }
        }
    }

    public static void genTextures(IntBuffer p_genTextures_0_) {
        GL11.glGenTextures(p_genTextures_0_);
    }

    public static void deleteTextures(IntBuffer p_deleteTextures_0_) {
        p_deleteTextures_0_.rewind();
        while (p_deleteTextures_0_.position() < p_deleteTextures_0_.limit()) {
            int i = p_deleteTextures_0_.get();
            GlStateManager.deleteTexture(i);
        }
        p_deleteTextures_0_.rewind();
    }

    public static boolean isFogEnabled() {
        return GlStateManager.FOG.fog.currentState;
    }

    public static void setFogEnabled(boolean p_setFogEnabled_0_) {
        GlStateManager.FOG.fog.setEnabled(p_setFogEnabled_0_);
    }

    public static void lockAlpha(GlAlphaState p_lockAlpha_0_) {
        if (!alphaLock.isLocked()) {
            GlStateManager.getAlphaState(alphaLockState);
            GlStateManager.setAlphaState(p_lockAlpha_0_);
            alphaLock.lock();
        }
    }

    public static void unlockAlpha() {
        if (alphaLock.unlock()) {
            GlStateManager.setAlphaState(alphaLockState);
        }
    }

    public static void getAlphaState(GlAlphaState p_getAlphaState_0_) {
        if (alphaLock.isLocked()) {
            p_getAlphaState_0_.setState(alphaLockState);
        } else {
            p_getAlphaState_0_.setState(GlStateManager.ALPHA_TEST.test.currentState, GlStateManager.ALPHA_TEST.func, GlStateManager.ALPHA_TEST.ref);
        }
    }

    public static void setAlphaState(GlAlphaState p_setAlphaState_0_) {
        if (alphaLock.isLocked()) {
            alphaLockState.setState(p_setAlphaState_0_);
        } else {
            GlStateManager.ALPHA_TEST.test.setEnabled(p_setAlphaState_0_.isEnabled());
            GlStateManager.alphaFunc(p_setAlphaState_0_.getFunc(), p_setAlphaState_0_.getRef());
        }
    }

    public static void lockBlend(GlBlendState p_lockBlend_0_) {
        if (!blendLock.isLocked()) {
            GlStateManager.getBlendState(blendLockState);
            GlStateManager.setBlendState(p_lockBlend_0_);
            blendLock.lock();
        }
    }

    public static void unlockBlend() {
        if (blendLock.unlock()) {
            GlStateManager.setBlendState(blendLockState);
        }
    }

    public static void getBlendState(GlBlendState p_getBlendState_0_) {
        if (blendLock.isLocked()) {
            p_getBlendState_0_.setState(blendLockState);
        } else {
            p_getBlendState_0_.setState(GlStateManager.BLEND.blend.currentState, GlStateManager.BLEND.srcFactorRgb, GlStateManager.BLEND.dstFactorRgb, GlStateManager.BLEND.srcFactorAlpha, GlStateManager.BLEND.dstFactorAlpha);
        }
    }

    public static void setBlendState(GlBlendState p_setBlendState_0_) {
        if (blendLock.isLocked()) {
            blendLockState.setState(p_setBlendState_0_);
        } else {
            GlStateManager.BLEND.blend.setEnabled(p_setBlendState_0_.isEnabled());
            if (!p_setBlendState_0_.isSeparate()) {
                GlStateManager.blendFunc(p_setBlendState_0_.getSrcFactor(), p_setBlendState_0_.getDstFactor());
            } else {
                GlStateManager.blendFuncSeparate(p_setBlendState_0_.getSrcFactor(), p_setBlendState_0_.getDstFactor(), p_setBlendState_0_.getSrcFactorAlpha(), p_setBlendState_0_.getDstFactorAlpha());
            }
        }
    }

    public static void lockCull(GlCullState p_lockCull_0_) {
        if (!cullLock.isLocked()) {
            GlStateManager.getCullState(cullLockState);
            GlStateManager.setCullState(p_lockCull_0_);
            cullLock.lock();
        }
    }

    public static void unlockCull() {
        if (cullLock.unlock()) {
            GlStateManager.setCullState(cullLockState);
        }
    }

    public static void getCullState(GlCullState p_getCullState_0_) {
        if (cullLock.isLocked()) {
            p_getCullState_0_.setState(cullLockState);
        } else {
            p_getCullState_0_.setState(GlStateManager.CULL.cullFace.currentState, GlStateManager.CULL.mode);
        }
    }

    public static void setCullState(GlCullState p_setCullState_0_) {
        if (cullLock.isLocked()) {
            cullLockState.setState(p_setCullState_0_);
        } else {
            GlStateManager.CULL.cullFace.setEnabled(p_setCullState_0_.isEnabled());
            GlStateManager.CULL.mode = p_setCullState_0_.getMode();
        }
    }

    public static void glMultiDrawArrays(int p_glMultiDrawArrays_0_, IntBuffer p_glMultiDrawArrays_1_, IntBuffer p_glMultiDrawArrays_2_) {
        int i;
        GL14.glMultiDrawArrays(p_glMultiDrawArrays_0_, p_glMultiDrawArrays_1_, p_glMultiDrawArrays_2_);
        if (Config.isShaders() && !creatingDisplayList && (i = Shaders.activeProgram.getCountInstances()) > 1) {
            for (int j = 1; j < i; ++j) {
                Shaders.uniform_instanceId.setValue(j);
                GL14.glMultiDrawArrays(p_glMultiDrawArrays_0_, p_glMultiDrawArrays_1_, p_glMultiDrawArrays_2_);
            }
            Shaders.uniform_instanceId.setValue(0);
        }
    }

    public static void clear(int p_clear_0_) {
        GlStateManager.clear(p_clear_0_, false);
    }

    public static void callLists(IntBuffer p_callLists_0_) {
        int i;
        GL11.glCallLists(p_callLists_0_);
        if (Config.isShaders() && !creatingDisplayList && (i = Shaders.activeProgram.getCountInstances()) > 1) {
            for (int j = 1; j < i; ++j) {
                Shaders.uniform_instanceId.setValue(j);
                GL11.glCallLists(p_callLists_0_);
            }
            Shaders.uniform_instanceId.setValue(0);
        }
    }

    public static void bufferData(int p_bufferData_0_, long p_bufferData_1_, int p_bufferData_3_) {
        GL15.glBufferData(p_bufferData_0_, p_bufferData_1_, p_bufferData_3_);
    }

    public static void bufferSubData(int p_bufferSubData_0_, long p_bufferSubData_1_, ByteBuffer p_bufferSubData_3_) {
        GL15.glBufferSubData(p_bufferSubData_0_, p_bufferSubData_1_, p_bufferSubData_3_);
    }

    public static void copyBufferSubData(int p_copyBufferSubData_0_, int p_copyBufferSubData_1_, long p_copyBufferSubData_2_, long p_copyBufferSubData_4_, long p_copyBufferSubData_6_) {
        if (openGL31) {
            GL31.glCopyBufferSubData(p_copyBufferSubData_0_, p_copyBufferSubData_1_, p_copyBufferSubData_2_, p_copyBufferSubData_4_, p_copyBufferSubData_6_);
        } else {
            ARBCopyBuffer.glCopyBufferSubData(p_copyBufferSubData_0_, p_copyBufferSubData_1_, p_copyBufferSubData_2_, p_copyBufferSubData_4_, p_copyBufferSubData_6_);
        }
    }

    public static boolean isFogAllowed() {
        return fogAllowed;
    }

    public static void setFogAllowed(boolean p_setFogAllowed_0_) {
        fogAllowed = p_setFogAllowed_0_;
    }

    public static void lockClientState() {
        clientStateLocked = true;
    }

    public static void unlockClientState() {
        clientStateLocked = false;
    }

    public static void readPixels(int p_readPixels_0_, int p_readPixels_1_, int p_readPixels_2_, int p_readPixels_3_, int p_readPixels_4_, int p_readPixels_5_, long p_readPixels_6_) {
        GL11.glReadPixels(p_readPixels_0_, p_readPixels_1_, p_readPixels_2_, p_readPixels_3_, p_readPixels_4_, p_readPixels_5_, p_readPixels_6_);
    }

    public static int getFramebufferRead() {
        return framebufferRead;
    }

    public static int getFramebufferDraw() {
        return framebufferDraw;
    }

    public static void applyCurrentBlend() {
        if (GlStateManager.BLEND.blend.currentState) {
            GL11.glEnable(3042);
        } else {
            GL11.glDisable(3042);
        }
        GL14.glBlendFuncSeparate(GlStateManager.BLEND.srcFactorRgb, GlStateManager.BLEND.dstFactorRgb, GlStateManager.BLEND.srcFactorAlpha, GlStateManager.BLEND.dstFactorAlpha);
    }

    public static void setBlendsIndexed(GlBlendState[] p_setBlendsIndexed_0_) {
        if (p_setBlendsIndexed_0_ != null) {
            for (int i = 0; i < p_setBlendsIndexed_0_.length; ++i) {
                GlBlendState glblendstate = p_setBlendsIndexed_0_[i];
                if (glblendstate == null) continue;
                if (glblendstate.isEnabled()) {
                    GL30.glEnablei(3042, i);
                } else {
                    GL30.glDisablei(3042, i);
                }
                ARBDrawBuffersBlend.glBlendFuncSeparateiARB(i, glblendstate.getSrcFactor(), glblendstate.getDstFactor(), glblendstate.getSrcFactorAlpha(), glblendstate.getDstFactorAlpha());
            }
        }
    }

    public static void bindImageTexture(int p_bindImageTexture_0_, int p_bindImageTexture_1_, int p_bindImageTexture_2_, boolean p_bindImageTexture_3_, int p_bindImageTexture_4_, int p_bindImageTexture_5_, int p_bindImageTexture_6_) {
        if (p_bindImageTexture_0_ >= 0 && p_bindImageTexture_0_ < IMAGE_TEXTURES.length) {
            if (IMAGE_TEXTURES[p_bindImageTexture_0_] == p_bindImageTexture_1_) {
                return;
            }
            GlStateManager.IMAGE_TEXTURES[p_bindImageTexture_0_] = p_bindImageTexture_1_;
        }
        GL42.glBindImageTexture(p_bindImageTexture_0_, p_bindImageTexture_1_, p_bindImageTexture_2_, p_bindImageTexture_3_, p_bindImageTexture_4_, p_bindImageTexture_5_, p_bindImageTexture_6_);
    }

    static {
        TEXTURES = (TextureState[])IntStream.range(0, 32).mapToObj(p_lambda$static$3_0_ -> new TextureState()).toArray(TextureState[]::new);
        shadeModel = 7425;
        RESCALE_NORMAL = new BooleanState(32826);
        COLOR_MASK = new ColorMask();
        COLOR = new Color();
        alphaLock = new LockCounter();
        alphaLockState = new GlAlphaState();
        blendLock = new LockCounter();
        blendLockState = new GlBlendState();
        cullLock = new LockCounter();
        cullLockState = new GlCullState();
        clientStateLocked = false;
        clientActiveTexture = 0;
        creatingDisplayList = false;
        lastBrightnessX = 0.0f;
        lastBrightnessY = 0.0f;
        fogAllowed = true;
        IMAGE_TEXTURES = new int[8];
    }

    @Deprecated
    static class AlphaState {
        public final BooleanState test = new BooleanState(3008);
        public int func = 519;
        public float ref = -1.0f;

        private AlphaState() {
        }
    }

    static class BooleanState {
        private final int capability;
        private boolean currentState;

        public BooleanState(int capability) {
            this.capability = capability;
        }

        public void disable() {
            this.setEnabled(false);
        }

        public void enable() {
            this.setEnabled(true);
        }

        public void setEnabled(boolean enabled) {
            RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
            if (enabled != this.currentState) {
                this.currentState = enabled;
                if (enabled) {
                    GL11.glEnable(this.capability);
                } else {
                    GL11.glDisable(this.capability);
                }
            }
        }
    }

    @Deprecated
    static class ColorMaterialState {
        public final BooleanState colorMaterial = new BooleanState(2903);
        public int face = 1032;
        public int mode = 5634;

        private ColorMaterialState() {
        }
    }

    static class ScissorState {
        public final BooleanState field_244595_a = new BooleanState(3089);

        private ScissorState() {
        }
    }

    static class DepthState {
        public final BooleanState test = new BooleanState(2929);
        public boolean mask = true;
        public int func = 513;

        private DepthState() {
        }
    }

    static class BlendState {
        public final BooleanState blend = new BooleanState(3042);
        public int srcFactorRgb = 1;
        public int dstFactorRgb = 0;
        public int srcFactorAlpha = 1;
        public int dstFactorAlpha = 0;

        private BlendState() {
        }
    }

    public static enum SupportType {
        BASE,
        EXT,
        NONE;

    }

    public static enum FramebufferExtension {
        BASE,
        ARB,
        EXT;

    }

    static class TextureState {
        public final BooleanState texture2DState = new BooleanState(3553);
        public int textureName;

        private TextureState() {
        }
    }

    @Deprecated
    public static enum TexGen {
        S,
        T,
        R,
        Q;

    }

    @Deprecated
    static class FogState {
        public final BooleanState fog = new BooleanState(2912);
        public int mode = 2048;
        public float density = 1.0f;
        public float start;
        public float end = 1.0f;

        private FogState() {
        }
    }

    static class CullState {
        public final BooleanState cullFace = new BooleanState(2884);
        public int mode = 1029;

        private CullState() {
        }
    }

    static class PolygonOffsetState {
        public final BooleanState polyOffset = new BooleanState(32823);
        public final BooleanState lineOffset = new BooleanState(10754);
        public float factor;
        public float units;

        private PolygonOffsetState() {
        }
    }

    static class ColorLogicState {
        public final BooleanState colorLogicOp = new BooleanState(3058);
        public int logicOpcode = 5379;

        private ColorLogicState() {
        }
    }

    @Deprecated
    static class TexGenCoord {
        public final BooleanState textureGen;
        public final int coord;
        public int mode = -1;

        public TexGenCoord(int coord, int textureGen) {
            this.coord = coord;
            this.textureGen = new BooleanState(textureGen);
        }
    }

    @Deprecated
    static class TexGenState {
        public final TexGenCoord s = new TexGenCoord(8192, 3168);
        public final TexGenCoord t = new TexGenCoord(8193, 3169);
        public final TexGenCoord r = new TexGenCoord(8194, 3170);
        public final TexGenCoord q = new TexGenCoord(8195, 3171);

        private TexGenState() {
        }
    }

    public static enum Viewport {
        INSTANCE;

        protected int x;
        protected int y;
        protected int w;
        protected int h;
    }

    static class ColorMask {
        public boolean red = true;
        public boolean green = true;
        public boolean blue = true;
        public boolean alpha = true;

        private ColorMask() {
        }
    }

    static class StencilState {
        public final StencilFunc func = new StencilFunc();
        public int mask = -1;
        public int sfail = 7680;
        public int dpfail = 7680;
        public int dppass = 7680;

        private StencilState() {
        }
    }

    static class StencilFunc {
        public int func = 519;
        public int ref;
        public int mask = -1;

        private StencilFunc() {
        }
    }

    @Deprecated
    static class Color {
        public float red = 1.0f;
        public float green = 1.0f;
        public float blue = 1.0f;
        public float alpha = 1.0f;

        public Color() {
            this(1.0f, 1.0f, 1.0f, 1.0f);
        }

        public Color(float red, float green, float blue, float alpha) {
            this.red = red;
            this.green = green;
            this.blue = blue;
            this.alpha = alpha;
        }
    }

    public static enum SourceFactor {
        CONSTANT_ALPHA(32771),
        CONSTANT_COLOR(32769),
        DST_ALPHA(772),
        DST_COLOR(774),
        ONE(1),
        ONE_MINUS_CONSTANT_ALPHA(32772),
        ONE_MINUS_CONSTANT_COLOR(32770),
        ONE_MINUS_DST_ALPHA(773),
        ONE_MINUS_DST_COLOR(775),
        ONE_MINUS_SRC_ALPHA(771),
        ONE_MINUS_SRC_COLOR(769),
        SRC_ALPHA(770),
        SRC_ALPHA_SATURATE(776),
        SRC_COLOR(768),
        ZERO(0);

        public final int param;

        private SourceFactor(int param) {
            this.param = param;
        }
    }

    public static enum LogicOp {
        AND(5377),
        AND_INVERTED(5380),
        AND_REVERSE(5378),
        CLEAR(5376),
        COPY(5379),
        COPY_INVERTED(5388),
        EQUIV(5385),
        INVERT(5386),
        NAND(5390),
        NOOP(5381),
        NOR(5384),
        OR(5383),
        OR_INVERTED(5389),
        OR_REVERSE(5387),
        SET(5391),
        XOR(5382);

        public final int opcode;

        private LogicOp(int opCode) {
            this.opcode = opCode;
        }
    }

    @Deprecated
    public static enum FogMode {
        LINEAR(9729),
        EXP(2048),
        EXP2(2049);

        public final int param;

        private FogMode(int param) {
            this.param = param;
        }
    }

    public static enum DestFactor {
        CONSTANT_ALPHA(32771),
        CONSTANT_COLOR(32769),
        DST_ALPHA(772),
        DST_COLOR(774),
        ONE(1),
        ONE_MINUS_CONSTANT_ALPHA(32772),
        ONE_MINUS_CONSTANT_COLOR(32770),
        ONE_MINUS_DST_ALPHA(773),
        ONE_MINUS_DST_COLOR(775),
        ONE_MINUS_SRC_ALPHA(771),
        ONE_MINUS_SRC_COLOR(769),
        SRC_ALPHA(770),
        SRC_COLOR(768),
        ZERO(0);

        public final int param;

        private DestFactor(int param) {
            this.param = param;
        }
    }
}

