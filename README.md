# Introduction
Implemented a simple HTTP server application and use it with offthe-shelf HTTP clients (including httpc client)
Built a simple remote file manager on top of the HTTP protocol in the server side
Enhanced the file server application to support simultaneous multi-requests using Read-Write Lock
Implemented the support for Content-Type and Content-Disposition headers

Please refer to [ProjectInfo1.pdf](https://github.com/sunyanl1236/ComputerNetworksProj/blob/master/ProjectInfo1.pdf) and [ProjectInfo2.pdf](https://github.com/sunyanl1236/ComputerNetworksProj/blob/master/ProjectInfo2.pdf)

# Detailed Usage
Server side:
```
httpfs is a simple file server.

usage: httpfs [-v] [-p PORT] [-d PATH-TO-DIR]
-v Prints debugging messages.
-p Specifies the port number that the server will listen and serve at. Default is 8080.
-d Specifies the directory that the server will use to read/write requested files. Default is the current directory when launching the
application.
```

Client side:
General
```
httpc help
httpc is a curl-like application but supports HTTP protocol only. 

Usage:
httpc command [arguments]
The commands are:
get executes a HTTP GET request and prints the response.
post executes a HTTP POST request and prints the response.
help prints this screen.

Use "httpc help [command]" for more information about a command.
```

Get Usage
```
httpc help get
Usage: httpc get [-v] [-h key:value] URL
Get executes a HTTP GET request for a given URL.
-v Prints the detail of the response such as protocol, status, and headers.
-h key:value Associates headers to HTTP Request with the format 'key:value'.
```

Post Usage
```
httpc help post
usage: httpc post [-v] [-h key:value] [-d inline-data] [-f file] URL
Post executes a HTTP POST request for a given URL with inline data or from file.
-v Prints the detail of the response such as protocol, status, and headers.
-h key:value Associates headers to HTTP Request with the format 'key:value'.
-d string Associates an inline data to the body HTTP POST request.
-f file Associates the content of a file to the body HTTP POST request.
Either [-d] or [-f] can be used but not both.
```
