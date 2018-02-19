package com.alexanderthelen.applicationkit.gui;

import com.alexanderthelen.applicationkit.database.Column;
import com.alexanderthelen.applicationkit.database.Data;
import com.alexanderthelen.applicationkit.database.Row;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;

/**
 * Eine {@code RowViewController}-Instanz stellt die Informationen einer
 * gegebenen Zeile (siehe {@link Row}) in einem Formular dar.
 *
 * Diese Instanz ist nicht zuständig für die Verwaltung (Hinzufügen bzw.
 * Aktualisieren einer Zeile) der Tabelle - je nach Implementierung der
 * abstrakten Methoden der Tabelle (siehe
 * {@link com.alexanderthelen.applicationkit.database.Table}) der
 * {@link TableViewController}-Instanz kann die Zeile hinzugefügt oder
 * aktualisiert werden.
 */
public class RowViewController extends FormViewController {
    /**
     * View des RowViewControllers.
     */
    @FXML
    protected BorderPane view;
    /**
     * Container der einzelnen Formularfelder.
     */
    @FXML
    protected GridPane contentView;
    /**
     * Container der Buttons
     */
    @FXML
    protected HBox buttonsView;

    /**
     * Zugehöriger TableViewController, an den Befehle übergeben werden können
     */
    private TableViewController tableViewController;
    /**
     * Anzuzeigende Zeile.
     *
     * Die Schlüssel bestimmen die Anzahl der anzuzeigenden Informationen.
     */
    private final Row row;

    /**
     * Erstellt eine {@code RowViewController}-Instanz mit einem Namen, einer
     * Zeile und einem TableViewController.
     *
     * Außerdem wird der View geladen.
     *
     * @param name
     *            Name des RowViewControllers.
     * @param row
     *            Zeile (Model).
     * @param tableViewController
     *            TableViewController.
     * @return RowViewController.
     * @throws IOException
     *             wenn der View nicht geladen werden kann.
     */
    public static RowViewController createWithNameAndRowAndTableViewController(String name, Row row,
                                                                               TableViewController tableViewController) throws IOException {
        RowViewController viewController = new RowViewController(name, row, tableViewController);
        viewController.loadView();
        return viewController;
    }

    /**
     * Erstellt eine {@code RowViewController}-Instanz mit einem Namen, einer
     * Zeile und einem TableViewController (per
     * {@link #createWithNameAndRowAndTableViewController(String, Row, TableViewController)}).
     *
     * Außerdem wird der Standard-View gesetzt.
     *
     * @param name
     *            Name des RowViewControllers.
     * @param row
     *            Zeile (Model).
     * @param tableViewController
     *            TableViewController.
     */
    protected RowViewController(String name, Row row, TableViewController tableViewController) {
        super(name, RowViewController.class.getResource("RowView.fxml"));
        this.row = row;
        this.tableViewController = tableViewController;
    }

    /**
     * Gibt eine Liste aller Eingabefelder zurück.
     *
     * @return Liste aller Eingabefelder.
     */
    @Override
    public ArrayList<Node> getInputNodes() {
        ArrayList<Node> inputNodes = new ArrayList<>();
        for (Node node : contentView.getChildren()) {
            if (node instanceof TextInputControl) {
                inputNodes.add(node);
            } else if (node instanceof VBox) {
                ImageView imageView = (ImageView) ((VBox) node).getChildren().get(0);
                inputNodes.add(imageView);
            }
        }
        return inputNodes;
    }

    /**
     * Gibt die Daten aller Eingabefelder zurück.
     *
     * @return Daten aller Eingabefelder.
     */
    @Override
    public Data getInputData() {
        ArrayList<Node> inputNodes = getInputNodes();
        Data data = new Data();
        int i = 0;
        for (Column column : row.keySet()) {
            if (column.getType().equals("BLOB")) {
                byte[] result = null;
                if (((ImageView) inputNodes.get(i)).getImage() != null) {
                    Image image = ((ImageView) inputNodes.get(i)).getImage();
                    BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    try {
                        ImageIO.write(bufferedImage, "png", outputStream);
                        InputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
                        Image resizedImage = new Image(inputStream, 64, 64, true, true);
                        BufferedImage bufferedImage2 = SwingFXUtils.fromFXImage(resizedImage, null);
                        ByteArrayOutputStream outputStream2 = new ByteArrayOutputStream();
                        ImageIO.write(bufferedImage2, "png", outputStream2);
                        result = outputStream2.toByteArray();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                data.put(column.getFullName(), result);
            } else {
                data.put(column.getFullName(), ((TextInputControl) inputNodes.get(i)).getText());
            }
            i++;
        }
        return data;
    }

    @Override
    public void resetInput() {
        ArrayList<Node> inputNodes = getInputNodes();
        int i = 0;
        for (Column column : row.keySet()) {
            if (column.getType().equals("BLOB")) {
                ImageView imageView = (ImageView) inputNodes.get(i);
                imageView.setImage((Image) imageView.getUserData());
            } else {
                ((TextInputControl) inputNodes.get(i)).setText(row.get(column) == null ? null : row.get(column).toString());
            }
            i++;
        }
        inputNodes.get(0).requestFocus();
    }

    /**
     * Validiert die Eingabe und versucht diese als neue Zeile abzuspeichern
     * oder eine gegebene Zeile zu aktualisieren.
     */
    @Override
    @FXML
    public void acceptInput() {
        Row newRow;
        try {
            newRow = getNewRow();
        } catch (Exception e) {
            presentAlertDialog(Alert.AlertType.ERROR, "Eingaben ung�ltig!", "Die Eingaben sind ung�ltig.", e,
                    ButtonType.OK);
            return;
        }
        if (row.values().iterator().next() == null) {
            try {
                tableViewController.addRow(newRow);
            } catch (Exception e) {
                presentAlertDialog(Alert.AlertType.ERROR, "Zeile nicht hinzugef�gt!",
                        "Die Zeile konnte nicht hinzugef�gt werden.", e, ButtonType.OK);
                return;
            }
        } else {
            try {
                tableViewController.updateOldRowWithNewRow(row, newRow);
            } catch (Exception e) {
                presentAlertDialog(Alert.AlertType.ERROR, "Zeile nicht aktualisiert!",
                        "Die Zeile konnte nicht aktualisiert werden.", e, ButtonType.OK);
                return;
            }
        }
        if (getParentController() instanceof NavigationViewController) {
            ((NavigationViewController) getParentController()).popViewController();
        } else {
            tableViewController.dismissViewController(this);
        }
        tableViewController = null;
    }

    /**
     * Baut die Zeilenansicht auf.
     */
    public void buildView() {
        contentView.getChildren().clear();

        int indexOfRow = 0;
        for (Column column : row.keySet()) {
            Label label = new Label(column.getName() + ":");
            label.setFont(Font.font(label.getFont().getFamily(), FontWeight.BOLD, label.getFont().getSize()));
            GridPane.setConstraints(label, 0, indexOfRow);
            contentView.getChildren().add(label);

            if (column.getType().equals("INTEGER")) {
                TextField textField = new TextField();
                GridPane.setConstraints(textField, 1, indexOfRow);
                contentView.getChildren().add(textField);
            } else if (column.getType().equals("BOOLEAN")) {
                TextField textField = new TextField();
                GridPane.setConstraints(textField, 1, indexOfRow);
                contentView.getChildren().add(textField);
            } else if (column.getType().equals("REAL")) {
                TextField textField = new TextField();
                GridPane.setConstraints(textField, 1, indexOfRow);
                contentView.getChildren().add(textField);
            } else if (column.getType().equals("NUMERIC")) {
                TextField textField = new TextField();
                GridPane.setConstraints(textField, 1, indexOfRow);
                contentView.getChildren().add(textField);
            } else if (column.getType().equals("TEXT")) {
                TextArea textArea = new TextArea();
                GridPane.setConstraints(textArea, 1, indexOfRow);
                contentView.getChildren().add(textArea);
            } else if (column.getType().equals("VARCHAR")) {
                TextField textField = new TextField();
                GridPane.setConstraints(textField, 1, indexOfRow);
                contentView.getChildren().add(textField);
            } else if (column.getType().equals("BLOB")) {
                VBox vBox = new VBox(5);
                ImageView imageView = new ImageView();
                imageView.setFitWidth(64);
                imageView.setFitHeight(64);
                imageView.setPreserveRatio(true);
                vBox.getChildren().add(imageView);
                HBox hBox = new HBox(5);
                Button deleteButton = new Button("L�schen");
                deleteButton.setOnAction(event -> imageView.setImage(null));
                Button resetButton = new Button("Zur�cksetzen");
                resetButton.setOnAction(event -> imageView.setImage((Image) imageView.getUserData()));
                Button acceptButton = new Button("Ausw�hlen...");
                acceptButton.setOnAction(event -> {
                    FileChooser fileChooser = new FileChooser();
                    fileChooser.setTitle("Open Resource File");
                    fileChooser.getExtensionFilters().addAll(
                            new FileChooser.ExtensionFilter("Bilder", "*.png"),
                            new FileChooser.ExtensionFilter("Alle Dateien", "*.*"));
                    File selectedFile = fileChooser.showOpenDialog(getWindowController().getWindow());
                    if (selectedFile != null) {
                        Image image;
                        try {
                            BufferedImage bufferedImage = ImageIO.read(selectedFile);
                            image = SwingFXUtils.toFXImage(bufferedImage, null);
                        } catch (IOException e) {
                            image = null;
                        }
                        imageView.setImage(image);
                    }
                });
                hBox.getChildren().add(deleteButton);
                hBox.getChildren().add(resetButton);
                hBox.getChildren().add(acceptButton);
                vBox.getChildren().add(hBox);
                GridPane.setConstraints(vBox, 1, indexOfRow);
                contentView.getChildren().add(vBox);
            } else {
                TextField textField = new TextField();
                GridPane.setConstraints(textField, 1, indexOfRow);
                contentView.getChildren().add(textField);
            }

            indexOfRow++;
        }
    }

    /**
     * Füllt die Zeilenansicht.
     */
    public void fillView() {
        int i = 0;
        for (Column column : row.keySet()) {
            if (column.getType().equals("BLOB")) {
                WritableImage image = null;
                if (row.get(column) != null && !row.get(column).equals("")) {
                    try {
                        ByteArrayInputStream bis = new ByteArrayInputStream((byte[]) row.get(column));
                        BufferedImage read = ImageIO.read(bis);
                        image = SwingFXUtils.toFXImage(read, null);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                getInputNodes().get(i).setUserData(image);
                ((ImageView) getInputNodes().get(i)).setImage(image);
            } else {
                ((TextInputControl) getInputNodes().get(i))
                        .setText(row.get(column) == null || row.get(column).equals("") ? null : row.get(column).toString());
            }
            i++;
        }
    }

    /**
     * Getter für {@link #tableViewController}.
     *
     * @return TableViewController.
     */
    public TableViewController getTableViewController() {
        return tableViewController;
    }

    /**
     * Getter für {@link #row}.
     *
     * @return Alte Zeile, mit der die Zeilenansicht geladen wurde.
     */
    public Row getRow() {
        return row;
    }

    /**
     * Gibt eine neue Zeile anhand der aktuell eingegebenen Informationen
     * zurück.
     *
     * Außerdem werden die Informationen validiert, indem die Einhaltung des
     * Datentyps überprüft wird.
     *
     * @return Neue Zeile.
     * @throws Exception
     *             wenn irgendein Fehler auftritt (z.B. wenn der Datentyp nicht
     *             eingehalten wurde).
     */
    public Row getNewRow() throws Exception {
        Data data = getInputData();
        Row row = new Row();
        for (Column column : this.row.keySet()) {
            if (column.getType().equals("INTEGER")) {
                row.put(column, data.get(column.getFullName()) == null || data.get(column.getFullName()).equals("")
                        ? null : Integer.parseInt((String) data.get(column.getFullName())));
            } else if (column.getType().equals("BOOLEAN")) {
                row.put(column, data.get(column.getFullName()) == null || data.get(column.getFullName()).equals("")
                        ? null : Integer.parseInt((String) data.get(column.getFullName())));
            } else if (column.getType().equals("REAL")) {
                row.put(column, data.get(column.getFullName()) == null || data.get(column.getFullName()).equals("")
                        ? null : Double.parseDouble((String) data.get(column.getFullName())));
            } else if (column.getType().equals("NUMERIC")) {
                row.put(column, data.get(column.getFullName()) == null || data.get(column.getFullName()).equals("")
                        ? null : Double.parseDouble((String) data.get(column.getFullName())));
            } else if (column.getType().equals("TEXT")) {
                row.put(column, data.get(column.getFullName()) == null || data.get(column.getFullName()).equals("")
                        ? null : (String) data.get(column.getFullName()));
            } else if (column.getType().equals("VARCHAR")) {
                row.put(column, data.get(column.getFullName()) == null || data.get(column.getFullName()).equals("")
                        ? null : (String) data.get(column.getFullName()));
            } else if (column.getType().equals("BLOB")) {
                row.put(column, data.get(column.getFullName()) == null || data.get(column.getFullName()).equals("")
                        ? null : (byte[]) data.get(column.getFullName()));
            } else {
                row.put(column, data.get(column.getFullName()) == null || data.get(column.getFullName()).equals("")
                        ? null : (String) data.get(column.getFullName()));
            }
        }
        return row;
    }

    /**
     * Baut die Ansicht auf und füllt diese in der richtigen Reihenfolge.
     */
    @Override
    public void viewWillAppear() {
        buildView();
        super.viewWillAppear();
        fillView();
    }
}
