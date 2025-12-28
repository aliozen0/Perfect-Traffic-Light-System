// src/App.js
import React, { useState } from 'react';
import { ThemeProvider, CssBaseline } from '@mui/material';
import theme from './config/theme';
import LoginScreen from './screens/LoginScreen';
import DashboardScreen from './screens/DashboardScreen';

function App() {
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [userRole, setUserRole] = useState('admin'); // 'admin' veya 'viewer'

  const handleLogin = (email, role) => {
    // Şifre kontrolü kalktı, direkt role ataması yapıyoruz
    setUserRole(role);
    setIsLoggedIn(true);
    // İstersen email'i de bir state'e atıp ekranda gösterebilirsin
  };

  const handleLogout = () => {
    setIsLoggedIn(false);
    setUserRole('');
  };

  // KULLANICI GİRİŞ YAPTIYSA DASHBOARD'I GÖSTER, YOKSA LOGIN EKRANINI GÖSTER
  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      {isLoggedIn ? (
        <DashboardScreen userRole={userRole} onLogout={handleLogout} />
      ) : (
        <LoginScreen onLogin={handleLogin} />
      )}
    </ThemeProvider>
  );
}

export default App;
