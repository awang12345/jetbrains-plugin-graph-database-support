// This is a generated file. Not intended for manual editing.
package com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi.impl;

import com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi.CypherPropertyExpression;
import com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi.CypherRelationshipPatternSyntax;
import com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi.CypherRelationshipPropertyExistenceConstraintSyntax;
import com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi.CypherVisitor;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;

import static com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi.CypherTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi.*;

public class CypherRelationshipPropertyExistenceConstraintSyntaxImpl extends ASTWrapperPsiElement implements CypherRelationshipPropertyExistenceConstraintSyntax {

  public CypherRelationshipPropertyExistenceConstraintSyntaxImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull CypherVisitor visitor) {
    visitor.visitRelationshipPropertyExistenceConstraintSyntax(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CypherVisitor) accept((CypherVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public CypherPropertyExpression getPropertyExpression() {
    return findNotNullChildByClass(CypherPropertyExpression.class);
  }

  @Override
  @NotNull
  public CypherRelationshipPatternSyntax getRelationshipPatternSyntax() {
    return findNotNullChildByClass(CypherRelationshipPatternSyntax.class);
  }

  @Override
  @NotNull
  public PsiElement getKAssert() {
    return findNotNullChildByType(K_ASSERT);
  }

  @Override
  @NotNull
  public PsiElement getKConstraint() {
    return findNotNullChildByType(K_CONSTRAINT);
  }

  @Override
  @NotNull
  public PsiElement getKExists() {
    return findNotNullChildByType(K_EXISTS);
  }

  @Override
  @NotNull
  public PsiElement getKOn() {
    return findNotNullChildByType(K_ON);
  }

}
