package gov.utah.dts.openid.config;

import gov.utah.dts.openid.Constants;
import gov.utah.dts.openid.model.Audit;
import gov.utah.dts.openid.model.Member;
import gov.utah.dts.openid.repository.MemberRepository;
import gov.utah.dts.openid.service.AppUserDetails;
import gov.utah.dts.openid.service.AuditService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistration.ProviderDetails;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.converter.ClaimConversionService;
import org.springframework.security.oauth2.core.converter.ClaimTypeConverter;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.StandardClaimNames;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.*;
import java.util.function.Function;

/**
 * Based on Spring Security 5.5.0 version of {@link OidcUserService}
 * An implementation of an {@link OAuth2UserService} that supports OpenID Connect 1.0
 * Provider's.
 *
 * @see OAuth2UserService
 * @see OidcUserRequest
 * @see OidcUser
 * @see DefaultOidcUser
 * @see OidcUserInfo
 */
@Service
@Slf4j
public class CustomOidcUserService implements OAuth2UserService<OidcUserRequest, OidcUser> {

	/**
	 * The member repo.
	 */
	private final MemberRepository memberRepo;

	/**
	 * The audit service.
	 */
	private final AuditService auditService;

	private static final String UNABLE_TO_LOAD_USER = "********** UNABLE TO LOAD USER FOR email: ";
	private static final String INVALID_USER_INFO_RESPONSE_ERROR_CODE = "invalid_user_info_response";
	private static final Converter<Map<String, Object>, Map<String, Object>> DEFAULT_CLAIM_TYPE_CONVERTER = new ClaimTypeConverter(createDefaultClaimTypeConverters());
	private Set<String> accessibleScopes = new HashSet<>(Arrays.asList(OidcScopes.OPENID, OidcScopes.PROFILE, OidcScopes.EMAIL, "directory"));
	private OAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2UserService = new DefaultOAuth2UserService();
	private Function<ClientRegistration, Converter<Map<String, Object>, Map<String, Object>>> claimTypeConverterFactory = (clientRegistration) -> DEFAULT_CLAIM_TYPE_CONVERTER;

	public CustomOidcUserService(MemberRepository memberRepo, AuditService auditService) {
		this.memberRepo = memberRepo;
		this.auditService = auditService;
	}

	@Override
	public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
		Assert.notNull(userRequest, "userRequest cannot be null");
		OidcUserInfo userInfo = null;
		if (this.shouldRetrieveUserInfo(userRequest)) {
			OAuth2User oauth2User = this.oauth2UserService.loadUser(userRequest);
			Map<String, Object> claims = getClaims(userRequest, oauth2User);
			userInfo = new OidcUserInfo(claims);
			/*
			 https://openid.net/specs/openid-connect-core-1_0.html#UserInfoResponse
			 1) The sub (subject) Claim MUST always be returned in the UserInfo Response
			*/
			if (userInfo.getSubject() == null) {
				OAuth2Error oauth2Error = new OAuth2Error(INVALID_USER_INFO_RESPONSE_ERROR_CODE);
				throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString());
			}
			/*
			 2) Due to the possibility of token substitution attacks (see Section 16.11),
			 the UserInfo Response is not guaranteed to be about the End-User identified by the sub (subject) element of the ID Token.
			 The sub Claim in the UserInfo Response MUST be verified to exactly match the sub Claim in the ID Token; if they do not match,
			 the UserInfo Response values MUST NOT be used.
			*/
			if (!userInfo.getSubject().equals(userRequest.getIdToken().getSubject())) {
				OAuth2Error oauth2Error = new OAuth2Error(INVALID_USER_INFO_RESPONSE_ERROR_CODE);
				throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString());
			}
		}

		return getUser(userRequest.getIdToken(), userInfo);
	}

	private Map<String, Object> getClaims(OidcUserRequest userRequest, OAuth2User oauth2User) {
		Converter<Map<String, Object>, Map<String, Object>> converter = this.claimTypeConverterFactory.apply(userRequest.getClientRegistration());
		if (converter != null) {
			return converter.convert(oauth2User.getAttributes());
		}
		return DEFAULT_CLAIM_TYPE_CONVERTER.convert(oauth2User.getAttributes());
	}

	private OidcUser getUser(OidcIdToken idToken, OidcUserInfo userInfo) {
		OidcUser oidcUser = null;
		try {
			var member = loadDatabaseUser(userInfo.getEmail());
			if (member != null) {
				oidcUser = grantMemberAccess(member, idToken, userInfo);
			}
		} catch (Exception e) {
			log.error("exception occurred loading user in getUser: {}", userInfo.getEmail(), e);
			oidcUser = null;
		}
		return oidcUser;
	}

	private boolean shouldRetrieveUserInfo(OidcUserRequest userRequest) {
		// Auto-disabled if UserInfo Endpoint URI is not provided
		ProviderDetails providerDetails = userRequest.getClientRegistration().getProviderDetails();
		if (!StringUtils.hasText(providerDetails.getUserInfoEndpoint().getUri())) {
			return false;
		}
		// The Claims requested by the profile, email, address, and phone scope values
		// are returned from the UserInfo Endpoint (as described in Section 5.3.2),
		// when a response_type value is used that results in an Access Token being
		// issued.
		// However, when no Access Token is issued, which is the case for the
		// response_type=id_token,
		// the resulting Claims are returned in the ID Token.
		// The Authorization Code Grant Flow, which is response_type=code, results in an
		// Access Token being issued.
		if (AuthorizationGrantType.AUTHORIZATION_CODE.equals(userRequest.getClientRegistration().getAuthorizationGrantType())) {
			// Return true if there is at least one match between the authorized scope(s)
			// and accessible scope(s)
			return this.accessibleScopes.isEmpty()
				|| CollectionUtils.containsAny(userRequest.getAccessToken().getScopes(), this.accessibleScopes);
		}
		return false;
	}

	/**
	 * Sets the {@link OAuth2UserService} used when requesting the user info resource.
	 *
	 * @param oauth2UserService the {@link OAuth2UserService} used when requesting the
	 *                          user info resource.
	 * @since 5.1
	 */
	public final void setOauth2UserService(OAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2UserService) {
		Assert.notNull(oauth2UserService, "oauth2UserService cannot be null");
		this.oauth2UserService = oauth2UserService;
	}

	/**
	 * Sets the factory that provides a {@link Converter} used for type conversion of
	 * claim values for an {@link OidcUserInfo}. The default is {@link ClaimTypeConverter}
	 * for all {@link ClientRegistration clients}.
	 *
	 * @param claimTypeConverterFactory the factory that provides a {@link Converter} used
	 *                                  for type conversion of claim values for a specific {@link ClientRegistration
	 *                                  client}
	 * @since 5.2
	 */
	public final void setClaimTypeConverterFactory(
		Function<ClientRegistration, Converter<Map<String, Object>, Map<String, Object>>> claimTypeConverterFactory) {
		Assert.notNull(claimTypeConverterFactory, "claimTypeConverterFactory cannot be null");
		this.claimTypeConverterFactory = claimTypeConverterFactory;
	}

	/**
	 * Sets the scope(s) that allow access to the user info resource. The default is
	 * {@link OidcScopes#PROFILE profile}, {@link OidcScopes#EMAIL email},
	 * {@link OidcScopes#ADDRESS address} and {@link OidcScopes#PHONE phone}. The scope(s)
	 * are checked against the "granted" scope(s) associated to the
	 * {@link OidcUserRequest#getAccessToken() access token} to determine if the user info
	 * resource is accessible or not. If there is at least one match, the user info
	 * resource will be requested, otherwise it will not.
	 *
	 * @param accessibleScopes the scope(s) that allow access to the user info resource
	 * @since 5.2
	 */
	public final void setAccessibleScopes(Set<String> accessibleScopes) {
		Assert.notNull(accessibleScopes, "accessibleScopes cannot be null");
		this.accessibleScopes = accessibleScopes;
	}

	/**
	 * Grants a member access to the system based on their associated role
	 *
	 * @param dbUser The member object that represents the database user
	 * @return a BudgetUserDetails entity representing the user's access in the system
	 */
	private AppUserDetails grantMemberAccess(Member dbUser, OidcIdToken idToken, OidcUserInfo userInfo) {

		Set<GrantedAuthority> authorities  = new LinkedHashSet<>();
		AppUserDetails appUserDetails;

		if (dbUser != null) {
			authorities = createUserAuthority(dbUser);
			appUserDetails = new AppUserDetails(dbUser, idToken, userInfo, authorities);

			log.info("OpenIdConnectUserDetails -> userId:{}",  appUserDetails.getUmdUserId());
		} else {
			log.error("Error loading user in loadDatabaseUser. email: " + userInfo.getEmail());
			GrantedAuthority ga = new SimpleGrantedAuthority(Constants.ROLE_PUBLIC);
			authorities.add(ga);
			appUserDetails = unauthorizedUser(idToken, userInfo, authorities);
		}

		GrantedAuthority ga = new SimpleGrantedAuthority(dbUser.getRole().getName());
		authorities.add(ga);


		return appUserDetails;
	}

	private AppUserDetails unauthorizedUser(OidcIdToken idToken, OidcUserInfo userInfo, Set<GrantedAuthority> authorities) {
		Member member = new Member();
		member.setName(userInfo.getFullName());
		member.setEmail(userInfo.getEmail());

		return new AppUserDetails(member, idToken, userInfo, authorities);
	}

	/**
	 * Attempts to load a db user entity for a provided email
	 * A value of null is returned if the userInfo can not be loaded, either because the email is not found, the db user is inactive,
	 * the db user's role is null, or the assigned role has a name value of null
	 *
	 * @param email the email
	 * @return null if invalid, UserInfo object if found/valid
	 */
	private Member loadDatabaseUser(String email) {
		try {
			Member m = memberRepo.findByEmailAndInactive(email, false);
			if ((m == null) || (m.getEmail() == null) || (m.isInactive()) || (m.getRole() == null) || (m.getRole().getName() == null)) {
				log.warn("Did not find user in loadUserByUsername. failed to find email: " + email);
				return null;
			}

			return m;
		} catch (Exception e) {
			log.error("Error loading user in loadUserByUsername. email: " + email, e);
			return null;
		}
	}

	private void logWarn(String message) {
		log.warn(message);
	}

	private Set<GrantedAuthority> createUserAuthority(Member member) {
		GrantedAuthority ga = new SimpleGrantedAuthority(member.getRole().getName());
		Set<GrantedAuthority> authList = new LinkedHashSet<>();
		authList.add(ga);
		return authList;
	}

	/**
	 * Returns the default {@link Converter}'s used for type conversion of claim values
	 * for an {@link OidcUserInfo}.
	 *
	 * @return a {@link Map} of {@link Converter}'s keyed by {@link StandardClaimNames
	 * claim name}
	 * @since 5.2
	 */
	public static Map<String, Converter<Object, ?>> createDefaultClaimTypeConverters() {
		Converter<Object, ?> booleanConverter = getConverter(TypeDescriptor.valueOf(Boolean.class));
		Converter<Object, ?> instantConverter = getConverter(TypeDescriptor.valueOf(Instant.class));
		Map<String, Converter<Object, ?>> claimTypeConverters = new HashMap<>();
		claimTypeConverters.put(StandardClaimNames.EMAIL_VERIFIED, booleanConverter);
		claimTypeConverters.put(StandardClaimNames.UPDATED_AT, instantConverter);
		return claimTypeConverters;
	}

	private static Converter<Object, ?> getConverter(TypeDescriptor targetDescriptor) {
		TypeDescriptor sourceDescriptor = TypeDescriptor.valueOf(Object.class);
		return (source) -> ClaimConversionService.getSharedInstance().convert(source, sourceDescriptor, targetDescriptor);
	}

}
