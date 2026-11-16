# Black Mench — Card Game Project

This is a simple Java Swing Blackjack variant based on the `RayTechEnthusiast/cardGame` starter code and Attila-style methods. `Solitaire.java` keeps the starter filename so the existing `Main` / `GUI` pattern still makes sense.

## What is in the ZIP?

- `src/main/java/com/cardGame/Card.java` — stores and draws cards, including Mench.
- `src/main/java/com/cardGame/Solitaire.java` — creates/shuffles a 53-card deck; controls the game, tokens, payouts, dealer, and Mench.
- `src/main/java/com/cardGame/GUI.java` — original null-layout / `JLayeredPane` Swing approach; draws cards and handles clicks.
- `src/main/java/com/cardGame/Main.java` — starts the window.
- `src/main/java/com/cardGame/ImagePanel.java` — original background-image helper.
- `tests/BlackMenchTests.java` — automated game logic tests without JUnit.
- `setup-black-mench.ps1` — clone (if needed), back up files, install, compile, test, commit, push, and run.

The **original card PNGs, back.png, background.jpg, pom.xml and GitHub settings stay in your starter repository**. The setup script clones that repository or updates your already-open local copy; it does not erase the resources. If your original graphics are missing, the cards still draw a simple fallback design.

## How to play

1. Select **Bet 5**, **Bet 15**, **Bet 30**, or **Bet All**.
2. Click the pictured **deck** or **Hit** button to draw. Click **Stand** to let the dealer play.
3. Dealer draws until their hand reaches 17 or more. Face-down dealer card is revealed after the round.
4. Winner receives double the wager, ties get their bet returned, and losses lose the bet.
5. Click **Next Round** to continue. Reach 1000 tokens to win the game; losing all tokens ends the game. **Play Again** starts over.

Values: Ace=1, 2–10=face value, Jack=11, Queen=12, King=13. There are 52 normal cards and one **Mench**. Whenever Mench appears, compute the other cards in that hand first. If their total is below 18, Mench adds just enough for a randomly selected total from 18 to 21. If total is 18 or higher, Mench subtracts 1–18 randomly. The Mench card shows its signed adjustment. Each new round creates a clean, reshuffled 53-card deck, without duplicating cards within a round.

## VS Code PowerShell (Windows)

1. Download `BlackMench_Project.zip` to Downloads.
2. Open VS Code **Terminal → New Terminal** (PowerShell). If you already have the `cardGame` repository checked out, first open that folder in VS Code so the script uses it.
3. Paste this **one block**:

```powershell
$zip = Join-Path $HOME 'Downloads\BlackMench_Project.zip'
$unpacked = Join-Path $HOME 'Downloads\BlackMench_Project_Unpacked'
Expand-Archive -LiteralPath $zip -DestinationPath $unpacked -Force
powershell -NoProfile -ExecutionPolicy Bypass -File (Join-Path $unpacked 'setup-black-mench.ps1')
```

The command checks Git and JDK 17+, creates/switches to the `feature/black-mench-rubric` branch, backs up your old source files, compiles everything, runs tests, commits, attempts to push to GitHub, then opens the game.

You must be signed into GitHub locally for the `git push` to succeed. If not, the script will show the push command to rerun after authentication. It never force-pushes or overwrites your `main` branch. If Git reports uncommitted changes, commit/stash them first rather than letting the installer overwrite them.

## Manual commands after setup (inside the cardGame folder)

```powershell
$javaFiles = @(Get-ChildItem src/main/java/com/cardGame -Filter '*.java' | ForEach-Object FullName)
New-Item -ItemType Directory -Force target/blackmench-classes | Out-Null
javac -encoding UTF-8 -d target/blackmench-classes @javaFiles tests/BlackMenchTests.java
java -Djava.awt.headless=true -cp target/blackmench-classes com.cardGame.BlackMenchTests
java -cp target/blackmench-classes com.cardGame.Main
```

## Rubric checklist

- **Programming layout:** `GUI` handles Swing and input, `Solitaire` handles rules, `Card` stores graphics and values, `Main` starts the game.
- **Code integrity/layout:** clear function names, input guards, no duplicate payouts, pre/post comments above methods.
- **Card storage:** `Stack<Card>` for full shuffled deck and separate player/dealer hands.
- **Graphics:** a fixed-size window with all buttons and overlapped `JLayeredPane` hands that adjust spacing so long hands fit.
- **Deck behavior:** clicking the deck transfers its top card to the player; the deck is recreated and shuffled for each round.
- **Game logic:** proper win/loss/tie resolution, betting, dealer behavior, Mench effect and starting order.
- **Reveal:** dealer's hole card is hidden until round result.
- **Winning the game:** win/game-over dialog and Play Again action.

### Before submitting

- [ ] Run the setup commands and confirm `PASS` from tests.
- [ ] Try Bet → Hit by clicking the deck → Stand → Next Round.
- [ ] Confirm dealer's first card flips up at the end.
- [ ] Confirm your GitHub branch appears (the script prints its link after a successful push).
- [ ] Show Mr. M your Cycle 1 plan as requested. If your teacher needs the code on `main` instead of a feature branch, merge it only after you've reviewed it.

Note: Automated tests were run with Java without the GUI; a desktop Swing screenshot was also checked. You should still test actual clicks on your Windows computer with the repository's card images.
