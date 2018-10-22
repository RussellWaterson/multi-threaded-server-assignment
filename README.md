# Multi-Threaded Server Assignment

The aim of this assignment from my final year Networks module, was to write a small, multi-threaded webserver, and use appropriate tools to check it works correctly. Written in Java, the webserver is capable of serving multiple documents in parallel, which gives correct status responses for ﬁle found or not found. The server correctly serves documents when tested against “wget” and “curl”.

Also included is a HTTP/1.1 client which fetches ﬁles and prints them on the screen.

Extra functionality above and beyond the spec included: authentication, content types, (client) rendering some HTML.


## Server

To run the server, use the test harness script.

Tests a number of different file types (tests 2-6) and root dir (test 1).

Also included are a 401 authorisation error (test 7, correct **username:password** is **RussellW:password1** stored in Base64) and 404 not found error (test 8).


## Client

To run the client, just use the "java Client" command after it has been compiled (which the server script should take care of).

Enter the website of choice and whether you would like the html code to be printed on the terminal.

It will attempt to connect to the site and render the html in a JFrame.