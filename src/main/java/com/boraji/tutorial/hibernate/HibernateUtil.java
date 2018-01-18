package com.boraji.tutorial.hibernate;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Environment;

import com.boraji.tutorial.hibernate.entity.Department;
import com.boraji.tutorial.hibernate.entity.Employee;

public class HibernateUtil {

   private static StandardServiceRegistry registry;
   private static SessionFactory sessionFactory;

   public static SessionFactory getSessionFactory() {
      if (sessionFactory == null) {
         try {
            StandardServiceRegistryBuilder registryBuilder = 
                  new StandardServiceRegistryBuilder();

            Map<String, Object> settings = new HashMap<>();
            settings.put(Environment.DRIVER, "com.mysql.cj.jdbc.Driver");
            settings.put(Environment.URL, "jdbc:mysql://localhost:3306/BORAJI?useSSL=false");
            settings.put(Environment.USER, "root");
            settings.put(Environment.PASS, "qaz123wsx");
            settings.put(Environment.HBM2DDL_AUTO, "update");
            settings.put(Environment.SHOW_SQL, true);

            //Hibernate search properties
            settings.put("hibernate.search.default.directory_provider", "filesystem");
            settings.put("hibernate.search.default.indexBase", "C:/hibernate/lucence/indexes");
            settings.put("hibernate.search.default.indexwriter.infostream", true);
            
            registryBuilder.applySettings(settings);

            registry = registryBuilder.build();
            MetadataSources sources = new MetadataSources(registry)
            		.addAnnotatedClass(Employee.class)
            		.addAnnotatedClass(Department.class);
            
            
            Metadata metadata = sources.getMetadataBuilder().build();
            sessionFactory = metadata.getSessionFactoryBuilder().build();
         } catch (Exception e) {
            if (registry != null) {
               StandardServiceRegistryBuilder.destroy(registry);
            }
            e.printStackTrace();
         }
      }
      return sessionFactory;
   }

   public static void shutdown() {
      if (registry != null) {
         StandardServiceRegistryBuilder.destroy(registry);
      }
   }
}
