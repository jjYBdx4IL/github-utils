package com.github.jjYBdx4IL.utils;

import java.util.Locale;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class ExampleConfig extends AbstractConfig {

    public String configOption1 = DEFAULT_STRING_VALUE;
    public String configOption2 = DEFAULT_STRING_VALUE;

    public ExampleConfig(String appName) {
        super(appName);
    }

    @Override
    public void postprocess() {
        if (configOption2 != null) {
            configOption2 = configOption2.toUpperCase(Locale.ROOT);
        }
    }

}
