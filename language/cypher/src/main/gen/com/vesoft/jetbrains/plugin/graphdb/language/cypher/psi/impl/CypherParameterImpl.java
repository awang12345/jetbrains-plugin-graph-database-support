// This is a generated file. Not intended for manual editing.
package com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi.impl;

import com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi.*;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi.*;

public class CypherParameterImpl extends ASTWrapperPsiElement implements CypherParameter {

  public CypherParameterImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull CypherVisitor visitor) {
    visitor.visitParameter(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CypherVisitor) accept((CypherVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public CypherNewParameter getNewParameter() {
    return findChildByClass(CypherNewParameter.class);
  }

  @Override
  @Nullable
  public CypherOldParameter getOldParameter() {
    return findChildByClass(CypherOldParameter.class);
  }

  @Override
  public String getParameterName() {
    return CypherPsiImplUtil.getParameterName(this);
  }

}
