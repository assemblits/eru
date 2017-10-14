package org.assemblits.eru.gui.model;

import lombok.Data;
import org.assemblits.eru.entities.*;
import org.assemblits.eru.jfx.links.LinksContainer;
import org.springframework.stereotype.Component;

/**
 * Created by marlontrujillo1080 on 10/14/17.
 */
@Component
@Data
public class ProjectLinks {
    private final LinksContainer<Device> deviceLinksContainer;
    private final LinksContainer<Connection> connectionLinksContainer;
    private final LinksContainer<Tag> tagLinksContainer;
    private final LinksContainer<User> userLinksContainer;
    private final LinksContainer<Display> displayLinksContainer;
}
