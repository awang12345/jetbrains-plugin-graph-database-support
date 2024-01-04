package com.neueda.jetbrains.plugin.graphdb.language.ngql.psi;

import com.intellij.lang.Language;
import com.intellij.psi.tree.IElementType;
import com.neueda.jetbrains.plugin.graphdb.language.ngql.NGqlLanguage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 业务说明：
 *
 * @Author jiangyiwang-jk
 * @Date 2024/1/4 10:18
 */
public class NGqlElementType extends IElementType {
    public NGqlElementType(@NotNull String debugName) {
        super(debugName, NGqlLanguage.INSTANCE);
    }

}
