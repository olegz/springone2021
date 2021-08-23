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

import static java.util.Collections.singletonList;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import software.amazon.awscdk.core.BundlingOptions;
import software.amazon.awscdk.core.CfnOutput;
import software.amazon.awscdk.core.CfnOutputProps;
import software.amazon.awscdk.core.Construct;
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
import software.amazon.awscdk.services.s3.assets.AssetOptions;

/**
 * @author Mark Sailes (AWS Lambda)
 * @author Oleg Zhurakousky (Spring/VMWARE)
 *
 */
public class InfrustructureRoutingStack extends Stack {
    public InfrustructureRoutingStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public InfrustructureRoutingStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        Map<String, String> rngEnvVars = new HashMap<>();
        rngEnvVars.put("spring_cloud_function_definition", "function");

        List<String> functionOnePackagingInstructions = Arrays.asList(
                "/bin/sh",
                "-c",
                "mvn clean install " +
                "&& cp /asset-input/target/function-aws-springone-1.0.0.RELEASE-aws.jar /asset-output/");

        BundlingOptions.Builder builderOptions = BundlingOptions.builder()
                .command(functionOnePackagingInstructions)
                .image(Runtime.JAVA_11.getBundlingImage())
                .volumes(singletonList(DockerVolume.builder()
                        .hostPath(System.getProperty("user.home") + "/.m2/")
                        .containerPath("/root/.m2/")
                        .build()))
                .user("root");

        Function rngService = new Function(this, "RoutingGateway", FunctionProps.builder()
                .runtime(Runtime.JAVA_11)
                .functionName("routingGateway")
                .code(Code.fromAsset("../function-aws-springone/", AssetOptions.builder()
                        .bundling(builderOptions
                                .build())
                        .build()))
                .handler("org.springframework.cloud.function.adapter.aws.FunctionInvoker")
                .memorySize(1024)
                .timeout(Duration.seconds(20))
                .environment(rngEnvVars)
                .build());

        HttpApi httpApi = new HttpApi(this, "RoutingGatewayAPI", HttpApiProps.builder()
                .apiName("RoutingGateway")
                .build());

        httpApi.addRoutes(AddRoutesOptions.builder()
                .path("/routingGateway")
                .methods(singletonList(HttpMethod.POST))
                .integration(new LambdaProxyIntegration(LambdaProxyIntegrationProps.builder()
                        .handler(rngService)
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
