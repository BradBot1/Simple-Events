package fun.bb1.events.tests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import fun.bb1.events.abstraction.listener.EventHandler;
import fun.bb1.events.abstraction.listener.IEventListener;
import fun.bb1.events.bus.EventBus;
import fun.bb1.events.handler.EventPriority;
import fun.bb1.objects.container.Container;

public class ListenerTest {
	
	private static final String EVENT_NAME = "5e19a7e5-2064-4a29-b785-5bf061d1ced3";
	
	public static class Listener implements IEventListener {
		
		@EventHandler(value = EVENT_NAME)
		public void handle(final Container<Boolean> message) {
			assertFalse("Event did not occur in the correct order", message.getContained());
			message.setContained(true);
		}
		
		@EventHandler(value = EVENT_NAME, priority = EventPriority.LAST)
		public void handle2(final Container<Boolean> message) {
			assertTrue("Event did not occur in the correct order", message.getContained());
			message.setContained(true);
		}
		
	}
	
	@Before
	public void setUp() {
		EventBus.DEFAULT_BUS.publishRoute(EVENT_NAME, Container.class);
	}
	
	@Test
	public void test_listener() {
		new Listener().register();
		final Container<Boolean> container = new Container<Boolean>(false);
		EventBus.DEFAULT_BUS.recievePassengerAndInformWatchers(EVENT_NAME, container);
		assertTrue("Event did not recieve message!", container.getContained());
	}

}
