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

package oz.example.aws;

import static java.util.Collections.singletonList;

import java.util.HashMap;
import java.util.Map;

import software.amazon.awscdk.core.CfnOutput;
import software.amazon.awscdk.core.CfnOutputProps;
import software.amazon.awscdk.core.Construct;
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

/**
 *
 */
public class InfrustructureDemoAwsStack extends Stack {
    public InfrustructureDemoAwsStack(final Construct scope, final CDKProperties cdkProperties) {
        this(scope, cdkProperties, null);
    }

    public InfrustructureDemoAwsStack(final Construct scope, final CDKProperties cdkProperties, final StackProps props) {
        super(scope, cdkProperties.getCloudFormationId(), props);

        System.out.println("==> CloudFormationId: " + cdkProperties.getCloudFormationId());
        System.out.println("==> f-definition: " + cdkProperties.getFunctionDefinition());

        Map<String, String> rngEnvVars = new HashMap<>();
        rngEnvVars.put("spring_cloud_function_definition", cdkProperties.getFunctionDefinition());

        String normalizedFunctionDefName = cdkProperties.getFunctionDefinition().replace("|", "_").replace(",", "_");

        Function function = new Function(this, normalizedFunctionDefName + "FN", FunctionProps.builder()
                .runtime(Runtime.JAVA_11)
                .functionName(normalizedFunctionDefName)
                .code(Code.fromAsset("target/demo-aws-app-1.0.0.RELEASE-aws.jar"))
                .handler("org.springframework.cloud.function.adapter.aws.FunctionInvoker")
                .memorySize(1024)
                .timeout(Duration.seconds(20))
                .environment(rngEnvVars)
                .build());

        HttpApi httpApi = new HttpApi(this, normalizedFunctionDefName + "API", HttpApiProps.builder()
                .apiName(normalizedFunctionDefName)
                .build());

        httpApi.addRoutes(AddRoutesOptions.builder()
                .path("/" + normalizedFunctionDefName)
                .methods(singletonList(HttpMethod.ANY))
                .integration(new LambdaProxyIntegration(LambdaProxyIntegrationProps.builder()
                        .handler(function)
                        .payloadFormatVersion(PayloadFormatVersion.VERSION_2_0)
                        .build()))
                .build());

		// Save and output the endpoint URL to ease manual testing.
		CfnOutput apiUrl = new CfnOutput(this, normalizedFunctionDefName + "OUT", CfnOutputProps.builder()
				.exportName("uppercase")
				.value(httpApi.getApiEndpoint())
				.build());
    }
}
