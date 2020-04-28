/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.liquibase;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import liquibase.changelog.ChangeSet;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ChangeLogDetectiveTest {
	
	@Test
	public void shouldGetSnapshotVersionsInDescendingOrder() {
		Map<String, List<String>> snapshotCombinations = new HashMap<>();
		snapshotCombinations.put("1.9.x", null);
		snapshotCombinations.put("2.4.x", null);
		snapshotCombinations.put("2.1.x", null);
		snapshotCombinations.put("2.2.x", null);
		snapshotCombinations.put("2.3.x", null);
		
		ChangeLogDetective changeLogDetective = new ChangeLogDetective();
		List<String> actual = changeLogDetective.getSnapshotVersionsInDescendingOrder(snapshotCombinations);
		List<String> expected = Arrays.asList("2.4.x", "2.3.x", "2.2.x", "2.1.x", "1.9.x");
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void shouldIgnoreDisableForeignKeyChecks() {
		ChangeSet changeSet = mock(ChangeSet.class);
		when(changeSet.getAuthor()).thenReturn("ben");
		when(changeSet.getId()).thenReturn("disable-foreign-key-checks");
		
		ChangeLogDetective changeLogDetective = new ChangeLogDetective();
		boolean actual = changeLogDetective.isVintageChangeSet(changeSet);
		assertTrue(actual);
	}
	
	@Test
	public void shouldIgnoreEnableForeignKeyChecks() {
		ChangeSet changeSet = mock(ChangeSet.class);
		when(changeSet.getAuthor()).thenReturn("ben");
		when(changeSet.getId()).thenReturn("enable-foreign-key-checks");
		
		ChangeLogDetective changeLogDetective = new ChangeLogDetective();
		boolean actual = changeLogDetective.isVintageChangeSet(changeSet);
		assertTrue(actual);
	}
	
	@Test
	public void shouldNotIgnoreOtherChangeSetFromBen() {
		ChangeSet changeSet = mock(ChangeSet.class);
		when(changeSet.getAuthor()).thenReturn("ben");
		when(changeSet.getId()).thenReturn("anything");
		
		ChangeLogDetective changeLogDetective = new ChangeLogDetective();
		boolean actual = changeLogDetective.isVintageChangeSet(changeSet);
		assertFalse(actual);
	}
	
	@Test
	public void shouldExcludeVintageChangeSets() {
		ChangeSet anyChangeSet = new ChangeSet("any id", "any author", false, false, null, null, null, null);
		ChangeSet changeSetToIgnore = new ChangeSet("disable-foreign-key-checks", "ben", false, false, null, null, null,
		        null);
		ChangeSet anotherChangeSetToIgnore = new ChangeSet("enable-foreign-key-checks", "ben", false, false, null, null,
		        null, null);
		List<ChangeSet> changeSets = Arrays.asList(anyChangeSet, changeSetToIgnore, anotherChangeSetToIgnore);
		
		ChangeLogDetective changeLogDetective = new ChangeLogDetective();
		List<ChangeSet> actual = changeLogDetective.excludeVintageChangeSets(changeSets);
		List<ChangeSet> expected = Arrays.asList(anyChangeSet);
		
		assertEquals(expected, actual);
	}
}
