package com.alexanderthelen.applicationkit.database;

import java.sql.SQLException;
import java.util.LinkedHashMap;

/**
 * {@code Data} ist eine Klasse, die eine einfache {@code LinkedHashMap}
 * erweitert.
 *
 * Dabei betrifft die Erweiterung nur die Vorgabe der Datentypen: {@link String}
 * für die Schlüssel und {@link Object} für die Werte.
 *
 * @author Alexander Thelen
 * @version 1.0
 */
public class Data extends LinkedHashMap<String, Object> {
	/**
	 * Erstellt eine {@code Data}-Instanz.
	 */
	public Data() {
		super();
	}

	/**
	 * Gibt zu einem übergebenem Schlüssel die dazugehörige Information zurück.
	 * 
	 * @param key
	 *            Schlüssel.
	 * @return Information, die zum Schlüssel gehört.
	 * @throws SQLException
	 *             wenn der Schlüssel nicht gefunden werden kann.
	 */
	public Object get(String key) throws SQLException {
		if (!containsKey(key))
			throw new SQLException("Schlüssel \"" + key + "\" nicht vorhanden.");
		return super.get(key);
	}
}
