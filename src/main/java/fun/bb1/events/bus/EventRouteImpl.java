package fun.bb1.events.bus;

import java.util.Collection;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import fun.bb1.events.handler.EventPriority;
import fun.bb1.events.handler.IEventHandler;
import fun.bb1.events.middleware.IEventMiddleware;
import fun.bb1.events.middleware.MiddlewareResult;
import fun.bb1.events.store.IEventHandlerStore;

public final record EventRouteImpl<I>(@NotNull Collection<IEventMiddleware<I>> middleware, @NotNull IEventHandlerStore<I> handlerStore, @NotNull Class<I> clazz) implements IEventRoute<I> {
	/***
	 * {@inheritDoc}
	 */
	@Override
	public final @Nullable I passThroughMiddleware(@NotNull I eventData) {
		for (final IEventMiddleware<I> middleware : this.middleware) {
			final MiddlewareResult<I> result = middleware.handle(eventData);
			if (!result.passOn()) return null; // Do not do event
			eventData = result.eventObject();
		}
		return eventData;
	}
	/***
	 * {@inheritDoc}
	 */
	@Override
	public final @NotNull Runnable travelRoute(final @NotNull I eventData) {
		this.handlerStore.fetchInOrder().forEach((i) -> i.handleEvent(eventData));
		return () -> {
			this.handlerStore.fetchWithPriority(EventPriority.WATCH).forEach((i) -> i.handleEvent(eventData));
		};
	}
	/***
	 * {@inheritDoc}
	 */
	@Override
	public void addMiddleware(@NotNull final IEventMiddleware<I> middleware) {
		this.middleware.add(middleware);
	}
	/***
	 * {@inheritDoc}
	 */
	@Override
	public void addStop(@NotNull final EventPriority priority, @NotNull final IEventHandler<I> eventHandler, @Nullable final Boolean force) {
		this.handlerStore.register(priority == null ? EventPriority.DEFAULT : priority, eventHandler, force == null ? false : force.booleanValue());
	}
	
}
