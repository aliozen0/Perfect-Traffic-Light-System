import React, { useState } from 'react';
import axios from 'axios';
import { 
    Box, Card, CardContent, Typography, Button, Slider, 
    Grid, Divider, Chip, LinearProgress, Alert, Paper 
} from '@mui/material';
import { 
    AutoAwesome, Speed, DirectionsCar, 
    Traffic, AccessTime, TrendingUp, CheckCircle 
} from '@mui/icons-material';

const OptimizationPanel = ({ onOptimizationComplete }) => {
    // --- STATE YÖNETİMİ ---
    const [simulationData, setSimulationData] = useState({
        vehicleCount: 45,   // Varsayılan araç sayısı
        averageSpeed: 30,   // Varsayılan hız
    });

    const [loading, setLoading] = useState(false);
    const [result, setResult] = useState(null);
    const [error, setError] = useState(null);

    // --- HANDLERS ---
    const handleSliderChange = (name) => (e, newValue) => {
        setSimulationData(prev => ({ ...prev, [name]: newValue }));
    };

    const handleOptimize = async () => {
        setLoading(true);
        setError(null);
        setResult(null);

        try {
            const token = localStorage.getItem('token');
            // Backend'e istek atıyoruz
            const response = await axios.post('http://localhost:8080/api/optimization/apply', {
                intersectionId: 1,
                vehicleCount: simulationData.vehicleCount,
                averageSpeed: simulationData.averageSpeed,
                direction: 'NORTH'
            }, {
                headers: { Authorization: `Bearer ${token}` }
            });

            // Başarılı cevabı kaydet
            setResult(response.data);
            
            // Üst bileşene (Haritaya) yeni ışık sürelerini haber ver (Opsiyonel)
            if(onOptimizationComplete && response.data.success) {
                onOptimizationComplete(response.data.details.newGreenDuration);
            }

        } catch (err) {
            console.error(err);
            setError("Backend bağlantısında hata oluştu! Token süresi dolmuş veya sunucu kapalı olabilir.");
        } finally {
            setLoading(false);
        }
    };

    return (
        <Card sx={{ height: '100%', bgcolor: '#1e1e1e', color: 'white', borderRadius: 3, boxShadow: 6 }}>
            <CardContent>
                {/* --- BAŞLIK --- */}
                <Box sx={{ display: 'flex', alignItems: 'center', mb: 3 }}>
                    <AutoAwesome sx={{ color: '#00e676', mr: 1, fontSize: 30 }} />
                    <Typography variant="h6" sx={{ fontWeight: 'bold', bg: 'linear-gradient(45deg, #FE6B8B 30%, #FF8E53 90%)', backgroundClip: 'text' }}>
                        Yapay Zeka Trafik Operatörü
                    </Typography>
                </Box>

                {/* --- SİMÜLASYON GİRİŞLERİ --- */}
                <Paper sx={{ p: 2, bgcolor: '#2c2c2c', borderRadius: 2, mb: 3 }}>
                    <Typography variant="subtitle2" sx={{ color: '#aaa', mb: 2 }}>
                        ANLIK TRAFİK SİMÜLASYONU
                    </Typography>

                    {/* Araç Sayısı Slider */}
                    <Box sx={{ mb: 2 }}>
                        <Grid container spacing={2} alignItems="center">
                            <Grid item><DirectionsCar sx={{ color: '#4fc3f7' }} /></Grid>
                            <Grid item xs>
                                <Typography variant="body2">Araç Sayısı: <strong>{simulationData.vehicleCount}</strong></Typography>
                                <Slider 
                                    value={simulationData.vehicleCount}
                                    onChange={handleSliderChange('vehicleCount')}
                                    min={0} max={100}
                                    sx={{ color: '#4fc3f7' }}
                                />
                            </Grid>
                        </Grid>
                    </Box>

                    {/* Hız Slider */}
                    <Box>
                        <Grid container spacing={2} alignItems="center">
                            <Grid item><Speed sx={{ color: '#ffb74d' }} /></Grid>
                            <Grid item xs>
                                <Typography variant="body2">Ortalama Hız: <strong>{simulationData.averageSpeed} km/h</strong></Typography>
                                <Slider 
                                    value={simulationData.averageSpeed}
                                    onChange={handleSliderChange('averageSpeed')}
                                    min={0} max={120}
                                    sx={{ color: '#ffb74d' }}
                                />
                            </Grid>
                        </Grid>
                    </Box>
                </Paper>

                {/* --- AKSİYON BUTONU --- */}
                <Button 
                    variant="contained" 
                    fullWidth 
                    size="large"
                    onClick={handleOptimize}
                    disabled={loading}
                    startIcon={loading ? <Traffic /> : <AutoAwesome />}
                    sx={{ 
                        bgcolor: loading ? '#555' : '#00e676', 
                        color: 'black', fontWeight: 'bold', 
                        mb: 3, py: 1.5,
                        '&:hover': { bgcolor: '#00c853' }
                    }}
                >
                    {loading ? "Yapay Zeka Analiz Ediyor..." : "TRAFİĞİ OPTİMİZE ET"}
                </Button>

                {/* --- SONUÇ EKRANI (Varsa Göster) --- */}
                {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}

                {result && result.success && (
                    <Box sx={{ animation: 'fadeIn 0.5s ease-in' }}>
                        <Divider sx={{ borderColor: '#444', mb: 2 }} />
                        
                        {/* 1. Karar Özeti */}
                        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
                            <Chip 
                                label={result.intersection.densityLevel} 
                                color={result.intersection.vehicleCount > 40 ? "error" : "success"} 
                                variant="outlined"
                            />
                            <Typography variant="caption" sx={{ color: '#aaa' }}>
                                {new Date().toLocaleTimeString()}
                            </Typography>
                        </Box>

                        {/* 2. Süre Değişimi (Before -> After) */}
                        <Paper sx={{ p: 2, bgcolor: '#121212', border: '1px solid #333', borderRadius: 2, mb: 2, display: 'flex', justifyContent: 'space-around', alignItems: 'center' }}>
                            <Box sx={{ textAlign: 'center' }}>
                                <Typography variant="caption" color="text.secondary">Şu Anki Süre</Typography>
                                <Typography variant="h5" color="error">{result.details.previousGreenDuration}sn</Typography>
                            </Box>
                            <Box>
                                <TrendingUp sx={{ color: '#00e676', fontSize: 30 }} />
                            </Box>
                            <Box sx={{ textAlign: 'center' }}>
                                <Typography variant="caption" color="text.secondary">Optimize Süre</Typography>
                                <Typography variant="h4" color="#00e676" fontWeight="bold">{result.details.newGreenDuration}sn</Typography>
                            </Box>
                        </Paper>

                        {/* 3. Uygulanan Kural Bilgisi */}
                        <Alert icon={<CheckCircle fontSize="inherit" />} severity="success" sx={{ bgcolor: 'rgba(0, 230, 118, 0.1)', color: '#00e676', mb: 2 }}>
                            <strong>Karar:</strong> {result.appliedRules[0].description}
                        </Alert>

                        {/* 4. Performans Metrikleri */}
                        <Box>
                            <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 0.5 }}>
                                <Typography variant="caption">Verimlilik Skoru</Typography>
                                <Typography variant="caption" color="#00e676">{result.performance.efficiencyScore}</Typography>
                            </Box>
                            <LinearProgress 
                                variant="determinate" 
                                value={parseInt(result.performance.efficiencyScore)} 
                                sx={{ height: 8, borderRadius: 5, bgcolor: '#333', '& .MuiLinearProgress-bar': { bgcolor: '#00e676' } }} 
                            />
                            
                            <Grid container spacing={1} sx={{ mt: 1 }}>
                                <Grid item xs={6}>
                                    <Typography variant="caption" display="block" color="#aaa">Akış İyileşmesi</Typography>
                                    <Typography variant="body2" color="white">{result.performance.flowImprovement}</Typography>
                                </Grid>
                                <Grid item xs={6} sx={{ textAlign: 'right' }}>
                                    <Typography variant="caption" display="block" color="#aaa">Bekleme Süresi</Typography>
                                    <Typography variant="body2" color="white">{result.performance.waitTimeReduction}</Typography>
                                </Grid>
                            </Grid>
                        </Box>
                    </Box>
                )}
            </CardContent>
            
            {/* Animasyon CSS */}
            <style>{`
                @keyframes fadeIn {
                    from { opacity: 0; transform: translateY(10px); }
                    to { opacity: 1; transform: translateY(0); }
                }
            `}</style>
        </Card>
    );
};

export default OptimizationPanel;