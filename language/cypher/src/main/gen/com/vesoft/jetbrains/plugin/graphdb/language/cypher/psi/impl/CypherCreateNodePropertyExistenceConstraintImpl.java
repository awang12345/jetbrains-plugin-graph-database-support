// This is a generated file. Not intended for manual editing.
package com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi.impl;

import com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi.CypherCreateNodePropertyExistenceConstraint;
import com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi.CypherNodePropertyExistenceConstraintSyntax;
import com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi.CypherVisitor;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;

import static com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi.CypherTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi.*;

public class CypherCreateNodePropertyExistenceConstraintImpl extends ASTWrapperPsiElement implements CypherCreateNodePropertyExistenceConstraint {

  public CypherCreateNodePropertyExistenceConstraintImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull CypherVisitor visitor) {
    visitor.visitCreateNodePropertyExistenceConstraint(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CypherVisitor) accept((CypherVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public CypherNodePropertyExistenceConstraintSyntax getNodePropertyExistenceConstraintSyntax() {
    return findNotNullChildByClass(CypherNodePropertyExistenceConstraintSyntax.class);
  }

  @Override
  @NotNull
  public PsiElement getKCreate() {
    return findNotNullChildByType(K_CREATE);
  }

}
