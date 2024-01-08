package com.vesoft.jetbrains.plugin.graphdb.jetbrains.util;

import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBCheckBox;
import org.apache.commons.lang3.ObjectUtils;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author jiangyiwang
 */
public class JPanelUtils {
    public static void setSize(Container panel, Dimension dimension) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = screenSize.width - 100;
        int screenHeight = screenSize.height - 100;

        int width = dimension.width;
        int height = dimension.height;
        if (width > screenWidth || height > screenHeight) {
            dimension = new Dimension(Math.min(screenWidth, width), Math.min(screenHeight, height));
        }

        panel.setSize(dimension);
        panel.setPreferredSize(dimension);
        panel.setMinimumSize(dimension);
        panel.setMaximumSize(dimension);
    }

    public static JPanel settingPanel(String label, Component valueComponent, Dimension labelSize, Dimension valueSize, boolean nextLine) {
        JPanel settingPanel = new JPanel(new BorderLayout());

        JLabel titleLabel = new JLabel();
        titleLabel.setText(label);
        if (Objects.nonNull(labelSize)) {
            titleLabel.setPreferredSize(labelSize);
        }

        if (nextLine) {
            settingPanel.add(titleLabel, BorderLayout.NORTH);
        } else {
            settingPanel.add(titleLabel, BorderLayout.WEST);
        }

        settingPanel.add(valueComponent, BorderLayout.CENTER);
        if (Objects.nonNull(valueSize)) {
            valueComponent.setPreferredSize(valueSize);
        }

        return settingPanel;
    }

    public static JPanel settingPanel(String label, Component valueComponent, Dimension labelSize, Dimension valueSize) {
        return settingPanel(label, valueComponent, labelSize, valueSize, false);
    }

    public static JPanel settingPanel(String label, Component valueComponent) {
        return settingPanel(label, valueComponent, null, null, false);
    }

    public static JPanel combinePanel(String label, Component westComponent, Component centerComponent, Dimension labelSize, Dimension valueSize) {
        return combinePanel(label, westComponent, centerComponent, null, labelSize, valueSize);
    }

    public static JPanel combinePanel(String label, Component westComponent, Component centerComponent, Component eastComponent) {
        return combinePanel(label, westComponent, centerComponent, eastComponent, null, null);
    }

    public static JPanel combinePanel(String label, Component westComponent, Component centerComponent, Component eastComponent, Dimension labelSize, Dimension valueSize) {
        JPanel combinePanel = new JPanel(new BorderLayout());

        if (Objects.nonNull(westComponent)) {
            combinePanel.add(westComponent, BorderLayout.WEST);
        }
        if (Objects.nonNull(eastComponent)) {
            combinePanel.add(eastComponent, BorderLayout.EAST);
        }
        if (Objects.nonNull(centerComponent)) {
            combinePanel.add(centerComponent, BorderLayout.CENTER);
        }
        return JPanelUtils.settingPanel(label, combinePanel, labelSize, valueSize);
    }

    public static JPanel combinePanel(String label, Component westComponent, Component centerComponent) {
        return combinePanel(label, westComponent, centerComponent, null, null);
    }

    public static JPanel flowPanel(int align, Component... components) {
        JPanel flowPanel = new JPanel(new FlowLayout(align));
        for (Component component : components) {
            flowPanel.add(component);
        }

        return flowPanel;
    }


    public static JTextArea tips() {
        return tips(JBColor.GREEN);
    }

    public static JTextArea tips(Color foreground) {
        JTextArea tipsArea = new JTextArea();
        tipsArea.setEditable(false);
        tipsArea.setForeground(foreground);
        tipsArea.setBorder(null);
        tipsArea.setOpaque(false);
        tipsArea.setBackground(JBColor.background());
        tipsArea.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        tipsArea.setLineWrap(true);

        return tipsArea;
    }

    public static ComboBox<String> createPrototypeComboBox() {
        ComboBox<String> prototypeComboBox = new ComboBox<>();
        prototypeComboBox.setSwingPopup(false);
        prototypeComboBox.setPrototypeDisplayValue("Prototype");

        return prototypeComboBox;
    }

    public static <T> ComboBox<T> createSearchComboBox() {
        ComboBox<T> prototypeComboBox = new ComboBox<>();
        prototypeComboBox.setSwingPopup(false);

        return prototypeComboBox;
    }

    public static <T> ComboBox<T> createSearchComboBox(T prototypeDisplayValue) {
        ComboBox<T> prototypeComboBox = new ComboBox<>();
        prototypeComboBox.setSwingPopup(false);
        prototypeComboBox.setPrototypeDisplayValue(prototypeDisplayValue);

        return prototypeComboBox;
    }

    public static JPanel singleCheckBox(JBCheckBox... checkboxes) {
        return singleCheckBox(null, checkboxes);
    }

    public static JPanel singleCheckBox(LayoutManager layout, JBCheckBox... checkboxes) {
        layout = ObjectUtils.defaultIfNull(layout, new FlowLayout(FlowLayout.LEFT));
        JPanel checkBoxPanel = new JPanel(layout);

        List<JBCheckBox> checkboxList = new ArrayList<>();

        for (JBCheckBox checkbox : checkboxes) {
            checkBoxPanel.add(checkbox);
            checkboxList.add(checkbox);
        }

        for (JBCheckBox checkbox : checkboxList) {
            checkbox.addActionListener(e -> {
                if (checkbox.isSelected()) {
                    checkboxList.stream().filter(c -> !checkbox.getText().equals(c.getText())).forEach(c -> c.setSelected(false));
                }
            });
        }

        return checkBoxPanel;
    }


    @SuppressWarnings("unchecked")
    public static <T> T getDialogComboBoxVal(ComboBox<T> comboBox, T defaultVal) {
        Object selectedItem = comboBox.getSelectedItem();
        return Objects.nonNull(selectedItem) ? (T) selectedItem : defaultVal;
    }
}
