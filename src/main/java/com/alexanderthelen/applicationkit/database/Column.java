package com.alexanderthelen.applicationkit.database;

/**
 * Die {@code Column}-Klasse ist der Bauplan einer Spalte in einer Tabelle
 * (siehe {@link com.alexanderthelen.applicationkit.database.Table}) eines
 * {@link com.alexanderthelen.applicationkit.gui.TableViewController}s.
 *
 * @author Alexander Thelen
 * @version 1.0
 */
public class Column {
	/**
	 * Name der Spalte der Datenbanktabelle.
	 */
	private String name;
	/**
	 * Datentyp der Spalte.
	 *
	 * Folgende Datentypen werden unterstützt:
	 * <ul>
	 * <li>Integer (SQLite-Datentyp)</li>
	 * <li>Boolean (0 und 1, wobei 1 {@code true} darstellt)</li>
	 * <li>Real (SQLite-Datentyp)</li>
	 * <li>Numeric (SQLite-Datentyp)</li>
	 * <li>Text (SQLite-Datentyp)</li>
	 * <li>Varchar</li>
	 * <li>Blob (SQLite-Datentyp)</li>
	 * </ul>
	 */
	private String type;
	/**
	 * Name der Datenbanktabelle, die diese Spalte besitzt.
	 */
	private String nameOfTable;

	/**
	 * Erstellt eine neue {@code Column} Instanz.
	 *
	 * @param name
	 *            Name der Spalte.
	 * @param type
	 *            Datentyp der Spalte.
	 * @param nameOfTable
	 *            Name der Tabelle, die diese Spalte besitzt.
	 */
	public Column(String name, String type, String nameOfTable) {
		this.name = name;
		this.type = type.toUpperCase();
		this.nameOfTable = nameOfTable;
	}

	/**
	 * Getter für {@code name}.
	 *
	 * @return Name der Spalte.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Getter für den kompletten Namen bestehend aus dem Namen der Tabelle,
	 * einem Punkt und dem Namen der Spalte.
	 *
	 * @return kompletter Name der Spalte.
	 */
	public String getFullName() {
		return nameOfTable + "." + name;
	}

	/**
	 * Getter für {@code type}.
	 *
	 * @return Datentyp der Spalte.
	 */
	public String getType() {
		return type;
	}

	/**
	 * Getter für {@code nameOfTable}.
	 *
	 * @return Name der Tabelle, die diese Spalte besitzt.
	 */
	public String getNameOfTable() {
		return nameOfTable;
	}
}
