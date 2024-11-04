package xyz.sunqian.common.base;

/**
 * This interface represents starter to build and start a {@link Thread}.
 *
 * @author fredsuvn
 */
public abstract class ThreadStarter implements BaseStarter<Thread, ThreadStarter> {

    static ThreadStarter newInstance() {
        return new OfJdk8();
    }

    private static long threadInitNumber;

    private static synchronized long nextThreadNum() {
        return threadInitNumber++;
    }

    private String name;
    private int priority;
    private boolean daemon;
    private Runnable runnable;
    private ThreadGroup group;
    private ClassLoader contextClassLoader;
    private long stackSize;

    ThreadStarter() {
        reset();
    }

    /**
     * Sets thread name.
     *
     * @param name thread name
     * @return this
     */
    public ThreadStarter name(String name) {
        this.name = name;
        return this;
    }

    /**
     * Sets thread priority, in [{@link Thread#MIN_PRIORITY}, {@link Thread#MAX_PRIORITY}].
     *
     * @param priority thread priority
     * @return this
     */
    public ThreadStarter priority(int priority) {
        this.priority = priority;
        return this;
    }

    /**
     * Sets whether the thread is daemon.
     *
     * @param daemon whether the thread is daemon
     * @return this
     */
    public ThreadStarter daemon(boolean daemon) {
        this.daemon = daemon;
        return this;
    }

    /**
     * Sets the target runnable object which will be invoked when the thread is started. The {@code target} is same with
     * target parameter of {@link Thread#Thread(Runnable)}.
     *
     * @param runnable target runnable object
     * @return this
     */
    public ThreadStarter runnable(Runnable runnable) {
        this.runnable = runnable;
        return this;
    }

    /**
     * Sets context class loader.
     *
     * @param contextClassLoader context class loader
     * @return this
     */
    public ThreadStarter contextClassLoader(ClassLoader contextClassLoader) {
        this.contextClassLoader = contextClassLoader;
        return this;
    }

    /**
     * Sets thread group.
     *
     * @param group thread group
     * @return this
     */
    public ThreadStarter group(ThreadGroup group) {
        this.group = group;
        return this;
    }

    /**
     * Sets stack size.
     *
     * @param stackSize stack size
     * @return this
     */
    public ThreadStarter stackSize(long stackSize) {
        this.stackSize = stackSize;
        return this;
    }

    @Override
    public ThreadStarter reset() {
        this.name = null;
        this.priority = -1;
        this.daemon = false;
        this.runnable = null;
        this.group = null;
        this.contextClassLoader = null;
        this.stackSize = -1;
        return this;
    }

    /**
     * Builds and starts a new {@link Thread}.
     *
     * @return a new {@link Thread}
     */
    @Override
    public Thread start() {
        String threadName = name == null ? JieThread.class.getSimpleName() + nextThreadNum() : name;
        JieThread thread;
        if (stackSize != -1) {
            thread = new JieThread(group, runnable, threadName, stackSize);
        } else {
            thread = new JieThread(group, runnable, threadName);
        }
        if (priority != -1) {
            thread.setPriority(priority);
        }
        thread.setDaemon(daemon);
        if (contextClassLoader != null) {
            thread.setContextClassLoader(contextClassLoader);
        }
        return thread;
    }

    private static final class JieThread extends Thread {
        public JieThread(ThreadGroup group, Runnable target, String name) {
            super(group, target, name);
        }

        public JieThread(ThreadGroup group, Runnable target, String name, long stackSize) {
            super(group, target, name, stackSize);
        }
    }

    private static final class OfJdk8 extends ThreadStarter {
    }
}
