package icons;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.util.ScalableIcon;

import javax.swing.Icon;

import static com.intellij.openapi.util.IconLoader.getIcon;

public final class GraphIcons extends AllIcons {
    public static final class Database {
        public static final Icon UNKNOWN = AllIcons.FileTypes.Unknown;
        public static final Icon NEO4J = getIcon("/graphdb/icons/database/neo4j.svg");
        public static final Icon OPENCYPHER = getIcon("/graphdb/icons/language/cypher.svg");
        public static final Icon NEBULA = getIcon("/graphdb/icons/database/nebula-graph.svg");
    }

    public static final class Language {
        public static final Icon CYPHER = getIcon("/graphdb/icons/language/cypher.svg");
    }

    public static final class Window {
        public static final Icon GRAPH = getIcon("/META-INF/pluginIcon.svg");
    }

    public static final class Nodes {
        public static final Icon INDEX = AllIcons.Nodes.ResourceBundle;
        public static final Icon CONSTRAINT = AllIcons.Nodes.C_protected;
        public static final Icon LABEL = AllIcons.Nodes.Class;
        public static final Icon RELATIONSHIP_TYPE = AllIcons.Vcs.Arrow_right;
        public static final Icon PROPERTY_KEY = AllIcons.Nodes.Property;
        public static final Icon VARIABLE = AllIcons.Nodes.Variable;
        public static final Icon FUNCTION = AllIcons.Nodes.Function;
        public static final Icon STORED_PROCEDURE = AllIcons.Nodes.Function;
        public static final Icon USER_FUNCTION = AllIcons.Nodes.Function;
        public static final Icon KEY_WORD = AllIcons.Nodes.Alias;
        public static final Icon DDL = getIcon("/graphdb/icons/nodes/ddl.svg");
        public static final Icon NEBULA_SPACE = getIcon("/graphdb/icons/nodes/space.svg");
        public static final Icon NEBULA_TAG = getIcon("/graphdb/icons/nodes/tag.svg");
        public static final Icon NEBULA_EDGE = getIcon("/graphdb/icons/nodes/edge.svg");
        public static final Icon NEBULA_FIELD = getIcon("/graphdb/icons/nodes/field.svg");
        public static final Icon NEBULA_CONSOLE = getIcon("/graphdb/icons/nodes/console.svg");
        public static final Icon NEBULA_FIELD_NUM = getIcon("/graphdb/icons/nodes/field_number.svg");
        public static final Icon NEBULA_FIELD_TIME = getIcon("/graphdb/icons/nodes/field_time.svg");
        public static final Icon NEBULA_FIELD_STR = getIcon("/graphdb/icons/nodes/field_str.svg");
        public static final Icon NEBULA_FIELD_BOOLEAN = getIcon("/graphdb/icons/nodes/field_boolean.svg");
    }

    private GraphIcons() {
    }

    public static Icon scaleToWidth(Icon icon, float newWidth) {
        if (icon instanceof ScalableIcon) {
            ScalableIcon scalableIcon = (ScalableIcon) icon;

            int iconWidth = scalableIcon.getIconWidth();
            if (newWidth != iconWidth) {
                return scalableIcon.scale(newWidth / iconWidth);
            }
        }
        return icon;
    }
}
