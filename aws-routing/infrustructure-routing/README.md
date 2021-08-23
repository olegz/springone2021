# Welcome to the CDK Java project for function-aws-springone!

The _function-aws-springone_ example defines function configuration to be deployed to AWS. which consists of two functions.

After deployment you can invoke your function via API Gateway using the following URL as an example:
```
curl -X POST https://4rk7hiobg0.execute-api.eu-west-3.amazonaws.com/functionRouter -H "spring.cloud.function.definition: uppercase" -H "Content-Type: application/json"  -d '"foo"'
```
NOTE: The `4rk7hiobg0.execute-api.eu-west-3.amazonaws.com` is the unique endpoint address so yuo have to change it to reflect your environment. 

===
The `cdk.json` file tells the CDK Toolkit how to execute your app.

It is a [Maven](https://maven.apache.org/) based project, so you can open this project with any Maven compatible Java IDE to build and run tests.

After installing and configuring AWS CDK environment as described [here](https://aws.amazon.com/cdk/?nc1=h_ls), simply run

```
cdk deploy

```

. . . which will build your project and deploy it to your instance of AWS.

## Other useful commands

 * `mvn package`     compile and run tests
 * `cdk ls`          list all stacks in the app
 * `cdk synth`       emits the synthesized CloudFormation template
 * `cdk diff`        compare deployed stack with current state
 * `cdk docs`        open CDK documentation

Enjoy!
