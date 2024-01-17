package com.vesoft.jetbrains.plugin.graphdb.database.opencypher.gremlin;

import com.vesoft.jetbrains.plugin.graphdb.database.api.GraphDatabaseApi;
import com.vesoft.jetbrains.plugin.graphdb.database.api.data.GraphMetadata;
import com.vesoft.jetbrains.plugin.graphdb.database.api.data.GraphNode;
import com.vesoft.jetbrains.plugin.graphdb.database.api.data.GraphRelationship;
import com.vesoft.jetbrains.plugin.graphdb.database.api.query.GraphQueryResult;
import com.vesoft.jetbrains.plugin.graphdb.database.api.query.GraphQueryResultColumn;
import com.vesoft.jetbrains.plugin.graphdb.database.api.query.GraphQueryResultRow;
import com.vesoft.jetbrains.plugin.graphdb.database.neo4j.bolt.data.Neo4jBoltQueryResultColumn;
import com.vesoft.jetbrains.plugin.graphdb.database.neo4j.bolt.data.Neo4jBoltQueryResultRow;
import com.vesoft.jetbrains.plugin.graphdb.database.opencypher.gremlin.exceptions.OpenCypherGremlinException;
import com.vesoft.jetbrains.plugin.graphdb.database.opencypher.gremlin.query.OpenCypherGremlinQueryResult;
import com.vesoft.jetbrains.plugin.graphdb.database.opencypher.gremlin.exceptions.ExceptionWrapper;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.tinkerpop.gremlin.driver.Client;
import org.apache.tinkerpop.gremlin.driver.Cluster;
import org.apache.tinkerpop.gremlin.driver.Result;
import org.opencypher.gremlin.client.CypherGremlinClient;

import java.net.URI;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static java.util.Collections.*;
import static java.util.stream.Collectors.toList;

/**
 * Communicates with TinkerPop database by translating Cypher to Gremlin
 */
public class OpenCypherGremlinDatabase implements GraphDatabaseApi {
    private final OpenCypherGremlinValueConverter converter;
    private final Cluster cluster;
    private final GremlinFlavor flavor;

    static {
        disableGremlinLog();
    }

    public OpenCypherGremlinDatabase(Map<String, String> configuration) {
        this(new OpenCypherGremlinConfiguration(configuration));
    }

    public OpenCypherGremlinDatabase(OpenCypherGremlinConfiguration configuration) {
        String host = configuration.getHost();
        Integer port = configuration.getPort();
        String username = configuration.getUser();
        String password = configuration.getPassword();

        String url = String.format("gremlin://%s:%s", host, port);
        URI uri = URI.create(url);

        cluster = Cluster.build()
                .addContactPoint(uri.getHost())
                .credentials(username, password)
                .serializer(configuration.getSerializer())
                .enableSsl(configuration.getSSL())
                .port(uri.getPort())
                .create();

        flavor = configuration.getFlavor();

        converter = new OpenCypherGremlinValueConverter();
    }

    @Override
    public GraphQueryResult execute(String query) {
        return execute(query, Collections.emptyMap());
    }

    @Override
    public GraphQueryResult execute(String query, Map<String, Object> statementParameters) {
        Client gremlinClient = cluster.connect();

        try (CypherGremlinClient client = flavor.client(gremlinClient)) {
            if (query.toUpperCase().startsWith("PROFILE")) {
                return notSupported();
            }

            HashMap<String, Object> serializableMap = new HashMap<>(statementParameters);
            long startTime = System.currentTimeMillis();
            List<Map<String, Object>> result = client.submit(query, serializableMap).all();
            long endTime = System.currentTimeMillis();

            List<GraphQueryResultColumn> headers = getHeaders(result);

            List<GraphQueryResultRow> rows = result.stream()
                    .map(converter::toRecord)
                    .collect(toList());

            List<GraphNode> nodes = rows.stream().flatMap(e -> e.getNodes().stream()).distinct().collect(toList());
            List<GraphRelationship> relationships = rows.stream().flatMap(e -> e.getRelationships().stream()).distinct().collect(toList());

            return new OpenCypherGremlinQueryResult(endTime - startTime,
                    headers,
                    rows,
                    nodes,
                    relationships);
        } catch (RuntimeException e) {
            if (query.toUpperCase().startsWith("EXPLAIN")) {
                return new OpenCypherGremlinQueryResult(0, emptyList(), emptyList(), emptyList(), emptyList());
            } else {
                String exceptionMessage = ExceptionWrapper.wrapExceptionInMeaningMessage(e);
                throw new OpenCypherGremlinException(exceptionMessage, e);
            }
        }
    }

    private List<GraphQueryResultColumn> getHeaders(List<Map<String, Object>> result) {
        if (result.isEmpty()) {
            return emptyList();
        } else {
            return result.get(0).keySet().stream()
                    .map(Neo4jBoltQueryResultColumn::new)
                    .collect(toList());
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public GraphMetadata metadata(Consumer<String> progressDetailDisplay, Consumer<Float> progressPercentageDisplay) {
        Client gremlinClient = cluster.connect();
        try {
            Optional.ofNullable(progressDetailDisplay).ifPresent(a -> a.accept("gremlin execute : g.V().label().groupCount()"));
            List<Result> labels = gremlinClient.submit("g.V().label().groupCount()").all().get();
            Map<String, Number> labelResult = labels.stream()
                    .map(r -> r.get(Map.class))
                    .map(r -> (Map<String, Number>) r)
                    .flatMap(m -> m.entrySet().stream())
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

            Optional.ofNullable(progressDetailDisplay).ifPresent(a -> a.accept("gremlin execute : g.E().label().groupCount()"));
            List<Result> rels = gremlinClient.submit("g.E().label().groupCount()").all().get();
            Map<String, Number> relResult = rels.stream()
                    .map(r -> r.get(Map.class))
                    .map(r -> (Map<String, Number>) r)
                    .flatMap(m -> m.entrySet().stream())
                    .collect((Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));

            Optional.ofNullable(progressDetailDisplay).ifPresent(a -> a.accept("gremlin execute : g.V().properties().key().dedup()"));
            List<Result> vertexProp = gremlinClient.submit("g.V().properties().key().dedup()").all().get();

            List<String> vertexPropResult = vertexProp.stream()
                    .map(Result::getString)
                    .collect(toList());

            Optional.ofNullable(progressDetailDisplay).ifPresent(a -> a.accept("gremlin execute : g.E().properties().key().dedup()"));
            List<Result> edgeProp = gremlinClient.submit("g.E().properties().key().dedup()").all().get();

            List<String> edgePropResult = edgeProp.stream()
                    .map(Result::getString)
                    .collect(toList());

            return new OpenCypherGremlinGraphMetadata(labelResult, relResult, vertexPropResult, edgePropResult);
        } catch (InterruptedException | ExecutionException e) {
            String exceptionMessage = ExceptionWrapper.wrapExceptionInMeaningMessage(e);
            throw new OpenCypherGremlinException(exceptionMessage, e);
        } finally {
            gremlinClient.close();
        }
    }

    private GraphQueryResult notSupported() {
        String resultColumn = "result";
        return new OpenCypherGremlinQueryResult(
                0,
                singletonList(new Neo4jBoltQueryResultColumn(resultColumn)),
                singletonList(new Neo4jBoltQueryResultRow(singletonMap(resultColumn, "Currently no supported."))),
                emptyList(),
                emptyList()
        );
    }

    @SuppressWarnings("unchecked")
    private static void disableGremlinLog() {
        final String gremlinDriver = "org.apache.tinkerpop.gremlin.driver";

        Properties props = new Properties();
        props.setProperty("log4j.logger." + gremlinDriver, Level.OFF.toString());
        PropertyConfigurator.configure(props);

        Enumeration<Logger> loggers = LogManager.getCurrentLoggers();
        Collections.list(loggers)
                .stream()
                .filter(logger -> logger.getName().startsWith(gremlinDriver))
                .forEach(logger -> logger.setLevel(Level.OFF));
    }
}
