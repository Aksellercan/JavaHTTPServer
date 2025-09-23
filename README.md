# Java HTTP Server

----------

Simple HTTP Server written in Java. Currently, It can serve HTML, CSS and Scripts to Google Chrome, Firefox and other web browsers. 

## Usage

JRE/JDK is required to run. Command to run it in terminal:
```bash
java -jar HTTPServer.jar
```
### Help
To get help run:
```bash
# you can run h or help
HTTPServer help
HTTPServer start --name <server-name> --dir <directory> --port <port> --backlog <backlog> --verbose
```

## Defaults
- port = 0 (OS assigns an available port)
- backlog = 10
- name = JavaHTTPServer
- path = path jar is running from
- Verbosity = false

## Issues
1. Does not support HTTPS