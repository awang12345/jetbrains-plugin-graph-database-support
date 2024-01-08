package com.vesoft.jetbrains.plugin.graphdb.language.cypher.references.types;

import com.intellij.psi.PsiElement;
import com.vesoft.jetbrains.plugin.graphdb.language.cypher.completion.metadata.atoms.CypherType;

public interface CypherTyped extends PsiElement {

    CypherType getType();

}
