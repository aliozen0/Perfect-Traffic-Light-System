import React from 'react';
import { GoogleMap, useJsApiLoader, MarkerF, InfoWindowF } from '@react-google-maps/api';
import { Box, Typography, Paper, Chip, Alert, CircularProgress } from '@mui/material';

const containerStyle = {
  width: '100%',
  height: '600px',
  borderRadius: '8px'
};

// Ankara Merkezi
const center = {
  lat: 39.925,
  lng: 32.850
};

const intersections = [
  { id: 'active', name: 'Beşevler Meydan (JN-01)', lat: 39.935, lng: 32.825, status: 'online' },
  { id: 'kizilay', name: 'Kızılay Merkez (JN-02)', lat: 39.920, lng: 32.854, status: 'online' },
  { id: 'ulus', name: 'Ulus Heykel (JN-03)', lat: 39.941, lng: 32.856, status: 'warning' },
  { id: 'cankaya', name: 'Çankaya Köşkü (JN-04)', lat: 39.888, lng: 32.864, status: 'offline' }
];

export default function MapTab({ selectedIntersection, setSelectedIntersection }) {
  const { isLoaded, loadError } = useJsApiLoader({
    id: 'google-map-script',
    googleMapsApiKey: "AIzaSyBTUPTa7vYKAXP1UJZpsQd4KIPVdcRfTZo"
  });

  const [activeMarker, setActiveMarker] = React.useState(null);

  // Harita yüklendiğinde seçili olanı aç
  React.useEffect(() => {
    if (selectedIntersection) {
      setActiveMarker(selectedIntersection);
    }
  }, [selectedIntersection]);

  if (loadError) {
    return (
      <Paper elevation={3} sx={{ p: 2 }}>
        <Alert severity="error">
          Harita yüklenemedi! Lütfen API anahtarınızı ve internet bağlantınızı kontrol edin.
          <br />
          Hata Detayı: {loadError.message}
        </Alert>
      </Paper>
    );
  }

  if (!isLoaded) {
    return (
      <Paper elevation={3} sx={{ p: 2, height: '600px', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
        <Box textAlign="center">
          <CircularProgress />
          <Typography sx={{ mt: 2 }}>Harita Yükleniyor...</Typography>
        </Box>
      </Paper>
    );
  }

  return (
    <Paper elevation={3} sx={{ p: 2 }}>
      <Box sx={{ mb: 2 }}>
        <Typography variant="h6" gutterBottom>Canlı Şehir Haritası</Typography>
        <Alert severity="info" sx={{ mb: 2 }}>
          Harita üzerindeki işaretçilere tıklayarak o kavşağı yönetmeye başlayabilirsiniz.
        </Alert>
      </Box>
      
      <GoogleMap
        mapContainerStyle={containerStyle}
        center={center}
        zoom={13}
      >
        {intersections.map((intersection) => (
          <MarkerF
            key={intersection.id}
            position={{ lat: intersection.lat, lng: intersection.lng }}
            onClick={() => {
              setActiveMarker(intersection.id);
              setSelectedIntersection(intersection.id);
            }}
          />
        ))}

        {activeMarker && (() => {
          const intersection = intersections.find(i => i.id === activeMarker);
          if (!intersection) return null;
          return (
            <InfoWindowF
              position={{ lat: intersection.lat, lng: intersection.lng }}
              onCloseClick={() => setActiveMarker(null)}
            >
              <Box sx={{ minWidth: 150 }}>
                <Typography variant="subtitle2" fontWeight="bold">{intersection.name}</Typography>
                <Box sx={{ mt: 1, display: 'flex', alignItems: 'center', gap: 1 }}>
                  <Chip 
                    label={intersection.status === 'online' ? 'AKTİF' : intersection.status === 'warning' ? 'UYARI' : 'KAPALI'} 
                    color={intersection.status === 'online' ? 'success' : intersection.status === 'warning' ? 'warning' : 'error'} 
                    size="small" 
                  />
                  {selectedIntersection === intersection.id && (
                    <Typography variant="caption" color="primary" fontWeight="bold">
                      (YÖNETİLİYOR)
                    </Typography>
                  )}
                </Box>
              </Box>
            </InfoWindowF>
          );
        })()}
      </GoogleMap>
    </Paper>
  );
}
