import { useCallback, useEffect, useState } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { pedidosApi, restaurantesApi } from '../api/cliente';
import type { Plato, Restaurante } from '../api/tipos';
import Carrito, { type DatosConfirmacion } from '../componentes/Carrito';
import { Cargando, Encabezado, MensajeError } from '../componentes/Comunes';
import PlatoModal from '../componentes/PlatoModal';
import { agregarLinea, armarPedido, type LineaCarrito } from '../util/carrito';
import { formatoPesos, nombreCategoria, ordenCategorias } from '../util/formato';
import { useCliente } from '../util/useCliente';

export default function MenuPage() {
  const restauranteId = Number(useParams().id);
  const navegar = useNavigate();
  const [cliente, guardarCliente] = useCliente();

  const [restaurante, setRestaurante] = useState<Restaurante | null>(null);
  const [menu, setMenu] = useState<Plato[] | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [errorPedido, setErrorPedido] = useState<string | null>(null);
  const [platoAbierto, setPlatoAbierto] = useState<Plato | null>(null);
  const [lineas, setLineas] = useState<LineaCarrito[]>([]);
  const [enviando, setEnviando] = useState(false);

  const cargar = useCallback(() => {
    setError(null);
    Promise.all([restaurantesApi.obtener(restauranteId), restaurantesApi.menu(restauranteId)])
      .then(([r, m]) => {
        setRestaurante(r);
        setMenu(m);
      })
      .catch((e) => setError(e.message));
  }, [restauranteId]);
  useEffect(cargar, [cargar]);

  if (error) return <MensajeError mensaje={error} alReintentar={cargar} />;
  if (!restaurante || !menu) return <Cargando texto="Cargando menú…" />;

  const confirmar = async (datos: DatosConfirmacion) => {
    setEnviando(true);
    setErrorPedido(null);
    guardarCliente(datos.cliente);
    try {
      const pedido = await pedidosApi.crear(armarPedido(restauranteId, lineas, datos.cliente, datos.tipo, datos.direccion));
      navegar(`/pedidos/${pedido.id}`);
    } catch (e) {
      setErrorPedido((e as Error).message);
      cargar(); // La disponibilidad pudo cambiar: se refresca el menú.
    } finally {
      setEnviando(false);
    }
  };

  return (
    <>
      <Link to="/" className="volver">← Restaurantes</Link>
      <Encabezado
        titulo={restaurante.nombre}
        subtitulo={`${restaurante.direccion} · ${restaurante.estado === 'ABIERTO' ? 'Abierto' : 'Cerrado'}`}
      />
      {restaurante.estado !== 'ABIERTO' && (
        <div className="aviso">Este restaurante está cerrado y no está recibiendo pedidos.</div>
      )}

      <div className="dos-columnas">
        <section>
          {ordenCategorias.map((categoria) => {
            const platos = menu.filter((p) => p.categoria === categoria);
            if (platos.length === 0) return null;
            return (
              <div key={categoria} className="categoria">
                <h2>{nombreCategoria[categoria]}</h2>
                <div className="lista-platos">
                  {platos.map((plato) => (
                    <button
                      key={plato.id}
                      className="tarjeta plato"
                      disabled={!plato.disponible}
                      onClick={() => setPlatoAbierto(plato)}
                    >
                      <div>
                        <h3>{plato.nombre}</h3>
                        {plato.descripcion && <p className="tenue">{plato.descripcion}</p>}
                        {plato.extras.length > 0 && (
                          <p className="pequeno tenue">Extras: {plato.extras.map((e) => e.nombre).join(', ')}</p>
                        )}
                      </div>
                      <div className="plato__lateral">
                        <span className="precio">{formatoPesos(plato.precio)}</span>
                        {plato.disponible ? (
                          plato.unidadesDisponibles !== null && plato.unidadesDisponibles <= 5 && (
                            <span className="etiqueta etiqueta--alerta">Quedan {plato.unidadesDisponibles}</span>
                          )
                        ) : (
                          <span className="etiqueta etiqueta--cerrado">Agotado</span>
                        )}
                      </div>
                    </button>
                  ))}
                </div>
              </div>
            );
          })}
          {menu.length === 0 && <p className="vacio">Este restaurante todavía no tiene platos.</p>}
        </section>

        <div className="columna-fija">
          {errorPedido && <MensajeError mensaje={errorPedido} />}
          <Carrito
            lineas={lineas}
            alCambiarLineas={setLineas}
            cliente={cliente}
            enviando={enviando || restaurante.estado !== 'ABIERTO'}
            alConfirmar={confirmar}
          />
        </div>
      </div>

      {platoAbierto && (
        <PlatoModal
          plato={platoAbierto}
          alCerrar={() => setPlatoAbierto(null)}
          alAgregar={(seleccion) => {
            setLineas((actuales) => agregarLinea(actuales, { plato: platoAbierto, ...seleccion }));
            setPlatoAbierto(null);
          }}
        />
      )}
    </>
  );
}
