package fun.bb1.events.abstraction;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Logger;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import fun.bb1.events.handler.EventPriority;
import fun.bb1.events.handler.IEventHandler;

/**
 * 
 * Copyright 2022 BradBot_1
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
 * An event
 * 
 * @author BradBot_1
 */
public class Event<I> {
	
	private final @NotNull Map<EventPriority, List<IEventHandler<I>>> handlers = new EnumMap<EventPriority, List<IEventHandler<I>>>(EventPriority.class) {
		private static final long serialVersionUID = 1L;
	{
		for (final EventPriority priority : EventPriority.values()) {
			put(priority, priority.isSingleton() ? List.of() : new ArrayList<IEventHandler<I>>());
		}
	}};
	private @Nullable Function<I, Object[]> decomposer;
	private @NotNull final String name;
	private @NotNull final Logger logger;
	
	public Event() {
		this.name = getClass().getName();
		this.logger = Logger.getLogger("Event | " + (this.name == null ? this.getClass().getName() : this.name));
	}
	
	public Event(@NotNull final String name) {
		this(name, null);
	}
	
	public Event(@NotNull final String name, @Nullable final Function<I, Object[]> decomposer) {
		this.name = name;
		this.decomposer = decomposer;
		this.logger = Logger.getLogger("Event | " + (this.name == null ? this.getClass().getName() : this.name));
	}
	
	public void addHandler(@NotNull final IEventHandler<I> handler) {
		this.addHandler(handler, EventPriority.DEFAULT, false);
	}
	
	public void addHandler(@NotNull final IEventHandler<I> handler, @NotNull final EventPriority priority) {
		this.addHandler(handler, priority, false);
	}
	
	public void addHandler(@NotNull final IEventHandler<I> handler, @NotNull final EventPriority priority, final boolean force) {
		final List<IEventHandler<I>> handlerList = this.handlers.get(priority);
		if (priority.isSingleton()) {
			if (handlerList.isEmpty()) {
				this.handlers.put(priority, List.of(handler));
				return;
			}
			if (force) {
				this.addHandler(handlerList.get(0), priority.getFallbackValue(), false);
				this.handlers.put(priority, List.of(handler));
				this.logger.info("Singleton handler overriden for " + priority.name().toLowerCase() + ", " + handlerList.get(0).getClass().getName() + " => " + handler.getClass().getName());
				return;
			}
			this.addHandler(handler, priority.getFallbackValue(), false);
			this.logger.warning("Failed to install singleton handler for " + handler.getClass().getName());
		}
		
	}
	
	public Runnable emit(@NotNull I input) {
		for (final EventPriority priority : EventPriority.getOrderedArray()) {
			this.handlers.get(priority).forEach((handler) -> {
				handler.handleEvent(input);
			});
		}
		return () -> {
			this.handlers.get(EventPriority.WATCH).forEach((handler) -> {
				handler.handleEvent(input);
			});
		};
	}
	
	public final @NotNull String getName() {
		return this.name;
	}
	
	public final @NotNull Object[] decompose(@NotNull final I given) {
		return this.decomposer == null ? new Object[] { given } : this.decomposer.apply(given);
	}
	
	public final void setDecomposer(@NotNull final Function<I, Object[]> decomposer) {
		this.decomposer = decomposer;
	}
	
}
