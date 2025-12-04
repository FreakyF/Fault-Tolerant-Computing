package com.fudala.wrr;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.converter.IntegerStringConverter;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class LoadBalancerApplication extends Application {

    private LoadBalancerServer loadBalancerServer;
    private ObservableList<BackendServer> backendServers;
    private TableView<BackendServer> tableView;
    private Label statusLabel;

    @Override
    public void start(Stage stage) throws Exception {
        List<BackendServer> servers = new ArrayList<>();
        servers.add(new SimpleBackendServer("Backend A", 5));
        servers.add(new SimpleBackendServer("Backend B", 3));
        servers.add(new SimpleBackendServer("Backend C", 2));

        backendServers = FXCollections.observableArrayList(servers);
        loadBalancerServer = new LoadBalancerServer(new InetSocketAddress("localhost", 8080), backendServers);
        loadBalancerServer.start();

        tableView = createTableView();

        VBox centerCard = new VBox(tableView);
        centerCard.setFillWidth(true);
        centerCard.getStyleClass().add("card");

        statusLabel = new Label("HTTP server running on http://localhost:8080");
        statusLabel.getStyleClass().add("status-label");

        Button loadTestButton = new Button("Run Load Test");
        loadTestButton.getStyleClass().add("primary-button");
        loadTestButton.setOnAction(event -> {
            event.consume();
            runLoadTest();
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox bottomBar = new HBox(16, statusLabel, spacer, loadTestButton);
        bottomBar.setAlignment(Pos.CENTER_LEFT);
        bottomBar.setPadding(new Insets(12, 24, 16, 24));
        bottomBar.getStyleClass().add("bottom-bar");
        bottomBar.setMinHeight(56);
        bottomBar.setPrefHeight(56);

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(24, 24, 0, 24));

        Label title = new Label("Weighted Round Robin Load Balancer");
        title.getStyleClass().add("title-label");
        BorderPane.setMargin(title, new Insets(0, 0, 16, 0));

        root.setTop(title);
        root.setCenter(centerCard);
        root.setBottom(bottomBar);

        Scene scene = new Scene(root, 840, 420);
        var stylesheetUrl = Objects.requireNonNull(
                getClass().getResource("/com/fudala/wrr/styles.css")
        );
        scene.getStylesheets().add(stylesheetUrl.toExternalForm());

        stage.setTitle("Load Balancer Dashboard");
        stage.setScene(scene);
        stage.show();

        startAutoRefresh();
    }

    @Override
    public void stop() {
        if (loadBalancerServer != null) {
            loadBalancerServer.stop();
        }
        Platform.exit();
    }

    private TableView<BackendServer> createTableView() {
        TableView<BackendServer> table = new TableView<>();
        table.setEditable(true);
        table.setItems(backendServers);
        table.getStyleClass().add("metrics-table");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        TableColumn<BackendServer, String> idColumn = new TableColumn<>("Backend");
        idColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getId()));
        idColumn.setMinWidth(150);
        idColumn.setPrefWidth(200);
        idColumn.setMaxWidth(1f * Integer.MAX_VALUE);
        idColumn.setReorderable(false);

        TableColumn<BackendServer, Integer> weightColumn = getBackendServerIntegerTableColumn(table);

        TableColumn<BackendServer, Long> servedColumn = new TableColumn<>("Served Requests");
        servedColumn.setCellValueFactory(data ->
                new SimpleLongProperty(data.getValue().getServedCount()).asObject()
        );
        servedColumn.setMinWidth(170);
        servedColumn.setPrefWidth(180);
        servedColumn.setMaxWidth(180);
        servedColumn.setReorderable(false);

        table.getColumns().add(idColumn);
        table.getColumns().add(weightColumn);
        table.getColumns().add(servedColumn);

        return table;
    }

    private TableColumn<BackendServer, Integer> getBackendServerIntegerTableColumn(TableView<BackendServer> table) {
        TableColumn<BackendServer, Integer> weightColumn = new TableColumn<>("Weight");
        weightColumn.setCellValueFactory(data ->
                new SimpleIntegerProperty(data.getValue().getWeight()).asObject()
        );
        weightColumn.setCellFactory(
                TextFieldTableCell.forTableColumn(new IntegerStringConverter())
        );
        weightColumn.setOnEditCommit(event -> {
            BackendServer backend = event.getRowValue();
            int newWeight = event.getNewValue();
            if (newWeight <= 0) {
                table.refresh();
                return;
            }
            loadBalancerServer.getLoadBalancer().updateWeight(backend.getId(), newWeight);
            table.refresh();
        });
        weightColumn.setMinWidth(100);
        weightColumn.setPrefWidth(110);
        weightColumn.setMaxWidth(110);
        weightColumn.setReorderable(false);
        return weightColumn;
    }

    private void startAutoRefresh() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(500), event -> {
            event.consume();
            if (tableView.getEditingCell() == null) {
                tableView.refresh();
            }
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    private void runLoadTest() {
        statusLabel.setText("Running load test...");
        Thread worker = new Thread(() -> {
            try {
                LoadTest.runWithLoadBalancer(loadBalancerServer.getLoadBalancer());
                Platform.runLater(() -> statusLabel.setText("Load test completed"));
            } catch (Exception e) {
                Platform.runLater(() -> statusLabel.setText("Load test failed: " + e.getMessage()));
            }
        });
        worker.setDaemon(true);
        worker.start();
    }

    @SuppressWarnings("unused")
    static void main(String[] args) {
        launch(args);
    }
}
