:spring_data_rest: current
:spring_data_commons: current
:toc:
:icons: font
:source-highlighter: prettify
:project_id: gs-accessing-data-rest
:java_version: 17

This guide walks you through the process of creating an application that accesses
relational JPA data through a link:/guides/gs/rest-hateoas[hypermedia-based]
link:/understanding/REST[RESTful] front end.

== What You Will Build

You will build a Spring application that lets you create and retrieve `Person` objects
stored in a database by using Spring Data REST. Spring Data REST takes the features of
https://spring.io/projects/spring-hateoas[Spring HATEOAS] and
https://spring.io/projects/spring-data-jpa[Spring Data JPA] and automatically combines them
together.

NOTE: Spring Data REST also supports link:/guides/gs/accessing-neo4j-data-rest[Spring Data
Neo4j], link:/guides/gs/accessing-gemfire-data-rest[Spring Data Gemfire], and
link:/guides/gs/accessing-mongodb-data-rest[Spring Data MongoDB] as backend data stores,
but those are not part of this guide.

include::https://raw.githubusercontent.com/spring-guides/getting-started-macros/main/guide_introduction.adoc[]

[[scratch]]
== Starting with Spring Initializr

You can use this https://start.spring.io/#!type=maven-project&groupId=com.example&artifactId=accessing-data-rest&name=accessing-data-rest&description=Demo%20project%20for%20Spring%20Boot&packageName=com.example.accessing-data-rest&dependencies=data-rest,data-jpa,h2[pre-initialized project] and click Generate to download a ZIP file. This project is configured to fit the examples in this tutorial.

To manually initialize the project:

. Navigate to https://start.spring.io.
This service pulls in all the dependencies you need for an application and does most of the setup for you.
. Choose either Gradle or Maven and the language you want to use. This guide assumes that you chose Java.
. Click *Dependencies* and select *Rest Repositories*, *Spring Data JPA*, and *H2 Database*.
. Click *Generate*.
. Download the resulting ZIP file, which is an archive of a web application that is configured with your choices.

NOTE: If your IDE has the Spring Initializr integration, you can complete this process from your IDE.

NOTE: You can also fork the project from Github and open it in your IDE or other editor.

[[initial]]
== Create a Domain Object

Create a new domain object to present a person, as the following listing (in
  `src/main/java/com/example/accessingdatarest/Person.java`) shows:

====
[source,java,tabsize=2]
----
include::complete/src/main/java/com/example/accessingdatarest/Person.java[]
----
====

The `Person` object has a first name and a last name. (There is also an ID object that is
configured to be automatically generated, so you need not deal with that.)

== Create a Person Repository

Next, you need to create a simple repository, as the following listing (in
  `src/main/java/com/example/accessingdatarest/PersonRepository.java`) shows:

====
[source,java,tabsize=2]
----
include::complete/src/main/java/com/example/accessingdatarest/PersonRepository.java[]
----
====

This repository is an interface that lets you perform various operations involving
`Person` objects. It gets these operations by extending the
https://docs.spring.io/spring-data/commons/docs/{spring_data_commons}/api/org/springframework/data/repository/PagingAndSortingRepository.html[`PagingAndSortingRepository`] interface that is defined in
Spring Data Commons.

At runtime, Spring Data REST automatically creates an implementation of this interface.
Then it uses the https://docs.spring.io/spring-data/rest/docs/{spring_data_rest}/api/org/springframework/data/rest/core/annotation/RepositoryRestResource.html[@RepositoryRestResource] annotation to direct Spring MVC to
create RESTful endpoints at `/people`.

NOTE: `@RepositoryRestResource` is not required for a repository to be exported. It is
used only to change the export details, such as using `/people` instead of the default
value of `/persons`.

Here you have also defined a custom query to retrieve a list of `Person` objects based on
the `lastName`. You can see how to invoke it later in this guide.

Spring Boot automatically spins up Spring Data JPA to create a concrete implementation of
the `PersonRepository` and configure it to talk to a back end in-memory database by using
JPA.

Spring Data REST builds on top of Spring MVC. It creates a collection of Spring MVC
controllers, JSON converters, and other beans to provide a RESTful front end. These
components link up to the Spring Data JPA backend. When you use Spring Boot, this is all
autoconfigured. If you want to investigate how that works, by looking at the
`RepositoryRestMvcConfiguration` in Spring Data REST.

== Running the Application

You can now run the application by executing the main method in `AccessingDataRestApplication`.
You can run the program from your IDE, or by executing the following Gradle command in the project root directory:
```
./gradlew bootRun
```

Alternatively, you could use Maven to run the application using the command:
```
./mvnw spring-boot:run
```

== Test the Application

Now that the application is running, you can test it. You can use any REST client you
wish. The following examples use the *nix tool, `curl`.

First you want to see the top level service. The following example shows how to do so:

====
[src,bash]
----
$ curl http://localhost:8080
{
  "_links" : {
    "people" : {
      "href" : "http://localhost:8080/people{?page,size,sort}",
      "templated" : true
    }
  }
}
----
====

The preceding example provides a first glimpse of what this server has to offer. There is a `people` link located at `http://localhost:8080/people`. It has some options, such as `?page`, `?size`, and `?sort`.

NOTE: Spring Data REST uses the http://stateless.co/hal_specification.html[HAL format] for
JSON output. It is flexible and offers a convenient way to supply links adjacent to the
data that is served.

The following example shows how to see the people records (none at present):

====
[src,bash]
----
$ curl http://localhost:8080/people
{
  "_embedded" : {
    "people" : []
  },
  "_links" : {
    "self" : {
      "href" : "http://localhost:8080/people{?page,size,sort}",
      "templated" : true
    },
    "search" : {
      "href" : "http://localhost:8080/people/search"
    }
  },
  "page" : {
    "size" : 20,
    "totalElements" : 0,
    "totalPages" : 0,
    "number" : 0
  }
}
----
====

There are currently no elements and, hence, no pages. Time to create a new `Person`!
The following listing shows how to do so:

====
[src,bash]
----
$ curl -i -H "Content-Type:application/json" -d '{"firstName": "Frodo", "lastName": "Baggins"}' http://localhost:8080/people
HTTP/1.1 201 Created
Server: Apache-Coyote/1.1
Location: http://localhost:8080/people/1
Content-Length: 0
Date: Wed, 26 Feb 2014 20:26:55 GMT
----

- `-i`: Ensures you can see the response message including the headers. The URI of the
newly created `Person` is shown.
- `-H "Content-Type:application/json"`: Sets the content type so the application knows the
payload contains a JSON object.
- `-d '{"firstName": "Frodo", "lastName": "Baggins"}'`: Is the data being sent.
- If you are on Windows, the command above will work on https://docs.microsoft.com/en-us/windows/wsl[WSL]. If you can't install WSL, you might need to replace the single quotes with double quotes and escape the existing double quotes, i.e. `-d "{\"firstName\": \"Frodo\", \"lastName\": \"Baggins\"}"`.
====

NOTE: Notice how the response to the `POST` operation includes a `Location` header. This contains
the URI of the newly created resource. Spring Data REST also has two methods
(`RepositoryRestConfiguration.setReturnBodyOnCreate(…)` and `setReturnBodyOnUpdate(…)`)
that you can use to configure the framework to immediately return the representation of
the resource just created. `RepositoryRestConfiguration.setReturnBodyForPutAndPost(…)` is
a shortcut method to enable representation responses for create and update operations.

You can query for all people, as the following example shows:

====
[src,bash]
----
$ curl http://localhost:8080/people
{
  "_links" : {
    "self" : {
      "href" : "http://localhost:8080/people{?page,size,sort}",
      "templated" : true
    },
    "search" : {
      "href" : "http://localhost:8080/people/search"
    }
  },
  "_embedded" : {
    "people" : [ {
      "firstName" : "Frodo",
      "lastName" : "Baggins",
      "_links" : {
        "self" : {
          "href" : "http://localhost:8080/people/1"
        }
      }
    } ]
  },
  "page" : {
    "size" : 20,
    "totalElements" : 1,
    "totalPages" : 1,
    "number" : 0
  }
}
----
====

The `people` object contains a list that includes `Frodo`. Notice how it includes a
`self` link. Spring Data REST also uses
https://www.atteo.org/2011/12/12/Evo-Inflector.html[Evo Inflector] to pluralize the name of
the entity for groupings.

You can query directly for the individual record, as follows:

====
[src,bash]
----
$ curl http://localhost:8080/people/1
{
  "firstName" : "Frodo",
  "lastName" : "Baggins",
  "_links" : {
    "self" : {
      "href" : "http://localhost:8080/people/1"
    }
  }
}
----
====

NOTE: This might appear to be purely web-based. However, behind the scenes, there is an H2
relational database. In production, you would probably use a real one, such as PostgreSQL.

TIP: In this guide, there is only one domain object. With a more complex system, where domain objects are related to each other, Spring Data REST renders additional links to help navigate to connected records.

You can find all the custom queries, as shown in the following example:

====
[src,bash]
----
$ curl http://localhost:8080/people/search
{
  "_links" : {
    "findByLastName" : {
      "href" : "http://localhost:8080/people/search/findByLastName{?name}",
      "templated" : true
    }
  }
}
----
====

You can see the URL for the query, including the HTTP query parameter, `name`. Note that
this matches the `@Param("name")` annotation embedded in the interface.

The following example shows how to use the `findByLastName` query:

====
[src,bash]
----
$ curl http://localhost:8080/people/search/findByLastName?name=Baggins
{
  "_embedded" : {
    "persons" : [ {
      "firstName" : "Frodo",
      "lastName" : "Baggins",
      "_links" : {
        "self" : {
          "href" : "http://localhost:8080/people/1"
        }
      }
    } ]
  }
}
----
====

Because you defined it to return `List<Person>` in the code, it returns all of the
results. If you had defined it to return only `Person`, it picks one of the `Person`
objects to return. Since this can be unpredictable, you probably do not want to do that
for queries that can return multiple entries.

You can also issue `PUT`, `PATCH`, and `DELETE` REST calls to replace, update, or delete
existing records (respectively). The following example uses a `PUT` call:

====
[src,bash]
----
$ curl -X PUT -H "Content-Type:application/json" -d '{"firstName": "Bilbo", "lastName": "Baggins"}' http://localhost:8080/people/1
$ curl http://localhost:8080/people/1
{
  "firstName" : "Bilbo",
  "lastName" : "Baggins",
  "_links" : {
    "self" : {
      "href" : "http://localhost:8080/people/1"
    }
  }
}
----
====


The following example uses a `PATCH` call:

====
[src,bash]
----
$ curl -X PATCH -H "Content-Type:application/json" -d '{"firstName": "Bilbo Jr."}' http://localhost:8080/people/1
$ curl http://localhost:8080/people/1
{
  "firstName" : "Bilbo Jr.",
  "lastName" : "Baggins",
  "_links" : {
    "self" : {
      "href" : "http://localhost:8080/people/1"
    }
  }
}
----
====

NOTE: `PUT` replaces an entire record. Fields not supplied are replaced with `null`. You
can use `PATCH` to update a subset of items.

You can also delete records, as the following example shows:

====
[src,bash]
----
$ curl -X DELETE http://localhost:8080/people/1
$ curl http://localhost:8080/people
{
  "_links" : {
    "self" : {
      "href" : "http://localhost:8080/people{?page,size,sort}",
      "templated" : true
    },
    "search" : {
      "href" : "http://localhost:8080/people/search"
    }
  },
  "page" : {
    "size" : 20,
    "totalElements" : 0,
    "totalPages" : 0,
    "number" : 0
  }
}
----
====

A convenient aspect of this hypermedia-driven interface is that you can discover all the
RESTful endpoints by using curl (or whatever REST client you like). You need not exchange
a formal contract or interface document with your customers.

== Summary

Congratulations! You have developed an application with a
link:/guides/gs/rest-hateoas[hypermedia-based] RESTful front end and a JPA-based back end.

== See Also

The following guides may also be helpful:

* https://spring.io/guides/gs/rest-hateoas/[Building a Hypermedia-Driven RESTful Web Service]
* https://spring.io/guides/gs/accessing-gemfire-data-rest/[Accessing GemFire Data with REST]
* https://spring.io/guides/gs/accessing-mongodb-data-rest/[Accessing MongoDB Data with REST]
* https://spring.io/guides/gs/accessing-data-mysql/[Accessing data with MySQL]
* https://spring.io/guides/gs/accessing-neo4j-data-rest/[Accessing Neo4j Data with REST]
* https://spring.io/guides/gs/consuming-rest/[Consuming a RESTful Web Service]
* https://spring.io/guides/gs/securing-web/[Securing a Web Application]
* https://spring.io/guides/tutorials/rest/[Building REST services with Spring]
* https://spring.io/guides/gs/spring-boot/[Building an Application with Spring Boot]
* https://spring.io/guides/gs/testing-restdocs/[Creating API Documentation with Restdocs]
* https://spring.io/guides/gs/rest-service-cors/[Enabling Cross Origin Requests for a RESTful Web Service]

include::https://raw.githubusercontent.com/spring-guides/getting-started-macros/main/footer.adoc[]
