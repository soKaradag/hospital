package com.hospital.hospital.auth.config;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import com.hospital.hospital.auth.model.Role;
import com.hospital.hospital.auth.model.RoleEntity;
import com.hospital.hospital.auth.model.User;
import com.hospital.hospital.auth.model.UserRole;
import com.hospital.hospital.auth.model.UserInfo;
import com.hospital.hospital.auth.repository.RoleEntityRepository;
import com.hospital.hospital.auth.repository.UserInfoRepository;
import com.hospital.hospital.auth.repository.UserRepository;
import com.hospital.hospital.auth.repository.UserRoleRepository;
import com.hospital.hospital.auth.service.PasswordHashService;
import com.hospital.hospital.common.model.Contact;
import com.hospital.hospital.common.model.Phone;

@Configuration
public class AdminSeedConfig {

	private static final Logger log = LoggerFactory.getLogger(AdminSeedConfig.class);

	@Bean
	CommandLineRunner adminSeedRunner(
			DataSource dataSource,
			UserRepository userRepository,
			UserInfoRepository userInfoRepository,
			RoleEntityRepository roleEntityRepository,
			UserRoleRepository userRoleRepository,
			PasswordHashService passwordHashService,
			@Value("${app.seed.admin.enabled:true}") boolean seedEnabled,
			@Value("${app.seed.admin.username:admin}") String username,
			@Value("${app.seed.admin.password:admin123}") String password,
			@Value("${app.seed.admin.first-name:System}") String firstName,
			@Value("${app.seed.admin.last-name:Admin}") String lastName,
			@Value("${app.seed.admin.email:admin@hospital.local}") String email,
			@Value("${app.seed.admin.phone-country-code:90}") String phoneCountryCode,
			@Value("${app.seed.admin.phone-number:5550000000}") String phoneNumber) {
		return args -> seedAdminIfNeeded(
				dataSource,
				userRepository,
				userInfoRepository,
				roleEntityRepository,
				userRoleRepository,
				passwordHashService,
				seedEnabled,
				username,
				password,
				firstName,
				lastName,
				email,
				phoneCountryCode,
				phoneNumber);
	}

	@Transactional
	void seedAdminIfNeeded(
			DataSource dataSource,
			UserRepository userRepository,
			UserInfoRepository userInfoRepository,
			RoleEntityRepository roleEntityRepository,
			UserRoleRepository userRoleRepository,
			PasswordHashService passwordHashService,
			boolean seedEnabled,
			String username,
			String password,
			String firstName,
			String lastName,
			String email,
			String phoneCountryCode,
			String phoneNumber) {
		if (!seedEnabled) {
			log.info("Admin seed is disabled.");
			return;
		}

		if (!hasTable(dataSource, "users") || !hasTable(dataSource, "user_info")) {
			log.info("Admin seed skipped. Auth tables are not available yet.");
			return;
		}

		User existingUser = null;
		try {
			existingUser = userRepository.findByUsername(username).orElse(null);
		} catch (InvalidDataAccessResourceUsageException exception) {
			log.info("Admin seed skipped. Auth tables are not queryable yet.");
			return;
		}

		if (existingUser != null) {
			assignAdminRoleIfNeeded(dataSource, roleEntityRepository, userRoleRepository, existingUser);
			log.info("Admin seed skipped. User '{}' already exists.", username);
			return;
		}

		User user = new User(username, passwordHashService.hash(password), Role.ADMIN);
		User savedUser = userRepository.save(user);

		Contact contact = new Contact(new Phone(phoneCountryCode, phoneNumber), email);
		UserInfo userInfo = new UserInfo(savedUser, firstName, lastName, contact);
		userInfoRepository.save(userInfo);
		assignAdminRoleIfNeeded(dataSource, roleEntityRepository, userRoleRepository, savedUser);

		log.info("Default admin user created. username='{}'", username);
	}

	private void assignAdminRoleIfNeeded(
			DataSource dataSource,
			RoleEntityRepository roleEntityRepository,
			UserRoleRepository userRoleRepository,
			User user) {
		if (!hasTable(dataSource, "roles") || !hasTable(dataSource, "user_roles")) {
			return;
		}

		roleEntityRepository.findByCode(Role.ADMIN.name()).ifPresent(adminRole -> saveUserRoleIfMissing(userRoleRepository, user, adminRole));
	}

	private void saveUserRoleIfMissing(UserRoleRepository userRoleRepository, User user, RoleEntity role) {
		if (userRoleRepository.existsByUserAndRole(user, role)) {
			return;
		}

		userRoleRepository.save(new UserRole(user, role, true));
	}

	private boolean hasTable(DataSource dataSource, String tableName) {
		try (Connection connection = dataSource.getConnection()) {
			DatabaseMetaData metaData = connection.getMetaData();
			return tableExists(metaData, tableName)
					|| tableExists(metaData, tableName.toUpperCase())
					|| tableExists(metaData, tableName.toLowerCase());
		}
		catch (Exception exception) {
			log.warn("Admin seed table check failed for '{}': {}", tableName, exception.getMessage());
			return false;
		}
	}

	private boolean tableExists(DatabaseMetaData metaData, String tableName) throws Exception {
		try (ResultSet resultSet = metaData.getTables(null, null, tableName, null)) {
			return resultSet.next();
		}
	}
}
