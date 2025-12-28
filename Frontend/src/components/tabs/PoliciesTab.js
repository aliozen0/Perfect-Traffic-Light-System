/*
  Dosya: src/components/tabs/PoliciesTab.js
  Yapılan Değişiklik:
  - Mevcut tasarım (Priority, Eco, Enforcement) KORUNDU.
  - En tepeye "Varsayılan Kuralları Yükle" (Rule Engine Loader) paneli eklendi.
  - Gerekli Axios ve State mantıkları entegre edildi.
*/
const API_URL = "https://traffic-backend-api.onrender.com";
import React, { useState } from 'react';
import { 
  Grid, Card, CardHeader, CardContent, Typography, FormControlLabel, Switch, 
  Divider, Box, Slider, Chip, Select, MenuItem, FormControl, InputLabel, 
  Button, Alert, CircularProgress, Stack 
} from '@mui/material';
import { 
  LocalPolice as PoliceIcon, 
  DirectionsBus as BusIcon, 
  AccessTime as TimeIcon, 
  NaturePeople as EcoIcon, 
  PedalBike as BikeIcon,
  Speed as SpeedIcon,
  Storage as StorageIcon,
  CheckCircle as CheckCircleIcon,
  Error as ErrorIcon,
  AutoFixHigh as AutoFixHighIcon
} from '@mui/icons-material';
import axios from 'axios';

export default function PoliciesTab({ 
  isReadOnly, 
  emergencyMode, setEmergencyMode, 
  busPriority, setBusPriority,
  rushHourMode, setRushHourMode,
  ecoMode, setEcoMode,
  pedestrianLPI, setPedestrianLPI,
  enforcementLevel, setEnforcementLevel
}) {
  
  // --- YENİ EKLENEN KISIM: KURAL YÜKLEME MANTIĞI ---
  const [loading, setLoading] = useState(false);
  const [status, setStatus] = useState(null); // 'success' | 'error' | null
  const [message, setMessage] = useState("");

  const handleCreateDefaults = async () => {
      setLoading(true);
      setStatus(null);
      setMessage("");

      try {
          const token = localStorage.getItem('token');
          // Backend'deki "Create Defaults" endpoint'ine istek atıyoruz
          const response = await axios.post(
              '${API_URL}/api/optimization/rules/create-defaults',
              {}, 
              {
                  headers: { 
                      'Authorization': `Bearer ${token}`,
                      'Content-Type': 'application/json'
                  }
              }
          );

          setStatus('success');
          setMessage(`Başarılı: ${response.data.message || "Varsayılan kurallar veritabanına yüklendi."}`);
          
      } catch (error) {
          console.error("Kural Yükleme Hatası:", error);
          const errorMsg = error.response?.data?.message || error.message || "Sunucuya bağlanılamadı.";
          
          if(errorMsg.includes("already exists") || error.response?.status === 409) {
              setStatus('warning');
              setMessage("Bilgi: Varsayılan kurallar sistemde zaten mevcut.");
          } else {
              setStatus('error');
              setMessage(`Hata: ${errorMsg}`);
          }
      } finally {
          setLoading(false);
      }
  };
  // ---------------------------------------------------

  return (
    <Grid container spacing={3}>
      
      {/* --- YENİ BÖLÜM: SİSTEM BAŞLANGIÇ AYARLARI (RULE ENGINE) --- */}
      <Grid item xs={12}>
        <Card sx={{ bgcolor: '#1e1e1e', color: 'white', border: '1px solid #333' }}>
            <CardContent>
                <Box display="flex" alignItems="center" justifyContent="space-between">
                    <Box display="flex" alignItems="center" gap={2}>
                        <AutoFixHighIcon sx={{ fontSize: 32, color: '#29b6f6' }} />
                        <Box>
                            <Typography variant="h6" fontWeight="bold">
                                Optimizasyon Motoru Kurulumu
                            </Typography>
                            <Typography variant="caption" sx={{ color: '#aaa' }}>
                                CURL komutu kullanmadan varsayılan yapay zeka kurallarını buradan yükleyebilirsiniz.
                            </Typography>
                        </Box>
                    </Box>
                    
                    <Button
                        variant="contained"
                        onClick={handleCreateDefaults}
                        disabled={loading}
                        startIcon={loading ? <CircularProgress size={20} color="inherit" /> : <StorageIcon />}
                        sx={{ bgcolor: '#29b6f6', '&:hover': { bgcolor: '#0288d1' } }}
                    >
                        {loading ? "Yükleniyor..." : "Varsayılan Kuralları Yükle"}
                    </Button>
                </Box>

                {/* Bildirim Alanı */}
                {message && (
                    <Box sx={{ mt: 2 }}>
                        <Alert 
                            severity={status === 'success' ? 'success' : status === 'warning' ? 'warning' : 'error'}
                            icon={status === 'success' ? <CheckCircleIcon fontSize="inherit" /> : <ErrorIcon fontSize="inherit" />}
                            sx={{ borderRadius: 2 }}
                        >
                            {message}
                        </Alert>
                    </Box>
                )}
            </CardContent>
        </Card>
      </Grid>
      
      {/* ---------------- MEVCUT KODLARINIZ (AYNEN KORUNDU) ---------------- */}

      {/* 1. ÖNCELİK YÖNETİMİ (PRIORITY MANAGEMENT) */}
      <Grid item xs={12} md={6}>
        <Card sx={{ height: '100%', borderLeft: '6px solid #ff9800' }}>
          <CardHeader 
            avatar={<PoliceIcon color="warning" fontSize="large"/>}
            title="Öncelik Yönetimi" 
            subheader="Acil Durum ve VIP Protokolleri"
          />
          <Divider />
          <CardContent>
            <Box sx={{ mb: 3, p: 2, bgcolor: '#fff3e0', borderRadius: 2 }}>
              <Typography variant="subtitle2" fontWeight="bold" gutterBottom>🚑 Acil Durum (EVP)</Typography>
              <FormControlLabel 
                  control={<Switch checked={emergencyMode} onChange={(e) => setEmergencyMode(e.target.checked)} color="error" disabled={isReadOnly} />} 
                  label={emergencyMode ? <Chip label="AKTİF: Tüm Işıklar Kırmızı" color="error" size="small"/> : "Pasif"} 
              />
              <Typography variant="caption" display="block" color="text.secondary" mt={1}>
                Ambulans/İtfaiye yaklaştığında kavşağı boşaltır.
              </Typography>
            </Box>

            <Box sx={{ mb: 2 }}>
              <Typography variant="subtitle2" fontWeight="bold" gutterBottom>🚌 Toplu Taşıma Önceliği (TSP)</Typography>
              <Box display="flex" alignItems="center" justifyContent="space-between">
                <FormControlLabel 
                    control={<Switch checked={busPriority} onChange={(e) => setBusPriority(e.target.checked)} color="primary" disabled={isReadOnly} />} 
                    label="Otobüs/Tramvay Önceliği" 
                />
                <Chip label={busPriority ? "High Priority" : "Low Priority"} variant="outlined" size="small" color={busPriority ? "primary" : "default"} />
              </Box>
              <Box sx={{ mt: 2, px: 1 }}>
                <Typography variant="caption">Maksimum Yeşil Uzatma (sn)</Typography>
                <Slider 
                  defaultValue={15} step={5} min={5} max={60} valueLabelDisplay="auto" 
                  disabled={!busPriority || isReadOnly} 
                />
              </Box>
            </Box>
          </CardContent>
        </Card>
      </Grid>

      {/* 2. ZAMAN VE ÇEVRE (TIME & ECO) */}
      <Grid item xs={12} md={6}>
        <Card sx={{ height: '100%', borderLeft: '6px solid #4caf50' }}>
          <CardHeader 
            avatar={<EcoIcon color="success" fontSize="large"/>}
            title="Çevre ve Zaman Kuralları" 
            subheader="Sürdürülebilir Trafik Yönetimi"
          />
          <Divider />
          <CardContent>
            
            {/* Rush Hour */}
            <Box display="flex" alignItems="center" justifyContent="space-between" mb={3}>
              <Box display="flex" alignItems="center">
                <TimeIcon color="action" sx={{ mr: 1 }} />
                <Box>
                  <Typography variant="subtitle2">Zirve Saat Modu (Rush Hour)</Typography>
                  <Typography variant="caption" color="text.secondary">07:00-09:00 & 17:00-19:00</Typography>
                </Box>
              </Box>
              <Switch checked={rushHourMode} onChange={(e) => setRushHourMode(e.target.checked)} disabled={isReadOnly} />
            </Box>

            {/* Eco Mode */}
            <Box display="flex" alignItems="center" justifyContent="space-between" mb={3}>
              <Box display="flex" alignItems="center">
                <NaturePeopleIconWrapper />
                <Box>
                  <Typography variant="subtitle2">Hava Kalitesi Kontrolü</Typography>
                  <Typography variant="caption" color="text.secondary">Kirlilik artarsa dur-kalkı azalt.</Typography>
                </Box>
              </Box>
              <Switch checked={ecoMode} onChange={(e) => setEcoMode(e.target.checked)} color="success" disabled={isReadOnly} />
            </Box>

            <Divider sx={{ my: 2 }} />

            {/* Yaya & Bisiklet */}
            <Typography variant="subtitle2" gutterBottom><BikeIcon fontSize="small" sx={{verticalAlign:'middle', mr:1}}/> Yaya ve Bisiklet Dostu</Typography>
            <Box sx={{ px: 1 }}>
               <Typography variant="caption">Yaya Erken Başlangıç (LPI) - {pedestrianLPI} sn</Typography>
               <Slider 
                 value={pedestrianLPI} onChange={(e, v) => setPedestrianLPI(v)} 
                 min={0} max={10} step={1} marks 
                 disabled={isReadOnly}
                 size="small"
               />
               <FormControlLabel control={<Switch disabled={isReadOnly}/>} label="Bisiklet Yeşil Dalga (Green Wave)" />
            </Box>

          </CardContent>
        </Card>
      </Grid>

      {/* 3. DENETİM (ENFORCEMENT) */}
      <Grid item xs={12}>
        <Card sx={{ borderLeft: '6px solid #d32f2f' }}>
          <CardContent>
            <Box display="flex" alignItems="center" mb={2}>
              <SpeedIcon color="error" sx={{ mr: 1 }} />
              <Typography variant="h6">Denetim ve Ceza Sistemleri (EDS)</Typography>
            </Box>
            <Grid container spacing={2} alignItems="center">
              <Grid item xs={12} md={4}>
                <FormControl fullWidth size="small" disabled={isReadOnly}>
                  <InputLabel>Denetim Seviyesi</InputLabel>
                  <Select value={enforcementLevel} label="Denetim Seviyesi" onChange={(e) => setEnforcementLevel(e.target.value)}>
                    <MenuItem value="strict">Sıkı (Strict) - 0 Tolerans</MenuItem>
                    <MenuItem value="moderate">Orta (Moderate) - %10 Tolerans</MenuItem>
                    <MenuItem value="relaxed">Esnek (Relaxed)</MenuItem>
                  </Select>
                </FormControl>
              </Grid>
              <Grid item xs={12} md={8}>
                <Box display="flex" gap={2}>
                  <FormControlLabel control={<Switch defaultChecked disabled={isReadOnly} color="error"/>} label="Kırmızı Işık Kamerası" />
                  <FormControlLabel control={<Switch defaultChecked disabled={isReadOnly} color="error"/>} label="Hız Koridoru" />
                  <FormControlLabel control={<Switch disabled={isReadOnly}/>} label="Emniyet Şeridi İhlal" />
                </Box>
              </Grid>
            </Grid>
          </CardContent>
        </Card>
      </Grid>

    </Grid>
  );
}

// İkon Wrapper
const NaturePeopleIconWrapper = () => <EcoIcon color="success" sx={{ mr: 1 }} />;