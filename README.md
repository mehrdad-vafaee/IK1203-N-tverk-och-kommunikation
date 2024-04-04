## Network communication client and server
Implemented as part of IK1203 Networks and Communication course at KTH. 
Both the client and server are implemented in Java.

Task 1: TCP client   

Task 2: TCP client (extended)   

Task 3: HTTP server  

Task 4: HTTP server (concurrent)   

Testing for client:   
Protocol  |  Server name  |  Port  |  Arguments(data sent to server)  
Daytime | time.nist.gov | 13 | None  
Whois | whois.iis.se | 43 | String (a domain name, an IP address or an AS number)  
Whois | whois.internic.net | 43 | String (a domain name, an IP address or an AS number)   
 
Testing for server:   
Use a browser - http://hostname.domain/ask?<parameters>  
Example:        http://hostname.domain/ask?hostname=time.nist.gov&limit=1200&port=13  
