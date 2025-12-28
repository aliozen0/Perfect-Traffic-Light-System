import React, { useRef, useEffect } from 'react';
import { Paper } from '@mui/material';

const CAR_WIDTH = 24;
const CAR_LENGTH = 44;
const LANE_WIDTH = 60;
const ROAD_WIDTH = 140;

// Easing for smooth transitions
const lerp = (start, end, t) => start * (1 - t) + end * t;

class Car {
    constructor(id, direction) {
        this.id = id;
        this.direction = direction;
        this.speed = Math.random() * 2 + 3; // Initial Speed
        this.maxSpeed = this.speed + 1;
        this.acceleration = 0.15;
        this.brakes = 0.3;
        this.x = 0;
        this.y = 0;
        this.stopped = false;
        this.waitTime = 0;

        // Visual Props
        this.color = this.getRandomColor();
        this.type = Math.random() > 0.85 ? 'TRUCK' : 'CAR'; // Simple type
        this.length = this.type === 'TRUCK' ? 70 : 44;

        this.initPosition();
    }

    getRandomColor() {
        const colors = [
            '#e53935', // Red
            '#1e88e5', // Blue
            '#43a047', // Green
            '#fdd835', // Yellow
            '#fafafa', // White
            '#212121', // Black
            '#8e24aa'  // Purple
        ];
        return colors[Math.floor(Math.random() * colors.length)];
    }

    initPosition() {
        const laneOffset = (LANE_WIDTH / 2); // Center of lane
        // Offset for variety in lane? For now single lane per direction for simplicity
        const jitter = (Math.random() - 0.5) * 10;

        // Map Dimensions: 800x600
        // Center: 400, 300

        switch (this.direction) {
            case 'N': // Going North (Up) - Start Bottom Right
                this.x = 400 + LANE_WIDTH / 2 + jitter;
                this.y = 650;
                break;
            case 'S': // Going South (Down) - Start Top Left
                this.x = 400 - LANE_WIDTH / 2 + jitter;
                this.y = -60;
                break;
            case 'E': // Going East (Right) - Start Left Bottom
                this.x = -60;
                this.y = 300 + LANE_WIDTH / 2 + jitter;
                break;
            case 'W': // Going West (Left) - Start Right Top
                this.x = 860;
                this.y = 300 - LANE_WIDTH / 2 + jitter;
                break;
            default: break;
        }
    }

    update(lights, cars) {
        // Stop Lines
        // NS: Center Y=300. RoadWidth=140. S-Bound Stop: Y=230 (300-70). N-Bound Stop: Y=370 (300+70).
        // EW: Center X=400. RoadWidth=140. E-Bound Stop: X=330 (400-70). W-Bound Stop: X=470 (400+70).

        const stopS = 300 - ROAD_WIDTH / 2 - 10;
        const stopN = 300 + ROAD_WIDTH / 2 + 10;
        const stopE = 400 - ROAD_WIDTH / 2 - 10;
        const stopW = 400 + ROAD_WIDTH / 2 + 10;

        let shouldStop = false;

        // Traffic Light Logic
        // Buffer: Stop if within 120px approaching line
        const buffer = 140;

        if (this.direction === 'S') { // Moving +Y
            if (this.y < stopS && this.y > stopS - buffer && (lights.NS === 'red' || lights.NS === 'yellow')) shouldStop = true;
        }
        else if (this.direction === 'N') { // Moving -Y
            if (this.y > stopN && this.y < stopN + buffer && (lights.NS === 'red' || lights.NS === 'yellow')) shouldStop = true;
        }
        else if (this.direction === 'E') { // Moving +X
            if (this.x < stopE && this.x > stopE - buffer && (lights.EW === 'red' || lights.EW === 'yellow')) shouldStop = true;
        }
        else if (this.direction === 'W') { // Moving -X
            if (this.x > stopW && this.x < stopW + buffer && (lights.EW === 'red' || lights.EW === 'yellow')) shouldStop = true;
        }

        // Leader Safety Logic
        const safeDist = this.type === 'TRUCK' ? 80 : 60;
        for (let other of cars) {
            if (other === this) continue;
            if (other.direction !== this.direction) continue;

            let dist = Infinity;

            if (this.direction === 'S' && other.y > this.y) dist = other.y - this.y;
            if (this.direction === 'N' && other.y < this.y) dist = this.y - other.y;
            if (this.direction === 'E' && other.x > this.x) dist = other.x - this.x;
            if (this.direction === 'W' && other.x < this.x) dist = this.x - other.x;

            if (dist < safeDist && dist > 0) shouldStop = true;
        }

        // Physics
        if (shouldStop) {
            this.speed = Math.max(0, this.speed - this.brakes);
            this.stopped = (this.speed < 0.1);
            if (this.stopped) this.waitTime += 1 / 60;
        } else {
            this.speed = Math.min(this.maxSpeed, this.speed + this.acceleration);
            this.stopped = false;
        }

        // Move
        if (this.direction === 'S') this.y += this.speed;
        if (this.direction === 'N') this.y -= this.speed;
        if (this.direction === 'E') this.x += this.speed;
        if (this.direction === 'W') this.x -= this.speed;
    }

    draw(ctx) {
        ctx.save();
        ctx.translate(this.x, this.y);

        if (this.direction === 'N') ctx.rotate(Math.PI);
        if (this.direction === 'E') ctx.rotate(-Math.PI / 2);
        if (this.direction === 'W') ctx.rotate(Math.PI / 2);
        // S is 0 rotation (Down)

        // Shadow
        ctx.fillStyle = 'rgba(0,0,0,0.3)';
        ctx.fillRect(-CAR_WIDTH / 2 + 4, -this.length / 2 + 4, CAR_WIDTH, this.length);

        // Body
        ctx.fillStyle = this.color;
        // Rounded Rect
        ctx.beginPath();
        const r = 4;
        const w = CAR_WIDTH;
        const h = this.length;
        const x = -w / 2;
        const y = -h / 2;
        ctx.moveTo(x + r, y);
        ctx.arcTo(x + w, y, x + w, y + h, r);
        ctx.arcTo(x + w, y + h, x, y + h, r);
        ctx.arcTo(x, y + h, x, y, r);
        ctx.arcTo(x, y, x + w, y, r);
        ctx.fill();

        // Roof/Windshield (Darker area)
        ctx.fillStyle = 'rgba(0,0,0,0.3)';
        ctx.fillRect(-w / 2 + 2, -h / 4, w - 4, h / 2);

        // Headlights (Front is +Y in local coords because S is default down)
        // Wait, for S (moving +Y), Front is +Y.
        ctx.fillStyle = '#ffeb3b';
        ctx.shadowColor = '#ffeb3b';
        ctx.shadowBlur = 10;
        ctx.fillRect(-w / 2 + 2, h / 2 - 2, 6, 4);
        ctx.fillRect(w / 2 - 8, h / 2 - 2, 6, 4);
        ctx.shadowBlur = 0;

        // Brake Lights (Back is -Y)
        if (this.stopped || this.speed < 1) {
            ctx.fillStyle = '#ff1744';
            ctx.shadowColor = '#ff1744';
            ctx.shadowBlur = 15;
        } else {
            ctx.fillStyle = '#b71c1c';
            ctx.shadowBlur = 0;
        }
        ctx.fillRect(-w / 2 + 2, -h / 2 - 1, 6, 3);
        ctx.fillRect(w / 2 - 8, -h / 2 - 1, 6, 3);

        ctx.restore();
    }
}

const TrafficMap = ({ lights, spawnRate = 1.0, onStatsUpdate, onDemand }) => {
    const canvasRef = useRef(null);

    // STATE IN REFS (No Re-renders)
    const carsRef = useRef([]);
    const frameRef = useRef(0);
    const carIdRef = useRef(0);
    const lightGlowRef = useRef({ NS: 0, EW: 0 }); // 0 to 1 intensity

    const lightsRef = useRef(lights);
    useEffect(() => { lightsRef.current = lights; }, [lights]);

    useEffect(() => {
        const canvas = canvasRef.current;
        const ctx = canvas.getContext('2d');
        let animationId;

        const render = () => {
            const w = canvas.width;
            const h = canvas.height;
            const lightsVal = lightsRef.current;

            // --- 1. DRAW BACKGROUND ---
            ctx.fillStyle = '#263238'; // Dark city ground
            ctx.fillRect(0, 0, w, h);

            // --- 2. DRAW ROADS ---
            drawRoads(ctx, w, h);

            // --- 3. DRAW CROSSWALKS ---
            drawCrosswalks(ctx, w, h);

            // --- 4. LOGIC & DRAW CARS ---
            // Spawn?
            if (frameRef.current % Math.max(10, Math.floor(60 / spawnRate)) === 0) {
                if (Math.random() > 0.3) {
                    const dirs = ['N', 'S', 'E', 'W'];
                    const dir = dirs[Math.floor(Math.random() * dirs.length)];
                    // Check spawn collision
                    const isClear = !carsRef.current.some(c =>
                        c.direction === dir &&
                        (Math.abs(c.x - (dir === 'N' || dir === 'S' ? 400 : -100)) < 60 || Math.abs(c.y - (dir === 'E' || dir === 'W' ? 300 : -100)) < 60) // Rough check
                    );

                    // Simply add if 'last car' is far enough
                    // Better: check distance 
                    carsRef.current.push(new Car(carIdRef.current++, dir));
                }
            }

            // Update
            let totalWait = 0;
            carsRef.current.forEach(car => {
                car.update(lightsVal, carsRef.current);
                car.draw(ctx);
                totalWait += car.waitTime;

                // --- ON DEMAND DETECTION (PERFECT AI) ---
                if (onDemand) {
                    const stopS = 300 - ROAD_WIDTH / 2;
                    const stopN = 300 + ROAD_WIDTH / 2;
                    const stopE = 400 - ROAD_WIDTH / 2;
                    const stopW = 400 + ROAD_WIDTH / 2;
                    const demandZone = 250;

                    let demandDir = null;
                    if (car.direction === 'S' && car.y < stopS && car.y > stopS - demandZone && lightsVal.NS !== 'green') demandDir = 'NS';
                    if (car.direction === 'N' && car.y > stopN && car.y < stopN + demandZone && lightsVal.NS !== 'green') demandDir = 'NS';
                    if (car.direction === 'E' && car.x < stopE && car.x > stopE - demandZone && lightsVal.EW !== 'green') demandDir = 'EW';
                    if (car.direction === 'W' && car.x > stopW && car.x < stopW + demandZone && lightsVal.EW !== 'green') demandDir = 'EW';

                    if (demandDir) {
                        onDemand(demandDir);
                        // VISUAL: Draw Detection Ray
                        ctx.strokeStyle = '#00e676';
                        ctx.lineWidth = 1;
                        ctx.globalAlpha = 0.4;
                        ctx.beginPath();
                        ctx.moveTo(car.x, car.y);
                        ctx.lineTo(w / 2, h / 2); // Center
                        ctx.stroke();
                        ctx.globalAlpha = 1.0;

                        // AI Tag
                        ctx.fillStyle = '#00e676';
                        ctx.font = '10px monospace';
                        ctx.fillText("AI_LOCK", car.x + 15, car.y - 15);
                    }
                }
            });

            // Cleanup off-screen
            carsRef.current = carsRef.current.filter(c =>
                c.x > -100 && c.x < w + 100 && c.y > -100 && c.y < h + 100
            );

            // --- 5. DRAW LIGHTS & GLOW ---
            drawTrafficLights(ctx, w, h, lightsVal, lightGlowRef.current);

            // Stats Callback (Throttled)
            if (frameRef.current % 30 === 0 && onStatsUpdate) {
                onStatsUpdate({
                    activeCars: carsRef.current.length,
                    totalWait: totalWait,
                    totalThroughput: carIdRef.current
                });
            }

            frameRef.current++;
            animationId = requestAnimationFrame(render);
        };

        render();
        return () => cancelAnimationFrame(animationId);
    }, [spawnRate, onDemand]);

    return (
        <Paper elevation={6} sx={{
            border: '8px solid #1a1a1a',
            borderRadius: 4,
            overflow: 'hidden',
            bgcolor: '#000',
            boxShadow: '0 0 50px rgba(0,0,0,0.8)'
        }}>
            <canvas ref={canvasRef} width={800} height={600} style={{ width: '100%', display: 'block' }} />
        </Paper>
    );
};

// --- VISUAL HELPERS ---

function drawRoads(ctx, w, h) {
    ctx.fillStyle = '#37474f'; // Asphalt
    // NS
    ctx.fillRect(w / 2 - ROAD_WIDTH / 2, 0, ROAD_WIDTH, h);
    // EW
    ctx.fillRect(0, h / 2 - ROAD_WIDTH / 2, w, ROAD_WIDTH);

    // Intersection
    ctx.fillStyle = '#455a64'; // Lighter intersection
    ctx.fillRect(w / 2 - ROAD_WIDTH / 2, h / 2 - ROAD_WIDTH / 2, ROAD_WIDTH, ROAD_WIDTH);

    // Markings
    ctx.strokeStyle = '#cfd8dc';
    ctx.lineWidth = 2;
    ctx.setLineDash([20, 30]); // Dashed Line

    // NS Divider
    ctx.beginPath();
    ctx.moveTo(w / 2, 0); ctx.lineTo(w / 2, h / 2 - ROAD_WIDTH / 2);
    ctx.moveTo(w / 2, h / 2 + ROAD_WIDTH / 2); ctx.lineTo(w / 2, h);
    ctx.stroke();

    // EW Divider
    ctx.beginPath();
    ctx.moveTo(0, h / 2); ctx.lineTo(w / 2 - ROAD_WIDTH / 2, h / 2);
    ctx.moveTo(w / 2 + ROAD_WIDTH / 2, h / 2); ctx.lineTo(w, h / 2);
    ctx.stroke();

    // Shoulders (Yellow Solid)
    ctx.strokeStyle = '#fbc02d';
    ctx.lineWidth = 4;
    ctx.setLineDash([]);

    // Borders
    ctx.beginPath();
    ctx.moveTo(w / 2 - ROAD_WIDTH / 2, 0); ctx.lineTo(w / 2 - ROAD_WIDTH / 2, h); // Left NS
    ctx.moveTo(w / 2 + ROAD_WIDTH / 2, 0); ctx.lineTo(w / 2 + ROAD_WIDTH / 2, h); // Right NS
    ctx.moveTo(0, h / 2 - ROAD_WIDTH / 2); ctx.lineTo(w, h / 2 - ROAD_WIDTH / 2); // Top EW
    ctx.moveTo(0, h / 2 + ROAD_WIDTH / 2); ctx.lineTo(w, h / 2 + ROAD_WIDTH / 2); // Bottom EW
    ctx.stroke();
}

function drawCrosswalks(ctx, w, h) {
    ctx.fillStyle = '#eceff1';
    const cwSize = 40;
    const rw = ROAD_WIDTH;
    const cx = w / 2;
    const cy = h / 2;

    // Draw Zebra stripes for each side entry
    // North Entry (Top)
    for (let i = 0; i < ROAD_WIDTH; i += 20) {
        ctx.fillRect(cx - rw / 2 + i, cy - rw / 2 - cwSize, 12, cwSize);
    }
    // South Entry (Bottom)
    for (let i = 0; i < ROAD_WIDTH; i += 20) {
        ctx.fillRect(cx - rw / 2 + i, cy + rw / 2, 12, cwSize);
    }
    // West Entry (Left)
    for (let i = 0; i < ROAD_WIDTH; i += 20) {
        ctx.fillRect(cx - rw / 2 - cwSize, cy - rw / 2 + i, cwSize, 12);
    }
    // East Entry (Right)
    for (let i = 0; i < ROAD_WIDTH; i += 20) {
        ctx.fillRect(cx + rw / 2, cy - rw / 2 + i, cwSize, 12);
    }
}

function drawTrafficLights(ctx, w, h, lights, glow) {
    // Helper to draw a single light box
    const drawBox = (x, y, color, label) => {
        // Post
        ctx.fillStyle = '#212121';
        ctx.fillRect(x - 2, y, 4, 30); // pole?

        // Box
        ctx.fillStyle = '#000';
        ctx.shadowColor = '#000';
        ctx.shadowBlur = 5;
        ctx.fillRect(x - 25, y - 60, 50, 100); // Housing

        // Lights
        const lightY = [y - 45, y - 10, y + 25]; // R, Y, G

        ['red', 'yellow', 'green'].forEach((c, idx) => {
            const active = (color === c);
            const ly = lightY[idx];

            ctx.beginPath();
            ctx.arc(x, ly, 12, 0, Math.PI * 2);

            if (active) {
                ctx.fillStyle = c === 'red' ? '#ff1744' : c === 'yellow' ? '#ffeb3b' : '#00e676';
                ctx.shadowColor = ctx.fillStyle;
                ctx.shadowBlur = 30; // Glow
            } else {
                ctx.fillStyle = '#333';
                ctx.shadowBlur = 0;
            }
            ctx.fill();

            // Bloom (Extra overlay)
            if (active) {
                const grad = ctx.createRadialGradient(x, ly, 0, x, ly, 60);
                grad.addColorStop(0, ctx.fillStyle);
                grad.addColorStop(1, 'rgba(0,0,0,0)');
                ctx.fillStyle = grad;
                ctx.globalAlpha = 0.3;
                ctx.beginPath(); ctx.arc(x, ly, 60, 0, Math.PI * 2); ctx.fill();
                ctx.globalAlpha = 1.0;
            }
        });

        ctx.shadowBlur = 0;
    };

    const cx = w / 2;
    const cy = h / 2;
    const rw = ROAD_WIDTH;
    const offset = 80;

    // NS Lights (Vertical)
    // For S-bound (Top-Left)
    drawBox(cx - rw / 2 - 40, cy - rw / 2 - 40, lights.NS);
    // For N-bound (Bottom-Right)
    drawBox(cx + rw / 2 + 40, cy + rw / 2 + 40, lights.NS);

    // EW Lights (Horizontal)
    // For E-bound (Bottom-Left)
    // Wait, visual position depends on "facing". E-bound cars see light on RIGHT side? Or overhead?
    // Let's put them on corners.
    // E-bound light (Bottom Left Corner)
    drawBox(cx - rw / 2 - 40, cy + rw / 2 + 40, lights.EW);
    // W-bound light (Top Right Corner)
    drawBox(cx + rw / 2 + 40, cy - rw / 2 - 40, lights.EW);
}

export default TrafficMap;
