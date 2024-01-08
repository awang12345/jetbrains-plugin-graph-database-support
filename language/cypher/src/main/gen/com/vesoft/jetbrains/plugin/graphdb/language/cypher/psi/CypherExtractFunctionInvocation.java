// This is a generated file. Not intended for manual editing.
package com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi;

import com.vesoft.jetbrains.plugin.graphdb.language.cypher.references.CypherInvocation;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface CypherExtractFunctionInvocation extends CypherInvocation {

  @Nullable
  CypherExpression getExpression();

  @NotNull
  CypherFilterExpression getFilterExpression();

  @NotNull
  PsiElement getKExtract();

  String getFullName();

}
