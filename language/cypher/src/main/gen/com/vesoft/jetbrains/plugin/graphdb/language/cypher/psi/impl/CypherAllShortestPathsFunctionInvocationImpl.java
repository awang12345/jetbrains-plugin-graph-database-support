// This is a generated file. Not intended for manual editing.
package com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi.impl;

import com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi.CypherAllShortestPathsFunctionInvocation;
import com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi.CypherPatternElement;
import com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi.CypherVisitor;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;

import static com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi.CypherTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi.*;

public class CypherAllShortestPathsFunctionInvocationImpl extends ASTWrapperPsiElement implements CypherAllShortestPathsFunctionInvocation {

  public CypherAllShortestPathsFunctionInvocationImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull CypherVisitor visitor) {
    visitor.visitAllShortestPathsFunctionInvocation(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CypherVisitor) accept((CypherVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public CypherPatternElement getPatternElement() {
    return findNotNullChildByClass(CypherPatternElement.class);
  }

  @Override
  @NotNull
  public PsiElement getKAllshortestpaths() {
    return findNotNullChildByType(K_ALLSHORTESTPATHS);
  }

}
