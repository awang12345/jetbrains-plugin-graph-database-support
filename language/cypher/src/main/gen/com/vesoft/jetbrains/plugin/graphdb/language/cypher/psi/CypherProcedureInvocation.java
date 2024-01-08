// This is a generated file. Not intended for manual editing.
package com.vesoft.jetbrains.plugin.graphdb.language.cypher.psi;

import com.vesoft.jetbrains.plugin.graphdb.language.cypher.references.CypherInvocation;
import org.jetbrains.annotations.*;

public interface CypherProcedureInvocation extends CypherInvocation {

  @NotNull
  CypherProcedureArguments getProcedureArguments();

  @NotNull
  CypherProcedureInvocationBody getProcedureInvocationBody();

  String getFullName();

}
