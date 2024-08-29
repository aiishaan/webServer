# Multi-threaded HTTP Web Server

This is a Java implementation of a multi-threaded HTTP web server that supports non-persistent connections. The project consists of three main files: `WebServer.java`, `ServerDriver.java`, and `ServerUtils.java`

## Features

- Multi-threaded server capable of handling multiple client connections simultaneously.
- Supports GET requests for static files.
- Implements basic HTTP/1.1 protocol.
- Configurable server port, root directory, and connection timeout.
- Graceful shutdown mechanism.
- Command-line interface for starting and stopping the server.
- Utility methods for handling HTTP-specific date formats and file properties

## Main Components

### 1. WebServer Class (WebServer.java)

The `WebServer` class is the core of the server, setting up the server socket and managing client connections.

#### Key Methods:

- `WebServer(int port, String root, int timeout)`: Constructor to initialize the web server
- `run()`: Main method that keeps the server running and accepting new connections
- `shutdown()`: Method to signal the server to shut down

### 2. ServerDriver Class (ServerDriver.java)

The `ServerDriver` class serves as the entry point for the application, handling command-line arguments and server lifecycle.

#### Key Functionalities:

- Parses command-line arguments for server configuration
- Sets up logging
- Initializes and starts the WebServer
- Provides a command-line interface to stop the server

#### Command-line Options:

- `-p`: Server port number (default: 2025)
- `-t`: Idle connection timeout in milliseconds (default: 0, meaning infinity)
- `-r`: Root directory of the web server (default: current directory)
- `-v`: Log level (options: all, info, off; default: all)

### 3. ServerUtils Class (ServerUtils.java)

The `ServerUtils` class provides utility methods for handling HTTP-specific operations and file properties.

#### Key Methods:

- `getCurrentDate()`: Returns the current date in HTTP format
- `getContentType(File object)`: Determines the content type of a file
- `getContentLength(File object)`: Returns the content length of a file
- `getLastModified(File object)`: Returns the last modified date of a file

## Usage

To use this web server:

1. Compile all Java files.
2. Run the ServerDriver class with desired command-line options. For example:
    ```sh
    java ServerDriver -p 8080 -r /path/to/web/root -t 5000 -v info
    ```
3. The server will start and display configuration information.
4. Type "quit" in the console to stop the server gracefully.

## HTTP Response Handling

The server handles the following scenarios:

- 200 OK: When a requested file is found and served successfully
- 400 Bad Request: For malformed requests or unsupported methods
- 404 Not Found: When a requested file doesn't exist
- 408 Request Timeout: When a client connection times out

## Dependencies

This implementation uses Java's built-in networking, I/O, and logging libraries.

## Limitations

- Only supports GET requests
- Does not support persistent connections

## Future Improvements

- Add support for POST and other HTTP methods
- Implement persistent connections
- Enhance error handling capabilities
- Add configuration file support for easier deployment
