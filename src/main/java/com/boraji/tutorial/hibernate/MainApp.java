package com.boraji.tutorial.hibernate;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.hibernate.search.query.dsl.QueryBuilder;

import com.boraji.tutorial.hibernate.entity.Department;
import com.boraji.tutorial.hibernate.entity.Employee;

public class MainApp {

	public static void main(String[] args) {

		//Insert some records into department and employee tables
		insertData();
		
		//Search department names
		List<Department> departments2=searchData("Department");
		for (Department department : departments2) {
			System.out.println("Department Name:-"+department.getName());
			for (Employee employee : department.getEmployees()) {
				System.out.println("\tEmployee Name:- "+employee.getName());
			}
		}
		
		HibernateUtil.shutdown();
	}

	private static void insertData() {
		Session session = null;
	      Transaction transaction = null;
	      try {
	         session = HibernateUtil.getSessionFactory().openSession();
	         transaction = session.getTransaction();
	         transaction.begin();
	         
	         Department department1 = new Department();
	         department1.setName("IT Department");
	         Department department2 = new Department();
	         department2.setName("HR Department");
	         
	         Employee employee1 = new Employee();
	         employee1.setName("Robin Edward");
	         employee1.setDesignation("Manager");
	         employee1.setDepartment(department1);

	         Employee employee2 = new Employee();
	         employee2.setName("Vivian Jackman");
	         employee2.setDesignation("Senior HR Manager");
	         employee2.setDepartment(department1);

	         Employee employee3 = new Employee();
	         employee3.setName("Eliza Edward");
	         employee3.setDesignation("Software Engineer");
	         employee3.setDepartment(department2);
	         
	         Employee employee4 = new Employee();
	         employee4.setName("Nancy Newman");
	         employee4.setDesignation("Senior Software Engineer");
	         employee4.setDepartment(department2);

	         department1.getEmployees().add(employee1);
	         department1.getEmployees().add(employee2);
	         
	         department2.getEmployees().add(employee3);
	         department2.getEmployees().add(employee4);

	         session.persist(department1);
	         session.persist(department2);

	         transaction.commit();
	      } catch (Exception e) {
	         if (transaction != null) {
	            transaction.rollback();
	         }
	         e.printStackTrace();
	      } finally {
	         if (session != null) {
	            session.close();
	         }
	      }
	}

	private static List<Department> searchData(String text) {
		List<Department> departments=null;
		Session session = null;
		Transaction transaction = null;
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			transaction = session.getTransaction();
			transaction.begin();

			FullTextSession fullTextSession = Search.getFullTextSession(session);
			fullTextSession.createIndexer().startAndWait();
			
			QueryBuilder qb = fullTextSession.getSearchFactory().buildQueryBuilder()
					.forEntity(Department.class).get();
			
			//Create lucene  query
			// Set indexed field
			org.apache.lucene.search.Query lucenceQuery=qb.keyword()
					.onFields("name","employees.name").matching(text).createQuery();
			
			//Warp lucene query in org.hibernate.query.Query
			@SuppressWarnings("unchecked")
			Query<Department> query=fullTextSession.createFullTextQuery(lucenceQuery,
					Department.class);
			departments=query.getResultList();
			
			transaction.commit();
		} catch (Exception e) {
			e.printStackTrace();
			if (transaction != null) {
				transaction.rollback();
			}
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return departments;

	}
}
