// This is a generated file. Not intended for manual editing.
package com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi.impl;

import com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi.*;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi.*;

public class CypherQueryImpl extends ASTWrapperPsiElement implements CypherQuery {

  public CypherQueryImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull CypherVisitor visitor) {
    visitor.visitQuery(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CypherVisitor) accept((CypherVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public CypherBulkImportQuery getBulkImportQuery() {
    return findChildByClass(CypherBulkImportQuery.class);
  }

  @Override
  @Nullable
  public CypherRegularQuery getRegularQuery() {
    return findChildByClass(CypherRegularQuery.class);
  }

  @Override
  @Nullable
  public CypherStandaloneCall getStandaloneCall() {
    return findChildByClass(CypherStandaloneCall.class);
  }

}
