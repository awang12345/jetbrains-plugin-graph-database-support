// This is a generated file. Not intended for manual editing.
package com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi;

import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface CypherSortItem extends PsiElement {

  @NotNull
  CypherExpression getExpression();

  @Nullable
  PsiElement getKAsc();

  @Nullable
  PsiElement getKAscending();

  @Nullable
  PsiElement getKDesc();

  @Nullable
  PsiElement getKDescending();

}
