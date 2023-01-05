package fun.bb1.events.abstraction.bukkit;

import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;

import fun.bb1.events.bus.EventBus;
import fun.bb1.events.handler.IEventHandler;

public final class HandlerListImpl<I extends StaticEvent<I>> implements IHandlerList<I> {
	
	protected final @NotNull EventBus bus;
	protected final @NotNull Class<I> clazz;
	
	public HandlerListImpl(final @NotNull Class<I> clazz) {
		this(clazz, EventBus.DEFAULT_BUS);
	}
	
	@Internal
	public HandlerListImpl(final @NotNull Class<I> clazz, final @NotNull EventBus bus) {
		this.clazz = clazz;
		this.bus = bus;
		this.bus.publishRoute(clazz.getSimpleName(), clazz);
	}

	@Override
	public void register(@NotNull IEventHandler<I> eventHandler) {
		this.bus.subscribe(this.clazz.getSimpleName(), eventHandler);
	}

	@Override
	public void unregister(@NotNull IEventHandler<I> eventHandler) {
		this.bus.unsubscribe(this.clazz.getSimpleName(), eventHandler);
	}
	
}
