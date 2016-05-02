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
package com.spotify.statistics;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.codahale.metrics.MetricRegistry;

public class SingleMetricMuninDataSourceTest {

  @Test
  public void testForMetric() throws Exception {
    final String name = MetricRegistry.name("g", "t", "n");
    final MuninDataSourceFactory dataSourceFactory = new MuninDataSourceFactory();

    MuninDataSource dataSource = dataSourceFactory.forMetric(
        name, null, null, new MuninDataSourceConfig());
    assertEquals("g.t.n", dataSource.getLabel(name));
    assertEquals("g.t.n", dataSource.getName(name));

    MuninDataSource dataSource2 = dataSourceFactory.forMetric(
        name, "label", null, new MuninDataSourceConfig().withName("munin_name"));
    assertEquals("label", dataSource2.getLabel(name));
    assertEquals("munin_name", dataSource2.getName(name));
  }
}
