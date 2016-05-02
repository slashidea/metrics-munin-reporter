/*
 * Copyright (c) 2012-2014 Spotify AB
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.spotify.statistics.example;

import static com.spotify.statistics.MuninGraph.muninName;

import java.io.IOException;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.spotify.statistics.MuninGraphCategoryConfig;
import com.spotify.statistics.MuninGraphProviderBuilder;
import com.spotify.statistics.MuninReporter;
import com.spotify.statistics.MuninReporterConfig;
import com.spotify.statistics.Property.MeterProperty;
import com.spotify.statistics.Property.TimerProperty;

public class Example {

  public static void main(final String[] args) throws InterruptedException, IOException {

      // Define the metrics
      MetricRegistry registry = new MetricRegistry();
      
      String totalName = MetricRegistry.name("Spotify", "hits", "total");
      Counter counter = registry.counter(totalName);
      
      String cdnName = MetricRegistry.name("Spotify", "hits", "cdn");
      Counter cdnCounter = registry.counter(cdnName);
      
      String meteredName = MetricRegistry.name(Example.class, "requests");
      Meter metered = registry.meter(meteredName);
            
      String timerName = MetricRegistry.name(Example.class, "duration");
      Timer timer = registry.timer(timerName);
            
      String gaugeName = MetricRegistry.name("Spotify", "worker", "queue size");
      registry.register(gaugeName, new Gauge<Integer>() {
          
        @Override
        public Integer getValue() {
            return 17;
        }
      });    

    // let's generate some data
    counter.inc();
    counter.inc();
    cdnCounter.inc();

    metered.mark();
    metered.mark();

    String inputGroup = "Spotify";
    String inputType = "input types";

    // this is a poor way to fake some input not known before-hand
    String someInput = Integer.toString(new Random().nextInt());
    
    timer = registry.timer(MetricRegistry.name(inputGroup, inputType, someInput));
    final Timer.Context context = timer.time();
    try {
      Thread.sleep(500); // Work...
    } finally {
      context.stop();
    }

    // Define the munin graphs
    MuninReporterConfig config = new MuninReporterConfig();

    MuninGraphCategoryConfig category = config.category("Spotify");

    category.graph("Number of responses")
            .muninName("spotify_num_responses")
            .dataSource(totalName, "Total number of responses")
            .dataSource(cdnName, "CDN responses");

    category.graph("Number of requests")
            .dataSource(meteredName, "Requests", MeterProperty.ONE_MINUTE_RATE);

    category.graph("Request duration")
            .dataSource(timerName, "Median", TimerProperty.MEDIAN)
            .dataSource(timerName, "95%", TimerProperty.PERCENTILE95);

    category.graph("In vs Out")
            .vlabel("messages")
            .muninName("custom_munin_name")
            .dataSource(totalName, "Requests", muninName("requests"))
            .dataSource(meteredName, "Responses", muninName("responses"));

    MuninReporter munin = new MuninReporter(registry, config);
    munin.start();

    // Add metrics for a runtime component, e.g. a plugin
    String pluginRequestsName = MetricRegistry.name("Plugin", "requests", "requests");
    Meter pluginRequests = registry.meter(pluginRequestsName);
    pluginRequests.mark(4711);

    // Set up graphs for the plugin, i.e. add graphs after the reporter was started
    MuninGraphProviderBuilder pluginGraphs = new MuninGraphProviderBuilder();
    MuninGraphCategoryConfig pluginCategory = pluginGraphs.category("Plugin");
    pluginCategory.graph("Requests")
                  .dataSource(pluginRequestsName, "Requests", muninName("requests"));

    // Add the graphs
    munin.addGraphs(pluginGraphs.build());

    // wait, try to play around using telnet localhost 4951
    Object mutex = new Object();
    synchronized (mutex) {
      mutex.wait();
    }
  }

}
