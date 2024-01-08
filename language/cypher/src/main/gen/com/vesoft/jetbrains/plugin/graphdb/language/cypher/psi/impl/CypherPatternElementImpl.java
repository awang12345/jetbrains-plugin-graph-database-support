// This is a generated file. Not intended for manual editing.
package com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi.impl;

import java.util.List;

import com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi.CypherNodePattern;
import com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi.CypherPatternElement;
import com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi.CypherPatternElementChain;
import com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi.CypherVisitor;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi.*;

public class CypherPatternElementImpl extends ASTWrapperPsiElement implements CypherPatternElement {

  public CypherPatternElementImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull CypherVisitor visitor) {
    visitor.visitPatternElement(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CypherVisitor) accept((CypherVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public CypherNodePattern getNodePattern() {
    return findChildByClass(CypherNodePattern.class);
  }

  @Override
  @Nullable
  public CypherPatternElement getPatternElement() {
    return findChildByClass(CypherPatternElement.class);
  }

  @Override
  @NotNull
  public List<CypherPatternElementChain> getPatternElementChainList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, CypherPatternElementChain.class);
  }

}
