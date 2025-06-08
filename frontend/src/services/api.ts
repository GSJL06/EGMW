import axios, { AxiosInstance, AxiosRequestConfig, AxiosResponse } from 'axios'
import { useAuthStore } from '@/store/authStore'

// API Base URL
const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api'

// Create axios instance
const api: AxiosInstance = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
})

// Request interceptor to add auth token
api.interceptors.request.use(
  (config) => {
    const { accessToken } = useAuthStore.getState()
    
    if (accessToken) {
      config.headers.Authorization = `Bearer ${accessToken}`
    }
    
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// Response interceptor to handle token refresh
api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config
    
    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true
      
      const { refreshToken, updateTokens, logout } = useAuthStore.getState()
      
      if (refreshToken) {
        try {
          const response = await axios.post(`${API_BASE_URL}/auth/refresh`, {
            refreshToken,
          })
          
          const { accessToken: newAccessToken, refreshToken: newRefreshToken } = response.data
          
          updateTokens({
            accessToken: newAccessToken,
            refreshToken: newRefreshToken,
          })
          
          // Retry original request with new token
          originalRequest.headers.Authorization = `Bearer ${newAccessToken}`
          return api(originalRequest)
        } catch (refreshError) {
          logout()
          window.location.href = '/login'
          return Promise.reject(refreshError)
        }
      } else {
        logout()
        window.location.href = '/login'
      }
    }
    
    return Promise.reject(error)
  }
)

// API Response types
export interface ApiResponse<T = any> {
  data: T
  message?: string
  status: number
}

export interface PaginatedResponse<T> {
  content: T[]
  totalElements: number
  totalPages: number
  size: number
  number: number
  first: boolean
  last: boolean
}

// Generic API methods
export const apiService = {
  // GET request
  get: async <T = any>(url: string, config?: AxiosRequestConfig): Promise<T> => {
    const response: AxiosResponse<T> = await api.get(url, config)
    return response.data
  },

  // POST request
  post: async <T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T> => {
    const response: AxiosResponse<T> = await api.post(url, data, config)
    return response.data
  },

  // PUT request
  put: async <T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T> => {
    const response: AxiosResponse<T> = await api.put(url, data, config)
    return response.data
  },

  // DELETE request
  delete: async <T = any>(url: string, config?: AxiosRequestConfig): Promise<T> => {
    const response: AxiosResponse<T> = await api.delete(url, config)
    return response.data
  },

  // PATCH request
  patch: async <T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T> => {
    const response: AxiosResponse<T> = await api.patch(url, data, config)
    return response.data
  },
}

// Auth API
export const authApi = {
  login: (credentials: { usernameOrEmail: string; password: string }) =>
    apiService.post('/auth/login', credentials),

  register: (userData: {
    username: string
    email: string
    password: string
    confirmPassword: string
    role: string
  }) => apiService.post('/auth/register', userData),

  refreshToken: (refreshToken: string) =>
    apiService.post('/auth/refresh', { refreshToken }),

  logout: () => apiService.post('/auth/logout'),

  getProfile: () => apiService.get('/auth/profile'),

  updateProfile: (data: { username?: string; email?: string }) =>
    apiService.put('/auth/profile', data),

  changePassword: (data: {
    currentPassword: string
    newPassword: string
    confirmPassword: string
  }) => apiService.post('/auth/change-password', data),
}

// Students API
export const studentsApi = {
  getAll: (params?: { page?: number; size?: number; sort?: string }) =>
    apiService.get<PaginatedResponse<any>>('/students', { params }),

  getById: (id: number) => apiService.get(`/students/${id}`),

  getByCode: (code: string) => apiService.get(`/students/code/${code}`),

  create: (data: any) => apiService.post('/students', data),

  update: (id: number, data: any) => apiService.put(`/students/${id}`, data),

  delete: (id: number) => apiService.delete(`/students/${id}`),

  search: (searchTerm: string, params?: { page?: number; size?: number }) =>
    apiService.get<PaginatedResponse<any>>('/students/search', {
      params: { searchTerm, ...params },
    }),

  getByInstitution: (institutionId: number, params?: { page?: number; size?: number }) =>
    apiService.get<PaginatedResponse<any>>(`/students/institution/${institutionId}`, { params }),

  getByStatus: (status: string, params?: { page?: number; size?: number }) =>
    apiService.get<PaginatedResponse<any>>(`/students/status/${status}`, { params }),

  existsByCode: (code: string) => apiService.get<boolean>(`/students/exists/code/${code}`),

  countByInstitution: (institutionId: number) =>
    apiService.get<number>(`/students/count/institution/${institutionId}`),

  countByStatus: (status: string) => apiService.get<number>(`/students/count/status/${status}`),
}

// Teachers API
export const teachersApi = {
  getAll: (params?: { page?: number; size?: number; sort?: string }) =>
    apiService.get<PaginatedResponse<any>>('/teachers', { params }),

  getById: (id: number) => apiService.get(`/teachers/${id}`),

  getByCode: (code: string) => apiService.get(`/teachers/code/${code}`),

  create: (data: any) => apiService.post('/teachers', data),

  update: (id: number, data: any) => apiService.put(`/teachers/${id}`, data),

  delete: (id: number) => apiService.delete(`/teachers/${id}`),

  search: (searchTerm: string, params?: { page?: number; size?: number }) =>
    apiService.get<PaginatedResponse<any>>('/teachers/search', {
      params: { searchTerm, ...params },
    }),

  getByInstitution: (institutionId: number, params?: { page?: number; size?: number }) =>
    apiService.get<PaginatedResponse<any>>(`/teachers/institution/${institutionId}`, { params }),

  getByStatus: (status: string, params?: { page?: number; size?: number }) =>
    apiService.get<PaginatedResponse<any>>(`/teachers/status/${status}`, { params }),

  existsByCode: (code: string) => apiService.get<boolean>(`/teachers/exists/code/${code}`),

  countByInstitution: (institutionId: number) =>
    apiService.get<number>(`/teachers/count/institution/${institutionId}`),

  countByStatus: (status: string) => apiService.get<number>(`/teachers/count/status/${status}`),
}

// Courses API
export const coursesApi = {
  getAll: (params?: { page?: number; size?: number; sort?: string }) =>
    apiService.get<PaginatedResponse<any>>('/courses', { params }),

  getById: (id: number) => apiService.get(`/courses/${id}`),

  getByCode: (code: string) => apiService.get(`/courses/code/${code}`),

  create: (data: any) => apiService.post('/courses', data),

  update: (id: number, data: any) => apiService.put(`/courses/${id}`, data),

  delete: (id: number) => apiService.delete(`/courses/${id}`),

  search: (searchTerm: string, params?: { page?: number; size?: number }) =>
    apiService.get<PaginatedResponse<any>>('/courses/search', {
      params: { searchTerm, ...params },
    }),

  getByInstitution: (institutionId: number, params?: { page?: number; size?: number }) =>
    apiService.get<PaginatedResponse<any>>(`/courses/institution/${institutionId}`, { params }),

  getByTeacher: (teacherId: number, params?: { page?: number; size?: number }) =>
    apiService.get<PaginatedResponse<any>>(`/courses/teacher/${teacherId}`, { params }),

  getByStatus: (status: string, params?: { page?: number; size?: number }) =>
    apiService.get<PaginatedResponse<any>>(`/courses/status/${status}`, { params }),

  getCurrent: (params?: { page?: number; size?: number }) =>
    apiService.get<PaginatedResponse<any>>('/courses/current', { params }),

  getAvailable: () => apiService.get<any[]>('/courses/available'),

  existsByCode: (code: string) => apiService.get<boolean>(`/courses/exists/code/${code}`),
}

// Dashboard API
export const dashboardApi = {
  getStats: () => apiService.get('/dashboard/stats'),
  getUserStats: () => apiService.get('/dashboard/stats/users'),
  getStudentStats: () => apiService.get('/dashboard/stats/students'),
  getTeacherStats: () => apiService.get('/dashboard/stats/teachers'),
  getCourseStats: () => apiService.get('/dashboard/stats/courses'),
}

export default api
