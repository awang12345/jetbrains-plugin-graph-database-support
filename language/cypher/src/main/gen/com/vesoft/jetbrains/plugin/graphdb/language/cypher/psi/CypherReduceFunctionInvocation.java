// This is a generated file. Not intended for manual editing.
package com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi;

import java.util.List;

import com.vesoft.jetbrains.plugin.graphdb.language.cypher.references.CypherInvocation;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface CypherReduceFunctionInvocation extends CypherInvocation {

  @NotNull
  List<CypherExpression> getExpressionList();

  @NotNull
  CypherIdInColl getIdInColl();

  @NotNull
  CypherVariable getVariable();

  @NotNull
  PsiElement getKReduce();

  String getFullName();

}
