package com.example;



import static java.util.Collections.singletonList;

import java.util.HashMap;
import java.util.Map;

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

public class InfrustructureRoutingStack extends Stack {
    public InfrustructureRoutingStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public InfrustructureRoutingStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        Map<String, String> rngEnvVars = new HashMap<>();
        rngEnvVars.put("LOG_LEVEL", "INFO");
        rngEnvVars.put("POWERTOOLS_SERVICE_NAME", "User-Service");

//        rngEnvVars.put("spring_cloud_function_definition", "function");


        Function rngService = new Function(this, "User-Service", FunctionProps.builder()
                .runtime(Runtime.JAVA_11)
                .functionName("functionRouter")
                .code(Code.fromAsset("../function-aws-springone/target/function-aws-springone-1.0.0.RELEASE-aws.jar"))
                .handler("org.springframework.cloud.function.adapter.aws.FunctionInvoker")
                .memorySize(1024)
                .timeout(Duration.seconds(20))
                .environment(rngEnvVars)
                .build());

        HttpApi httpApi = new HttpApi(this, "RoutingGateway", HttpApiProps.builder()
                .apiName("RoutingGateway")
                .build());

        httpApi.addRoutes(AddRoutesOptions.builder()
                .path("/functionRouter")
                .methods(singletonList(HttpMethod.POST))
                .integration(new LambdaProxyIntegration(LambdaProxyIntegrationProps.builder()
                        .handler(rngService)
                        .payloadFormatVersion(PayloadFormatVersion.VERSION_2_0)
                        .build()))
                .build());

    }
}
