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

import static com.spotify.statistics.MuninUtil.validateMuninName;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.Validate;

public class MuninGraph {

  private final String name;
  private final String category;
  private final String title;
  private final String vlabel;
  private final String args;
  private final List<MuninDataSource> dataSources;

  public MuninGraph(final String name, final String category, final String title) {
    this(name, category, title, new ArrayList<MuninDataSource>());
  }

  public MuninGraph(final String name, final String category, final String title,
                    final List<MuninDataSource> dataSources) {
    this(name, category, title, dataSources, "");
  }

  public MuninGraph(final String name, final String category, final String title,
                    final List<MuninDataSource> dataSources, final String vlabel) {
    this(name, category, title, dataSources, vlabel, null);
  }

  public MuninGraph(final String name, final String category, final String title,
                    final List<MuninDataSource> dataSources, final String vlabel, final String args) {
    Validate.notNull(name);
    Validate.notNull(category);
    Validate.notNull(title);
    Validate.notNull(dataSources);
    Validate.notNull(vlabel);
    this.name = name;
    this.category = category;
    this.title = title;
    this.dataSources = dataSources;
    this.vlabel = vlabel;
    this.args = args;
  }

  public String getName() {
    return name;
  }

  public String getCategory() {
    return category;
  }

  public String getTitle() {
    return title;
  }

  public String getVlabel() {
    return vlabel;
  }

  public String getArgs() {
    return args;
  }

  public List<MuninDataSource> getDataSources() {
    return dataSources;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    MuninGraph that = (MuninGraph) o;

    if (args != null ? !args.equals(that.args) : that.args != null) {
      return false;
    }
    if (!category.equals(that.category)) {
      return false;
    }
    if (!dataSources.equals(that.dataSources)) {
      return false;
    }
    if (!name.equals(that.name)) {
      return false;
    }
    if (!title.equals(that.title)) {
      return false;
    }
    if (!vlabel.equals(that.vlabel)) {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int result = name.hashCode();
    result = 31 * result + category.hashCode();
    result = 31 * result + title.hashCode();
    result = 31 * result + vlabel.hashCode();
    result = 31 * result + (args != null ? args.hashCode() : 0);
    result = 31 * result + dataSources.hashCode();
    return result;
  }

  @Override
  public String toString() {
    return "MuninGraph{"
      + "name='" + name + '\''
      + ", category='" + category + '\''
      + ", title='" + title + '\''
      + ", vlabel='" + vlabel + '\''
      + ", args='" + args + '\''
      + ", dataSources=" + dataSources
      + '}';
  }

  public static class Builder {
    private final String category;
    private final String title;
    private String vlabel;
    private String muninName;
    private String args;
    private final List<MuninDataSource> dataSources = new ArrayList<MuninDataSource>();
    private final MuninDataSourceFactory dataSourceFactory;

    Builder(final MuninDataSourceFactory dataSourceFactory, final String muninName,
            final String category, final String title, final String vlabel) {
      Validate.notNull(muninName);
      Validate.notNull(category);
      Validate.notNull(title);
      Validate.notNull(vlabel);
      this.muninName = validateMuninName(muninName);
      this.category = category;
      this.title = title;
      this.vlabel = vlabel;
      this.dataSourceFactory = dataSourceFactory;
    }

    Builder(final MuninDataSourceFactory dataSourceFactory, final String muninName,
            final String category, final String title) {
      this(dataSourceFactory, muninName, category, title, "");
    }

    public Builder(final String muninName, final String category, final String title) {
      this(new MuninDataSourceFactory(), muninName, category, title, "");
    }

    public Builder muninName(final String muninName) {
      Validate.notNull(muninName);
      this.muninName = validateMuninName(muninName);
      return this;
    }

    public Builder vlabel(final String vlabel) {
      Validate.notNull(vlabel);
      this.vlabel = vlabel;
      return this;
    }

    public Builder dataSource(final String metricName) {
      return dataSource(metricName, null);
    }

    public Builder dataSource(final String metricName, final String label) {
      return dataSource(metricName, label, (Property) null);
    }

    public Builder dataSource(final String metricName, final String label,
                              final String muninName) {
      return dataSource(metricName, label, new MuninDataSourceConfig().withName(muninName));
    }

    public Builder dataSource(final String metricName, final String label,
                              final Property property) {
      return dataSource(metricName, label, property, new MuninDataSourceConfig());
    }

    public Builder dataSource(final String metricName, final String label,
                              final MuninDataSourceConfig dataSourceConfig) {
      return dataSource(metricName, label, null, dataSourceConfig);
    }

    public Builder dataSource(final String metricName, final String label,
                              final Property property,
                              final MuninDataSourceConfig dataSourceConfig) {
      dataSources.add(dataSourceFactory.forMetric(metricName, label, property, dataSourceConfig));
      return this;
    }

    public Builder args(final String args) {
      this.args = args;
      return this;
    }

    public String getCategory() {
      return category;
    }

    public String getTitle() {
      return title;
    }

    public String getVlabel() {
      return vlabel;
    }

    public String getMuninName() {
      return muninName;
    }

    public String getArgs() {
      return args;
    }

    public List<MuninDataSource> getDataSources() {
      return dataSources;
    }

    public MuninGraph build() {
      return new MuninGraph(muninName, category, title, dataSources, vlabel, args);
    }
  }

  /**
   * Helper for succinctly configuring field munin names for data sources like below:
   * {@code
   *  ...
   *    .dataSource(REQUESTS, "Requests", muninName("requests"))
   *  ...
   * }
   * @param name The desired name of the field in the munin output.
   * @return A munin data source config with the munin name set.
   */
  public static MuninDataSourceConfig muninName(final String name) {
    return new MuninDataSourceConfig().withName(name);
  }
}
