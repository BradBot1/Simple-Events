package fun.bb1.events.abstraction;

import java.util.UUID;

import org.jetbrains.annotations.NotNull;

import fun.bb1.events.bus.EventBus;
import fun.bb1.events.handler.IEventHandler;
import fun.bb1.objects.defineables.ICancellable;

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
 * An abstracted form of event that can be cancelled
 * 
 * @apiNote This does force any handlers registered with {@link #addHandler(IEventHandler)} to respect cancelled events by any added directly to the {@link EventBus} must respect it itself
 * 
 * @author BradBot_1
 */
public class CancellableEvent<I extends ICancellable> extends Event<I> {
	
	public CancellableEvent(@NotNull final Class<I> typeClass) {
		this(typeClass, UUID.randomUUID().toString()); // default to random name
	}
	
	public CancellableEvent(@NotNull final Class<I> typeClass, @NotNull final String name) {
		this(typeClass, name, EventBus.DEFAULT_BUS);
	}
	
	public CancellableEvent(@NotNull final Class<I> typeClass, @NotNull final String name, @NotNull final EventBus bus) {
		super(typeClass, name, bus);
	}
	
	@Override
	public void addHandler(@NotNull IEventHandler<I> eventHandler) {
		super.addHandler((eventData) -> {
			if (eventData.isCancelled()) return;
			eventHandler.handleEvent(eventData);
		});
	}
	
}
