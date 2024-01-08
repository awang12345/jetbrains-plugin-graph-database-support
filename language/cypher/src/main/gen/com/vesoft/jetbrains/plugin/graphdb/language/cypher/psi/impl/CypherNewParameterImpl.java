// This is a generated file. Not intended for manual editing.
package com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi.impl;

import com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi.CypherNewParameter;
import com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi.CypherSymbolicNameString;
import com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi.CypherUnsignedInteger;
import com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi.CypherVisitor;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi.*;

public class CypherNewParameterImpl extends ASTWrapperPsiElement implements CypherNewParameter {

  public CypherNewParameterImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull CypherVisitor visitor) {
    visitor.visitNewParameter(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CypherVisitor) accept((CypherVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public CypherSymbolicNameString getSymbolicNameString() {
    return findChildByClass(CypherSymbolicNameString.class);
  }

  @Override
  @Nullable
  public CypherUnsignedInteger getUnsignedInteger() {
    return findChildByClass(CypherUnsignedInteger.class);
  }

}
