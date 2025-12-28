// src/components/TabPanel.js
import React from 'react';
import { Box } from '@mui/material';

export default function TabPanel(props) {
  const { children, value, index, ...other } = props;

  if (value !== index) return null;

  return (
    <div role="tabpanel" hidden={value !== index} {...other}>
      <Box sx={{ p: 3 }}>
        {children}
      </Box>
    </div>
  );
}
