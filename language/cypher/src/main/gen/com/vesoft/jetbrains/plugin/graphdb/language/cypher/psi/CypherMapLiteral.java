// This is a generated file. Not intended for manual editing.
package com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi;

import java.util.List;

import com.vesoft.jetbrains.plugin.graphdb.language.cypher.references.types.CypherMapYielding;
import org.jetbrains.annotations.*;

public interface CypherMapLiteral extends CypherMapYielding {

  @NotNull
  List<CypherExpression> getExpressionList();

  @NotNull
  List<CypherPropertyKeyName> getPropertyKeyNameList();

}
