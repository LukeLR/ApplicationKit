package com.alexanderthelen.applicationkit.gui;

import java.io.IOException;

/**
 * Die {@code MasterDetailViewController}-Klasse vereinfacht den Umgang eines
 * MasterViewControllers mit entsprechenden ViewControllern für Detailansichten.
 *
 * Wählt man im MasterViewController ein Element aus, ändert sich der
 * {@link #detailViewController}. Dieses Verhalten wird in der
 * {@link MasterViewController}-Instanz durch Implementierung der
 * {@link MasterViewController#getTreeItems()} festgelegt.
 */
public class MasterDetailViewController extends SplitViewController {
	/**
	 * Masteransicht.
	 */
	private MasterViewController masterViewController;
	/**
	 * Detailansicht.
	 */
	private ViewController detailViewController;

	/**
	 * Erstellt eine {@code MasterViewController}-Instanz mit einem Namen.
	 *
	 * Außerdem wird der View geladen.
	 *
	 * @param name
	 *            Name des MasterViewControllers.
	 * @return MasterViewController.
	 * @throws IOException
	 *             wenn der View nicht geladen werden kann.
	 */
	public static MasterDetailViewController createWithName(String name) throws IOException {
		MasterDetailViewController controller = new MasterDetailViewController(name);
		controller.loadView();
		return controller;
	}

	/**
	 * Erstellt eine {@code MasterDetailViewController}-Instanz mit einem Namen
	 * (per {@link #createWithName(String)}).
	 *
	 * @param name
	 *            Name des MasterDetailViewControllers.
	 */
	protected MasterDetailViewController(String name) {
		super(name);
	}

	/**
	 * Getter für {@link #masterViewController}.
	 *
	 * @return MasterViewController.
	 */
	public MasterViewController getMasterViewController() {
		return masterViewController;
	}

	/**
	 * Setter für {@link #masterViewController}.
	 *
	 * @param masterViewController
	 *            MasterViewController.
	 */
	public void setMasterViewController(MasterViewController masterViewController) {
		if (this.masterViewController != null)
			removeChildViewControllerFromView(this.masterViewController);
		this.masterViewController = masterViewController;
		if (masterViewController != null)
			addChildViewControllerToViewAtIndex(masterViewController, view, 0);
	}

	/**
	 * Getter für {@link #detailViewController}.
	 *
	 * @return DetailViewController.
	 */
	public ViewController getDetailViewController() {
		return detailViewController;
	}

	/**
	 * Setter für {@link #detailViewController}.
	 *
	 * @param detailViewController
	 *            DetailViewController.
	 */
	public void setDetailViewController(ViewController detailViewController) {
		if (this.detailViewController != null)
			removeChildViewControllerFromView(this.detailViewController);
		this.detailViewController = detailViewController;
		if (detailViewController != null)
			addChildViewControllerToViewAtIndex(detailViewController, view, 1);
	}
}
