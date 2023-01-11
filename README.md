# Explore-with-me project
Explore with me - REST API for an application that provides the ability to share information about interesting events and helps find company to participate in them.

## Tech stack:
- Java 11;
- Spring MVC, Spring WebFlux;
- Spring Data, SQL, H2, Hibernate;
- Maven(multi-module project), Lombok, Junit, Mockito;
- Docker, Postman.

## Project Structure:
There are 2 microservices made as modules in project:

Ewm-service - contains everything necessary for operation:
- Viewing events without authorization;
- Ability to create and manage categories;
- Events and working with them - creating, moderation;
- User requests to participate in an event - request, confirmation, rejection.
- Creating and managing compilations.
- Adding and deleting likes of events, forming ratings. 
- Runs on port 8080.

Stats-server - stores the number of views and allows to make various selections for analyzing the application usage.
- Separate service for collecting statistics.
- Runs on port 9090.

## Quick start:
While in the directory on the command line, type:

`mvn package`  
`docker-compose up`

## Services description:
<details>

__The ewm-service, assigned port: 8080__

>__Public (available to all users)__
>> - API for working with events
>> - API for working with categories
> 
>__Private (available only to registered users)__
>> - API for working with events
>> - API for working with requests of the current user to participate in events
>> - API for working with ratings
>
>__Administrative (available only to project administrator)__
>> - API for working with events
>> - API for working with categories
>> - API for working with users
>> - API for working with event compilations

__Statistics service, assigned port: 9090__

>__Administrative (available only to the project administrator)__
>> - API for working with visit statistics

__The _Ratings_ feature is included in the ewm-service__
>> - Sorting events by rating
>> - The ability to like/dislike an event
>> - Only Published events can be rated
>> - Only users with confirmed participation can change the rating
>> - When the rating of an event is changed, the rating of the event creator is also changed
>> - When viewing events publicly, the data of the event creator is hidden.
>> - Event creators cannot rate their own events
</details>

## Swagger specification for REST API:
- [Ewm-service](ewm-main-service-spec.json)
- [Stats-server](ewm-stats-service-spec.json)

## Postman tests for services:
- [Ewm-service](postman/ewm-main-service.json)
- [Stats-server](postman/ewm-stat-service.json)

## Architecture schema of the project:
![Architecture schema](https://github)

## Database description:
![ER diagram](https://github.com/RomanBatrakov/java-filmorat)
