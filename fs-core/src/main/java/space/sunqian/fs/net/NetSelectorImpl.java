package space.sunqian.fs.net;

import space.sunqian.annotation.Nonnull;
import space.sunqian.fs.Fs;

import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Set;

final class NetSelectorImpl implements NetSelector {

    private static final int EMPTY_SELECT_THRESHOLD = 512;

    private volatile @Nonnull Selector selector;
    private int emptySelectCount = 0;

    NetSelectorImpl() throws NetException {
        this.selector = Fs.uncheck(Selector::open, NetException::new);
    }

    @Override
    public @Nonnull Selector selector() {
        return selector;
    }

    @Override
    public int select(long timeout) throws NetException {
        return Fs.uncheck(() -> {
            int keysNum = selector.select(timeout);
            if (keysNum == 0) {
                emptySelectCount++;
                if (emptySelectCount >= EMPTY_SELECT_THRESHOLD) {
                    rebuildSelector();
                    emptySelectCount = 0;
                }
            } else {
                emptySelectCount = 0;
            }
            return keysNum;
        }, NetException::new);
    }

    @Override
    public @Nonnull Set<SelectionKey> selectedKeys() throws NetException {
        return Fs.uncheck(selector::selectedKeys, NetException::new);
    }

    @Override
    public @Nonnull Set<SelectionKey> keys() throws NetException {
        return Fs.uncheck(selector::keys, NetException::new);
    }

    @Override
    public void wakeup() {
        selector.wakeup();
    }

    @Override
    public void close() throws NetException {
        Fs.uncheck(selector::close, NetException::new);
    }

    @Override
    public void cancel(@Nonnull SelectableChannel channel) throws NetException {
        Fs.uncheck(() -> {
            SelectionKey key = channel.keyFor(selector);
            if (key != null) {
                key.cancel();
            }
        }, NetException::new);
    }

    private void rebuildSelector() throws Exception {
        Selector newSelector = Selector.open();
        for (SelectionKey key : selector.keys()) {
            if (key.isValid()) {
                @SuppressWarnings("resource")
                SelectableChannel channel = key.channel();
                channel.register(newSelector, key.interestOps(), key.attachment());
            }
        }
        selector.close();
        selector = newSelector;
    }
}