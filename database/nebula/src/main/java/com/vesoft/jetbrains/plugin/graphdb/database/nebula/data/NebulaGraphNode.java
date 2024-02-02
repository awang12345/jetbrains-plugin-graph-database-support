package com.vesoft.jetbrains.plugin.graphdb.database.nebula.data;

import com.vesoft.jetbrains.plugin.graphdb.database.api.data.GraphNode;
import com.vesoft.jetbrains.plugin.graphdb.database.api.data.GraphPropertyContainer;
import com.vesoft.jetbrains.plugin.graphdb.database.nebula.query.NebulaValueToString;
import com.vesoft.nebula.Tag;
import com.vesoft.nebula.Value;
import com.vesoft.nebula.Vertex;
import com.vesoft.nebula.client.graph.data.Node;
import com.vesoft.nebula.client.graph.data.ValueWrapper;
import org.apache.commons.lang.StringUtils;

import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * 业务说明：
 *
 * @Author jiangyiwang-jk
 * @Date 2023/12/29 15:56
 */
public class NebulaGraphNode implements GraphNode {

    private String id;
    private String name;
    private Map<String, Object> properties;

    public NebulaGraphNode(String id) {
        this.id = id;
        this.name = id;
    }

    public NebulaGraphNode(String id, String name, Map<String, Object> properties) {
        this.id = id;
        this.name = name;
        this.properties = properties;
    }

    public NebulaGraphNode(Node node) {
        this.id = NebulaValueToString.valueToString(node.getId().getValue());
        if (!node.tagNames().isEmpty()) {
            this.name = node.tagNames().get(0);
            try {
                HashMap<String, ValueWrapper> prop = node.properties(this.name);
                if (prop != null) {
                    this.properties = new HashMap<>();
                    for (Map.Entry<String, ValueWrapper> entry : prop.entrySet()) {
                        String key = entry.getKey();
                        String value = NebulaValueToString.valueToString(entry.getValue().getValue());
                        this.properties.put(key, value);
                    }
                }
            } catch (UnsupportedEncodingException e) {
                ;
            }
        }
    }

    public NebulaGraphNode(Vertex vertex) {
        this.id = NebulaValueToString.valueToString(vertex.getVid());
        if (!vertex.getTags().isEmpty()) {
            Tag tag = vertex.getTags().get(0);
            this.name = StringUtils.defaultIfBlank(new String(tag.getName()), this.id);
            this.properties = new HashMap<>();
            if (tag.getProps() != null) {
                for (Map.Entry<byte[], Value> entry : tag.getProps().entrySet()) {
                    String key = new String(entry.getKey());
                    String value = NebulaValueToString.valueToString(entry.getValue());
                    this.properties.put(key, value);
                }
            }
        }
    }


    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public GraphPropertyContainer getPropertyContainer() {
        return () -> Optional.ofNullable(this.properties).orElse(Collections.emptyMap());
    }

    @Override
    public List<String> getTypes() {
        return Arrays.asList(this.name);
    }

    @Override
    public String getTypesName() {
        return this.name;
    }

    @Override
    public boolean isTypesSingle() {
        return false;
    }

    @Override
    public String getRepresentation() {
        return StringUtils.defaultIfBlank(this.name, this.id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NebulaGraphNode that = (NebulaGraphNode) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
