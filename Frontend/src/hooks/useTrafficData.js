import { useState, useEffect } from 'react';
import { doc, setDoc, addDoc, collection, query, orderBy, limit, onSnapshot } from "firebase/firestore";
import { db } from "../config/firebase";
import { initialLanes } from '../data/laneData';

export const useTrafficData = (selectedIntersection, userRole) => {
  // --- STATE YÖNETİMİ ---
  const [notification, setNotification] = useState({ open: false, message: '' });
  const [logs, setLogs] = useState([]);
  
  // Konfigürasyon Ayarları
  const [emergencyMode, setEmergencyMode] = useState(true);
  const [busPriority, setBusPriority] = useState(true);
  const [failsafeMode, setFailsafeMode] = useState('flash-yellow');
  const [simulationSpeed, setSimulationSpeed] = useState(1);
  const [controlMode, setControlMode] = useState('adaptive-ai'); 
  const [intersectionType, setIntersectionType] = useState('4-way');
  const [controllerIp, setControllerIp] = useState('192.168.1.50');

  // Trafik Politikaları
  const [rushHourMode, setRushHourMode] = useState(false);
  const [ecoMode, setEcoMode] = useState(true);
  const [pedestrianLPI, setPedestrianLPI] = useState(3);
  const [enforcementLevel, setEnforcementLevel] = useState('strict');

  // Güvenlik Ayarları
  const [firewallEnabled, setFirewallEnabled] = useState(true);
  const [cabinetDoorOpen, setCabinetDoorOpen] = useState(false);

  // Sinyal Fazları ve Şeritler
  const [phases, setPhases] = useState([
    { id: 1, name: 'Faz 1: Kuzey-Güney Düz', shortName: 'P1 (NS)', duration: 40, color: '#4caf50' },
    { id: 2, name: 'Faz 1: Geçiş (Sarı)', shortName: 'Y1', duration: 4, color: '#ffeb3b' },
    { id: 3, name: 'Faz 2: Doğu-Batı Düz', shortName: 'P2 (EW)', duration: 30, color: '#2196f3' },
    { id: 4, name: 'Faz 2: Geçiş (Sarı)', shortName: 'Y2', duration: 4, color: '#ffeb3b' },
    { id: 5, name: 'Faz 3: Yaya & Dönüşler', shortName: 'P3 (Ped)', duration: 20, color: '#9c27b0' },
  ]);
  
  const [lanes, setLanes] = useState(initialLanes);

  const isReadOnly = userRole === 'viewer';

  // --- VERİ SENKRONİZASYONU (FIREBASE LISTENER) ---
  useEffect(() => {
    if (!selectedIntersection) return;

    // 1. Konfigürasyon Dinleyicisi
    const configRef = doc(db, "config", selectedIntersection);
    const unsubConfig = onSnapshot(configRef, (docSnap) => {
      if (docSnap.exists()) {
        const data = docSnap.data();
        console.log("⚡ Canlı Veri Geldi:", data);

        if (data.intersectionType) setIntersectionType(data.intersectionType);
        if (data.network?.ip) setControllerIp(data.network.ip);
        if (data.controlMode) setControlMode(data.controlMode);
        if (data.simulationSpeed) setSimulationSpeed(data.simulationSpeed);
        
        if (data.policies) {
          setEmergencyMode(data.policies.emergency);
          setBusPriority(data.policies.busPriority);
          if (data.policies.failsafe) setFailsafeMode(data.policies.failsafe);
          if (data.policies.rushHour !== undefined) setRushHourMode(data.policies.rushHour);
          if (data.policies.ecoMode !== undefined) setEcoMode(data.policies.ecoMode);
          if (data.policies.pedestrianLPI !== undefined) setPedestrianLPI(data.policies.pedestrianLPI);
          if (data.policies.enforcementLevel !== undefined) setEnforcementLevel(data.policies.enforcementLevel);
        }

        if (data.security) {
          if (data.security.firewall !== undefined) setFirewallEnabled(data.security.firewall);
          if (data.security.cabinetDoor !== undefined) setCabinetDoorOpen(data.security.cabinetDoor);
        }

        if (data.phases) setPhases(data.phases);
      }
    });

    // 2. Log Kayıtları Dinleyicisi
    const logsQuery = query(collection(db, "logs"), orderBy("date", "desc"), limit(5));
    const unsubLogs = onSnapshot(logsQuery, (snapshot) => {
      const loadedLogs = [];
      snapshot.forEach((doc) => {
        loadedLogs.push({ id: doc.id, ...doc.data() });
      });
      setLogs(loadedLogs);
    });

    return () => {
      unsubConfig();
      unsubLogs();
    };
  }, [selectedIntersection]);

  // --- VERİ KAYDETME İŞLEMİ ---
  const saveToFirebase = async () => {
    if (isReadOnly) return;

    try {
      const timestamp = new Date().toISOString();
      const configData = {
        updatedAt: timestamp,
        updatedBy: userRole,
        intersectionId: "JN-01", // Bunu dinamik yapmak gerekebilir ama şimdilik sabit
        intersectionType,
        network: { ip: controllerIp, port: 8080 },
        controlMode,
        lanes: lanes.map(l => l.id), 
        policies: { 
          emergency: emergencyMode, 
          busPriority: busPriority,
          failsafe: failsafeMode,
          rushHour: rushHourMode,
          ecoMode: ecoMode,
          pedestrianLPI: pedestrianLPI,
          enforcementLevel: enforcementLevel
        },
        security: {
          firewall: firewallEnabled,
          cabinetDoor: cabinetDoorOpen
        },
        phases,
        simulationSpeed
      };

      await setDoc(doc(db, "config", selectedIntersection), configData);

      await addDoc(collection(db, "logs"), {
        date: timestamp,
        user: userRole,
        intersection: selectedIntersection,
        action: "Configuration Updated",
        details: `Mode: ${controlMode}, Speed: ${simulationSpeed}x`
      });

      setNotification({ open: true, message: 'Ayarlar ve Log kaydı başarıyla işlendi! 📝' });
    } catch (error) {
      console.error("Hata:", error);
      setNotification({ open: true, message: 'Hata! Kayıt başarısız oldu.' });
    }
  };

  return {
    // State
    notification, setNotification,
    logs,
    emergencyMode, setEmergencyMode,
    busPriority, setBusPriority,
    failsafeMode, setFailsafeMode,
    simulationSpeed, setSimulationSpeed,
    controlMode, setControlMode,
    intersectionType, setIntersectionType,
    controllerIp, setControllerIp,
    rushHourMode, setRushHourMode,
    ecoMode, setEcoMode,
    pedestrianLPI, setPedestrianLPI,
    enforcementLevel, setEnforcementLevel,
    firewallEnabled, setFirewallEnabled,
    cabinetDoorOpen, setCabinetDoorOpen,
    phases, setPhases,
    lanes, setLanes,
    
    // Actions
    saveToFirebase,
    isReadOnly
  };
};
