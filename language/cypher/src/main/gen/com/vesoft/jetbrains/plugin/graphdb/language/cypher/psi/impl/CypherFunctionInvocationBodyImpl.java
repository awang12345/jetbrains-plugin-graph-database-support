// This is a generated file. Not intended for manual editing.
package com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi.impl;

import com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi.CypherFunctionInvocationBody;
import com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi.CypherFunctionName;
import com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi.CypherNamespace;
import com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi.CypherVisitor;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi.*;

public class CypherFunctionInvocationBodyImpl extends ASTWrapperPsiElement implements CypherFunctionInvocationBody {

  public CypherFunctionInvocationBodyImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull CypherVisitor visitor) {
    visitor.visitFunctionInvocationBody(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CypherVisitor) accept((CypherVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public CypherFunctionName getFunctionName() {
    return findNotNullChildByClass(CypherFunctionName.class);
  }

  @Override
  @NotNull
  public CypherNamespace getNamespace() {
    return findNotNullChildByClass(CypherNamespace.class);
  }

}
