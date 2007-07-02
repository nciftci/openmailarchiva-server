/* Copyright (C) 2005 Jamie Angus Band 
 * This software program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package com.stimulus.archiva.webservice;

import com.stimulus.archiva.domain.*;

public class Configuration {

	boolean archiveInbound;
	boolean archiveOutbound;
	boolean archiveInternal;
	ArchiveRules.Rule[] archiveRules;

	public Configuration(boolean archiveInbound, boolean archiveOutbound, boolean archiveInternal, ArchiveRules.Rule[] archiveRules) {
		this.archiveInbound = archiveInbound;
		this.archiveOutbound = archiveOutbound;
		this.archiveInternal = archiveInternal;
		this.archiveRules = archiveRules;
	}

	boolean getArchiveInbound() {
		return archiveInbound;
	}

	boolean getArchiveOutbound() {
		return archiveOutbound;
	}

	boolean getArchiveInternal() {
		return archiveInternal;
	}

	ArchiveRules.Rule[] getArchiveRules() {
		return archiveRules;
	}

}