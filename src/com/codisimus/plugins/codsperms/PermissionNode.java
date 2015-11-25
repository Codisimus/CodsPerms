package com.codisimus.plugins.codsperms;

/**
 * PermissionNode represents a Minecraft Permission node and it's value
 *
 * @author Codisimus
 */
public class PermissionNode {
    private final String name;
    private boolean value;

    public PermissionNode(String name) {
        this.name = name;
        value = true;
    }

    public PermissionNode(String name, boolean value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public boolean getValue() {
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }
}
