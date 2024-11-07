package com.gracaconsultores.ReporteGeneralAfiliaciones.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import criptografia.TripleDes;
import graca.bancaenlinea.empresa.error.exception.TechnicalException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Configuration
@Component
@Slf4j
public class HikariConfigs{

    @Autowired
    private Environment env;


    @Primary
    @Bean(name = "dsBusiness")
    public HikariDataSource dataSource() throws Exception {
        try {
            log.debug("--- > DataSource datasource pcp :  {}" , env.getProperty("spring.datasource.url"));
            log.debug("--- > user   pcp :  {}" , env.getProperty("spring.datasource.username"));
            log.debug("--- > passwd pcp :  {}" , env.getProperty("spring.datasource.passwordE"));
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(env.getProperty("spring.datasource.url"));                  //Direccion para Local
//            config.setJdbcUrl(env.getProperty("spring.datasource.url2"));                 //Direccion para QA
            log.info("Valor de la URL del JDBC: {}", config.getJdbcUrl());
            config.setUsername(env.getProperty("spring.datasource.username"));
            config.setPassword(env.getProperty("spring.datasource.password"));
            //config.setPassword(this.getPasswordDecrypt(env.getProperty("spring.datasource.passwordE"), "Custodio"));
            config.setLeakDetectionThreshold(60 * 300000);
            config.addDataSourceProperty("cachePrepStmts", true);
            config.addDataSourceProperty("prepStmtCacheSize", 250);
            config.addDataSourceProperty("prepStmtCacheSqlLimit", 2048);
            config.addDataSourceProperty("useServerPrepStmts", true);
            config.addDataSourceProperty("cacheResultSetMetadata", true);
            config.addDataSourceProperty("autoReconnect", true);
            config.addDataSourceProperty("maxReconnects", 5);
            config.addDataSourceProperty("tcpKeepAlive", true);
            config.setMaximumPoolSize(40);
            config.setMinimumIdle(40);
            config.setIdleTimeout(500000);
            config.setMaxLifetime(1023000);
            config.setConnectionInitSql("select 1 from dual");

            return new HikariDataSource(config);
        } catch (Exception e) {
            log.error("Exception HikariConfig : " + e);
            throw new TechnicalException(e, e.getMessage());
        }
    }


    @Bean(name = "dsAudi")
    public HikariDataSource createDsAudi() throws Exception {
        try {
            log.debug("--- > DataSource datasource PcpAudi :  {}" , env.getProperty("audi.spring.datasource.url"));
            log.debug("--- > user   pcp :  {}" , env.getProperty("audi.spring.datasource.username"));
            log.debug("--- > passwd pcp :  {}" , env.getProperty("audi.spring.datasource.passwordE"));
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(env.getProperty("audi.spring.datasource.url"));
            config.setUsername(env.getProperty("audi.spring.datasource.username"));
            config.setPassword(env.getProperty("spring.datasource.password"));
            //config.setPassword(this.getPasswordDecrypt(env.getProperty("audi.spring.datasource.passwordE"), "Custodio"));
            config.setLeakDetectionThreshold(60 * 300000);
            config.addDataSourceProperty("cachePrepStmts", true);
            config.addDataSourceProperty("prepStmtCacheSize", 250);
            config.addDataSourceProperty("prepStmtCacheSqlLimit", 2048);
            config.addDataSourceProperty("useServerPrepStmts", true);
            config.addDataSourceProperty("cacheResultSetMetadata", true);
            config.addDataSourceProperty("autoReconnect", true);
            config.addDataSourceProperty("maxReconnects", 5);
            config.addDataSourceProperty("tcpKeepAlive", true);
            config.setMaximumPoolSize(15);
            config.setMinimumIdle(15);
            config.setIdleTimeout(500000);
            config.setMaxLifetime(1023000);
            config.setConnectionInitSql("select 1 from dual");

            return new HikariDataSource(config);
        } catch (Exception e) {
            log.error("Exception HikariConfig : " + e);
            throw new TechnicalException(e, e.getMessage());
        }
    }


    @Bean(name = "jdbcTemplateBusiness")
    @Autowired
    public JdbcTemplate createJdbcTemplateBusiness(@Qualifier("dsBusiness") HikariDataSource dsIn) {
        try {
            if (dsIn.getConnection() != null) {
                log.info("---->DS PCP CONECTADO");
            } else {
                log.info("-->DS PCP NO PUEDE CONECTAR");

            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("*****error con el ds dsBusiness: " + e.getMessage());
        }
        return new JdbcTemplate(dsIn);
    }

    @Bean(name = "jdbcTemplateAudi")
    @Autowired
    public JdbcTemplate createJdbcTemplateAudi(@Qualifier("dsAudi") HikariDataSource dsIn) {
        try {
            if (dsIn.getConnection() != null) {
                log.info("---->DS AUDI CONECTADO");
            } else {
                log.info("-->DS AUDI NO PUEDE CONECTAR");
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("*****error con el ds dsAudi: " + e.getMessage());
        }
        return new JdbcTemplate(dsIn);
    }

    public String getPasswordDecrypt(String encryptPassword, String groupKeys) {
        if (encryptPassword != null) {
            // Instanciar el encriptador
            log.info("Desencriptando ... " + encryptPassword);
            TripleDes tdes = new TripleDes();
            log.info("Grupo de Keys : " + "keys/" + groupKeys + "1/cl.xml");
            try {
                //logger.info("password : " + tdes.descifrar("keys/" + groupKeys + "1/cl.xml", "keys/" + groupKeys + "2/cl.xml", encryptPassword));
                return tdes.descifrar("keys/" + groupKeys + "1/cl.xml", "keys/" + groupKeys + "2/cl.xml", encryptPassword);
            } catch (Exception e) {
                log.info("Error leyendo las llaves  " + e.getMessage());
            }
            return null;
        } else {
            log.info("password solicitado a desencriptar es nulo ");
            return null;
        }
    }
}
