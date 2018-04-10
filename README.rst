GraphStream
===========

.. image:: https://travis-ci.org/graphstream/gs-core.svg?branch=2.x
    :target: https://travis-ci.org/graphstream/gs-core

.. image:: http://graphstream-project.org/media/img/gs-110.png

The GraphStream project is java library that provides a API to model, 
analyze and visualize graphs and dynamic graphs.

Check out the Website (http://www.graphstream-project.org/) for more information.

Installing GraphStream
----------------------

The release comes with a pre-packaged jar file named gs-core.jar that
contains the GraphStream classes. To start using GraphStream, 
simply put it in your class path. You can download GraphStream on the [github releases pages](https://github.com/graphstream/gs-core/releases) 
or on the website (http://www.graphstream-project.org/). 

Maven users, you may include gs-core as a dependency to your project using (https://jitpack.io). 
Simply add the jitpack repository to the `pom.xml`: 

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
    <version>2.0</version>
</dependency>
```


Help
----

You may check the documentation on the website (http://www.graphstream-project.org/). 
You may also share your questions on the mailing list at 

http://sympa.litislab.fr/sympa/subscribe/graphstream-users 


License
-------

See the COPYING file.