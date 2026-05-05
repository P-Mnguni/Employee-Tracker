import React, { useState, useEffect } from "react";
import { toast } from 'react-hot-toast';
import { timeEntryApi, timesheetApi, ptoApi } from '../services/api';

const Dashboard = () => {
    const [employeeId, setEmployeeId] = useState(4);
    const [status, setStatus] = useState(null);
    const [activeSession, setActiveSession] = useState(null);
    const [todayEntries, setTodayEntries] = useState([]);
    const [pendingTimesheets, setPendingTimesheets] = useState([]);
    const [ptos, setPtos] = useState([]);
    const [loading, setLoading] = useState(false);

    const loadStatus = async () => {
        try {
            const data = await timeEntryApi.getStatus(employeeId);
            console.log('Status:', data);
        } catch (error) {
            console.error(error);
        }
    };

    const loadActiveSession = async () => {
        try {
            const data = await timeEntryApi.getActiveSession(employeeId);
            setActiveSession(data);
        } catch (error) {
            console.error(error);
        }
    };

    const loadTodayEntries = async () => {
        try {
            const data = await timeEntryApi.getTodayEntries(employeeId);
            setTodayEntries(data.entries || []);
        } catch (error) {
            console.error(error);
        }
    };

    const loadPendingTimesheets = async () => {
        try {
            const data = await timesheetApi.getPending();
            setPendingTimesheets(data.pendingTimesheets || []);
        } catch (error) {
            console.error(error);
        }
    };

    const loadPendingPTO = async () => {
        try {
            const data = await ptoApi.getPending();
            setPtos(data.pendingRequests || []);
        } catch (error) {
            console.error(error);
        }
    }

    // Load initial data
    useEffect(() => {
        if (employeeId) {
            loadStatus();
            loadActiveSession();
            loadTodayEntries();
            loadPendingTimesheets();
            loadPendingPTO();
        }
    }, [employeeId]);

    const handleClockIn = async () => {
        setLoading(true);
        try {
            await timeEntryApi.clockIn(employeeId);
            toast.success('Clocked in successfully!');
            await loadActiveSession();
            await loadTodayEntries();
        } catch (error) {
            toast.error(error);
        } finally {
            setLoading(false);
        }
    };

    const handleClockOut = async () => {
        setLoading(true);
        try {
            await timeEntryApi.clockOut(employeeId);
            toast.success('Clocked out successfully!');
            await loadActiveSession();
            await loadTodayEntries();
        } catch (error) {
            toast.error(error);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="min-h-screen bg-gray-100">
            {/* Header */}
            <header className="bg-white shadow">
                <div className="max-w-7xl mx-auto px-4 py-6">
                    <h1 className="text-3xl font-bold text-gray-900">Employee Tracker Dashboard</h1>
                    <p className="text-gray-600 mt-1">Welcome back, John Doe</p>
                </div>
            </header>

            <main className="max-w-7xl mx-auto px-4 py-6">
                {/* Clock In/Out Section */}
                <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mb-8">
                    <div className="bg-white rounded-lg shadow p-6">
                        <h2 className="text-xl font-semibold mb-4">Time Tracking</h2>
                        <div className="flex gap-4">
                            <button
                                onClick={handleClockIn}
                                disabled={loading || activeSession?.isClockedIn}
                                className={`px-6 py-2 rounded-lg font-medium transition ${
                                    activeSession?.isClockedIn
                                    ? 'bg-gray-400 cursor-not-allowed'
                                    : 'bg-green-600 hover:bg-green-700 text-white'
                                }`}
                            >
                                Clock In
                            </button>
                            <button
                                onClick={handleClockOut}
                                disabled={loading || !activeSession?.isClockedIn}
                                className={`px-6 py-2 rounded-lg font-medium transition ${
                                    !activeSession?.isClockedIn
                                    ? 'bg-gray-400 cursor-not-allowed'
                                    : 'bg-red-600 hover:bg-red-700 text-white'
                                }`}
                            >
                                Clock Out
                            </button>
                        </div>
                        {activeSession?.isClockedIn && (
                            <div className="mt-4 p-3 bg-green-50 rounded-lg">
                                <p className="text-green-800">
                                    ✅ Currently clocked in since{' '}
                                    {new Date(activeSession.activeEntry?.clockInTime).toLocaleTimeString()}
                                </p>
                            </div>
                        )}
                    </div>

                    {/* Today's Activity */}
                    <div className="bg-white rounded-lg shadow p-6">
                        <h2 className="text-xl font-semibold mb-4">Today's Activity</h2>
                        {todayEntries.length > 0 ? (
                            <div className="space-y-2">
                                {todayEntries.map((entry) => (
                                    <div key={entry.id} className="border-b pb-2">
                                        <p>⏰ In: {new Date(entry.clockInTime).toLocaleTimeString()}</p>
                                        {entry.clockOutTime && (
                                            <p>⏰ Out: {new Date(entry.clockOutTime).toLocaleTimeString()}</p>
                                        )}
                                        <p className="text-sm text-gray-500">Status: {entry.status}</p>
                                    </div>
                                ))}
                            </div>
                        ): (
                            <p className="text-gray-500">No entries today</p>
                        )}
                    </div>
                </div>

                {/* Metrics Grid */}
                <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mb-8">
                    <div className="bg-white rounded-lg shadow p-6">
                        <h2 className="text-xl font-semibold mb-4">Pending Timesheets</h2>
                        <p className="text-3xl font-bold text-blue-600">{pendingTimesheets.length}</p>
                        <p className="text-gray-500 mt-2">Awaiting {pendingTimesheets.length === 1 ? 'approval' : 'approvals'}</p>
                    </div>
                    <div className="bg-white rounded-lg shadow p-6">
                        <h2 className="text-xl font-semibold mb-4">Pending PTO Requests</h2>
                        <p className="text-3xl font-bold text-yellow-600">{ptos.length}</p>
                        <p className="text-gray-500 mt-2">Awaiting {ptos.length === 1 ? 'review' : 'reviews'}</p>
                    </div>
                </div>

                {/* Quick Actions */}
                <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
                    <button className="bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700 transition">
                        Submit Timesheet
                    </button>
                    <button className="bg-green-600 text-white px-4 py-2 rounded-lg hover:bg-green-700 transition">
                        Request PTO
                    </button>
                    <button className="bg-purple-600 text-white px-4 py-2 rounded-lg hover:bg-purple-700 transition">
                        View Timesheets
                    </button>
                    <button className="bg-orange-600 text-white px-4 py-2 rounded-lg hover:bg-orange-700 transition">
                        View PTO History
                    </button>
                </div>
            </main>
        </div>
    );
};

export default Dashboard;