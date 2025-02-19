// This is a generated file. Not intended for manual editing.
package com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi.impl;

import com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi.CypherLoadCSV;
import com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi.CypherLoadCSVQuery;
import com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi.CypherSingleQuery;
import com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi.CypherVisitor;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi.*;

public class CypherLoadCSVQueryImpl extends ASTWrapperPsiElement implements CypherLoadCSVQuery {

  public CypherLoadCSVQueryImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull CypherVisitor visitor) {
    visitor.visitLoadCSVQuery(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CypherVisitor) accept((CypherVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public CypherLoadCSV getLoadCSV() {
    return findNotNullChildByClass(CypherLoadCSV.class);
  }

  @Override
  @Nullable
  public CypherSingleQuery getSingleQuery() {
    return findChildByClass(CypherSingleQuery.class);
  }

}
