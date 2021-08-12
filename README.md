# Key-value database

A key–value database, or key–value store, is a data storage paradigm designed for storing, retrieving, and managing associative arrays.

![img](https://user-images.githubusercontent.com/54929583/128924448-74dafb34-bee1-42d1-a4a4-2e6e2bedd68e.jpg)

# FAQ
## How to run?

Download intern.jar, then run **java -jar intern.jar** in the terminal.

To access the website, go to localhost:8080.

## What are the options?

You can upload a new pair by specifying key, value, and TTL. To do so, go to the upload page. 

To delete the value, press the big red button on the main page. 

To make a dump or load, choose the "File" option.

## What are the available HTTP requests?
GET:
- /upload, /navbar, / - return an HTML page
- /upload/dump - get a dump file
- /upload/value/{key} - get a value by the key
- /upload/pair/{key} - get a pair (with timestamp) by the key

POST:
- /upload/load - load pairs from a dump file
- /upload - upload a pair by key, value, TTL (if not specified, set to 60)

DELETE:
- /{key} - delete a pair by the key

## Some details

### About TTL

Query request to the repository in PairRepository compares saved timestamp with the current. If the timestamp is outdated, then return null. There is a scheduled task that deletes all useless pairs.

### HTTP

To handle requests, RestController was implemented. Business logic contains in pairService.

### H2

The Entity Pair contains the key, value, and timestamp when the pair should be removed. The UploadController has no business logic, so after receiving the pair, it sends the data to the model. PairService checks if the pair has already been added to the repository and if so, updates the value and TTL, otherwise it saves the new entity.
