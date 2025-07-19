package com.example.HTTPServer;

public class Main {
	public static void main(String[] args) {
		MyHTTPServer myHTTPServer = new MyHTTPServer(8080, 10);
		myHTTPServer.StartServer();
	}
}