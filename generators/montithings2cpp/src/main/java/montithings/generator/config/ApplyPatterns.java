package montithings.generator.config;

public enum ApplyPatterns {
    OFF("OFF"),
    ON("ON");

    final String name;

    ApplyPatterns(String name) {
        this.name = name;
    }

    /**
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
        return this.name;
    }

    public static ApplyPatterns fromString(String applyPatterns) {
        switch (applyPatterns) {
            case "OFF":
                return ApplyPatterns.OFF;
            case "ON":
                return ApplyPatterns.ON;
            default:
                throw new IllegalArgumentException(
                        "0xMT327 Apply Patterns " + applyPatterns + " is unknown");
        }
    }
}
