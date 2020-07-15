# Test task (Telegram bot) 
Сreate a telegram bot management web application.

1. The telegram bot gives the user reference information about the entered city. For example, a user enters: “Moscow”, the chatbot answers: “Do not forget to visit Red Square. Well, you don’t have to go to the Central Department Store))). ”

2. Data about cities should be stored in a database.

3. Manage data on cities (add new cities and information about them, change and delete any information) is necessary through REST WebService.

### Tools and technologies ###

1.  **JDK version** - 8;
2.  **Database connection pooling** - HikariCP 3.4.1;
3.  **Data access** - SpringData, Hibernate 5.4.12.Final;
4.  **Database migration tool** - Liquibase 3.8.9;
5.  **Validation** - Hibernate Validator 6.1.0.Final;
6.  **Build tool** - Apache Maven 3.5;
7.  **Web server** - Apache Tomcat 8.5.50;
8.  **Application container** - Spring IoC., SpringBoot 2.3.1.RELEASE;
9.  **Tool for demonstrating** - Postman v7.24.0;
10.  **Database** - PostgreSQL 11;
11.  **Testing** - JUnit5, Mockito 3.2.4, Spring test module, HyperSQL DataBase 2.5.0, Hamcrest 2.2, Jsonpath 2.4.0;
12.  **Logging** - Log4j 2.13.3.

### Application launch ###

1.  **Install Java** - java version must not be less than 8.
2.  **Install Apache Maven** - maven version must not be less 3.5.
3.  **Install PostgreSQL** - PostgreSQL version must not be less 11. If you just want to check how bot works,
In [application.properties](https://github.com/SergeiVolosevich/telegram-bot/blob/master/src/main/resources/application.properties) just change property - spring.profiles.active from 'prod' to 'dev'.
Application will start with in-memory database(HyperSQL DataBase).
4.  **Download this repository and buil application** - In command line execute command 'mvn clean install'.
5.  **Lauch Application** - In command line execute command 'java -jar trip-advisor.jar';

**Bot name** - travel_tips_task_bot;

**Bot token** - you can find in [telegram.properties](https://github.com/SergeiVolosevich/telegram-bot/blob/master/src/main/resources/telegram.properties).

## REST API ##

### City ###
*	**GET**: /cities?page=0&size=10 - Get cities with limit(pagination). Page and size parameters are required. Size parameter start from 0;
*	**GET**: /cities/id - Get city by id;
*	**POST**: /cities - Create city. City should be sent using request-body in appropriate JSON-format;

	Example of JSON-format:
	
    `{
    			"name": "Name",
    			"description": "Description"
    }`

**Description** must be between 50 - 2000 characters, **name** - 3 - 100 characters.

*	**PUT**: /cities/id - Update concrete city. City should be sent using request-body in appropriate JSON-format;

	Example of JSON-format:
	
    `{
                "id": "1",
    			"name": "Name",
    			"description": "description"
    }`

*	**DELETE**: /city/id - Delete city by id.
