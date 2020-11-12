/*
 * Copyright 2012-2018 the original author or authors.
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

package org.lamisplus.modules.base.tenant.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceInitializationMode;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.nio.charset.Charset;
import java.util.List;

/**
 * Base class for configuration of a data source.
 *
 * @author Dave Syer
 * @author Maciej Walkowiak
 * @author Stephane Nicoll
 * @author Benedikt Ritter
 * @author Eddú Meléndez
 * @since 1.1.0
 */
@ConfigurationProperties(prefix = "spring.datasource")
@Configuration
@Data
public class DatabaseProperties {

    /**
     * Name of the datasource. Default to "testdb" when using an embedded database.
     */
    private String name;

    /**
     * Whether to generate a random datasource name.
     */
    private boolean generateUniqueName;

    /**
     * Fully qualified name of the connection pool implementation to use. By default, it
     * is auto-detected from the classpath.
     */
    private Class<? extends DataSource> type;

    /**
     * Fully qualified name of the JDBC driver. Auto-detected based on the URL by default.
     */
    private String driverClassName;

    /**
     * JDBC URL of the database.
     */
    private String url;

    /**
     * Login username of the database.
     */
    private String username;

    /**
     * Login password of the database.
     */
    private String password;

    /**
     * JNDI location of the datasource. Class, url, username & password are ignored when
     * set.
     */
    private String jndiName;

    /**
     * Initialize the datasource with available DDL and DML scripts.
     */
    private DataSourceInitializationMode initializationMode = DataSourceInitializationMode.EMBEDDED;

    /**
     * Platform to use in the DDL or DML scripts (such as schema-${platform}.sql or
     * data-${platform}.sql).
     */
    private String platform = "all";

    /**
     * Schema (DDL) script resource references.
     */
    private List<String> schema;

    /**
     * Username of the database to execute DDL scripts (if different).
     */
    private String schemaUsername;

    /**
     * Password of the database to execute DDL scripts (if different).
     */
    private String schemaPassword;

    /**
     * Data (DML) script resource references.
     */
    private List<String> data;

    /**
     * Username of the database to execute DML scripts (if different).
     */
    private String dataUsername;

    /**
     * Password of the database to execute DML scripts (if different).
     */
    private String dataPassword;

    /**
     * Whether to stop if an error occurs while initializing the database.
     */
    private boolean continueOnError = false;

    /**
     * Statement separator in SQL initialization scripts.
     */
    private String separator = ";";

    /**
     * SQL scripts encoding.
     */
    private Charset sqlScriptEncoding;

    private String uniqueName;
}
