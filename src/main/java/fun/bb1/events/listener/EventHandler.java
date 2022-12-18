package fun.bb1.events.listener;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

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
 * Used to declare a method as an event handler
 * 
 * @author BradBot_1
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface EventHandler {
	/**
	 * Declares the name of the event that should be binded to
	 * 
	 * @apiNote This should be <i>exactly</i> the same (case sensitive) as the one used to register the event
	 * 
	 * @return The name of the event to bind to
	 */
	public String value();
	/**
	 * Used to designate an event handler as required, only use when the handler is integral to the use case!
	 * 
	 * @apiNote This will cause an {@link IllegalStateException} if the event fails to bind
	 * 
	 * @return If the event handler is required
	 */
	public boolean required() default false;
	/**
	 * TODO: think of a good way to explain this
	 * 
	 * @apiNote Not all events will support this!
	 * 
	 * @return If the arguments should be split from an array to individual arguments
	 */
	public boolean decompose() default false;
	
}
