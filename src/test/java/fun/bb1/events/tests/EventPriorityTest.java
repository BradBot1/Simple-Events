package fun.bb1.events.tests;

import static org.junit.Assert.*;

import org.junit.Test;

import fun.bb1.events.bus.EventBus;
import fun.bb1.events.handler.EventPriority;
import fun.bb1.objects.container.Container;

public class EventPriorityTest {

	@Test
	public void test_first() {
		final String eventName = "0d2cb492-2f11-447d-bc08-5416462999a4";
		final Container<Boolean> recievedEvent = new Container<>(false);
		EventBus.DEFAULT_BUS.publishRoute(eventName, String.class);
		EventBus.DEFAULT_BUS.subscribe(eventName, EventPriority.FIRST, (recievedMessage) -> {
			assertFalse("Event was not recieved in correct order", recievedEvent.getContained());
			recievedEvent.setContained(true);
		}, String.class);
		EventBus.DEFAULT_BUS.subscribe(eventName, (recievedMessage) -> {
			assertTrue("Event was not recieved in correct order", recievedEvent.getContained());
			recievedEvent.setContained(true);
		}, String.class);
		
		EventBus.DEFAULT_BUS.recievePassengerAndInformWatchers(eventName, "");
		assertTrue("Event not executed", recievedEvent.getContained());
	}
	
	@Test
	public void test_first_fallback() {
		final String eventName = "425933c8-503c-4845-8dbe-144cf1de13aa";
		final Container<Boolean> recievedEvent = new Container<>(false);
		EventBus.DEFAULT_BUS.publishRoute(eventName, String.class);
		EventBus.DEFAULT_BUS.subscribe(eventName, EventPriority.FIRST, (recievedMessage) -> {
			assertFalse("Event was not recieved in correct order", recievedEvent.getContained());
			recievedEvent.setContained(true);
		}, String.class);
		EventBus.DEFAULT_BUS.subscribe(eventName, EventPriority.FIRST, (recievedMessage) -> {
			assertTrue("Event did not fallback to high", recievedEvent.getContained());
			recievedEvent.setContained(true);
		}, String.class);
		EventBus.DEFAULT_BUS.recievePassengerAndInformWatchers(eventName, "");
		assertTrue("Event not executed", recievedEvent.getContained());
	}
	
	@Test
	public void test_first_fallback_force() {
		final String eventName = "fe6e5253-0f9e-43f6-94d9-e60054d1c3d7";
		final Container<Boolean> recievedEvent = new Container<>(false);
		EventBus.DEFAULT_BUS.publishRoute(eventName, String.class);
		EventBus.DEFAULT_BUS.subscribe(eventName, EventPriority.FIRST, (recievedMessage) -> {
			assertTrue("Event was not forced to fallback value", recievedEvent.getContained());
			recievedEvent.setContained(true);
		}, String.class);
		EventBus.DEFAULT_BUS.subscribe(eventName, EventPriority.FIRST, (recievedMessage) -> {
			assertFalse("Event was not forced to first", recievedEvent.getContained());
			recievedEvent.setContained(true);
		}, true, String.class);
		EventBus.DEFAULT_BUS.recievePassengerAndInformWatchers(eventName, "");
		assertTrue("Event not executed", recievedEvent.getContained());
	}
	
	@Test
	public void test_last() {
		final String eventName = "9d04703e-66d7-4e5b-b010-b067736c5955";
		final Container<Boolean> recievedEvent = new Container<>(false);
		EventBus.DEFAULT_BUS.publishRoute(eventName, String.class);
		EventBus.DEFAULT_BUS.subscribe(eventName, EventPriority.LAST, (recievedMessage) -> {
			assertTrue("Event was not recieved in correct order", recievedEvent.getContained());
			recievedEvent.setContained(true);
		}, String.class);
		EventBus.DEFAULT_BUS.subscribe(eventName, (recievedMessage) -> {
			assertFalse("Event was not recieved in correct order", recievedEvent.getContained());
			recievedEvent.setContained(true);
		}, String.class);
		
		EventBus.DEFAULT_BUS.recievePassengerAndInformWatchers(eventName, "");
		assertTrue("Event not executed", recievedEvent.getContained());
	}
	
	@Test
	public void test_last_fallback() {
		final String eventName = "4d5039cf-03f3-4765-8c4d-43aff3c7b65c";
		final Container<Boolean> recievedEvent = new Container<>(false);
		EventBus.DEFAULT_BUS.publishRoute(eventName, String.class);
		EventBus.DEFAULT_BUS.subscribe(eventName, EventPriority.LAST, (recievedMessage) -> {
			assertTrue("Event was not recieved in correct order", recievedEvent.getContained());
			recievedEvent.setContained(true);
		}, String.class);
		EventBus.DEFAULT_BUS.subscribe(eventName, EventPriority.LAST, (recievedMessage) -> {
			assertFalse("Event did not fallback to low", recievedEvent.getContained());
			recievedEvent.setContained(true);
		}, String.class);
		EventBus.DEFAULT_BUS.recievePassengerAndInformWatchers(eventName, "");
		assertTrue("Event not executed", recievedEvent.getContained());
	}
	
	@Test
	public void test_last_fallback_force() {
		final String eventName = "6af25ab9-9812-4220-9a05-06ef193d4186";
		final Container<Boolean> recievedEvent = new Container<>(false);
		EventBus.DEFAULT_BUS.publishRoute(eventName, String.class);
		EventBus.DEFAULT_BUS.subscribe(eventName, EventPriority.LAST, (recievedMessage) -> {
			assertFalse("Event was not forced to fallback value", recievedEvent.getContained());
			recievedEvent.setContained(true);
		}, String.class);
		EventBus.DEFAULT_BUS.subscribe(eventName, EventPriority.LAST, (recievedMessage) -> {
			assertTrue("Event was not forced to last", recievedEvent.getContained());
			recievedEvent.setContained(true);
		}, true, String.class);
		EventBus.DEFAULT_BUS.recievePassengerAndInformWatchers(eventName, "");
		assertTrue("Event not executed", recievedEvent.getContained());
	}

}
