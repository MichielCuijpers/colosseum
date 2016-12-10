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

package cloud.resources;

import cloud.SlashEncodedId;
import de.uniulm.omi.cloudiator.sword.api.domain.KeyPair;
import models.Cloud;
import models.CloudCredential;
import models.service.ModelService;

import java.util.Optional;

/**
 * Created by daniel on 10.09.15.
 */
public class KeyPairInCloud extends BaseLocationScoped implements KeyPair {

    private final KeyPair keyPair;

    public KeyPairInCloud(KeyPair keyPair, String cloud, String credential,
        ModelService<Cloud> cloudModelService,
        ModelService<CloudCredential> cloudCredentialModelService) {
        super(keyPair, cloud, credential, cloudModelService, cloudCredentialModelService);
        this.keyPair = keyPair;
    }

    @Override public String publicKey() {
        return keyPair.publicKey();
    }

    @Override public Optional<String> privateKey() {
        return keyPair.privateKey();
    }

    @Override public String id() {
        return SlashEncodedId.of(credential(), cloud(), keyPair).userId();
    }

    @Override public String providerId() {
        return keyPair.providerId();
    }

    @Override public String name() {
        return keyPair.name();
    }

    @Override public String cloudId() {
        return SlashEncodedId.of(credential(), cloud(), keyPair).cloudId();
    }

    @Override public String swordId() {
        return SlashEncodedId.of(credential(), cloud(), keyPair).swordId();
    }
}
