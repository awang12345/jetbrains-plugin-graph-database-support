package com.vesoft.jetbrains.plugin.graphdb.language.ngql;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.HighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.vesoft.jetbrains.plugin.graphdb.language.ngql.psi.NGqlTypes;
import org.jetbrains.annotations.NotNull;

import static com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey;

/**
 * 业务说明：
 *
 * @Author jiangyiwang-jk
 * @Date 2024/1/4 10:29
 */
public class NGqlSyntaxHighlighter extends SyntaxHighlighterBase {
    private TextAttributesKey SEPARATOR = createTextAttributesKey("SIMPLE_SEPARATOR", DefaultLanguageHighlighterColors.OPERATION_SIGN);
    private TextAttributesKey IDENTIFIER_KEY = createTextAttributesKey("SIMPLE_IDENTIFIER", DefaultLanguageHighlighterColors.IDENTIFIER);
    private TextAttributesKey KEY = createTextAttributesKey("SIMPLE_KEY", DefaultLanguageHighlighterColors.KEYWORD);
    private TextAttributesKey STRING_KEY = createTextAttributesKey("SIMPLE_STRING", DefaultLanguageHighlighterColors.STRING);
    private TextAttributesKey FUNCTION_D = createTextAttributesKey("SIMPLE_FUNCTION", DefaultLanguageHighlighterColors.FUNCTION_DECLARATION);
    private TextAttributesKey CONSTANT = createTextAttributesKey("SIMPLE_CONSTANT", DefaultLanguageHighlighterColors.CONSTANT);
    private TextAttributesKey COMMENT = createTextAttributesKey("SIMPLE_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT);
    private TextAttributesKey BLOCK_COMMENT = createTextAttributesKey("SIMPLE_BLOCK_COMMENT", DefaultLanguageHighlighterColors.BLOCK_COMMENT);
    private TextAttributesKey DOC_COMMENT = createTextAttributesKey("SIMPLE_DOC_COMMENT", DefaultLanguageHighlighterColors.DOC_COMMENT);
    public TextAttributesKey BAD_CHARACTER = createTextAttributesKey("SIMPLE_BAD_CHARACTER", HighlighterColors.BAD_CHARACTER);

    private TextAttributesKey[] BAD_CHAR_KEYS = new TextAttributesKey[]{BAD_CHARACTER};

    private TextAttributesKey[] TYPE = new TextAttributesKey[]{CONSTANT};
    private TextAttributesKey[] KEYWORD = new TextAttributesKey[]{KEY};
    private TextAttributesKey[] FUNCTION = new TextAttributesKey[]{FUNCTION_D};
    private TextAttributesKey[] IDENTIFIER = new TextAttributesKey[]{IDENTIFIER_KEY};
    private TextAttributesKey[] STRING = new TextAttributesKey[]{STRING_KEY};
    private TextAttributesKey[] COMMENT_KEYS = new TextAttributesKey[]{COMMENT, BLOCK_COMMENT, DOC_COMMENT};
    private TextAttributesKey[] EMPTY_KEYS = new TextAttributesKey[0];


    @NotNull
    @Override
    public Lexer getHighlightingLexer() {
        return new NGqlLexerAdapter();
    }

    @NotNull
    @Override
    public TextAttributesKey[] getTokenHighlights(IElementType tokenType) {
        if (tokenType == NGqlTypes.IDENTIFIER) {
            return IDENTIFIER;
        }
        if (tokenType == NGqlTypes.STRING) {
            return STRING;
        }
        if (tokenType == NGqlTypes.FUNCTION) {
            return FUNCTION;
        }
        if (tokenType == NGqlTypes.TYPE) {
            return TYPE;
        }
        if (tokenType == NGqlTypes.KEYWORD) {
            return KEYWORD;
        }
        if (tokenType == NGqlTypes.COMMENT) {
            return COMMENT_KEYS;
        }
        return EMPTY_KEYS;
    }
}
