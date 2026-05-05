import axios from "axios";

const API_BASE_URL = 'http://localhost:8080/api';

const api = axios.create({
    baseURL: API_BASE_URL,
    headers: {
        'Content-Type': 'application/json',
    },
});

// Response interceptor for error handling
api.interceptors.response.use(
    (response) => response.data,
    (error) => {
        const errorMessage = error.response?.data?.message || error.message || 'An error occurred';
        console.error('API ERROR:', errorMessage);
        return Promise.reject(errorMessage);
    }
);

// Employee API
export const employeeApi = {
    getAll: () => api.get('/employees'),
    getById: (id) => api.get(`/employees/${id}`),
    getTimesheets: (employeeId) => api.get(`/timesheets/employee/${employeeId}`),
};

// Time Entry API
export const timeEntryApi = {
    clockIn: (employeeId) => api.post('/time/clock-in', { employeeId }),
    clockOut: (employeeId) => api.post('/time/clock-out', { employeeId }),
    getEmployeeEntries: (employeeId) => api.get(`/time/employee/${employeeId}`),
    getActiveSession: (employeeId) => api.get(`/time/employee/${employeeId}/active-session`),
    getStatus: (employeeId) => api.get(`/time/employee/${employeeId}/status`),
    getTodayEntries: (employeeId) => api.get(`/time/employee/${employeeId}/today`),
};

// Timesheet API
export const timesheetApi = {
    submit: (employeeId, startDate, endDate, notes) => api.post('/timesheets/submit', { employeeId, startDate, endDate, notes }),
    approve: (timesheetId, managerId) => api.put(`/timesheets/${timesheetId}/approve?managerId=${managerId}`),
    reject: (timesheetId, managerId, reason) => api.put(`/timesheets/${timesheetId}/reject?managerId=${managerId}&reason=${reason}`),
    getEmployeeTimesheets: (employeeId) => api.get(`/timesheets/employee/${employeeId}`),
    getPending: () => api.get('/timesheets/pending'),
};

// PTO API
export const ptoApi = {
    request: (employeeId, startDate, endDate, type, reason) => api.post('/pto/request', { employeeId, startDate, endDate, type, reason }),
    approve: (requestId, managerId) => api.put(`/pto/${requestId}/approve?managerId=${managerId}`),
    reject: (requestId, managerId, reason) => api.put(`/pto/${requestId}/reject?managerId=${managerId}&reason=${reason}`),
    getEmployeeRequests: (employeeId) => api.get(`/pto/employee/${employeeId}`),
    getBalance: (employeeId, year) => api.get(`/pto/employee/${employeeId}/balance?year=${year}`),
    getPending: () => api.get('/pto/pending'),
};

export default api;