# Multi-threaded HTTP Web Server

This project implements a simple multi-threaded HTTP web server in Java, supporting non-persistent connections. The server listens for incoming client requests, processes them, and serves the requested files from a specified root directory.

## Features

- **Multi-threading**: Each client request is handled in a separate thread, allowing the server to manage multiple connections concurrently.
- **Non-Persistent Connections**: The server closes the connection after serving each client request.
- **Request Timeout**: Configurable timeout for idle connections.
- **Response Handling**: The server supports basic HTTP responses, including `200 OK`, `400 Bad Request`, `404 Not Found`, and `408 Request Timeout`.

## Class Overview

### `WebServer`

The `WebServer` class is responsible for managing the server's lifecycle. It listens for incoming connections on a specified port, creates worker threads to handle each connection, and supports server shutdown.

#### Constructor

```java
public WebServer(int port, String root, int timeout)
