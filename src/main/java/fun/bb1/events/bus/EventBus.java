package fun.bb1.events.bus;

import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import fun.bb1.events.handler.EventPriority;
import fun.bb1.events.handler.IEventHandler;
import fun.bb1.events.middleware.IEventMiddleware;
import fun.bb1.events.store.EventHandlerStoreImpl;
import fun.bb1.objects.annotations.DisallowsEmptyString;

/**
 * 
 * Copyright 2023 BradBot_1
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at

 * http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * Passes events onto their respective handlers
 * 
 * @author BradBot_1
 */
public final class EventBus {
	
	public static final @NotNull EventBus DEFAULT_BUS = new EventBus();
	
	private final @NotNull Map<String, IEventRoute<?>> routes = new ConcurrentHashMap<String, IEventRoute<?>>();
	private final @NotNull Logger logger = Logger.getLogger("EventBus | " + this.hashCode());
	
	public <I> void publishRoute(@NotNull @DisallowsEmptyString final String eventName, @NotNull final Class<I> clazz) {
		this.publishRoute(eventName, new EventRouteImpl<I>(new HashSet<IEventMiddleware<I>>(), new EventHandlerStoreImpl<I>(null), clazz));
	}
	
	@Internal
	public void publishRoute(@NotNull @DisallowsEmptyString final String eventName, @NotNull final IEventRoute<?> route) {
		this.routes.putIfAbsent(eventName, route);
	}
	/**
	 * Forwards to {@link #subscribe(String, EventPriority, IEventHandler, boolean, Class)} with priority set to {@link EventPriority#DEFAULT}, force set to false and clazz set to null
	 * 
	 * @param <I> The event data type
	 * @param eventName The name of the event
	 * @param handler The {@link IEventHandler} that is forming a subscription
	 */
	public <I> void subscribe(@NotNull @DisallowsEmptyString final String eventName, @NotNull final IEventHandler<I> handler) {
		this.subscribe(eventName, EventPriority.DEFAULT, handler, false, null);
	}
	/**
	 * Forwards to {@link #subscribe(String, EventPriority, IEventHandler, boolean, Class)} with priority set to {@link EventPriority#DEFAULT} and force set to false
	 * 
	 * @param <I> The event data type
	 * @param eventName The name of the event
	 * @param handler The {@link IEventHandler} that is forming a subscription
	 * @param clazz The {@link Class} of the type (can be null)
	 */
	public <I> void subscribe(@NotNull @DisallowsEmptyString final String eventName, @NotNull final IEventHandler<I> handler, @Nullable final Class<I> clazz) {
		this.subscribe(eventName, EventPriority.DEFAULT, handler, false, clazz);
	}
	/**
	 * Forwards to {@link #subscribe(String, EventPriority, IEventHandler, boolean, Class)} with force set to false and clazz set to null
	 * 
	 * @param <I> The event data type
	 * @param eventName The name of the event
	 * @param priority The priority to register the event under
	 * @param handler The {@link IEventHandler} that is forming a subscription
	 */
	public <I> void subscribe(@NotNull @DisallowsEmptyString final String eventName, @NotNull final EventPriority priority, @NotNull final IEventHandler<I> handler) {
		this.subscribe(eventName, priority, handler, false, null);
	}
	/**
	 * Forwards to {@link #subscribe(String, EventPriority, IEventHandler, boolean, Class)} with force set to false
	 * 
	 * @param <I> The event data type
	 * @param eventName The name of the event
	 * @param priority The priority to register the event under
	 * @param handler The {@link IEventHandler} that is forming a subscription
	 * @param clazz The {@link Class} of the type (can be null)
	 */
	public <I> void subscribe(@NotNull @DisallowsEmptyString final String eventName, @NotNull final EventPriority priority, @NotNull final IEventHandler<I> handler, @Nullable final Class<I> clazz) {
		this.subscribe(eventName, priority, handler, false, clazz);
	}
	/**
	 * Forwards to {@link #subscribe(String, EventPriority, IEventHandler, boolean, Class)} with clazz set to null
	 * 
	 * @param <I> The event data type
	 * @param eventName The name of the event
	 * @param priority The priority to register the event under
	 * @param handler The {@link IEventHandler} that is forming a subscription
	 * @param force If to override any pre-existing {@link IEventHandler}s (Only used when {@link EventPriority#isSingleton()} returns true on the provided priority)
	 */
	public <I> void subscribe(@NotNull @DisallowsEmptyString final String eventName, @NotNull final EventPriority priority, final @NotNull IEventHandler<I> handler, final boolean force) {
		this.subscribe(eventName, priority, handler, force, null);
	}
	/**
	 * Subscribes the provided {@link IEventHandler} up to the requested event
	 * 
	 * @param <I> The event data type
	 * @param eventName The name of the event
	 * @param priority The priority to register the event under
	 * @param handler The {@link IEventHandler} that is forming a subscription
	 * @param force If to override any pre-existing {@link IEventHandler}s (Only used when {@link EventPriority#isSingleton()} returns true on the provided priority)
	 * @param clazz The {@link Class} of the type (can be null)
	 * 
	 * @throws IllegalArgumentException If clazz is null and the event is not published
	 * @throws IllegalArgumentException If the {@link IEventHandler} type does not match the {@link IEventRoute} type
	 */
	@SuppressWarnings("unchecked")
	public <I> void subscribe(@NotNull @DisallowsEmptyString final String eventName, @NotNull final EventPriority priority, final @NotNull IEventHandler<I> handler, final boolean force, @Nullable final Class<I> clazz) {
		if (!this.routes.containsKey(eventName)) {
			this.logger.warning("Event handler registered before the event \"" + eventName + "\" was published!");
			if (clazz == null) throw new IllegalArgumentException("Null provided for clazz when event is not published!");
			else this.publishRoute(eventName, clazz);
			this.logger.warning("The event \"" + eventName + "\" has been published to avoid issues, this may lead to conflicts");
		}
		try {
			((IEventRoute<I>) this.routes.get(eventName)).addStop(priority, handler, force);
		} catch (Throwable e) {
			throw new IllegalArgumentException("The EventHandler provided does not match the event \"" + eventName + '"', e);
		}
	}
	
	@SuppressWarnings("unchecked")
	public <I> void unsubscribe(@NotNull @DisallowsEmptyString final String eventName, final @NotNull IEventHandler<I> handler) {
		if (!this.routes.containsKey(eventName)) return;
		try {
			((IEventRoute<I>) this.routes.get(eventName)).removeStop(handler);
		} catch (Throwable e) {
			throw new IllegalArgumentException("The EventHandler provided does not match the event \"" + eventName + '"', e);
		}
	}
	/**
	 * Invokes {@link #recievePassenger(String, Object)} and then invokes the {@link Runnable} that is returned
	 * 
	 * @param <I> The event data type
	 * @param eventName The name of the event 
	 * @param eventData The data of the event
	 */
	public <I> void recievePassengerAndInformWatchers(final @NotNull @DisallowsEmptyString String eventName, final @NotNull I eventData) {
		this.recievePassenger(eventName, eventData).run();
	}
	/**
	 * Emits an event through the appropriate {@link IEventMiddleware} and {@link IEventHandler}s
	 * 
	 * @param <I> The event data type
	 * @param eventName The name of the event 
	 * @param eventData The data of the event
	 * @return A runnable that informs {@link EventPriority#WATCH} of the event, call after event is completely done
	 * @throws IllegalArgumentException If the eventName given has no published event
	 * @throws IllegalArgumentException When the type of the published route and eventData do not match
	 */
	@SuppressWarnings("unchecked")
	public <I> @Nullable Runnable recievePassenger(final @NotNull @DisallowsEmptyString String eventName, final @NotNull I eventData) {
		if (!this.routes.containsKey(eventName)) throw new IllegalArgumentException("The event \"" + eventName + "\" isn't published!");
		try {
			final IEventRoute<I> route = (IEventRoute<I>) this.routes.get(eventName);
			final I alteredEventData = route.passThroughMiddleware(eventData);
			if (alteredEventData == null) {
				this.logger.info("The event \"" + eventName + "\" was cancelled by middleware");
				return null;
			}
			return route.travelRoute(alteredEventData);
		} catch (Throwable e) {
			throw new IllegalArgumentException("The EventHandler provided does not match the event \"" + eventName + '"', e);
		}
	}
	
}
