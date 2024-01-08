// This is a generated file. Not intended for manual editing.
package com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi;

import com.vesoft.jetbrains.plugin.graphdb.language.cypher.references.CypherInvocation;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface CypherExistsFunctionInvocation extends CypherInvocation {

  @NotNull
  CypherExpression getExpression();

  @NotNull
  PsiElement getKExists();

  String getFullName();

}
