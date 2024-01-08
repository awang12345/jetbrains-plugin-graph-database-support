package com.vesoft.jetbrains.plugin.graphdb.language.ngql.psi;

import com.intellij.lang.Language;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 业务说明：
 *
 * @Author jiangyiwang-jk
 * @Date 2024/1/4 10:11
 */
public class NGqlTokenType extends IElementType {

    public NGqlTokenType(@NotNull String debugName, @Nullable Language language) {
        super(debugName, language);
    }

    protected NGqlTokenType(@NotNull String debugName, @Nullable Language language, boolean register) {
        super(debugName, language, register);
    }

    @Override
    public String toString() {
        return "NGqlTokenType:" + super.toString();
    }
}
