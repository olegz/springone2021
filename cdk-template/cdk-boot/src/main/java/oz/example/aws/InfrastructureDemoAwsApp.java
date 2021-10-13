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

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;

import software.amazon.awscdk.core.App;
import software.amazon.awscdk.core.StackProps;

/**
 * @author Mark Sailes (AWS Lambda)
 * @author Oleg Zhurakousky (Spring/VMWARE)
 *
 */
@SpringBootApplication
@EnableConfigurationProperties(CDKProperties.class)
@PropertySource("file:cdk.properties")
public class InfrastructureDemoAwsApp {

	public static void main(final String[] args) {
		SpringApplication.run(InfrastructureDemoAwsApp.class, args);
	}

	@Bean
	public ApplicationRunner runner(CDKProperties cdkProperties) {
		return args -> {
			App app = new App();
			new InfrustructureDemoAwsStack(app, cdkProperties, StackProps.builder()
	                // If you don't specify 'env', this stack will be environment-agnostic.
	                // Account/Region-dependent features and context lookups will not work,
	                // but a single synthesized template can be deployed anywhere.

	                // Uncomment the next block to specialize this stack for the AWS Account
	                // and Region that are implied by the current CLI configuration.
	                /*
	                .env(Environment.builder()
	                        .account(System.getenv("CDK_DEFAULT_ACCOUNT"))
	                        .region(System.getenv("CDK_DEFAULT_REGION"))
	                        .build())
	                */

	                // Uncomment the next block if you know exactly what Account and Region you
	                // want to deploy the stack to.
	                /*
	                .env(Environment.builder()
	                        .account("123456789012")
	                        .region("us-east-1")
	                        .build())
	                */


	                // For more information, see https://docs.aws.amazon.com/cdk/latest/guide/environments.html
	                .build());

	        app.synth();
		};
	}

}
