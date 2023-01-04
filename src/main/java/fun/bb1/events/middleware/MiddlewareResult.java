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
 * The result of a call to {@link IEventMiddleware#handle(Object)}
 * 
 * @author BradBot_1
 */
public record MiddlewareResult<I>(/**If the event should be handed on to the next {@link IEventMiddleware}*/boolean passOn, /**The object that will be passed on*/@NotNull I eventObject) {
	
}
