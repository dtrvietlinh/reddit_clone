package provider;

import java.util.List;

import org.keycloak.component.ComponentModel;
import org.keycloak.component.ComponentValidationException;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.provider.ProviderConfigurationBuilder;
import org.keycloak.storage.UserStorageProviderFactory;
import org.keycloak.utils.StringUtil;

public class RedditUserProviderFactory implements UserStorageProviderFactory<RedditUserProvider> {

	public static final String PROVIDER_ID = "reddit-user-provider";

	@Override
	public RedditUserProvider create(KeycloakSession session, ComponentModel model) {
		return new RedditUserProvider(session, model);
	}

	@Override
	public String getId() {
		return PROVIDER_ID;
	}

	@Override
	public String getHelpText() {
		return "Peanuts User Provider";
	}

	@Override
	public List<ProviderConfigProperty> getConfigProperties() {
		return ProviderConfigurationBuilder.create()
			.property(Constants.BASE_URL, "Base URL", "Base URL of the API", ProviderConfigProperty.STRING_TYPE, "", null)
			.build();
	}

	@Override
	public void validateConfiguration(KeycloakSession session, RealmModel realm, ComponentModel config) throws ComponentValidationException {
		if (StringUtil.isBlank(config.get(Constants.BASE_URL))) {
			throw new ComponentValidationException("Configuration not properly set, please verify.");
		}
	}
}
