package com.neueda.jetbrains.plugin.graphdb.jetbrains.ui.console.plan;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.util.ProcessingContext;
import com.neueda.jetbrains.plugin.graphdb.database.nebula.data.NebulaEdge;
import com.neueda.jetbrains.plugin.graphdb.database.nebula.data.NebulaSpace;
import com.neueda.jetbrains.plugin.graphdb.database.nebula.data.NebulaTag;
import com.neueda.jetbrains.plugin.graphdb.jetbrains.component.datasource.metadata.DataSourceMetadata;
import com.neueda.jetbrains.plugin.graphdb.jetbrains.component.datasource.metadata.NebulaDataSourceMetadata;
import com.neueda.jetbrains.plugin.graphdb.jetbrains.context.DataContext;
import com.neueda.jetbrains.plugin.graphdb.jetbrains.ui.console.event.CommonConsoleLogEvent;
import com.neueda.jetbrains.plugin.graphdb.language.ngql.NGqlLanguage;
import com.neueda.jetbrains.plugin.graphdb.language.ngql.psi.NGqlTypes;
import icons.GraphIcons;
import org.jetbrains.annotations.NotNull;

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
        customFillCompletionVariants(parameters, result);
        super.fillCompletionVariants(parameters, result);
    }

    private void customFillCompletionVariants(CompletionParameters parameters, CompletionResultSet result) {
        addSpaceTagEdgePropKeyword(parameters, result);

        String[] functionArray = functions.split("\\|");
        for (String function : functionArray) {
            result.addElement(LookupElementBuilder.create(function)
                    .withPresentableText(function)
                    .withIcon(GraphIcons.Nodes.FUNCTION)
                    .withTypeText("Function")
                    .bold());
        }

        String[] keywordArray = keywords.split("\\|");
        for (String keyword : keywordArray) {
            result.addElement(LookupElementBuilder.create(keyword)
                    .withPresentableText(keyword)
                    .withIcon(GraphIcons.Nodes.KEY_WORD)
                    .withTypeText("Keyword")
                    .bold());
        }
    }

    private void addSpaceTagEdgePropKeyword(CompletionParameters parameters, CompletionResultSet result) {
        String fileName = parameters.getOriginalFile().getVirtualFile().getName();
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
            result.addElement(LookupElementBuilder
                    .create(nebulaSpace.getSpaceName())
                    .withPresentableText(nebulaSpace.getSpaceName())
                    .withIcon(GraphIcons.Nodes.NEBULA_SPACE)
                    .withTypeText("Space")
                    .bold());

            List<NebulaTag> tagList = nebulaSpace.getTagList();
            if (tagList != null) {
                for (NebulaTag nebulaTag : tagList) {
                    result.addElement(LookupElementBuilder
                            .create(nebulaTag.getTagName())
                            .withPresentableText(nebulaTag.getTagName())
                            .withIcon(GraphIcons.Nodes.NEBULA_TAG)
                            .withTypeText("Tag")
                            .bold());

                    if (nebulaTag.getProperties() != null) {
                        for (String prop : nebulaTag.getProperties().keySet()) {
                            result.addElement(LookupElementBuilder
                                    .create(prop)
                                    .withPresentableText(prop)
                                    .withIcon(GraphIcons.Nodes.NEBULA_FIELD)
                                    .withTypeText("Field")
                                    .bold());
                        }
                    }
                }
            }

            List<NebulaEdge> edgeList = nebulaSpace.getEdgeList();
            if (edgeList != null) {
                for (NebulaEdge nebulaEdge : edgeList) {
                    result.addElement(LookupElementBuilder
                            .create(nebulaEdge.getTagName())
                            .withPresentableText(nebulaEdge.getTagName())
                            .withIcon(GraphIcons.Nodes.NEBULA_EDGE)
                            .withTypeText("Tag")
                            .bold());

                    if (nebulaEdge.getProperties() != null) {
                        for (String prop : nebulaEdge.getProperties().keySet()) {
                            result.addElement(LookupElementBuilder
                                    .create(prop)
                                    .withPresentableText(prop)
                                    .withIcon(GraphIcons.Nodes.NEBULA_FIELD)
                                    .withTypeText("Field")
                                    .bold());
                        }
                    }
                }
            }
        }
    }

}
