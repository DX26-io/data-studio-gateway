package com.flair.bi.security;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.flair.bi.domain.User;
import com.flair.bi.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Authenticate a user from the database.
 */
@Component("userDetailsService")
@Slf4j
@RequiredArgsConstructor
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

	private final UserRepository userRepository;

	@Override
	@Transactional
	public UserDetails loadUserByUsername(final String login) {
		log.debug("Authenticating {}", login);
		String lowercaseLogin = login.toLowerCase(Locale.ENGLISH);
		Optional<User> userFromDatabase = userRepository.findOneByLogin(lowercaseLogin);
		return userFromDatabase.map(user -> {
			if (!user.isActivated()) {
				throw new UserNotActivatedException("User " + lowercaseLogin + " was not activated");
			}
			final List<GrantedAuthority> grantedAuthorities = user.retrieveAllUserPermissions(false).stream()
					.map(PermissionGrantedAuthority::new).collect(Collectors.toList());

			return new org.springframework.security.core.userdetails.User(lowercaseLogin, user.getPassword(),
					grantedAuthorities);
		}).orElseThrow(
				() -> new UsernameNotFoundException("User " + lowercaseLogin + " was not found in the " + "database"));
	}
}
