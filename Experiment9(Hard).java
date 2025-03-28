//Database Setup
CREATE DATABASE studentDB;

USE studentDB;

CREATE TABLE attendance (
    id INT AUTO_INCREMENT PRIMARY KEY,
    student_name VARCHAR(100) NOT NULL,
    course_name VARCHAR(100) NOT NULL,
    attendance_date DATE NOT NULL,
    status VARCHAR(10) NOT NULL CHECK (status IN ('Present', 'Absent'))
);

INSERT INTO attendance (student_name, course_name, attendance_date, status)
VALUES
('Harshit Mishra', 'Mathematics', '2024-01-20', 'Present'),
('Akshita Sharma', 'Science', '2024-01-21', 'Absent');

//Java Servlet (AttendanceServlet.java)
import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
@WebServlet("/AttendanceServlet")
public class AttendanceServlet extends HttpServlet {
    // Database connection details
    private static final String DB_URL = "jdbc:mysql://localhost:3306/studentDB";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "password";

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Retrieve form data
        String studentName = request.getParameter("studentName");
        String courseName = request.getParameter("courseName");
        String attendanceDate = request.getParameter("attendanceDate");
        String status = request.getParameter("status");

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        // Save attendance data to the database
        if (insertAttendance(studentName, courseName, attendanceDate, status)) {
            out.println("<html><head><title>Success</title></head><body>");
            out.println("<h2 style='color:green;'>Attendance recorded successfully!</h2>");
            out.println("<a href='attendance.jsp'>Add More Attendance</a><br>");
            out.println("<a href='viewAttendance.jsp'>View Attendance</a>");
            out.println("</body></html>");
        } else {
            out.println("<html><head><title>Error</title></head><body>");
            out.println("<h2 style='color:red;'>Error recording attendance. Please try again.</h2>");
            out.println("<a href='attendance.jsp'>Go Back</a>");
            out.println("</body></html>");
        }
    }

    // Insert attendance record into the database
    private boolean insertAttendance(String studentName, String courseName, String attendanceDate, String status) {
        boolean success = false;
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "INSERT INTO attendance (student_name, course_name, attendance_date, status) VALUES (?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, studentName);
            pstmt.setString(2, courseName);
            pstmt.setDate(3, Date.valueOf(attendanceDate));
            pstmt.setString(4, status);

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                success = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return success;
    }
}

