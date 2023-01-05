package fun.bb1.events.handler;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
 * The order that the event is dispatched in, where highest gets the event first
 * 
 * @author BradBot_1
 */
public enum EventPriority {
	HIGH,
	DEFAULT,
	LOW,
	FIRST(HIGH),
	LAST(LOW),
	/**
	 * @apiNote Use when you just wish to see the end result of an event
	 */
	WATCH,
	;
	/**
	 * The fallback value that is used when a priority is already occuipied
	 */
	private final @Nullable EventPriority fallbackValue;
	
	private EventPriority() {
		this.fallbackValue = null;
	}
	
	private EventPriority(final @NotNull EventPriority fallbackValue) {
		this.fallbackValue = fallbackValue;
	}
	
	public final @NotNull EventPriority getFallbackValue() {
		if (this.fallbackValue == null) return DEFAULT;
		return this.fallbackValue;
	}
	/**
	 * @return If only one {@link IEventHandler} can be registered to this priority
	 */
	public final boolean isSingleton() {
		return this.fallbackValue != null;
	}
	
	public static final EventPriority[] getOrderedArray() {
		return new EventPriority[] { FIRST, HIGH, DEFAULT, LOW, LAST };
	}
	
}
