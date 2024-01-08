package com.vesoft.jetbrains.plugin.graphdb.language.cypher.references.types;

import com.intellij.psi.PsiElement;
import com.vesoft.jetbrains.plugin.graphdb.language.cypher.completion.metadata.atoms.CypherType;

import static com.vesoft.jetbrains.plugin.graphdb.language.cypher.completion.metadata.atoms.CypherSimpleType.ANY;

public interface CypherTypePropagator extends CypherTyped {

    @Override
    default CypherType getType() {
        PsiElement[] children = getChildren();
        if (children.length != 1) {
            return ANY;
        }

        PsiElement child = children[0];
        if (child instanceof CypherTyped) {
            return ((CypherTyped) child).getType();
        }
        return ANY;
    }

}
