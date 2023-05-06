package provider;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.keycloak.common.util.MultivaluedHashMap;
import org.keycloak.component.ComponentModel;
import org.keycloak.credential.LegacyUserCredentialManager;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.SubjectCredentialManager;
import org.keycloak.models.UserModel;
import org.keycloak.storage.ReadOnlyException;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.adapter.AbstractUserAdapterFederatedStorage;

import provider.external.RedditUser;


public class UserAdapter extends AbstractUserAdapterFederatedStorage.Streams {
	
	private final RedditUser user;
	
	public UserAdapter(KeycloakSession session, RealmModel realm, ComponentModel model, RedditUser user) {
		super(session, realm, model);
		this.storageId = new StorageId(storageProviderModel.getId(), user.getUsername());
		this.user = user;
	}

	@Override
	public String getUsername() {
		return user.getUsername();
	}

	@Override
	public void setUsername(String username) {
		throw new ReadOnlyException("user is read only for this update");
	}

	@Override
	public String getEmail() {
		return user.getEmail();
	}
	
	@Override
    public Long getCreatedTimestamp() {       
        return user.getCreated();
    }

	@Override
    public boolean isEnabled() {
        return user.isEnabled();
    }
	
	@Override
    public boolean isEmailVerified() {
		String val = getFirstAttribute(EMAIL_VERIFIED_ATTRIBUTE);       
        if (val == null) return false;
        else return Boolean.valueOf(val);
    }
	
	@Override
    public Map<String, List<String>> getAttributes() {
        MultivaluedHashMap<String, String> attributes = getFederatedStorage().getAttributes(realm, this.getId());
        if (attributes == null) {
            attributes = new MultivaluedHashMap<>();
        }
        attributes.add(UserModel.USERNAME, getUsername());
		attributes.add(UserModel.EMAIL, getEmail());
        return attributes;
    }

	@Override
	public SubjectCredentialManager credentialManager() {
		return new LegacyUserCredentialManager(session, realm, this);
	}

	@Override
	public String getFirstAttribute(String name) {
		List<String> list = getAttributes().getOrDefault(name, List.of());
		return list.isEmpty() ? null : list.get(0);
	}


	@Override
	public Stream<String> getAttributeStream(String name) {
		Map<String, List<String>> attributes = getAttributes();
		return (attributes.containsKey(name)) ? attributes.get(name).stream() : Stream.empty();
	}
}
