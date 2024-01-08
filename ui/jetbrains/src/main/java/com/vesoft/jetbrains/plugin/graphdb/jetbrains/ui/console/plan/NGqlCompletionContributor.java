package com.vesoft.jetbrains.plugin.graphdb.jetbrains.ui.console.plan;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.vesoft.jetbrains.plugin.graphdb.database.nebula.data.NebulaEdge;
import com.vesoft.jetbrains.plugin.graphdb.database.nebula.data.NebulaSpace;
import com.vesoft.jetbrains.plugin.graphdb.database.nebula.data.NebulaTag;
import com.vesoft.jetbrains.plugin.graphdb.jetbrains.component.datasource.metadata.DataSourceMetadata;
import com.vesoft.jetbrains.plugin.graphdb.jetbrains.component.datasource.metadata.NebulaDataSourceMetadata;
import com.vesoft.jetbrains.plugin.graphdb.jetbrains.context.DataContext;
import com.vesoft.jetbrains.plugin.graphdb.language.ngql.NGqlFileType;
import com.vesoft.jetbrains.plugin.graphdb.platform.GraphConstants;
import icons.GraphIcons;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.List;

/**
 * 业务说明：
 *
 * @Author jiangyiwang-jk
 * @Date 2024/1/4 11:18
 */
public class NGqlCompletionContributor extends CompletionContributor {

    final String functions = "abs|floor|ceil|round|sqrt|cbrt|hypot|pow|exp|exp2|log|log2|log10\n" +
            "         |sin|asin|cos|acos|tan|atan|rand|rand32|rand64|collect|avg|count\n" +
            "         |max|min|std|sum|bit_and|bit_or|bit_xor|size|range|sign|e|pi|radians\n" +
            "         |strcasecmp|lower|toLower|upper|toUpper|length|trim|ltrim|rtrim|left\n" +
            "         |right|lpad|rpad|substr|substring|reverse|replace|split|toString|hash\n" +
            "         |now|date|time|datetime|id|tags|labels|properties|type|startNode|endNode\n" +
            "         |rank|keys|labels|nodes|range|relationships|reverse|tail|head|last|coalesce|reduce";


    final String keywords = "GO|FIND|LOOKUP|FETCH|MATCH|SHOW|UNWIND|AS|TO\n" +
            "            |USE|SET|FROM|WHERE|INSERT|YIELD|RETURN|DESCRIBE\n" +
            "            |DESC|VERTEX|EDGE|EDGES|UPDATE|UPSERT|WHEN|DELETE\n" +
            "            |ALTER|STEPS|STEP|OVER|UPTO|REVERSELY|INDEX|INDEXES\n" +
            "            |REBUILDTAG|TAGS|UNION|INTERSECT|MINUS|NO|OVERWRITE\n" +
            "            |ADD|CREATE|DROP|REMOVE|NOT|EXISTS|WITH|CHANGE|GRANT\n" +
            "            |REVOKE|ON|BY|IN|DOWNLOAD|GET|OF|ORDER|INGEST|COMPACT\n" +
            "            |FLUSH|SUBMIT|ASC|ASCENDING|DESCENDING|DISTINCT|PROP\n" +
            "            |BALANCE|STOP|LIMIT|OFFSET|IS|RECOVER|FORMAT|CASE\n" +
            "            |EXPLAIN|PROFILE|IF|THEN|ELSE|END|_id|_type|_src|_dst|_rank\n" +
            "            |HOST|HOSTS|SPACE|SPACES|VALUE|VALUES|USER|USERS|PASSWORD\n" +
            "            |ROLE|ROLES|GOD|ADMIN|DBA|GUEST|GROUP|PARTITION|NUM|REPLICA\n" +
            "            |FACTOR|VID|TYPE|CHARSET|COLLATE|COLLATION|ATOMIC|ALL|ANY\n" +
            "            |SINGLE|NONE|REDUCE|LEADER|UUID|DATA|SNAPSHOT|SNAPSHOTS|ACCOUNT\n" +
            "            |JOBS|JOB|PATH|BIDIRECT|STATS|STATUS|FORCE|PART|PARTS|DEFAULT\n" +
            "            |HDFS|CONFIGS|TTL|DURATION|COL|GRAPH|META|STORAGE|SHORTEST|NOLOOP\n" +
            "            |OUT|BOTH|SUBGRAPH|CONTAINS|STARTS|ENDS|EMPTY|SKIP|OPTIONAL|GROUPS\n" +
            "            |ZONE|ZONES|INTO|LISTENER|ELASTICSEARCH|AUTO|FUZZY|PREFIX|REGEXP\n" +
            "            |WILDCARD|TEXT|SEARCH|CLIENTS|SIGN|SERVICE|RESET|PLAN|TAG";

    @Override
    public void fillCompletionVariants(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet result) {
        String fileName = parameters.getOriginalFile().getVirtualFile().getName();
        if (!fileName.startsWith(GraphConstants.BOUND_DATA_SOURCE_PREFIX) || !fileName.endsWith(NGqlFileType.FILE_EXT)) {
            return;
        }
        customFillCompletionVariants(parameters, result);
        super.fillCompletionVariants(parameters, result);
    }

    private void customFillCompletionVariants(CompletionParameters parameters, CompletionResultSet result) {
        addSpaceTagEdgePropKeyword(parameters, result);
        addFunctionAndKeyword(result);
    }

    private void addFunctionAndKeyword(CompletionResultSet result) {
        String[] functionArray = functions.split("[\\|\n]+");
        for (String function : functionArray) {
            addElement(result, function, GraphIcons.Nodes.FUNCTION, "Function");
        }

        String[] keywordArray = keywords.split("[\\|\n]+");
        for (String keyword : keywordArray) {
            addElement(result, keyword, GraphIcons.Nodes.KEY_WORD, "Keyword");
        }
    }

    private void addSpaceTagEdgePropKeyword(CompletionParameters parameters, CompletionResultSet result) {
        String fileName = parameters.getOriginalFile().getVirtualFile().getName();

        if (!fileName.startsWith(GraphConstants.BOUND_DATA_SOURCE_PREFIX)) {
            return;
        }

        Project project = parameters.getOriginalFile().getProject();

        DataSourceMetadata metadata = DataContext.getInstance(project).getMetadataByFileName(fileName);
        if (!(metadata instanceof NebulaDataSourceMetadata)) {
            return;
        }
        List<NebulaSpace> nebulaSpaceList = ((NebulaDataSourceMetadata) metadata).getNebulaGraphMetadata().getNebulaSpaceList();
        if (nebulaSpaceList == null || nebulaSpaceList.isEmpty()) {
            return;
        }

        for (NebulaSpace nebulaSpace : nebulaSpaceList) {
            addElement(result, nebulaSpace.getSpaceName(), GraphIcons.Nodes.NEBULA_SPACE, "Space");

            List<NebulaTag> tagList = nebulaSpace.getTagList();
            if (tagList != null) {
                for (NebulaTag nebulaTag : tagList) {
                    addElement(result, nebulaTag.getTagName(), GraphIcons.Nodes.NEBULA_TAG, "Tag");

                    if (nebulaTag.getProperties() != null) {
                        for (String prop : nebulaTag.getProperties().keySet()) {
                            addElement(result, prop, GraphIcons.Nodes.NEBULA_FIELD, "Field");
                        }
                    }
                }
            }

            List<NebulaEdge> edgeList = nebulaSpace.getEdgeList();
            if (edgeList != null) {
                for (NebulaEdge nebulaEdge : edgeList) {
                    addElement(result, nebulaEdge.getTagName(), GraphIcons.Nodes.NEBULA_EDGE, "Tag");

                    if (nebulaEdge.getProperties() != null) {
                        for (String prop : nebulaEdge.getProperties().keySet()) {
                            addElement(result, prop, GraphIcons.Nodes.NEBULA_FIELD, "Field");
                        }
                    }
                }
            }
        }
    }

    private static void addElement(CompletionResultSet result, String prop, Icon nebulaField, String Field) {
        result.addElement(LookupElementBuilder
                .create(prop)
                .withPresentableText(prop)
                .withIcon(nebulaField)
                .withTypeText(Field)
                .withCaseSensitivity(false)
                .bold());

    }

}
