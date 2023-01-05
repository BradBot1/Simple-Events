package fun.bb1.events.bus;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import fun.bb1.events.handler.EventPriority;
import fun.bb1.events.handler.IEventHandler;
import fun.bb1.events.middleware.IEventMiddleware;

public interface IEventRoute<I> {
	/**
	 * Passes the event data through the middleware
	 * 
	 * @apiNote Will return null when an {@link IEventMiddleware} cancels the event
	 * 
	 * @param eventData The event data to pass through the {@link IEventMiddleware}
	 * @return The event data to utilise or null
	 */
	public @Nullable I passThroughMiddleware(@NotNull final I eventData);
	
	public void addMiddleware(final @NotNull IEventMiddleware<I> middleware);
	/**
	 * Invokes all event handlers in order
	 * 
	 * @param eventData The data that will be passed through all event handlers
	 * @return A runnable that informs all {@link EventPriority#WATCH}s about the event
	 */
	public @NotNull Runnable travelRoute(final @NotNull I eventData);
	/**
	 * Adds a stop to this route
	 * 
	 * @param priority The {@link EventPriority} to register under
	 * @param handler The {@link IEventHandler} that is being registered
	 * @param force If to override any pre-existing {@link IEventHandler}s (Only used when {@link EventPriority#isSingleton()} returns true on the provided priority)
	 */
	public void addStop(final @Nullable EventPriority priority, final @NotNull IEventHandler<I> eventHandler, final @Nullable Boolean force);
	
	public void removeStop(final @NotNull IEventHandler<I> eventHandler);
}
