# Simple-Events [![License](https://img.shields.io/github/license/BradBot1/Simple-Events.svg)](https://github.com/BradBot1/Simple-Events/blob/master/LICENSE) [![Build_Status](https://img.shields.io/github/actions/workflow/status/BradBot1/Simple-Events/maven.yml)](https://github.com/BradBot1/Simple-Events/actions) [![Offical_Repo_Version](https://img.shields.io/maven-metadata/v?metadataUrl=https%3A%2F%2Frepo.bb1.fun%2Freleases%2Ffun%2Fbb1%2Fsimple-events%2Fmaven-metadata.xml)](https://repo.bb1.fun/#/releases/fun/bb1/simple-events)
A simple event library for Java

## Usage

Simple Events utilises a simple [EventBus](https://github.com/BradBot1/Simple-Events/blob/master/src/main/java/fun/bb1/events/bus/EventBus.java) approach as a base for all event management.
Anything found inside the [abstraction package](https://github.com/BradBot1/Simple-Events/tree/master/src/main/java/fun/bb1/events/abstraction) will invoke calls to an [event bus](https://github.com/BradBot1/Simple-Events/blob/master/src/main/java/fun/bb1/events/bus/EventBus.java) for registry.
For instance, an event registered via an [Instance Event](https://github.com/BradBot1/Simple-Events/blob/master/src/main/java/fun/bb1/events/abstraction/Event.java) will still be accessible via the [EventBus](https://github.com/BradBot1/Simple-Events/blob/master/src/main/java/fun/bb1/events/bus/EventBus.java).

### The basics of the EventBus

Before an event can be sent it must be published so that the route it set up for it ahead of time.
The main way to do this is via the [EventBus](https://github.com/BradBot1/Simple-Events/blob/master/src/main/java/fun/bb1/events/bus/EventBus.java) by providing a name and event type. The event type is the class the event wishes to pass along.

```java
EventBus.DEFAULT_BUS.publishRoute("ExampleEvent", String.class);
```
In this snippet we create an event named `ExampleEvent` with the event type `String`

If we wanted to push a message along this event we need to inform the route of a passenger, we do this by proving the event name for the event we want to emit and the message for the event.
The message *MUST* be of the event type published!

When the [EventBus](https://github.com/BradBot1/Simple-Events/blob/master/src/main/java/fun/bb1/events/bus/EventBus.java) recieves a passenger it will be passed through [middleware](#Middleware) first.
The middleware are able to block the event from executing, if they do null will be returned.
However, if the event goes ahead the return will be a Runnable that invokes any [WATCH level priority](https://github.com/BradBot1/Simple-Events/blob/master/src/main/java/fun/bb1/events/handler/EventPriority.java), this runnable should only be called after the events actions have occured, you can also not call the watchers if you so choose.

```java
Runnable informWatchers = EventBus.DEFAULT_BUS.recievePassenger("ExampleEvent", "This is the event message!");
if (informWatchers != null) informWatchers.run(); // Can be null!
```

If you don't want to manually invoke run and check type saftey you can simply utilise `#recievePassengerAndInformWatchers()V`

```java
EventBus.DEFAULT_BUS.recievePassengerAndInformWatchers("ExampleEvent", "This is the event message!"); // Will perform the same code as the snippet above !
```

To register an event on the [EventBus](https://github.com/BradBot1/Simple-Events/blob/master/src/main/java/fun/bb1/events/bus/EventBus.java) you can invoke the `#subscribe()V` method.
It takes the event's name, an [EventHandler](https://github.com/BradBot1/Simple-Events/blob/master/src/main/java/fun/bb1/events/handler/IEventHandler.java) and the event type (optional but recommended).

```java
EventBus.DEFAULT_BUS.subscribe("ExampleEvent", (recievedMessage) -> {
  // Do something with the event message here!
}, String.class); // This reference to String.class IS optional, but it's best practice to include it for readability
```

### Middleware

[Middleware](https://github.com/BradBot1/Simple-Events/blob/master/src/main/java/fun/bb1/events/middleware/IEventMiddleware.java) is simular to an [EventHandler](https://github.com/BradBot1/Simple-Events/blob/master/src/main/java/fun/bb1/events/handler/IEventHandler.java), however, it recieves the event message first and is able to make modifications to it or outright block it.

So then, when is [Middleware](https://github.com/BradBot1/Simple-Events/blob/master/src/main/java/fun/bb1/events/middleware/IEventMiddleware.java) usefull?<br>
It is best to utilise it when you need to preform some processing on all events such as authenticating that the event is allowed to be proccessed.
It should *NOT* be used to handle an event early! Use the proper [priority](#EventPriority) for that!

To register a [Middleware](https://github.com/BradBot1/Simple-Events/blob/master/src/main/java/fun/bb1/events/middleware/IEventMiddleware.java) you can do as follows:

```java
EventBus.DEFAULT_BUS.addMiddleware("ExampleEvent", (c) -> {
  if (c.contains("a")) return new MiddlewareResult<>(false, null); // If the message contains the character 'a', block the event
  if (c.contains("b")) return new MiddlewareResult<>(true, c.replaceAll("b", "*")); // If the message contains the character 'b', replace it with an asterisk
  return new MiddlewareResult<>(true, c); // Elsewise, do nothing
}, String.class); // Like the EventHandler, this is optional but it's best to add it anyway
```
In this snippet we add middleware that blocks any message containing the character 'a' and replaces all characters 'b' with '\*'

All [Middleware](https://github.com/BradBot1/Simple-Events/blob/master/src/main/java/fun/bb1/events/middleware/IEventMiddleware.java) must return a [MiddlewareResult](https://github.com/BradBot1/Simple-Events/blob/master/src/main/java/fun/bb1/events/middleware/MiddlewareResult.java) that defines if the event should be blocked and what message to pass on.

### EventPriority

When registering an [EventHandler](https://github.com/BradBot1/Simple-Events/blob/master/src/main/java/fun/bb1/events/handler/IEventHandler.java) you can specify the [EventPriority](https://github.com/BradBot1/Simple-Events/blob/master/src/main/java/fun/bb1/events/handler/EventPriority.java) that you want it to assume.
The order of these events is as [specified in the ordered array](https://github.com/BradBot1/Simple-Events/blob/c88b6849d99c055f5378bdfe36dc291578cea6b0/src/main/java/fun/bb1/events/handler/EventPriority.java#L63).

Some [EventPriority](https://github.com/BradBot1/Simple-Events/blob/master/src/main/java/fun/bb1/events/handler/EventPriority.java)'s such as [First](https://github.com/BradBot1/Simple-Events/blob/c88b6849d99c055f5378bdfe36dc291578cea6b0/src/main/java/fun/bb1/events/handler/EventPriority.java#L31) and [Last](https://github.com/BradBot1/Simple-Events/blob/c88b6849d99c055f5378bdfe36dc291578cea6b0/src/main/java/fun/bb1/events/handler/EventPriority.java#L32) cannot have more than one [EventHandler](https://github.com/BradBot1/Simple-Events/blob/master/src/main/java/fun/bb1/events/handler/IEventHandler.java) registered to them.
If another [EventHandler](https://github.com/BradBot1/Simple-Events/blob/master/src/main/java/fun/bb1/events/handler/IEventHandler.java) is registered under the aforementioned priority anyway, it will be forced into the [fallback priority](https://github.com/BradBot1/Simple-Events/blob/c88b6849d99c055f5378bdfe36dc291578cea6b0/src/main/java/fun/bb1/events/handler/EventPriority.java#L51) that it has set.
You can overcome this by setting `force` to true, this will instead set the existing [EventHandler](https://github.com/BradBot1/Simple-Events/blob/master/src/main/java/fun/bb1/events/handler/IEventHandler.java) to the [fallback priority](https://github.com/BradBot1/Simple-Events/blob/c88b6849d99c055f5378bdfe36dc291578cea6b0/src/main/java/fun/bb1/events/handler/EventPriority.java#L51).

```java
EventBus.DEFAULT_BUS.subscribe("ExampleEvent", EventPriority.FIRST, (recievedMessage) -> {
  // This will run FIRST
}, String.class);
EventBus.DEFAULT_BUS.subscribe("ExampleEvent", EventPriority.FIRST, (recievedMessage) -> {
  // This will replace the current FIRST EventHandler and run FIRST
  // So the old FIRST will now be set to HIGH
}, true, String.class);
EventBus.DEFAULT_BUS.subscribe("ExampleEvent", EventPriority.FIRST, (recievedMessage) -> {
  // This will fallback to HIGH as FIRST is occupied
}, String.class);
```

### Listeners

If you don't want to create events but instead just listen to them you can utilise the [listener class](https://github.com/BradBot1/Simple-Events/blob/master/src/main/java/fun/bb1/events/abstraction/listener/IEventListener.java) that is provided by the [abstraction package](https://github.com/BradBot1/Simple-Events/tree/master/src/main/java/fun/bb1/events/abstraction)!

To use it do as follows:

```java
public class Listener implements IEventListener {

  @EventHandler("ExampleEvent") // Put the events name here
  public void handle(final String message) {
    // Do something with the message
  }

  @EventHandler(value = "ExampleEvent", priority = EventPriority.LAST) // You can event set priority, but this is not needed!
  public void handle_with_priority(final String message) {
    // Do something with the message
  }

}
```

Once you have created your [listener](https://github.com/BradBot1/Simple-Events/blob/master/src/main/java/fun/bb1/events/abstraction/listener/IEventListener.java) make sure to invoke it's `#register()V` method, elsewise your [EventHandlers](https://github.com/BradBot1/Simple-Events/blob/master/src/main/java/fun/bb1/events/handler/IEventHandler.java) will not be registered!

### EventInstances

If you want an object to represent you event you can! By utilising the [Event](https://github.com/BradBot1/Simple-Events/blob/master/src/main/java/fun/bb1/events/abstraction/Event.java) from the [abstraction package](https://github.com/BradBot1/Simple-Events/tree/master/src/main/java/fun/bb1/events/abstraction).

Any time an event is created this way it is also provided to the event

```java
final Event<String> instanceEvent = new Event<>(String.class, "ExampleEvent");
```
In the snippet above an event with the name "ExampleEvent" is initilised, just as you would with the [EventBus](https://github.com/BradBot1/Simple-Events/blob/master/src/main/java/fun/bb1/events/bus/EventBus.java).

You can then use this object, performing `#emit()V` to invoke an event and `#addHandler()V` to register an [EventHandler](https://github.com/BradBot1/Simple-Events/blob/master/src/main/java/fun/bb1/events/handler/IEventHandler.java)

```java
final Event<String> instanceEvent = ...;
instanceEvent.addHandler(recievedMessage -> {
  // Do something with the message
});
instanceEvent.emit(eventMessage); // Calls recievePassengerAndInformWatchers on the event bus
```

## Installation

Simple Events is available on Maven from either the [Official Maven Repository](https://repo.bb1.fun/#/releases/fun/bb1/simple-events) or [JitPack](https://jitpack.io/#BradBot1/Simple-Events/master-SNAPSHOT)

### Official Repository

The latest version is hosted on an [Official Maven Repository](https://repo.bb1.fun/#/releases/fun/bb1/simple-events)

First include the repository:

```xml
<repository>
  <id>bb1-repository-releases</id>
  <name>BradBot_1's Repository</name>
  <url>https://repo.bb1.fun/releases</url>
</repository>
```

Then add the dependency:

```xml
<dependency>
  <groupId>fun.bb1</groupId>
  <artifactId>simple-events</artifactId>
  <version>2.0.1</version>
</dependency>
```

### Jitpack

If the official repository is down or you choose not to trust it you can always pull it from [JitPack](https://jitpack.io/#BradBot1/Simple-Events/master-SNAPSHOT)

First include the repository:

```xml
<repository>
  <id>jitpack.io</id>
  <url>https://jitpack.io</url>
</repository>
```

Then add the dependency:

```xml
<dependency>
  <groupId>com.github.BradBot1</groupId>
  <artifactId>Simple-Events</artifactId>
  <version>master-c88b6849d9-1</version>
</dependency>
```

### Locally installing it

If you wish to store it locally you can!

First, in an empty folder, execute `git clone https://github.com/BradBot1/Simple-Events.git`<br>
Once it has gathered a local copy execute `cd Simple-Events` to navigate into the directory<br>
Finally execute `mvn clean install` to install it to your local [Maven Repoistory](https://www.javatpoint.com/maven-repository)

Now you can simply add the following dependency without a repository:

```xml
<dependency>
  <groupId>fun.bb1</groupId>
  <artifactId>simple-events</artifactId>
  <version>2.0.1</version>
</dependency>
```
