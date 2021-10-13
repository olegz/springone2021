package oz.example.aws;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.aws.cdk")
public class CDKProperties {


	/**
	 * Corresponds to AWS Cloud Formation id - REQUIRED
	 */
	private String cloudFormationId;

	/**
	 * Corresponds to 'spring.cloud.function.definition' property - OPTIONAL
	 */
	private String functionDefinition;

	public String getCloudFormationId() {
		return cloudFormationId;
	}

	public void setCloudFormationId(String cloudFormationId) {
		this.cloudFormationId = cloudFormationId;
	}

	public String getFunctionDefinition() {
		return functionDefinition;
	}

	public void setFunctionDefinition(String functionDefinition) {
		this.functionDefinition = functionDefinition;
	}
}
