// This is a generated file. Not intended for manual editing.
package com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi;

import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface CypherQuery extends PsiElement {

  @Nullable
  CypherBulkImportQuery getBulkImportQuery();

  @Nullable
  CypherRegularQuery getRegularQuery();

  @Nullable
  CypherStandaloneCall getStandaloneCall();

}
