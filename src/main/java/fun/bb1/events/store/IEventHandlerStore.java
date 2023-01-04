package fun.bb1.events.store;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import fun.bb1.events.handler.EventPriority;
import fun.bb1.events.handler.IEventHandler;
import fun.bb1.events.listener.EventHandler;
import fun.bb1.objects.annotations.Reassignable;
import fun.bb1.objects.container.Container;

public interface IEventHandlerStore<I> {
	
	@Reassignable
	public static @NotNull Container<Logger> DEFAULT_LOGGER = new Container<Logger>(Logger.getLogger("EventHandlerStore"));
	/**
	 * Forwards to {@link #register(EventPriority, IEventHandler, boolean)} with priority set to {@link EventPriority#DEFAULT} and force set to false
	 * 
	 * @param handler The {@link EventHandler} that is being registered
	 */
	public default void register(@NotNull final IEventHandler<I> handler) {
		this.register(EventPriority.DEFAULT, handler, false);
	}
	/**
	 * Forwards to {@link #register(EventPriority, IEventHandler, boolean)} with force set to false
	 * 
	 * @param priority The {@link EventPriority} to register under
	 * @param handler The {@link EventHandler} that is being registered
	 */
	public default void register(@NotNull final EventPriority priority, @NotNull final IEventHandler<I> handler) {
		this.register(priority, handler, false);
	}
	/**
	 * Registers the provided {@link EventHandler} under the specified {@link EventPriority}
	 * 
	 * @param priority The {@link EventPriority} to register under
	 * @param handler The {@link EventHandler} that is being registered
	 * @param force If to override any pre-existing {@link EventHandler}s (Only used when {@link EventPriority#isSingleton()} returns true on the provided priority)
	 */
	public void register(@NotNull final EventPriority priority, @NotNull final IEventHandler<I> handler, final boolean force);
	/**
	 * Gets and returns all {@link EventHandler}s in order (as defined by {@link EventPriority#getOrderedArray()})
	 * 
	 * @apiNote Excludes {@link EventPriority#WATCH}
	 * @apiNote Result will be immutable
	 * 
	 * @return The resulting list of all {@link EventHandler}s in order
	 */
	public default @NotNull List<IEventHandler<I>> fetchInOrder() {
		List<IEventHandler<I>> resultList = new ArrayList<IEventHandler<I>>();
		for (final EventPriority priority : EventPriority.getOrderedArray()) {
			resultList.addAll(this.fetchWithPriority(priority));
		}
		return List.copyOf(resultList); // make immutable
	}
	/**
	 * Gets and returns all {@link EventHandler}s that have the specified {@link EventPriority}
	 * 
	 * @apiNote Will return an empty array if there are no {@link EventHandler}s registered
	 * @apiNote Result will be immutable
	 * 
	 * @param eventPriority The {@link EventPriority} of the events to fetch
	 * 
	 * @return All {@link EventHandler}s with the given {@link EventPriority}
	 */
	public @NotNull List<IEventHandler<I>> fetchWithPriority(final @NotNull EventPriority eventPriority);
	/**
	 * 
	 * @apiNote Will return null if one is not registered
	 * 
	 * @return The {@link EventHandler} with the priority {@link EventPriority#FIRST}
	 */
	public default @Nullable IEventHandler<I> fetchFirst() {
		final List<IEventHandler<I>> firstEventHandlerList = this.fetchWithPriority(EventPriority.FIRST);
		return firstEventHandlerList.size() > 0 ? firstEventHandlerList.get(0) : null;
	}
	/**
	 * 
	 * @apiNote Will return null if one is not registered
	 * 
	 * @return The {@link EventHandler} with the priority {@link EventPriority#LAST}
	 */
	public default @Nullable IEventHandler<I> fetchLast() {
		final List<IEventHandler<I>> lastEventHandlerList = this.fetchWithPriority(EventPriority.FIRST);
		return lastEventHandlerList.size() > 0 ? lastEventHandlerList.get(0) : null;
	}
	
}
