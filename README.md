# code-analyser project
Dissertation project meant to offer any GitHub passionate the possibility of performing analysis over public repositories.

## Purpose
GitHub does offer at this point an insights menu. It allows the user to inspect, analyse and visualize some statistics on the
repository. However, there are way more advanced statistics or charts that can be produced that would allow us have a deeper
understanding.
This project allows anyone to analyse public work on GitHub, by inserting a username. This operation will retrieve all its 
repositories, and enable the user to view each one of them.

### Features
- Incorporates JGit and eGit libraries to navigate through commits in a repository
- Allows gathering pure statistical data
- Includes tables, charts, graphs, maps and other tools for better visualization
- Provides a timebar that we can use to select the period of time needed for the user to anaylse
- Uses PMD for running static analysis over *Java code only*
- Identifies antipatterns several kinds of antipatterns from a project point of view

Most of these features are available for any programming language inside the repository.

## Prerequisites

- clone this repository
- Sign Up/In for an account on GitHub.com
- install a `MySQL` database. I've used `8.0.16 (MySQL Community Server - GPL)`
- install a Tomcat server. I've personally used Tomcat 8.5.42, but you could also try with newer versions, I guess.


## Running code-analyser

- create two environment/system variables: `GITHUB_USER` and `GITHUB_PASS` which you'll need to add. Provide them the values
you used for signing up on GitHub.com
- connect to the database and create a schema inside the database, and name it `codeanalyser`
- search for a file named `application.properties` inside the project. It should be under `code-analyser/src/main/resources/application.properties`
- set the database properties for your own connection under `spring.datasource.username`, `spring.datasource.password` and `spring.datasource.url`
(if you are not using default ones)
- build the project via Maven `mvn clean install`
- deploy the resulting *war* file on Tomcat
- open *http://localhost:8080/*

## Using code-analyser

- unfortunately you will not be able to provide a valid `name` and `password` to login as you have no entry inside table `codeanalyser.user`
- SQL scrips can be found under: <project-root>/src/main/resources/scripts.sql. If for some reason Spring JPA does not create the necessary tables,
 please use the DDL scripts provided in `scripts.sql`. Create a SQL insert script and add an entry inside `codeanalyser.user` using ths DML 
 scrips provided in `scripts.sql`
- use the values you inserted above as `name` and `password`
- if you've ran the scrips just as in `scrips.sql` then user should be `test` and user should be `pass`
- in the next screen, provide a valid GitHub username and retrieve all its public repositories
- enjoy stalking repositories


