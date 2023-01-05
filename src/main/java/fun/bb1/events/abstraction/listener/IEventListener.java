package fun.bb1.events.abstraction.listener;


import static fun.bb1.reflection.MethodUtils.getInheritedMethodsWithAnnotation;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.jetbrains.annotations.NotNull;

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
		for (final Method method : getInheritedMethodsWithAnnotation(this.getClass(), EventHandler.class, null)) {
			method.canAccess(true); // ensure we can invoke the method
			final EventHandler handler = method.getAnnotation(EventHandler.class);
			if (handler.value() == null || handler.value().equals("")) {
				// TODO: inform user
				continue;
			}
			EventBus bus = defaultBus;
			if (handler.busToUse() == null || handler.busToUse().equals("")) {
				// TODO: inform user
			} else {
				final String[] pathToBus = handler.busToUse().split("#", 2);
				try {
					bus = (EventBus) Class.forName(pathToBus[0]).getField(pathToBus[0]).get(null);
				} catch (ClassCastException t) {
					// TODO: inform user
				} catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException | IllegalArgumentException t) {
					// TODO: inform user
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
	
}
