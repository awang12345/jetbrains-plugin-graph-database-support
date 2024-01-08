package com.vesoft.jetbrains.plugin.graphdb.language.ngql;

/**
 * 业务说明：
 *
 * @Author jiangyiwang-jk
 * @Date 2024/1/4 10:34
 */
public class NGqlLexerAdapter extends com.intellij.lexer.FlexAdapter {
    public NGqlLexerAdapter() {
        super(new NGqlLexer(null));
    }
}
