// This is a generated file. Not intended for manual editing.
package com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi;

import java.util.List;

import com.vesoft.jetbrains.plugin.graphdb.language.cypher.references.types.CypherAnyYielding;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface CypherCaseExpression extends CypherAnyYielding {

  @NotNull
  List<CypherCaseAlternatives> getCaseAlternativesList();

  @NotNull
  List<CypherExpression> getExpressionList();

  @NotNull
  PsiElement getKCase();

  @Nullable
  PsiElement getKElse();

  @NotNull
  PsiElement getKEnd();

}
