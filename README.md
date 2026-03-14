# OpenCode Connector

IntelliJ IDEA plugin for quickly sending selected code to OpenCode with one-click OpenCode launch support.

## Features

- **Keyboard Shortcut**: Send selected code to OpenCode via keyboard shortcut
- **Context Menu**: Send code through editor right-click menu
- **One-Click Launch**: Automatically start OpenCode in Terminal via toolbar button
- **Auto Port Detection**: Automatically find available ports (20000-40000)
- **Multi-Instance Support**: Support running multiple OpenCode instances simultaneously

## Usage

### Launch OpenCode

Click the run icon in the main toolbar

![img.png](docs/resource/img.png)

new ui
![img_1.png](docs/resource/img_1.png)
- Automatically finds available port
- Executes `opencode --port <port>` in Terminal
- Activates Terminal window

### Send Code to OpenCode
1. Select code in the editor
2. Use either method to send:
   - **Keyboard Shortcut**:
     - Windows/Linux: `Ctrl+Alt+K`
     - macOS: `Cmd+Option+K`
   - **Context Menu**: Select `Send to OpenCode`

Code is sent in format: `@file-path#Lstart-line-end-line`

**Note**: Code can only be sent to OpenCode sessions running in the same working directory as your project.

## Development Build

### Requirements

- JDK 17+
- Gradle 8.0+

### Build Commands

```bash
# Build plugin
./gradlew buildPlugin

# Run plugin (sandbox environment)
./gradlew runIde

# Run tests
./gradlew test
```

Build artifacts located at: `build/distributions/opencode-connector-*.zip`

## Tech Stack

- Java 17
- IntelliJ Platform SDK 2023.2
- Gradle (Kotlin DSL)
- Gson 2.10.1

## License

MIT License

