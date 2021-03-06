package com.github.scilldev.data.database;

import com.github.scilldev.data.yaml.Settings;

import javax.sql.DataSource;
import java.sql.SQLException;

public interface DataSourceProvider {

	/**
	 * Initializes the data source.
	 * @param settings config data which holds the settings of the data source
	 */
	void init(Settings settings);

	/**
	 * Checks the connection of the DataSource.
	 * @throws SQLException connection is invalid
	 */
	void testConection() throws SQLException;

	/**
	 * @return the DataSource object
	 */
	DataSource getSource();

	/**
	 * Closes the data source.
	 */
	void close();
}
