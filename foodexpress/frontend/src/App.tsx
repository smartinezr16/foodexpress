import { NavLink, Route, Routes } from 'react-router-dom';
import CocinaPage from './paginas/CocinaPage';
import MenuPage from './paginas/MenuPage';
import MisPedidosPage from './paginas/MisPedidosPage';
import PedidoPage from './paginas/PedidoPage';
import RestaurantesPage from './paginas/RestaurantesPage';

export default function App() {
  return (
    <>
      <header className="barra">
        <div className="barra__contenido">
          <NavLink to="/" className="marca">
            <span className="marca__logo" aria-hidden="true">FE</span>
            FoodExpress
          </NavLink>
          <nav className="navegacion">
            <NavLink to="/" end>Restaurantes</NavLink>
            <NavLink to="/mis-pedidos">Mis pedidos</NavLink>
            <NavLink to="/cocina">Cocina</NavLink>
          </nav>
        </div>
      </header>

      <main className="pagina">
        <Routes>
          <Route path="/" element={<RestaurantesPage />} />
          <Route path="/restaurantes/:id" element={<MenuPage />} />
          <Route path="/pedidos/:id" element={<PedidoPage />} />
          <Route path="/mis-pedidos" element={<MisPedidosPage />} />
          <Route path="/cocina" element={<CocinaPage />} />
          <Route path="/cocina/:restauranteId" element={<CocinaPage />} />
          <Route path="*" element={<p className="vacio">Esta página no existe.</p>} />
        </Routes>
      </main>
    </>
  );
}
