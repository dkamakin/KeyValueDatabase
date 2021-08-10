# Key-value storage service

A key–value database, or key–value store, is a data storage paradigm designed for storing, retrieving, and managing associative arrays.

# FAQ
## How to run?

Download intern.jar, then run **java -jar intern.jar** in the terminal.
To access the website, go to localhost:8080.

## What are the options?

You can upload a new pair by specifying key, value, and TTL. To do so, go to the upload page. To delete the value, press the big red button on the main page. To make a dump or load, choose the "File" option.

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

Query request to the repository in pairRepository compares saved timestamp with the current. If the timestamp is outdated, then return null. There is a scheduled task that deletes all useless pairs.

### HTTP

To handle requests, RestController was implemented. Business logic contains in pairService.