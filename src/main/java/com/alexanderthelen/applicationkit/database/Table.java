package com.alexanderthelen.applicationkit.database;

import com.alexanderthelen.applicationkit.Application;
import com.alexanderthelen.applicationkit.gui.RowViewController;
import com.alexanderthelen.applicationkit.gui.TableViewController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.LinkedHashMap;

/**
 * {@code Table} ist die abstrakte Oberklasse jeder Tabelle einer
 * {@link TableViewController}-Instanz.
 *
 * Jede Klasse, die eine Datenbanktabelle (oder ggf. mehrere Datenbanktabellen)
 * visualisieren soll, sollte von dieser Klasse erben und als Model eines
 * TableViewControllers fungieren. Dabei muss die Unterklasse folgende Methoden
 * implementieren:
 * <ul>
 * <li>{@code public String }{@link #getSelectQueryForTableWithFilter(String)}</li>
 * <li>{@code public String }{@link #getSelectQueryForRowWithData(Data)}</li>
 * <li>{@code public void }{@link #insertRowWithData(Data)}
 * {@code throws SQLException}</li>
 * <li>{@code public void }{@link #updateRowWithData(Data, Data)}
 * {@code throws SQLException}</li>
 * <li>{@code public void }{@link #deleteRowWithData(Data)}
 * {@code throws SQLException}</li>
 * </ul>
 *
 */
public abstract class Table {
    /**
     * Titel der Tabelle.
     *
     * Dieser Titel erscheint in der {@link TableViewController} über der
     * Tabelle.
     */
    private String title;
    /**
     * Suchanfrage in der Tabelle.
     *
     * Wird vom {@code TableViewController} verwaltet, der diese Tabelle
     * besitzt.
     */
    private String filter;
    /**
     * Liste aller angezeigten Spalten der Tabelle. Der Schlüssel besteht aus
     * dem kompletten Namen der Spalte (siehe {@link Column#getFullName()}).
     *
     * Diese Liste muss alle Primärschlüssel der beteiligten Datenbanktabellen
     * enthalten!
     */
    private final LinkedHashMap<String, Column> columns = new LinkedHashMap<>();
    /**
     * Liste aller Zeilen in der Tabelle.
     */
    private final ObservableList<Row> rows = FXCollections.observableArrayList();
    /**
     * Gefilterte Liste aller Zeilen in der Tabelle (siehe {@link #filter}).
     */
    private final FilteredList<Row> filteredRows = new FilteredList<>(rows);
    /**
     * Sortierte gefilterte Liste aller Zeilen in der Tabelle.
     *
     * Diese Liste dient dem
     * {@link com.alexanderthelen.applicationkit.gui.TableViewController}.
     */
    private final SortedList<Row> sortedRows = new SortedList<>(filteredRows);

    /**
     * Baut die Tabelle anhand der Anfragen, die die Methoden
     * {@link #getSelectQueryForTableWithFilter(String)} und
     * {@link #getSelectQueryForRowWithData(Data)} (String)} zurückgeben, auf.
     *
     * Der Aufbau umfasst das Füllen der Liste {@link #columns}.
     *
     * @throws SQLException
     *             wenn eine Anfrage fehlschlägt.
     */
    public void build() throws SQLException {
        getRows().clear();
        getColumns().clear();

        String selectQueryForTableWithFilter = getSelectQueryForTableWithFilter(getFilter());

        if (selectQueryForTableWithFilter == null) {
            throw new SQLException(getClass().getName() + ".getSelectQueryForTableWithFilter(String filter) nicht implementiert.");
        }

        ResultSet resultSet = Application.getInstance().getConnection().executeQuery(selectQueryForTableWithFilter);
        ResultSetMetaData metaData = resultSet.getMetaData();
        Row row = new Row();
        for (int i = 1; i <= metaData.getColumnCount(); i++) {
            String name = metaData.getColumnName(i);
            String type = metaData.getColumnTypeName(i);
            String nameOfTable = metaData.getTableName(i);

            Column column = new Column(name, type, nameOfTable);
            getColumns().put(column.getFullName(), column);
            row.put(column, null);
        }
    }

    /**
     * Füllt die Tabelle anhand der Anfrage, die die Methode
     * {@link #getSelectQueryForTableWithFilter(String)} zurückgibt.
     *
     * @throws SQLException
     *             wenn eine Anfrage fehlschlägt.
     */
    public void fill() throws SQLException {
        if (getColumns().size() == 0)
            build();
        getRows().clear();

        ResultSet resultSet = Application.getInstance().getConnection()
                .executeQuery(getSelectQueryForTableWithFilter(getFilter()));
        while (resultSet.next()) {
            Row row = new Row();
            int i = 1;
            for (Column column : getColumns().values()) {
                if (column.getType().equals("BLOB")) {
                    row.put(column, resultSet.getBytes(i));
                } else {
                    row.put(column, resultSet.getObject(i));
                }
                i++;
            }
            rows.add(row);
        }

    }

    /**
     * Fügt eine Zeile (siehe {@link Row}) der Tabelle hinzu.
     *
     * @param row
     *            Zeile, die hinzugefügt werden soll.
     * @throws SQLException
     *             wenn die Zeile nicht hinzugefügt werden kann.
     */
    public final void addRow(Row row) throws SQLException {
        insertRowWithData(row.getData());
    }

    /**
     * Ersetzt die alte Zeile mit der neuen Zeile.
     *
     * @param oldRow
     *            Alte Zeile.
     * @param newRow
     *            Neue Zeile.
     * @throws SQLException
     *             wenn die Zeile nicht aktualisiert werden kann.
     */
    public final void updateRow(Row oldRow, Row newRow) throws SQLException {
        updateRowWithData(oldRow.getData(), newRow.getData());
    }

    /**
     * Löscht eine Zeile.
     *
     * @param row
     *            Zu löschende Zeile.
     * @throws SQLException
     *             wenn die Zeile nicht gelöscht werden kann.
     */
    public final void deleteRow(Row row) throws SQLException {
        deleteRowWithData(row.getData());
    }

    /**
     * Getter für {@link #title}.
     *
     * @return Name der Tabelle.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Setter für {@link #title}.
     *
     * @param title
     *            Name der Tabelle.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Getter für {@link #filter}.
     *
     * @return Suchanfrage in der Tabelle.
     */
    public String getFilter() {
        return filter;
    }

    /**
     * Setter für {@link #filter}.
     *
     * @param filter
     *            Suchanfrage in der Tabelle.
     */
    public void setFilter(String filter) {
        this.filter = filter;
    }

    /**
     * Getter für {@link #columns}.
     *
     * @return Liste aller angezeigten Spalten der Tabelle.
     */
    public LinkedHashMap<String, Column> getColumns() {
        return columns;
    }

    /**
     * Getter für {@link #rows}.
     *
     * @return Lister aller Zeilen in der Tabelle.
     */
    public ObservableList<Row> getRows() {
        return rows;
    }

    /**
     * Getter für {@link #filteredRows}.
     *
     * @return Gefilterte Liste aller Zeilen in der Tabelle.
     */
    public FilteredList<Row> getFilteredRows() {
        return filteredRows;
    }

    /**
     * Getter für {@link #sortedRows}.
     *
     * @return Sortierte gefilterte Liste aller Zeilen in der Tabelle.
     */
    public SortedList<Row> getSortedRows() {
        return sortedRows;
    }

    /**
     * Gibt die Select-Anfrage zur Erstellung der Tabellenansicht (siehe
     * {@link com.alexanderthelen.applicationkit.gui.TableViewController})
     * zurück.
     *
     * Diese Anfrage bestimmt, welche Spalten (siehe {@link #columns}) und
     * Zeilen angezeigt werden. Sie muss von allen beteiligten Datenbanktabellen
     * den Primärschlüssel beinhalten. Nur so können andere Anfragen richtig
     * ausgeführt werden.
     *
     * @param filter
     *            Anfragenfilter (siehe {@link #filter}).
     * @return Select-Anfrage.
     * @throws SQLException
     *             wenn ein SQL-Fehler auftritt.
     */
    public abstract String getSelectQueryForTableWithFilter(String filter) throws SQLException;

    /**
     * Gibt die Select-Anfrage zur Erstellung der Zeilenansicht (siehe
     * {@link RowViewController}) zurück.
     *
     * {@code data} beinhaltet dabei die Daten einer Zeile aus einer Tabelle.
     * Dies ist für Where-Bedingungen in der Select-Anfrage unerlässlich. Diese
     * Anfrage bestimmt, welche Spalten editierbar sind und somit in dem
     * {@link RowViewController} bearbeitet werden können. Sie muss von allen
     * beteiligten Datenbanktabellen den Primärschlüssel beinhalten. Nur so
     * können andere Anfragen richtig ausgeführt werden.
     *
     * @param data
     *            Daten der Zeile. Der Schlüssel ist der komplette Name der
     *            Spalte, in dem der dazugehörige Wert steht (siehe
     *            {@link #getSelectQueryForTableWithFilter(String)}).
     * @return Select-Anfrage.
     * @throws SQLException
     *             wenn ein SQL-Fehler auftritt.
     */
    public abstract String getSelectQueryForRowWithData(Data data) throws SQLException;

    /**
     * Fügt die gegebenen Daten an der richtigen Stelle in der Datenbank ein.
     *
     * Diese Methode fügt die in {@code data} stehenden Informationen in eine
     * oder ggf. mehrere Datenbanktabellen ein. Dazu werden eine oder mehrere
     * Insert-Anfragen ausgeführt.
     *
     * @param data
     *            Daten der Zeile, der hinzugefügt werden soll. Der Schlüssel
     *            ist der komplette Name der Spalte, in dem der dazugehörige
     *            Wert steht (siehe
     *            {@link #getSelectQueryForRowWithData(Data)}).
     * @throws SQLException
     *             wenn ein SQL-Fehler auftritt.
     */
    public abstract void insertRowWithData(Data data) throws SQLException;

    /**
     * Überschreibt die in der jeweiligen Zeile stehenden alten Informationen
     * mit den neuen Informationen an der richtigen Stelle in der Datenbank.
     *
     * Diese Methode aktualisiert Informationen in einer oder ggf. mehreren
     * Datenbanktabellen. Dazu werden eine oder mehrere Update-Anfragen
     * ausgeführt.
     *
     * @param oldData
     *            Alte Daten, die durch die neuen Daten übeschrieben werden
     *            soll. Die Schlüssel dieser {@link Data}-Instanz sind die
     *            kompletten Spaltennamen der zugehörigen Tabelle (siehe
     *            {@link #getSelectQueryForRowWithData(Data)}).
     * @param newData
     *            Neue Daten, die die alten Daten überschreiben soll. Die
     *            Schlüssel dieser {@link Data}-Instanz sind die kompletten
     *            Spaltennamen der zugehörigen Tabelle (siehe
     *            {@link #getSelectQueryForRowWithData(Data)}).
     * @throws SQLException
     *             wenn ein SQL-Fehler auftritt.
     */
    public abstract void updateRowWithData(Data oldData, Data newData) throws SQLException;

    /**
     * Löscht die entsprechenden Zeilen an der richtigen Stelle in der Datenbank
     * anhand von übergebenen Daten.
     *
     * @param data
     *            Daten, die Hinweise auf die entsprechenden Datenbankeinträge
     *            geben, die gelöscht werden sollen. Die Schlüssel dieser
     *            {@link Data}-Instanz sind die kompletten Spaltennamen der
     *            zugehörigen Tabelle (siehe
     *            {@link #getSelectQueryForTableWithFilter(String)}).
     * @throws SQLException
     *             wenn ein SQL-Fehler auftritt.
     */
    public abstract void deleteRowWithData(Data data) throws SQLException;
}
