/*
  Dosya: src/components/TrafficSimulationContainer.js
  Güncelleme: REALISTIC EMERGENCY FLOW
  
  Yenilikler:
  1. Ambulans geçerken trafik durmaz, arkasından araçlar akmaya devam eder.
  2. Kırmızı yanan taraf (Yan yollar) hızla tıkanır (%100'e yaklaşır).
  3. Görsel olarak "Birikme" (Queueing) efekti eklendi.
*/
const API_URL = "https://traffic-backend-api.onrender.com";
import React, { useState, useEffect, useRef } from 'react';
import { 
    Box, Grid, Paper, Typography, LinearProgress, 
    Button, CircularProgress, Alert, Chip, IconButton, Stack, Divider
} from '@mui/material';
import { 
    Traffic as TrafficIcon,
    CloudSync as SyncIcon,
    CloudOff as OfflineIcon,
    Speed as SpeedIcon,
    Bolt as FlashIcon,
    Block as BlockIcon,
    ClearAll as ClearIcon,
    SwapVert, SwapHoriz,
    LocalHospital as AmbulanceIcon,
    LocalFireDepartment as FireIcon,
    LocalPolice as PoliceIcon,
    Warning as WarningIcon
} from '@mui/icons-material';
import TrafficMap from './TrafficMap';
import axios from 'axios';

// --- RENK TEMASI ---
const THEME = {
    bg: '#0b0f19',
    panel: '#151b2b',
    border: '#2a3449',
    green: '#00e676',
    red: '#ff1744',
    text: '#eceff1',
    blue: '#2979ff',
    warning: '#ff9100',
    emergencyBg: 'rgba(255, 23, 68, 0.15)'
};

export default function TrafficSimulationContainer() {
    
    // --- STATE ---
    const [timeLeft, setTimeLeft] = useState(0); 
    const [activeDirection, setActiveDirection] = useState('NS'); 
    const [status, setStatus] = useState('INITIALIZING'); 
    const [isGapOutTriggered, setIsGapOutTriggered] = useState(false);

    // ACİL DURUM
    const [emergencyType, setEmergencyType] = useState(null); 
    const [emergencyVehiclePos, setEmergencyVehiclePos] = useState(-20); 

    // Sensör Verileri
    const [sensorData, setSensorData] = useState({
        north: 45, south: 40, east: 25, west: 20
    });

    const [logs, setLogs] = useState([]);
    const logContainerRef = useRef(null);

    const addLog = (msg, type = 'INFO') => {
        const time = new Date().toLocaleTimeString('tr-TR');
        setLogs(prev => [...prev.slice(-12), { time, msg, type }]);
    };

    // --- 1. SAYAÇ MOTORU ---
    useEffect(() => {
        let interval = null;

        if (status === 'COUNTING' && timeLeft > 0) {
            interval = setInterval(() => {
                
                // Gap-Out Kontrolü (Sadece Normal Modda)
                if (!emergencyType) {
                    const currentDensity = activeDirection === 'NS' 
                        ? Math.max(sensorData.north, sensorData.south) 
                        : Math.max(sensorData.east, sensorData.west);

                    if (timeLeft > 6 && currentDensity < 10 && !isGapOutTriggered) {
                        setIsGapOutTriggered(true);
                        addLog(`AKILLI KESME: Yol boşaldı, süre iptal ediliyor!`, "GAP-OUT");
                        setTimeLeft(4); 
                        return; 
                    }
                }

                setTimeLeft((prev) => prev - 1);
                
                // Trafik Fiziğini Çalıştır
                simulateTrafficFlow(activeDirection);

            }, 1000);

        } else if (timeLeft === 0 && status === 'COUNTING') {
            if (emergencyType) endEmergency();
            else askBackendForNextPhase();
        }

        return () => clearInterval(interval);
    }, [timeLeft, status, sensorData, activeDirection, isGapOutTriggered, emergencyType]);


    // --- 2. GELİŞMİŞ TRAFİK FİZİĞİ (Tıkanıklık Simülasyonu) ---
    const simulateTrafficFlow = (greenDir) => {
        setSensorData(prev => {
            let newData = { ...prev };

            // --- ACİL DURUM FİZİĞİ ---
            if (emergencyType) {
                // YEŞİL OLAN YÖN (Ambulansın Yönü):
                // Araçlar akmaya devam eder ama tamamen boşalmaz (Arkasından gelenler)
                if (greenDir === 'NS') {
                    newData.north = Math.max(20, prev.north - 2 + Math.random() * 5); // Hafif akış
                    newData.south = Math.max(20, prev.south - 2 + Math.random() * 5);
                    
                    // KIRMIZI OLAN YÖN (Yan Yollar):
                    // FELAKET! Trafik hızla birikir (%100'e koşar)
                    newData.east = Math.min(100, prev.east + 5 + Math.random() * 5); // Hızlı artış
                    newData.west = Math.min(100, prev.west + 5 + Math.random() * 5);
                } else {
                    newData.east = Math.max(20, prev.east - 2 + Math.random() * 5);
                    newData.west = Math.max(20, prev.west - 2 + Math.random() * 5);
                    newData.north = Math.min(100, prev.north + 5 + Math.random() * 5);
                    newData.south = Math.min(100, prev.south + 5 + Math.random() * 5);
                }
            } 
            // --- NORMAL MOD FİZİĞİ ---
            else {
                if (greenDir === 'NS') {
                    newData.north = Math.max(0, prev.north - 5 + Math.floor(Math.random() * 2));
                    newData.south = Math.max(0, prev.south - 5 + Math.floor(Math.random() * 2));
                    newData.east = Math.min(100, prev.east + 3 + Math.floor(Math.random() * 3));
                    newData.west = Math.min(100, prev.west + 3 + Math.floor(Math.random() * 3));
                } else {
                    newData.east = Math.max(0, prev.east - 5 + Math.floor(Math.random() * 2));
                    newData.west = Math.max(0, prev.west - 5 + Math.floor(Math.random() * 2));
                    newData.north = Math.min(100, prev.north + 3 + Math.floor(Math.random() * 3));
                    newData.south = Math.min(100, prev.south + 3 + Math.floor(Math.random() * 3));
                }
            }
            return newData;
        });
    };


    // --- 3. ACİL DURUM ANİMASYONU ---
    useEffect(() => {
        let animInterval = null;
        if (emergencyType) {
            animInterval = setInterval(() => {
                setEmergencyVehiclePos(prev => {
                    if (prev > 120) return -20; 
                    return prev + 1.2; // Biraz daha ağır geçsin, heybetli olsun
                });
            }, 50);
        } else {
            setEmergencyVehiclePos(-20);
        }
        return () => clearInterval(animInterval);
    }, [emergencyType]);


    // --- 4. BACKEND TETİKLEYİCİLER ---
    const triggerEmergency = async (type) => {
        if (emergencyType) return;

        let endpoint = "";
        let direction = "NS"; 
        
        if (type === 'AMBULANCE') { endpoint = "test/ambulance"; direction = "NS"; }
        else if (type === 'FIRE') { endpoint = "test/firetruck"; direction = "NS"; }
        else if (type === 'POLICE') { endpoint = "test/police"; direction = "EW"; }

        addLog(`ACİL SİNYAL: ${type} yaklaşıyor! Yan yollar kilitleniyor...`, "ALERT");
        setStatus('WAITING_BACKEND');

        try {
            const token = localStorage.getItem('token');
            const response = await axios.post(`${API_URL}/api/emergency/${endpoint}`, {}, {
                headers: { Authorization: `Bearer ${token}` }
            });

            if (response.data && response.data.success) {
                setEmergencyType(type);
                setActiveDirection(direction);
                setTimeLeft(60); 
                setStatus('COUNTING');
                
                // Görsel olarak başlangıç değerlerini ayarla (Yan yollar biraz dolu başlasın)
                setSensorData(prev => ({
                    ...prev,
                    [direction === 'NS' ? 'east' : 'north']: 60, // Yan yol hemen şişmeye başlasın
                    [direction === 'NS' ? 'west' : 'south']: 60
                }));
                
                addLog(`YEŞİL DALGA AKTİF! Diğer yönlerde kuyruk oluşuyor.`, "EMERGENCY");
            }

        } catch (error) {
            console.error(error);
            addLog("Acil durum hatası!", "ERR");
            setStatus('COUNTING');
        }
    };

    const endEmergency = () => {
        setEmergencyType(null);
        addLog("Acil durum bitti. Trafik normal seyrine dönüyor.", "INFO");
        askBackendForNextPhase();
    };

    // --- 5. NORMAL BACKEND DÖNGÜSÜ ---
    const askBackendForNextPhase = async () => {
        setStatus('WAITING_BACKEND');
        setIsGapOutTriggered(false); 
        
        const nextDirection = activeDirection === 'NS' ? 'EW' : 'NS';
        const targetApiDirection = nextDirection === 'NS' ? 'NORTH' : 'EAST';
        const waitingDensity = nextDirection === 'NS' ? (sensorData.north + sensorData.south) : (sensorData.east + sensorData.west);

        addLog(`Backend Analizi: ${targetApiDirection} yönü için süre hesaplanıyor...`, "REQ");

        try {
            const token = localStorage.getItem('token');
            const response = await axios.post(`${API_URL}/api/optimization/apply`, {
                intersectionId: 1,
                vehicleCount: waitingDensity, 
                averageSpeed: Math.max(10, 60 - (waitingDensity / 2)),
                direction: targetApiDirection
            }, {
                headers: { Authorization: `Bearer ${token}` }
            });

            if (response.data && response.data.success) {
                const aiDuration = response.data.details.newGreenDuration;
                setActiveDirection(nextDirection);
                setTimeLeft(aiDuration); 
                setStatus('COUNTING');
                addLog(`Backend Onayı: ${aiDuration} sn tanımlandı.`, "SUCCESS");
            }
        } catch (error) {
            setStatus('ERROR');
        }
    };

    useEffect(() => { if (status === 'INITIALIZING') askBackendForNextPhase(); }, []);
    useEffect(() => { if (logContainerRef.current) logContainerRef.current.scrollTop = logContainerRef.current.scrollHeight; }, [logs]);


    // --- UI RENDER ---
    return (
        <Box sx={{ height: '100%', width: '100%', bgcolor: THEME.bg, color: THEME.text, p: 2, overflow: 'hidden' }}>
            
            {/* ÜST PANEL */}
            <Paper sx={{ 
                p: 2, mb: 2, 
                bgcolor: emergencyType ? THEME.emergencyBg : THEME.panel, 
                border: emergencyType ? `2px solid ${THEME.red}` : `1px solid ${THEME.border}`,
                display: 'flex', justifyContent: 'space-between', alignItems: 'center',
                animation: emergencyType ? 'pulse-border 1s infinite' : 'none'
            }}>
                <Box display="flex" alignItems="center" gap={2}>
                    {emergencyType ? <WarningIcon sx={{ color: THEME.red, fontSize: 36, animation: 'spin 2s infinite' }} /> : <TrafficIcon sx={{ color: THEME.blue, fontSize: 32 }} />}
                    <Box>
                        <Typography variant="h6" fontWeight="bold" sx={{ color: emergencyType ? THEME.red : '#fff' }}>
                            {emergencyType ? '⚠️ ACİL DURUM PROTOKOLÜ' : 'SMART COMMAND CENTER'}
                        </Typography>
                        <Typography variant="caption" sx={{ color: '#889eb5' }}>
                           {emergencyType ? 'YAN YOLLAR DURDURULDU - KUYRUK OLUŞUYOR' : 'AI SYSTEM V3.2'}
                        </Typography>
                    </Box>
                </Box>

                <Box sx={{ 
                    display: 'flex', alignItems: 'center', gap: 4, 
                    bgcolor: 'rgba(0,0,0,0.3)', px: 4, py: 1, borderRadius: 3, 
                    border: `1px solid ${emergencyType ? THEME.red : THEME.border}` 
                }}>
                    <Box textAlign="center">
                        <Typography variant="caption" sx={{ color: '#888' }}>AKTİF HAT</Typography>
                        <Typography variant="body1" fontWeight="bold" sx={{ color: activeDirection === 'NS' ? THEME.green : THEME.blue }}>
                            {activeDirection === 'NS' ? '↕ KUZEY-GÜNEY' : '↔ DOĞU-BATI'}
                        </Typography>
                    </Box>
                    <Divider orientation="vertical" flexItem sx={{ bgcolor: '#444' }} />
                    <Box textAlign="center" sx={{ minWidth: '100px' }}>
                        <Typography variant="caption" sx={{ color: '#888' }}>SÜRE</Typography>
                        <Typography variant="h3" fontWeight="bold" sx={{ fontFamily: 'monospace', lineHeight: 1, color: emergencyType ? THEME.red : '#fff' }}>
                            {timeLeft}s
                        </Typography>
                    </Box>
                </Box>
                
                <Box sx={{ minWidth: '200px', textAlign: 'right' }}>
                    {emergencyType && <Chip label="ÖNCELİK MODU" color="error" sx={{ fontWeight: 'bold' }} />}
                    {!emergencyType && status === 'COUNTING' && <Chip icon={<SyncIcon />} label="Sistem Normal" color="success" variant="outlined" />}
                </Box>
            </Paper>

            <Grid container spacing={2} sx={{ height: 'calc(100% - 100px)' }}>
                
                {/* SOL: KONTROLLER */}
                <Grid item xs={12} md={3}>
                    <Paper sx={{ p: 2, height: '100%', bgcolor: THEME.panel, border: `1px solid ${THEME.border}`, display: 'flex', flexDirection: 'column' }}>
                        <Typography variant="subtitle2" sx={{ color: THEME.red, mb: 2, fontWeight: 'bold' }}>🚨 ACİL MÜDAHALE</Typography>
                        <Stack spacing={1} sx={{ mb: 3 }}>
                            <Button variant="contained" color="error" startIcon={<AmbulanceIcon />} onClick={() => triggerEmergency('AMBULANCE')} disabled={!!emergencyType}>AMBULANS (NS)</Button>
                            <Button variant="contained" color="warning" startIcon={<FireIcon />} onClick={() => triggerEmergency('FIRE')} disabled={!!emergencyType}>İTFAİYE (NS)</Button>
                            <Button variant="contained" sx={{ bgcolor: '#1a237e' }} startIcon={<PoliceIcon />} onClick={() => triggerEmergency('POLICE')} disabled={!!emergencyType}>POLİS (EW)</Button>
                        </Stack>

                        <Typography variant="subtitle2" sx={{ color: THEME.blue, mb: 1 }}>TEST ARAÇLARI</Typography>
                        <Stack spacing={1} direction="row" sx={{ mb: 3 }}>
                            <Button fullWidth size="small" variant="outlined" color="error" onClick={() => setSensorData({ north: 95, south: 90, east: 85, west: 80 })}>TIKA</Button>
                            <Button fullWidth size="small" variant="outlined" color="info" onClick={() => setSensorData({ north: 5, south: 5, east: 5, west: 5 })}>BOŞALT</Button>
                        </Stack>

                        <Box sx={{ flex: 1, overflowY: 'auto' }}>
                            <SensorDisplay label="Kuzey (N)" value={sensorData.north} active={activeDirection === 'NS'} />
                            <SensorDisplay label="Güney (S)" value={sensorData.south} active={activeDirection === 'NS'} />
                            <SensorDisplay label="Doğu (E)" value={sensorData.east} active={activeDirection === 'EW'} />
                            <SensorDisplay label="Batı (W)" value={sensorData.west} active={activeDirection === 'EW'} />
                        </Box>
                    </Paper>
                </Grid>

                {/* ORTA: HARİTA */}
                <Grid item xs={12} md={6}>
                    <Box sx={{ position: 'relative', height: '100%', bgcolor: '#000', borderRadius: 2, overflow: 'hidden', border: `1px solid ${THEME.border}` }}>
                        <TrafficMap 
                            lights={{ NS: activeDirection === 'NS' ? 'green' : 'red', EW: activeDirection === 'EW' ? 'green' : 'red' }}
                            // ÖNEMLİ: Acil durumda araçları silme (1.5), aksine akıtmaya devam et!
                            spawnRate={status === 'COUNTING' ? 2.0 : 0} 
                        />
                        
                        {/* ARAÇ ANİMASYONU */}
                        {emergencyType && (
                            <Box sx={{ 
                                position: 'absolute', 
                                left: activeDirection === 'NS' ? '48%' : `${emergencyVehiclePos}%`, 
                                top: activeDirection === 'NS' ? `${emergencyVehiclePos}%` : '48%',
                                transform: activeDirection === 'NS' ? 'translateX(-50%) scale(1.5)' : 'translateY(-50%) rotate(-90deg) scale(1.5)',
                                zIndex: 99, transition: 'none' 
                            }}>
                                <Box sx={{ 
                                    p: 1, borderRadius: '50%', 
                                    bgcolor: emergencyType === 'POLICE' ? '#1a237e' : THEME.red,
                                    boxShadow: '0 0 30px 10px rgba(255, 0, 0, 0.9)', // Daha büyük glow
                                    animation: 'pulse-light 0.3s infinite alternate' // Hızlı çakar
                                }}>
                                    {emergencyType === 'AMBULANCE' && <AmbulanceIcon sx={{ color: '#fff', fontSize: 40 }} />}
                                    {emergencyType === 'FIRE' && <FireIcon sx={{ color: '#fff', fontSize: 40 }} />}
                                    {emergencyType === 'POLICE' && <PoliceIcon sx={{ color: '#fff', fontSize: 40 }} />}
                                </Box>
                            </Box>
                        )}
                    </Box>
                </Grid>

                {/* SAĞ: LOGLAR */}
                <Grid item xs={12} md={3}>
                    <Paper sx={{ p: 0, height: '100%', bgcolor: '#0f121a', border: `1px solid ${THEME.border}`, display: 'flex', flexDirection: 'column' }}>
                        <Box sx={{ p: 1.5, borderBottom: '1px solid #333', bgcolor: '#1a1f2e' }}>
                            <Typography variant="subtitle2" sx={{ fontFamily: 'monospace', color: THEME.green }}>system@traffic-ai:~$ logs</Typography>
                        </Box>
                        <Box ref={logContainerRef} sx={{ p: 2, flex: 1, overflowY: 'auto', fontFamily: 'Fira Code, monospace', fontSize: '0.8rem' }}>
                            {logs.map((log, i) => (
                                <Box key={i} sx={{ mb: 1, borderLeft: `2px solid ${getLogColor(log.type)}`, pl: 1 }}>
                                    <span style={{ color: '#666', fontSize: '0.75rem' }}>[{log.time}]</span><br/>
                                    <span style={{ color: getLogColor(log.type), fontWeight: 'bold' }}>{log.type}</span>
                                    <span style={{ color: '#ccc', marginLeft: '8px' }}>{log.msg}</span>
                                </Box>
                            ))}
                        </Box>
                    </Paper>
                </Grid>

            </Grid>

            <style>{`
                @keyframes pulse-light { from { opacity: 1; transform: scale(1); } to { opacity: 0.7; transform: scale(1.1); } }
                @keyframes spin { from { transform: rotate(0deg); } to { transform: rotate(360deg); } }
                @keyframes pulse-border { 0% { border-color: ${THEME.red}; } 50% { border-color: transparent; } 100% { border-color: ${THEME.red}; } }
            `}</style>
        </Box>
    );
}

const getLogColor = (type) => {
    if (type === 'EMERGENCY' || type === 'ALERT') return THEME.red;
    if (type === 'GAP-OUT') return THEME.warning;
    if (type === 'SUCCESS') return THEME.green;
    return THEME.blue;
};

const SensorDisplay = ({ label, value, active }) => (
    <Box sx={{ mb: 2, opacity: active ? 1 : 0.5 }}>
        <Box display="flex" justifyContent="space-between" mb={0.5}>
            <Box display="flex" alignItems="center" gap={1}>
                {active && <SpeedIcon sx={{ fontSize: 14, color: THEME.green }} />}
                <Typography variant="caption" sx={{ color: active ? '#fff' : '#888' }}>{label}</Typography>
            </Box>
            <Typography variant="caption" sx={{ color: '#fff' }}>%{Math.floor(value)}</Typography>
        </Box>
        <LinearProgress variant="determinate" value={value} sx={{ height: 6, borderRadius: 3, bgcolor: '#333', '& .MuiLinearProgress-bar': { bgcolor: value > 80 ? THEME.red : THEME.blue } }} />
    </Box>
);