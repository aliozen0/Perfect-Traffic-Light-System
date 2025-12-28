import { createTheme } from '@mui/material/styles';

const theme = createTheme({
  palette: {
    mode: 'light',
    primary: {
      main: '#2563eb', // Modern, canlı bir mavi (Royal Blue)
      light: '#60a5fa',
      dark: '#1e40af',
    },
    secondary: {
      main: '#10b981', // Zümrüt yeşili (Emerald)
      light: '#34d399',
      dark: '#059669',
    },
    background: {
      default: '#f3f4f6', // Çok açık gri (Cool Gray)
      paper: '#ffffff',
    },
    text: {
      primary: '#111827', // Tam siyah değil, koyu gri (Daha yumuşak)
      secondary: '#6b7280',
    },
  },
  typography: {
    fontFamily: '"Inter", "Roboto", "Helvetica", "Arial", sans-serif',
    h5: {
      fontWeight: 600,
      letterSpacing: '-0.025em',
    },
    h6: {
      fontWeight: 600,
    },
    button: {
      textTransform: 'none', // Buton yazıları tamamen büyük harf olmasın
      fontWeight: 500,
    },
  },
  shape: {
    borderRadius: 12, // Daha yuvarlak köşeler
  },
  components: {
    MuiCard: {
      styleOverrides: {
        root: {
          boxShadow: '0 4px 6px -1px rgb(0 0 0 / 0.1), 0 2px 4px -2px rgb(0 0 0 / 0.1)', // Modern, yumuşak gölge
          border: '1px solid #e5e7eb',
        },
      },
    },
    MuiButton: {
      styleOverrides: {
        root: {
          borderRadius: 8,
          boxShadow: 'none',
          '&:hover': {
            boxShadow: '0 4px 6px -1px rgb(0 0 0 / 0.1), 0 2px 4px -2px rgb(0 0 0 / 0.1)',
          },
        },
      },
    },
    MuiPaper: {
      styleOverrides: {
        root: {
          backgroundImage: 'none',
        },
      },
    },
  },
});

export default theme;
