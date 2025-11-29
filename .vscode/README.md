# VS Code Configuration

This directory contains VS Code workspace settings for the CustomBooks project.

## Files

### settings.json
Workspace-specific settings including:
- **Git/Source Control**: Auto-fetch, smart commit, decorations
- **GitLens**: Code lens, blame annotations, current line info
- **Java**: Compilation and formatting settings
- **Editor**: Rulers, formatting, and file handling

### extensions.json
Recommended extensions for development:
- Java Extension Pack
- Spring Boot Tools
- GitLens
- Git Graph
- SonarLint
- Docker support

### launch.json
Debug configurations for:
- Spring Boot application
- Java debugging

### tasks.json
Build and run tasks:
- Maven clean install
- Maven Spring Boot run
- Docker build and run
- Docker stop

## Source Control View Features

### Enabled Features
1. **Auto-fetch**: Automatically fetches from remote every 3 minutes
2. **Smart Commit**: Commits all changes when no staged changes
3. **Tree View**: Source Control displayed as a file tree
4. **Diff Decorations**: Shows changes in gutter and overview ruler
5. **Git Decorations**: File status colors and badges in Explorer
6. **Auto-reveal**: Automatically reveals changes in Source Control view

### GitLens Features (if installed)
- Current line blame
- Code lens showing authors and recent changes
- Rich hover information
- Git history and file history

## Using the Source Control View

### Keyboard Shortcuts
- `Ctrl+Shift+G`: Open Source Control view
- `Ctrl+Enter`: Commit staged changes
- `Ctrl+K Ctrl+P`: Show commit details
- `Alt+Left/Right`: Navigate through changes

### Common Tasks
1. **Stage Changes**: Click '+' next to file or use `git add`
2. **Commit**: Type message and click checkmark or press `Ctrl+Enter`
3. **Push**: Click '...' menu → Push
4. **Pull**: Click '...' menu → Pull
5. **View History**: Right-click file → View File History (GitLens)

## Recommended Extensions

Install recommended extensions by:
1. Press `Ctrl+Shift+P`
2. Type "Extensions: Show Recommended Extensions"
3. Click "Install All" or install individually

## Git Settings Explained

```json
"git.autofetch": true               // Auto-fetch from remote
"git.confirmSync": false            // No confirmation for sync
"git.enableSmartCommit": true       // Commit all when no staged
"git.decorations.enabled": true     // Show git status colors
"scm.defaultViewMode": "tree"       // Tree view for changes
"scm.autoReveal": true             // Auto-show changed files
```

## Workspace File

The `customized-bookstore.code-workspace` file provides:
- Folder configuration
- Integrated launch configurations
- Task definitions
- Extension recommendations

Open the workspace by:
1. File → Open Workspace from File
2. Select `customized-bookstore.code-workspace`

---

**Note**: These settings are shared across the team. Personal preferences should be configured in User Settings.
