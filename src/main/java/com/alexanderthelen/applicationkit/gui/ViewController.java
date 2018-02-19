package com.alexanderthelen.applicationkit.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;

/**
 * Basisklasse aller ViewController.
 *
 * Eine {@code ViewController}-Instanz kann den View anderer Instanzen in sich
 * einbetten (siehe die überladenen Methoden
 * {@link #addChildViewControllerToView(ViewController)} und
 * {@link #addChildViewControllerToViewAtIndex(ViewController, int)}).
 * Unterklassen können nicht direkt instanziert werden (siehe unten). Alle
 * Konstruktoren der Unterklassen müssen {@code protected} sein. Alle
 * Unterklassen, die eine fxml-Datei (im gleichen Ordner wie die Unterklasse)
 * und damit den View vorgeben, müssen einen entsprechenden Konstruktor
 * hinzufügen. Beispiel:
 * 
 * <pre>
 * {@code
 * protected Unterklasse(String name) {
 *     super(name, Unterklasse.class.getResource("UnterklasseView.fxml"));
 * }
 * }
 * </pre>
 * 
 * Jede Unterklasse, die keine fxml-Datei vorgibt, muss den Konstruktor nach
 * folgendem Gerüst implementieren:
 * 
 * <pre>
 * {@code
 * protected Unterklasse(String name, URL urlOfView) {
 *     super(name, urlOfView);
 * }
 * }
 * </pre>
 * 
 * Alle Unterklassen, die instanzierbar sein sollen, müssen dies über eine
 * statische Methode tun. Die statische Methode {@code createWithName(String)}
 * muss dazu implementiert werden. Beispiel:
 * 
 * <pre>
 * {@code
 * public static Unterklasse createWithName(String name) throws IOException {
 *     Unterklasse controller = new Unterklasse(name); // oder new Unterklasse(name, Unterklasse.class.getResource("UnterklasseView.fxml");
 *     controller.loadView();
 *     return controller;
 * }
 * }
 * </pre>
 * 
 * Die Methoden
 * <ul>
 * <li>{@link #viewWillAppear()}</li>
 * <li>{@link #viewDidAppear()}</li>
 * <li>{@link #viewWillDisappear()}</li>
 * <li>{@link #viewDidDisappear()}</li>
 * </ul>
 * werden entsprechend des Anzeigestatus aufgerufen.
 *
 * @author Alexander Thelen
 * @version 1.0
 */
public abstract class ViewController extends Controller {
	/**
	 * View des ViewControllers.
	 */
	@FXML
	protected Parent view;

	/**
	 * URL des Views zur fxml-Datei.
	 *
	 * Diese Variable muss in den Konstruktoren derjenigen Unterklassen gesetzt
	 * werden, die eine fxml-Datei und damit den View vorgeben.
	 */
	private final URL urlOfView;

	/**
	 * Erzeugt eine {@code ViewController}-Instanz mit einem Namen und einer URL
	 * des Views zur fxml-Datei.
	 *
	 * @param name
	 *            Name des ViewControllers.
	 * @param urlOfView
	 *            URL des Views zur fxml-Datei.
	 */
	protected ViewController(String name, URL urlOfView) {
		super(name);
		this.urlOfView = urlOfView;
	}

	/**
	 * Lädt den View und setzt den Controller.
	 *
	 * @throws IOException
	 *             wenn die URL nicht gelesen werden kann oder das Laden
	 *             fehlschlägt.
	 */
	protected void loadView() throws IOException {
		FXMLLoader loader = new FXMLLoader(urlOfView);
		loader.setController(this);
		loader.load();
	}

	/**
	 * Kurz bevor der View des MVC angezeigt wird, wird diese Methode
	 * aufgerufen.
	 *
	 * Unterklassen müssen beim Überschreiben dieser Methode zuerst
	 * {@code super.viewWillAppear()} aufrufen, damit die Methoden der
	 * {@link #childControllers} auch ausgeführt werden.
	 */
	public void viewWillAppear() {
		for (Controller controller : getChildControllers()) {
			((ViewController) controller).viewWillAppear();
		}
	}

	/**
	 * Kurz nachdem der View des MVC angezeigt wird, wird diese Methode
	 * aufgerufen.
	 *
	 * Unterklassen müssen beim Überschreiben dieser Methode zuerst
	 * {@code super.viewDidAppear()} aufrufen, damit die Methoden der
	 * {@link #childControllers} auch ausgeführt werden.
	 */
	public void viewDidAppear() {
		for (Controller controller : getChildControllers()) {
			((ViewController) controller).viewDidAppear();
		}
	}

	/**
	 * Kurz bevor der View des MVC verschwindet, wird diese Methode aufgerufen.
	 *
	 * Unterklassen müssen beim Überschreiben dieser Methode zuerst
	 * {@code super.viewWillDisappear()} aufrufen, damit die Methoden der
	 * {@link #childControllers} auch ausgeführt werden.
	 */
	public void viewWillDisappear() {
		for (Controller controller : getChildControllers()) {
			((ViewController) controller).viewDidDisappear();
		}
	}

	/**
	 * Kurz nachdem der View des MVC verschwindet, wird diese Methode
	 * aufgerufen.
	 *
	 * Unterklassen müssen beim Überschreiben dieser Methode zuerst
	 * {@code super.viewDidDisappear()} aufrufen, damit die Methoden der
	 * {@link #childControllers} auch ausgeführt werden.
	 */
	public void viewDidDisappear() {
		for (Controller controller : getChildControllers()) {
			((ViewController) controller).viewDidDisappear();
		}
	}

	/**
	 * Bettet einen ViewController in den aktuellen ViewController direkt ein.
	 *
	 * @param viewController
	 *            Anzuzeigender ViewController.
	 */
	public void addChildViewControllerToView(ViewController viewController) {
		addChildViewControllerToView(viewController, view);
	}

	/**
	 * Bettet einen ViewController in den aktuellen ViewController in einem
	 * entsprechenden View ein.
	 *
	 * @param viewController
	 *            Anzuzeigender ViewController.
	 * @param view
	 *            View, in dem der View des ViewControllers angezeigt werden
	 *            soll.
	 */
	public void addChildViewControllerToView(ViewController viewController, Parent view) {
		addChildController(viewController);
		if (getWindowController() != null)
			viewController.viewWillAppear();
		if (view instanceof BorderPane) {
			((BorderPane) view).setCenter(viewController.getView());
		} else if (view instanceof SplitPane) {
			((SplitPane) view).getItems().add(viewController.getView());
		} else {
			((Pane) view).getChildren().add(viewController.getView());
		}
		if (getWindowController() != null)
			viewController.viewDidAppear();
	}

	/**
	 * Bettet einen ViewController in den aktuellen ViewController direkt an
	 * einem gegebenem Index ein.
	 *
	 * @param viewController
	 *            Anzuzeigender ViewController.
	 * @param index
	 *            Position, in der eingebettet werden soll.
	 */
	public void addChildViewControllerToViewAtIndex(ViewController viewController, int index) {
		addChildViewControllerToViewAtIndex(viewController, view, index);
	}

	/**
	 * Bettet einen ViewController in den aktuellen ViewController in einem
	 * entsprechenden View an einem gegebenen Index ein.
	 *
	 * @param viewController
	 *            Anzuzeigender ViewController.
	 * @param view
	 *            View, in dem der View des ViewControllers angezeigt werden
	 *            soll.
	 * @param index
	 *            Position, in der eingebettet werden soll.
	 */
	public void addChildViewControllerToViewAtIndex(ViewController viewController, Parent view, int index) {
		addChildController(viewController);
		if (getWindowController() != null)
			viewController.viewWillAppear();
		if (view instanceof SplitPane) {
			((SplitPane) view).getItems().add(index, viewController.getView());
		} else {
			((Pane) view).getChildren().add(index, viewController.getView());
		}
		if (getWindowController() != null)
			viewController.viewDidAppear();
	}

	/**
	 * Entfernt einen ViewController direkt aus einem View des aktuellen
	 * ViewControllers.
	 *
	 * @param viewController
	 *            Zu entfernender ViewController.
	 */
	public void removeChildViewControllerFromView(ViewController viewController) {
		removeChildViewControllerFromView(viewController, view);
	}

	/**
	 * Entfernt einen ViewController aus einem View des aktuellen
	 * ViewControllers.
	 *
	 * @param viewController
	 *            Zu entfernender ViewController.
	 * @param view
	 *            View, das den View des zu entfernenden ViewControllers
	 *            enthält.
	 */
	public void removeChildViewControllerFromView(ViewController viewController, Parent view) {
		removeChildController(viewController);
		if (getWindowController() != null)
			viewController.viewWillDisappear();
		if (view instanceof BorderPane) {
			((BorderPane) view).setCenter(null);
		} else if (view instanceof SplitPane) {
			((SplitPane) view).getItems().remove(viewController.getView());
		} else {
			((Pane) view).getChildren().remove(viewController.getView());
		}
		if (getWindowController() != null)
			viewController.viewDidDisappear();
	}

	/**
	 * Zeigt eine Meldung an.
	 *
	 * Die Meldung kann nur angezeigt werden, wenn der ViewController in einem
	 * WindowController direkt oder indirekt eingebettet ist.
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
		if (getWindowController() == null)
			return null;
		return getWindowController().presentAlertDialog(type, title, message, exception, buttons);
	}

	/**
	 * Präsentiert einen ViewController modal.
	 *
	 * Dabei wird eine neue {@link WindowController}-Instanz erzeugt.
	 *
	 * @param viewController
	 *            Zu präsentierender ViewController.
	 */
	public void presentViewController(ViewController viewController) {
		if (getWindowController() == null)
			return;
		try {
			WindowController windowController = WindowController.createWithName("modal");
			windowController.setViewController(viewController);
			getWindowController().presentWindowController(windowController);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void dismissViewController(ViewController viewController) {
		viewController.getWindowController().close();
	}

	/**
	 * Getter für den WindowController, der diesen ViewController besitzt.
	 *
	 * @return WindowController.
	 */
	public WindowController getWindowController() {
		Controller controller = getParentController();
		while (controller != null && !(controller instanceof WindowController)) {
			controller = controller.getParentController();
		}
		return (WindowController) controller;
	}

	/**
	 * Getter für View.
	 *
	 * @return View.
	 */
	public Parent getView() {
		return view;
	}
}
