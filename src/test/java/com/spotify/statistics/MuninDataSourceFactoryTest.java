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
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.Test;

import com.codahale.metrics.MetricRegistry;

public class MuninDataSourceFactoryTest {

  /**
   * Test {@link MuninDataSourceFactory#forMetric(com.yammer.metrics.core.MetricName, String,
   * Property, MuninDataSourceConfig)}
   */
  @Test
  public void testForMetric() {
    final MuninDataSourceFactory dataSourceFactory = new MuninDataSourceFactory();
    final String name = MetricRegistry.name("t1");

    final Property property = mock(Property.class);

    final MuninDataSourceConfig dataSourceConfig = mock(MuninDataSourceConfig.class);
    when(dataSourceConfig.getCdef()).thenReturn("cdef");
    when(dataSourceConfig.getColor()).thenReturn("color");
    when(dataSourceConfig.getDraw()).thenReturn("draw");
    when(dataSourceConfig.getLine()).thenReturn("line");
    when(dataSourceConfig.getMin()).thenReturn(42);
    when(dataSourceConfig.getStack()).thenReturn("stack");

    final MuninDataSource dataSource = dataSourceFactory.forMetric(name, "label", property,
                                                                   dataSourceConfig);

    assertTrue(dataSource instanceof SingleMetricMuninDataSource);
    assertEquals(property, dataSource.getPropertyOrNull());
    assertEquals(Arrays.asList(name), dataSource.getMetricNames(null));
    assertEquals("label", dataSource.getLabel(name));

    verify(dataSourceConfig).getCdef();
    assertEquals("cdef", dataSource.getCdef());
    verify(dataSourceConfig).getColor();
    assertEquals("color", dataSource.getColor());
    verify(dataSourceConfig).getDraw();
    assertEquals("draw", dataSource.getDraw());
    verify(dataSourceConfig).getLine();
    assertEquals("line", dataSource.getLine());
    verify(dataSourceConfig).getMin();
    assertEquals(42, dataSource.getMin());
    verify(dataSourceConfig).getStack();
    assertEquals("stack", dataSource.getStack());
  }

  @Test
  public void testForMetricNullLabel() {
    final MuninDataSourceFactory dataSourceFactory = new MuninDataSourceFactory();
    final String name = MetricRegistry.name("t1");

    final Property property = mock(Property.class);

    final MuninDataSourceConfig dataSourceConfig = mock(MuninDataSourceConfig.class);
    when(dataSourceConfig.getCdef()).thenReturn("cdef");
    when(dataSourceConfig.getColor()).thenReturn("color");
    when(dataSourceConfig.getDraw()).thenReturn("draw");
    when(dataSourceConfig.getLine()).thenReturn("line");
    when(dataSourceConfig.getMin()).thenReturn(42);
    when(dataSourceConfig.getStack()).thenReturn("stack");

    final MuninDataSource dataSource = dataSourceFactory.forMetric(name, null, property,
                                                                   dataSourceConfig);

    assertEquals("t1", dataSource.getLabel(name));
  }

}
