package com.neueda.jetbrains.plugin.graphdb.language.cypher.completion.metadata.elements;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import icons.GraphIcons;

public class NebulaLabelElement implements CypherElement {

    private final String label;

    public NebulaLabelElement(String label) {
        this.label = label;
    }

    @Override
    public LookupElement getLookupElement() {
        return LookupElementBuilder.create(label)
                .withIcon(GraphIcons.Nodes.LABEL)
                .withTypeText("label");
    }

    public String getName() {
        return label;
    }
}
