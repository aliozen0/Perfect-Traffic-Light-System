import React from 'react';
import { Grid, Card, CardHeader, CardContent, Divider, TextField, FormControl, InputLabel, Select, MenuItem, Chip, Typography, Button, ListSubheader, Tooltip, IconButton } from '@mui/material';
import { Map as MapIcon, Router as RouterIcon, Tune as TuneIcon, Download as DownloadIcon, Info as InfoIcon } from '@mui/icons-material';
import { RadioGroup, FormControlLabel, Radio } from '@mui/material';

export default function GeneralSettingsTab({ 
  isReadOnly, intersectionType, setIntersectionType, 
  controllerIp, setControllerIp, controlMode, setControlMode, 
  onDownload, configJSON 
}) {
  return (
    <Grid container spacing={3}>
      <Grid item xs={12} md={6}>
        <Card sx={{mb: 3}}>
          <CardHeader title="Lokasyon & Kimlik" subheader="Fiziksel Konum Bilgileri" avatar={<MapIcon color="primary"/>} />
          <Divider />
          <CardContent>
            <TextField disabled={isReadOnly} fullWidth label="Kavşak Adı" margin="dense" size="small" defaultValue="Beşevler Meydan" />
            <TextField disabled={isReadOnly} fullWidth label="Koordinat" margin="dense" size="small" defaultValue="39.9355, 32.8597" />
            <FormControl fullWidth margin="dense" size="small" disabled={isReadOnly}>
                <InputLabel>Altyapı Tipi (Topology)</InputLabel>
                <Select value={intersectionType} label="Altyapı Tipi (Topology)" onChange={(e) => setIntersectionType(e.target.value)}>
                    <ListSubheader>Araç Kavşakları</ListSubheader>
                    <MenuItem value="4-way">4 Kollu Kavşak (Standart)</MenuItem>
                    <MenuItem value="t-junction">T-Kavşak</MenuItem>
                    <MenuItem value="roundabout">Döner Kavşak (Roundabout)</MenuItem>
                    <MenuItem value="highway-merge">Otoyol Katılımı (Y-Kavşak)</MenuItem>
                    
                    <ListSubheader>Yaya & Bisiklet</ListSubheader>
                    <MenuItem value="pedestrian-crossing">Sinyalize Yaya Geçidi</MenuItem>
                    <MenuItem value="pedestrian-overpass">Yaya Üst Geçidi</MenuItem>
                    
                    <ListSubheader>Toplu Taşıma & Raylı Sistem</ListSubheader>
                    <MenuItem value="tram-crossing">Tramvay Kesişimi</MenuItem>
                    <MenuItem value="metro-station">Metro İstasyonu Entegre</MenuItem>
                    <MenuItem value="bus-terminal">Otobüs Terminal Çıkışı</MenuItem>
                    
                    <ListSubheader>Özel Yapılar</ListSubheader>
                    <MenuItem value="tunnel-entrance">Tünel / Alt Geçit Girişi</MenuItem>
                    <MenuItem value="bridge">Köprü Geçişi</MenuItem>
                </Select>
            </FormControl>
          </CardContent>
        </Card>
        <Card>
          <CardHeader 
            title="Donanım & Ağ Bağlantısı" 
            subheader="Controller Box Settings" 
            avatar={<RouterIcon color="primary"/>} 
            action={
              <Tooltip title="Bu ayarlar, sahadaki fiziksel trafik kontrol cihazı ile bağlantı kurmak içindir. Şu an simülasyon modunda olduğumuz için sadece konfigürasyon amaçlıdır.">
                <IconButton size="small"><InfoIcon fontSize="small" /></IconButton>
              </Tooltip>
            }
          />
          <Divider />
          <CardContent>
            <Grid container spacing={2}>
                <Grid item xs={8}><TextField disabled={isReadOnly} fullWidth label="Controller IP Adresi" size="small" value={controllerIp} onChange={(e) => setControllerIp(e.target.value)} /></Grid>
                <Grid item xs={4}><TextField disabled={isReadOnly} fullWidth label="Port" size="small" defaultValue="8080" /></Grid>
                <Grid item xs={12}>
                  <Chip label="BAĞLANTI: ONLİNE (SİMÜLASYON)" color="success" size="small" variant="outlined" sx={{width: '100%'}}/>
                  <Typography variant="caption" color="text.secondary" sx={{mt:1, display:'block', textAlign:'center'}}>
                    Fiziksel cihaz bağlantısı aktif değil. Veriler bulut üzerinden senkronize ediliyor.
                  </Typography>
                </Grid>
            </Grid>
          </CardContent>
        </Card>
      </Grid>

      <Grid item xs={12} md={6}>
         <Card sx={{ mb: 3 }}>
            <CardHeader title="Operasyon Modu" subheader="Sinyal Kontrol Stratejisi" avatar={<TuneIcon color="secondary"/>} />
            <Divider />
            <CardContent>
                <FormControl component="fieldset" disabled={isReadOnly}>
                    <RadioGroup value={controlMode} onChange={(e) => setControlMode(e.target.value)}>
                        <FormControlLabel value="adaptive-ai" control={<Radio />} label="Adaptive AI (Akıllı Mod)" />
                        <Typography variant="caption" color="text.secondary" sx={{ml: 4, mb: 1, display:'block'}}>
                          Trafik yoğunluğuna göre yeşil ışık sürelerini otomatik ayarlar.
                        </Typography>
                        
                        <FormControlLabel value="fixed-time" control={<Radio />} label="Fixed Time (Sabit Süreli)" />
                        <Typography variant="caption" color="text.secondary" sx={{ml: 4, mb: 1, display:'block'}}>
                          Önceden belirlenmiş sabit süreleri kullanır (Geleneksel).
                        </Typography>

                        <FormControlLabel value="manual" control={<Radio />} label="Manuel Override" />
                        <Typography variant="caption" color="text.secondary" sx={{ml: 4, mb: 1, display:'block'}}>
                          Otomasyonu devre dışı bırakır, operatör müdahalesi gerektirir.
                        </Typography>
                    </RadioGroup>
                </FormControl>
            </CardContent>
         </Card>
         <Card sx={{ bgcolor: '#1e293b', color: '#f8fafc', borderRadius: 2, border: '1px solid #334155' }}>
          <CardHeader 
            title={<Typography variant="subtitle2" sx={{ fontFamily: 'Monaco, Consolas, monospace', color: '#60a5fa' }}>CANLI KONFİGÜRASYON ÇIKTISI</Typography>}
            action={<Button variant="outlined" size="small" startIcon={<DownloadIcon />} onClick={onDownload} sx={{ color: '#60a5fa', borderColor: 'rgba(96, 165, 250, 0.5)', '&:hover': { borderColor: '#60a5fa', bgcolor: 'rgba(96, 165, 250, 0.1)' }}}>JSON İNDİR</Button>}
            sx={{ pb: 1 }}
          />
          <Divider sx={{bgcolor: '#334155'}} />
          <CardContent sx={{ pt: 1, pb: 1, maxHeight: '300px', overflow: 'auto', '&::-webkit-scrollbar': { width: '8px' }, '&::-webkit-scrollbar-thumb': { backgroundColor: '#475569', borderRadius: '4px' } }}>
            <Typography variant="body2" component="pre" sx={{ fontFamily: 'Monaco, Consolas, monospace', fontSize: '11px', whiteSpace: 'pre-wrap', color: '#cbd5e1', m: 0 }}>
              {configJSON}
            </Typography>
          </CardContent>
         </Card>
      </Grid>
    </Grid>
  );
}