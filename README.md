# WebServer Project

## Overview

This project implements a multi-threaded web server that supports non-persistent connections. The server listens for client requests and handles them using worker threads.

## Files

- `WebServer.java`: Contains the logic for initializing the server, accepting client connections, and managing worker threads

## Usage

### Compilation

To compile the project, ensure you have Java installed and run:

```bash
javac WebServer.java
java WebServer <port> <root> <timeout>
java WebServer 8080 /var/www 300000

**
