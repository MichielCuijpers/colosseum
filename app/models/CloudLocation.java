/*
 * Copyright (c) 2014 University of Ulm
 *
 * See the NOTICE file distributed with this work for additional information
 * regarding copyright ownership.  Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import models.generic.Model;

/**
 * Represents the relation between a cloud and a generic location, by specifing
 * the cloud-dependant uuid.
 */
@Entity
public class CloudLocation extends Model {

	/**
	 * Serial version.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The cloud where this location is available.
	 */
	@ManyToOne
	private Cloud cloud;

	/**
	 * The location.
	 */
	@ManyToOne
	private Location location;

	/**
	 * The cloud-dependent unique identifier.
	 */
	private String uuid;

	/**
	 * Empty constructor needed for hibernate.
	 */
	public CloudLocation() {

	}

	public Cloud getCloud() {
		return cloud;
	}

	public void setCloud(Cloud cloud) {
		this.cloud = cloud;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
}
