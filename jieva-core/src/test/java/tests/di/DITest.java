package tests.di;

import org.testng.annotations.Test;
import xyz.sunqian.common.di.SimpleApp;

import javax.annotation.Resource;

import static org.testng.Assert.assertEquals;

public class DITest {

    @Test
    public void testDI() {
        SimpleApp app = SimpleApp.newBuilder()
            .resources(Starter.class, ServiceAaa.class, ServiceBbb.class)
            .aspect(true)
            .build();
        ServiceAaa serviceAaa = app.getResource(ServiceAaa.class);
        assertEquals(serviceAaa.getLocalName(), "A");
        assertEquals(serviceAaa.getRemoteName(), "B");
        ServiceBbb serviceBbb = app.getResource(ServiceBbb.class);
        assertEquals(serviceBbb.getLocalName(), "B");
        assertEquals(serviceBbb.getRemoteName(), "A");
        Starter starter = app.getResource(Starter.class);
        assertEquals(starter.getNames(), "AB");
    }

    public static class Starter {

        @Resource
        private ServiceAaa serviceAaa;
        @Resource
        private ServiceBbb serviceBbb;

        public String getNames() {
            return serviceAaa.getLocalName() + serviceBbb.getLocalName();
        }
    }

    public static class ServiceAaa {

        @Resource
        private ServiceBbb serviceBbb;

        public String getLocalName() {
            return "A";
        }

        public String getRemoteName() {
            return serviceBbb.getLocalName();
        }
    }

    public static class ServiceBbb {

        @Resource
        private ServiceAaa serviceAaa;

        public String getLocalName() {
            return "B";
        }

        public String getRemoteName() {
            return serviceAaa.getLocalName();
        }
    }

    //public static class
}
