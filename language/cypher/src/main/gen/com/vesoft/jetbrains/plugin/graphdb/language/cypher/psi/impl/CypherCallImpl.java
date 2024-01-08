// This is a generated file. Not intended for manual editing.
package com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi.impl;

import com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi.CypherCall;
import com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi.CypherProcedureInvocation;
import com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi.CypherProcedureResults;
import com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi.CypherVisitor;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;

import static com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi.CypherTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi.*;

public class CypherCallImpl extends ASTWrapperPsiElement implements CypherCall {

  public CypherCallImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull CypherVisitor visitor) {
    visitor.visitCall(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CypherVisitor) accept((CypherVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public CypherProcedureInvocation getProcedureInvocation() {
    return findNotNullChildByClass(CypherProcedureInvocation.class);
  }

  @Override
  @Nullable
  public CypherProcedureResults getProcedureResults() {
    return findChildByClass(CypherProcedureResults.class);
  }

  @Override
  @NotNull
  public PsiElement getKCall() {
    return findNotNullChildByType(K_CALL);
  }

}
