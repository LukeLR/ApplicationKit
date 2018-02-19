package com.alexanderthelen.applicationkit;

import com.alexanderthelen.applicationkit.database.Connection;
import com.alexanderthelen.applicationkit.database.Data;
import com.alexanderthelen.applicationkit.gui.WindowController;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;

/**
 * {@code Application} ist die abstrakte Startklasse des Programms.
 *
 * Sie ist die einzige Struktur dieses Systems, die nicht dem MVC-Modell folgt.
 * Klassen, die von dieser Klasse erben, müssen {@link #main(String[])} und
 * {@link #start()} implementieren (siehe unten). Es sollte nur eine Instanz
 * dieser Klasse erzeugt werden. Dies geschieht automatisch durch JavaFX,
 * nachdem die {@link #main(String[])} aufgerufen wurde. Auf das
 * {@code Application}-Objekt kann dann durch {@link #getInstance()} zugegriffen
 * werden. Die Instanz dieser Klasse besitzt ein Fenster, das durch die Variable
 * {@link #windowController} verwaltet wird, und eine Verbindung zu einer
 * Datenbank in der Variable {@link #connection}. {@link #connection} muss am
 * Anfang der {@link #start()}-Methode gesetzt werden (siehe unten). Bestimmte
 * Daten, die von Programm zu Programm unterschiedlich sind, können in
 * {@link #data} gespeichert werden. Für alle Variablen gibt es entsprechende
 * Getter- und ggf. Setter-Methoden.
 *
 * Ein Beispiel für eine Unterklasse:
 * 
 * <pre>
 * {@code
 * public class Application extends com.alexanderthelen.applicationkit.Application {
 *     public static void main(String[] args) {
 *         launch(args);
 *     }
 *
 *     public void start() throws Exception {
 *         Path path = Paths.get(...) // Pfad zur Datenbank
 *         setConnection(new Connection(path));
 *
 *         WindowController windowController = WindowController.createWithName("window1");
 *         windowController.setTitle("Fenster 1");
 *
 *         ... // Erzeugen und setzen des ViewController.
 *
 *         setWindowController(windowController);
 *         show();
 *     }
 * }
 * }
 * </pre>
 *
 * For my mother. Return to energiser.
 *
 * @author Alexander Thelen
 * @version 1.0
 */
public abstract class Application extends javafx.application.Application {
	/**
	 * Eindeutige Instanz des Programms.
	 */
	private static Application instance;

	/**
	 * {@link WindowController}-Instanz zur Verwaltung des einzigen Fensters.
	 */
	private WindowController windowController;
	/**
	 * {@link Connection}-Instanz zur Verwaltung der einzigen Verbindung zur
	 * Datenbank.
	 */
	private Connection connection;
	/**
	 * Liste, die global abrufbare Daten beinhaltet.
	 *
	 * Diese Liste eignet sich dafür, wichtige Informationen zu speichern (wie
	 * z.B. den Namen des angemeldeten Nutzers und die Rechte).
	 */
	private final Data data = new Data();

	/**
	 * Getter für {@link #instance}.
	 *
	 * @return Instanz des Programms.
	 */
	public static Application getInstance() {
		return instance;
	}

	/**
	 * Standard-{@code main}-Methode.
	 *
	 * @param args
	 *            Argumente, die beim Aufruf der Klasse übergeben wurden.
	 */
	public static void main(String[] args) {
		launch(args);
	}

	/**
	 * Erstellt eine Instanz der Klasse.
	 *
	 * Wird von JavaFX automatisch aufgerufen.
	 */
	public Application() {
		instance = this;
	}

	/**
	 * Einstiegspunkt eines Programms.
	 *
	 * {@code primareStage} wird nicht gebraucht. Diese Methode wird von JavaFX
	 * aufgerufen und ruft die zu implementierende Methode {@link #start()} auf.
	 *
	 * @param primaryStage
	 *            nicht gebrauchte {@code Stage}.
	 * @throws Exception
	 *             wenn ein Fehler auftritt
	 */
	@Override
	public final void start(Stage primaryStage) throws Exception {
		try {
			start();
		} catch (Exception e) {
			presentAlertDialog(AlertType.ERROR, "Kritischer Fehler!", "Programm wird beendet.", e, ButtonType.OK);
		}
	}

	/**
	 * Einstiegspunkt eines Programms.
	 *
	 * Diese Methode muss implementiert werden.
	 *
	 * @throws Exception
	 *             wenn ein Fehler auftritt
	 */
	public abstract void start() throws Exception;

	/**
	 * Zeigt das Programm.
	 */
	public void show() {
		windowController.show();
	}

	/**
	 * Versteckt das Programm.
	 */
	public void hide() {
		windowController.hide();
	}

	/**
	 * Beendet das Programm.
	 *
	 * Ggf. wird noch eine Bestätigungsabfrage vorgeschaltet.
	 */
	public void quit() {
		if (windowController == null)
			System.exit(0);
		Optional<ButtonType> result = windowController.presentAlertDialog(AlertType.WARNING,
				"Programm wird beendet!", "Das Programm wird beendet.", null, ButtonType.CANCEL, ButtonType.OK);
		if (result.isPresent() && result.get() == ButtonType.OK) {
			System.exit(0);
		}
	}

	/**
	 * Zeigt eine Meldung an.
	 *
	 * @param type
	 *            Typ der Meldung.
	 * @param title
	 *            Titel der Meldung.
	 * @param message
	 *            Nachricht.
	 * @param exception
	 *            Zusätzliche Information (kann {@code null} sein).
	 * @param buttons
	 *            Schaltflächen, die angezeigt werden sollen.
	 * @return {@code Optional<ButtonType>}
	 */
	public Optional<ButtonType> presentAlertDialog(AlertType type, String title, String message,
                                                   Exception exception, ButtonType... buttons) {
		Alert alert = new Alert(type, message, buttons);
		alert.setHeaderText(title);
		if (exception != null) {
			Label label = new Label("Zusätzliche Informationen:");

			StringWriter stringWriter = new StringWriter();
			PrintWriter printWriter = new PrintWriter(stringWriter);
			exception.printStackTrace(printWriter);
			TextArea textArea = new TextArea(stringWriter.toString());
			textArea.setEditable(false);
			textArea.setWrapText(true);

			textArea.setMaxWidth(Region.USE_COMPUTED_SIZE);
			textArea.setMaxHeight(Region.USE_COMPUTED_SIZE);
			VBox.setVgrow(textArea, Priority.SOMETIMES);

			VBox expContent = new VBox(label, textArea);

			alert.getDialogPane().setExpandableContent(expContent);
			alert.getDialogPane().expandedProperty().addListener((observable, oldValue, newValue) -> {
				alert.getDialogPane().getScene().getWindow().centerOnScreen();
			});
		}
		return alert.showAndWait();
	}

	/**
	 * Getter für {@link #windowController}.
	 *
	 * @return {@link WindowController}-Instanz zur Verwaltung des einzigen
	 *         Fensters.
	 */
	public WindowController getWindowController() {
		return windowController;
	}

	/**
	 * Setter für {@link #windowController}.
	 *
	 * @param windowController
	 *            {@link WindowController}-Instanz zur Verwaltung des einzigen
	 *            Fensters.
	 */
	public void setWindowController(WindowController windowController) {
		this.windowController = windowController;
	}

	/**
	 * Getter für {@link #connection}.
	 *
	 * @return {@link Connection}-Instanz zur Verwaltung der einzigen Verbindung
	 *         zur Datenbank.
	 */
	public Connection getConnection() {
		return connection;
	}

	/**
	 * Setter für {@link #connection}.
	 *
	 * @param connection
	 *            {@link Connection}-Instanz zur Verwaltung der einzigen
	 *            Verbindung zur Datenbank.
	 */
	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	/**
	 * Getter für {@link #data}.
	 *
	 * @return Liste, die global abrufbare Daten beinhaltet.
	 */
	public Data getData() {
		return data;
	}
}
