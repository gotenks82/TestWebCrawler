TestWebCrawler
==============

A simple web crawler in standard Java.
--------------------------------------

Syntax: java -jar WebCrawlerTest.jar {root_url} {options}"
Options:
max_urls=<n>         sets max number of urls
ethical=true/false   sets ethical behaviour, if true the crawler will check robots.txt for allowed paths, defaults to true
filename=<filename>  sets a specific output filename, defaults to ./webcrawlerresults_yyyyMMddHHmmss.txt

The crawler will start by visiting the root_url specified.
It will extract links from the page content, and for each new valid link it will spawn a worker thread that will visit the link and repeat the process.
The crawler will not specify a user-agent in the request, and will not consider user-agent specific info inside each host's robot.txt, it will simply ignore every disallowed path specify in the robot.txt
The crawler does not implement a throttle to slow down the number if requests per second.
It will log each visited url in stdout and will produce a file with the specified filename containing the list of valid found links.