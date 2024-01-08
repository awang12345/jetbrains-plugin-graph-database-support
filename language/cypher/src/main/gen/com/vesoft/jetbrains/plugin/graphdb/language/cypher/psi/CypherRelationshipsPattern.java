// This is a generated file. Not intended for manual editing.
package com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi;

import java.util.List;

import com.vesoft.jetbrains.plugin.graphdb.language.cypher.references.types.CypherPathYielding;
import org.jetbrains.annotations.*;

public interface CypherRelationshipsPattern extends CypherPathYielding {

  @NotNull
  CypherNodePattern getNodePattern();

  @NotNull
  List<CypherPatternElementChain> getPatternElementChainList();

}
