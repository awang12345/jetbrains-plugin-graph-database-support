// This is a generated file. Not intended for manual editing.
package com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi.impl;

import com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi.CypherDoubleLiteral;
import com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi.CypherIntegerLiteral;
import com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi.CypherNumberLiteral;
import com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi.CypherVisitor;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi.*;

public class CypherNumberLiteralImpl extends ASTWrapperPsiElement implements CypherNumberLiteral {

  public CypherNumberLiteralImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull CypherVisitor visitor) {
    visitor.visitNumberLiteral(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CypherVisitor) accept((CypherVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public CypherDoubleLiteral getDoubleLiteral() {
    return findChildByClass(CypherDoubleLiteral.class);
  }

  @Override
  @Nullable
  public CypherIntegerLiteral getIntegerLiteral() {
    return findChildByClass(CypherIntegerLiteral.class);
  }

}
