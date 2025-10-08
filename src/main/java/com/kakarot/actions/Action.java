package com.kakarot.actions;

import com.kakarot.data.Plot;

public interface Action {
    boolean isValid();
    void execute(Plot plot);
}
