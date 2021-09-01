# Welcome to Spring One 2021 samples for _Spring Cloud Function_ session 

This repository consists of two sample directories which demonstrate different style of deployments as well as few other things.
For more details please refer to the individual README files.

* aws-native - demonstrates GraalVM native deployment in AWS
* aws-routing - demonstrates function routing feature with AWS

Both modules are relying on [AWS Cloud Development Kit](https://aws.amazon.com/cdk/) (CDK).


The `spring-cloud-stream` directory contains a simple Spring Boot microservice that demonstrates the processor pattern using Spring Cloud Function, Spring Cloud Stream and the Solace Binder. The microservice also contains commented out code for two options to implement dynamic publishing. 
1. Using StreamBridge
2. Using a special message header - BindersHeader.TARGET_DESTINATION

Have more questions? Ask in the [Solace Community](https://solace.community).

