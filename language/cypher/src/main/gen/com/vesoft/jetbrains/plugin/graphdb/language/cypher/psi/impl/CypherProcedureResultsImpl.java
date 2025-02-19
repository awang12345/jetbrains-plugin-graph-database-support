// This is a generated file. Not intended for manual editing.
package com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi.impl;

import java.util.List;

import com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi.CypherProcedureResult;
import com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi.CypherProcedureResults;
import com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi.CypherVisitor;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi.CypherTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi.*;

public class CypherProcedureResultsImpl extends ASTWrapperPsiElement implements CypherProcedureResults {

  public CypherProcedureResultsImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull CypherVisitor visitor) {
    visitor.visitProcedureResults(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CypherVisitor) accept((CypherVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<CypherProcedureResult> getProcedureResultList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, CypherProcedureResult.class);
  }

  @Override
  @NotNull
  public PsiElement getKYield() {
    return findNotNullChildByType(K_YIELD);
  }

}
