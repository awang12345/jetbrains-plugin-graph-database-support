package com.vesoft.jetbrains.plugin.graphdb.database.nebula.query;

import com.vesoft.nebula.*;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 业务说明：
 *
 * @Author jiangyiwang-jk
 * @Date 2024/1/9 11:19
 */
public class NebulaValueToString {

    public static final String NULL_VALUE = "[NULL]";

    public static String valueToString(Value value) {
        return getValueString(value);
    }

    private static String getValueString(Value value) {
        if (value == null || value.getFieldValue() == null) {
            return NULL_VALUE;
        }
        int setField = value.getSetField();
        switch (setField) {
            case Value.SVAL:
                return new String(value.getSVal(), StandardCharsets.UTF_8);
            case Value.EVAL:
                return edgeToString(value.getEVal());
            case Value.LVAL:
                return listToString(value.getLVal());
            case Value.DVAL:
                Date dVal = value.getDVal();
                return String.format("%d-%02d-%02d", dVal.getYear(), dVal.getMonth(), dVal.getDay());
            case Value.TVAL:
                Time tVal = value.getTVal();
                return String.format("%02d:%02d:%02d", tVal.getHour(), tVal.getMinute(), tVal.getSec());
            case Value.DTVAL:
                DateTime dtVal = value.getDtVal();
                return String.format("%d-%02d-%02d %02d:%02d:%02d", dtVal.getYear(), dtVal.getMonth(), dtVal.getDay(), dtVal.getHour(), dtVal.getMinute(), dtVal.getSec());
            case Value.VVAL:
                return vertexToString(value.getVVal());
            case Value.MVAL:
                return mapToString(value.getMVal());
            default:
                return String.valueOf(value.getFieldValue());
        }
    }

    private static String vertexToString(Vertex vertex) {
        String vid = getVidString(vertex.getVid());
        return "{" + vid + ": [" + vertex.getTags().stream().map(NebulaValueToString::tagToString).collect(Collectors.joining(",")) + "]}";
    }

    private static String tagToString(Tag tag) {
        String tagName = new String(tag.getName());
        StringBuilder sb = new StringBuilder();
        sb.append("{").append(tagName).append(":");
        propToString(sb, tag.getProps());
        sb.append("}");
        return sb.toString();
    }

    private static String edgeToString(Edge edge) {
        //[:like 2->1 @0 {pname: "2-1"}]
        String edgeName = new String(edge.getName());
        String srcId = getVidString(edge.getSrc());
        String dstId = getVidString(edge.getDst());
        long ranking = edge.getRanking();
        StringBuilder builder = new StringBuilder();
        builder.append("[:").append(edgeName).append(" ");
        boolean isReversely = edge.getType() == -1;
        if (isReversely) {
            //反向
            builder.append(dstId).append("->").append(srcId);
        } else {
            //正向
            builder.append(srcId).append("->").append(dstId);
        }
        builder.append(" @").append(ranking);
        Map<byte[], Value> props = edge.getProps();
        propToString(builder, props);
        return builder.append("]").toString();
    }

    public static String getVidString(Value value) {
        if (value.getSetField() == Value.SVAL) {
            return "\"" + new String(value.getSVal()) + "\"";
        }
        return String.valueOf(value.getFieldValue());
    }

    private static void propToString(StringBuilder builder, Map<byte[], Value> props) {
        builder.append(" {");
        if (props != null) {
            for (Map.Entry<byte[], Value> entry : props.entrySet()) {
                String key = new String(entry.getKey());
                String value = getValueString(entry.getValue());
                builder.append(key).append(": ").append(value).append(", ");
            }
            if (!props.entrySet().isEmpty()) {
                builder.delete(builder.length() - 2, builder.length());
            }
        }
        builder.append("} ");
    }

    private static String listToString(NList nList) {
        if (nList == null) {
            return NULL_VALUE;
        }
        return nList.values.stream().map(NebulaValueToString::getValueString).collect(Collectors.joining(","));
    }

    private static String mapToString(NMap nMap) {
        if (nMap == null) {
            return NULL_VALUE;
        }
        if (nMap.getKvs().isEmpty()) {
            return "{}";
        }
        StringBuilder builder = new StringBuilder("{");
        for (Map.Entry<byte[], Value> entry : nMap.getKvs().entrySet()) {
            builder.append(new String(entry.getKey())).append(": ").append(getMapValue(entry.getValue())).append(",");
        }
        builder.delete(builder.length() - 1, builder.length());
        builder.append("}");
        return builder.toString();
    }

    public static String getMapValue(Value value) {
        String valueString = getValueString(value);
        if (value.getSetField() == Value.IVAL
                || value.getSetField() == Value.FVAL
                || value.getSetField() == Value.DVAL
                || value.getSetField() == Value.NVAL) {
            return valueString;
        }
        return "\"" + valueString + "\"";
    }

//    private static String pathToString(Path path) {
//        if (path == null) {
//            return "[NULL]";
//        }
//        List<Step> steps = path.getSteps();
//        List<String> stepStrings = new ArrayList<>(steps.size());
//        if (steps != null) {
//            for (Step step : steps) {
//                StringBuilder builder = new StringBuilder("<");
//                builder.append("(").append(vertexToString(path.getSrc())).append(")");
//                if (step.getType()==-1) {
//                    builder.append("<-").append(step.getRanking()).append(">");
//                } else {
//                    builder.append("->").append(step.getType()).append(">");
//                }
//            }
//        }
//    }

}
