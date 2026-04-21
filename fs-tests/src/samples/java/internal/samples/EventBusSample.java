package internal.samples;

import space.sunqian.fs.utils.eventbus.SimpleEventBus;

/**
 * Sample: Event Bus Usage
 * <p>
 * Purpose: Demonstrate how to use the event bus utilities provided by fs-core module.
 * <p>
 * Use Cases:
 * <ul>
 *   <li>
 *     Publish and subscribe to events
 *   </li>
 *   <li>
 *     Handle events asynchronously
 *   </li>
 * </ul>
 * <p>
 * Key Classes:
 * <ul>
 *   <li>
 *     {@link SimpleEventBus}: The main event bus interface
 *   </li>
 * </ul>
 */
public class EventBusSample {

    public static void main(String[] args) {
        // Create event bus
        SimpleEventBus eventBus = SimpleEventBus.newEventBus();

        // Register String event subscriber
        eventBus.register(String.class, event -> {
            System.out.println("Received String event: " + event);
        });

        // Register Integer event subscriber
        eventBus.register(Integer.class, event -> {
            System.out.println("Received Integer event: " + event);
        });

        // Publish events
        System.out.println("Publishing String event...");
        eventBus.post("Hello, Event Bus!");

        System.out.println("Publishing Integer event...");
        eventBus.post(42);
    }
}