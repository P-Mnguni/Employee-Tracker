import React from "react";
import { Toaster } from "react-hot-toast";
import MainLayout from "./layouts/MainLayout";
import Dashboard from './components/Dashboard';

function App() {
  return (
    <div className="App">
      <Toaster position="top-right" />
      <MainLayout>
        <Dashboard />
      </MainLayout>
    </div>
  );
}

export default App;
