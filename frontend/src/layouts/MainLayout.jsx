import React, { useState } from 'react';
import {
    HomeIcon,
    ClockIcon,
    DocumentTextIcon,
    CalendarIcon,
    UserCircleIcon,
    ArrowRightOnRectangleIcon,
    Bars3Icon,
    XMarkIcon
} from '@heroicons/react/24/outline';

// Navigation items configuration
const navigation = [
    { name: 'Dashboard', href: '/', icon: HomeIcon },
    { name: 'Time Entries', href: '/time-entries', icon: ClockIcon },
    { name: 'Timesheets', href: '/timesheets', icon: DocumentTextIcon },
    { name: 'PTO Requests', href: '/pto', icon: CalendarIcon },
];

const MainLayout = ({ children }) => {
    const [sidebarOpen, setSidebarOpen] = useState(false);

    return (
        <div className="min-h-screen bg-gray-100">
            {/* Mobile sidebar backdrop */}
            {sidebarOpen && (
                <div 
                    className="fixed inset-0 z-20 bg-black bg-opacity-50 lg:hidden"
                    onClick={() => setSidebarOpen(false)}
                />
            )}

            {/* Sidebar */}
            <aside className={`
                fixed inset-y-0 left-0 z-30 w-64 transform bg-gray-900 transition-transform duration-300 ease-in-out lg:translate-x-0
                ${sidebarOpen ? 'translate-x-0' : '-translate-x-full'}
            `}>
                {/* Sidebar Header */}
                <div className="flex h-16 items-center justify-between px-4 border-b border-gray-800">
                    <div className="flex items-center space-x-2">
                        <div className="h-8 w-8 bg-blue-500 rounded-lg flex items-center justify-center">
                            <span className="text-white font-bold text-lg">ET</span>
                        </div>
                        <span className="text-white font-semibold text-lg">Employee Tracker</span>
                    </div>
                    <button
                        className="lg:hidden text-gray-400 hover:text-white"
                        onClick={() => setSidebarOpen(false)}
                    >
                        <XMarkIcon className="h-6 w-6" />
                    </button>
                </div>

                {/* Navigation Links */}
                <nav className="mt-6 px-3 space-y-1">
                    {navigation.map((item) => (
                        <a
                            key={item.name}
                            href={item.href}
                            className="flex items-center px-3 py-2.5 text-sm font-medium text-gray-300 rounded-lg hover:bg-gray-800
                            hover:text-white transition-colors duration-200 group"
                        >
                            <item.icon className="mr-3 h-5 w-5 text-gray-400 group-hover:text-gray-300" />
                            {item.name}
                        </a>
                    ))}
                </nav>

                {/* Bottom Section */}
                <div className="absolute bottom-0 left-0 right-0 p-4 border-t border-gray-800">
                    <div className="flex items-center space-x-3 px-2 py-2 rounded-lg hover:bg-gray-800
                    transition-colors cursor-pointer">
                        <div className="h-8 w-8 bg-gray-700 rounded-full flex items-center justify-center">
                            <UserCircleIcon className="h-6 w-6 text-gray-400" />
                        </div>
                        <div className="flex-1">
                            <p className="text-sm font-medium text-white">John Doe</p>
                            <p className="text-xs text-gray-400">Employee</p>
                        </div>
                        <ArrowRightOnRectangleIcon className="h-5 w-5 text-gray-400 hover:text-white" />
                    </div>
                </div>
            </aside>

            {/* Main Content Area */}
            <div className="lg:pl-64 flex flex-col min-h-screen">
                {/* Top Navbar */}
                <header className="bg-white shadow-sm sticky top-0 z-10">
                    <div className="flex h-16 items-center justify-between px-4 lg:px-6">
                        {/* Mobile menu button */}
                        <button
                            className="lg:hidden text-gray-500 hover:text-gray-700"
                            onClick={() => setSidebarOpen(true)}
                        >
                            <Bars3Icon className="h-6 w-6" />
                        </button>

                        {/* Page Title (will be dynamic later) */}
                        <div className="flex-1 lg:flex-none">
                            <h1 className="text-lg font-semibold text-gray-800">Dashboard</h1>
                        </div>

                        {/* User menu placeholder */}
                        <div className="flex items-center space-x-3">
                            <div className="hidden md:block text-right">
                                <p className="text-sm font-medium text-gray-700">John Doe</p>
                                <p className="text-xs text-gray-500">john.doe@example.com</p>
                            </div>
                            <div className="h-9 w-9 bg-gray-200 rounded-full flex items-center justify-center">
                                <UserCircleIcon className="h-7 w-7 text-gray-500" />
                            </div>
                        </div>
                    </div>
                </header>

                {/* Page Content */}
                <main className="flex-1 p-4 lg:p-6">
                    {children}
                </main>
            </div>
        </div>
    );
};

export default MainLayout;