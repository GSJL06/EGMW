import React from 'react'
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom'
import { QueryClient, QueryClientProvider } from '@tanstack/react-query'

import { useAuthStore } from '@/store/authStore'
import { LoginForm } from '@/components/auth/LoginForm'
import { Dashboard } from '@/components/dashboard/Dashboard'
import { ProtectedRoute } from '@/components/auth/ProtectedRoute'
import { Layout } from '@/components/layout/Layout'

// Create a client
const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      retry: 1,
      refetchOnWindowFocus: false,
    },
  },
})

function App() {
  const { isAuthenticated, checkAuth } = useAuthStore()

  // Check authentication on app load
  React.useEffect(() => {
    checkAuth()
  }, [checkAuth])

  return (
    <QueryClientProvider client={queryClient}>
      <Router>
        <div className="min-h-screen bg-background">
          <Routes>
            {/* Public routes */}
            <Route 
              path="/login" 
              element={
                isAuthenticated ? <Navigate to="/dashboard" replace /> : <LoginForm />
              } 
            />
            
            {/* Protected routes */}
            <Route
              path="/*"
              element={
                <ProtectedRoute>
                  <Layout>
                    <Routes>
                      <Route path="/dashboard" element={<Dashboard />} />
                      <Route path="/admin/dashboard" element={<Dashboard />} />
                      <Route path="/teacher/dashboard" element={<Dashboard />} />
                      <Route path="/student/dashboard" element={<Dashboard />} />
                      
                      {/* Students routes */}
                      <Route path="/students" element={<div>Students Page</div>} />
                      <Route path="/students/:id" element={<div>Student Detail</div>} />
                      
                      {/* Teachers routes */}
                      <Route path="/teachers" element={<div>Teachers Page</div>} />
                      <Route path="/teachers/:id" element={<div>Teacher Detail</div>} />
                      
                      {/* Courses routes */}
                      <Route path="/courses" element={<div>Courses Page</div>} />
                      <Route path="/courses/:id" element={<div>Course Detail</div>} />
                      
                      {/* Enrollments routes */}
                      <Route path="/enrollments" element={<div>Enrollments Page</div>} />
                      
                      {/* Grades routes */}
                      <Route path="/grades" element={<div>Grades Page</div>} />
                      
                      {/* Attendance routes */}
                      <Route path="/attendance" element={<div>Attendance Page</div>} />
                      
                      {/* Resources routes */}
                      <Route path="/resources" element={<div>Resources Page</div>} />
                      
                      {/* Profile routes */}
                      <Route path="/profile" element={<div>Profile Page</div>} />
                      <Route path="/settings" element={<div>Settings Page</div>} />
                      
                      {/* Default redirect */}
                      <Route path="/" element={<Navigate to="/dashboard" replace />} />
                      <Route path="*" element={<Navigate to="/dashboard" replace />} />
                    </Routes>
                  </Layout>
                </ProtectedRoute>
              }
            />
          </Routes>
        </div>
      </Router>
    </QueryClientProvider>
  )
}

export default App
