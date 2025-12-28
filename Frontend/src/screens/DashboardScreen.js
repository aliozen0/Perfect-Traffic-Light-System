// src/screens/DashboardScreen.js
import React, { useState } from 'react';
import { 
  Box, Button, Typography, Paper, Toolbar, IconButton, 
  Snackbar, Alert, Chip, TableContainer, Table, TableHead, TableRow, TableCell, TableBody, Card, CardContent,
  Grid, Dialog, DialogTitle, DialogContent, DialogContentText, DialogActions,
  Select, MenuItem, FormControl, InputLabel, Drawer, List, ListItemButton, ListItemIcon, ListItemText, Divider 
} from '@mui/material';
import { 
  Menu as MenuIcon, Logout as LogoutIcon, Save as SaveIcon, Traffic as TrafficIcon,
  Settings as SettingsIcon, Policy as PolicyIcon, Map as MapIcon, Security as SecurityIcon, 
  Assessment as AssessmentIcon, History as HistoryIcon, ChevronLeft as ChevronLeftIcon
} from '@mui/icons-material';

import TabPanel from '../components/TabPanel';
import IntersectionVisualizer from '../components/IntersectionVisualizer';
import GeneralSettingsTab from '../components/tabs/GeneralSettingsTab';
import LanesTab from '../components/tabs/LanesTab';
import PoliciesTab from '../components/tabs/PoliciesTab';
import SecurityTab from '../components/tabs/SecurityTab';
import AnalyticsTab from '../components/tabs/AnalyticsTab';
import MapTab from '../components/tabs/MapTab';
import { initialLanes } from '../data/laneData';
import { useTrafficData } from '../hooks/useTrafficData';
import TrafficMap from '../components/TrafficMap';
import TrafficSimulationContainer from '../components/TrafficSimulationContainer';

export default function DashboardScreen({ userRole, onLogout }) {
  const [selectedIntersection, setSelectedIntersection] = useState('active'); // KAVŞAK SEÇİMİ
  const [tabValue, setTabValue] = useState(0);
  const [confirmOpen, setConfirmOpen] = useState(false); // Onay penceresi kontrolü
  const [drawerOpen, setDrawerOpen] = useState(true); // Sidebar açık/kapalı durumu

  // --- VERİ YÖNETİMİ (HOOK) ---
  const {
    notification, setNotification,
    logs,
    emergencyMode, setEmergencyMode,
    busPriority, setBusPriority,
    failsafeMode, setFailsafeMode,
    simulationSpeed, setSimulationSpeed,
    controlMode, setControlMode,
    intersectionType, setIntersectionType,
    controllerIp, setControllerIp,
    rushHourMode, setRushHourMode,
    ecoMode, setEcoMode,
    pedestrianLPI, setPedestrianLPI,
    enforcementLevel, setEnforcementLevel,
    firewallEnabled, setFirewallEnabled,
    cabinetDoorOpen, setCabinetDoorOpen,
    phases, setPhases,
    lanes, setLanes,
    saveToFirebase,
    isReadOnly
  } = useTrafficData(selectedIntersection, userRole);

  // 1. Kaydetme Onayı Penceresini Aç
  const handleSaveClick = () => {
    setConfirmOpen(true);
  };

  // 2. Onay Verildiğinde Kaydetme İşlemini Başlat
  const handleConfirmSave = () => {
    setConfirmOpen(false);
    saveToFirebase(); 
  };

  const getConfigJSON = () => {
    return JSON.stringify({
      id: "JN-01", 
      role: userRole, 
      type: intersectionType,
      mode: controlMode.toUpperCase(),
      network: { ip: controllerIp, status: "online" },
      policies: { 
        emergency: emergencyMode, 
        busPriority: busPriority,
        rushHour: rushHourMode,
        ecoMode: ecoMode,
        pedestrianLPI: pedestrianLPI,
        enforcement: enforcementLevel
      },
      security: {
        firewall: firewallEnabled,
        cabinetDoor: cabinetDoorOpen,
        failsafe: failsafeMode
      },
      phases: phases.map(p => ({ name: p.shortName, duration: p.duration })),
      simulationSpeed: simulationSpeed
    }, null, 2);
  };

  const handleDownloadConfig = () => {
    const configData = {
      intersectionId: "JN-01",
      role: userRole,
      type: intersectionType,
      network: { ip: controllerIp, port: 8080, status: "online" },
      controlStrategy: controlMode.toUpperCase(),
      lanes: initialLanes.map(l => l.label),
      policies: { emergency: emergencyMode, busPriority: busPriority, failsafe: failsafeMode },
      simulationSpeed: simulationSpeed,
      version: "v1.4.2"
    };

    const jsonString = JSON.stringify(configData, null, 2);
    const blob = new Blob([jsonString], { type: "application/json" });
    const href = URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = href; link.download = "traffic_config_jn01.json";
    document.body.appendChild(link); link.click(); document.body.removeChild(link);
    setNotification({ open: true, message: 'Konfigürasyon dosyası (JSON) İndirildi!' });
  };

  const toggleDrawer = () => {
    setDrawerOpen(!drawerOpen);
  };

  const menuItems = [
    { index: 0, label: 'Genel Ayarlar', icon: <SettingsIcon /> },
    { index: 1, label: 'Şerit Yönetimi', icon: <TrafficIcon /> },
    { index: 2, label: 'Trafik Kuralları', icon: <PolicyIcon /> },
    { index: 3, label: 'Güvenlik', icon: <SecurityIcon /> },
    { index: 4, label: 'Analiz & Rapor', icon: <AssessmentIcon /> },
    { index: 5, label: 'Canlı Harita', icon: <MapIcon /> },
    { index: 6, label: 'Canlı Simülasyon', icon: <TrafficIcon /> },
  ];

  return (
    <Box sx={{ display: 'flex', minHeight: '100vh', bgcolor: 'background.default' }}>
      {/* --- SIDEBAR (DRAWER) --- */}
      <Drawer
        variant="permanent"
        open={drawerOpen}
        sx={{
          width: drawerOpen ? 240 : 65,
          flexShrink: 0,
          '& .MuiDrawer-paper': {
            width: drawerOpen ? 240 : 65,
            boxSizing: 'border-box',
            transition: 'width 0.3s',
            overflowX: 'hidden',
            bgcolor: 'primary.main', // Sidebar rengi
            color: 'white'
          },
        }}
      >
        <Toolbar sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', px: [1] }}>
          {drawerOpen && <Typography variant="h6" noWrap component="div" sx={{ ml: 2, fontWeight: 'bold' }}>TrafficOS</Typography>}
          <IconButton onClick={toggleDrawer} sx={{ color: 'white' }}>
            {drawerOpen ? <ChevronLeftIcon /> : <MenuIcon />}
          </IconButton>
        </Toolbar>
        <Divider sx={{ bgcolor: 'rgba(255,255,255,0.2)' }} />
        <List component="nav">
          {menuItems.map((item) => (
            <ListItemButton 
              key={item.index} 
              selected={tabValue === item.index}
              onClick={() => setTabValue(item.index)}
              sx={{
                '&.Mui-selected': { bgcolor: 'rgba(255,255,255,0.2)' },
                '&:hover': { bgcolor: 'rgba(255,255,255,0.1)' },
                my: 0.5, mx: 1, borderRadius: 2
              }}
            >
              <ListItemIcon sx={{ color: 'white', minWidth: 40 }}>
                {item.icon}
              </ListItemIcon>
              {drawerOpen && <ListItemText primary={item.label} />}
            </ListItemButton>
          ))}
        </List>
        <Box sx={{ mt: 'auto', p: 2 }}>
           {drawerOpen ? (
             <Button 
               fullWidth 
               variant="outlined" 
               color="inherit" 
               startIcon={<LogoutIcon />} 
               onClick={onLogout}
               sx={{ borderColor: 'rgba(255,255,255,0.5)', color: 'white' }}
             >
               Çıkış Yap
             </Button>
           ) : (
             <IconButton onClick={onLogout} sx={{ color: 'white' }}><LogoutIcon /></IconButton>
           )}
        </Box>
      </Drawer>

      {/* --- MAIN CONTENT --- */}
      <Box component="main" sx={{ flexGrow: 1, p: 3, overflow: 'auto', height: '100vh' }}>
        {/* HEADER BAR */}
        <Paper elevation={0} sx={{ p: 2, mb: 3, display: 'flex', alignItems: 'center', justifyContent: 'space-between', borderRadius: 3, bgcolor: 'background.paper' }}>
           <Box display="flex" alignItems="center">
              <Box sx={{ p: 1, borderRadius: 2, bgcolor: 'primary.light', color: 'white', mr: 2 }}>
                 {menuItems[tabValue].icon}
              </Box>
              <Box>
                <Typography variant="h5" fontWeight="bold" color="text.primary">{menuItems[tabValue].label}</Typography>
                <Typography variant="body2" color="text.secondary">
                  Hoşgeldin, {userRole === 'admin' ? 'Yönetici' : 'İzleyici'}
                </Typography>
              </Box>
           </Box>

           <Box display="flex" alignItems="center" gap={2}>
              {/* KAVŞAK SEÇİCİ */}
              <FormControl variant="outlined" size="small" sx={{ minWidth: 200 }}>
                <InputLabel>Aktif Kavşak</InputLabel>
                <Select
                  value={selectedIntersection}
                  onChange={(e) => setSelectedIntersection(e.target.value)}
                  label="Aktif Kavşak"
                >
                  <MenuItem value="active">Beşevler Meydan (JN-01)</MenuItem>
                  <MenuItem value="kizilay">Kızılay Merkez (JN-02)</MenuItem>
                  <MenuItem value="ulus">Ulus Heykel (JN-03)</MenuItem>
                  <MenuItem value="cankaya">Çankaya Köşkü (JN-04)</MenuItem>
                </Select>
              </FormControl>

              <Button 
                  variant="contained" 
                  startIcon={<SaveIcon />} 
                  onClick={handleSaveClick} 
                  disabled={isReadOnly} 
                  sx={{ borderRadius: 2, px: 3 }}
              >
                  Kaydet
              </Button>
           </Box>
        </Paper>

        {/* --- MODÜLER SEKME İÇERİĞİ --- */}
        <Box sx={{ mb: 4 }}>
          <TabPanel value={tabValue} index={0}>
              {/* GÖRSELLEŞTİRİCİ (VISUALIZER) */}
              <Box sx={{ mb: 3 }}>
                <IntersectionVisualizer 
                    emergencyMode={emergencyMode}
                    failsafeMode={failsafeMode}
                    intersectionType={intersectionType}
                    controlMode={controlMode}
                />
              </Box>

              <GeneralSettingsTab 
                  isReadOnly={isReadOnly}
                  intersectionType={intersectionType} setIntersectionType={setIntersectionType}
                  controllerIp={controllerIp} setControllerIp={setControllerIp}
                  controlMode={controlMode} setControlMode={setControlMode}
                  onDownload={handleDownloadConfig} configJSON={getConfigJSON()}
              />
          </TabPanel>

          <TabPanel value={tabValue} index={1}>
              <LanesTab 
                isReadOnly={isReadOnly} 
                phases={phases} setPhases={setPhases}
                lanes={lanes} setLanes={setLanes}
              />
          </TabPanel>

          <TabPanel value={tabValue} index={2}>
              <PoliciesTab 
                  isReadOnly={isReadOnly}
                  emergencyMode={emergencyMode} setEmergencyMode={setEmergencyMode}
                  busPriority={busPriority} setBusPriority={setBusPriority}
                  rushHourMode={rushHourMode} setRushHourMode={setRushHourMode}
                  ecoMode={ecoMode} setEcoMode={setEcoMode}
                  pedestrianLPI={pedestrianLPI} setPedestrianLPI={setPedestrianLPI}
                  enforcementLevel={enforcementLevel} setEnforcementLevel={setEnforcementLevel}
              />
          </TabPanel>

          <TabPanel value={tabValue} index={3}>
              <SecurityTab 
                  isReadOnly={isReadOnly}
                  failsafeMode={failsafeMode} setFailsafeMode={setFailsafeMode}
                  simulationSpeed={simulationSpeed} setSimulationSpeed={setSimulationSpeed}
                  firewallEnabled={firewallEnabled} setFirewallEnabled={setFirewallEnabled}
                  cabinetDoorOpen={cabinetDoorOpen} setCabinetDoorOpen={setCabinetDoorOpen}
              />
          </TabPanel>

          <TabPanel value={tabValue} index={4}>
              <AnalyticsTab />
          </TabPanel>

          <TabPanel value={tabValue} index={5}>
              <MapTab 
                selectedIntersection={selectedIntersection}
                setSelectedIntersection={setSelectedIntersection}
              />
          </TabPanel>

          {/* YENİ EKLENEN SİMÜLASYON PANELİ */}
          <TabPanel value={tabValue} index={6}>
            {/* Div'i kaldırdık, padding'i yok ettik */}
            <Box sx={{ width: '100%', height: 'calc(100vh - 150px)', overflow: 'hidden', borderRadius: 2 }}>
              <TrafficSimulationContainer lanes={lanes} phases={phases} />
            </Box>
          </TabPanel>


        </Box>

        {/* VERSİYON GEÇMİŞİ TABLOSU */}
        <Grid item xs={12}>
          <Card>
              <CardContent>
              <Box display="flex" alignItems="center" mb={2}><HistoryIcon color="action" sx={{mr:1}}/><Typography variant="h6">Son İşlem Geçmişi</Typography></Box>
              <TableContainer>
                  <Table size="small">
                      <TableHead><TableRow><TableCell>Tarih</TableCell><TableCell>Kullanıcı</TableCell><TableCell>İşlem</TableCell></TableRow></TableHead>
                      <TableBody>
                        {logs.map((log) => (
                          <TableRow key={log.id}>
                            <TableCell>{new Date(log.date).toLocaleString()}</TableCell>
                            <TableCell>{log.user}</TableCell>
                            <TableCell>
                              <Chip label={log.action} size="small" color="success" variant="outlined" />
                              <Typography variant="caption" sx={{ml:1}}>{log.details}</Typography>
                            </TableCell>
                          </TableRow>
                        ))}
                        {logs.length === 0 && (
                          <TableRow><TableCell colSpan={3} align="center">Henüz kayıt yok.</TableCell></TableRow>
                        )}
                      </TableBody>
                  </Table>
              </TableContainer>
              </CardContent>
          </Card>
        </Grid>
      </Box>
      
      <Snackbar open={notification.open} autoHideDuration={4000} onClose={() => setNotification({...notification, open:false})} anchorOrigin={{ vertical: 'bottom', horizontal: 'right' }}>
        <Alert onClose={() => setNotification({...notification, open:false})} severity={isReadOnly ? 'info' : 'success'} sx={{ width: '100%' }}>{notification.message}</Alert>
      </Snackbar>

      {/* --- ONAY PENCERESİ (CONFIRMATION DIALOG) --- */}
      <Dialog
        open={confirmOpen}
        onClose={() => setConfirmOpen(false)}
      >
        <DialogTitle>Değişiklikleri Kaydet?</DialogTitle>
        <DialogContent>
          <DialogContentText>
            Bu işlem, canlı trafik sistemindeki aktif konfigürasyonu güncelleyecektir.
            <br/><br/>
            <strong>Etkilenecek Ayarlar:</strong> Mod ({controlMode}), Hız ({simulationSpeed}x)
            <br/>
            Devam etmek istiyor musunuz?
          </DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setConfirmOpen(false)} color="inherit">Vazgeç</Button>
          <Button onClick={handleConfirmSave} variant="contained" color="primary" autoFocus>
            EVET, GÜNCELLE
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
}