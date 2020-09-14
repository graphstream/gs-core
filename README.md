# GraphStream

[![Build Status](https://travis-ci.org/graphstream/gs-core.svg?branch=dev)](https://travis-ci.org/graphstream/gs-core) [![](https://jitpack.io/v/graphstream/gs-core.svg)](https://jitpack.io/#graphstream/gs-core)

The GraphStream project is a java library that provides an API to model,
analyze and visualize graphs and dynamic graphs.

Check out the Website <http://www.graphstream-project.org/> for more information.

## Install GraphStream

### Install Major releases

You can download GraphStream on the [github releases pages](https://github.com/graphstream/gs-core/releases/), or on the website <http://www.graphstream-project.org/download>.

But the preferred way to install GraphStream is through a build tool such as [Maven](https://maven.apache.org/) or [Gradle](https://gradle.org/).

GraphStream major releases are distributed on the Central repository so you only need to specify the dependency through its group and artifact ids. 

```xml
<!-- https://mvnrepository.com/artifact/org.graphstream/gs-core -->
<dependency>
    <groupId>org.graphstream</groupId>
    <artifactId>gs-core</artifactId>
    <version>2.0</version>
</dependency>
```

### Install nightly builds / development branches

For specific needs, development version can be used through the build tools using [JitPack](https://jitpack.io/#graphstream/gs-core)

In order to use JitPack one need to specify the repository:

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

and the dependency:

```xml
<dependencies>
    <dependency>
        <groupId>com.github.graphstream</groupId>
        <artifactId>gs-core</artifactId>
        <version>Tag</version>
    </dependency>
</dependencies>
```

You can use any version of `gs-core` you need. Simply specify the desired version in the `<version>` tag. The version can be a git tag name (e.g. `2.0`), a commit number, or a branch name followed by `-SNAPSHOT` (e.g. `dev-SNAPSHOT`). More details on the [possible versions on jitpack](https://jitpack.io/#graphstream/gs-core).

## User interface

`gs-core` does not ship any default user interface anymore. In order to display graphs, one need a GraphStream viewer (mainly [gs-ui-javafx](https://github.com/graphstream/gs-ui-javafx) or [gs-ui-swing](https://github.com/graphstream/gs-ui-swing)). These are the steps to get a viewer:

1. Download a GraphStream viewer and add the jar to your classpath (or as a dependency to `pom.xml`, for maven users).
2. Set a system property to tell `gs-core` which viewer to use :
```java
         System.setProperty("org.graphstream.ui", "javafx");
```
3. Enjoy `Graph.display()` as usual.

## Help

You may check the documentation on the website <http://www.graphstream-project.org/>. 
You may also share your questions on the mailing list at <http://sympa.litislab.fr/sympa/subscribe/graphstream-users>.

## License

See the COPYING file.
