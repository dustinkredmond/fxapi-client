package com.dustinredmond.sparkfx.ui;

/*
 *  Copyright 2020  Dustin K. Redmond
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

import com.dustinredmond.sparkfx.ServerContext;
import com.dustinredmond.sparkfx.model.Route;
import com.dustinredmond.sparkfx.ui.custom.CustomAlert;
import com.dustinredmond.sparkfx.ui.custom.CustomGrid;
import com.dustinredmond.sparkfx.ui.custom.CustomStage;
import com.dustinredmond.sparkfx.ui.custom.GroovySyntaxEditor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

/**
 * Class representing the window used to delete Route objects.
 */
public final class RouteEditWindow {

    public void show(final TableView<Route> table) {
        if (!ServerContext.isActive()) {
            CustomAlert.showInfo(
                "The server is not running, "
                    + "please start it before adding/modifying routes");
            return;
        }
        if (table.getSelectionModel().isEmpty()) {
            CustomAlert.showWarning(
                "Please first select a route from the table.");
        } else {
            showEditDialog(table);
        }
    }

    private void showEditDialog(final TableView<Route> table) {
        final Route route = table.getSelectionModel().getSelectedItem();
        CustomStage stage = new CustomStage();
        stage.setTitle("Edit Route");
        CustomGrid grid = new CustomGrid();
        stage.setScene(new Scene(grid));

        int rowIndex = 0;

        TextField tfRoute = new TextField(route.getUrl());
        grid.add(new Label("URL:"), 0, rowIndex);
        grid.add(tfRoute, 1, rowIndex++);

        GroovySyntaxEditor taCode = new GroovySyntaxEditor();
        taCode.setText(route.getCode());
        grid.add(taCode, 0, rowIndex++, 2, 1);
        GridPane.setVgrow(taCode, Priority.ALWAYS);
        GridPane.setHgrow(taCode, Priority.ALWAYS);
        taCode.setPrefWidth(Double.MAX_VALUE);

        Button buttonAdd = new Button("Save Changes");
        buttonAdd.setMinWidth(120);
        buttonAdd.setOnAction(e -> {
            if (!CustomAlert.showConfirmation(REMOVE_PROMPT)) {
                return;
            }
            controller.removeRoute(route);
            if (controller.addRoute(tfRoute, taCode)) {
                taCode.dispose();
                stage.hide();
                RouteWindow.refreshTable();
            }
        });
        grid.add(buttonAdd, 0, rowIndex);

        stage.setMaximized(true);
        stage.show();
    }

    private final RoutesController controller = new RoutesController();
    private static final String REMOVE_PROMPT =
        "In order to modify the route, the current route will have to "
            + "first be removed. Ensure that no users are using the currently "
            + "mapped route. Are you sure you wish to continue?";
}
