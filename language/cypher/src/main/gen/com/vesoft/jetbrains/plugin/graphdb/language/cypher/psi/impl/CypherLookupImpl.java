// This is a generated file. Not intended for manual editing.
package com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi.impl;

import com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi.CypherLookup;
import com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi.CypherNodeLookup;
import com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi.CypherRelationshipLookup;
import com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi.CypherVisitor;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi.*;

public class CypherLookupImpl extends ASTWrapperPsiElement implements CypherLookup {

  public CypherLookupImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull CypherVisitor visitor) {
    visitor.visitLookup(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CypherVisitor) accept((CypherVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public CypherNodeLookup getNodeLookup() {
    return findChildByClass(CypherNodeLookup.class);
  }

  @Override
  @Nullable
  public CypherRelationshipLookup getRelationshipLookup() {
    return findChildByClass(CypherRelationshipLookup.class);
  }

}
