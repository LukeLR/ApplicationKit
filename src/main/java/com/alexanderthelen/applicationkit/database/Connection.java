package com.alexanderthelen.applicationkit.database;

import java.io.IOException;
import java.sql.*;

/**
 * Die {@code Connection}-Klasse vereinfacht den Umgang mit SQLite-Datenbanken.
 */
public class Connection {
    /**
     * URL zur Datenbank.
     */
    private final String url;
    /**
     * Von Java implementierte Verbindung zur Datenbank (siehe
     * {@link java.sql.Connection} der Java-Dokumentation).
     */
    private final java.sql.Connection rawConnection;

    /**
     * Erstellt eine neue {@code Connection}-Instanz mit einer URL. Eine neue
     * Instanz wird mit den gegebenen Parametern erstellt und anschließend
     * initialisiert (siehe {@link #initialize()}).
     *
     * @param url
     *            URL zur Datenbank
     * @throws IOException
     *             wenn die Validierung fehlschlägt.
     * @throws SQLException
     *             wenn die Verbindung nicht aufgebaut werden kann.
     */
    public Connection(String url) throws IOException, SQLException {
        this.url = url;
        rawConnection = DriverManager.getConnection(this.url);
        initialize();
    }

    /**
     * Schließt die Verbindung zur Datenbank.
     *
     * @throws SQLException
     *             wenn die Verbindung nicht geschlossen werden kann.
     */
    public void close() throws SQLException {
        rawConnection.close();
    }

    /**
     * Gibt den Status der Verbindung zurück.
     *
     * @return {@code true}, wenn die Verbindung geschlossen ist. Ansonsten
     *         {@code false}.
     * @throws SQLException
     *             wenn der Status nicht geprüft werden kann.
     */
    public boolean isClosed() throws SQLException {
        return rawConnection.isClosed();
    }

    /**
     * Initialisiert die Datenbank. Diese Methode wird automatisch aufgerufen,
     * sobald eine Instanz erstellt wird. Sie sorgt dafür, dass UTF-8 und
     * Fremdschlüssel verwendet werden.
     *
     * @throws SQLException
     *             wenn die Initialisierung fehlschlägt.
     */
    public void initialize() throws SQLException {
        Statement statement = createStatement();
        statement.execute("PRAGMA auto_vacuum = 1;");
        statement.execute("PRAGMA automatic_index = 1;");
        statement.execute("PRAGMA case_sensitive_like = 0;");
        statement.execute("PRAGMA defer_foreign_keys = 0;");
        statement.execute("PRAGMA encoding = 'UTF-8';");
        statement.execute("PRAGMA foreign_keys = 1;");
        statement.execute("PRAGMA ignore_check_constraints = 0;");
        statement.execute("PRAGMA journal_mode= WAL;");
        statement.execute("PRAGMA query_only = 0;");
        statement.execute("PRAGMA recursive_triggers = 1;");
        statement.execute("PRAGMA reverse_unordered_selects = 0;");
        statement.execute("PRAGMA secure_delete = 0;");
        statement.execute("PRAGMA synchronous = NORMAL;");
    }

    /**
     * Erzeugt ein {@link Statement}. Die Methode erstellt ein {@link Statement}
     * und setzt den Timeout auf 5 Sekunden. Außerdem soll die Anweisung
     * geschlossen werden, sobald sie ausgeführt wurde.
     *
     * @return {@code Statement}.
     * @throws SQLException
     *             wenn kein {@code Statement} erstellt werden kann.
     */
    public Statement createStatement() throws SQLException {
        Statement statement = rawConnection.createStatement();
        statement.setQueryTimeout(5);
        statement.closeOnCompletion();
        return statement;
    }

    /**
     * Führt eine SELECT-Anfrage aus.
     *
     * Für mehr Informationen siehe {@link Statement#executeQuery(String)} der
     * Java-Dokumentation.
     *
     * @param sql
     *            Anfrage, die ausgeführt werden soll.
     * @return Ergebnismenge der Anfrage.
     * @throws SQLException
     *             wenn die Anfrage fehlerhaft ist.
     */
    public ResultSet executeQuery(String sql) throws SQLException {
        return createStatement().executeQuery(sql);
    }

    /**
     * Führt eine INSERT/UPDATE/DELETE-Anfrage aus.
     *
     * Für mehr Informationen siehe {@link Statement#executeUpdate(String)} der
     * Java-Dokumentation.
     *
     * @param sql
     *            Anfrage, die ausgeführt werden soll.
     * @return Anzahl der betroffenen Zeilen.
     * @throws SQLException
     *             wenn die Anfrage fehlerhaft ist.
     */
    public int executeUpdate(String sql) throws SQLException {
        return createStatement().executeUpdate(sql);
    }

    /**
     * Bereitet eine Anfrage mit Platzhaltern vor.
     *
     * Für mehr Informationen siehe
     * {@link java.sql.Connection#prepareStatement(String)} der
     * Java-Dokumentation.
     *
     * @param sql
     *            Anfrage, die Platzhalter beinhaltet.
     * @return vorbereitete Anfrage.
     * @throws SQLException
     *             wenn die Anfrage fehlerhaft ist.
     */
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        PreparedStatement statement = rawConnection.prepareStatement(sql);
        statement.setQueryTimeout(5);
        statement.closeOnCompletion();
        return statement;
    }

    /**
     * Getter für {@link #rawConnection}.
     *
     * @return von Java implementierte Verbindung zur Datenbank.
     */
    public java.sql.Connection getRawConnection() {
        return rawConnection;
    }

    /**
     * Getter für {@link #url}.
     *
     * @return URL zur Datenbank.
     */
    public String getURL() {
        return url;
    }
}
