package com.epochbyte.settings;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(
    name = "OpencodeSettings",
    storages = @Storage("OpencodeSettings.xml")
)
public class OpencodeSettings implements PersistentStateComponent<OpencodeSettings.State> {
    
    public static class State {
        public String host = "http://127.0.0.1";
    }
    
    private State state = new State();
    
    public static OpencodeSettings getInstance() {
        return ApplicationManager.getApplication().getService(OpencodeSettings.class);
    }
    
    @Nullable
    @Override
    public State getState() {
        return state;
    }
    
    @Override
    public void loadState(@NotNull State state) {
        this.state = state;
    }
}
