// This is a generated file. Not intended for manual editing.
package com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi.impl;

import com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi.CypherFilterExpression;
import com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi.CypherNoneFunctionInvocation;
import com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi.CypherPsiImplUtil;
import com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi.CypherVisitor;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;

import static com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi.CypherTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi.*;

public class CypherNoneFunctionInvocationImpl extends ASTWrapperPsiElement implements CypherNoneFunctionInvocation {

  public CypherNoneFunctionInvocationImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull CypherVisitor visitor) {
    visitor.visitNoneFunctionInvocation(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CypherVisitor) accept((CypherVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public CypherFilterExpression getFilterExpression() {
    return findNotNullChildByClass(CypherFilterExpression.class);
  }

  @Override
  @NotNull
  public PsiElement getKNone() {
    return findNotNullChildByType(K_NONE);
  }

  @Override
  public String getFullName() {
    return CypherPsiImplUtil.getFullName(this);
  }

}
