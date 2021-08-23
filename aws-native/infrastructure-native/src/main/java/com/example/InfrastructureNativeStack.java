/*
 * Copyright 2021-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example;

import software.amazon.awscdk.core.BundlingOptions;
import software.amazon.awscdk.core.CfnOutput;
import software.amazon.awscdk.core.CfnOutputProps;
import software.amazon.awscdk.core.Construct;
import software.amazon.awscdk.core.DockerImage;
import software.amazon.awscdk.core.DockerVolume;
import software.amazon.awscdk.core.Duration;
import software.amazon.awscdk.core.Stack;
import software.amazon.awscdk.core.StackProps;
import software.amazon.awscdk.services.apigatewayv2.AddRoutesOptions;
import software.amazon.awscdk.services.apigatewayv2.HttpApi;
import software.amazon.awscdk.services.apigatewayv2.HttpApiProps;
import software.amazon.awscdk.services.apigatewayv2.HttpMethod;
import software.amazon.awscdk.services.apigatewayv2.PayloadFormatVersion;
import software.amazon.awscdk.services.apigatewayv2.integrations.LambdaProxyIntegration;
import software.amazon.awscdk.services.apigatewayv2.integrations.LambdaProxyIntegrationProps;
import software.amazon.awscdk.services.lambda.Code;
import software.amazon.awscdk.services.lambda.Function;
import software.amazon.awscdk.services.lambda.FunctionProps;
import software.amazon.awscdk.services.lambda.Runtime;
import software.amazon.awscdk.services.logs.RetentionDays;
import software.amazon.awscdk.services.s3.assets.AssetOptions;

import java.util.Arrays;
import java.util.List;

import static java.util.Collections.singletonList;
import static software.amazon.awscdk.core.BundlingOutput.ARCHIVED;

/**
 * This stack creates a simple AWS Lambda function which is triggered by an Amazon API Gateway.
 *
 * A Docker image is used build the artifact, this also gives the advantage of being platform agnostic.
 *
 * Outputs:
 *
 *  UppercaseApiUrl: The endpoint url of the API Gateway
 *
 * @author Mark Sailes (AWS Lambda)
 * @author Oleg Zhurakousky (Spring/VMWARE)
 */
public class InfrastructureNativeStack extends Stack {
    public InfrastructureNativeStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public InfrastructureNativeStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        // The commands to be run as part of your docker build.
        // -P native is used to enable the Spring native profile
        List<String> functionOnePackagingInstructions = Arrays.asList(
                "-c",
                "cd function-aws-native-springone " +
                "&& mvn clean package -P native -DskipTests " +
                "&& cp /asset-input/function-aws-native-springone/target/function-aws-native-springone-0.0.1-SNAPSHOT-native-zip.zip /asset-output/"
        );

        // The docker image used has all the tools required to build the native binary.
        // GraalVM native-image and a suitable version of Maven.
        // The .m2 folder is mapped to the container to reduce excess downloading.
        BundlingOptions.Builder builderOptions = BundlingOptions.builder()
                .command(functionOnePackagingInstructions)
                .image(DockerImage.fromRegistry("marksailes/al2-graalvm"))
                .volumes(singletonList(
                        DockerVolume.builder()
                                .hostPath(System.getProperty("user.home") + "/.m2/")
                                .containerPath("/root/.m2/")
                                .build()
                ))
                .user("root")
                .outputType(ARCHIVED);

        Function uppercaseFunction = new Function(this, "UppercaseFunction", FunctionProps.builder()
                // To run the native binary a custom Lambda runtime is used.
                // PROVIDED_AL2 is the Amazon Linux 2 OS - https://docs.aws.amazon.com/lambda/latest/dg/lambda-runtimes.html
                .runtime(Runtime.PROVIDED_AL2)
                .functionName("uppercase")
                .code(Code.fromAsset("../", AssetOptions.builder()
                        .bundling(builderOptions.build())
                        .build()))
                // The function definition associated to your spring cloud function
                .handler("uppercase")
                // How long the Lambda service will run your function code for before timing out.
                .timeout(Duration.seconds(30))
                // CPU is allocated proportionally to memory
                // native binaries require much less memory and CPU compared to Java
                .memorySize(256)
                // All standard out and standard error is automatically collected and centrally logged to CloudWatch
                // Delete logs after 1 week to save storage costs
                // NOTE: An additional Lambda function will be create to enable this functionality
                // https://docs.aws.amazon.com/cdk/api/latest/docs/aws-lambda-readme.html#log-group
                .logRetention(RetentionDays.ONE_WEEK)
                .build());

        // Create a HTTP API in API Gateway
        HttpApi httpApi = new HttpApi(this, "UppercaseFunctionAPI", HttpApiProps.builder()
                .apiName("UppercaseFunction")
                .build());

        // Whenever a GET request is made to the /uppercase path, forward that request to our Lambda function
        httpApi.addRoutes(AddRoutesOptions.builder()
                .path("/uppercase")
                .methods(singletonList(HttpMethod.GET))
                .integration(new LambdaProxyIntegration(LambdaProxyIntegrationProps.builder()
                        .handler(uppercaseFunction)
                        .payloadFormatVersion(PayloadFormatVersion.VERSION_2_0)
                        .build()))
                .build());

        // Save and output the endpoint URL to ease manual testing.
        CfnOutput apiUrl = new CfnOutput(this, "UppercaseFunctionCFN", CfnOutputProps.builder()
                .exportName("UppercaseFunction")
                .value(httpApi.getApiEndpoint())
                .build());
    }
}
