package com.epochbyte.settings;

import com.intellij.openapi.options.Configurable;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class OpencodeConfigurable implements Configurable {
    private JTextField hostField;
    private JTextField sessionIdField;
    
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
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        OpencodeSettings.State settings = OpencodeSettings.getInstance().getState();
        
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Host:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        hostField = new JTextField(settings.host, 20);
        panel.add(hostField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(new JLabel("Session ID:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        sessionIdField = new JTextField(settings.sessionId, 20);
        panel.add(sessionIdField, gbc);
        
        return panel;
    }
    
    @Override
    public boolean isModified() {
        OpencodeSettings.State settings = OpencodeSettings.getInstance().getState();
        return !hostField.getText().equals(settings.host) ||
               !sessionIdField.getText().equals(settings.sessionId);
    }
    
    @Override
    public void apply() {
        OpencodeSettings.State settings = OpencodeSettings.getInstance().getState();
        settings.host = hostField.getText();
        settings.sessionId = sessionIdField.getText();
    }
    
    @Override
    public void reset() {
        OpencodeSettings.State settings = OpencodeSettings.getInstance().getState();
        hostField.setText(settings.host);
        sessionIdField.setText(settings.sessionId);
    }
}
