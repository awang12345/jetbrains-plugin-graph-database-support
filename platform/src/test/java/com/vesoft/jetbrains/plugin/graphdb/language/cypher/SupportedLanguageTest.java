package com.vesoft.jetbrains.plugin.graphdb.language.cypher;

import com.vesoft.jetbrains.plugin.graphdb.platform.SupportedLanguage;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SupportedLanguageTest {

    @Test
    public void languageSupported() {
        assertTrue(SupportedLanguage.isSupported(SupportedLanguage.CYPHER.getLanguageId()));
    }

    @Test
    public void languageUnsupported() {
        assertFalse(SupportedLanguage.isSupported("Java"));
    }

}
