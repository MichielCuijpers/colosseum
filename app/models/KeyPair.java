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

package models;

import com.google.common.collect.Lists;
import models.generic.RemoteResourceInCloud;

import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import java.util.Collections;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by daniel on 18.05.15.
 */
@Entity public class KeyPair extends RemoteResourceInCloud {

    @Lob @Column(nullable = false) private String privateKey;
    @Lob @Nullable private String publicKey;

    /**
     * No-args constructor for hibernate
     */
    protected KeyPair() {
    }

    public KeyPair(@Nullable String remoteId, @Nullable String providerId, @Nullable String swordId,
        Cloud cloud, @Nullable CloudCredential owner, String privateKey,
        @Nullable String publicKey) {
        super(remoteId, providerId, swordId, cloud, owner);
        checkNotNull(privateKey);
        this.privateKey = privateKey;
        this.publicKey = publicKey;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    @Nullable public String getPublicKey() {
        return publicKey;
    }

    @Override public List<CloudCredential> cloudCredentials() {
        if (owner().isPresent()) {
            return Lists.newArrayList(owner().get());
        }
        return Collections.emptyList();
    }

    /**
     * @todo find a better way to undecorate the keypair for the virtual machine template.
     * Maybe do it in the template itself?
     */
    public String name() {
        return providerId().get();
    }

}
