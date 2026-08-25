package com.epochbyte.settings;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 * OpenCode Connector 插件的应用级设置面板。
 * 注册在 Settings -> Tools -> OpenCode Connector 下。
 */
public class OpencodeConfigurable implements Configurable {

    private JBTextField serverUrlField;
    private JBCheckBox focusTerminalCheckBox;
    private JPanel rootPanel;

    @Nls
    @Override
    public String getDisplayName() {
        return "OpenCode Connector";
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        serverUrlField = new JBTextField();
        serverUrlField.setToolTipText("Leave empty to start a local OpenCode server");
        focusTerminalCheckBox = new JBCheckBox("Focus OpenCode terminal after sending code");
        rootPanel = FormBuilder.createFormBuilder()
            .addLabeledComponent("Server URL:", serverUrlField)
            .addComponent(focusTerminalCheckBox)
            .addComponentFillVertically(new JPanel(), 0)
            .getPanel();
        reset();
        return rootPanel;
    }

    @Override
    public boolean isModified() {
        if (serverUrlField == null || focusTerminalCheckBox == null) {
            return false;
        }
        OpencodeSettings settings = OpencodeSettings.getInstance();
        String serverUrl = serverUrlField.getText().trim();
        try {
            serverUrl = OpencodeSettings.normalizeServerUrl(serverUrl);
        } catch (IllegalArgumentException ex) {
            return true;
        }
        return !serverUrl.equals(settings.getServerUrl())
            || focusTerminalCheckBox.isSelected() != settings.isFocusTerminalAfterSend();
    }

    @Override
    public void apply() throws ConfigurationException {
        if (serverUrlField == null || focusTerminalCheckBox == null) {
            return;
        }

        String serverUrl = serverUrlField.getText().trim();
        try {
            OpencodeSettings settings = OpencodeSettings.getInstance();
            String normalizedServerUrl = OpencodeSettings.normalizeServerUrl(serverUrl);
            settings.setServerUrl(normalizedServerUrl);
            serverUrlField.setText(normalizedServerUrl);
            settings.setFocusTerminalAfterSend(focusTerminalCheckBox.isSelected());
        } catch (IllegalArgumentException ex) {
            throw new ConfigurationException(ex.getMessage());
        }
    }

    @Override
    public void reset() {
        if (serverUrlField == null || focusTerminalCheckBox == null) {
            return;
        }
        serverUrlField.setText(OpencodeSettings.getInstance().getServerUrl());
        focusTerminalCheckBox.setSelected(
            OpencodeSettings.getInstance().isFocusTerminalAfterSend()
        );
    }

    @Override
    public void disposeUIResources() {
        serverUrlField = null;
        focusTerminalCheckBox = null;
        rootPanel = null;
    }
}
