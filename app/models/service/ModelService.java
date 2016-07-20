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

package models.service;

import java.util.List;

import javax.annotation.Nullable;

import models.generic.Model;

/**
 * Created by daniel on 31.10.14.
 */
public interface ModelService<T extends Model> extends UniqueService<T> {

    @Nullable T getById(Long id);

    List<T> getByAttributeValue(String attribute, String value) throws IllegalSearchException;

    List<T> getAll();

    void save(T t);

    void delete(T t);

}
