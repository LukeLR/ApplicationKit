package com.alexanderthelen.applicationkit.gui;

import com.alexanderthelen.applicationkit.database.Data;

import java.sql.SQLException;

/**
 * Die abstrakte {@code AuthenticationViewController}-Klasse stellt eine
 * einfache Oberklasse bereit, die für Authentifizierungen (Anmeldung und
 * Registrierung) genutzt werden kann.
 *
 * Unterklassen müssen die Methoden {@link #loginUser(Data)} und
 * {@link #registerUser(Data)} implementieren. Nachdem eine Instanz erzeugt
 * wurde, müssen mit den Methoden
 * <ul>
 * <li>{@link #setMainViewController(ViewController)}</li>
 * <li>{@link #setLoginViewController(LoginViewController)}</li>
 * <li>{@link #setRegistrationViewController(RegistrationViewController)}</li>
 * </ul>
 * die entsprechenden ViewController gesetzt werden.
 *
 * @author Alexander Thelen
 * @version 1.0
 */
public abstract class AuthenticationViewController extends NavigationViewController {
	/**
	 * LoginViewController für die Anmeldung.
	 */
	private LoginViewController loginViewController;
	/**
	 * RegistrationViewController für die Registrierung.
	 */
	private RegistrationViewController registrationViewController;
	/**
	 * ViewController, der angezeigt wird, sobald die Anmeldung erfolgreich war.
	 */
	private ViewController mainViewController;

	/**
	 * Erstellt eine {@code AuthenticationViewController}-Instanz mit einem
	 * Namen (per {@code createWithName(String)} der Unterklasse).
	 *
	 * @param name
	 *            Name des AuthenticationViewController.
	 */
	protected AuthenticationViewController(String name) {
		super(name);
	}

	/**
	 * Meldet einen Nutzer an.
	 *
	 * Die Daten des Nutzers sind in {@code data} gespeichert. Sollte die
	 * Anmeldung nicht erfolgreich sein, muss eine {@code SQLException} geworfen
	 * werden.
	 *
	 * @param data
	 *            Daten des Nutzers.
	 * @throws SQLException
	 *             wenn die Anmeldung fehlschlägt.
	 */
	public abstract void loginUser(Data data) throws SQLException;

	/**
	 * Wird ausgeführt, sobald die Anmeldung erfolgreich war.
	 */
	public void onUserLogin() {
		if (getParentController() instanceof WindowController) {
			((WindowController) getParentController()).setViewController(mainViewController);
		}
	}

	/**
	 * Registriert einen Nutzer.
	 *
	 * Die Daten des neuen Nutzers sind in {@code data} gespeichert. Sollte die
	 * Registrierung nicht erfolgreich sein, muss eine {@code SQLException}
	 * geworfen werden.
	 *
	 * @param data
	 *            Daten des neuen Nutzers.
	 * @throws SQLException
	 *             wenn die Registrierung fehlschlägt.
	 */
	public abstract void registerUser(Data data) throws SQLException;

	/**
	 * Wird ausgeführt, sobald die Registrierung erfolgreich war.
	 */
	public void onUserRegister() {
		popRegistrationViewController();
	}

	/**
	 * Zeigt den RegistrationViewController an.
	 */
	public void pushRegistrationViewController() {
		pushViewController(registrationViewController);
	}

	/**
	 * Nimmt den RegistrationViewController vom Stack und zeigt den
	 * LoginViewController an.
	 *
	 * @return RegistrationViewController.
	 */
	public RegistrationViewController popRegistrationViewController() {
		if (getTopViewController() != registrationViewController)
			return null;
		return (RegistrationViewController) popViewController();
	}

	/**
	 * Pusht einen ViewController auf den Stack und zeigt ihn an.
	 *
	 * Ggf. werden die Eingabefelder zurückgesetzt.
	 *
	 * @param viewController
	 *            ViewController, der angezeigt werden soll.
	 */
	@Override
	public void pushViewController(ViewController viewController) {
		if (getTopViewController() instanceof FormViewController) {
			((FormViewController) getTopViewController()).resetInput();
		}
		super.pushViewController(viewController);
	}

	/**
	 * Popt einen ViewController von dem Stack und zeigt den darunter liegenden
	 * ViewController an.
	 *
	 * Ggf. werden die Eingabefelder zurückgesetzt.
	 *
	 * @return ViewController, der entfernt wurde.
	 */
	@Override
	public ViewController popViewController() {
		if (getTopViewController() instanceof FormViewController) {
			((FormViewController) getTopViewController()).resetInput();
		}
		return super.popViewController();
	}

	/**
	 * Getter für {@link #loginViewController}.
	 *
	 * @return LoginViewController.
	 */
	public LoginViewController getLoginViewController() {
		return loginViewController;
	}

	/**
	 * Setter für {@link #loginViewController}.
	 *
	 * @param loginViewController
	 *            LoginViewController.
	 */
	public void setLoginViewController(LoginViewController loginViewController) {
		setInitialViewController(loginViewController);
		this.loginViewController = loginViewController;
	}

	/**
	 * Getter für {@link #registrationViewController}.
	 *
	 * @return RegistrationViewController.
	 */
	public RegistrationViewController getRegistrationViewController() {
		return registrationViewController;
	}

	/**
	 * Setter für {@link #registrationViewController}.
	 *
	 * @param registrationViewController
	 *            RegistrationViewController.
	 */
	public void setRegistrationViewController(RegistrationViewController registrationViewController) {
		if (getTopViewController() == this.registrationViewController) {
			popViewController();
			pushViewController(registrationViewController);
		}
		this.registrationViewController = registrationViewController;
	}

	/**
	 * Getter für {@link #mainViewController}.
	 *
	 * @return ViewController.
	 */
	public ViewController getMainViewController() {
		return mainViewController;
	}

	/**
	 * Setter für {@link #mainViewController}.
	 *
	 * @param viewController
	 *            ViewController.
	 */
	public void setMainViewController(ViewController viewController) {
		mainViewController = viewController;
	}
}
