package org.assemblits.eru.javafx;

import java.util.function.BiConsumer;

/**
 * Created by marlontrujillo1080 on 10/13/17.
 */

public class GenericLinker<O,L> implements Linker {

    private final O deviceToLink;

    private final L linker;

    private final BiConsumer<? super O, ? super L> link;

    private final BiConsumer<? super O, ? super L> unlink;

    private boolean linked;

    public GenericLinker(O deviceToLink, L linker, BiConsumer<? super O, ? super L> link, BiConsumer<? super O, ? super L> unlink) {
        this.deviceToLink = deviceToLink;
        this.linker = linker;
        this.link = link;
        this.unlink = unlink;
    }

    @Override
    public void link() {
        if (linked) return;
        this.link.accept(deviceToLink, linker);
        this.linked = true;
    }

    @Override
    public void unlink() {
        if (!linked) return;
        this.unlink.accept(deviceToLink, linker);
        this.linked = false;
    }

}
