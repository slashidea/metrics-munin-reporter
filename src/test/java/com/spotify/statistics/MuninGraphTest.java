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
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNull;
import static org.mockito.Matchers.notNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.Test;

public class MuninGraphTest {

  @Test
  public void testEmptyGraph() throws Exception {
    MuninGraph graph = new MuninGraph.Builder("n", "c", "t")
            .build();
    MuninGraph expectedGraph = new MuninGraph("n", "c", "t");
    assertEquals(expectedGraph, graph);
  }

  /**
   * Test {@link MuninGraph.Builder#dataSource(com.yammer.metrics.core.MetricName)}.
   */
  @Test
  public void testDataSource1() {
    final MuninDataSourceFactory dataSourceFactory = mock(MuninDataSourceFactory.class);
    final MuninDataSource dataSource = mock(MuninDataSource.class);
    when(dataSourceFactory.forMetric(any(String.class), anyString(), any(Property.class),
                                     any(MuninDataSourceConfig.class)))
        .thenReturn(dataSource);

    final String name = "t1";

    final MuninGraph graph = new MuninGraph.Builder(dataSourceFactory, "n", "c", "t")
        .dataSource(name)
        .build();

    verify(dataSourceFactory).forMetric(eq(name), isNull(String.class), isNull(Property.class),
                                        notNull(MuninDataSourceConfig.class));

    assertEquals(Arrays.asList(dataSource), graph.getDataSources());
  }

  /**
   * Test {@link MuninGraph.Builder#dataSource(com.yammer.metrics.core.MetricName, String)}.
   */
  @Test
  public void testDataSource2() {
    final MuninDataSourceFactory dataSourceFactory = mock(MuninDataSourceFactory.class);
    final MuninDataSource dataSource = mock(MuninDataSource.class);
    when(dataSourceFactory.forMetric(any(String.class), anyString(), any(Property.class),
                                     any(MuninDataSourceConfig.class)))
        .thenReturn(dataSource);

    final String name = "t1";

    final MuninGraph graph = new MuninGraph.Builder(dataSourceFactory, "n", "c", "t")
        .dataSource(name, "label")
        .build();

    verify(dataSourceFactory).forMetric(eq(name), eq("label"), isNull(Property.class),
                                        notNull(MuninDataSourceConfig.class));

    assertEquals(Arrays.asList(dataSource), graph.getDataSources());
  }

  /**
   * Test {@link MuninGraph.Builder#dataSource(com.yammer.metrics.core.MetricName, String,
   * MuninDataSourceConfig)}.
   */
  @Test
  public void testDataSource3() {
    final MuninDataSourceFactory dataSourceFactory = mock(MuninDataSourceFactory.class);
    final MuninDataSource dataSource = mock(MuninDataSource.class);
    when(dataSourceFactory.forMetric(any(String.class), anyString(), any(Property.class),
                                     any(MuninDataSourceConfig.class)))
        .thenReturn(dataSource);

    final String name = "t1";
    final MuninDataSourceConfig dataSourceConfig = mock(MuninDataSourceConfig.class);

    final MuninGraph graph = new MuninGraph.Builder(dataSourceFactory, "n", "c", "t")
        .dataSource(name, "label", dataSourceConfig)
        .build();

    verify(dataSourceFactory).forMetric(eq(name), eq("label"), isNull(Property.class),
                                        eq(dataSourceConfig));

    assertEquals(Arrays.asList(dataSource), graph.getDataSources());
  }

  /**
   * Test {@link MuninGraph.Builder#dataSource(com.yammer.metrics.core.MetricName, String,
   * Property)}.
   */
  @Test
  public void testDataSource4() {
    final MuninDataSourceFactory dataSourceFactory = mock(MuninDataSourceFactory.class);
    final MuninDataSource dataSource = mock(MuninDataSource.class);
    when(dataSourceFactory.forMetric(any(String.class), anyString(), any(Property.class),
                                     any(MuninDataSourceConfig.class)))
        .thenReturn(dataSource);

    final String name = "t1";
    final Property property = mock(Property.class);

    final MuninGraph graph = new MuninGraph.Builder(dataSourceFactory, "n", "c", "t")
        .dataSource(name, "label", property)
        .build();

    verify(dataSourceFactory).forMetric(eq(name), eq("label"), eq(property),
                                        notNull(MuninDataSourceConfig.class));

    assertEquals(Arrays.asList(dataSource), graph.getDataSources());
  }

  /**
   * Test {@link MuninGraph.Builder#dataSource(com.yammer.metrics.core.MetricName, String, Property,
   * MuninDataSourceConfig)}
   */
  @Test
  public void testDataSource5() {
    final MuninDataSourceFactory dataSourceFactory = mock(MuninDataSourceFactory.class);
    final MuninDataSource dataSource = mock(MuninDataSource.class);
    when(dataSourceFactory.forMetric(any(String.class), anyString(), any(Property.class),
                                     any(MuninDataSourceConfig.class)))
        .thenReturn(dataSource);

    final String name = "t1";
    final Property property = mock(Property.class);
    final MuninDataSourceConfig dataSourceConfig = mock(MuninDataSourceConfig.class);

    final MuninGraph graph = new MuninGraph.Builder(dataSourceFactory, "n", "c", "t")
        .dataSource(name, "label", property, dataSourceConfig)
        .build();

    verify(dataSourceFactory).forMetric(eq(name), eq("label"), eq(property), eq(dataSourceConfig));

    assertEquals(Arrays.asList(dataSource), graph.getDataSources());
  }


}
