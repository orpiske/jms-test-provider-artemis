JMS Test Util: Hornet Q provider
============

Introduction
----

This is an Hornet Q provider for JMS Test Util.


Building
----

To download the project you can use:

```
git clone https://github.com/orpiske/jms-test-provider-hornetq.git -b
jms-test-provider-hornetq-1.0.0
```

You can use Maven to build the project. No additional configuration or setup
should* be required. To compile and install the project, please run:

```
mvn clean install
```

The build system will generate deliverables in zip, tar.bz2, tar.gz format.
Project specific jars will also be generated.



Usage
----

Annotate the test class with:

```
@RunWith(JmsTestRunner.class)
@Provider(
        value = HornetQProvider.class,
        configuration = HornetQConfiguration.class)
```

References
----

* [Main Site](http://orpiske.net/)
* [JBoss HornetQ](http://hornetq.jboss.org/)

