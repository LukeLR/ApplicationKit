package com.alexanderthelen.applicationkit.database;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * {@code Row} ist eine Klasse, die eine einfache {@code LinkedHashMap}
 * erweitert.
 *
 * Dabei betrifft die Erweiterung nur die Vorgabe der Datentypen: {@link Column}
 * für die Schlüssel und {@link Object} für die Werte. Eine {@link Row}-Instanz
 * stellt eine Zeile in einer {@link Table}-Instanz dar.
 *
 * @author Alexander Thelen
 * @version 1.0
 */
public class Row extends LinkedHashMap<Column, Object> {
	/**
	 * Erstellt eine {@code Row}-Instanz.
	 */
	public Row() {
		super();
	}

	/**
	 * Gibt die Daten der Zeile zurück.
	 *
	 * Die jeweilige Information wird mit dem kompletten Spaltennamen der
	 * Tabelle abgefragt.
	 *
	 * @return Daten.
	 */
	public Data getData() {
		Data data = new Data();
		for (Map.Entry<Column, Object> entry : entrySet()) {
			data.put(entry.getKey().getFullName(), entry.getValue());
		}
		return data;
	}
}
