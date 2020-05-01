/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 *
 * The contents of this file were modified from code released by Datical under
 * the Apache 2.0 license.
 */
package liquibase.ext.modifycolumn;

import java.util.ArrayList;
import java.util.List;

import liquibase.change.AbstractChange;
import liquibase.change.ChangeMetaData;
import liquibase.change.ChangeWithColumns;
import liquibase.change.ColumnConfig;
import liquibase.change.DatabaseChange;
import liquibase.change.DatabaseChangeProperty;
import liquibase.database.Database;
import liquibase.statement.SqlStatement;
import liquibase.util.StringUtils;

/**
 * Modifies an existing column
 */
@DatabaseChange(name = "modifyColumn", description = "Modifies an existing column on an existing table", priority = ChangeMetaData.PRIORITY_DEFAULT, appliesTo = "column")
@SuppressWarnings("unused")
public class ModifyColumnChange extends AbstractChange implements ChangeWithColumns<ColumnConfig> {

	private String catalogName;

	private String schemaName;

	private String tableName;

	private List<ColumnConfig> columns = new ArrayList<>();

	@DatabaseChangeProperty(mustEqualExisting = "column.relation.catalog", since = "3.0")
	public String getCatalogName() {
		return catalogName;
	}

	public void setCatalogName(String catalogName) {
		this.catalogName = catalogName;
	}

	@DatabaseChangeProperty(mustEqualExisting = "column.relation.schema")
	public String getSchemaName() {
		return schemaName;
	}

	public void setSchemaName(String schemaName) {
		this.schemaName = StringUtils.trimToNull(schemaName);
	}

	@DatabaseChangeProperty(mustEqualExisting = "column.relation", description = "Name of the table to modify the column in")
	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	@DatabaseChangeProperty(mustEqualExisting = "column", description = "Column constraint and foreign key information. Setting the \"defaultValue\" attribute will specify a default value for the column.")
	public List<ColumnConfig> getColumns() {
		return columns;
	}

	@Override
	public void setColumns(List<ColumnConfig> columns) {
		this.columns = columns;
	}

	public void addColumn(ColumnConfig column) {
		columns.add(column);
	}

	public void removeColumn(ColumnConfig column) {
		columns.remove(column);
	}

	public SqlStatement[] generateStatements(Database database) {

		return new SqlStatement[] {
			new ModifyColumnStatement(getSchemaName(), getTableName(),
				getColumns().toArray(new ColumnConfig[0]))
		};
	}

	public String getConfirmationMessage() {
		List<String> names = new ArrayList<>(columns.size());
		for (ColumnConfig col : columns) {
			names.add(col.getName() + "(" + col.getType() + ")");
		}

		return "Columns " + StringUtils.join(names, ",") + " of " + getTableName() + " modified";
	}
}
