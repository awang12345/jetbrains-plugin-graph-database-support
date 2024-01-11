package com.vesoft.jetbrains.plugin.graphdb.jetbrains.ui.datasource.metadata;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorSettings;
import com.intellij.openapi.editor.impl.softwrap.SoftWrapAppliancePlaces;
import com.intellij.openapi.project.Project;
import com.vesoft.jetbrains.plugin.graphdb.database.nebula.data.NebulaEdge;
import com.vesoft.jetbrains.plugin.graphdb.database.nebula.data.NebulaSchema;
import com.vesoft.jetbrains.plugin.graphdb.database.nebula.data.NebulaSpace;
import com.vesoft.jetbrains.plugin.graphdb.database.nebula.data.NebulaTag;
import com.vesoft.jetbrains.plugin.graphdb.jetbrains.util.EditorComponentUtils;
import com.vesoft.jetbrains.plugin.graphdb.jetbrains.util.JPanelUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ShowNebulaDDLDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;

    private final Project project;

    private final NebulaSchema data;

    public ShowNebulaDDLDialog(Project project, NebulaSchema data) {
        this.data = data;
        this.project = project;

        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        initDDLPanel();

        initSize();
        this.setLocationRelativeTo(null);

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
        JPanelUtils.setSize(this, new Dimension(800, 600));
    }

    private void initDDLPanel() {
        contentPane.setLayout(new BorderLayout());
        Editor editor = EditorComponentUtils.createEditorPanel(project, EditorComponentUtils.LightVirtualType.SQL);
        editor.getSettings().setUseSoftWraps(true);
        JComponent component = editor.getComponent();
        component.setSize(800, 600);
        contentPane.add(component, BorderLayout.CENTER);


        String ddl = data.getDdl();
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
