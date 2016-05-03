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
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.Meter;
import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.spotify.statistics.Property.CounterProperty;
import com.spotify.statistics.Property.GaugeProperty;
import com.spotify.statistics.Property.HistogramProperty;
import com.spotify.statistics.Property.MeterProperty;
import com.spotify.statistics.Property.PropertyFactory;
import com.spotify.statistics.Property.TimerProperty;

public class PropertyTest {

  private final MetricRegistry metricRegistry = new MetricRegistry();
    
  private final Gauge<Integer> gauge = metricRegistry.register(MetricRegistry.name("g", "t", "g"), new Gauge<Integer>() {
    @Override
    public Integer getValue() {
      return 17;
    }});

  private final Counter counter = metricRegistry.counter(MetricRegistry.name("g", "t", "c"));
  private final Meter meter = metricRegistry.meter(MetricRegistry.name("g", "t", "m"));
  private final Histogram histogram = metricRegistry.histogram(MetricRegistry.name("g", "t", "h"));
  private final Timer timer = metricRegistry.timer(MetricRegistry.name("g", "t", "t"));

  @Test
  public void testPropertyFactory() {
    Property prop = PropertyFactory.getProperty(GaugeProperty.VALUE_DERIVE, gauge);

    assertEquals(Type.DERIVE, prop.getType());
    assertEquals(17, prop.getNumber(gauge, null, TimeUnit.SECONDS, TimeUnit.SECONDS));
  }

  @Test
  public void testPropertyFactoryDefault() {
    Property prop = PropertyFactory.getProperty(null, gauge);

    assertEquals(Type.GAUGE, prop.getType());
    assertEquals(17, prop.getNumber(gauge, null, TimeUnit.SECONDS, TimeUnit.SECONDS));

    assertSame(GaugeProperty.VALUE_GAUGE, PropertyFactory.getProperty(null, gauge));
    assertSame(CounterProperty.COUNT, PropertyFactory.getProperty(null, counter));
    assertSame(MeterProperty.FIVE_MINUTE_RATE, PropertyFactory.getProperty(null, meter));
    assertSame(HistogramProperty.MEDIAN, PropertyFactory.getProperty(null, histogram));
    assertSame(TimerProperty.MEDIAN, PropertyFactory.getProperty(null, timer));
  }

  @Test
  public void testGauge() {
    assertProperty(GaugeProperty.VALUE_DERIVE, Type.DERIVE, 17, gauge, counter);
  }

  @Test
  public void testCounter() {
    assertProperty(CounterProperty.COUNT, Type.DERIVE, 0L, counter, gauge);
    assertProperty(CounterProperty.GAUGE, Type.GAUGE, 0L, counter, gauge);
  }


  @Test
  public void testMeter() {
    assertProperty(MeterProperty.ONE_MINUTE_RATE, Type.GAUGE, 0.0, meter, gauge);
  }

  @Test
  public void testHistogram() {
    assertProperty(HistogramProperty.MEAN, Type.GAUGE, 0.0, histogram, gauge);
  }

  @Test
  public void testTimer() {
    assertProperty(TimerProperty.MEAN, Type.GAUGE, 0.0, timer, gauge);
  }

  private void assertProperty(Property actual, Type expectedType, Number expectedValue, Metric metric, Metric otherMetric) {
    assertEquals(expectedType, actual.getType());
    assertEquals(expectedValue, actual.getNumber(metric, null, TimeUnit.SECONDS, TimeUnit.SECONDS));

    try {
      actual.getNumber(otherMetric, null, TimeUnit.SECONDS, TimeUnit.SECONDS);
      fail("Must throw IllegalArgumentException");
    } catch(IllegalArgumentException expected) {}
  }


}
