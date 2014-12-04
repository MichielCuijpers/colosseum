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
 * Represents the relation between a cloud and a generic image, by specifing the
 * cloud-dependant uuid.
 */
@Entity
public class CloudImage extends Model {

    /**
     * Empty constructor for Hibernate.
     */
    public CloudImage() {
    }

    /**
	 * Serial version.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The cloud where this image is available.
	 */
	@ManyToOne
	private Cloud cloud;

	/**
	 * The image.
	 */
	@ManyToOne
	private Image image;

	/**
	 * The cloud-dependant unique identifier.
	 */
	private String uuid;

    public Cloud getCloud() {
        return cloud;
    }

    public void setCloud(Cloud cloud) {
        this.cloud = cloud;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
