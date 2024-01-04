# [Nebula Graph Database support](https://github.com/awang12345/nebula-console-for-idea)
[![Build Status](https://travis-ci.org/awang12345/nebula-console-for-idea.svg?branch=master)](https://travis-ci.org/awang12345/nebula-console-for-idea)

## Plugin for [IntelliJ IDEA](http://plugins.jetbrains.com/plugin/6317-lombok-plugin) to support [Nebula-Console](https://docs.nebula-graph.com.cn/3.6.0/nebula-console/).
- Provides support for console to manage nebula data with IntelliJ IDEA.

Plugin is developed and supported by [awang12345](http://awang12345.github.io/).

![plugin screenshot](docs/screenshots/nebula-console.gif)

This plugin is based on the
[Graph Database Support](https://github.com/neueda/jetbrains-plugin-graph-database-support)
plugin, originally developed by [Neueda Technologies](http://technologies.neueda.com/).
The original plugin has not been updated for a long time and does not work with Nebula.

## Installation

Plugin is [available for download](https://plugins.jetbrains.com/plugin/8087) from Jetbrains repository.

1. Go to `Preferences` -> `Plugins` -> `Browser repositories...`
2. Search for `Nebula Console`.
3. Install plugin and restart IDE.

## Features

- Works in **any** Jetbrains IDE
- Manage data sources
- Write and execute queries
- Explore query results in table view
- Supported databases:
  - Neo4j 3.4+ (Bolt)
  - Nebula 3.0+ (Thrift)
- [nGQL](https://docs.nebula-graph.com.cn/3.6.0/3.ngql-guide/1.nGQL-overview/1.overview/) query language:
    - Understands queries in `.ngql` files
    - Syntax highlight
    - Autocompletion support for identifiers, keywords, functions, space, tag, edge, etc.
      [nebula-graph](https://www.nebula-graph.com.cn),
      [nebula-client](https://docs.nebula-graph.com.cn/3.6.0/14.client/4.nebula-java-client)
- [Cypher](https://github.com/opencypher/openCypher) query language:
  - Understands queries in `.cyp`, `.cypher` or `.cql` files
  - Syntax highlight and error reporting
  - Refactoring support for identifiers, labels, relationship types and properties
  - Autocompletion support for identifiers, labels, relationship types, properties, functions and stored procedures. Information gathered from existing queries and configured data sources
  - Code reformatting
  - Provide documentation for functions and stored procedures
  - Inspections: database warnings, function checks, type system. 

## Supported Jetbrains products

* IntelliJ IDEA
* RubyMine
* WebStorm
* PhpStorm
* PyCharm
* AppCode
* Android Studio
* Datagrip
* CLion

## Development

Warning: Required to switch local environment to JDK 11.

Gradle is used as build system.

```shell
# Build plugin distribution
./gradlew buildPlugin

# Run idea in development mode
./gradlew :graph-database-support-plugin:runIde
```

