package com.marlontrujillo.eru.gui.toolbars.user;

import com.marlontrujillo.eru.gui.toolbars.tag.TagGroup;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.image.ImageView;

import java.util.Date;

/**
 * FXML Controller class
 *
 * @author mtrujillo
 */
public class UsersToolbarController extends TreeTableView<TagGroup> {

    private TabPane externalTabPane;

    /* ********** Constructor ********** */
    public UsersToolbarController(TabPane externalTabPane) {
        TreeTableColumn<TagGroup, String> nameColumn = new TreeTableColumn<>("Name");
        TreeTableColumn<TagGroup, Long> sizeColumn   = new TreeTableColumn<>("Size");
        TreeTableColumn<TagGroup, Date> modifiedColumn = new TreeTableColumn<>("Modified");

        nameColumn.setCellValueFactory(param -> param.getValue().getValue().nameProperty());
        sizeColumn.setCellValueFactory(param -> param.getValue().getValue().sizeProperty());
        modifiedColumn.setCellValueFactory(param -> param.getValue().getValue().lastModifiedProperty());

        this.setRoot(createTree(new TagGroup("Groups")));
    }

    private TreeItem<TagGroup> createTree(TagGroup tagGroup) {
        TreeItem<TagGroup> item = new TreeItem<>(tagGroup);
        if (item.getValue().getChilds().isEmpty()){
            item.setGraphic(new ImageView(getClass().getResource("folder.png").toExternalForm()));
        } else {
            for (TagGroup t : tagGroup.getChilds()) {
                item.getChildren().add(createTree(t));
            }
            item.setGraphic(new ImageView(getClass().getResource("tag.png").toExternalForm()));
        }

        return item;
    }

}
