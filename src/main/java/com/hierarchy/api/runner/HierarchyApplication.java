package com.hierarchy.api.runner;

import com.hierarchy.api.config.GuiceConfig;
import com.hierarchy.api.controller.UserController;
import com.hierarchy.api.entity.User;
import com.hierarchy.api.resources.UserResource;
import io.dropwizard.Application;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.setup.Environment;
import org.eclipse.jetty.servlets.CrossOriginFilter;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import java.util.EnumSet;

public class HierarchyApplication extends Application<GuiceConfig> {

    public static void main(String[] args) throws Exception {
        new HierarchyApplication().run(args);
    }

   private HibernateBundle<GuiceConfig> hibernate = new HibernateBundle<GuiceConfig>(User.class) {
        @Override
        public DataSourceFactory getDataSourceFactory(GuiceConfig configuration) {
            return configuration.getDatabaseAppDataSourceFactory();
        }
    };

    public void run(GuiceConfig guiceConfig, Environment environment) throws Exception {
        final FilterRegistration.Dynamic cors =
                environment.servlets().addFilter("CORS", CrossOriginFilter.class);

        // Configure CORS parameters
        cors.setInitParameter("allowedOrigins", "*");
        cors.setInitParameter("allowedHeaders", "X-Requested-With,Content-Type,Accept,Origin");
        cors.setInitParameter("allowedMethods", "OPTIONS,GET,PUT,POST,DELETE,HEAD");

        // Add URL mapping
        cors.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");

        hibernate.run(guiceConfig, environment);

        final UserController personDAO = new UserController(hibernate.getSessionFactory());

        final UserResource personResource = new UserResource(personDAO);

        environment.jersey().register(personResource);

    }
}
