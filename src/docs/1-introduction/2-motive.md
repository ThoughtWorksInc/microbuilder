# Motive

We, some guys in [ThoughtWorks](http://thoughtworks.com/), are working for a client
helping them maintain a large legacy system.

The legacy system were one single J2EE application that contains hundreds of thousands of lines of source code.
The application was tightly coupled and very hard to add new feature.

In the past couple of years,
we turned the system into [Microservice Architecture](http://martinfowler.com/articles/microservices.html).
We experienced a huge improvement in productivity and maintainability during the process.
Nowaday, there are hundreds of microservices in the system,
and we are continuously creating new microservices for new business domains.

These microservices are written in different programming languages
like Scala, Ruby, Java, or JavaScript,
and usually are maintained by different teams spread on different countries.

As a result, the communication between people became one of the most serious topic in our system,
and Microbuilder is designed for communication.
