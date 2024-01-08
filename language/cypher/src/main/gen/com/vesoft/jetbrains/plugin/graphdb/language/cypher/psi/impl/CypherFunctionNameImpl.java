// This is a generated file. Not intended for manual editing.
package com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi.impl;

import com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi.CypherEscapedSymbolicNameString;
import com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi.CypherFunctionName;
import com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi.CypherUnescapedSymbolicNameString;
import com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi.CypherVisitor;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;

import static com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi.CypherTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi.*;

public class CypherFunctionNameImpl extends ASTWrapperPsiElement implements CypherFunctionName {

  public CypherFunctionNameImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull CypherVisitor visitor) {
    visitor.visitFunctionName(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CypherVisitor) accept((CypherVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public CypherEscapedSymbolicNameString getEscapedSymbolicNameString() {
    return findChildByClass(CypherEscapedSymbolicNameString.class);
  }

  @Override
  @Nullable
  public CypherUnescapedSymbolicNameString getUnescapedSymbolicNameString() {
    return findChildByClass(CypherUnescapedSymbolicNameString.class);
  }

  @Override
  @Nullable
  public PsiElement getKCount() {
    return findChildByType(K_COUNT);
  }

}
