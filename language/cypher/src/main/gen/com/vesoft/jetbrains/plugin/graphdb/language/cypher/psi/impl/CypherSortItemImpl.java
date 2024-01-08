// This is a generated file. Not intended for manual editing.
package com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi.impl;

import com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi.CypherExpression;
import com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi.CypherSortItem;
import com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi.CypherVisitor;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;

import static com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi.CypherTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi.*;

public class CypherSortItemImpl extends ASTWrapperPsiElement implements CypherSortItem {

  public CypherSortItemImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull CypherVisitor visitor) {
    visitor.visitSortItem(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CypherVisitor) accept((CypherVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public CypherExpression getExpression() {
    return findNotNullChildByClass(CypherExpression.class);
  }

  @Override
  @Nullable
  public PsiElement getKAsc() {
    return findChildByType(K_ASC);
  }

  @Override
  @Nullable
  public PsiElement getKAscending() {
    return findChildByType(K_ASCENDING);
  }

  @Override
  @Nullable
  public PsiElement getKDesc() {
    return findChildByType(K_DESC);
  }

  @Override
  @Nullable
  public PsiElement getKDescending() {
    return findChildByType(K_DESCENDING);
  }

}
