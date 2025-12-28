import React, { useState } from 'react';
import { Grid, Card, CardHeader, CardContent, Box, Typography, FormControl, FormLabel, RadioGroup, FormControlLabel, Radio, TextField, Divider, Switch, LinearProgress, List, ListItem, ListItemIcon, ListItemText, Chip, Button, Alert } from '@mui/material';
import { 
  WarningAmber as WarningIcon, 
  Security as SecurityIcon, 
  GppGood as VerifiedIcon, 
  Lock as LockIcon, 
  Power as PowerIcon, 
  Thermostat as TempIcon, 
  Memory as CpuIcon, 
  Videocam as CameraIcon,
  Speed as SpeedIcon,
  BugReport as BugIcon
} from '@mui/icons-material';

export default function SecurityTab({ 
  isReadOnly, 
  failsafeMode, setFailsafeMode, 
  simulationSpeed, setSimulationSpeed,
  firewallEnabled, setFirewallEnabled,
  cabinetDoorOpen, setCabinetDoorOpen
}) {
  // Demo Verileri KALDIRILDI (Artık Props'tan geliyor)
  
  return (
    <Grid container spacing={3}>
      
      {/* 1. SİBER GÜVENLİK & AĞ (CYBER SECURITY) */}
      <Grid item xs={12} md={6}>
        <Card sx={{ height: '100%' }}>
          <CardHeader 
            avatar={<SecurityIcon color="primary"/>}
            title="Siber Güvenlik & Ağ Durumu"
            subheader="Ağ Koruması ve Şifreleme"
            action={<Chip label="GÜVENLİ" color="success" size="small" icon={<VerifiedIcon/>}/>}
          />
          <Divider />
          <CardContent>
            <List dense>
              <ListItem>
                <ListItemIcon><LockIcon /></ListItemIcon>
                <ListItemText primary="Bağlantı Şifrelemesi" secondary="TLS 1.3 (AES-256-GCM)" />
                <Switch defaultChecked disabled />
              </ListItem>
              <ListItem>
                <ListItemIcon><SecurityIcon /></ListItemIcon>
                <ListItemText primary="Firewall Koruması" secondary={firewallEnabled ? "Aktif - İzinsiz girişler engelleniyor" : "Pasif - Riskli!"} />
                <Switch checked={firewallEnabled} onChange={(e) => setFirewallEnabled(e.target.checked)} disabled={isReadOnly} color="success"/>
              </ListItem>
              <ListItem>
                <ListItemIcon><BugIcon /></ListItemIcon>
                <ListItemText primary="Sızma Testi (Penetration Test)" secondary="Son Tarama: 2 saat önce (Temiz)" />
                <Button size="small" variant="outlined" disabled={isReadOnly}>Tara</Button>
              </ListItem>
            </List>
            
            <Box sx={{ mt: 2, p: 2, bgcolor: '#e8f5e9', borderRadius: 2, border: '1px solid #c8e6c9' }}>
              <Typography variant="caption" color="success.main" fontWeight="bold">✓ Yetkisiz Erişim Tespit Edilmedi</Typography>
            </Box>
          </CardContent>
        </Card>
      </Grid>

      {/* 2. DONANIM SAĞLIĞI (HARDWARE HEALTH) */}
      <Grid item xs={12} md={6}>
        <Card sx={{ height: '100%' }}>
          <CardHeader 
            avatar={<MemoryIconWrapper />}
            title="Donanım ve Sistem Sağlığı"
            subheader="Controller Box Durumu"
          />
          <Divider />
          <CardContent>
            <Box sx={{ mb: 2 }}>
              <Box display="flex" justifyContent="space-between" mb={0.5}>
                <Box display="flex" alignItems="center"><CpuIcon fontSize="small" sx={{mr:1, color:'text.secondary'}}/> <Typography variant="body2">CPU Kullanımı</Typography></Box>
                <Typography variant="body2" fontWeight="bold">24%</Typography>
              </Box>
              <LinearProgress variant="determinate" value={24} sx={{ height: 8, borderRadius: 4 }} />
            </Box>

            <Box sx={{ mb: 2 }}>
              <Box display="flex" justifyContent="space-between" mb={0.5}>
                <Box display="flex" alignItems="center"><MemoryIconWrapper fontSize="small" sx={{mr:1, color:'text.secondary'}}/> <Typography variant="body2">RAM Kullanımı</Typography></Box>
                <Typography variant="body2" fontWeight="bold">45%</Typography>
              </Box>
              <LinearProgress variant="determinate" value={45} color="secondary" sx={{ height: 8, borderRadius: 4 }} />
            </Box>

            <Grid container spacing={2} sx={{ mt: 1 }}>
              <Grid item xs={6}>
                <Box sx={{ p: 1.5, border: '1px solid #eee', borderRadius: 2, textAlign: 'center' }}>
                  <TempIcon color="error" />
                  <Typography variant="body2" color="text.secondary">Sıcaklık</Typography>
                  <Typography variant="h6">42°C</Typography>
                </Box>
              </Grid>
              <Grid item xs={6}>
                <Box sx={{ p: 1.5, border: '1px solid #eee', borderRadius: 2, textAlign: 'center' }}>
                  <PowerIcon color="warning" />
                  <Typography variant="body2" color="text.secondary">Güç Kaynağı</Typography>
                  <Typography variant="h6">UPS (Aktif)</Typography>
                </Box>
              </Grid>
            </Grid>
          </CardContent>
        </Card>
      </Grid>

      {/* 3. FAIL-SAFE & FİZİKSEL GÜVENLİK */}
      <Grid item xs={12} md={6}>
        <Card sx={{ height: '100%', borderLeft: '6px solid #d32f2f' }}>
          <CardContent>
            <Box display="flex" alignItems="center" mb={2}>
              <WarningIcon color="error" sx={{ mr: 1 }} />
              <Typography variant="h6">Fail-Safe (Hata) Modu</Typography>
            </Box>
            <Typography variant="body2" color="text.secondary" paragraph>
              Sistem hatası veya iletişim kopukluğu durumunda devreye girecek güvenlik protokolü.
            </Typography>
            <FormControl disabled={isReadOnly} component="fieldset">
              <RadioGroup value={failsafeMode} onChange={(e) => setFailsafeMode(e.target.value)}>
                <FormControlLabel value="all-red" control={<Radio color="error"/>} label={<Typography fontWeight="bold" color="error">Tüm Yönler Kırmızı (Tam Durdurma)</Typography>} />
                <FormControlLabel value="flash-yellow" control={<Radio color="warning"/>} label="Kontrollü Sarı Flaş (Dikkat)" />
                <FormControlLabel value="fixed-time" control={<Radio />} label="Sabit Süreli Plan (Offline Mod)" />
              </RadioGroup>
            </FormControl>

            <Divider sx={{ my: 2 }} />
            
            <Typography variant="subtitle2" gutterBottom>Fiziksel Güvenlik</Typography>
            <FormControlLabel 
              control={<Switch checked={cabinetDoorOpen} onChange={(e) => setCabinetDoorOpen(e.target.checked)} color="error" disabled={isReadOnly}/>} 
              label={cabinetDoorOpen ? "Kabin Kapağı AÇIK (Alarm!)" : "Kabin Kapağı KAPALI"} 
            />
            {cabinetDoorOpen && <Alert severity="error" sx={{ mt: 1 }}>DİKKAT: Saha dolabı kapağı açık sinyali alınıyor!</Alert>}
          </CardContent>
        </Card>
      </Grid>

      {/* 4. SİMÜLASYON & TEST ORTAMI */}
      <Grid item xs={12} md={6}>
        <Card sx={{ height: '100%', bgcolor: '#fafafa' }}>
          <CardContent>
            <Box display="flex" alignItems="center" mb={2}>
              <SpeedIcon color="action" sx={{ mr: 1 }} />
              <Typography variant="h6">Simülasyon Ayarları</Typography>
            </Box>
            
            <Box sx={{ width: '100%', px: 1, py: 2, display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
                <TextField
                    label="Simülasyon Hızı (x)" type="number" value={simulationSpeed} disabled={isReadOnly}
                    onChange={(e) => { let val = Number(e.target.value); if (val < 1) val = 1; if (val > 10) val = 10; setSimulationSpeed(val); }}
                    InputProps={{ inputProps: { min: 1, max: 10 } }} variant="outlined" size="small" sx={{ width: '150px', mb: 1 }}
                />
                <Typography variant="caption" sx={{ color: 'text.secondary' }}>(1x = Gerçek Zaman, 10x = Hızlandırılmış)</Typography>
            </Box>
            
            <List dense>
              <ListItem>
                <ListItemIcon><CameraIcon /></ListItemIcon>
                <ListItemText primary="Sanal Kamera Beslemesi" secondary="Sentetik görüntü üretimi aktif" />
                <Switch defaultChecked disabled={isReadOnly} />
              </ListItem>
              <ListItem>
                <ListItemIcon><WarningIcon /></ListItemIcon>
                <ListItemText primary="Kaza Senaryosu Testi" secondary="Rastgele kaza olayları üret" />
                <Switch disabled={isReadOnly} />
              </ListItem>
            </List>
          </CardContent>
        </Card>
      </Grid>

    </Grid>
  );
}

// Icon Wrapper
const MemoryIconWrapper = (props) => <CpuIcon {...props} />;

