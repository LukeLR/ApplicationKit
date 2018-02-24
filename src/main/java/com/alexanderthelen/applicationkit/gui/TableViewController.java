package com.alexanderthelen.applicationkit.gui;

import com.alexanderthelen.applicationkit.Application;
import com.alexanderthelen.applicationkit.database.Column;
import com.alexanderthelen.applicationkit.database.Row;
import com.alexanderthelen.applicationkit.database.Table;
import javafx.collections.ListChangeListener;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Die {@code TabeViewController}-Klasse dient zur Visualisierung einer Tabelle.
 *
 *
 */
public class TableViewController extends ViewController {
    /**
     * View des TableViewControllers.
     */
    @FXML
    protected BorderPane view;
    /**
     * Obere Werkzeugleiste.
     */
    @FXML
    protected ToolBar topToolBar;
    /**
     * Oberes Label.
     */
    @FXML
    protected Label topStatusLabel;
    /**
     * Suchfeld.
     */
    @FXML
    protected TextField searchTextField;
    /**
     * Suchbutton.
     */
    @FXML
    protected Button searchButton;
    /**
     * View der Tabelle.
     */
    @FXML
    protected TableView<Row> tableView;
    /**
     * Untere Werkzeugleiste.
     */
    @FXML
    protected ToolBar bottomToolBar;
    /**
     * Container der Buttons, die der Manipulation der Tabelle dienen.
     */
    @FXML
    protected HBox buttonsView;
    /**
     * Button zum Hinzufügen einer neuen Zeile.
     */
    @FXML
    protected Button addButton;
    /**
     * Button zum Bearbeiten einer ausgewählten Zeile.
     */
    @FXML
    protected Button editButton;
    /**
     * Button zum Löschen einer ausgewählten Zeile.
     */
    @FXML
    protected Button deleteButton;
    /**
     * Unteres Label.
     */
    @FXML
    protected Label bottomStatusLabel;
    /**
     * Button zum Aktualisieren der Tabelle.
     */
    @FXML
    protected Button refreshButton;

    /**
     * {@link Table}-Instanz.
     *
     * Dies ist das Model des TableViewControllers.
     */
    private final Table table;
    /**
     * Ausgewählte Zeile in der Tabelle.
     */
    private Row selectedRow;
    /**
     * Zum TableViewController gehörende Zeilenansicht.
     */
    private RowViewController rowViewController;

    /**
     * Erstellt eine {@code TableViewController}-Instanz mit einem Namen und
     * einer Tabelle.
     *
     * Außerdem wird der View geladen.
     *
     * @param name
     *            Name des TableViewControllers.
     * @param table
     *            Tabelle (Model).
     * @return TableViewController.
     * @throws IOException
     *             wenn der View nicht geladen werden kann.
     */
    public static TableViewController createWithNameAndTable(String name, Table table) throws IOException {
        TableViewController viewController = new TableViewController(name, table);
        viewController.loadView();
        return viewController;
    }

    /**
     * Erstellt eine {@code TableViewController}-Instanz mit einem Namen und
     * einer Tabelle (per {@link #createWithNameAndTable(String, Table)}).
     *
     * Außerdem wird der Standard-View gesetzt.
     *
     * @param name
     *            Name des TableViewControllers.
     * @param table
     *            Tabelle (Model).
     */
    protected TableViewController(String name, Table table) {
        super(name, TableViewController.class.getResource("TableView.fxml"));
        this.table = table;
    }

    /**
     * Initialisiert den TableViewController.
     */
    @Override
    protected void initialize() {
        super.initialize();

        table.getRows().addListener(new ListChangeListener<Row>() {
            @Override
            public void onChanged(Change<? extends Row> c) {
                bottomStatusLabel.setText("Einträge: " + table.getRows().size());
            }
        });

        table.getSortedRows().comparatorProperty().bind(tableView.comparatorProperty());
        tableView.setItems(table.getSortedRows());

        tableView.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> selectedRow = newValue);

        tableView.setRowFactory(param -> {
            TableRow<Row> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty() && selectedRow == row.getItem()) {
                    showEditRowViewController();
                }
            });
            return row;
        });
    }

    /**
     * Filtert die Tabelle nach der Suchanfrage.
     */
    @FXML
    public void searchInTable() {
        try {
            table.setFilter(searchTextField.getText());
            table.fill();
        } catch (SQLException e) {
            presentAlertDialog(Alert.AlertType.ERROR, "Suche nicht möglich!",
                    "Die Suchanfrage konnte nicht ausgeführt werden.", e, ButtonType.OK);
        }
        searchTextField.requestFocus();
    }

    /**
     * Zeigt die Zeilenansicht für das Hinzufügen einer neuen Zeile an.
     *
     * @throws IOException
     *             wenn die Zeilenansicht nicht angezeigt werden kann.
     */
    @FXML
    public void showAddRowViewController() throws IOException {
        showRowViewControllerWithRow(null);
    }

    /**
     * Zeigt die Zeilenansicht für das Editieren einer Zeile an.
     */
    @FXML
    public void showEditRowViewController() {
        if (selectedRow == null) {
            presentAlertDialog(Alert.AlertType.ERROR, "Zeile nicht aktualisiert!", "Es wurde keine Zeile ausgewählt.",
                    null, ButtonType.OK);
            return;
        }
        showRowViewControllerWithRow(selectedRow);
    }

    /**
     * Löscht eine Zeile.
     */
    @FXML
    public void deleteRow() {
        if (selectedRow == null) {
            presentAlertDialog(Alert.AlertType.ERROR, "Zeile nicht gelöscht!", "Es wurde keine Zeile ausgewählt.", null,
                    ButtonType.OK);
            return;
        }
        deleteRow(selectedRow);
    }

    /**
     * Fügt eine Zeile (siehe {@link Row}) der Tabelle hinzu.
     *
     * Anschließend wird die Tabelle visuell aktualisiert.
     *
     * @param row
     *            Zeile, die hinzugefügt werden soll.
     * @throws SQLException
     *             wenn die Zeile nicht hinzugefügt werden kann.
     */
    public void addRow(Row row) throws SQLException {
        table.addRow(row);
        fillView();
    }

    /**
     * Ersetzt die alte Zeile mit der neuen Zeile.
     *
     * Anschließend wird die Tabelle visuell aktualisiert.
     *
     * @param oldRow
     *            Alte Zeile.
     * @param newRow
     *            Neue Zeile.
     * @throws SQLException
     *             wenn die Zeile nicht aktualisiert werden kann.
     */
    public void updateOldRowWithNewRow(Row oldRow, Row newRow) throws SQLException {
        table.updateRow(oldRow, newRow);
        fillView();
    }

    /**
     * Löscht eine Zeile.
     *
     * Anschließend wird die Tabelle visuell aktualisiert.
     *
     * @param row
     *            Zu löschende Zeile.
     */
    public void deleteRow(Row row) {
        try {
            table.deleteRow(row);
            fillView();
        } catch (SQLException e) {
            presentAlertDialog(Alert.AlertType.ERROR, "Zeile nicht gelöscht!",
                    "Die Zeile konnte nicht gelöscht werden.", e, ButtonType.OK);
        }
    }

    /**
     * Zeigt die Zeilenansicht mit einer Zeile an.
     *
     * @param row
     *            Anzuzeigende Zeile.
     */
    private void showRowViewControllerWithRow(Row row) {
        try {
            if (row == null) {
                row = new Row();
                for (Column column : getTable().getColumns().values()) {
                    row.put(column, null);
                }
            }
            String selectQueryForRow = getTable().getSelectQueryForRowWithData(row.getData());
            if (selectQueryForRow == null) {
                throw new SQLException("getSelectQueryForRowWithData(Data data) nicht implementiert.");
            }
            Row editableRow = new Row();
            ResultSet resultSet = Application.getInstance().getConnection().executeQuery(selectQueryForRow);
            ResultSetMetaData metaData = resultSet.getMetaData();
            ArrayList<Column> columns = new ArrayList<>();
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                String name = metaData.getColumnName(i);
                String type = metaData.getColumnTypeName(i);
                String nameOfTable = metaData.getTableName(i);

                Column column = new Column(name, type, nameOfTable);
                columns.add(column);
            }
            for (Column column : columns) {
                editableRow.put(column, null);
            }
            ResultSet resultSet2 = Application.getInstance().getConnection().executeQuery(selectQueryForRow);
            if (resultSet2.next()) {
                int i = 1;
                for (Column column : columns) {
                    editableRow.put(column, resultSet2.getObject(i));
                    i++;
                }
            }
            rowViewController = RowViewController.createWithNameAndRowAndTableViewController("row", editableRow, this);
            if (getParentController() instanceof NavigationViewController) {
                ((NavigationViewController) getParentController()).pushViewController(rowViewController);
            } else {
                presentViewController(rowViewController);
            }
        } catch (Exception e) {
            presentAlertDialog(Alert.AlertType.ERROR, "Zeile nicht angezeigt!",
                    "Die Zeile kann nicht angezeigt werden.", e, ButtonType.OK);
        }
    }

    /**
     * Aktualisiert die Tabelle sowohl im Aufbau als auch im Inhalt.
     */
    @FXML
    public void refreshTable() {
        viewWillAppear();
    }

    @Override
    public void viewWillAppear() {
        super.viewWillAppear();
        buildView();
        if (tableView.getColumns().size() > 0)
            fillView();
    }

    /**
     * Baut die Tabelle auf.
     */
    public void buildView() {
        tableView.getColumns().clear();
        topStatusLabel.setText(table.getTitle());
        try {
            table.build();

            for (Column column : table.getColumns().values()) {
                if ((column.getType().equals("BLOB"))) {
                    TableColumn<Row, byte[]> tableColumn = new TableColumn<>(column.getName());
                    tableColumn.setCellFactory(new Callback<TableColumn<Row, byte[]>, TableCell<Row, byte[]>>() {
                        @Override
                        public TableCell<Row, byte[]> call(TableColumn<Row, byte[]> param) {
                            TableCell<Row, byte[]> cell = new TableCell<Row, byte[]>() {
                                @Override
                                protected void updateItem(byte[] item, boolean empty) {
                                    super.updateItem(item, empty);

                                    ImageView imageView = new ImageView();
                                    imageView.setFitWidth(64);
                                    imageView.setFitHeight(64);
                                    imageView.setPreserveRatio(true);
                                    setGraphic(imageView);

                                    if (item != null && !empty) {
                                        try {
                                            ByteArrayInputStream bis = new ByteArrayInputStream(item);
                                            BufferedImage read = ImageIO.read(bis);
                                            Image image = SwingFXUtils.toFXImage(read, null);
                                            imageView.setImage(image);
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            };
                            return cell;
                        }
                    });
                    tableColumn.setCellValueFactory(new MapValueFactory(column));
                    tableView.getColumns().add(tableColumn);
                } else {
                    TableColumn tableColumn = new TableColumn(column.getName());
                    tableColumn.setCellValueFactory(new MapValueFactory(column));
                    tableView.getColumns().add(tableColumn);
                }
            }
        } catch (SQLException e) {
            presentAlertDialog(Alert.AlertType.ERROR, "Tabelle nicht aufgebaut!",
                    "Die Tabelle konnte nicht aufgebaut werden.", e, ButtonType.OK);
        }
    }

    /**
     * Füllt die Tabelle.
     */
    public void fillView() {
        try {
            table.fill();
        } catch (SQLException e) {
            presentAlertDialog(Alert.AlertType.ERROR, "Tabelle nicht gefüllt!",
                    "Die Tabelle konnte nicht gefüllt werden.", e, ButtonType.OK);
        }
    }

    /**
     * Getter für {@link #table}.
     *
     * @return Tabelle (Model).
     */
    public Table getTable() {
        return table;
    }
}
