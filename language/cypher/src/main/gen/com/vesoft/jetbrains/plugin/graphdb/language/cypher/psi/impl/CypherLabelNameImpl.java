// This is a generated file. Not intended for manual editing.
package com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi.impl;

import com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi.CypherLabelName;
import com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi.CypherPsiImplUtil;
import com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi.CypherSymbolicNameString;
import com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi.CypherVisitor;
import com.vesoft.jetbrains.plugin.graphdb.language.cypher.references.CypherNamedElementImpl;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi.*;
import com.intellij.psi.PsiReference;

public class CypherLabelNameImpl extends CypherNamedElementImpl implements CypherLabelName {

  public CypherLabelNameImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull CypherVisitor visitor) {
    visitor.visitLabelName(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CypherVisitor) accept((CypherVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public CypherSymbolicNameString getSymbolicNameString() {
    return findNotNullChildByClass(CypherSymbolicNameString.class);
  }

  @Override
  public String getName() {
    return CypherPsiImplUtil.getName(this);
  }

  @Override
  public CypherLabelName setName(String newName) {
    return CypherPsiImplUtil.setName(this, newName);
  }

  @Override
  public PsiElement getNameIdentifier() {
    return CypherPsiImplUtil.getNameIdentifier(this);
  }

  @Override
  @NotNull
  public PsiReference[] getReferences() {
    return CypherPsiImplUtil.getReferences(this);
  }

}
