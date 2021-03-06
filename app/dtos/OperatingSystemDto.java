/*
 * Copyright (c) 2014-2015 University of Ulm
 *
 * See the NOTICE file distributed with this work for additional information
 * regarding copyright ownership.  Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package dtos;

import de.uniulm.omi.cloudiator.common.os.OperatingSystemArchitecture;
import de.uniulm.omi.cloudiator.common.os.OperatingSystemFamily;
import dtos.generic.ValidatableDto;

/**
 * Created by daniel on 15.04.15.
 */
public class OperatingSystemDto extends ValidatableDto {

    private OperatingSystemFamily operatingSystemFamily;
    private OperatingSystemArchitecture operatingSystemArchitecture;
    private String version;

    @Override public void validation() {
        //todo implement validation
    }

    public OperatingSystemFamily getOperatingSystemFamily() {
        return operatingSystemFamily;
    }

    public void setOperatingSystemFamily(OperatingSystemFamily operatingSystemFamily) {
        this.operatingSystemFamily = operatingSystemFamily;
    }

    public OperatingSystemArchitecture getOperatingSystemArchitecture() {
        return operatingSystemArchitecture;
    }

    public void setOperatingSystemArchitecture(
        OperatingSystemArchitecture operatingSystemArchitecture) {
        this.operatingSystemArchitecture = operatingSystemArchitecture;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
