package com.vesoft.jetbrains.plugin.graphdb.language.ngql;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import org.jetbrains.annotations.NotNull;

/**
 * 业务说明：
 *
 * @Author jiangyiwang-jk
 * @Date 2024/1/4 10:38
 */
public class NGqlFile extends PsiFileBase {

    protected NGqlFile(@NotNull FileViewProvider viewProvider) {
        super(viewProvider, NGqlLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public FileType getFileType() {
        return NGqlFileType.INSTANCE;
    }
}
