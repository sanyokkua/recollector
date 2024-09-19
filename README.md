# Recollector

Welcome to **Recollector**, your ultimate organizer app designed to help you keep track of your favorite movies, video
games, and more!

## Overview

Recollector is an intuitive application that helps you manage and organize your entertainment and tasks. Whether you're
a movie enthusiast, a gamer, or someone who loves staying organized, Recollector is tailored for you.

<!-- TOC -->

* [Recollector](#recollector)
    * [Overview](#overview)
    * [Key Features](#key-features)
    * [Use Cases](#use-cases)
    * [Why You’ll Love It](#why-youll-love-it)
    * [Getting Started](#getting-started)
        * [Prerequisites](#prerequisites)
        * [Environment Variables](#environment-variables)
        * [Build and Run](#build-and-run)
    * [Development](#development)
    * [Technologies and Libraries](#technologies-and-libraries)
    * [Technical Details](#technical-details)
    * [Short REST API Description:](#short-rest-api-description)
        * [Helper Operations](#helper-operations)
        * [Authentication REST Controller](#authentication-rest-controller)
        * [Category Management](#category-management)
        * [Item Management](#item-management)
* [App Screens](#app-screens)

<!-- TOC -->

## Key Features

- **Create Categories**: Customize and create categories to organize your items, such as movies, video games, or other
  interests.
- **Add Items**: Easily add items to each category to keep track of what you’ve watched, played, or plan to enjoy in the
  future.
- **Status Tracking**: Track the status of each item with labels like `TODO_LATER`, `IN_PROGRESS`, or `FINISHED`.
- **Edit and Remove**: Flexibly edit or remove categories and items as your interests and priorities change.

## Use Cases

- **Entertainment Tracker**: Maintain a detailed log of movies and video games you've interacted with. Plan your next
  watch or play, and keep track of your favorites.
- **To-Do List**: Use Recollector as a versatile to-do list, organizing tasks by category, setting statuses, and staying
  on top of your activities.

## Why You’ll Love It

- **User-Friendly Interface**: Designed to be intuitive and easy for everyone to use.
- **Customizable**: Tailor the app to fit your specific needs and preferences.
- **Stay Organized**: Keep all your entertainment and tasks in one neatly organized and easily accessible place.

## Getting Started

To run the project locally, you'll need to build and run both the application and the database.

### Prerequisites

- **Java 21**: [Java Documentation](https://docs.oracle.com/en/java/)
- **Maven**: [Maven Documentation](https://maven.apache.org/guides/)
- **Docker**: [Docker Documentation](https://docs.docker.com/)
- **Node.js**: [Node.js Documentation](https://nodejs.org/en)

### Environment Variables

Set the following environment variables before running the backend (For manual run without docker compose):

```bash
export DB_USER_NAME=development
export DB_USER_PASSWORD=dev_pass
export JDBC_URL=jdbc:postgresql://localhost:5402/recollector
export JWT_REFRESH=secretd21uy3id28ib3duybc2uy3vfbuyfdkey
export JWT_REFRESH_EXP_HOURS=1
export JWT_SECRET=secretdb2uy3id28ib3duybc2uy3vfbuyfdkey
export JWT_SECRET_EXP_MINUTES=1
export LOG_LEVEL=DEBUG
```

For running Liquibase:

```bash
export DB_USER_NAME=development
export DB_USER_PASSWORD=dev_pass
export JDBC_URL=jdbc:postgresql://localhost:5402/recollector
```

### Build and Run

1. **Build Maven Project**:
   ```bash
   mvn clean install
   ```

2. **Build Docker Images Individually**:

    - Backend:
      ```bash
      cd backend
      docker build -t recollector-backend .
      ```

    - Liquibase:
      ```bash
      cd ../liquibase
      docker build -t recollector-liquibase .
      ```

   Alternatively, build images using Docker Compose:

   ```bash
   docker-compose build
   ```

   To build individual images with Docker Compose:

   ```bash
   docker-compose build backend
   docker-compose build liquibase
   ```

3. **Run Docker Compose**:

   To build and run all images:
   ```bash
   mvn clean install && docker-compose up --build
   ```

   To simply run:
   ```bash
   docker-compose up
   ```

   To shut down:
   ```bash
   docker-compose down
   ```

   The app will be available at [http://localhost:8080](http://localhost:8080).

## Development

For development:

- **UI Development**:
  ```bash
  npm run dev
  ```

- **Backend Development**:

  ```bash
  cd backend
  # Set all env vars
  export DB_USER_NAME=development
  export DB_USER_PASSWORD=dev_pass
  export JDBC_URL=jdbc:postgresql://localhost:5402/recollector
  export JWT_REFRESH=secretd21uy3id28ib3duybc2uy3vfbuyfdkey
  export JWT_REFRESH_EXP_HOURS=1
  export JWT_SECRET=secretdb2uy3id28ib3duybc2uy3vfbuyfdkey
  export JWT_SECRET_EXP_MINUTES=1
  export LOG_LEVEL=DEBUG
  # Run spring-boot app
  mvn spring-boot:run
  ```

## Technologies and Libraries

- **Java 21**: [Java Documentation](https://docs.oracle.com/en/java/)
- **Spring Boot 3**: [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- **Springdoc OpenAPI 2.5.0**: [Springdoc OpenAPI Documentation](https://springdoc.org/)
- **Commons Lang 3**: [Commons Lang Documentation](https://commons.apache.org/proper/commons-lang/)
- **JJWT**: [JJWT Documentation](https://github.com/jwtk/jjwt)
- **Testcontainers**: [Testcontainers Documentation](https://www.testcontainers.org/)
- **Frontend Maven Plugin**: [Frontend Maven Plugin Documentation](https://github.com/eirslett/frontend-maven-plugin)
- **PostgreSQL**: [PostgreSQL Documentation](https://www.postgresql.org/docs/)
- **ReactJS**: [React Documentation](https://react.dev/)
- **React-Redux**: [React-Redux Documentation](https://react-redux.js.org/)
- **React Router**: [React Router Documentation](https://reactrouter.com/)
- **MUI**: [MUI Documentation](https://mui.com/)
- **ViteJS**: [ViteJS Documentation](https://vitejs.dev/)
- **Yup**: [Yup Documentation](https://github.com/jquense/yup)
- **TypeScript**: [TypeScript Documentation](https://www.typescriptlang.org/docs/)

## Technical Details

Recollector is a Single Page Application (SPA) built with TypeScript, ReactJS, MUI, and Vite. The backend is a Java
Spring Boot application that provides REST APIs. Communication between the UI and backend is achieved via REST
endpoints. The application uses JWT tokens for security with both access and refresh tokens.

The project is a multimodule Maven project including frontend and backend modules, as well as Liquibase for database
management. The build process involves compiling the frontend first, which is then integrated into the backend build.

## Short REST API Description:

### Helper Operations

| Http Method | Path                        | Short Description        | Secured |
|-------------|-----------------------------|--------------------------|---------|
| GET         | /api/v1/helper/settings     | Retrieve user settings   | Yes     |
| PUT         | /api/v1/helper/settings     | Update user settings     | Yes     |
| GET         | /api/v1/helper/statistics   | Retrieve user statistics | Yes     |
| GET         | /api/v1/helper/itemStatuses | Retrieve item statuses   | No      |

### Authentication REST Controller

| Http Method | Path                         | Short Description       | Secured |
|-------------|------------------------------|-------------------------|---------|
| POST        | /api/v1/auth/reset-password  | Reset user password     | No      |
| POST        | /api/v1/auth/register        | Register a new user     | No      |
| POST        | /api/v1/auth/refresh-token   | Refresh access token    | Yes     |
| POST        | /api/v1/auth/logout          | Logout user             | Yes     |
| POST        | /api/v1/auth/login           | Login an existing user  | No      |
| POST        | /api/v1/auth/forgot-password | Initiate password reset | No      |
| POST        | /api/v1/auth/delete-account  | Delete user account     | Yes     |
| POST        | /api/v1/auth/change-password | Change user password    | Yes     |

### Category Management

| Http Method | Path                             | Short Description            | Secured |
|-------------|----------------------------------|------------------------------|---------|
| GET         | /api/v1/categories/{category_id} | Retrieve a specific category | Yes     |
| PUT         | /api/v1/categories/{category_id} | Update an existing category  | Yes     |
| DELETE      | /api/v1/categories/{category_id} | Delete a specific category   | Yes     |
| GET         | /api/v1/categories               | Retrieve all categories      | Yes     |
| POST        | /api/v1/categories               | Create a new category        | Yes     |

### Item Management

| Http Method | Path                                           | Short Description                          | Secured |
|-------------|------------------------------------------------|--------------------------------------------|---------|
| GET         | /api/v1/categories/{categoryId}/items/{itemId} | Retrieve a specific item within a category | Yes     |
| PUT         | /api/v1/categories/{categoryId}/items/{itemId} | Update an existing item within a category  | Yes     |
| DELETE      | /api/v1/categories/{categoryId}/items/{itemId} | Delete a specific item within a category   | Yes     |
| GET         | /api/v1/categories/{categoryId}/items          | Retrieve all items within a category       | Yes     |
| POST        | /api/v1/categories/{categoryId}/items          | Create a new item within a category        | Yes     |

# App Screens

![screen_00](/docs/Screens/00.png)
![screen_01](/docs/Screens/01.png)
![screen_02](/docs/Screens/02.png)
![screen_03](/docs/Screens/03.png)
![screen_04](/docs/Screens/04.png)
![screen_05](/docs/Screens/05.png)
![screen_06](/docs/Screens/06.png)
![screen_07](/docs/Screens/07.png)
![screen_08](/docs/Screens/08.png)
![screen_09](/docs/Screens/09.png)
![screen_10](/docs/Screens/10.png)
![screen_11](/docs/Screens/11.png)
![screen_12](/docs/Screens/12.png)
![screen_13](/docs/Screens/13.png)
![screen_14](/docs/Screens/14.png)
![screen_15](/docs/Screens/15.png)
![screen_16](/docs/Screens/16.png)
![screen_17](/docs/Screens/17.png)
![screen_18](/docs/Screens/18.png)
![screen_19](/docs/Screens/19.png)
![screen_20](/docs/Screens/20.png)