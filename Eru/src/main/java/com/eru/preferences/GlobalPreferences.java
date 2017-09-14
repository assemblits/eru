package com.eru.preferences;

import org.springframework.stereotype.Component;

import static com.eru.gui.Application.Theme;

/**
 * Created by mtrujillo on 9/2/17.
 */
@Component
public class GlobalPreferences {

    static final Theme DEFAULT_THEME = Theme.DEFAULT;
    static final String THEME = "THEME";
    private Theme theme = DEFAULT_THEME;

    public GlobalPreferences() {
    }

    public Theme getTheme() {
        return theme;
    }

    public void setTheme(Theme theme) {
        this.theme = theme;
    }
}
