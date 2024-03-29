
# MultiThread Cached-Proxy Server


This project implements a multi-threaded Proxy server in Java using socket programming. The Proxy server is designed to handle multiple client requests concurrently while also caching frequently accessed responses using an LRU (Least Recently Used) cache system. Additionally, the server logs client interaction details to files, enabling monitoring and analysis of traffic.

## Author

- [@Er-Sadiq-Killedar](https://github.com/Er-Sadiq)


## Features

- Multi-threaded: The Proxy server is capable of handling multiple client requests concurrently, improving efficiency and responsiveness.
- Caching: Implemented an LRU cache system using LinkedList and HashMap to store frequently accessed responses. This helps in reducing response time for repetitive requests.
- Socket Programming: Utilized Java's socket programming capabilities to establish communication between clients and the Proxy server.
- File Handling: Implemented file handling to log client interaction details, allowing for monitoring and analysis of traffic patterns.

## Configuration

The Proxy server can be configured by modifying the following parameters in the ProxyServer.java file:

- Cache Size: Adjust the maximum size of the cache by modifying the MAX_CACHE_SIZE constant.
- Port Number: Change the port number on which the Proxy server listens by modifying the PORT constant.
- Log File Path: Modify the LOG_FILE_PATH constant to specify the file path where client interaction details will be logged.
## Demo

Flow Diagram :
![Flow Chat](https://github.com/Er-Sadiq/MultiThread-Cached-Proxy-Server/assets/125464939/5dc78660-271f-4c2a-80e8-23228b561da0)
Running ProxyServer.java (Class)
![Vs_Code](https://github.com/Er-Sadiq/MultiThread-Cached-Proxy-Server/assets/125464939/bed07263-8000-45bf-95d4-374f47c3ad72)
Text Log of Uncached Server :
![UnCached-V1](https://github.com/Er-Sadiq/MultiThread-Cached-Proxy-Server/assets/125464939/d6bfa987-5d9c-4d88-b496-73ae1b9c59ab)
Text Log of cached Server :
![cahce resome](https://github.com/Er-Sadiq/MultiThread-Cached-Proxy-Server/assets/125464939/a1681f4c-ee2e-4892-bdd4-a584a815735b)




