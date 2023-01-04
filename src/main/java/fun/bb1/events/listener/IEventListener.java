package fun.bb1.events.listener;


import static fun.bb1.exceptions.handler.ExceptionHandler.handle;
import static fun.bb1.reflection.MethodUtils.getInheritedMethodsWithAnnotation;
import static fun.bb1.reflection.MethodUtils.invokeMethod;

import java.lang.reflect.Method;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import fun.bb1.events.abstraction.Event;

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
	
	static final @NotNull Method DECOMPOSE_METHOD = handle(()->Event.class.getMethod("decompose", Object.class));
	
	public default void register(@Nullable String name) {
		for (final Method method : getInheritedMethodsWithAnnotation(this.getClass(), EventHandler.class, null)) {
			method.canAccess(true); // ensure we can invoke the method
			final EventHandler handler = method.getAnnotation(EventHandler.class);
			final Event<?> event = null; // TODO: this
			if (event==null) { // the event cannot be found
				if (handler.required()) { // we need to throw an exception as this handler is required
					throw new IllegalStateException("The required event handler '" + method.getName() + "' cannot bind to '" + handler.value() + "' as it cannot be found. Is the event registered?");
				}
				continue; // go onto the next event
			}
			if (handler.decompose()) {
				event.addHandler((givenEventObject) -> invokeMethod(method, this, invokeMethod(DECOMPOSE_METHOD, event, givenEventObject)));
			} else {
				event.addHandler((givenEventObject) -> invokeMethod(method, event, givenEventObject));
			}
		}
	}
	
}
