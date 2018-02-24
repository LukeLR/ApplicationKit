package com.alexanderthelen.applicationkit.gui;

import com.alexanderthelen.applicationkit.database.Data;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.net.URL;

/**
 * Basisklasse aller Registrierungsformulare.
 *
 * Unterklassen, die von dieser Klasse erben, müssen {@link #getInputNodes()}
 * und {@link #getInputData()} implementieren (siehe
 * {@link FormViewController}). Außerdem müssen {@link #showLogin()} und
 * {@link #registerUser(Data)} implementiert werden, wenn die Unterklasse keinem
 * AuthenticationViewController angehört.
 *
 * @author Alexander Thelen
 * @version 1.0
 */
public abstract class RegistrationViewController extends FormViewController {
	/**
	 * Erstellt eine {@code RegistrationViewController}-Instanz mit einem Namen
	 * (für Unterklassen).
	 *
	 * @param name
	 *            Name des RegistrationViewControllers.
	 * @param urlOfView
	 *            URL des Views zur fxml-Datei.
	 */
	protected RegistrationViewController(String name, URL urlOfView) {
		super(name, urlOfView);
	}

	/**
	 * Zeigt das Anmeldungsformular an.
	 *
	 * Unterklassen, die keinem AuthenticationViewController angehören, müssen
	 * diese Methode überschreiben, sofern diese eine Möglichkeit (Button,
	 * Hyperlink, etc.) haben, diese Methode auszuführen.
	 *
	 * @throws Exception
	 *             wenn die Methode nicht implementiert wurde.
	 */
	@FXML
	public void showLogin() throws Exception {
		if (getParentController() instanceof AuthenticationViewController) {
			((AuthenticationViewController) getParentController()).popRegistrationViewController();
		}
		throw new Exception("Nicht implementiert.");
	}

	/**
	 * Bestätigt die Formulareingabe, indem {@link #registerUser(Data)}
	 * aufgerufen wird.
	 *
	 * Nachdem die Registrierung versucht wurde, wird eine Meldung über den
	 * Erfolg angezeigt. Sollte die Unterklasse einem
	 * AuthenticationViewController angehören, übernimmt dieser das weitere
	 * Vorgehen, indem {@link AuthenticationViewController#onUserRegister()}
	 * aufgerufen wird.
	 */
	@Override
	@FXML
	public void acceptInput() {
		try {
			registerUser(getInputData());
			presentAlertDialog(Alert.AlertType.INFORMATION, "Registrierung erfolgreich!",
					"Die Registrierung des Nutzers konnte ausgeführt werden.", null, ButtonType.OK);
			if (getParentController() instanceof AuthenticationViewController) {
				((AuthenticationViewController) getParentController()).onUserRegister();
			}
			resetInput();
		} catch (Exception e) {
			presentAlertDialog(Alert.AlertType.ERROR, "Registrierung fehlgeschlagen!",
					"Die Registrierung des Nutzers konnte nicht ausgeführt werden.", e, ButtonType.OK);
			getInputNodes().get(0).requestFocus();
		}
	}

	/**
	 * Versucht einen Nutzer zu registrieren.
	 *
	 * Unterklassen, die keinem AuthenticationViewController angehören, müssen
	 * diese Methode implementieren. Ansonsten übernimmt der
	 * AuthenticationViewController das weitere Vorgehen, indem
	 * {@link AuthenticationViewController#registerUser(Data)} aufgerufen wird.
	 *
	 * @param data
	 *            Daten des Nutzers.
	 * @throws Exception
	 *             wenn der Nutzer nicht registriert werden kann oder die
	 *             Methode nicht implementiert wurde.
	 */
	public void registerUser(Data data) throws Exception {
		if (getParentController() instanceof AuthenticationViewController) {
			((AuthenticationViewController) getParentController()).registerUser(data);
			return;
		}
		throw new Exception("Nicht implementiert.");
	}
}
