package com.neueda.jetbrains.plugin.graphdb.jetbrains.util;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.highlighter.EditorHighlighterFactory;
import com.intellij.openapi.editor.impl.EditorImpl;
import com.intellij.openapi.project.Project;
import com.intellij.testFramework.LightVirtualFile;

import java.awt.*;

/**
 * @author jiangyiwang
 */
public class EditorComponentUtils {
    public static final Color BACKGROUND;

    static {
        EditorFactory editorFactory = EditorFactory.getInstance();
        Document document = editorFactory.createDocument("");
        document.setReadOnly(false);
        EditorImpl editor = (EditorImpl) editorFactory.createEditor(document);

        BACKGROUND = editor.getBackgroundColor();
    }

    public static Editor createEditorPanel(Project project, LightVirtualType virtualType) {
        EditorFactory editorFactory = EditorFactory.getInstance();
        Document document = editorFactory.createDocument("");
        document.setReadOnly(false);
        EditorImpl editor = (EditorImpl) editorFactory.createEditor(document);
        // 初始默认设置
        EditorSettingsInit.init(editor);
        // 添加监控事件
        editor.getDocument().setReadOnly(false);

        EditorHighlighterFactory highlighterFactory = EditorHighlighterFactory.getInstance();
        editor.setHighlighter(highlighterFactory.createEditorHighlighter(project, new LightVirtualFile(virtualType.value)));

        return editor;
    }

    public static void write(Project project, Editor editor, String text, boolean readOnly) {
        WriteCommandAction.runWriteCommandAction(project, () -> {
            editor.getDocument().setReadOnly(false);
            editor.getDocument().setText(text);
            editor.getDocument().setReadOnly(readOnly);
        });
    }

    public static void write(Project project, Editor editor, String text) {
        write(project, editor, text, true);
    }

    public enum LightVirtualType {
        JAVA(".java"),
        XML(".xml"),
        FTL(".ftl"),
        JSON(".json"),
        VM(".java.vm"),
        MARKDOWN(".md"),
        SQL(".sql"),
        ;

        private final String value;

        LightVirtualType(String value) {
            this.value = value;
        }
    }

    private static final String JAVA_SUFFIX = ".java";
    private static final String XML_SUFFIX = ".xml";
    private static final String FTL_SUFFIX = ".ftl";
    private static final String JSON_SUFFIX = ".json";
    private static final String VM_SUFFIX = ".java.vm";
    private static final String MARKDOWN_SUFFIX = ".md";
}
