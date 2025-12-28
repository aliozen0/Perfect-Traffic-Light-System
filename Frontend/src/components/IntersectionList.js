import React, { useState, useEffect } from 'react';
import { getIntersections, getIntersectionsByCity } from '../services/api';

/**
 * Example component showing how to use the backend API
 */
function IntersectionList() {
  const [intersections, setIntersections] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [selectedCity, setSelectedCity] = useState('');
  const [cities] = useState(['Istanbul', 'Ankara', 'Izmir']);

  useEffect(() => {
    fetchIntersections();
  }, [selectedCity]);

  const fetchIntersections = async () => {
    try {
      setLoading(true);
      setError(null);
      
      let response;
      if (selectedCity) {
        response = await getIntersectionsByCity(selectedCity);
      } else {
        response = await getIntersections({ page: 0, limit: 50 });
      }
      
      // Backend returns data in ApiResponse format: { success, message, data, ... }
      if (response.success && response.data) {
        setIntersections(response.data.content || response.data);
      } else {
        setError('Failed to fetch intersections');
      }
    } catch (err) {
      setError('Error connecting to backend: ' + err.message);
      console.error('Fetch error:', err);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div style={styles.container}>
        <div style={styles.loading}>Loading intersections...</div>
      </div>
    );
  }

  if (error) {
    return (
      <div style={styles.container}>
        <div style={styles.error}>
          <h3>❌ Error</h3>
          <p>{error}</p>
          <p style={styles.hint}>
            Make sure your backend is running on http://localhost:8080
          </p>
          <button onClick={fetchIntersections} style={styles.retryButton}>
            Retry
          </button>
        </div>
      </div>
    );
  }

  return (
    <div style={styles.container}>
      <div style={styles.header}>
        <h1>🚦 Traffic Light Intersections</h1>
        <p>Connected to Spring Boot Backend</p>
      </div>

      <div style={styles.filters}>
        <label style={styles.label}>Filter by City:</label>
        <select 
          value={selectedCity} 
          onChange={(e) => setSelectedCity(e.target.value)}
          style={styles.select}
        >
          <option value="">All Cities</option>
          {cities.map(city => (
            <option key={city} value={city}>{city}</option>
          ))}
        </select>
        <button onClick={fetchIntersections} style={styles.refreshButton}>
          🔄 Refresh
        </button>
      </div>

      <div style={styles.stats}>
        <div style={styles.statCard}>
          <h3>{intersections.length}</h3>
          <p>Total Intersections</p>
        </div>
      </div>

      <div style={styles.grid}>
        {intersections.length === 0 ? (
          <div style={styles.empty}>
            <p>No intersections found</p>
            <p style={styles.hint}>Try selecting a different city or check if data seeding is enabled</p>
          </div>
        ) : (
          intersections.map(intersection => (
            <div key={intersection.id} style={styles.card}>
              <div style={styles.cardHeader}>
                <h3>{intersection.name}</h3>
                <span style={getStatusBadgeStyle(intersection.status)}>
                  {intersection.status}
                </span>
              </div>
              <div style={styles.cardBody}>
                <p><strong>Code:</strong> {intersection.code}</p>
                <p><strong>City:</strong> {intersection.city}</p>
                {intersection.district && <p><strong>District:</strong> {intersection.district}</p>}
                <p><strong>Type:</strong> {intersection.intersectionType}</p>
                <p><strong>Lanes:</strong> {intersection.lanesCount}</p>
                <p>
                  <strong>Location:</strong> {intersection.latitude}, {intersection.longitude}
                </p>
                <div style={styles.features}>
                  {intersection.hasPedestrianCrossing && (
                    <span style={styles.badge}>🚶 Pedestrian</span>
                  )}
                  {intersection.hasVehicleDetection && (
                    <span style={styles.badge}>🚗 Detection</span>
                  )}
                  {intersection.hasEmergencyOverride && (
                    <span style={styles.badge}>🚨 Emergency</span>
                  )}
                </div>
              </div>
            </div>
          ))
        )}
      </div>
    </div>
  );
}

const getStatusBadgeStyle = (status) => {
  const baseStyle = {
    padding: '4px 8px',
    borderRadius: '4px',
    fontSize: '12px',
    fontWeight: 'bold',
  };

  const colors = {
    ACTIVE: { backgroundColor: '#4caf50', color: 'white' },
    INACTIVE: { backgroundColor: '#ff9800', color: 'white' },
    MAINTENANCE: { backgroundColor: '#f44336', color: 'white' },
    UNDER_CONSTRUCTION: { backgroundColor: '#9e9e9e', color: 'white' },
  };

  return { ...baseStyle, ...colors[status] };
};

const styles = {
  container: {
    padding: '20px',
    maxWidth: '1200px',
    margin: '0 auto',
    fontFamily: 'Arial, sans-serif',
  },
  header: {
    textAlign: 'center',
    marginBottom: '30px',
  },
  filters: {
    display: 'flex',
    gap: '10px',
    alignItems: 'center',
    marginBottom: '20px',
    padding: '15px',
    backgroundColor: '#f5f5f5',
    borderRadius: '8px',
  },
  label: {
    fontWeight: 'bold',
  },
  select: {
    padding: '8px 12px',
    borderRadius: '4px',
    border: '1px solid #ddd',
    fontSize: '14px',
  },
  refreshButton: {
    padding: '8px 16px',
    backgroundColor: '#2196f3',
    color: 'white',
    border: 'none',
    borderRadius: '4px',
    cursor: 'pointer',
    fontSize: '14px',
  },
  retryButton: {
    padding: '10px 20px',
    backgroundColor: '#4caf50',
    color: 'white',
    border: 'none',
    borderRadius: '4px',
    cursor: 'pointer',
    marginTop: '10px',
  },
  stats: {
    display: 'flex',
    gap: '20px',
    marginBottom: '20px',
  },
  statCard: {
    flex: 1,
    padding: '20px',
    backgroundColor: '#2196f3',
    color: 'white',
    borderRadius: '8px',
    textAlign: 'center',
  },
  grid: {
    display: 'grid',
    gridTemplateColumns: 'repeat(auto-fill, minmax(300px, 1fr))',
    gap: '20px',
  },
  card: {
    border: '1px solid #ddd',
    borderRadius: '8px',
    padding: '16px',
    backgroundColor: 'white',
    boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
  },
  cardHeader: {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: '12px',
    paddingBottom: '12px',
    borderBottom: '1px solid #eee',
  },
  cardBody: {
    fontSize: '14px',
    lineHeight: '1.6',
  },
  features: {
    display: 'flex',
    gap: '8px',
    marginTop: '12px',
    flexWrap: 'wrap',
  },
  badge: {
    padding: '4px 8px',
    backgroundColor: '#e3f2fd',
    borderRadius: '12px',
    fontSize: '12px',
  },
  loading: {
    textAlign: 'center',
    fontSize: '18px',
    padding: '40px',
  },
  error: {
    textAlign: 'center',
    padding: '40px',
    backgroundColor: '#ffebee',
    borderRadius: '8px',
    color: '#c62828',
  },
  empty: {
    gridColumn: '1 / -1',
    textAlign: 'center',
    padding: '40px',
    backgroundColor: '#f5f5f5',
    borderRadius: '8px',
  },
  hint: {
    fontSize: '12px',
    color: '#666',
    marginTop: '10px',
  },
};

export default IntersectionList;


