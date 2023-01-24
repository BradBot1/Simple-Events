package fun.bb1.events.tests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import fun.bb1.events.abstraction.CancellableEvent;
import fun.bb1.events.abstraction.Event;
import fun.bb1.events.handler.IEventHandler;
import fun.bb1.objects.container.Container;
import fun.bb1.objects.defineables.ICancellable;

public class EventTest {
	
	public static class ClassEvent extends Event<String> {
		
		private static ClassEvent INSTANCE;
		
		public static final ClassEvent getInstance() {
			if (INSTANCE == null) INSTANCE = new ClassEvent();
			return INSTANCE;
		}
		
		private ClassEvent() {
			super(String.class);
		}
		
	}

	@Test
	public void test_class() {
		final ClassEvent instanceEvent = new ClassEvent();
		final String eventMessage = "Funkys!";
		final Container<Boolean> recievedEvent = new Container<Boolean>(false);
		instanceEvent.addHandler(recievedMessage -> {
			assertTrue("Event got incorrect message!", eventMessage.equals(recievedMessage));
			recievedEvent.setContained(true);
		});
		instanceEvent.emit(eventMessage);
		assertTrue("Event was never recieved!", recievedEvent.getContained());
	}
	
	@Test
	public void test_inline_instance() {
		final Event<String> instanceEvent = new Event<>(String.class);
		final String eventMessage = "Funky!";
		final Container<Boolean> recievedEvent = new Container<Boolean>(false);
		instanceEvent.addHandler(recievedMessage -> {
			assertTrue("Event got incorrect message!", eventMessage.equals(recievedMessage));
			recievedEvent.setContained(true);
		});
		instanceEvent.emit(eventMessage);
		assertTrue("Event was never recieved!", recievedEvent.getContained());
	}
	
	public static class Cancel implements ICancellable {
		
		private boolean cancel;
		
		@Override
		public boolean isCancelled() {
			return this.cancel;
		}

		@Override
		public void cancel() {
			this.cancel = true;
		}
		
	}
	
	@Test
	public void test_cancel() {
		final Event<Cancel> instanceEvent = new CancellableEvent<>(Cancel.class);
		final Container<Boolean> recievedEvent = new Container<Boolean>(false);
		final Cancel eventMessage = new Cancel();
		final IEventHandler<Cancel> handler = recievedMessage -> {
			assertFalse("Event recieved despite being cancelled!", recievedMessage.isCancelled());
			recievedEvent.setContained(true);
			recievedMessage.cancel();
		};
		instanceEvent.addHandler(handler);
		instanceEvent.addHandler(handler);
		instanceEvent.emit(eventMessage);
		assertTrue("Event was never recieved!", recievedEvent.getContained());
		assertTrue("Event was not cancelled", eventMessage.isCancelled());
	}

}
