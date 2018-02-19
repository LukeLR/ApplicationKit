package com.alexanderthelen.applicationkit.gui;

import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Die {@code NavigationViewController}-Klasse vereinfacht den Wechsel zwischen
 * ViewControllern.
 *
 * Dabei wird {@link ViewController#childControllers} als Stack angesehen und
 * verwaltet. In der Navigationsleiste steht der Titel des aktuell angezeigten
 * ViewControllers. Sollten sich mehrere ViewController auf dem Stack bzw. in
 * {@link ViewController#childControllers} befinden, dann erscheint zusätzlich
 * ein Zurück-Button, der den obersten ViewController entfernt. Folgende
 * Methoden dienen zur Verwaltung:
 * <ul>
 * <li>{@link #setInitialViewController(ViewController)}</li>
 * <li>{@link #pushViewController(ViewController)}</li>
 * <li>{@link #popViewController()}</li>
 * <li>{@link #getTopViewController()}</li>
 * </ul>
 *
 * @author Alexander Thelen
 * @version 1.0
 */
public class NavigationViewController extends ViewController {
	/**
	 * View des NavigationViewControllers.
	 */
	@FXML
	protected BorderPane view;
	/**
	 * Navigationsleiste.
	 */
	@FXML
	protected HBox navigationBar;
	/**
	 * Zurück-Button, der den aktuell angezeigten ViewController schließt.
	 */
	@FXML
	protected Button dismissButton;
	/**
	 * Label für den Titel.
	 */
	@FXML
	protected Label titleLabel;
	/**
	 * Anzeigebreich des ViewControllers.
	 */
	@FXML
	protected StackPane contentView;

	/**
	 * Erstellt eine {@code NavigationViewController}-Instanz mit einem Namen.
	 *
	 * Außerdem wird der View geladen.
	 *
	 * @param name
	 *            Name des NavigationViewControllers.
	 * @return NavigationViewController.
	 * @throws IOException
	 *             wenn der View nicht geladen werden kann.
	 */
	public static NavigationViewController createWithName(String name) throws IOException {
		NavigationViewController controller = new NavigationViewController(name);
		controller.loadView();
		return controller;
	}

	/**
	 * Erstellt eine {@code NavigationViewController}-Instanz mit einem Namen
	 * (per {@link #createWithName(String)}).
	 *
	 * Gleichzeitig wird der Standard-View gesetzt.
	 *
	 * @param name
	 *            Name des NavigationViewControllers.
	 */
	protected NavigationViewController(String name) {
		super(name, NavigationViewController.class.getResource("NavigationView.fxml"));
	}

	/**
	 * Erstellt eine {@code NavigationViewController}-Instanz mit einem Namen
	 * (für Unterklassen).
	 *
	 * @param name
	 *            Name des NavigationViewControllers.
	 * @param urlOfView
	 *            URL des Views zur fxml-Datei.
	 */
	protected NavigationViewController(String name, URL urlOfView) {
		super(name, urlOfView);
	}

	/**
	 * Initialisiert den NavigationViewController.
	 */
	@Override
	protected void initialize() {
		dismissButton.setVisible(false);
		view.setTop(null);
		contentView.getChildren().addListener(new ListChangeListener<Node>() {
			@Override
			public void onChanged(Change<? extends Node> c) {
				titleLabel.setText((getTopViewController() != null) ? getTopViewController().getTitle() : null);
				if (contentView.getChildren().size() > 1) {
					dismissButton.setVisible(true);
				} else {
					dismissButton.setVisible(false);
				}
				if (contentView.getChildren().size() > 0) {
					view.setTop(navigationBar);
				} else {
					view.setTop(null);
				}
			}
		});
	}

	/**
	 * Setzt den initialen ViewController.
	 *
	 * Befinden sich noch andere ViewController auf dem Stack, werden diese
	 * vorher entfernt.
	 *
	 * @param viewController
	 *            Initialer ViewController.
	 */
	public void setInitialViewController(ViewController viewController) {
		ArrayList<Controller> tmp = new ArrayList<>(getChildControllers());
		for (Controller controller : tmp) {
			removeChildViewControllerFromView(((ViewController) controller), contentView);
		}

		if (viewController == null)
			return;

		addChildViewControllerToView(viewController, contentView);
	}

	/**
	 * Pusht einen ViewController auf den Stack und zeigt ihn an.
	 *
	 * @param viewController
	 *            ViewController, der angezeigt werden soll.
	 */
	public void pushViewController(ViewController viewController) {
		if (viewController == null)
			return;

		ViewController oldTopViewController = getTopViewController();

		if (oldTopViewController != null)
			oldTopViewController.viewWillDisappear();
		addChildViewControllerToView(viewController, contentView);
		if (oldTopViewController != null)
			oldTopViewController.viewDidDisappear();
	}

	/**
	 * Popt einen ViewController von dem Stack und zeigt den darunter liegenden
	 * ViewController an.
	 *
	 * @return ViewController, der entfernt wurde.
	 */
	@FXML
	public ViewController popViewController() {
		ViewController topViewController = getTopViewController();
		if (topViewController == null)
			return null;

		ViewController newViewController = null;
		if (getChildControllers().size() > 1)
			newViewController = (ViewController) getChildControllers().get(getChildControllers().size() - 2);
		if (newViewController != null)
			newViewController.viewWillAppear();

		removeChildViewControllerFromView(topViewController, contentView);

		if (newViewController != null)
			newViewController.viewDidAppear();

		return topViewController;
	}

	/**
	 * Gibt den obersten ViewController zurück ohne ihn vom Stack zu pushen.
	 *
	 * @return Oberster ViewController.
	 */
	public ViewController getTopViewController() {
		if (getChildControllers().isEmpty())
			return null;
		return (ViewController) getChildControllers().get(getChildControllers().size() - 1);
	}
}
