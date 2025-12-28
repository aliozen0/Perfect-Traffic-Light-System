/*
  Dosya: src/screens/LoginScreen.js
  Güncelleme: Google + Standart (E-posta/Şifre) Giriş Desteği
*/

import React, { useState } from 'react';
import { Box, Button, Typography, Paper, TextField, Divider, InputAdornment, IconButton } from '@mui/material';
import { 
    Google as GoogleIcon, 
    Email as EmailIcon, 
    Lock as LockIcon, 
    Visibility, 
    VisibilityOff,
    Login as LoginIcon 
} from '@mui/icons-material';
import { auth, provider } from '../config/firebase'; 
import { signInWithPopup } from "firebase/auth";
import axios from 'axios';

// --- YETKİLİ KULLANICI LİSTESİ (Sadece Google İçin) ---
const ALLOWED_USERS = [
  "ali_isakoca@hotmail.com",
  "akyuzmustafaa@hotmail.com",
  "menes.gurkan@gmail.com",
  "bedirhanyigit71@gmail.com"
];

export default function LoginScreen({ onLogin }) {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [showPassword, setShowPassword] = useState(false);
  const [loading, setLoading] = useState(false);

  // --- 1. STANDART GİRİŞ (E-posta/Şifre) ---
  const handleStandardLogin = async (e) => {
    e.preventDefault(); // Sayfa yenilenmesini engelle
    setLoading(true);

    try {
        // Backend'e Login İsteği At
        const response = await axios.post('http://localhost:8080/api/auth/login', {
            username: email, // Backend 'username' bekliyor (DataSeeder'da email username olarak atandı)
            password: password
        });

        if (response.data && response.data.token) {
            console.log("Standart Giriş Başarılı ✅");
            localStorage.setItem('token', response.data.token);
            onLogin(email, response.data.isAdmin ? 'admin' : 'user');
        }

    } catch (error) {
        console.error("Giriş Hatası:", error);
        alert("Giriş Başarısız! E-posta veya şifre hatalı.\n(Backend çalışıyor mu?)");
    } finally {
        setLoading(false);
    }
  };

  // --- 2. GOOGLE İLE GİRİŞ ---
  const handleGoogleLogin = async () => {
    try {
      const result = await signInWithPopup(auth, provider);
      const user = result.user;
      
      if (ALLOWED_USERS.includes(user.email)) {
        // Google Login sonrası Backend'den Token Al (Önceki adımda eklemiştik)
        const response = await axios.post('http://localhost:8080/api/auth/google-login', {
            email: user.email
        });

        if (response.data && response.data.token) {
             localStorage.setItem('token', response.data.token);
             onLogin(user.email, 'admin'); 
        }
      } else {
        alert("Erişim İzni Yok! Bu mail adresi yetkili listede değil.");
      }
    } catch (error) {
      console.error("Google Giriş Hatası:", error);
      alert("Google ile giriş yapılamadı.");
    }
  };

  return (
    <Box 
      sx={{ 
        height: '100vh', width: '100vw',
        backgroundImage: 'url(https://images.unsplash.com/photo-1451187580459-43490279c0fa?q=80&w=2072&auto=format&fit=crop)',
        backgroundSize: 'cover', backgroundPosition: 'center',
        display: 'flex', alignItems: 'center', justifyContent: 'center'
      }}
    >
      <Paper 
        elevation={24}
        sx={{ 
          p: 4, display: 'flex', flexDirection: 'column', alignItems: 'center', 
          backgroundColor: 'rgba(255, 255, 255, 0.95)', backdropFilter: 'blur(10px)',
          borderRadius: '16px', maxWidth: '400px', width: '90%',
          boxShadow: '0 8px 32px 0 rgba(31, 38, 135, 0.37)'
        }}
      >
        {/* LOGO & BAŞLIK */}
        <Typography variant="h4" fontWeight="bold" sx={{ mb: 1, color: '#1a237e', fontFamily: 'Orbitron, sans-serif' }}>
          TRAFFIC<span style={{color: '#d32f2f'}}>OS</span>
        </Typography>
        <Typography variant="body2" sx={{ mb: 4, color: '#555' }}>
          Güvenli Komuta Merkezi Girişi
        </Typography>

        {/* --- FORM ALANI --- */}
        <form onSubmit={handleStandardLogin} style={{ width: '100%' }}>
            
            <TextField 
                fullWidth label="E-posta" variant="outlined" margin="normal"
                value={email} onChange={(e) => setEmail(e.target.value)}
                InputProps={{
                    startAdornment: (<InputAdornment position="start"><EmailIcon color="action"/></InputAdornment>),
                }}
            />
            
            <TextField 
                fullWidth label="Şifre" variant="outlined" margin="normal"
                type={showPassword ? "text" : "password"}
                value={password} onChange={(e) => setPassword(e.target.value)}
                InputProps={{
                    startAdornment: (<InputAdornment position="start"><LockIcon color="action"/></InputAdornment>),
                    endAdornment: (
                        <InputAdornment position="end">
                            <IconButton onClick={() => setShowPassword(!showPassword)} edge="end">
                                {showPassword ? <VisibilityOff /> : <Visibility />}
                            </IconButton>
                        </InputAdornment>
                    )
                }}
            />

            <Button 
                type="submit" variant="contained" fullWidth size="large"
                disabled={loading}
                startIcon={<LoginIcon />}
                sx={{ mt: 3, mb: 2, bgcolor: '#1a237e', '&:hover': { bgcolor: '#0d1b60' }, py: 1.5 }}
            >
                {loading ? "Giriş Yapılıyor..." : "Giriş Yap"}
            </Button>

        </form>

        <Divider sx={{ width: '100%', my: 2 }}>VEYA</Divider>

        {/* GOOGLE BUTONU */}
        <Button 
          variant="outlined" fullWidth size="large"
          startIcon={<GoogleIcon />}
          onClick={handleGoogleLogin}
          sx={{ 
            color: '#444', borderColor: '#ccc', textTransform: 'none',
            '&:hover': { bgcolor: '#f5f5f5', borderColor: '#bbb' }
          }}
        >
          Google ile Devam Et
        </Button>

 

      </Paper>
    </Box>
  );
}