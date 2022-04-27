package com.bb1.events;

import java.util.Collections;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

import org.jetbrains.annotations.NotNull;

import com.bb1.registry.SimpleRegistry;

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
 * A simple registry to contain all events
 * 
 * @author BradBot_1
 */
final class EventRegistry extends SimpleRegistry<String, Event<?>> {
	
	private final @NotNull Set<BiConsumer<String, Event<?>>> registerHandlers = Collections.newSetFromMap(new ConcurrentHashMap<BiConsumer<String, Event<?>>, Boolean>());
	
	public final void addRegisterHandler(@NotNull final BiConsumer<String, Event<?>> handler) {
		this.registerHandlers.add(handler);
		for (final Entry<String, Event<?>> entry : this.map.entrySet()) {
			handler.accept(entry.getKey(), entry.getValue());
		}
	}
	
	@Override
	public void onRegister(@NotNull final String identifier, @NotNull final Event<?> registree) {
		super.onRegister(identifier, registree);
		for (final BiConsumer<String, Event<?>> consumer : this.registerHandlers) {
			consumer.accept(identifier, registree);
		}
	}
	
	
	
	
}
