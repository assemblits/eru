package org.assemblits.eru.comm.bus;

import lombok.Data;
import org.assemblits.eru.comm.actors.Context;
import org.assemblits.eru.comm.actors.Director;
import org.assemblits.eru.entities.Device;
import org.assemblits.eru.jfx.links.GenericLinker;
import org.assemblits.eru.jfx.links.Linker;
import org.assemblits.eru.jfx.links.LinksContainer;
import org.springframework.stereotype.Component;

import java.util.function.BiConsumer;

/**
 * Created by marlontrujillo1080 on 10/16/17.
 */
@Component
@Data
public class Fieldbus {
    private final Director director;
    private final LinksContainer<Device> deviceLinks;

    public void startDirector(){
        if (director.isAlive()) return;
        director.setDaemon(true);
        director.setName("Fieldbus communications director");
        director.start();
    }

    public void stopDirector(){
        director.getContexts().forEach(Context::stop);
        director.getContexts().clear();
    }

    public void add(Device device, Context context){
        context.setTarget(device);
        BiConsumer<Director, Context> startCommunication = (d, c) -> d.getContexts().add(c);
        BiConsumer<Director, Context> stopCommunication = (d, c) -> {c.stop(); d.getContexts().remove(c);};
        GenericLinker<Director, Context> linker = new GenericLinker<>(director, context, startCommunication, stopCommunication);
        linker.link();
        deviceLinks.addLink(device, linker);
    }

    public void remove(Device device){
        deviceLinks.getLinksOf(device).forEach(Linker::unlink);
        deviceLinks.removeAllLinks(device);
    }

    public boolean contains(Device device) {
        return (deviceLinks.getLinksOf(device) != null) && (!deviceLinks.getLinksOf(device).isEmpty());
    }
}
