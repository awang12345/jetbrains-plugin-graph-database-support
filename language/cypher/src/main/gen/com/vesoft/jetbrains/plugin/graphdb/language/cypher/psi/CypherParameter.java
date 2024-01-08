// This is a generated file. Not intended for manual editing.
package com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi;

import com.vesoft.jetbrains.plugin.graphdb.language.cypher.references.types.CypherAnyYielding;
import org.jetbrains.annotations.*;

public interface CypherParameter extends CypherAnyYielding {

  @Nullable
  CypherNewParameter getNewParameter();

  @Nullable
  CypherOldParameter getOldParameter();

  String getParameterName();

}
