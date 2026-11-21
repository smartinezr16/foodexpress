import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

// En desarrollo (npm run dev) Vite reenvía las llamadas a cada microservicio,
// igual que lo hace Nginx en Docker. Así el navegador habla con un solo origen y no hay CORS.
const RESTAURANTES = process.env.RESTAURANTES_URL ?? 'http://localhost:8081';
const PEDIDOS = process.env.PEDIDOS_URL ?? 'http://localhost:8082';
const NOTIFICACIONES = process.env.NOTIFICACIONES_URL ?? 'http://localhost:8083';

export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      '/api/restaurantes': RESTAURANTES,
      '/api/platos': RESTAURANTES,
      '/api/pedidos': PEDIDOS,
      '/api/notificaciones': NOTIFICACIONES,
      '/ws': { target: NOTIFICACIONES, ws: true },
    },
  },
});
