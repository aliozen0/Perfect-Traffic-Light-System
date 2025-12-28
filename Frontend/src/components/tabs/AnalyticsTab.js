import React from 'react';
import { Grid, Card, CardContent, Typography, Box, Divider } from '@mui/material';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer, BarChart, Bar, PieChart, Pie, Cell, AreaChart, Area } from 'recharts';
import { TrendingUp, Speed, Warning, DirectionsCar } from '@mui/icons-material';

// Örnek Veriler
const hourlyTrafficData = [
  { time: '06:00', density: 20, speed: 70 },
  { time: '08:00', density: 85, speed: 30 },
  { time: '10:00', density: 60, speed: 50 },
  { time: '12:00', density: 50, speed: 55 },
  { time: '14:00', density: 55, speed: 52 },
  { time: '16:00', density: 70, speed: 40 },
  { time: '18:00', density: 95, speed: 20 },
  { time: '20:00', density: 65, speed: 45 },
  { time: '22:00', density: 30, speed: 65 },
];

const violationData = [
  { name: 'Kırmızı Işık', count: 12 },
  { name: 'Hız İhlali', count: 45 },
  { name: 'Emniyet Şeridi', count: 8 },
  { name: 'Yaya Yolu', count: 15 },
];

const vehicleTypeData = [
  { name: 'Otomobil', value: 65 },
  { name: 'Otobüs', value: 15 },
  { name: 'Kamyon', value: 10 },
  { name: 'Motosiklet', value: 10 },
];

const COLORS = ['#0088FE', '#00C49F', '#FFBB28', '#FF8042'];

const StatCard = ({ title, value, icon, color }) => (
  <Card sx={{ height: '100%', borderLeft: `4px solid ${color}` }}>
    <CardContent>
      <Box display="flex" justifyContent="space-between" alignItems="center">
        <Box>
          <Typography color="textSecondary" gutterBottom variant="caption">{title}</Typography>
          <Typography variant="h5" fontWeight="bold">{value}</Typography>
        </Box>
        <Box sx={{ p: 1, borderRadius: 2, bgcolor: `${color}20`, color: color }}>
          {icon}
        </Box>
      </Box>
    </CardContent>
  </Card>
);

export default function AnalyticsTab() {
  return (
    <Grid container spacing={3}>
      {/* ÖZET KARTLAR */}
      <Grid item xs={12} sm={6} md={3}>
        <StatCard title="Günlük Araç Geçişi" value="14,250" icon={<DirectionsCar />} color="#1976d2" />
      </Grid>
      <Grid item xs={12} sm={6} md={3}>
        <StatCard title="Ortalama Hız" value="42 km/s" icon={<Speed />} color="#2e7d32" />
      </Grid>
      <Grid item xs={12} sm={6} md={3}>
        <StatCard title="Yoğunluk İndeksi" value="%78" icon={<TrendingUp />} color="#ed6c02" />
      </Grid>
      <Grid item xs={12} sm={6} md={3}>
        <StatCard title="Tespit Edilen İhlal" value="80" icon={<Warning />} color="#d32f2f" />
      </Grid>

      {/* GRAFİKLER */}
      <Grid item xs={12} md={8}>
        <Card sx={{ height: '400px' }}>
          <CardContent sx={{ height: '100%' }}>
            <Typography variant="h6" gutterBottom>Saatlik Trafik Yoğunluğu & Hız</Typography>
            <ResponsiveContainer width="100%" height="90%">
              <AreaChart data={hourlyTrafficData}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="time" />
                <YAxis yAxisId="left" />
                <YAxis yAxisId="right" orientation="right" />
                <Tooltip />
                <Legend />
                <Area yAxisId="left" type="monotone" dataKey="density" name="Yoğunluk (%)" stroke="#8884d8" fill="#8884d8" />
                <Area yAxisId="right" type="monotone" dataKey="speed" name="Ort. Hız (km/s)" stroke="#82ca9d" fill="#82ca9d" />
              </AreaChart>
            </ResponsiveContainer>
          </CardContent>
        </Card>
      </Grid>

      <Grid item xs={12} md={4}>
        <Card sx={{ height: '400px' }}>
          <CardContent sx={{ height: '100%' }}>
            <Typography variant="h6" gutterBottom>Araç Tipi Dağılımı</Typography>
            <ResponsiveContainer width="100%" height="90%">
              <PieChart>
                <Pie
                  data={vehicleTypeData}
                  cx="50%"
                  cy="50%"
                  innerRadius={60}
                  outerRadius={80}
                  fill="#8884d8"
                  paddingAngle={5}
                  dataKey="value"
                  label
                >
                  {vehicleTypeData.map((entry, index) => (
                    <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                  ))}
                </Pie>
                <Tooltip />
                <Legend verticalAlign="bottom" height={36}/>
              </PieChart>
            </ResponsiveContainer>
          </CardContent>
        </Card>
      </Grid>

      <Grid item xs={12}>
        <Card sx={{ height: '350px' }}>
          <CardContent sx={{ height: '100%' }}>
            <Typography variant="h6" gutterBottom>İhlal İstatistikleri (Günlük)</Typography>
            <ResponsiveContainer width="100%" height="85%">
              <BarChart data={violationData} layout="vertical" margin={{ left: 20 }}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis type="number" />
                <YAxis dataKey="name" type="category" width={100}/>
                <Tooltip />
                <Legend />
                <Bar dataKey="count" name="İhlal Sayısı" fill="#ff6b6b" radius={[0, 4, 4, 0]} />
              </BarChart>
            </ResponsiveContainer>
          </CardContent>
        </Card>
      </Grid>
    </Grid>
  );
}
