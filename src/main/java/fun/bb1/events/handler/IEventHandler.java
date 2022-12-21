package fun.bb1.events.handler;

import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface IEventHandler<T> {
	
	public void handleEvent(@NotNull final T event);
	
}
