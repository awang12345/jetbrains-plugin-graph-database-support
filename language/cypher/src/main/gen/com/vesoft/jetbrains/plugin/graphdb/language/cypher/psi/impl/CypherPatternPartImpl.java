// This is a generated file. Not intended for manual editing.
package com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi.impl;

import com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi.CypherAnonymousPatternPart;
import com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi.CypherPatternPart;
import com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi.CypherVariable;
import com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi.CypherVisitor;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi.*;

public class CypherPatternPartImpl extends ASTWrapperPsiElement implements CypherPatternPart {

  public CypherPatternPartImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull CypherVisitor visitor) {
    visitor.visitPatternPart(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CypherVisitor) accept((CypherVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public CypherAnonymousPatternPart getAnonymousPatternPart() {
    return findNotNullChildByClass(CypherAnonymousPatternPart.class);
  }

  @Override
  @Nullable
  public CypherVariable getVariable() {
    return findChildByClass(CypherVariable.class);
  }

}
