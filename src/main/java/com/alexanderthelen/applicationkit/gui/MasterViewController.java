package com.alexanderthelen.applicationkit.gui;

import javafx.scene.control.TreeItem;

import java.util.ArrayList;

/**
 * Abstrakte Klasse für die Masteransicht in einer
 * {@link MasterDetailViewController}-Instanz.
 *
 * Die Oberklasse {@link TreeViewController} wird mit dem Datentyp
 * {@link ViewController} versehen, sodass die TreeItems diesen Typ besitzen.
 * Unterklassen müssen {@link #getTreeItems()} implementieren, sodass der
 * TreeView überhaupt aufgebaut werden kann.
 */
public abstract class MasterViewController extends TreeViewController<ViewController> {
	/**
	 * Erstellt eine {@code MasterViewController}-Instanz mit einem Namen (für
	 * Unterklassen).
	 *
	 * @param name
	 *            Name des MasterViewControllers.
	 */
	protected MasterViewController(String name) {
		super(name);
	}

	/**
	 * Initialisiert den MasterViewController.
	 */
	@Override
	protected void initialize() {
		super.initialize();
		view.setMinWidth(200);
		view.setMaxWidth(200);
		TreeItem<ViewController> root = new TreeItem<>();
		root.setExpanded(true);
		treeView.setShowRoot(false);
		treeView.setRoot(root);
	}

	/**
	 * Zeigt den zum neu ausgewählten TreeItem gehörenden ViewController in der
	 * Detailansicht an.
	 *
	 * @param oldItem
	 *            Altes Element.
	 * @param newItem
	 *            Neues Element.
	 */
	@Override
	protected void onSelectionChange(TreeItem<ViewController> oldItem, TreeItem<ViewController> newItem) {
		getMasterDetailViewController().setDetailViewController(newItem == null ? null : newItem.getValue());
	}

	/**
	 * Getter für die {@link MasterDetailViewController}-Instanz, die diesen
	 * MasterViewController besitzt.
	 *
	 * @return MasterDetailViewController.
	 */
	public MasterDetailViewController getMasterDetailViewController() {
		return (MasterDetailViewController) getParentController();
	}

	/**
	 * Setzt den Titel des ViewControllers als Text für das TreeItem.
	 *
	 * @param item
	 *            TreeItem.
	 * @return Text.
	 */
	@Override
	protected String getTextForItem(ViewController item) {
		return item.getTitle();
	}

	/**
	 * Ruft die Methode {@link #buildView()} jedes Mal kurz bevor dem Erscheinen
	 * auf.
	 */
	@Override
	public void viewWillAppear() {
		super.viewWillAppear();
		buildView();
	}

	/**
	 * Baut die Baumstruktur auf.
	 */
	private void buildView() {
		getRootItem().getChildren().clear();
		for (TreeItem<ViewController> treeItem : getTreeItems()) {
			getRootItem().getChildren().add(treeItem);
		}
	}

	/**
	 * Gibt die Liste der TreeItems zurück.
	 *
	 * Jedes TreeItem besitzt den bei Auswahl anzuzeigenden ViewController.
	 *
	 * @return Liste der TreeItems.
	 */
	protected abstract ArrayList<TreeItem<ViewController>> getTreeItems();
}
