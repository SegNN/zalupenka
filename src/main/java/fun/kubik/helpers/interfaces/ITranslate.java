/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.helpers.interfaces;

import fun.kubik.helpers.translate.TranslateHelpers;

public interface ITranslate {
    default public String getTranslation(String text) {
        return new TranslateHelpers().has(text) ? new TranslateHelpers().get(text) : text;
    }
}

