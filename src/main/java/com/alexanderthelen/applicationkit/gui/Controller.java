package com.alexanderthelen.applicationkit.gui;

import javafx.fxml.FXML;

import java.util.ArrayList;

/**
 * Basisklasse des Controller aller MVCs.
 *
 * Ein Controller eines MVC-Gerüsts (Model-View-Controller) ist zuständig für
 * die Synchronisierung von Model und View. Zusätzlich kann er mit anderen
 * Controllern auf gleicher Ebene und seinen {@link #childControllers} und
 * seinem {@link #parentController} kommunizieren. Jeder Controller eines MVC
 * sollte von dieser Klasse erben. Die {@link #childControllers}-Liste muss
 * sämtliche Controller beinhalten, die in dem übergeordneten Controller
 * eingebettet sind. Zusätzlich muss {@link #parentController} der jeweiligen
 * untergeordneten Controller aktualisiert werden. Getter und Setter stehen für
 * diese Zwecke bereit.
 *
 * @author Alexander Thelen
 * @version 1.0
 */
public class Controller {
	/**
	 * Eindeutiger Name des Controllers zu Verwaltungszwecken.
	 */
	private String name;
	/**
	 * Titel des Controllers.
	 */
	private String title;
	/**
	 * Übergeordneter Controller.
	 */
	private Controller parentController;
	/**
	 * Liste aller untergeordneten Controller.
	 */
	private ArrayList<Controller> childControllers = new ArrayList<>();

	/**
	 * Erstellt eine {@code Controller}-Instanz mit einem Namen.
	 *
	 * @param name
	 *            Name des Controllers.
	 */
	public Controller(String name) {
		this.name = name;
	}

	/**
	 * Initialisiert den View.
	 *
	 * Diese Methode wird aufgerufen, sobald der View geladen wurde und alle
	 * FXML-Annotations verarbeitet wurden. Unterklassen können diese Methode
	 * überschreiben, um einmalige Konfigurationen zu treffen. Für Anweisungen,
	 * die jedes Mal vor Erscheinen des Views ausgeführt werden sollen, siehe
	 * {@link WindowController#windowWillAppear()},
	 * {@link ViewController#viewWillAppear()} oder andere Methoden.
	 *
	 */
	@FXML
	protected void initialize() {

	}

	/**
	 * Fügt einen Controller der {@link #childControllers}-Liste hinzu.
	 *
	 * Gleichzeitig wird der {@link #parentController} des Controllers gesetzt.
	 *
	 * @param controller
	 *            Controller, der hinzugefügt werden soll.
	 */
	protected void addChildController(Controller controller) {
		getChildControllers().add(controller);
		controller.setParentController(this);
	}

	/**
	 * Entfernt einen Controller aus der {@link #childControllers}-Liste.
	 *
	 * Gleichzeitig wird der entsprechende {@link #parentController} auf
	 * {@code null} gesetzt.
	 *
	 * @param controller
	 *            Controller, der entfernt werden soll.
	 */
	protected void removeChildController(Controller controller) {
		getChildControllers().remove(controller);
		controller.setParentController(null);
	}

	/**
	 * Getter für {@link #name}.
	 *
	 * @return Name des Controllers.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Setter für {@link #name}.
	 *
	 * @param name
	 *            Name des Controllers.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Getter für {@link #title}.
	 *
	 * @return Titel des Controllers.
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Setter für {@link #title}.
	 *
	 * @param title
	 *            Titel des Controllers.
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Getter für {@link #parentController}.
	 *
	 * @return Übergeordneter Controller.
	 */
	public Controller getParentController() {
		return parentController;
	}

	/**
	 * Setter für {@link #parentController}.
	 *
	 * @param parentController
	 *            Übergeordneter Controller.
	 */
	public void setParentController(Controller parentController) {
		this.parentController = parentController;
	}

	/**
	 * Getter für {@link #childControllers}.
	 *
	 * @return Liste aller untergeordneten Controller.
	 */
	public ArrayList<Controller> getChildControllers() {
		return childControllers;
	}

	/**
	 * Gibt den Namen, die Klasse und den Speicherort der Instanz aus.
	 *
	 * @return Name, Klasse und Speicherort des Controllers.
	 */
	@Override
	public String toString() {
		return getName() + " (" + super.toString() + ")";
	}
}
