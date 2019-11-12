# GraphStream


[![Build Status](https://travis-ci.org/graphstream/gs-core.svg?branch=dev)](https://travis-ci.org/graphstream/gs-core)

The GraphStream project is a java library that provides an API to model,
analyze and visualize graphs and dynamic graphs.

Check out the Website <http://www.graphstream-project.org/> for more information.

## Installing GraphStream

The release comes with a pre-packaged jar file named gs-core.jar that contains the GraphStream classes. To start using GraphStream, simply put it in your class path. You can download GraphStream on the [github releases pages](https://github.com/graphstream/gs-core/releases), or on the website <http://www.graphstream-project.org/>.

Maven users, you may include gs-core as a dependency to your project using (https://jitpack.io). 
Simply add the `jitpack` repository to the `pom.xml`:

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

then, add the `gs-core` to your dependencies:

```xml
<dependency>
    <groupId>com.github.graphstream</groupId>
    <artifactId>gs-core</artifactId>
    <version>2.0.0-beta</version>
</dependency>
```

You can use any version of `gs-core` you need. Simply specify the desired version in the `<version>` tag. The version can be a git tag name (e.g. `2.0.0-beta`), a commit number, or a branch name followed by `-SNAPSHOT` (e.g. `dev-SNAPSHOT`). More details on the [possible versions on jitpack](https://jitpack.io/#graphstream/gs-core).

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
