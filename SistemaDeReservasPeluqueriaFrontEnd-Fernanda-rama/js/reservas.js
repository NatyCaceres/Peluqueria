// ====== Config ======
const API = "http://localhost:8080";

// Headers con JWT
const authHeaders = () => {
  const t = localStorage.getItem("token");
  return t ? { Authorization: `Bearer ${t}` } : {};
};

// ====== Estado ======
let servicios = [];
let trabajadores = [];
let reservasTrabajador = [];
let disponibilidadesTrabajador = [];
let historialReservas = [];

let servicioSeleccionado = null;
let idTrabajadorSeleccionado = null;
let fechaSeleccionada = null;
let horarioSeleccionado = null;

const selServicio = document.getElementById("select-servicio");
const selTrabajador = document.getElementById("select-trabajador");
const btnRefrescar = document.getElementById("btn-refrescar");
const lblFecha = document.getElementById("fecha-seleccionada");
const contHorarios = document.getElementById("horarios");
const btnConfirmar = document.getElementById("btn-confirmar");
const historialBody = document.getElementById("historial-body");
const nombreUsuario = document.getElementById("nombreUsuario");
const btnCerrarSesion = document.getElementById("btn-cerrar-sesion");

let calendar;
const hoy = new Date();
const hoyStr = hoy.toISOString().split("T")[0];

// ====== Helpers ======
function parseTimeToMinutes(t) {
  const [hh, mm] = t.split(":").map(Number);
  return hh * 60 + mm;
}
function toHHMM(minutes) {
  const h = Math.floor(minutes / 60).toString().padStart(2, "0");
  const m = (minutes % 60).toString().padStart(2, "0");
  return `${h}:${m}`;
}
function addMinutes(hhmm, delta) {
  const [h, m] = hhmm.split(":").map(Number);
  const total = h * 60 + m + delta;
  return toHHMM(total);
}
const pad2 = (n) => n.toString().padStart(2, "0");
const formatDateToHM = (date) => `${pad2(date.getHours())}:${pad2(date.getMinutes())}`;
function overlaps(aStart, aEnd, bStart, bEnd) { return aStart < bEnd && bStart < aEnd; }

// ====== Fetch API ======
async function getServiciosActivos() {
  const res = await fetch(`${API}/servicios/activos`, { headers: authHeaders() });
  if (!res.ok) throw new Error("No se pudo cargar servicios");
  return res.json();
}
async function getTrabajadoresPorServicio(idServicio) {
  const res = await fetch(`${API}/trabajadores-servicios/por-servicio/${idServicio}`, { headers: authHeaders() });
  if (!res.ok) throw new Error("No se pudo cargar trabajadores");
  return res.json();
}
async function getReservasTrabajador(idTrabajador) {
  const res = await fetch(`${API}/reservas/trabajador/${idTrabajador}`, { headers: authHeaders() });
  if (!res.ok) throw new Error("No se pudo cargar reservas del trabajador");
  return res.json();
}
async function getDisponibilidadesTrabajador(idTrabajador) {
  const res = await fetch(`${API}/horarios-disponibles/trabajador/${idTrabajador}`, { headers: authHeaders() });
  if (!res.ok) throw new Error("No se pudo cargar disponibilidades");
  return res.json();
}
async function getHistorialReservas() {
  const res = await fetch(`${API}/reservas/mis-reservas`, { headers: authHeaders() });
  if (!res.ok) throw new Error("No se pudo cargar historial");
  return res.json();
}
async function postCrearReserva(payload) {
  const res = await fetch(`${API}/reservas/crear`, {
    method: "POST",
    headers: { "Content-Type": "application/json", ...authHeaders() },
    body: JSON.stringify(payload)
  });
  if (!res.ok) throw new Error(await res.text() || `Error HTTP ${res.status}`);
  return res.json();
}

// ====== Render UI ======
function initCalendar() {
  const calendarEl = document.getElementById("calendar");
  calendar = new FullCalendar.Calendar(calendarEl, {
    initialView: "dayGridMonth",
    height: "auto",
    selectable: true,
    locale: "es",
    validRange: { start: hoyStr },
    eventTimeFormat: { hour: '2-digit', minute: '2-digit', hour12: false },

    eventContent: (arg) => {
      const ev = arg.event;
      const tipo = ev.extendedProps?.tipo;
      const fechaEvento = arg.event.startStr;
      if (fechaEvento < hoyStr) return { html: `<span class="text-muted">Pasado</span>` };
      if (tipo === 'reserva') return { html: `<b>${formatDateToHM(ev.start)}</b> <span>&nbsp;Ocupado</span>` };
      if (tipo === 'disponible') return { html: `<span>Disponible</span>` };
      return { html: ev.title || '' };
    },

    dateClick: (info) => {
      if (!idTrabajadorSeleccionado || !servicioSeleccionado) {
        alert("Primero elige servicio y trabajador.");
        return;
      }
      fechaSeleccionada = info.dateStr;
      lblFecha.textContent = fechaSeleccionada;
      renderHorariosDeFecha(fechaSeleccionada);
    },

    events: []
  });
  calendar.render();
}

function pintarEventosCalendario() {
  if (!calendar) return;
  calendar.removeAllEvents();

  reservasTrabajador.forEach(r => {
    const start = `${r.fecha}T${r.horaInicio?.substring(0,5) || "00:00"}`;
    calendar.addEvent({ title:"Ocupado", start, color:"#dc3545", extendedProps:{tipo:'reserva'} });
  });

  const diasDisponibles = new Set(disponibilidadesTrabajador.map(d => d.fecha).filter(f => f >= hoyStr));
  diasDisponibles.forEach(f => {
    calendar.addEvent({ title:"Disponible", start:f, allDay:true, color:"#198754", extendedProps:{tipo:'disponible'} });
  });
}

// --- reemplazar renderHorariosDeFecha completo ---
function renderHorariosDeFecha(yyyy_mm_dd) {
  contHorarios.innerHTML = "";
  horarioSeleccionado = null;
  btnConfirmar.disabled = true;

  const durMin = servicioSeleccionado?.duracionEstimadaMinutos || 60;
  const bloquesDia = disponibilidadesTrabajador.filter(d => d.fecha === yyyy_mm_dd);
  if (!bloquesDia.length) {
    contHorarios.innerHTML = `<div class="text-muted">No hay disponibilidad para este día.</div>`;
    return;
  }

  const reservasDia = reservasTrabajador
    .filter(r => r.fecha === yyyy_mm_dd)
    .map(r => ({ ini: parseTimeToMinutes(r.horaInicio), fin: parseTimeToMinutes(r.horaFin) }));

  const botones = [];
  const minutosAhora = new Date().getHours() * 60 + new Date().getMinutes();

  bloquesDia.forEach(b => {
    const inicio = parseTimeToMinutes(b.horaInicio);
    const fin = parseTimeToMinutes(b.horaFin);

    for (let t = inicio; t + durMin <= fin; t += durMin) {
      if (yyyy_mm_dd === hoyStr && t < minutosAhora) continue;

      const tFin = t + durMin;
      const ocupado = reservasDia.some(rr => overlaps(t, tFin, rr.ini, rr.fin));
      const hhmm = toHHMM(t);
      const btn = document.createElement("button");

      if (ocupado) {
        btn.className = "btn btn-outline-secondary horario-btn disabled";
        btn.innerHTML = `<s>${hhmm}</s>`;
        btn.disabled = true;
        btn.title = "Horario ocupado";
      } else {
        btn.className = "btn btn-outline-success horario-btn";
        btn.textContent = hhmm;
        btn.addEventListener("click", () => {
          document.querySelectorAll(".horario-btn").forEach(b => b.classList.remove("active"));
          btn.classList.add("active");
          horarioSeleccionado = hhmm;
          btnConfirmar.disabled = false;
        });
      }

      botones.push(btn);
    }
  });

  if (!botones.length)
    contHorarios.innerHTML = `<div class="text-muted">No hay horarios disponibles.</div>`;
  else botones.forEach(b => contHorarios.appendChild(b));
}

function renderHistorial() {
  historialBody.innerHTML = "";

  // 🔹 Mostrar solo las reservas que NO estén canceladas
  const reservasActivas = historialReservas.filter(
    r => r.estadoReserva && r.estadoReserva.toUpperCase() !== "CANCELADA"
  );

  reservasActivas.forEach(r => {
    const tr = document.createElement("tr");
    const esFuturo = r.fecha >= hoyStr;

    const servicioNom = r.servicioNombre || r.servicio?.nombreServicio || "—";
    const trabajadorNom = r.trabajadorNombre || `${r.trabajador?.nombre || ""} ${r.trabajador?.apellido || ""}`.trim() || "—";

    tr.innerHTML = `
      <td>${servicioNom}</td>
      <td>${trabajadorNom}</td>
      <td>${r.fecha}</td>
      <td>${r.horaInicio}</td>
      <td>${r.horaFin}</td>
      <td>
        ${esFuturo
          ? `<button class="btn btn-sm btn-warning btn-modificar">Modificar</button>
             <button class="btn btn-sm btn-danger btn-cancelar">Cancelar</button>`
          : "-"}
      </td>
    `;

    historialBody.appendChild(tr);

    if (esFuturo) {
      tr.querySelector(".btn-cancelar").addEventListener("click", async () => {
        if (!confirm("¿Cancelar esta reserva?")) return;
        try {
          const resp = await fetch(`${API}/reservas/${r.idReserva}/cancelar`, {
            method: "DELETE",
            headers: authHeaders()
          });
          if (!resp.ok) {
            const msg = await resp.text();
            throw new Error(msg);
          }
          alert("✅ Reserva cancelada correctamente.");
          cargarHistorial(); // 🔁 Recargar tabla actualizada
        } catch (e) {
          alert("❌ " + e.message);
        }
      });

      tr.querySelector(".btn-modificar").addEventListener("click", () => {
        servicioSeleccionado = servicios.find(s => s.nombreServicio === servicioNom);
        selServicio.value = servicioSeleccionado.idServicio;
        selServicio.dispatchEvent(new Event("change"));
        alert("Selecciona una nueva fecha y horario para modificar tu reserva.");
        localStorage.setItem("reservaModificando", r.idReserva);
      });
    }
  });
}

async function cargarHistorial() {
  try {
    historialReservas = await getHistorialReservas();
    console.log("Historial recibido:", historialReservas); // 👈 Muestra lo que llega del backend
    renderHistorial();
  } catch (e) {
    console.error("Error al inicializar el dashboard:", e);
    alert("Error al inicializar el dashboard. No se pudo cargar historial");
  }
}


// ====== Eventos UI ======
selServicio.addEventListener("change", async ()=>{
  try{
    const idServicio = Number(selServicio.value);
    servicioSeleccionado = servicios.find(s=>s.idServicio===idServicio) || null;
    selTrabajador.innerHTML = `<option value="" disabled selected>Cargando...</option>`;
    selTrabajador.disabled = true;
    trabajadores = await getTrabajadoresPorServicio(idServicio);
    selTrabajador.innerHTML = `<option value="" disabled selected>Elige un trabajador</option>`;
    trabajadores.forEach(t=>{
      const id = t.idUsuario||t.id||t.id_trabajador;
      selTrabajador.appendChild(Object.assign(document.createElement("option"),{value:id,textContent:`${t.nombre} ${t.apellido}`}));
    });
    selTrabajador.disabled=false;
  }catch(e){alert("No se pudieron cargar los trabajadores de este servicio.");}
});

selTrabajador.addEventListener("change", async ()=>{
  try{
    idTrabajadorSeleccionado = Number(selTrabajador.value);
    fechaSeleccionada = null;
    horarioSeleccionado = null;
    lblFecha.textContent = "—";
    contHorarios.innerHTML = "";
    btnConfirmar.disabled=true;
    [reservasTrabajador, disponibilidadesTrabajador] = await Promise.all([
      getReservasTrabajador(idTrabajadorSeleccionado),
      getDisponibilidadesTrabajador(idTrabajadorSeleccionado)
    ]);
    pintarEventosCalendario();
  }catch(e){alert("No se pudieron cargar datos del trabajador");}
});

btnRefrescar.addEventListener("click", ()=>{if(servicioSeleccionado && idTrabajadorSeleccionado) selTrabajador.dispatchEvent(new Event("change"));});

btnConfirmar.addEventListener("click", async () => {
  try {
    if (!servicioSeleccionado || !idTrabajadorSeleccionado || !fechaSeleccionada || !horarioSeleccionado) {
      alert("Selecciona servicio, trabajador, fecha y horario.");
      return;
    }

    const [hh, mm] = horarioSeleccionado.split(":").map(Number);
    const dur = servicioSeleccionado.duracionEstimadaMinutos || 60;
    const horaFinHHMM = addMinutes(horarioSeleccionado, dur);
    const [hhf, mmf] = horaFinHHMM.split(":").map(Number);

    const payload = {
      idTrabajador: idTrabajadorSeleccionado,
      idServicio: servicioSeleccionado.idServicio,
      fecha: fechaSeleccionada,
      horaInicio: `${pad2(hh)}:${pad2(mm)}:00`,
      horaFin: `${pad2(hhf)}:${pad2(mmf)}:00`,
    };

    const idModificar = localStorage.getItem("reservaModificando");
    if (idModificar) {
      await fetch(`${API}/reservas/modificar/${idModificar}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json", ...authHeaders() },
        body: JSON.stringify(payload),
      });
      localStorage.removeItem("reservaModificando");
      alert("✅ Reserva modificada con éxito");
    } else {
      await postCrearReserva(payload);
      alert("✅ Reserva creada con éxito");
    }

    selTrabajador.dispatchEvent(new Event("change"));
    cargarHistorial();
  } catch (e) {
    alert("❌ Error al crear o modificar reserva: " + e.message);
  }
});

btnCerrarSesion.addEventListener("click", ()=>{
  localStorage.removeItem("token");
  window.location.href="index.html";
});

// ====== Init ======
window.addEventListener("DOMContentLoaded", async ()=>{
  if(!localStorage.getItem("token")){alert("Debes iniciar sesión");window.location.href="index.html";return;}
  try{
    servicios = await getServiciosActivos();
    selServicio.innerHTML=`<option value="" disabled selected>Elige un servicio</option>`;
    servicios.forEach(s=>{const opt=document.createElement("option");opt.value=s.idServicio;opt.textContent=`${s.nombreServicio} (${s.duracionEstimadaMinutos} min)`;selServicio.appendChild(opt);});
    nombreUsuario.textContent = localStorage.getItem("nombreUsuario") || "Usuario";
  }catch(e){alert("No se pudieron cargar los servicios");}
  initCalendar();
  cargarHistorial();
});
