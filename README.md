# 2048

A desktop implementation of *2048* built with *Java 21* and *JavaFX 21*.

This project started as a straightforward clone and evolved into a more structured application with animation sequencing, undo support, leaderboard persistence, confirmation overlays, and screen-scale adaptation.

# Highlights

- Built with JavaFX for a native desktop UI
- Deterministic undo system with bounded history
- Animated move, merge, spawn, game-over, and board-transition effects
- Persistent local leaderboard with date and score
- Side control panels for undo, reset, and exit
- Global scaling based on screen size to preserve layout proportions across different resolutions

# Tech Stack

- Java 21
- JavaFX 21
- Maven

# Features

- Classic 4x4 2048 gameplay
- Score tracking
- Undo support with a history limit of 10 valid moves
- Game-over overlay with:
  - restart
  - undo last move
- Reset confirmation dialog
- Exit confirmation dialog
- Local leaderboard saved to `leaderboard.txt`
- Top ranked scores displayed in the right-side panel
- Responsive stage scaling through global UI scaling instead of per-component resizing

# Project Structure

- `GameState`
  - core game rules
  - movement resolution
  - merge logic
  - score updates
  - RNG state for deterministic undo behavior
- `InputHandler`
  - keyboard input
  - input buffering during animations
- `GameView`
  - main UI composition
  - layout wiring for panels, overlays, score, and board
- `BoardAnimationController`
  - slide, merge, spawn, and board crossfade transitions
- `GameOverOverlay`
  - game-over presentation and restart/undo actions
- `ControlPanel`
  - left-side action buttons
- `LeaderboardPanel`
  - right-side leaderboard UI
- `ConfirmationOverlay`
  - modal confirmation dialogs
- `GameSessionController`
  - application flow orchestration between state, view, and persistence
- `LeaderboardStore`
  - score persistence in a local text file

# Architecture Notes

- The game logic is separated from the JavaFX rendering layer.
- Move animations are based on explicit transition data (`MoveResult`, `TileMove`, `SpawnedTile`) instead of trying to infer animation state from the final board alone.
- Undo restores both board state and RNG state. This prevents using undo to reroll future spawns.
- The application uses a logical base resolution and scales the whole UI to fit different screen sizes while preserving proportions.

# Running the Project

- Requirements:
  - Java 21
  - Maven

- Run with Maven:

```bash
mvn javafx:run
```

- Compile only:

```bash
mvn compile
```

# Leaderboard Storage

- Scores are stored locally in `leaderboard.txt`
- Each line uses this format:

```text
YYYY-MM-DD|score
```


# What I Focused On

- Keeping gameplay logic deterministic and debuggable
- Making animation phases explicit instead of mixing logical and visual state
- Improving maintainability by splitting large UI responsibilities into smaller classes
- Preserving a consistent visual composition across different monitor resolutions
