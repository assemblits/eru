package org.assemblits.eru.jfx.links;

import org.springframework.context.annotation.Scope;
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
@Scope(value = "prototype")
public class LinksContainer<T> {

    private Map<T, List<Linker>> links;

    public LinksContainer() {
        this.links = new HashMap<>();
    }

    public void addLink(T target, Linker linker){
        if (!links.containsKey(target)) {
            links.put(target, new ArrayList<>());
        }
        links.get(target).add(linker);
    }

    public void removeLink(T target, Linker linker){
        if(links.containsKey(target)){
            links.get(target).remove(linker);
        }
    }

    public void removeAllLinks(T target){
        if(links.containsKey(target)){
            links.get(target).clear();
        }
    }

    public List<Linker> getLinksOf(T target){
        return links.getOrDefault(target, null);
    }


}
