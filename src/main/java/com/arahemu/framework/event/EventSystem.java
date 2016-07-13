package com.arahemu.framework.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author direct
 */
public final class EventSystem {
    private static final Logger LOGGER = LoggerFactory.getLogger(EventSystem.class);

    private final Map<Class<?>, List<EventHandler<?>>> eventHandlerMap;
    private final ExecutorService executorService;

    public EventSystem() {
        eventHandlerMap = new ConcurrentHashMap<>();
        executorService = Executors.newCachedThreadPool();
    }

    public EventSystem register(Registrable registrable) {
        registrable.register(this);

        return this;
    }

    public <T extends Event> EventSystem register(Class<T> eventClass, EventHandler<T> eventHandler) {
        if (!eventHandlerMap.containsKey(eventClass)) {
            eventHandlerMap.put(eventClass, new ArrayList<>());
        }

        List<EventHandler<?>> eventHandlers = eventHandlerMap.get(eventClass);

        synchronized (eventHandlerMap) {
            eventHandlers.add(eventHandler);
        }

        return this;
    }

    public EventSystem submit(Event event) {
        return submit(event, false);
    }

    public EventSystem submitAsync(Event event) {
        return submit(event, true);
    }

    @SuppressWarnings("unchecked")
    private EventSystem submit(Event event, boolean asynchronous) {
        Class<?> eventClass = event.getClass();

        if (eventHandlerMap.containsKey(eventClass)) {
            List<EventHandler<?>> eventHandlers = eventHandlerMap.get(eventClass);

            synchronized (eventHandlerMap) {
                for (EventHandler<?> eventHandler : eventHandlers) {
                    if (!asynchronous) {
                        ((EventHandler<Event>)eventHandler).handle(event);
                    } else {
                        executorService.submit(() -> ((EventHandler<Event>)eventHandler).handle(event));
                    }
                }
            }
        } else {
            LOGGER.debug("Unhandled event '{}'.", eventClass.getName());
        }

        return this;
    }
}
