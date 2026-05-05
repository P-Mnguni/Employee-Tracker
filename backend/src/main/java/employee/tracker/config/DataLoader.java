package employee.tracker.config;

import employee.tracker.model.*;
import employee.tracker.repository.EmployeeRepository;
import employee.tracker.repository.TimeEntryRepository;
import employee.tracker.repository.TimesheetRepository;
import employee.tracker.repository.PTORequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * DataLoader - Loads mock data into the database on application startup
 * This creates comprehensive test data for all controllers:
 * - TimeEntryController: clock-in/out entries, active sessions
 * - TimesheetController: timesheets in various states (DRAFT, PENDING, APPROVED, REJECTED)
 * - PTOController: leave requests in various states (PENDING, APPROVED, REJECTED)
 */
@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private TimeEntryRepository timeEntryRepository;
    
    @Autowired
    private TimesheetRepository timesheetRepository;
    
    @Autowired
    private PTORequestRepository ptoRequestRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // Only load data if database is empty
        if (employeeRepository.count() == 0) {
            loadMockData();
            System.out.println("✅ Mock data loaded successfully!");
            printTestDataSummary();
        } else {
            System.out.println("📊 Database already contains data. Skipping mock data load.");
            printTestDataSummary();
        }
    }

    private void loadMockData() {
        // ===== 1. Create Employees =====
        Employee admin = new Employee("John Admin", "admin@employeetracker.com", "IT", Role.ADMIN);
        Employee managerSarah = new Employee("Sarah Manager", "sarah.manager@employeetracker.com", "Engineering", Role.MANAGER);
        Employee managerMike = new Employee("Mike Manager", "mike.manager@employeetracker.com", "Sales", Role.MANAGER);
        Employee empJohn = new Employee("John Doe", "john.doe@employeetracker.com", "Engineering", Role.EMPLOYEE);
        Employee empJane = new Employee("Jane Smith", "jane.smith@employeetracker.com", "Engineering", Role.EMPLOYEE);
        Employee empBob = new Employee("Bob Johnson", "bob.johnson@employeetracker.com", "Sales", Role.EMPLOYEE);
        Employee empAlice = new Employee("Alice Brown", "alice.brown@employeetracker.com", "Sales", Role.EMPLOYEE);
        Employee empCharlie = new Employee("Charlie Wilson", "charlie.wilson@employeetracker.com", "Marketing", Role.EMPLOYEE);
        
        employeeRepository.save(admin);
        employeeRepository.save(managerSarah);
        employeeRepository.save(managerMike);
        employeeRepository.save(empJohn);
        employeeRepository.save(empJane);
        employeeRepository.save(empBob);
        employeeRepository.save(empAlice);
        employeeRepository.save(empCharlie);
        
        System.out.println("📝 Created " + employeeRepository.count() + " employees");

        // ===== 2. Create Time Entries for TimeEntryController Testing =====
        LocalDate today = LocalDate.now();
        
        // John Doe - Active session (clocked in today)
        createTimeEntry(empJohn, today, LocalTime.of(9, 0), null, TimeEntryStatus.PENDING);
        createTimeEntry(empJohn, today.minusDays(1), LocalTime.of(9, 0), LocalTime.of(17, 0), TimeEntryStatus.APPROVED);
        createTimeEntry(empJohn, today.minusDays(2), LocalTime.of(8, 45), LocalTime.of(17, 15), TimeEntryStatus.APPROVED);
        createTimeEntry(empJohn, today.minusDays(3), LocalTime.of(9, 15), LocalTime.of(18, 0), TimeEntryStatus.PENDING);
        
        // Jane Smith - Not clocked in today
        createTimeEntry(empJane, today.minusDays(1), LocalTime.of(8, 30), LocalTime.of(16, 30), TimeEntryStatus.APPROVED);
        createTimeEntry(empJane, today.minusDays(2), LocalTime.of(9, 0), LocalTime.of(17, 0), TimeEntryStatus.APPROVED);
        createTimeEntry(empJane, today.minusDays(3), LocalTime.of(8, 45), LocalTime.of(17, 30), TimeEntryStatus.PENDING);
        
        // Bob Johnson - Active session
        createTimeEntry(empBob, today, LocalTime.of(10, 0), null, TimeEntryStatus.PENDING);
        createTimeEntry(empBob, today.minusDays(1), LocalTime.of(9, 30), LocalTime.of(17, 30), TimeEntryStatus.APPROVED);
        
        // Alice Brown - Multiple entries
        createTimeEntry(empAlice, today.minusDays(1), LocalTime.of(8, 0), LocalTime.of(16, 0), TimeEntryStatus.APPROVED);
        createTimeEntry(empAlice, today.minusDays(2), LocalTime.of(8, 30), LocalTime.of(16, 30), TimeEntryStatus.APPROVED);
        
        // Charlie Wilson
        createTimeEntry(empCharlie, today.minusDays(1), LocalTime.of(9, 0), LocalTime.of(17, 0), TimeEntryStatus.PENDING);
        
        // Sarah Manager - Active session
        createTimeEntry(managerSarah, today, LocalTime.of(8, 15), null, TimeEntryStatus.PENDING);
        
        System.out.println("⏰ Created " + timeEntryRepository.count() + " time entries");

        // ===== 3. Create Timesheets for TimesheetController Testing =====
        LocalDate lastWeek = today.minusDays(7);
        LocalDate twoWeeksAgo = today.minusDays(14);
        LocalDate threeWeeksAgo = today.minusDays(21);
        
        // John Doe's timesheets (Engineering)
        createTimesheet(empJohn.getId(), threeWeeksAgo, threeWeeksAgo.plusDays(6), TimesheetStatus.APPROVED, managerSarah.getId());
        createTimesheet(empJohn.getId(), twoWeeksAgo, twoWeeksAgo.plusDays(6), TimesheetStatus.APPROVED, managerSarah.getId());
        createTimesheet(empJohn.getId(), lastWeek, lastWeek.plusDays(6), TimesheetStatus.PENDING, null);
        
        // Jane Smith's timesheets (Engineering)
        createTimesheet(empJane.getId(), threeWeeksAgo, threeWeeksAgo.plusDays(6), TimesheetStatus.APPROVED, managerSarah.getId());
        createTimesheet(empJane.getId(), twoWeeksAgo, twoWeeksAgo.plusDays(6), TimesheetStatus.REJECTED, managerSarah.getId());
        createTimesheet(empJane.getId(), lastWeek, lastWeek.plusDays(6), TimesheetStatus.DRAFT, null);
        
        // Bob Johnson's timesheets (Sales)
        createTimesheet(empBob.getId(), threeWeeksAgo, threeWeeksAgo.plusDays(6), TimesheetStatus.APPROVED, managerMike.getId());
        createTimesheet(empBob.getId(), twoWeeksAgo, twoWeeksAgo.plusDays(6), TimesheetStatus.PENDING, null);
        
        // Alice Brown's timesheets (Sales)
        createTimesheet(empAlice.getId(), threeWeeksAgo, threeWeeksAgo.plusDays(6), TimesheetStatus.APPROVED, managerMike.getId());
        createTimesheet(empAlice.getId(), twoWeeksAgo, twoWeeksAgo.plusDays(6), TimesheetStatus.APPROVED, managerMike.getId());
        
        System.out.println("📋 Created " + timesheetRepository.count() + " timesheets");

        // ===== 4. Create PTO Requests for PTOController Testing =====
        
        // John Doe's PTO requests (Engineering)
        createPTORequest(empJohn.getId(), today.plusDays(5), today.plusDays(7), LeaveType.PTO, 
            "Family vacation", PTOStatus.APPROVED, managerSarah.getId());
        createPTORequest(empJohn.getId(), today.plusDays(15), today.plusDays(16), LeaveType.PTO, 
            "Doctor appointment", PTOStatus.PENDING, null);
        createPTORequest(empJohn.getId(), today.minusDays(10), today.minusDays(8), LeaveType.SICK, 
            "Was sick", PTOStatus.APPROVED, managerSarah.getId());
        
        // Jane Smith's PTO requests (Engineering)
        createPTORequest(empJane.getId(), today.plusDays(10), today.plusDays(14), LeaveType.PTO, 
            "Beach vacation", PTOStatus.PENDING, null);
        createPTORequest(empJane.getId(), today.plusDays(20), today.plusDays(21), LeaveType.UNPAID, 
            "Personal day", PTOStatus.REJECTED, managerSarah.getId());
        createPTORequest(empJane.getId(), today.minusDays(5), today.minusDays(5), LeaveType.SICK, 
            "Sick day", PTOStatus.APPROVED, managerSarah.getId());
        
        // Bob Johnson's PTO requests (Sales)
        createPTORequest(empBob.getId(), today.plusDays(8), today.plusDays(10), LeaveType.PTO, 
            "Weekend trip", PTOStatus.PENDING, null);
        createPTORequest(empBob.getId(), today.plusDays(25), today.plusDays(30), LeaveType.PTO, 
            "Annual leave", PTOStatus.APPROVED, managerMike.getId());
        
        // Alice Brown's PTO requests (Sales)
        createPTORequest(empAlice.getId(), today.plusDays(12), today.plusDays(13), LeaveType.SICK, 
            "Dentist appointment", PTOStatus.PENDING, null);
        createPTORequest(empAlice.getId(), today.minusDays(3), today.minusDays(3), LeaveType.PTO, 
            "Half day", PTOStatus.APPROVED, managerMike.getId());
        
        // Charlie Wilson's PTO requests (Marketing)
        createPTORequest(empCharlie.getId(), today.plusDays(18), today.plusDays(22), LeaveType.PTO, 
            "Family wedding", PTOStatus.PENDING, null);
        
        System.out.println("📅 Created " + ptoRequestRepository.count() + " PTO requests");
    }

    private void createTimeEntry(Employee employee, LocalDate date, LocalTime startTime, 
                                  LocalTime endTime, TimeEntryStatus status) {
        LocalDateTime clockIn = LocalDateTime.of(date, startTime);
        TimeEntry entry = new TimeEntry(employee, clockIn);
        
        if (endTime != null) {
            LocalDateTime clockOut = LocalDateTime.of(date, endTime);
            entry.setClockOutTime(clockOut);
        }
        
        entry.setStatus(status);
        timeEntryRepository.save(entry);
    }
    
    private void createTimesheet(Long employeeId, LocalDate startDate, LocalDate endDate, 
                                  TimesheetStatus status, Long approverId) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);
        
        List<TimeEntry> entries = timeEntryRepository.findByEmployeeIdAndClockInTimeBetween(
            employeeId, startDateTime, endDateTime);
        
        if (!entries.isEmpty()) {
            Employee employee = employeeRepository.findById(employeeId).get();
            Timesheet timesheet = new Timesheet(employee, startDate, endDate);
            
            if (status != TimesheetStatus.DRAFT) {
                timesheet.submit();
            }
            
            if (status == TimesheetStatus.APPROVED && approverId != null) {
                Employee approver = employeeRepository.findById(approverId).get();
                timesheet.approve(approver);
            } else if (status == TimesheetStatus.REJECTED && approverId != null) {
                Employee approver = employeeRepository.findById(approverId).get();
                timesheet.reject(approver, "Missing required information. Please correct and resubmit.");
            }
            
            timesheet = timesheetRepository.save(timesheet);
            
            for (TimeEntry entry : entries) {
                entry.setTimesheet(timesheet);
                timeEntryRepository.save(entry);
            }
            
            timesheetRepository.save(timesheet);
        }
    }
    
    private void createPTORequest(Long employeeId, LocalDate startDate, LocalDate endDate,
                                   LeaveType leaveType, String reason, PTOStatus status, Long approverId) {
        Employee employee = employeeRepository.findById(employeeId).get();
        PTORequest request = new PTORequest(employee, startDate, endDate, leaveType, reason);
        
        if (status == PTOStatus.APPROVED && approverId != null) {
            Employee approver = employeeRepository.findById(approverId).get();
            request.approve(approver);
        } else if (status == PTOStatus.REJECTED && approverId != null) {
            Employee approver = employeeRepository.findById(approverId).get();
            request.reject(approver, "Not enough team coverage during requested period.");
        }
        // PENDING status is default, no action needed
        
        ptoRequestRepository.save(request);
    }
    
    private void printTestDataSummary() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("🧪 TEST DATA SUMMARY - READY FOR CONTROLLER TESTING");
        System.out.println("=".repeat(60));
        
        System.out.println("\n📌 EMPLOYEES:");
        System.out.println("   ID 1 - John Admin (ADMIN)");
        System.out.println("   ID 2 - Sarah Manager (MANAGER - Engineering)");
        System.out.println("   ID 3 - Mike Manager (MANAGER - Sales)");
        System.out.println("   ID 4 - John Doe (EMPLOYEE - Engineering) - ACTIVE SESSION");
        System.out.println("   ID 5 - Jane Smith (EMPLOYEE - Engineering)");
        System.out.println("   ID 6 - Bob Johnson (EMPLOYEE - Sales) - ACTIVE SESSION");
        System.out.println("   ID 7 - Alice Brown (EMPLOYEE - Sales)");
        System.out.println("   ID 8 - Charlie Wilson (EMPLOYEE - Marketing)");
        
        System.out.println("\n📌 TIME ENTRY CONTROLLER TESTS:");
        System.out.println("   ✅ Clock In: POST /api/time/clock-in?employeeId=8");
        System.out.println("   ✅ Clock Out: POST /api/time/clock-out?employeeId=4");
        System.out.println("   ✅ Active Session: GET /api/time/employee/4/active-session");
        System.out.println("   ✅ Today's Entries: GET /api/time/employee/4/today");
        System.out.println("   ✅ Pending Entries (Manager): GET /api/time/pending");
        
        System.out.println("\n📌 TIMESHEET CONTROLLER TESTS:");
        System.out.println("   ✅ Submit: POST /api/timesheets/submit?employeeId=5&startDate=" + LocalDate.now().minusDays(7) + "&endDate=" + LocalDate.now().minusDays(1));
        System.out.println("   ✅ Approve: PUT /api/timesheets/{id}/approve?managerId=2");
        System.out.println("   ✅ Reject: PUT /api/timesheets/{id}/reject?managerId=2&reason=Need corrections");
        System.out.println("   ✅ Pending (Manager): GET /api/timesheets/pending");
        System.out.println("   ✅ Pending by Dept: GET /api/timesheets/pending/department?departmentName=Engineering");
        System.out.println("   ✅ Employee Timesheets: GET /api/timesheets/employee/4");
        
        System.out.println("\n📌 PTO CONTROLLER TESTS:");
        System.out.println("   ✅ Request PTO: POST /api/pto/request?employeeId=8&startDate=" + LocalDate.now().plusDays(14) + "&endDate=" + LocalDate.now().plusDays(16) + "&type=PTO&reason=Vacation");
        System.out.println("   ✅ Approve: PUT /api/pto/{id}/approve?managerId=2");
        System.out.println("   ✅ Reject: PUT /api/pto/{id}/reject?managerId=2&reason=Not enough coverage");
        System.out.println("   ✅ Pending (Manager): GET /api/pto/pending");
        System.out.println("   ✅ Pending by Dept: GET /api/pto/pending/department?departmentName=Engineering");
        System.out.println("   ✅ Employee Requests: GET /api/pto/employee/4");
        System.out.println("   ✅ PTO Balance: GET /api/pto/employee/4/balance?year=2024");
        System.out.println("   ✅ Check Conflict: GET /api/pto/employee/4/has-conflict?startDate=" + LocalDate.now().plusDays(5) + "&endDate=" + LocalDate.now().plusDays(7));
        
        System.out.println("\n" + "=".repeat(60));
        System.out.println("🚀 Application ready for testing! Use Postman or browser to test endpoints.");
        System.out.println("=".repeat(60));
    }
}