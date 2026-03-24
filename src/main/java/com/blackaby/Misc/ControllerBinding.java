package com.blackaby.Misc;

import java.util.Locale;
import java.util.Map;

/**
 * Stores one generic controller input binding.
 */
public record ControllerBinding(String componentId, Kind kind, Direction direction) {

    private static final String povComponentId = "pov";
    private static final float povUpLeft = 0.125f;
    private static final float povUp = 0.25f;
    private static final float povUpRight = 0.375f;
    private static final float povRight = 0.5f;
    private static final float povDownRight = 0.625f;
    private static final float povDown = 0.75f;
    private static final float povDownLeft = 0.875f;
    private static final float povLeft = 1.0f;

    /**
     * Supported controller input source kinds.
     */
    public enum Kind {
        BUTTON,
        AXIS,
        POV
    }

    /**
     * Supported input directions.
     */
    public enum Direction {
        NONE,
        POSITIVE,
        NEGATIVE,
        UP,
        DOWN,
        LEFT,
        RIGHT
    }

    /**
     * Creates a controller binding and validates its shape.
     *
     * @param componentId component identifier name
     * @param kind        source kind
     * @param direction   source direction
     */
    public ControllerBinding {
        if (componentId == null || componentId.isBlank()) {
            throw new IllegalArgumentException("Controller component id is required.");
        }
        if (kind == null) {
            throw new IllegalArgumentException("Controller binding kind is required.");
        }
        if (direction == null) {
            throw new IllegalArgumentException("Controller binding direction is required.");
        }
        if (kind == Kind.BUTTON && direction != Direction.NONE) {
            throw new IllegalArgumentException("Button bindings cannot use a direction.");
        }
        if (kind == Kind.AXIS && direction != Direction.POSITIVE && direction != Direction.NEGATIVE) {
            throw new IllegalArgumentException("Axis bindings must use a positive or negative direction.");
        }
        if (kind == Kind.POV && direction != Direction.UP && direction != Direction.DOWN
                && direction != Direction.LEFT && direction != Direction.RIGHT) {
            throw new IllegalArgumentException("POV bindings must use a cardinal direction.");
        }
    }

    /**
     * Creates a button binding.
     *
     * @param componentId component identifier name
     * @return button binding
     */
    public static ControllerBinding Button(String componentId) {
        return new ControllerBinding(componentId, Kind.BUTTON, Direction.NONE);
    }

    /**
     * Creates an axis binding.
     *
     * @param componentId component identifier name
     * @param positive    axis direction
     * @return axis binding
     */
    public static ControllerBinding Axis(String componentId, boolean positive) {
        return new ControllerBinding(componentId, Kind.AXIS,
                positive ? Direction.POSITIVE : Direction.NEGATIVE);
    }

    /**
     * Creates a POV binding.
     *
     * @param direction cardinal direction
     * @return POV binding
     */
    public static ControllerBinding Pov(Direction direction) {
        return new ControllerBinding(povComponentId, Kind.POV, direction);
    }

    /**
     * Returns whether the binding is active for the supplied component map.
     *
     * @param components component value lookup by identifier name
     * @param deadzone   deadzone threshold from 0.0 to 1.0
     * @return {@code true} if the binding is currently active
     */
    public boolean Matches(Map<String, Float> components, float deadzone) {
        if (components == null) {
            return false;
        }

        Float value = components.get(componentId);
        if (value == null) {
            return false;
        }

        return switch (kind) {
            case BUTTON -> value >= 0.5f;
            case AXIS -> MatchesAxis(value, deadzone);
            case POV -> MatchesPov(value);
        };
    }

    /**
     * Serialises the binding for the config file.
     *
     * @return config-safe value
     */
    public String ToConfigValue() {
        return kind.name() + ":" + componentId + ":" + direction.name();
    }

    /**
     * Returns a user-facing summary of the binding.
     *
     * @return binding label
     */
    public String ToDisplayText() {
        return switch (kind) {
            case BUTTON -> "Button " + FormatButtonIndex(componentId);
            case AXIS -> DescribeAxis(componentId, direction);
            case POV -> "D-pad " + Capitalise(direction.name());
        };
    }

    /**
     * Parses a stored config value.
     *
     * @param value stored config value
     * @return parsed binding, or {@code null} if invalid
     */
    public static ControllerBinding FromConfigValue(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        String[] tokens = value.split(":", 3);
        if (tokens.length != 3) {
            return null;
        }

        try {
            Kind kind = Kind.valueOf(tokens[0]);
            Direction direction = Direction.valueOf(tokens[2]);
            return new ControllerBinding(tokens[1], kind, direction);
        } catch (IllegalArgumentException exception) {
            return null;
        }
    }

    private boolean MatchesAxis(float value, float configuredDeadzone) {
        float threshold = Math.max(configuredDeadzone, 0.35f);
        return direction == Direction.POSITIVE ? value >= threshold : value <= -threshold;
    }

    private boolean MatchesPov(float value) {
        return switch (direction) {
            case UP -> Float.compare(value, povUp) == 0
                    || Float.compare(value, povUpLeft) == 0
                    || Float.compare(value, povUpRight) == 0;
            case DOWN -> Float.compare(value, povDown) == 0
                    || Float.compare(value, povDownLeft) == 0
                    || Float.compare(value, povDownRight) == 0;
            case LEFT -> Float.compare(value, povLeft) == 0
                    || Float.compare(value, povUpLeft) == 0
                    || Float.compare(value, povDownLeft) == 0;
            case RIGHT -> Float.compare(value, povRight) == 0
                    || Float.compare(value, povUpRight) == 0
                    || Float.compare(value, povDownRight) == 0;
            default -> false;
        };
    }

    private static String DescribeAxis(String componentId, Direction direction) {
        String id = componentId.toLowerCase(Locale.ROOT);
        String suffix = direction == Direction.POSITIVE ? "+" : "-";
        return switch (id) {
            case "x" -> direction == Direction.POSITIVE ? "Left Stick Right" : "Left Stick Left";
            case "y" -> direction == Direction.POSITIVE ? "Left Stick Down" : "Left Stick Up";
            case "rx" -> direction == Direction.POSITIVE ? "Right Stick Right" : "Right Stick Left";
            case "ry" -> direction == Direction.POSITIVE ? "Right Stick Down" : "Right Stick Up";
            case "z" -> "Axis Z" + suffix;
            case "rz" -> "Axis RZ" + suffix;
            default -> componentId.toUpperCase(Locale.ROOT) + suffix;
        };
    }

    private static String FormatButtonIndex(String componentId) {
        try {
            return String.valueOf(Integer.parseInt(componentId) + 1);
        } catch (NumberFormatException exception) {
            return componentId.toUpperCase(Locale.ROOT);
        }
    }

    private static String Capitalise(String value) {
        String lower = value.toLowerCase(Locale.ROOT);
        return Character.toUpperCase(lower.charAt(0)) + lower.substring(1);
    }
}
