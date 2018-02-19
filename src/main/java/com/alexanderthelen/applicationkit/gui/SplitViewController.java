package com.alexanderthelen.applicationkit.gui;

import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.scene.control.SplitPane;

import java.io.IOException;

/**
 * Basisklasse aller geteilten Container für ViewController.
 *
 * Mit folgenden Methoden können ViewController hinzugefügt oder entfernt
 * werden:
 * <ul>
 * <li>{@link #addChildViewControllerToView(ViewController)}</li>
 * <li>{@link #removeChildViewControllerFromView(ViewController)}
 * (ViewController)}</li>
 * </ul>
 * Die Orientierung kann durch entsprechende Getter- und Setter-Methoden
 * verwaltet werden.
 */
public class SplitViewController extends ViewController {
	/**
	 * View des SplitViewControllers.
	 */
	@FXML
	protected SplitPane view;

	/**
	 * Orientierung des SplitViews.
	 */
	private Orientation orientation;

	/**
	 * Erstellt eine {@code SplitViewController}-Instanz mit einem Namen.
	 *
	 * Außerdem wird der View geladen.
	 *
	 * @param name
	 *            Name des SplitViewControllers.
	 * @return SplitViewController.
	 * @throws IOException
	 *             wenn der View nicht geladen werden kann.
	 */
	public static SplitViewController createWithName(String name) throws IOException {
		SplitViewController controller = new SplitViewController(name);
		controller.loadView();
		return controller;
	}

	/**
	 * Erstellt eine {@code SplitViewController}-Instanz mit einem Namen (per
	 * {@link #createWithName(String)}).
	 *
	 * Gleichzeitig wird der Standard-View gesetzt.
	 *
	 * @param name
	 *            Name des SplitViewControllers.
	 */
	protected SplitViewController(String name) {
		super(name, SplitViewController.class.getResource("SplitView.fxml"));
	}

	/**
	 * Initialisiert den SplitViewController.
	 */
	@Override
	@FXML
	protected void initialize() {
		orientation = view.getOrientation();
		view.orientationProperty().addListener((observable, oldValue, newValue) -> orientation = newValue);
	}

	/**
	 * Getter für {@link #orientation}.
	 *
	 * @return Orientierung des SplitViews.
	 */
	public Orientation getOrientation() {
		return orientation;
	}

	/**
	 * Setter für {@link #orientation}.
	 *
	 * @param orientation
	 *            Orientierung des SplitViews.
	 */
	public void setOrientation(Orientation orientation) {
		view.setOrientation(orientation);
		this.orientation = orientation;
	}
}
