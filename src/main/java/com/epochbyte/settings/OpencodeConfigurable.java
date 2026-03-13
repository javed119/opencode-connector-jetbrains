package com.epochbyte.settings;

import com.intellij.openapi.options.Configurable;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class OpencodeConfigurable implements Configurable {
    private JTextField hostField;
    
    @Nls
    @Override
    public String getDisplayName() {
        return "OpenCode";
    }
    
    @Nullable
    @Override
    public JComponent createComponent() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = JBUI.insets(5);
        gbc.anchor = GridBagConstraints.WEST;
        
        OpencodeSettings.State settings = OpencodeSettings.getInstance().getState();
        String host = settings != null ? settings.host : "http://127.0.0.1";
        
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Host:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        hostField = new JTextField(host, 20);
        panel.add(hostField, gbc);
        
        return panel;
    }
    
    @Override
    public boolean isModified() {
        OpencodeSettings.State settings = OpencodeSettings.getInstance().getState();
        if (settings == null) {
            return false;
        }
        return !hostField.getText().equals(settings.host);
    }
    
    @Override
    public void apply() {
        OpencodeSettings.State settings = OpencodeSettings.getInstance().getState();
        if (settings != null) {
            settings.host = hostField.getText();
        }
    }
    
    @Override
    public void reset() {
        OpencodeSettings.State settings = OpencodeSettings.getInstance().getState();
        String host = settings != null ? settings.host : "http://127.0.0.1";
        hostField.setText(host);
    }
}
