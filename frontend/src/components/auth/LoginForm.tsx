import React, { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import * as z from 'zod'
import { Eye, EyeOff, LogIn, Loader2 } from 'lucide-react'

import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { useAuthStore } from '@/store/authStore'
import { authApi } from '@/services/api'

const loginSchema = z.object({
  usernameOrEmail: z.string().min(1, 'Usuario o email es requerido'),
  password: z.string().min(1, 'Contraseña es requerida'),
})

type LoginFormData = z.infer<typeof loginSchema>

export const LoginForm: React.FC = () => {
  const navigate = useNavigate()
  const { login, setLoading, setError, isLoading, error } = useAuthStore()
  const [showPassword, setShowPassword] = useState(false)

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<LoginFormData>({
    resolver: zodResolver(loginSchema),
  })

  const onSubmit = async (data: LoginFormData) => {
    try {
      setLoading(true)
      setError(null)

      const response = await authApi.login(data)
      
      login({
        accessToken: response.accessToken,
        refreshToken: response.refreshToken,
        user: response.user,
      })

      // Redirect based on user role
      switch (response.user.role) {
        case 'ADMIN':
          navigate('/admin/dashboard')
          break
        case 'TEACHER':
          navigate('/teacher/dashboard')
          break
        case 'STUDENT':
          navigate('/student/dashboard')
          break
        default:
          navigate('/dashboard')
      }
    } catch (error: any) {
      console.error('Login error:', error)
      setError(
        error.response?.data?.message || 
        'Error al iniciar sesión. Verifica tus credenciales.'
      )
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-blue-50 to-indigo-100 p-4">
      <Card className="w-full max-w-md">
        <CardHeader className="space-y-1">
          <div className="flex items-center justify-center mb-4">
            <div className="w-12 h-12 bg-primary rounded-lg flex items-center justify-center">
              <LogIn className="w-6 h-6 text-primary-foreground" />
            </div>
          </div>
          <CardTitle className="text-2xl text-center">EducaGestor360</CardTitle>
          <CardDescription className="text-center">
            Ingresa tus credenciales para acceder al sistema
          </CardDescription>
        </CardHeader>
        <CardContent>
          <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
            <div className="space-y-2">
              <label htmlFor="usernameOrEmail" className="text-sm font-medium">
                Usuario o Email
              </label>
              <Input
                id="usernameOrEmail"
                type="text"
                placeholder="Ingresa tu usuario o email"
                {...register('usernameOrEmail')}
                className={errors.usernameOrEmail ? 'border-red-500' : ''}
              />
              {errors.usernameOrEmail && (
                <p className="text-sm text-red-500">{errors.usernameOrEmail.message}</p>
              )}
            </div>

            <div className="space-y-2">
              <label htmlFor="password" className="text-sm font-medium">
                Contraseña
              </label>
              <div className="relative">
                <Input
                  id="password"
                  type={showPassword ? 'text' : 'password'}
                  placeholder="Ingresa tu contraseña"
                  {...register('password')}
                  className={errors.password ? 'border-red-500 pr-10' : 'pr-10'}
                />
                <button
                  type="button"
                  onClick={() => setShowPassword(!showPassword)}
                  className="absolute right-3 top-1/2 transform -translate-y-1/2 text-gray-500 hover:text-gray-700"
                >
                  {showPassword ? (
                    <EyeOff className="w-4 h-4" />
                  ) : (
                    <Eye className="w-4 h-4" />
                  )}
                </button>
              </div>
              {errors.password && (
                <p className="text-sm text-red-500">{errors.password.message}</p>
              )}
            </div>

            {error && (
              <div className="p-3 text-sm text-red-600 bg-red-50 border border-red-200 rounded-md">
                {error}
              </div>
            )}

            <Button
              type="submit"
              className="w-full"
              disabled={isLoading}
            >
              {isLoading ? (
                <>
                  <Loader2 className="w-4 h-4 mr-2 animate-spin" />
                  Iniciando sesión...
                </>
              ) : (
                <>
                  <LogIn className="w-4 h-4 mr-2" />
                  Iniciar Sesión
                </>
              )}
            </Button>
          </form>

          <div className="mt-6 text-center">
            <p className="text-sm text-gray-600">
              ¿No tienes una cuenta?{' '}
              <button
                onClick={() => navigate('/register')}
                className="text-primary hover:underline font-medium"
              >
                Regístrate aquí
              </button>
            </p>
          </div>

          <div className="mt-4 text-center">
            <button
              onClick={() => navigate('/forgot-password')}
              className="text-sm text-gray-600 hover:text-primary hover:underline"
            >
              ¿Olvidaste tu contraseña?
            </button>
          </div>
        </CardContent>
      </Card>
    </div>
  )
}
