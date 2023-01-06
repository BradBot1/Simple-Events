package fun.bb1.events.abstraction.listener;


import java.lang.reflect.InaccessibleObjectException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.logging.Logger;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import fun.bb1.events.bus.EventBus;

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
 * Used to allow a class to register its own functions to handle events
 * 
 * @author BradBot_1
 */
public interface IEventListener {
		
	public default void register() {
		this.register(EventBus.DEFAULT_BUS);
	}
	
	public default void register(final @NotNull EventBus defaultBus) {
		final Logger logger = Logger.getLogger("EventListener | " + defaultBus.hashCode());
		for (final Method method : this.getClass().getMethods()) {
			if (!method.isAnnotationPresent(EventHandler.class)) continue;
			final EventHandler handler = method.getAnnotation(EventHandler.class);
			if (method.getParameters().length <= 0) {
				logger.warning("Skipping \"" + handler.value() + "\" as method has no parameters");
				continue;
			}
			try {
				method.setAccessible(true); // ensure we can invoke the method
			} catch (InaccessibleObjectException | SecurityException e) {
				logger.warning("Skipping \"" + handler.value() + "\" as method is inaccessible");
				continue;
			}
			if (handler.value() == null || handler.value().equals("")) {
				logger.warning("Skipping \"" + handler.value() + "\" as value is invalid");
				continue;
			}
			for (final Parameter parameters : method.getParameters()) {
				initStatics(parameters.getType());
			}
			EventBus bus = defaultBus;
			if (handler.busToUse() == null || handler.busToUse().equals("")) {
				logger.warning("Ignoring \"" + handler.value() + "\" as busToUse is invalid, using default");
			} else {
				final String[] pathToBus = handler.busToUse().split("#", 2);
				try {
					bus = (EventBus) Class.forName(pathToBus[0]).getField(pathToBus[1]).get(null);
				} catch (ClassCastException t) {
					logger.warning("Ignoring \"" + handler.value() + "\" as busToUse is not an instance of EventBus, using default");
				} catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException | IllegalArgumentException t) {
					logger.warning("Ignoring \"" + handler.value() + "\" as busToUse cannot be found, using default");
				} 
			}
			bus.subscribe(handler.value(), handler.priority(), (eventData)->{
				try {
					method.invoke(this, eventData);
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					e.printStackTrace();
				}
			});
		}
	}
	
	private static <T> @Nullable Class<T> initStatics(final @NotNull Class<T> clazz) {
		try {
			Class.forName(clazz.getName(), true, clazz.getClassLoader());
		} catch (ClassNotFoundException e) {
			throw new IllegalArgumentException("Exception should not be reachable", e);
		}
		return clazz;
	}
	
}
