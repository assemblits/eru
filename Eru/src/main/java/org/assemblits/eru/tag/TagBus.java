package org.assemblits.eru.tag;

import javafx.beans.property.ObjectProperty;
import lombok.Data;
import org.assemblits.eru.entities.Tag;
import org.assemblits.eru.jfx.links.InvalidObservableLinker;
import org.assemblits.eru.jfx.links.Linker;
import org.assemblits.eru.jfx.links.LinksContainer;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;

/**
 * Created by marlontrujillo1080 on 10/16/17.
 */
@Component
@Data
public class TagBus {

    private final LinksContainer linksContainer;

    public void add(Tag tag) {
        ObjectProperty<Timestamp> observable = tag.getLinkedAddress().timestampProperty();
        TagAddressInvalidationListener listener = new TagAddressInvalidationListener(tag);
        InvalidObservableLinker linker = new InvalidObservableLinker(observable, listener);
        linksContainer.addLink(tag, linker);
    }

    public void remove(Tag tag) {
        linksContainer.getLinksOf(tag).forEach(Linker::unlink);
        linksContainer.removeAllLinks(tag);
    }
}
