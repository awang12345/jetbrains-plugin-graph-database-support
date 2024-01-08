// This is a generated file. Not intended for manual editing.
package com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi;

import java.util.List;

import com.vesoft.jetbrains.plugin.graphdb.language.cypher.references.types.CypherMapYielding;
import org.jetbrains.annotations.*;

public interface CypherMapProjection extends CypherMapYielding {

  @NotNull
  List<CypherMapProjectionVariants> getMapProjectionVariantsList();

  @NotNull
  CypherVariable getVariable();

}
