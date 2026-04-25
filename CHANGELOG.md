# Changelog

## [1.1.0]

### Added
- Project View context menu action `Add To OpenCode` for sending selected files and directories to OpenCode.
- Support for sending multiple selected files or directories to OpenCode in a single action.
- OpenCode action icons in the editor and Project View context menus for easier discovery.

### Changed
- Switched the development IDE target to IntelliJ IDEA Community for `runIde` testing.
- Shared OpenCode reference-building and terminal-focusing logic across editor and Project View actions.

### Fixed
- Project View action update threading by moving selection lookups off the EDT.
- Duplicate nested selections by collapsing child paths when a parent directory is already selected.

## [1.0.2]

### Added
- One-click OpenCode launch support from the IntelliJ toolbar.
- Automatic OpenCode port detection across the configured range.

### Fixed
- Multi-instance OpenCode session handling within the same project directory.
