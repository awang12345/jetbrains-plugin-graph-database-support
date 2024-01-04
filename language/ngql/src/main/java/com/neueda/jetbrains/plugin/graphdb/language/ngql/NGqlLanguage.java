package com.neueda.jetbrains.plugin.graphdb.language.ngql;

import com.intellij.lang.Language;
import org.jetbrains.annotations.NotNull;

/**
 * 业务说明：
 *
 * @Author jiangyiwang-jk
 * @Date 2024/1/4 10:19
 */
public class NGqlLanguage extends Language {

    public static final NGqlLanguage INSTANCE = new NGqlLanguage();

    private static final String name = "nGQL";

    protected NGqlLanguage() {
        super(name);
    }
}
