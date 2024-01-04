package com.neueda.jetbrains.plugin.graphdb.jetbrains.ui.datasource.metadata;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.neueda.jetbrains.plugin.graphdb.database.nebula.data.NebulaEdge;
import com.neueda.jetbrains.plugin.graphdb.database.nebula.data.NebulaSpace;
import com.neueda.jetbrains.plugin.graphdb.database.nebula.data.NebulaTag;
import com.neueda.jetbrains.plugin.graphdb.jetbrains.context.DataContext;
import com.neueda.jetbrains.plugin.graphdb.jetbrains.util.EditorComponentUtils;
import com.neueda.jetbrains.plugin.graphdb.jetbrains.util.JPanelUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ShowNebulaDDLDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;

    private final Project project;

    private final Object data;

    public ShowNebulaDDLDialog(Project project, Object data) {
        this.data = data;
        this.project = project;

        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        initDDLPanel();

        initSize();

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

    }

    private void initSize() {
        JPanelUtils.setSize(this, new Dimension(1000, 600));
    }

    private void initDDLPanel() {
        contentPane.setLayout(new BorderLayout());
        Editor editor = EditorComponentUtils.createEditorPanel(project, EditorComponentUtils.LightVirtualType.SQL);
        JComponent component = editor.getComponent();
        component.setSize(1000, 600);
        contentPane.add(component, BorderLayout.CENTER);

        String ddl = null;
        if (data instanceof NebulaSpace) {
            ddl = ((NebulaSpace) data).getDdl();
            contentPane.setName(((NebulaSpace) data).getSpaceName());
        } else if (data instanceof NebulaTag) {
            ddl = ((NebulaTag) data).getDdl();
            contentPane.setName(((NebulaTag) data).getTagName());
        } else if (data instanceof NebulaEdge) {
            ddl = ((NebulaEdge) data).getDdl();
            contentPane.setName(((NebulaEdge) data).getTagName());
        }
        final String scheme = ddl;
        WriteCommandAction.runWriteCommandAction(project, () -> {
            editor.getDocument().setText(scheme);
            editor.getDocument().setReadOnly(true);
        });
    }

    private void onOK() {
        // add your code here
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

}
