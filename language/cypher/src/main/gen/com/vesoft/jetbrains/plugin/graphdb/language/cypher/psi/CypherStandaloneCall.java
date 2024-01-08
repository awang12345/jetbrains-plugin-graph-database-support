// This is a generated file. Not intended for manual editing.
package com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi;

import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface CypherStandaloneCall extends PsiElement {

  @NotNull
  CypherCall getCall();

  @Nullable
  CypherReturn getReturn();

  @Nullable
  CypherWhere getWhere();

}
