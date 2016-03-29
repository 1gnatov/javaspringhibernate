import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;

public class PersistenceConfig {

//        @Autowired
//        private Environment env;

        /**
         * The @Bean annotation is used to declare a Spring bean and the DI requirements. The @Bean annotation is equivalent to
         *  the <bean> tag, the method name is equivalent to the id attribute within the <bean> tag.
         *
         * <bean id="mySqlDataSource" class="org.apache.commons.dbcp2.BasicDataSource" destroy-method="close"
         p:driverClassName="${jdbc.mysql.driverClassName}"
         p:url="${jdbc.mysql.url}"
         p:username="${jdbc.mysql.username}"
         p:password="${jdbc.mysql.password}" />
         *
         * @return
         */
        @Bean
        public DataSource H2DataSource() {

            EmbeddedDatabase dataSource = new EmbeddedDatabaseBuilder()
                    .setType(EmbeddedDatabaseType.H2)
                    .addScript("create-tables.sql")
                    .build();


            return dataSource;
        }

        @Bean
        public SessionFactory createSessionFactory(DataSource dataSource) {

            return HibernateConfigFactory.prod().buildSessionFactory(new StandardServiceRegistryBuilder()
                    .applySetting(org.hibernate.cfg.Environment.DATASOURCE, dataSource)
                    .build());
        }

        /*
        @Bean(destroyMethod = "close")
        public DataSource ls360DataSource() {
            BasicDataSource dataSource = new BasicDataSource();
            dataSource.setDriverClassName(env.getProperty("jdbc.ls360.driverClassName"));
            dataSource.setUrl(env.getProperty("jdbc.ls360.url"));
            dataSource.setUsername(env.getProperty("jdbc.ls360.username"));
            dataSource.setPassword(env.getProperty("jdbc.ls360.password"));
            return dataSource;
        }
        */
}

