// This is a generated file. Not intended for manual editing.
package com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi.impl;

import com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi.CypherBulkImportQuery;
import com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi.CypherLoadCSVQuery;
import com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi.CypherPeriodicCommitHint;
import com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi.CypherVisitor;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi.*;

public class CypherBulkImportQueryImpl extends ASTWrapperPsiElement implements CypherBulkImportQuery {

  public CypherBulkImportQueryImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull CypherVisitor visitor) {
    visitor.visitBulkImportQuery(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CypherVisitor) accept((CypherVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public CypherLoadCSVQuery getLoadCSVQuery() {
    return findNotNullChildByClass(CypherLoadCSVQuery.class);
  }

  @Override
  @NotNull
  public CypherPeriodicCommitHint getPeriodicCommitHint() {
    return findNotNullChildByClass(CypherPeriodicCommitHint.class);
  }

}
