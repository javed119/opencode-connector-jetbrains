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

        /**
         * 控制发送代码后是否将焦点切换到 OpenCode 所在的 Terminal 标签页。
         * 默认开启以保留历史行为；用户可在设置中关闭。
         */
        public boolean focusTerminalAfterSend = true;
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

    /**
     * 是否在发送代码后聚焦 OpenCode Terminal。
     *
     * @return true 表示发送后切焦点到 Terminal；false 表示保持当前焦点
     */
    public boolean isFocusTerminalAfterSend() {
        return state.focusTerminalAfterSend;
    }

    /**
     * 更新「发送代码后聚焦 OpenCode Terminal」开关。
     *
     * @param focusTerminalAfterSend 新的开关值
     */
    public void setFocusTerminalAfterSend(boolean focusTerminalAfterSend) {
        state.focusTerminalAfterSend = focusTerminalAfterSend;
    }
}
