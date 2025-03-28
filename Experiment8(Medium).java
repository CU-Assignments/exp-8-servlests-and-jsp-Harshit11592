// Crating Database to Store the Data
CREATE DATABASE companyDB;

USE companyDB;

CREATE TABLE employees (
    id INT PRIMARY KEY,
    name VARCHAR(50),
    department VARCHAR(50),
    salary DECIMAL(10, 2)
);

INSERT INTO employees (id, name, department, salary) VALUES
(1, 'Harshit Mishra', 'HR', 60000.00),
(2, 'Aniket', 'Finance', 75000.00),
(3, 'Akshita Sharma', 'IT', 85000.00),
(4, 'Arpit Mahajan', 'Marketing', 70000.00);

//Java Servlet (EmployeeServlet.java)
import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

// Servlet mapped to /EmployeeServlet
@WebServlet("/EmployeeServlet")
public class EmployeeServlet extends HttpServlet {
    // Database connection details
    private static final String DB_URL = "jdbc:mysql://localhost:3306/companyDB";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "password";

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String empId = request.getParameter("empId");
        String action = request.getParameter("action");

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        if (action != null && action.equals("list")) {
            displayAllEmployees(out);
        } else if (empId != null && !empId.isEmpty()) {
            displayEmployeeById(empId, out);
        } else {
            out.println("<html><body><h3 style='color:red;'>Invalid request!</h3></body></html>");
        }
    }

    // Display all employees
    private void displayAllEmployees(PrintWriter out) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT * FROM employees";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            out.println("<html><head><title>Employee List</title></head><body>");
            out.println("<h2>All Employees</h2>");
            out.println("<table>");
            out.println("<tr><th>ID</th><th>Name</th><th>Department</th><th>Salary</th></tr>");

            while (rs.next()) {
                out.println("<tr>");
                out.println("<td>" + rs.getInt("id") + "</td>");
                out.println("<td>" + rs.getString("name") + "</td>");
                out.println("<td>" + rs.getString("department") + "</td>");
                out.println("<td>$" + rs.getBigDecimal("salary") + "</td>");
                out.println("</tr>");
            }
            out.println("</table>");
            out.println("<br><a href='index.html'>Go Back</a>");
            out.println("</body></html>");

        } catch (Exception e) {
            out.println("<h3 style='color:red;'>Error fetching employee data!</h3>");
            e.printStackTrace(out);
        }
    }

    // Display employee by ID
    private void displayEmployeeById(String empId, PrintWriter out) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT * FROM employees WHERE id=?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, Integer.parseInt(empId));

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                out.println("<html><head><title>Employee Details</title></head><body>");
                out.println("<h2>Employee Details</h2>");
                out.println("<p><strong>ID:</strong> " + rs.getInt("id") + "</p>");
                out.println("<p><strong>Name:</strong> " + rs.getString("name") + "</p>");
                out.println("<p><strong>Department:</strong> " + rs.getString("department") + "</p>");
                out.println("<p><strong>Salary:</strong> $" + rs.getBigDecimal("salary") + "</p>");
                out.println("<br><a href='index.html'>Go Back</a>");
                out.println("</body></html>");
            } else {
                out.println("<html><body><h3 style='color:red;'>No employee found with ID: " + empId + "</h3>");
                out.println("<br><a href='index.html'>Go Back</a></body></html>");
            }

        } catch (Exception e) {
            out.println("<h3 style='color:red;'>Error processing request!</h3>");
            e.printStackTrace(out);
        }
    }
}

