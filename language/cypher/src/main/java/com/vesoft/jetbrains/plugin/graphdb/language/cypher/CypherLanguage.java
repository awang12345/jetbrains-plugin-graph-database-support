package com.vesoft.jetbrains.plugin.graphdb.language.cypher;

import com.intellij.lang.Language;

import static com.vesoft.jetbrains.plugin.graphdb.platform.SupportedLanguage.CYPHER;

/**
 * @author dmitry@vrublesvky.me
 */
public final class CypherLanguage extends Language {

    public static final CypherLanguage INSTANCE = new CypherLanguage();

    private CypherLanguage() {
        super(CYPHER.getLanguageId());
    }
}
