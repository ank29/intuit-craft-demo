**Comments Service**

**Overview**
The Comments Service is a RESTful API designed for a social media website. It allows users to add comments, likes, and dislikes to posts, reply to comments (including nested replies), and retrieve comments with associated likes and dislikes. Users can also view the list of users who liked or disliked a comment.

**Features**
1. Add Comments: Users can add comments to posts and reply to existing comments.
2. Like/Dislike Comments: Users can like or dislike comments.
3. View First level Comments: Retrieve n first level comments for a specific post.
4. View Replies: Retrieve replies for a specific comments, including nested replies.
5. View Likes/Dislikes: View the list of users who liked or disliked a comment.

**Technologies Used**
1. Java 17
2. Spring Boot 3.3.2
3. Hibernate
4. PostgreSQL
5. Lombok
6. Gradle

**Prerequisites**

Java 17 or higher
PostgreSQL database
Gradle


**Getting Started**
**Database Setup**

1. Create a PostgreSQL database:
   `CREATE DATABASE intuitdemo;`

2.Update the application.properties file with your database credentials:


`spring.datasource.url=jdbc:postgresql://localhost:5432/intuitdemo
spring.datasource.username=your_username
spring.datasource.password=your_password`

**Build and Run the Application**

Clone the repository:

`git clone https://github.com/llimon/intuit-craft-demo.git
cd IntuitCraftDemoProject`

Build the application using Gradle:

`./gradlew build`

Run the application:

`./gradlew bootRun`

The application will start on http://localhost:8080.

**API Endpoints**


**Add a Comment**

   URL: /comments

   Method: POST

Request Body:
`{
"postId": 1,
"userId": 1,
"parentCommentId": 0,
"content": "This is a comment."
}`


Response: Returns the added comment.

**Like a Comment**

URL: /comments/{commentId}/like

Method: POST

Request Params: userId

Response: Returns the updated comment.


**Dislike a Comment**

URL: /comments/{commentId}/dislike

Method: POST

Request Params: userId

Response: Returns the updated comment.

**Get Top Level Comments**

URL: /post/top/{postId}

Method: GET

Request Params: limit (optional)

Response: Returns the list of top n first level comments for the specified post.

**Get Users Who Liked a Comment**

URL: /comments/{commentId}/likes

Method: GET

Response: Returns the list of users who liked the comment.


**Get Users Who Disliked a Comment**

URL: /comments/{commentId}/dislikes

Method: GET

Response: Returns the list of users who disliked the comment.

**Project Structure**


`src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── example/
│   │           └── intuitcraftdemoproject/
│   │               ├── controller/
│   │               │   └── CommentController.java
|   |               |   └── PostController.java  
│   │               ├── dto/
│   │               │   └── CommentRequest.java
│   │               ├── exception/
│   │               │   ├── GlobalExceptionHandler.java
│   │               │   └── ResourceNotFoundException.java
│   │               ├── model/
│   │               │   ├── Comment.java
│   │               │   └── User.java
│   │               ├── repository/
│   │               │   ├── CommentRepository.java
│   │               │   └── UserRepository.java
│   │               └── service/
│   │                   └── CommentService.java
│   └── resources/
│       └── application.properties
└── test/
└── java/
└── com/
└── example/
└── intuitcraftdemoproject/
└── CommentServiceTests.java`


**Testing**
To run the tests, use the following command:

`./gradlew test`


**Future Enhancements**
1. Add authentication and authorization.
2. Develop a basic UI for better UX.
3. Implement Caching for Enhanced Scalability
4. Asynchronous processing for operations that do not need to be immediate, such as updating like/dislike counts.
5. Content Moderation for comments
6. Profiler for metrics and monitoring
7. Proper database sharding or partition strategy for scalability


This README provides a comprehensive overview of the project, its setup, usage, and future directions. Adjust the content based on your actual project details and requirements.

