package com.alexanderthelen.applicationkit.gui;

import com.alexanderthelen.applicationkit.database.Data;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.net.URL;

/**
 * Abstrakte Basisklasse aller Anmeldungsformulare.
 *
 * Unterklassen, die von dieser Klasse erben, müssen {@link #getInputNodes()}
 * und {@link #getInputData()} implementieren (siehe
 * {@link FormViewController}). Außerdem müssen {@link #showRegistration()} und
 * {@link #loginUser(Data)} implementiert werden, wenn die Unterklasse keinem
 * AuthenticationViewController angehört.
 *
 * @author Alexander Thelen
 * @version 1.0
 */
public abstract class LoginViewController extends FormViewController {
	/**
	 * Erstellt eine {@code LoginViewController}-Instanz mit einem Namen (für
	 * Unterklassen).
	 *
	 * @param name
	 *            Name des LoginViewControllers.
	 * @param urlOfView
	 *            URL des Views zur fxml-Datei.
	 */
	protected LoginViewController(String name, URL urlOfView) {
		super(name, urlOfView);
	}

	/**
	 * Zeigt das Registrierungsformular an.
	 *
	 * Unterklassen, die keinem AuthenticationViewController angehören, müssen
	 * diese Methode überschreiben, sofern diese eine Möglichkeit (Button,
	 * Hyperlink, etc.) haben, diese Methode auszuführen.
	 *
	 * @throws Exception
	 *             wenn die Methode nicht implementiert wurde.
	 */
	@FXML
	public void showRegistration() throws Exception {
		if (getParentController() instanceof AuthenticationViewController) {
			((AuthenticationViewController) getParentController()).pushRegistrationViewController();
			return;
		}
		throw new Exception("Nicht implementiert.");
	}

	/**
	 * Bestätigt die Formulareingabe, indem {@link #loginUser(Data)} aufgerufen
	 * wird.
	 *
	 * Nachdem die Anmeldung versucht wurde, wird eine Meldung über den Erfolg
	 * angezeigt. Sollte die Unterklasse einem AuthenticationViewController
	 * angehören, übernimmt dieser das weitere Vorgehen, indem
	 * {@link AuthenticationViewController#onUserLogin()} aufgerufen wird.
	 */
	@Override
	@FXML
	public void acceptInput() {
		try {
			loginUser(getInputData());
			presentAlertDialog(Alert.AlertType.INFORMATION, "Anmeldung erfolgreich!",
					"Die Anmeldung des Nutzers konnte ausgeführt werden.", null, ButtonType.OK);
			if (getParentController() instanceof AuthenticationViewController) {
				((AuthenticationViewController) getParentController()).onUserLogin();
			}
		} catch (Exception e) {
			presentAlertDialog(Alert.AlertType.ERROR, "Anmeldung fehlgeschlagen!",
					"Die Anmeldung des Nutzers konnte nicht ausgeführt werden.", e, ButtonType.OK);
			getInputNodes().get(0).requestFocus();
		}
	}

	/**
	 * Versucht einen Nutzer anzumelden.
	 *
	 * Unterklassen, die keinem AuthenticationViewController angehören, müssen
	 * diese Methode implementieren. Ansonsten übernimmt der
	 * AuthenticationViewController das weitere Vorgehen, indem
	 * {@link AuthenticationViewController#loginUser(Data)} aufgerufen wird.
	 *
	 * @param data
	 *            Daten des Nutzers.
	 * @throws Exception
	 *             wenn der Nutzer nicht anmeldet werden kann oder die Methode
	 *             nicht implementiert wurde.
	 */
	public void loginUser(Data data) throws Exception {
		if (getParentController() instanceof AuthenticationViewController) {
			((AuthenticationViewController) getParentController()).loginUser(data);
			return;
		}
		throw new Exception("Nicht implementiert.");
	}
}
