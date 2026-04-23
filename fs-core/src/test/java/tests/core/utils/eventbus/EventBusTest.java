package tests.core.utils.eventbus;

import org.junit.jupiter.api.Test;
import space.sunqian.fs.base.value.IntVar;
import space.sunqian.fs.base.value.Var;
import space.sunqian.fs.collect.ListKit;
import space.sunqian.fs.collect.MapKit;
import space.sunqian.fs.utils.eventbus.EventBusException;
import space.sunqian.fs.utils.eventbus.SimpleEventBus;

import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class EventBusTest {

    @Test
    public void testEventBusWithIntegerEvents() throws Exception {
        SimpleEventBus eventBus = SimpleEventBus.newEventBus();
        IntVar iCounter = IntVar.of(0);

        Consumer<Integer> iConsumer1 = integer -> {
            if (integer.equals(3)) {
                throw new EventBusException();
            }
            iCounter.add(integer);
        };
        Consumer<Integer> iConsumer2 = iCounter::add;

        eventBus.register(Integer.class, iConsumer1);
        eventBus.register(Integer.class, iConsumer2);

        eventBus.post(1);
        assertEquals(2, iCounter.get());
        eventBus.post(2);
        assertEquals(6, iCounter.get());
    }

    @Test
    public void testEventBusWithExceptionHandling() throws Exception {
        SimpleEventBus eventBus = SimpleEventBus.newEventBus();
        IntVar iCounter = IntVar.of(0);

        Consumer<Integer> iConsumer1 = integer -> {
            if (integer.equals(3)) {
                throw new EventBusException();
            }
            iCounter.add(integer);
        };
        Consumer<Integer> iConsumer2 = iCounter::add;

        eventBus.register(Integer.class, iConsumer1);
        eventBus.register(Integer.class, iConsumer2);

        assertThrows(EventBusException.class, () -> eventBus.post(3));
    }

    @Test
    public void testEventBusWithChainDispatchMode() throws Exception {
        SimpleEventBus eventBus = SimpleEventBus.newEventBus();
        IntVar iCounter = IntVar.of(0);

        Consumer<Integer> iConsumer1 = integer -> {
            if (integer.equals(3)) {
                throw new EventBusException();
            }
            iCounter.add(integer);
        };
        Consumer<Integer> iConsumer2 = iCounter::add;

        eventBus.register(Integer.class, iConsumer1);
        eventBus.register(Integer.class, iConsumer2);

        eventBus.post(1, SimpleEventBus.DispatchMode.CHAIN);
        assertEquals(2, iCounter.get());
        eventBus.post(3, SimpleEventBus.DispatchMode.CHAIN);
        assertEquals(2, iCounter.get());
    }

    @Test
    public void testEventBusWithStringEvents() throws Exception {
        SimpleEventBus eventBus = SimpleEventBus.newEventBus();
        Var<String> strVar = Var.of("");

        Consumer<String> strConsumer = strVar::set;
        eventBus.register(MapKit.map(
            String.class, ListKit.list(strConsumer)
        ));

        eventBus.post("666");
        assertEquals("666", strVar.get());
        eventBus.post("999");
        assertEquals("999", strVar.get());
    }

    @Test
    public void testEventBusWithUnregister() throws Exception {
        SimpleEventBus eventBus = SimpleEventBus.newEventBus();
        IntVar iCounter = IntVar.of(0);
        Var<String> strVar = Var.of("");

        Consumer<Integer> iConsumer1 = integer -> {
            if (integer.equals(3)) {
                throw new EventBusException();
            }
            iCounter.add(integer);
        };
        Consumer<Integer> iConsumer2 = iCounter::add;
        Consumer<String> strConsumer = strVar::set;

        eventBus.register(Integer.class, iConsumer1);
        eventBus.register(Integer.class, iConsumer2);
        eventBus.register(MapKit.map(String.class, ListKit.list(strConsumer)));

        // Unregister iConsumer1
        eventBus.unregister(iConsumer1);
        eventBus.post(3);
        assertEquals(3, iCounter.get());
        eventBus.post(3, SimpleEventBus.DispatchMode.CHAIN);
        assertEquals(6, iCounter.get());

        // Unregister all consumers
        eventBus.unregister(ListKit.list(iConsumer2, strConsumer));
        eventBus.post(3);
        eventBus.post("666");
        eventBus.post(8L);
        assertEquals(6, iCounter.get());
        assertEquals("", strVar.get());
    }

    @Test
    public void testExceptions() {
        {
            // EventBusException
            assertThrows(EventBusException.class, () -> {
                throw new EventBusException();
            });
            assertThrows(EventBusException.class, () -> {
                throw new EventBusException("");
            });
            assertThrows(EventBusException.class, () -> {
                throw new EventBusException("", new RuntimeException());
            });
            assertThrows(EventBusException.class, () -> {
                throw new EventBusException(new RuntimeException());
            });
        }
    }
}
