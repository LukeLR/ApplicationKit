package com.alexanderthelen.applicationkit.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;

/**
 * Basisklasse aller WindowController.
 *
 * Eine {@code WindowController}-Instanz verwaltet ein Fenster (siehe
 * {@link #window}), den darin enthaltenen View (siehe
 * {@link #setViewController(ViewController)}) und die Menüleiste (siehe
 * {@link #menuBar}). Der anzuzeigende View wird durch
 * {@link #setViewController(ViewController)} gesetzt. Der Anzeigestatus des
 * Fensters kann durch die Methoden {@link #show()}, {@link #hide()} und
 * {@link #close()} bestimmt werden. Wie auch bei {@link ViewController} werden
 * die Methoden
 * <ul>
 * <li>{@link #windowWillAppear()}</li>
 * <li>{@link #windowDidAppear()}</li>
 * <li>{@link #windowWillDisappear()}</li>
 * <li>{@link #windowDidDisappear()}</li>
 * <li>{@link #windowWillClose()}</li>
 * <li>{@link #windowDidClose()}</li>
 * </ul>
 * entsprechend aufgerufen. Mit
 * {@link #presentAlertDialog(Alert.AlertType, String, String, Exception, ButtonType...)}
 * und {@link #presentWindowController(WindowController)} können sowohl
 * Meldungen als auch Fenster modal angezeigt werden. Ein WindowController
 * sollte in der {@code start}-Methode einer
 * {@link com.alexanderthelen.applicationkit.Application}-Instanz erzeugt und
 * gesetzt werden.
 *
 * @author Alexander Thelen
 * @version 1.0
 */
public class WindowController extends Controller {
	/**
	 * View des Fenster, dass {@link #menuBar} und {@link #contentView} enthält.
	 */
	@FXML
	protected BorderPane view;
	/**
	 * Menüleiste des Fensters.
	 */
	@FXML
	protected MenuBar menuBar;
	/**
	 * Anzeigebereich des ViewControllers.
	 */
	@FXML
	protected StackPane contentView;

	/**
	 * Fenster.
	 */
	private final Stage window;

	/**
	 * Erstellt eine {@code WindowController}-Instanz mit einem Namen.
	 *
	 * Gleichzeitig wird der View geladen.
	 *
	 * @param name
	 *            Name des WindowControllers.
	 * @return WindowController, der angezeigt werden kann.
	 * @throws IOException
	 *             wenn das Fenster nicht geladen werden kann.
	 */
	public static WindowController createWithName(String name) throws IOException {
		FXMLLoader loader = new FXMLLoader(WindowController.class.getResource("Window.fxml"));
		loader.setController(new WindowController(name));
		loader.load();
		return loader.getController();
	}

	/**
	 * Erstellt eine {@code WindowController}-Instanz mit einem Namen (per
	 * {@link #createWithName(String)}).
	 *
	 * Gleichzeitig wird {@link #window} mit einem neuen Fenster gesetzt.
	 *
	 * @param name
	 *            Name des WindowControllers.
	 */
	protected WindowController(String name) {
		super(name);
		window = new Stage();
	}

	/**
	 * Initialisiert das Fenster.
	 *
	 * Das Fenster wird initialisiert und mit entsprechenden Handlern versehen.
	 */
	@Override
	protected void initialize() {
		window.setScene(new Scene(view));
		window.setOnCloseRequest(event -> {
			windowWillClose();
			if (window.getOwner() == null) {
				Optional<ButtonType> result = presentAlertDialog(Alert.AlertType.WARNING, "Fenster wird geschlossen!",
						"Das aktuelle Fenster wird geschlossen.", null, ButtonType.CANCEL, ButtonType.OK);
				if (!result.isPresent() || result.get() != ButtonType.OK) {
					event.consume();
					return;
				}
			}
			windowDidClose();
		});
		window.setOnShowing(event -> windowWillAppear());
		window.setOnShown(event -> windowDidAppear());
		window.setOnHiding(event -> windowWillDisappear());
		window.setOnHidden(event -> windowDidDisappear());
		window.setMaximized(true);
	}

	/**
	 * Kurz bevor der View des MVC angezeigt wird, wird diese Methode
	 * aufgerufen.
	 *
	 * Unterklassen müssen beim Überschreiben dieser Methode zuerst
	 * {@code super.windowWillAppear()} aufrufen, damit die Methoden der
	 * {@link #childControllers} auch ausgeführt werden.
	 */
	public void windowWillAppear() {
		for (Controller controller : getChildControllers()) {
			((ViewController) controller).viewWillAppear();
		}
	}

	/**
	 * Kurz nachdem der View des MVC angezeigt wird, wird diese Methode
	 * aufgerufen.
	 *
	 * Unterklassen müssen beim Überschreiben dieser Methode zuerst
	 * {@code super.windowDidAppear()} aufrufen, damit die Methoden der
	 * {@link #childControllers} auch ausgeführt werden.
	 */
	public void windowDidAppear() {
		for (Controller controller : getChildControllers()) {
			((ViewController) controller).viewDidAppear();
		}
	}

	/**
	 * Kurz bevor der View des MVC verschwindet, wird diese Methode aufgerufen.
	 *
	 * Unterklassen müssen beim Überschreiben dieser Methode zuerst
	 * {@code super.windowWillDisappear()} aufrufen, damit die Methoden der
	 * {@link #childControllers} auch ausgeführt werden.
	 */
	public void windowWillDisappear() {
		for (Controller controller : getChildControllers()) {
			((ViewController) controller).viewDidDisappear();
		}
	}

	/**
	 * Kurz nachdem der View des MVC verschwindet, wird diese Methode
	 * aufgerufen.
	 *
	 * Unterklassen müssen beim Überschreiben dieser Methode zuerst
	 * {@code super.windowDidDisappear()} aufrufen, damit die Methoden der
	 * {@link #childControllers} auch ausgeführt werden.
	 */
	public void windowDidDisappear() {
		for (Controller controller : getChildControllers()) {
			((ViewController) controller).viewDidDisappear();
		}
	}

	/**
	 * Kurz bevor der View des MVC geschlossen wird, wird diese Methode
	 * aufgerufen.
	 *
	 * Unterklassen müssen beim Überschreiben dieser Methode zuerst
	 * {@code super.windowWillClose()} aufrufen, damit die Methoden der
	 * {@link #childControllers} auch ausgeführt werden.
	 */
	public void windowWillClose() {
		windowWillDisappear();
	}

	/**
	 * Kurz nachdem der View des MVC geschlossen wird, wird diese Methode
	 * aufgerufen.
	 *
	 * Unterklassen müssen beim Überschreiben dieser Methode zuerst
	 * {@code super.windowDidClose()} aufrufen, damit die Methoden der
	 * {@link #childControllers} auch ausgeführt werden.
	 */
	public void windowDidClose() {
		windowDidDisappear();
	}

	/**
	 * Getter für den angezeigten ViewController.
	 *
	 * @return Angezeigter ViewController.
	 */
	public ViewController getViewController() {
		if (getChildControllers().isEmpty())
			return null;
		return (ViewController) getChildControllers().get(0);
	}

	/**
	 * Setter für anzuzeigenden ViewController.
	 *
	 * Dieser Setter übernimmt auch jegliche Bereinigung der Anzeige.
	 *
	 * @param viewController
	 *            ViewController, der angezeigt werden soll.
	 */
	public void setViewController(ViewController viewController) {
		if (!getChildControllers().isEmpty()) {
			getViewController().viewWillDisappear();
			contentView.getChildren().remove(getViewController().getView());
			getViewController().viewDidDisappear();
			removeChildController(getViewController());
			window.setTitle(getTitle());
		}
		if (viewController == null)
			return;
		addChildController(viewController);
		viewController.viewWillAppear();
		contentView.getChildren().add(viewController.getView());
		viewController.viewDidAppear();
	}

	/**
	 * Zeigt das Fenster an.
	 */
	public void show() {
		window.show();
	}

	/**
	 * Versteckt das Fenster.
	 */
	public void hide() {
		window.hide();
	}

	/**
	 * Schließt das Fenster, nachdem eine Meldung bestätigt wurde.
	 */
	public void close() {
		window.close();
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
	public Optional<ButtonType> presentAlertDialog(Alert.AlertType type, String title, String message,
                                                   Exception exception, ButtonType... buttons) {
		Alert alert = new Alert(type, message, buttons);
		alert.initOwner(window);
		alert.setHeaderText(title);
		alert.setResizable(true);
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
	 * Präsentiert einen WindowController modal.
	 *
	 * @param windowController
	 *            Zu präsentierender WindowController.
	 */
	public void presentWindowController(WindowController windowController) {
		windowController.getWindow().initStyle(StageStyle.UTILITY);
		windowController.getWindow().initOwner(window);
		windowController.getWindow().initModality(Modality.WINDOW_MODAL);
		windowController.show();
	}

	/**
	 * Setter für {@link #title}.
	 *
	 * Setzt den Titel des Fensters neu.
	 *
	 * @param title
	 *            Titel des WindowControllers.
	 */
	@Override
	public void setTitle(String title) {
		super.setTitle(title);
		window.setTitle(title);
	}

	/**
	 * Getter für {@link #window}.
	 *
	 * @return Fenster.
	 */
	public Stage getWindow() {
		return window;
	}
}
