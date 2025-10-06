package com.kakarot.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor
@Getter
public class Plot {
    private final UUID owner;
    private final int gridX;
    private final int gridZ;
}
