# Java HTTP Server

----------

Simple HTTP Server written in Java. Currently, It can serve HTML, CSS and Scripts to Google Chrome and Firefox. 

## Usage

```bash
HTTPServer start --path <path> --port <port> --backlog <backlog> --verbose
```

- Required JDK version is 24.

## Defaults
- port = 0 (OS assigns an available port)
- backlog = 10
- name = JavaHTTPServer
- path = path jar is running from
- Verbosity = false

## Issues
- Can't navigate file structures to return files
- Does not support HTTPS and CORS
- HTTP/1 currently