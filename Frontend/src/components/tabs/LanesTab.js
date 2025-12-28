import React, { useState } from 'react';
import { 
  Grid, Card, CardContent, Box, Typography, Button, Divider, List, ListItem, ListItemIcon, ListItemText, 
  Chip, Alert, TextField, Slider, Paper, IconButton, Tooltip, Dialog, DialogTitle, DialogContent, 
  DialogActions, FormControl, InputLabel, Select, MenuItem 
} from '@mui/material';
import { 
  AddCircle as AddCircleIcon, AccessTime as AccessTimeIcon, DirectionsCar, DirectionsBus, 
  Tram, Edit as EditIcon, Delete as DeleteIcon, ArrowUpward, ArrowDownward, ArrowForward, ArrowBack 
} from '@mui/icons-material';
import { initialLanes } from '../../data/laneData';

// Basit bir Sinyal Zamanlama Çizelgesi (Gantt Style)
const SignalTimingChart = ({ phases, totalCycle }) => {
  return (
    <Box sx={{ mt: 2, mb: 4 }}>
      <Typography variant="subtitle2" gutterBottom>Sinyal Zamanlama Diyagramı (Toplam Döngü: {totalCycle}sn)</Typography>
      <Box sx={{ display: 'flex', width: '100%', height: '40px', borderRadius: 2, overflow: 'hidden', border: '1px solid #ccc' }}>
        {phases.map((phase, index) => (
          <Tooltip key={phase.id} title={`${phase.name}: ${phase.duration}sn`}>
            <Box sx={{ 
              width: `${(phase.duration / totalCycle) * 100}%`, 
              bgcolor: phase.color, 
              display: 'flex', alignItems: 'center', justifyContent: 'center',
              color: '#fff', fontSize: '0.75rem', fontWeight: 'bold',
              borderRight: '1px solid #fff', cursor: 'pointer',
              transition: 'all 0.2s',
              '&:hover': { opacity: 0.8 }
            }}>
              {phase.shortName} ({phase.duration}s)
            </Box>
          </Tooltip>
        ))}
      </Box>
      {/* Zaman Cetveli */}
      <Box sx={{ display: 'flex', justifyContent: 'space-between', mt: 0.5 }}>
        <Typography variant="caption">0sn</Typography>
        <Typography variant="caption">{totalCycle / 2}sn</Typography>
        <Typography variant="caption">{totalCycle}sn</Typography>
      </Box>
    </Box>
  );
};

export default function LanesTab({ isReadOnly, phases, setPhases, lanes = initialLanes, setLanes }) {
  // Dialog State
  const [openDialog, setOpenDialog] = useState(false);
  const [editingLane, setEditingLane] = useState(null); // null ise yeni ekleme, dolu ise düzenleme
  const [formData, setFormData] = useState({ id: '', label: '', direction: 'N', type: 'vehicle' });

  const totalCycle = phases ? phases.reduce((acc, curr) => acc + curr.duration, 0) : 0;

  const handleDurationChange = (id, newValue) => {
    setPhases(phases.map(p => p.id === id ? { ...p, duration: newValue } : p));
  };

  // --- CRUD İŞLEMLERİ ---
  const handleAddClick = () => {
    setEditingLane(null);
    setFormData({ id: '', label: '', direction: 'N', type: 'vehicle' });
    setOpenDialog(true);
  };

  const handleEditClick = (lane) => {
    setEditingLane(lane);
    // ID'den yönü çıkaralım (N, S, E, W)
    const dir = lane.id.charAt(0); 
    setFormData({ 
      id: lane.id, 
      label: lane.label, 
      direction: ['N','S','E','W'].includes(dir) ? dir : 'N', 
      type: lane.id.includes('R') ? 'bus' : 'vehicle' 
    });
    setOpenDialog(true);
  };

  const handleDeleteClick = (laneId) => {
    if (window.confirm('Bu şeridi silmek istediğinize emin misiniz?')) {
      if (setLanes) {
        setLanes(lanes.filter(l => l.id !== laneId));
      }
    }
  };

  const handleSave = () => {
    if (!formData.label) return;

    // ID Oluşturma (Eğer yeni ise)
    let newId = formData.id;
    if (!editingLane) {
      // Basit ID üretimi: Yön + Random Sayı
      newId = `${formData.direction}-${Math.floor(Math.random() * 1000)}`;
    }

    const newLaneObj = {
      id: newId,
      label: formData.label,
      icon: <ArrowUpward />, // Varsayılan ikon, yönüne göre güncellenebilir
      // Diğer özellikler...
    };

    // Yönüne göre ikon seçimi (Basitçe)
    if (formData.direction === 'N') newLaneObj.icon = <ArrowUpward />;
    if (formData.direction === 'S') newLaneObj.icon = <ArrowDownward />;
    if (formData.direction === 'E') newLaneObj.icon = <ArrowForward />;
    if (formData.direction === 'W') newLaneObj.icon = <ArrowBack />;

    if (setLanes) {
      if (editingLane) {
        // Güncelleme
        setLanes(lanes.map(l => l.id === editingLane.id ? newLaneObj : l));
      } else {
        // Ekleme
        setLanes([...lanes, newLaneObj]);
      }
    }
    setOpenDialog(false);
  };

  // Şeritleri Yönlerine Göre Grupla (State'den gelen 'lanes' kullanılıyor)
  const groupedLanes = {
    'Kuzey (North)': lanes.filter(l => l.id.startsWith('N')),
    'Güney (South)': lanes.filter(l => l.id.startsWith('S')),
    'Doğu (East)': lanes.filter(l => l.id.startsWith('E')),
    'Batı (West)': lanes.filter(l => l.id.startsWith('W')),
  };

  return (
    <Grid container spacing={3}>
      {/* SOL KOLON: ŞERİT YÖNETİMİ */}
      <Grid item xs={12} md={5}>
        <Card sx={{ height: '100%' }}>
          <CardContent>
            <Box display="flex" justifyContent="space-between" mb={2} alignItems="center">
               <Typography variant="h6">Şerit Konfigürasyonu</Typography>
               <Button 
                 startIcon={<AddCircleIcon />} 
                 size="small" 
                 disabled={isReadOnly}
                 onClick={handleAddClick}
               >
                 Şerit Ekle
               </Button>
            </Box>
            <Divider sx={{ mb: 2 }}/>
            
            <Box sx={{ maxHeight: '600px', overflow: 'auto' }}>
              {Object.entries(groupedLanes).map(([region, laneList]) => (
                <Box key={region} sx={{ mb: 2 }}>
                  <Typography variant="subtitle2" sx={{ bgcolor: '#eee', p: 1, borderRadius: 1 }}>{region}</Typography>
                  <List dense>
                    {laneList.map((lane) => (
                      <ListItem key={lane.id} secondaryAction={
                        <Box>
                          <IconButton edge="end" size="small" disabled={isReadOnly} onClick={() => handleEditClick(lane)}>
                            <EditIcon fontSize="small"/>
                          </IconButton>
                          <IconButton edge="end" size="small" disabled={isReadOnly} color="error" onClick={() => handleDeleteClick(lane.id)}>
                            <DeleteIcon fontSize="small"/>
                          </IconButton>
                        </Box>
                      }>
                        <ListItemIcon sx={{ minWidth: 36, color: '#1a237e' }}>{lane.icon}</ListItemIcon>
                        <ListItemText 
                          primary={lane.label} 
                          // DÜZELTME 1: İkincil metnin bir div olmasını sağlıyoruz
                          secondaryTypographyProps={{ component: 'div' }}
                          secondary={
                            // DÜZELTME 2: İçerideki span'i div yaptık (div > div > div geçerli HTML'dir)
                            <Box component="div" display="flex" gap={0.5} mt={0.5}>
                              <Chip label="Araç" size="small" icon={<DirectionsCar sx={{fontSize:14}}/>} sx={{height:20, fontSize:10}} />
                              {/* Rastgele bazılarına Otobüs/Tramvay ekleyelim simülasyon hissi için */}
                              {lane.id.includes('R') && <Chip label="Otobüs" size="small" color="warning" icon={<DirectionsBus sx={{fontSize:14}}/>} sx={{height:20, fontSize:10}} />}
                            </Box>
                          } 
                        />
                      </ListItem>
                    ))}
                    {laneList.length === 0 && <Typography variant="caption" sx={{ml:2, fontStyle:'italic'}}>Bu yönde şerit yok.</Typography>}
                  </List>
                </Box>
              ))}
            </Box>
          </CardContent>
        </Card>
      </Grid>

      {/* SAĞ KOLON: SİNYAL PLANLAMA */}
      <Grid item xs={12} md={7}>
         <Card sx={{ mb: 3 }}>
           <CardContent>
             <Box display="flex" alignItems="center" mb={2}>
               <AccessTimeIcon color="primary" sx={{ mr: 1 }} />
               <Typography variant="h6">Sinyal Faz Planı (Signal Timing)</Typography>
             </Box>
             <Divider />
             
             {/* GÖRSEL DİYAGRAM */}
             <SignalTimingChart phases={phases} totalCycle={totalCycle} />

             <Typography variant="subtitle2" sx={{ mt: 3, mb: 1 }}>Faz Sürelerini Düzenle</Typography>
             <Grid container spacing={2}>
               {phases.map((phase) => (
                 <Grid item xs={12} sm={6} key={phase.id}>
                   <Paper variant="outlined" sx={{ p: 2, borderLeft: `4px solid ${phase.color}` }}>
                      <Box display="flex" justifyContent="space-between" alignItems="center" mb={1}>
                        <Typography variant="body2" fontWeight="bold">{phase.name}</Typography>
                        <Chip label={`${phase.duration} sn`} size="small" sx={{ bgcolor: phase.color, color: '#fff', fontWeight: 'bold' }} />
                      </Box>
                      <Slider 
                        value={phase.duration} 
                        min={3} max={120} 
                        onChange={(e, val) => handleDurationChange(phase.id, val)}
                        disabled={isReadOnly}
                        size="small"
                        sx={{ color: phase.color }}
                      />
                   </Paper>
                 </Grid>
               ))}
             </Grid>

             <Box sx={{ mt: 3, p: 2, bgcolor: '#e3f2fd', borderRadius: 2, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <Box>
                  <Typography variant="body2" color="primary" fontWeight="bold">Toplam Döngü Süresi (Cycle Time)</Typography>
                  <Typography variant="caption">Tüm fazların toplam süresidir.</Typography>
                </Box>
                <Typography variant="h4" color="primary">{totalCycle} <span style={{fontSize: '1rem'}}>sn</span></Typography>
             </Box>

           </CardContent>
         </Card>
      </Grid>

      {/* --- ŞERİT EKLEME/DÜZENLEME DIALOG --- */}
      <Dialog open={openDialog} onClose={() => setOpenDialog(false)}>
        <DialogTitle>{editingLane ? 'Şeridi Düzenle' : 'Yeni Şerit Ekle'}</DialogTitle>
        <DialogContent>
          <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2, mt: 1, minWidth: 300 }}>
            <TextField 
              label="Şerit Adı / Tanımı" 
              fullWidth 
              value={formData.label} 
              onChange={(e) => setFormData({...formData, label: e.target.value})}
              placeholder="Örn: Kuzey - Düz"
            />
            <FormControl fullWidth>
              <InputLabel>Yön</InputLabel>
              <Select
                value={formData.direction}
                label="Yön"
                onChange={(e) => setFormData({...formData, direction: e.target.value})}
              >
                <MenuItem value="N">Kuzey (North)</MenuItem>
                <MenuItem value="S">Güney (South)</MenuItem>
                <MenuItem value="E">Doğu (East)</MenuItem>
                <MenuItem value="W">Batı (West)</MenuItem>
              </Select>
            </FormControl>
            <FormControl fullWidth>
              <InputLabel>Tip</InputLabel>
              <Select
                value={formData.type}
                label="Tip"
                onChange={(e) => setFormData({...formData, type: e.target.value})}
              >
                <MenuItem value="vehicle">Araç</MenuItem>
                <MenuItem value="bus">Otobüs / Toplu Taşıma</MenuItem>
                <MenuItem value="tram">Tramvay</MenuItem>
              </Select>
            </FormControl>
          </Box>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setOpenDialog(false)}>İptal</Button>
          <Button onClick={handleSave} variant="contained">Kaydet</Button>
        </DialogActions>
      </Dialog>
    </Grid>
  );
}