This example defines function configuration which consists of two functions.
Given that _RoutingFunction_(`functionRouter`) is automatically enebled you can interact with these individual functions 
via single HTTP endpoint (providing API gateway was setup, which it is in infrustructure portion of this demo) while 
providing `spring.cloud.function.definition` header with the name of the function you want to invoke.
For example:
```
curl -X POST https://4rk7hiobg0.execute-api.eu-west-3.amazonaws.com/functionRouter -H "spring.cloud.function.definition: uppercase" -H "Content-Type: application/json"  -d '"foo"'
```
NOTE: The `4rk7hiobg0.execute-api.eu-west-3.amazonaws.com` is the unique endpoint address so yuo have to change it to reflect your environment. 

You can also provide `spring.cloud.function.definition` if you only want to invoke a specigfic function.
What this means is that spring-cloud-function AWS integration will automatically default to Routing Function (`functionRouter`) or you can specify 

