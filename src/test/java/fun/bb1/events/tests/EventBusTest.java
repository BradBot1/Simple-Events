package fun.bb1.events.tests;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Ignore;
import org.junit.Test;

import fun.bb1.events.bus.EventBus;
import fun.bb1.events.middleware.MiddlewareResult;
import fun.bb1.objects.container.Container;

public class EventBusTest {
	
	@Test
	public void test_unkown_event_no_type() {
		final String eventName = "66eabd52-1943-4e96-93e2-b9f94d62dd4c";
		assertThrows("Unkown event accepted without default type provided", IllegalArgumentException.class, () -> {
			EventBus.DEFAULT_BUS.subscribe(eventName, (recievedMessage) -> {});
		});
		assertThrows("Unkown middleware accepted without default type provided", IllegalArgumentException.class, () -> {
			EventBus.DEFAULT_BUS.addMiddleware(eventName, (recievedMessage) -> new MiddlewareResult<>(true, recievedMessage));
		});
	}
	
	@Test
	public void test_unkown_event_provided_type() {
		final String eventName = "76bda618-3358-4395-b49b-7a5483bac320";
		final double eventMessage = 1.4d;
		final Container<Boolean> recievedEvent = new Container<>(false);
		EventBus.DEFAULT_BUS.subscribe(eventName, (recievedMessage) -> {
			assertTrue("Expected message was not recieved", eventMessage == recievedMessage);
			recievedEvent.setContained(true);
		}, Double.class);
		EventBus.DEFAULT_BUS.recievePassengerAndInformWatchers(eventName, eventMessage);
		assertTrue("Event not executed", recievedEvent.getContained());
	}
	
	@Test
	@Ignore // Disabled as Java's generic system doesn't allow for the type validation that would be required for this
	public void test_type_mix_match() {
		final String eventName = "2e493b6b-7968-4cad-a482-5fb4e0825c44";
		EventBus.DEFAULT_BUS.publishRoute(eventName, String.class);
		assertThrows("Invalid type accepted", IllegalArgumentException.class, () -> {
			EventBus.DEFAULT_BUS.subscribe(eventName, (recievedMessage) -> {}, Integer.class); // pass integer class when string is expected, should throw as miss match
		});
	}
	
	@Test
	public void test_event_passthrough() {
		final String eventName = "2debe9d2-a607-4f0b-8060-f80b62824f15";
		final String eventMessage = "What a funky!!";
		final Container<Boolean> recievedEvent = new Container<>(false);
		EventBus.DEFAULT_BUS.publishRoute(eventName, String.class);
		EventBus.DEFAULT_BUS.subscribe(eventName, (recievedMessage) -> {
			assertTrue("Expected message was not recieved", eventMessage.equals(recievedMessage));
			recievedEvent.setContained(true);
		}, String.class);
		EventBus.DEFAULT_BUS.recievePassengerAndInformWatchers(eventName, eventMessage);
		assertTrue("Event not executed", recievedEvent.getContained());
	}
	
	@Test
	public void test_middleware_edits_value() {
		final String eventName = "06f7a46c-80fd-4448-b38b-8d2186530fe2";
		final char sentEventMessage = 'a';
		final char modifiedEventMessage = 'b';
		final Container<Boolean> recievedEvent = new Container<>(false);
		EventBus.DEFAULT_BUS.publishRoute(eventName, Character.class);
		EventBus.DEFAULT_BUS.subscribe(eventName, (recievedMessage) -> {
			assertTrue("Event message was not modified", sentEventMessage != recievedMessage);
			assertTrue("Event message was modified incorrectly", modifiedEventMessage == recievedMessage);
			recievedEvent.setContained(true);
		}, Character.class);
		EventBus.DEFAULT_BUS.addMiddleware(eventName, (c) -> new MiddlewareResult<>(true, modifiedEventMessage), Character.class);
		EventBus.DEFAULT_BUS.recievePassengerAndInformWatchers(eventName, sentEventMessage);
		assertTrue("Event failed to execute", recievedEvent.getContained());
	}
	
	@Test
	public void test_middleware_cancel_event() {
		final String eventName = "bd528264-49d5-46b9-9cdc-d547a0bf04fb";
		final Container<Boolean> recievedEvent = new Container<>(false);
		EventBus.DEFAULT_BUS.publishRoute(eventName, Boolean.class);
		EventBus.DEFAULT_BUS.subscribe(eventName, (recievedMessage) -> fail("Event executed"), Character.class);
		EventBus.DEFAULT_BUS.addMiddleware(eventName, (c) -> {
			recievedEvent.setContained(true);
			return new MiddlewareResult<>(false, null);
		}, Boolean.class);
		assertNull("Event was not stopped by middleware", EventBus.DEFAULT_BUS.recievePassenger(eventName, true));
		assertTrue("Event was not recieved by middleware", recievedEvent.getContained());
	}

}
