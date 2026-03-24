package com.blackaby.Frontend;

import com.blackaby.Backend.Emulation.DuckEmulation;
import com.blackaby.Backend.Helpers.GUIActions;
import com.blackaby.Backend.Emulation.Peripherals.DuckJoypad;
import com.blackaby.Misc.AppShortcut;
import com.blackaby.Misc.AppShortcutBindings;
import com.blackaby.Misc.Settings;

import javax.swing.KeyStroke;
import java.awt.event.ActionEvent;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.Set;

/**
 * Routes host keyboard input to the emulated joypad while the main window has
 * focus.
 */
public final class InputRouter implements KeyEventDispatcher {

    private final MainWindow mainWindow;
    private final DuckEmulation emulation;
    private final ControllerInputService controllerInputService = ControllerInputService.Shared();
    private final Object inputStateLock = new Object();
    private final Set<Integer> pressedKeyCodes = new HashSet<>();
    private final Set<Integer> consumedShortcutKeyCodes = new HashSet<>();
    private final EnumSet<DuckJoypad.Button> keyboardPressedButtons = EnumSet.noneOf(DuckJoypad.Button.class);
    private final EnumSet<DuckJoypad.Button> controllerPressedButtons = EnumSet.noneOf(DuckJoypad.Button.class);
    private final ScheduledExecutorService controllerPollingExecutor = Executors.newSingleThreadScheduledExecutor(run -> {
        Thread thread = new Thread(run, "gameduck-controller-input");
        thread.setDaemon(true);
        return thread;
    });
    private volatile boolean routedInputActive;

    /**
     * Creates an input router bound to the main window and emulator instance.
     *
     * @param mainWindow owning main window
     * @param emulation running emulator
     */
    public InputRouter(MainWindow mainWindow, DuckEmulation emulation) {
        this.mainWindow = mainWindow;
        this.emulation = emulation;
        controllerPollingExecutor.scheduleAtFixedRate(this::PollControllerInput, 0L, 8L, TimeUnit.MILLISECONDS);
    }

    /**
     * Registers the router with the current keyboard focus manager.
     */
    public void Install() {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(this);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (!ShouldRouteInput()) {
            ClearAllInputStates();
            return false;
        }

        if (HandleAppShortcut(event)) {
            return true;
        }

        DuckJoypad.Button button = Settings.inputBindings.GetButtonForKeyCode(event.getKeyCode());
        if (button == null) {
            return false;
        }

        if (event.getID() == KeyEvent.KEY_PRESSED) {
            SetKeyboardButtonState(button, true);
        } else if (event.getID() == KeyEvent.KEY_RELEASED) {
            SetKeyboardButtonState(button, false);
        }

        return false;
    }

    private boolean HandleAppShortcut(KeyEvent event) {
        if (event.getID() == KeyEvent.KEY_TYPED || event.getKeyCode() == KeyEvent.VK_UNDEFINED) {
            return false;
        }

        if (event.getID() == KeyEvent.KEY_PRESSED) {
            boolean repeatedPress = !pressedKeyCodes.add(event.getKeyCode());
            AppShortcut shortcut = FindShortcut(event);
            if (shortcut == null) {
                return false;
            }

            consumedShortcutKeyCodes.add(event.getKeyCode());
            if (!repeatedPress) {
                TriggerShortcut(shortcut);
            }
            return true;
        }

        if (event.getID() == KeyEvent.KEY_RELEASED) {
            pressedKeyCodes.remove(event.getKeyCode());

            if (FindShortcut(event) != null) {
                consumedShortcutKeyCodes.remove(event.getKeyCode());
                return true;
            }

            return consumedShortcutKeyCodes.remove(event.getKeyCode());
        }

        return false;
    }

    private AppShortcut FindShortcut(KeyEvent event) {
        KeyStroke keyStroke = AppShortcutBindings.Normalise(
                KeyStroke.getKeyStroke(event.getKeyCode(), event.getModifiersEx()));
        if (keyStroke == null) {
            return null;
        }
        return Settings.appShortcutBindings.GetShortcutForKeyStroke(keyStroke);
    }

    private void TriggerShortcut(AppShortcut shortcut) {
        new GUIActions(mainWindow, shortcut.Action(), emulation)
                .actionPerformed(new ActionEvent(mainWindow, ActionEvent.ACTION_PERFORMED, shortcut.name()));
    }

    private void PollControllerInput() {
        boolean shouldRouteInput = ShouldRouteInput();
        if (!shouldRouteInput) {
            if (routedInputActive) {
                ClearAllInputStates();
                routedInputActive = false;
            }
            return;
        }

        routedInputActive = true;
        ApplyControllerState(controllerInputService.PollBoundButtons());
    }

    private void SetKeyboardButtonState(DuckJoypad.Button button, boolean pressed) {
        synchronized (inputStateLock) {
            if (pressed) {
                keyboardPressedButtons.add(button);
            } else {
                keyboardPressedButtons.remove(button);
            }
            ApplyCombinedState(button);
        }
    }

    private void ApplyControllerState(EnumSet<DuckJoypad.Button> pressedButtons) {
        synchronized (inputStateLock) {
            for (DuckJoypad.Button button : DuckJoypad.Button.values()) {
                boolean nextPressed = pressedButtons.contains(button);
                if (nextPressed) {
                    controllerPressedButtons.add(button);
                } else {
                    controllerPressedButtons.remove(button);
                }
                ApplyCombinedState(button);
            }
        }
    }

    private void ApplyCombinedState(DuckJoypad.Button button) {
        emulation.SetButtonPressed(button,
                keyboardPressedButtons.contains(button) || controllerPressedButtons.contains(button));
    }

    private void ClearAllInputStates() {
        synchronized (inputStateLock) {
            pressedKeyCodes.clear();
            consumedShortcutKeyCodes.clear();
            keyboardPressedButtons.clear();
            controllerPressedButtons.clear();
            for (DuckJoypad.Button button : DuckJoypad.Button.values()) {
                emulation.SetButtonPressed(button, false);
            }
        }
    }

    /**
     * Returns whether host input should currently be sent to the emulator.
     *
     * @return {@code true} when the main window is the active window
     */
    private boolean ShouldRouteInput() {
        Window activeWindow = KeyboardFocusManager.getCurrentKeyboardFocusManager().getActiveWindow();
        return activeWindow == mainWindow;
    }
}
