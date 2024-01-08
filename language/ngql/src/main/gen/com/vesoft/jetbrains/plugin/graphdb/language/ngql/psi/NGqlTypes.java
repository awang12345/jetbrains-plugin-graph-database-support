package com.vesoft.jetbrains.plugin.graphdb.language.ngql.psi;

import com.intellij.psi.tree.IElementType;

/**
 * 业务说明：
 *
 * @Author jiangyiwang-jk
 * @Date 2024/1/4 10:18
 */
public interface NGqlTypes {

    IElementType NGQL = new NGqlElementType("NGQL");

    IElementType COMMENT = new NGqlElementType("COMMENT");
    IElementType CRLF = new NGqlElementType("CRLF");
    IElementType DELETE = new NGqlElementType("DELETE");
    IElementType INSERT = new NGqlElementType("INSERT");
    IElementType QUERY = new NGqlElementType("QUERY");
    IElementType UPDATE = new NGqlElementType("UPDATE");
    IElementType KEYWORD = new NGqlElementType("KEYWORD");
    IElementType TYPE = new NGqlElementType("TYPE");
    IElementType FUNCTION = new NGqlElementType("FUNCTION");
    IElementType IDENTIFIER = new NGqlElementType("IDENTIFIER");
    IElementType STRING = new NGqlElementType("STRING");
    IElementType WHITE_SPACE = new NGqlElementType("WHITE_SPACE");
    IElementType SEMICOLON = new NGqlElementType(";");

}
