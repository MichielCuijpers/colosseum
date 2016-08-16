/*
 * Copyright (c) 2014-2015 University of Ulm
 *
 * See the NOTICE file distributed with this work for additional information
 * regarding copyright ownership.  Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package cloud.sword.resources;

import de.uniulm.omi.cloudiator.common.os.OperatingSystem;
import de.uniulm.omi.cloudiator.sword.api.domain.Image;
import models.Cloud;
import models.CloudCredential;
import models.service.ModelService;

/**
 * Created by daniel on 28.05.15.
 */
public class ImageInLocation extends ResourceWithCredential implements Image {

    private final Image image;

    public ImageInLocation(Image image, String cloud, String credential,
        ModelService<Cloud> cloudModelService,
        ModelService<CloudCredential> cloudCredentialModelService) {
        super(image, cloud, credential, cloudModelService, cloudCredentialModelService);
        this.image = image;
    }

    @Override public OperatingSystem operatingSystem() {
        return image.operatingSystem();
    }
}