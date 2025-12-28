import React from 'react';
import { Box, Typography, Paper } from '@mui/material';
import { styled, keyframes } from '@mui/material/styles';

// --- VISUAL CONSTANTS & KEYFRAMES ---
const GLOW_COLOR = {
  red: 'rgba(255, 23, 68, 0.8)',
  yellow: 'rgba(255, 234, 0, 0.8)',
  green: 'rgba(0, 230, 118, 0.8)',
};

const OFF_COLOR = {
  red: '#4a0d0d',
  yellow: '#4a4400',
  green: '#003314',
};

const pulse = keyframes`
  0% { box-shadow: 0 0 10px inherit; }
  50% { box-shadow: 0 0 20px inherit, 0 0 30px inherit; }
  100% { box-shadow: 0 0 10px inherit; }
`;

// --- STYLED COMPONENTS ---

// The Main Housing - Glassmorphism & Metallic Feel
const Housing = styled(Paper)(({ theme }) => ({
  position: 'relative',
  width: '180px',
  padding: '20px',
  borderRadius: '30px',
  background: 'linear-gradient(145deg, rgba(40,40,40,0.95) 0%, rgba(20,20,20,0.98) 100%)',
  border: '1px solid rgba(255,255,255,0.1)',
  boxShadow: `
    0 10px 30px rgba(0,0,0,0.8),
    inset 0 1px 1px rgba(255,255,255,0.1),
    0 0 0 4px rgba(10,10,10,0.8)
  `,
  display: 'flex',
  flexDirection: 'column',
  alignItems: 'center',
  gap: '20px',
  zIndex: 10,
  '&::before': { // Metallic Reflection Highlight
    content: '""',
    position: 'absolute',
    top: 0, left: 0, right: 0, bottom: 0,
    borderRadius: '30px',
    background: 'linear-gradient(120deg, rgba(255,255,255,0.05) 0%, transparent 40%)',
    pointerEvents: 'none',
  }
}));

// The Lens Container - Deep Recessed Look
const LensSocket = styled(Box)({
  width: '120px',
  height: '120px',
  borderRadius: '50%',
  background: 'linear-gradient(145deg, #1a1a1a, #000000)',
  boxShadow: 'inset 2px 2px 5px rgba(0,0,0,0.9), inset -1px -1px 2px rgba(50,50,50,0.3)',
  display: 'flex',
  alignItems: 'center',
  justifyContent: 'center',
  border: '4px solid #111',
  position: 'relative',
});

// The Light - Complex Gradient & Glow
const Light = styled(Box, { shouldForwardProp: (prop) => prop !== 'active' && prop !== 'colorType' })(
  ({ active, colorType }) => ({
    width: '100px',
    height: '100px',
    borderRadius: '50%',
    transition: 'all 0.5s cubic-bezier(0.4, 0, 0.2, 1)',
    background: active 
      ? `radial-gradient(circle at 30% 30%, #fff 0%, ${colorType} 20%, ${colorType} 60%, #000 100%)` 
      : `radial-gradient(circle at 30% 30%, rgba(255,255,255,0.1) 0%, ${OFF_COLOR[colorType]} 60%, #000 100%)`,
    boxShadow: active 
      ? `0 0 20px ${GLOW_COLOR[colorType]}, 0 0 60px ${GLOW_COLOR[colorType]}, inset 0 0 20px rgba(0,0,0,0.2)`
      : 'inset 0 0 10px rgba(0,0,0,0.8)',
    opacity: active ? 1 : 0.4,
    filter: active ? 'brightness(1.2)' : 'brightness(0.8)',
    animation: active ? `${pulse} 2s infinite ease-in-out` : 'none',
    zIndex: 2,
    
    // LED Grid Texture (Subtle)
    '&::after': {
      content: '""',
      position: 'absolute',
      top: 0, left: 0, right: 0, bottom: 0,
      borderRadius: '50%',
      backgroundImage: 'radial-gradient(rgba(0,0,0,0.2) 1px, transparent 1px)',
      backgroundSize: '4px 4px',
      opacity: 0.6,
      mixBlendMode: 'overlay',
    }
  })
);

// Visor (Hood) over the light
const Visor = styled(Box)({
  position: 'absolute',
  top: '-15%',
  width: '110%',
  height: '60%',
  borderTopLeftRadius: '100px',
  borderTopRightRadius: '100px',
  background: 'linear-gradient(180deg, #151515 0%, #252525 100%)',
  boxShadow: '0 4px 6px rgba(0,0,0,0.5)',
  zIndex: 1,
});

// Digital Countdown - Retro Segment Style
const CountdownPanel = styled(Box)({
  marginTop: '20px',
  background: '#000',
  border: '2px solid #555',
  borderRadius: '8px',
  padding: '10px 20px',
  boxShadow: 'inset 0 0 10px rgba(0,0,0,0.8)',
  display: 'flex',
  flexDirection: 'column',
  alignItems: 'center',
});

const DigitalNumber = styled(Typography)(({ activeColor }) => ({
  fontFamily: '"Courier New", Courier, monospace', // Fallback for segment font
  fontSize: '2.5rem',
  fontWeight: 'bold',
  color: activeColor || '#333',
  textShadow: activeColor ? `0 0 10px ${activeColor}` : 'none',
  lineHeight: 1,
  letterSpacing: '5px',
}));

// Status Badge
const StatusBadge = styled(Box)(({ status }) => ({
  position: 'absolute',
  top: '-15px',
  right: '-30px',
  background: status === 'EMERGENCY' ? '#d32f2f' : 'rgba(0,0,0,0.8)',
  color: '#fff',
  padding: '5px 15px',
  borderRadius: '20px',
  border: '1px solid rgba(255,255,255,0.2)',
  backdropFilter: 'blur(5px)',
  fontSize: '0.8rem',
  fontWeight: 'bold',
  textTransform: 'uppercase',
  boxShadow: '0 5px 15px rgba(0,0,0,0.5)',
  zIndex: 20,
}));


const EnhancedTrafficLight = ({ currentLight, timeLeft, status = "NORMAL" }) => {
  // Determine active color for digital display based on current light
  const getDigitalColor = () => {
    if (currentLight === 'red') return '#ff1744';
    if (currentLight === 'yellow') return '#ffea00';
    if (currentLight === 'green') return '#00e676';
    return '#555';
  };

  return (
    <Box sx={{ position: 'relative', display: 'inline-block', p: 4 }}>
      <StatusBadge status={status}>
        {status}
      </StatusBadge>
      
      <Housing>
        {/* Red Light */}
        <LensSocket>
          <Visor />
          <Light active={currentLight === 'red'} colorType="red" />
        </LensSocket>

        {/* Yellow Light */}
        <LensSocket>
          <Visor />
          <Light active={currentLight === 'yellow'} colorType="yellow" />
        </LensSocket>

        {/* Green Light */}
        <LensSocket>
          <Visor />
          <Light active={currentLight === 'green'} colorType="green" />
        </LensSocket>
      </Housing>

      <Box sx={{ display: 'flex', justifyContent: 'center' }}>
        <CountdownPanel>
          <Typography variant="caption" sx={{ color: '#888', mb: 0.5, letterSpacing: 1 }}>TIME LEFT</Typography>
          <DigitalNumber activeColor={getDigitalColor()}>
            {String(timeLeft).padStart(2, '0')}
          </DigitalNumber>
        </CountdownPanel>
      </Box>
    </Box>
  );
};

export default EnhancedTrafficLight;
