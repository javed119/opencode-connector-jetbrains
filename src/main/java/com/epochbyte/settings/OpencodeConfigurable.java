package com.epochbyte.settings;

import com.intellij.openapi.options.Configurable;
import com.intellij.ui.components.JBCheckBox;
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
        focusTerminalCheckBox = new JBCheckBox("Focus OpenCode terminal after sending code");
        rootPanel = FormBuilder.createFormBuilder()
            .addComponent(focusTerminalCheckBox)
            .addComponentFillVertically(new JPanel(), 0)
            .getPanel();
        reset();
        return rootPanel;
    }

    @Override
    public boolean isModified() {
        if (focusTerminalCheckBox == null) {
            return false;
        }
        return focusTerminalCheckBox.isSelected()
            != OpencodeSettings.getInstance().isFocusTerminalAfterSend();
    }

    @Override
    public void apply() {
        if (focusTerminalCheckBox == null) {
            return;
        }
        OpencodeSettings.getInstance()
            .setFocusTerminalAfterSend(focusTerminalCheckBox.isSelected());
    }

    @Override
    public void reset() {
        if (focusTerminalCheckBox == null) {
            return;
        }
        focusTerminalCheckBox.setSelected(
            OpencodeSettings.getInstance().isFocusTerminalAfterSend()
        );
    }

    @Override
    public void disposeUIResources() {
        focusTerminalCheckBox = null;
        rootPanel = null;
    }
}
