// This is a generated file. Not intended for manual editing.
package com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi.impl;

import com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi.CypherCall;
import com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi.CypherInQueryCall;
import com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi.CypherVisitor;
import com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi.CypherWhere;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi.*;

public class CypherInQueryCallImpl extends ASTWrapperPsiElement implements CypherInQueryCall {

  public CypherInQueryCallImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull CypherVisitor visitor) {
    visitor.visitInQueryCall(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CypherVisitor) accept((CypherVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public CypherCall getCall() {
    return findNotNullChildByClass(CypherCall.class);
  }

  @Override
  @Nullable
  public CypherWhere getWhere() {
    return findChildByClass(CypherWhere.class);
  }

}
