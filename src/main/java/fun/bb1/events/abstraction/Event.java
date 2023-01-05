package fun.bb1.events.abstraction;

import java.util.UUID;
import java.util.logging.Logger;

import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;

import fun.bb1.events.bus.EventBus;
import fun.bb1.events.handler.IEventHandler;

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
 * An abstracted form of event
 * 
 * @author BradBot_1
 */
public class Event<I> {
	
	protected @NotNull final Class<I> typeClass;
	protected @NotNull final String name;
	protected @NotNull Logger logger;
	protected @NotNull EventBus bus = EventBus.DEFAULT_BUS;
	
	public Event(@NotNull final Class<I> typeClass) {
		this(typeClass, UUID.randomUUID().toString()); // default to random name
	}
	
	public Event(@NotNull final Class<I> typeClass, @NotNull final String name) {
		this(typeClass, name, EventBus.DEFAULT_BUS);
	}
	
	public Event(@NotNull final Class<I> typeClass, @NotNull final String name, @NotNull final EventBus bus) {
		this.name = name;
		this.typeClass = typeClass;
		this.setBus(bus);
	}
	
	@Internal
	public final void setBus(@NotNull final EventBus bus) {
		this.bus = bus;
		this.logger = Logger.getLogger("Event | " + bus.hashCode() + " | " + this.name);
		this.bus.publishRoute(this.name, this.typeClass);
	}
	
	public final @NotNull String getName() {
		return this.name;
	}
	
	public void addHandler(final @NotNull IEventHandler<I> eventHandler) {
		this.bus.subscribe(this.name, eventHandler);
	}
	
	public void emit(final @NotNull I eventData) {
		this.bus.recievePassengerAndInformWatchers(this.name, eventData);
	}
	
}
