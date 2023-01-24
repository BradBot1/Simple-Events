package fun.bb1.events.tests;

import static org.junit.Assert.*;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import fun.bb1.events.abstraction.bukkit.HandlerListImpl;
import fun.bb1.events.abstraction.bukkit.IHandlerList;
import fun.bb1.events.abstraction.bukkit.StaticEvent;
import fun.bb1.events.bus.EventBus;
import fun.bb1.objects.container.Container;

public class StaticEventTest {
	
	public static class Event extends StaticEvent<Event> {
		
		private static final IHandlerList<Event> HANDLERS = new HandlerListImpl<StaticEventTest.Event>(Event.class);
		
		private String message;
		
		public Event(final String message) {
			this.message = message;
		}
		
		public String getMessage() {
			return this.message;
		}
		
		public void setMessage(final String message) {
			this.message = message;
		}
		
		@Override
		public @NotNull IHandlerList<Event> getHandlers() {
			return HANDLERS;
		}
		
	}

	@Test
	public void test() {
		final Event toEmit = new Event("a");
		final Container<Boolean> recievedEvent = new Container<>(false);
		Event.HANDLERS.register((e) -> {
			recievedEvent.setContained(true);
		});
		EventBus.DEFAULT_BUS.recievePassengerAndInformWatchers(toEmit.getEventName(), toEmit);
		assertTrue("Event not executed", recievedEvent.getContained());
	}

}
