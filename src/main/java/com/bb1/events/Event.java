package com.bb1.events;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.bb1.registry.IRegisterable;
import com.bb1.registry.IRegistry;

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
public class Event<I> implements IRegisterable<String> {
	
	public static final @NotNull IRegistry<String, Event<?>> EVENT_REGISTRY = new EventRegistry();
	
	public static final void addEventRegisterHandler(@NotNull final BiConsumer<String, Event<?>> handler) {
		((EventRegistry)EVENT_REGISTRY).addRegisterHandler(handler); // done with casting as the registry used for events may change in the future
	}
	
	private final @NotNull Set<Consumer<I>> handlers = Collections.newSetFromMap(new ConcurrentHashMap<Consumer<I>, Boolean>());
	private @Nullable Function<I, Object[]> decomposer;
	private @NotNull String name = null;
	
	public Event() { }
	
	public Event(@NotNull final String name) {
		this.register(name);
	}
	
	public Event(@NotNull final String name, @NotNull final Function<I, Object[]> decomposer) {
		this.register(name);
		this.decomposer = decomposer;
	}
	
	public void addHandler(@NotNull Consumer<I> handler) {
		this.handlers.add(handler);
	}
	
	public void emit(@NotNull I input) {
		this.handlers.forEach((handler)->handler.accept(input));
	}
	
	public final @NotNull String getName() {
		if (this.name == null) throw new IllegalStateException("The name has not been set yet! Please register the event with #register(Ljava/lang/String;)V");
		return this.name;
	}
	
	public final @NotNull Object[] decompose(@NotNull final I given) {
		return this.decomposer == null ? new Object[] { given } : this.decomposer.apply(given);
	}
	
	public final void setDecomposer(@NotNull final Function<I, Object[]> decomposer) {
		this.decomposer = decomposer;
	}
	
	@Override
	public void register(@Nullable final String name) {
		if (this.name != null) throw new IllegalStateException("This event is already registered!");
		if (name == null) throw new IllegalArgumentException("Invalid argument: 'name' cannot be null!");
		this.name = name;
		EVENT_REGISTRY.register(this.name, this);
	}
	
}
