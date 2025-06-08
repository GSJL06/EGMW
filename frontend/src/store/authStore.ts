import { create } from 'zustand'
import { persist } from 'zustand/middleware'
import { isTokenExpired } from '@/lib/utils'

export interface User {
  id: number
  username: string
  email: string
  role: 'ADMIN' | 'TEACHER' | 'STUDENT'
  active: boolean
}

export interface AuthState {
  user: User | null
  accessToken: string | null
  refreshToken: string | null
  isAuthenticated: boolean
  isLoading: boolean
  error: string | null
}

export interface AuthActions {
  login: (tokens: { accessToken: string; refreshToken: string; user: User }) => void
  logout: () => void
  setUser: (user: User) => void
  setLoading: (loading: boolean) => void
  setError: (error: string | null) => void
  clearError: () => void
  checkAuth: () => boolean
  updateTokens: (tokens: { accessToken: string; refreshToken: string }) => void
}

export type AuthStore = AuthState & AuthActions

const initialState: AuthState = {
  user: null,
  accessToken: null,
  refreshToken: null,
  isAuthenticated: false,
  isLoading: false,
  error: null,
}

export const useAuthStore = create<AuthStore>()(
  persist(
    (set, get) => ({
      ...initialState,

      login: (tokens) => {
        set({
          user: tokens.user,
          accessToken: tokens.accessToken,
          refreshToken: tokens.refreshToken,
          isAuthenticated: true,
          error: null,
        })
      },

      logout: () => {
        set(initialState)
      },

      setUser: (user) => {
        set({ user })
      },

      setLoading: (isLoading) => {
        set({ isLoading })
      },

      setError: (error) => {
        set({ error })
      },

      clearError: () => {
        set({ error: null })
      },

      checkAuth: () => {
        const { accessToken, refreshToken } = get()
        
        if (!accessToken || !refreshToken) {
          get().logout()
          return false
        }

        if (isTokenExpired(accessToken)) {
          // Token expired, should refresh
          return false
        }

        return true
      },

      updateTokens: (tokens) => {
        set({
          accessToken: tokens.accessToken,
          refreshToken: tokens.refreshToken,
        })
      },
    }),
    {
      name: 'auth-storage',
      partialize: (state) => ({
        user: state.user,
        accessToken: state.accessToken,
        refreshToken: state.refreshToken,
        isAuthenticated: state.isAuthenticated,
      }),
    }
  )
)
