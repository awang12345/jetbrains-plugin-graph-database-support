// This is a generated file. Not intended for manual editing.
package com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi;

import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface CypherReturnBody extends PsiElement {

  @Nullable
  CypherLimit getLimit();

  @Nullable
  CypherOrder getOrder();

  @NotNull
  CypherReturnItems getReturnItems();

  @Nullable
  CypherSkip getSkip();

}
