package com.arahemu.framework.event;

/**
 * @author direct
 */
public abstract class AbstractEvent implements Event {
    private Object sender;

    public AbstractEvent(Object sender) {
        this.sender = sender;
    }

    @Override
    public Object getSender() {
        return sender;
    }
}
