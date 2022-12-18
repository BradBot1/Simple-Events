package fun.bb1.events.handler;

import org.jetbrains.annotations.NotNull;

import fun.bb1.events.Event;

@FunctionalInterface
public interface IEventHandler<E, T> {
	
	public void handleEvent(@NotNull final Event<T> E);
	
}
