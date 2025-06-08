import React from 'react'
import { NavLink } from 'react-router-dom'
import { 
  Home, 
  Users, 
  GraduationCap, 
  BookOpen, 
  ClipboardList, 
  BarChart3, 
  Calendar,
  FileText,
  X,
  Settings,
  User
} from 'lucide-react'

import { Button } from '@/components/ui/button'
import { useAuthStore } from '@/store/authStore'
import { cn } from '@/lib/utils'

interface SidebarProps {
  isOpen: boolean
  onClose: () => void
}

export const Sidebar: React.FC<SidebarProps> = ({ isOpen, onClose }) => {
  const { user } = useAuthStore()

  const navigationItems = [
    {
      name: 'Dashboard',
      href: '/dashboard',
      icon: Home,
      roles: ['ADMIN', 'TEACHER', 'STUDENT']
    },
    {
      name: 'Estudiantes',
      href: '/students',
      icon: Users,
      roles: ['ADMIN', 'TEACHER']
    },
    {
      name: 'Profesores',
      href: '/teachers',
      icon: GraduationCap,
      roles: ['ADMIN']
    },
    {
      name: 'Cursos',
      href: '/courses',
      icon: BookOpen,
      roles: ['ADMIN', 'TEACHER', 'STUDENT']
    },
    {
      name: 'Matrículas',
      href: '/enrollments',
      icon: ClipboardList,
      roles: ['ADMIN', 'TEACHER']
    },
    {
      name: 'Calificaciones',
      href: '/grades',
      icon: BarChart3,
      roles: ['ADMIN', 'TEACHER', 'STUDENT']
    },
    {
      name: 'Asistencia',
      href: '/attendance',
      icon: Calendar,
      roles: ['ADMIN', 'TEACHER', 'STUDENT']
    },
    {
      name: 'Recursos',
      href: '/resources',
      icon: FileText,
      roles: ['ADMIN', 'TEACHER', 'STUDENT']
    }
  ]

  const filteredNavigation = navigationItems.filter(item => 
    item.roles.includes(user?.role || '')
  )

  return (
    <>
      {/* Mobile overlay */}
      {isOpen && (
        <div 
          className="fixed inset-0 bg-black bg-opacity-50 z-40 lg:hidden"
          onClick={onClose}
        />
      )}

      {/* Sidebar */}
      <div className={cn(
        "fixed inset-y-0 left-0 z-50 w-64 bg-white shadow-lg transform transition-transform duration-300 ease-in-out lg:translate-x-0 lg:static lg:inset-0",
        isOpen ? "translate-x-0" : "-translate-x-full"
      )}>
        <div className="flex flex-col h-full">
          {/* Header */}
          <div className="flex items-center justify-between p-6 border-b border-gray-200">
            <div className="flex items-center space-x-2">
              <div className="w-8 h-8 bg-primary rounded-lg flex items-center justify-center">
                <GraduationCap className="w-5 h-5 text-primary-foreground" />
              </div>
              <span className="text-lg font-semibold text-gray-900">
                EducaGestor360
              </span>
            </div>
            <Button
              variant="ghost"
              size="icon"
              onClick={onClose}
              className="lg:hidden"
            >
              <X className="w-5 h-5" />
            </Button>
          </div>

          {/* Navigation */}
          <nav className="flex-1 px-4 py-6 space-y-2">
            {filteredNavigation.map((item) => (
              <NavLink
                key={item.name}
                to={item.href}
                onClick={onClose}
                className={({ isActive }) =>
                  cn(
                    "flex items-center px-3 py-2 text-sm font-medium rounded-md transition-colors",
                    isActive
                      ? "bg-primary text-primary-foreground"
                      : "text-gray-600 hover:bg-gray-100 hover:text-gray-900"
                  )
                }
              >
                <item.icon className="w-5 h-5 mr-3" />
                {item.name}
              </NavLink>
            ))}
          </nav>

          {/* Footer */}
          <div className="p-4 border-t border-gray-200">
            <div className="space-y-2">
              <NavLink
                to="/profile"
                onClick={onClose}
                className={({ isActive }) =>
                  cn(
                    "flex items-center px-3 py-2 text-sm font-medium rounded-md transition-colors",
                    isActive
                      ? "bg-primary text-primary-foreground"
                      : "text-gray-600 hover:bg-gray-100 hover:text-gray-900"
                  )
                }
              >
                <User className="w-5 h-5 mr-3" />
                Mi Perfil
              </NavLink>
              <NavLink
                to="/settings"
                onClick={onClose}
                className={({ isActive }) =>
                  cn(
                    "flex items-center px-3 py-2 text-sm font-medium rounded-md transition-colors",
                    isActive
                      ? "bg-primary text-primary-foreground"
                      : "text-gray-600 hover:bg-gray-100 hover:text-gray-900"
                  )
                }
              >
                <Settings className="w-5 h-5 mr-3" />
                Configuración
              </NavLink>
            </div>
          </div>
        </div>
      </div>
    </>
  )
}
