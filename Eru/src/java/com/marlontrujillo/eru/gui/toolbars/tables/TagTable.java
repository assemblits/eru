package com.marlontrujillo.eru.gui.toolbars.tables;

import com.marlontrujillo.eru.tag.Tag;
import javafx.beans.property.StringProperty;

import java.util.List;

/**
 * Created by mtrujillo on 8/9/17.
 */
public class TagTable extends EruTable<Tag> {

    public TagTable(List<Tag> items) {
        super(items);
    }

    @Override
    public void addNewItem() {

    }

    @Override
    public void setTextToFilter(StringProperty textToFilter) {

    }
}
