// This is a generated file. Not intended for manual editing.
package com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi.impl;

import com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi.CypherNodePattern;
import com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi.CypherPatternElementChain;
import com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi.CypherRelationshipPattern;
import com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi.CypherVisitor;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi.*;

public class CypherPatternElementChainImpl extends ASTWrapperPsiElement implements CypherPatternElementChain {

  public CypherPatternElementChainImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull CypherVisitor visitor) {
    visitor.visitPatternElementChain(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CypherVisitor) accept((CypherVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public CypherNodePattern getNodePattern() {
    return findChildByClass(CypherNodePattern.class);
  }

  @Override
  @NotNull
  public CypherRelationshipPattern getRelationshipPattern() {
    return findNotNullChildByClass(CypherRelationshipPattern.class);
  }

}
