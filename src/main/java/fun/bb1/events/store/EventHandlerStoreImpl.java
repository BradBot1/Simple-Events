package fun.bb1.events.store;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import fun.bb1.events.handler.EventPriority;
import fun.bb1.events.handler.IEventHandler;

public final class EventHandlerStoreImpl<I> implements IEventHandlerStore<I> {
	
	private final @NotNull Map<EventPriority, List<IEventHandler<I>>> handlers = new EnumMap<EventPriority, List<IEventHandler<I>>>(EventPriority.class) {
		private static final long serialVersionUID = 2008249952001806207L;
	{
		for (final EventPriority priority : EventPriority.values()) {
			put(priority, priority.isSingleton() ? List.of() : new ArrayList<IEventHandler<I>>());
		}
	}};
	
	private @NotNull final Logger logger;
	
	@Internal
	public EventHandlerStoreImpl(final @Nullable Logger logger) {
		this.logger = logger == null ? DEFAULT_LOGGER.getContained() : logger;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void register(final @Nullable EventPriority priority, final @NotNull IEventHandler<I> handler, final boolean force) {
		final List<IEventHandler<I>> handlerList = this.handlers.get(priority);
		if (priority.isSingleton()) {
			if (handlerList.isEmpty()) {
				this.handlers.put(priority, List.of(handler));
				return;
			}
			if (force) {
				this.register(priority.getFallbackValue(), handlerList.get(0), false);
				this.handlers.put(priority, List.of(handler));
				this.logger.info("Singleton handler overriden for " + priority.name().toLowerCase() + ", " + handlerList.get(0).getClass().getName() + " => " + handler.getClass().getName());
				return;
			}
			this.register(priority.getFallbackValue(), handler, false);
			this.logger.warning("Failed to install singleton handler for " + handler.getClass().getName() + " as the desired priority is already taken");
		} else {
			handlerList.add(handler);
		}
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public @NotNull List<IEventHandler<I>> fetchWithPriority(@NotNull final EventPriority eventPriority) {
		return List.copyOf(this.handlers.get(eventPriority)); // since docs state must be immutable
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void unregister(@NotNull final IEventHandler<I> handler) {
		this.handlers.values().forEach(l -> l.remove(handler));
	}
	
}
