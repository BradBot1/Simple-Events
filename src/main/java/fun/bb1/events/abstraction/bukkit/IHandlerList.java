package fun.bb1.events.abstraction.bukkit;

import java.util.Collection;

import org.jetbrains.annotations.NotNull;

import fun.bb1.events.handler.IEventHandler;

public interface IHandlerList<I extends StaticEvent<I>> {
	
	public void register(@NotNull final IEventHandler<I> eventHandler);
	
	public default void registerAll(@NotNull final Collection<IEventHandler<I>> eventHandlers) {
		eventHandlers.forEach(this::register);
	}
	
	public void unregister(@NotNull final IEventHandler<I> eventHandler);
	
	public default void unregisterAll(@NotNull final Collection<IEventHandler<I>> eventHandlers) {
		eventHandlers.forEach(this::unregister);
	}
	
}
