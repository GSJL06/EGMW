import React from 'react'
import { useQuery } from '@tanstack/react-query'
import { 
  Users, 
  GraduationCap, 
  BookOpen, 
  TrendingUp,
  Calendar,
  BarChart3,
  Clock,
  Award
} from 'lucide-react'

import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { useAuthStore } from '@/store/authStore'
import { dashboardApi } from '@/services/api'

export const Dashboard: React.FC = () => {
  const { user } = useAuthStore()

  const { data: stats, isLoading } = useQuery({
    queryKey: ['dashboard-stats'],
    queryFn: dashboardApi.getStats,
  })

  const getGreeting = () => {
    const hour = new Date().getHours()
    if (hour < 12) return 'Buenos días'
    if (hour < 18) return 'Buenas tardes'
    return 'Buenas noches'
  }

  const getRoleTitle = (role: string) => {
    switch (role) {
      case 'ADMIN':
        return 'Administrador'
      case 'TEACHER':
        return 'Profesor'
      case 'STUDENT':
        return 'Estudiante'
      default:
        return 'Usuario'
    }
  }

  if (isLoading) {
    return (
      <div className="space-y-6">
        <div className="animate-pulse">
          <div className="h-8 bg-gray-200 rounded w-1/3 mb-2"></div>
          <div className="h-4 bg-gray-200 rounded w-1/2"></div>
        </div>
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
          {[...Array(4)].map((_, i) => (
            <div key={i} className="animate-pulse">
              <div className="h-32 bg-gray-200 rounded-lg"></div>
            </div>
          ))}
        </div>
      </div>
    )
  }

  return (
    <div className="space-y-6">
      {/* Welcome Section */}
      <div className="bg-gradient-to-r from-blue-600 to-purple-600 rounded-lg p-6 text-white">
        <h1 className="text-2xl font-bold mb-2">
          {getGreeting()}, {user?.username}!
        </h1>
        <p className="text-blue-100">
          Bienvenido al panel de {getRoleTitle(user?.role || '')} de EducaGestor360
        </p>
      </div>

      {/* Stats Cards */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        {/* Total Students */}
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">
              Total Estudiantes
            </CardTitle>
            <Users className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">
              {stats?.studentStats?.totalStudents || 0}
            </div>
            <p className="text-xs text-muted-foreground">
              {stats?.studentStats?.activeStudents || 0} activos
            </p>
          </CardContent>
        </Card>

        {/* Total Teachers */}
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">
              Total Profesores
            </CardTitle>
            <GraduationCap className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">
              {stats?.teacherStats?.totalTeachers || 0}
            </div>
            <p className="text-xs text-muted-foreground">
              {stats?.teacherStats?.activeTeachers || 0} activos
            </p>
          </CardContent>
        </Card>

        {/* Total Courses */}
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">
              Total Cursos
            </CardTitle>
            <BookOpen className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">
              {stats?.courseStats?.totalCourses || 0}
            </div>
            <p className="text-xs text-muted-foreground">
              {stats?.courseStats?.activeCourses || 0} activos
            </p>
          </CardContent>
        </Card>

        {/* Growth Rate */}
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">
              Crecimiento
            </CardTitle>
            <TrendingUp className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">+12%</div>
            <p className="text-xs text-muted-foreground">
              vs. mes anterior
            </p>
          </CardContent>
        </Card>
      </div>

      {/* Quick Actions & Recent Activity */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Quick Actions */}
        <Card>
          <CardHeader>
            <CardTitle>Acciones Rápidas</CardTitle>
            <CardDescription>
              Accede rápidamente a las funciones más utilizadas
            </CardDescription>
          </CardHeader>
          <CardContent className="space-y-4">
            {user?.role === 'ADMIN' && (
              <>
                <QuickActionButton
                  icon={Users}
                  title="Registrar Estudiante"
                  description="Agregar nuevo estudiante al sistema"
                  href="/students/new"
                />
                <QuickActionButton
                  icon={GraduationCap}
                  title="Registrar Profesor"
                  description="Agregar nuevo profesor al sistema"
                  href="/teachers/new"
                />
                <QuickActionButton
                  icon={BookOpen}
                  title="Crear Curso"
                  description="Crear un nuevo curso"
                  href="/courses/new"
                />
              </>
            )}
            
            {user?.role === 'TEACHER' && (
              <>
                <QuickActionButton
                  icon={Calendar}
                  title="Tomar Asistencia"
                  description="Registrar asistencia de estudiantes"
                  href="/attendance/new"
                />
                <QuickActionButton
                  icon={BarChart3}
                  title="Registrar Calificaciones"
                  description="Ingresar calificaciones de estudiantes"
                  href="/grades/new"
                />
                <QuickActionButton
                  icon={BookOpen}
                  title="Mis Cursos"
                  description="Ver y gestionar mis cursos"
                  href="/courses"
                />
              </>
            )}
            
            {user?.role === 'STUDENT' && (
              <>
                <QuickActionButton
                  icon={BookOpen}
                  title="Mis Cursos"
                  description="Ver mis cursos matriculados"
                  href="/courses"
                />
                <QuickActionButton
                  icon={BarChart3}
                  title="Mis Calificaciones"
                  description="Ver mis calificaciones"
                  href="/grades"
                />
                <QuickActionButton
                  icon={Calendar}
                  title="Mi Asistencia"
                  description="Ver mi registro de asistencia"
                  href="/attendance"
                />
              </>
            )}
          </CardContent>
        </Card>

        {/* Recent Activity */}
        <Card>
          <CardHeader>
            <CardTitle>Actividad Reciente</CardTitle>
            <CardDescription>
              Últimas actividades en el sistema
            </CardDescription>
          </CardHeader>
          <CardContent className="space-y-4">
            <ActivityItem
              icon={Users}
              title="Nuevo estudiante registrado"
              description="Juan Pérez se registró en el sistema"
              time="Hace 2 horas"
            />
            <ActivityItem
              icon={BookOpen}
              title="Curso creado"
              description="Matemáticas Avanzadas fue creado"
              time="Hace 4 horas"
            />
            <ActivityItem
              icon={BarChart3}
              title="Calificaciones actualizadas"
              description="Se actualizaron las calificaciones de Física"
              time="Hace 6 horas"
            />
            <ActivityItem
              icon={Calendar}
              title="Asistencia registrada"
              description="Se registró asistencia para Historia"
              time="Hace 8 horas"
            />
          </CardContent>
        </Card>
      </div>
    </div>
  )
}

interface QuickActionButtonProps {
  icon: React.ElementType
  title: string
  description: string
  href: string
}

const QuickActionButton: React.FC<QuickActionButtonProps> = ({
  icon: Icon,
  title,
  description,
  href
}) => {
  return (
    <a
      href={href}
      className="flex items-center p-3 rounded-lg border border-gray-200 hover:bg-gray-50 transition-colors"
    >
      <div className="flex-shrink-0">
        <Icon className="w-8 h-8 text-primary" />
      </div>
      <div className="ml-3">
        <p className="text-sm font-medium text-gray-900">{title}</p>
        <p className="text-sm text-gray-500">{description}</p>
      </div>
    </a>
  )
}

interface ActivityItemProps {
  icon: React.ElementType
  title: string
  description: string
  time: string
}

const ActivityItem: React.FC<ActivityItemProps> = ({
  icon: Icon,
  title,
  description,
  time
}) => {
  return (
    <div className="flex items-start space-x-3">
      <div className="flex-shrink-0">
        <Icon className="w-5 h-5 text-gray-400" />
      </div>
      <div className="flex-1 min-w-0">
        <p className="text-sm font-medium text-gray-900">{title}</p>
        <p className="text-sm text-gray-500">{description}</p>
        <p className="text-xs text-gray-400 mt-1">{time}</p>
      </div>
    </div>
  )
}
