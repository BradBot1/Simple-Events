package fun.bb1.events.middleware;

import org.jetbrains.annotations.NotNull;

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
 * Middleware that is ran just before an event gets handled
 * 
 * @apiNote Modifying the eventObject is allowed
 * @apiNote Stopping the event is allowed
 * 
 * @author BradBot_1
 */
public interface IEventMiddleware<I> {
	
	static <I> @NotNull MiddlewareResult<I> fail() {
		return new MiddlewareResult<I>(false, null);
	}
	
	static <I> @NotNull MiddlewareResult<I> pass(@NotNull final I eventObject) {
		return new MiddlewareResult<I>(true, eventObject);
	}
	
	public MiddlewareResult<I> handle(@NotNull final I eventObject);
	
}
