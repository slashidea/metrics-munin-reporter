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

import com.codahale.metrics.Counter;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.Meter;
import com.codahale.metrics.Metric;
import com.codahale.metrics.Snapshot;
import com.codahale.metrics.Timer;

public interface Property {

  public static class PropertyFactory {
    public static Property getProperty(final Property propertyOrNull, final Metric metric) {
      if (propertyOrNull != null) {
        return propertyOrNull;
      } else {
        // is null, find a default
        if (metric instanceof Counter) {
          return CounterProperty.COUNT;
        } else if (metric instanceof Gauge) {
          return GaugeProperty.VALUE_GAUGE;
        } else if (metric instanceof Timer) {
          return TimerProperty.MEDIAN;
        } else if (metric instanceof Meter) {
          return MeterProperty.FIVE_MINUTE_RATE;
        } else if (metric instanceof Histogram) {
          return HistogramProperty.MEDIAN;
        } else {
          throw new RuntimeException("Unexpected metric type: " + metric.getClass());
        }
      }
    }
  }

  String name();

  Type getType();

  Number getNumber(Metric metric, Snapshot snapshotOrNull);

  public static enum CounterProperty implements Property {
    COUNT(Type.DERIVE),
    GAUGE(Type.GAUGE);

    private final Type type;

    private CounterProperty(final Type type) {
      this.type = type;
    }

    public Type getType() {
      return type;
    }

    public Number getNumber(final Metric metric, final Snapshot none) {
      if (metric instanceof Counter) {
        Counter counter = (Counter) metric;
        return counter.getCount();
      } else {
        throw new IllegalArgumentException("Invalid metric for property");
      }
    }
  }

  public static enum GaugeProperty implements Property {
    VALUE_GAUGE(Type.GAUGE),
    VALUE_DERIVE(Type.DERIVE);

    private final Type type;

    private GaugeProperty(final Type type) {
      this.type = type;
    }

    public Type getType() {
      return type;
    }

    public Number getNumber(final Metric metric, final Snapshot none) {
      if (metric instanceof Gauge) {
        Gauge<?> gauge = (Gauge<?>) metric;
        Object o = gauge.getValue();
        if (o instanceof Number) {
          return (Number) o;
        } else {
          throw new IllegalArgumentException("Non-numeric gauge can not be graphed");
        }
      } else {
        throw new IllegalArgumentException("Invalid metric for property");
      }
    }
  }

  public static enum TimerProperty implements Property {
    COUNT(Type.DERIVE),
    ONE_MINUTE_RATE(Type.GAUGE),
    FIVE_MINUTE_RATE(Type.GAUGE),
    FIFTEEN_MINUTE_RATE(Type.GAUGE),
    MAX(Type.GAUGE),
    MIN(Type.GAUGE),
    MEAN(Type.GAUGE),
    MEAN_RATE(Type.GAUGE),
//    SUM(Type.GAUGE),
    STD_DEV(Type.GAUGE),
    MEDIAN(Type.GAUGE),
    PERCENTILE75(Type.GAUGE),
    PERCENTILE95(Type.GAUGE),
    PERCENTILE98(Type.GAUGE),
    PERCENTILE99(Type.GAUGE),
    PERCENTILE999(Type.GAUGE);

    private final Type type;

    private TimerProperty(final Type type) {
      this.type = type;
    }

    public Type getType() {
      return type;
    }

    public Number getNumber(final Metric metric, final Snapshot snapshot) {
      if (metric instanceof Timer) {
        Timer timer = (Timer) metric;
        Snapshot timerSnahpshot = timer.getSnapshot();
        switch(this) {
          case COUNT:
            return timer.getCount();
          case ONE_MINUTE_RATE:
            return timer.getOneMinuteRate();
          case FIVE_MINUTE_RATE:
            return timer.getFiveMinuteRate();
          case FIFTEEN_MINUTE_RATE:
            return timer.getFifteenMinuteRate();
          case MAX:
            return timerSnahpshot.getMax();
          case MIN:
            return timerSnahpshot.getMin();
          case MEAN:
            return timerSnahpshot.getMean();
          case MEAN_RATE:
            return timer.getMeanRate();
//          case SUM:
//            return snapshot.get;
          case STD_DEV:
            return timerSnahpshot.getStdDev();
          case MEDIAN:
            return timerSnahpshot.getMedian();
          case PERCENTILE75:
            return timerSnahpshot.get75thPercentile();
          case PERCENTILE95:
            return timerSnahpshot.get95thPercentile();
          case PERCENTILE98:
            return timerSnahpshot.get98thPercentile();
          case PERCENTILE99:
            return timerSnahpshot.get99thPercentile();
          case PERCENTILE999:
            return timerSnahpshot.get999thPercentile();
          default:
            throw new RuntimeException("Unexpected property");
        }
      } else {
        throw new IllegalArgumentException("Invalid metric for property");
      }
    }
  }

  public static enum MeterProperty implements Property {
    COUNT(Type.DERIVE),
    ONE_MINUTE_RATE(Type.GAUGE),
    FIVE_MINUTE_RATE(Type.GAUGE),
    FIFTEEN_MINUTE_RATE(Type.GAUGE),
    MEAN_RATE(Type.GAUGE);

    private final Type type;

    private MeterProperty(final Type type) {
      this.type = type;
    }

    public Type getType() {
      return type;
    }

    public Number getNumber(final Metric metric, final Snapshot none) {
      if (metric instanceof Meter) {
        Meter meter = (Meter) metric;
        switch(this) {
          case COUNT:
            return meter.getCount();
          case ONE_MINUTE_RATE:
            return meter.getOneMinuteRate();
          case FIVE_MINUTE_RATE:
            return meter.getFifteenMinuteRate();
          case FIFTEEN_MINUTE_RATE:
            return meter.getFifteenMinuteRate();
          case MEAN_RATE:
            return meter.getMeanRate();
          default:
            throw new RuntimeException("Unexpected property");
        }
      } else {
        throw new IllegalArgumentException("Invalid metric for property");
      }
    }

  }

  public static enum HistogramProperty implements Property {
    COUNT(Type.DERIVE),
    MAX(Type.GAUGE),
    MIN(Type.GAUGE),
    MEAN(Type.GAUGE),
//    SUM(Type.GAUGE),
    STD_DEV(Type.GAUGE),
    MEDIAN(Type.GAUGE),
    PERCENTILE75(Type.GAUGE),
    PERCENTILE95(Type.GAUGE),
    PERCENTILE98(Type.GAUGE),
    PERCENTILE99(Type.GAUGE),
    PERCENTILE999(Type.GAUGE);

    private final Type type;

    private HistogramProperty(final Type type) {
      this.type = type;
    }

    public Type getType() {
      return type;
    }

    public Number getNumber(final Metric metric, final Snapshot snapshot) {
      if (metric instanceof Histogram) {
        Histogram histogram = (Histogram) metric;
        Snapshot histogramSnapshot = histogram.getSnapshot();
        switch(this) {
          case COUNT:
            return histogram.getCount();
          case MAX:
            return histogramSnapshot.getMax();
          case MIN:
            return histogramSnapshot.getMin();
          case MEAN:
            return histogramSnapshot.getMean();
//          case SUM:
//            return snapshot.getS();
          case STD_DEV:
            return histogramSnapshot.getStdDev();
          case MEDIAN: 
            return histogramSnapshot.getMedian();
          case PERCENTILE75:
            return histogramSnapshot.get75thPercentile();
          case PERCENTILE95:
            return histogramSnapshot.get95thPercentile();
          case PERCENTILE98:
            return histogramSnapshot.get98thPercentile();
          case PERCENTILE99:
            return histogramSnapshot.get99thPercentile();
          case PERCENTILE999:
            return histogramSnapshot.get999thPercentile();
          default:
            throw new RuntimeException("Unexpected property");
        }
      } else {
        throw new IllegalArgumentException("Invalid metric for property");
      }
    }
  }
}
