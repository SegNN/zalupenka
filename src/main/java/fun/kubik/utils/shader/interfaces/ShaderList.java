/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.utils.shader.interfaces;

import fun.kubik.utils.shader.AbstractShader;
import fun.kubik.utils.shader.list.BlackRectShader;
import fun.kubik.utils.shader.list.BloomShader;
import fun.kubik.utils.shader.list.BlurShader;
import fun.kubik.utils.shader.list.WhiteRectShader;

public interface ShaderList {
    public static final AbstractShader BLUR_SHADER = new BlurShader();
    public static final AbstractShader BLOOM_SHADER = new BloomShader();
    public static final AbstractShader WHITE_RECT_SHADER = new WhiteRectShader();
    public static final AbstractShader BLACK_RECT_SHADER = new BlackRectShader();
}

