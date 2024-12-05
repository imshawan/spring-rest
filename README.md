# Spring REST Service

A comprehensive boilerplate project designed to accelerate the development of Spring Boot applications. This starter comes equipped with essential configurations, reusable components, and modular architecture to provide a seamless starting point for new developers and teams.

The project emphasizes standardized practices, robust error handling, and scalability, making it ideal for building production-grade REST APIs. Additionally, it includes prebuilt modules for user onboarding and file upload services, enabling faster implementation of common features without reinventing the wheel.

## Features

### What comes Out-of-the-box?

This starter project provides a ready-to-use setup for quickly bootstrapping Spring Boot applications with:

1. Standardized API Responses

   - Success responses via `ApiResponse` class.
   - Error responses via `HTTPError` class.
   - Prebuilt User Onboarding Service

2. Basic user management functionality (e.g., user registration, login).

   - Extendable design for adding authentication or user roles.
   - File Upload Service
   - JWT authentication and validation layers
   - CORS implementation

3. Custom exception handlers to manage and format application-level and system-level errors.

4. Preconfigured connection to MongoDB for easy database integration.

   - Spring Data MongoDB setup for seamless interaction with MongoDB collections.
   - Environment Configurations

5. Modular and Scalable Architecture - adheres to Spring best practices for clean code, modularity, and scalability.
6. RESTful Standards

   - Preconfigured REST controllers and response formats to follow REST best practices.
     Security Placeholder.
   - Global Exception Handling

7. API Layer Abstraction
   - Service layer abstraction for better separation of concerns and testability.
   - Placeholder for Swagger Integration

## Setup and Installation

### Prerequisites

- Java 21 or higher installed
- Spring Boot 3.4.0
- Maven 3.8.1 or higher installed
- MongoDB Atlas or Local server set up

### Steps

1. Clone the repository:

```bash
git clone https://github.com/imshawan/spring-rest.git
cd spring-rest
```

2. Configure the application properties:
   Update the database configuration in src/main/resources/application.properties:

```properties
spring.application.name=spring-rest
spring.data.mongodb.uri=
spring.data.mongodb.database=RestStarterDb
spring.web.resources.static-locations=classpath:/static/,file:uploads/
server.port=8080
logging.level.org.mongodb.driver=warn
jwt.secret=your-secret-key
jwt.expiration=3600000
cors.allowedOrigins=http://localhost:8005,http://localhost:8000
```

3. Build and run the application:

```bash
mvn clean install
mvn spring-boot:run
```

4. Access the application:

Base URL: `http://localhost:8080`

## API Endpoints

| HTTP Method | Endpoint                              | Description                                      | Request Body                                          | Response                                     |
| ----------- | ------------------------------------- | ------------------------------------------------ | ----------------------------------------------------- | -------------------------------------------- |
| `POST`      | `/api/files/upload`                   | Upload a file to the server.                     | `file`: Multipart file                                | Success message with file info.              |
| `POST`      | `/api/users/register`                 | Register a new user.                             | JSON with `username`, `email`, `fullname`, `password` | Success message with user info.              |
| `POST`      | `/api/users/signin`                   | Sign in an existing user and return a JWT token. | JSON with `username/email` and `password`             | Success message with user data and token.    |
| `GET`       | `/api/users/{id}`                     | Fetch user profile by user ID.                   | N/A                                                   | User profile data or 404 if not found.       |
| `PUT`       | `/api/users/{id}`                     | Update user profile by user ID.                  | JSON with user data                                   | Updated user data or 404 if not found.       |
| `DELETE`    | `/api/users/{id}`                     | Delete user profile by user ID.                  | N/A                                                   | 204 No Content if deleted, 404 if not found. |
| `POST`      | `/api/users/profile/{userId}/picture` | Upload profile picture for the user.             | `file`: Multipart file                                | Success message with file info.              |

### Response Handlers

#### Success Response

The `ApiResponse` class is used to standardize successful responses.
Example:

```json
{
  "message": "Data retrieved successfully",
  "status": 200,
  "path": "/api/users/123",
  "statusMessage": "OK",
  "data": {
    "key": "value"
  }
}
```

#### Error Response

The `HTTPError` class encapsulates error details for failed HTTP requests.

Example:

```json
{
  "message": "Invalid input data",
  "status": 400,
  "path": "/api/users/register",
  "statusMessage": "Bad Request"
}
```

## **Contributing**

We welcome contributions to this project! If you'd like to contribute, please follow these steps:

1. **Fork the repository**: Create your own fork of the project on GitHub.
2. **Clone the repository**: Clone your fork to your local machine to work on it.
   ```bash
   git clone https://github.com/imshawan/spring-rest.git
   ```
3. **Create a new branch**: Create a new branch to work on your changes.
   ```bash
   git checkout -b your-branch-name
   ```
4. **Make changes**: Implement your feature, fix a bug, or improve the documentation.
5. **Commit changes**: Commit your changes with a descriptive commit message.
   ```bash
   git commit -m "Describe your changes"
   ```
6. **Push changes**: Push your changes to your forked repository.
   ```bash
   git push origin your-branch-name
   ```
7. **Create a pull request**: Go to the original repository and create a pull request from your branch. Provide a detailed description of the changes you've made.

### **Guidelines**:

- Ensure your code follows the existing code style and conventions.
- Write clear commit messages.
- Ensure all tests pass before submitting a pull request.
- Be respectful and considerate when interacting with the community.


## **License**

This project is licensed under the **MIT License** - see the [LICENSE](LICENSE.md) file for details.

## **Author**

This project is maintained by [Shawan Mandal](https://github.com/imshawan).

If you have any questions or suggestions, feel free to reach out via GitHub or at [github@imshawan.dev](mailto:github@imshawan.dev).
