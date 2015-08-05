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

package components.scalability;

import de.uniulm.omi.executionware.srl.aggregator.AggregatorService;
import de.uniulm.omi.executionware.srl.aggregator.observer.TelnetMetricObserver;
import de.uniulm.omi.executionware.srl.api.Monitor;
import models.MonitorSubscription;
import models.scalability.SubscriptionType;

/**
 * Created by Frank on 03.08.2015.
 */
public class SubscribeAggregation implements Aggregation {
    private final MonitorSubscription subscription;
    private final Monitor monitor;

    public SubscribeAggregation(Monitor monitor, MonitorSubscription subscription) {
        super();
        this.subscription = subscription;
        this.monitor = monitor;
    }

    @Override public void execute(AggregatorService service) {
        try {
            service.addObserverToMonitor(monitor.getId(), new TelnetMetricObserver(subscription.getFilterValue(), Converter.convert(subscription.getFilterType()), "localhost", 27182)); /*TODO dynamic*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override public int getPriority() {
        return 0;
    }
}