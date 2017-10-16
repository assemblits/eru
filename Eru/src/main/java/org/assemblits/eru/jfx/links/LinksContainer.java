package org.assemblits.eru.jfx.links;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by marlontrujillo1080 on 10/14/17.
 * One T can have many linkers...
 */
@Component
public class LinksContainer {

    private Map<Object, List<Linker>> links;

    public LinksContainer() {
        this.links = new HashMap<>();
    }

    public void addLink(Object target, Linker linker){
        if (!links.containsKey(target)) {
            links.put(target, new ArrayList<>());
        }
        links.get(target).add(linker);
    }

    public void removeLink(Object target, Linker linker){
        if(links.containsKey(target)){
            links.get(target).remove(linker);
        }
    }

    public void removeAllLinks(Object target){
        if(links.containsKey(target)){
            links.get(target).clear();
        }
    }

    public List<Linker> getLinksOf(Object target){
        return links.getOrDefault(target, null);
    }

    public boolean containsLinksFor(Object target) {
        return links.containsKey(target);
    }

    @Override
    public String toString() {
        return "LinksContainer{" +
                "links=" + links +
                '}';
    }
}
