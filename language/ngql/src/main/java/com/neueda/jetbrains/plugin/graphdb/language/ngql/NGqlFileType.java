package com.neueda.jetbrains.plugin.graphdb.language.ngql;

import com.intellij.lang.Language;
import com.intellij.openapi.fileTypes.LanguageFileType;
import icons.GraphIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * 业务说明：
 *
 * @Author jiangyiwang-jk
 * @Date 2024/1/4 10:36
 */
public class NGqlFileType extends LanguageFileType {

    public static final NGqlFileType INSTANCE = new NGqlFileType();

    protected NGqlFileType() {
        super(NGqlLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public String getName() {
        return "nGQL";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Nebula Graph Query Language";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return "ngql";
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return GraphIcons.Database.NEBULA;
    }
}
