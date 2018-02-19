package com.alexanderthelen.applicationkit.gui;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;

/**
 * Die abstrakte {@code TreeViewController}-Klasse stellt eine Baumstruktur
 * (siehe {@link TreeView} in der Java-Dokumentation) bereit, deren Elemente
 * auswählbar sind.
 *
 * Unterklassen müssen die Methode
 * {@link #onSelectionChange(TreeItem, TreeItem)} implementieren.
 *
 * @param <T>
 *            Datentyp der TreeItems.
 */
public abstract class TreeViewController<T> extends ViewController {
	/**
	 * View des TreeViewController.
	 */
	@FXML
	protected BorderPane view;
	/**
	 * TreeView, der die Baumstruktur visualisiert.
	 */
	@FXML
	protected TreeView<T> treeView;

	/**
	 * Erstellt eine {@code TreeViewController}-Instanz mit einem Namen (für
	 * Unterklassen).
	 *
	 * @param name
	 *            Name des TreeViewControllers.
	 */
	protected TreeViewController(String name) {
		super(name, TreeViewController.class.getResource("TreeView.fxml"));
	}

	/**
	 * Initialisiert den TreeViewController.
	 */
	@Override
	protected void initialize() {
		super.initialize();
		treeView.setCellFactory(tree -> {
			TreeCell<T> cell = new TreeCell<T>() {
				@Override
				public void updateItem(T item, boolean empty) {
					super.updateItem(item, empty);
					if (empty || item == null) {
						setText(null);
						setGraphic(null);
					} else {
						setText(getTextForItem(item));
						setGraphic(getGraphicForItem(item));
					}
				}
			};
			cell.addEventFilter(MouseEvent.MOUSE_PRESSED, t -> {
				if (t.getButton() == MouseButton.PRIMARY && t.getClickCount() >= 2) {
					t.consume();
				}
			});
			return cell;
		});
		treeView.getSelectionModel().selectedItemProperty().addListener((observable, oldItem, newItem) -> {
			onSelectionChange(oldItem, newItem);
		});
	}

	/**
	 * Getter für das Wurzelelement der Baumstruktur.
	 *
	 * @return Wurzelelement.
	 */
	protected TreeItem<T> getRootItem() {
		return treeView.getRoot();
	}

	/**
	 * Gibt eine {@link Node}-Instanz zurück, die im TreeView vor dem TreeItem
	 * angezeigt wird.
	 *
	 * @param item
	 *            TreeItem.
	 * @return {@link Node}-Instanz (siehe Java-Dokumentation).
	 */
	protected Node getGraphicForItem(T item) {
		return null;
	}

	/**
	 * Gibt den Text zurück, der das TreeItem im TreeView repräsentiert.
	 *
	 * @param item
	 *            TreeItem.
	 * @return Text.
	 */
	protected abstract String getTextForItem(T item);

	/**
	 * Methode, die aufgerufen wird, sobald ein neues Element im TreeView
	 * ausgewählt wurde.
	 *
	 * @param oldItem
	 *            Altes Element.
	 * @param newItem
	 *            Neues Element.
	 */
	protected abstract void onSelectionChange(TreeItem<T> oldItem, TreeItem<T> newItem);
}
