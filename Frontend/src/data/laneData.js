// src/data/laneData.js
import ArrowUpwardIcon from '@mui/icons-material/ArrowUpward';
import TurnLeftIcon from '@mui/icons-material/TurnLeft';
import TurnRightIcon from '@mui/icons-material/TurnRight';

export const initialLanes = [
    { id: 'N-S', label: 'Kuzey - Düz', icon: <ArrowUpwardIcon /> },
    { id: 'N-L', label: 'Kuzey - Sola Dönüş', icon: <TurnLeftIcon /> },
    { id: 'N-R', label: 'Kuzey - Sağa Dönüş', icon: <TurnRightIcon /> },
    { id: 'S-S', label: 'Güney - Düz', icon: <ArrowUpwardIcon sx={{ transform: 'rotate(180deg)' }}/> },
    { id: 'S-L', label: 'Güney - Sola Dönüş', icon: <TurnRightIcon sx={{ transform: 'rotate(180deg)' }}/> },
    { id: 'S-R', label: 'Güney - Sağa Dönüş', icon: <TurnLeftIcon sx={{ transform: 'rotate(180deg)' }}/> },
    { id: 'E-S', label: 'Doğu - Düz', icon: <ArrowUpwardIcon sx={{ transform: 'rotate(90deg)' }}/> },
    { id: 'E-L', label: 'Doğu - Sola Dönüş', icon: <TurnLeftIcon sx={{ transform: 'rotate(90deg)' }}/> },
    { id: 'E-R', label: 'Doğu - Sağa Dönüş', icon: <TurnRightIcon sx={{ transform: 'rotate(90deg)' }}/> },
    { id: 'W-S', label: 'Batı - Düz', icon: <ArrowUpwardIcon sx={{ transform: 'rotate(-90deg)' }}/> },
    { id: 'W-L', label: 'Batı - Sola Dönüş', icon: <TurnLeftIcon sx={{ transform: 'rotate(-90deg)' }}/> },
    { id: 'W-R', label: 'Batı - Sağa Dönüş', icon: <TurnRightIcon sx={{ transform: 'rotate(-90deg)' }}/> },
];
