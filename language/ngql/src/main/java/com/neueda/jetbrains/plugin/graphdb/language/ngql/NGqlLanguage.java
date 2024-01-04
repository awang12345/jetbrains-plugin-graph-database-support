package com.neueda.jetbrains.plugin.graphdb.language.ngql;

import com.intellij.lang.Language;
import com.neueda.jetbrains.plugin.graphdb.platform.SupportedLanguage;

/**
 * 业务说明：
 *
 * @Author jiangyiwang-jk
 * @Date 2024/1/4 10:19
 */
public class NGqlLanguage extends Language {

    public static final NGqlLanguage INSTANCE = new NGqlLanguage();


    protected NGqlLanguage() {
        super(SupportedLanguage.NGQL.getLanguageId());
    }
}
