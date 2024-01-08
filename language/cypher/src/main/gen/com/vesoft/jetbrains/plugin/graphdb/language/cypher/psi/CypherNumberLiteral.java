// This is a generated file. Not intended for manual editing.
package com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi;

import com.vesoft.jetbrains.plugin.graphdb.language.cypher.references.types.CypherTypePropagator;
import org.jetbrains.annotations.*;

public interface CypherNumberLiteral extends CypherTypePropagator {

  @Nullable
  CypherDoubleLiteral getDoubleLiteral();

  @Nullable
  CypherIntegerLiteral getIntegerLiteral();

}
