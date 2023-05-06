package provider;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.keycloak.component.ComponentModel;
import org.keycloak.credential.CredentialInput;
import org.keycloak.credential.CredentialInputUpdater;
import org.keycloak.credential.CredentialInputValidator;
import org.keycloak.models.GroupModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserCredentialModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.credential.PasswordCredentialModel;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.UserStorageProvider;
import org.keycloak.storage.user.UserLookupProvider;
import org.keycloak.storage.user.UserQueryProvider;
import org.keycloak.storage.user.UserRegistrationProvider;

import lombok.extern.slf4j.Slf4j;
import provider.external.RedditPasswordEncoder;
import provider.external.RedditUser;
import provider.external.UpdateCredentialsRequest;
import provider.external.UserClient;
import provider.external.UserClientSimpleHttp;

@Slf4j
public class RedditUserProvider implements UserStorageProvider, UserLookupProvider, 
										   UserQueryProvider, CredentialInputUpdater, 
										   CredentialInputValidator, UserRegistrationProvider {

	private final KeycloakSession session;
	private final ComponentModel model;
	private final UserClient client;
	private final RedditPasswordEncoder pwdEncoder;
	
	protected Map<String, UserModel> loadedUsers = new HashMap<>();

	public RedditUserProvider(KeycloakSession session, ComponentModel model) {
		this.session = session;
		this.model = model;
		this.client = new UserClientSimpleHttp(session, model);
		this.pwdEncoder = new RedditPasswordEncoder();
	}
	
	@Override
	public void close() {
	}

	@Override
	public boolean supportsCredentialType(String credentialType) {
		return PasswordCredentialModel.TYPE.equals(credentialType);
	}

	@Override
	public boolean isConfiguredFor(RealmModel realm, UserModel user, String credentialType) {
		return supportsCredentialType(credentialType);
	}
	
	@Override
	public boolean isValid(RealmModel realm, UserModel user, CredentialInput input) {
		if (!supportsCredentialType(input.getType()) || !(input instanceof UserCredentialModel)) {
			return false;
		}

		String credentialData;
		try {
			credentialData = client.getCredentialData(StorageId.externalId(user.getId()));
			log.debug("Received credential data for userId {}: %{}", user.getId(), credentialData);
			if (credentialData == null) {
				return false;
			}
		} catch (WebApplicationException e) {
			log.error(String.format("Request to verify credentials for userId %s failed with response status %d",
				user.getId(), e.getResponse().getStatus()), e);
			return false;
		}

		UserCredentialModel cred = (UserCredentialModel) input;
		boolean isValid = pwdEncoder.matches(cred.getChallengeResponse(), credentialData);
		log.debug("Password validation result: {}", isValid);
		return isValid;
	}
	
	@Override
	public boolean updateCredential(RealmModel realm, UserModel user, CredentialInput input) {
		log.debug("Try to update credentials type {} for user {}.", input.getType(), user.getId());
		if (!supportsCredentialType(input.getType()) || !(input instanceof UserCredentialModel)) {
			return false;
		}

		UserCredentialModel cred = (UserCredentialModel) input;

		String encodedPwd = pwdEncoder.encode(cred.getChallengeResponse());
		log.debug("Sending updateCredential request for userId {}", user.getId());
		try {
			Response updateResponse = client.updateCredentialData(new UpdateCredentialsRequest(StorageId.externalId(user.getId()), encodedPwd));
			return updateResponse.getStatusInfo().getFamily().equals(Response.Status.Family.SUCCESSFUL);
		} catch (WebApplicationException e) {
			log.warn("Credential data update for user {} failed with response {}", user.getId(), e.getResponse().getStatus());
			return false;
		}
	}

	@Override
	public void disableCredentialType(RealmModel realm, UserModel user, String credentialType) {
	}

	@Override
	public Stream<String> getDisableableCredentialTypesStream(RealmModel realm, UserModel user) {
		return Stream.empty();
	}

	@Override
	public UserModel getUserById(RealmModel realm, String id) {
		log.debug("getUserById: {}", id);
		return findUser(realm, StorageId.externalId(id));
	}

	@Override
	public UserModel getUserByUsername(RealmModel realm, String username) {
		log.debug("getUserByUsername: {}", username);
		return findUser(realm, username);
	}

	@Override
	public UserModel getUserByEmail(RealmModel realm, String email) {
		log.debug("getUserByEmail: {}", email);
		return findUser(realm, email);
	}

	private UserModel findUser(RealmModel realm, String identifier) {
		UserModel adapter = loadedUsers.get(identifier);
		if (adapter == null) {
			try {
				RedditUser user = client.getPeanutById(identifier);
				if (user==null) return null;
				adapter = new UserAdapter(session, realm, model, user);
				loadedUsers.put(identifier, adapter);
			} catch (WebApplicationException e) {
				log.warn("User with identifier '{}' could not be found, response from server: {}", identifier, e.getResponse().getStatus());
			}
		} else {
			log.debug("Found user data for {} in loadedUsers.", identifier);
		}
		return adapter;
	}

	@Override
	public int getUsersCount(RealmModel realm) {
		return client.getPeanutsCount();
	}

	@Override
	public Stream<UserModel> searchForUserStream(RealmModel realm, String search, Integer firstResult, Integer maxResults) {
		log.debug("searchForUserStream, search={}, first={}, max={}", search, firstResult, maxResults);
		return toUserModelStream(client.getPeanuts(search, firstResult, maxResults), realm);
	}

	@Override
	public Stream<UserModel> searchForUserStream(RealmModel realm, Map<String, String> params, Integer firstResult, Integer maxResults) {
		log.debug("searchForUserStream, params={}, first={}, max={}", params, firstResult, maxResults);
		return toUserModelStream(client.getPeanuts(null, firstResult, maxResults), realm);
	}

	private Stream<UserModel> toUserModelStream(List<RedditUser> peanuts, RealmModel realm) {
		log.debug("Received {} users from provider", peanuts.size());
		return peanuts.stream().map(user -> new UserAdapter(session, realm, model, user));
	}

	@Override
	public Stream<UserModel> getGroupMembersStream(RealmModel realm, GroupModel group, Integer firstResult, Integer maxResults) {
		return Stream.empty();
	}

	@Override
	public Stream<UserModel> searchForUserByUserAttributeStream(RealmModel realm, String attrName, String attrValue) {
		return Stream.empty();
	}

	@Override
	public UserModel addUser(RealmModel realm, String username) {
		log.debug("Received {} at {}", username, realm);
		RedditUser user = new RedditUser();
		user.setUsername(username);
		user.setEmail(username);
		user.setCreated(Instant.now().getEpochSecond());
		user.setEnabled(true);
		if (client.addUser(user)) {
			log.debug("Called addUser successfully");
			return new UserAdapter(session, realm, model, user);
		}
		return null;
	}

	@Override
	public boolean removeUser(RealmModel realm, UserModel user) {
		log.debug("Called removeUser successfully");
		return client.deleteUser(user.getUsername());
	}
}
