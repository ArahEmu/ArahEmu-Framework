package com.arahemu.framework.event;

/**
 * @author direct
 */
@FunctionalInterface
public interface EventHandler<T extends Event> {
    void handle(T event);
}
