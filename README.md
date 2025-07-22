# Java HTTP Server

----------

Simple HTTP Server written in Java. Currently, It can serve HTML, CSS and Scripts to Google Chrome and Firefox. 

## Usage

```bash
HTTPServer start --path <path> --port <port> --backlog <backlog> --verbose
```

- Required JDK version is 24.

## Defaults
- port = 8080
- backlog = 10
- path = User Home Directory (for now)
- Verbosity = false