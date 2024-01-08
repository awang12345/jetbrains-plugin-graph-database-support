// This is a generated file. Not intended for manual editing.
package com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi.impl;

import com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi.CypherNamespace;
import com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi.CypherProcedureInvocationBody;
import com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi.CypherProcedureName;
import com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi.CypherVisitor;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi.*;

public class CypherProcedureInvocationBodyImpl extends ASTWrapperPsiElement implements CypherProcedureInvocationBody {

  public CypherProcedureInvocationBodyImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull CypherVisitor visitor) {
    visitor.visitProcedureInvocationBody(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CypherVisitor) accept((CypherVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public CypherNamespace getNamespace() {
    return findNotNullChildByClass(CypherNamespace.class);
  }

  @Override
  @NotNull
  public CypherProcedureName getProcedureName() {
    return findNotNullChildByClass(CypherProcedureName.class);
  }

}
