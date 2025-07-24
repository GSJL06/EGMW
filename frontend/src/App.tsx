import React, { useState, useEffect } from "react";
import { useAuthStore } from "./store/authStore";
import { authApi } from "./services/api";
import { StudentList } from "./components/students/StudentList";

function App() {
  const { isAuthenticated, user, login, logout, setLoading, setError, error } =
    useAuthStore();
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [currentView, setCurrentView] = useState("dashboard");

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!username || !password) return;

    setIsSubmitting(true);
    setError(null);

    try {
      const response = await authApi.login({
        usernameOrEmail: username,
        password: password,
      });

      login({
        accessToken: response.accessToken,
        refreshToken: response.refreshToken,
        user: response.user,
      });
    } catch (error: any) {
      setError(error.response?.data?.message || "Error al iniciar sesión");
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleLogout = () => {
    logout();
    setUsername("");
    setPassword("");
  };

  if (isAuthenticated) {
    return (
      <div className="min-h-screen bg-gray-100 p-8">
        <div className="max-w-6xl mx-auto">
          <header className="bg-white shadow rounded-lg p-6 mb-8">
            <div className="flex justify-between items-center">
              <div>
                <h1 className="text-3xl font-bold text-gray-900">
                  EducaGestor360
                </h1>
                {user && (
                  <p className="text-gray-600 mt-1">
                    Bienvenido, {user.username} ({user.role})
                  </p>
                )}
              </div>
              <button
                onClick={handleLogout}
                className="bg-red-500 hover:bg-red-600 text-white px-4 py-2 rounded transition-colors"
              >
                Cerrar Sesión
              </button>
            </div>
          </header>

          {/* Navigation */}
          <nav className="bg-white shadow rounded-lg p-4 mb-8">
            <div className="flex space-x-4">
              <button
                onClick={() => setCurrentView("dashboard")}
                className={`px-4 py-2 rounded-lg transition-colors ${
                  currentView === "dashboard"
                    ? "bg-blue-600 text-white"
                    : "text-gray-600 hover:bg-gray-100"
                }`}
              >
                Dashboard
              </button>
              <button
                onClick={() => setCurrentView("students")}
                className={`px-4 py-2 rounded-lg transition-colors ${
                  currentView === "students"
                    ? "bg-blue-600 text-white"
                    : "text-gray-600 hover:bg-gray-100"
                }`}
              >
                Estudiantes
              </button>
              <button
                onClick={() => setCurrentView("teachers")}
                className={`px-4 py-2 rounded-lg transition-colors ${
                  currentView === "teachers"
                    ? "bg-blue-600 text-white"
                    : "text-gray-600 hover:bg-gray-100"
                }`}
              >
                Profesores
              </button>
              <button
                onClick={() => setCurrentView("courses")}
                className={`px-4 py-2 rounded-lg transition-colors ${
                  currentView === "courses"
                    ? "bg-blue-600 text-white"
                    : "text-gray-600 hover:bg-gray-100"
                }`}
              >
                Cursos
              </button>
            </div>
          </nav>

          {/* Main Content */}
          {currentView === "dashboard" && (
            <>
              <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
                <div
                  className="bg-white p-6 rounded-lg shadow hover:shadow-md transition-shadow cursor-pointer"
                  onClick={() => setCurrentView("students")}
                >
                  <h2 className="text-xl font-semibold mb-4 text-gray-800">
                    Estudiantes
                  </h2>
                  <p className="text-gray-600 mb-4">
                    Gestión de estudiantes del sistema
                  </p>
                  <div className="flex items-baseline">
                    <span className="text-3xl font-bold text-blue-600">
                      150
                    </span>
                    <span className="text-gray-500 ml-2">
                      estudiantes activos
                    </span>
                  </div>
                  <div className="mt-4">
                    <span className="text-blue-600 text-sm font-medium">
                      Clic para gestionar →
                    </span>
                  </div>
                </div>

                <div className="bg-white p-6 rounded-lg shadow hover:shadow-md transition-shadow cursor-pointer">
                  <h2 className="text-xl font-semibold mb-4 text-gray-800">
                    Profesores
                  </h2>
                  <p className="text-gray-600 mb-4">
                    Gestión de profesores y personal
                  </p>
                  <div className="flex items-baseline">
                    <span className="text-3xl font-bold text-green-600">
                      25
                    </span>
                    <span className="text-gray-500 ml-2">
                      profesores activos
                    </span>
                  </div>
                  <div className="mt-4">
                    <span className="text-green-600 text-sm font-medium">
                      Próximamente →
                    </span>
                  </div>
                </div>

                <div className="bg-white p-6 rounded-lg shadow hover:shadow-md transition-shadow cursor-pointer">
                  <h2 className="text-xl font-semibold mb-4 text-gray-800">
                    Cursos
                  </h2>
                  <p className="text-gray-600 mb-4">
                    Gestión de cursos y materias
                  </p>
                  <div className="flex items-baseline">
                    <span className="text-3xl font-bold text-purple-600">
                      12
                    </span>
                    <span className="text-gray-500 ml-2">
                      cursos disponibles
                    </span>
                  </div>
                  <div className="mt-4">
                    <span className="text-purple-600 text-sm font-medium">
                      Próximamente →
                    </span>
                  </div>
                </div>
              </div>

              <div className="mt-8 bg-white p-6 rounded-lg shadow">
                <h2 className="text-xl font-semibold mb-4 text-gray-800">
                  Estado del Sistema
                </h2>
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  <div>
                    <h3 className="font-medium text-gray-700">Base de Datos</h3>
                    <p className="text-green-600">✅ Conectada (PostgreSQL)</p>
                  </div>
                  <div>
                    <h3 className="font-medium text-gray-700">Backend API</h3>
                    <p className="text-green-600">✅ Conectado (Spring Boot)</p>
                  </div>
                </div>
              </div>
            </>
          )}

          {currentView === "students" && (
            <div>
              <h2>Gestión de Estudiantes - Debug</h2>
              <p>Current view: {currentView}</p>
              <StudentList />
            </div>
          )}

          {currentView === "teachers" && (
            <div className="bg-white p-8 rounded-lg shadow text-center">
              <h2 className="text-2xl font-bold text-gray-900 mb-4">
                Gestión de Profesores
              </h2>
              <p className="text-gray-600">
                Esta funcionalidad estará disponible próximamente.
              </p>
            </div>
          )}

          {currentView === "courses" && (
            <div className="bg-white p-8 rounded-lg shadow text-center">
              <h2 className="text-2xl font-bold text-gray-900 mb-4">
                Gestión de Cursos
              </h2>
              <p className="text-gray-600">
                Esta funcionalidad estará disponible próximamente.
              </p>
            </div>
          )}
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-100 flex items-center justify-center">
      <div className="bg-white p-8 rounded-lg shadow-lg w-full max-w-md">
        <h1 className="text-2xl font-bold text-center mb-6 text-gray-900">
          EducaGestor360
        </h1>
        {error && (
          <div className="mb-4 p-3 bg-red-100 border border-red-400 text-red-700 rounded">
            {error}
          </div>
        )}
        <form onSubmit={handleLogin}>
          <div className="mb-4">
            <label className="block text-gray-700 text-sm font-bold mb-2">
              Usuario
            </label>
            <input
              type="text"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              className="w-full px-3 py-3 border border-gray-300 rounded focus:outline-none focus:border-blue-500"
              placeholder="Ingresa tu usuario"
              required
            />
          </div>
          <div className="mb-6">
            <label className="block text-gray-700 text-sm font-bold mb-2">
              Contraseña
            </label>
            <input
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              className="w-full px-3 py-3 border border-gray-300 rounded focus:outline-none focus:border-blue-500"
              placeholder="Ingresa tu contraseña"
              required
            />
          </div>
          <button
            type="submit"
            disabled={isSubmitting}
            className="w-full bg-blue-500 hover:bg-blue-600 disabled:bg-blue-300 text-white font-bold py-3 rounded transition-colors"
          >
            {isSubmitting ? "Iniciando sesión..." : "Iniciar Sesión"}
          </button>
        </form>
      </div>
    </div>
  );
}

export default App;
