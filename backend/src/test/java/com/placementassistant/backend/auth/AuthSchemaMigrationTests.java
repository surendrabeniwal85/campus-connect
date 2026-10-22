package com.placementassistant.backend.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;

class AuthSchemaMigrationTests {

	@Test
	void createsMappedTablesConstraintsAndCanonicalRoles() throws SQLException {
		try (Connection connection = DriverManager.getConnection(
				"jdbc:h2:mem:auth_schema_migration;MODE=MySQL;DB_CLOSE_DELAY=-1",
				"sa",
				"")) {
			ScriptUtils.executeSqlScript(
					connection,
					new ClassPathResource("db/migration/V43__create_auth_tables.sql"));

			assertThat(connection.getMetaData().getTables(null, null, "APP_ROLE", null).next()).isTrue();
			assertThat(connection.getMetaData().getTables(null, null, "APP_USER", null).next()).isTrue();
			assertThat(connection.getMetaData().getTables(null, null, "USER_ROLE", null).next()).isTrue();

			try (Statement statement = connection.createStatement();
					var roles = statement.executeQuery("SELECT name FROM app_role ORDER BY name")) {
				assertThat(readRoleNames(roles)).containsExactly(
						"ROLE_DIRECTOR_HR",
						"ROLE_D_TPO",
						"ROLE_HOD",
						"ROLE_PLACEMENT_COORDINATOR",
						"ROLE_STUDENT",
						"ROLE_TEACHER",
						"ROLE_TPO");
			}

			try (Statement statement = connection.createStatement()) {
				statement.executeUpdate("""
						INSERT INTO app_user (display_name, email, password_hash)
						VALUES ('Test Student', 'student@example.test', 'test-hash')
						""");
				statement.executeUpdate("""
						INSERT INTO user_role (user_id, role_id)
						SELECT u.id, r.id
						FROM app_user u CROSS JOIN app_role r
						WHERE u.email = 'student@example.test' AND r.name = 'ROLE_STUDENT'
						""");
				try (var associations = statement.executeQuery("SELECT COUNT(*) FROM user_role")) {
					assertThat(associations.next()).isTrue();
					assertThat(associations.getInt(1)).isEqualTo(1);
				}
				assertThatThrownBy(() -> statement.executeUpdate("""
						INSERT INTO app_user (display_name, email, password_hash)
						VALUES ('Duplicate Student', 'student@example.test', 'another-hash')
						""")).isInstanceOf(SQLException.class);
				assertThatThrownBy(() -> statement.executeUpdate("""
						INSERT INTO app_role (name) VALUES ('ROLE_STUDENT')
						""")).isInstanceOf(SQLException.class);
				assertThatThrownBy(() -> statement.executeUpdate("""
						INSERT INTO user_role (user_id, role_id)
						VALUES (-1, (SELECT id FROM app_role WHERE name = 'ROLE_STUDENT'))
						""")).isInstanceOf(SQLException.class);
				assertThatThrownBy(() -> statement.executeUpdate("""
						INSERT INTO user_role (user_id, role_id)
						SELECT u.id, r.id
						FROM app_user u CROSS JOIN app_role r
						WHERE u.email = 'student@example.test' AND r.name = 'ROLE_STUDENT'
						""")).isInstanceOf(SQLException.class);
			}

			assertThat(connection.getMetaData().getPrimaryKeys(null, null, "USER_ROLE").next()).isTrue();
			assertThat(connection.getMetaData().getImportedKeys(null, null, "USER_ROLE").next()).isTrue();
		}
	}

	private static List<String> readRoleNames(java.sql.ResultSet roles) throws SQLException {
		var names = new java.util.ArrayList<String>();
		while (roles.next()) {
			names.add(roles.getString("name"));
		}
		return names;
	}
}
