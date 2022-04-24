package com.bb1.events;

import java.util.Collections;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

import org.jetbrains.annotations.NotNull;

import com.bb1.registry.SimpleRegistry;

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
